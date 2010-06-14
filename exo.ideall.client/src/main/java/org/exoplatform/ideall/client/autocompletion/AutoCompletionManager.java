/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ideall.client.autocompletion;

import java.util.HashMap;
import java.util.List;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.editor.api.Token;
import org.exoplatform.gwtframework.editor.event.EditorAutoCompleteCalledEvent;
import org.exoplatform.gwtframework.editor.event.EditorAutoCompleteCalledHandler;
import org.exoplatform.gwtframework.editor.event.EditorAutoCompleteEvent;
import org.exoplatform.gwtframework.ui.client.component.autocomlete.AutoCompleteForm;
import org.exoplatform.gwtframework.ui.client.component.autocomlete.AutocompleteTokenSelectedHandler;
import org.exoplatform.ideall.client.autocompletion.groovy.GroovyTokenCollector;
import org.exoplatform.ideall.client.autocompletion.js.JavaScriptTokenCollector;
import org.exoplatform.ideall.client.editor.event.EditorSetFocusOnActiveFileEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class AutoCompletionManager implements EditorAutoCompleteCalledHandler, TokensCollectedCallback,
   AutocompleteTokenSelectedHandler
{

   private HashMap<String, TokenCollector> factories = new HashMap<String, TokenCollector>();

   private HandlerManager eventBus;

   private ApplicationContext context;

   private int cursorOffsetX;

   private int cursorOffsetY;

   private String lineContent;

   private String editorId;

   private String tokenToComplete;

   private String afterToken;

   private String beforeToken;

   private int cursorPos;

   public AutoCompletionManager(HandlerManager eventBus, ApplicationContext context)
   {
      this.context = context;
      this.eventBus = eventBus;

      factories.put(MimeType.SCRIPT_GROOVY, new GroovyTokenCollector(eventBus, context, this));
      factories.put(MimeType.APPLICATION_JAVASCRIPT, new JavaScriptTokenCollector(eventBus, context, this));
      factories.put(MimeType.GOOGLE_GADGET, new JavaScriptTokenCollector(eventBus, context, this));

      eventBus.addHandler(EditorAutoCompleteCalledEvent.TYPE, this);
   }

   public void onEditorAutoCompleteCalled(EditorAutoCompleteCalledEvent event)
   {
      cursorOffsetX = event.getCursorOffsetX();
      cursorOffsetY = event.getCursorOffsetY();
      editorId = event.getEditorId();
      lineContent = event.getLineContent();
      cursorPos = event.getCursorPositionX();
      getTokenFromLine(lineContent);
      TokenCollector collector = factories.get(event.getMimeType());
      if (collector != null)
      {
         collector.getTokens(tokenToComplete, event.getCursorPositionY(), event.getTokenList());
      }
   }

   /**
    * @param line
    */
   private void getTokenFromLine(String line)
   {
      String tokenLine = "";
      tokenToComplete = "";
      afterToken = "";
      beforeToken = "";
      if (line.length() > cursorPos - 1)
      {
         afterToken = line.substring(cursorPos - 1, line.length());
         tokenLine = line.substring(0, cursorPos - 1);

      }
      else
      {
         afterToken = "";
         if (line.endsWith(" "))
         {
            tokenToComplete = "";
            beforeToken = line;
            return;
         }

         tokenLine = line;
      }

      for (int i = tokenLine.length() - 1; i >= 0; i--)
      {
         switch (tokenLine.charAt(i))
         {
            case ' ' :
               beforeToken = tokenLine.substring(0, i + 1);
               tokenToComplete = tokenLine.substring(i + 1);
               return;

            case '.' :
               beforeToken = tokenLine.substring(0, i + 1);
               tokenToComplete = tokenLine.substring(i + 1);
               return;

            case '(' :
               beforeToken = tokenLine.substring(0, i + 1);
               tokenToComplete = tokenLine.substring(i + 1);
               return;

            case ')' :
               beforeToken = tokenLine.substring(0, i + 1);
               tokenToComplete = tokenLine.substring(i + 1);
               return;

            case '{' :
               beforeToken = tokenLine.substring(0, i + 1);
               tokenToComplete = tokenLine.substring(i + 1);
               return;

            case '}' :
               beforeToken = tokenLine.substring(0, i + 1);
               tokenToComplete = tokenLine.substring(i + 1);
               return;

            case ';' :
               beforeToken = tokenLine.substring(0, i + 1);
               tokenToComplete = tokenLine.substring(i + 1);
               return;
         }
         beforeToken = "";
         tokenToComplete = tokenLine;
      }

   }

   public void onTokensCollected(List<Token> tokens)
   {
      int x = cursorOffsetX - tokenToComplete.length() * 8 + 8;
      int y = cursorOffsetY + 4;
      new AutoCompleteForm(eventBus, x, y, tokenToComplete, tokens, this);
   }

   /**
    * @see org.exoplatform.gwtframework.ui.client.component.autocomlete.AutocompleteTokenSelectedHandler#onAutocompleteTokenSelected(java.lang.String)
    */
   public void onAutocompleteTokenSelected(String token)
   {
      String tokenToPaste = beforeToken + token + afterToken;
      int newCursorPos = (beforeToken + token).length() + 1;
      eventBus.fireEvent(new EditorAutoCompleteEvent(editorId, tokenToPaste, newCursorPos));
   }

   /**
    * @see org.exoplatform.gwtframework.ui.client.component.autocomlete.AutocompleteTokenSelectedHandler#onAutocompleteCancel()
    */
   public void onAutocompleteCancel()
   {
      eventBus.fireEvent(new EditorSetFocusOnActiveFileEvent());
   }

}
