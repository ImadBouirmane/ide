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
package org.exoplatform.ide.extension.gadget.client.ui;

import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.ui.PreviewFrame;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.gadget.shared.GadgetMetadata;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class GadgetPreviewPane extends ViewImpl
{

   /**
    * 
    */
   public static final String TITLE = "Preview";

   private String meta;

   private GadgetMetadata metadata;

   /**
    * 
    */
   public static final String ID = "gadgetpreview";

   private IDEConfiguration configuration;

   private FlowPanel framePanel;
   
   private String href;

   /**
    * @param eventBus
    * @param gadgetMetadata
    */
   public GadgetPreviewPane(String href)
   {
      
      super(ID, ViewType.OPERATION, TITLE);
      this.href =  href;
      framePanel = new FlowPanel();
      framePanel.setSize("100%", "100%");
      add(framePanel);

   }

   /**
    * @param configuration the configuration to set
    */
   public void setConfiguration(IDEConfiguration configuration)
   {
      this.configuration = configuration;
   }

   /**
    * @param metadata the metadata to set
    */
   public void setMetadata(GadgetMetadata metadata)
   {
      this.metadata = metadata;
   }

   private native String getST()/*-{
                                return encodeURIComponent(gadgets.util.getUrlParameters().st);
                                }-*/;

   private native String getGadgetParent()/*-{
                                          return encodeURIComponent(gadgets.util.getUrlParameters().parent);
                                          }-*/;

   private native boolean isGadget()/*-{
                                    return (typeof (gadgets) !== "undefined" && typeof (gadgets) !== "null");
                                    }-*/;

   /**
    * Create iframe. Gadget will be load here.
    * 
    */
   public void showGadget()
   {
      
     
      String url = configuration.getGadgetServer() // 
                   + "samplecontainer/samplecontainer.html?url=" + href;
      final PreviewFrame frame = new PreviewFrame(url);
      DOM.setElementAttribute(frame.getElement(), "scrolling", "no");
      DOM.setElementAttribute(frame.getElement(), "frameborder", "0");
      frame.setWidth("100%");
      frame.setHeight("100%");
      frame.setStyleName("");
      frame.addLoadHandler(new LoadHandler()
      {

         @Override
         public void onLoad(LoadEvent event)
         {
            setHandler(IFrameElement.as(frame.getElement()));
         }
      });

      framePanel.clear();
      framePanel.add(frame);
   }

   private native void setHandler(Element e)/*-{
                                            var type = "mousedown";
                                            var instance = this;
                                            if (typeof e.contentDocument != "undefined") {
                                            e.contentDocument
                                            .addEventListener(
                                            type,
                                            function() {
                                            instance.@org.exoplatform.ide.extension.gadget.client.ui.GadgetPreviewPane::activate()();
                                            }, false);
                                            } else if (typeof e.contentWindow != "undefined") {
                                            e.contentWindow.document
                                            .attachEvent(
                                            "on" + type,
                                            function() {
                                            instance.@org.exoplatform.ide.extension.gadget.client.ui.GadgetPreviewPane::activate()();
                                            });
                                            }
                                            }-*/;

   public String getId()
   {
      return ID;
   }

}
