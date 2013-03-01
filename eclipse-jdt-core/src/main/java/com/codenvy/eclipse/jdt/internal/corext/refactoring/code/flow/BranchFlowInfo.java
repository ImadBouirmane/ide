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
package com.codenvy.eclipse.jdt.internal.corext.refactoring.code.flow;

import com.codenvy.eclipse.jdt.core.dom.SimpleName;

import java.util.HashSet;

class BranchFlowInfo extends FlowInfo
{

   public BranchFlowInfo(SimpleName label, FlowContext context)
   {
      super(NO_RETURN);
      fBranches = new HashSet<String>(2);
      fBranches.add(makeString(label));
   }
}

