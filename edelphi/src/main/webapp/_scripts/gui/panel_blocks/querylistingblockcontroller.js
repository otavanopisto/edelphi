QueryListingBlockController = Class.create(BlockController, {
  initialize: function ($super) {
    $super();
    this._hideLinkClickListener = this._onHideLinkClick.bindAsEventListener(this);
    this._showLinkClickListener = this._onShowLinkClick.bindAsEventListener(this);
    this._deleteClickListener = this._onDeleteClick.bindAsEventListener(this);
    this._copyClickListener = this._onCopyClick.bindAsEventListener(this);
  },
  setup: function ($super) {
    $super($('panelQueryListingBlockContent'));

    var _this = this;
    this.getBlockElement().select('.blockContextualLink.hide').each(function (linkElement) {
      Event.observe(linkElement, "click", _this._hideLinkClickListener);
    });
    
    this.getBlockElement().select('.blockContextualLink.show').each(function (linkElement) {
      Event.observe(linkElement, "click", _this._showLinkClickListener);
    });

    this.getBlockElement().select('.blockContextualLink.delete').each(function (linkElement) {
      Event.observe(linkElement, "click", _this._deleteClickListener);
    });

    this.getBlockElement().select('.blockContextualLink.copy').each(function (linkElement) {
      Event.observe(linkElement, "click", _this._copyClickListener);
    });
  },
  deinitialize: function ($super) {
    $super();
    this.getBlockElement().select('.blockContextualLink.hide').invoke("purge");
    this.getBlockElement().select('.blockContextualLink.show').invoke("purge");
    this.getBlockElement().select('.blockContextualLink.delete').invoke("purge");
    this.getBlockElement().select('.blockContextualLink.copy').invoke("purge");
  },
  _onHideLinkClick: function (event) {
    Event.stop(event);

    var linkElement = Event.element(event);
    var linkHref = linkElement.getAttribute("href");
    var hashParams = this._parseHash(linkHref);
    var queryId = hashParams.get("queryId");
    
    JSONUtils.request(CONTEXTPATH + '/resources/setresourcevisibility.json', {
      parameters: {
        resourceId: queryId,
        visible: 0
      },
      onSuccess : function(jsonRequest) {
        var linksContainer = linkElement.up('.contextualLinks');
        linksContainer.down('.hide').addClassName("blockContextualLinkHidden");
        linksContainer.down('.show').removeClassName("blockContextualLinkHidden");
      }
    });
  },
  _onShowLinkClick: function (event) {
    Event.stop(event);
    
    var linkElement = Event.element(event);
    var linkHref = linkElement.getAttribute("href");
    var hashParams = this._parseHash(linkHref);
    var queryId = hashParams.get("queryId");
    
    JSONUtils.request(CONTEXTPATH + '/resources/setresourcevisibility.json', {
      parameters: {
        resourceId: queryId,
        visible: 1
      },
      onSuccess : function(jsonRequest) {
        var linksContainer = linkElement.up('.contextualLinks');
        linksContainer.down('.show').addClassName("blockContextualLinkHidden");
        linksContainer.down('.hide').removeClassName("blockContextualLinkHidden");
      }
    });
  },
  _onDeleteClick: function (event) {
    Event.stop(event);

    var linkElement = Event.element(event);
    var linkHref = linkElement.getAttribute("href");
    var hashParams = this._parseHash(linkHref);
    var queryId = hashParams.get("queryId");
    
    var titleContainer = new Element("div", { className: "modalPopupTitleContent" });
    titleContainer.update(getLocale().getText('panelAdmin.block.query.listing.deleteQueryDialogText'));
    
    var popup = new ModalPopup({
      content: titleContainer,
      buttons: [
        {
          text: getLocale().getText('panelAdmin.block.query.listing.deleteQueryDialogCancelButton'),
          action: function(instance) {
            instance.close();
          }
        },
        {
          text: getLocale().getText('panelAdmin.block.query.listing.deleteQueryDialogDeleteButton'),
          classNames: "modalPopupButtonRed",
          action: function(instance) {
            instance.close();
    
            JSONUtils.request(CONTEXTPATH + '/resources/archiveresource.json', {
              parameters: {
                resourceId: queryId
              },
              onSuccess : function(jsonRequest) {
                var queryElement = linkElement.up(".panelAdminQueryRow");
                if (queryElement != null)
                  queryElement.remove();
                else {
                  queryElement = linkElement.up(".panelQueryRow");
                  if (queryElement != null)
                    queryElement.remove();
                }
              }
            });
          }
        }
      ]
    });

    popup.open(linkElement);
  },
  _onCopyClick: function (event) {
    Event.stop(event);
    var linkElement = Event.element(event);
    var linkHref = linkElement.getAttribute("href");
    var hashParams = this._parseHash(linkHref);
    var queryId = hashParams.get("queryId");
    
    var _this = this;
    JSONUtils.request(CONTEXTPATH + '/admin/listavailablepanels.json', {
      parameters: {
      },
      onSuccess : function(jsonRequest) {
        var dialog = _this._buildCopyQueryDialog(queryId, jsonRequest.panels, jsonRequest.currentPanel);
        dialog.open(linkElement);
      }
    });
  },
  _buildCopyQueryDialog: function (queryId, panels, currentPanel) {
    var contentContainer = new Element("form", { id: "copyQueryForm", className: "copyQueryModalTextContainer" });
    var queryElement = new Element("input", { type: "hidden", name: "query", value: queryId });
    var titleElement = new Element("div", { className: "modalPopupTitleContent" });
    titleElement.update(getLocale().getText('panelAdmin.block.query.listing.copyQueryDialogText'));
    var infoElement = new Element("div", { className: "modalPopupDescriptionContent" });
    infoElement.update(getLocale().getText('panelAdmin.block.query.listing.copyQueryDialogDescription'));
    var radioContainer = new Element("div", { className: "modalPopupRadioButtonContainer" });
    var radioSection1 = new Element("div", { className: "modalPopupRadioButtonSection" });
    var inputWithDataElement = new Element("input", { id: "copyQueryWithData", type: "radio", value: 'true', name: "copyData", 'checked': 'checked' });
    var labelWithDataElement = new Element("label", { 'for': "copyQueryWithData" });
    labelWithDataElement.update(getLocale().getText('panelAdmin.block.query.listing.copyQueryDialogWithDataLabel'));
    var radioSection2 = new Element("div", { className: "modalPopupRadioButtonSection" });
    var inputWithoutDataElement = new Element("input", { id: "copyQueryWithoutData", type: "radio", value: 'false', name: "copyData" });
    var labelWithoutDataElement = new Element("label", { 'for': "copyQueryWithoutData" });
    labelWithoutDataElement.update(getLocale().getText('panelAdmin.block.query.listing.copyQueryDialogWithoutDataLabel'));
    var nameContainer = new Element("div", { className: "modalPopupTextFieldContainer" });
    var labelNameElement = new Element("label");
    labelNameElement.update(getLocale().getText('panelAdmin.block.query.listing.copyQueryDialogNewNameFieldLabel'));
    var inputNameElement = new Element("input", { className: "modalPopupTextfield formField formTextField", type: "text", value: "", name: "name" });
    var panelContainer = new Element("div", { className: "modalPopupSelectContainer" });
    var labelPanelElement = new Element("label");
    labelPanelElement.update(getLocale().getText('panelAdmin.block.query.listing.copyQueryDialogTargetPanelFieldLabel'));
    var selectElement = new Element("select", { name: "panel" });
    for (var i = 0, l = panels.length; i < l; i++) {
      var panelOptionElement = new Element("option", { value: panels[i].id });
      if (panels[i].id == currentPanel) {
        panelOptionElement.writeAttribute('selected', 'selected');
      }
      panelOptionElement.update(panels[i].name);
      selectElement.appendChild(panelOptionElement);
    }
    
    contentContainer.appendChild(queryElement);
    contentContainer.appendChild(titleElement);
    contentContainer.appendChild(infoElement);
    contentContainer.appendChild(radioContainer);
    radioContainer.appendChild(radioSection1);
    radioSection1.appendChild(inputWithDataElement);
    radioSection1.appendChild(labelWithDataElement);
    radioContainer.appendChild(radioSection2);
    radioSection2.appendChild(inputWithoutDataElement);
    radioSection2.appendChild(labelWithoutDataElement);
    contentContainer.appendChild(nameContainer);
    nameContainer.appendChild(labelNameElement);
    nameContainer.appendChild(inputNameElement);
    contentContainer.appendChild(panelContainer);
    panelContainer.appendChild(labelPanelElement);
    panelContainer.appendChild(selectElement);

    var popup = new ModalPopup({
      width: 400,
      height: 320,
      content: contentContainer,
      buttons: [
        {
          text: getLocale().getText('panelAdmin.block.query.listing.copyQueryDialogCancelButton'),
          action: function(instance) {
            instance.close();
          }
        },
        {
          text: getLocale().getText('panelAdmin.block.query.listing.copyQueryDialogCopyButton'),
          classNames: "modalPopupButtonGreen",
          action: function(instance) {
            startLoadingOperation('panelAdmin.block.query.listing.copyingQuery');
            instance.close();
            
            var form = instance.getFrame().down("form[id='copyQueryForm']");
            var copyData = '';
            for (var i = 0; i < form.copyData.length; i++) {
              if (form.copyData[i].checked) {
                copyData = form.copyData[i].value;
                break;
              }
            }
            var query = form.query.value;
            var name = form.name.value;            
            var panel = form.panel.value;            
            
            JSONUtils.request(CONTEXTPATH + '/panel/admin/copyquery.json', {
              parameters: {
                copyData: copyData,
                query: query,
                name: name,
                panel: panel
              },
              onComplete : function(transport) {
                endLoadingOperation();
              },
              onSuccess: function (jsonRequest) {
                location.reload(true);
              }
            });
          }
        }
      ] 
    });
    return popup;
  }
});

addBlockController(new QueryListingBlockController());