/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prototype.utils;

/**
 *
 * @author mweigel
 */
public enum Command {
    START_INDEX_MONITOR(0),
    STOP_INDEX_MONITOR(1),
    WATCH_DIR_CREATE(2),
    WATCH_DIR_CHANGE(3),
    WATCH_DIR_DELETE(4),
    WATCH_FILE_CREATE(5),
    WATCH_FILE_CHANGE(6),
    WATCH_FILE_DELETE(7),
    RECEIVE_EVENT(8);

    public final int value;

    private Command(int value) {
        this.value = value;
    }
}
