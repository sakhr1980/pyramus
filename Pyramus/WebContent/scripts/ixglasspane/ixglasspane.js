IxGlassPane = Class.create({
  initialize : function(parentNode, options) {
    this.domNode = new Element("div", {className: "IxGlassPane"});
    this._parentNode = parentNode;
    this._windowScrollListener = this._onWindowScroll.bindAsEventListener(this);
    this._windowResizeListener = this._onWindowResize.bindAsEventListener(this);
  },
  show: function () {
    this._originalScrollOffsets = document.viewport.getScrollOffsets();
    this.domNode.setStyle({
      top: this._originalScrollOffsets.top + 'px'
    });
    
    Event.observe(window, "scroll", this._windowScrollListener);
    Event.observe(window, "resize", this._windowResizeListener);
    this._parentNode.appendChild(this.domNode);
  },
  hide: function () {
    Event.stopObserving(window, "scroll", this._windowScrollListener);
    Event.stopObserving(window, "resize", this._windowResizeListener);
    this._parentNode.removeChild(this.domNode);
  },
  _onWindowScroll: function (event) {
    Event.stop(event);
    window.scroll(this._originalScrollOffsets.left, this._originalScrollOffsets.top);
  },
  _onWindowResize: function (event) {
    this._originalScrollOffsets = document.viewport.getScrollOffsets();
    this.domNode.setStyle({
      top: this._originalScrollOffsets.top + 'px'
    });
  }
});