package com.prototype.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public final class AppConstants {

    private static final String HEADER = "AppConstants: ";
    private static final Logger LOGGER = Logger.getLogger(AppConstants.class.getName());
    private static final String SEP = System.getProperty("file.separator");
    private static final String CONFIG_DIR_FILE = "." + SEP + "conf" + SEP + "config.properties";

    public static final String APP_ENDPOINT = "/alertMessage";
    public static final String TOPIC_ENDPOINT = "/topic/responseMessage";
    public static final String WS_ENDPOINT = "/gs-guide-websocket";
    public static final String[] INDEX_FILE_EXTS = {"cfe", "cfs", "doc", "dvd",
        "dvm", "fdt", "fdx", "fnm", "lock", "nvd", "nvm", "pos", "si", "tim", "tip"};
    public static final int POLL_INTERVAL = 2000; // Milliseconds
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String INDEX_FOLDER_CHILD = "$_shard#_replica1" + SEP + "data" + SEP + "index";

    public static String INDEX_FOLDER = "";
    public static String PROXY_HOST = "";
    public static String PROXY_PORT = "";

    static {
        try (InputStream inputStream = new FileInputStream(CONFIG_DIR_FILE)) {
            Properties defaultProps = new Properties();
            defaultProps.load(inputStream);
            String indexFolderRoot = defaultProps.getProperty("INDEX_FOLDER_ROOT");
            PROXY_HOST = defaultProps.getProperty("PROXY_HOST");
            PROXY_PORT = defaultProps.getProperty("PROXY_PORT");
            INDEX_FOLDER = indexFolderRoot + INDEX_FOLDER_CHILD;
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, HEADER, ex);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, HEADER, ex);
        }
    }
}
