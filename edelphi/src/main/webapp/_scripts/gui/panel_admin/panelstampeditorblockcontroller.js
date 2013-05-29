PanelStampEditorBlockController = Class.create(BlockController, {
  initialize: function ($super) {
    $super();
    this._newStampListener = this._onNewStampClick.bindAsEventListener(this);
    this._editStampListener = this._onEditStampClick.bindAsEventListener(this);
    this._saveButtonClickListener = this._onSaveButtonClick.bindAsEventListener(this);
  },
  setup: function ($super) {
    $super($('panelAdminManagePanelStampsViewBlockContent'));
    this._listingBlock = $('panelAdminManagePanelStampsListingBlockContent');
    this._editorBlock = $('panelAdminManagePanelStampsViewBlockContent');
    this._saveButton = this._editorBlock.down('input[name="save"]');
    Event.observe(this._saveButton, "click", this._saveButtonClickListener);
    var _this = this;
    this._listingBlock.select('.managePanelStamps_panelStamp').each(function (stampElement) {
      Event.observe(stampElement, "click", _this._editStampListener);
    });
    this._newStampElement = this._listingBlock.up('.block').select('.blockContextMenuItem.CREATE')[0].down('a');
    Event.observe(this._newStampElement, "click", this._newStampListener); 
  },
  deinitialize: function ($super) {
    var _this = this;
    Event.stopObserving(this._newStampElement, "click", this._newStampListener);
    this._listingBlock.select('.managePanelStamps_panelStamp').each(function (stampElement) {
      Event.stopObserving(stampElement, "click", _this._editStampListener);
    });
    Event.stopObserving(this._saveButton, "click", this._saveButtonClickListener);
    $super();
  },
  _addStamp: function(id,name,description,stampTime) {
    var stampDate = new Date();
    stampDate.setTime(stampTime);
    var stampElement = new Element("div", {
      id: "managePanelStamps_panelStamp_" + id,
      className: "managePanelStamps_panelStamp"
    });
    var nameElement = new Element("div", {
      className: "managePanelStamps_panelStamp_name"
    });
    nameElement.update(name);
    var createdElement = new Element("div", {
      className: "managePanelStamps_panelStamp_created"
    });
    createdElement.update(getLocale().getText("panelAdmin.block.panelStamps.created") + ": " + getLocale().getDate(stampDate));
    var descriptionElement = new Element("div", {
      className: "managePanelStamps_panelStamp_desc"
    });
    descriptionElement.update(description);
    stampElement.appendChild(nameElement);
    stampElement.appendChild(createdElement);
    stampElement.appendChild(descriptionElement);
    if (this._listingBlock.hasChildNodes()) {
      this._listingBlock.insertBefore(stampElement, this._listingBlock.firstChild);
    }
    else {
      this._listingBlock.appendChild(stampElement);
    }
    Event.observe(stampElement, "click", this._editStampListener);
    this._editStamp(stampElement);
  },
  _refreshSelectedStamp: function() {
    var stampElement = this._listingBlock.down('.managePanelStamps_panelStamp_selected'); 
    var titleElement = stampElement.down('.managePanelStamps_panelStamp_name');
    titleElement.update(this._editorBlock.down('input[name="title"]').value);
    var descriptionElement = stampElement.down('.managePanelStamps_panelStamp_desc');
    descriptionElement.update(this._editorBlock.down('textarea[name="description"]').value);
  },
  _editStamp: function(stampElement) {
    this._listingBlock.select('.managePanelStamps_panelStamp_selected').each(function (stampElement) {
      stampElement.removeClassName('managePanelStamps_panelStamp_selected');
    });
    if (stampElement) {
      var stampId = stampElement.id.split("_");
      stampId = stampId[stampId.length - 1];
      stampElement.addClassName('managePanelStamps_panelStamp_selected');
      var idElement = this._editorBlock.down('input[name="stampId"]');
      idElement.value = stampId;
      var titleElement = this._editorBlock.down('input[name="title"]');
      titleElement.value = stampElement.down('.managePanelStamps_panelStamp_name').innerHTML;
      var descElement = this._editorBlock.down('textarea[name="description"]');
      descElement.value = stampElement.down('.managePanelStamps_panelStamp_desc').innerHTML;
      $('managePanelStampsStampViewColumn_stampName').update(getLocale().getText("panelAdmin.block.panelStamps.editStamp"));
    }
    else {
      var idElement = this._editorBlock.down('input[name="stampId"]');
      idElement.value = '';
      var titleElement = this._editorBlock.down('input[name="title"]');
      titleElement.value = '';
      titleElement.focus();
      var descElement = this._editorBlock.down('textarea[name="description"]');
      descElement.value = '';
      $('managePanelStampsStampViewColumn_stampName').update(getLocale().getText("panelAdmin.block.panelStamps.newStamp"));
    }
  },
  _onEditStampClick: function(event) {
    var stampElement = event.target;
    if (!stampElement.hasClassName('managePanelStamps_panelStamp')) {
      stampElement = stampElement.up('.managePanelStamps_panelStamp');
    }
    this._editStamp(stampElement);
  },
  _onNewStampClick: function(event) {
    Event.stop(event);
    this._editStamp();
  },
  _onSaveButtonClick: function(event) {
    Event.stop(event);
    
    var stampId = this.getBlockElement().down('input[name="stampId"]').value;
    var title = this.getBlockElement().down('input[name="title"]').value;
    var description = this.getBlockElement().down('textarea[name="description"]').value;
    
    var _this = this;
    if (stampId == '') {
      startLoadingOperation("panelAdmin.block.panelStamps.creatingStamp");
      JSONUtils.request(CONTEXTPATH + '/panel/admin/createpanelstamp.json', {
        parameters: {
          title: title,
          description: description
        },
        onComplete: function (transport) {
          endLoadingOperation();
        },
        onSuccess: function (jsonResponse) {
          _this._addStamp(
            jsonResponse.stampId,
            jsonResponse.name,
            jsonResponse.description,
            jsonResponse.stampTime
          );
        }
      });
    }
    else {
      startLoadingOperation("panelAdmin.block.panelStamps.updatingStamp");
      JSONUtils.request(CONTEXTPATH + '/panel/admin/updatepanelstamp.json', {
        parameters: {
          stampId: stampId,
          title: title,
          description: description
        },
        onComplete: function (transport) {
          endLoadingOperation();
        },
        onSuccess: function (jsonResponse) {
          _this._refreshSelectedStamp();
        }
      });
    }
  }
});

addBlockController(new PanelStampEditorBlockController());