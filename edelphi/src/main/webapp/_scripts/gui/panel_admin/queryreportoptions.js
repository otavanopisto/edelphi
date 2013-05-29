QueryReportOptionsBlockController = Class.create(BlockController, {
  initialize: function ($super) {
    $super();

    this._groupClickListener = this._onGroupClick.bindAsEventListener(this);
  },
  setup: function ($super) {
    $super($('queryReportOptions'));

    var _this = this;
    $('queryReportOptions').select(".queryExpertiseFilterGroup").each(function (node) {
      Event.observe(node, "click", _this._groupClickListener);
    });
  },
  deinitialize: function ($super) {
    var _this = this;
    $('queryReportOptions').select(".queryExpertiseFilterGroup").each(function (node) {
      Event.stopObserving(node, "click", _this._groupClickListener);
    });
  },
  _onGroupClick: function (event) {
    var element = Event.element(event);
    
    if (element.down("input[name='groupId']")) {
      if (element.hasClassName("queryExpertiseFilterGroupSelected"))
        element.removeClassName("queryExpertiseFilterGroupSelected");
      else
        element.addClassName("queryExpertiseFilterGroupSelected");
      
      this._checkSelected();
    }
  },
  _checkSelected: function () {
    var value = "";
    
    $('queryReportOptions').select(".queryExpertiseFilterGroupSelected").each(function (node) {
      var v = node.down("input[name='groupId']").value;
      if (value == "")
        value = v;
      else
        value = value + "," + v;
    });
    
    $('queryExpertiseFilterValue').value = value;
  }
});

addBlockController(new QueryReportOptionsBlockController());
