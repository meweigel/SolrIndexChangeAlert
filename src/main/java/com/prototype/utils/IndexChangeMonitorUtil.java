package com.prototype.utils;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prototype.client.StompMessageClient;
import com.prototype.utils.AppConstants;

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
 *         The IndexChangeMonitorUtil is a utility class for Monitor FileA
 *         lterations
 */
public class IndexChangeMonitorUtil {
	private static final String HEADER_MSG = "IndexChangeMonitorUtil: ";
	private static final Logger LOGGER = LoggerFactory.getLogger(IndexChangeMonitorUtil.class);
	
	
	/**
	 * The indexing monitoring thread
	 * 
	 * @param client
	 *            StompMessageClient used for routing messages to a subscribed
	 *            endpoint
	 * 
	 * @throws Exception
	 */
	public static FileAlterationMonitor monitorSolr(StompMessageClient client) throws Exception {
		File directory = new File(AppConstants.INDEX_FOLDER);

		// Create an instance of a FileAlterationObserver that is given a
		// SolrIndexFileFilter.
		FileAlterationObserver observer = new FileAlterationObserver(directory, new SolrIndexFileFilter());

		// Add a IndexChangeListenerImpl that has a reference to an instance of
		// a StompMessageClient
		// The listener is invoked when an index change event happens, and uses
		// StompMessageClient to
		// send a message to a Websocket topic endpoint.
		observer.addListener(new IndexChangeListenerImpl(client));

		FileAlterationMonitor monitor = new FileAlterationMonitor(AppConstants.POLL_INTERVAL);

		// The FileAlterationObserver will observe file alterations and then
		// invoke the IndexChangeListenerImpl
		monitor.addObserver(observer);

		// Start the FileAlterationMonitor thread
		monitor.start();
		
		LOGGER.info(HEADER_MSG + "monitorSolr() The FileAlterationMonitor thread was started");

		return monitor;
	}
}
