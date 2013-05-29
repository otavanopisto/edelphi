MessageCenterMessageViewController = Class.create({
  initialize : function() {
    this._submitButton = $('messageCenterSendMessage');
    this._submitButtonClickListener = this._onSubmitClick.bindAsEventListener(this);
    Event.observe(this._submitButton, "click", this._submitButtonClickListener);
  },
  deinitialize : function() {
    Event.stopObserving(this._submitButton, "click", this._submitButtonClickListener);
  },
  _onSubmitClick: function (event) {
    Event.stop(event);
    
    var button = Event.element(event);
    var form = button.form;
    
    startLoadingOperation("panel.admin.messageCenter.sendingMessage");
    
    JSONUtils.sendForm(form, {
      onSuccess: function (jsonResponse) {
        JSONUtils.showMessages(jsonResponse);
        
        $('messageCenterMessageSubject').value = "";
        $('messageCenterMessageContent').value = "";
        endLoadingOperation();
      },
      onError: function (jsonResponse) {
        JSONUtils.showMessages(jsonResponse);
        endLoadingOperation();
      },
    });
  }
});

document.observe("dom:loaded", function(event) {
  new MessageCenterMessageViewController();
});
