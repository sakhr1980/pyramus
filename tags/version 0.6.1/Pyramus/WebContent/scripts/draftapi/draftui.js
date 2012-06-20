/**
 * TODO: 
 * 
 * T�m� luokka pit�� saada lokalisoitua!!!
 */

IxDraftUI = Class.create({ 
  initialize : function(options) {
    this._saveInfoContainer = new Element("div", {className: "draftSavedContainer"});
    this._saveInfoText = new Element("span");
//    this._discardDraftLink = new Element("a", {className: "discardDraftLink"}).update("Discard draft");
    this._saveInfoContainer.appendChild(this._saveInfoText);
//    this._saveInfoContainer.appendChild(this._discardDraftLink);
//    this._discardDraftLinkClickListener = this._onDiscardDraftLinkClick.bindAsEventListener(this);
//    Event.observe(this._discardDraftLink, "click", this._discardDraftLinkClickListener);
    
    this._draftSavingContainer = new Element("div", {className: "draftSavingContainer"}).update('Saving draft...');
    
    options.draftSavedTextContainer.appendChild(this._saveInfoContainer);
    options.draftSavingTextContainer.appendChild(this._draftSavingContainer);
    
    this._saveInfoContainer.hide();
    this._draftSavingContainer.hide();
  },
  updateDraftStart: function () {
    this._showSavingInfo();
  },
  updateDraftEnd: function (draftSaved) {
    this._hideSavingInfo();
    this._changeInfoText('<div style="font-size:110%">Draft saved at ' + this._getDate(draftSaved) + "</div>");
  },
  restoreDraftStart: function () {
    
  },
  restoreDraftEnd: function (draftSaved) {
    this._changeInfoText('<div style="font-size:14px">View data was restored from draft succesfully</div>');
  },
  deleteDraftStart: function () {
    
  },
  deleteDraftEnd: function () {
    this._changeInfoText("");
  },
  _getDate: function (timestamp) {
    var date = new Date(timestamp);
    return date.getDate() + '.' + (date.getMonth() + 1) + '.' + date.getFullYear() + ' ' + date.getHours().toPaddedString(2) + ':' + date.getMinutes().toPaddedString(2);
  },
  _showSavingInfo: function () {
    try {
      this._showingSavingInfo = true;
      var _this = this;
      Effect.Appear(this._draftSavingContainer, {
    	duration: 1,
        afterFinish: function () {
          _this._showingSavingInfo = false;
          if (_this._hidingSavingInfo == true) {
            
            _this._hideSavingInfo();
          }
        }
      });
    } catch (e) { }
  },
  _hideSavingInfo: function () {
    try {
      this._hidingSavingInfo = true;
      if (this._showingSavingInfo != true) {
        var _this = this;
        Effect.Fade(this._draftSavingContainer, {
          duration: 1,
          delay: 2,
          afterFinish: function () {
            _this._hidingSavingInfo = false;
            if (_this._showingSavingInfo == true) {
              _this._showSavingInfo();
            }
          }
        });
      }
    } catch (e) {}
  },
  _changeInfoText: function (text) {
    try {
      if (this._saveInfoContainer) {
        if (text.blank()) {
          this._saveInfoContainer.hide();
        } else {
          this._saveInfoContainer.show();
        }
        
        this._saveInfoText.update(text);  
        this._visualizeInfoTextChange(0);
      }
    } catch (e) { }
  },
  _visualizeInfoTextChange: function (repeat) {
    if (this._saveInfoContainer) {
      var originalBackgroundColor = this._saveInfoContainer.getStyle("background-color");
      var _this = this;
      new Effect.Morph(this._saveInfoContainer, {
        style: {'background-color': '#9BEC9B'},
        duration: 1,
        afterFinish: function () {
          new Effect.Morph(_this._saveInfoContainer, {
            style: {'background-color': originalBackgroundColor},
            duration: 1,
            afterFinish: function () {
              if (repeat > 0)
              _this._visualizeInfoTextChange(repeat - 1);
            }
          });
        }
      });
    }
  }/**,
  _onDiscardDraftLinkClick: function (event) {
    deleteFormDraft(function() {
      window.location.href = window.location.href;
    });
  }**/
});