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
package com.codenvy.ide.java.client.internal.compiler.problem;

import com.codenvy.ide.java.client.core.compiler.CategorizedProblem;
import com.codenvy.ide.java.client.internal.compiler.CompilationResult;

/*
 * Special unchecked exception type used
 * to abort from the compilation process
 *
 * should only be thrown from within problem handlers.
 */
public class AbortCompilationUnit extends AbortCompilation {

    private static final long serialVersionUID = -4253893529982226734L; // backward compatible

    public String encoding;

    public AbortCompilationUnit(CompilationResult compilationResult, CategorizedProblem problem) {
        super(compilationResult, problem);
    }

}