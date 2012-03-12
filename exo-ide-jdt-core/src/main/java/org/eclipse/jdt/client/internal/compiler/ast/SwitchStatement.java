/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Herrmann - Contribution for bug 319201 - [null] no warning when unboxing SingleNameReference causes NPE
 *******************************************************************************/
package org.eclipse.jdt.client.internal.compiler.ast;

import org.eclipse.jdt.client.internal.compiler.ASTVisitor;
import org.eclipse.jdt.client.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.client.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.client.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.client.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.client.internal.compiler.flow.SwitchFlowContext;
import org.eclipse.jdt.client.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.client.internal.compiler.impl.Constant;
import org.eclipse.jdt.client.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.client.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.client.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.client.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.client.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.client.internal.compiler.lookup.SyntheticMethodBinding;
import org.eclipse.jdt.client.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.client.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.client.internal.compiler.problem.ProblemSeverities;

public class SwitchStatement extends Statement
{

   public Expression expression;

   public Statement[] statements;

   public BlockScope scope;

   public int explicitDeclarations;

   public BranchLabel breakLabel;

   public CaseStatement[] cases;

   public CaseStatement defaultCase;

   public int blockStart;

   public int caseCount;

   int[] constants;

   String[] stringConstants;

   // fallthrough
   public final static int CASE = 0;

   public final static int FALLTHROUGH = 1;

   public final static int ESCAPING = 2;

   // for switch on strings
   private static final char[] SecretStringVariableName = " switchDispatchString".toCharArray(); //$NON-NLS-1$

   public SyntheticMethodBinding synthetic; // use for switch on enums types

   // for local variables table attributes
   int preSwitchInitStateIndex = -1;

   int mergedInitStateIndex = -1;

   CaseStatement[] duplicateCaseStatements = null;

   int duplicateCaseStatementsCounter = 0;

   private LocalVariableBinding dispatchStringCopy = null;

   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
   {
      try
      {
         flowInfo = this.expression.analyseCode(currentScope, flowContext, flowInfo);
         if ((this.expression.implicitConversion & TypeIds.UNBOXING) != 0
            || (this.expression.resolvedType != null && this.expression.resolvedType.id == T_JavaLangString))
         {
            this.expression.checkNPE(currentScope, flowContext, flowInfo);
         }
         SwitchFlowContext switchContext =
            new SwitchFlowContext(flowContext, this, (this.breakLabel = new BranchLabel()));

         // analyse the block by considering specially the case/default statements (need to bind them
         // to the entry point)
         FlowInfo caseInits = FlowInfo.DEAD_END;
         // in case of statements before the first case
         this.preSwitchInitStateIndex = currentScope.methodScope().recordInitializationStates(flowInfo);
         int caseIndex = 0;
         if (this.statements != null)
         {
            int initialComplaintLevel =
               (flowInfo.reachMode() & FlowInfo.UNREACHABLE) != 0 ? Statement.COMPLAINED_FAKE_REACHABLE
                  : Statement.NOT_COMPLAINED;
            int complaintLevel = initialComplaintLevel;
            int fallThroughState = CASE;
            for (int i = 0, max = this.statements.length; i < max; i++)
            {
               Statement statement = this.statements[i];
               if ((caseIndex < this.caseCount) && (statement == this.cases[caseIndex]))
               { // statement is a case
                  this.scope.enclosingCase = this.cases[caseIndex]; // record entering in a switch case block
                  caseIndex++;
                  if (fallThroughState == FALLTHROUGH && (statement.bits & ASTNode.DocumentedFallthrough) == 0)
                  { // the case is not fall-through protected by a line comment
                     this.scope.problemReporter().possibleFallThroughCase(this.scope.enclosingCase);
                  }
                  caseInits = caseInits.mergedWith(flowInfo.unconditionalInits());
                  complaintLevel = initialComplaintLevel; // reset complaint
                  fallThroughState = CASE;
               }
               else if (statement == this.defaultCase)
               { // statement is the default case
                  this.scope.enclosingCase = this.defaultCase; // record entering in a switch case block
                  if (fallThroughState == FALLTHROUGH && (statement.bits & ASTNode.DocumentedFallthrough) == 0)
                  {
                     this.scope.problemReporter().possibleFallThroughCase(this.scope.enclosingCase);
                  }
                  caseInits = caseInits.mergedWith(flowInfo.unconditionalInits());
                  complaintLevel = initialComplaintLevel; // reset complaint
                  fallThroughState = CASE;
               }
               else
               {
                  fallThroughState = FALLTHROUGH; // reset below if needed
               }
               if ((complaintLevel = statement.complainIfUnreachable(caseInits, this.scope, complaintLevel)) < Statement.COMPLAINED_UNREACHABLE)
               {
                  caseInits = statement.analyseCode(this.scope, switchContext, caseInits);
                  if (caseInits == FlowInfo.DEAD_END)
                  {
                     fallThroughState = ESCAPING;
                  }
               }
            }
         }

         final TypeBinding resolvedTypeBinding = this.expression.resolvedType;
         if (resolvedTypeBinding.isEnum())
         {
            final SourceTypeBinding sourceTypeBinding = currentScope.classScope().referenceContext.binding;
            this.synthetic = sourceTypeBinding.addSyntheticMethodForSwitchEnum(resolvedTypeBinding);
         }
         // if no default case, then record it may jump over the block directly to the end
         if (this.defaultCase == null)
         {
            // only retain the potential initializations
            flowInfo.addPotentialInitializationsFrom(caseInits.mergedWith(switchContext.initsOnBreak));
            this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(flowInfo);
            return flowInfo;
         }

         // merge all branches inits
         FlowInfo mergedInfo = caseInits.mergedWith(switchContext.initsOnBreak);
         this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
         return mergedInfo;
      }
      finally
      {
         if (this.scope != null)
            this.scope.enclosingCase = null; // no longer inside switch case block
      }
   }

   public StringBuffer printStatement(int indent, StringBuffer output)
   {

      printIndent(indent, output).append("switch ("); //$NON-NLS-1$
      this.expression.printExpression(0, output).append(") {"); //$NON-NLS-1$
      if (this.statements != null)
      {
         for (int i = 0; i < this.statements.length; i++)
         {
            output.append('\n');
            if (this.statements[i] instanceof CaseStatement)
            {
               this.statements[i].printStatement(indent, output);
            }
            else
            {
               this.statements[i].printStatement(indent + 2, output);
            }
         }
      }
      output.append("\n"); //$NON-NLS-1$
      return printIndent(indent, output).append('}');
   }

   public void resolve(BlockScope upperScope)
   {
      try
      {
         boolean isEnumSwitch = false;
         boolean isStringSwitch = false;
         TypeBinding expressionType = this.expression.resolveType(upperScope);
         if (expressionType != null)
         {
            this.expression.computeConversion(upperScope, expressionType, expressionType);
            checkType :
            {
               if (!expressionType.isValidBinding())
               {
                  expressionType = null; // fault-tolerance: ignore type mismatch from constants from hereon
                  break checkType;
               }
               else if (expressionType.isBaseType())
               {
                  if (this.expression.isConstantValueOfTypeAssignableToType(expressionType, TypeBinding.INT))
                     break checkType;
                  if (expressionType.isCompatibleWith(TypeBinding.INT))
                     break checkType;
               }
               else if (expressionType.isEnum())
               {
                  isEnumSwitch = true;
                  break checkType;
               }
               else if (upperScope.isBoxingCompatibleWith(expressionType, TypeBinding.INT))
               {
                  this.expression.computeConversion(upperScope, TypeBinding.INT, expressionType);
                  break checkType;
               }
               else if (upperScope.compilerOptions().complianceLevel >= ClassFileConstants.JDK1_7
                  && expressionType.id == TypeIds.T_JavaLangString)
               {
                  isStringSwitch = true;
                  break checkType;
               }
               upperScope.problemReporter().incorrectSwitchType(this.expression, expressionType);
               expressionType = null; // fault-tolerance: ignore type mismatch from constants from hereon
            }
         }
         if (isStringSwitch)
         {
            // the secret variable should be created before iterating over the switch's statements that could
            // create more locals. This must be done to prevent overlapping of locals
            // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=356002
            this.dispatchStringCopy =
               new LocalVariableBinding(SecretStringVariableName, upperScope.getJavaLangString(),
                  ClassFileConstants.AccDefault, false);
            upperScope.addLocalVariable(this.dispatchStringCopy);
            this.dispatchStringCopy.setConstant(Constant.NotAConstant);
            this.dispatchStringCopy.useFlag = LocalVariableBinding.USED;
         }
         if (this.statements != null)
         {
            this.scope = new BlockScope(upperScope);
            int length;
            // collection of cases is too big but we will only iterate until caseCount
            this.cases = new CaseStatement[length = this.statements.length];
            if (!isStringSwitch)
            {
               this.constants = new int[length];
            }
            else
            {
               this.stringConstants = new String[length];
            }
            int counter = 0;
            for (int i = 0; i < length; i++)
            {
               Constant constant;
               final Statement statement = this.statements[i];
               if ((constant = statement.resolveCase(this.scope, expressionType, this)) != Constant.NotAConstant)
               {
                  if (!isStringSwitch)
                  {
                     int key = constant.intValue();
                     // ----check for duplicate case statement------------
                     for (int j = 0; j < counter; j++)
                     {
                        if (this.constants[j] == key)
                        {
                           reportDuplicateCase((CaseStatement)statement, this.cases[j], length);
                        }
                     }
                     this.constants[counter++] = key;
                  }
                  else
                  {
                     String key = constant.stringValue();
                     // ----check for duplicate case statement------------
                     for (int j = 0; j < counter; j++)
                     {
                        if (this.stringConstants[j].equals(key))
                        {
                           reportDuplicateCase((CaseStatement)statement, this.cases[j], length);
                        }
                     }
                     this.stringConstants[counter++] = key;
                  }
               }
            }
            if (length != counter)
            { // resize constants array
               if (!isStringSwitch)
               {
                  System.arraycopy(this.constants, 0, this.constants = new int[counter], 0, counter);
               }
               else
               {
                  System.arraycopy(this.stringConstants, 0, this.stringConstants = new String[counter], 0, counter);
               }
            }
         }
         else
         {
            if ((this.bits & UndocumentedEmptyBlock) != 0)
            {
               upperScope.problemReporter().undocumentedEmptyBlock(this.blockStart, this.sourceEnd);
            }
         }
         // for enum switch, check if all constants are accounted for (if no default)
         if (isEnumSwitch
            && this.defaultCase == null
            && upperScope.compilerOptions().getSeverity(CompilerOptions.IncompleteEnumSwitch) != ProblemSeverities.Ignore)
         {
            int constantCount = this.constants == null ? 0 : this.constants.length; // could be null if no case statement
            if (constantCount == this.caseCount
               && this.caseCount != ((ReferenceBinding)expressionType).enumConstantCount())
            {
               FieldBinding[] enumFields = ((ReferenceBinding)expressionType.erasure()).fields();
               for (int i = 0, max = enumFields.length; i < max; i++)
               {
                  FieldBinding enumConstant = enumFields[i];
                  if ((enumConstant.modifiers & ClassFileConstants.AccEnum) == 0)
                     continue;
                  findConstant :
                  {
                     for (int j = 0; j < this.caseCount; j++)
                     {
                        if ((enumConstant.id + 1) == this.constants[j]) // zero should not be returned see bug 141810
                           break findConstant;
                     }
                     // enum constant did not get referenced from switch
                     upperScope.problemReporter().missingEnumConstantCase(this, enumConstant);
                  }
               }
            }
         }
      }
      finally
      {
         if (this.scope != null)
            this.scope.enclosingCase = null; // no longer inside switch case block
      }
   }

   private void reportDuplicateCase(final CaseStatement duplicate, final CaseStatement original, int length)
   {
      if (this.duplicateCaseStatements == null)
      {
         this.scope.problemReporter().duplicateCase(original);
         this.scope.problemReporter().duplicateCase(duplicate);
         this.duplicateCaseStatements = new CaseStatement[length];
         this.duplicateCaseStatements[this.duplicateCaseStatementsCounter++] = original;
         this.duplicateCaseStatements[this.duplicateCaseStatementsCounter++] = duplicate;
      }
      else
      {
         boolean found = false;
         searchReportedDuplicate : for (int k = 2; k < this.duplicateCaseStatementsCounter; k++)
         {
            if (this.duplicateCaseStatements[k] == duplicate)
            {
               found = true;
               break searchReportedDuplicate;
            }
         }
         if (!found)
         {
            this.scope.problemReporter().duplicateCase(duplicate);
            this.duplicateCaseStatements[this.duplicateCaseStatementsCounter++] = duplicate;
         }
      }
   }

   public void traverse(ASTVisitor visitor, BlockScope blockScope)
   {

      if (visitor.visit(this, blockScope))
      {
         this.expression.traverse(visitor, blockScope);
         if (this.statements != null)
         {
            int statementsLength = this.statements.length;
            for (int i = 0; i < statementsLength; i++)
               this.statements[i].traverse(visitor, this.scope);
         }
      }
      visitor.endVisit(this, blockScope);
   }

   /** Dispatch the call on its last statement. */
   public void branchChainTo(BranchLabel label)
   {

      // in order to improve debug attributes for stepping (11431)
      // we want to inline the jumps to #breakLabel which already got
      // generated (if any), and have them directly branch to a better
      // location (the argument label).
      // we know at this point that the breakLabel already got placed
      if (this.breakLabel.forwardReferenceCount() > 0)
      {
         label.becomeDelegateFor(this.breakLabel);
      }
   }
}