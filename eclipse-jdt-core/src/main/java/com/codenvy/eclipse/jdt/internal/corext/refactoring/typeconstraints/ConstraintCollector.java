/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.jdt.internal.corext.refactoring.typeconstraints;

import com.codenvy.eclipse.core.runtime.Assert;
import com.codenvy.eclipse.jdt.core.dom.ASTVisitor;
import com.codenvy.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import com.codenvy.eclipse.jdt.core.dom.ArrayAccess;
import com.codenvy.eclipse.jdt.core.dom.ArrayCreation;
import com.codenvy.eclipse.jdt.core.dom.ArrayInitializer;
import com.codenvy.eclipse.jdt.core.dom.ArrayType;
import com.codenvy.eclipse.jdt.core.dom.AssertStatement;
import com.codenvy.eclipse.jdt.core.dom.Assignment;
import com.codenvy.eclipse.jdt.core.dom.Block;
import com.codenvy.eclipse.jdt.core.dom.BooleanLiteral;
import com.codenvy.eclipse.jdt.core.dom.BreakStatement;
import com.codenvy.eclipse.jdt.core.dom.CastExpression;
import com.codenvy.eclipse.jdt.core.dom.CatchClause;
import com.codenvy.eclipse.jdt.core.dom.CharacterLiteral;
import com.codenvy.eclipse.jdt.core.dom.ClassInstanceCreation;
import com.codenvy.eclipse.jdt.core.dom.CompilationUnit;
import com.codenvy.eclipse.jdt.core.dom.ConditionalExpression;
import com.codenvy.eclipse.jdt.core.dom.ConstructorInvocation;
import com.codenvy.eclipse.jdt.core.dom.ContinueStatement;
import com.codenvy.eclipse.jdt.core.dom.DoStatement;
import com.codenvy.eclipse.jdt.core.dom.EmptyStatement;
import com.codenvy.eclipse.jdt.core.dom.ExpressionStatement;
import com.codenvy.eclipse.jdt.core.dom.FieldAccess;
import com.codenvy.eclipse.jdt.core.dom.FieldDeclaration;
import com.codenvy.eclipse.jdt.core.dom.ForStatement;
import com.codenvy.eclipse.jdt.core.dom.IfStatement;
import com.codenvy.eclipse.jdt.core.dom.ImportDeclaration;
import com.codenvy.eclipse.jdt.core.dom.InfixExpression;
import com.codenvy.eclipse.jdt.core.dom.Initializer;
import com.codenvy.eclipse.jdt.core.dom.InstanceofExpression;
import com.codenvy.eclipse.jdt.core.dom.Javadoc;
import com.codenvy.eclipse.jdt.core.dom.LabeledStatement;
import com.codenvy.eclipse.jdt.core.dom.MarkerAnnotation;
import com.codenvy.eclipse.jdt.core.dom.MethodDeclaration;
import com.codenvy.eclipse.jdt.core.dom.MethodInvocation;
import com.codenvy.eclipse.jdt.core.dom.NormalAnnotation;
import com.codenvy.eclipse.jdt.core.dom.NullLiteral;
import com.codenvy.eclipse.jdt.core.dom.NumberLiteral;
import com.codenvy.eclipse.jdt.core.dom.PackageDeclaration;
import com.codenvy.eclipse.jdt.core.dom.ParenthesizedExpression;
import com.codenvy.eclipse.jdt.core.dom.PostfixExpression;
import com.codenvy.eclipse.jdt.core.dom.PrefixExpression;
import com.codenvy.eclipse.jdt.core.dom.PrimitiveType;
import com.codenvy.eclipse.jdt.core.dom.QualifiedName;
import com.codenvy.eclipse.jdt.core.dom.ReturnStatement;
import com.codenvy.eclipse.jdt.core.dom.SimpleName;
import com.codenvy.eclipse.jdt.core.dom.SimpleType;
import com.codenvy.eclipse.jdt.core.dom.SingleMemberAnnotation;
import com.codenvy.eclipse.jdt.core.dom.SingleVariableDeclaration;
import com.codenvy.eclipse.jdt.core.dom.StringLiteral;
import com.codenvy.eclipse.jdt.core.dom.SuperConstructorInvocation;
import com.codenvy.eclipse.jdt.core.dom.SuperFieldAccess;
import com.codenvy.eclipse.jdt.core.dom.SuperMethodInvocation;
import com.codenvy.eclipse.jdt.core.dom.SwitchCase;
import com.codenvy.eclipse.jdt.core.dom.SwitchStatement;
import com.codenvy.eclipse.jdt.core.dom.SynchronizedStatement;
import com.codenvy.eclipse.jdt.core.dom.ThisExpression;
import com.codenvy.eclipse.jdt.core.dom.ThrowStatement;
import com.codenvy.eclipse.jdt.core.dom.TryStatement;
import com.codenvy.eclipse.jdt.core.dom.TypeDeclaration;
import com.codenvy.eclipse.jdt.core.dom.TypeDeclarationStatement;
import com.codenvy.eclipse.jdt.core.dom.TypeLiteral;
import com.codenvy.eclipse.jdt.core.dom.VariableDeclarationExpression;
import com.codenvy.eclipse.jdt.core.dom.VariableDeclarationFragment;
import com.codenvy.eclipse.jdt.core.dom.VariableDeclarationStatement;
import com.codenvy.eclipse.jdt.core.dom.WhileStatement;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;


public final class ConstraintCollector extends ASTVisitor {

    private final ConstraintCreator fCreator;

    private final Set<ITypeConstraint> fConstraints;

    public ConstraintCollector() {
        this(new FullConstraintCreator());
    }

    public ConstraintCollector(ConstraintCreator creator) {
        Assert.isNotNull(creator);
        fCreator = creator;
        fConstraints = new LinkedHashSet<ITypeConstraint>();
    }

    private void add(ITypeConstraint[] constraints) {
        fConstraints.addAll(Arrays.asList(constraints));
    }

    public void clear() {
        fConstraints.clear();
    }

    public ITypeConstraint[] getConstraints() {
        return fConstraints.toArray(new ITypeConstraint[fConstraints.size()]);
    }

    //------------------------- visit methods -------------------------//

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.AnonymousClassDeclaration)
     */
    @Override
    public boolean visit(AnonymousClassDeclaration node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ArrayAccess)
     */
    @Override
    public boolean visit(ArrayAccess node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ArrayCreation)
     */
    @Override
    public boolean visit(ArrayCreation node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ArrayInitializer)
     */
    @Override
    public boolean visit(ArrayInitializer node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ArrayType)
     */
    @Override
    public boolean visit(ArrayType node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.AssertStatement)
     */
    @Override
    public boolean visit(AssertStatement node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.Assignment)
     */
    @Override
    public boolean visit(Assignment node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.Block)
     */
    @Override
    public boolean visit(Block node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.BooleanLiteral)
     */
    @Override
    public boolean visit(BooleanLiteral node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.BreakStatement)
     */
    @Override
    public boolean visit(BreakStatement node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.CastExpression)
     */
    @Override
    public boolean visit(CastExpression node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.CatchClause)
     */
    @Override
    public boolean visit(CatchClause node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.CharacterLiteral)
     */
    @Override
    public boolean visit(CharacterLiteral node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ClassInstanceCreation)
     */
    @Override
    public boolean visit(ClassInstanceCreation node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.CompilationUnit)
     */
    @Override
    public boolean visit(CompilationUnit node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ConditionalExpression)
     */
    @Override
    public boolean visit(ConditionalExpression node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ConstructorInvocation)
     */
    @Override
    public boolean visit(ConstructorInvocation node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ContinueStatement)
     */
    @Override
    public boolean visit(ContinueStatement node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.DoStatement)
     */
    @Override
    public boolean visit(DoStatement node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.EmptyStatement)
     */
    @Override
    public boolean visit(EmptyStatement node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ExpressionStatement)
     */
    @Override
    public boolean visit(ExpressionStatement node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.FieldAccess)
     */
    @Override
    public boolean visit(FieldAccess node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.FieldDeclaration)
     */
    @Override
    public boolean visit(FieldDeclaration node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ForStatement)
     */
    @Override
    public boolean visit(ForStatement node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.IfStatement)
     */
    @Override
    public boolean visit(IfStatement node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ImportDeclaration)
     */
    @Override
    public boolean visit(ImportDeclaration node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.InfixExpression)
     */
    @Override
    public boolean visit(InfixExpression node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.Initializer)
     */
    @Override
    public boolean visit(Initializer node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.InstanceofExpression)
     */
    @Override
    public boolean visit(InstanceofExpression node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.Javadoc)
     */
    @Override
    public boolean visit(Javadoc node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.LabeledStatement)
     */
    @Override
    public boolean visit(LabeledStatement node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MarkerAnnotation)
     */
    @Override
    public boolean visit(MarkerAnnotation node) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MethodDeclaration)
     */
    @Override
    public boolean visit(MethodDeclaration node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MethodInvocation)
     */
    @Override
    public boolean visit(MethodInvocation node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.NormalAnnotation)
     */
    @Override
    public boolean visit(NormalAnnotation node) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.NullLiteral)
     */
    @Override
    public boolean visit(NullLiteral node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.NumberLiteral)
     */
    @Override
    public boolean visit(NumberLiteral node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.PackageDeclaration)
     */
    @Override
    public boolean visit(PackageDeclaration node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ParenthesizedExpression)
     */
    @Override
    public boolean visit(ParenthesizedExpression node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.PostfixExpression)
     */
    @Override
    public boolean visit(PostfixExpression node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.PrefixExpression)
     */
    @Override
    public boolean visit(PrefixExpression node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.PrimitiveType)
     */
    @Override
    public boolean visit(PrimitiveType node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.QualifiedName)
     */
    @Override
    public boolean visit(QualifiedName node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ReturnStatement)
     */
    @Override
    public boolean visit(ReturnStatement node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SimpleName)
     */
    @Override
    public boolean visit(SimpleName node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SimpleType)
     */
    @Override
    public boolean visit(SimpleType node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SingleMemberAnnotation)
     */
    @Override
    public boolean visit(SingleMemberAnnotation node) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SingleVariableDeclaration)
     */
    @Override
    public boolean visit(SingleVariableDeclaration node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.StringLiteral)
     */
    @Override
    public boolean visit(StringLiteral node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SuperConstructorInvocation)
     */
    @Override
    public boolean visit(SuperConstructorInvocation node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SuperFieldAccess)
     */
    @Override
    public boolean visit(SuperFieldAccess node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SuperMethodInvocation)
     */
    @Override
    public boolean visit(SuperMethodInvocation node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SwitchCase)
     */
    @Override
    public boolean visit(SwitchCase node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SwitchStatement)
     */
    @Override
    public boolean visit(SwitchStatement node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SynchronizedStatement)
     */
    @Override
    public boolean visit(SynchronizedStatement node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ThisExpression)
     */
    @Override
    public boolean visit(ThisExpression node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ThrowStatement)
     */
    @Override
    public boolean visit(ThrowStatement node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.TryStatement)
     */
    @Override
    public boolean visit(TryStatement node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.TypeDeclaration)
     */
    @Override
    public boolean visit(TypeDeclaration node) {
        add(fCreator.create(node));
        return true;

        // TODO account for enums and annotations
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.TypeDeclarationStatement)
     */
    @Override
    public boolean visit(TypeDeclarationStatement node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.TypeLiteral)
     */
    @Override
    public boolean visit(TypeLiteral node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.VariableDeclarationExpression)
     */
    @Override
    public boolean visit(VariableDeclarationExpression node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.VariableDeclarationFragment)
     */
    @Override
    public boolean visit(VariableDeclarationFragment node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.VariableDeclarationStatement)
     */
    @Override
    public boolean visit(VariableDeclarationStatement node) {
        add(fCreator.create(node));
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.WhileStatement)
     */
    @Override
    public boolean visit(WhileStatement node) {
        add(fCreator.create(node));
        return true;
    }
}