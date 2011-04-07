IxDateField = Class.create({
  initialize : function(options) {
    var element = options.element;
    this._paramName = element.name;
    
    var idAttr = element.getAttribute("ix:datefieldid");
    if (idAttr)
      this._id = idAttr;
    else
      this._id = null;
    
    this._dayInput = Builder.node("input", {id: this._paramName + "_-dd", name: this._paramName + "_-dd", type: 'text', maxlength: 2, className: 'ixDateFieldDay'});
    this._monthInput = Builder.node("input", {id: this._paramName + "_-mm", name: this._paramName + "_-mm", type: 'text', maxlength: 2, className: 'ixDateFieldMonth'});
    this._yearInput = Builder.node("input", {id: this._paramName + '_', name: this._paramName + '_', type: 'text', maxlength: 4, className: 'ixDateFieldYear split-date'});
    this._timestampInput = Builder.node("input", {id: this._paramName, name: this._paramName, type: 'hidden'});
    
    if (options.yearClass)
      this.addYearClass(options.yearClass);
    if (options.monthClass)
      this.addMonthClass(options.monthClass);
    if (options.dayClass)
      this.addDayClass(options.dayClass);
    if (options.value != undefined)
      this.setTimestamp(options.value);
    if (options.enabled === false)
      this.disable();
   
    this._dayFieldValueChangeListener = this._onDayFieldValueChange.bindAsEventListener(this);
    this._monthFieldValueChangeListener = this._onMonthFieldValueChange.bindAsEventListener(this);
    this._yearFieldValueChangeListener = this._onYearFieldValueChange.bindAsEventListener(this);
    this._timestampFieldValueChangeListener = this._onTimestampFieldValueChange.bindAsEventListener(this);
    
    Event.observe(this._dayInput, "change", this._dayFieldValueChangeListener);
    Event.observe(this._monthInput, "change", this._monthFieldValueChangeListener);
    Event.observe(this._yearInput, "change", this._yearFieldValueChangeListener);

    this._updatingTimestamp = false;
    
    this._domNode = Builder.node("div", {className: 'ixDateField'}, [
      this._timestampInput,
      this._dayInput,
      this._monthInput,
      this._yearInput
    ]);
    
    this.replaceNode(element);
  },
  getParamName : function() {
    return this._paramName;
  },
  getId : function() {
    return this._id;
  },
  setDay : function(day) {
    this._dayInput.value = day;
  },
  setMonth : function(month) {
    this._monthInput.value = month;
  },
  setYear : function(year) {
    this._yearInput.value = year;
  },
  setTimestamp : function(timestamp) {
    var date = new Date();
    date.setTime(timestamp);
    this._yearInput.value = date.getFullYear();
    this._monthInput.value = (date.getMonth() + 1).toPaddedString(2);
    this._dayInput.value = (date.getDate()).toPaddedString(2);

    date.setUTCFullYear(this.getYear());
    date.setUTCMonth(this.getMonth() - 1);
    date.setUTCDate(this.getDay());
    date.setUTCHours(0);
    date.setUTCMilliseconds(0);
    date.setUTCMinutes(0);
    date.setUTCSeconds(0);
    this._timestampInput.value = date.getTime();
  },
  getDay : function() {
    return this._dayInput.value == '' ? NaN : new Number(this._dayInput.value);
  },
  getMonth : function() {
    return this._monthInput.value == '' ? NaN : new Number(this._monthInput.value);
  },
  getYear : function() {
    return this._yearInput.value == '' ? NaN : new Number(this._yearInput.value);
  },
  getTimestamp : function() {
    return this._timestampInput.value == NaN ? '' : new Number(this._timestampInput.value);
  },
  getTimestampNode: function () {
    return this._timestampInput;
  },
  disable: function () {
    this._dayInput.disabled = true;
    this._monthInput.disabled = true;
    this._yearInput.disabled = true;
  },
  enable: function () {
    this._dayInput.disabled = false;
    this._monthInput.disabled = false;
    this._yearInput.disabled = false;
  },
  getDOMNode: function () {
    return this._domNode;
  },
  getDayField: function () {
    return this._dayInput;
  },
  getMonthField: function () {
    return this._monthInput;
  },
  getYearField: function () {
    return this._yearInput;
  },
  addDayClass: function(className) {
    this._dayInput.addClassName(className);
  },
  addMonthClass: function(className) {
    this._monthInput.addClassName(className);
  },
  addYearClass: function(className) {
    this._yearInput.addClassName(className);
  },
  replaceNode : function(node) {
    var parent = node.parentNode;
    parent.insertBefore(this._domNode, node);
    
    if (node.value) {
      var initialValue = new Number(node.value);
      if (initialValue != NaN) {
        this.setTimestamp(initialValue);
      }
    }
    
    parent.removeChild(node);
    datePickerController.create(this._yearInput);
    document.fire("ix:dateFieldReplace", {
      dateField: this
    });
  },
  destroy: function() {
    datePickerController.datePickers[this._yearInput.id].destroy();
    datePickerController.datePickers[this._yearInput.id] = undefined;
    delete datePickerController.datePickers[this._yearInput.id];
    this._domNode.remove();
    this._datePicker = undefined;
  },
  _onDayFieldValueChange : function(event) {
    this._updateTimestampField();
  },
  _onMonthFieldValueChange : function(event) {
    this._updateTimestampField();
  },
  _onYearFieldValueChange : function(event) {
    this._updateTimestampField();
  },
  _onTimestampFieldValueChange: function (event) {
    if (this._updatingTimestamp != true) {
      this._updatingTimestamp = true;
      try {
        this.setTimestamp(this._timestampInput.value);
      } finally {
        this._updatingTimestamp = false;
      }
    }
  },
  _updateTimestampField : function() {
    this._updatingTimestamp = true;
    try {
      var year = this.getYear();
      var month = this.getMonth();
      var day = this.getDay();
      if (year == NaN || month == NaN || day == NaN) {
        this._timestampInput.value = '';
      }
      else {
        var date = new Date();
        date.setUTCFullYear(this.getYear(), this.getMonth() - 1, this.getDay());
        date.setUTCHours(0);
        date.setUTCMilliseconds(0);
        date.setUTCMinutes(0);
        date.setUTCSeconds(0);
        this._timestampInput.value = date.getTime();
      }
    } finally {
      this._updatingTimestamp = false;
    }
    
    this.fire("change", {
      dateFieldComponent: this
    });
  }
});

var __ixDateFields = new Array(); 

Object.extend(IxDateField.prototype,fni.events.FNIEventSupport);

function replaceDateField(field, options) {
  var dateField = new IxDateField(Object.extend({ element: field }, options||{}));
    __ixDateFields.push(dateField);
  return dateField;
}

function getIxDateFields() {
  return __ixDateFields;
}

function getIxDateField(id) {
  var fields = getIxDateFields();
  
  for (var i = 0, l = fields.length; i < l; i++) {
    if (fields[i].getId() && (fields[i].getId() == id)) {
      return fields[i];
    }
  }
  
  return null;
}

function replaceDateFields(container) {
  var dateFields;
  
  if (!container)
    dateFields = $$("input[ix:datefield='true']");
  else
    dateFields = container.select("input[ix:datefield='true']");
  
  for (var i = 0; i < dateFields.length; i++) {
    replaceDateField(dateFields[i]);
  }
}
