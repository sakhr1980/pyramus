IxFieldValidator = Class.create({
  initialize : function() {},
  validate: function (field) {},
  _getFieldValue: function (field) {
    if (field.type == 'checkbox')
      return field.checked ? '0' : '1';
    else
      return field.value;
  },
  getType: function () {
    return null;
  },
  className: undefined
});

IxLinkedFieldValidator = Class.create(IxFieldValidator, {
  initialize : function($super) {
    $super();
  },
  getLinkedField: function (field) {},
  getType: function ($super) {
    return IxFieldValidator.TYPE_LINKED;
  }
});

IxRequiredFieldValidator = Class.create(IxFieldValidator, {
  initialize : function($super) {
    $super();
  },
  validate: function ($super, field) {
    return this._getFieldValue(field).blank() ? IxFieldValidator.STATUS_INVALID : IxFieldValidator.STATUS_VALID;
  },
  getType: function ($super) {
    return IxFieldValidator.TYPE_MANDATORY;
  },
  className: 'required'
});

IxEqualsFieldValidator = Class.create(IxLinkedFieldValidator, {
  initialize : function($super) {
    $super();
  },
  validate: function ($super, field) {
    var equalsField = this.getLinkedField(field);
    if (equalsField) {
      var value1 = this._getFieldValue(field);
      var value2 = this._getFieldValue(equalsField);
      if (value1) 
        return value1 == value2 ? IxFieldValidator.STATUS_VALID : IxFieldValidator.STATUS_INVALID;
    } 
    
    return IxFieldValidator.STATUS_UNKNOWN;
  },
  getLinkedField: function (field) {
    var equalsFieldName = field.getAttribute("ix:equals-field-name");
    if (equalsFieldName) {
      var equalsFields = document.getElementsByName(equalsFieldName);
      if (equalsFields.length == 1) {
        return equalsFields[0];
      }
    }
    
    return null;
  },
  className: 'equals'
});

IxMaskFieldValidator = Class.create(IxFieldValidator, {
  initialize : function($super) {
    $super();
  },
  validate: function ($super, field) {
    var value = this._getFieldValue(field);
    var mask = field.getAttribute("ix:validatemask");
    
    if (mask && value) {
      return new RegExp(mask).test(value) ? IxFieldValidator.STATUS_VALID : IxFieldValidator.STATUS_INVALID;
    } else {
      return IxFieldValidator.STATUS_UNKNOWN;
    }
  },
  getType: function ($super) {
    return IxFieldValidator.TYPE_NORMAL;
  },
  className: 'mask'
});

IxEmailFieldValidator = Class.create(IxFieldValidator, {
  initialize : function($super) {
    $super();
    this._validEmailMask = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
  },
  validate: function ($super, field) {
    var value = this._getFieldValue(field);
    
    if (value) {
      return this._validEmailMask.test(value.strip()) ? IxFieldValidator.STATUS_VALID : IxFieldValidator.STATUS_INVALID;
    } else {
      return IxFieldValidator.STATUS_UNKNOWN;
    }
  },
  getType: function ($super) {
    return IxFieldValidator.TYPE_NORMAL;
  },
  className: 'email'
});

IxDateFieldTextValidator = Class.create(IxFieldValidator, {
  initialize : function($super) {
    $super();
  },
  validate: function ($super, field) {
    var value = this._getFieldValue(field);
    if (value) {
      var fieldId = field.parentNode.getAttribute('ix:datefieldid');
      var component = fieldId ? getIxDateField(fieldId) : null;
      if (component) {
        return component.hasValidValue() ? IxFieldValidator.STATUS_VALID : IxFieldValidator.STATUS_INVALID;
      } else {
        return IxFieldValidator.STATUS_UNKNOWN;
      }
    } else {
      return IxFieldValidator.STATUS_UNKNOWN;
    }
  },
  getType: function ($super) {
    return IxFieldValidator.TYPE_NORMAL;
  },
  className: 'ixDateFieldText'
});

IxFloatValidValidator = Class.create(IxFieldValidator, {
  initialize : function($super) {
    $super();
    this._validFloatMask = /(^[0-9]+[,\.][0-9]+$)|(^[0-9]+$)/;
  },
  validate: function ($super, field) {
    var value = this._getFieldValue(field);
    if (value) {
      return this._validFloatMask.test(value) ? IxFieldValidator.STATUS_VALID : IxFieldValidator.STATUS_INVALID;
    } else {
      return IxFieldValidator.STATUS_UNKNOWN;
    }
  },
  getType: function ($super) {
    return IxFieldValidator.TYPE_NORMAL;
  },
  className: 'float'
});

IxNumberValidValidator = Class.create(IxFieldValidator, {
  initialize : function($super) {
    $super();
    this._validNumberMask = /^([0-9]*)$/;
  },
  validate: function ($super, field) {
    var value = this._getFieldValue(field);
    if (value) {
      return this._validNumberMask.test(value) ? IxFieldValidator.STATUS_VALID : IxFieldValidator.STATUS_INVALID;
    } else {
      return IxFieldValidator.STATUS_UNKNOWN;
    }
  },
  getType: function ($super) {
    return IxFieldValidator.TYPE_NORMAL;
  },
  className: 'number'
});

IxTableAutoCompleteSelectFieldValidator = Class.create(IxFieldValidator, {
  initialize : function($super) {
    $super();
  },
  validate: function ($super, field) {
    var idField = $(field.parentNode).down('input.ixTableCellEditorAutoCompleteSelectId');
    
    if (field.value) {
      if ((idField.value && (parseInt(idField.value) >= 0))) 
        return IxFieldValidator.STATUS_VALID;
      else 
        return IxFieldValidator.STATUS_INVALID;
    } else {
      return IxFieldValidator.STATUS_UNKNOWN;
    }
  },
  getType: function ($super) {
    return IxFieldValidator.TYPE_NORMAL;
  },
  className: 'ixTableCellEditorAutoCompleteSelectText'
});

Object.extend(IxFieldValidator, {
  STATUS_UNDEFINED: -1,
  STATUS_UNKNOWN: 0,
  STATUS_INVALID: 1,
  STATUS_VALID: 2,
  
  TYPE_NORMAL:0,
  TYPE_MANDATORY:1,
  TYPE_LINKED:2
});

IxFieldValidatorVault = {
  registerValidator: function (validator) {
    this._validators.set(validator.className, validator);
  },
  getValidators: function () {
    return this._validators.values();
  },
  _validators: new Hash()
};

IxValidationDelegator = Class.create({
  initialize : function(field) {
    this._field = field;
    this._fieldChangeListener = this._onFieldChange.bindAsEventListener(this);
  
    if (field.type == 'checkbox') {
      Event.observe(field, 'click', this._fieldChangeListener);
    } else {
      Event.observe(field, 'keyup', this._fieldChangeListener);
      Event.observe(field, 'change', this._fieldChangeListener);
    }
    this._validators = new Array();
  },
  addValidator: function (validator) {
    this._validators.push(validator);
  },
  deinitialize: function () {
    this._validators.clear();
    
    if (this._field.type == 'checkbox') {
      Event.stopObserving(this._field, 'click', this._fieldChangeListener);
    } else {
      Event.stopObserving(this._field, 'keyup', this._fieldChangeListener);
      Event.stopObserving(this._field, 'change', this._fieldChangeListener);
    }
    
    delete this._validators;
    this._validators = undefined;
    this._field = undefined;
  },
  _onFieldChange: function (event) {
    this.validate(false);
  },
  validate: function (requiredCheckAsUnknown) {
    this._validate(requiredCheckAsUnknown, this._validators, IxFieldValidator.STATUS_UNKNOWN);
  },
  getStatus: function () {
    return this._field._validity == undefined ? IxFieldValidator.STATUS_UNDEFINED : this._field._validity;
  },
  isMandatory: function () {
    return this._field.hasClassName('required');
  },
  insideForm: function (formElement) {
    return this._field.form == formElement;
  },
  _isVisible: function () {
    var e = $(this._field);
    
    if (e.getAttribute('type') == 'hidden')
      return false;
    
    while (e.parentNode) {
      if (!e.visible())
        return false;
      
      e = $(e.parentNode);
    }
    
    return true;
  },
  _validate: function (requiredCheckAsUnknown, validators, initialStatus) {
    var oldStatus = this.getStatus();
    var status = initialStatus;
    
    if (this._isVisible()) {
      for (var i = 0, l = validators.length; i < l; i++) {
        if (status != IxFieldValidator.STATUS_INVALID) {
          switch (validators[i].validate(this._field)) {
            case IxFieldValidator.STATUS_INVALID:
              if (requiredCheckAsUnknown && (validators[i].className == "required"))
                status = IxFieldValidator.STATUS_UNKNOWN;
              else
                status = IxFieldValidator.STATUS_INVALID;
            break;
            case IxFieldValidator.STATUS_VALID:
              status = IxFieldValidator.STATUS_VALID;
            break;
          }
        } else {
          break;
        }
      }
      
      if (status != IxFieldValidator.STATUS_UNKNOWN) {
        var linkedValidators = this._getLinkedValidators(validators);
        for (var i = 0, l = linkedValidators.length; i < l; i++) {
          var linkedField = linkedValidators[i].getLinkedField(this._field);
          var fieldDelegator = IxValidationDelegatorVault.getDelegator(linkedField);
          fieldDelegator._linkedValidate(requiredCheckAsUnknown, status, linkedValidators[i]);
        }
      }
    } else {
      // Invisible elements are always "valid"
      status = IxFieldValidator.STATUS_VALID;
    }
    
    if (oldStatus != status) {
      this._field._validity = status;
      
      switch (status) {
        case IxFieldValidator.STATUS_UNKNOWN:
          if (oldStatus == IxFieldValidator.STATUS_VALID)
            this._field.removeClassName('valid');
          else if (oldStatus == IxFieldValidator.STATUS_INVALID)
            this._field.removeClassName('invalid');
          formValidationHook(this._field.form, this.isMandatory());          
        break;
        case IxFieldValidator.STATUS_INVALID:
          if (oldStatus == IxFieldValidator.STATUS_VALID)
            this._field.removeClassName('valid');
          this._field.addClassName('invalid');
          formValidationHook(this._field.form, true);
        break;
        case IxFieldValidator.STATUS_VALID:
          if (oldStatus == IxFieldValidator.STATUS_INVALID)
            this._field.removeClassName('invalid');
          this._field.addClassName('valid');
          formValidationHook(this._field.form, false);
        break;
      }
    } 
  },
  _getLinkedValidators: function (validators) {
    var result = new Array();
    for (var i = 0, l = validators.length; i < l; i++) {
      var validator = validators[i];
      if (validator.getType() == IxFieldValidator.TYPE_LINKED) 
        result.push(validator);
    }
    
    return result;
  },
  _linkedValidate: function (requiredCheckAsUnknown, status, linkedValidator) {
    this._validate(requiredCheckAsUnknown, this._validators.without(linkedValidator), status);
  }
});

IxValidationDelegatorVault = {
  getDelegator: function (field) {
    return this._delegators.get(this._generateFieldName(field));
  },
  setDelegator: function (field, validationDelegator) {
    if (!this.getDelegator(field)) {
      this._delegators.set(this._generateFieldName(field), validationDelegator);
    } 
  },
  releaseDelegator: function (field) {
    var delegator = this.getDelegator(field);
    if (delegator) {
      this._delegators.unset(this._generateFieldName(field));
      delegator.deinitialize();
      delete delegator;
    }
  },
  getDelegators: function () {
    return this._delegators.values();
  },
  getFormDelegators: function (formElement) {
    var delegators = new Array();
    
    var allDelegators = this.getDelegators();
    for (var i = 0, l = allDelegators.length; i < l; i++) {
      if (allDelegators[i].insideForm(formElement))
        delegators.push(allDelegators[i]);
    }
    
    return delegators;
  },
  _generateFieldName: function (field) {
    return field.name + ';' + field.id;
  },
  _delegators: new Hash()
};

IxFieldValidatorVault.registerValidator(new IxRequiredFieldValidator());
IxFieldValidatorVault.registerValidator(new IxMaskFieldValidator());
IxFieldValidatorVault.registerValidator(new IxEmailFieldValidator());
IxFieldValidatorVault.registerValidator(new IxDateFieldTextValidator());
IxFieldValidatorVault.registerValidator(new IxFloatValidValidator());
IxFieldValidatorVault.registerValidator(new IxNumberValidValidator());
IxFieldValidatorVault.registerValidator(new IxEqualsFieldValidator());
IxFieldValidatorVault.registerValidator(new IxTableAutoCompleteSelectFieldValidator());

function initializeValidation(container) {
  var delegators = new Array();
  var c = $(container||document.body);
  var validators = IxFieldValidatorVault.getValidators();
  for (var i = 0, l = validators.length; i < l; i++) {
    var fields = c.select('.' + validators[i].className);
    for (var j = 0, le = fields.length; j < le; j++) {
      var field = fields[j];
      var delegator = IxValidationDelegatorVault.getDelegator(field);
      if (!delegator) {
        delegator = new IxValidationDelegator(field);
        IxValidationDelegatorVault.setDelegator(field, delegator);
      } 
        
      delegator.addValidator(validators[i]);
      delegators.push(delegator);
    }
  };

  var uniqueDelegators = delegators.uniq();
  for (var i = 0, l = uniqueDelegators.length; i < l; i++)
    uniqueDelegators[i].validate(true);
  
  delete delegators;
  delete uniqueDelegators;
};

function initializeElementValidation(element) {
  var delegator = IxValidationDelegatorVault.getDelegator(element);
  if (!delegator) {
    delegator = new IxValidationDelegator(element);
    IxValidationDelegatorVault.setDelegator(element, delegator);
  } 
  
  var validators = IxFieldValidatorVault.getValidators();
  for (var i = 0, l = validators.length; i < l; i++) {
    var validator = validators[i];
    if (element.hasClassName(validator.className))
      delegator.addValidator(validator);       
  }

  delegator.validate(true);
};

function deinitializeValidation(container) {
  var c = container||document.body;
  for (var i = 0, l = c.childNodes.length; i < l; i++) {
    if (c.childNodes[i].nodeType == 1) {
      deinitializeValidation(c.childNodes[i]);
      IxValidationDelegatorVault.releaseDelegator(c.childNodes[i]);
    }
  };
}

function revalidateAll(requiredCheckAsUnknown) {
  var delegators = IxValidationDelegatorVault.getDelegators();
  for (var i = 0, l = delegators.length; i < l; i++) {
    delegators[i].validate(requiredCheckAsUnknown);
  }
};

function formValidationHook(formElement, isInvalid) {
  if (formElement) {
    formElement = $(formElement);
    var formValidButton = formElement.down(".formvalid");
    if (formValidButton) {
      var valid = !isInvalid;
      
      if (valid) {
        var delegators = IxValidationDelegatorVault.getFormDelegators(formElement);
        for (var i = 0, l = delegators.length; i < l; i++) {
          switch (delegators[i].getStatus()) {
            case IxFieldValidator.STATUS_INVALID:
              valid = false;
            break;
            case IxFieldValidator.STATUS_UNKNOWN:
              if (delegators[i].isMandatory()) {
                valid = false;
              }
            break;
          }

          if (valid == false) {
            break;
          }
        }
      }
      
      if (valid)
        formValidButton.removeAttribute("disabled");
      else
        formValidButton.setAttribute("disabled", "disabled");
    }
  }
};


Element.addMethods({
  validate: function (element, requiredCheckAsUnknown) {
    var delegator = IxValidationDelegatorVault.getDelegator(element);
    if (delegator) {
      delegator.validate(requiredCheckAsUnknown);
    }
  }
});