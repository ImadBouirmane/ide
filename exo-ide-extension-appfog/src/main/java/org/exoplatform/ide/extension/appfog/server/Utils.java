/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.extension.appfog.server;

import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.PropertyFilter;

import java.io.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codenvy.commons.lang.ZipUtils.unzip;



/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class Utils {
    private static final Pattern SPRING1 = Pattern.compile("WEB-INF/lib/spring-core.*\\.jar");
    private static final Pattern SPRING2 = Pattern.compile("WEB-INF/classes/org/springframework/.+");
    private static final Pattern GRAILS  = Pattern.compile("WEB-INF/lib/grails-web.*\\.jar");
    private static final Pattern LIFT    = Pattern.compile("WEB-INF/lib/lift-webkit.*\\.jar");
    private static final Pattern SINATRA = Pattern.compile("^\\s*require\\s*[\"']sinatra[\"']");

    static interface NameFilter {
        boolean accept(String name);
    }

    static final NameFilter WAR_FILTER  = new TypeFilter(".war");
    static final NameFilter RUBY_FILTER = new TypeFilter(".rb");
    static final NameFilter PHP_FILTER  = new TypeFilter(".php");

    private static class TypeFilter implements NameFilter {
        private final String ext;

        public TypeFilter(String ext) {
            this.ext = ext;
        }

        @Override
        public boolean accept(String name) {
            return name.endsWith(ext);
        }
    }

    static void copy(VirtualFileSystem vfs, String source, java.io.File target) throws VirtualFileSystemException,
                                                                                       IOException {
        InputStream zip = vfs.exportZip(source).getStream();
        unzip(zip, target);
    }

    /** Read the first line from file or <code>null</code> if file not found. */
    static String readFile(VirtualFileSystem vfs, Item parent, String name) throws VirtualFileSystemException,
                                                                                   IOException {
        return readFile(vfs, (parent.getPath() + '/' + name));
    }

    /** Read the first line from file or <code>null</code> if file not found. */
    public static String readFile(VirtualFileSystem vfs, String path) throws VirtualFileSystemException, IOException {
        InputStream in = null;
        BufferedReader r = null;
        try {
            ContentStream content = vfs.getContent(path, null);
            in = content.getStream();
            r = new BufferedReader(new InputStreamReader(in));
            return r.readLine();
        } catch (ItemNotFoundException ignored) {
        } finally {
            if (r != null) {
                r.close();
            }
            if (in != null) {
                in.close();
            }
        }
        return null;
    }

    static void delete(VirtualFileSystem vfs, String parentId, String name) throws VirtualFileSystemException {
        Item item = vfs.getItem(parentId, false, PropertyFilter.NONE_FILTER);
        String parentPath = item.getPath();
        try {
            Item file = vfs.getItemByPath(parentPath + '/' + name, null, false, PropertyFilter.NONE_FILTER);
            vfs.delete(file.getId(), null);
        } catch (ItemNotFoundException ignored) {
        }
    }

    public static String detectFramework(java.io.File path) throws IOException {
        if (path.isFile() && WAR_FILTER.accept(path.getName())) {
            FileInputStream fis = null;
            ZipInputStream zipIn = null;
            try {
                fis = new FileInputStream(path);
                zipIn = new ZipInputStream(fis);
                Matcher springMatcher1 = null;
                Matcher springMatcher2 = null;
                Matcher grailsMatcher = null;
                Matcher liftMatcher = null;
                for (ZipEntry e = zipIn.getNextEntry(); e != null; e = zipIn.getNextEntry()) {
                    String name = e.getName();
                    springMatcher1 = springMatcher1 == null ? SPRING1.matcher(name) : springMatcher1.reset(name);
                    if (springMatcher1.matches()) {
                        return "spring";
                    }
                    springMatcher2 = springMatcher2 == null ? SPRING2.matcher(name) : springMatcher2.reset(name);
                    if (springMatcher2.matches()) {
                        return "spring";
                    }
                    grailsMatcher = grailsMatcher == null ? GRAILS.matcher(name) : grailsMatcher.reset(name);
                    if (grailsMatcher.matches()) {
                        return "grails";
                    }
                    liftMatcher = liftMatcher == null ? LIFT.matcher(name) : liftMatcher.reset(name);
                    if (liftMatcher.matches()) {
                        return "lift";
                    }
                }
            } finally {
                if (zipIn != null) {
                    zipIn.close();
                }
                if (fis != null) {
                    fis.close();
                }
            }
            return "java_web";
        }
        return "standalone";
    }

    public static String detectFramework(VirtualFileSystem vfs, String projectId) throws VirtualFileSystemException, IOException {
        Item project = vfs.getItem(projectId, false, PropertyFilter.NONE_FILTER);
        try {
            vfs.getItemByPath(project.getPath() + "/config/environment.rb", null, false, PropertyFilter.NONE_FILTER);
            return "rails3";
        } catch (ItemNotFoundException ignored) {
        }
        List<Item> children = vfs.getChildren(projectId, -1, 0, "file", false, PropertyFilter.NONE_FILTER).getItems();
        for (Item i : children) {
            if (RUBY_FILTER.accept(i.getName())) {
                InputStream in = null;
                BufferedReader reader = null;
                // Check each ruby file to include "sinatra" import.
                Matcher sinatraMatcher = null;
                try {
                    in = vfs.getContent(i.getId()).getStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sinatraMatcher = sinatraMatcher == null ? SINATRA.matcher(line) : sinatraMatcher.reset(line);
                        if (sinatraMatcher.matches()) {
                            return "sinatra";
                        }
                    }
                } finally {
                    if (reader != null) {
                        reader.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                }
            }
        }

        for (Item i : children) {
            if ("server.js".equals(i.getName())
                || "app.js".equals(i.getName())
                || "index.js".equals(i.getName())
                || "main.js".equals(i.getName())) {
                return "node";
            } else if (PHP_FILTER.accept(i.getName())) {
                return "php";
            } else if ("manage.py".equals(i.getName()) || "settings.py".equals(i.getName())) {
                return "django";
            } else if ("wsgi.py".equals(i.getName())) {
                return "wsgi";
            }
        }
        return "standalone";
    }

    static String countFileHash(java.io.File file, MessageDigest digest) throws IOException {
        FileInputStream fis = null;
        DigestInputStream dis = null;
        byte[] b = new byte[8192];
        try {
            fis = new FileInputStream(file);
            dis = new DigestInputStream(fis, digest);
            while (dis.read(b) != -1) {
            }
            return toHex(digest.digest());
        } finally {
            if (dis != null) {
                dis.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
    }

    private static final char[] hex = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
                                                 'e', 'f'};

    static String toHex(byte[] hash) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < hash.length; i++) {
            b.append(hex[(hash[i] >> 4) & 0x0f]);
            b.append(hex[hash[i] & 0x0f]);
        }
        return b.toString();
    }
}