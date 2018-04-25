function Utility() {
}

Utility.contains = function (arr, value) {
    for (var i = 0; i < arr.length; i++) {
        if (arr[i] == value) {
            return true;
        }
    }
    return false;
};

//TODO remove useless function - array it self has indexOf function
Utility.indexOf = function (arr, value) {
    for (var i = 0; i < arr.length; i++) {
        if (arr[i] == value) {
            return i;
        }
    }
    return -1;
};

Utility.indexOfId = function (arr, id) {
    for (var i = 0; i < arr.length; i++) {
        if (arr[i].id == id) {
            return i;
        }
    }
    return -1;
};

Utility.containsId = function (arr, id) {
    for (var i = 0; i < arr.length; i++) {
        if (arr[i].id == id) {
            return true;
        }
    }
    return false;
};

Utility.getById = function (arr, id) {
    for (var i = 0; i < arr.length; i++) {
        if (arr[i].id === id) {
            return arr[i];
        }
    }
    return null;
};

Utility.filterById = function(objectArray, idArray){
    var set = {};
    var result = [];
    for(var i = 0; i < objectArray.length; i++){
        if(idArray.indexOf("" + objectArray[i].id) != -1 && !set[objectArray[i].id]){
            result.push(objectArray[i]);
            set[objectArray[i].id] = 1;
        }
    }
    return result;
};

Utility.stringAsIdentifier = function (str) {
    return str.replace(" ", "_");
};