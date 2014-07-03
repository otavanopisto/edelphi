CompareReportsBlockController = Class.create(BlockController, {
  initialize: function($super) {
    $super();
    this._queryChangeListener = this._onQueryChanged.bindAsEventListener(this);
    this._stampChangeListener = this._onStampChanged.bindAsEventListener(this);
    this._expertiseGroupClickListener = this._onExpertiseGroupClicked.bindAsEventListener(this);
    this._settingsIconClickListener = this._onSettingsIconClicked.bindAsEventListener(this);
    this._applySettingsClickListener = this._onApplySettingsClicked.bindAsEventListener(this);
  },
  setup: function($super) {
    $super(null);
    var _this = this;
    // Listen mouse clicks on settings icons
    $$('.settingsIcon').each(function(e) {
      Event.observe(e, "click", _this._settingsIconClickListener);
    });
    // Set all settings containers hidden by default
    $$('.settingsContainer').each(function(e) {
      e.hide();
    });
    // Listen mouse clicks on filter buttons
    $$('input[name="applySettings"]').each(function(e) {
      Event.observe(e, "click", _this._applySettingsClickListener);
    });
    // Listen selection change on query dropdowns
    $$('select[name="queryId"]').each(function(e) {
      Event.observe(e, "change", _this._queryChangeListener);
    });
    // Initially hide report options
    $$('.selectedQueryReportActions-filters').each(function(e) {
      e.hide();
    });
    $$('.selectedQueryReportActions-exports').each(function(e) {
      e.hide();
    });
    $$('.selectedQueryReportActions-settings').each(function(e) {
      e.hide();
    });
  },
  deinitialize: function($super) {
    $super();
    var _this = this;
    $$('.settingsIcon').each(function(e) {
      Event.stopObserving(e, "click", _this._settingsIconClickListener);
    });
    $$('input[name="applySettings"]').each(function(e) {
      Event.stopObserving(e, "click", _this._applySettingsClickListener);
    });
    $$('select[name="queryId"]').each(function(e) {
      Event.stopObserving(e, "change", _this._queryChangeListener);
    });
  },
  _onQueryChanged: function (event) {
    Event.stop(event);
    var queryDropdown = event.target;
    var settingsForm = queryDropdown.up('form');
    queryDropdown.blur();
    this._loadQueryOptions(settingsForm, true);
  },
  _onStampChanged: function (event) {
    Event.stop(event);
    var stampDropdown = event.target;
    var settingsForm = stampDropdown.up('form');
    this._loadQueryOptions(settingsForm, false);
  },
  _onExpertiseGroupClicked: function (event) {
    Event.stop(event);
    var element = Event.element(event);
    var settingsForm = element.up('form');
    if (element.down("input[name='groupId']")) {
      if (element.hasClassName("queryExpertiseFilterGroupSelected")) {
        element.removeClassName("queryExpertiseFilterGroupSelected");
      }
      else {
        element.addClassName("queryExpertiseFilterGroupSelected");
      }
      var value = "";
      settingsForm.select('.queryExpertiseFilterGroupSelected').each(function(e) {
        var v = e.down("input[name='groupId']").value;
        if (value == "")
          value = v;
        else
          value = value + "," + v;
      });
      settingsForm.queryExpertiseFilter.value = value;
    }
  },
  _onSettingsIconClicked: function (event) {
    Event.stop(event);
    var settingsIcon = event.target;
    if (settingsIcon.hasClassName('icon-selected')) {
      // Hide currently active settings container
      settingsIcon.removeClassName('icon-selected');
      var settingsContainer = settingsIcon.next('.settingsContainer');
      settingsContainer.removeClassName('expanded');
      settingsContainer.hide();
    }
    else {
      // Activate a settings container 
      var actionsContainer = settingsIcon.up('.selectedQueryReportActions');
      actionsContainer.select('.settingsIcon').each(function(e) {
        e.removeClassName('icon-selected');
      });
      settingsIcon.addClassName('icon-selected');
      actionsContainer.select('.settingsContainer').each(function(e) {
        e.removeClassName('expanded');
        e.hide();
      });
      var settingsContainer = settingsIcon.next('.settingsContainer');
      settingsContainer.addClassName('expanded');
      settingsContainer.show();
    }
  },
  _onApplySettingsClicked: function (event) {
    Event.stop(event);
    var applyButton = event.target;
    var settingsForm = applyButton.up('form');
    
    // Hide settings

    settingsForm.select('.settingsIcon').each(function(e) {
      if (e.hasClassName('icon-selected')) {
        var clickEvent = document.createEvent("MouseEvents");
        clickEvent.initEvent("click", true, true);
        e.dispatchEvent(clickEvent);
      }
    });

    // Clear the potential previous report
    
    var reportContainer = settingsForm.down('.selectedQueryReportContainer');
    while (reportContainer.firstChild) {
      reportContainer.removeChild(reportContainer.firstChild);
    }

    // Either do full report, page by page, or just a single page
    
    if (settingsForm.queryPageId.value == 0) {
      for (var i = 1; i < settingsForm.queryPageId.length; i++) {
        settingsForm.queryPageId.value = settingsForm.queryPageId.options[i].value;
        new Ajax.Request(CONTEXTPATH + '/panel/admin/report/viewqueryreport.page', {
          method: "POST",
          asynchronous: false,
          parameters: settingsForm.serialize(),
          onSuccess : function(response) {

            // Append report
            
            var div = new Element('div');
            div.innerHTML = response.responseText;
            
            // Each page comes with serializedContext but we only need it once
            
            if (settingsForm.serializedContext != undefined) {
              div.removeChild(div.down('input[name="serializedContext"]'));
            }
            reportContainer.appendChild(div);
          }
        });
      }
      settingsForm.queryPageId.value = 0;
    }
    else {
      new Ajax.Request(CONTEXTPATH + '/panel/admin/report/viewqueryreport.page', {
        method: "POST",
        asynchronous: false,
        parameters: settingsForm.serialize(),
        onSuccess : function(response) {

          // Append report
          
          var div = new Element('div');
          div.innerHTML = response.responseText;
          reportContainer.appendChild(div);
        }
      });
    }
    // Recreate export links
    new Ajax.Request(CONTEXTPATH + '/panel/admin/report/exportlinks.page', {
      method: "POST",
      asynchronous: false,
      parameters: {
        panelId: settingsForm.panelId.value,
        queryId : settingsForm.queryId.value,
        queryPageId : settingsForm.queryPageId.value,
        stampId : settingsForm.stampId.value,
        serializedContext : settingsForm.serializedContext.value
      },
      onSuccess : function(response) {
        // Add report
        var exportContainer = settingsForm.down('.selectedQueryReportActions-exports-container');
        exportContainer.innerHTML = response.responseText;
        // Enable report options
        settingsForm.down('.selectedQueryReportActions-filters').show();
        settingsForm.down('.selectedQueryReportActions-exports').show();
        settingsForm.down('.selectedQueryReportActions-settings').show();
      }
    });
  },
  _loadQueryOptions: function(settingsForm, refreshReport) {
    var _this = this;
    // Remove old stamp listener
    var stampDropdown = settingsForm.down('select[name="stampId"]');
    if (stampDropdown != undefined) {
      Event.stopObserving(stampDropdown, "change", _this._stampChangeListener);
    }
    // Remove old expertise group listeners
    settingsForm.select('.queryExpertiseFilterGroup').each(function (e) {
      Event.stopObserving(e, "click", _this._expertiseGroupClickListener);
    });
    var queryDropdown = settingsForm.down('select[name="queryId"]');
    new Ajax.Request(CONTEXTPATH + '/panel/admin/report/reportoptions.page', {
      method: "GET",
      asynchronous: false,
      parameters: {
        panelId: settingsForm.panelId.value,
        queryId: queryDropdown.value,
        stampId: settingsForm.stampId ? settingsForm.stampId.value : -1
      },
      onSuccess : function(response) {
        // New report options
        var reportOptionsContainer = settingsForm.down('.reportOptionsContainer');
        reportOptionsContainer.innerHTML = response.responseText;
        // Add new stamp listener
        var stampDropdown = settingsForm.down('select[name="stampId"]');
        if (stampDropdown != undefined) {
          Event.observe(stampDropdown, "change", _this._stampChangeListener);
        }
        // Add new expertise group listeners
        settingsForm.select('.queryExpertiseFilterGroup').each(function (e) {
          Event.observe(e, "click", _this._expertiseGroupClickListener);
        });
        if (refreshReport === true) {
          var settingsButton = settingsForm.down('input[name="applySettings"]');
          settingsButton.click();
        }
      }
   });
  }
});

addBlockController(new CompareReportsBlockController());