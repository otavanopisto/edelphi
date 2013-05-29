MaterialImportGDocsBlockController = Class.create(BlockController, {
  initialize: function ($super) {
    $super();

    this._saveButtonClickListener = this._onSaveButtonClick.bindAsEventListener(this);
  },
  setup: function ($super) {
    $super($('panelAdminMaterialImportGDocsBlockContent'));
    
    this._formElement = this.getBlockElement().down('form[name="importGDocs"]');
    this._saveElement = this.getBlockElement().down("input[name='save']");
    Event.observe(this._saveElement, "click", this._saveButtonClickListener);
  },
  deinitialize: function ($super) {
    Event.stopObserving(this._saveElement, "click", this._saveButtonClickListener);
    $super();
  },
  _onSaveButtonClick: function (event) {
    Event.stop(event);
    
    var panelId = this.getBlockElement().down('input[name="panelId"]').value;
    
    startLoadingOperation("panelAdmin.block.importMaterialsGDocs.importingMaterials");
    JSONUtils.sendForm(this._formElement, {
      onComplete: function (transport) {
        endLoadingOperation();
      },
      onSuccess: function () {
        window.location.href = CONTEXTPATH + '/panel/admin/managematerials.page?panelId=' + panelId;
      }
    });
  }
});

addBlockController(new MaterialImportGDocsBlockController());