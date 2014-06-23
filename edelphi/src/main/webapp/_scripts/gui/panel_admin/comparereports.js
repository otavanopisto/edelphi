CompareReportsBlockController = Class.create(BlockController, {
  initialize: function($super) {
    $super();
    this._queryChangeListener = this._onQueryChanged.bindAsEventListener(this);
    this._stampChangeListener = this._onStampChanged.bindAsEventListener(this);
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
    var _this = this;
    var queryDropdown = event.target;
    var settingsForm = queryDropdown.up('form');
    var stampDropdown = settingsForm.down('select[name="stampId"]');
    if (stampDropdown != undefined) {
      Event.stopObserving(stampDropdown, "change", _this._stampChangeListener);
    }
    this._loadQueryOptions(settingsForm, true);
  },
  _onStampChanged: function (event) {
    Event.stop(event);
    var stampDropdown = event.target;
    var settingsForm = stampDropdown.up('form');
    this._loadQueryOptions(settingsForm, false);
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
    new Ajax.Request(CONTEXTPATH + '/panel/admin/report/viewqueryreport.page', {
      method: "POST",
      parameters: settingsForm.serialize(),
      onSuccess : function(response) {
        var reportContainer = settingsForm.down('.selectedQueryReportContainer');
        reportContainer.innerHTML = response.responseText;
      }
    });
  },
  _loadQueryOptions: function(settingsForm, refreshReport) {
    var _this = this;
    var queryDropdown = settingsForm.down('select[name="queryId"]');
    new Ajax.Request(CONTEXTPATH + '/panel/admin/report/reportoptions.page', {
      method: "GET",
      parameters: {
        panelId: settingsForm.panelId.value,
        queryId: queryDropdown.value,
        stampId: settingsForm.stampId ? settingsForm.stampId.value : -1
      },
      onSuccess : function(response) {
        var reportOptionsContainer = settingsForm.down('.reportOptionsContainer');
        reportOptionsContainer.innerHTML = response.responseText;
        var stampDropdown = settingsForm.down('select[name="stampId"]');
        if (stampDropdown != undefined) {
          Event.observe(stampDropdown, "change", _this._stampChangeListener);
        }
        if (refreshReport === true) {
          var settingsButton = settingsForm.down('input[name="applySettings"]');
          settingsButton.dispatchEvent(new Event('click'));
        }
      }
   });
  }
});

addBlockController(new CompareReportsBlockController());