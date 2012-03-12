/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.eclipse.jdt.client.outline;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import org.eclipse.jdt.client.UpdateOutlineEvent;
import org.eclipse.jdt.client.UpdateOutlineHandler;
import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.core.dom.FieldDeclaration;
import org.eclipse.jdt.client.core.dom.ImportDeclaration;
import org.eclipse.jdt.client.core.dom.MethodDeclaration;
import org.eclipse.jdt.client.core.dom.TypeDeclaration;
import org.eclipse.jdt.client.core.dom.VariableDeclarationFragment;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorGoToLineEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.outline.ui.OutlineDisplay;
import org.exoplatform.ide.client.framework.outline.ui.ShowOutlineEvent;
import org.exoplatform.ide.client.framework.outline.ui.ShowOutlineHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.event.EditorCursorActivityEvent;
import org.exoplatform.ide.editor.api.event.EditorCursorActivityHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.HashMap;
import java.util.List;

/**
 * Presenter for Java Outline View.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Feb 6, 2012 4:29:06 PM anya $
 * 
 */
public class OutlinePresenter implements UpdateOutlineHandler, ViewClosedHandler, EditorCursorActivityHandler,
   EditorActiveFileChangedHandler, ShowOutlineHandler, EditorFileClosedHandler, ApplicationSettingsReceivedHandler
{
   interface Display extends OutlineDisplay
   {
      /**
       * Update the Outline values.
       * 
       * @param cUnit compilation unit to update
       */
      void updateOutline(CompilationUnit cUnit);

      /**
       * Returns the selection model of the Outline tree.
       * 
       * @return {@link SingleSelectionModel} selection model
       */
      SingleSelectionModel<Object> getSingleSelectionModel();

      /**
       * Select node in Outline tree. It is also will be shown, if parent node is closed, then will be expanded.
       * 
       * @param node node to select
       */
      void selectNode(ASTNode node);

      /**
       * Give focus to the tree.
       */
      void focusInTree();

      /**
       * Get root child nodes.
       * 
       * @return {@link List} root nodes
       */
      List<Object> getNodes();

      /**
       * Get child nodes of the pointed parent.
       * 
       * @param parent
       * @return {@link List} child nodes
       */
      List<Object> getNodes(ASTNode parent);
   }

   /**
    * Display.
    */
   private Display display;

   /**
    * Current compilation unit.
    */
   private CompilationUnit compilationUnit;

   /**
    * Editor activity must be processed or not.
    */
   private boolean processEditorActivity = true;

   /**
    * Current row selected.
    */
   private int currentRow = -1;

   /**
    * Outline selection must be processed or not.
    */
   private boolean processSelection = true;

   /**
    * Current editor.
    */
   private Editor currentEditor;

   /**
    * Current active file.
    */
   private FileModel activeFile;

   /**
    * The map of the opened Java files and their compilation units.
    */
   private HashMap<String, CompilationUnit> openedFiles = new HashMap<String, CompilationUnit>();

   /**
    * Application's settings.
    */
   private ApplicationSettings applicationSettings;

   /**
    * Timer for selecting Outline's node.
    */
   private Timer selectOutlineTimer = new Timer()
   {
      @Override
      public void run()
      {
         if (compilationUnit != null && display != null)
         {
            selectNode(currentRow);
         }
      }
   };

   public OutlinePresenter()
   {
      IDE.addHandler(UpdateOutlineEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(EditorCursorActivityEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      IDE.addHandler(ShowOutlineEvent.TYPE, this);
      IDE.addHandler(EditorFileClosedEvent.TYPE, this);
      IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
   }

   /**
    * Bind display with presenter.
    */
   public void bindDisplay()
   {
      display.getSingleSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler()
      {

         @Override
         public void onSelectionChange(SelectionChangeEvent event)
         {
            if (!processSelection)
            {
               processSelection = true;
               return;
            }

            if (display.getSingleSelectionModel().getSelectedObject() instanceof ASTNode)
            {
               ASTNode node = ((ASTNode)display.getSingleSelectionModel().getSelectedObject());
               int startPosition = node.getStartPosition();

               // Find method's name start position:
               if (node instanceof MethodDeclaration)
               {
                  startPosition = ((MethodDeclaration)node).getName().getStartPosition();
               }
               // Find type's name start position:
               else if (node instanceof TypeDeclaration)
               {
                  startPosition = ((TypeDeclaration)node).getName().getStartPosition();
               }
               // Find field's name start position:
               else if (node instanceof FieldDeclaration)
               {
                  FieldDeclaration field = (FieldDeclaration)node;
                  if (field.fragments().iterator().hasNext())
                  {
                     startPosition =
                        ((VariableDeclarationFragment)field.fragments().iterator().next()).getStartPosition();
                  }
               }
               selectEditorLine(compilationUnit.getLineNumber(startPosition));
            }
         }
      });

      if (compilationUnit != null)
      {
         display.updateOutline(compilationUnit);
      }
   }

   /**
    * @see org.eclipse.jdt.client.UpdateOutlineHandler#onUpdateOutline(org.eclipse.jdt.client.UpdateOutlineEvent)
    */
   @Override
   public void onUpdateOutline(UpdateOutlineEvent event)
   {
      compilationUnit = event.getCompilationUnit();
      openedFiles.put(event.getFile().getId(), compilationUnit);

      if (display != null)
      {
         display.updateOutline(compilationUnit);
         if (currentEditor != null)
         {
            selectNode(currentEditor.getCursorRow());
         }
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   /**
    * @see org.exoplatform.ide.editor.api.event.EditorCursorActivityHandler#onEditorCursorActivity(org.exoplatform.ide.editor.api.event.EditorCursorActivityEvent)
    */
   @Override
   public void onEditorCursorActivity(EditorCursorActivityEvent event)
   {
      if (display == null)
      {
         return;
      }
      if (!processEditorActivity)
      {
         display.focusInTree();
         processEditorActivity = true;
         return;
      }

      if (currentRow == event.getRow())
      {
         return;
      }
      currentRow = event.getRow();
      selectOutlineTimer.cancel();
      selectOutlineTimer.schedule(100);
   }

   public void selectEditorLine(int line)
   {
      processEditorActivity = false;
      IDE.fireEvent(new EditorGoToLineEvent(line));
   }

   /**
    * Select node by it's line number.
    * 
    * @param lineNumber line number
    */
   public void selectNode(int lineNumber)
   {
      ASTNode node = getNodeByLineNumber(lineNumber, display.getNodes());
      if (node != null)
      {
         processSelection = false;
         display.selectNode(node);
      }
   }

   /**
    * Find node by it's line number.
    * 
    * @param lineNumber line number
    * @param nodes list of nodes to start search
    * @return {@link ASTNode} found node or <code>null</code>, if not found
    */
   protected ASTNode getNodeByLineNumber(int lineNumber, List<Object> nodes)
   {
      for (int i = 0; i < nodes.size(); i++)
      {
         if (nodes.get(i) instanceof ASTNode)
         {
            ASTNode node = (ASTNode)nodes.get(i);

            int startLineNumber = compilationUnit.getLineNumber(node.getStartPosition());
            int endLineNumber = compilationUnit.getLineNumber(node.getStartPosition() + node.getLength());

            if (startLineNumber == lineNumber)
            {
               return node;
            }

            // Check current line is between node's start and end lines:
            if (startLineNumber <= lineNumber & lineNumber <= endLineNumber)
            {
               // If there are no children - return this node
               if (display.getNodes(node).isEmpty())
               {
                  return node;
               }
               // Checking line ranges of children, if no proper is found - return parent node:
               else
               {
                  ASTNode foundNode = getNodeByLineNumber(lineNumber, display.getNodes(node));
                  return (foundNode == null) ? node : foundNode;
               }
            }
         }
         // Process import group node:
         else if (nodes.get(i) instanceof ImportGroupNode)
         {
            for (Object object : ((ImportGroupNode)nodes.get(i)).getImports())
            {
               if (compilationUnit.getLineNumber(((ImportDeclaration)object).getStartPosition()) == lineNumber)
               {
                  return (ImportDeclaration)object;
               }
            }
         }
      }
      // Nothing was found:
      return null;
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      this.activeFile = event.getFile();

      if (activeFile != null && MimeType.APPLICATION_JAVA.equals(activeFile.getMimeType()))
      {
         this.currentEditor = event.getEditor();
         boolean isOutlineOpened = applicationSettings.getValueAsBoolean("outline");
         if (isOutlineOpened && display == null)
         {
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bindDisplay();
         }

         compilationUnit = openedFiles.get(activeFile.getId());
         display.updateOutline(compilationUnit);
      }
      else
      {
         this.currentEditor = null;
         if (display != null)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.outline.ui.ShowOutlineHandler#onShowOutline(org.exoplatform.ide.client.framework.outline.ui.ShowOutlineEvent)
    */
   @Override
   public void onShowOutline(ShowOutlineEvent event)
   {
      if (event.isShow() && display == null && canShowOutline())
      {
         if (display == null)
         {
            display = GWT.create(Display.class);
            bindDisplay();
         }
         IDE.getInstance().openView(display.asView());
         return;
      }

      if (!event.isShow() && display != null)
      {
         IDE.getInstance().closeView(display.asView().getId());
         return;
      }
   }

   private boolean canShowOutline()
   {
      return (activeFile != null && MimeType.APPLICATION_JAVA.equals(activeFile.getMimeType()));
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler#onEditorFileClosed(org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent)
    */
   @Override
   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      if (openedFiles.containsKey(event.getFile().getId()))
      {
         CompilationUnit cUnit = openedFiles.remove(event.getFile().getId());
         if (compilationUnit != null && compilationUnit.equals(cUnit))
         {
            compilationUnit = null;
         }
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent)
    */
   @Override
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      this.applicationSettings = event.getApplicationSettings();
   }
}