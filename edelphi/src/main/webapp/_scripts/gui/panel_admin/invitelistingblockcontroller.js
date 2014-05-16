InviteListingBlockController = Class.create(BlockController, {
  initialize: function($super) {
    $super();
    this._resendInvitationClickListener = this._onResendInvitationClick.bindAsEventListener(this);    
    this._resendAllInvitationsClickListener = this._onResendAllInvitationsClick.bindAsEventListener(this);
    this._refreshListener = this._onRefreshRequested.bindAsEventListener(this);
  },
  setup: function($super) {
    $super(null);
    this._panelId = JSDATA['panelId'];
    this._container = $('GUI_inviteUsersListColumn');
    this.loadInvitations();
    Event.observe(document, "ed:invitationListRefreshRequired", this._refreshListener);
  },
  deinitialize: function($super) {
    $super();
    Event.stopObserving(document, "ed:invitationListRefreshRequired", this._refreshListener);
  },
  loadInvitations: function () {
    var _this = this;
    new Ajax.Request('invitationlist.page', {
      parameters: {
        panelId: this._panelId
      },
      onSuccess : function(response) {
        _this._detachResendListeners();
        _this._container.update(response.responseText);
        _this.setBlockElement(_this._container.down('div.block'));
        _this._attachResendListeners();
      },
      onFailure: function () {
        
      }
    });
  },
  _attachResendListeners: function() {
    var _this = this;
    $$('a.inviteUsersResendAllInvitationsLink').each(function(e) {
      Event.observe(e, "click", _this._resendAllInvitationsClickListener);
    });
    $$('a.inviteUsersResendInvitationLink').each(function(e) {
      Event.observe(e, "click", _this._resendInvitationClickListener);
    });
  },
  _detachResendListeners: function() {
    if (this.getBlockElement()) {
      var _this = this;
      $$('a.inviteUsersResendInvitationLink').each(function(e) {
        Event.stopObserving(e, "click", _this._resendInvitationClickListener);
      });
      $$('a.inviteUsersResendAllInvitationsLink').each(function(e) {
        Event.stopObserving(e, "click", _this._resendAllInvitationsClickListener);
      });
    }
  },
  _onResendInvitationClick: function (event) {
    var rowElement = event.target.up('div.inviteUsersListRow');
    var rowElements = [rowElement];
    document.fire("ed:resendInvitation", {
      "rowElements": rowElements
    });
  },
  _onResendAllInvitationsClick: function (event) {
    var containerElement = event.target.up('div.userContainer');
    var rowElements = containerElement.select('div.inviteUsersListRow');
    document.fire("ed:resendInvitation", {
      "rowElements": rowElements
    });
  },
  _onRefreshRequested: function (event) {
    this.loadInvitations();
  }
});

addBlockController(new InviteListingBlockController());