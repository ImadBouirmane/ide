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
package org.exoplatform.ide.operation.templates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;

import com.thoughtworks.selenium.Selenium;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class TemplateUtils
{
   static final String CREATE_BUTTON_LOCATOR = "scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCreateButton\"]/";
   
   static final String DELETE_BUTTON_LOCATOR = "scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormDeleteButton\"]/";
   
   static final String CANCEL_BUTTON_LOCATOR = "scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCancelButton\"]/";
   
   static final String NAME_FIELD_LOCATOR = "scLocator=//DynamicForm[ID=\"ideCreateFileFromTemplateFormDynamicForm\"]/item[name=ideCreateFileFromTemplateFormFileNameField]/element";
   
   public static final String DEFAULT_PROJECT_TEMPLATE_NAME = "ide-project";
   
   public static void checkCreateProjectFromTemplateForm(Selenium selenium)
   {
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/"));
      assertEquals("Create project", selenium.getText("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/header/"));
      assertTrue(selenium.isElementPresent(DELETE_BUTTON_LOCATOR));
      assertTrue(selenium.isElementPresent(CREATE_BUTTON_LOCATOR));
      assertTrue(selenium.isElementPresent(CANCEL_BUTTON_LOCATOR));
      assertTrue(selenium.isElementPresent("scLocator=//DynamicForm[ID=\"ideCreateFileFromTemplateFormDynamicForm\"]/item[name=ideCreateFileFromTemplateFormFileNameField]/element"));
   }
   
   public static void selectProjectTemplate(Selenium selenium, String projectTemplateName) throws Exception
   {
      selenium.mouseDownAt("//div[@eventproxy='ideCreateFileFromTemplateFormTemplateListGrid_body']//span[@title='" 
         + projectTemplateName + "']", "");
      selenium.mouseUpAt("//div[@eventproxy='ideCreateFileFromTemplateFormTemplateListGrid_body']//span[@title='"
         + projectTemplateName + "']", "");
      
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }
   
   static void selectItemInTemplateList(Selenium selenium, String templateName) throws Exception
   {
      //Get rows count:
      int rows = (Integer)selenium.getXpathCount("//div[@eventproxy='ideCreateFileFromTemplateFormTemplateListGrid_body']/div[@eventproxy='ideCreateFileFromTemplateFormTemplateListGrid_body']/div/table/tbody[2]/tr");
      
      for (int i = 0; i < rows; i++)
      {
         String rowLocator =
            "scLocator=//ListGrid[ID=\"ideCreateFileFromTemplateFormTemplateListGrid\"]/body/row[" + i + "]/col[1]";
         String text = selenium.getText(rowLocator);
         if (templateName.equals(text))
         {
            selenium.click(rowLocator);
            break;
         }
      }
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   static void checkSaveAsTemplateWindow(Selenium selenium)
   {
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideSaveAsTemplateForm\"]/header/"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideSaveAsTemplateFormCancelButton\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideSaveAsTemplateFormSaveButton\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//DynamicForm[ID=\"ideSaveAsTemplateFormDynamicForm\"]/item[name=ideSaveAsTemplateFormTypeField]/element"));
      assertTrue(selenium.isElementPresent("scLocator=//DynamicForm[ID=\"ideSaveAsTemplateFormDynamicForm\"]/item[name=ideSaveAsTemplateFormNameField]/element"));
      assertTrue(selenium.isElementPresent("scLocator=//DynamicForm[ID=\"ideSaveAsTemplateFormDynamicForm\"]/item[name=ideSaveAsTemplateFormDescriptionField]/element"));
      assertTrue(selenium.isTextPresent("Type:"));
      assertTrue(selenium.isTextPresent("Name:"));
      assertTrue(selenium.isTextPresent("Description:"));
   }
   
   static void checkCreateFileFromTemplateWindow(Selenium selenium)
   {
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]"));
      assertEquals("Create file", selenium.getText("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/header"));
      assertTrue(selenium.isElementPresent("scLocator=//DynamicForm[ID=\"ideCreateFileFromTemplateFormDynamicForm\"]/item[name=ideCreateFileFromTemplateFormFileNameField]/element"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormDeleteButton\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCreateButton\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCancelButton\"]/"));
      //check that Delete and Create buttons are disabled and Cancel is enabled
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleDisabled']/table//td[text()='Delete']"));
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleDisabled']/table//td[text()='Create']"));
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitle']/table//td[text()='Cancel']"));
      //assert templates present
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='" + "Groovy REST Service" + "']"));
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='" + "Empty XML" + "']"));
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='" + "Empty HTML" + "']"));
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='" + "Empty TEXT" + "']"));
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='" + "Google Gadget" + "']"));
   }
   
   
   static void checkNameFieldEnabled(Selenium selenium, boolean enabled)
   {
      if (enabled)
      {
         assertFalse(selenium.isElementPresent("//div[@eventproxy='ideCreateFileFromTemplateForm']//input[@class='textItemDisabled']"));
         assertTrue(selenium.isElementPresent("//div[@eventproxy='ideCreateFileFromTemplateForm']//input[@class='textItem']"));
      }
      else
      {
         assertFalse(selenium.isElementPresent("//div[@eventproxy='ideCreateFileFromTemplateForm']//input[@class='textItem']"));
         assertTrue(selenium.isElementPresent("//div[@eventproxy='ideCreateFileFromTemplateForm']//input[@class='textItemDisabled']"));
      }
   }
   
   static void checkCreateButtonEnabled(Selenium selenium, boolean enabled)
   {
      if (enabled)
      {
         assertFalse(selenium
            .isElementPresent("//div[@eventproxy='ideCreateFileFromTemplateForm']//td[@class='buttonTitleDisabled' and text()='Create']"));
         assertTrue(selenium
            .isElementPresent("//div[@eventproxy='ideCreateFileFromTemplateForm']//td[@class='buttonTitle' and text()='Create']"));
      }
      else
      {
         assertFalse(selenium
            .isElementPresent("//div[@eventproxy='ideCreateFileFromTemplateForm']//td[@class='buttonTitle' and text()='Create']"));
         assertTrue(selenium
            .isElementPresent("//div[@eventproxy='ideCreateFileFromTemplateForm']//td[@class='buttonTitleDisabled' and text()='Create']"));
      }
   }
   
   static void checkDeleteButtonEnabled(Selenium selenium, boolean enabled)
   {
      if (enabled)
      {
         assertFalse(selenium
            .isElementPresent("//div[@eventproxy='ideCreateFileFromTemplateForm']//td[@class='buttonTitleDisabled' and text()='Delete']"));
         assertTrue(selenium
            .isElementPresent("//div[@eventproxy='ideCreateFileFromTemplateForm']//td[@class='buttonTitle' and text()='Delete']"));
      }
      else
      {
         assertFalse(selenium
            .isElementPresent("//div[@eventproxy='ideCreateFileFromTemplateForm']//td[@class='buttonTitle' and text()='Delete']"));
         assertTrue(selenium
            .isElementPresent("//div[@eventproxy='ideCreateFileFromTemplateForm']//td[@class='buttonTitleDisabled' and text()='Delete']"));
      }
   }
   
   static void closeCreateFromTemplateForm(Selenium selenium) throws Exception
   {
      selenium.click("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/closeButton/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   public static void typeProjectName(Selenium selenium, String projectName) throws Exception
   {
      selenium.type("scLocator=//DynamicForm[ID=\"ideCreateFileFromTemplateFormDynamicForm\"]/item[" 
         + "name=ideCreateFileFromTemplateFormFileNameField]/element", projectName);
      
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }
   
   public static void clickCreateProjectButton(Selenium selenium) throws Exception
   {
      selenium.click(CREATE_BUTTON_LOCATOR);
      Thread.sleep(TestConstants.SLEEP);
   }
   
   public static void clickCreateFileButton(Selenium selenium) throws Exception
   {
      selenium.click(CREATE_BUTTON_LOCATOR);
      Thread.sleep(TestConstants.SLEEP);
   }
   
   /**
    * Create project from template: 
    * 1. Call "Project From Template" form
    * 2. Select <code>templateName</code> template in list grid
    * 3. Type project name <code>projectName</code> (if null - leave the default project name)
    * 4. Click Create button
    * 
    * @param selenium - selenium
    * @param templateName - the template name in list grid
    * @param projectName - new project name (if null - project with default name will be created).
    * @throws Exception
    */
   public static void createProjectFromTemplate(Selenium selenium, String templateName, String projectName) throws Exception
   {
      BaseTest.IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.PROJECT_FROM_TEMPLATE);
      
      TemplateUtils.checkCreateProjectFromTemplateForm(selenium);
      TemplateUtils.selectProjectTemplate(selenium, templateName);
      if (projectName != null)
      {
         TemplateUtils.typeProjectName(selenium, projectName);
      }
      TemplateUtils.clickCreateProjectButton(selenium);
      Thread.sleep(TestConstants.SLEEP);
   }
}
