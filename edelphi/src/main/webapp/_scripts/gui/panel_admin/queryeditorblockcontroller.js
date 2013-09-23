QueryEditorUtils = {
  parseSerializedList: function (serializedValue) {
    var list = new Array();
    if (Object.isString(serializedValue) && (serializedValue !== '')) {
      var values = serializedValue.split('&');
      for (var i = 0, l = values.length; i < l; i++) {
        var value = values[i];
        if (value !== undefined)
          value = decodeURIComponent(value);
        list.push(value);
      }
    }
    
    return list;
  },
  serializeList: function (list) {
    var serializedValue = '';
    for (var i = 0, l = list.length; i < l; i++) {
      serializedValue += encodeURIComponent(list[i]);
      if (i < (l - 1)) {
        serializedValue += '&';
      }
    }

    return serializedValue;
  },
  parseSerializedMap: function (serializedValue) {
    var map = new Object();
    if (Object.isString(serializedValue) && (serializedValue !== '')) {
      var split = serializedValue.split('&');
      for (var i = 0, l = split.length; i < l; i++) {
        var pair = split[i].split('=');
        var key = pair[0];
        var value = pair[1];
        if (key !== undefined)
          key = decodeURIComponent(key);
        if (value)
          value = decodeURIComponent(value);
        map[key] = value;
      }
    }
    
    return map;
  },
  serializeMap: function (map) {
    return $H(map).toQueryString();
  }
};

QueryEditorDraftTask = Class.create(fi.internetix.draft.CustomDraftTask, {
  initialize: function ($super, editor) {
    $super();
    
    this._editor = editor;
  },
  createDraftData: function () {
    this._editor._commitChanges();
    return new fi.internetix.draft.ElementDraft(this.getId(), 'data', this._compress(Object.toJSON({
      sectionDatas: this._editor._sectionDatas,
      pageDatas: this._editor._pageDatas 
    })));
  },
  restoreDraftData: function (elementDraft) { 
    this._editor.loadPageData(elementDraft.getData().sectionDatas, elementDraft.getData().pageDatas);
  },
  getId: function () {
    return 'queryEditor';
  }
});

QueryEditorBlockController = Class.create(BlockController, {
  initialize: function ($super) {
    $super();
    
    this._saveButtonClickListener = this._onSaveButtonClick.bindAsEventListener(this);
    this._pageClickListener = this._onPageClick.bindAsEventListener(this);
    this._deletePageClickListener = this._onDeletePageClick.bindAsEventListener(this);
    this._clonePageClickListener = this._onClonePageClick.bindAsEventListener(this);
    this._createPageClickListener = this._onCreatePageClick.bindAsEventListener(this);
    this._createSectionClickListener = this._onCreateSectionClick.bindAsEventListener(this);
    this._tabChangeListener = this._onTabChange.bindAsEventListener(this);
    this._sectionTitleClickListener = this._onSectionTitleClick.bindAsEventListener(this);
    this._deleteSectionClickListener = this._onDeleteSectionClick.bindAsEventListener(this);
    this._queryNameChangeListener = this._onQueryNameChange.bindAsEventListener(this);
    
    this._elementEditor = null;
    
    if ((typeof JSDATA) != 'undefined') {
      this._sectionDatas = JSDATA['querySections'].evalJSON();
      
      if (JSDATA['queryPages'])
        this._pageDatas = JSDATA['queryPages'].evalJSON();
    }
    else {
      this._sectionDatas = new Array();
    }
    
    if (!this._pageDatas)
      this._pageDatas = new Array();
    
    this._currentPageId = undefined;
  },
  setup: function ($super) {
    $super($('panelAdminQueryEditorBlock'));
    
    this._createPageLink = $('panelAdminQueryEditorCreatePageLink');
    this._createSectionLink = $('panelAdminQueryEditorCreateSectionLink');
    
    this._editorContainer = this.getBlockElement().down('.panelAdminQueryEditorEditorContainer');
    this._pageListPagesContainer = this.getBlockElement().down('.panelAdminQueryEditorPages');
    this._saveButton = this.getBlockElement().down('input[name="save"]');
    this._tabsContainer = this.getBlockElement().down('.panelAdminQueryEditorTabs');
    this._tabControl = new S2.UI.Tabs(this._tabsContainer);
    this._queryNameInput = this.getBlockElement().down('input[name="name"]');
    
    this._queryIdInput = this.getBlockElement().down('input[name="queryId"]');
    if (this._queryIdInput.value != 'NEW') {
      startLockToucher(this._queryIdInput.value);
    }

    Event.observe(this._saveButton, "click", this._saveButtonClickListener);
    Event.observe(this._createPageLink, "click", this._createPageClickListener);
    Event.observe(this._createSectionLink, "click", this._createSectionClickListener);
    Event.observe(this._queryNameInput, "change", this._queryNameChangeListener);
    
    this._tabControl.element.observe('ui:tabs:change', this._tabChangeListener);
    
    this._initializePages();
    
    fi.internetix.draft.DraftTaskVault.registerCustomTask(new QueryEditorDraftTask(this));
  },
  deinitialize: function ($super) {
    Event.stopObserving(this._saveButton, "click", this._saveButtonClickListener);
    Event.stopObserving(this._createPageLink, "click", this._createPageClickListener);
    Event.stopObserving(this._createSectionLink, "click", this._createSectionClickListener);
    Event.stopObserving(this._queryNameInput, "change", this._queryNameChangeListener);
 
    this._tabControl.element.stopObserving('ui:tabs:change', this._tabChangeListener);
    this._deinitializePages();
    
    $super();
  },
  loadPageData: function (sectionDatas, pageDatas) {
    this._sectionDatas = sectionDatas;
    if (!this._sectionDatas) {
      this._sectionDatas = new Array();
    }
    
    this._pageDatas = pageDatas;
    if (!this._pageDatas)
      this._pageDatas = new Array();
    
    this._deinitializePages();
    this._initializePages();
    
    // for proper validation after a draft restore
    forceRevalidateAll(true);
    this._validate();
  },
  getPageDatasByType: function (type) {
    var result = new Array();
    
    this._pageDatas.each(function (pageData) {
      if (pageData.type == type) {
        result.push(pageData);
      }
    });
    
    return result;
  },
  
  _initializePages: function () {

    // remove current editor to avoid deprecated data during draft restore  
    if (this._elementEditor) {
      this._elementEditor.deinitialize();
      this._elementEditor = null;
    }
    
    var hasPages = this._pageDatas.length > 0;
    
    for (var sectionIndex = 0, sectionsLength = this._sectionDatas.length; sectionIndex < sectionsLength; sectionIndex++) {
      var sectionData = this._sectionDatas[sectionIndex];
      
      var sectionElement = this._addSectionElement(sectionData.id, sectionData.title);
      var sectionPagesElement = sectionElement.down('.panelAdminQueryEditorSectionPages');
      
      var sectionPages = sectionData['pages'];
      if (sectionPages) {
        for (var i = 0, l = sectionPages.length; i < l; i++) {
          var pageId = sectionPages[i];
          var pageData = this._getPageDataById(pageId);

          this._addPage(sectionPagesElement, pageData);
        }
      }
    }

    this._initSortContainers();
    
    if (hasPages) {
//      this._editPage(0);
//      this._commitChanges();
    } else {
      this._editSection(this._sectionDatas[0].id);
    }
  },
  _deinitializePages: function () {
    var pages = this.getBlockElement().select('.panelAdminQueryEditorPage');
    for (var i = pages.length - 1; i >= 0; i--) {
      pages[i].purge();
      pages[i].remove();
    }
    
    this.getBlockElement().select('.panelAdminQueryEditorSectionTitle').invoke('purge');
    this.getBlockElement().select('.panelAdminQueryEditorSection').invoke('remove');
  },
  _initSortContainers: function () {
    var _this = this;
    
    Position.includeScrollOffsets = true; 
    
    Sortable.create(this._pageListPagesContainer, { 
      tag: "div",
      overlap: "horizontal",
      only: "panelAdminQueryEditorSection",
      constraint: "horizontal",
      endeffect: Prototype.emptyFunction,
      onUpdate: function () {
        var sectionElements = _this._pageListPagesContainer.select(".panelAdminQueryEditorSection");
        var sectionCount = sectionElements.length;
        
        for (var i = 0; i < sectionCount; i++) {
          var sectionElement = sectionElements[i];
          var sectionId = _this._getSectionIdFromElement(sectionElement);
          
          if (_this._sectionDatas[i].id != sectionId) {
            for (var j = i; j < sectionCount; j++) {
              if (_this._sectionDatas[j].id == sectionId) {
                var tmpData = _this._sectionDatas[i];
                _this._sectionDatas[i] = _this._sectionDatas[j];
                _this._sectionDatas[j] = tmpData;
              }
            }
          }
        }
      }
    });
    
    var pageContainers = this._pageListPagesContainer.select(".panelAdminQueryEditorSectionPages");
    pageContainers.each(function (node) {
      Sortable.create(node, { 
        tag: "div",
        overlap: "horizontal",
        only: "panelAdminQueryEditorPage",
        dropOnEmpty: true,
        constraint: false,
        containment: pageContainers,
        endeffect: Prototype.emptyFunction,
        onUpdate: function (sectionPagesElement) {
          var sectionElement = sectionPagesElement.up(".panelAdminQueryEditorSection");
          var sectionId = _this._getSectionIdFromElement(sectionElement);
          var sectionData = _this.getSectionData(sectionId);
          
          if (!sectionData.pages)
            sectionData.pages = new Array();
          
          var sectionPages = sectionElement.select(".panelAdminQueryEditorPage");
          
          sectionData.pages.clear();
          
          for (var i = 0, l = sectionPages.length; i < l; i++) {
            var pageId = _this._getPageIdFromElement(sectionPages[i]);
            
            sectionData.pages.push(pageId);
          }
        }
      });
    });
  },
  appendFormField: function (formField) {
    this._editorContainer.appendChild(formField);
  },
  getCurrentPageData: function () {
    return this._getPageDataById(this._currentPageId);
  },
  _getElementEditor: function () {
    return this._elementEditor;
  },
  _getSelectedSectionElement: function () {
    // Returns selected section, or if n/a returns first section of the query
    var sectionElement = this._pageListPagesContainer.down(".panelAdminQueryEditorSectionSelected");
    
    if (sectionElement == undefined)
      sectionElement = this._pageListPagesContainer.down(".panelAdminQueryEditorSectionPageSelected");
    
    if (sectionElement == undefined)
      sectionElement = this._pageListPagesContainer.select(".panelAdminQueryEditorSection")[0];
        
    return sectionElement;
  },
  _addPage: function(sectionPagesElement, pageData) {
    var pageElement = new Element("div", {
      className: "panelAdminQueryEditorPage",
      id: "panelAdminQueryEditorPage_" + pageData.id
    });
    
    var pageTitleElement = new Element("div", {
      className: "panelAdminQueryEditorPageTitle"
    }).update(pageData.title);

    var pageCloneElement = new Element("div", {
      className: "blockContextualLink panelAdminQueryEditorPageClone",
      title: getLocale().getText("panelAdmin.block.query.clonePage")
    });
    var pageDeleteElement = new Element("div", {
      className: "blockContextualLink panelAdminQueryEditorPageDelete",
      title: getLocale().getText("panelAdmin.block.query.deletePage")
    });
    var pageValidationErrorElement = new Element("div", {
      className: "panelAdminQueryEditorPageValidationError"
    });
    var pageLockedElement = new Element("div", {
      className: "blockContextualLink panelAdminQueryEditorPageLocked",
      title: getLocale().getText("panelAdmin.block.query.pageLocked")
    });
    
    pageElement.appendChild(pageTitleElement);
    pageElement.appendChild(pageDeleteElement);
    pageElement.appendChild(pageCloneElement);
    pageElement.appendChild(pageValidationErrorElement);
    pageElement.appendChild(pageLockedElement);
    pageElement.appendChild(new Element("input", {
      type: "hidden",
      name: "pageId",
      value: pageData.id
    }));
    
    pageValidationErrorElement.hide();
    if (pageData.hasAnswers != "true")
      pageLockedElement.hide();
    
    sectionPagesElement.appendChild(pageElement);
    
    sectionPagesElement
      .up(".panelAdminQueryEditorSection")
      .down(".panelAdminQueryEditorDeleteSection").addClassName("panelAdminQueryEditorDeleteSectionDisabled");

    Event.observe(pageElement, "click", this._pageClickListener);
    Event.observe(pageDeleteElement, "click", this._deletePageClickListener);
    Event.observe(pageCloneElement, "click", this._clonePageClickListener);

    this._initSortContainers();

    return pageElement;
  },
  _addSectionElement: function (id, title) {
    var sectionElement = new Element("div", {
      className: "panelAdminQueryEditorSection",
      id: "panelAdminQueryEditorSection_" + id
    }); 
    
    var sectionTitleElement = new Element("div", {
      className: "panelAdminQueryEditorSectionTitle"
    }).update(title);
    
    Event.observe(sectionTitleElement, "click", this._sectionTitleClickListener);

    var deleteSectionElement = new Element("div", {
      className: "panelAdminQueryEditorDeleteSection"
    });

    var sectionValidationErrorElement = new Element("div", {
      className: "panelAdminQueryEditorSectionValidationError"
    });

    var sectionPagesElement = new Element("div", {
      className: "panelAdminQueryEditorSectionPages",
      id: "panelAdminQueryEditorSectionPages_" + id
    });
    
    Event.observe(deleteSectionElement, "click", this._deleteSectionClickListener);
    
    sectionElement.appendChild(sectionTitleElement);
    sectionElement.appendChild(deleteSectionElement);
    sectionElement.appendChild(sectionValidationErrorElement);
    sectionElement.appendChild(sectionPagesElement);
    sectionElement.appendChild(new Element("input", {
      type: "hidden",
      name: "sectionId",
      value: id
    }));
    
    sectionValidationErrorElement.hide();
    this._pageListPagesContainer.appendChild(sectionElement);

    return sectionElement;
  },
  _getPageDataById: function (pageId) {
    for (var pageIndex = 0, pagesLength = this._pageDatas.length; pageIndex < pagesLength; pageIndex++) {
      if (this._pageDatas[pageIndex].id == pageId)
        return this._pageDatas[pageIndex];
    }
    
    return null;
  },
  _removePageDataById: function (pageId) {
    for (var pageIndex = 0, pagesLength = this._pageDatas.length; pageIndex < pagesLength; pageIndex++) {
      if (this._pageDatas[pageIndex].id == pageId) {
        this._pageDatas.splice(pageIndex, 1);
        break;
      }
    }
  },
  _commitChanges: function () {
    if (this._getElementEditor())
      this._getElementEditor().commit();
  },
  _editPage: function (pageId) {
    if (this._elementEditor) {
      this._elementEditor.deinitialize();
    }
    
    this._editorContainer.show();
    this._currentPageId = pageId;

    // Clear selected pages
    this._pageListPagesContainer.select(".panelAdminQueryEditorPageSelected").each(function (node) {
      node.removeClassName("panelAdminQueryEditorPageSelected");
    });
    // Clear selected sections
    this._pageListPagesContainer.select(".panelAdminQueryEditorSectionSelected").each(function (node) {
      node.removeClassName("panelAdminQueryEditorSectionSelected");
    });
    this._pageListPagesContainer.select(".panelAdminQueryEditorSectionPageSelected").each(function (node) {
      node.removeClassName("panelAdminQueryEditorSectionPageSelected");
    });

    if (this._sectionDatas) {
      var pageData = this._getPageDataById(pageId);
      if (pageData) {
        if (pageData.isDeleted) {
          this._elementEditor = new QueryEditorEmptyPageEditor(this);
        } else {
          switch (pageData.type) {
            case 'TEXT':
              this._elementEditor = new QueryEditorTextPageEditor(this);
            break;
            case 'FORM':
              this._elementEditor = new QueryEditorFormPageEditor(this);
            break;
            case 'THESIS_SCALE_1D':
              this._elementEditor = new QueryEditorScale1DThesisPageEditor(this);
            break;
            case 'THESIS_SCALE_2D':
              this._elementEditor = new QueryEditorScale2DThesisPageEditor(this);
            break;
            case 'THESIS_TIME_SERIE':
              this._elementEditor = new QueryEditorTimeSerieThesisPageEditor(this);
            break;
            case 'EXPERTISE':
              this._elementEditor = new QueryEditorExpertisePageEditor(this);
            break;
            case 'THESIS_MULTI_SELECT':
              this._elementEditor = new QueryEditorMultiselectPageEditor(this);
            break;
            case 'THESIS_ORDER':
              this._elementEditor = new QueryEditorOrderingPageEditor(this);
            break;
            case 'THESIS_TIMELINE':
              this._elementEditor = new QueryEditorTimelineThesisPageEditor(this);
            break;
            case 'THESIS_GROUPING':
              this._elementEditor = new QueryEditorGroupingPageEditor(this);
            break;
            case 'COLLAGE_2D':
              this._elementEditor = new QueryEditorCollage2DPageEditor(this);
            break;
          }
        }
        
        var pageElement = this._getPageElementById(pageData.id);

        // Add selection to page element
        pageElement.addClassName("panelAdminQueryEditorPageSelected");

        // When page is selected, the same section will also be highlighted
        var sectionElement = pageElement.up(".panelAdminQueryEditorSection");
        if (sectionElement) {
          sectionElement.addClassName("panelAdminQueryEditorSectionPageSelected");
        }
         
        if (this._elementEditor) {
          this._elementEditor.setup();
          this._elementEditor.addListener("titleChange", this, this._onPageEditorTitleChange);
        }
      }
    }
    
    forceRevalidateAll(true);
    this._validate();
  },
  _editSection: function (sectionId) {
    if (this._elementEditor) {
      this._elementEditor.deinitialize();
    }
    
    this._editorContainer.show();

    var sectionData = this.getSectionData(sectionId);

    // Clear selected pages
    this._pageListPagesContainer.select(".panelAdminQueryEditorPageSelected").each(function (node) {
      node.removeClassName("panelAdminQueryEditorPageSelected");
    });

    // Clear selected sections
    this._pageListPagesContainer.select(".panelAdminQueryEditorSectionSelected").each(function (node) {
      node.removeClassName("panelAdminQueryEditorSectionSelected");
    });
    this._pageListPagesContainer.select(".panelAdminQueryEditorSectionPageSelected").each(function (node) {
      node.removeClassName("panelAdminQueryEditorSectionPageSelected");
    });
    
    // Add selection to selected section
    var sectionElement = this._getSectionElementById(sectionId);
    sectionElement.addClassName("panelAdminQueryEditorSectionSelected");
    
    if (sectionData.isDeleted) {
      this._createPageLink.addClassName('panelAdminQueryEditorCreatePageLinkDisabled');

      this._elementEditor = new QueryEditorEmptyPageEditor(this);
      this._elementEditor.setup();
    } else {
      this._createPageLink.removeClassName('panelAdminQueryEditorCreatePageLinkDisabled');

      this._elementEditor = new QueryEditorSectionEditor(this, sectionId);
      this._elementEditor.setup();
      this._elementEditor.addListener("titleChange", this, this._onSectionEditorTitleChange);
    }
    forceRevalidateAll(true);
    this._validate();
  },
  getPageOption: function (name) {
    if (this._sectionDatas) {
      var pageData = this.getCurrentPageData();
      if (pageData) {
        var pageOptions = pageData.options;
        for (var optionIndex = 0, optionsCount = pageOptions.length; optionIndex < optionsCount; optionIndex++) {
          if (pageData.options[optionIndex].name == name) {
            return pageData.options[optionIndex];
          }
        }
      }
    }
    
    return null;
  },
  getPageOptionValue: function (name) {
    var pageOption = this.getPageOption(name);
    if (pageOption)
      return pageOption.value;
    return null;
  },
  setPageOptionValue: function (name, value) {
    var pageOption = this.getPageOption(name);
    if (pageOption)
      pageOption.value = value;
  },
  getSectionData: function (sectionId) {
    if (this._sectionDatas) {
      for (var sectionIndex = 0, sectionsLength = this._sectionDatas.length; sectionIndex < sectionsLength; sectionIndex++) {
        var sectionData = this._sectionDatas[sectionIndex];
        if (sectionData.id == sectionId)
          return sectionData;
      }
    }
    
    return null;
  },
  _createNewPage: function (templateId) {
    var _this = this;
    JSONUtils.request(CONTEXTPATH + "/queries/getquerypagetemplate.json", {
      parameters: {
        queryPageTemplateId: templateId
      },
      onSuccess: function (jsonResponse) {
        var tempId = "temp" + new Date().getTime();
        var pageData = {
          id: tempId,
          isNew: true,
          title: jsonResponse.queryPage.title,
          type: jsonResponse.queryPage.type,
          options: jsonResponse.queryPage.options
        };
        
        var sectionElement = _this._getSelectedSectionElement();
        var sectionId = sectionElement.down('input[name="sectionId"]').value;
        var sectionData = _this.getSectionData(sectionId);
        
        var sectionPagesElement = sectionElement.down(".panelAdminQueryEditorSectionPages");
        var pages = sectionData.pages;
        if (!pages) {
          sectionData.pages = pages = new Array();
        }
        
        pages.push(tempId);
        _this._pageDatas.push(pageData);
        _this._addPage(sectionPagesElement, pageData);

        _this._commitChanges();
        _this._editPage(tempId);
      }
    });
  },
  _getTabNameFromPanel: function (panel) {
    switch (panel.id) {
      case 'panelAdminQueryEditorSettingsTab':
        return 'SETTINGS';
      break;
      case 'panelAdminQueryEditorPagesTab':
        return 'PAGES';
      break;
    }
    
    return null;
  },
  _removePage: function (pageId) {
    var pageIdInput = this._pageListPagesContainer.down('input[name="pageId"][value="' + pageId + '"]');
    var pageElement = pageIdInput.up(".panelAdminQueryEditorPage");
    var sectionElement = pageElement.up(".panelAdminQueryEditorSection");

    pageElement.purge();
    pageElement.remove();

    if (sectionElement.select(".panelAdminQueryEditorPage").length == 0)
      sectionElement.down(".panelAdminQueryEditorDeleteSection").removeClassName("panelAdminQueryEditorDeleteSectionDisabled");
    
    var done = false;

    this._removePageDataById(pageId);

    for (var sectionIndex = 0, sectionsLength = this._sectionDatas.length; sectionIndex < sectionsLength; sectionIndex++) {
      var sectionData = this._sectionDatas[sectionIndex];
      var sectionPages = sectionData['pages'];
      if (sectionPages) {
        for (var pageIndex = 0, pagesLength = sectionPages.length; pageIndex < pagesLength; pageIndex++) {
          if (sectionPages[pageIndex] == pageId) {
            sectionPages.splice(pageIndex, 1);
            done = true;
            break;
          }
        }
        
        if (done)
          break;
      }
    }
  },
  _removeSection: function (sectionId) {
    var sectionElement = this._getSectionElementById(sectionId);
    sectionElement.purge();
    sectionElement.remove();
    
    for (var i = 0, l = this._sectionDatas.length; i < l; i++) {
      if (this._sectionDatas[i].id == sectionId) {
        this._sectionDatas.splice(i, 1);
        break;
      }
    }
  },
  _onSaveButtonClick: function (event) {
    // TODO: Save animation
    
    Event.stop(event);
    
    this._commitChanges();
    
    if (!this._validate())
      return;
    
    var parameters = new Hash();
    
    var queryId = this._queryIdInput.value;
    var createNewQuery = queryId == 'NEW';
    
    parameters.set("queryId", queryId);
    parameters.set("parentFolderId", this.getBlockElement().down('input[name="parentFolderId"]').value);
    
    parameters.set("name", this.getBlockElement().down('input[name="name"]').value);
    parameters.set("description", this.getBlockElement().down('textarea[name="description"]').value);
    parameters.set("allowEditReply", this.getBlockElement().down('input[name="allowEditReply"]').checked ? '1': '0');
    parameters.set("state", this.getBlockElement().select('input[name="state"]').find(function(radio) { return radio.checked; }).value);

    var sectionCount = this._sectionDatas.length;
    for (var sectionIndex = 0; sectionIndex < sectionCount; sectionIndex++) {
      var sectionData = this._sectionDatas[sectionIndex];
      parameters.set("section." + sectionIndex + ".id",  sectionData.id);
      parameters.set("section." + sectionIndex + ".title",  sectionData.title);
      parameters.set("section." + sectionIndex + ".visible",  sectionData.visible);
      parameters.set("section." + sectionIndex + ".commentable",  sectionData.commentable);
      parameters.set("section." + sectionIndex + ".viewDiscussions",  sectionData.viewDiscussions);
      parameters.set("section." + sectionIndex + ".showLiveReports",  sectionData.showLiveReports);
      parameters.set("section." + sectionIndex + ".isDeleted",  sectionData.isDeleted === true);
      parameters.set("section." + sectionIndex + ".isNew",  (sectionData.isNew == true) || (sectionData.isNew == "true"));

      var sectionPages = sectionData['pages'];
      if (sectionPages) {
        var pageCount = sectionPages.length;
        for (var pageIndex = 0; pageIndex < pageCount; pageIndex++) {
          var pageData = this._getPageDataById(sectionPages[pageIndex]);
          var optionCount = pageData.options.length;
          
          parameters.set("section." + sectionIndex + ".page." + pageIndex + '.id', pageData.id);
          parameters.set("section." + sectionIndex + ".page." + pageIndex + '.type', pageData.type);
          parameters.set("section." + sectionIndex + ".page." + pageIndex + '.title', pageData.title);
          parameters.set("section." + sectionIndex + ".page." + pageIndex + '.optionCount', optionCount);
          parameters.set("section." + sectionIndex + ".page." + pageIndex + '.isEditable', pageData.hasAnswers != "true");
          parameters.set("section." + sectionIndex + ".page." + pageIndex + '.isDeleted', pageData.isDeleted === true);
          parameters.set("section." + sectionIndex + ".page." + pageIndex + '.isNew', (pageData.isNew === true) || (pageData.isNew == "true"));
    
          for (var optionIndex = 0; optionIndex < optionCount; optionIndex++) {
            parameters.set("section." + sectionIndex + ".page." + pageIndex + '.option.' + optionIndex + '.name', pageData.options[optionIndex].name);
            parameters.set("section." + sectionIndex + ".page." + pageIndex + '.option.' + optionIndex + '.value', pageData.options[optionIndex].value);
          }
        }
        
        parameters.set("section." + sectionIndex + ".pageCount", pageCount);
      } else {
        parameters.set("section." + sectionIndex + ".pageCount", 0);
      }
    }
    
    parameters.set("sectionCount", sectionCount);
    
    startLoadingOperation("panelAdmin.block.query.savingQuery");
    var _this = this;
    JSONUtils.request(CONTEXTPATH + '/queries/savequery.json', {
      parameters: parameters,
      onComplete: function (transport) {
        endLoadingOperation();
      },
      onSuccess: function (jsonResponse) {
        if (createNewQuery) {
          var panelId = JSDATA['securityContextId'];
          window.location.href = CONTEXTPATH + '/panel/admin/editquery.page?panelId=' + panelId + '&queryId=' + jsonResponse.queryId;
          // _this._queryIdInput.value = jsonResponse.queryId;
          deleteFormDraft(false);
        } else {
          var sectionContainer = _this._pageListPagesContainer;
  
          var newSectionCount = jsonResponse.newSectionCount;
          for (var i = 0; i < newSectionCount; i++) {
            var tempId = jsonResponse["newSection." + i + ".tempId"];
            var sectionId = jsonResponse["newSection." + i + ".id"];
  
            var sectionIdInput = sectionContainer.down('input[name="sectionId"][value="' + tempId + '"]');
            sectionIdInput.value = sectionId;
            
            var sectionData = _this.getSectionData(tempId);
            sectionData.id = sectionId;
            sectionData.isNew = false;
            
            _this.fire("sectionIdChange", {
              from: tempId,
              to: sectionId
            });
          }
          
          var newPageCount = jsonResponse.newPageCount;
          for (var i = 0; i < newPageCount; i++) {
            var newPageId = jsonResponse["newPage." + i + ".id"];
            var tempId = jsonResponse["newPage." + i + ".tempId"];
            var sectionId = jsonResponse["newPage." + i + ".sectionId"];
  
            var pageIdInput = sectionContainer.down('input[name="pageId"][value="' + tempId + '"]');
            pageIdInput.value = newPageId;
  
            var pageData = _this._getPageDataById(tempId);
            pageData.id = newPageId;
            pageData.isNew = false;
            
            var sectionData = _this.getSectionData(sectionId);
            var pages = sectionData.pages;
            for (var spi = 0, spl = pages.length; spi < spl; spi++) {
              if (pages[spi] == tempId)
                pages[spi] = newPageId;
            }
            
            if (_this._currentPageId == tempId)
              _this._currentPageId = newPageId;
            
            _this.fire("pageIdChange", {
              from: tempId,
              to: newPageId
            });
          }
  
          var removedPageCount = jsonResponse.removedPageCount;
          for (var i = 0; i < removedPageCount; i++) {
            var pageId = jsonResponse["removedPage." + i + ".id"];
            _this._removePage(pageId);
          }
  
          var removedSectionCount = jsonResponse.removedSectionCount;
          for (var i = 0; i < removedSectionCount; i++) {
            var sectionId = jsonResponse["removedSection." + i + ".id"];
            _this._removeSection(sectionId);
          }
          
          JSONUtils.showMessages(jsonResponse);
          deleteFormDraftByStrategy(false, "URL");
        }
        
      }
    });
  },
  _getSectionIdFromElement: function (sectionElement) {
    return sectionElement.down('input[name="sectionId"]').value;
  },
  _getSectionElementById: function (sectionId) {
    var sectionIdInput = this._pageListPagesContainer.down('input[name="sectionId"][value="' + sectionId + '"]');
    
    if (sectionIdInput == undefined)
      return undefined;
    
    return sectionIdInput.up(".panelAdminQueryEditorSection");
  },
  _getPageIdFromElement: function (pageElement) {
    return pageElement.down('input[name="pageId"]').value;
  },
  _getPageElementById: function (pageId) {
    var pageIdInput = this._pageListPagesContainer.down('input[name="pageId"][value="' + pageId + '"]');
    
    if (pageIdInput == undefined)
      return undefined;
    
    return pageIdInput.up(".panelAdminQueryEditorPage");
  },
  _onPageClick: function (event) {
    var pageElement = Event.element(event);
    if (!pageElement.hasClassName('panelAdminQueryEditorPage')) {
      pageElement = pageElement.up('.panelAdminQueryEditorPage');
    }
    var pageId = this._getPageIdFromElement(pageElement);
    
    this._commitChanges();
    this._editPage(pageId);
  },
  _onClonePageClick: function (event) {
    Event.stop(event);
    this._commitChanges();
    var queryPage = this.getCurrentPageData();
    var tempId = "temp" + new Date().getTime();
    var pageData = {
      id: tempId,
      isNew: true,
      title: queryPage.title,
      type: queryPage.type,
      options: Object.toJSON(queryPage.options).evalJSON()
    };
    
    var sectionElement = this._getSelectedSectionElement();
    var sectionId = sectionElement.down('input[name="sectionId"]').value;
    var sectionData = this.getSectionData(sectionId);
    
    var sectionPagesElement = sectionElement.down(".panelAdminQueryEditorSectionPages");
    var pages = sectionData.pages;
    if (!pages) {
      sectionData.pages = pages = new Array();
    }
    
    pages.push(tempId);
    this._pageDatas.push(pageData);
    this._addPage(sectionPagesElement, pageData);

    this._commitChanges();
    this._editPage(tempId);
  },
  _onDeletePageClick: function (event) {
    Event.stop(event);

    this._commitChanges();
    
    var element = Event.element(event);
    var pageElement = element.up(".panelAdminQueryEditorPage");
    var pageData = this.getCurrentPageData();
    
    if (pageData.hasAnswers != "true") {
      pageData.isDeleted = (pageData.isDeleted == undefined) ? true : pageData.isDeleted === false;
      
      if (pageData.isDeleted) {
        pageElement.addClassName("panelAdminQueryEditorPageDeleted");
  
        var eventQueue = getGlobalEventQueue();
        eventQueue.addItem(new EventQueueItem(getLocale().getText("panelAdmin.block.query.pageMarkedForDeletion"), {
          className: "eventQueueSuccessItem",
          timeout: 1000 * 3
        }));
      }
      else
        pageElement.removeClassName("panelAdminQueryEditorPageDeleted");
      
      this._editPage(this._currentPageId);
    }
  },
  _onDeleteSectionClick: function (event) {
    Event.stop(event);
    if (this._sectionDatas.length > 1) {
      var element = Event.element(event);
      
      if (element.hasClassName("panelAdminQueryEditorDeleteSectionDisabled"))
        return;
      
      var sectionElement = element.up(".panelAdminQueryEditorSection");
      var sectionId = this._getSectionIdFromElement(sectionElement);
      var sectionData = this.getSectionData(sectionId);
      sectionData.isDeleted = (sectionData.isDeleted == undefined) ? true : sectionData.isDeleted === false;
  
      if (sectionData.isDeleted) {
        this._createPageLink.addClassName('panelAdminQueryEditorCreatePageLinkDisabled');
        sectionElement.addClassName("panelAdminQueryEditorSectionDeleted");
        
        var eventQueue = getGlobalEventQueue();
        eventQueue.addItem(new EventQueueItem(getLocale().getText("panelAdmin.block.query.sectionMarkedForDeletion"), {
          className: "eventQueueSuccessItem",
          timeout: 1000 * 3
        }));
      }
      else {
        this._createPageLink.removeClassName('panelAdminQueryEditorCreatePageLinkDisabled');
        sectionElement.removeClassName("panelAdminQueryEditorSectionDeleted");
      }
      this._editSection(sectionId);
    }
    else {
      var eventQueue = getGlobalEventQueue();
      eventQueue.addItem(new EventQueueItem(getLocale().getText("panelAdmin.block.query.needsOneSection"), {
        className: "eventQueueWarningItem",
        timeout: -1
      }));
    }
  },
  _onCreatePageClick: function (event) {
    var _this = this;
    
    if (Event.element(event).hasClassName("panelAdminQueryEditorCreatePageLinkDisabled"))
      return;
    
    new Ajax.Request('createquerypage.page', {
      parameters: {
        panelId: JSDATA['securityContextId']
      },
      onSuccess : function(response) {
        var dialog = new S2.UI.Dialog({
          title: getLocale().getText('panelAdmin.block.query.createPageDialog.title'),
          zIndex: 2000,
          modal: true,
          content: response.responseText,
          buttons: [{
            label: getLocale().getText('panelAdmin.block.query.createPageDialog.cancelButton'),
            action: function(instance) {
              instance.close(false);
            }
          }]
        });
        
        dialog.toElement().addClassName("panelAdminQueryEditorCreatePageDialog");
        
        dialog.element.observe("ui:dialog:after:open", function (event) {
          var _dialog = event.memo.dialog;
          
          event.memo.dialog.content.select('.panelAdminQueryEditorCreatePagePageTemplate').each(function (element) {
            Event.observe(element, "click", function (event) {
              var element = Event.element(event);
              if (!element.hasClassName("panelAdminQueryEditorCreatePagePageTemplate"))
                element = element.up('.panelAdminQueryEditorCreatePagePageTemplate');
              
              var templateIdInput = element.down('input[name="templateId"]');
              _this._createNewPage(templateIdInput.value);
              _dialog.close(true);
            });
            // TODO page description
            Event.observe(element, "mouseenter", function (event) {
              var element = Event.element(event);
              var templateTitle = element.down('.panelAdminQueryEditorCreatePagePageTemplateName').innerHTML;
              var templateDescription = element.down('input[name="templateDescription"]').value;
              $('templateGuideContainerTitle').innerHTML = templateTitle;
              $('templateGuideContainerDescription').innerHTML = templateDescription;
            });
          }); 
        });

        dialog.element.observe("ui:dialog:before:close", function (event) {
          event.memo.dialog.content.select('.panelAdminQueryEditorCreatePagePageTemplate').invoke("purge");
          event.memo.dialog.element.purge();
        });

        dialog.open();
      },
      onFailure: function () {
        // TODO:
      }
    });
  },
  _onCreateSectionClick: function (event) {
    var id = "temp" + new Date().getTime();
    
    this._addSectionElement(id, '');
    this._sectionDatas.push({
      id: id,
      isNew: true,
      title: '',
      visible: 1
    });

    this._initSortContainers();
    this._commitChanges();
    this._editSection(id);
  },
  _onTabChange: function (event) {
    var from = this._getTabNameFromPanel(event.memo.from.panel);
    var to = this._getTabNameFromPanel(event.memo.to.panel);
    
    this.fire("tabChange", {
      from: from,
      fromPanel: event.memo.from.panel,
      to: to,
      toPanel: event.memo.to.panel
    });
  },
  _onPageEditorTitleChange: function (event) {
    var pageIdInput = this._pageListPagesContainer.down('input[name="pageId"][value="' + this._currentPageId + '"]');
    
    if (pageIdInput) {
      var title = event.newTitle;
      var pageElement = pageIdInput.up(".panelAdminQueryEditorPage");
      pageElement.down('.panelAdminQueryEditorPageTitle').update(title);
      
      this._validate();
    }
  },
  _onSectionTitleClick: function (event) {
    var titleElement = Event.element(event);

    var sectionElement = titleElement;
    if (!sectionElement.hasClassName("panelAdminQueryEditorSection"))
      sectionElement = titleElement.up(".panelAdminQueryEditorSection");
    
    var sectionId = this._getSectionIdFromElement(sectionElement);
    this._commitChanges();
    this._editSection(sectionId);
  },
  _onSectionEditorTitleChange: function (event) {
    var sectionTitle = null;
    
    var sectionIdInputs = this._pageListPagesContainer.select('input[name="sectionId"]');
    for (var i = 0, l = sectionIdInputs.length; i < l; i++) {
      var sectionIdInput = sectionIdInputs[i];
      if (sectionIdInput.value == event.sectionId) {
        sectionTitle = sectionIdInput.up('.panelAdminQueryEditorSection').down('.panelAdminQueryEditorSectionTitle');
        break;
      }
    }
    
    sectionTitle.update(event.newTitle);
    
    this._validate();
  },
  _onQueryNameChange: function (event) {
    this._validate();
  },
  _validate: function() {
    var queryTitle = this.getBlockElement().down('input[name="name"]').value;

    var queryValid = (queryTitle) && (queryTitle != "");
    var sectionsValid = true;
    var pagesValid = true;

    for (var i = 0, l = this._sectionDatas.length; i < l; i++) {
      var sectionData = this._sectionDatas[i];
      var sectionElement = this._getSectionElementById(sectionData.id);
      var sectionElementValidationErrorIndicator = sectionElement.down(".panelAdminQueryEditorSectionValidationError");
      var sectionValid = (sectionData.isDeleted) || ((sectionData.title) && (sectionData.title != ""));
      
      if (!sectionValid) {
        sectionElementValidationErrorIndicator.show();
        sectionsValid = false;
      } else {
        sectionElementValidationErrorIndicator.hide();
      }
    }

    for (var i = 0, l = this._pageDatas.length; i < l; i++) {
      var pageData = this._pageDatas[i];
      if (pageData.isDeleted != true) {
        var pageElement = this._getPageElementById(pageData.id);
        var pageElementValidationErrorIndicator = pageElement.down(".panelAdminQueryEditorPageValidationError");
        var pageValid = ((pageData.title) && (pageData.title != ""));
        
        if (!pageValid) {
          pageElementValidationErrorIndicator.show();
          pagesValid = false;
        } else {
          pageElementValidationErrorIndicator.hide();
        }
      }
    }
    
    if (queryValid)
      $('panelAdminQueryEditorSettingsTabValidationError').hide();
    else
      $('panelAdminQueryEditorSettingsTabValidationError').show();

    if (sectionsValid && pagesValid)
      $('panelAdminQueryEditorPagesTabValidationError').hide();
    else
      $('panelAdminQueryEditorPagesTabValidationError').show();
    
    if (!(pagesValid && sectionsValid && queryValid)) {
      this._saveButton.disable();
      this._saveButton.addClassName("disabledButton");
    } else {
      this._saveButton.enable();
      this._saveButton.removeClassName("disabledButton");
    }
    
    return (pagesValid && sectionsValid && queryValid);
  }
});

Object.extend(QueryEditorBlockController.prototype, fni.events.FNIEventSupport);

addBlockController(new QueryEditorBlockController());

QueryOptionEditor = Class.create({
  initialize: function (pageEditor, caption, name, value) {
    this._editorContainer =  new Element("div", {
      className: 'panelAdminQueryEditorOptionEditorOptionContainer'
    });
    
    this._editorTitle = new Element("div", {
      className: "panelAdminQueryEditorOptionEditorOptionTitle"
    }).update(caption);
    
    this._editorContainer.appendChild(this._editorTitle);

    this._pageEditor = pageEditor;
    this._caption = caption;
    this._name = name;
    this._value = value;
  },
  setup: function () {
  },
  deinitialize: function () {
  },
  disable: function () {
  },
  getDomNode: function () {
    return this._editorContainer;
  },
  getPageEditor: function () {
    return this._pageEditor;
  },
  getBlockController: function () {
    return this.getPageEditor().getBlockController();
  },
  getCaption: function () {
    return this._caption;
  },
  hideCaption: function () {
    this._editorTitle.hide();
  },
  getName: function () {
    return this._name;
  },
  getValue: function () {
    return this._value;
  }
});

Object.extend(QueryOptionEditor.prototype, fni.events.FNIEventSupport);

QueryOptionTextEditor = Class.create(QueryOptionEditor, {
  initialize: function ($super, pageEditor, caption, name, value) {
    $super(pageEditor, caption, name, value);
    
    this._editor = new Element("input", {
      className: "panelAdminQueryEditorOptionEditorOptionEditor",
      name: name,
      value: value||''
    });
    
    this._editorContainer.appendChild(this._editor);
  },
  setup: function ($super) {
    Event.observe(this._editor, "change", function (event) {
      var editorElement = Event.element(event);
      this.fire("valueChange", {
        value: editorElement.value,
        name: this.getName(),
        editorElement: editorElement
      });
    }.bind(this));
  },
  disable: function ($super) {
    this._editor.disable();
  },
  deinitialize: function ($super) {
    $super();
    this._editor.purge();
  }
});

QueryOptionMemoEditor = Class.create(QueryOptionEditor, {
  initialize: function ($super, pageEditor, caption, name, value) {
    $super(pageEditor, caption, name, value);
    
    this._editor = new Element("textarea", {
      className: "panelAdminQueryEditorOptionEditorOptionEditor",
      name: name
    }).update(value||'');
    
    this._editorContainer.appendChild(this._editor);
  },
  setup: function ($super) {
    Event.observe(this._editor, "change", function (event) {
      var editorElement = Event.element(event);
      this.fire("valueChange", {
        value: editorElement.value,
        name: this.getName(),
        editorElement: editorElement
      });
    }.bind(this));
  },
  disable: function ($super) {
    this._editor.disable();
  },
  deinitialize: function ($super) {
    $super();
    this._editor.purge();
  }
});

QueryOptionFloatEditor = Class.create(QueryOptionEditor, {
  initialize: function ($super, pageEditor, caption, name, value) {
    $super(pageEditor, caption, name, value);
    
    this._editor = new Element("input", {
      className: "panelAdminQueryEditorOptionEditorOptionEditor float",
      name: name,
      value: value === undefined ? '' : value
    });
    this._editor._oldValue = this._editor.value;
    
    this._editorContainer.appendChild(this._editor);
  },
  setup: function ($super) {
    initializeElementValidation(this._editor);
    
    Event.observe(this._editor, "change", function (event) {
      var editorElement = Event.element(event);
      if (editorElement.hasClassName("invalid")) {
        editorElement.value = editorElement._oldValue; 
        editorElement.validate();
      } else {
        if (!this.fire("valueChange", {
            value: editorElement.value,
            name: this.getName(),
            editorElement: editorElement
          })) {
          editorElement.value = editorElement._oldValue; 
        } else {
          editorElement._oldValue = editorElement.value; 
        }
      }
    }.bind(this));
  },
  disable: function ($super) {
    this._editor.disable();
  },
  deinitialize: function ($super) {
    deinitializeValidation(this._editor);
    $super();
    this._editor.purge();
  }
});

QueryOptionIntegerEditor = Class.create(QueryOptionEditor, {
  initialize: function ($super, pageEditor, caption, name, value) {
    $super(pageEditor, caption, name, value);
    
    this._editor = new Element("input", {
      className: "panelAdminQueryEditorOptionEditorOptionEditor number",
      name: name,
      value: value === undefined ? '' : value
    });
    
    this._editorContainer.appendChild(this._editor);
  },
  setup: function ($super) {
    Event.observe(this._editor, "change", function (event) {
      var editorElement = Event.element(event);
      this.fire("valueChange", {
        value: editorElement.value,
        name: this.getName(),
        editorElement: editorElement
      });
    }.bind(this));
  },
  disable: function ($super) {
    this._editor.disable();
  },
  deinitialize: function ($super) {
    $super();
    this._editor.purge();
  }
});

QueryOptionBooleanEditor = Class.create(QueryOptionEditor, {
  initialize: function ($super, pageEditor, caption, name, value) {
    $super(pageEditor, caption, name, value);
    
    this._editor = new Element("input", {
      type: "checkbox",
      className: "panelAdminQueryEditorOptionEditorOptionEditor",
      name: name,
      value: '1'
    });
    
    if (value == '1') 
      this._editor.setAttribute("checked", "checked");
    
    this._editorContainer.appendChild(this._editor);
  },
  setup: function ($super) {
    Event.observe(this._editor, "change", function (event) {
      var editorElement = Event.element(event);
      this.fire("valueChange", {
        value: editorElement.value,
        name: this.getName(),
        editorElement: editorElement
      });
    }.bind(this));
  },
  disable: function ($super) {
    this._editor.disable();
  },
  deinitialize: function ($super) {
    $super();
    this._editor.purge();
  }
});

QueryOptionTimeSerieDataEditor = Class.create(QueryOptionEditor, {
  initialize: function ($super, pageEditor, caption, name, value) {
    $super(pageEditor, caption, name, value);
    
    this._values = QueryEditorUtils.parseSerializedMap(value);
    this._minX = parseInt(this.getBlockController().getPageOptionValue("time_serie.minX"));
    this._maxX = parseInt(this.getBlockController().getPageOptionValue("time_serie.maxX"));
    this._stepX = parseFloat(this.getBlockController().getPageOptionValue("time_serie.stepX")||1);
    
    this._serializedData = new Element("input", {
      type: 'hidden',
      name: name,
      value: value
    });
    
    this._disabled = false;
    this._editorContainer.appendChild(this._serializedData);
  },
  disable: function ($super) {
    this._disabled = true;
    this.draw();
  },
  setup: function ($super) {
    this.draw();
    this.getPageEditor().addListener("optionEditorValueChange", this, this._onPageEditorValueChange);
  },
  deinitialize: function ($super) {
    $super();
    this.getPageEditor().removeListener("optionEditorValueChange", this);
  },
  setMinX: function (minX) {
    this._minX = parseInt(minX);
  },
  setMaxX: function (maxX) {
    this._maxX = parseInt(maxX);
  },
  setStepX: function (stepX) {
    this._stepX = parseFloat(stepX);
  },
  draw: function () {
    if (this._dataGrid) {
      this._dataGrid.deinitialize();
    }

    var columns = new Array();
    var columnsCount = Math.ceil((this._maxX - this._minX) / this._stepX) + 1;
    var columnWidthPct = 100 / columnsCount;

    for (var columnIndex = 0; columnIndex < columnsCount; columnIndex++) {
      var columnYear = parseFloat(this._minX + (columnIndex * this._stepX));
      
      columns.push({
        header : columnYear,
        left : columnWidthPct * columnIndex,
        width: columnWidthPct,
        measurementUnit: '%',
        dataType : 'number',
        editable: true,
        paramName: columnIndex
      });
    }

    var columnValues = new Array();

    for (var columnIndex = 0; columnIndex < columnsCount; columnIndex++) {
      var columnX = parseFloat(this._minX + (columnIndex * this._stepX));
      
      var columnValue = '';
      for (var valueX in this._values) {
        var value = this._values[valueX];
        if (parseFloat(valueX) == parseFloat(columnX)) {
          columnValue = value;
          break;
        };
      }
      
      columnValues.push(columnValue);
    }  
    
    this._dataGrid = new DataGrid(this._editorContainer, {
      id : this._name,
      columns : columns    
    });
    
    this._dataGrid.addListener("cellValueChange", this, this._onDataGridCellValueChange);
    this._dataGrid.addRows([columnValues]);
    this._serialize();
    
    if (this._disabled) {
      for (var i = this._dataGrid.getRowCount() - 1; i >= 0; i--) {
        this._dataGrid.disableRow(i);
      }
    }
  },
  _serialize: function () {
    var columnsCount = Math.ceil((this._maxX - this._minX) / this._stepX) + 1;
    
    var serializedData = new Hash();
    
    for (var columnIndex = 0; columnIndex < columnsCount; columnIndex++) {
      var columnX = parseFloat(this._minX + (columnIndex * this._stepX));
      var value = this._dataGrid.getCellValue(0, columnIndex);
      serializedData.set(columnX, value);
    }  

    this._serializedData.value = serializedData.toQueryString();
  },
  _onDataGridCellValueChange: function (event) {
    this._serialize();
    // TODO: this does not seem like a good idea...
    this.getPageEditor().fire("optionEditorValueChange", {
      value: event.value,
      name: event.dataGridComponent.getId(),
      dataGridComponent: event.dataGridComponent
    });
  },
  _onPageEditorValueChange: function (event)  {
    var redraw = false;
    
    switch (event.name) {
      case 'time_serie.minX':
        this.setMinX(event.value);
        redraw = true;
      break;
      case 'time_serie.maxX':
        this.setMaxX(event.value);
        redraw = true;
      break;
      case 'time_serie.stepX':
        this.setStepX(event.value);
        redraw = true;
      break;
    }
    
    if (redraw) {
      this.draw();
    }
  }
});

QueryOptionOptionSetEditor = Class.create(QueryOptionEditor, {
  initialize: function ($super, pageEditor, caption, name, value) {
    $super(pageEditor, caption, name, value);
    
    this._addOptionLinkClickListener = this._onAddOptionLinkClick.bindAsEventListener(this);
    
    this._addOptionLink = new Element("a", {
      className: "panelAdminQueryEditorOptionSetOptionEditorAddOptionLink",
      href: 'javascript:void(null)'
    }).update(getLocale().getText('panelAdmin.block.query.optionSetEditor.addOptionLabel'));

    this._editorContainer.appendChild(this._addOptionLink);
    
    var _this = this;
    
    this._dataGrid = new DataGrid(this._editorContainer, {
      id : this._name,
      columns : [{
        left : 0,
        right: 30,
        measurementUnit: 'px',
        dataType : 'text',
        editable: true,
        paramName: 'name'
      }, {
        right : 0,
        width: 25,
        measurementUnit: 'px',
        dataType : 'button',
        imgsrc: THEMEPATH + '/gfx/icons/16x16/actions/fileclose.png',
        tooltip: getLocale().getText('panelAdmin.block.query.optionSetEditor.removeOptionTooltip'),
        onclick: function (event) {
          event.dataGridComponent.deleteRow(event.row);
          var serializedValue = _this._serialize();
          // TODO: this does not seem like a good idea...
          _this.getPageEditor().fire("optionEditorValueChange", {
            value: serializedValue,
            name: event.dataGridComponent.getId(),
            dataGridComponent: event.dataGridComponent
          });
        }
      }]   
    });
    
    this._serializedData = new Element("input", {
      type: 'hidden',
      name: name,
      value: value
    });
    
    this._editorContainer.appendChild(this._serializedData);
    
    var list = QueryEditorUtils.parseSerializedList(value);
    for (var i = 0, l = list.length; i < l; i++) {
      this._dataGrid.addRow([list[i], null]); 
    }
  },
  setup: function ($super) {
    Event.observe(this._addOptionLink, "click", this._addOptionLinkClickListener);
    this._dataGrid.addListener("cellValueChange", this, this._onDataGridCellValueChange);
  },
  deinitialize: function ($super) {
    $super();
    Event.stopObserving(this._addOptionLink, "click", this._addOptionLinkClickListener);
    this._dataGrid.removeListener("cellValueChange", this);
  },
  disable: function ($super) {
    for (var i = this._dataGrid.getRowCount() - 1; i >= 0; i--) {
      this._dataGrid.disableRow(i);
    }
    
    this._addOptionLink.hide();
  },
  _serialize: function () {
    var list = new Array();
    
    var rowCount = this._dataGrid.getRowCount();
    for (var rowIndex = 0; rowIndex < rowCount; rowIndex++) {
      var value = this._dataGrid.getCellValue(rowIndex, 0);
      list.push(value);
    } 

    return this._serializedData.value = QueryEditorUtils.serializeList(list);
  },
  _onAddOptionLinkClick: function (event) {
    this._dataGrid.addRow(['', null]);
  },
  _onDataGridCellValueChange: function (event) {
    var serializedValue = this._serialize();
    // TODO: this does not seem like a good idea
    this.getPageEditor().fire("optionEditorValueChange", {
      value: serializedValue,
      name: event.dataGridComponent.getId(),
      dataGridComponent: event.dataGridComponent
    });
  }
});

QueryOptionScale1DEditor = Class.create(QueryOptionEditor, {
  initialize: function ($super, pageEditor, caption, name, value) {
    $super(pageEditor, caption, name, value);
    
    this._editor = new Element("select", {
      className: "panelAdminQueryEditorOptionEditorOptionEditor",
      name: name
    });

    var radioOption = new Element("option", {
      value: 0      
    }).update(getLocale().getText('panelAdmin.block.query.scale1DOptionEditor.typeRadioLabel'));
    
    var sliderOption = new Element("option", {
      value: 1
    }).update(getLocale().getText('panelAdmin.block.query.scale1DOptionEditor.typeSliderLabel'));

    this._editor.appendChild(radioOption);
    
    this._editor.appendChild(sliderOption);
    
    if (value == 1) {
      sliderOption.setAttribute("selected", "selected");
    } else {
      radioOption.setAttribute("selected", "selected");
    }
    
    this._editorContainer.appendChild(this._editor);
  },
  setup: function ($super) {
    $super();

    Event.observe(this._editor, "change", function (event) {
      var editorElement = Event.element(event);
      this.fire("valueChange", {
        value: editorElement.value,
        name: this.getName(),
        editorElement: editorElement
      });
    }.bind(this));
  },
  deinitialize: function ($super) {
    $super();
    this._editor.purge();
  },
  disable: function ($super) {
    this._editor.disable();
  }
});

QueryOptionTimelineTypeEditor = Class.create(QueryOptionEditor, {
  initialize: function ($super, pageEditor, caption, name, value) {
    $super(pageEditor, caption, name, value);
    
    this._editor = new Element("select", {
      className: "panelAdminQueryEditorOptionEditorOptionEditor",
      name: name
    });

    var option1 = new Element("option", {
      value: 0      
    }).update(getLocale().getText('panelAdmin.block.query.timelineTypeEditor.type1Value'));
    this._editor.appendChild(option1);
    
    var option2 = new Element("option", {
      value: 1
    }).update(getLocale().getText('panelAdmin.block.query.timelineTypeEditor.type2Value'));
    this._editor.appendChild(option2);
    
    if (value == 1) {
      option2.setAttribute("selected", "selected");
    } else {
      option1.setAttribute("selected", "selected");
    }
    
    this._editorContainer.appendChild(this._editor);
  },
  setup: function ($super) {
    $super();

    Event.observe(this._editor, "change", function (event) {
      var editorElement = Event.element(event);
      this.fire("valueChange", {
        value: editorElement.value,
        name: this.getName(),
        editorElement: editorElement
      });
    }.bind(this));
  },
  deinitialize: function ($super) {
    $super();
    this._editor.purge();
  },
  disable: function ($super) {
    this._editor.disable();
  }
});

QueryOptionScale2DEditor = Class.create(QueryOptionEditor, {
  initialize: function ($super, pageEditor, caption, name, value) {
    $super(pageEditor, caption, name, value);
    
    this._editor = new Element("select", {
      className: "panelAdminQueryEditorOptionEditorOptionEditor",
      name: name
    });
    
    var radioOption = new Element("option", {
      value: 0      
    }).update(getLocale().getText('panelAdmin.block.query.scale2DOptionEditor.typeRadioLabel'));
    
    var sliderOption = new Element("option", {
      value: 1
    }).update(getLocale().getText('panelAdmin.block.query.scale2DOptionEditor.typeSliderLabel'));
    
    var graphOption = new Element("option", {
      value: 2
    }).update(getLocale().getText('panelAdmin.block.query.scale2DOptionEditor.typeGraphLabel'));

    this._editor.appendChild(radioOption);
    this._editor.appendChild(sliderOption);
    this._editor.appendChild(graphOption);
    
    if (value == 1) {
      sliderOption.setAttribute("selected", "selected");
    } else if (value == 2) {
      graphOption.setAttribute("selected", "selected");
    } else {
      radioOption.setAttribute("selected", "selected");
    }
    
    this._editorContainer.appendChild(this._editor);
  },
  setup: function ($super) {
    $super();
    
    Event.observe(this._editor, "change", function (event) {
      var editorElement = Event.element(event);
      this.fire("valueChange", {
        value: editorElement.value,
        name: this.getName(),
        editorElement: editorElement
      });
    }.bind(this));
  },
  deinitialize: function ($super) {
    $super();
    this._editor.purge();
  },
  disable: function ($super) {
    this._editor.disable();
  }
});

QueryOptionHiddenEditor = Class.create(QueryOptionEditor, {
  initialize: function ($super, pageEditor, caption, name, value) {
    $super(pageEditor, caption, name, value);
    
    this._editor = new Element("input", {
      className: "panelAdminQueryEditorOptionEditorOptionEditor",
      type: 'hidden',
      name: name,
      value: value||''
    });
    
    this._editorContainer.appendChild(this._editor);
  },
  replaceEditor: function (newEditor) {
    this._editor.purge();
    this._editor.replace(newEditor);
    
    Event.observe(this._editor, "change", function (event) {
      var editorElement = Event.element(event);
      this.fire("valueChange", {
        value: editorElement.value,
        name: this.getName(),
        editorElement: editorElement
      });
    }.bind(this));
  },
  setValue: function (value) {
    this._editor.value = value;
    this.fire("valueChange", {
      value: value,
      name: this.getName(),
      editorElement: this._editor
    });
  },
  getEditor: function () {
    return this._editor;
  },
  setup: function ($super) {
    Event.observe(this._editor, "change", function (event) {
      var editorElement = Event.element(event);
      this.fire("valueChange", {
        value: editorElement.value,
        name: this.getName(),
        editorElement: editorElement
      });
    }.bind(this));
  },
  disable: function ($super) {
    this._editor.disable();
  },
  deinitialize: function ($super) {
    $super();
    this._editor.purge();
  }
});

QueryEditorQuestionPreview = Class.create({
  initialize: function (pageEditor, container) {
    this._pageEditor = pageEditor;
    this._container = container;
  },
  deinitialize: function () {
    
  },
  setup: function ($super) {
    
  },
  getPageEditor: function () {
    return this._pageEditor;
  },
  getBlockController: function () {
    return this.getPageEditor().getBlockController();
  }
});

QueryEditorTimeSerieQuestionPreview = Class.create(QueryEditorQuestionPreview, {
  initialize: function ($super, pageEditor, container) {
    $super(pageEditor, container);
    
    this.getBlockController().addListener("tabChange", this, this._onBlockControllerTabChange);
    
    this._flotrContainer = new Element("div", {
      className: "queryEditorQuestionTimeSerieContainer"
    });
    
    pageEditor.addListener("optionEditorValueChange", this, this._onPageEditorOptionValueChange);
    container.appendChild(this._flotrContainer);
   
    this._maxTicks = 100;
    this._maxY = parseInt(this.getBlockController().getPageOptionValue("time_serie.maxY"));
    this._maxX = parseInt(this.getBlockController().getPageOptionValue("time_serie.maxX"));
    this._minY = parseInt(this.getBlockController().getPageOptionValue("time_serie.minY"));
    this._minX = parseInt(this.getBlockController().getPageOptionValue("time_serie.minX"));
    this._stepX = parseFloat(this.getBlockController().getPageOptionValue("time_serie.stepX")||1);
    this._stepY = parseInt(this.getBlockController().getPageOptionValue("time_serie.stepY")||1);
    this._userStepX = parseFloat(this.getBlockController().getPageOptionValue("time_serie.userStepX")||this._stepX);
    this._yTickDecimals = parseInt(this.getBlockController().getPageOptionValue("time_serie.yTickDecimals")||1);
    this._xAxisTitle = this.getBlockController().getPageOptionValue("time_serie.xAxisTitle");
    this._yAxisTitle = this.getBlockController().getPageOptionValue("time_serie.yAxisTitle");
    this._predefinedSetLabel = this.getBlockController().getPageOptionValue("time_serie.predefinedSetLabel");
    this._userSetLabel = this.getBlockController().getPageOptionValue("time_serie.userSetLabel");
    
    yTicks = Math.round((this._maxY - this._minY) / this._stepY);
    xTicks = Math.round((this._maxX - this._minX) / Math.getGCD(this._stepX, this._userStepX));
    
    this._flotrOptions = {
      "xaxis": { 
        min: this._minX, 
        max: this._maxX,
        tickDecimals: 0,
        noTicks: xTicks,
        title: this._xAxisTitle
      },
      "yaxis" : {
        min : this._minY,
        max : this._maxY,
        tickDecimals: this._yTickDecimals,
        noTicks: yTicks,
        title: this._yAxisTitle
      },
      "legend" : {
        "noColumns": 1,
        "position" : "ne"
      }
    };
    
    this._dataSerie = new Array();

    var serializedValues = this.getBlockController().getPageOptionValue('time_serie.predefinedValues');
    var columnsCount = Math.ceil((this._maxX - this._minX) / this._stepX) + 1;

    var values = QueryEditorUtils.parseSerializedMap(serializedValues);

    for (var columnIndex = 0; columnIndex < columnsCount; columnIndex++) {
      var columnX = parseFloat(this._minX + (columnIndex * this._stepX));
      var columnValue = NaN;
      for (var valueX in values) {
        var value = values[valueX];
        if (parseFloat(valueX) == parseFloat(columnX)) {
          columnValue = parseFloat(value);
          break;
        };
      }
      
      this._dataSerie.push([columnX, columnValue]);
    }
  },
  deinitialize: function ($super) {
    $super();
    
    this.getBlockController().removeListener("tabChange", this);
    this.getPageEditor().removeListener("optionEditorValueChange", this);

    this._flotrContainer.purge();
    this._flotrContainer.remove();
  },
  setup: function ($super) {
    $super();
    
    this._render();
  },
  _render: function() {
    this._userDataSerie = new Array();
    
    var x = null;
    var y = null;
    if (this._dataSerie) {
      var i = this._dataSerie.length - 1;
      while (i >= 0) {
        var lastPredefined = this._dataSerie[i];
        var value = parseFloat(lastPredefined[1]);
        
        if (!isNaN(value)) {
          x = parseFloat(lastPredefined[0]);
          y = value;
          break;
        }
        
        i--;
      }
    }
    
    if ((x === null)||(y === null)) {
      x = this._minX;
      y = this._minY;
    }
    
    while (x <= this._maxX) {
      this._userDataSerie.push([ x,  y ]);
      x += this._userStepX;
      if (x > this._maxX) {
        this._userDataSerie.push([ this._maxX,  y ]);
        break;
      }
    }

    this._userDataSerie = this._userDataSerie.sort(function(a, b){ return a[0] - b[0]; });
    
    this._flotr = Flotr.draw(this._flotrContainer, [ {
      // color: "blue",
      data : this._dataSerie,
      label : this._predefinedSetLabel,
      lines : {
        show : true,
        fill : false
      },
      points : {
        "show" : true,
        "radius" : 3,
        "lineWidth" : 2,
        "fill" : true,
        "fillColor" : "#FFFFFF",
        "fillOpacity" : 0.4
      }
    }, {
      // color: "blue",
      data : this._userDataSerie,
      label : this._userSetLabel,
      lines : {
        show : true,
        fill : false
      },
      points : {
        "show" : true,
        "radius" : 3,
        "lineWidth" : 2,
        "fill" : true,
        "fillColor" : "#FFFFFF",
        "fillOpacity" : 0.4
      }
    }], this._flotrOptions);
  },
  _onPageEditorOptionValueChange: function (event) {
    var render = false;
    
    switch (event.name) {
      case 'time_serie.minX':
        this._flotrOptions.xaxis.min = this._minX = parseInt(event.value);
        render = true;
      break;
      case 'time_serie.maxX':
        this._flotrOptions.xaxis.max = this._maxX = parseInt(event.value);
        render = true;
      break;
      case 'time_serie.stepX':
        var stepX = parseFloat(event.value.replace(",","."));

        if (stepX) {
          var noTicks = Math.round((this._maxX - this._minX) / Math.getGCD(stepX, this._userStepX||stepX));
          if (noTicks > 0) {
            if (noTicks <= this._maxTicks) {
              this._stepX = stepX;
              if (!this._userStepX)
                this._userStepX = stepX;

              this._flotrOptions.xaxis.noTicks = noTicks;
              render = true;
            } else {
              event.stop();
              getGlobalEventQueue().addItem(new EventQueueItem(getLocale().getText('panelAdmin.block.query.timelineTypeEditor.tooManyTicksError', noTicks, this._maxTicks), {
                className: "eventQueueWarningItem",
                timeout: 1000 * 3
              }));
            }
          } else {
            event.stop();
            getGlobalEventQueue().addItem(new EventQueueItem(getLocale().getText('panelAdmin.block.query.timelineTypeEditor.noTicksError'), {
              className: "eventQueueWarningItem",
              timeout: 1000 * 3
            }));
          }
        
        } else {
          event.stop();
          getGlobalEventQueue().addItem(new EventQueueItem(getLocale().getText('panelAdmin.block.query.timelineTypeEditor.zeroStepError'), {
            className: "eventQueueWarningItem",
            timeout: 1000 * 3
          }));
        }
      break;
      case 'time_serie.minY':
        this._flotrOptions.yaxis.min = this._minY = parseInt(event.value);
        render = true;
      break;
      case 'time_serie.userStepX':
        var userStepX = parseFloat(event.value.replace(",","."));

        if (userStepX) {
          var noTicks = Math.round((this._maxX - this._minX) / Math.getGCD(this._stepX, userStepX));
          if (noTicks > 0) {
            if (noTicks <= this._maxTicks) {
              this._userStepX = userStepX;
              this._flotrOptions.xaxis.noTicks = noTicks;
              render = true;
            } else {
              event.stop();
              getGlobalEventQueue().addItem(new EventQueueItem(getLocale().getText('panelAdmin.block.query.timelineTypeEditor.tooManyTicksError', noTicks, this._maxTicks), {
                className: "eventQueueWarningItem",
                timeout: 1000 * 3
              }));
            }
          } else {
            event.stop();
            getGlobalEventQueue().addItem(new EventQueueItem(getLocale().getText('panelAdmin.block.query.timelineTypeEditor.noTicksError'), {
              className: "eventQueueWarningItem",
              timeout: 1000 * 3
            }));
          }
        
        } else {
          event.stop();
          getGlobalEventQueue().addItem(new EventQueueItem(getLocale().getText('panelAdmin.block.query.timelineTypeEditor.zeroStepError'), {
            className: "eventQueueWarningItem",
            timeout: 1000 * 3
          }));
        }
      break;
      case 'time_serie.maxY':
        this._flotrOptions.yaxis.max = this._maxY = parseInt(event.value);
        render = true;
      break;
      case 'time_serie.stepY':
        this._stepY = parseInt(event.value);
        this._flotrOptions.yaxis.noTicks = Math.round((this._maxY - this._minY) / this._stepY);
        render = true;
      break;
      case 'time_serie.yTickDecimals':
        this._flotrOptions.yaxis.tickDecimals = this._yTickDecimals = parseInt(event.value);
        render = true;
      break;
      case 'time_serie.xAxisTitle':
        this._flotrOptions.xaxis.title = this._xAxisTitle = event.value;
        render = true;
      break;
      case 'time_serie.yAxisTitle':
        this._flotrOptions.yaxis.title = this._yAxisTitle = event.value;
        render = true;
      break;
      case 'time_serie.predefinedSetLabel':
        this._predefinedSetLabel = event.value;
        render = true;
      break;
      case 'time_serie.userSetLabel':
        this._userSetLabel = event.value;
        render = true;
      break;
      case 'time_serie.predefinedValues':
        this._dataSerie = new Array();
        
        var dataGridComponent = event.dataGridComponent;
        for (var columnIndex = 0, columnsCount = dataGridComponent.getColumnCount(); columnIndex < columnsCount; columnIndex++) {
          var columnX = parseFloat(this._minX + (columnIndex * this._stepX));
          var value = parseFloat(dataGridComponent.getCellValue(0, columnIndex));
          this._dataSerie.push([columnX, value]); 
        }
        
        render = true;
      break;
    }
    
    if (render) {
      this._render();
    }
  },
  _onBlockControllerTabChange: function (event) {
    if (event.to == 'PAGES') {
      event.toPanel.removeClassName('ui-tabs-hide');
      
      this._render();
    }
  }
});

QueryEditorTimelineQuestionPreview = Class.create(QueryEditorQuestionPreview, {
  initialize: function ($super, pageEditor, container) {
    $super(pageEditor, container);
    
    this._min = parseInt(this.getBlockController().getPageOptionValue("timeline.min"));
    this._max = parseInt(this.getBlockController().getPageOptionValue("timeline.max"));
    this._type = parseInt(this.getBlockController().getPageOptionValue("timeline.type"));
    this._value1Label = this.getBlockController().getPageOptionValue("timeline.value1Label");
    this._value2Label = this.getBlockController().getPageOptionValue("timeline.value2Label");
    
    pageEditor.addListener("optionEditorValueChange", this, this._onPageEditorOptionValueChange);
  },
  deinitialize: function ($super) {
    $super();
    this._container.update('');
    this.getPageEditor().removeListener("optionEditorValueChange", this);
  },
  setup: function ($super) {
    $super();
    this._draw();
  },
  _draw: function() {
    if (this._slider) {
      this._slider.destroy();
      this._slider = undefined;
    }
    
    this._container.update('');

    var minElement = new Element("label", {
    	className: "queryPreviewTimelineMin"
    }).update(this._min); 
    this._container.appendChild(minElement);

    var maxElement = new Element("label", {
    	className: "queryPreviewTimelineMax"
    }).update(this._max); 
    this._container.appendChild(maxElement);

    var value1LabelElement = new Element("label", {
    	className: "queryPreviewTimelineValue1Label"
    }).update(this._value1Label);
    this._container.appendChild(value1LabelElement);
    
    var sliderTrack = new Element("div");
    this._container.appendChild(sliderTrack);
    
    if (this._type == 1) {
      var value2LabelElement = new Element("label", {
        className: "queryPreviewTimelineValue2Label"
      }).update(this._value2Label);
      this._container.appendChild(value2LabelElement);
    }    
    
    new S2.UI.Slider(sliderTrack, {
      value: {
        initial: this._min + ((this._max - this._min) / 2),
        min: this._min,
        max: this._max
      },
      disabled: true
    });
  },
  _onPageEditorOptionValueChange: function (event) {
    var redraw = false;
    switch (event.name) {
      case 'timeline.type':
        this._type = parseInt(event.value);
        redraw = true;
      break;
      case 'timeline.min':
        this._min = parseInt(event.value);
        redraw = true;
      break;
      case 'timeline.max':
        this._max = parseInt(event.value);
        redraw = true;
      break;
      case 'timeline.value1Label':
        this._value1Label = event.value;
        redraw = true;
      break;
      case 'timeline.value2Label':
        this._value2Label = event.value;
        redraw = true;
      break;
    }
    if (redraw) {
      this._draw();
    }
  }
});

QueryEditorAbstractScaleQuestionPreview = Class.create(QueryEditorQuestionPreview, {
  initialize: function ($super, pageEditor, container) {
    $super(pageEditor, container);
  },
  _drawRadio: function (container, label, options) {
    var labelElement = new Element("label").update(label);
    container.appendChild(labelElement);
    
    for (var i = 0, l = options.length; i < l; i++) {
      var itemContainer = new Element("div", {
        className: "queryEditorQuestionScaleRadioPreviewItemContainer"
      });
      
      var inputContainer = new Element("div", {
        className: "queryEditorQuestionScaleRadioPreviewInputContainer"
      });
      
      var labelContainer = new Element("div", {
        className: "queryEditorQuestionScaleRadioPreviewLabelContainer"
      }).update(options[i]);
      
      var input = new Element("input", {
        type: "radio",
        disabled: 'disabled'
      });
      
      inputContainer.appendChild(input);
      itemContainer.appendChild(labelContainer);
      itemContainer.appendChild(inputContainer);
            
      this._container.appendChild(itemContainer);
    }
  },
  _drawSlider: function (container, label, options) {
    var labelElement = new Element("label", {
      className: "queryEditorQuestionScaleSliderLabel"
    }).update(label);
    container.appendChild(labelElement);
    
    var sliderTrack = new Element("div");
    var possibleValues = new Array();
    container.appendChild(sliderTrack);
    
    var trackDimensions = sliderTrack.getDimensions();
    var labelStep = trackDimensions.width / (options.length - 1); 
    
    for (var i = 0, l = options.length; i < l; i++) {
      possibleValues.push(i);
      
      var label = new Element("label", {
        className: "queryEditorQuestionScaleSliderPreviewLabel"
      }).update(options[i]);
      this._container.appendChild(label);
   
      if (i == 0) {
        // First label is always in far left
        label.setStyle({
          left: '0px'
        });  
      } else if (i == (options.length - 1)) {
        // ...and last in far right
	      label.setStyle({
	        right: '0px'
	      });  
      } else {
        // others can be centered
        var labelWidth = label.getLayout().get("width");
        var left = (labelStep * i) - (labelWidth / 2);

  	    label.setStyle({
  	      left: left + 'px'
  	    });  
      }
    }
    
    var minValue = 0;
    var maxValue = options.length - 1;
    var initialValue = Math.floor((maxValue - minValue) / 2);
    
    var slider = new S2.UI.Slider(sliderTrack, {
      value: {
        initial: initialValue,
        min: minValue,
        max: maxValue
      },
      disabled: true,
      possibleValues: possibleValues
    });
    
    return slider;
  }
});

QueryEditorScale1DQuestionPreview = Class.create(QueryEditorAbstractScaleQuestionPreview, {
  initialize: function ($super, pageEditor, container) {
    $super(pageEditor, container);
    
    this._options = QueryEditorUtils.parseSerializedList(this.getBlockController().getPageOptionValue("scale1d.options"));
    this._type = this.getBlockController().getPageOptionValue("scale1d.type");
    this._label = this.getBlockController().getPageOptionValue("scale1d.label");
    
    pageEditor.addListener("optionEditorValueChange", this, this._onPageEditorOptionValueChange);
  },
  deinitialize: function ($super) {
    $super();
    
    this._container.update('');
    this.getPageEditor().removeListener("optionEditorValueChange", this);
  },
  setup: function ($super) {
    $super();
    
    this._draw();
  },
  _draw: function() {
    if (this._slider) {
      this._slider.destroy();
      this._slider = undefined;
    }
    
    this._container.update('');

    switch (this._type) {
      case '0':
        this._drawRadio(this._container, this._label, this._options);
      break;
      case '1':
        this._slider = this._drawSlider(this._container, this._label, this._options);
      break;
    }
  },
  _onPageEditorOptionValueChange: function (event) {
    switch (event.name) {
      case 'scale1d.options':
        this._options = QueryEditorUtils.parseSerializedList(event.value);
        this._draw();
      break;
      case 'scale1d.type':
        this._type = event.value;
        this._draw();
      break;
      case 'scale1d.label':
        this._label = event.value;
        this._draw();
      break;
    }
  }
});

QueryEditorScale2DQuestionPreview = Class.create(QueryEditorAbstractScaleQuestionPreview, {
  initialize: function ($super, pageEditor, container) {
    $super(pageEditor, container);
    
    this._optionsX = QueryEditorUtils.parseSerializedList(this.getBlockController().getPageOptionValue("scale2d.options.x"));
    this._optionsY = QueryEditorUtils.parseSerializedList(this.getBlockController().getPageOptionValue("scale2d.options.y"));
    this._labelX = this.getBlockController().getPageOptionValue("scale2d.label.x");
    this._labelY = this.getBlockController().getPageOptionValue("scale2d.label.y");
    this._type = this.getBlockController().getPageOptionValue("scale2d.type");

    pageEditor.addListener("optionEditorValueChange", this, this._onPageEditorOptionValueChange);
  },
  deinitialize: function ($super) {
    $super();
    
    this._container.update('');
    this.getPageEditor().removeListener("optionEditorValueChange", this);
  },
  setup: function ($super) {
    $super();
    
    this._draw();
  },
  _draw: function() {
    if (this._sliderX) {
      this._sliderX.destroy();
      this._sliderX = undefined;
    }
    
    if (this._sliderY) {
      this._sliderY.destroy();
      this._sliderY = undefined;
    }
    
    if (this._flotr) {
      this._flotr = undefined;
    }
    
    this._container.update('');

    switch (this._type) {
      case '0':
        this._drawRadio(this._container, this._labelX, this._optionsX);
        this._drawRadio(this._container, this._labelY, this._optionsY);
      break;
      case '1':
        this._sliderX = this._drawSlider(this._container, this._labelX, this._optionsX);
        this._sliderY = this._drawSlider(this._container, this._labelY, this._optionsY);
      break;
      case '2':
        this._flotr = this._drawGraph(this._container, this._labelX, this._labelY, this._optionsX, this._optionsY);
      break;
    }
  },
  _drawGraph: function (container, labelX, labelY, optionsX, optionsY) {
    var flotrContainer = new Element("div", {
      className: "queryEditorQuestionScale2DGraphContainer"
    });
    container.appendChild(flotrContainer);
    
    var minX = Infinity;
    var maxX = -Infinity;
    var minY = Infinity;
    var maxY = -Infinity;
    
    var xTicks = new Array();
    var yTicks = new Array();
    
    for (var i = 0, l = optionsX.length; i < l; i++) {
      var text = optionsX[i];
      xTicks.push([i, text]);
      minX = Math.min(minX, i);
      maxX = Math.max(maxX, i);
    }
    
    for (var i = 0, l = optionsY.length; i < l; i++) {
      var text = optionsY[i];
      yTicks.push([i, text]);
      minY = Math.min(minY, i);
      maxY = Math.max(maxY, i);
    }
    
    var flotrOptions = {
      "xaxis": { 
        min: minX, 
        max: maxX,
        title: labelX,
        ticks: xTicks
      },
      "yaxis" : {
        min : minY,
        max : maxY,
        title: labelY,
        ticks: yTicks
      }
    };
    
    return Flotr.draw(flotrContainer, [ {
      data : []
    }], flotrOptions);
  },
  _onPageEditorOptionValueChange: function (event) {
    switch (event.name) {
      case 'scale2d.options.x':
        this._optionsX = QueryEditorUtils.parseSerializedList(event.value);
        this._draw();
      break;
      case 'scale2d.options.y':
        this._optionsY = QueryEditorUtils.parseSerializedList(event.value);
        this._draw();
      break;
      case 'scale2d.type':
        this._type = event.value;
        this._draw();
      break;
      case 'scale2d.label.x':
        this._labelX = event.value;
        this._draw();
      break;
      case 'scale2d.label.y':
        this._labelY = event.value;
        this._draw();
      break;
    }
  }
});

QueryEditorMultiselectQuestionPreview = Class.create(QueryEditorQuestionPreview, {
  initialize: function ($super, pageEditor, container) {
    $super(pageEditor, container);
    
    this._options = QueryEditorUtils.parseSerializedList(this.getBlockController().getPageOptionValue("multiselect.options"));
    
    pageEditor.addListener("optionEditorValueChange", this, this._onPageEditorOptionValueChange);
  },
  deinitialize: function ($super) {
    $super();
    
    this._container.update('');
    this.getPageEditor().removeListener("optionEditorValueChange", this);
  },
  setup: function ($super) {
    $super();
    
    this._draw();
  },
  _draw: function() {
    this._container.update('');

    for (var i = 0, l = this._options.length; i < l; i++) {
      var itemContainer = new Element("div", {
        className: "queryMultiselectListItemContainer" //"queryEditorQuestionScale1DRadioPreviewItemContainer"
      });
      
      var inputContainer = new Element("div", {
        className: "queryMultiselectListItemInputContainer" //"queryEditorQuestionScale1DRadioPreviewInputContainer"
      });
      
      var labelContainer = new Element("label", {
        className: "queryMultiselectListItemLabel" // "queryEditorQuestionScale1DRadioPreviewLabelContainer"
      }).update(this._options[i]);
      
      var input = new Element("input", {
        type: "checkbox",
        disabled: 'disabled'
      });
      
      inputContainer.appendChild(input);
      itemContainer.appendChild(inputContainer);
      itemContainer.appendChild(labelContainer);
      
      this._container.appendChild(itemContainer);
    }
  },
  _onPageEditorOptionValueChange: function (event) {
    switch (event.name) {
      case 'multiselect.options':
        this._options = QueryEditorUtils.parseSerializedList(event.value);
        this._draw();
      break;
    }
  }
});

QueryEditorOrderingQuestionPreview = Class.create(QueryEditorQuestionPreview, {
  initialize: function ($super, pageEditor, container) {
    $super(pageEditor, container);
    
    this._items = QueryEditorUtils.parseSerializedList(this.getBlockController().getPageOptionValue("orderingField.items"));
    
    pageEditor.addListener("optionEditorValueChange", this, this._onPageEditorOptionValueChange);
  },
  deinitialize: function ($super) {
    $super();
    
    this._container.update('');
    this.getPageEditor().removeListener("optionEditorValueChange", this);
  },
  setup: function ($super) {
    $super();
    
    this._draw();
  },
  _draw: function() {
    this._container.update('');

    for (var i = 0, l = this._items.length; i < l; i++) {
      var itemContainer = new Element("div", { className: "queryOrderingFieldItemContainer" });
      
      var labelIndexContainer = new Element("div", { className: "queryOrderingFieldItemIndexNumber" });
      var labelContainer = new Element("div", { className: "queryOrderingFieldItemText" });
      
      var upBtn = new Element("div", { className: "queryOrderingFieldItemMoveUpButton" });
      var downBtn = new Element("div", { className: "queryOrderingFieldItemMoveDownButton" });

      itemContainer.appendChild(labelIndexContainer);
      itemContainer.appendChild(labelContainer);
      itemContainer.appendChild(upBtn);
      itemContainer.appendChild(downBtn);
      
      labelIndexContainer.update((i + 1) + ".");
      labelContainer.update(this._items[i]);
      this._container.appendChild(itemContainer);
    }
  },
  _onPageEditorOptionValueChange: function (event) {
    switch (event.name) {
      case 'orderingField.items':
        this._items = QueryEditorUtils.parseSerializedList(event.value);
        this._draw();
      break;
    }
  }
});

QueryEditorGroupingQuestionPreview = Class.create(QueryEditorQuestionPreview, {
  initialize: function ($super, pageEditor, container) {
    $super(pageEditor, container);
    
    this._items = QueryEditorUtils.parseSerializedList(this.getBlockController().getPageOptionValue("grouping.items"));
    this._groups = QueryEditorUtils.parseSerializedList(this.getBlockController().getPageOptionValue("grouping.groups"));
    
    pageEditor.addListener("optionEditorValueChange", this, this._onPageEditorOptionValueChange);
  },
  deinitialize: function ($super) {
    $super();
    
    this._container.update('');
    this.getPageEditor().removeListener("optionEditorValueChange", this);
  },
  setup: function ($super) {
    $super();
    
    this._draw();
  },
  _draw: function() {
    this._container.update('');

    var itemContainer = new Element("div", { className: "queryGroupingItemContainer" });
    var groupContainer = new Element("div", { className: "queryGroupingGroupContainer" });
    var clearBothDiv = new Element("div", { className: "clearBoth" });
    this._container.appendChild(itemContainer);
    this._container.appendChild(groupContainer);
    this._container.appendChild(clearBothDiv);
    
    for (var i = 0, l = this._items.length; i < l; i++) {
      var item = new Element("div", { className: "queryGroupingItem" });
      var label = new Element("label", { className: "queryMultiselectListItemLabel" });
      label.update(this._items[i]);
      
      item.appendChild(label);
      itemContainer.appendChild(item);
    }
    
    for (var i = 0, l = this._groups.length; i < l; i++) {
      var group = new Element("div", { className: "queryGroupingGroup" });
      var label = new Element("label", { className: "queryGroupingGroupLabel" });
      label.update(this._groups[i]);
      
//      itemContainer.appendChild(label);
      groupContainer.appendChild(group);
      group.appendChild(label);
    }
  },
  _onPageEditorOptionValueChange: function (event) {
    switch (event.name) {
      case 'grouping.items':
        this._items = QueryEditorUtils.parseSerializedList(event.value);
        this._draw();
      break;
      case 'grouping.groups':
        this._groups = QueryEditorUtils.parseSerializedList(event.value);
        this._draw();
      break;
    }
  }
});

QueryEditorExpertsQuestionPreview = Class.create(QueryEditorQuestionPreview, {
  initialize: function ($super, pageEditor, container) {
    $super(pageEditor, container);

    this._expertiseClasses = JSDATA['panelExpertiseClasses'].evalJSON();
    this._interestClasses = JSDATA['panelIntressClasses'].evalJSON();
    this._panelExpertiseGroups = JSDATA['panelExpertiseGroups'].evalJSON();
  },
  deinitialize: function ($super) {
    $super();
    
    this._container.update('');
  },
  setup: function ($super) {
    $super();
    
    this._draw();
  },
  _draw: function() {
    this._container.update('');

    var tableElem = new Element("table", { className: "queryExpertiseMatrix" });
    var headerRowElem = new Element("tr", { className: "queryExpertiseMatrixHeaderRow" });
    headerRowElem.appendChild(new Element("td"), { className: "queryExpertiseMatrixCell" });
    tableElem.appendChild(headerRowElem);
    
    for (var i = 0, l1 = this._expertiseClasses.length; i < l1; i++) {
      var expertiseElem = new Element("td", { className: "queryExpertiseMatrixCell queryExpertiseMatrixHeaderCell" });
      expertiseElem.update(this._expertiseClasses[i].name);
      headerRowElem.appendChild(expertiseElem);
    }
    
    for (var i = 0, l1 = this._interestClasses.length; i < l1; i++) {
      var rowElem = new Element("tr", { className: "queryExpertiseMatrixRow" });
      tableElem.appendChild(rowElem);
      
      var interestElem = new Element("td", { className: "queryExpertiseMatrixCell queryExpertiseMatrixHeaderCell" });
      interestElem.update(this._interestClasses[i].name);
      rowElem.appendChild(interestElem);

      for (var j = 0, l2 = this._expertiseClasses.length; j < l2; j++) {
        rowElem.appendChild(new Element("td", { className: "queryExpertiseMatrixCell queryExpertiseMatrixAnswerCell" }));
      }
    }
    
    this._container.appendChild(tableElem);
  }
});


QueryEditorElementEditor = Class.create({
  initialize: function (blockController) {
    this._blockController = blockController;
    
    this._settingsContainer = new Element("form", {
      className: "queryEditorSettingContainer"
    });
    
    this._uniqueId = 0;
  },
  deinitialize: function() {
    this._settingsContainer.remove();
  },
  getBlockController: function () {
    return this._blockController;
  },
  setup: function () {    
  },
  getElementType: function () {
    throw new Error("ElementType method not implemented");
  },
  commit: function () {
  },
  appendSettingsContainer: function () {
    this.getBlockController().appendFormField(this._settingsContainer);
  },
  addSettingField: function (field) {
    this._settingsContainer.appendChild(field);
  },
  _createCheckBoxField: function (name, title, checked) {
    var fieldId = this._getUniqueId();
    
    var formFieldContainer = new Element("div", {
      'className': "formFieldContainer formCheckBoxContainer"
    });
    
    var formFieldLabel = new Element("label", {
      'className': "formFieldLabel",
      'for': fieldId
    }).update(title);
    
    var formFieldInput = new Element("input", {
      'id': fieldId,
      'name': name,
      'type': 'checkbox',
      'class': 'formField formCheckBox'
    });
    
    if (checked) {
      formFieldInput.setAttribute("checked", "checked");
    }

    formFieldContainer.appendChild(formFieldInput);
    formFieldContainer.appendChild(formFieldLabel);
    
    return formFieldContainer;
  },
  _createTextFormField: function (name, title, value, required) {
    var fieldId = this._getUniqueId();
    
    var formFieldContainer = new Element("div", {
      'className': "formFieldContainer formTextFieldContainer"
    });
    
    var formFieldLabel = new Element("label", {
      'className': "formFieldLabel",
      'for': fieldId
    }).update(title);
    
    var formFieldInput = new Element("input", {
      'id': fieldId,
      'name': name,
      'class': 'formField formTextField',
      'value': value||''
    });
    
    if (required) {
      formFieldInput.addClassName("required");
      initializeElementValidation(formFieldInput);
    }
    
    formFieldContainer.appendChild(formFieldLabel);
    formFieldContainer.appendChild(formFieldInput);
    
    return formFieldContainer;
  },
  _getUniqueId: function () {
    return new Date().getTime() + (++this._uniqueId);
  }
});

Object.extend(QueryEditorElementEditor.prototype, fni.events.FNIEventSupport);

QueryEditorSectionEditor = Class.create(QueryEditorElementEditor, {
  initialize: function ($super, blockController, sectionId) {
    $super(blockController);

    this._sectionId = sectionId;
    this._titleChangeListener = this._onTitleChange.bindAsEventListener(this);
  },
  deinitialize: function($super) {
    this.getBlockController().removeListener("sectionIdChange", this);
    Event.stopObserving(this._titleInput, "change", this._titleChangeListener);
    this._titleInputButtonInputComponent.deinitialize();
    this._titleContainer.remove();
    this._visibleContainer.remove();
    this._commentableContainer.remove();
    this._viewDiscussionsContainer.remove();
    this._showLiveReportsContainer.remove();
    
    $super();
  },
  setup: function ($super) {
    $super();
    
    var sectionData = this.getBlockController().getSectionData(this._sectionId);
    this._titleContainer = this._createTextFormField('title', getLocale().getText('panelAdmin.block.query.sectionTitle'), sectionData.title, true);
    this._visibleContainer = this._createCheckBoxField('visible', getLocale().getText('panelAdmin.block.query.sectionVisibleTitle'), "1" == sectionData.visible);
    this._commentableContainer = this._createCheckBoxField('commentable', getLocale().getText('panelAdmin.block.query.sectionCommentableTitle'), "0" != sectionData.commentable);
    this._showLiveReportsContainer = this._createCheckBoxField('showLiveReports', getLocale().getText('panelAdmin.block.query.sectionShowLiveReportsTitle'), true);
    this._viewDiscussionsContainer = this._createCheckBoxField('viewDiscussions', getLocale().getText('panelAdmin.block.query.sectionViewDiscussionTitle'), "0" != sectionData.viewDiscussions);

    this.getBlockController().appendFormField(this._titleContainer);
    this.addSettingField(this._visibleContainer);
    this.addSettingField(this._commentableContainer);
    this.addSettingField(this._viewDiscussionsContainer);
    this.addSettingField(this._showLiveReportsContainer);
    
    this._titleInput = this._titleContainer.down('input');
    this._visibleInput = this._visibleContainer.down('input');
    this._commentableInput = this._commentableContainer.down('input');
    this._viewDiscussionsInput = this._viewDiscussionsContainer.down('input');
    this._showLiveReportInput = this._showLiveReportsContainer.down('input');
    this._titleInputButtonInputComponent = new ButtonInputComponent(this._titleInput);
    
    this.getBlockController().addListener("sectionIdChange", this, this._onIdChange);
    Event.observe(this._titleInput, "change", this._titleChangeListener);
    
    this.appendSettingsContainer();
  },
  getElementType: function () {
    return 'SECTION';
  },
  commit: function ($super) {
    $super();
    
    var titleChanged = this.getBlockController().getSectionData(this._sectionId).title != this._titleInput.value; 

    this.getBlockController().getSectionData(this._sectionId).title = this._titleInput.value;
    this.getBlockController().getSectionData(this._sectionId).visible = this._visibleInput.checked ? 1 : 0;
    this.getBlockController().getSectionData(this._sectionId).commentable = this._commentableInput.checked ? 1 : 0;
    this.getBlockController().getSectionData(this._sectionId).viewDiscussions = this._viewDiscussionsInput.checked ? 1 : 0;
    this.getBlockController().getSectionData(this._sectionId).showLiveReports = this._showLiveReportInput.checked ? 1 : 0;
    
    if (titleChanged) {
      this.fire("titleChange", {
        sectionId: this._sectionId,
        newTitle: this._titleInput.value
      });
    }
  },
  _onTitleChange: function (event) {
    this.commit();
  },
  _onIdChange: function (event) {
    if (this._sectionId == event.from) {
      this._sectionId = event.to;
    }
  }
});

QueryEditorPageEditor = Class.create(QueryEditorElementEditor, {
  initialize: function ($super, blockController) {
    $super(blockController);
  
    this._titleChangeListener = this._onTitleChange.bindAsEventListener(this);
  },
  deinitialize: function($super) {
    Event.stopObserving(this._titleInput, "change", this._titleChangeListener);
    this._titleInputButtonInputComponent.deinitialize();
    this._titleContainer.remove();
    this._visibleContainer.remove();
    
    $super();
  },
  setup: function ($super) {
    $super();
    
    var titleOption = this.getBlockController().getPageOption('title');
    var visibleOption = this.getBlockController().getPageOption('visible');
    
    this._titleContainer = this._createTextFormField('title', titleOption.caption, titleOption.value, true);
//    this._titleContainer.down('input').addClassName('keepEnabled');
    this._visibleContainer = this._createCheckBoxField('visible', visibleOption.caption, "1" == visibleOption.value);
    
    this.getBlockController().appendFormField(this._titleContainer);
    this.addSettingField(this._visibleContainer);

    this._titleInput = this._titleContainer.down('input');
    this._visibleInput = this._visibleContainer.down('input');

    
    this._titleInputButtonInputComponent = new ButtonInputComponent(this._titleInput);
    
    Event.observe(this._titleInput, "change", this._titleChangeListener);
  },
  getElementType: function () {
    return 'PAGE';
  },
  commit: function ($super) {
    $super();
    
    var pageData = this.getBlockController().getCurrentPageData();
    if (pageData) {
      var newTitle = this._titleInput.value;
      var titleChanged = pageData.title != newTitle;
      
      var pageOptions = pageData.options;
      for ( var optionIndex = 0, optionsCount = pageOptions.length; optionIndex < optionsCount; optionIndex++) {
        var optionValue = this.getOptionValue(pageData.options[optionIndex]);
        this.getBlockController().setPageOptionValue(pageData.options[optionIndex].name, optionValue);
      }
      
      if (titleChanged) {
        pageData.title = newTitle;
        this.fire("titleChange", {
          newTitle: newTitle
        });
      }
    }
  },
  getOptionValue: function (option) {
    switch (option.type) {
      case 'PAGE':
        if (option.name == 'title') {
          return this._titleInput.value;
        } if (option.name == 'visible') {
          return this._visibleInput.checked ? 1 : 0; 
        } else {
          throw new Error("Unrecognized PAGE option: " + option.name);
        }
      break;
      default:
        throw new Error("Unsupported option type: " + option.type);
      break;
    }
  },
  filterJsonSerializedSetting: function (value) {
    return "/**JSS-" + value + "-JSS**/";
    
  },
  unfilterJsonSerializedSetting: function (value) {
    if (value && value.startsWith("/**JSS-") && value.endsWith("-JSS**/")) {
      return value.substring("/**JSS-".length, value.length - "-JSS**/".length);
    }
    
    return value;
  },
  _onTitleChange: function (event) {
    this.commit();
  }
});

QueryEditorEmptyPageEditor = Class.create(QueryEditorElementEditor, {
  initialize: function ($super, blockController) {
  },
  deinitialize: function($super) {
  },
  setup: function ($super) {
  }
});

QueryEditorTextPageEditor = Class.create(QueryEditorPageEditor, {
  initialize: function ($super, blockController) {
    $super(blockController);
  },
  deinitialize: function($super) {
    $super();
    this._commentableContainer.remove();
    this._viewDiscussionsContainer.remove();
    if (this._textContentEditor) {
      $(this._textContentEditor.element).remove();
      this._textContentEditor.destroy();
      this._textContentEditor = undefined;
    }
  },
  setup: function ($super) {
    $super();

    var commentableOption = this.getBlockController().getPageOption('text.commentable');
    this._commentableContainer = this._createCheckBoxField('text.commentable', commentableOption.caption, "1" == commentableOption.value);
    this.addSettingField(this._commentableContainer);
    this._commentableInput = this._commentableContainer.down('input');
    
    var viewDiscussionsOption = this.getBlockController().getPageOption('text.viewDiscussions');
    this._viewDiscussionsContainer = this._createCheckBoxField('text.viewDiscussions', viewDiscussionsOption.caption, "1" == viewDiscussionsOption.value);
    this.addSettingField(this._viewDiscussionsContainer);
    this._viewDiscussionsInput = this._viewDiscussionsContainer.down('input');
    
    var textContent = new Element("textarea", {
      name: 'text.content'
    }).update(this.getBlockController().getPageOptionValue('text.content'));
    
    this.getBlockController().appendFormField(textContent);
    
    var panelId = JSDATA['securityContextId'];
    this._textContentEditor = CKEDITOR.replace(textContent, {
// TODO: Can we load all extra plugins from same config file or should we load plugins based on individual editor instances?    
//      extraPlugins: 'fnigenericbrowser',
      toolbar: "materialToolbar",
      fniGenericBrowser:{
        enabledInDialogs: ['image', 'link'],
        connectorUrl: CONTEXTPATH + '/system/ckbrowserconnector.json?panelId=' + panelId
      }
    });
    
    this.appendSettingsContainer();
  },
  getOptionValue: function ($super,option) {
    switch (option.type) {
      case 'TEXT':
        switch (option.name) {
          case 'text.content':
            return this._textContentEditor.getData();
          break;
          case 'text.commentable':
            return this._commentableInput.checked ? 1 : 0;
          break;
          case 'text.viewDiscussions':
            return this._viewDiscussionsInput.checked ? 1 : 0;
          break;
          default:
            throw new Error("Unrecognized TEXT option: " + option.name);
          break;
        } 
      break;
      default:
        return $super(option);
      break;
    }
  }
});

QueryEditorFormPageFieldEditor = Class.create({
  initialize: function (pageEditor, fieldJson) {
    this._pageEditor = pageEditor;
    this._parent = null;
    this._container = new Element("div", {
      className: "queryEditorFormFieldEditor"
    }); 
    this._fieldJson = fieldJson;
    
    this._settingsContainer = null;
    this._actionsContainer = new Element("div", {
      className: "queryEditorFormFieldEditorActions"
    }); 
    this._deleteLink = new Element("a", {
      href: "javascript:void(null)",
      className: "queryEditorFormFieldEditorDeleteAction"
    });
    this._actionsContainer.appendChild(this._deleteLink);
    
    this._settingsLink = new Element("a", {
      href: "javascript:void(null)",
      className: "queryEditorFormFieldEditorSettingsAction"
    });
    this._actionsContainer.appendChild(this._settingsLink);
    
    this._container.appendChild(this._actionsContainer);
    
    this._deleteLinkClickListener = this._onDeleteLinkClick.bindAsEventListener(this);
    this._settingsLinkClickListener = this._onSettingsLinkClick.bindAsEventListener(this);
    
    Event.observe(this._deleteLink, "click", this._deleteLinkClickListener);
    Event.observe(this._settingsLink, "click", this._settingsLinkClickListener);
  },
  deinitialize: function () {
    Event.stopObserving(this._deleteLink, "click", this._deleteLinkClickListener);
    Event.stopObserving(this._settingsLink, "click", this._settingsLinkClickListener);
    
    this._closeFieldSettings();
    
    this._container.remove();
  },
  addToDom: function (parent) {
    this._parent = parent;
    this._parent.appendChild(this._container);
  },
  replaceToDom: function (node) {
    this._parent = node.parentNode;
    node.replace(this._container);
  },
  disableEditor: function() {
    this._actionsContainer.hide();
  },
  _getPageEditor: function () {
    return this._pageEditor;
  },
  _getParent: function () {
    return this._parent;
  },
  _getContainer: function () {
    return this._container;
  },
  getFieldJson: function () {
    return this._fieldJson;
  },
  _getUniqueId: function () {
    return (++this._ID);
  },
  _createFieldSettingsEditors: function (settingsContainer) {
    var captionSettingContainer = new Element("div", {
      className: "queryEditorFormListFieldSetting"
    });
    
    var fieldId = this._getUniqueId();
    
    var fieldCaptionLabel = new Element("label", {
      'for': fieldId
    }).update(getLocale().getText('panelAdmin.block.query.formFieldCaptionLabel')); 
    
    var fieldCaption = new Element("input", {
      type: "text",
      name: "caption",
      value: this.getFieldJson().caption,
      id: fieldId
    });
 
    captionSettingContainer.appendChild(fieldCaptionLabel);
    captionSettingContainer.appendChild(fieldCaption);
    
    settingsContainer.appendChild(captionSettingContainer);
  },
  _createFieldSettings: function () {
    var settingsContainer = new Element("div", {
      className: "queryEditorFormFieldSettings"
    });
    
    this._createFieldSettingsEditors(settingsContainer);
    
    var settingsButtonsContainer = new Element("div", {
      className: "queryEditorFormFieldSettingsButtons"
    });
    
    var settingsApplyButton = new Element("button", {
      type: "submit"
    }).update(getLocale().getText('panelAdmin.block.query.formFieldSettingsApplyButton'));
    
    var settingsCancelButton = new Element("button", {
      type: "submit"
    }).update(getLocale().getText('panelAdmin.block.query.formFieldSettingsCancelButton'));
    
    var _this = this;
    Event.observe(settingsApplyButton, "click", function (event) {
      Event.stop(event);
      
      if (_this.fire("settingsApply")) {
        _this._applySettings(settingsContainer);
        _this._closeFieldSettings();
      }
    });
    
    Event.observe(settingsCancelButton, "click", function (event) {
      Event.stop(event);
      if (_this.fire("settingsCancel")) {
        _this._closeFieldSettings();
      }
    });
    
    settingsButtonsContainer.appendChild(settingsApplyButton);
    settingsButtonsContainer.appendChild(settingsCancelButton);
    settingsContainer.appendChild(settingsButtonsContainer);

    this._container.appendChild(settingsContainer);
    
    return settingsContainer;
  },
  openFieldSettings: function () {
    if (this._settingsContainer == null) {
      this._settingsOverlay = new S2.UI.Overlay();
      $(document.body).insert(this._settingsOverlay);    
      this._settingsContainer = this._createFieldSettings();
    }
  },
  _closeFieldSettings: function () {
    if (this._settingsContainer != null) {
      this._settingsOverlay.destroy();
      this._settingsContainer.select('button').invoke('purge');
      this._settingsContainer.remove();
      this._settingsContainer = null;
    }
  },
  _applySettings: function (settingsContainer) {
    this.getFieldJson().caption = settingsContainer.down('input[name="caption"]').value;
  },
  isMarkedForDeletion: function () {
    return this._container.hasClassName("queryEditorFormFieldEditorDeleted");
  },
  _markForDeletion: function () {
    this._container.addClassName("queryEditorFormFieldEditorDeleted");
    
    var eventQueue = getGlobalEventQueue();
    eventQueue.addItem(new EventQueueItem(getLocale().getText("panelAdmin.block.query.formFieldMarkedForDeletion"), {
      className: "eventQueueSuccessItem",
      timeout: 1000 * 3
    }));
  },
  _undoDeletion: function () {
    this._container.removeClassName("queryEditorFormFieldEditorDeleted");
  },
  _onDeleteLinkClick: function (event) {
    if (this.isMarkedForDeletion()) {
      this._undoDeletion();
    } else {
      this._markForDeletion();
    }
  },
  _onSettingsLinkClick: function (event) {
    if (!this.isMarkedForDeletion())
      this.openFieldSettings();
  },
  _ID: new Date().getTime()
});

Object.extend(QueryEditorFormPageFieldEditor.prototype, fni.events.FNIEventSupport);

QueryEditorFormPageTextFieldEditor = Class.create(QueryEditorFormPageFieldEditor, {
  initialize: function ($super, pageEditor, fieldJson) {
    $super(pageEditor, fieldJson);
    
    this._getContainer().appendChild(this._constructPreview());
  },
  _constructPreview: function () {
    var container = new Element("div", {
      className: "queryEditorFormFieldPreview queryEditorFormTextFieldPreview" 
    });
    
    var id = this._getUniqueId();
    var fieldElement = new Element("input", {
      id: id,
      type: "text",
      disabled: 'disabled'
    });
    
    var fieldJson = this.getFieldJson();

    container.appendChild(new Element("label", {
      "for": id
    }).update(fieldJson.caption));
    container.appendChild(fieldElement);

    return container;
  },
  _applySettings: function ($super, settingsContainer) {
    $super(settingsContainer);
    var oldPreview = this._getContainer().down('.queryEditorFormFieldPreview');
    oldPreview.replace(this._constructPreview());
  }
});

QueryEditorFormPageMemoFieldEditor = Class.create(QueryEditorFormPageFieldEditor, {
  initialize: function ($super, pageEditor, fieldJson) {
    $super(pageEditor, fieldJson);
    
    this._getContainer().appendChild(this._constructPreview());
  },
  _constructPreview: function () {
    var container = new Element("div", {
      className: "queryEditorFormFieldPreview queryEditorFormMemoFieldPreview" 
    });
    
    var id = this._getUniqueId();
    
    var fieldElement = new Element("textarea", {
      id: id,
      disabled: 'disabled'
    });
    
    var fieldJson = this.getFieldJson();

    container.appendChild(new Element("label", {
      "for": id
    }).update(fieldJson.caption));
    container.appendChild(fieldElement);

    return container;
  },
  _applySettings: function ($super, settingsContainer) {
    $super(settingsContainer);
    var oldPreview = this._getContainer().down('.queryEditorFormFieldPreview');
    oldPreview.replace(this._constructPreview());
  }
});

QueryEditorFormPageListFieldEditor = Class.create(QueryEditorFormPageFieldEditor, {
  initialize: function ($super, pageEditor, fieldJson) {
    $super(pageEditor, fieldJson);
    
    this._getContainer().appendChild(this._constructPreview());
    
    this._generatedIdCount = 0;
    
    var fieldJson = this.getFieldJson();
    var options = fieldJson.options;
    if (options) {
      for (var i = 0, l = options.length; i < l; i++) {
        if (options[i].value && options[i].value.startsWith('NEW-')) {
          this._generatedIdCount++;
        }
      }
    }
  },
  _constructPreview: function () {
    var container = new Element("div", {
      className: "queryEditorFormFieldPreview queryEditorFormListFieldPreview" 
    });

    var id = this._getUniqueId();
    var fieldJson = this.getFieldJson();
    
    switch (fieldJson.listType) {
      case 'RADIO':
        container.appendChild(new Element("label").update(fieldJson.caption));
        
        var options = fieldJson.options;
        if (options) {
          for (var i = 0, l = options.length; i < l; i++) {
            var option = options[i];
            
            var optionContainer = new Element("div", {
              className: "queryEditorFormListFieldRadioItem"
            });
            var input = new Element("input", {
              type: "radio",
              id: id + '-' + i,
              className: "queryEditorFormListFieldRadioItemInput"
            });
            
            var label = new Element("label", {
              'for': id + '-' + i,
              className: "queryEditorFormListFieldRadioItemLabel"
            }).update(option.label);
            
            optionContainer.appendChild(input);
            optionContainer.appendChild(label);
            
            container.appendChild(optionContainer);
          }
        }
      break;
      case 'SELECT':
        var selectElement = new Element("select", {
          id: id,
          className: "queryEditorFormListFieldSelect"
        });
        
        var options = fieldJson.options;
        if (options) {
          for (var i = 0, l = options.length; i < l; i++) {
            var option = options[i];
            selectElement.appendChild(new Element("option", {
              value: option.value
            }).update(option.label));
          };
        }
        
        container.appendChild(new Element("label", {
          "for": id
        }).update(fieldJson.caption));
        container.appendChild(selectElement);
      break;
      case 'SLIDER':
        var trackElement = new Element("div", {
          id: id,
          className: "queryEditorFormListFieldSlider"
        });
        
        container.appendChild(new Element("label", {
          "for": id
        }).update(fieldJson.caption));
        container.appendChild(trackElement);
        
        var options = fieldJson.options;
        if (options) {
          var labelStep = 100 / (options.length - 1);
          
          for (var i = 0, l = options.length; i < l; i++) {
            var option = options[i];
            
            var label = new Element("label", {
              className: "queryEditorFormListFieldSliderLabel"
            }).update(option.label);
            
            container.appendChild(label);
         
            if (i == 0) {
              // First label is always in far left
              label.setStyle({
                left: '0px'
              });  
            } else if (i == (options.length - 1)) {
              // ...and last in far right
              label.setStyle({
                right: '0px'
              });  
            } else {
              // others are positioned by percentage

              label.setStyle({
                left: (labelStep * i) + '%'
              }); 
              label.addClassName('queryEditorFormListFieldSliderLabelCentered');
            }
          };
        }

        new S2.UI.Slider(trackElement, {
          value: {
            initial: 0,
            min: 0,
            max: 0
          },
          disabled: true
        });
      break;
    }
    
    return container;
  },
  addToDom: function ($super, parent) {
    $super(parent);

    if (this.getFieldJson().listType == 'SLIDER') {
      this._getContainer().select('.queryEditorFormListFieldSliderLabelCentered').each(function (label) {
        var labelWidth = label.getLayout().get("width");
        label.setStyle({
          marginLeft: (-labelWidth / 2) + 'px'
        });
      });
    }
  },
  _markForDeletion: function ($super) {
    $super();
    if (this.getFieldJson().listType == 'SELECT')
      this._getContainer().down('.queryEditorFormFieldPreview select').setAttribute("disabled", "disabled");
  },
  _undoDeletion: function ($super) {
    $super();
    if (this.getFieldJson().listType == 'SELECT')
      this._getContainer().down('.queryEditorFormFieldPreview select').removeAttribute("disabled");
  },
  _applySettings: function ($super, settingsContainer) {
    $super(settingsContainer);
  },
  _createFieldSettingsEditors: function ($super, settingsContainer) {
    $super(settingsContainer);
    
    var fieldJson = this.getFieldJson();

    var listTypeSettingContainer = new Element("div", {
      className: "queryEditorFormListFieldSetting"
    });
    
    var listTypeId = this._getUniqueId();
    
    var fieldListTypeLabel = new Element("label", {
      'for': listTypeId
    }).update(getLocale().getText('panelAdmin.block.query.formFieldListTypeLabel')); 
    
    var fieldListType = new Element("select", {
      name: "listType",
      id: listTypeId
    });
    
    var fieldListTypeRadioOption = new Element("option", {
      value: "RADIO"
    }).update(getLocale().getText('panelAdmin.block.query.formFieldListTypeRadioLabel'));
    if (fieldJson.listType == 'RADIO') {
      fieldListTypeRadioOption.setAttribute("selected", "selected");
    }
    
    var fieldListTypeSelectOption = new Element("option", {
      value: "SELECT"
    }).update(getLocale().getText('panelAdmin.block.query.formFieldListTypeSelectLabel'));
    if (fieldJson.listType == 'SELECT') {
      fieldListTypeSelectOption.setAttribute("selected", "selected");
    }

    var fieldListTypeSliderOption = new Element("option", {
      value: "SLIDER"
    }).update(getLocale().getText('panelAdmin.block.query.formFieldListTypeSliderLabel'));
    if (fieldJson.listType == 'SLIDER') {
      fieldListTypeSliderOption.setAttribute("selected", "selected");
    }
    
    fieldListType.appendChild(fieldListTypeRadioOption);
    fieldListType.appendChild(fieldListTypeSelectOption);
    fieldListType.appendChild(fieldListTypeSliderOption);
    
    listTypeSettingContainer.appendChild(fieldListTypeLabel);
    listTypeSettingContainer.appendChild(fieldListType);
    
    settingsContainer.appendChild(listTypeSettingContainer);
    
    var optionsSettingContainer = new Element("div", {
      className: "queryEditorFormListFieldSetting"
    });
    
    var fieldId = this._getUniqueId();
    
    var label = new Element("label", {
      'for': fieldId
    }).update(getLocale().getText('panelAdmin.block.query.formFieldListOptionsLabel'));
    
    var optionsContainer = new Element("div", {
      className: "queryEditorFormListFieldOptions"
    });
    
    var options = fieldJson.options;
    if (options) {
      for (var i = 0, l = options.length; i < l; i++) {
        var option = options[i];
        optionsContainer.appendChild(this._createSettingsListItem(option.label, option.value));
      };
    }

    this._updateSettingsListItemsLinkStates(optionsContainer);

    optionsSettingContainer.appendChild(label);
    optionsSettingContainer.appendChild(optionsContainer);
       
    var addOptionLink = new Element("a", {
      className: "queryEditorFormListFieldAddOptionLink",
      href: "javascript:void(null)"
    }).update(getLocale().getText('panelAdmin.block.query.formFieldListAddOptionLabel'));
    

    var _this = this;
    Event.observe(addOptionLink, "click", function (event) {
      var label = '';
      var value = 'NEW-' + (_this._generatedIdCount++);
      _this.getFieldJson().options.push({
        label: label,
        value: value 
      });
      optionsContainer.appendChild(_this._createSettingsListItem(label, value));
      _this._updateSettingsListItemsLinkStates(optionsContainer);
    }); 
    
    optionsSettingContainer.appendChild(addOptionLink);
    
    settingsContainer.appendChild(optionsSettingContainer);
  },
  _createSettingsListItem: function (label, value) {
    var optionContainer = new Element("div", {
      className: "queryEditorFormListFieldOptionContainer"
    });
    
    optionContainer.appendChild(new Element("input", {
      type: 'text',
      value: label,
      className: "queryEditorFormListFieldOptionLabel"
    }));
    
    optionContainer.appendChild(new Element("input", {
      type: 'hidden',
      value: value,
      className: "queryEditorFormListFieldOptionValue"
    }));

    var deleteLink = new Element("a", {
      href: "javascript:void(null)",
      className: "queryEditorFormListFieldOptionDelete"
    }).update(getLocale().getText('panelAdmin.block.query.formFieldListSettingsItemDelete'));
    
    var _this = this;
    Event.observe(deleteLink, "click", function () {
      var optionsContainer = $(optionContainer.parentNode);
      var optionContainers = optionsContainer.select('.queryEditorFormListFieldOptionContainer');
      for (var i = 0, l = optionContainers.length; i < l; i++) {
        if (optionContainers[i] === optionContainer) {
          _this.getFieldJson().options.splice(i, 1);
          optionContainer.remove();
        }
      }
      _this._updateSettingsListItemsLinkStates(optionsContainer);
    });
    
    var upLink = new Element("a", {
      href: "javascript:void(null)",
      className: "queryEditorFormListFieldOptionUp"
    }).update(getLocale().getText('panelAdmin.block.query.formFieldListSettingsItemUp'));
    Event.observe(upLink, "click", function () {
      var optionsContainer = $(optionContainer.parentNode);
      var index = _this._getSettingsListItemIndex(optionsContainer, optionContainer);
      _this._swapSettingsListItems(optionsContainer, index, index - 1);
      _this._updateSettingsListItemsLinkStates(optionsContainer);
    });

    var downLink = new Element("a", {
      href: "javascript:void(null)",
      className: "queryEditorFormListFieldOptionDown"
    }).update(getLocale().getText('panelAdmin.block.query.formFieldListSettingsItemDown'));
    Event.observe(downLink, "click", function () {
      var optionsContainer = $(optionContainer.parentNode);
      var index = _this._getSettingsListItemIndex(optionsContainer, optionContainer);
      _this._swapSettingsListItems(optionsContainer, index, index + 1);
      _this._updateSettingsListItemsLinkStates(optionsContainer);
    });
    
    optionContainer.appendChild(deleteLink);
    optionContainer.appendChild(upLink);
    optionContainer.appendChild(downLink);
    
    return optionContainer;
  },
  _getSettingsListItemIndex: function (optionsContainer, optionContainer) {
    var optionContainers = $(optionsContainer).select('.queryEditorFormListFieldOptionContainer');
    for (var i = 0, l = optionContainers.length; i < l; i++) {
      if (optionContainers[i] === optionContainer) {
        return i;
      }
    }
    
    return null;
  },
  _swapSettingsListItems: function (optionsContainer, index1, index2) {
    var swap = function (array, i1, i2) {
      var t = array[i1];
      array[i1] = array[i2];
      array[i2] = t;
      return array;
    };

    var i1Value = this.getFieldJson().options[index1].value;
    var i2Value = this.getFieldJson().options[index2].value;
    this.getFieldJson().options[index1].value = i2Value;
    this.getFieldJson().options[index2].value = i1Value;
    this.getFieldJson().options = swap(this.getFieldJson().options, index1, index2);
    
    var optionContainers = optionsContainer.select('.queryEditorFormListFieldOptionContainer');
    optionContainers[index1].down('.queryEditorFormListFieldOptionValue').value = i2Value;
    optionContainers[index2].down('.queryEditorFormListFieldOptionValue').value = i1Value;
    optionContainers = swap(optionContainers, index1, index2);
    for (var i = 0, l = optionContainers.length; i < l; i++) {
      optionsContainer.appendChild(optionContainers[i]);
    }
  },
  _updateSettingsListItemsLinkStates: function (optionsContainer) {
    var optionContainers = optionsContainer.select('.queryEditorFormListFieldOptionContainer');
    for (var i = 0, l = optionContainers.length; i < l; i++) {
      var option = optionContainers[i];
      if (i === 0) {
        option.down('.queryEditorFormListFieldOptionUp').addClassName("queryEditorFormListFieldOptionLinkDisabled");
        if (i === l - 1)
          option.down('.queryEditorFormListFieldOptionDown').addClassName("queryEditorFormListFieldOptionLinkDisabled");
        else
          option.down('.queryEditorFormListFieldOptionDown').removeClassName("queryEditorFormListFieldOptionLinkDisabled");
      } else if (i === l - 1) {
        option.down('.queryEditorFormListFieldOptionUp').removeClassName("queryEditorFormListFieldOptionLinkDisabled");
        option.down('.queryEditorFormListFieldOptionDown').addClassName("queryEditorFormListFieldOptionLinkDisabled");
      } else {
        option.down('.queryEditorFormListFieldOptionUp').removeClassName("queryEditorFormListFieldOptionLinkDisabled");
        option.down('.queryEditorFormListFieldOptionDown').removeClassName("queryEditorFormListFieldOptionLinkDisabled");
      }
    }
  },
  _applySettings: function ($super, settingsContainer) {
    $super(settingsContainer);
    
    var listType = settingsContainer.down('select[name="listType"]');
    this.getFieldJson().listType = listType.value;
    
    var optionContainers = settingsContainer.select('.queryEditorFormListFieldOptionContainer');
    for (var i = 0, l = optionContainers.length; i < l; i++) {
      var optionContainer = optionContainers[i];
      var label = optionContainer.down('.queryEditorFormListFieldOptionLabel').value;
      var value = optionContainer.down('.queryEditorFormListFieldOptionValue').value;
      
      for (var j = 0, jl = this.getFieldJson().options.length; j < jl; j++) {
        if (this.getFieldJson().options[j].value === value) {
          this.getFieldJson().options[j].label = label;
          break;
        }
      }
    }
    
    var oldPreview = this._getContainer().down('.queryEditorFormFieldPreview');
    oldPreview.replace(this._constructPreview());
  }
});

QueryEditorFormPageEditor = Class.create(QueryEditorPageEditor, {
  initialize: function ($super, blockController) {
    $super(blockController);
    
    this._addFieldLinkClickListener = this._onAddFieldLink.bindAsEventListener(this);
    
    this._fieldEditors = new Array();
  },
  deinitialize: function($super) {
    this._fieldEditors.each(function (e) {
      e.deinitialize();
    });
    this._fieldEditors.clear();
    this._addFieldLink.remove();  

    Event.stopObserving(this._addFieldLink, "click", this._addFieldLinkClickListener);
    
    $super();
  },
  setup: function ($super) {
    $super();
    
    this._fieldEditors.clear();
    
    this._fieldEditorsContainer = new Element("div", {
      className: "queryEditorFormFieldEditors"
    });
    
    this._addFieldLink = new Element("a", {
      className: "queryEditorFormAddField",
      href: "javascript:void(null)"
    }).update(getLocale().getText('panelAdmin.block.query.formFieldAddField'));
    Event.observe(this._addFieldLink, "click", this._addFieldLinkClickListener);
    
    var fieldsOption = this.getBlockController().getPageOption('form.fields');
    
    this.getBlockController().appendFormField(this._fieldEditorsContainer);
    if (fieldsOption.value) {
      var optionValue = this.unfilterJsonSerializedSetting(fieldsOption.value);
      var fieldsJson = optionValue.evalJSON();
      for (var i = 0, l = fieldsJson.length; i < l; i++) {
        var fieldJson = fieldsJson[i];
        var editor = null;
        
        switch (fieldJson.type) {
          case 'TEXT':
            editor = new QueryEditorFormPageTextFieldEditor(this, fieldJson);
          break;
          case 'MEMO':
            editor = new QueryEditorFormPageMemoFieldEditor(this, fieldJson);
          break;
          case 'LIST':
            editor = new QueryEditorFormPageListFieldEditor(this, fieldJson);
          break;
        }

        if (editor) {
          this._fieldEditors.push(editor);
          editor.addToDom(this._fieldEditorsContainer);
        }
      }
    }
    
    this.getBlockController().appendFormField(this._addFieldLink);
    
    var pageData = this.getBlockController().getCurrentPageData();
    if (pageData.hasAnswers == "true") {
      this._disableEditor();
    }
    
    this.appendSettingsContainer();
  },
  commit: function ($super) {
    for (var i = this._fieldEditors.length - 1; i >= 0; i--) {
      var fieldEditor = this._fieldEditors[i];
      if (fieldEditor.isMarkedForDeletion()) {
        fieldEditor.deinitialize();
        this._fieldEditors.splice(i, 1);
      }
    }
    
    var maxName = 0;
    for (var i = this._fieldEditors.length - 1; i >= 0; i--) {
      var fieldEditor = this._fieldEditors[i];
      var name = fieldEditor.getFieldJson().name; 
      if (name && !name.startsWith('NEW')) {
        maxName = Math.max(maxName, parseInt(name));
      }
      
      if (fieldEditor.getFieldJson().type == 'LIST') {
        var maxValue = 0;
        for (var j = 0, jl = fieldEditor.getFieldJson().options.length; j < jl; j++) {
          var option = fieldEditor.getFieldJson().options[j];
          if (option.value&&!option.value.startsWith('NEW')) {
            maxValue = Math.max(maxValue, parseInt(option.value));
          }
        }
        
        for (var j = 0, jl = fieldEditor.getFieldJson().options.length; j < jl; j++) {
          if (!fieldEditor.getFieldJson().options[j].value||fieldEditor.getFieldJson().options[j].value.startsWith('NEW')) {
            fieldEditor.getFieldJson().options[j].value = new String(++maxValue);
          }
        }
      }
    }
    
    for (var i = this._fieldEditors.length - 1; i >= 0; i--) {
      var fieldEditor = this._fieldEditors[i];
      var name = fieldEditor.getFieldJson().name; 
      if (!name || name.startsWith('NEW')) {
        fieldEditor.getFieldJson().name = new String(++maxName);
      }
    }
    
    $super();
  },
  getOptionValue: function ($super,option) {
    switch (option.type) {
      case 'FORM':
        switch (option.name) {
          case 'form.fields':
            var fields = new Array();
            for (var i = 0, l = this._fieldEditors.length; i < l; i++) {
              if (!this._fieldEditors[i].isMarkedForDeletion())
                fields.push(this._fieldEditors[i].getFieldJson());
            }
            
            return this.filterJsonSerializedSetting(Object.toJSON(fields));
          break;
          default:
            throw new Error("Unrecognized FORM option: " + option.name);
          break;
        } 
      break;
      default:
        return $super(option);
      break;
    }
  },
  _openAddFieldDialog: function () {
    var holder = new Element("div", { className: "queryEditorFormFieldEditorHolder" });
    this._fieldEditorsContainer.appendChild(holder);
    
    var overlay = new S2.UI.Overlay();
    $(document.body).insert(overlay);    
    
    var createDialog = new Element("div", { className: "queryEditorFormFieldCreate" });
    createDialog.appendChild(new Element("span").update(getLocale().getText('panelAdmin.block.query.formFieldCreateHelp')));
    
    var createDialogFieldButtons = new Element("div", { className: "queryEditorFormFieldCreateDialogFieldButtons" });
    var createDialogButtons = new Element("div", { className: "queryEditorFormFieldCreateDialogButtons" });

    var textButton = new Element("button", {
      className: "queryEditorFormFieldCreateButton",
      fieldType: 'TEXT'
    }).update(getLocale().getText('panelAdmin.block.query.formFieldCreateTextFieldButton'));
    
    var memoButton = new Element("button", {
      className: "queryEditorFormFieldCreateButton",
      fieldType: 'MEMO'
    }).update(getLocale().getText('panelAdmin.block.query.formFieldCreateMemoFieldButton'));
    
    var listButton = new Element("button", {
      className: "queryEditorFormFieldCreateButton",
      fieldType: 'LIST'
    }).update(getLocale().getText('panelAdmin.block.query.formFieldCreateListFieldButton'));

    createDialogFieldButtons.appendChild(textButton);
    createDialogFieldButtons.appendChild(memoButton);
    createDialogFieldButtons.appendChild(listButton);
    
    var cancelButton = new Element("button", {
      className: "queryEditorFormFieldCreateCancelButton"
    }).update(getLocale().getText('panelAdmin.block.query.formFieldCreateCancelButton'));
    
    Event.observe(cancelButton, "click", function (event) {
      holder.remove();
      overlay.destroy();
    });
    
    createDialogButtons.appendChild(cancelButton);
    
    createDialog.appendChild(createDialogFieldButtons);
    createDialog.appendChild(createDialogButtons);

    var _this = this;
    var buttonListener = function (event) {
      Event.stop(event);
      
      var button = Event.element(event);
      
      createDialog.select("button").invoke("purge");
      var editor = null;
      
      switch (button.getAttribute("fieldType")) {
        case 'TEXT':
          editor = new QueryEditorFormPageTextFieldEditor(this, {
            type: 'TEXT',
            caption: ''
          });
        break;
        case 'MEMO':
          editor = new QueryEditorFormPageMemoFieldEditor(this, {
            type: 'MEMO',
            caption: ''
          });
        break;
        case 'LIST':
          editor = new QueryEditorFormPageListFieldEditor(this, {
            type: 'LIST',
            caption: '',
            options: new Array()
          });
        break;
      }

      _this._fieldEditors.push(editor);
      editor.replaceToDom(holder);
      editor.openFieldSettings();
      overlay.destroy();
    };
    
    Event.observe(textButton, "click", buttonListener);
    Event.observe(memoButton, "click", buttonListener);
    Event.observe(listButton, "click", buttonListener);
    
    holder.appendChild(createDialog);
  },
  _disableEditor: function () {
    this._addFieldLink.hide();
    for (var i = 0, l = this._fieldEditors.length; i < l; i++) {
      this._fieldEditors[i].disableEditor();
    }
  },
  _onAddFieldLink: function (event) {
    this._openAddFieldDialog();
  }
});

QueryEditorQuestionEditor = Class.create(QueryEditorPageEditor, {
  initialize: function ($super,blockController) {
    $super(blockController);
    
    this._questionOptionsLinkClickListener = this._onQuestionOptionsLinkClick.bindAsEventListener(this);
  },
  deinitialize: function($super) {
    $super();
    
    this._questionContainer.remove();

    Event.stopObserving(this._questionOptionsLink, "click", this._questionOptionsLinkClickListener);
    this._removeOptionsEditor();
  },
  getOptionEditor: function (name) {
    for (var i = this._optionEditors.length - 1; i >= 0; i--) {
      if (this._optionEditors[i].getName() == name) {
        return this._optionEditors[i];
      }
    }
    
    return null;
  },
  setup: function ($super) {
    $super();

    this._optionEditors = new Array();
    
    this._questionContainer = new Element("div", {
      className: "queryEditorQuestionContainer"
    });

    this._questionOptionsLink = new Element("a", {
      href: "javascript:void(null);",
      className: "queryEditorPreviewShowOptionsLink"
    }).update(getLocale().getText("panelAdmin.block.query.showOptions"));
    
    this._optionsContainer = new Element("div", {
      className: "queryEditorQuestionOptions",
      style: "display:none"
    });
    
    this._optionsArrowUpContainer = new Element("div", {
        className: "queryEditorQuestionOptionsArrowUp"
      });
    
    this._questionContainer.appendChild(this._questionOptionsLink);
    this._questionContainer.appendChild(this._optionsContainer);
    this._optionsContainer.appendChild(this._optionsArrowUpContainer);
    
    this.getBlockController().appendFormField(this._questionContainer);
    
    Event.observe(this._questionOptionsLink, "click", this._questionOptionsLinkClickListener);
  
    this._buildOptionsEditor();
    this.appendSettingsContainer();
  },
  getOptionValue: function ($super, option) {
    switch (option.type) {
      case 'QUESTION':
        var optionElement = this._optionsContainer.down('input[name="' + option.name + '"]');
        
        if (!optionElement) {
          optionElement = this._optionsContainer.down('select[name="' + option.name + '"]');
        }
        
        if (!optionElement) {
          optionElement = this._optionsContainer.down('textarea[name="' + option.name + '"]');
        }
        
        if (optionElement) {
          return optionElement.value;
        }
      break;
      default:
        return $super(option);
      break;
    }
  },
  _removeOptionsEditor: function() {
    var children = this._optionsContainer.childNodes;
    for (var i = children.length - 1; i >= 0; i--) {
      var child = $(children[i]);
      if (child.nodeType == 1) {
        child.purge();
        child.remove();
      }
    }
  },
  _optionsVisible: function () {
    return this._optionsContainer.visible();
  },
  _showOptions: function () {
    this._optionsContainer.setStyle({
      display: ''
    });
    
    this._questionOptionsLink.update(getLocale().getText("panelAdmin.block.query.hideOptions"));
  },
  _hideOptions: function () {
    this._optionsContainer.setStyle({
      display: 'none'
    });
    
    this._questionOptionsLink.update(getLocale().getText("panelAdmin.block.query.showOptions"));
  },
  _buildOptionsEditor: function () {
    while (this._optionEditors.length > 0) {
      this._optionEditors.pop().deinitialize();
    }
    
    var pageData = this.getBlockController().getCurrentPageData();
    if (pageData) {
      var pageOptions = pageData.options;
      for ( var optionIndex = 0, optionsCount = pageOptions.length; optionIndex < optionsCount; optionIndex++) {
        var editor = null;
        
        if (pageData.options[optionIndex].type == 'QUESTION') {
  
          switch (pageData.options[optionIndex].editor) {
            case 'TEXT':
              editor = new QueryOptionTextEditor(this, pageData.options[optionIndex].caption, pageData.options[optionIndex].name, pageData.options[optionIndex].value);
            break;
            case 'MEMO':
              editor = new QueryOptionMemoEditor(this, pageData.options[optionIndex].caption, pageData.options[optionIndex].name, pageData.options[optionIndex].value);
            break;
            case 'INTEGER':
              editor = new QueryOptionIntegerEditor(this, pageData.options[optionIndex].caption, pageData.options[optionIndex].name, pageData.options[optionIndex].value);
            break;
            case 'FLOAT':
              editor = new QueryOptionFloatEditor(this, pageData.options[optionIndex].caption, pageData.options[optionIndex].name, pageData.options[optionIndex].value);
            break;
            case 'BOOLEAN':
              editor = new QueryOptionBooleanEditor(this, pageData.options[optionIndex].caption, pageData.options[optionIndex].name, pageData.options[optionIndex].value);
            break;
            case 'SPREADSHEET':
              // TODO
            break;          
            case 'TIME_SERIE_DATA':
              editor = new QueryOptionTimeSerieDataEditor(this, pageData.options[optionIndex].caption, pageData.options[optionIndex].name, pageData.options[optionIndex].value);
            break;
            case 'OPTION_SET':
              editor = new QueryOptionOptionSetEditor(this, pageData.options[optionIndex].caption, pageData.options[optionIndex].name, pageData.options[optionIndex].value);
            break;
            case 'TIMELINE_TYPE':
              editor = new QueryOptionTimelineTypeEditor(this, pageData.options[optionIndex].caption, pageData.options[optionIndex].name, pageData.options[optionIndex].value);
            break;
            case 'SCALE1D_TYPE':
              editor = new QueryOptionScale1DEditor(this, pageData.options[optionIndex].caption, pageData.options[optionIndex].name, pageData.options[optionIndex].value);
            break;
            case 'SCALE2D_TYPE':
              editor = new QueryOptionScale2DEditor(this, pageData.options[optionIndex].caption, pageData.options[optionIndex].name, pageData.options[optionIndex].value);
            break;
            case 'HIDDEN':
              editor = new QueryOptionHiddenEditor(this, pageData.options[optionIndex].caption, pageData.options[optionIndex].name, pageData.options[optionIndex].value);
            break;
            
          }
          
          if (editor != null) {
            this._optionsContainer.appendChild(editor.getDomNode());
            editor.setup();
            
            if ((pageData.hasAnswers == "true") && (pageData.options[optionIndex].editableWithAnswers != true)) {
              editor.disable();
            }
            
            editor.addListener("valueChange", this, function (event) {
              if (!this.fire("optionEditorValueChange", {
                  value: event.value,
                  name: event.name,
                  editorElement: event.editorElement
                })) {
                event.stop();
              }
            });
            
            this._optionEditors.push(editor);
          }
        }
      }
    }
  },
  _createMemoFormField: function (name, title, value) {
    var fieldId = new Date().getTime();
    
    var formFieldContainer = new Element("div", {
      'className': "formFieldContainer formTextFieldContainer"
    });
    
    var formFieldLabel = new Element("label", {
      'className': "formFieldLabel",
      'for': fieldId
    }).update(title);
    
    var formFieldTextArea = new Element("textarea", {
      'id': fieldId,
      'name': name,
      'class': 'formField formMemoField'
    }).update(value);
    
    formFieldContainer.appendChild(formFieldLabel);
    formFieldContainer.appendChild(formFieldTextArea);
    
    return formFieldContainer;
  },
  _onQuestionOptionsLinkClick: function (event) {
    if (this._optionsVisible())
      this._hideOptions();
    else
      this._showOptions();
  }
});

QueryEditorThesisPageEditor = Class.create(QueryEditorQuestionEditor, {
  initialize: function ($super,blockController) {
    $super(blockController);
  },
  deinitialize: function($super) {
    $super();
    
    this._thesisTextContainer.remove();
    this._showLiveReportContainer.remove();
    this._commentableContainer.remove();
    this._viewDiscussionsContainer.remove();
    
    this._thesisTextInputButtonInputComponent.deinitialize();
    if (this._thesisDescriptionEditor) {
      $(this._thesisDescriptionEditor.element).remove();
      this._thesisDescriptionEditor.destroy();
      this._thesisDescriptionEditor = undefined;
    }
    
    this._previewContainer.remove();
  },
  setup: function ($super) {
    $super();

    var commentableOption = this.getBlockController().getPageOption('thesis.commentable');
    this._commentableContainer = this._createCheckBoxField('thesis.commentable', commentableOption.caption, "1" == commentableOption.value);
    this.addSettingField(this._commentableContainer);
    this._commentableInput = this._commentableContainer.down('input');
    
    var viewDiscussionsOption = this.getBlockController().getPageOption('thesis.viewDiscussions');
    this._viewDiscussionsContainer = this._createCheckBoxField('thesis.viewDiscussions', viewDiscussionsOption.caption, "1" == viewDiscussionsOption.value);
    this.addSettingField(this._viewDiscussionsContainer);
    this._viewDiscussionsInput = this._viewDiscussionsContainer.down('input');

    var showLiveReportOption = this.getBlockController().getPageOption('thesis.showLiveReport');
    this._showLiveReportContainer = this._createCheckBoxField('showLiveReport', showLiveReportOption.caption, "1" == showLiveReportOption.value);
    this.addSettingField(this._showLiveReportContainer);
    this._showLiveReportInput = this._showLiveReportContainer.down('input');
    
    this._thesisTextContainer = this._createMemoFormField('thesis.text', this.getBlockController().getPageOption('thesis.text').caption, this.getBlockController().getPageOptionValue("thesis.text"));
//    this._thesisTextContainer.down('textarea').addClassName('keepEnabled');
    this.getBlockController().appendFormField(this._thesisTextContainer);
    this._thesisText = this._thesisTextContainer.down('textarea');
    
    this._titleContainer.insert({
      after: this._thesisTextContainer
    });
    
    var thesisDescriptionContainer = new Element("textarea", {
      name: 'thesis.description'
    }).update(this.getBlockController().getPageOptionValue('thesis.description'));
    this.getBlockController().appendFormField(thesisDescriptionContainer);
    
    this._thesisTextContainer.insert({
      after: thesisDescriptionContainer
    });
    
    var panelId = JSDATA['securityContextId'];
    this._thesisDescriptionEditor = CKEDITOR.replace(thesisDescriptionContainer, {
      toolbar: "thesisDescriptionToolbar",
      autoGrow_minHeight: 100,
      fniGenericBrowser:{
        enabledInDialogs: ['image', 'link'],
        connectorUrl: CONTEXTPATH + '/system/ckbrowserconnector.json?panelId=' + panelId
      }
    });

    this._thesisTextInputButtonInputComponent = new ButtonInputComponent(this._thesisText);
    
    this._previewContainer = new Element("div", {
      className: "queryEditorQuestionPreviewContainer"
    });
    
    this._questionContainer.insert({
      top: this._previewContainer
    });
  },
  getOptionValue: function ($super, option) {
    switch (option.type) {
      case 'THESIS':
        switch (option.name) {
          case 'thesis.text':
            return this._thesisText.value;
          break;
          case 'thesis.description':
            return this._thesisDescriptionEditor.getData();
          break;
          case 'thesis.showLiveReport':
            return this._showLiveReportInput.checked ? 1 : 0;
          break;
          case 'thesis.commentable':
            return this._commentableInput.checked ? 1 : 0;
          break;
          case 'thesis.viewDiscussions':
            return this._viewDiscussionsInput.checked ? 1 : 0;
          break;
          default:
            throw new Error("Unrecognized THESIS option: " + option.name);
          break;
        } 
      break;
      default:
        return $super(option);
      break;
    }
  }
});

QueryEditorScale1DThesisPageEditor = Class.create(QueryEditorThesisPageEditor, {
  initialize: function ($super,blockController) {
    $super(blockController);
  },
  deinitialize: function($super) {
    $super();
    
    this._preview.deinitialize();
  },
  setup: function ($super) {
    $super();
    
    this._preview = new QueryEditorScale1DQuestionPreview(this, this._previewContainer);
    this._preview.setup();
  }
});

QueryEditorScale2DThesisPageEditor = Class.create(QueryEditorThesisPageEditor, {
  initialize: function ($super,blockController) {
    $super(blockController);
  },
  deinitialize: function($super) {
    $super();
    
    this._preview.deinitialize();
  },
  setup: function ($super) {
    $super();
    
    this._preview = new QueryEditorScale2DQuestionPreview(this, this._previewContainer);
    this._preview.setup();
  }
});

QueryEditorTimeSerieThesisPageEditor = Class.create(QueryEditorThesisPageEditor, {
  initialize: function ($super,blockController) {
    $super(blockController);
  },
  deinitialize: function($super) {
    $super();
    
    this._preview.deinitialize();
  },
  setup: function ($super) {
    $super();
    
    this._preview = new QueryEditorTimeSerieQuestionPreview(this, this._previewContainer);
    this._preview.setup();
  }
});

QueryEditorTimelineThesisPageEditor = Class.create(QueryEditorThesisPageEditor, {
  initialize: function ($super,blockController) {
    $super(blockController);
  },
  deinitialize: function($super) {
    $super();
    
    this._preview.deinitialize();
  },
  setup: function ($super) {
    $super();
    
    this._preview = new QueryEditorTimelineQuestionPreview(this, this._previewContainer);
    this._preview.setup();
    
    var selectedType = 0;
    var pageData = this.getBlockController().getCurrentPageData();
    if (pageData) {
      var pageOptions = pageData.options;
      for ( var optionIndex = 0, optionsCount = pageOptions.length; optionIndex < optionsCount; optionIndex++) {
        if (pageData.options[optionIndex].name == 'timeline.type') {
          selectedType = parseInt(this.getOptionValue(pageData.options[optionIndex]));
          break;
        }
      }
    }
    
    this._updateSelectedType(selectedType);
    
    this.addListener("optionEditorValueChange", function (event) {
      if (event.name == 'timeline.type') {
        this._updateSelectedType(parseInt(event.value));
      }
    });
  },
  _updateSelectedType: function (type) {
    var value2Element = this._optionsContainer.down('input[name="timeline.value2Label"]').parentNode;
    
    value2Element.setStyle({
      display: type === 0 ? 'none' : 'block'
    });
  }
});

QueryEditorExpertisePageEditor = Class.create(QueryEditorPageEditor, {
  initialize: function ($super, blockController) {
    $super(blockController);
  },
  deinitialize: function($super) {
    this._showLiveReportContainer.remove();
    
    $super();

    $(this._expertiseDescriptionEditor.element).remove();
    this._expertiseDescriptionEditor.destroy();
    this._expertiseDescriptionEditor = undefined;

    this._preview.deinitialize();
    this._previewContainer.remove();  
  },
  setup: function ($super) {
    $super();
    
    var showLiveReportOption = this.getBlockController().getPageOption('expertise.showLiveReport');
    this._showLiveReportContainer = this._createCheckBoxField('showLiveReport', showLiveReportOption.caption, "1" == showLiveReportOption.value);
    this.addSettingField(this._showLiveReportContainer);
    this._showLiveReportInput = this._showLiveReportContainer.down('input');

    // Expertise description
    
    var expertiseDescriptionContainer = new Element("textarea", {
      name: 'expertise.description'
    }).update(this.getBlockController().getPageOptionValue('expertise.description'));
    this.getBlockController().appendFormField(expertiseDescriptionContainer);
    var panelId = JSDATA['securityContextId'];
    this._expertiseDescriptionEditor = CKEDITOR.replace(expertiseDescriptionContainer, {
      toolbar: "expertiseDescriptionToolbar",
      autoGrow_minHeight: 100,
      fniGenericBrowser:{
        enabledInDialogs: ['image', 'link'],
        connectorUrl: CONTEXTPATH + '/system/ckbrowserconnector.json?panelId=' + panelId
      }
    });

    this._previewContainer = new Element("div", {
      className: "queryEditorQuestionPreviewContainer"
    });
    
    this.getBlockController().appendFormField(this._previewContainer);

    this._preview = new QueryEditorExpertsQuestionPreview(this, this._previewContainer);
    this._preview.setup();    
    
    this.appendSettingsContainer();
  },
  getOptionValue: function ($super, option) {
    switch (option.type) {
      case 'QUESTION':
        switch (option.name) {
          case 'expertise.showLiveReport':
            return this._showLiveReportInput.checked ? 1 : 0;
          break;
          case 'expertise.description':
            return this._expertiseDescriptionEditor.getData();
          break;
          default:
            throw new Error("Unrecognized QUESTION option: " + option.name);
          break;
        } 
      break;
      default:
        return $super(option);
      break;
    }
  }
});

QueryEditorMultiselectPageEditor = Class.create(QueryEditorThesisPageEditor, {
  initialize: function ($super,blockController) {
    $super(blockController);
  },
  deinitialize: function($super) {
    $super();
    
    this._preview.deinitialize();
  },
  setup: function ($super) {
    $super();
    
    this._preview = new QueryEditorMultiselectQuestionPreview(this, this._previewContainer);
    this._preview.setup();
  }
});

QueryEditorOrderingPageEditor = Class.create(QueryEditorThesisPageEditor, {
  initialize: function ($super,blockController) {
    $super(blockController);
  },
  deinitialize: function($super) {
    $super();
    
    this._preview.deinitialize();
  },
  setup: function ($super) {
    $super();
    
    this._preview = new QueryEditorOrderingQuestionPreview(this, this._previewContainer);
    this._preview.setup();
  }
});

QueryEditorGroupingPageEditor = Class.create(QueryEditorThesisPageEditor, {
  initialize: function ($super,blockController) {
    $super(blockController);
  },
  deinitialize: function($super) {
    $super();
    
    this._preview.deinitialize();
  },
  setup: function ($super) {
    $super();
    
    this._preview = new QueryEditorGroupingQuestionPreview(this, this._previewContainer);
    this._preview.setup();
  }
});

QueryEditorCollage2DPageEditor = Class.create(QueryEditorQuestionEditor, {
  initialize: function ($super, blockController) {
    $super(blockController);
    
    this._pageSelectClickListener = this._onPageSelectClick.bindAsEventListener(this);
    this._pageColorClickListener = this._onPageColorClick.bindAsEventListener(this);
    this._colorDialogItemClickListener = this._onColorDialogItemClick.bindAsEventListener(this);

    this._colors = ['#FF0000', '#FF8000', '#FFFF00', '#00FF00', '#0000FF', '#FF00FF', '#8000FF', '#800000', '#808000', '#008000', '#008080', '#000080', '#000000', '#303030', '#808080', '#C0C0C0'];
  },
  deinitialize: function($super) {
    $super();
    
    this._pagesContainer.select('.queryEditorCollage2DPageSelect').invoke("stopObserving", "click", this._pageSelectClickListener);
    this._pagesContainer.select('.queryEditorCollage2DPageColor').invoke("stopObserving", "click", this._pageColorClickListener);

    this._pagesContainer.remove();
  },
  setup: function ($super) {
    $super();

    var includedPages = QueryEditorUtils.parseSerializedList(this.getBlockController().getPageOptionValue('collage2d.includedPages'));
    for (var i = 0, l = includedPages.length; i < l; i++) {
      includedPages[i] = parseInt(includedPages[i]);
    };
    
    var pageSettings = $H(QueryEditorUtils.parseSerializedMap(this.getBlockController().getPageOptionValue('collage2d.pageSettings')));
    pageSettings.each(function (item) {
      pageSettings.set(item[0], $H(QueryEditorUtils.parseSerializedMap(item[1])));
    });
    
    this._pagesContainer = new Element("div", {
      className: "queryEditorCollage2DPagesContainer"
    });
    
    var _this = this;
    this.getBlockController().getPageDatasByType('THESIS_SCALE_2D').each(function (pageData) {
      _this._pagesContainer.appendChild(_this._createPageField(pageData, includedPages, pageSettings));
    });
    
    this._pagesContainer.select('.queryEditorCollage2DPageSelect').invoke("observe", "click", this._pageSelectClickListener);
    this._pagesContainer.select('.queryEditorCollage2DPageColor').invoke("observe", "click", this._pageColorClickListener);

    this._questionContainer.insert({
      top: this._pagesContainer
    });
    
    this.appendSettingsContainer();
    
    // Hide captions of serialized options collage2d.includedPages and collage2d.

    this.getOptionEditor('collage2d.includedPages').hideCaption();
    this.getOptionEditor('collage2d.pageSettings').hideCaption();
    
    // Replace hidden replySource, labelsVisible and replyCountVisible settings with selects 
    
    this._replaceSelectOptionEditor('collage2d.replySource', {
      "ALL": getLocale().getText("panelAdmin.block.query.collage2DReplySource.all"),
      "MANAGER": getLocale().getText("panelAdmin.block.query.collage2DReplySource.manager"),
      "OWN": getLocale().getText("panelAdmin.block.query.collage2DReplySource.own")
    });
    
    this._replaceSelectOptionEditor('collage2d.labelVisibility', {
      "NONE": getLocale().getText("panelAdmin.block.query.collage2DLabelsVisible.never"),
      "HOVER": getLocale().getText("panelAdmin.block.query.collage2DLabelsVisible.hover"),
      "ALWAYS": getLocale().getText("panelAdmin.block.query.collage2DLabelsVisible.always")
    });
    
    this._replaceSelectOptionEditor('collage2d.replyCountsVisible', {
      "FALSE": getLocale().getText("panelAdmin.block.query.collage2DReplyCountVisible.no"),
      "TRUE": getLocale().getText("panelAdmin.block.query.collage2DReplyCountVisible.yes")
    });
    
    this._checkSelectables(includedPages);
  },
  _replaceSelectOptionEditor: function (name, options) {
    var editor = this.getOptionEditor(name);
    var value = editor.getValue();
    
    var selectElement = new Element("select", {
      name: name
    });
    
    var opts = $H(options);
    var keys = opts.keys();
    
    for (var i = 0, l = keys.length; i < l; i++) {
      var key = keys[i];
      var option = new Element("option", {
        value: key
      });
      
      if (key == value) {
        option.writeAttribute("selected", "selected");
      }
      
      selectElement.appendChild(option).update(opts.get(key));
    }
    
    editor.replaceEditor(selectElement);
    
    Event.observe(selectElement, "change", function (event) {
      var editorElement = Event.element(event);
      this.fire("valueChange", {
        value: editorElement.value,
        name: this.getName(),
        editorElement: editorElement
      });
    }.bind(editor));
  },
  _createColorSelectDialog: function () {
    var dialog = new Element("div", {
      className: "queryEditorCollage2DColorSelectDialog"
    });
    
    this._colors.each(function (color) {
      dialog.appendChild(new Element("div", {
        className: "queryEditorCollage2DColorSelectDialogItem",
        style: 'background-color: ' + color
      }));
    });
    
    return dialog;
  },
  
  _createPageField: function (pageData, includedPages, pageSettings) {
    var result = new Element("div", {
      className: "queryEditorCollage2DPage"
    });

    var title = null;
    var thesis = null;
    var optionsX = null;
    var optionsY = null;
    
    for (var i = 0, l = pageData.options.length; i < l; i++) {
      switch (pageData.options[i].name) {
        case "title":
          title = pageData.options[i].value;
        break;
        case "thesis.text":
          thesis = pageData.options[i].value;
        break;
        case "scale2d.options.x":
          optionsX = QueryEditorUtils.parseSerializedList(pageData.options[i].value);
        break;
        case "scale2d.options.y":
          optionsY = QueryEditorUtils.parseSerializedList(pageData.options[i].value);
        break;
      }
      
      if (title && thesis && optionsX && optionsY) {
        break;
      }
    }
    
    var includedPage = includedPages.indexOf(pageData.id) != -1; 
    
    result.appendChild(new Element("input", {
      type: "hidden",
      name: "pageId",
      value: pageData.id
    }));
    
    result.appendChild(new Element("input", {
      type: "hidden",
      name: "axes",
      value: [optionsX.length, optionsY.length].join('-')
    }));
    
    result.appendChild(this._createGraph(optionsX, optionsY));
    
    var selectElement = new Element("input", {
      type: "checkbox",
      className: "queryEditorCollage2DPageSelect"
    });
    
    if (includedPage) {
      selectElement.writeAttribute("checked", "checked");
    }
    
    result.appendChild(selectElement);
    
    var colorElement = new Element("div", {
      className: "queryEditorCollage2DPageColor"
    });
    
    var settings = pageSettings.get(new String(pageData.id));
    if (settings) {
      var color = settings.get("color")||'#aaaaaa';
      colorElement.setStyle({
        backgroundColor: color
      });
    }
    
    result.appendChild(colorElement);
    
    result.appendChild(new Element("div", {
      className: "queryEditorCollage2DPageTitle"
    }).update(title));
    
    result.appendChild(new Element("div", {
      className: "queryEditorCollage2DPageThesis"
    }).update(thesis));
    
    return result;
  },
  
  _createGraph: function (optionsX, optionsY) {
    var graph = new Element("div", {
      className: "queryEditorCollage2DPageGraph"
    });
    var countX = optionsX.length;
    var countY = optionsY.length;

    var sizeW = 100 / (countX + 1);
    var sizeH = 100 / (countY + 1); 
    
    for (var x = 1, xs = countX; x <= xs; x++) {
      var dot = new Element("div", {
        className: "queryEditorCollage2DPageGraphDotHorizontal"
      });
      
      dot.setStyle({
        left: (sizeW * x) + '%'
      });
      
      graph.appendChild(dot);
    }
    
    for (var y = 1, ys = countY; y <= ys; y++) {
      var dot = new Element("div", {
        className: "queryEditorCollage2DPageGraphDotVertical"
      });
      
      dot.setStyle({
        top: (sizeH * y) + '%'
      });
      
      graph.appendChild(dot);
    }
    
    return graph;
  },
  
  _getIncludedPages: function () {
    var includedPages = new Array();
    
    this._pagesContainer.select('.queryEditorCollage2DPageSelect').each(function (selectElement) {
      if (selectElement.checked) {
        includedPages.push(selectElement.up('.queryEditorCollage2DPage').down('input[name="pageId"]').value);
      }
    });
    
    return includedPages;
  },
  _getPageElementById: function (pageId) {
    var pageIdInputs = this._pagesContainer.select('input[name="pageId"]');
    for (var i = 0, l = pageIdInputs.length; i < l; i++) {
      if (pageIdInputs[i].value == pageId) {
        return pageIdInputs[i].up('.queryEditorCollage2DPage');
      }
    }
    
    return null;
  },
  _getAxesByPageId: function (pageId) {
    return this._getPageElementById(pageId).down('input[name="axes"]').value;
  },
  
  _checkSelectables: function (includedPages) {
    if (includedPages.length == 0) {
      var selects = this._pagesContainer.select('.queryEditorCollage2DPageSelect');
      selects.invoke("writeAttribute", 'disabled', null);
      selects.invoke("writeAttribute", 'checked', null);
      var pages = this._pagesContainer.select('.queryEditorCollage2DPage');
      pages.invoke("removeClassName", 'queryEditorCollage2DPageIncompatible');
    } else {
      var accpetedAxes = this._getAxesByPageId(includedPages[0]);
      
      this._pagesContainer.select('input[name="axes"]').each(function (axesElement) {
        var page = axesElement.up('.queryEditorCollage2DPage');
        var select = page.down('.queryEditorCollage2DPageSelect');
        
        if (axesElement.value == accpetedAxes) {
          page.removeClassName('queryEditorCollage2DPageIncompatible');
          select.writeAttribute("disabled", null);
        } else {
          page.addClassName('queryEditorCollage2DPageIncompatible');
          select.writeAttribute("disabled", "disabled");
          select.writeAttribute("checked", null);
        }
      });
    }
  },
  
  _updatePageColor: function (pageId, color) {
    this._getPageElementById(pageId).down('.queryEditorCollage2DPageColor').setStyle({
      'backgroundColor': color
    });
    
    var pageSettings = QueryEditorUtils.parseSerializedMap(this.getOptionEditor('collage2d.pageSettings').getEditor().value);
    
    var settings = QueryEditorUtils.parseSerializedMap(pageSettings[pageId]);
    settings['color'] = color;
    pageSettings[pageId] = QueryEditorUtils.serializeMap(settings);
    
    this.getOptionEditor('collage2d.pageSettings').setValue(QueryEditorUtils.serializeMap(pageSettings));
  },

  _onPageSelectClick: function (event) {
    var includedPages = this._getIncludedPages();
    
    this.getOptionEditor('collage2d.includedPages').setValue(QueryEditorUtils.serializeList(includedPages));
    this._checkSelectables(includedPages);
  },
  
  _onPageColorClick: function (event) {
    var colorElement = Event.element(event);
    var pageElement = colorElement.up('.queryEditorCollage2DPage');
    var dialog = this._createColorSelectDialog();
    
    pageElement.insert({
      top: dialog
    });
    
    dialog.close = function () {
      this.select('.queryEditorCollage2DColorSelectDialogItem').invoke("purge");
      Event.stopObserving(window, "mousedown", this._onWindowWouseDown);
      this.remove();
    };
    
    dialog._onWindowWouseDown = function (event) {
      var element = Event.element(event);
      if (!element.hasClassName('queryEditorCollage2DColorSelectDialog') && !element.up('.queryEditorCollage2DColorSelectDialog')) {
        dialog.close();
      }
    };
    
    dialog.select('.queryEditorCollage2DColorSelectDialogItem').invoke("observe", "click", this._colorDialogItemClickListener);
    
    Event.observe(window, "mousedown", dialog._onWindowWouseDown);
  },
  
  _onColorDialogItemClick: function (event) {
    var item = Event.element(event);
    var color = item.getStyle('background-color');
    var dialog = item.up('.queryEditorCollage2DColorSelectDialog');
    var pageId = dialog.up('.queryEditorCollage2DPage').down('input[name="pageId"]').value;
    this._updatePageColor(pageId, color);
    dialog.close();
  }
});