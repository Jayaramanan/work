function showWaitCursor() {
    document.body.style.cursor = 'wait';
}

function showDefaultCursor() {
    document.body.style.cursor = 'default';
}

function Ni3WebNavigator(){
    this.ni3Container = new Ni3Container();
    this.ni3Model = new Ni3Model();
}

Ni3WebNavigator.prototype.constructor = Ni3WebNavigator;

Ni3WebNavigator.prototype.doLogin = function(username, password, serverUrl) {
    password = calcMD5(password);
    new LoginGateway().loginWithUsernamePassword(username, password, serverUrl);
};

Ni3WebNavigator.prototype.validateLogin = function(loginResponse) {
    if (loginResponse == null) {
        alert("Invalid server url");
    } else if (loginResponse.status != 1) {
        alert("Invalid username or password");
    } else {
        document.loginForm.style.display = 'none';
        this.init();
    }
};

Ni3WebNavigator.prototype.init = function(){
    this.ni3Container.init();
};

var app = new Ni3WebNavigator();
