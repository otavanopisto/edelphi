EventQueue = Class.create({
  initialize: function (options) {
    this._options = Object.extend({
    }, options || {});
    
    this.domNode = new Element("div", {
      className: "eventQueue"
    });
    
    this._itemsContainer = new Element("div", {
      className: "eventQueueItemsContainer"
    });
    
    this.domNode.appendChild(this._itemsContainer);
    
    this._items = new Array(); 
  },
  addItem: function (eventQueueItem) {
    if (this._items.length > 0) {
      this._items[this._items.length - 1].domNode.removeClassName("eventQueueItemLast");
    }
    
    this._items.push(eventQueueItem);
    this._itemsContainer.appendChild(eventQueueItem.domNode);
    eventQueueItem.domNode.addClassName("eventQueueItemLast");
    
    eventQueueItem.setup(this);
    
    return eventQueueItem;
  },
  removeItem: function (eventQueueItem) {
    for (var i = 0, l = this._items.length; i < l; i++) {
      if (this._items[i] === eventQueueItem) {
        this._items.splice(i, 1);
        break;
      }
    }
    
    if (this._items.length > 0) {
      this._items[this._items.length - 1].domNode.addClassName("eventQueueItemLast");
    }
    
    var height = eventQueueItem.domNode.getDimensions().height;
    
    eventQueueItem.domNode.setStyle({
      zIndex: 0
    });
    
    // TODO: Remove this when scripty2 fixes accelerated effects on Firefox
    var engine = Prototype.Browser.WebKit ? 'css-transition' : 'javascript';
    
    new S2.FX.Morph(eventQueueItem.domNode, {
      style: "margin-top: -" + height + 'px',
      engine: engine,
       duration: 0.6,
      after: function(){
        eventQueueItem.deinitialize();
      }
    }).play();
  }
});

EventQueueItem = Class.create({
  initialize: function (text, options) {
    this._options = Object.extend({
      timeout: -1
    },options||{});
    
    this._removeLinkClickListener = this._onRemoveLinkClick.bindAsEventListener(this);
    
    this.domNode = new Element("div", {
      className: "eventQueueItem"
    });
    
    this._removeLink = new Element("a", {
      className: "eventQueueItemRemoveLink"
    });
    
    this.domNode.appendChild(new Element("span", {
      className: "eventQueueItemText"
    }).update(text));
    this.domNode.appendChild(this._removeLink);
    
    if (this._options.className) {
      this.domNode.addClassName(this._options.className);
    }
    
    this._text = text;
    this._removed = false;
    
    Event.observe(this._removeLink, "click", this._removeLinkClickListener);
  },
  deinitialize: function () {
    Event.stopObserving(this._removeLink, "click", this._removeLinkClickListener);
    
    this.domNode.remove();
  },
  setup: function (eventQueue) {
    this._eventQueue = eventQueue;
    if (this._options.timeout > 0)
      this._scheduleRemoval();
  },
  getText: function () {
    return this._text;
  },
  remove: function () {
    this._remove();
  },
  _remove: function () {
    if (this._removed === false) {
      this._removed = true;
      this._eventQueue.removeItem(this);
    }
  },
  _scheduleRemoval: function () {
    var _this = this;
    setTimeout(function () {
      _this._remove();
    }, this._options.timeout);
  },  
  _cancelScheduledRemoval: function () {
    
  },
  _onRemoveLinkClick: function (event) {
    this._remove();
  }
});