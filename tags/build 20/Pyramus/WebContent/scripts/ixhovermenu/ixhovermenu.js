IxHoverMenu = Class.create({
  initialize : function(parentNode, options) {
    this.domNode = new Element("div", {className: "IxHoverMenu"});
    this._items = new Array();
    this._parentNode = parentNode;
    this._parentNode.appendChild(this.domNode);
    this._textNode = new Element("div", {className: "IxHoverMenuText"}).update(options.text);
    this._arrowNode = new Element("div", {className: "IxHoverMenuTextArrow"}).update("▼");
    this._textContainer = new Element("div", {className: "IxHoverMenuTextContainer"});
    
    this._itemsContainer = new Element("div", {className: "IxHoverMenuItemsContainer"});
    this._textContainer.appendChild(this._textNode);
    this._textContainer.appendChild(this._arrowNode);
    this.domNode.appendChild(this._textContainer);
    this.domNode.appendChild(this._itemsContainer);
  },
  addItem: function (item) {
    this._itemsContainer.appendChild(item.domNode);
    this._items.push(item);
  }
});

IxHoverMenuItem = Class.create({
  initialize : function(options) {
    this._options = options;
    this.domNode = new Element("div", {className: "IxHoverMenuItem"});
    this._textNode = new Element("div", {className: "IxHoverMenuItemText"}).update(options.text);
    this.domNode.appendChild(this._textNode);
    
    if (options.iconURL) {
      this._iconNode = new Element("div", {className: "IxHoverMenuItemIcon", style: "background-image: url(" + options.iconURL + ')'});
      this.domNode.appendChild(this._iconNode);
    }
  }
});

IxHoverMenuLinkItem = Class.create(IxHoverMenuItem, {
  initialize : function($super, options) {
    $super(options);
    this._itemClickListener = this._onItemClick.bindAsEventListener(this);
    Event.observe(this.domNode, "click", this._itemClickListener);
  },
  _onItemClick: function (event) {
    window.location.href = this._options.link;
  }
});

IxHoverMenuClickableItem = Class.create(IxHoverMenuItem, {
  initialize : function($super, options) {
    $super(options);
    this._itemClickListener = this._onItemClick.bindAsEventListener(this);
    Event.observe(this.domNode, "click", this._itemClickListener);
  },
  _onItemClick: function (event) {
    this._options.onclick.call(window, {
      
    });
  }
});