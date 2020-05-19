package com.prototype.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.prototype.model.AlertMessage;
import com.prototype.model.ResponseMessage;
import com.prototype.utils.AppConstants;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
 * The singleton StompMessageClient class allows Stomp messages to be sent to
 * subscriber endpoints enabling proccess control and intervention. We only want
 * one instance of this class ever - Singleton
 */
public class StompMessageClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(StompMessageClient.class);
    private static StompMessageClient stompMessageClient = null;
    private StompSession theSession;

    /**
     * The private singleton parameterized constructor
     *
     * @param wsEndpoint - The SockJS client connection endpoint
     * @param topicEndPoint - The subscriber topic endpoint
     */
    private StompMessageClient(String wsEndpoint, String topicEndPoint) {

        System.setProperty("proxyHost", AppConstants.PROXY_HOST);
        System.setProperty("proxyPort", AppConstants.PROXY_PORT);

        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        String url = "ws://" + AppConstants.PROXY_HOST + ":" + AppConstants.PROXY_PORT + "/" + wsEndpoint;

        StompSessionHandler handler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
                session.subscribe(AppConstants.TOPIC_ENDPOINT, new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return ResponseMessage.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        // ResponseMessage responseMessage = (ResponseMessage) payload;
                    }
                });

                theSession = session;
            }
        };

        stompClient.connect(url, new WebSocketHttpHeaders(), handler, AppConstants.PROXY_PORT);
    }

    /**
     * The user acces point to retrieving an instance of the StompMessageClient
     * class
     *
     * @param wsEndpoint - The SockJS client connection endpoint
     * @param topicEndPoint - The subscriber topic endpoint
     * @return StompMessageClient
     */
    public static StompMessageClient getInstance(String wsEndpoint, String topicEndPoint) {
        if (stompMessageClient == null) {
            stompMessageClient = new StompMessageClient(wsEndpoint, topicEndPoint);
        }

        return stompMessageClient;
    }

    /**
     * The sendMessage client method for sending Stomp messages to the
     * topicEndPoint
     *
     * @param message The AlertMessage message
     */
    synchronized public void sendMessage(AlertMessage message) {
        try {
            theSession.send("/app" + AppConstants.APP_ENDPOINT, message);
        } catch (Throwable t) {
            LOGGER.error("sendMessage() " + t.toString());
        }
    }
}
