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
package org.exoplatform.ide.extension.openshift.client.controls;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.extension.openshift.client.OpenShiftClientBundle;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;
import org.exoplatform.ide.extension.openshift.client.delete.DeleteApplicationEvent;

/**
 * Control is used for deleting application from OpenShift.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 9, 2011 12:17:37 PM anya $
 *
 */
public class DeleteApplicationControl extends SimpleControl implements IDEControl
{
   public DeleteApplicationControl()
   {
      super(OpenShiftExtension.LOCALIZATION_CONSTANT.deleteApplicationControlId());
      setTitle(OpenShiftExtension.LOCALIZATION_CONSTANT.deleteApplicationControlTitle());
      setPrompt(OpenShiftExtension.LOCALIZATION_CONSTANT.deleteApplicationControlPrompt());
      setImages(OpenShiftClientBundle.INSTANCE.destroyApplicationControl(),
         OpenShiftClientBundle.INSTANCE.destroyApplicationControlDisabled());
      setEvent(new DeleteApplicationEvent());
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   @Override
   public void initialize(HandlerManager eventBus)
   {
      setVisible(true);
      setEnabled(true);
   }
}
