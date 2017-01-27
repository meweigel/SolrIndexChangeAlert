package com.prototype.app;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.prototype.client.StompMessageClient;
import com.prototype.model.AlertMessage;
import com.prototype.model.ResponseMessage;
import com.prototype.utils.AppConstants;
import com.prototype.utils.IndexChangeMonitorUtil;

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

@Controller
public class ResponseController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ResponseController.class);
	
	private boolean started = false;
	private FileAlterationMonitor monitorRef;

	/**
	 * The incoming messages from APP_POINT are processed by onMessageReceived,
	 * then routed to TOPIC_ENDPOINT
	 * 
	 * @param message : The incoming AlertMessage
	 * @return ResponseMessage
	 * @throws Exception
	 */
	@MessageMapping(AppConstants.APP_ENDPOINT)
	@SendTo(AppConstants.TOPIC_ENDPOINT)
	public ResponseMessage onMessageReceived(AlertMessage message) throws Exception {

		//System.out.println("ResponseController: onMessageReceived() " + message.getAlert());
		LOGGER.info("onMessageReceived() " + message.getAlert());


		String msg = message.getAlert().toLowerCase();

		
		// StompMessageClient will run as background thread when
		// onMessageReceived receives a "start" message.
		if (!started && msg.equals("start")) {
			try {
				started = true;
				message.setAlert("The Solr index change montitor was started");
				monitorRef = IndexChangeMonitorUtil
						.monitorSolr(new StompMessageClient(AppConstants.WS_ENDPOINT, AppConstants.TOPIC_ENDPOINT));
			} catch (Exception e) {
				LOGGER.error("onMessageReceived() " + e.toString());
			}
		} else if (msg.equals("stop")) {
			monitorRef.stop();
			started = false;
			message.setAlert("The Solr index change montitor was stopped");
		}

		return new ResponseMessage(message.getAlert());
	}
}