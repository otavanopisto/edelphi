PanelistActivityController = Class.create(BlockController, {
  initialize: function ($super) {
    $super();
    this._sortAscending = true;
    this._queryClickListener = this._onQueryClick.bindAsEventListener(this);
    this._sortClickListener = this._onSortClick.bindAsEventListener(this);
  },
  deinitialize : function() {
    var _this = this;
    $('panelistActivityBlock').select('.panelAdminQueryRow').each(function(e) {
      Event.stopObserving(e, "click", _this._queryClickListener);
    });
    if ($('repliedPanelistName')) {
      Event.stopObserving($('repliedPanelistName'), 'click', this._sortClickListener);
      Event.stopObserving($('repliedPanelistLogin'), 'click', this._sortClickListener);
      Event.stopObserving($('repliedPanelistReply'), 'click', this._sortClickListener);
      Event.stopObserving($('unrepliedPanelistName'), 'click', this._sortClickListener);
      Event.stopObserving($('unrepliedPanelistLogin'), 'click', this._sortClickListener);
    }
  },
  setup: function($super) {
    $super($('panelistActivityBlock'));
    var _this = this;
    $('panelistActivityBlock').select('.panelAdminQueryRow').each(function(e) {
      Event.observe(e, "click", _this._queryClickListener);
    });
    if ($('repliedPanelistName')) {
      Event.observe($('repliedPanelistName'), 'click', this._sortClickListener);
      Event.observe($('repliedPanelistLogin'), 'click', this._sortClickListener);
      Event.observe($('repliedPanelistReply'), 'click', this._sortClickListener);
      Event.observe($('unrepliedPanelistName'), 'click', this._sortClickListener);
      Event.observe($('unrepliedPanelistLogin'), 'click', this._sortClickListener);
    }
  },
  _onQueryClick: function(event) {
    Event.stop(event);
    var queryElement = Event.element(event);
    if (!queryElement.hasClassName('panelAdminQueryRow')) {
      queryElement = queryElement.up('.panelAdminQueryRow');
    }
    var queryId = queryElement.down('input[name="queryId"]').value;
    window.location.href = CONTEXTPATH + '/panel/admin/panelistactivity.page?panelId=' + JSDATA['panelId'] + '&queryId=' + queryId;
  },
  _onSortClick: function(event) {
    Event.stop(event);
    var sortElement = Event.element(event);
    var _this = this;
    if (sortElement.id == 'repliedPanelistName') {
      var panelists = this.getBlockElement().select('.repliedPanelist');
      panelists.sort(function(o1, o2) {
        var o1value = o1.down('.panelistActivityName').innerHTML.toLowerCase().strip();
        var o2value = o2.down('.panelistActivityName').innerHTML.toLowerCase().strip();
        return o1value == o2value ? 0 : _this._sortAscending ? o1value < o2value ? -1 : 1 : o1value < o2value ? 1 : -1; 
      });
      panelists.each(function(panelist) {
        $('panelistActivityAnsweredReplicants').appendChild(panelist);
      });
    }
    else if (sortElement.id == 'repliedPanelistLogin') {
      var panelists = this.getBlockElement().select('.repliedPanelist');
      panelists.sort(function(o1, o2) {
        var o1value = o1.down('input[name="lastLogin"]').value;
        var o2value = o2.down('input[name="lastLogin"]').value;
        return o1value == o2value ? 0 : _this._sortAscending ? o1value < o2value ? -1 : 1 : o1value < o2value ? 1 : -1; 
      });
      panelists.each(function(panelist) {
        $('panelistActivityAnsweredReplicants').appendChild(panelist);
      });
    }
    else if (sortElement.id == 'repliedPanelistReply') {
      var panelists = this.getBlockElement().select('.repliedPanelist');
      panelists.sort(function(o1, o2) {
        var o1value = o1.down('input[name="lastReply"]').value;
        var o2value = o2.down('input[name="lastReply"]').value;
        return o1value == o2value ? 0 : _this._sortAscending ? o1value < o2value ? -1 : 1 : o1value < o2value ? 1 : -1; 
      });
      panelists.each(function(panelist) {
        $('panelistActivityAnsweredReplicants').appendChild(panelist);
      });
    }
    else if (sortElement.id == 'unrepliedPanelistName') {
      var panelists = this.getBlockElement().select('.unrepliedPanelist');
      panelists.sort(function(o1, o2) {
        var o1value = o1.down('.panelistActivityName').innerHTML.toLowerCase().strip();
        var o2value = o2.down('.panelistActivityName').innerHTML.toLowerCase().strip();
        return o1value == o2value ? 0 : _this._sortAscending ? o1value < o2value ? -1 : 1 : o1value < o2value ? 1 : -1; 
      });
      panelists.each(function(panelist) {
        $('panelistActivityUnAnsweredReplicants').appendChild(panelist);
      });
    }
    else if (sortElement.id == 'unrepliedPanelistLogin') {
      var panelists = this.getBlockElement().select('.unrepliedPanelist');
      panelists.sort(function(o1, o2) {
        var o1value = o1.down('input[name="lastLogin"]').value;
        var o2value = o2.down('input[name="lastLogin"]').value;
        return o1value == o2value ? 0 : _this._sortAscending ? o1value < o2value ? -1 : 1 : o1value < o2value ? 1 : -1; 
      });
      panelists.each(function(panelist) {
        $('panelistActivityUnAnsweredReplicants').appendChild(panelist);
      });
    }
    this._sortAscending = !this._sortAscending;
  }
});

addBlockController(new PanelistActivityController());