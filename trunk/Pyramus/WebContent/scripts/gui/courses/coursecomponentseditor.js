CourseComponentsEditor = Class.create({
  initialize : function(parentNode, options) {
    this._domNode = new Element("div", {className: "courseComponents"});
    
    this._options = options;
    
    this._componentTableSettings = [
      { },
      {
        title: options.nameHeader,
        left: 8,
        width: 236
      },
      {
        title: options.lengthHeader,
        left : 248,
        width : 60
      },
      {
        title: options.descriptionHeader,
        left: 312,
        right : 68
      },
      {
        width: 30,
        right: 30,
        tooltip: this._options.editButtonTooltip
      },
      {
        width: 30,
        right: 0,
        tooltip: this._options.removeButtonTooltip  
      },
      {
        width: 30,
        right: 0,
        tooltip: this._options.archiveButtonTooltip
      }
    ];
    
    this._componentCountElement = new Element("input", {type: "hidden", value:"0", name: this._options.paramName + '.componentCount'});  
    this._domNode.appendChild(this._componentCountElement);
    
    this._titlesContainer = new Element("div", {className: "courseComponentTitles"});

    for (var i = 0, l = this._componentTableSettings.length; i < l; i++) {
      var setting = this._componentTableSettings[i];
      if (setting.title) {
        var titleElement = new Element("div", {className: "courseComponentTitle"}).update(setting.title);
        
        if (setting.left) {
          titleElement.setStyle({
            left: setting.left + 'px'
          });
        }
        
        if (setting.right) {
          titleElement.setStyle({
            right: setting.right + 'px'
          });
        }
        
        if (setting.width) {
          titleElement.setStyle({
            width: setting.width + 'px'
          });
        }
        
        this._titlesContainer.appendChild(titleElement);
      }
    }
    
    this._domNode.appendChild(this._titlesContainer);
    
    this._components = new Array();
    
    parentNode.appendChild(this._domNode);
  },
  getCourseComponentIndex: function (courseComponent) {
    for (var i = 0, l = this._components.length; i < l; i++) {
      if (this._components[i] == courseComponent) {
        return i;
      }
    }
    
    return -1;
  },
  addCourseComponent: function (componentId, componentName, componentLength, componentDescription) {
    var componentCount = parseInt(this._componentCountElement.value);
     
    var componentEditor = new CourseComponentEditor(this._domNode, this, {
      paramName: this._options.paramName + '.component.' + componentCount,
      componentId: componentId,
      componentName: componentName,
      componentLength: componentLength,
      componentDescription: componentDescription,
      editButtonTooltip: this._options.editButtonTooltip,
      removeButtonTooltip: this._options.removeButtonTooltip,
      archiveButtonTooltip: this._options.archiveButtonTooltip,
      materialResourceUnit: this._options.materialResourceUnit,
      workResourceUnit: this._options.workResourceUnit,
      archiveConfirmTitle: this._options.archiveConfirmTitle,
      archiveConfirmContentLocale: this._options.archiveConfirmContentLocale,
      archiveConfirmOkLabel: this._options.archiveConfirmOkLabel,
      archiveConfirmCancelLabel: this._options.archiveConfirmCancelLabel,
      resourceSearchUrl: this._options.resourceSearchUrl,
      resourceSearchParamName: this._options.resourceSearchParamName,
      resourceSearchProgressImageUrl: this._options.resourceSearchProgressImageUrl,
      resourceNameTitle: this._options.resourceNameTitle,
      resourceUsageTitle: this._options.resourceUsageTitle,
      resourceQuantityTitle: this._options.resourceQuantityTitle,
      resourceRemoveButtonTooltip: this._options.resourceRemoveButtonTooltip,
      resourceArchiveButtonTooltip: this._options.resourceArchiveButtonTooltip,
      componentTableSettings: this._componentTableSettings,
      resourceDeleteConfirmTitle: this._options.resourceDeleteConfirmTitle,
      resourceDeleteConfirmContentLocale: this._options.resourceDeleteConfirmContentLocale,
      resourceDeleteConfirmOkLabel: this._options.resourceDeleteConfirmOkLabel,
      resourceDeleteConfirmCancelLabel: this._options.resourceDeleteConfirmCancelLabel,
      resourceCategoryTableSettings: [
        { },
        { },
        { },
        {
          title: this._options.resourceNameTitle,
          left: 8,
          width: 236
        }, { 
          title: this._options.resourceUsageTitle,
          left : 248,
          width : 120
        }, { 
          title: '',
          left : 248,
          width : 120
        }, { 
          left : 372,
          width : 20
        },{
          left: 392,
          right: 0,
          tooltip: this._options.resourceRemoveButtonTooltip
        }, {
          left: 392,
          right: 0,
          tooltip: this._options.resourceArchiveButtonTooltip
        }
      ]
    });
    
    this._componentCountElement.value = componentCount + 1;
    
    this._components.push(componentEditor);
    
    var _this = this;
    componentEditor.getComponentsInfoTable().addListener("cellValueChange", function (event) {
      _this._updateComponentHoursSum();
    });
    
    this._updateComponentHoursSum();
    
    return componentEditor;
  },
  removeCourseComponent: function (courseComponentIndex) {
    var component = this._components[courseComponentIndex];
    if (component) {
      this._components.splice(courseComponentIndex, 1);
      for (var i = courseComponentIndex, l = this._components.length; i < l;i++) {
        this._components[i].setParamName(this._options.paramName + '.component.' + i);
      }
      component.remove();
      this._componentCountElement.value = parseInt(this._componentCountElement.value) - 1;
    }
    
    this._updateComponentHoursSum();
  },
  getComponentEditors: function () {
    return this._components;
  },
  removeAllCourseComponents: function () {
    var components = this.getComponentEditors();
    for (var i = components.length - 1; i >= 0; i--) {
      this.removeCourseComponent(i);
    }
  },
  _updateComponentHoursSum: function () {
    var sum = 0;
    
    for (var i = 0, l = this._components.length; i < l; i++) {
      sum += parseInt(this._components[i].getLength());
    }
    
    $(this._options.componentHoursSumElement).update(sum);
  }
});