package com.prototype.model;

import com.prototype.utils.Command;
import java.io.Serializable;

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
 * The Alert Message class
 */
public class AlertMessage implements Serializable {

    private Command command;
    private String collection;
    private String alert;
    private String dateTimeStamp;
    private String indexType;
    private boolean active;
    private long elapsedTime;
    private long sizeKb;
    private long deltaKb;
    private long totalSizeKb;
    private long typeTotalKb;

    public AlertMessage() {
        command = Command.RECEIVE_EVENT;
        alert = " ";
        dateTimeStamp = " ";
        indexType = " ";
        active = false;
        elapsedTime = 0L;
        sizeKb = 0L;
        deltaKb = 0L;
        totalSizeKb = 0L;
        typeTotalKb = 0L;
    }

    public AlertMessage(String msg) {
        command = Command.RECEIVE_EVENT;
        alert = msg;
        dateTimeStamp = " ";
        indexType = " ";
        active = false;
        elapsedTime = 0L;
        sizeKb = 0L;
        deltaKb = 0L;
        totalSizeKb = 0L;
        typeTotalKb = 0L;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getIndexType() {
        return indexType;
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    public long getSizeKb() {
        return sizeKb;
    }

    public void setSizeKb(long sizeKb) {
        this.sizeKb = sizeKb;
    }

    public long getDeltaKb() {
        return deltaKb;
    }

    public void setDeltaKb(long deltaKb) {
        this.deltaKb = deltaKb;
    }

    public long getTotalSizeKb() {
        return totalSizeKb;
    }

    public void setTotalSizeKb(long totalSizeKb) {
        this.totalSizeKb = totalSizeKb;
    }

    public long getTypeTotalKb() {
        return typeTotalKb;
    }

    public void setTypeTotalKb(long typeTotalKb) {
        this.typeTotalKb = typeTotalKb;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDateTimeStamp() {
        return dateTimeStamp;
    }

    public void setDateTimeStamp(String dateTimeStamp) {
        this.dateTimeStamp = dateTimeStamp;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }
}
