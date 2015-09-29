/*
 * @(#)Resource.java - resource and i18n
 *
 * Copyright (c) 2004-2005 by pstorch, All rights reserved.
 * 
 * This file is part of ProjectX, a free Java based demux utility.
 * By the authors, ProjectX is intended for educational purposes only, 
 * as a non-commercial test project.
 * 
 *
 * This program is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package sk.vzahradn.dvb.projectx.common;

import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.*;

import java.awt.Image;
import java.awt.Toolkit;

/**
 * Project-X resource and localization handling.
 *
 * @author Peter Storch
 */
public class Resource {
    // the prefix of all pjx resource files
    private static final String PJX_LANG_RESOURCE_NAME = "sk.vzahradn.dvb.projectx.i18n.pjxresources";

    // name of the resource directory
    private static final String PJX_RESOURCE_DIR_NAME = "res";

    // current working directory
    public static final String workDir = System.getProperty("user.dir");

    // resource directory
    private static final String resDir = workDir + File.separator + PJX_RESOURCE_DIR_NAME;

    // the users locale
    private static Locale locale = null;

    // the resource bundle for the current users locale or language setting
    private static ResourceBundle resource = null;

    /**
     * Constructor of Resource.
     */
    private Resource() {
        // singleton
    }

    /**
     * Loads Language from ini file.
     *
     * @param lang Name of the language file to use.
     */
    public static void loadLang(String lang) {
        locale = new Locale(lang, "");

        try {
            resource = ResourceBundle.getBundle(PJX_LANG_RESOURCE_NAME, locale);
        } catch (MissingResourceException e) {
            System.out.println("Unable to load language resources");
            throw e;
        }

        // initialize languages dependent keys
        new Keys();
    }

    /**
     *
     */
    public static Locale getChosenLanguage() {
        if (locale == null)
            return null;

        return locale;
    }

    /**
     *
     */
    public static void setChosenLanguage(String str) {
        if (str == null)
            locale = null;

        else
            locale = new Locale(str, "", "");
    }


    /**
     * Gets a String from the Resource file. If the key is not found, the key
     * itself is returned as text.
     *
     * @param key Key in ResourceBundle, which we want to get.
     * @return Localized value from ResourceBundle matching the key, returned as a String.
     */
    public static String getString(String key) {
        String text = null;

        try {
            text = resource.getString(key);
        } catch (MissingResourceException e) {
            System.out.println("ResourceKey '" + key + "' not found in pjxresources");
        }

        // use key as text as fallback
        if (text == null) {
            text = "?" + key + "?";
        }

        return text;
    }

    /**
     * Returns a resource String as a String Array of lines.
     *
     * @return String[]
     */
    public static String[] getStringByLines(String key) {
        List<String> lines = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(getString(key), "\n");
        while (st.hasMoreTokens()) {
            lines.add(st.nextToken());
        }

        return lines.toArray(new String[lines.size()]);
    }

    /**
     * Gets a String from the resource and inserts optional arguments.
     * This method is just an alias for MessageFormat.format(), passing all its arguments.
     *
     * @param key Key from ResourceBundle.
     * @param args Optional arguments.
     * @return Requested string with optional arguments.
     */
    public static String getString(String key, Object... args) {
        return MessageFormat.format(getString(key), args);
    }

    /**
     * Returns the available Locales for pjxresources.
     *
     * @return Set of all available locales
     */
    public static SortedSet<Locale> getAvailableLocales() {

        // Sort available locales alphabetically in ascending order based on language name.
        // Order is dependant on interface language.
        Comparator<Locale> comparator = new Comparator<Locale>() {
            @Override
            public int compare(Locale lang1, Locale lang2) {
                Locale uiLocale = getChosenLanguage();

                if (uiLocale != null) {
                    return lang1.getDisplayLanguage(uiLocale).compareTo(lang2.getDisplayLanguage(uiLocale));
                } else {
                    return lang1.getDisplayLanguage().compareTo(lang2.getDisplayLanguage());
                }
            }
        };

        SortedSet<Locale> locales = new TreeSet<Locale>(comparator);

        for (Locale locale : DateFormat.getAvailableLocales()) {
            try {
                // Check if we have actually got target locale, not fallback
                if (!locale.toString().equals("") && ResourceBundle.getBundle(PJX_LANG_RESOURCE_NAME, locale).
                        getLocale() == locale) {
                    locales.add(locale);
                }
            } catch (MissingResourceException e) {
                // Means that resource for the language is not available,
                // which is OK as we are checking list of available languages
            }
        }

        // Clear all cached bundles from memory
        ResourceBundle.clearCache();

        return locales;
    }

    /**
     * Returns a resource (e.g. from the jar file) as an URL.
     *
     * @param resource the name of the resource
     * @return URL
     */
    public static URL getResourceURL(String resource) {
        try {
            String filename = resDir + File.separator + resource;
            File file = new File(filename);
            if (file.exists() && file.canRead()) {
                return file.toURI().toURL();
            }
        } catch (Exception e) {
            // ignore it, it was just a try to get this resource from the filesystem
        }

        // for the classloader we need to replace all backslashes to forward slashes.
        // this is only necessary on windows systems and doesn't harm others
        resource = resource.replace('\\', '/');

        // ok, not found in the filesystem, now try the classloader
        return Resource.class.getClassLoader().getResource(resource);
    }

    /**
     * Returns a resource (e.g. from the jar file) as an URL.
     * This is used to print localized Help HTML pages.
     *
     * @param resourceName the name of the resource
     * @return URL
     */
    public static URL getLocalizedResourceURL(String path, String resourceName) {
        Locale usedLocale;
        if (locale != null) {
            usedLocale = locale;
        } else {
            usedLocale = Locale.getDefault();
        }

        String localizedResource = path + File.separator + usedLocale.getLanguage() + File.separator + resourceName;

        URL url = getResourceURL(localizedResource);
        if (url != null) {
            return url;
        }

        // there is no localized version of this file, try the default version
        return getResourceURL(path + File.separator + resourceName);
    }

    /**
     *
     */
    public static Image loadImage(String imageName) {
        try {
            return Toolkit.getDefaultToolkit().createImage(getResourceURL(imageName));

        } catch (Exception e) {
            return null;
        }
    }

}
