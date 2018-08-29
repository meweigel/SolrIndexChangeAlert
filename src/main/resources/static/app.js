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
var messageCount = 0;

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

function DataSet(data, label, borderColor, fill) {
    this.data = data;
    this.label = label;
    this.borderColor = borderColor;
    this.fill = fill;
}

var DataTypeHash = {};

DataTypeHash["cfe"] = {};
DataTypeHash["cfe"].ySizeData = [];
DataTypeHash["cfe"].yDeltaData = [];
DataTypeHash["cfe"].yTypeTotalData = [];
DataTypeHash["cfe"].index = 0;
DataTypeHash["cfs"] = {};
DataTypeHash["cfs"].ySizeData = [];
DataTypeHash["cfs"].yDeltaData = [];
DataTypeHash["cfs"].yTypeTotalData = [];
DataTypeHash["cfs"].index = 0;
DataTypeHash["doc"] = {};
DataTypeHash["doc"].ySizeData = [];
DataTypeHash["doc"].yDeltaData = [];
DataTypeHash["doc"].yTypeTotalData = [];
DataTypeHash["doc"].index = 0;
DataTypeHash["dvd"] = {};
DataTypeHash["dvd"].ySizeData = [];
DataTypeHash["dvd"].yDeltaData = [];
DataTypeHash["dvd"].yTypeTotalData = [];
DataTypeHash["dvd"].index = 0;
DataTypeHash["dvm"] = {};
DataTypeHash["dvm"].ySizeData = [];
DataTypeHash["dvm"].yDeltaData = [];
DataTypeHash["dvm"].yTypeTotalData = [];
DataTypeHash["dvm"].index = 0;
DataTypeHash["fdt"] = {};
DataTypeHash["fdt"].ySizeData = [];
DataTypeHash["fdt"].yDeltaData = [];
DataTypeHash["fdt"].yTypeTotalData = [];
DataTypeHash["fdt"].index = 0;
DataTypeHash["fdx"] = {};
DataTypeHash["fdx"].ySizeData = [];
DataTypeHash["fdx"].yDeltaData = [];
DataTypeHash["fdx"].yTypeTotalData = [];
DataTypeHash["fdx"].index = 0;
DataTypeHash["fnm"] = {};
DataTypeHash["fnm"].ySizeData = [];
DataTypeHash["fnm"].yDeltaData = [];
DataTypeHash["fnm"].yTypeTotalData = [];
DataTypeHash["fnm"].index = 0;
DataTypeHash["lock"] = {};
DataTypeHash["lock"].ySizeData = [];
DataTypeHash["lock"].yDeltaData = [];
DataTypeHash["lock"].yTypeTotalData = [];
DataTypeHash["lock"].index = 0;
DataTypeHash["nvd"] = {};
DataTypeHash["nvd"].ySizeData = [];
DataTypeHash["nvd"].yDeltaData = [];
DataTypeHash["nvd"].yTypeTotalData = [];
DataTypeHash["nvd"].index = 0;
DataTypeHash["nvm"] = {};
DataTypeHash["nvm"].ySizeData = [];
DataTypeHash["nvm"].yDeltaData = [];
DataTypeHash["nvm"].yTypeTotalData = [];
DataTypeHash["nvm"].index = 0;
DataTypeHash["pos"] = {};
DataTypeHash["pos"].ySizeData = [];
DataTypeHash["pos"].yDeltaData = [];
DataTypeHash["pos"].yTypeTotalData = [];
DataTypeHash["pos"].index = 0;
DataTypeHash["si"] = {};
DataTypeHash["si"].ySizeData = [];
DataTypeHash["si"].yDeltaData = [];
DataTypeHash["si"].yTypeTotalData = [];
DataTypeHash["si"].index = 0;
DataTypeHash["tim"] = {};
DataTypeHash["tim"].ySizeData = [];
DataTypeHash["tim"].yDeltaData = [];
DataTypeHash["tim"].yTypeTotalData = [];
DataTypeHash["tim"].index = 0;
DataTypeHash["tip"] = {};
DataTypeHash["tip"].ySizeData = [];
DataTypeHash["tip"].yDeltaData = [];
DataTypeHash["tip"].yTypeTotalData = [];
DataTypeHash["tip"].index = 0;


function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    $("#checkboxSet").prop("disabled", !connected);

    if (connected) {
        $("#resultsTable").show();
    } else {
        $("#resultsTable").hide();
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


        stompClient.send("/topic/responseMessage", {}, JSON.stringify({
            'content': getDateTime() + '|WebSocket connection successfully made '
        }));
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

function getRndInteger(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function RGBToHex(r, g, b) {
    var bin = r << 16 | g << 8 | b;
    return (function (h) {
        return new Array(7 - h.length).join("0") + h
    })(bin.toString(16).toUpperCase())
}

function getRandomColor() {
    var red = getRndInteger(0, 255);
    var green = getRndInteger(0, 255);
    var blue = getRndInteger(0, 255);
    return ("#" + RGBToHex(red, green, blue));
}


function plot(xLabelData, dataSet) {

    new Chart(document.getElementById("solrLineChart"), {
        type: 'line',
        data: {
            labels: xLabelData,
            datasets: dataSet
        },
        options: {
            responsive: true,
            title: {
                display: true,
                fontSize: 14,
                text: 'Size (KB) of Solr Index Types Vs. Elapsed Time (sec)'
            },
            scales: {
                xAxes: [{
                        display: true,
                        scaleLabel: {
                            display: true,
                            labelString: 'Seconds'
                        },
                        position: 'bottom',
                        gridLines: {
                            offsetGridLines: true
                        },
                        ticks: {
                            major: {
                                fontStyle: 'bold',
                                fontColor: '#FF0000'
                            }
                        }
                    }],
                yAxes: [{
                        display: true,
                        type: 'logarithmic',
                        scaleLabel: {
                            display: true,
                            labelString: 'Size (KB)'
                        }
                    }]
            }
        }
    });
}


function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
    document.getElementById("clear").disabled = true;
    document.getElementById("btn-save").disabled = true;
    document.getElementById("btn-plot").disabled = true;
}

function sendMessage(selectValue) {
    stompClient.send("/app/alertMessage", {}, JSON.stringify(selectValue));
}

function clearTable() {
    $("#resultsTable > tbody").empty();
}

function showMessageAlert(message) {
    var text = message.split("|");
    if (text.length == 2) {
        $("#indexChangeAlert").append("<tr><td id='time'>" +
                text[0] +
                "</td><td id='elapsedTime'>N/A</td><td id='message'>" +
                text[1] +
                "</td><td id='type'>N/A</td><td id='size'>N/A</td><td id='delta'>N/A</td>" +
                "<td id='totalSize'>N/A</td><td id='typeTotal'>N/A</td></tr>");
        document.getElementById("clear").disabled = false;
        document.getElementById("btn-save").disabled = false;
        document.getElementById("btn-plot").disabled = false;
    } else if (text.length == 8) {
        $("#indexChangeAlert").append("<tr><td id='time'>" +
                text[0] +
                "</td><td id='elapsedTime'>" +
                text[1] +
                "</td><td id='message'>" +
                text[2] +
                "</td><td id='type'>" +
                text[3] +
                "</td><td id='size'>" +
                text[4] +
                "</td><td id='delta'>" +
                text[5] +
                "</td><td id='totalSize'>" +
                text[6] +
                "</td><td id='typeTotal'>" +
                text[7] +
                "</td></tr>");
    }
    messageCount++;
    //$("#indexChangeAlert").scroll();
    $("#indexChangeAlert").scrollTop(messageCount * 100);
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });

    $("#connect").click(function () {
        var collection = $("#collection").val();
        if (collection.length > 0) {
            connect();
            var shard = $("#shard").val();
            var alertMessage = new AlertMessage("|Connect Index Monitoring Collection:" +
                    collection + ":" + shard, Command.START_INDEX_MONITOR);
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
        var alertMessage = new AlertMessage("|Disconnect Index Monitoring", Command.STOP_INDEX_MONITOR);
        sendMessage(alertMessage);
        setTimeout(function () {
            disconnect()
        }, 1000);
    });

    $('input#directoryCreate').change(function () {
        if ($('input[id=directoryCreate]').is(':checked')) {
            var alertMessage = new AlertMessage("|Watch Directory Create:true", Command.WATCH_DIR_CREATE);
            sendMessage(alertMessage);
        } else {
            var alertMessage = new AlertMessage("|Watch Directory Create:false", Command.WATCH_DIR_CREATE);
            sendMessage(alertMessage);
        }
    });

    $('input#directoryChange').change(function () {
        if ($('input[id=directoryChange]').is(':checked')) {
            var alertMessage = new AlertMessage("|Watch Directory Change:true", Command.WATCH_DIR_CHANGE);
            sendMessage(alertMessage);
        } else {
            var alertMessage = new AlertMessage("|Watch Directory Change:false", Command.WATCH_DIR_CHANGE);
            sendMessage(alertMessage);
        }
    });

    $('input#directoryDelete').change(function () {
        if ($('input[id=directoryDelete]').is(':checked')) {
            var alertMessage = new AlertMessage("|Watch Directory Delete:true", Command.WATCH_DIR_DELETE);
            sendMessage(alertMessage);
        } else {
            var alertMessage = new AlertMessage("|Watch Directory Delete:false", Command.WATCH_DIR_DELETE);
            sendMessage(alertMessage);
        }
    });

    $('input#indexCreate').change(function () {
        if ($('input[id=indexCreate]').is(':checked')) {
            var alertMessage = new AlertMessage("|Watch File Create:true", Command.WATCH_FILE_CREATE);
            sendMessage(alertMessage);
        } else {
            var alertMessage = new AlertMessage("|Watch File Create:false", Command.WATCH_FILE_CREATE);
            sendMessage(alertMessage);
        }
    });

    $('input#indexChange').change(function () {
        if ($('input[id=indexChange]').is(':checked')) {
            var alertMessage = new AlertMessage("|Watch File Change:true", Command.WATCH_FILE_CHANGE);
            sendMessage(alertMessage);
        } else {
            var alertMessage = new AlertMessage("|Watch File Change:false", Command.WATCH_FILE_CHANGE);
            sendMessage(alertMessage);
        }
    });

    $('input#indexDelete').change(function () {
        if ($('input[id=indexDelete]').is(':checked')) {
            var alertMessage = new AlertMessage("|Watch File Delete:true", Command.WATCH_FILE_DELETE);
            sendMessage(alertMessage);
        } else {
            var alertMessage = new AlertMessage("|Watch File Delete:false", Command.WATCH_FILE_DELETE);
            sendMessage(alertMessage);
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

    $("#btn-plot").click(function () {

        var jsonObject = makeJsonFromTable("resultsTable");

        if (jsonObject !== null && jsonObject !== undefined) {
            if (jsonObject.count > 0) {
                var key;
                var xSecondsData = [];
                var yTotalSize = [];
                var dataSets = [];
                var indexTypes = [];
                var j = 0;

                // Collect the data and congregate per index type
                for (var i = 0; i < jsonObject.count; i++) {
                    //xDateTime[i] = jsonObject.value[i].DateTime;
                    xSecondsData[i] = jsonObject.value[i].Seconds;
                    //DataTypeHash[indexType].Message[i] = jsonObject.value[i].Message;
                    yTotalSize[i] = jsonObject.value[i].TotalSize;

                    key = jsonObject.value[i].IndexType;
                    j = DataTypeHash[key].index;
                    DataTypeHash[key].ySizeData[j] = jsonObject.value[i].Size;
                    DataTypeHash[key].yDeltaData[j] = jsonObject.value[i].Delta;
                    DataTypeHash[key].yTypeTotalData[j] = jsonObject.value[i].TypeTotal;
                    DataTypeHash[key].index++;

                    if (!indexTypes.includes(key)) {
                        indexTypes.push(key);
                    }
                }

                // Create the plot DataSets
                var label;
                var type;
                for (j = 0; j < indexTypes.length; j++) {
                    type = indexTypes[j];
                    if (DataTypeHash[type].index !== null && DataTypeHash[type].index !== undefined) {
                        if (DataTypeHash[type].index > 0) {
                            label = "Size:" + type;
                            dataSets.push(new DataSet(DataTypeHash[type].ySizeData, label, getRandomColor(), false));
                            label = "DeltaSize:" + type;
                            dataSets.push(new DataSet(DataTypeHash[type].yDeltaData, label, getRandomColor(), false));
                            label = "TypeTotal:" + type;
                            dataSets.push(new DataSet(DataTypeHash[type].yTypeTotalData, label, getRandomColor(), false));
                        }
                    }
                }

                dataSets[dataSets.length] = new DataSet(yTotalSize, "Total Size", getRandomColor(), false);

                plot(xSecondsData, dataSets);
            }
        }
    });
});
