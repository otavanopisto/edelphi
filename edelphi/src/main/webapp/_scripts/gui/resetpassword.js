ResetPasswordBlockController = Class.create(BlockController, {
  initialize: function ($super) {
    $super();
    this._saveButtonClickListener = this._onSaveButtonClick.bindAsEventListener(this);
  },
  setup: function ($super) {
    $super($('resetPasswordBlockContent'));
    this._formElement = $('resetPasswordForm');
    this._saveButton = this._formElement.down("input[name='changePasswordButton']");
    Event.observe(this._saveButton, "click", this._saveButtonClickListener);
  },
  deinitialize: function ($super) {
    Event.stopObserving(this._saveButton, "click", this._saveButtonClickListener);
  },
  _onSaveButtonClick: function (event) {
    Event.stop(event);
    var parameters = {
      email: this._formElement.email.value,
      hash: this._formElement.hash.value,
      password: hex_md5(this._formElement.password1.value)
    };
    startLoadingOperation("resetPassword.block.savingPassword");
    JSONUtils.request(CONTEXTPATH + '/changepassword.json', {
      parameters: parameters,
      onComplete: function (transport) {
        endLoadingOperation();
      },
      onSuccess: function (jsonResponse) {
        JSONUtils.showMessages(jsonResponse);
      },
      onError: function (jsonResponse) {
        JSONUtils.showMessages(jsonResponse);
      }
    });
  }
});

addBlockController(new ResetPasswordBlockController());