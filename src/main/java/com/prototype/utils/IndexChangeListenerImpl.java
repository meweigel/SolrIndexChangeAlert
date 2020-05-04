package com.prototype.utils;

import java.io.File;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.prototype.client.StompMessageClient;
import static com.prototype.utils.Command.RECEIVE_EVENT;
import java.util.HashMap;
import org.apache.commons.io.monitor.FileAlterationMonitor;

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
 * A listener class with methods that are invoked when Linux file events occur
 */
public class IndexChangeListenerImpl implements FileAlterationListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexChangeListenerImpl.class);
    private final StompMessageClient client;
    private final HashMap<String, Long> sizeMap;
    private final HashMap<String, Long> typeTotalSizeMap;
    private boolean directoryCreate;
    private boolean directoryChange;
    private boolean directoryDelete;
    private boolean fileCreate;
    private boolean fileChange;
    private boolean fileDelete;
    private long totalSize;

    /**
     * The single parameterized constructor
     *
     * @param client - An instance of a StompMessageClient
     */
    public IndexChangeListenerImpl(StompMessageClient client) {
        this.client = client;
        totalSize = 0L;
        sizeMap = new HashMap<>();
        typeTotalSizeMap = new HashMap<>();
    }

    /**
     * The onStart method is called when FileAlterationObserver is started
     *
     * @param observer FileAlterationObserver
     */
    @Override
    public void onStart(final FileAlterationObserver observer) {
        //LOGGER.info("onStart() called because FileAlterationObserver started");
    }

    /**
     * The onDirectoryCreate is called when a directory is created
     *
     * @param directory File
     */
    @Override
    public void onDirectoryCreate(final File directory) {
        if (directoryCreate) {
            try {
                long sizeKb = directory.length() / 1000;
                totalSize += sizeKb;
                String name = directory.getName();
                sizeMap.put(name, sizeKb);
                String msg = "Directory - " + name
                        + " was created|N/A|" + sizeKb + "|" + sizeKb + "|" + totalSize + "|N/A";
                LOGGER.info("onDirectoryCreate() " + msg);
                client.sendMessage(msg, RECEIVE_EVENT);
            } catch (Exception e) {
                LOGGER.error("onDirectoryCreate() ", e);
            }
        }
    }

    /**
     * The onDirectoryChange method is called when a directory is changed
     *
     * @param directory File
     */
    @Override
    public void onDirectoryChange(final File directory) {
        if (directoryChange) {
            try {
                String name = directory.getName();
                long sizeKb = directory.length() / 1000;
                long delta = (sizeKb - sizeMap.get(name));
                totalSize += delta;
                sizeMap.put(name, sizeKb);
                String msg = "Directory - " + name
                        + " was changed|N/A|" + sizeKb + "|" + delta + "|" + totalSize + "|N/A";
                LOGGER.info("onDirectoryChange() " + msg);
                client.sendMessage(msg, RECEIVE_EVENT);
            } catch (Exception e) {
                LOGGER.error("onDirectoryChange() ", e);
            }
        }
    }

    /**
     * The onDirectoryDelete method is called when a directory is deleted
     *
     * @param directory File
     */
    @Override
    public void onDirectoryDelete(final File directory) {
        if (directoryDelete) {
            try {
                String name = directory.getName();
                long sizeKb = sizeMap.get(name);
                totalSize -= sizeKb;
                String msg = "Directory - " + name
                        + " was deleted|N/A|0|" + -sizeKb + "|" + totalSize + "|N/A";
                sizeMap.put(name, 0L);
                LOGGER.info("onDirectoryDelete() " + msg);
                client.sendMessage(msg, RECEIVE_EVENT);
            } catch (Exception e) {
                LOGGER.error("onDirectoryDelete() ", e);
            }
        }
    }

    /**
     * The onFileCreate method is called when a file is created
     *
     * @param file File
     */
    @Override
    public void onFileCreate(final File file) {
        if (fileCreate) {
            try {
                long sizeKb = file.length() / 1000;
                totalSize += sizeKb;
                String name = file.getName();

                String suffix = name.substring(name.indexOf('.') + 1);
                if (typeTotalSizeMap.containsKey(suffix)) {
                    long typeTotalSize = typeTotalSizeMap.get(suffix) + sizeKb;
                    typeTotalSizeMap.put(suffix, typeTotalSize);
                } else {
                    typeTotalSizeMap.put(suffix, sizeKb);
                }

                sizeMap.put(name, sizeKb);
                String msg = "Index - " + name
                        + " was created|" + suffix + "|" + sizeKb
                        + "|" + sizeKb + "|"
                        + totalSize + "|" + typeTotalSizeMap.get(suffix);
                LOGGER.info("onFileCreate() " + msg);
                client.sendMessage(msg, RECEIVE_EVENT);
            } catch (Exception e) {
                LOGGER.error("onFileCreate() ", e);
            }
        }
    }

    /**
     * The onFileChange method is called when a file is changed
     *
     * @param file File
     */
    @Override
    public void onFileChange(final File file) {
        if (fileChange) {
            try {
                String name = file.getName();
                long sizeKb = file.length() / 1000;
                long delta = (sizeKb - sizeMap.get(name));
                totalSize += delta;
                sizeMap.put(name, sizeKb);

                String suffix = name.substring(name.indexOf('.') + 1);
                if (typeTotalSizeMap.containsKey(suffix)) {
                    long typeTotalSize = typeTotalSizeMap.get(suffix) + delta;
                    typeTotalSizeMap.put(suffix, typeTotalSize);
                }

                String msg = "Index - " + name
                        + " was changed|" + suffix + "|" + sizeKb
                        + "|" + delta + "|" + totalSize
                        + "|" + typeTotalSizeMap.get(suffix);
                LOGGER.info("onFileChange() " + msg);
                client.sendMessage(msg, RECEIVE_EVENT);
            } catch (Exception e) {
                LOGGER.error("onFileChange() ", e);
            }
        }
    }

    /**
     * The onFileDelete method is called when a file is deleted
     *
     * @param file File
     */
    @Override
    public void onFileDelete(final File file) {
        if (fileDelete) {
            try {
                String name = file.getName();
                long sizeKb = sizeMap.get(name);
                totalSize -= sizeKb;

                String suffix = name.substring(name.indexOf('.') + 1);
                if (typeTotalSizeMap.containsKey(suffix)) {
                    long typeTotalSize = typeTotalSizeMap.get(suffix) - sizeKb;
                    typeTotalSizeMap.put(suffix, typeTotalSize);
                }

                String msg = "Index - " + name
                        + " was deleted|" + suffix + "|0|"
                        + -sizeKb + "|" + totalSize
                        + "|" + typeTotalSizeMap.get(suffix);
                sizeMap.put(name, 0L);
                LOGGER.info("onFileDelete() " + msg);
                client.sendMessage(msg, RECEIVE_EVENT);
            } catch (Exception e) {
                LOGGER.error("onFileDelete() ", e);
            }
        }
    }

    /**
     * The onStop method is called when the FileAlterationObserver is stopped
     *
     * @param observer FileAlterationObserver
     */
    @Override
    public void onStop(final FileAlterationObserver observer) {
        //LOGGER.info("onStop() called because FileAlterationObserver stopped");
    }

    /**
     *
     * @param command Enum
     * @param state true or false
     */
    public void setWatch(Command command, boolean state) {
        switch (command) {
            case WATCH_DIR_CREATE:
                directoryCreate = state;
                break;
            case WATCH_DIR_CHANGE:
                directoryChange = state;
                break;
            case WATCH_DIR_DELETE:
                directoryDelete = state;
                break;
            case WATCH_FILE_CREATE:
                fileCreate = state;
                break;
            case WATCH_FILE_CHANGE:
                fileChange = state;
                break;
            case WATCH_FILE_DELETE:
                fileDelete = state;
                break;
        }
    }
}
