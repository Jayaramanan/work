function FavoritesDialog(){

}

FavoritesDialog.prototype.constructor = FavoritesDialog;

FavoritesDialog.prototype.createDialog = function(){
    this.favoritesDialog = d3.select("body")
        .append("div").attr("id", "favoritesDialog")
        .attr("title", "Favorites");
    this.loadingLabel = this.favoritesDialog.append("span").style("display", "none").text("Loading...");
    this.favoritesDialog.append("br");
    this.favoritesDialog.append("span").text("My favorites");
    this.favoritesDialog.append("ul").attr("id", "myFavorites");
    this.favoritesDialog.append("span").text("Group favorites");
    this.favoritesDialog.append("ul").attr("id", "groupFavorites").attr("title", "Group favorites");
};

FavoritesDialog.prototype.show = function(){
    if(!this.favoritesDialog)
        this.createDialog();
    this.favoritesDialog = $("#favoritesDialog");
    this.favoritesDialog.dialog({
        autoOpen:false,
        height:300,
        width:300,
        modal:true,
        buttons:{
            Cancel:function () {
                $(this).dialog("close");
            }
        }
    });
    this.favoritesDialog.dialog("open");
    this.loadFavorites();
};

FavoritesDialog.prototype.loadFavorites = function(){
    var self = this;
    this.loadingLabel.style("display", null);

//    var req = new request.FavoritesFolderManagement();
//    req.action = request.FavoritesFolderManagement.Action.GET_ALL_FOLDERS;
//    req.schemaId = app.ni3Model.getSchemaId();
//    gateway.sendRequest(req, "FavoritesFolderManagementServlet", function(payload){
//        self.folders = new response.Folders();
//        self.folders.ParseFromStream(payload);
//        self.folders = self.folders.folders;

        var req = new request.FavoriteManagement();
        req.action = request.FavoriteManagement.Action.GET_ALL_FOR_SCHEMA;
        req.schemaId = app.ni3Model.getSchemaId();
        gateway.sendRequest(req, "FavoritesManagementServlet", function(payload){
            self.favorites = new response.Favorites();
            self.favorites.ParseFromStream(payload);
            self.favorites = self.favorites.favorites;
            self.makeFavoritesTree();
        }, null);
//    }, null);
};

FavoritesDialog.prototype.loadFavorite = function(favorite){
    var self = app.ni3Container.favoritesDialog;
    self.favoritesDialog.dialog("close");
    app.ni3Container.loadFavorite(favorite);
};

FavoritesDialog.prototype.makeFavoritesTree = function(){
    d3.select(this.favoritesDialog[0]).selectAll("ul").selectAll("li").remove();
    var groupFavorites = [];
    var myFavorites = [];
    for(var i = 0; i < this.favorites.length; i++){
        var favorite = this.favorites[i];
        if(favorite.groupFavorite)
            groupFavorites.push(favorite);
        else
            myFavorites.push(favorite);
    }
    var links = d3.select(this.favoritesDialog[0]).select("ul#myFavorites").selectAll("li")
        .data(myFavorites).enter()
        .append("li");

    links.append("img").attr("src", function(favorite){
        var url = imageUrl + "/";
        switch(favorite.mode){
            case 1:
                return url + "f.png";
            case 2:
                return url + "q.png";
            case 3:
                return url + "t.png";
        }
    });
    links.append("span").attr("class", "favoriteTitle").append("a").attr("href", "#")
        .on("click", this.loadFavorite)
        .text(function(favorite){return favorite.name;});

    links = d3.select(this.favoritesDialog[0]).select("ul#groupFavorites").selectAll("li")
        .data(groupFavorites).enter()
        .append("li");
    links.append("img").attr("src", function(favorite){
        var url = imageUrl + "/";
        switch(favorite.mode){
            case 1:
                return url + "f.png";
            case 2:
                return url + "q.png";
            case 3:
                return url + "t.png";
        }
    });
    links.append("span").attr("class", "favoriteTitle").append("a").attr("href", "#")
        .on("click", this.loadFavorite)
        .text(function(favorite){return favorite.name;});
//    var topItems = d3.select(this.favoritesDialog[0]).select("ul").selectAll("li")
//        .data([
//        {id: "myFolders", name: "My folders"},
//        {id: "groupFolders", name: "Group Folders"}]);
//
//    topItems.enter()
//        .append("li")
//        .attr("id", function(d){return d.id;})
//        .append("label")
//        .text(function(d){return d.name;});
//
//    for(var i = 0; i < this.folders.length; i++) {
//        var folder = this.folders[i];
//        var selector = this.getParentForItem(folder.parentFolderId, folder.groupFolder);
//        selector.append("li").attr("id", "favFolder_" + folder.id).append("label").text(folder.folderName);
//    }
//
//    for(i = 0; i < this.favorites.length; i++){
//        var favorite = this.favorites[i];
//        selector = this.getParentForItem(favorite.folderId, favorite.groupFavorite);
//        selector.append("li").attr("id", "favorite_" + favorite.id).property("__data__", favorite).append("label").text(favorite.name);
//    }
//
//    this.favoritesDialog.checkboxTree({
//        leafImage: function(li){
//            var htmlElement = li[0];
//            var id = htmlElement.id;
//            var type = id.substring(0, 8);
//            var html = '<img src="' + imageUrl;
//            switch(type){
//                case "favorite":
//                    switch(htmlElement.__data__.mode){
//                        case 1:
//                            html += '/f.png';
//                            break;
//                        case 2:
//                            html += '/q.png';
//                            break;
//                        case 3:
//                            html += '/t.png';
//                            break;
//                    }
//                    break;
//                case "myFolder":
//                case "favFolde":
//                case "groupFol":
//                    html += '/folder.png';
//                    break;
//            }
//            html +=  '"/>';
//            li.children("span").html(html);
//        }
//    });
    this.loadingLabel.style("display", "none");
};

//FavoritesDialog.prototype.getParentForItem = function (parentFolderId, isGroup) {
//    var selector;
//    if (parentFolderId)
//        selector = d3.select("#favFolder_" + parentFolderId);
//    else if (isGroup)
//        selector = d3.select("#groupFolders");
//    else
//        selector = d3.select("#myFolders");
//    var subList = selector.select("ul");
//    if(subList.empty())
//        subList = selector.append("ul");
//    return subList;
//};