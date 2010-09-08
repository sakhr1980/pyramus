Object.extend(document, {
  getCookie: function (key) {
    var cookie = document.cookie.match(new RegExp('(^|;)\\s*' + escape(key) + '=([^;\\s]*)'));
    return (cookie ? unescape(cookie[2]) : null);
  },
  setCookie: function (key, value, path, expire) {
    var cookieStr = key + '=' + escape(value);
    
    if (expire) {
      var date = new Date();
      date.setTime(date.getTime() + expire);
      cookieStr += "; expires=" + date.toGMTString();
    }
    
    if (path) {
      cookieStr += '; path=' + escape(path);
    }
    
    document.cookie = cookieStr;
  }
});