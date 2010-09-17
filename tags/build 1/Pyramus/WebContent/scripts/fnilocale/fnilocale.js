FNILocaleClass = Class.create({
  initialize : function($super) {
    this._locales = new Object();
    this._settings = new Object();
    this._loadedLocales = new Hash();
    this._defaultLocale = 'en_US';
  },
  getDate: function (timestamp, longFormat) {
    var d = new Date(timestamp);
    return this._getDateFormat(longFormat).format(this, d);
  },  
  getTime: function (timestamp) {
    var d = new Date(timestamp);
    return this._getTimeFormat().format(this, d);
  },
  getDateTime: function (timestamp, longFormat) {    
    return this.getDate(timestamp, longFormat) + ' ' + this.getTime(timestamp);
  },
  getText: function() {
    var params = new Array();
    params.push(this.getLocale());
    var args = $A(arguments);
    for (var i = 0, l = args.length; i < l; i++) {
      params.push(args[i]); 
    }
    
    return this.getLocalizedText.apply(this, params);
  },
  getLocalizedText: function () {
    var args = $A(arguments);
    var locale = args[0];
    var text = args[1]; 
    
    var localeVault = this._getLocaleVault(locale);

    if (localeVault) {
      var localizedText = localeVault[text];
      if (localizedText == undefined) {
        localizedText = '[[' + text + ']]';
      } else {
        for (var i = 2; i < args.length; i++) {
          localizedText = localizedText.replace('\{' + (i - 2) + '\}', arguments[i]);
        }
      }
        
      return localizedText;  
    } else {
      return '[[' + text + ']]';
    }
  },
  setLanguage: function (language) {
    this._dateFormatShort = undefined;
    this._dateFormatLong = undefined;
    this._timeFormat = undefined;

    this._currentLanguage = language;
  },
  setCountry: function (country) {
    this._dateFormatShort = undefined;
    this._dateFormatLong = undefined;
    this._timeFormat = undefined;
    
    this._currentCountry = country;
  },
  getCountry: function () {
    return this._currentCountry;
  },
  setLocale: function (locale) {
    var splitted = locale.split('_');
    if (splitted.length == 2) {
      this.setLanguage(splitted[0]);
      this.setCountry(splitted[1]);
    } else {
      this.setLanguage(splitted[0]);
      this.setCountry(null);
    }
  },
  getLanguage: function () {
    return this._currentLanguage;
  },
  getLocale: function () {
    return this.getLanguage() + '_' + this.getCountry();
  },
  getLocales: function () {
    return this._loadedLocales.keys();
  },
  getLanguages: function () {
    var result = new Array();
    var locales = this.getLocales();
    for (var i = 0, l = locales.length; i < l; i++) {
      result.push(locales[i].split('_')[0]);
    }
    
    return result;
  },
  getSetting: function (name) {
    var settingsVault = this._getSettingVault(this.getLocale());
    if (settingsVault)
      return settingsVault[name];
    else 
      return "[[" + name + "]]";
  },
  setSetting: function (name, value) {
    var settingsVault = this._getSettingVault(this.getLocale());
    if (settingsVault)
      settingsVault[name] = value;
  },
  localizeElements: function (rootElement) {
    var elements = $(rootElement).select('[fni:locale]');
    for (var i = 0; i < elements.length; i++) {
      var e = elements[i];
      var text = this.getText(e.getAttribute("fni:locale"));
      
      if ((e.tagName == 'INPUT') && (e.type == 'submit')) 
        e.value = text;
      else 
        e.innerHTML = text;
    }
  },
  loadLocale: function (locale, file) {
    var _this = this;
    new Ajax.Request(file, {
      onSuccess: function (transport) {
        try {
          var json = transport.responseText.evalJSON();
          
          for (var key in json.localeStrings) {
            _this._setLocaleString(locale, key, json.localeStrings[key]);
          }
          
          for (var key in json.settings) {
            _this._setLocaleSetting(locale, key, json.settings[key]);
          }
          
          _this._loadedLocales.set(locale, true);
        } catch (e) {
          alert(e);
        }
      },
      asynchronous: false
    });
  },
  _getLocaleVault: function (locale) {
    var localeVault = this._locales[locale];
    if (!localeVault) {
      var s = locale.split("_");
      if (s.length == 2)
        localeVault = this._locales[s[0]];
    }
    
    if (!localeVault)
      localeVault = this._locales[this._defaultLocale];
    
    return localeVault;
  },
  _getSettingVault: function (locale) {
    var settingsVault = this._settings[locale];
    if (!settingsVault) {
      var s = locale.split("_");
      if (s.length == 2)
        settingsVault = this._settings[s[0]];
    }
    
    if (!settingsVault)
      settingsVault = this._settings[this._defaultLocale];
    
    return settingsVault;
  },
  _setLocaleString: function (locale, key, string) {
    if (!this._locales[locale])
      this._locales[locale] = new Object();
    
    var s = locale.split('_');
    
    if (s.length == 2) {
      var lang = s[0];
      if (!this._locales[lang])
        this._locales[lang] = new Object();
      
      this._locales[locale][key] = string;
      this._locales[lang][key] = string;      
    } else {
      this._locales[locale][key] = string;
    }
  },
  _setLocaleSetting: function (locale, key, setting) {
    if (!this._settings[locale])
      this._settings[locale] = new Object();
    
    var s = locale.split('_');
    if (s.lenght == 2) {
      if (!this._settings[lang])
        this._settings[lang] = new Object();
      
      this._settings[locale][key] = setting;
      this._settings[lang][key] = setting;
    } else {
      this._settings[locale][key] = setting;
    }
  },
  _getDateFormat: function (longFormat) {
    if (longFormat == true) {
      if (!this._dateFormatLong) {
        var dateFormatString = this.getSetting("dateFormatLong");
        if (!dateFormatString||dateFormatString.blank()||(dateFormatString == '[[dateFormatLong]]'))
          dateFormatString = 'dd.MMMM.yyyy';
        
        this._dateFormatLong = new FNIDateFormat(dateFormatString);
      }
      
      return this._dateFormatLong;
    } else {
      if (!this._dateFormatShort) {
        var dateFormatString = this.getSetting("dateFormatShort");
        if (!dateFormatString||dateFormatString.blank()||(dateFormatString == '[[dateFormatShort]]'))
          dateFormatString = 'dd.MM.yyyy';
        
        this._dateFormatShort = new FNIDateFormat(dateFormatString);
      }
      
      return this._dateFormatShort;
    }
  },
  _getTimeFormat: function () {
    if (!this._timeFormat) {
      var timeFormatString = this.getSetting("timeFormat");
      if (!timeFormatString||timeFormatString.blank()||(timeFormatString == '[[timeFormat]]'))
        timeFormatString = 'kk:mm';
      
      this._timeFormat = new FNIDateFormat(timeFormatString);
    }
        
    return this._timeFormat;
  }
});

/**
 * Java style date formatter
 * 
 * y    year              (yy = 09, yyyy = 2009) 
 * M    month             (M = 1, MM = 01, MMMM = month_00 - 11) TODO
 * d    day in month      (d = 3, DD = 03) 
 * E    day in week       (E = weekdayShort_00 - 06, EE = weekdayLong_00 - 06, [00 = Sunday]) TODO 
 * a    am/pm
 * k    Hour in day       (k = 1, kk = 01)
 * h    Hour in day am/pm (h = 1, hh = 01)
 * m    Minute in hour    (m = 1, mm = 01)
 * s    Second in minute  (s = 1, ss = 01)
 * S    Millisecond       (S = 5, SSS = 005)
 */
FNIDateFormat = Class.create({
  initialize : function (formatString) {
    this._formatString = formatString;
    this._tokenFormatters = new Array();
    
    this._localizedMonth = false;
    this._localizedDayShort = false;
    this._localizedDayLong = false;
    
    var parsed = formatString;
    var formatters = FNITokenFormatters.getFormatters();
    var keys = formatters.keys();
    for (var i = 0, len = keys.length; i < len; i++) {
      var formatter = formatters.get(keys[i]);
      var re = new RegExp(formatter.token, 'g');
      if (re.test(parsed)) {
        this._tokenFormatters.push(formatter);
        parsed = parsed.replace(re, '');
        if (formatter.token == 'E')
          this._localizedDayShort = true;
        else if (formatter.token == 'EE')
          this._localizedDayLong = true;
        else if (formatter.token == 'MMMM')
          this._localizedMonth = true;
      }      
    }
  },
  format: function (locale, date) {
    var result = this._formatString;
    for (var i = 0, len = this._tokenFormatters.length; i < len; i++)
      result = this._tokenFormatters[i].format(result, date);
    
    if (this._localizedDayLong) {
      var i = result.length;
      while ((i = result.lastIndexOf('EL', i - 1)) >= 0) {
        var n = result.substring(i + 2, i + 4);
        result = result.substring(0, i) + locale.getText('weekdayLong_' + n) + result.substring(i + 4);
      } 
    }
    
    if (this._localizedDayShort) {
      var i = result.length;
      while ((i = result.lastIndexOf('EE', i - 1)) >= 0) {
        var n = result.substring(i + 2, i + 4);
        result = result.substring(0, i) + locale.getText('weekdayShort_' + n) + result.substring(i + 4);
      } 
    }
    
    if (this._localizedMonth) {
      var i = result.length;
      while ((i = result.lastIndexOf('MM', i - 1)) >= 0) {
        var n = result.substring(i + 2, i + 4);
        result = result.substring(0, i) + locale.getText('month_' + n) + result.substring(i + 4);
      } 
    }
    
    return result;
  }
});

FNITokenFormatters = {
  register: function (formatter) {
    this._formatters.set(formatter.token, formatter);
  },
  getFormatters: function () {
    return this._formatters;
  },
  _formatters: new Hash()
};

FNITokenFormatters.register({token: 'yyyy', format: function (str, d) { 
  return str.replace(/yyyy/g, d.getFullYear());
}});

FNITokenFormatters.register({token: 'yy', format: function (str, d) {
  return str.replace(/yy/g, new String(d.getFullYear()).substring(2));
}});

FNITokenFormatters.register({token: 'MM', format: function (str, d) {
  return str.replace(/MM/g, (d.getMonth() + 1).toPaddedString(2));
}});

FNITokenFormatters.register({token: 'M', format: function (str, d) {
  return str.replace(/M/g, d.getMonth() + 1);
}});

FNITokenFormatters.register({token: 'dd', format: function (str, d) {
  return str.replace(/dd/g, d.getDate().toPaddedString(2));
}});

FNITokenFormatters.register({token: 'd', format: function (str, d) {
  return str.replace(/d/g, d.getDate());
}});

FNITokenFormatters.register({token: 'a', format: function (str, d) {
  return str.replace(/a/g, (d.getHours() - 12) > -1 ? "PM" : "AM");
}});

FNITokenFormatters.register({token: 'kk', format: function (str, d) {
  return str.replace(/kk/g, d.getHours().toPaddedString(2));
}});

FNITokenFormatters.register({token: 'k', format: function (str, d) {
  return str.replace(/k/g, d.getHours());
}});

FNITokenFormatters.register({token: 'hh', format: function (str, d) {
  return str.replace(/hh/g, (((d.getHours() + 11) % 12) + 1).toPaddedString(2));
}});

FNITokenFormatters.register({token: 'h', format: function (str, d) {
  return str.replace(/h/g, (((d.getHours() + 11) % 12) + 1));
}});

FNITokenFormatters.register({token: 'mm', format: function (str, d) {
  return str.replace(/mm/g, d.getMinutes().toPaddedString(2));
}});

FNITokenFormatters.register({token: 'm', format: function (str, d) {
  return str.replace(/m/g, d.getMinutes());
}});

FNITokenFormatters.register({token: 'ss', format: function (str, d) {
  return str.replace(/ss/g, d.getSeconds().toPaddedString(2));
}});

FNITokenFormatters.register({token: 's', format: function (str, d) {
  return str.replace(/s/g, d.getSeconds());
}});

FNITokenFormatters.register({token: 'SSS', format: function (str, d) {
  return str.replace(/SSS/g, d.getMilliseconds().toPaddedString(3));
}});

FNITokenFormatters.register({token: 'S', format: function (str, d) {
  return str.replace(/S/g, d.getMilliseconds());
}});

FNITokenFormatters.register({token: 'MMMM', format: function (str, d) {
  return str.replace(/MMMM/g, 'MM' + (d.getMonth() + 1).toPaddedString(2));
}});

FNITokenFormatters.register({token: 'EE', format: function (str, d) {
  return str.replace(/MMMM/g, 'EL' + d.getDay().toPaddedString(2));
}});

FNITokenFormatters.register({token: 'E', format: function (str, d) {
  return str.replace(/MMMM/g, 'EE' + d.getDay().toPaddedString(2));
}});

Object.extend(FNILocaleClass.prototype, FNIEventSupport);