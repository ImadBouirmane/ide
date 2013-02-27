/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.impl.fs;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.commons.FileUtils;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CleanableSearcherProvider extends SearcherProvider
{

//   * Implementation of Searcher which run all update tasks in ExecutorService.
//   * <p/>
//   * NOTE: This implementation always create new index in directory specified in constructor. This directory must be
//   * empty. If directory is not empty IllegalStateException is thrown.


   private final ConcurrentMap<java.io.File, CleanableSearcher> instances;
   private final ExecutorService executor;

   public CleanableSearcherProvider(InitParams initParams)
   {
      super(initParams);
      executor = Executors.newFixedThreadPool(1 + Runtime.getRuntime().availableProcessors());
      instances = new ConcurrentHashMap<java.io.File, CleanableSearcher>();
   }

   public CleanableSearcherProvider(String indexRoot)
   {
      super(indexRoot);
      executor = Executors.newFixedThreadPool(1 + Runtime.getRuntime().availableProcessors());
      instances = new ConcurrentHashMap<java.io.File, CleanableSearcher>();
   }

   public CleanableSearcherProvider(java.io.File indexRoot)
   {
      super(indexRoot);
      executor = Executors.newFixedThreadPool(1 + Runtime.getRuntime().availableProcessors());
      instances = new ConcurrentHashMap<java.io.File, CleanableSearcher>();
   }

   @Override
   public Searcher getSearcher(MountPoint mountPoint) throws VirtualFileSystemException
   {
      final java.io.File vfsIoRoot = mountPoint.getRoot().getIoFile();
      CleanableSearcher searcher = instances.get(vfsIoRoot);
      if (searcher == null)
      {
         final java.io.File indexDir;
         CleanableSearcher newSearcher;
         try
         {
            indexDir = FileUtils.createTempDirectory(indexRoot, "vfs_index");
            newSearcher = new CleanableSearcher(this, indexDir, getIndexedMediaTypes());
         }
         catch (IOException e)
         {
            throw new VirtualFileSystemException("Unable create searcher.");
         }
         searcher = instances.putIfAbsent(vfsIoRoot, newSearcher);
         if (searcher == null)
         {
            searcher = newSearcher;
            searcher.init(executor, mountPoint);
         }
      }
      return searcher;
   }

   void close(CleanableSearcher searcher)
   {
      searcher.doClose();
      instances.values().remove(searcher);
   }

   private Set<String> getIndexedMediaTypes() throws VirtualFileSystemException
   {
      Set<String> forIndex = null;
      final URL url = Thread.currentThread().getContextClassLoader().getResource("META-INF/indices_types.txt");
      if (url != null)
      {
         InputStream in = null;
         BufferedReader reader = null;
         try
         {
            in = url.openStream();
            reader = new BufferedReader(new InputStreamReader(in));
            forIndex = new LinkedHashSet<String>();
            String line;
            while ((line = reader.readLine()) != null)
            {
               int c = line.indexOf('#');
               if (c >= 0)
               {
                  line = line.substring(0, c);
               }
               line = line.trim();
               if (line.length() > 0)
               {
                  forIndex.add(line);
               }
            }
         }
         catch (IOException e)
         {
            throw new VirtualFileSystemException(
               String.format("Failed to get list of media types for indexing. %s", e.getMessage()));
         }
         finally
         {
            if (reader != null)
            {
               try
               {
                  reader.close();
               }
               catch (IOException ignored)
               {
               }
            }
            if (in != null)
            {
               try
               {
                  in.close();
               }
               catch (IOException ignored)
               {
               }
            }
         }
      }
      if (forIndex == null || forIndex.isEmpty())
      {
         throw new VirtualFileSystemException("Failed to get list of media types for indexing. " +
            "File 'META-INF/indices_types.txt not found or empty. ");
      }
      return forIndex;
   }
}

