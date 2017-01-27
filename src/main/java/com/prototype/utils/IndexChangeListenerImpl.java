package com.prototype.utils;

import java.io.File;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prototype.app.ResponseController;
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
 *         A listener class with methods that are invoked when when Linux file
 *         events occur
 */
public class IndexChangeListenerImpl implements FileAlterationListener {
	private static final String HEADER_MSG = "IndexChangeListenerImpl: ";
	private static final Logger LOGGER = LoggerFactory.getLogger(IndexChangeListenerImpl.class);
	private final StompMessageClient client;

	/**
	 * The single parameterized constructor
	 * 
	 * @param client
	 *            - An instance of a StompMessageClient
	 */
	public IndexChangeListenerImpl(StompMessageClient client) {
		this.client = client;
	}

	/**
	 * The onStart method is called when FileAlterationObserver is started
	 */
	@Override
	public void onStart(final FileAlterationObserver observer) {
		//LOGGER.info(HEADER_MSG + "onStart() called because FileAlterationObserver started");
	}

	/**
	 * The onDirectoryCreate is called when a directory is created
	 */
	@Override
	public void onDirectoryCreate(final File directory) {
		try {
			String msg = "Directory - " + directory.getName() + " was created";
			LOGGER.info(HEADER_MSG + "onDirectoryCreate() " + msg);
			client.sendMessage(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The onDirectoryChange method is called when a directory is changed
	 */
	@Override
	public void onDirectoryChange(final File directory) {
		try {
			String msg = "Directory - " + directory.getName() + " was changed";
			LOGGER.info(HEADER_MSG + "onDirectoryChange() " + msg);
			client.sendMessage(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The onDirectoryDelete method is called when a directory is deleted
	 */
	@Override
	public void onDirectoryDelete(final File directory) {
		try {
			String msg = "Directory - " + directory.getName() + " was deleted";
			LOGGER.info(HEADER_MSG + "onDirectoryDelete() " + msg);
			client.sendMessage(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The onFileCreate method is called when a file is created
	 */
	@Override
	public void onFileCreate(final File file) {
		try {
			String msg = "File - " + file.getName() + " was created";
			LOGGER.info(HEADER_MSG + "onFileCreate() " + msg);
			client.sendMessage(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The onFileChange method is called when a file is changed
	 */
	@Override
	public void onFileChange(final File file) {
		try {
			String msg = "File - " + file.getName() + " was changed";
			LOGGER.info(HEADER_MSG + "onFileChange() " + msg);
			client.sendMessage(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The onFileDelete method is called when a file is deleted
	 */
	@Override
	public void onFileDelete(final File file) {
		try {
			String msg = "File - " + file.getName() + " was deleted";
			LOGGER.info(HEADER_MSG + "onFileDelete() " + msg);
			client.sendMessage(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The onStop method is called when the FileAlterationObserver is stopped
	 */
	@Override
	public void onStop(final FileAlterationObserver observer) {
		//LOGGER.info(HEADER_MSG + "onStop() called because FileAlterationObserver stopped");
	}
}
