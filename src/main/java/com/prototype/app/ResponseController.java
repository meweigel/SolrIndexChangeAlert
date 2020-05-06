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
import static com.prototype.utils.AppConstants.MESSAGES;
import com.prototype.utils.Command;
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
import com.prototype.utils.IterateFiles;
import com.prototype.utils.JsonSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

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
 * This class controls the processing and routing of WebSocket messages
 */
@Controller
public class ResponseController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseController.class);
    private boolean started;
    private long startTime;
    private final List<FileAlterationMonitor> monitorRefList;
    private final List<IndexChangeListenerImpl> indexChangeListenerImplList;
    private final JsonSerializer jsonSerializer;
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat();
    
    public ResponseController() {
        DATE_FORMATTER.applyPattern(AppConstants.DATE_FORMAT);
        started = false;
        startTime = 0L;
        monitorRefList = new ArrayList<>();
        indexChangeListenerImplList = new ArrayList<>();
        jsonSerializer = new JsonSerializer();
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
        long currentTime = System.currentTimeMillis();
        String dateTimeString = DATE_FORMATTER.format(new Date(currentTime));
        boolean state = message.isActive();
        Command command = message.getCommand();

        // StompMessageClient will run as background thread when
        // onMessageReceived receives a "start" message.
        switch (command) {
            case START_INDEX_MONITOR:
                if (!started) {
                    startIndexMonitors(message, dateTimeString);
                }
                break;
            case STOP_INDEX_MONITOR:
                stopIndexMonitors(message, dateTimeString, command);
                break;
            case WATCH_DIR_CREATE:
            case WATCH_DIR_CHANGE:
            case WATCH_DIR_DELETE:
            case WATCH_FILE_CREATE:
            case WATCH_FILE_CHANGE:
            case WATCH_FILE_DELETE:
                for (IndexChangeListenerImpl indexChangeListenerImpl : indexChangeListenerImplList) {
                    indexChangeListenerImpl.setWatch(command, state);
                    message.setDateTimeStamp(dateTimeString);
                    message.setActive(state);
                    message.setAlert(MESSAGES[command.value]);
                }
                break;
            default:
                if (startTime == 0) {
                    startTime = currentTime;
                }
                
                message.setElapsedTime(((currentTime - startTime) / 1000));
                message.setDateTimeStamp(dateTimeString);
                break;
        }
        
        LOGGER.info("onMessageReceived() " + message.getAlert());
        
        //System.out.println("json AlertMessage = " + jsonSerializer.getJson(message));
        
        return new ResponseMessage(jsonSerializer.getJson(message));
    }

    /**
     * Start all Index Monitors
     *
     * @param message
     * @param dateTimeString
     */
    private void startIndexMonitors(AlertMessage message, String dateTimeString) {
        
        message.setDateTimeStamp(dateTimeString);
        
        try {
            StompMessageClient client = StompMessageClient.getInstance(AppConstants.WS_ENDPOINT,
                    AppConstants.TOPIC_ENDPOINT);
            
            Collection<String> indexDirList = IterateFiles.getTargetFiles(AppConstants.INDEX_FOLDER_ROOT, "index", message.getCollection());
            
            for (String indexDir : indexDirList) {
                
                System.out.println("startIndexMonitors(): indexDir = " + indexDir);
                
                FileAlterationMonitor monitorRef = IndexChangeMonitorUtil.monitorSolr(client, indexDir);
                
                if (monitorRef != null) {
                    monitorRefList.add(monitorRef);
                    message.setAlert(MESSAGES[0]);
                    started = true;
                    message.setActive(started);
                    indexChangeListenerImplList.add(IndexChangeMonitorUtil.getIndexChangeListenerImpl());
                } else {
                    message.setAlert("Invalid Collection was entered!");
                }
            }
        } catch (Exception e) {
            LOGGER.error("onMessageReceived() " + e.toString());
        }
    }

    /**
     * Stop all Index Monitors
     *
     * @param message
     * @param dateTimeString
     * @param command
     */
    private void stopIndexMonitors(AlertMessage message, String dateTimeString, Command command) {
        for (FileAlterationMonitor monitorRef : monitorRefList) {
            try {
                monitorRef.stop();
                started = false;
                startTime = 0L;
                message.setDateTimeStamp(dateTimeString);
                message.setAlert(MESSAGES[command.value]);
                message.setActive(started);
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(ResponseController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
