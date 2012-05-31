_Dialogs = new Hash();

IxDialog = Class.create({
  initialize : function(options) {
    this._id = options.id;
    this._isOpen = false;
    this._dragging = false;
    this._isHideOnly = options.hideOnly||false;
    this._autoEvaluateSize = options.autoEvaluateSize == true;
    this._appearDuration = options.appearDuration||0.2;
    this._contentLoaded = false;
    
    this._listeners = new Array();

    this._dialogNode = $(document.createElement("div"));
    this._dialogNode.addClassName('IxDialog');
    this._dialogNode.writeAttribute('id',this._id);

    // Create a div representing the title bar of the dialog. Make its text
    // unselectable since it will be used to drag the dialog around.
    
    this._titleBar = $(document.createElement("div"));
    this._titleBar.addClassName('IxDialogTitleBar');
    this._titleBar.onselectstart = function(){
      return false;
    };
    this._titleBar.unselectable = "on";
    
    this._title = $(document.createElement("div"));
    if (options.title)
      this.setTitle(options.title);

    this._title.addClassName('IxDialogTitle');
    
    // TODO _titleBarButtonsContainer does nothing at all? Future in the making?
    
    this._titleBarButtonsContainer = $(document.createElement("div"));
    this._titleBarButtonsContainer.addClassName('IxDialogTitleBarButtons');

    this._titleBar.appendChild(this._titleBarButtonsContainer);
    this._titleBar.appendChild(this._title);

    this._dialogNode.appendChild(this._titleBar);
    
    if (options.content)
      this._pendingContent = options.content;
    
    var contentURL = options.contentURL ? options.contentURL : 'about:blank';
    
    this._dialogContent = Builder.node("iframe", {frameborder: 0, border: 0, className: "IxDialogContent", src: contentURL});
    
    if (this._autoEvaluateSize != true) {
      this._dialogContent.setStyle({
        width: "100%",
        height: "100%"
      });
    } 
    
    this._dialogNode.setStyle({
      opacity: 0 
    });
    
    this._dialogContentLoadListener = this._onDialogContentLoad.bindAsEventListener(this);
    Event.observe(this._dialogContent, "load", this._dialogContentLoadListener);
    this._dialogNode.appendChild(this._dialogContent);
    
    this._buttonsContainer = Builder.node("div", {className: "IxDialogButtonsContainer"});
    
    if (options.showOk) {
      this._okButton = Builder.node("button", {className: "IxDialogButton IxDialogOkButton"}, options.okLabel);
      this._buttonsContainer.appendChild(this._okButton);
      this._okButtonClickListener = this._onOkButtonClick.bindAsEventListener(this);
      Event.observe(this._okButton, "click", this._okButtonClickListener);

      if (options.disableOk) {
        this.disableOkButton();
      }
    }
    
    if (options.showCancel) {
      this._cancelButton = Builder.node("button", {className: "IxDialogButton IxDialogCancelButton"}, options.cancelLabel);
      this._buttonsContainer.appendChild(this._cancelButton);
      this._cancelButtonClickListener = this._onCancelButtonClick.bindAsEventListener(this);
      Event.observe(this._cancelButton, "click", this._cancelButtonClickListener);

      if (options.disableCancel) {
        this.disableCancelButton();
      }
    }
    
    this._dialogNode.appendChild(this._buttonsContainer);
    this._parentNode = document.getElementsByTagName('body')[0];
    this._centered = options.centered;

    this._windowResizedListener = this._onWindowResized.bindAsEventListener(this);
    this._windowMouseMoveListener = this._onWindowMouseMove.bindAsEventListener(this);
    this._titleBarMouseDownListener = this._onTitleBarMouseDown.bindAsEventListener(this);
    this._windowMouseUpListener = this._onWindowMouseUp.bindAsEventListener(this);
    
    Event.observe(window, "mousemove", this._windowMouseMoveListener);
    Event.observe(this._titleBar, "mousedown", this._titleBarMouseDownListener);
    Event.observe(window, "resize", this._windowResizedListener);

    _Dialogs.set(this._id, this);
  },
  setContentUrl : function (contentURL) {
    this._dialogContent.setAttribute("src", contentURL);
  },
  open : function() {
    this._glassPane = Builder.node("div", {className: "dialogGlassPane"});
    document.body.appendChild(this._glassPane);
    this._parentNode.appendChild(this._dialogNode);
    this._isOpen = true;
    this._recalculateSize();
  },
  close : function() {
    this._glassPane.remove();
    this._parentNode.removeChild(this._dialogNode);
    Event.stopObserving(window, "scroll", this._windowScrollListener);
    this._isOpen = false;
    this._dialogNode = undefined;
    Event.stopObserving(window, "resize", this._windowResizedListener);
    Event.stopObserving(window, "mousemove", this._windowMouseMoveListener);
    Event.stopObserving(window, "mouseup", this._windowMouseUpListener);
    Event.stopObserving(document, "mouseup", this._windowMouseUpListener);
    Event.stopObserving(this._frameDocument , "mouseup", this._windowMouseUpListener);
   
    // Event.stopObserving(this._frameWindow, "mouseup", this._windowMouseUpListener);
    Event.stopObserving(this._titleBar, "mousedown", this._titleBarMouseDownListener);
    _Dialogs.unset(this._id);

    while (this._listeners.length > 0)
      this._listeners.pop();
  },
  show: function() {
    if (this._isHidden) {
      this._dialogNode.show();
	    this._glassPane = Builder.node("div", {className: "dialogGlassPane"});
	    document.body.appendChild(this._glassPane);
//	    this._parentNode.appendChild(this._dialogNode); // Causes a reload of dialog contents
	    this._isHidden = false;
	    this._recalculateSize();
    }
  },
  hide : function() {
    if (!this._isHidden) {
      this._glassPane.remove();
      Event.stopObserving(window, "scroll", this._windowScrollListener);
      this._isHidden = true;
      this._dialogNode.hide();
    }
  },
  center : function() {
    var dialogDims = this._dialogNode.getDimensions();
    var viewportDims = document.viewport.getDimensions();
    var scrollOffs = document.viewport.getScrollOffsets();
    var left = (viewportDims.width / 2) - (dialogDims.width / 2);
    var top = Math.max((viewportDims.height / 2) - (dialogDims.height / 2) + scrollOffs.top, 0);
    
    this._dialogNode.setStyle( {
      top :top + 'px',
      left :left + 'px'
    });
  },
  getContentElement : function() {
    return this._dialogContent;
  },
  getDialogElement: function () {
    return this._dialogNode;
  },
  setSize : function(width, height) {
    this._dialogNode.setStyle( {
      width :width,
      height :height
    });

    this._recalculateSize();
  },
  addDialogListener : function(listener) {
    this._listeners.push(listener);
  },
  setTitle : function(title) {
    this._title.innerHTML = title;
    this._recalculateSize();
  },
  getContentDocument: function () {
    return this._frameDocument;  
  },
  getContentWindow: function () {
    return this._frameWindow;  
  },
  setHideOnly: function(value) {
    this._isHideOnly = value;
  },
  clickOk: function () {
    var resultsFunc = this._frameWindow.getResults;
    var results;
    if (resultsFunc)
      results = resultsFunc();
    if (this._fire("okClick", { results: results })) {
      if (this._isHideOnly) {
        this.hide();
      } else {
        this.close();
      }
    }
  },
  clickCancel: function () {
    if (this._fire("cancelClick", { })) {
      if (this._isHideOnly) {
        this.hide();
      } else {
        this.close();
      }
    }
  },
  getOkButtonElement: function () {
    return this._okButton;
  },
  getCancelButtonElement: function () {
    return this._cancelButton;
  },
  enableOkButton: function () {
    this._okButton.removeAttribute("disabled");
  },
  disableOkButton: function () {
    this._okButton.setAttribute("disabled", "disabled");
  },
  enableCancelButton: function () {
    this._cancelButton.removeAttribute("disabled");
  },
  disableCancelButton: function () {
    this._cancelButton.setAttribute("disabled", "disabled");
  },
  _onOkButtonClick: function (event) {
    this.clickOk();
  },
  _onCancelButtonClick: function (event) {
    this.clickCancel();
  },
  _onDialogContentLoad: function (event) {
    if (!this._contentLoaded) {
      this._contentLoaded = true;
      this._frameDocument = this.getContentElement().contentDocument;
      this._frameWindow = this._frameDocument.parentWindow;
      if (!this._frameWindow)
        this._frameWindow = this._frameDocument.defaultView;
      
      var _this = this;
      this._frameWindow.getDialog = function () {
        return _this;
      };
      
      Event.observe(window, "mouseup", this._windowMouseUpListener);
      Event.observe(document, "mouseup", this._windowMouseUpListener);
      Event.observe(this._frameDocument , "mouseup", this._windowMouseUpListener);
      Event.observe(this._frameWindow, "mouseup", this._windowMouseUpListener);
      
      if (this._pendingContent)
        this._frameDocument.body.innerHTML = this._pendingContent;
      
      if (this._autoEvaluateSize == true) {
        var windowFramesHeight = this._titleBar.getHeight() + this._titleBar.getHorizontalPaddings() + this._titleBar.getHorizontalMargins() + this._buttonsContainer.getHeight() + this._buttonsContainer.getHorizontalPaddings() + this._buttonsContainer.getHorizontalMargins();
      
        var contentHeight = 0;
        var contentWidth = 0;
        var children = this._frameDocument.body.childNodes;
        for (var i = 0; i < children.length; i++) {
          var node = $(children[i]);
          if (node.nodeType == 1) {
            if (node.getReservedHeight) {
              contentHeight += node.getReservedHeight();
              contentWidth += node.getReservedWidth();
            }
          }
        }
       
        $(this._frameDocument.body).setStyle({
          height: contentHeight + 'px',
          width: contentWidth + 'px',
          overflow: 'hidden'
        });
       
        this.getContentElement().setStyle({
          height: contentHeight + 'px',
          width: contentWidth + 'px' 
        });
       
        this._dialogNode.setStyle( {
          height: (contentHeight + windowFramesHeight) + 'px'
        });
        
        this._recalculateSize();
      }
  
      // Event.stopObserving(this._dialogContent, "load", this._dialogContentLoadListener);
      
      if (this._appearDuration > 0) {
        var _this = this;
        new Effect.Appear(this._dialogNode, {
          duration: this._appearDuration,
          afterFinish: function () {
            _this._fire("onLoad", { });
          }
        });
      } else {
        this._fire("onLoad", { });
      }
    } else {
      Event.stopObserving(window, "mouseup", this._windowMouseUpListener);
      Event.stopObserving(document, "mouseup", this._windowMouseUpListener);
      Event.stopObserving(this._frameDocument , "mouseup", this._windowMouseUpListener);
      Event.stopObserving(this._frameWindow, "mouseup", this._windowMouseUpListener);
      
      this._frameDocument = this.getContentElement().contentDocument;
      this._frameWindow = this._frameDocument.parentWindow;
      if (!this._frameWindow)
        this._frameWindow = this._frameDocument.defaultView;
      
      Event.observe(window, "mouseup", this._windowMouseUpListener);
      Event.observe(document, "mouseup", this._windowMouseUpListener);
      Event.observe(this._frameDocument , "mouseup", this._windowMouseUpListener);
      Event.observe(this._frameWindow, "mouseup", this._windowMouseUpListener);
    }
  },
  _onWindowResized : function(event) {
    this._recalculateSize();
  },
  _fire : function(eventName, extraInfo) {
    var eventObj = Object.extend( {
      name: eventName,
      dialog: this,
      _preventDefault: false,
      preventDefault: function (b) {
        this._preventDefault = b || true;
      }
    }, extraInfo);
    
    for ( var i = 0; i < this._listeners.length; i++) {
      this._listeners[i].call(this, eventObj);
    }
    
    return !eventObj._preventDefault;
  },
  _recalculateSize : function() {
    if (this._isOpen == true) {
      if (this._centered == true) {
        this.center();
      }

      var contentElement = $(this.getContentElement());

      if (contentElement) {
        var maxHeight = Element.getMaxHeight(contentElement);
        contentElement.setStyle({
          height: maxHeight + 'px'
        });
      }
      
      this._fire("resized", {});
    }
  },
  _onWindowMouseMove: function(event) {
    if (this._dragging == true) {
      var mx = Event.pointerX(event);
      var my = Event.pointerY(event);

      this._dialogNode.setStyle({
        top: (this._dialogNode.getStyleInPixels('top') + (my - this._dragY)) + 'px',
        left: (this._dialogNode.getStyleInPixels('left') + (mx - this._dragX)) + 'px'
      });
      
      this._dragX = mx;
      this._dragY = my;
    }
  },
  _onTitleBarMouseDown: function(event) {
    if (this._dragging == false) {
      this._dragging = true;
      this._dragX = Event.pointerX(event);
      this._dragY = Event.pointerY(event);
      
      // Temp glasspane is needed due to dialog content being in an event eating iframe that
      // easily stops dragging when the dialog is dragged downwards quickly enough for the
      // cursor to get on top of the iframe.
      
      var glassPane = Builder.node("div");
      glassPane.setStyle({
        position: 'absolute',
        top: '0px',
        left: '0px',
        height: this._dialogNode.offsetHeight + 'px',
        width: '100%'
      });
      glassPane.id = 'dialogDragGlasspane';
      this._dialogNode.appendChild(glassPane);
    }
  },
  _onWindowMouseUp: function(event) {
    this._dragging = false;
      
    // Remove the glasspane inserted in _onTitleBarMouseDown
      
    var glassPane = $('dialogDragGlasspane');
    if (glassPane) {
      glassPane.remove();
    }
  }
});

function getDialog(id) {
  return _Dialogs.get(id);
}
