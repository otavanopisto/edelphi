BulletinEditorBlockController = Class.create(BlockController, {
  initialize: function ($super) {
    $super();
    
    this._saveButtonClickListener = this._onSaveButtonClick.bindAsEventListener(this);
  },
  setup: function ($super) {
    $super($('panelAdminBulletinEditorBlock'));
    
    var panelId = JSDATA['securityContextId'];

    this._ckEditor = CKEDITOR.replace('message', {
// TODO: Can we load all extra plugins from same config file or should we load plugins based on individual editor instances?    
//      extraPlugins: 'fnigenericbrowser',
      toolbar: "materialToolbar",
      fniGenericBrowser:{
        enabledInDialogs: ['image', 'link'],
        connectorUrl: CONTEXTPATH + '/system/ckbrowserconnector.json?panelId=' + panelId
      }
    });
    
    this._saveButton = this.getBlockElement().down('input[name="save"]');
    Event.observe(this._saveButton, "click", this._saveButtonClickListener);
    this._bulletinId = this.getBlockElement().down('input[name="bulletinId"]').value;
  },
  deinitialize: function ($super) {
    Event.stopObserving(this._saveButton, "click", this._saveButtonClickListener);
    this._ckEditor.destroy();
    
    $super();
  },
  _onSaveButtonClick: function (event) {
    Event.stop(event);
    
    var title = this.getBlockElement().down('input[name="title"]').value;
    var message = this._ckEditor.getData();
    if (message) {
	    startLoadingOperation("panelAdmin.block.bulletins.savingBulletin");
	    
	    if (this._bulletinId) {
	      JSONUtils.request(CONTEXTPATH + '/panel/admin/updatebulletin.json', {
	        parameters: {
	          bulletinId: this._bulletinId,
	          title: title,
	          message: message
	        },
	        onComplete: function (transport) {
	          endLoadingOperation();
	        },
	        onSuccess: function (jsonResponse) {
	          var panelId = JSDATA['securityContextId'];
	          window.location.href = CONTEXTPATH + '/panel/admin/editpanelbulletin.page?panelId=' + panelId + '&bulletinId=' + jsonResponse.bulletinId;
	        }
	      });
	    }
	    else {
	      JSONUtils.request(CONTEXTPATH + '/panel/admin/createbulletin.json', {
	        parameters: {
	          title: title,
	          message: message
	        },
	        onComplete: function (transport) {
	          endLoadingOperation();
	        },
	        onSuccess: function (jsonResponse) {
	          var panelId = JSDATA['securityContextId'];
	          window.location.href = CONTEXTPATH + '/panel/admin/editpanelbulletin.page?panelId=' + panelId + '&bulletinId=' + jsonResponse.bulletinId;
	        }
	      });
	    }
    }
    else {
      var eventQueue = getGlobalEventQueue();
      eventQueue.addItem(new EventQueueItem(getLocale().getText("panelAdmin.block.bulletins.emptyContent"), {
        className: "eventQueueWarningItem",
        timeout: -1
      }));
    }
  }
});

addBlockController(new BulletinEditorBlockController());