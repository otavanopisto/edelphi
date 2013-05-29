LocalDocumentEditorBlockController = Class.create(BlockController, {
  initialize: function ($super) {
    $super();
    
    this._currentPage = null;
    
    this._newPageElementClickListener = this._onNewPageElementClick.bindAsEventListener(this);
    this._pageElementClickListener = this._onPageElementClick.bindAsEventListener(this);
    this._deletePageClickListener = this._onDeletePageClick.bindAsEventListener(this);
    this._saveButtonClickListener = this._onSaveButtonClick.bindAsEventListener(this);
    this._titleChangeListener = this._onTitleChange.bindAsEventListener(this);
    
    if ((typeof JSDATA) != 'undefined') {
      if (JSDATA['localDocumentPages'])
        this._pageDatas = JSDATA['localDocumentPages'].evalJSON();
    }
    
    if (!this._pageDatas)
      this._pageDatas = new Array();
  },
  setup: function ($super) {
    $super($('panelAdminDocumentEditorBlock'));
    
    this._formElement = $('panelAdminDocumentEditorForm');
    this._nameInputElement = this._formElement.down("input[name='name']");
    this._localDocumentIdElement = this._formElement.down("input[name='localDocumentId']");
    this._parentFolderIdElement = this._formElement.down("input[name='parentFolderId']");
    this._saveElement = this._formElement.down("input[name='save']");
    this._editorPages = this.getBlockElement().down('.panelAdminDocumentEditorPages');
    this._newPageElement = this.getBlockElement().down('#panelAdminDocumentEditorCreatePageLink');
    
    this._titleInput = this._formElement.down("input[name='title']");
    if (this._localDocumentIdElement.value) {
      startLockToucher(this._localDocumentIdElement.value);
    }

    var _this = this;
    this.getBlockElement().select('.panelAdminDocumentEditorPage').each(function (pageElement) {
      Event.observe(pageElement, "click", _this._pageElementClickListener);
    });
    
    Event.observe(this._newPageElement, "click", this._newPageElementClickListener);
    Event.observe(this._saveElement, "click", this._saveButtonClickListener);
    Event.observe(this._titleInput, "change", this._titleChangeListener);
    
    var panelId = JSDATA['securityContextId'];

    this._ckEditor = CKEDITOR.replace('content', {
// TODO: Can we load all extra plugins from same config file or should we load plugins based on individual editor instances?    
//      extraPlugins: 'fnigenericbrowser',
      toolbar: "materialToolbar",
      fniGenericBrowser:{
        enabledInDialogs: ['image', 'link'],
        connectorUrl: CONTEXTPATH + '/system/ckbrowserconnector.json?panelId=' + panelId
      }
    });
    
    this._initSortContainers();
    
    if (this._getPageCount() < 1) {
      var pageData = this._createPageData();
      this._addNewPage(pageData);
      this._setActivePage(pageData.id);
    } else {
      for (var i = 0, l = this._pageDatas.length; i < l; i++) {
        this._addNewPage(this._pageDatas[i]);
      }
      
      var pageData = undefined;
//      var pageHash = this.getHashParam("p");
//      if (pageHash)
//        pageData = this._getPageDataById(pageHash); 
          
      if (!pageData)
        pageData = this._pageDatas[0];
      
      this._setActivePage(pageData.id);
    }
  },
  deinitialize: function ($super) {
    var _this = this;
    this.getBlockElement().select('.panelAdminDocumentEditorPage').each(function (pageElement) {
      Event.stopObserving(pageElement, "click", _this._pageElementClickListener);
    });
    
    Event.stopObserving(this._newPageElement, "click", this._newPageElementClickListener);
    Event.stopObserving(this._saveElement, "click", this._saveButtonClickListener);
    Event.stopObserving(this._titleInput, "change", this._titleChangeListener);
        
    this._ckEditor.destroy();
    
    $super();
  },
  _initSortContainers: function () {
    Sortable.create(this._editorPages, { 
      tag: "div",
      overlap: "horizontal",
      only: "panelAdminDocumentEditorPage",
//      dropOnEmpty: true,
      constraint: "horizontal",
      endeffect: Prototype.emptyFunction,
      onUpdate: function () {
      }
    });
  },  
  _addNewPage: function (pageData) {
    var pageElement = new Element("div", {
      className: "panelAdminDocumentEditorPage",
      id: "panelAdminDocumentEditorPage_" + pageData.id
    });
    
    var pageTitle = new Element("div", {
      className: "panelAdminDocumentEditorPageTitle"
    });
    pageTitle.update(pageData.title);

    var pageDeleteElement = new Element("div", {
      className: "panelAdminDocumentEditorPageDelete"
    });
    var pageValidationErrorElement = new Element("div", {
      className: "panelAdminDocumentEditorPageValidationError"
    });
    
    var idInput = new Element("input", {
      type: "hidden",
      name: "pageId",
      value: pageData.id
    });

    pageElement.appendChild(pageValidationErrorElement);
    pageElement.appendChild(pageTitle);
    pageElement.appendChild(pageDeleteElement);
    pageElement.appendChild(idInput);
    
    pageValidationErrorElement.hide();
    this._editorPages.appendChild(pageElement);
    
    Event.observe(pageElement, "click", this._pageElementClickListener);
    Event.observe(pageDeleteElement, "click", this._deletePageClickListener);
    
    this._initSortContainers();
  },
  _getPageDataById: function(pageId) {
    for (var i = 0, l = this._pageDatas.length; i < l; i++) {
      if (this._pageDatas[i].id == pageId) {
        return this._pageDatas[i];
      }
    }
    return undefined;
  },
  _getPageElementById: function(pageId) {
    var pageIdInput = this._editorPages.down("input[name='pageId'][value='" + pageId + "']");
    
    if (pageIdInput)
      return pageIdInput.up(".panelAdminDocumentEditorPage");
    else
      return null;
  },
  _getPageCount: function () {
    return this._pageDatas.length;
  },
  _removePage: function (pageId) {
    var pageElement = this._getPageElementById(pageId);
    pageElement.purge();
    pageElement.remove();
    
    for (var i = 0, l = this._pageDatas.length; i < l; i++) {
      if (this._pageDatas[i].id == pageId)
        this._pageDatas.splice(i, 1);
    }
    
    if (this._currentPage == pageId)
      this._currentPage = null;
  },
  _createPageData: function() {
    var tempId = "temp" + new Date().getTime();

    var pageData = {
      id: tempId,
      title: getLocale().getText("panelAdmin.block.localdocumenteditor.newPageTitle"),
      isNew: true,
      content: ""
    };

    this._pageDatas.push(pageData);
    
    return pageData;
  },
  _setActivePage: function (pageId) {
    this._updateEditorContents();

    var pageData = this._getPageDataById(pageId);
    
    this._ckEditor.setData(pageData.content);

    this._titleInput.value = pageData.title;

    this._currentPage = pageId;

    var pageElementId = "panelAdminDocumentEditorPage_" + pageId;
    this.getBlockElement().select('.panelAdminDocumentEditorPage').each(function (pageElement) {
      if (pageElement.getAttribute("id") != pageElementId)
        pageElement.removeClassName("panelAdminDocumentEditorPageSelected");
      else
        pageElement.addClassName("panelAdminDocumentEditorPageSelected");
    });

    forceRevalidateAll(true);
    this._validate();
  },
  _getCurrentPageData: function () {
    var pageId = this._currentPage; 
    if (pageId !== null)
      return this._getPageDataById(pageId);
    else
      return undefined;
  },
  _updateEditorContents: function () {
    var pageId = this._currentPage; 
    if (pageId !== null) {
      var pageElement = this._getPageElementById(pageId);
      var pageData = this._getPageDataById(pageId);

      pageData.content = this._ckEditor.getData();
      pageData.title = this._titleInput.value;
      
      pageElement.down(".panelAdminDocumentEditorPageTitle").update(this._titleInput.value);
    }
  },
  _onSaveButtonClick: function (event) {
    Event.stop(event);

    if (!this._validate())
      return;
    
    this._updateEditorContents();
    // TODO: Disable content element while sending 
    
    var pages = this._pageDatas;
    var pageCount = pages.length;

    var pageIds = new Array();
    this._editorPages.select(".panelAdminDocumentEditorPage").each(function (node) {
      pageIds.push(node.down("input[name='pageId']").value);
    });
    
    if (pageIds.length != pageCount) {
      alert("Editor synchronization error");
      return;
    }
    
    var parameters = new Hash();

    parameters.set("name", this._nameInputElement.value);
    parameters.set("localDocumentId", this._localDocumentIdElement.value);
    parameters.set("parentFolderId", this._parentFolderIdElement.value);
    
    parameters.set("pageCount", pageCount);
    for (var pageIndex = 0; pageIndex < pageCount; pageIndex++) {
      var pageId = pageIds[pageIndex];
      var pageData = this._getPageDataById(pageId);
      
      parameters.set("page." + pageIndex + ".id", pageData.id);
      parameters.set("page." + pageIndex + ".title", pageData.title);
      parameters.set("page." + pageIndex + ".content", pageData.content);
      parameters.set("page." + pageIndex + ".isDeleted", pageData.isDeleted === true);
      parameters.set("page." + pageIndex + ".isNew", (pageData.isNew === true) || (pageData.isNew == "true"));
    }
    
    startLoadingOperation("panelAdmin.block.localDocument.savingDocument");
    
    var _this = this;
    
    JSONUtils.request(CONTEXTPATH + '/resources/savelocaldocument.json', {
      parameters: parameters,
      onComplete: function (transport) {
        endLoadingOperation();
      },
      onSuccess: function (jsonResponse) {
        if (!_this._formElement.localDocumentId.value) {
          var securityContextType = JSDATA["securityContextType"];
          var localDocumentId = jsonResponse.localDocumentId;
          
          if (securityContextType == "PANEL") {
            var panelId = JSDATA['securityContextId'];
            window.location.href = CONTEXTPATH + '/panel/admin/editlocaldocument.page?panelId=' + panelId + '&localDocumentId=' + localDocumentId;
          } else {
            var parentFolderId = jsonResponse.localDocumentParentFolderId;
            var queryParams = document.location.search;
            if (queryParams.length > 1) {
              queryParams = '&' + queryParams.substring(1);
            }
            else {
              queryParams = '';
            }
            window.location.href = CONTEXTPATH + '/admin/editlocaldocument.page?parentFolderId=' + parentFolderId + '&localDocumentId=' + localDocumentId + queryParams;
          }
          deleteFormDraft(false);
        }
        else {
          var pagesContainer = _this._editorPages;
          
          var newPageCount = jsonResponse.newPageCount;
          for (var i = 0; i < newPageCount; i++) {
            var newPageId = jsonResponse["newPage." + i + ".id"];
            var tempId = jsonResponse["newPage." + i + ".tempId"];
  
            var pageIdInput = pagesContainer.down('input[name="pageId"][value="' + tempId + '"]');
            pageIdInput.value = newPageId;
  
            var pageData = _this._getPageDataById(tempId);
            pageData.id = newPageId;
            pageData.isNew = false;
            
            if (_this._currentPage == tempId)
              _this._currentPage = newPageId;
            
            // Hash check
//            var pageHash = _this.getHashParam("p");
//            if (pageHash == tempId)
//              window.location.hash = '#p:' + newPageId;
          }
  
          var removedPageCount = jsonResponse.removedPageCount;
          for (var i = 0; i < removedPageCount; i++) {
            var pageId = jsonResponse["removedPage." + i + ".id"];
            _this._removePage(pageId);
          }
  
          JSONUtils.showMessages(jsonResponse);
          deleteFormDraftByStrategy(false, "URL");
        }
        
      }
    });
  },
  _onPageElementClick: function (event) {
    var pageElement = Event.element(event);
    
    if (!pageElement.hasClassName("panelAdminDocumentEditorPage"))
      pageElement = pageElement.up(".panelAdminDocumentEditorPage");
    
    var pageId = pageElement.down("input[name='pageId']").value;

//    window.location.hash = '#p:' + pageId;
    this._setActivePage(pageId);
  },
  _onNewPageElementClick: function (event) {
    var pageData = this._createPageData();
    this._addNewPage(pageData);
    this._setActivePage(pageData.id);
  },
  _onDeletePageClick: function (event) {
    Event.stop(event);
    
    this._updateEditorContents();
    
    var element = Event.element(event);
    var pageElement = element.up(".panelAdminDocumentEditorPage");
    var pageData = this._getCurrentPageData();
    pageData.isDeleted = (pageData.isDeleted == undefined) ? true : pageData.isDeleted === false;
    
    if (pageData.isDeleted) {
      pageElement.addClassName("panelAdminDocumentEditorPageDeleted");

      var eventQueue = getGlobalEventQueue();
      eventQueue.addItem(new EventQueueItem(getLocale().getText("panelAdmin.block.localdocumenteditor.pageMarkedForDeletion"), {
        className: "eventQueueSuccessItem",
        timeout: 1000 * 3
      }));
    }
    else
      pageElement.removeClassName("panelAdminDocumentEditorPageDeleted");
    
    this._setActivePage(this._currentPage);
  },
  _onTitleChange: function (event) {
    var newTitle = this._titleInput.value;
    var pageElement = this._getPageElementById(this._currentPage);
    pageElement.down(".panelAdminDocumentEditorPageTitle").update(newTitle);
    
    var pageData = this._getCurrentPageData();
    pageData.title = newTitle;
    
    this._validate();
  },
  _validate: function() {
    var docTitle = this.getBlockElement().down('input[name="name"]').value;

    var docValid = (docTitle) && (docTitle != "");
    var pagesValid = true;
    
    for (var i = 0, l = this._pageDatas.length; i < l; i++) {
      var pageData = this._pageDatas[i];
      
      var pageElement = this._getPageElementById(pageData.id);
      var pageElementValidationErrorIndicator = pageElement.down(".panelAdminDocumentEditorPageValidationError");
      var pageValid = (pageData.title) && (pageData.title != "");
      
      if (!pageValid) {
        pageElementValidationErrorIndicator.show();
        pagesValid = false;
      } else {
        pageElementValidationErrorIndicator.hide();
      }
    }
    
    if (!(pagesValid && docValid)) {
      this._saveElement.disable();
      this._saveElement.addClassName("disabledButton");
    } else {
      this._saveElement.enable();
      this._saveElement.removeClassName("disabledButton");
    }
    
    return (pagesValid && docValid);
  }
});

addBlockController(new LocalDocumentEditorBlockController());