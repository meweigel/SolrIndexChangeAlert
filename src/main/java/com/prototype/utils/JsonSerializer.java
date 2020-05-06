/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prototype.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author mweigel
 * @param <T>
 */
public class JsonSerializer<T> {

    private final GsonBuilder gsonBuilder;
    private final Gson gson;
    private T t;

    public JsonSerializer() {
        gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gson = gsonBuilder.create();
    }

    public T getObject(String schemaJson) {
        return gson.fromJson(schemaJson, (Class<T>) t.getClass());
    }

    public String getJson(T object) {
        return gson.toJson(object);
    }
}
