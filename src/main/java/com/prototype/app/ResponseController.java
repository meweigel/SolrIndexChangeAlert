package com.prototype.app;

import java.text.SimpleDateFormat;
import java.util.Date;

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
/**
 *
 * @author mweigel
 *
 *
 * This clas controls the routing and processing of WebSocket messages
 */
@Controller
public class ResponseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseController.class);

    private boolean started = false;
    private FileAlterationMonitor monitorRef;
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat();

    static {
        DATE_FORMATTER.applyPattern(AppConstants.DATE_FORMAT);
    }

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

        LOGGER.info("onMessageReceived() " + message.getAlert());

        Date dateTime = new Date(System.currentTimeMillis());

        String msg = message.getAlert();
        boolean bool = msg.contains("true");

        // StompMessageClient will run as background thread when
        // onMessageReceived receives a "start" message.
        if (!started && msg.equals(AppConstants.START_INDEX_MONITOR)) {
            try {
                StompMessageClient client = StompMessageClient.getInstance(AppConstants.WS_ENDPOINT,
                        AppConstants.TOPIC_ENDPOINT);
                monitorRef = IndexChangeMonitorUtil.monitorSolr(client);
                message.setAlert(DATE_FORMATTER.format(dateTime) + " : The Solr index change montitor was started");
                started = true;
            } catch (Exception e) {
                LOGGER.error("onMessageReceived() " + e.toString());
            }
        } else if (msg.equals(AppConstants.STOP_INDEX_MONITOR)) {
            monitorRef.stop();
            started = false;
            message.setAlert(DATE_FORMATTER.format(dateTime) + " : The Solr index change montitor was stopped");
        } else if (msg.contains(AppConstants.WATCH_DIR_CREATE)) {
            IndexChangeMonitorUtil.getIndexChangeListenerImpl().setWatchDirectoryCreate(bool);
            message.setAlert(DATE_FORMATTER.format(dateTime) + " : Watching for directory creation: " + bool);
        } else if (msg.contains(AppConstants.WATCH_DIR_CHANGE)) {
            IndexChangeMonitorUtil.getIndexChangeListenerImpl().setWatchDirectoryChange(bool);
            message.setAlert(DATE_FORMATTER.format(dateTime) + " : Watching for directory change: " + bool);
        } else if (msg.contains(AppConstants.WATCH_DIR_DELETE)) {
            IndexChangeMonitorUtil.getIndexChangeListenerImpl().setWatchDirectoryDelete(bool);
            message.setAlert(DATE_FORMATTER.format(dateTime) + " : Watching for directory deletion: " + bool);
        } else if (msg.contains(AppConstants.WATCH_FILE_CREATE)) {
            IndexChangeMonitorUtil.getIndexChangeListenerImpl().setWatchFileCreate(bool);
            message.setAlert(DATE_FORMATTER.format(dateTime) + " : Watching for file creation: " + bool);
        } else if (msg.contains(AppConstants.WATCH_FILE_CHANGE)) {
            IndexChangeMonitorUtil.getIndexChangeListenerImpl().setWatchFileChange(bool);
            message.setAlert(DATE_FORMATTER.format(dateTime) + " : Watching for file change: " + bool);
        } else if (msg.contains(AppConstants.WATCH_FILE_DELETE)) {
            IndexChangeMonitorUtil.getIndexChangeListenerImpl().setWatchFileDelete(bool);
            message.setAlert(DATE_FORMATTER.format(dateTime) + " : Watching for file deletion: " + bool);
        } else {
            message.setAlert(DATE_FORMATTER.format(dateTime) + " : " + message.getAlert());
        }

        LOGGER.info("onMessageReceived() " + message.getAlert());

        return new ResponseMessage(message.getAlert());
    }
}
