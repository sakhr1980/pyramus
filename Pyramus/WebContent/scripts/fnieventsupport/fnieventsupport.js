/*
 * FNIEventSupport - Event handling routines 
 * Copyright (C) 2008 - 2009 Antti Leppä / Foyt
 * http://www.foyt.fi
 * 
 * License: 
 * 
 * Licensed under GNU Lesser General Public License Version 2.1 or later (the "LGPL") 
 * http://www.gnu.org/licenses/lgpl.html
 */
FNIEventSupport = {
  /**
   * Adds event listener
   *
   * @param {String} eventName event name which is been listened
   * @param {Object} invoking Object
   * @param {Function} function to be launched on event
   */
  addListener : function(eventName) {
    var args = $A(arguments);

    var object = null;
    var method = null;

    if (args.length == 2) {
      object = this;
      method = args[1];
    } else if (args.length == 3) {
      object = args[1];
      method = args[2];
    }

    var l = new Object();
    l.eventName = eventName;
    l.method = method;
    l.object = object;
    
    if (!this._listeners)
      this._listeners = new Array();
    
    this._listeners.push(l);
  },
  /**
   * Removes event listener
   *
   * @param {String} eventName
   * @param {Object} invoking Object
   */
  removeListener : function(eventName, object) {
    if (this._listeners) {
      for (var i = 0; i < this._listeners.length; i++) {
        var listener = this._listeners[i];
        if ((listener.eventName == eventName) && (listener.object == object))
          this._listeners.splice(i, 1);
      }
    }
  },
  /**
   * Fires event
   *
   * @param {Object} eventName event name
   * @param {Object} event event object
   */
  fire : function(eventName, event) {
    if (this._listeners) {
      var listenerLength = this._listeners.length;
      for ( var i = 0; i < listenerLength; i++) {
        if (this._listeners != null) {
          var listener = this._listeners[i];
          if (listener && (listener != null)) {
            if (listener.eventName == eventName)
              listener.method.call(listener.object, event);
          }
        }
      }
    }
  }
};