package com.prototype.utils;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prototype.client.StompMessageClient;

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
/**
 *
 * @author mweigel
 *
 * The IndexChangeMonitorUtil is a utility class for Monitor File alterations.
 */
public class IndexChangeMonitorUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexChangeMonitorUtil.class);
    private static IndexChangeListenerImpl indexChangeListenerImpl;

    /**
     * The indexing monitoring thread
     *
     * @param client StompMessageClient used for routing messages to a
     * subscribed endpoint
     * @param collection Name of Collection
     * @return FileAlterationMonitor
     *
     * @throws Exception
     */
    public static FileAlterationMonitor monitorSolr(StompMessageClient client, String collection, String shard) throws Exception {
        String indexFolder = AppConstants.INDEX_FOLDER.replace("$", collection);
        indexFolder = indexFolder.replace("#", shard);
        File directory = new File(indexFolder);
        FileAlterationMonitor monitor = null;

        if (directory.exists() && directory.canRead()) {

            // Create an instance of a FileAlterationObserver that is given a
            // SolrIndexFileFilter.
            FileAlterationObserver observer = new FileAlterationObserver(directory, new SolrIndexFileFilter());

            // Add a IndexChangeListenerImpl that has a reference to an instance of
            // a StompMessageClient. The listener is invoked when an index change
            // event happens,
            // and uses StompMessageClient to send a message to a Websocket topic
            // endpoint.
            indexChangeListenerImpl = new IndexChangeListenerImpl(client);
            observer.addListener(indexChangeListenerImpl);

            monitor = new FileAlterationMonitor(AppConstants.POLL_INTERVAL);

            // The FileAlterationObserver will observe file alterations and then
            // invoke the IndexChangeListenerImpl
            monitor.addObserver(observer);

            // Start the FileAlterationMonitor thread
            monitor.start();

            LOGGER.info("monitorSolr() The FileAlterationMonitor thread was started");
        } else {
            LOGGER.error("monitorSolr() Invalid directory: " + directory.getAbsolutePath());
        }

        return monitor;
    }

    public static IndexChangeListenerImpl getIndexChangeListenerImpl() {
        return indexChangeListenerImpl;
    }
}
