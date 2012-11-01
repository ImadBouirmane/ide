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
package org.exoplatform.ide.editor.netvibes.client;

import com.google.gwt.resources.client.ImageResource;

import com.google.gwt.core.client.GWT;

import com.google.gwt.resources.client.ClientBundle;

import org.exoplatform.gwtframework.ui.client.util.UIHelper;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Images Mar 11, 2011 3:07:44 PM evgen $
 *
 */
public interface Images extends ClientBundle
{
   Images INSTANCE = GWT.create(Images.class);
   
   public static final String IMAGE_URL = UIHelper.getGadgetImagesURL();

   public static final String UWA_WIGET = IMAGE_URL + "uwa-widget.png";
   
   @Source("org/exoplatform/ide/editor/netvibes/public/images/uwa-widget.png")
   ImageResource uwa();
}