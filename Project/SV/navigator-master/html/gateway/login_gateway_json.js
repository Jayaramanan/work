function LoginGateway() {

}

LoginGateway.prototype.loginHandler = function (jsonResult, serverUrl) {
    app.validateLogin(jsonResult, serverUrl);
    showDefaultCursor();
};

LoginGateway.prototype.loginWithUsernamePassword = function(username, password, serverUrl) {
    var msg = {action:"LOGIN_BY_PASSWORD", userName:username, password:password, sync:false};
    showWaitCursor();

    app.ni3Model.setServerUrl(serverUrl);
    this.sendRequest(msg, "LoginServletJson", this.loginHandler, null, serverUrl);
};

LoginGateway.prototype.constructor = LoginGateway;

LoginGateway.prototype.sendRequest = function (message, servletName, okHandler, errorHandler, params) {
    var jsonStr = JSON.stringify(message);

    var xmlhttp = new XMLHttpRequest();
    var url = app.ni3Model.getServerUrl() + "/servlet/" + servletName;
    xmlhttp.open("POST", url, true);
    xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    xmlhttp.withCredentials = true;
    xmlhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            var result = xmlhttp.responseText;
            alert(result);
            var jsonResult = JSON.parse(result);
            if (okHandler != null) {
                okHandler(jsonResult, params);
            }
        }
    };
    xmlhttp.onerror = function () {
        alert("Error sending request to the url: " + url);
        showDefaultCursor();
        if (errorHandler != null)
            errorHandler();
    };

    xmlhttp.send(jsonStr);
};