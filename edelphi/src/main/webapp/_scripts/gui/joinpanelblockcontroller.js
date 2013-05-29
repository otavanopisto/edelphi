JoinPanelBlockController = Class.create(BlockController, {
  initialize: function ($super) {
    $super();
    this._linkAccountClickListener = this._onLinkAccountClick.bindAsEventListener(this);
    this._createAccountClickListener = this._onCreateAccountClick.bindAsEventListener(this);
    this._invitationUserMail = JSDATA['invitationUserMail']; 
  },
  setup: function ($super) {
    $super();
    this._linkAccountLink = $('linkAccountLink');
    this._createAccountLink = $('createAccountLink');
    if (this._linkAccountLink) {
      Event.observe(this._linkAccountLink, "click", this._linkAccountClickListener);
    }
    if (this._createAccountLink) {
      Event.observe(this._createAccountLink, "click", this._createAccountClickListener);
    }
  },
  deinitialize: function ($super) {
    if (this._linkAccountLink) {
      Event.stopObserving(this._linkAccountLink, "click", this._linkAccountClickListener);
    }
    if (this._createAccountLink) {
      Event.stopObserving(this._createAccountLink, "click", this._createAccountClickListener);
    }
  },
  _onLinkAccountClick: function (event) {
    Event.stop(event);
    var parameters = {
      link: true,
      email: this._invitationUserMail
    };
    JSONUtils.request(CONTEXTPATH + '/processnewemail.json', {
      parameters: parameters,
      onError: function (jsonResponse) {
        JSONUtils.showMessages(jsonResponse);
      }
    });
  },
  _onCreateAccountClick: function (event) {
    Event.stop(event);
    var parameters = {
      link: false,
      email: this._invitationUserMail
    };
    JSONUtils.request(CONTEXTPATH + '/processnewemail.json', {
      parameters: parameters,
      onError: function (jsonResponse) {
        JSONUtils.showMessages(jsonResponse);
      }
    });
  }
});

addBlockController(new JoinPanelBlockController());