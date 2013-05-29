SendMailViewController = Class.create({
  initialize : function() {
    this._submitButton = $('sendEmailSubmitButton');
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
    var _this = this;
    
    startLoadingOperation("panel.admin.sendEmail.sendingMail");
    
    JSONUtils.sendForm(form, {
      onSuccess: function (jsonResponse) {
        JSONUtils.showMessages(jsonResponse);
        
        $('sendEmailSubject').value = "";
        $('sendEmailContent').value = "";
        endLoadingOperation();
      },
      onFailure: function (jsonResponse) {
        JSONUtils.showMessages(jsonResponse);
        endLoadingOperation();
      },
    });
  }
});

document.observe("dom:loaded", function(event) {
  new SendMailViewController();
});
