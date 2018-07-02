
var testWalkMe = function(){
    console.log("WalkMe enabled in test mode")
    init('https://cdn.walkme.com/users/9d6c202bd5f14ccca763dc4d94ce3b70/test/walkme_9d6c202bd5f14ccca763dc4d94ce3b70_https.js');
};

var prodWalkMe = function() {
    console.log("WalkMe enabled in production mode")
    init('https://cdn.walkme.com/users/9d6c202bd5f14ccca763dc4d94ce3b70/walkme_9d6c202bd5f14ccca763dc4d94ce3b70_https.js');
};

var vetsourceUWalkme = function() {
    console.log("WalkMe enabled in VetsourceU mode")
    init('https://cdn.walkme.com/users/1e5ff2e96e9245179a72cdc6122ce954/test/walkme_1e5ff2e96e9245179a72cdc6122ce954_https.js')
}

var init = function(src){
    console.log("WalkMe init source: " + src);

    var walkme = document.createElement('script');
    walkme.type = 'text/javascript';
    walkme.async = true;
    walkme.src = src;
    var s = document.getElementsByTagName('script')[0];
    s.parentNode.insertBefore(walkme, s);
    window._walkmeConfig = {smartLoad: true};

};