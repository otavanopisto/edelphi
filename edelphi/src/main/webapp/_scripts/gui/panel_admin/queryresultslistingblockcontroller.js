QueryResultsListingBlockController = Class.create(BlockController, {
  initialize: function ($super) {
    $super();
    this._clearQueryDataLinkClickListener = this._onClearQueryDataLinkClick.bindAsEventListener(this);
    this._clearPageDataLinkClickListener = this._onClearPageDataLinkClick.bindAsEventListener(this);
    this._downloadOrExportLinkClickListener = this._onDownloadOrExportLinkClick.bindAsEventListener(this);
    this._downloadOrExportPageLinkClickListener = this._onDownloadOrExportPageLinkClick.bindAsEventListener(this);
  },
  setup: function ($super) {
    $super($('panelQueryResultsListingBlockContent'));

    var _this = this;
    this.getBlockElement().select('.deleteAllAnswers>a').each(function (link) {
      Event.observe(link, "click", _this._clearQueryDataLinkClickListener);
    }); 
    this.getBlockElement().select('.deletePageAnswers>a').each(function (link) {
      Event.observe(link, "click", _this._clearPageDataLinkClickListener);
    }); 
    this.getBlockElement().select('.downloadOrExportReport>a').each(function (link) {
        Event.observe(link, "click", _this._downloadOrExportLinkClickListener);
    });
    this.getBlockElement().select('.downloadOrExportPageReport>a').each(function (link) {
        Event.observe(link, "click", _this._downloadOrExportPageLinkClickListener);
    });    
  },
  deinitialize: function ($super) {
    var _this = this;
    this.getBlockElement().select('.deleteAllAnswers>a').each(function (link) {
      Event.stopObserving(link, "click", _this._clearQueryDataLinkClickListener);
    });
    this.getBlockElement().select('.deletePageAnswers>a').each(function (link) {
      Event.stopObserving(link, "click", _this._clearPageDataLinkClickListener);
    });

    $super();
  },
  _onClearQueryDataLinkClick: function (event) {
    Event.stop(event);

    var linkElement = Event.element(event);
    var linkHref = linkElement.getAttribute("href");
    var hashParams = this._parseHash(linkHref);
    var queryId = hashParams.get("queryId");
    var replyCount = hashParams.get("replyCount");
    
    var titleContainer = new Element("div", { className: "modalPopupTitleContent" });
    titleContainer.update(getLocale().getText('panelAdmin.block.queryResults.deleteQueryAnswersDialogText', [replyCount]));
    
    var popup = new ModalPopup({
      content: titleContainer,
      buttons: [
        {
          text: getLocale().getText('panelAdmin.block.queryResults.deleteQueryAnswersDialogCancelButton'),
          action: function(instance) {
            instance.close();
          }
        },
        {
          text: getLocale().getText('panelAdmin.block.queryResults.deleteQueryAnswersDialogDeleteButton'),
          classNames: "modalPopupButtonRed",
          action: function(instance) {
            startLoadingOperation('panelAdmin.block.queryResults.deletingQueryAnswers');
            instance.close(true);
            
            var panelId = JSDATA['securityContextId'];

            JSONUtils.request(CONTEXTPATH + '/panel/admin/removeanswers.json', {
              parameters: {
                queryId: queryId,
                panelId: panelId
              },
              onComplete : function(transport) {
                endLoadingOperation();
              },
              onSuccess: function (jsonResponse) {
                window.location.reload();
              }
            });
          }
        }
      ]
    });

    popup.open(linkElement);
  },
  _onClearPageDataLinkClick: function (event) {
    Event.stop(event);

    var linkElement = Event.element(event);
    var linkHref = linkElement.getAttribute("href");
    var hashParams = this._parseHash(linkHref);
    var queryPageId = hashParams.get("queryPageId");
    var replyCount = hashParams.get("replyCount");
    
    var titleContainer = new Element("div", { className: "modalPopupTitleContent" });
    titleContainer.update(getLocale().getText('panelAdmin.block.queryResults.deletePageAnswersDialogText', [replyCount]));
    
    var popup = new ModalPopup({
      content: titleContainer,
      buttons: [
        {
          text: getLocale().getText('panelAdmin.block.queryResults.deletePageAnswersDialogCancelButton'),
          action: function(instance) {
            instance.close();
          }
        },
        {
          text: getLocale().getText('panelAdmin.block.queryResults.deletePageAnswersDialogDeleteButton'),
          classNames: "modalPopupButtonRed",
          action: function(instance) {
            startLoadingOperation('panelAdmin.block.queryResults.deletingPageAnswers');
            instance.close(true);
            
            var panelId = JSDATA['securityContextId'];

            JSONUtils.request(CONTEXTPATH + '/panel/admin/removeanswers.json', {
              parameters: {
                queryPageId: queryPageId,
                panelId: panelId
              },
              onComplete : function(transport) {
                endLoadingOperation();
              },
              onSuccess: function (jsonResponse) {
                window.location.reload();
              }
            });
          }
        }
      ]
    });

    popup.open(linkElement);
  },
  
  _onDownloadOrExportLinkClick: function (event) {
    Event.stop(event);

    var linkElement = Event.element(event);
    var linkHref = linkElement.getAttribute("href");
    var hashParams = this._parseHash(linkHref);
    var contextPath = hashParams.get("contextPath");
    var queryId = hashParams.get("queryId");
    var panelId = hashParams.get("panelId");
    var stampId = hashParams.get("stampId");
    
    var titleContainer = new Element("div", { className: "modalPopupTitleContent" });
    titleContainer.update(getLocale().getText('panelAdmin.block.queryResults.downloadOrExportDialogTitle'));
    var linksContainer = new Element("div", { className: "modalPopupLinksContainer" });
    linksContainer.appendChild(new Element("h4").update(getLocale().getText('panelAdmin.block.queryResults.downloadOrExportDialogExportContentsTitle')));
    
    linksContainer.appendChild(
      new Element("a", { 
  	    className: "modalPopupLinkContainer exportPDF",
	      href: contextPath + "/queries/exportreport.binary?format=PDF&queryId=" + queryId + "&stampId=" + stampId
	    }).update(getLocale().getText('panelAdmin.block.queryResults.downloadOrExportDialogExportContentsPDF'))
    );
    
    linksContainer.appendChild(
	  new Element("a", { 
  		target: '_blank',
	      className: "modalPopupLinkContainer exportGoogleDrive",
	      href: contextPath + "/queries/exportreport.binary?format=GOOGLE_DOCUMENT&queryId=" + queryId + "&stampId=" + stampId
	    }).update(getLocale().getText('panelAdmin.block.queryResults.downloadOrExportDialogExportContentsGoogleDocument'))
  	);

    linksContainer.appendChild(new Element("h4").update(getLocale().getText('panelAdmin.block.queryResults.downloadOrExportDialogExportChartsTitle')));
    linksContainer.appendChild(
      new Element("a", { 
        className: "modalPopupLinkContainer exportPDF",
        href: contextPath + "/queries/exportreport.binary?format=PNG_ZIP&queryId=" + queryId + "&stampId=" + stampId
      }).update(getLocale().getText('panelAdmin.block.queryResults.downloadOrExportDialogExportChartsPNG'))
    );
    linksContainer.appendChild(
      new Element("a", { 
        className: "modalPopupLinkContainer exportPDF",
        href: contextPath + "/queries/exportreport.binary?format=SVG_ZIP&queryId=" + queryId + "&stampId=" + stampId
      }).update(getLocale().getText('panelAdmin.block.queryResults.downloadOrExportDialogExportChartsSVG'))
    );
    linksContainer.appendChild(
      new Element("a", { 
        target: '_blank',
          className: "modalPopupLinkContainer exportGoogleDrive",
          href: contextPath + "/queries/exportreport.binary?format=GOOGLE_IMAGES&queryId=" + queryId + "&stampId=" + stampId
        }).update(getLocale().getText('panelAdmin.block.queryResults.downloadOrExportDialogExportChartsGoogle'))
      );

    linksContainer.appendChild(new Element("h4").update(getLocale().getText('panelAdmin.block.queryResults.downloadOrExportDialogExportDataTitle')));

    var downloadCSVLink = new Element("a", { className: "modalPopupLinkContainer exportCSV" });
    downloadCSVLink.update(getLocale().getText('panelAdmin.block.queryResults.downloadOrExportDialogExportDataCSV'));
    downloadCSVLink.setAttribute("href",contextPath + "/queries/exportdata.binary?panelId=" + panelId + "&queryId=" + queryId + "&stampId=" + stampId + "&format=CSV");
    linksContainer.appendChild(downloadCSVLink);
    
    linksContainer.appendChild(
      new Element("a", { 
      	target: '_blank',
    	className: "modalPopupLinkContainer exportGoogle",
    	href: contextPath + "/queries/exportdata.binary?panelId=" + panelId + "&queryId=" + queryId + "&stampId=" + stampId + "&format=GOOGLE_SPREADSHEET"
      }).update(getLocale().getText('panelAdmin.block.queryResults.downloadOrExportDialogExportDataGoogleSpreadsheet'))
    );

    var contentContainer = new Element("div", { className: "modalPopupTextContent" });
    contentContainer.appendChild(titleContainer);
    contentContainer.appendChild(linksContainer);
    	
    var popup = new ModalPopup({
      content: contentContainer,
      width: 590,
      height: 345,
      buttons: [
        {
          text: getLocale().getText('panelAdmin.block.queryResults.downloadOrExportDialogCancelButton'),
          action: function(instance) {
            instance.close();
          }
        }
      ]
    });

    popup.open(linkElement);
  },
	  
  _onDownloadOrExportPageLinkClick: function (event) {
    Event.stop(event);

    var linkElement = Event.element(event);
    var linkHref = linkElement.getAttribute("href");
    var hashParams = this._parseHash(linkHref);
    var queryPageId = hashParams.get("queryPageId");
    var contextPath = hashParams.get("contextPath");
    var stampId = hashParams.get("stampId");
    
    var titleContainer = new Element("div", { className: "modalPopupTitleContent" });
    titleContainer.update(getLocale().getText('panelAdmin.block.queryResults.downloadOrExportDialogTitle'));
    var linksContainer = new Element("div", { className: "modalPopupLinksContainer" });
    
    linksContainer.appendChild(new Element("h4").update(getLocale().getText('panelAdmin.block.queryResults.downloadOrExportDialogExportContentsTitle')));
    
    linksContainer.appendChild(
      new Element("a", { 
  	    className: "modalPopupLinkContainer exportPDF",
	      href: contextPath + "/queries/exportreportpage.binary?format=PDF&queryPageId=" + queryPageId + "&stampId=" + stampId
	    }).update(getLocale().getText('panelAdmin.block.queryResults.downloadOrExportDialogExportContentsPDF'))
    );
    linksContainer.appendChild(
  	  new Element("a", { 
	  	target: '_blank',  
	      className: "modalPopupLinkContainer exportGoogleDrive",
  	    href: contextPath + "/queries/exportreportpage.binary?format=GOOGLE_DOCUMENT&queryPageId=" + queryPageId + "&stampId=" + stampId
	    }).update(getLocale().getText('panelAdmin.block.queryResults.downloadOrExportDialogExportContentsGoogleDocument'))
	  );

    linksContainer.appendChild(new Element("h4").update(getLocale().getText('panelAdmin.block.queryResults.downloadOrExportDialogExportChartsTitle')));
    linksContainer.appendChild(
      new Element("a", { 
        className: "modalPopupLinkContainer exportPDF",
        href: contextPath + "/queries/exportreportpage.binary?format=PNG_ZIP&queryPageId=" + queryPageId + "&stampId=" + stampId
      }).update(getLocale().getText('panelAdmin.block.queryResults.downloadOrExportDialogExportChartsPNG'))
    );
    linksContainer.appendChild(
      new Element("a", { 
        className: "modalPopupLinkContainer exportPDF",
        href: contextPath + "/queries/exportreportpage.binary?format=SVG_ZIP&queryPageId=" + queryPageId + "&stampId=" + stampId
      }).update(getLocale().getText('panelAdmin.block.queryResults.downloadOrExportDialogExportChartsSVG'))
    );
    linksContainer.appendChild(
      new Element("a", { 
      target: '_blank',  
        className: "modalPopupLinkContainer exportGoogleDrive",
        href: contextPath + "/queries/exportreportpage.binary?format=GOOGLE_IMAGES&queryPageId=" + queryPageId + "&stampId=" + stampId
      }).update(getLocale().getText('panelAdmin.block.queryResults.downloadOrExportDialogExportChartsGoogle'))
    );

    linksContainer.appendChild(new Element("h4").update(getLocale().getText('panelAdmin.block.queryResults.downloadOrExportDialogExportDataTitle')));

    var downloadCSVLink = new Element("a", { className: "modalPopupLinkContainer exportCSV" });
    downloadCSVLink.update(getLocale().getText('panelAdmin.block.queryResults.downloadOrExportDialogExportDataCSV'));
    downloadCSVLink.setAttribute("href",contextPath + "/queries/exportpagedata.binary?queryPageId=" + queryPageId + "&stampId=" + stampId + "&format=CSV");
    linksContainer.appendChild(downloadCSVLink);
    
    linksContainer.appendChild(
      new Element("a", { 
  		target: '_blank',  
    	className: "modalPopupLinkContainer exportGoogle",
    	href: contextPath + "/queries/exportpagedata.binary?queryPageId=" + queryPageId + "&stampId=" + stampId + "&format=GOOGLE_SPREADSHEET",
      }).update(getLocale().getText('panelAdmin.block.queryResults.downloadOrExportDialogExportDataGoogleSpreadsheet'))
    );
    
    var contentContainer = new Element("div", { className: "modalPopupTextContent" });
    contentContainer.appendChild(titleContainer);
    contentContainer.appendChild(linksContainer);
    	
    var popup = new ModalPopup({
      content: contentContainer,
      width: 590,
      height: 345,
      buttons: [
        {
          text: getLocale().getText('panelAdmin.block.queryResults.downloadOrExportDialogCancelButton'),
          action: function(instance) {
            instance.close();
          }
        }
      ]
    });
    
    popup.open(linkElement);
  } 	  
  
});

addBlockController(new QueryResultsListingBlockController());