
(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
        (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
    m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
})(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

var siteId=null
var hfpSiteId=null
var userId=null
var resellerName=null

var callback = function() {
    ga('create', 'UA-34246763-17', 'auto');
}

var setSiteId = function(id){
    ga('set','dimension1',id);
    ga('set', 'userId', id);
    siteId=id
}

var setHfpSiteId = function(id){
    ga('set','dimension2',id);
    hfpSiteId=id
}

var setResellerName = function(name){
    ga('set','dimension3',name);
	resellerName=name;
}

var executeGaPageview = function(value) {
    if (userId == null) {
        userId = siteId != null ? siteId : hfpSiteId != null ? hfpSiteId : null;
    }
    setGaSiteIds();
    ga('send', 'pageview', value);

}

var executeGaEvent = function(category,action,label) {
    if (userId == null) {
        userId = siteId != null ? siteId : hfpSiteId != null ? hfpSiteId : null;
    }

    setGaSiteIds();
    ga('send', 'event',category,action,label);

}

var setGaSiteIds = function(){
    ga('set', 'userId', userId);
    ga('set','dimension1',siteId);
    ga('set','dimension2',hfpSiteId);
    ga('set','dimension3',resellerName);
}

if (document.readyState === "complete" || (document.readyState !== "loading" && !document.documentElement.doScroll)) {
    callback();
} else {
    document.addEventListener("DOMContentLoaded", callback);
}