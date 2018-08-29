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
import static com.prototype.utils.Command.START_INDEX_MONITOR;
import static com.prototype.utils.Command.STOP_INDEX_MONITOR;
import static com.prototype.utils.Command.WATCH_DIR_CHANGE;
import static com.prototype.utils.Command.WATCH_DIR_CREATE;
import static com.prototype.utils.Command.WATCH_DIR_DELETE;
import static com.prototype.utils.Command.WATCH_FILE_CHANGE;
import static com.prototype.utils.Command.WATCH_FILE_CREATE;
import static com.prototype.utils.Command.WATCH_FILE_DELETE;
import com.prototype.utils.IndexChangeListenerImpl;
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
    private boolean started;
    private long startTime;
    private FileAlterationMonitor monitorRef;
    private IndexChangeListenerImpl indexChangeListenerImpl;
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat();

    public ResponseController() {
        DATE_FORMATTER.applyPattern(AppConstants.DATE_FORMAT);
        started = false;
        startTime = 0L;
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
        long elapsedTime;
        long currentTime = System.currentTimeMillis();
        String dateTimeString = DATE_FORMATTER.format(new Date(currentTime));
        boolean state = message.getAlert().contains("true");

        // StompMessageClient will run as background thread when
        // onMessageReceived receives a "start" message.
        switch (message.getCommand()) {
            case START_INDEX_MONITOR:
                if (!started) {
                    try {
                        String[] collection = message.getAlert().split(":", 3);
                        if (collection.length == 3) {
                            StompMessageClient client = StompMessageClient.getInstance(AppConstants.WS_ENDPOINT,
                                    AppConstants.TOPIC_ENDPOINT);

                            monitorRef = IndexChangeMonitorUtil.monitorSolr(client, collection[1], collection[2]);

                            if (monitorRef != null) {
                                message.setAlert(dateTimeString + "|The Solr index change montitor was started");
                                started = true;
                                indexChangeListenerImpl = IndexChangeMonitorUtil.getIndexChangeListenerImpl();
                            } else {
                                message.setAlert(dateTimeString + "|Invalid Collection or Shard was entered!");
                            }
                        } else {
                            message.setAlert(dateTimeString + "| You must enter a Collection Name");
                        }
                    } catch (Exception e) {
                        LOGGER.error("onMessageReceived() " + e.toString());
                    }
                }
                break;
            case STOP_INDEX_MONITOR:
                monitorRef.stop();
                started = false;
                startTime = 0L;
                message.setAlert(dateTimeString + "|The Solr index change montitor was stopped");
                break;
            case WATCH_DIR_CREATE:
                indexChangeListenerImpl.setWatchDirectoryCreate(state);
                message.setAlert(dateTimeString + "|Watching for directory creation: " + state);
                break;
            case WATCH_DIR_CHANGE:
                indexChangeListenerImpl.setWatchDirectoryChange(state);
                message.setAlert(dateTimeString + "|Watching for directory change: " + state);
                break;
            case WATCH_DIR_DELETE:
                indexChangeListenerImpl.setWatchDirectoryDelete(state);
                message.setAlert(dateTimeString + "|Watching for directory deletion: " + state);
                break;
            case WATCH_FILE_CREATE:
                indexChangeListenerImpl.setWatchFileCreate(state);
                message.setAlert(dateTimeString + "|Watching for file creation: " + state);
                break;
            case WATCH_FILE_CHANGE:
                indexChangeListenerImpl.setWatchFileChange(state);
                message.setAlert(dateTimeString + "|Watching for file change: " + state);
                break;
            case WATCH_FILE_DELETE:
                indexChangeListenerImpl.setWatchFileDelete(state);
                message.setAlert(dateTimeString + "|Watching for file deletion: " + state);
                break;
            default:
                if (startTime == 0) {
                    startTime = currentTime;
                }
                elapsedTime = ((currentTime - startTime) / 1000);
                message.setAlert(dateTimeString + "|" + elapsedTime + "|" + message.getAlert());
                break;
        }

        LOGGER.info("onMessageReceived() " + message.getAlert());

        return new ResponseMessage(message.getAlert());
    }
}
