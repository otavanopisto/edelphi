StampOverlayViewBlockController = Class.create(BlockController, {
  initialize : function ($super) {
	$super();
    this._overlayButtonClickListener = this._onOverlayButtonClick.bindAsEventListener(this);

    this.stampOverlayStatus = this._getCookie('stampOverlayStatus');
  },
  setup: function ($super) {
	this._overlayButton = $('stampSelectorOverLayButton');	  
	Event.observe(this._overlayButton, "click", this._overlayButtonClickListener);	  
	
    if (this._getCookie('stampOverlayStatus') == 'down'){
	  $('stampTimeLineSpacer').setStyle({
		  height: '51px'
	  });
      $('stampSelectorOverlay').setStyle({
    	  position: 'fixed',
    	  bottom: '-88px'
      });
      $('stampSelectorOverlayHeader').setStyle({
    	 color: '#000' 
      });
      this._overlayButton.removeClassName('stampSelectorOverLayDown');
	  this._overlayButton.addClassName('stampSelectorOverLayUp');
	  $('stampOverlayButtonTooltipText').update(getLocale().getText('panels.admin.panelStampsOverlay.stampOverlayUpTooltip'));
    }
  },
  deinitialize : function ($super) {
    Event.stopObserving(this._overlayButton, "click", this._overlayButtonClickListener);
  },
  _getCookie : function (CookieName) {
    var CookieValue = null;
	if (document.cookie) {
	  var arr = document.cookie.split((escape(CookieName) + '='));
	  if (arr.length >= 2){
	    var arr2 = arr[1].split(';');
	    CookieValue = unescape(arr2[0]);
	  }
    }
    return CookieValue;
  },  
  _setCookie : function (status) {
    var date = new Date();
    date.setTime(date.getTime() + (3650*24*60*60*1000));
    var expires = "; expires=" + date.toGMTString();
    document.cookie = "stampOverlayStatus=" + status + expires + "; path=/";
  },  
  _onOverlayButtonClick: function (event) {
	var _this = this;
	if (this.stampOverlayStatus == 'up' || this.stampOverlayStatus == null) {
      $('stampTimeLineSpacer').morph('height:51px;', { duration: .3 });
      $('stampSelectorOverlay').morph('position:fixed; bottom:-88px;', { 
    	duration: .3, 
  	    after: function(){ 
  	      _this._overlayButton.removeClassName('stampSelectorOverLayDown');
  	      _this._overlayButton.addClassName('stampSelectorOverLayUp');
  	      $('stampOverlayButtonTooltipText').update(getLocale().getText('panels.admin.panelStampsOverlay.stampOverlayUpTooltip'));
  	      $('stampSelectorOverlayHeader').morph('color:#000;', { duration:3 });
        }
      });
      this.stampOverlayStatus = 'down';
      this._setCookie('down');
	}
    else {
      $('stampTimeLineSpacer').morph('height:140px;', { duration: .3 });
      $('stampSelectorOverlay').morph('position:fixed; bottom:0px;', {
    	duration: .3,
    	after: function() {
    	  _this._overlayButton.removeClassName('stampSelectorOverLayUp');
    	  _this._overlayButton.addClassName('stampSelectorOverLayDown');
    	  $('stampOverlayButtonTooltipText').update(getLocale().getText('panels.admin.panelStampsOverlay.stampOverlayDownTooltip'));
    	  $('stampSelectorOverlayHeader').morph('color:#186089;', { duration:.3 });
    	}
      });
      this.stampOverlayStatus = 'up';
      this._setCookie('up');
    }

  }
});

addBlockController(new StampOverlayViewBlockController());
