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
package org.exoplatform.ide.editor.ckeditor;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Label;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.editor.api.Capability;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.EditorContentChangedEvent;
import org.exoplatform.ide.editor.api.EditorCursorActivityEvent;
import org.exoplatform.ide.editor.api.EditorFocusReceivedEvent;
import org.exoplatform.ide.editor.api.EditorInitializedEvent;
import org.exoplatform.ide.editor.api.EditorParameters;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmitry Nochevnov</a>
 * @version $
 */

public class CKEditor extends AbsolutePanel implements Editor
{
   
   protected String editorId;

   private Label label;

   private JavaScriptObject editorObject;

   private int onContentChangeListenerId;

   private int onEditorResizeListenerId;

   private String prefix = "";

   private String suffix = "";

   private CKEditorConfiguration configuration;
   
   protected String content;

   protected Map<String, Object> params;
   
   private String mimeType;

   public CKEditor(String mimeType, String content, Map<String, Object> params)
   {
      this.mimeType = mimeType;
      this.content = content;
      this.params = params;

      this.editorId = "CKEditor - " + String.valueOf(this.hashCode());
      if (params == null)
      {
         params = new HashMap<String, Object>();
      }

      label = new Label();
      DOM.setElementAttribute(label.getElement(), "id", getId());
      DOM.setElementAttribute(label.getElement(), "style", "overflow: hidden; position:absolute; left:0px; top:0px; width: 100%; height: 100%;");
      add(label);

      if (params.get(EditorParameters.CONFIGURATION) != null) {
         configuration = (CKEditorConfiguration)params.get(EditorParameters.CONFIGURATION);
      } else {
         configuration = new CKEditorConfiguration();
      }

      // switch on CKEditor fullPage mode only for html-files
      if (getMimeType() == null) {
         return;
      }
      
      if (getMimeType().equals(MimeType.TEXT_HTML))
      {
         CKEditorConfiguration.setFullPage(true);
      }
   }

   protected void onLoad()
   {
      super.onLoad();
      editorObject =
         initCKEditor(getId(),
            CKEditorConfiguration.BASE_PATH,
            CKEditorConfiguration.TOOLBAR.toString(), // aditional default configuration can be found in config.js
            CKEditorConfiguration.THEME.toString(),
            CKEditorConfiguration.SKIN.toString(),
            CKEditorConfiguration.LANGUAGE.toString(),
            CKEditorConfiguration.CONTINUOUS_SCANNING,
            CKEditorConfiguration.isFullPage());
   }

   private native JavaScriptObject initCKEditor(String id, String basePath, String toolbar, String theme, String skin, String language, int continuousScanning, boolean fullPage) /*-{     
      var instance = this;
      if (toolbar !== undefined) {
         $wnd.CKEDITOR.config.toolbar = toolbar;
      }       

      if (theme !== undefined) {
         $wnd.CKEDITOR.config.theme = theme;
      }       
                                                                 
      if (language !== undefined) {
         $wnd.CKEDITOR.config.language = language;
      }
      
      $wnd.CKEDITOR.config.resize_dir = 'both';
                                                                 
      if (basePath !== undefined) {
         $wnd.CKEDITOR.basePath = basePath;
         $wnd.CKEDITOR.config.contentsCss = basePath + "contents.css";   // reflects the CSS used in the final pages where the contents are to be used.
         $wnd.CKEDITOR.plugins.basePath = basePath + "plugins/";     // set base path to the plugins folder
         $wnd.CKEDITOR.config.templates_files[0] = basePath + "plugins/templates/templates/default.js";   // set default template path
         $wnd.CKEDITOR.config.smiley_path = basePath + "plugins/smiley/images/";   // The base path used to build the URL for the smiley images.
      }       
                                                                 
      if (skin !== undefined) {
         $wnd.CKEDITOR.config.skin = skin + ',' + basePath + 'skins/' + skin + '/';
      }
                                                                 
      if (fullPage !== undefined) {
         $wnd.CKEDITOR.config.fullPage = fullPage;
      }    
                                                                 
      // create editor instance      
      var editor = $wnd.CKEDITOR.appendTo(id, $wnd.CKEDITOR.config);  
                                                                 
      // add listeners
      if (editor !== null) {                         
         // init editor content variable
         editor.exoSavedContent = ""; 
                                                                 
         // set onContentChangeListener
         editor.exoChangeFunction = function(){
            // check if content was changed
            if (editor.checkDirty()) {            
               editor.resetDirty();
               if ( editor.getData() != editor.exoSavedContent ) {
                  editor.exoSavedContent = editor.getData();
                  instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::onContentChanged()();
               }               
            }
         }
         instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::onContentChangeListenerId = $wnd.setInterval(editor.exoChangeFunction, continuousScanning);
                                                                 
         // add Hot Key Listener
         instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::setHotKeysClickListener(Lcom/google/gwt/core/client/JavaScriptObject;)(editor);
         //         var Ctrl_s_keycode = $wnd.CKEDITOR.CTRL + 115;
         //         var Ctrl_S_keycode = $wnd.CKEDITOR.CTRL + 83;          
         //         editor.exoSaveFunction = function(e) {
         //            // test if was pressed "Ctrl + S" or "Ctrl + s"
         //            if (e.data.keyCode == Ctrl_s_keycode || e.data.keyCode == Ctrl_S_keycode) {
         //              instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::onSaveContent()();  // call onSaveContent() listener                
         //              return false;  // this disables default action (submitting the form)
         //            }
         //         }
         //         editor.on('key', editor.exoSaveFunction);
                                                                 
         // add onCursorActitvity listener
         editor.exoCursorActivity = function() {
            instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::onCursorActivity()();               
         }
         editor.on('key', editor.exoCursorActivity);

         // add onFocus listener
         editor.onFocusReceived = function() {
            instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::onFocusReceived()();               
         }
         editor.on('focus', editor.onFocusReceived);
  
         // set init callback
         editor.exoInitCallback = function() {          
            instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::onInitialized()(); 
         }
  
         editor.on('instanceReady', editor.exoInitCallback);
      }
  
      editor.exoNativeAlert = $wnd.alert;
      editor.exoNativeConfirm = $wnd.confirm;      
      instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::overrideNativeAlertAndConfirm()();
  
      return editor;
   }-*/;
   
   private void onContentChanged()
   {
      fireEvent(new EditorContentChangedEvent(getId()));
   }

   private void onSaveContent()
   {
      //eventBus.fireEvent(new EditorSaveContentEvent(getId()));
   }

   private void onCursorActivity()
   {
      fireEvent(new EditorCursorActivityEvent(getId(), -1, -1));
   }

   private void onFocusReceived()
   {
      fireEvent(new EditorFocusReceivedEvent(getId()));
   }
   
   private boolean initialized = false;

   private void onInitialized()
   {
      initialized = true;
      
      updateDimensions();
      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            updateDimensions();
         }
      });

      fireEvent(new EditorInitializedEvent(getId()));
      setText(content);
   }
   
   /**
    * This hack method is needs to automatically update size of CKEditor toolbar and frame.
    */
   private void updateDimensions() {
      Element spanElement = label.getElement().getFirstChild().cast();
      tuneElement(spanElement, false);

      Element span1Element = spanElement.getFirstChildElement().cast();
      tuneElement(span1Element, false);
      
      Element span2Element = span1Element.getFirstChildElement().cast();
      tuneElement(span2Element, false);
      
      Element tableElement = span2Element.getFirstChildElement().cast();
      tuneElement(tableElement, true);
      
      Element tBodyElement = tableElement.getFirstChildElement().cast();
      
      Element tr1Element = tBodyElement.getChild(0).cast();
      Element tr1TDElement = tr1Element.getFirstChildElement().cast();
      tr1TDElement.getStyle().setPosition(Position.RELATIVE);
      
      Element tr2Element = tBodyElement.getChild(1).cast();
      tr2Element.getStyle().setHeight(100, Unit.PCT);
      
      Element tr2TDElement = tr2Element.getFirstChildElement().cast();
      tr2TDElement.getStyle().setPosition(Position.RELATIVE);
      tr2TDElement.getStyle().setWidth(100, Unit.PCT);
      
      Element iFrameElement = tr2TDElement.getFirstChildElement().cast();
      if ("cke_browser_gecko".equals(span1Element.getClassName()))
      {
         iFrameElement.getStyle().setPosition(Position.RELATIVE);
      }
      else
      {
         iFrameElement.getStyle().setPosition(Position.ABSOLUTE);
      }
   }
   
   private void tuneElement(Element e, boolean absolutePosition)
   {
      if (absolutePosition)
      {
         e.getStyle().setPosition(Position.ABSOLUTE);
      }
      else
      {
         e.getStyle().setPosition(Position.RELATIVE);
      }

      e.getStyle().setLeft(0, Unit.PX);
      e.getStyle().setTop(0, Unit.PX);
      e.getStyle().setWidth(100, Unit.PCT);
      e.getStyle().setHeight(100, Unit.PCT);
      e.getStyle().setOverflow(Overflow.HIDDEN);
   }

   public String getText()
   {
      // replace "\t" delimiter on space symbol
      return getTextNative().replace("\t", " ");
   }

   private final native String getTextNative()/*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      if (editor != null) {
         editor.exoSavedContent = editor.getData();
         return this.@org.exoplatform.ide.editor.ckeditor.CKEditor::prefix +
            editor.exoSavedContent +
            this.@org.exoplatform.ide.editor.ckeditor.CKEditor::suffix;
      }
   }-*/;

   public String extractHtmlCodeFromGoogleGadget(String text)
   {
      this.prefix = GoogleGadgetParser.getPrefix(text);
      String content = GoogleGadgetParser.getContentSection(text);
      this.suffix = GoogleGadgetParser.getSuffix(text);
      return content;
   };

   public void setText(String text)
   {
      // removed odd "\r" symbols
      text = text.replace("\r", "");

      // extract CDATA section from google gadget
      if (getMimeType().equals(MimeType.GOOGLE_GADGET))
      {
         this.prefix = this.suffix = "";

         // test if it is possible to localize CDATA section
         if (GoogleGadgetParser.hasContentSection(text))
         {
            // extract HTML-code from <Content> tag
            text = this.extractHtmlCodeFromGoogleGadget(text);
         }
      }

      this.setData(text);
   }

   private native void setEditorMode(String mode)/*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      if (editor != null) {
         editor.setMode(mode);
      }
   }-*/;

   private final native void setData(String data)/*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      if (editor != null) {
         editor.setData(data, function() {
            editor.checkDirty();    // reset ckeditor content changed indicator (http://docs.cksource.com/ckeditor_api/symbols/CKEDITOR.editor.html#setData)
         });

         editor.exoSavedContent = data;
            editor.focus();
      }
   }-*/;

   public native void undo()/*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      if (editor != null) {
         editor.execCommand("undo");
      }
   }-*/;

   public native void redo()/*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      if (editor != null) {
         editor.execCommand("redo");
      }
   }-*/;

   public native boolean hasRedoChanges()/*-{
      return true;
   }-*/;

   public native boolean hasUndoChanges()/*-{
      return true;
   }-*/;

   /*
    * remove listeners and restore functions
    */
   protected void onUnload()
   {
      removeEditorListeners();
      removeOnContentChangeListener();
      removeOnEditorResizeListener();
      restoreNativeAlertAndConfirm();
   }

   private native void restoreNativeAlertAndConfirm() /*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      if (typeof editor.exoNativeAlert === 
         "function" && typeof editor.exoNativeConfirm === "function") {
         $wnd.alert = editor.exoNativeAlert;
         $wnd.confirm = editor.exoNativeConfirm;
      }            
   }-*/;

   private native void removeOnContentChangeListener() /*-{
      var onContentChangeListenerId = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::onContentChangeListenerId;
      if (onContentChangeListenerId !== null) {
         $wnd.clearInterval(onContentChangeListenerId);      
      }
   }-*/;

   private native void removeOnEditorResizeListener() /*-{
      var onEditorResizeListenerId = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::onEditorResizeListenerId;
      if (onEditorResizeListenerId !== null) {
         $wnd.clearInterval(onEditorResizeListenerId);      
      }
   }-*/;

   private native void removeEditorListeners() /*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      if (editor !== null) {
         // remove 'instanceReady' listener
         if (editor.hasListeners('instanceReady')) {
            editor.removeListener('instanceReady', editor.exoInitCallback)                
         }        
                                               
         // remove 'key' listeners       
         if (editor.hasListeners('key')) {
            editor.removeListener('key', editor.exoCursorActivity);          
         }
                                               
         if (editor.hasListeners('key')) {
            editor.removeListener('key', editor.exoHotKeysClickListener);          
         }        
      }   
   }-*/;

   public boolean isReadOnly()
   {
      return (Boolean)params.get(EditorParameters.IS_READ_ONLY);
   }

   private static void showErrorDialog(String title, String message)
   {
      Dialogs.getInstance().showError(title, message);
   }

   /**
    * replace window.alert() function on org.exoplatform.gwtframework.ui.client.dialogs.Dialogs.showError() and hide
    * window.confirm() function
    * */
   private native void overrideNativeAlertAndConfirm() /*-{ 
      (
      function(){
         var proxied = $wnd.alert;
         $wnd.alert = function(message){
            // test if this is a in context of ckeditor
            if (typeof $wnd.CKEDITOR !== "undefined" ) {
               @org.exoplatform.ide.editor.ckeditor.CKEditor::showErrorDialog(Ljava/lang/String;Ljava/lang/String;)("WYSIWYG Editor Error",message);
            } else {
               return proxied(message);
            }
         };
      })(this);
                                                       
      (
      function(){
         var proxied = $wnd.confirm;
         $wnd.confirm = function(message) {
            // test if this is a ckeditor
            if (typeof $wnd.CKEDITOR !== "undefined" ) {
               return true;
            } else {
               return proxied(message);
            }
         };
      })();
   }-*/;

   private CKEditorConfiguration getConfiguration()
   {
      return configuration;
   }

   /**
    * Set listeners of hot keys clicking
    */
   private native void setHotKeysClickListener(JavaScriptObject editor) /*-{
      var instance = this;
      if (editor) {
         editor.exoHotKeysClickListener = function(e) {
            // filter key pressed without ctrl 
            if (e.data.keyCode < $wnd.CKEDITOR.CTRL) {
               return;
            }
                                                                        
            // see doc at http://docs.cksource.com/ckeditor_api/symbols/CKEDITOR.html#event:key
            var keyPressed = "";
            if (e.data.keyCode < $wnd.CKEDITOR.ALT) {
               // after pressing Ctrl+something
               keyPressed += "Ctrl+" + String(e.data.keyCode - $wnd.CKEDITOR.CTRL);
            } else {
               // after pressing Alt+something
               keyPressed += "Alt+" + String(e.data.keyCode - $wnd.CKEDITOR.ALT);
            }              
                                                                        
//            // find similar key ammong the hotKeyList 
//            var hotKeyList = instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::getHotKeyList()();                  
//
//            // listen Ctrl+S key pressing if hotKeyList is null
//            if (hotKeyList === null) { 
//               if (keyPressed == "Ctrl+" + "S".charCodeAt(0)) {
//                  instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::onSaveContent()();
//                  return false;                        
//               } else {
//                  return;
//               }                      
//            }
//
//            for (var i = 0; i < hotKeyList.@java.util.List::size()(); i++) {
//               var currentHotKey = hotKeyList.@java.util.List::get(I)(i); 
//               if (currentHotKey == keyPressed) {
//                  // fire EditorHotKeyCalledEvent
//                  var editorHotKeyCalledEventInstance = @org.exoplatform.ide.editor.api.event.EditorHotKeyCalledEvent::new(Ljava/lang/String;)(currentHotKey);
//                  var eventBus = instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::getEventBus()();
//                  eventBus.@com.google.gwt.event.shared.HandlerManager::fireEvent(Lcom/google/gwt/event/shared/GwtEvent;)(editorHotKeyCalledEventInstance);
//                  return false;                
//               }
//            }
         }

         editor.on('key', editor.exoHotKeysClickListener);
      }
   }-*/;

   @Override
   public String getId()
   {
      return editorId;
   }

   @Override
   public boolean isCapable(Capability capability)
   {
      switch (capability)
      {
         default :
            return false;
      }
   }

   public String getMimeType()
   {
      return mimeType;
   }

   @Override
   public native void focus() /*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      var instance = this;
      if (editor != null) {
         $wnd.setTimeout(function(a, b){
            editor.focus();
            instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::onFocusReceived()();
         }, 200);
      }
   }-*/;
   
   @Override
   public int getCursorColumn()
   {
      return 0;
   }

   @Override
   public void setCursorPosition(int row, int column)
   {
   }

   @Override
   public void deleteLine(int lineNumber)
   {
   }

   @Override
   public void insetLine(int lineNumber, String text)
   {
   }

   @Override
   public void format()
   {
   }

   @Override
   public boolean findText(String text, boolean caseSensitive)
   {
      return false;
   }

   @Override
   public void replaceSelection(String text)
   {
   }

   @Override
   public String getLineText(int line)
   {
      return null;
   }

   @Override
   public void setLineText(int line, String text)
   {
   }

   @Override
   public void showLineNumbers(boolean isShowLineNumbers)
   {
   }

   @Override
   public int getCursorRow()
   {
      return 0;
   }

}