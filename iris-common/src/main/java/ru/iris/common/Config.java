/**
 * Copyright 2013 Nikolay A. Viguro, Tommi S.E. Laukkanen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.iris.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Class for loading configuration from properties files and database.
 */
public class Config {

    /**
     * The map of loaded properties.
     */
    private static HashMap<String, String> propertyMap = null;

    /**
     * Default constructor which loads properties from different storages.
     */
    public Config() {
        synchronized (Config.class) {

            if (propertyMap != null) {
                return;
            }
            propertyMap = new HashMap<String, String>();
            loadPropertiesFromClassPath("/conf/iris-default.properties");
            if (!loadPropertiesFromClassPath("/conf/iris-extended.properties")) {
                if (!loadPropertiesFromFileSystem("/conf/iris-extended.properties")) {
                    loadPropertiesFromFileSystem("./conf/main.property");
                }
            }
        }
    }

    /**
     * Loads given properties file from class path.
     *
     * @param propertiesFileName the property file name
     * @return true if file was found and loaded successfully.
     */
    private boolean loadPropertiesFromClassPath(final String propertiesFileName) {
        final InputStream inputStream = Config.class.getResourceAsStream(propertiesFileName);
        if (inputStream == null) {
            return false;
        }
        try {
            final Properties properties = new Properties();
            properties.load(inputStream);
            final Enumeration enumeration = properties.keys();
            while (enumeration.hasMoreElements()) {
                final String key = (String) enumeration.nextElement();
                propertyMap.put(key, (String) properties.get(key));
            }
        } catch (final IOException e) {
            return false;
        }

        return true;
    }

    /**
     * Loads given properties file from file system.
     *
     * @param propertiesFileName the property file name
     * @return true if file was found and loaded successfully.
     */
    private boolean loadPropertiesFromFileSystem(final String propertiesFileName) {
        try {
            final InputStream inputStream = new FileInputStream(propertiesFileName);
            if (inputStream == null) {
                return false;
            }

            final Properties properties = new Properties();
            properties.load(inputStream);
            final Enumeration enumeration = properties.keys();
            while (enumeration.hasMoreElements()) {
                final String key = (String) enumeration.nextElement();
                propertyMap.put(key, (String) properties.get(key));
            }

            return true;
        } catch (final IOException e) {
            return false;
        }
    }

    /**
     * Returns the configuration properties map containing key value pairs.
     *
     * @return the configuration properties.
     */
    public Map<String, String> getConfig() {
        return Collections.unmodifiableMap(propertyMap);
    }
}
