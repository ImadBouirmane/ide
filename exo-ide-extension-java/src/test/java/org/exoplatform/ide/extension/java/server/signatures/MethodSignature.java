/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.extension.java.server.signatures;

import org.exoplatform.ide.codeassistant.jvm.bean.TypeInfoBean;
import org.exoplatform.ide.codeassistant.jvm.shared.JavaType;
import org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Modifier;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 10:56:46 AM Mar 15, 2012 evgen $
 */
@RunWith(MockitoJUnitRunner.class)
public class MethodSignature extends SignatureBase {

    @Test
    public void metgodHasNoGeneric() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.List;\n");
        b.append("public class TestClass<E>{\n");
        b.append("public void method(){}\n}");
        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        MethodInfo methodInfo = typeInfo.getMethods().get(0);
        assertThat(methodInfo.getSignature()).isNullOrEmpty();
    }

    @Test
    public void metgodHasGericParameter() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.List;\n");
        b.append("public class TestClass<E>{\n");
        b.append("public int method(List<E> list){\n");
        b.append("retrun 0;");
        b.append(" }\n}");
        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        MethodInfo methodInfo = typeInfo.getMethods().get(0);
        assertThat(methodInfo.getSignature()).isNotNull().isEqualTo("(Ljava/util/List<TE;>;)I");
    }

    @Test
    public void metgodWithArrayAndGericParameter() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.List;\n");
        b.append("public class TestClass<E>{\n");
        b.append("public int method(String[] tt, List<String> list){\n");
        b.append("retrun 0;");
        b.append(" }\n}");
        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        MethodInfo methodInfo = typeInfo.getMethods().get(0);
        assertThat(methodInfo.getSignature()).isNotNull().isEqualTo("([Ljava/lang/String;Ljava/util/List<Ljava/lang/String;>;)I");
    }

    @Test
    public void metgodReturnGeneric() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.List;\n");
        b.append("public class TestClass{\n");
        b.append("public <E> E method(List<E> list){\n");
        b.append("retrun null;");
        b.append(" }\n}");
        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        MethodInfo methodInfo = typeInfo.getMethods().get(0);
        assertThat(methodInfo.getSignature()).isNotNull().isEqualTo("<E:Ljava/lang/Object;>(Ljava/util/List<TE;>;)TE;");
    }

    @Test
    public void metgodReturnPrimitive() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.List;\n");
        b.append("public class TestClass{\n");
        b.append("public <E> boolean method(List<E> list){\n");
        b.append("retrun false;");
        b.append(" }\n}");
        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        MethodInfo methodInfo = typeInfo.getMethods().get(0);
        assertThat(methodInfo.getSignature()).isNotNull().isEqualTo("<E:Ljava/lang/Object;>(Ljava/util/List<TE;>;)Z");
    }

    @Test
    public void boundedParameter() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.List;\n");
        b.append("public class TestClass{\n");
        b.append("public <E extends String> E method(List<E> list){\n");
        b.append("retrun null;");
        b.append(" }\n}");
        TypeInfo type = new TypeInfoBean();
        type.setType(JavaType.CLASS.toString());
        when(storage.getTypeByFqn(anyString(), anySetOf(String.class))).thenReturn(type);

        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        MethodInfo methodInfo = typeInfo.getMethods().get(0);
        assertThat(methodInfo.getSignature()).isNotNull().isEqualTo("<E:Ljava/lang/String;>(Ljava/util/List<TE;>;)TE;");
    }

    @Test
    public void boundedParameterIsInterface() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.List;\n");
        b.append("public class TestClass{\n");
        b.append("public <E extends List> E method(List<E> list){\n");
        b.append("retrun null;");
        b.append(" }\n}");
        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        MethodInfo methodInfo = typeInfo.getMethods().get(0);
        assertThat(methodInfo.getSignature()).isNotNull().isEqualTo("<E::Ljava/util/List;>(Ljava/util/List<TE;>;)TE;");
    }

    @Test
    public void multipleBounds() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.List;\n");
        b.append("public class TestClass{\n");
        b.append("public <E extends String & List> E method(List<E> list){\n");
        b.append("retrun null;");
        b.append(" }\n}");
        TypeInfo type = new TypeInfoBean();
        type.setType(JavaType.CLASS.toString());
        when(storage.getTypeByFqn(eq("java.lang.String"), anySetOf(String.class))).thenReturn(type);

        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        MethodInfo methodInfo = typeInfo.getMethods().get(0);
        assertThat(methodInfo.getSignature()).isNotNull().isEqualTo("<E:Ljava/lang/String;:Ljava/util/List;>(Ljava/util/List<TE;>;)TE;");
    }


    @Test
    public void complexSignature() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("import java.util.List;\n");
        b.append("import java.util.Comparator;\n");
        b.append("public class TestClass{\n");
        b.append("public <T extends List<S>, S> List<Comparator<S>> squeezeSuperExtendsWithFruit(List<? extends T> fruits){\n");
        b.append("retrun null;");
        b.append(" }\n}");
        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        MethodInfo methodInfo = typeInfo.getMethods().get(0);
        assertThat(methodInfo.getSignature()).isNotNull().isEqualTo(
                "<T::Ljava/util/List<TS;>;S:Ljava/lang/Object;>(Ljava/util/List<+TT;>;)Ljava/util/List<Ljava/util/Comparator<TS;>;>;");
    }

    @Test
    public void interfaceMethodModifier() throws Exception {
        StringBuilder b = new StringBuilder("package test;\n");
        b.append("public interface TestClass{\n");
        b.append(" void doSome();\n");
        b.append("\n}");
        TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
        MethodInfo methodInfo = typeInfo.getMethods().get(0);
        assertThat(Modifier.isAbstract(methodInfo.getModifiers())).isTrue();

    }

}