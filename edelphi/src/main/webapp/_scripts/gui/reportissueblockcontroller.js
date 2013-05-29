ReportIssueBlockController = Class.create(BlockController, {
  initialize: function ($super) {
    $super();
    this._sendButtonClickListener = this._onSendButtonClick.bindAsEventListener(this);
  },
  setup: function ($super) {
    $super($('reportIssueBlockContent'));
    this._formElement = this.getBlockElement().down('form[name="reportIssue"]');
    this._sendButton = this._formElement.send;
    Event.observe(this._sendButton, "click", this._sendButtonClickListener);
  },
  deinitialize: function ($super) {
    Event.stopObserving(this._sendButton, "click", this._sendButtonClickListener);
  },
  _onSendButtonClick: function (event) {
    Event.stop(event);
    
    startLoadingOperation("reportIssue.block.reportingIssue");
    JSONUtils.request(CONTEXTPATH + '/issues/reportissue.json', {
      parameters: {
        subject: this._formElement.subject.value,
        content: this._formElement.content.value
      },
      onComplete: function (transport) {
        endLoadingOperation();
      }
    });
  }
});

addBlockController(new ReportIssueBlockController());