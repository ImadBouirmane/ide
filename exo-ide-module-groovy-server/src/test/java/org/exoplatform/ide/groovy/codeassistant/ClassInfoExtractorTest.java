/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.groovy.codeassistant;

import org.exoplatform.ide.groovy.codeassistant.bean.TypeInfo;
import org.exoplatform.ide.groovy.codeassistant.bean.FieldInfo;
import org.exoplatform.ide.groovy.codeassistant.bean.MethodInfo;
import org.exoplatform.ide.groovy.codeassistant.extractors.TypeInfoExtractor;
import org.exoplatform.ws.frameworks.json.JsonGenerator;
import org.exoplatform.ws.frameworks.json.impl.JsonGeneratorImpl;
import org.exoplatform.ws.frameworks.json.impl.ObjectBuilder;
import org.exoplatform.ws.frameworks.json.value.JsonValue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import junit.framework.TestCase;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class ClassInfoExtractorTest extends TestCase
{

   public void testExctractClass()
   {
      TypeInfo cd = TypeInfoExtractor.extract(A.class);
      assertEquals(A.class.getDeclaredConstructors().length, cd.getDeclaredConstructors().length);
      assertEquals(A.class.getConstructors().length, cd.getConstructors().length);
      assertEquals(A.class.getDeclaredMethods().length, cd.getDeclaredMethods().length);
      assertEquals(A.class.getMethods().length, cd.getMethods().length);
      assertEquals(A.class.getFields().length, cd.getFields().length);
      assertEquals(A.class.getDeclaredFields().length, cd.getDeclaredFields().length); 
      assertEquals(A.class.getCanonicalName(), cd.getQualifiedName());
      assertEquals(A.class.getSimpleName(), cd.getName());
   }
   
   
   public void testExctractField()
   {
      TypeInfo cd = TypeInfoExtractor.extract(A.class);
      FieldInfo[] fds = cd.getDeclaredFields();
      Field[] fields = A.class.getDeclaredFields();
      for (int i = 0; i < fields.length; i++)
      {
         FieldInfo fd =getFieldInfo(fds, fields[i].getName());
         if (fd == null)
         {
            fail();
         }
         assertEquals(fields[i].getModifiers(), fd.getModifiers().intValue());
         assertEquals(fields[i].getType().getCanonicalName(), fd.getType());
      }
   }

   private FieldInfo getFieldInfo(FieldInfo[] fds, String fieldName)
   {
      for (FieldInfo fd : fds)
      {
         if (fd.getName().equals(fieldName))
            return fd;
      }
      return null;   
   }
   
   
   public void testExctractMethod() 
   {
      TypeInfo cd = TypeInfoExtractor.extract(B.class);
      MethodInfo[] mds = cd.getDeclaredMethods();
      Method[] methods = B.class.getDeclaredMethods();
      for (int i = 0; i < methods.length; i++)
      {
         MethodInfo md =getMethodInfo(mds, methods[i].toGenericString());
         if (md == null)
         {
            fail();
         }
         assertEquals(methods[i].getModifiers(), md.getModifiers().intValue());
      }
   }

   private MethodInfo getMethodInfo(MethodInfo[] mds, String generic)
   {
      for (MethodInfo md : mds)
      {
         if (md.getGeneric().equals(generic))
            return md;
      }
      return null;   
   }
}
