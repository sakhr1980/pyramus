IxProtoTabs = Class.create({
  initialize: function (tabId) {
    this._tabContainer = $(tabId);
    this._listeners = null;
    this._labelClickListener = this._onLabelClick.bindAsEventListener(this); 
    this._tabNames = new Array(); 
    this._tabs = new Hash();
    this._labels = new Hash();
		this._initializedTabs = new Array();
    
    this._labelElements = this._tabContainer.getElementsByTagName("a"); 
    for (var i = 0; i < this._labelElements.length; i++) { 
       var element = this._labelElements[i];   
       var tabName = this._getLinkkedTabName(element);
       Event.observe(element, "click", this._labelClickListener);
       this._tabNames.push(tabName);
       this._labels.set(tabName, element);
     }
   
    for (var i = 0; i < this._tabNames.length; i++) {
      var tabElement = $(this._tabNames[i]);
      if ((tabElement != null) && (tabElement != undefined)) {
        this._tabs.set(this._tabNames[i], tabElement);
      } else 
        throw new Error(this._tabNames[i] + " cannot be found");
    }
    
    this._activeTab = null; 
    this._activeLabel = null;
    
    if (this._tabNames.length > 0) {
      var selectedTab = this._tabNames[0];
      try {
        var hash = location.hash;
        if (hash && hash.length > 4) {
          if (hash.startsWith('#at-')) {
            selectedTab = hash.substring(4);
            if (!this._hasTab(selectedTab)) {
              selectedTab = this._tabNames[0];
            }
          }
        }
      } catch (e) {
        selectedTab = this._tabNames[0];
      }
       
      this.setActiveTab(selectedTab);
    }
  },
  setActiveTab: function (name) {
    var tab = this._tabs.get(name);
    var label = this._labels.get(name);
    
    if (this._activeTab != null) {
      this._activeTab.removeClassName("activeTab");
    }
      
    if (this._activeLabel != null) {
      this._activeLabel.removeClassName("activeTabLabel");
    }
    
    tab.addClassName("activeTab");
    label.addClassName("activeTabLabel");
     
    var _this = this;
    this._tabs.each(function(pair) {
      pair.value.addClassName("hiddenTab");
    });
    
    tab.removeClassName("hiddenTab");
    this._activeTab = tab;
    this._activeLabel = label;
		
		if (this._initializedTabs.indexOf(name) == -1) {
			this._initializedTabs.push(name);
			this._fire({ 
			  action: "tabInitialized",
				name: name
			});
		}
    
    this._fire({ 
     action: "tabActivated",
        name: name
    });
    
    location.hash = 'at-' + name;
  },
  getActiveTab: function () {
    return this._activeTab ? this._activeTab.id : null;
  },
  _hasTab: function (name) {
    return this._tabs.get(name);
  },
	addListener: function (listener) {
		if (this._listeners == null)
		  this._listeners = new Array();
			
		this._listeners.push(listener);
	},
  _onLabelClick: function (event) {
    Event.stop(event);
    var element = Event.element(event);
    if (element.tagName != 'A')
      element = element.up("a");
    
    var tabName = this._getLinkkedTabName(element);
    this.setActiveTab(tabName);
  },
  _getLinkkedTabName: function (element) {
    var ind = element.href.indexOf("#");
    var result = "";
    if (ind != -1) 
      result = element.href.substring(ind + 1);
    
    return result;
  },
	_fire: function (event) {
		if (this._listeners != null) {
			for (var i = 0; i < this._listeners.length; i++) 
				this._listeners[i].call(this, event);
		};
	}
});