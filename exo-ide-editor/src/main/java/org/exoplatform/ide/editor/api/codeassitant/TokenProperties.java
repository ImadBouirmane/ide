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
package org.exoplatform.ide.editor.api.codeassitant;

/**
 * Properties, that can be applied to {@link Token}
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 17, 2010 5:31:49 PM evgen $
 *
 */
public interface TokenProperties
{

   public static String CLASS = "CLASS";

   public static String FQN = "FQN";

   public static String PACKAGE = "PACKAGE";

   public static String MODIFIERS = "MODIFIERS";

   public static String DECLARING_CLASS = "DECLARINGCLASS";

   public static String GENERIC_PARAMETER_TYPES = "GENERICPARAMETERTYPES";

   public static String TYPE = "TYPE";

   public static String GENERIC_RETURN_TYPE = "GENERICRETURNTYPE";

   public static String PARAMETER_TYPES = "PARAMETERTYPES";

   public static String RETURN_TYPE = "RETURNTYPE";
   
   /**
    * Code, that will be inserted if token selected.
    * Used only for template tokens.
    */
   public static String CODE = "CODE";
   
   /**
    * Used for template tokens.
    * Short hint, that will be displayed near name in autocomplete form.
    * Can help user quickly to identify the purpose of template.
    */
   public static String SHORT_HINT =  "SHORT-HINT";
   
   /**
    * Used for template tokens.
    * Full text of template, that will be inserted to code editor.
    * Also, can be shown in javadoc window.
    */
   public static String FULL_TEXT =  "FULL-TEXT";
   
   
   /**
    * Used for sub token list property.
    * Contains {@link ArrayProperty} 
    */
   public static String CHILD_TOKEN_LIST =  "CHILD-TOKEN-LIST";
}
