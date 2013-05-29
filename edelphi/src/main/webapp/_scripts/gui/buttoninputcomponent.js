ButtonInputComponent = Class.create({
  initialize : function(element) {
    this._container = new Element("div", {
      className : "buttonInputComponent"
    });

    this._labelElementClickListener = this._onLabelElementClick.bindAsEventListener(this);
    this._elementFocusListener = this._onElementFocus.bindAsEventListener(this);
    this._elementBlurListener = this._onElementBlur.bindAsEventListener(this);

    this._element = element;
    this._mode = this._getMode(element);

    this._element.parentNode.appendChild(this._container);

    this._element.addClassName("buttonInputComponentInput");

    var labelElement = this._findLabelElement(element);
    if (labelElement) {
      this._label = labelElement.innerHTML;
      labelElement.remove();
    } else {
      this._label = '';
    }

    this._labelElement = new Element("div", {
      className : "buttonInputComponentLabel"
    }).update(this._label);

    Event.observe(this._labelElement, "click", this._labelElementClickListener);
    Event.observe(this._element, "focus", this._elementFocusListener);
    Event.observe(this._element, "blur", this._elementBlurListener);

    this._container.appendChild(this._element);
    this._container.appendChild(this._labelElement);

    this._checkLabelVisibility();
  },
  deinitialize : function() {
    Event.stopObserving(this._labelElement, "click", this._labelElementClickListener);
    Event.stopObserving(this._element, "focus", this._elementFocusListener);
    Event.stopObserving(this._element, "blur", this._elementBlurListener);
  },
  _getMode : function() {
    if (this._element.tagName.toUpperCase() == 'INPUT')
      return 'INPUT';
    if (this._element.tagName.toUpperCase() == 'TEXTAREA')
      return 'TEXTAREA';
  },
  _findLabelElement : function() {
    if (this._element.id) {
      return document.body.down('label[for="' + this._element.id + '"]');
    }

    return null;
  },
  _checkLabelVisibility : function() {
    if (this._element.value.length > 0) {
      this._labelElement.hide();
    } else {
      this._labelElement.show();
    }
  },
  _onLabelElementClick : function(event) {
    // this._labelElement.hide();
    // this._element.addClassName("buttonInputComponentInputFocus");
    this._element.focus();
  },
  _onElementFocus : function(event) {
    this._labelElement.hide();
    this._element.addClassName("buttonInputComponentInputFocus");
  },
  _onElementBlur : function(event) {
    this._checkLabelVisibility();
    this._element.removeClassName("buttonInputComponentInputFocus");
  }
});