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



var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    $("#radioSet").prop("disabled", !connected);
    
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#indexChangeAlert").html("");
}

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/responseMessage', function (response) {
            showMessageAlert(JSON.parse(response.body).content);
        });
        stompClient.send("/topic/responseMessage", {}, JSON.stringify({'content': 'Success: ' + frame}));
    });
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}


function sendMessage(selectValue) {
	var msg = "{'alert': '" + selectValue + "'}";
    stompClient.send("/app/alertMessage", {}, JSON.stringify({'alert': selectValue}));
}

function clearTable(){
	$("#conversation > tbody").html("");
}

function showMessageAlert(message) {
    $("#indexChangeAlert").append("<tr><td>" + message + "</td></tr>");
    $("#indexChangeAlert").scroll(function(){
        $("span").text(x += 1);
    });
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#start" ).click(function() {
        var selectValue = $('input[name=rbnNumber]:checked').val(); 
        sendMessage(selectValue);
    });
    $( "#stop" ).click(function() {
        var selectValue = $('input[name=rbnNumber]:checked').val(); 
        sendMessage(selectValue);
    });
    $( "#clear" ).click(function() { clearTable(); });
});

$("#tableId > tbody").html("");