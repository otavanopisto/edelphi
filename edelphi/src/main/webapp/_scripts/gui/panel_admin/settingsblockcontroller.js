SettingsBlockController = Class.create(BlockController, {
  initialize: function ($super) {
    $super();
    this._saveButtonClickListener = this._onSaveButtonClick.bindAsEventListener(this);
  },
  setup: function ($super) {
    $super($('panelAdminSettingsBlockContent'));
    this._formElement = $('panelAdminSettingsForm');
    this._saveButton = this._formElement.down("input[name='save']");
    Event.observe(this._saveButton, "click", this._saveButtonClickListener);
  },
  deinitialize: function ($super) {
    Event.stopObserving(this._saveButton, "click", this._saveButtonClickListener);
  },
  _onSaveButtonClick: function (event) {
    Event.stop(event);
    JSONUtils.sendForm(this._formElement, {
      onSuccess: function (jsonResponse) {
        JSONUtils.showMessages(jsonResponse);
      },
      onFailure: function (jsonResponse) {
        JSONUtils.showMessages(jsonResponse);
      }
    });
  }
});

addBlockController(new SettingsBlockController());