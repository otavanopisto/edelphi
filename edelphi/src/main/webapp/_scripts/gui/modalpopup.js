ModalPopup = Class.create({
  initialize : function(options) {
    this._buttonClickListener = this._onButtonClick.bindAsEventListener(this);
    this._options = options ? options : { };
    if (!options.width)
      options.width = 300;
    if (!options.height)
      options.height = 100;
    this.setup();
  },
  deinitialize: function () {
    var _this = this;
    this._popupFrame.select(".modalPopupButton").each(function (buttonElement) {
      Event.stopObserving(buttonElement, "click", _this._buttonClickListener);
    });
  },
  setup: function () {
    var options = this._options;
    this._glassPane = new Element("div", { className: "modalPopupGlassPane" });
    this._glassPane.setStyle({
      display: 'none'
    });
    
    this._popupFrame = new Element("div", { className: "modalPopupContentFrame" });
    this._popupFrame.setStyle({
      display: 'none',
      width: options.width + 'px',
      height: options.height + 'px'
    });
    
    var popupTextElement = new Element("div", { className: "modalPopupTextContent" });
    var popupButtonsContainer = new Element("div", { className: "modalPopupButtonsContainer" });
    popupTextElement.update(options.content);
    this._popupFrame.appendChild(popupTextElement);
    this._popupFrame.appendChild(popupButtonsContainer);
    
    var buttons = options.buttons;
    for (var i = 0, l = buttons.length; i < l; i++) {
      var classNames = "modalPopupButton";
      if (buttons[i].classNames)
        classNames = classNames + " " + buttons[i].classNames;
      
      var buttonElement = new Element("input", { type: "button", className: classNames, value: buttons[i].text });
      
      buttonElement._button = buttons[i];
      
      Event.observe(buttonElement, "click", this._buttonClickListener);
      
      popupButtonsContainer.appendChild(buttonElement);
    }
    
    var body = $(document.documentElement).down("body");
    
    body.appendChild(this._glassPane);
    body.appendChild(this._popupFrame);
  },
  open: function (refElement) {
    this._glassPane.show();
    this._popupFrame.show();

    if (refElement) {
      var loc = Element.cumulativeOffset(refElement);
      
      var left = Math.max(loc[0] - Math.round(this._options.width / 2), 0);
      var top = Math.max(loc[1] - Math.round(this._options.height / 2), 0);

      var viewportDimensions = document.viewport.getDimensions();
      
      if ((this._popupFrame.getWidth() + left) > viewportDimensions.width)
       left = viewportDimensions.width - this._popupFrame.getWidth() - 10;
    	  
      this._popupFrame.absolutize();
      this._popupFrame.setStyle({
        left: left + 'px',
        top: top + 'px'
      });
      
    }
  },
  close: function () {
    this._popupFrame.hide();
    this._glassPane.hide();
  },
  getFrame: function() {
    return this._popupFrame;
  },
  _onButtonClick: function (event) {
    Event.stop(event);
    var buttonElement = Event.element(event);
    
    if (buttonElement._button) {
      if (buttonElement._button.action) {
        buttonElement._button.action(this);
      }
    }
  }
});

