function LoginGateway(){

}

LoginGateway.prototype = new Gateway();
LoginGateway.prototype.constructor = LoginGateway;

LoginGateway.prototype.loginWithUsernamePassword = function(username, password, serverUrl) {
    var msg = new request.Login();
    msg.action = request.Login.Action.LOGIN_BY_PASSWORD;
    msg.userName = username;
    msg.password = password;
    msg.sync = false;

    showWaitCursor();
    app.ni3Model.setServerUrl(serverUrl);

    this.sendRequest(msg, "LoginServlet", this.loginHandler, this.errorHandler);
};

LoginGateway.prototype.errorHandler = function(){
    app.validateLogin(null);
};

LoginGateway.prototype.loginHandler = function(payload){
	var login = new response.Login();
	login.ParseFromStream(payload);
	app.validateLogin(login);
	showDefaultCursor();
};

