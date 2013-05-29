LoginBlockController = Class.create(BlockController, {
  initialize: function ($super) {
    $super();
    this._loginButtonClickListener = this._onLoginButtonClick.bindAsEventListener(this);
    this._passwordResetClickListener = this._onPasswordResetClick.bindAsEventListener(this);
  },
  setup: function ($super) {
    $super($('loginBlockContent'));
    this._formElement = $('loginForm');
    if (this._formElement) {
      this._loginButton = this._formElement.down("input[name='login']");
      this._resetLink = $('passwordResetLink');
      Event.observe(this._loginButton, "click", this._loginButtonClickListener);
      if (this._resetLink) {
        Event.observe(this._resetLink, "click", this._passwordResetClickListener);
      }
    }
  },
  deinitialize: function ($super) {
    if (this._formElement) {
      Event.stopObserving(this._loginButton, "click", this._loginButtonClickListener);
      if (this._resetLink) {
        Event.stopObserving(this._resetLink, "click", this._passwordResetClickListener);
      }
    }
  },
  _onPasswordResetClick: function (event) {
    Event.stop(event);
    var parameters = {
      email: this._formElement.username.value,
    };
    startLoadingOperation("resetPassword.block.processingRequest");
    JSONUtils.request(CONTEXTPATH + '/resetpassword.json', {
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
  },
  _onLoginButtonClick: function (event) {
    Event.stop(event);
    var parameters = {
      authSource: this._formElement.authSource.value,
      username: this._formElement.username.value,
      password: this._formElement.password.value
    };
    JSONUtils.request(CONTEXTPATH + '/dologin.json', {
      parameters: parameters,
      onError: function (jsonResponse) {
        JSONUtils.showMessages(jsonResponse);
      }
    });
  }
});

addBlockController(new LoginBlockController());