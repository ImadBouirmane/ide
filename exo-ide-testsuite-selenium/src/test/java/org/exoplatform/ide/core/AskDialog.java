/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.core;

import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.TestConstants;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: AskDialog Apr 27, 2011 2:28:35 PM evgen $
 *
 */
public class AskDialog extends AbstractTestModule
{
   interface Locators
   {
      String ASK_ID = "exoAskDialog";

      String NO_BUTTON = "exoAskDialogNoButton";
      
      String ASK_TITLE = "//div[@id='"+ ASK_ID +"']//div[@class='Caption']/span";
   }
   
   public void assertOpened()
   {
      assertTrue(selenium().isElementPresent(Locators.ASK_ID));
   }
   
   public void assertOpened(String message) {
      assertTrue(isDialogOpened(message));
   }
   
   public boolean isDialogOpened()
   {
      return selenium().isElementPresent(Locators.ASK_ID);
   }
   
   public boolean isDialogOpened(String message)
   {
      return selenium().isElementPresent(Locators.ASK_TITLE + "[contains(text(), '" + message+"')]");
   }
   
   public void clickNo() throws Exception
   {
      selenium().click(Locators.NO_BUTTON);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   public void clickYes() throws Exception {
      selenium().click("//div[@id='exoAskDialog']//div[@id='exoAskDialogYesButton']");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

}
