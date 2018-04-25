//Class abstract gateway
function Gateway() {

}

Gateway.prototype.sendRequest = function (message, servletName, okHandler, errorHandler, params) {
    var xmlhttp = new XMLHttpRequest();
    var url = app.ni3Model.getServerUrl() + "/servlet/" + servletName;
    xmlhttp.open("POST", url, true);
    if (xmlhttp.overrideMimeType) // other browsers
        xmlhttp.overrideMimeType('text/plain; charset=x-user-defined');
    else { // IE
        xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xmlhttp.setRequestHeader("Custom-IE", "true");
    }
    xmlhttp.withCredentials = true;
    xmlhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            var payload = Gateway.getPayloadFromResponse(this);
            if (payload != undefined && okHandler != null) {
                okHandler(payload, params);
            }
        }
    };
    xmlhttp.onerror = function () {
        showDefaultCursor();
        if (errorHandler != null)
            errorHandler();
        else
            alert("Error sending request to the url: " + url);
    };
    if (xmlhttp.overrideMimeType) { // other browsers
        var arr = [];
        message.SerializeToStream(new PROTO.ByteArrayStream(arr));

        var buffer = new Uint8Array(arr).buffer;
        xmlhttp.send(buffer);
    } else { // IE
    	var stream = new PROTO.Base64Stream();
        message.SerializeToStream(stream);
        xmlhttp.send("byteStr=" + encodeURIComponent(stream.getString()));
    }
};

Gateway.getPayloadFromResponse = function (xmlhttp) {
    var respStream;
    if (xmlhttp.overrideMimeType) { // other browsers
        var resp = xmlhttp.response || xmlhttp.responseText;
        var bytes = [];
        for (var i = 0; i < resp.length; i++) {
            bytes[i] = resp.charCodeAt(i) & 0xff;
        }
        respStream = new PROTO.ByteArrayStream(bytes);
    } else { // IE
        respStream = new PROTO.Base64Stream(xmlhttp.responseText);
    }

    var envelope = new response.Envelope();
    envelope.ParseFromStream(respStream);
    var stream;
    if (envelope.status == 1) {
        stream = new PROTO.ByteArrayStream(envelope.payload);
    }
    return stream;
};

var gateway = new Gateway();