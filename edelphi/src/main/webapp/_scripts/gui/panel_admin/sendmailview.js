SendMailViewController = Class.create({
  initialize : function() {
    this._selectAllRecipients = $('selectAllRecipients');
    this._selectAllRecipientsClickListener = this._onSelectAllRecipientsClick.bindAsEventListener(this);
    Event.observe(this._selectAllRecipients, "click", this._selectAllRecipientsClickListener);

    this._submitButton = $('sendEmailSubmitButton');
    this._submitButtonClickListener = this._onSubmitClick.bindAsEventListener(this);
    Event.observe(this._submitButton, "click", this._submitButtonClickListener);
  },
  deinitialize : function() {
    Event.stopObserving(this._selectAllRecipients, "click", this._selectAllRecipientsClickListener);
    Event.stopObserving(this._submitButton, "click", this._submitButtonClickListener);
  },
  _onSelectAllRecipientsClick: function (event) {
    Event.stop(event);
    $('panelAdminSendEmailRecipientBlockContent').select('input[type=checkbox]').each(function(e) {
      e.checked = !e.checked;
    });
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
