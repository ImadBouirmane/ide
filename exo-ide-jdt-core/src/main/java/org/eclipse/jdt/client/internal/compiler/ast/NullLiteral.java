/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.client.internal.compiler.ast;

import org.eclipse.jdt.client.internal.compiler.ASTVisitor;
import org.eclipse.jdt.client.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.client.internal.compiler.impl.Constant;
import org.eclipse.jdt.client.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.client.internal.compiler.lookup.TypeBinding;

public class NullLiteral extends MagicLiteral
{

   static final char[] source = {'n', 'u', 'l', 'l'};

   public NullLiteral(int s, int e)
   {

      super(s, e);
   }

   public void computeConstant()
   {

      this.constant = Constant.NotAConstant;
   }

   public TypeBinding literalType(BlockScope scope)
   {
      return TypeBinding.NULL;
   }

   public int nullStatus(FlowInfo flowInfo)
   {
      return FlowInfo.NULL;
   }

   public Object reusableJSRTarget()
   {
      return TypeBinding.NULL;
   }

   public char[] source()
   {
      return source;
   }

   public void traverse(ASTVisitor visitor, BlockScope scope)
   {
      visitor.visit(this, scope);
      visitor.endVisit(this, scope);
   }
}