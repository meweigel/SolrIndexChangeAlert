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
var chart = null;
var workerThread = undefined;
var messageCount = 0;
var count = 0;
var plotsSize = false;
var plotDelta = false;
var plotTypeTotal = false;
var tableRows = new String("");
var dataFeedFlag = false;


var Command = {
    START_INDEX_MONITOR: 0,
    STOP_INDEX_MONITOR: 1,
    WATCH_DIR_CREATE: 2,
    WATCH_DIR_CHANGE: 3,
    WATCH_DIR_DELETE: 4,
    WATCH_FILE_CREATE: 5,
    WATCH_FILE_CHANGE: 6,
    WATCH_FILE_DELETE: 7,
    RECEIVE_EVENT: 8
};

function AlertMessage(alert, command) {
    this.alert = alert;
    this.command = command;
}

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    $("#checkboxSet").prop("disabled", !connected);

    if (connected) {
        $("#tableboxSet").show();
        $("#clear").show();
        $("#btn-save").show();
        $("#btn-plot").show();
        $("#resultsTable").show();
    } else {
        $("#tableboxSet").hide();
        $("#clear").hide();
        $("#btn-save").hide();
        $("#btn-plot").hide();
        $("#resultsTable").hide();
    }
    clearTable();
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

        var alertMessage = {
            "command": "START_INDEX_MONITOR",
            "collection": $("#collection").val(),
            "alert": "WebSocket connection successfully made:",
            "dateTimeStamp": getDateTime(),
            "indexType": " ",
            "active": false,
            "elapsedTime": 0,
            "sizeKb": 0,
            "deltaKb": 0,
            "totalSizeKb": 0,
            "typeTotalKb": 0
        };

        showMessageAlert(JSON.stringify(alertMessage));
    });
}

function getDateTime() {
    var d = new Date();
    var month = d.getMonth() + 1;
    if (month < 10) {
        month = "0" + month;
    }
    var day = d.getDate();
    if (day < 10) {
        day = "0" + day;
    }
    var hrs = d.getHours();
    if (hrs < 10) {
        hrs = "0" + hrs;
    }
    var min = d.getMinutes();
    if (min < 10) {
        min = "0" + min;
    }
    var sec = d.getSeconds();
    if (sec < 10) {
        sec = "0" + sec;
    }

    var dateTime = d.getFullYear() +
            "-" + month +
            "-" + day +
            " " + hrs +
            ":" + min +
            ":" + sec;

    return dateTime;
}

function showMessageAlert(message) {

    if (message !== undefined && message !== null) {

        var alertMessage = JSON.parse(message);

        if (alertMessage.command === "START_INDEX_MONITOR") {
            $("#indexChangeAlert").append("<tr><td id='time'>" +
                    alertMessage.dateTimeStamp +
                    "</td><td id='elapsedTime'>N/A</td><td id='message'>" +
                    alertMessage.alert + ":" + alertMessage.collection +
                    "</td><td id='type'>N/A</td><td id='size'>N/A</td><td id='delta'>N/A</td>" +
                    "<td id='totalSize'>N/A</td><td id='typeTotal'>N/A</td></tr>");
            dataFeedFlag = true;
        } else {
            if (dataFeedFlag) {
                clearTable();
                dataFeedFlag = false;
            }

            messageCount++;
            count++;

            tableRows = tableRows.concat("<tr><td id='time'>" +
                    alertMessage.dateTimeStamp +
                    "</td><td id='elapsedTime'>" +
                    alertMessage.elapsedTime +
                    "</td><td id='message'>" +
                    alertMessage.alert +
                    "</td><td id='type'>" +
                    alertMessage.indexType +
                    "</td><td id='size'>" +
                    alertMessage.sizeKb +
                    "</td><td id='delta'>" +
                    alertMessage.deltaKb +
                    "</td><td id='totalSize'>" +
                    alertMessage.totalSizeKb +
                    "</td><td id='typeTotal'>" +
                    alertMessage.typeTotalKb +
                    "</td></tr>");

            if (count === 50) {
                appendTableRows();
            } else {
                timer();
            }
        }

        //$("#indexChangeAlert").scroll();
        $("#indexChangeAlert").scrollTop(messageCount * 100);
    } else {
        console.log("showMessageAlert(): alertMessage is undefined or null");
    }
}

function appendTableRows() {
    if (tableRows.length > 0) {
        $("#indexChangeAlert").append(tableRows);
        count = 0;
        tableRows = new String("");
    }
}

// Look for remaining row data
function timer() {
    var countMoment = count;
    setTimeout(function () {
        if (countMoment == count) {
            appendTableRows();
        }
    }, 3000);
}

function clearTable() {
    $("#resultsTable > tbody").empty();
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendMessage(selectValue) {
    stompClient.send("/app/alertMessage", {}, JSON.stringify(selectValue));
}

function callWorker(javaScript, data) {

    if (typeof (Worker) !== "undefined") {

        if (typeof (workerThread) == "undefined") {
            workerThread = new Worker(javaScript);
        }

        workerThread.onmessage = function (event) {
            plot(event.data);
        };

        workerThread.onerror = function (e) {
            alert('Error: Line ' + e.lineno + ' in ' + e.filename + ': ' + e.message);
        };

        //start the worker - Using Transferrable objects
        var ab = new ArrayBuffer(data);
        workerThread.postMessage(data, [ab]);
    } else {
        $("#errorMsg").innerHTML = "Sorry! No Web Worker support.";
    }
}

function stopWorker() {
    workerThread.terminate();
    workerThread = undefined;
}


function plot(dataSet) {

    var ctx = $("#solrLineChart");

    chart = new Chart(ctx, {
        type: 'scatter',
        data: {
            datasets: dataSet
        },
        options: {
            responsive: true,
            title: {
                display: true,
                fontSize: 24,
                text: 'Size (KB) of Solr Index Types Vs. Elapsed Time (sec)'
            },
            multiTooltipTemplate: function (self) {
                return self.label[self.datasetLabel] + ': ' + self.value;
            },
//            tooltips: {
//                mode: 'label',
//            },
//            hover: {
//                mode: 'nearest',
//                intersect: true
//            },
            scales: {
                xAxes: [{
                        display: true,
                        scaleLabel: {
                            display: true,
                            labelString: 'Time (Seconds)'
                        },
                        position: 'bottom',
                        gridLines: {
                            offsetGridLines: true
                        },
                        ticks: {
                            stepSize: 5.0,
                            major: {
                                fontStyle: 'bold',
                                fontColor: '#FF0000'
                            }
                        }
                    }],
                yAxes: [{
                        display: true,
                        type: 'logarithmic',
                        ticks: {
                            userCallback: function (tick) {
                                var remain = tick / (Math.pow(10, Math.floor(Chart.helpers.log10(tick))));
                                if (remain === 1 || remain === 10 || remain === 100 || remain === 1000 ||
                                        remain === 10000 || remain === 100000 || remain === 1000000 || remain === 10000000) {
                                    return "10^" + Chart.helpers.log10(tick) + " Kb";
                                }
                                return '';
                            },
                        },
                        scaleLabel: {
                            display: true,
                            labelString: 'Size (KB)'
                        }
                    }]
            }
        }
    });
}



$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });

    $("#connect").click(function () {
        var collection = $("#collection").val();
        if (collection.length > 0) {
            connect();

            var alertMessage = {
                "command": Command.START_INDEX_MONITOR,
                "collection": collection,
                "alert": "Connect Index Monitoring Collection",
                "dateTimeStamp": getDateTime(),
                "indexType": " ",
                "active": false,
                "elapsedTime": 0,
                "sizeKb": 0,
                "deltaKb": 0,
                "totalSizeKb": 0,
                "typeTotalKb": 0
            };

            setTimeout(function () {
                sendMessage(alertMessage);
            }, 1000);
        } else {
            alert("You must enter a Collection Name!");
        }
    });

    $("#disconnect").click(function () {
        $("#directoryCreate").prop("checked", false);
        $("#directoryChange").prop("checked", false);
        $("#directoryDelete").prop("checked", false);
        $("#indexCreate").prop("checked", false);
        $("#indexChange").prop("checked", false);
        $("#indexDelete").prop("checked", false);
        $("#size").prop("checked", false);
        $("#delta").prop("checked", false);
        $("#typeTotal").prop("checked", false);

        var alertMessage = {
            "command": Command.STOP_INDEX_MONITOR,
            "collection": $("#collection").val(),
            "alert": "Disconnect Index Monitoring",
            "dateTimeStamp": getDateTime(),
            "indexType": " ",
            "active": true,
            "elapsedTime": 0,
            "sizeKb": 0,
            "deltaKb": 0,
            "totalSizeKb": 0,
            "typeTotalKb": 0
        };

        sendMessage(alertMessage);
        setTimeout(function () {
            disconnect()
        }, 1000);
    });

    $('input#directoryCreate').change(function () {

        var alertMessage = {
            "command": Command.WATCH_DIR_CREATE,
            "collection": $("#collection").val(),
            "alert": "",
            "dateTimeStamp": getDateTime(),
            "indexType": " ",
            "active": true,
            "elapsedTime": 0,
            "sizeKb": 0,
            "deltaKb": 0,
            "totalSizeKb": 0,
            "typeTotalKb": 0
        };

        if ($('input[id=directoryCreate]').is(':checked')) {
            alertMessage.alert = "Watch Directory Create:true";
            sendMessage(alertMessage);
        } else {
            alertMessage.alert = "Watch Directory Create:false";
            sendMessage(alertMessage);
        }
    });

    $('input#directoryChange').change(function () {

        var alertMessage = {
            "command": Command.WATCH_DIR_CHANGE,
            "collection": $("#collection").val(),
            "alert": "",
            "dateTimeStamp": getDateTime(),
            "indexType": " ",
            "active": true,
            "elapsedTime": 0,
            "sizeKb": 0,
            "deltaKb": 0,
            "totalSizeKb": 0,
            "typeTotalKb": 0
        };

        if ($('input[id=directoryChange]').is(':checked')) {
            alertMessage.alert = "Watch Directory Change:true";
            sendMessage(alertMessage);
        } else {
            alertMessage.alert = "Watch Directory Change:false";
            sendMessage(alertMessage);
        }
    });

    $('input#directoryDelete').change(function () {

        var alertMessage = {
            "command": Command.WATCH_DIR_DELETE,
            "collection": $("#collection").val(),
            "alert": "",
            "dateTimeStamp": getDateTime(),
            "indexType": " ",
            "active": true,
            "elapsedTime": 0,
            "sizeKb": 0,
            "deltaKb": 0,
            "totalSizeKb": 0,
            "typeTotalKb": 0
        };

        if ($('input[id=directoryDelete]').is(':checked')) {
            alertMessage.alert = "Watch Directory Delete:true";
            sendMessage(alertMessage);
        } else {
            alertMessage.alert = "Watch Directory Delete:false";
            sendMessage(alertMessage);
        }
    });

    $('input#indexCreate').change(function () {

        var alertMessage = {
            "command": Command.WATCH_FILE_CREATE,
            "collection": $("#collection").val(),
            "alert": "",
            "dateTimeStamp": getDateTime(),
            "indexType": " ",
            "active": true,
            "elapsedTime": 0,
            "sizeKb": 0,
            "deltaKb": 0,
            "totalSizeKb": 0,
            "typeTotalKb": 0
        };

        if ($('input[id=indexCreate]').is(':checked')) {
            alertMessage.alert = "Watch File Create:true";
            sendMessage(alertMessage);
        } else {
            alertMessage.alert = "Watch File Create:false";
            sendMessage(alertMessage);
        }
    });

    $('input#indexChange').change(function () {

        var alertMessage = {
            "command": Command.WATCH_FILE_CHANGE,
            "collection": $("#collection").val(),
            "alert": "",
            "dateTimeStamp": getDateTime(),
            "indexType": " ",
            "active": true,
            "elapsedTime": 0,
            "sizeKb": 0,
            "deltaKb": 0,
            "totalSizeKb": 0,
            "typeTotalKb": 0
        };

        if ($('input[id=indexChange]').is(':checked')) {
            alertMessage.alert = "Watch File Change:true";
            sendMessage(alertMessage);
        } else {
            alertMessage.alert = "Watch File Change:false";
            sendMessage(alertMessage);
        }
    });

    $('input#indexDelete').change(function () {

        var alertMessage = {
            "command": Command.WATCH_FILE_DELETE,
            "collection": $("#collection").val(),
            "alert": "",
            "dateTimeStamp": getDateTime(),
            "indexType": " ",
            "active": true,
            "elapsedTime": 0,
            "sizeKb": 0,
            "deltaKb": 0,
            "totalSizeKb": 0,
            "typeTotalKb": 0
        };

        if ($('input[id=indexDelete]').is(':checked')) {
            alertMessage.alert = "Watch File Delete:true";
            sendMessage(alertMessage);
        } else {
            alertMessage.alert = "Watch File Delete:false";
            sendMessage(alertMessage);
        }
    });

    $('input#size').change(function () {
        if ($('input[id=size]').is(':checked')) {
            plotsSize = true;
        } else {
            plotsSize = false;
        }
    });

    $('input#delta').change(function () {
        if ($('input[id=delta]').is(':checked')) {
            plotDelta = true;
        } else {
            plotDelta = false;
        }
    });

    $('input#typeTotal').change(function () {
        if ($('input[id=typeTotal]').is(':checked')) {
            plotTypeTotal = true;
        } else {
            plotTypeTotal = false;
        }
    });


    $("#clear").click(function () {
        clearTable();
    });

    $("#btn-save").click(function () {
        //var html = $("div#messageTable").html();
        var json = JSON.stringify(makeJsonFromTable("resultsTable"));
        var filename = "SolrLogFile";
        var blob = new Blob([json], {type: "text/plain;charset=utf-8"});
        saveAs(blob, filename + ".json");
    });

    // Plot Data
    $("#btn-plot").click(function () {
        if (chart !== null) {
            chart.destroy();
        }

        var jsonObject = makeJsonFromTable("resultsTable");

        // Conduct operation in worker thread
        callWorker("./ChartDataSetBuilder.js", {'cmds': ['plotsSize', 'plotDelta', 'plotTypeTotal', 'createDataSet'],
            'values': [plotsSize, plotDelta, plotTypeTotal, jsonObject]});
    });

    // Close the plot
    $(".close").click(function () {
        stopWorker();
    });
});


