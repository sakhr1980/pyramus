IxAbstractDraftTask = Class.create({
  initialize : function() {
    
  },
  createDraftData: function () {
    throw new Error("Not implemented"); 
  },
  restoreDraftData: function () { 
    throw new Error("Not implemented");
  },
  _compress: function (value) {
    return value;
  },
  _uncompress: function (draftData) {
    return draftData;
  }
});

IxInputFieldDraftTask = Class.create(IxAbstractDraftTask, {
  initialize : function($super) {
    $super();
  },
  createDraftData: function (element) {
    var elementName = element.name;
    var elementValue;
    if (element.type == 'checkbox')
      elementValue = element.checked;
    else
      elementValue = element.value;
    
    if (elementName.blank())
      return null;
    else
      return new IxElementDraft('inputField', elementName, this._compress(elementValue));
  },
  restoreDraftData: function (elementDraft) {
    var name = elementDraft.getName();
    var index = elementDraft.getIndex();
    var elements = document.getElementsByName(name);
    if (elements.length > index) {
      if (elements[index].type == 'checkbox')
        elements[index].checked = this._uncompress(elementDraft.getData());
      else
        elements[index].value = this._uncompress(elementDraft.getData());
    } 
  }
});

Object.extend(IxInputFieldDraftTask, {
  supports: ['input[type="checkbox"]','input[type="hidden"]','input[type="password"]','input[type="radio"]','input[type="text"]']
});

IxSelectFieldDraftTask = Class.create(IxAbstractDraftTask, {
  initialize : function($super) {
    $super();
  },
  createDraftData: function (element) {
    var elementName = element.name;
    var elementValue = element.selectedIndex;
    
    if (elementName.blank())
      return null;
    else
      return new IxElementDraft('selectField', elementName, this._compress(elementValue));
  },
  restoreDraftData: function (elementDraft) {
    var name = elementDraft.getName();
    var index = elementDraft.getIndex();
    var elements = document.getElementsByName(name);
    var selectedValue = this._uncompress(elementDraft.getData());
    
    if (elements.length > index) {
      var element = elements[index];
      element.selectedIndex = selectedValue;
    }
  }
});

Object.extend(IxSelectFieldDraftTask, {
  supports: ['select']
});

IxTextAreaFieldDraftTask = Class.create(IxAbstractDraftTask, {
  initialize : function($super) {
    $super();
  },
  createDraftData: function (element) {
    var elementName = element.name;
    var elementValue = element.value;
    
    if (elementName.blank())
      return null;
    else
      return new IxElementDraft('textareaField', elementName, this._compress(elementValue));
  },
  restoreDraftData: function (elementDraft) {
    var name = elementDraft.getName();
    var index = elementDraft.getIndex();
    var elements = document.getElementsByName(name);
    if (elements.length > index) {
      elements[index].value = this._uncompress(elementDraft.getData());
    } 
  }
});

Object.extend(IxTextAreaFieldDraftTask, {
  supports: ['textarea']
});

IxTableComponentDraftTask = Class.create(IxAbstractDraftTask, {
  initialize : function($super) {
    $super();
  },
  createDraftData: function (element) {
    var draftData = new Hash();
    
    var tableId = element.getAttribute("ix:tableid");
    
    if (!tableId || tableId.blank())
      return null;
    
    var table = getIxTableById(tableId);
    if (!table)
      return null;
    
    var columns = table.getColumnCount();
    var rows = table.getRowCount();
    
    draftData.set("rows", rows);
    
    for (var column = 0; column < columns; column++) {
      for (var row = 0; row < rows; row++) {
        var value = table.getCellValue(row, column);
        var editable = table.isCellEditable(row, column);
        var dataType = table.getCellDataType(row, column);
        var visible = table.isCellVisible(row, column);
        var disabled = table.isCellDisabled(row, column);
          
        draftData.set(row + '.' + column + '.value', this._compress(value));
        draftData.set(row + '.' + column + '.editable', editable);
        draftData.set(row + '.' + column + '.dataType', dataType);
        draftData.set(row + '.' + column + '.visible', visible);
        draftData.set(row + '.' + column + '.disabled', disabled);
        
        switch (dataType) {
          case 'select':
            var cellEditor = table.getCellEditor(row, column);
            var selectController = IxTableControllers.getController('select');
            var dynamicOptions = selectController.isDynamicOptions(cellEditor);
            draftData.set(row + '.' + column + '.dynamicOptions', dynamicOptions);
            
            if (dynamicOptions) {
              var options = selectController.getOptions(cellEditor);
              draftData.set(row + '.' + column + '.options', options);
              
//              if (editable) {
//                var optionCount = cellEditor.options.length;
//                
//                draftData.set(row + '.' + column + '.dynamicOptions.count', optionCount);
//                
//                for (var optionIndex = 0; optionIndex < optionCount; optionIndex++) {
//                  TODO: Values should be retrieved thru controller 
//                  var option = cellEditor.options[optionIndex];
//                  var optionName = option.innerHTML;
//                  var optionValue = option.value;
//      
//                  draftData.set(row + '.' + column + '.dynamicOption.' + optionIndex + '.name', optionName);          
//                  draftData.set(row + '.' + column + '.dynamicOption.' + optionIndex + '.value', optionValue);
//                }
//              }
//              else {
//                var displayValue = cellEditor._fieldContent ? cellEditor._fieldContent.innerHTML : value;
//                draftData.set(row + '.' + column + '.displayValue', this._compress(value));
//              }
            }
          break;          
        } 
      }
    }
    
    return new IxElementDraft('tableComponent', tableId, draftData.toJSON());
  },
  restoreDraftData: function (elementDraft) {
    var name = elementDraft.getName();
    var index = elementDraft.getIndex();
    var table = getIxTableById(name);
    if (!table)
      return null;
    
    var tableData = Object.isString(elementDraft.getData()) ? elementDraft.getData().evalJSON() : elementDraft.getData();
    var columns = table.getColumnCount();
    var rows = tableData.rows;
    
    table.deleteAllRows();
    
    for (var row = 0; row < rows; row++) {
      var rowData = new Array();
      
      for (var column = 0; column < columns; column++) {
        rowData.push('');
      }
      
      table.addRow(rowData);
      
      for (var column = 0; column < columns; column++) {
        var editable = tableData[row + '.' + column + '.editable'];
        var dataType = tableData[row + '.' + column + '.dataType'];
        var visible = tableData[row + '.' + column + '.visible'];
        var disabled = tableData[row + '.' + column + '.disabled'];
        var value = this._uncompress(tableData[row + '.' + column + '.value']);

        table.setCellDataType(row, column, dataType);
        table.setCellEditable(row, column, editable);
        
        if (visible)
          table.showCell(row, column);
        else
          table.hideCell(row, column);
        
        if (disabled)  
          table.disableCellEditor(row, column);
        else
          table.enableCellEditor(row, column);
        
        switch (dataType) {
          case 'select':
            var dynamicOptions = tableData[row + '.' + column + '.dynamicOptions'];
            var cellEditor = table.getCellEditor(row, column);
            var selectController = IxTableControllers.getController('select');
            
            if (dynamicOptions) {
              var options = tableData[row + '.' + column + '.options'];
              if (options)
                selectController.setOptions(cellEditor, options);
            }
          break;
        }
        
        table.setCellValue(row, column, value);

//        if (dynamicOptions) {
//          var cellEditor = table.getCellEditor(row, column);
//          if (editable) {
//            var optionCount = tableData[row + '.' + column + '.dynamicOptions.count'];
//            
//            for (var optionIndex = 0; optionIndex < optionCount; optionIndex++) {
//              var optionName = tableData[row + '.' + column + '.dynamicOption.' + optionIndex + '.name'];
//              var optionValue = tableData[row + '.' + column + '.dynamicOption.' + optionIndex + '.value'];
//  
//              IxTableControllers.getController('select').addOption(cellEditor, optionValue, optionName);
//            }
//            var value = this._uncompress(tableData[row + '.' + column + '.value']);
//            table.setCellValue(row, column, value);
//          }
//          else {
//            IxTableControllers.getController('select').setEditorValue(cellEditor,
//                this._uncompress(tableData[row + '.' + column + '.value']),
//                this._uncompress(tableData[row + '.' + column + '.displayValue']));
//          }
//        }
//        else {
//          var value = this._uncompress(tableData[row + '.' + column + '.value']);
//          table.setCellValue(row, column, value);
//        }
      }
    }
  }
});

Object.extend(IxTableComponentDraftTask, {
  supports: ['div.ixTable']
});

IxCKEditorFieldDraftTask = Class.create(IxAbstractDraftTask, {
  initialize : function($super) {
    $super();
  },
  createDraftData: function (element) {
    var name = element.name;
    var ckInstance = CKEDITOR.instances[name];
    if (ckInstance) {
      ckInstance.updateElement();
      return new IxElementDraft('ckeditorField', name, ckInstance.getData());
    }
    
    return null;
  },
  restoreDraftData: function (elementDraft) {
    var name = elementDraft.getName();
    var ckInstance = CKEDITOR.instances[name];
    if (ckInstance) {
      try {
        ckInstance.setData(elementDraft.getData());
      } catch (e) {
        // TODO: Crashes on IE, Check if IE still restores draft correctly
      }
    }
  }
});

Object.extend(IxCKEditorFieldDraftTask, {
  supports: ['textarea[ix:ckeditor="true"]']
});

IxDraftTaskVault = {
  getTaskClassFor: function (element) {
    for (var i = this._taskTypes.length - 1; i >= 0; i--) {
      for (var j = 0; j < this._taskTypes[i].supports.length; j++) {
        var selector = new Selector(this._taskTypes[i].supports[j]);
        if (selector.match(element))
          return this._taskTypes[i];
      }
    }
  },
  getTaskClassById: function (taskId) {
    return this._tasksById.get(taskId);
  },
  _registerTaskType: function (clazz, taskId) {
    this._taskTypes.push(clazz);
    this._tasksById.set(taskId, clazz);
  },
  _taskTypes: new Array(),
  _tasksById: new Hash()
};

IxElementDraft = Class.create({
  initialize : function(draftTaskId, name, data) {
    this._data = data;
    this._name = name;
    this._draftTaskId = draftTaskId;
  },
  getData: function () {
    return this._data;
  },
  getName: function () {
    return this._name;
  },
  getIndex: function () {
    return 0;
  },
  getDraftTaskId: function () {
    return this._draftTaskId;
  }
});

IxDraftTaskVault._registerTaskType(IxInputFieldDraftTask, 'inputField');
IxDraftTaskVault._registerTaskType(IxSelectFieldDraftTask, 'selectField');
IxDraftTaskVault._registerTaskType(IxTextAreaFieldDraftTask, 'textareaField');
IxDraftTaskVault._registerTaskType(IxTableComponentDraftTask, 'tableComponent');
IxDraftTaskVault._registerTaskType(IxCKEditorFieldDraftTask, 'ckeditorField');

IxDraftAPI = Class.create({
  createFormDraft: function () {
    var elementDrafts = new Array();
    this._draftChildElements(document.documentElement, elementDrafts);
    var draftData = new Hash();
    draftData.set("elements", elementDrafts);
    return draftData.toJSON();
  },
  restoreFormDraft: function (restoreData) {
    var draftData = Object.isString(restoreData) ? restoreData.evalJSON() : restoreData;
    var elementDrafts = draftData.elements;
    
    for (var i = 0, l = elementDrafts.length; i < l; i++) {
      var elementDraft = new IxElementDraft(elementDrafts[i].taskId, elementDrafts[i].name, elementDrafts[i].data);
      var draftTaskClass = IxDraftTaskVault.getTaskClassById(elementDraft.getDraftTaskId());
      if (draftTaskClass) {
        var draftTask = new draftTaskClass();
        draftTask.restoreDraftData(elementDraft);
      }
    }
    
    document.fire("ix:draftRestore");
  },
  _draftChildElements: function (element, draftData) {
    var elements = element.childNodes;
    
    for (var i = 0, l = elements.length; i < l; i++) {
      if (elements[i].nodeType == 1) {
        var draftTaskClass = IxDraftTaskVault.getTaskClassFor(elements[i]);
        if (draftTaskClass) {
          var draftTask = new draftTaskClass();
          var elementDraft = draftTask.createDraftData(elements[i]);
          if ((elementDraft != null) && (elementDraft.getData() != undefined) && (elementDraft.getData() != null)) {
            var elementData = new Hash();
            elementData.set("name", elementDraft.getName());
            elementData.set("taskId", elementDraft.getDraftTaskId());
            elementData.set("data", elementDraft.getData());
            draftData.push(elementData);
          }
        } else {
          this._draftChildElements(elements[i], draftData);
        }
      }
    } 
  }
});