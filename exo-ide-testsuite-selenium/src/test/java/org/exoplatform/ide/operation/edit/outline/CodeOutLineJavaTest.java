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
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Outline.TokenType;
import org.exoplatform.ide.operation.autocompletion.CodeAssistantBaseTest;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a> 
 * @version $Id: Oct 25, 2010 $
 *
 */
public class CodeOutLineJavaTest extends CodeAssistantBaseTest
{
   private final static String FILE_NAME = "JavaCodeOutline.java";
   
   private OulineTreeHelper outlineTreeHelper;
   
   public CodeOutLineJavaTest()
   {
      this.outlineTreeHelper = new OulineTreeHelper();
   }

   @BeforeClass
   public static void setUp()
   {
      try
      {
         createProject(CodeOutLineChromatticTest.class.getSimpleName());
         VirtualFileSystemUtils.createFileFromLocal(project.get(Link.REL_CREATE_FILE), FILE_NAME,
            MimeType.APPLICATION_JAVA,
            "src/test/resources/org/exoplatform/ide/operation/edit/outline/" + FILE_NAME);
      }
      catch (Exception e)
      {
      }
   }
   
   @Before
   public void openFile() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitForItem(projectName + "/" + FILE_NAME);
      IDE.PROJECT.EXPLORER.openItem(projectName + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(projectName + "/" + FILE_NAME);
   }   

   @Test
   @Ignore
   //Java Outline is changed
   public void testCodeOutLineJava() throws Exception
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
      outlineTreeHelper.addOutlineItem("JavaCodeOutline", 8, TokenType.CLASS, "JavaCodeOutline");
      outlineTreeHelper.addOutlineItem("f@border : Color", 11, TokenType.PROPERTY, "border");
      outlineTreeHelper.addOutlineItem("sname : String", 12, TokenType.PROPERTY, "name");
      outlineTreeHelper.addOutlineItem("sfill : String", 12, false, TokenType.PROPERTY, "fill");      
      outlineTreeHelper.addOutlineItem("ColoredRect(int, int, int, int, Color, Color) : void", 17, TokenType.METHOD, "ColoredRect");
      outlineTreeHelper.addOutlineItem("draw(@Graphics) : void", 27, TokenType.METHOD, "draw");
      outlineTreeHelper.addOutlineItem("color : String", 30, false, TokenType.VARIABLE, "color");   // false, because outline node is not highlighted from test, but highlighted when goto this line manually
      
      outlineTreeHelper.addOutlineItem("@get(@java.lang.List<? extends Tree>) : Collection<HashMap<String,String>>", 38, TokenType.METHOD);
      outlineTreeHelper.addOutlineItem("var1 : List<String>", 39, TokenType.VARIABLE);
      outlineTreeHelper.addOutlineItem("var2 : List<String>", 48, TokenType.VARIABLE);
      
      outlineTreeHelper.addOutlineItem("JavaCodeOutline() : JavaCodeOutline", 55, TokenType.METHOD, "JavaCodeOutline");
      
      outlineTreeHelper.addOutlineItem("@add(HashMap<String,String>) : HashMap<String,String>", 60, TokenType.METHOD);
      outlineTreeHelper.addOutlineItem("addVar1 : List<Tree>", 63, false, TokenType.VARIABLE);   // false, because outline node is not highlighted from test, but highlighted when goto this line manually
            
      outlineTreeHelper.addOutlineItem("JavaCodeOutline(String) : JavaCodeOutline", 69, TokenType.METHOD, "JavaCodeOutline");
      outlineTreeHelper.addOutlineItem("@JavaCodeOutline(String, HashMap<String,String>) : JavaCodeOutline", 74, false, TokenType.METHOD, "JavaCodeOutline");      
      
      
      Thread.sleep(TestConstants.SLEEP * 3);
      
      // check is tree created correctly      
      outlineTreeHelper.checkOutlineTree();
   }

}