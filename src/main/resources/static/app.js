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
var START_INDEX_MONITOR = "startIndexMonitor";
var STOP_INDEX_MONITOR = "stopIndexMonitor";
var WATCH_DIR_CREATE = "watchDirCreate";
var WATCH_DIR_CHANGE = "watchDirChange";
var WATCH_DIR_DELETE = "watchDirDelete";
var WATCH_FILE_CREATE = "watchFileCreate";
var WATCH_FILE_CHANGE = "watchFileChange";
var WATCH_FILE_DELETE = "watchFileDelete";

function setConnected(connected) {
	$("#connect").prop("disabled", connected);
	$("#disconnect").prop("disabled", !connected);
	$("#checkboxSet").prop("disabled", !connected);

	if (connected) {
		$("#conversation").show();
	} else {
		$("#conversation").hide();
	}
	$("#indexChangeAlert").html("");
}

function connect() {
	var socket = new SockJS('/gs-guide-websocket');
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
		setConnected(true);
		console.log('Connected: ' + frame);
		stompClient.subscribe('/topic/responseMessage', function(response) {
			showMessageAlert(JSON.parse(response.body).content);
		});
		stompClient.send("/topic/responseMessage", {}, JSON.stringify({
			'content' : 'Success: ' + frame
		}));
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
	stompClient.send("/app/alertMessage", {}, JSON.stringify({'alert' : selectValue}));
}

function clearTable() {
	$("#conversation > tbody").html("");
}

function showMessageAlert(message) {
	$("#indexChangeAlert").append("<tr><td>" + message + "</td></tr>");
	$("#indexChangeAlert").scroll(function() {
		$("span").text(x += 1);
	});
}

$(function() {
	$("form").on('submit', function(e) {
		e.preventDefault();
	});
	
	$("#connect").click(function() {
		connect();
		setTimeout(function(){sendMessage(START_INDEX_MONITOR)}, 1000);
	});
	
	$("#disconnect").click(function() {
		$("#directoryCreate").prop("checked", false);	
		$("#directoryChange").prop("checked", false);
		$("#directoryDelete").prop("checked", false);
		$("#fileCreate").prop("checked", false);
		$("#fileChange").prop("checked", false);
		$("#fileDelete").prop("checked", false);
		sendMessage(STOP_INDEX_MONITOR);
		setTimeout(function(){disconnect()}, 1000);
	});
	
	
    $('input#directoryCreate').change(function() {
        if($('input[id=directoryCreate]').is(':checked')){
        	sendMessage(WATCH_DIR_CREATE + ":true");
        }else{
          	sendMessage(WATCH_DIR_CREATE + ":false");
        }
    });
    
    $('input#directoryChange').change(function() {
        if($('input[id=directoryChange]').is(':checked')){
        	sendMessage(WATCH_DIR_CHANGE + ":true");
        }else{
          	sendMessage(WATCH_DIR_CHANGE + ":false");
        }
    });
    
    $('input#directoryDelete').change(function() {
        if($('input[id=directoryDelete]').is(':checked')){
        	sendMessage(WATCH_DIR_DELETE + ":true");
        }else{
          	sendMessage(WATCH_DIR_DELETE + ":false");
        }
    });
     
    $('input#fileCreate').change(function() {
        if($('input[id=fileCreate]').is(':checked')){
        	sendMessage(WATCH_FILE_CREATE + ":true");
        }else{
          	sendMessage(WATCH_FILE_CREATE + ":false");
        }
    });
        
    $('input#fileChange').change(function() {
        if($('input[id=fileChange]').is(':checked')){
        	sendMessage(WATCH_FILE_CHANGE + ":true");
        }else{
          	sendMessage(WATCH_FILE_CHANGE + ":false");
        }
    });
        
    $('input#fileDelete').change(function() {
        if($('input[id=fileDelete]').is(':checked')){
        	sendMessage(WATCH_FILE_DELETE + ":true");
        }else{
          	sendMessage(WATCH_FILE_DELETE + ":false");
        }
    });
    
	$("#clear").click(function() {
		clearTable();
	});
});

$("#tableId > tbody").html("");

