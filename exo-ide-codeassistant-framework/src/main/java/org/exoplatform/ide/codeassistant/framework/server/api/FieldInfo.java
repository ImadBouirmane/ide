/*
 * Copyright (C) 2010 eXo Platform SAS.
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

package org.exoplatform.ide.codeassistant.framework.server.api;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class FieldInfo extends Member
{
   
   private String type;
   
   private String declaringClass; 

   public FieldInfo(String type, Integer modifiers, String name, String declaringClass)
   {
      super(modifiers, name);
      this.type = type;
      this.declaringClass = declaringClass;
   }
   
   public FieldInfo()
   {
      
   }
   
   public String getType()
   {
      return type;
   }
   
   public void setType(String type)
   {
      this.type = type;
   }
   
   public String getDeclaringClass()
   {
      return declaringClass;
   }
   
   public void setDeclaringClass(String declaringClass)
   {
      this.declaringClass = declaringClass;
   }

}
