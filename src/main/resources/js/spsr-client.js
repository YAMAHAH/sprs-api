"use strict";

function responseHandler(res) {
    var result = { status: 0, invoiceNumber: "", message: "" };
    var resObj = JSON.parse(res);
    var errorMessageEN = tryGetValue(function () { return resObj.root.error._ErrorMessageEN; }) || '';
    var errorMessageRU = new SPSRClient().tryGetValue(function () { return resObj.root.error._ErrorMessageRU; }) || 'work';
    resObj.errorMessage = errorMessageRU;
    var errorMessage = tryGetValue(function () { return resObj.root.error._ErrorMessage; }) || '';
    if (!errorMessage) {
        errorMessage = resObj.Error ? resObj.Error : '';
    }
    var resultError = errorMessageEN || errorMessageRU || errorMessage || 'Unknown Error';
    //throw new Error(resultError);
    return JSON.stringify(resObj);
}

function tryGetValue(selector, callBack) {
    try {
        var result = selector();
        if (result) {
            if (callBack)
                callBack(result);
            return result;
        }
        return null;
    }
    catch (error) {
        return null;
    }
}
var SPSRClient = (function () {
    function SPSRClient() {
    }
    SPSRClient.prototype.tryGetValue = function (selector, callBack) {
        try {
            var result = selector();
            if (result) {
                if (callBack)
                    callBack(result);
                return result;
            }
            return null;
        }
        catch (error) {
            return null;
        }
    };
    SPSRClient.prototype.test = function () {
        return "test";
    };
    return SPSRClient;
}());
var spsrClient = new SPSRClient();