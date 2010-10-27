CourseComponentEditor = Class.create({
  initialize : function(parentNode, componentsEditor, options) {
    this._domNode = new Element("div", {className: "courseComponent"});
    
    this._editing = false;
    this._options = options;
    this._paramName = options.paramName;
    
    this._resourceCategoryCountElement = new Element("input", {type: "hidden", value:"0", name: this._paramName + '.resourceCategoryCount'});
    this._domNode.appendChild(this._resourceCategoryCountElement);
    
    this._resourcesContainer = new Element("div", {className: "courseComponentResources"});
    
    this._categoryTables = new Hash();

    this._componentInfoTable = this._createComponentInfoTable();
    
    var rowIndex = this._componentInfoTable.addRow([this._options.componentId, this._options.componentName, this._options.componentLength, this._options.componentDescription, '', '', '']);
    
    if (this._options.componentId && this._options.componentId > 0) {
      this._componentInfoTable.showCell(rowIndex, this._componentInfoTable.getNamedColumnIndex("archiveButton"));
    } else {
      this._componentInfoTable.showCell(rowIndex, this._componentInfoTable.getNamedColumnIndex("removeButton"));
    }
    
    this._resourceTitlesContainer = new Element("div", {className: "courseComponentResourceTitles"});
    
    for (var i = 0, l = this._options.resourceCategoryTableSettings.length; i < l; i++) {
      var setting = this._options.resourceCategoryTableSettings[i];
      if (setting.title) {
        var resourceTitle = new Element("div", {className: "courseComponentResourceTitle"}).update(setting.title);
        
        if (setting.left) {
          resourceTitle.setStyle({
            left: setting.left + 'px'
          });
        }
        
        if (setting.right) {
          resourceTitle.setStyle({
            right: setting.right + 'px'
          });
        }
        
        if (setting.width) {
          resourceTitle.setStyle({
            width: setting.width + 'px'
          });
        }
        
        this._resourceTitlesContainer.appendChild(resourceTitle);
      }
    }
    
    this._domNode.appendChild(this._resourceTitlesContainer);

    this._domNode.appendChild(this._resourcesContainer);
  
    parentNode.appendChild(this._domNode);
  },
  setParamName: function (paramName) {
    this._paramName = paramName;
    this._resourceCategoryCountElement.name = this._paramName + '.resourceCategoryCount';
    this._componentInfoTable.changeTableId(this._options.paramName);
    
    var categoryTables = this._categoryTables.values();
    for (var i = 0, l = categoryTables.length; i < l; i++) {
      categoryTables[i].changeTableId(this._paramName + '.' + categoryCount + '.resources');
    }
  },
  addResourceCategory: function (categoryId, categoryName) {
    var categoryCount = parseInt(this._resourceCategoryCountElement.value);
    var categoryElement = new Element("div", {className: "courseComponentResourceCategory"});
    var categoryIdElement = new Element("input", {type: "hidden", value: categoryId});
    var categoryNameElement = new Element("div", {className: "courseComponentResourceCategoryName"}).update(categoryName);
    categoryElement.appendChild(categoryIdElement);
    categoryElement.appendChild(categoryNameElement);
    this._resourcesContainer.appendChild(categoryElement);
    
    var _this = this;
    
    var columnSettings = [
      {
        dataType: 'hidden',
        paramName: 'id'
      }, {
        dataType: 'hidden',
        paramName: 'resourceId'
      }, {
        dataType: 'text',
        editable: false,
        paramName: 'name'
      }, {
        dataType : 'number',
        editable: false,
        hidden: true,
        paramName: 'usage',
        editorClassNames: 'required'
      }, {
        dataType : 'number',
        editable: false,
        hidden: true,
        paramName: 'quantity',
        editorClassNames: 'required'
      }, {
        dataType : 'text',
        editable: false,
        paramName: 'unit'
      },{
        dataType: 'button',
        paramName: 'removeButton',
        hidden: true,
        imgsrc: GLOBAL_contextPath + '/gfx/list-remove.png',
        onclick: function (event) {
          event.tableObject.deleteRow(event.row);
        }
      }, {
        dataType: 'button',
        paramName: 'archiveButton',
        hidden: true,
        imgsrc: GLOBAL_contextPath + '/gfx/edit-delete.png',
        onclick: function (event) {
          var table = event.tableObject;
          var courseComponentResourceId = table.getCellValue(event.row, table.getNamedColumnIndex('id'));
          var name = table.getCellValue(event.row, table.getNamedColumnIndex('name'));
          var url = GLOBAL_contextPath + "/simpledialog.page?localeId=" + _this._options.resourceDeleteConfirmContentLocale + "&localeParams=" + encodeURIComponent(name);
           
          deleteRowIndex = event.row; 
             
          var dialog = new IxDialog({
            id : 'confirmRemoval',
            contentURL : url,
            centered : true,
            showOk : true,  
            showCancel : true,
            autoEvaluateSize: true,
            title : _this._options.resourceDeleteConfirmTitle,
            okLabel : _this._options.resourceDeleteConfirmOkLabel,
            cancelLabel : _this._options.resourceDeleteConfirmCancelLabel
          });
        
          dialog.addDialogListener(function(event) {
            switch (event.name) {
              case 'okClick':
                JSONRequest.request("courses/deletecoursecomponentresource.json", {
                  parameters: {
                    courseComponentResourceId: courseComponentResourceId
                  },
                  onSuccess: function (jsonResponse) {
                    table.deleteRow(deleteRowIndex);
                  }
                });   
              break;
            }
          });
        
          dialog.open();
        }
      }
    ];

    for (var i = 0, l = this._options.resourceCategoryTableSettings.length; i < l; i++) {
      var settings = this._options.resourceCategoryTableSettings[i];
      if (settings.left)
        columnSettings[i].left = settings.left;
      if (settings.right)
        columnSettings[i].right = settings.right;
      if (settings.width)
        columnSettings[i].width = settings.width;
      if (settings.tooltip)
        columnSettings[i].tooltip = settings.tooltip;
    }
    
    var resourcesTable = new IxTable(categoryElement, {
      id: this._options.paramName + '.' + categoryCount + '.resources',
      columns: columnSettings
    });
    
    this._categoryTables.set(categoryId, resourcesTable);
    
    this._resourceCategoryCountElement.value = categoryCount + 1;
    
    return categoryElement;
  },
  hasResourceCategory: function (categoryId) {
    return this._categoryTables.get(categoryId) ? true : false;
  },
  addResource: function (categoryId, componentResourceId, resourceId, resourceType, resourceName, usage, quantity) {
    var categoryTable = this._categoryTables.get(categoryId);
    var row = categoryTable.addRow([componentResourceId, resourceId, resourceName, usage, quantity, '', '', '']);
    var usageColumn;
    
    if (resourceType == 'MATERIAL_RESOURCE') {
      usageColumn = categoryTable.getNamedColumnIndex("quantity");
      categoryTable.setCellValue(row, categoryTable.getNamedColumnIndex("unit"), 'kpl');
    } else if (resourceType == 'WORK_RESOURCE') {
      usageColumn = categoryTable.getNamedColumnIndex("usage");
      categoryTable.setCellValue(row, categoryTable.getNamedColumnIndex("unit"), '%');
    }

    categoryTable.showCell(row, usageColumn);
    if (this._editing) {
      if (componentResourceId && componentResourceId > 0) {
        categoryTable.showCell(row, categoryTable.getNamedColumnIndex("archiveButton"));
      } else {
        categoryTable.showCell(row, categoryTable.getNamedColumnIndex("removeButton"));
      }

      for (var row = 0, rows = categoryTable.getRowCount(); row < rows; row++) {
        categoryTable.setCellEditable(row, usageColumn, true);
      }
    }
  },
  remove: function () {
    this._domNode.remove();
  },
  _addResourceAddInput: function () {
    this._resourceAddInput = new Element("div", {className: "courseComponentResourceInputContainer"});
    
    var inputElement = new Element("input", {type: "text"});
    var indicatorElement = new Element("span", {className: "autocomplete_progress_indicator", style: "display: none"}).update('<img src="' + this._options.resourceSearchProgressImageUrl + '"/>');
    var choicesElement = new Element("div", {className: "autocomplete_choices"});  
    this._resourceAddInput.appendChild(inputElement);
    this._resourceAddInput.appendChild(indicatorElement);
    this._resourceAddInput.appendChild(choicesElement);
    
    this._domNode.appendChild(this._resourceAddInput);
     
    var _this = this;
    new Ajax.Autocompleter(inputElement, choicesElement, this._options.resourceSearchUrl, {
      paramName: this._options.resourceSearchParamName, 
      minChars: 2, 
      indicator: indicatorElement,
      afterUpdateElement : function getSelectionId(text, li) {
        inputElement.value = '';
        
        var li = $(li);
        if (!li.hasClassName("autocompleteGroupTitle")) {
          var categoryId = null;
          var categoryName = null;
          var resourceId = li.down('input[name="resourceId"]').value;
          var resourceType = li.down('input[name="resourceType"]').value;
          var resourceName = li.down("span").innerHTML;
          
          var parent = $(li.parentNode);
          for (var i = 0, l = parent.childNodes.length; i < l; i++) {
            var child = $(parent.childNodes[i]);
            if (child.hasClassName("autocompleteGroupTitle")) {
              categoryId = child.down('input[name="categoryId"]').value;
              categoryName = child.down("span").innerHTML;
            } else {
              if (child.down('input[name="resourceId"]').value == resourceId) {
                break;
              }
            }
          }
          
          if (!_this.hasResourceCategory(categoryId))
            _this.addResourceCategory(categoryId, categoryName);
          
          _this.addResource(categoryId, -1, resourceId, resourceType, resourceName, 100, 1);
        }
      }
    });
  },
  _removeResourceAddInput: function () {
    if (this._resourceAddInput)
      this._resourceAddInput.remove();
  },
  toggleEditable: function () {
    if (!this._editing) {
      this._editing = true;
      
      this._addResourceAddInput();
      
      for (var i = 0; i < this._componentInfoTable.getColumnCount(); i++) {
        this._componentInfoTable.setCellEditable(0, i, true);
      }
      
      var categoryTableIds = this._categoryTables.keys();
      for (var cat = 0, cats = categoryTableIds.length; cat < cats; cat++) {
        var categoryTable = this._categoryTables.get(categoryTableIds[cat]);
        for (var row = 0, rows = categoryTable.getRowCount(); row < rows; row++) {
          categoryTable.setCellEditable(row, categoryTable.getNamedColumnIndex("usage"), true);
          categoryTable.setCellEditable(row, categoryTable.getNamedColumnIndex("quantity"), true);
          
          var componentResourceId = categoryTable.getCellValue(row, categoryTable.getNamedColumnIndex("id"));

          if (componentResourceId && componentResourceId > 0) {
            categoryTable.showCell(row, categoryTable.getNamedColumnIndex("archiveButton"));
          } else {
            categoryTable.showCell(row, categoryTable.getNamedColumnIndex("removeButton"));
          }
        }
      }
    } else {
      this._editing = false;
      
      this._removeResourceAddInput();
      
      for (var i = 0; i < this._componentInfoTable.getColumnCount(); i++) {
        this._componentInfoTable.setCellEditable(0, i, false);
      }
      
      var categoryTableIds = this._categoryTables.keys();
      for (var cat = 0, cats = categoryTableIds.length; cat < cats; cat++) {
        var categoryTable = this._categoryTables.get(categoryTableIds[cat]);
        for (var row = 0, rows = categoryTable.getRowCount(); row < rows; row++) {
          for (var col = 3, cols = categoryTable.getColumnCount(); col < cols; col++) {
            categoryTable.setCellEditable(row, col, false);
          }
          
          categoryTable.hideCell(row, categoryTable.getNamedColumnIndex("archiveButton"));
          categoryTable.hideCell(row, categoryTable.getNamedColumnIndex("removeButton"));
        }
      }
    }
  },
  _createComponentInfoTable: function () {
    var _this = this;
    var componentInfoTableColumnSettings = [
      {
        dataType: 'hidden',
        paramName: 'componentId'
      }, {
        left : 8,
        width : 236,
        dataType: 'text',
        editable: false,
        paramName: 'name',
        editorClassNames: 'required'
      }, {
        left : 248,
        width : 60,
        dataType : 'number',
        editable: false,
        paramName: 'length',
        editorClassNames: 'required'
      }, {
        left: 312,
        right : 68,
        dataType: 'text',
        editable: false,
        paramName: 'description'
      }, {
        width: 30,
        right: 30,
        dataType: 'button',
        paramName: 'editButton',
        imgsrc: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
        tooltip: this._options.editButtonTooltip,
        onclick: function (event) {
          _this.toggleEditable();
        }
      }, {
        width: 30,
        right: 0,
        dataType: 'button',
        paramName: 'removeButton',
        hidden: true,
        imgsrc: GLOBAL_contextPath + '/gfx/list-remove.png',
        tooltip: this._options.removeButtonTooltip,  
        onclick: function (event) {
          componentsEditor.removeCourseComponent(componentsEditor.getCourseComponentIndex(_this));
        }
      }, {
        width: 30,
        right: 0,
        dataType: 'button',
        paramName: 'archiveButton',
        hidden: true,
        imgsrc: GLOBAL_contextPath + '/gfx/edit-delete.png',
        tooltip: this._options.archiveButtonTooltip,
        onclick: function (event) {
        }
      }];

    for (var i = 0, l = this._options.componentTableSettings.length; i < l; i++) {
      var settings = this._options.componentTableSettings[i];
      if (settings.left)
        componentInfoTableColumnSettings[i].left = settings.left;
      if (settings.right)
        componentInfoTableColumnSettings[i].right = settings.right;
      if (settings.width)
        componentInfoTableColumnSettings[i].width = settings.width;
      if (settings.tooltip)
        componentInfoTableColumnSettings[i].tooltip = settings.tooltip;
    }
    
    return new IxTable(this._domNode, {
      id: this._options.paramName,
      columns: componentInfoTableColumnSettings
    });
  }
});