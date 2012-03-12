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
package org.exoplatform.ide.client.model.template;

import com.google.gwt.resources.client.ImageResource;

import org.exoplatform.ide.client.IDEImageBundle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 * 
 */
public class FolderTemplate extends Template
{
   private List<Template> children;

   public FolderTemplate()
   {
   }

   public FolderTemplate(String name)
   {
      super(name);
   }

   public FolderTemplate(String name, String description, String nodeName, List<Template> children)
   {
      super(name, description, nodeName);
      this.children = children;
   }

   public FolderTemplate(String name, String description, boolean isDefault)
   {
      super(name, description, isDefault);
   }

   /**
    * @return children on template
    */
   public List<Template> getChildren()
   {
      if (children == null)
      {
         children = new ArrayList<Template>();
      }
      return children;
   }

   public void setChildren(List<Template> children)
   {
      this.children = children;
   }

   /**
    * @see org.exoplatform.ide.client.model.template.Template#getIcon()
    */
   @Override
   public ImageResource getIcon()
   {
      return IDEImageBundle.INSTANCE.folder();
   }
}