BlockController = Class.create({
  initialize : function () {
  },
  setup: function (blockElement) {
    this._blockElement = blockElement;
  },
  deinitialize : function() {
  },
  getBlockElement: function () {
    return this._blockElement;
  },
  setBlockElement: function (blockElement) {
    this._blockElement = blockElement;
  },
  getQueryParams: function (url) {
    if (url) {
      return this._parseQueryParams(url);
    } else {
      var query = window.location.search;
      if (query.length > 0)
        return this._parseQueryParams(query.substring(1));
      else
        return new Hash();
    }
  },
  getQueryParam: function (name) {
    return this.getQueryParams().get(name);
  },
  getHashParams: function (url) {
    if (url) {
      var hashIndex = url.indexOf("#");
      if (hashIndex > 0) {
        var hashPart = url.substring(hashIndex);
        return this._parseHash(hashPart);
      }
    } else {
      return this._parseHash(window.location.hash);
    }
  },
  getHashParam: function (name) {
    return this.getHashParams().get(name);
  },
  _parseQueryParams: function (query) {
    var result = new Hash();
    
    if (query) {
      var split = query.split('&');
      for (var i = 0, l = split.length; i < l; i++) {
        var pair = split[i].split('=');
        result.set(pair[0], pair[1]);
      }
    } 
    
    return result;
  },
  _parseHash: function (hash) {
    var result = new Hash();
    if (hash.length > 1) {
      var params = hash.substring(1).split(";");
      for (var i = 0, l = params.length; i < l; i++) {
        var param = params[i].split(':');
        if (param.length == 2)
          result.set(param[0], param[1]);
      }
    }
    
    return result;
  }
});

function addBlockController(blockController) {
  if (!window._blockControllers)
    window._blockControllers = new Array();
  window._blockControllers.push(blockController);
}

document.observe("dom:loaded", function(event) {
  if (window._blockControllers) {
    for (var i = 0, l = window._blockControllers.length; i < l; i++) {
      try {
        window._blockControllers[i].setup();
      } catch (e) {
        alert(e);
        throw e;
      }
    }
  }  
});

Event.observe(window, "unload", function (event) {
  if (window._blockControllers) {
    for (var i = 0, l = window._blockControllers.length; i < l; i++) {
      window._blockControllers[i].deinitialize();
      delete window._blockControllers[i];
    }
  }  
});