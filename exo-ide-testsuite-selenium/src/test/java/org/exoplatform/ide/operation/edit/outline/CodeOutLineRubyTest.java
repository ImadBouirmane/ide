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
package org.exoplatform.ide.operation.edit.outline;

import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Outline.TokenType;
import org.exoplatform.ide.operation.autocompletion.CodeAssistantBaseTest;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a> 
 * @version $Id: Oct 25, 2010 $
 *
 */
public class CodeOutLineRubyTest extends CodeAssistantBaseTest
{
   private final static String FILE_NAME = "TestRubyFile.rb";

   private OulineTreeHelper outlineTreeHelper;

   public CodeOutLineRubyTest()
   {
      this.outlineTreeHelper = new OulineTreeHelper();
   }

   @BeforeClass
   public static void setUp()
   {
      try
      {
         createProject(CodeOutLineRubyTest.class.getSimpleName());
         VirtualFileSystemUtils.createFileFromLocal(project.get(Link.REL_CREATE_FILE), FILE_NAME,
            MimeType.APPLICATION_RUBY,
            "src/test/resources/org/exoplatform/ide/operation/edit/outline/" + FILE_NAME);
      }
      catch (Exception e)
      {
      }
   }
   
   @Before
   public void openFile() throws Exception
   {
      openProject();
      IDE.PROJECT.EXPLORER.waitForItem(projectName + "/" + FILE_NAME);
      IDE.PROJECT.EXPLORER.openItem(projectName + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(projectName + "/" + FILE_NAME);
   }

   @Test
   public void testCodeOutLineRuby() throws Exception
   {     
      // open outline panel
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      IDE.OUTLINE.waitOutlineTreeVisible();
      // check for presence and visibility of outline tab
      assertTrue(IDE.OUTLINE.isOutlineTreePresent());
      assertTrue(IDE.OUTLINE.isOutlineViewVisible());
      
      // expand outline tree
      outlineTreeHelper.expandOutlineTree();

      // create outline tree map
      OulineTreeHelper.init();
      outlineTreeHelper.addOutlineItem("TOPLEVEL_CONSTANT : String", 5, TokenType.CONSTANT);
      outlineTreeHelper.addOutlineItem("@@n2 : Array", 7, TokenType.CLASS_VARIABLE);
      outlineTreeHelper.addOutlineItem("$global : nil", 11, TokenType.GLOBAL_VARIABLE);
      outlineTreeHelper.addOutlineItem("@n1 : nil", 12, TokenType.INSTANCE_VARIABLE);
      outlineTreeHelper.addOutlineItem("ClassName", 17, TokenType.CLASS);
      outlineTreeHelper.addOutlineItem("CLASS_CONSTANT : Fixnum", 20, TokenType.CONSTANT);
      outlineTreeHelper.addOutlineItem("$global1 : Object", 22, TokenType.GLOBAL_VARIABLE);
      outlineTreeHelper.addOutlineItem("@@class_variable : Regexp", 23, TokenType.CLASS_VARIABLE);
      outlineTreeHelper.addOutlineItem("@field : Object", 24, TokenType.INSTANCE_VARIABLE);
      outlineTreeHelper.addOutlineItem("initialize()", 26, TokenType.METHOD);
      outlineTreeHelper.addOutlineItem("@@char : Ascii", 29, false, TokenType.CLASS_VARIABLE);
      outlineTreeHelper.addOutlineItem("$myFile : File", 33, TokenType.GLOBAL_VARIABLE);
      outlineTreeHelper.addOutlineItem("scale : String", 35, TokenType.LOCAL_VARIABLE);
      outlineTreeHelper.addOutlineItem("f : Object", 38, TokenType.LOCAL_VARIABLE);
      outlineTreeHelper.addOutlineItem("@str4 : String", 40, TokenType.INSTANCE_VARIABLE);
      outlineTreeHelper.addOutlineItem("@n1 : Float", 52, TokenType.INSTANCE_VARIABLE);
      outlineTreeHelper.addOutlineItem("@@n2 : Float", 53, TokenType.CLASS_VARIABLE);
      outlineTreeHelper.addOutlineItem("a : ClassName", 56, TokenType.LOCAL_VARIABLE);
      outlineTreeHelper.addOutlineItem("TC_MyTest", 58, TokenType.CLASS);
      outlineTreeHelper.addOutlineItem("foo()", 60, TokenType.METHOD);
      outlineTreeHelper.addOutlineItem("@@class_variable : Hash", 62, false, TokenType.CLASS_VARIABLE);
      outlineTreeHelper.addOutlineItem("@field : Number", 63, false, TokenType.INSTANCE_VARIABLE);
      outlineTreeHelper.addOutlineItem("a : Fixnum", 68, TokenType.LOCAL_VARIABLE);
      outlineTreeHelper.addOutlineItem("TestModule", 71, TokenType.MODULE);
      outlineTreeHelper.addOutlineItem("method()", 72, TokenType.METHOD);
      outlineTreeHelper.addOutlineItem("@@t : TrueClass", 73, TokenType.CLASS_VARIABLE);
      outlineTreeHelper.addOutlineItem("A : Symbol", 76, TokenType.CONSTANT);
      outlineTreeHelper.addOutlineItem("ascii1 : Ascii", 77, false, TokenType.LOCAL_VARIABLE); // false, because outline node is not highlighted from test, but highlighted when goto this line manually
      outlineTreeHelper.addOutlineItem("@field : nil", 78, TokenType.INSTANCE_VARIABLE);

      // check is tree created correctly      
      outlineTreeHelper.checkOutlineTree();
   }

}