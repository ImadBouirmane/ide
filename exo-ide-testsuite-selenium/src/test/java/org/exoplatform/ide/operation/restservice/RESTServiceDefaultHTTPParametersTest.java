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
package org.exoplatform.ide.operation.restservice;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.Utils;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class RESTServiceDefaultHTTPParametersTest extends BaseTest
{

   private final static String FILE_NAME = RESTServiceDefaultHTTPParametersTest.class.getSimpleName();

   private final static String TEST_FOLDER = "DefaultHTTPParameters";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + TEST_FOLDER + "/";

   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/restservice/DefaultHTTPParameters.groovy";
      try
      {
         //**************TODO***********change add folder for locked file
         VirtualFileSystemUtils.mkcol(URL);
         //***********************************************************

         VirtualFileSystemUtils.put(filePath, MimeType.GROOVY_SERVICE, URL + FILE_NAME);
         Thread.sleep(TestConstants.SLEEP_SHORT);
         Utils.deployService(BASE_URL, REST_CONTEXT, URL + FILE_NAME);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
      catch (InterruptedException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testDefaultHTTPParameters() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      IDE.NAVIGATION.selectItem(WS_URL);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      IDE.NAVIGATION.clickOpenIconOfFolder(URL);
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(URL + FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);
      IDE.REST_SERVICE.launchRestService();
      checkParam();
      IDE.REST_SERVICE.setMethodFieldValue("GET");

      checkParam();

      IDE.REST_SERVICE.closeForm();

   }

   /**
    *  Check Request parameters
    */
   private void checkParam()
   {
      assertEquals("TestQueryParam 1", IDE.REST_SERVICE.getQueryParameterName(1));
      assertEquals("boolean", IDE.REST_SERVICE.getQueryParameterType(1));
      assertEquals("true", IDE.REST_SERVICE.getQueryParameterDefaultValue(1));
      assertEquals("", IDE.REST_SERVICE.getQueryParameterValue(1));

      IDE.REST_SERVICE.selectHeaderParametersTab();

      assertEquals("Test-Header", IDE.REST_SERVICE.getHeaderParameterName(1));
      assertEquals("integer", IDE.REST_SERVICE.getHeaderParameterType(1));
      assertEquals("3", IDE.REST_SERVICE.getHeaderParameterDefaultValue(1));
      assertEquals("", IDE.REST_SERVICE.getHeaderParameterValue(1));

      IDE.REST_SERVICE.selectQueryParametersTab();
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         Utils.undeployService(BASE_URL, REST_CONTEXT, URL);
         VirtualFileSystemUtils.delete(URL);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }

}
