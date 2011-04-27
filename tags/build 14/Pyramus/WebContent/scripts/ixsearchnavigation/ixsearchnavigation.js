var _ixSearchNavigations = new Hash();

IxSearchNavigation = Class.create({
  initialize : function(containerNode, options) {
    this._containerNode = containerNode;
    this._options = options;
    this._pages = new Array();

    var centerPage = this.getMaxNavigationPages() / 2;
    this._centerNavigationPage = centerPage % 2 == 0 ? centerPage : (centerPage)|0;
    
    if (options.id) {
      _ixSearchNavigations.set(options.id, this);
    }

    this.domNode = new Element("div", {className: "IxSearchNavigation"});
    containerNode.appendChild(this.domNode);
    
    this._firstPage = new IxSearchNavigationPage(this.domNode);
    this._firstPage.setPage(0);
    this._firstPage.setText(options.firstText ? options.firstText : "&lt;&lt;");
    this._firstPage.addListener("linkClick", this, this._onPageClick);

    this._previousPage = new IxSearchNavigationPage(this.domNode);
    this._previousPage.setText(options.previousText ? options.previousText : "&lt;");
    this._previousPage.addListener("linkClick", this, this._onPageClick);
    
    for (var i = 0; i < options.maxNavigationPages; i++) {
      var page = new IxSearchNavigationPage(this.domNode);
      page.addListener("linkClick", this, this._onPageClick);
      this._pages.push(page);
    }

    this._nextPage = new IxSearchNavigationPage(this.domNode);
    this._nextPage.setText(options.nextText ? options.nextText : "&gt;");
    this._nextPage.addListener("linkClick", this, this._onPageClick);

    this._lastPage = new IxSearchNavigationPage(this.domNode);
    this._lastPage.setText(options.lastText ? options.lastText : "&gt;&gt;");
    this._lastPage.addListener("linkClick", this, this._onPageClick);
  },
  getFirstVisiblePage: function() {
    if (this.getTotalPages() <= this.getMaxNavigationPages() || this.getCurrentPage() < this.getCenterNavigationPage()) {
      return 0;
    }
    else if (this.getCurrentPage() >= this.getTotalPages() - this.getCenterNavigationPage()) {
      return this.getTotalPages() - this.getMaxNavigationPages();
    }
    else {
      return this.getCurrentPage() - this.getCenterNavigationPage();
    }
  },
  getCenterNavigationPage: function() {
    return this._centerNavigationPage;
  },
  getLastVisiblePage: function() {
    if (this.getTotalPages() <= this.getMaxNavigationPages() || this.getCurrentPage() >= this.getTotalPages() - this.getCenterNavigationPage()) {
      return this.getTotalPages() - 1;
    }
    else if (this.getCurrentPage() < this.getCenterNavigationPage()) {
      return this.getMaxNavigationPages() - 1;
    }
    else {
      return this.getCurrentPage() + this.getCenterNavigationPage();
    }
  },
  setCurrentPage: function(currentPage) {
    if (this._currentPage != currentPage) {
      this._currentPage = currentPage;
      this._previousPage.setPage(currentPage > 0 ? currentPage - 1 : 0);
      this._nextPage.setPage(currentPage < this.getTotalPages() - 1 ? currentPage + 1 : currentPage);
      this._updateNavigation();
    }
  },
  getCurrentPage: function() {
    return this._currentPage;
  },
  setTotalPages: function(totalPages) {
    if (this._totalPages != totalPages) {
      this._totalPages = totalPages;
      this._lastPage.setPage(totalPages - 1);
      this._currentPage = null;
    }
  },
  getTotalPages: function() {
    return this._totalPages;
  },
  getMaxNavigationPages: function() {
    return this._options.maxNavigationPages;
  },
  _updateNavigation: function() {
    var firstVisiblePage = this.getFirstVisiblePage();
    var lastVisiblePage = this.getLastVisiblePage();
    
    this._firstPage.setVisible(this.getTotalPages() > 0);
    this._previousPage.setVisible(this.getTotalPages() > 0);
    if (this.getTotalPages() > 0) {
      this._firstPage.setEnabled(this.getCurrentPage() > 0);
      this._previousPage.setEnabled(this.getCurrentPage() > 0);
    }
    
    var page = firstVisiblePage;
    for (var i = 0; i < this.getMaxNavigationPages(); i++) {
      this._pages[i].setVisible(page <= lastVisiblePage);
      if (this._pages[i].isVisible()) {
        this._pages[i].setPage(page);
        this._pages[i].setText("" + (page + 1));
        this._pages[i].setActive(page == this.getCurrentPage());
      }
      page++;
    }
    
    this._nextPage.setVisible(this.getTotalPages() > 0);
    this._lastPage.setVisible(this.getTotalPages() > 0);
    if (this.getTotalPages() > 0) {
      this._nextPage.setEnabled(this.getCurrentPage() < this.getTotalPages() - 1);
      this._lastPage.setEnabled(this.getCurrentPage() < this.getTotalPages() - 1);
    }
  },
  _onPageClick: function (event) {
    this._options.onclick.call(window, {
      page: event.page
    });
  }
});

IxSearchNavigationPage = Class.create({
  initialize : function(containerNode) {
    this._containerNode = containerNode;
    this.domNode = new Element("div", {className: "IxSearchNavigationPageContainer"});
    this._pageLink = new Element("span", {className: "IxSearchNavigationPageLink"});
    this.domNode.appendChild(this._pageLink);
    this._pageLinkClickListener = this._onPageLinkClick.bindAsEventListener(this);
    Event.observe(this.domNode, "click", this._pageLinkClickListener);
    this.setVisible(false);
    containerNode.appendChild(this.domNode);
  },
  setPage: function(page) {
    this._page = page;
  },
  setText: function(text) {
    this._pageLink.innerHTML = text;
  },
  setActive: function(active) {
    if (active == true) {
      this._pageLink.addClassName('IxSearchNavigationPageLinkActive');
    }
    else {
      this._pageLink.removeClassName('IxSearchNavigationPageLinkActive');
    }
  },
  setEnabled: function(enabled) {
    this._enabled = enabled;
    if (enabled == true) {
      this.domNode.removeClassName("IxSearchNavigationPageContainerDisabled");
    }
    else {
      this.domNode.addClassName("IxSearchNavigationPageContainerDisabled");
    }
  },
  setVisible: function(visible) {
    this._visible = visible;
    if (visible == true) {
      this.domNode.setStyle({
        visibility: 'visible',
        display: ''
      });
    }
    else {
      this.domNode.setStyle({
        visibility: '',
        display: 'none'
      });
    }
  },
  isVisible: function() {
    return this._visible;
  },
  _onPageLinkClick: function (event) {
    this.fire("linkClick", {
      page: this._page
    });
  }
});

Object.extend(IxSearchNavigationPage.prototype, fni.events.FNIEventSupport);

function getSearchNavigationById(id) {
  return _ixSearchNavigations.get(id);
}
