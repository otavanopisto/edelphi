RegisterBlockController = Class.create(BlockController, {
  initialize: function ($super) {
    $super();
    this._registerButtonClickListener = this._onRegisterButtonClick.bindAsEventListener(this);
  },
  setup: function ($super) {
    $super($('registerBlockContent'));
    this._formElement = $('registerForm');
    if (this._formElement) {
      this._registerButton = this._formElement.down("input[name='register']");
      Event.observe(this._registerButton, "click", this._registerButtonClickListener);
    }
  },
  deinitialize: function ($super) {
    if (this._formElement) {
      Event.stopObserving(this._registerButton, "click", this._registerButtonClickListener);
    }
  },
  _onRegisterButtonClick: function (event) {
    Event.stop(event);
    var parameters = {
      skipEmailVerification: this._formElement.skipEmailVerification ? this._formElement.skipEmailVerification.value : false,
      firstName: this._formElement.firstName.value, 
      lastName: this._formElement.lastName.value, 
      email: this._formElement.email.value,
      password: hex_md5(this._formElement.password1.value)
    };
    JSONUtils.request(CONTEXTPATH + '/register.json', {
      parameters: parameters,
      onError: function (jsonResponse) {
        JSONUtils.showMessages(jsonResponse);
      }
    });
  }
});

addBlockController(new RegisterBlockController());