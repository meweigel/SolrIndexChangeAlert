package com.prototype.utils;

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
//TODO - These should be put in a properties file later
public final class AppConstants {

    public static final String INDEX_FOLDER = "/cots/solr-6.4.0/collection1/data/index";
    public static final String APP_ENDPOINT = "/alertMessage";
    public static final String TOPIC_ENDPOINT = "/topic/responseMessage";
    public static final String WS_ENDPOINT = "/gs-guide-websocket";
    public static final String PROXY_HOST = "127.0.0.1";
    public static final String PROXY_PORT = "8080";
    public static final String[] INDEX_FILE_EXTS = {"doc", "dvd", "dvm", "fdt", "fdx", "fnm", "lock", "nvd", "nvm",
        "pos", "si", "tim", "tip"};
    public static final int POLL_INTERVAL = 2000; // Milliseconds
    public static final String START_INDEX_MONITOR = "startIndexMonitor";
    public static final String STOP_INDEX_MONITOR = "stopIndexMonitor";
    public static final String WATCH_DIR_CREATE = "watchDirCreate";
    public static final String WATCH_DIR_CHANGE = "watchDirChange";
    public static final String WATCH_DIR_DELETE = "watchDirDelete";
    public static final String WATCH_FILE_CREATE = "watchFileCreate";
    public static final String WATCH_FILE_CHANGE = "watchFileChange";
    public static final String WATCH_FILE_DELETE = "watchFileDelete";

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
}
