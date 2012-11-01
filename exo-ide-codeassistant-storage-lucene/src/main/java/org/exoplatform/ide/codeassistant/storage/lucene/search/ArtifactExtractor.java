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
package org.exoplatform.ide.codeassistant.storage.lucene.search;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.MapFieldSelector;
import org.apache.lucene.index.IndexReader;
import org.exoplatform.ide.codeassistant.storage.lucene.DataIndexFields;

import java.io.IOException;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class ArtifactExtractor implements ContentExtractor<String>
{

   /**
    * @see org.exoplatform.ide.codeassistant.storage.lucene.search.ContentExtractor#getValue(org.apache.lucene.index.IndexReader, int)
    */
   @Override
   public String getValue(IndexReader reader, int doc) throws IOException
   {
      Document document = reader.document(doc, new MapFieldSelector(new String[]{DataIndexFields.ARTIFACT}));

      return document.get(DataIndexFields.ARTIFACT);
   }

}