CreatePanelBlockController = Class.create(BlockController, {
  initialize : function() {
    this._createPanelButtonClickListener = this._onCreatePanelButtonClick.bindAsEventListener(this);
    this._closeModalButtonClickListener = this._onCloseModalButtonClick.bindAsEventListener(this);
    
    this._pages = new Array();
    this._pages.push(new ChoosePanelTypePage(this));
    this._pages.push(new PanelBasicInfoPage(this));
    this._currentPage = undefined;
    this._currentPageNumber = undefined;
    this._newPanelSettings = {};
  },
  deinitialize: function () {
    this._reset();

    Event.stopObserving(this._createPanelButton, "click", this._createPanelButtonClickListener);
    Event.stopObserving(this._closeModalButton, "click", this._closeModalButtonClickListener);
  },
  setup: function ($super, blockElement) {
    var blockElement = $('createPanelBlockContent');
    
    $super(blockElement);
    
    this._createPanelButton = blockElement.down('.createPanelBlockCreatePanelLink');
    this._closeModalButton = blockElement.down('.createPanelBlock_createPanelCloseModalButton');
    
    Event.observe(this._createPanelButton, "click", this._createPanelButtonClickListener);
    Event.observe(this._closeModalButton, "click", this._closeModalButtonClickListener);
  },
  hideModal: function () {
    var dialogElement = this.getBlockElement().down('.createPanelBlock_createPanelDialogOverlay');
    var contentContainer = this.getBlockElement().down('.createPanelBlock_createPanelPageContainer');

    dialogElement.hide();
    contentContainer.hide();

    this._reset();
  },
  _reset: function() {
    if (this._currentPage) {
      this._currentPage.deinitPage();
    }
    
    this._newPanelSettings = {};
    this._currentPage = undefined;
    this._currentPageNumber = undefined;
  },
  _showPage: function (pageNumber) {
    if (this._currentPage) {
      this._currentPage.deinitPage();
    }
    
    this._currentPageNumber = pageNumber;
    this._currentPage = this._pages[pageNumber];
    this._currentPage.initPage();
  },
  _onCreatePanelButtonClick: function (event) {
    this._showPage(0);
    
    var dialogElement = this.getBlockElement().down('.createPanelBlock_createPanelDialogOverlay');
    var contentContainer = this.getBlockElement().down('.createPanelBlock_createPanelPageContainer');
    dialogElement.show();
    contentContainer.show();
  },
  _onCloseModalButtonClick: function (event) {
    this.hideModal();
  }
});

CreatePanelPage = Class.create({
  initialize : function (blockController) {
    this._blockController = blockController;
  },
  initPage: function () {
    
  },
  deinitPage: function () {
    
  },
  _loadContent: function (url, onSuccessHandler) {
    var contentElement = this._blockController.getBlockElement().down('.createPanelBlock_createPanelPageContent');

    new Ajax.Request(url, {  
      onSuccess: function(transport){
        contentElement.update(transport.responseText);
        if (onSuccessHandler)
          onSuccessHandler(transport);
      },
      onFailure: function(transport) {
        throw new Error(transport.responseText);
      }
    });
  }
});

ChoosePanelTypePage = Class.create(CreatePanelPage, {
  initPage: function () {
    this._choosePanelTypeButtonClickListener = this._onChooseTypeButtonClick.bindAsEventListener(this);

    var _this = this;
    this._loadContent(CONTEXTPATH + '/createpanel/choosepaneltype.page', function (transport) {
      _this._blockController.getBlockElement().select('.createPanel_panelType').each(function (node) {
        Event.observe(node, "click", _this._choosePanelTypeButtonClickListener);
      });
    });
  },
  deinitPage: function () {
    this._blockController.getBlockElement().select('.createPanel_panelType').each(function (node) {
      Event.stopObserving(node, "click", this._choosePanelTypeButtonClickListener);
    });
  },
  _onChooseTypeButtonClick: function (event) {
    var panelTypeElement = Event.element(event);
    if (!panelTypeElement.hasClassName('createPanel_panelType'))
      panelTypeElement = panelTypeElement.up('.createPanel_panelType');
    
    var panelTypeId = panelTypeElement.down('input[name="panelTypeId"]').value;
    
    this._blockController._newPanelSettings.panelSettingsTemplateId = panelTypeId;
    
    this._blockController._showPage(++this._blockController._currentPageNumber);
  }
});

PanelBasicInfoPage = Class.create(CreatePanelPage, {
  initPage: function () {
    this._doneButtonClickListener = this._onDoneButtonClick.bindAsEventListener(this);
    this._prevPageButtonClickListener = this._onPrevPageButtonClick.bindAsEventListener(this);

    this._nameChangeListener = this._onNameChange.bindAsEventListener(this);
    this._descChangeListener = this._onDescChange.bindAsEventListener(this);

    var _this = this;
    this._loadContent(CONTEXTPATH + '/createpanel/panelbasicinfo.page', function (transport) {
      _this._doneButton = _this._blockController.getBlockElement().down('.createPanel_donePageLink');
      Event.observe(_this._doneButton, "click", _this._doneButtonClickListener);
      _this._prevPageButton = _this._blockController.getBlockElement().down('.createPanel_prevPageLink');
      Event.observe(_this._prevPageButton, "click", _this._prevPageButtonClickListener);
      
      _this._panelNameInput = _this._blockController.getBlockElement().down('input[name="createPanel_panelName"]');
      _this._panelDescInput = _this._blockController.getBlockElement().down('textarea[name="createPanel_panelDescription"]');
      
      Event.observe(_this._panelNameInput, "keyup", _this._nameChangeListener);
      Event.observe(_this._panelDescInput, "keyup", _this._descChangeListener);
      
      if (_this._blockController._newPanelSettings.panelName)
        _this._panelNameInput.value = _this._blockController._newPanelSettings.panelName;
      
      if (_this._blockController._newPanelSettings.panelDescription)
        _this._panelDescInput.value = _this._blockController._newPanelSettings.panelDescription;
    });
  },
  deinitPage: function () {
    Event.stopObserving(this._doneButton, "click", this._doneButtonClickListener);
    Event.stopObserving(this._prevPageButton, "click", this._prevPageButtonClickListener);
    Event.stopObserving(this._panelNameInput, "keyup", this._nameChangeListener);
    Event.stopObserving(this._panelDescInput, "keyup", this._descChangeListener);
  },
  _onDoneButtonClick: function (event) {
    var parameters = this._blockController._newPanelSettings;
    var _this = this;
    
    JSONUtils.request(CONTEXTPATH + '/panel/createpanel.json', {
      parameters: parameters,
      onSuccess : function(jsonRequest) {
        _this._blockController.hideModal();
        
        var panelUrl = CONTEXTPATH + '/panel/viewpanel.page?panelId=' + jsonRequest.panelId;
        redirectTo(panelUrl);
      }
    });
  },
  _onPrevPageButtonClick: function (event) {
    this._blockController._newPanelSettings.panelName = this._panelNameInput.value;
    this._blockController._newPanelSettings.panelDescription = this._panelDescInput.value;
    
    this._blockController._showPage(--this._blockController._currentPageNumber);
  },
  _onNameChange: function (event) {
    this._blockController._newPanelSettings.panelName = this._panelNameInput.value;
  },
  _onDescChange: function (event) {
    this._blockController._newPanelSettings.panelDescription = this._panelDescInput.value;
  }
});

addBlockController(new CreatePanelBlockController());