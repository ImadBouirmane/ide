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
package org.eclipse.jdt.client.core;

import org.eclipse.jdt.client.core.compiler.CharOperation;
import org.eclipse.jdt.client.core.compiler.InvalidInputException;
import org.eclipse.jdt.client.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.client.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.client.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.client.internal.compiler.parser.Scanner;
import org.eclipse.jdt.client.internal.compiler.parser.ScannerHelper;
import org.eclipse.jdt.client.internal.compiler.parser.TerminalTokens;
import org.eclipse.jdt.client.runtime.IStatus;
import org.eclipse.jdt.client.runtime.Status;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class JavaConventions
{

   private static final char DOT = '.';

   private static final String PACKAGE_INFO = new String(TypeConstants.PACKAGE_INFO_NAME);

   private static final Scanner SCANNER =
      new Scanner(false /*comment*/, true /*whitespace*/, false /*nls*/, ClassFileConstants.JDK1_3 /*sourceLevel*/,
         null/*taskTag*/, null/*taskPriorities*/, true /*taskCaseSensitive*/);

   private static final IStatus VERIFIED_OK = new Status(IStatus.OK, JavaCore.PLUGIN_ID, "ok");

   /**
    * Validate the given package name for the given source and compliance levels.
    * <p>
    * The syntax of a package name corresponds to PackageName as
    * defined by PackageDeclaration (JLS2 7.4). For example, <code>"java.lang"</code>.
    * <p>
    * Note that the given name must be a non-empty package name (that is, attempting to
    * validate the default package will return an error status.)
    * Also it must not contain any characters or substrings that are not valid
    * on the file system on which workspace root is located.
    *
    * @param name the name of a package
    * @param sourceLevel the source level
    * @param complianceLevel the compliance level
    * @return a status object with code <code>IStatus.OK</code> if
    *    the given name is valid as a package name, otherwise a status
    *    object indicating what is wrong with the name
    * @since 3.3
    */
   public static IStatus validatePackageName(String name, String sourceLevel, String complianceLevel)
   {

      if (name == null)
      {
         return new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, Messages.INSTANCE.convention_package_nullName(), null);
      }
      int length;
      if ((length = name.length()) == 0)
      {
         return new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, Messages.INSTANCE.convention_package_emptyName(),
            null);
      }
      if (name.charAt(0) == DOT || name.charAt(length - 1) == DOT)
      {
         return new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, Messages.INSTANCE.convention_package_dotName(), null);
      }
      if (CharOperation.isWhitespace(name.charAt(0)) || CharOperation.isWhitespace(name.charAt(name.length() - 1)))
      {
         return new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1,
            Messages.INSTANCE.convention_package_nameWithBlanks(), null);
      }
      int dot = 0;
      while (dot != -1 && dot < length - 1)
      {
         if ((dot = name.indexOf(DOT, dot + 1)) != -1 && dot < length - 1 && name.charAt(dot + 1) == DOT)
         {
            return new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1,
               Messages.INSTANCE.convention_package_consecutiveDotsName(), null);
         }
      }
      //      IWorkspace workspace = ResourcesPlugin.getWorkspace();
      //      StringTokenizer st = new StringTokenizer(name, "."); //$NON-NLS-1$
      String[] split = name.split("\\.");
      boolean firstToken = true;
      IStatus warningStatus = null;
      for (String typeName : split)
      {
         //         String typeName = st.nextToken();
         typeName = typeName.trim(); // grammar allows spaces
         char[] scannedID = scannedIdentifier(typeName, sourceLevel, complianceLevel);
         if (scannedID == null)
         {
            return new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1,
               Messages.INSTANCE.convention_illegalIdentifier(typeName), null);
         }
         //         IStatus status = workspace.validateName(new String(scannedID), IResource.FOLDER);
         //         if (!status.isOK())
         //         {
         //            return status;
         //         }
         if (firstToken && scannedID.length > 0 && ScannerHelper.isUpperCase(scannedID[0]))
         {
            if (warningStatus == null)
            {
               warningStatus =
                  new Status(IStatus.WARNING, JavaCore.PLUGIN_ID, -1,
                     Messages.INSTANCE.convention_package_uppercaseName(), null);
            }
         }
         firstToken = false;
      }
      if (warningStatus != null)
      {
         return warningStatus;
      }
      return VERIFIED_OK;
   }

   /*
    * Returns the current identifier extracted by the scanner (without unicode
    * escapes) from the given id and for the given source and compliance levels.
    * Returns <code>null</code> if the id was not valid
    */
   private static char[] scannedIdentifier(String id, String sourceLevel, String complianceLevel)
   {
      if (id == null)
      {
         return null;
      }
      // Set scanner for given source and compliance levels
      SCANNER.sourceLevel =
         sourceLevel == null ? ClassFileConstants.JDK1_3 : CompilerOptions.versionToJdkLevel(sourceLevel);
      SCANNER.complianceLevel =
         complianceLevel == null ? ClassFileConstants.JDK1_3 : CompilerOptions.versionToJdkLevel(complianceLevel);

      try
      {
         SCANNER.setSource(id.toCharArray());
         int token = SCANNER.scanIdentifier();
         if (token != TerminalTokens.TokenNameIdentifier)
            return null;
         if (SCANNER.currentPosition == SCANNER.eofPosition)
         { // to handle case where we had an ArrayIndexOutOfBoundsException
            try
            {
               return SCANNER.getCurrentIdentifierSource();
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
               return null;
            }
         }
         else
         {
            return null;
         }
      }
      catch (InvalidInputException e)
      {
         return null;
      }
   }

   /**
    * Validate the given compilation unit name for the given source and compliance levels.
    * <p>
    * A compilation unit name must obey the following rules:
    * <ul>
    * <li> it must not be null
    * <li> it must be suffixed by a dot ('.') followed by one of the
    *       {@link JavaCore#getJavaLikeExtensions() Java-like extensions}
    * <li> its prefix must be a valid identifier
    * <li> it must not contain any characters or substrings that are not valid
    *       on the file system on which workspace root is located.
    * </ul>
    * </p>
    * @param name the name of a compilation unit
    * @param sourceLevel the source level
    * @param complianceLevel the compliance level
    * @return a status object with code <code>IStatus.OK</code> if
    *    the given name is valid as a compilation unit name, otherwise a status
    *    object indicating what is wrong with the name
    * @since 3.3
    */
   public static IStatus validateCompilationUnitName(String name, String sourceLevel, String complianceLevel)
   {
      if (name == null)
      {
         return new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, "Compilation unit name must not be null", null);
      }
      String message = "Compilation unit name must end with .java, or one of the registered Java-like extensions";
      //      if (!org.eclipse.jdt.client.internal.core.util.Util.isJavaLikeFileName(name))
      //      {
      //         return new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, message, null);
      //      }
      String identifier;
      int index;
      index = name.lastIndexOf('.');
      if (index == -1)
      {
         return new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, message, null);
      }
      identifier = name.substring(0, index);
      // JSR-175 metadata strongly recommends "package-info.java" as the
      // file in which to store package annotations and
      // the package-level spec (replaces package.html)
      if (!identifier.equals(PACKAGE_INFO))
      {
         IStatus status = validateIdentifier(identifier, sourceLevel, complianceLevel);
         if (!status.isOK())
         {
            return status;
         }
      }
      //      IStatus status = ResourcesPlugin.getWorkspace().validateName(name, IResource.FILE);
      //      if (!status.isOK()) {
      //         return status;
      //      }
      return Status.OK_STATUS;
   }

   /**
    * Validate the given Java identifier for the given source and compliance levels
    * The identifier must not have the same spelling as a Java keyword,
    * boolean literal (<code>"true"</code>, <code>"false"</code>), or null literal (<code>"null"</code>).
    * See section 3.8 of the <em>Java Language Specification, Second Edition</em> (JLS2).
    * A valid identifier can act as a simple type name, method name or field name.
    *
    * @param id the Java identifier
    * @param sourceLevel the source level
    * @param complianceLevel the compliance level
    * @return a status object with code <code>IStatus.OK</code> if
    *    the given identifier is a valid Java identifier, otherwise a status
    *    object indicating what is wrong with the identifier
    * @since 3.3
    */
   public static IStatus validateIdentifier(String id, String sourceLevel, String complianceLevel)
   {
      if (scannedIdentifier(id, sourceLevel, complianceLevel) != null)
      {
         return Status.OK_STATUS;
      }
      else
      {
         return new Status(IStatus.ERROR, JavaCore.PLUGIN_ID, -1, id + " is not a valid Java identifier", null);
      }
   }

   /**
    * Validate the given method name for the given source and compliance levels.
    * The special names "&lt;init&gt;" and "&lt;clinit&gt;" are not valid.
    * <p>
    * The syntax for a method  name is defined by Identifier
    * of MethodDeclarator (JLS2 8.4). For example "println".
    *
    * @param name the name of a method
    * @param sourceLevel the source level
    * @param complianceLevel the compliance level
    * @return a status object with code <code>IStatus.OK</code> if
    *    the given name is valid as a method name, otherwise a status
    *    object indicating what is wrong with the name
    * @since 3.3
    */
   public static IStatus validateMethodName(String name, String sourceLevel, String complianceLevel)
   {
      return validateIdentifier(name, sourceLevel, complianceLevel);
   }

}
