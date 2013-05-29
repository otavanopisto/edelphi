JSONUtils = {
  request : function(url, options) {
    var opts = options ? options : {};
    var ajaxOptions = {
      method : opts.method ? opts.method : 'post',
//      parameters : params,
      onSuccess : function(transport) {
        if (Object.isFunction(opts.onComplete)) {
          opts.onComplete(transport);
        }

        try {
          var jsonResponse = transport.responseText.evalJSON();
          if (jsonResponse.statusCode == 0) {
            if (Object.isFunction(opts.onSuccess)) {
              opts.onSuccess(jsonResponse);
            }
            else if (jsonResponse.redirectURL) {
              redirectTo(jsonResponse.redirectURL);
            } else {
              JSONUtils.showMessages(jsonResponse);
            }
          }
          else if (Object.isFunction(opts.onFailure)) {
            opts.onFailure(jsonResponse);
          }
          else {
            JSONUtils.showMessages(jsonResponse);
          }
        } catch (e) {
          var jsonResponse = {
            statusCode : -1,
            messages : [{
              severity : 'CRITICAL',
              message : e
            }]
          };
          if (Object.isFunction(opts.onFailure)) {
            opts.onFailure(jsonResponse);
          } else {
            JSONUtils.showMessages(jsonResponse);
          }
        }
      },
      on403: function (transport) {
        if (Object.isFunction(opts.onComplete)) {
          opts.onComplete(transport);
        }
        
        var jsonResponse = transport.responseText.evalJSON();
        
        if (Object.isFunction(opts.onFailure)) {
          opts.onFailure(jsonResponse);
        }
        else {
          JSONUtils.showMessages(jsonResponse);
        }
      },
      on404: function (transport) {
        if (Object.isFunction(opts.onComplete)) {
          opts.onComplete(transport);
        }
        
        var jsonResponse = transport.responseText.evalJSON();
        
        if (Object.isFunction(opts.onFailure)) {
          opts.onFailure(jsonResponse);
        }
        else {
          JSONUtils.showMessages(jsonResponse);
        }
      },
      onFailure : function(transport) {
        if (Object.isFunction(opts.onComplete)) {
          opts.onComplete(transport);
        }
        var jsonResponse = {
          statusCode : -1,
          messages : [{
            severity : 'CRITICAL',
            message : transport.responseText
          }]
        };
        if (Object.isFunction(opts.onFailure)) {
          opts.onFailure(jsonResponse);
        } else {
          JSONUtils.showMessages(jsonResponse);
        }
      },
      asynchronous : opts.asynchronous === false ? false : true
    };
    
    if (opts.parameters) {
      var params = $H(opts.parameters);
      
      var paramsKeys = params.keys();
      for (var i = 0, l = paramsKeys.length; i < l; i++) {
        var key = paramsKeys[i];
        var value = params.get(key);
        if ((typeof value) == 'object')
          params.set(key, value.toString());
      }
      
      if ((typeof JSDATA) != 'undefined') {
        if (JSDATA["securityContextId"])
          params.set("securityContextId", JSDATA["securityContextId"]);
        if (JSDATA["securityContextType"])
          params.set("securityContextType", JSDATA["securityContextType"]);
      }
      
      ajaxOptions.parameters = params;
    } else {
      if (opts.postBody) {
        var body = opts.postBody;
        
        if ((typeof JSDATA) != 'undefined') {
          if (JSDATA["securityContextId"]) {
            body = (body ? body + "&" : "") + "securityContextId=" + JSDATA["securityContextId"];
          }
          
          if (JSDATA["securityContextType"]) {
            body = (body ? body + "&" : "") + "securityContextType=" + JSDATA["securityContextType"];
          }
        }
        
        ajaxOptions.postBody = body;
      }
    }
    
    new Ajax.Request(url, ajaxOptions);
  },
  showMessages: function (jsonResponse) {
    var eventQueue = getGlobalEventQueue();
    
    if (jsonResponse.messages) {
      for (var i = 0, l = jsonResponse.messages.length; i < l; i++) {
        var severity = jsonResponse.messages[i].severity;
        var message = jsonResponse.messages[i].message;
        
        switch (severity) {
          case 'OK':
            eventQueue.addItem(new EventQueueItem(message, {
              className: "eventQueueSuccessItem",
              timeout: 1000 * 2
            }));
          break;
          case 'INFORMATION':
            eventQueue.addItem(new EventQueueItem(message, {
              className: "eventQueueInfoItem",
              timeout: 1000 * 30
            }));
          break;
          case 'WARNING':
            eventQueue.addItem(new EventQueueItem(message, {
              className: "eventQueueWarningItem",
              timeout: -1
            }));          
          break;
          case 'ERROR':
            eventQueue.addItem(new EventQueueItem(message, {
              className: "eventQueueErrorItem",
              timeout: -1
            }));
          break;
          case 'CRITICAL':
            eventQueue.addItem(new EventQueueItem(message, {
              className: "eventQueueCriticalItem",
              timeout: -1
            }));
          break;
        }
      }
    }
  },
  sendForm: function (formElement, options) {
    var postBody = Form.serialize(formElement);
    var url = formElement.getAttribute("action");
    
    JSONUtils.request(url, {
      postBody : postBody,
      onComplete: function (jsonResponse) {
        if (Object.isFunction(options.onComplete)) {
          options.onComplete(jsonResponse);
        }
      },
      onSuccess : function(jsonResponse) {
       if (Object.isFunction(options.onSuccess)) {
         options.onSuccess(jsonResponse);
       }
       else if (jsonResponse.redirectURL) {
         redirectTo(jsonResponse.redirectURL);
       } else {
         JSONUtils.showMessages(jsonResponse);
       }
      },
      onFailure : function(jsonResponse) {
        if (Object.isFunction(options.onFailure)) {
          options.onFailure(jsonResponse);
        }
        else {
          JSONUtils.showMessages(jsonResponse);
        }
      }
    });
  }
};