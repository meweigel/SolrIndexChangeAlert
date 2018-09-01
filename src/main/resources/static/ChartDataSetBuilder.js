
var plotsSize = false;
var plotDelta = false;
var plotTypeTotal = false;

function DataSet(label, data, showLine, fill, borderColor) {
    this.label = label;
    this.data = data;
    this.showLine = showLine;
    this.fill = fill;
    this.borderColor = borderColor;
}

function DataPoint(x, y) {
    this.x = x;
    this.y = y;
}

var IndexTypes = ["cfe", "cfs", "doc", "dvd", "dvm", "fdt", "fdx", "fnm", "lock", "nvd", "nvm",
    "pos", "si", "tim", "tip"];

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


function flushData() {
    for (var i = 0; i < IndexTypes.length; i++) {
        DataTypeHash[IndexTypes[i]].index = 0;
        DataTypeHash[IndexTypes[i]].yTypeTotalData = [];
        DataTypeHash[IndexTypes[i]].yDeltaData = [];
        DataTypeHash[IndexTypes[i]].ySizeData = [];
    }
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


function createDataSet(jsonObject) {

    flushData();

    if (jsonObject !== null && jsonObject !== undefined) {
        if (jsonObject.count > 0) {
            var key;
            var totalSize = [];
            var dataSets = [];
            var indexTypes = [];
            var j = 0;

            // Collect the data and agregate per index type
            for (var i = 0; i < jsonObject.count; i++) {
                //jsonObject.value[i].DateTime;
                //jsonObject.value[i].Message;

                totalSize[i] = new DataPoint(jsonObject.value[i].Seconds, jsonObject.value[i].TotalSize);

                key = jsonObject.value[i].IndexType;
                j = DataTypeHash[key].index;
                if (plotsSize) {
                    DataTypeHash[key].ySizeData[j] = new DataPoint(jsonObject.value[i].Seconds, jsonObject.value[i].Size);
                }
                if (plotDelta) {
                    DataTypeHash[key].yDeltaData[j] = new DataPoint(jsonObject.value[i].Seconds, jsonObject.value[i].Delta);
                }
                if (plotTypeTotal) {
                    DataTypeHash[key].yTypeTotalData[j] = new DataPoint(jsonObject.value[i].Seconds, jsonObject.value[i].TypeTotal);
                }
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

                if (DataTypeHash[type].index > 0) {
                    if (plotsSize) {
                        label = "Size:" + type;
                        dataSets.push(new DataSet(label, DataTypeHash[type].ySizeData, true, false, getRandomColor()));
                    }
                    if (plotDelta) {
                        label = "DeltaSize:" + type;
                        dataSets.push(new DataSet(label, DataTypeHash[type].yDeltaData, true, false, getRandomColor()));
                    }
                    if (plotTypeTotal) {
                        label = "TypeTotal:" + type;
                        dataSets.push(new DataSet(label, DataTypeHash[type].yTypeTotalData, true, false, getRandomColor()));
                    }
                }
            }

            dataSets.push(new DataSet("Total Size", totalSize, true, false, getRandomColor()));

            //Using Transferrable objects
            var ab = new ArrayBuffer(dataSets);
            self.postMessage(dataSets, [ab]);
        }
    }
}

self.onmessage = function (e) {
    var data = e.data;

    for (var i = 0; i < data.cmds.length; i++) {
        switch (data.cmds[i]) {
            case 'plotsSize':
                plotsSize = data.values[i];
                break;
            case 'plotDelta':
                plotDelta = data.values[i];
                break;
            case 'plotTypeTotal':
                plotTypeTotal = data.values[i];
                break;
            case 'createDataSet':
                createDataSet(data.values[i]);
                break;
        }
    }
}
