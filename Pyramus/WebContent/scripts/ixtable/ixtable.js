/* TODO: DatePicker locales */ 
var _ixTables = new Hash();

IxTableControllers = {
  registerController: function (controller) {
    this._controllers.set(controller.getDataType(), controller);
  },
  getController: function (dataType) {
    return this._controllers.get(dataType);
  },
  EDITMODE_EDITABLE: 0,
  EDITMODE_NOT_EDITABLE: 1, 
  EDITMODE_ONLY_EDITABLE: 2,
  _controllers: new Hash()  
};

IxTable = Class.create({
  initialize : function(parentNode, options) {
    this._rowClickListener = this._onRowClick.bindAsEventListener(this);
    this._activeRows = new Hash();
  
    this._headerRowContent = Builder.node("div", {
      className : "ixTableRowContent"
    }, []);
    
    this._headerRow = Builder.node("div", {
      className : "ixTableHeaderRow"
    }, [ this._headerRowContent ]);

    this._content = Builder.node("div", {
      className : "ixTableContent"
    });
    
    this._rowCount = Builder.node("input", {
      type: 'hidden',
      name: options.id + '.rowCount',
      value: 0
    });
    
    this.domNode = Builder.node("div", {
      className : "ixTable"
    }, [ this._headerRow, this._content, this._rowCount]);

    this._headerCells = new Object();
    this._cellEditors = new Hash();
    parentNode.appendChild(this.domNode);
    
    this._hasHeader = false;
    
    this.options = options;
    for (var i = 0; i < options.columns.length; i++) {
      var column = options.columns[i];
      
      this._hasHeader = this._hasHeader || !((column.header == '') || (!column.header));
      
      var headerCell = Builder.node("div", {
        className : "ixTableHeaderCell"
      }, [ column.header ]);

      if ((column.left != undefined) && (column.left != NaN)) {
        headerCell.setStyle( {
          left : column.left + 'px'
        });
      };

      if ((column.right != undefined) && (column.right != NaN)) {
        headerCell.setStyle( {
          right : column.right + 'px'
        });
      };
      
      if ((column.width != undefined) && (column.width != NaN)) {
        headerCell.setStyle( {
          width : column.width + 'px'
        });
      };

      this._headerCells[i] = headerCell;

      this._headerRowContent.appendChild(headerCell);
    }
    
    this._headerRow.setStyle({
      display: 'none'
    });
    
    if (options.id) {
      _ixTables.set(options.id, this);
      document.fire("ix:tableAdd", {
        tableComponent: this 
      });
      
      this.domNode.setAttribute("ix:tableid", options.id);
    } 
  },
  addRow : function(values) {
    if (values.length != this.options.columns.length) {
      throw new Error("Value array length != table column length");
    }
    
    var rowNumber = this.getRowCount();
    
    var rowContent = Builder.node("div", {
      className : "ixTableRowContent"
    }, []);
    
    var row = Builder.node("div", {
      className : "ixTableRow"
    }, [ rowContent ]);
    
    if (this.options.rowClasses) {
      for (var i = 0, l = this.options.rowClasses.length; i < l; i++) {
        row.addClassName(this.options.rowClasses[i]);
      }
    }
    
    this._content.appendChild(row);

    for (var i = 0; i < this.options.columns.length; i++) {
      var column = this.options.columns[i];
      var name = this.options.id ? this.options.id + '.' + rowNumber + '.' + (column.paramName ? column.paramName : i) : '';
      
      var cell = Builder.node("div", {
        className : "ixTableCell"
      });

      if ((column.left != undefined) && (column.left != NaN)) {
        cell.setStyle( {
          left : column.left + 'px'
        });
      }
      
      if ((column.right != undefined) && (column.right != NaN)) {
        cell.setStyle( {
          right : column.right + 'px'
        });
      }
      
      if ((column.width != undefined) && (column.width != NaN)) {
        cell.setStyle( {
          width : column.width + 'px'
        });
      }
      
      IxTableControllers.getController(column.dataType).attachContentHandler(this, i, rowNumber, cell, this._createCellContentHandler(name, column));
      rowContent.appendChild(cell);
      
      this.setCellValue(rowNumber, i, values[i]);

      if (this._hasHeader == true) {
        this._headerRow.setStyle({
          display: ''
        });
      }
    }
    
    if (this.options.removeRowBtns == true) {
      var delRowButton = Builder.node("div", {className: "ixTableDelRowButton ixTableRowButton"});
      rowContent.appendChild(delRowButton);
      
      var _this = this;
      Event.observe(delRowButton, "click", function (event) {
        _this.deleteRow(row._rowNumber);
      });
    }
    
    row._rowNumber = rowNumber;
    this._setRowCount(this.getRowCount() + 1);
    
    this.fire("rowAdd", {
      tableObject: this,
      row: rowNumber
    });
    
    Event.observe(row, "click", this._rowClickListener);
    
    return rowNumber;
  },
  deleteRow: function (rowNumber) {
    this.fire("beforeRowDelete", {
      tableObject: this,
      row: rowNumber
    });
    
    for (var row = rowNumber; row < (this.getRowCount() - 1); row++) {
      for (var column = 0; column < this.options.columns.length; column++) {
        var cellEditor = this.getCellEditor(row, column);
        var nextCellEditor = this.getCellEditor(row + 1, column); 
        
        IxTableControllers.getController(cellEditor._dataType)._copyState(cellEditor, nextCellEditor);
        
        cellEditor._row = nextCellEditor._row - 1;
      }
    }
    
    this._setRowCount(this.getRowCount() - 1);
    var rowElements = this.domNode.select('.ixTableRow');
    for (var i = this.getRowCount(); i >= 0; i--) {
      if (rowElements[i]._rowNumber == this.getRowCount())
        rowElements[i].remove();
    }
    
    this.fire("rowDelete", {
      tableObject: this,
      row: rowNumber
    });

    if (this.getRowCount() == 0 && this._hasHeader == true) {
      this._headerRow.setStyle({
        display: 'none'
      });
    }
  },
  getNamedColumnIndex: function (name) {
    for (var i = 0; i < this.options.columns.length; i++) {
      var column = this.options.columns[i];
      if (column.paramName == name)
        return i;
    }
    
    return -1;
  },
  deleteAllRows: function () {
    while (this.getRowCount() > 0)
      this.deleteRow(this.getRowCount() - 1);
  },
  getCellEditor: function (row, column) {
    return this._cellEditors.get(row + '.' + column);
  },
  getCellValue: function (row, column) {
    var handlerInstance = this.getCellEditor(row, column);
    return IxTableControllers.getController(handlerInstance._dataType).getEditorValue(handlerInstance);
  },
  setCellValue: function (row, column, value) {
    var handlerInstance = this.getCellEditor(row, column);
    IxTableControllers.getController(handlerInstance._dataType).setEditorValue(handlerInstance, value);
    this.fire("cellValueChanged", {
      tableComponent: this,
      row: row,
      column: column, 
      value: value
    });
  },
  disableCellEditor: function (row, column) {
    var handlerInstance = this.getCellEditor(row, column);
    return IxTableControllers.getController(handlerInstance._dataType).disableEditor(handlerInstance);
  },
  enableCellEditor: function (row, column) {
    var handlerInstance = this.getCellEditor(row, column);
    return IxTableControllers.getController(handlerInstance._dataType).enableEditor(handlerInstance);
  },
  hideCell: function (row, column) {
    var handlerInstance = this.getCellEditor(row, column);
    IxTableControllers.getController(handlerInstance._dataType).hide(handlerInstance);
  },
  showCell: function (row, column) {
    var handlerInstance = this.getCellEditor(row, column);
    IxTableControllers.getController(handlerInstance._dataType).show(handlerInstance);
  },
  isCellVisible: function (row, column) {
    var handlerInstance = this.getCellEditor(row, column);
    return IxTableControllers.getController(handlerInstance._dataType).isVisible(handlerInstance);
  },
  isCellDynamicOptions: function (row, column) {
    var handlerInstance = this.getCellEditor(row, column);
    return IxTableControllers.getController(handlerInstance._dataType).isDynamicOptions(handlerInstance);
  },
  isCellDisabled: function (row, column) {
    var handlerInstance = this.getCellEditor(row, column);
    return IxTableControllers.getController(handlerInstance._dataType).isDisabled(handlerInstance);
  },
  preventCellSelection: function (row, column) {
    var handlerInstance = this.getCellEditor(row, column);
    
  },
  allowCellSelection: function (row, column) {
    
  },
  getColumnCount: function () {
    return this.options.columns.length;
  },
  disableRow: function (row) {
    for (var column = 0; column < this.options.columns.length; column++)
      this.disableCellEditor(row, column);
  },
  enableRow: function (row) {
    for (var column = 0; column < this.options.columns.length; column++)
      this.enableCellEditor(row, column);
  },
  getRowCount: function () {
    return parseInt(this._rowCount.value);
  },
  isCellEditable: function (row, column) {
    var editor = this.getCellEditor(row, column);
    return editor._editable;
  },
  setCellEditable: function (row, column, editable) {
    var editor = this.getCellEditor(row, column);
    var controller = IxTableControllers.getController(editor._dataType);
    
    if (controller.getEditable(editor) != editable) {
      controller.setEditable(editor, editable);
      this.fire("cellEditableChanged", {
        tableComponent: this,
        row: row,
        column: column,
        editable: editable
      });
    }
  },
  setCellDataType: function (row, column, dataType) {
    var editor = this.getCellEditor(row, column);
    if (editor._dataType != dataType) {
      var oldController = IxTableControllers.getController(editor._dataType);
      var newController = IxTableControllers.getController(dataType);
      
      var value = oldController.getEditorValue(editor);
      var editable = editor._editable;
      var name = editor._name;
      var columnDefinition = editor._columnDefinition;
      var column = editor._column;
      var row = editor._row;
      var cell = editor._cell;
      
      oldController.detachContentHandler(editor);
      oldController.destroyHandler(editor);
      
      if (editable) {
        var editor = newController.buildEditor(name, columnDefinition);
        newController.attachContentHandler(this, column, row, cell, editor);
        newController.setEditorValue(editor, value);
      } else {
        var viewer = newController.buildViewer(name, columnDefinition);
        newController.attachContentHandler(this, column, row, cell, viewer);
        newController.setEditorValue(viewer, value);
      }
      
      this.fire("cellDataTypeChanged", {
        tableComponent: this,
        row: row,
        column: column,
        dataType: dataType
      });
    }
  },
  getCellDataType: function (row, column) {
    var editor = this.getCellEditor(row, column);
    return editor._dataType;
  },
  setActiveRows: function (rows) {
    while (this.getActiveRows().length > 0) {
      this.removeActiveRow(this.getActiveRows()[0]);
    }
    
    for (var i = 0; i < rows.length; i++) {
      this.addActiveRow(rows[i]);
    }
  },
  getActiveRows: function () {
    return this._activeRows.keys();
  },
  isActiveRow: function (rowNumber) {
    return this._activeRows.get(rowNumber) == true;
  },
  addActiveRow: function (rowNumber) {
    this._activeRows.set(rowNumber, true);
    this.getRowElement(rowNumber).addClassName("ixActiveTableRow");
  },
  removeActiveRow: function (rowNumber) {
    this._activeRows.unset(rowNumber);
    this.getRowElement(rowNumber).removeClassName("ixActiveTableRow");
  },
  getRowElement: function (rowNumber) {
    var rowElements = this.domNode.select('.ixTableRow');
    for (var i = 0; i < rowElements.length; i++) {
      if (rowElements[i]._rowNumber == rowNumber)
        return rowElements[i];
    }
    
    return null;
  },
  _setRowCount: function (rowCount) {
    this._rowCount.value = rowCount; 
  },
  _onRowClick: function (event) {
    var row = Event.element(event);
    if (!row.hasClassName("ixTableRow"))
      row = row.up(".ixTableRow");
    if (row) {
      this.fire("rowClick", {
        tableObject: this,
        row: row._rowNumber
      });
    }
  },
  _createCellContentHandler: function (name, columnDefinition) {
    var controller = IxTableControllers.getController(columnDefinition.dataType);
    var editable = columnDefinition.editable;
    if (controller.getMode() == IxTableControllers.EDITMODE_NOT_EDITABLE)
      editable = false;
    else if (controller.getMode() == IxTableControllers.EDITMODE_ONLY_EDITABLE)
      editable = true;
    
    if (editable) {
      var editor = controller.buildEditor(name, columnDefinition);
      return editor;
    } else {
      var viewer = controller.buildViewer(name, columnDefinition);
      return viewer;
    }
  },
  _setCellContentHandler: function (row, column, handlerInstance) {
    this._cellEditors.set(row + '.' + column, handlerInstance);
  },
  _unsetCellContentHandler: function (row, column) {
    this._cellEditors.unset(row + '.' + column);
  }
});

Object.extend(IxTable.prototype, FNIEventSupport);

function getIxTableById(id) {
  return _ixTables.get(id);
};

function getIxTables() {
  return _ixTables.values();
};

IxTableEditorController = Class.create({
  buildEditor: function (name, columnDefinition) { },
  buildViewer: function (name, columnDefinition) { },
  attachContentHandler: function (table, column, row, parentNode, handlerInstance) {
    handlerInstance._column = column;
    handlerInstance._row = row;
    handlerInstance._table = table;
    handlerInstance._cell = parentNode;
    table._setCellContentHandler(row, column, handlerInstance);
    parentNode.appendChild(handlerInstance);
    
    if (handlerInstance._columnDefinition.hidden == true) 
      this.hide(handlerInstance);
    else
      this.show(handlerInstance);
    
    var selectable = handlerInstance._columnDefinition.selectable;
    if (selectable == false)
      this.setSelectable(handlerInstance, false);
    
    return handlerInstance;
  },
  detachContentHandler: function (handlerInstance) {
    handlerInstance._table._unsetCellContentHandler(handlerInstance._row, handlerInstance._column);
    handlerInstance._row = undefined;
    handlerInstance._column = undefined;
    handlerInstance._table = undefined;
    handlerInstance.parentNode.removeChild(handlerInstance);  
  },  
  destroyHandler: function (handlerInstance) { 
    handlerInstance._editable = undefined;
    handlerInstance._dataType = undefined;
    handlerInstance._name = undefined;
    handlerInstance._cell = undefined;
    handlerInstance._columnDefinition = undefined;
    
    if (handlerInstance._fieldValue) {
      handlerInstance.removeChild(handlerInstance._fieldValue);
      handlerInstance._fieldValue = undefined;
    }
    
    if (handlerInstance._fieldContent) {
      handlerInstance.removeChild(handlerInstance._fieldContent);
      handlerInstance._fieldContent = undefined;
    }
  },
  getEditable: function (handlerInstance) {
    return handlerInstance._editable;
  },
  setEditable: function (handlerInstance, editable) {
    if (handlerInstance._editable == editable)
      return;
    if ((this.getMode(handlerInstance) == IxTableControllers.EDITMODE_ONLY_EDITABLE) && (editable == false))
      return;
    if ((this.getMode(handlerInstance) == IxTableControllers.EDITMODE_NOT_EDITABLE) && (editable == true))
      return;
    
    var table = handlerInstance._table;
    var column = handlerInstance._column;
    var row = handlerInstance._row;
    var parentNode = handlerInstance._cell;
    
    this.detachContentHandler(handlerInstance);
    var newHandler = editable == true ? this.buildEditor(handlerInstance._name, handlerInstance._columnDefinition) : this.buildViewer(handlerInstance._name, handlerInstance._columnDefinition);
    this.attachContentHandler(table, column, row, parentNode, newHandler);
    this.setEditorValue(newHandler, this.getEditorValue(handlerInstance));
    this.destroyHandler(handlerInstance);
  },
  getEditorValue: function (handlerInstance) {},
  setEditorValue: function (handlerInstance, value) {},
  disableEditor: function (handlerInstance) {},
  enableEditor: function (handlerInstance) {},
  enableEditor: function (handlerInstance) {},
  isDisabled: function (handlerInstance) {},
  hide: function (handlerInstance) {
    handlerInstance._cell.hide();
  },
  show: function (handlerInstance) {
    handlerInstance._cell.show();
  },
  isVisible: function (handlerInstance) {
    return handlerInstance._cell.visible();
  },
  setSelectable: function (handlerInstance, selectable) {
    if (selectable == true) {
      handlerInstance.onselectstart = undefined;
      handlerInstance.unselectable = "off";
      handlerInstance.style.MozUserSelect = "text";
    } else {
      handlerInstance.onselectstart = function(){ return false; };
      handlerInstance.unselectable = "on";
      handlerInstance.style.MozUserSelect = "none";
    }
  },
  getMode: function () { },
  getDataType: function () { },
  _createEditorElement: function (elementName, name, className, attributes, columnDefinition) {
    var editor = new Element(elementName, Object.extend(attributes||{}, {className: "ixTableCellEditor" + (className ? ' ' + className : '')}));
    
    if (columnDefinition.editorClassNames) {
      var classNames = columnDefinition.editorClassNames.split(' ');
      for (var i = 0, l = classNames.length; i < l; i++) {
        editor.addClassName(classNames[i]);
      }
    }
    
    editor._editable = true;
    editor._dataType = this.getDataType();
    editor._name = name;
    editor._columnDefinition = columnDefinition;
    return editor;
  },
  _createViewerElement: function (elementName, name, className, attributes, columnDefinition) {
    var viewer = new Element(elementName, Object.extend(attributes||{}, {className: "ixTableCellViewer" + (className ? ' ' + className : '')}));
    
    if (columnDefinition.viewerClassNames) {
      var classNames = columnDefinition.viewerClassNames.split(' ');
      for (var i = 0, l = classNames.length; i < l; i++) {
        viewer.addClassName(classNames[i]);
      }
    }
    
    viewer._editable = false;
    viewer._dataType = this.getDataType();
    viewer._name = name;
    viewer._columnDefinition = columnDefinition;
    
    viewer._fieldValue = new Element("input", {type: "hidden", name: name});
    viewer._fieldContent = new Element("span"); 
    viewer.appendChild(viewer._fieldValue);
    viewer.appendChild(viewer._fieldContent); 
    
    return viewer;
  },
  _setViewerValue: function (viewer, value, displayValue) {
    if (value == undefined||value==null) {
      viewer._fieldValue.value = '';
      viewer._fieldContent.innerHTML = '';
    } else {
      viewer._fieldValue.value = value;
      viewer._fieldContent.innerHTML = displayValue ? displayValue : value;
    }
  },
  _getViewerValue: function (viewer) {
    return viewer._fieldValue.value;
  },
  _fireValueChange: function (handlerInstance, newValue) {
    handlerInstance._table.fire("cellValueChange", {
      tableObject: handlerInstance._table,
      fieldType: handlerInstance._dataType,
      column: handlerInstance._column,
      row: handlerInstance._row,
      value: newValue
    });
  },
  _addDisabledHiddenElement: function (handlerInstance) {
    if (handlerInstance.parentNode) {
      var value = this.getEditorValue(handlerInstance);
      if (handlerInstance._disabledHiddenElement) {
        handlerInstance._disabledHiddenElement.value = value;
      } else {
        var hiddenElement = new Element("input", {type: 'hidden', value: value, name: handlerInstance._name});
        handlerInstance._disabledHiddenElement = hiddenElement;
        handlerInstance.parentNode.appendChild(hiddenElement);
      }
    } else {
      // TODO: Onko tälläisiäkin tilanteita ????
    }
  },
  _removeDisabledHiddenElement: function (handlerInstance) {
    if (handlerInstance._disabledHiddenElement) {
      handlerInstance._disabledHiddenElement.remove();
      delete handlerInstance._disabledHiddenElement;
    }
  },
  _updateDisabledHiddenElement: function (handlerInstance) {
    if (handlerInstance._disabledHiddenElement) {
      var value = this.getEditorValue(handlerInstance);
      handlerInstance._disabledHiddenElement.value = value;
    }
  },
  isDynamicOptions: function ($super, handlerInstance) {
    return false;
  },
  _copyState: function (target, source) {
    this.setEditorValue(target, this.getEditorValue(source));
    this.setEditable(target, this.getEditable(source));
    // TODO: disabled, datatype yms tiedot
  }
});

Object.extend(IxTableEditorController.prototype, FNIEventSupport);

IxNumberTableEditorController = Class.create(IxTableEditorController, {
  buildEditor: function ($super, name, columnDefinition) {
    var editor = this._createEditorElement("input", name, "ixTableCellEditorNumber", {type: "text", name: name}, columnDefinition);
    this._editorValueChangeListener = this._onEditorValueChange.bindAsEventListener(this);
    Event.observe(editor, "change", this._editorValueChangeListener);
    return editor;
  },
  buildViewer: function ($super, name, columnDefinition) {
    return this._createViewerElement("div", name, "ixTableCellViewerNumber", {}, columnDefinition);
  },
  disableEditor: function ($super, handlerInstance) {
    if (handlerInstance._editable == false)
      handlerInstance.addClassName("ixTableCellViewerDisabled");
    else {
      this._addDisabledHiddenElement(handlerInstance);
      handlerInstance.disabled = true;
    }
  },
  enableEditor: function ($super, handlerInstance) {
    if (handlerInstance._editable == false)
      handlerInstance.removeClassName("ixTableCellViewerDisabled");
    else {
      handlerInstance.disabled = false;
      this._removeDisabledHiddenElement(handlerInstance);
    }
  },
  getEditorValue: function ($super, handlerInstance) {
    if (handlerInstance._editable != true) 
      return this._getViewerValue(handlerInstance);
    else
      return handlerInstance.value;
  },
  setEditorValue: function ($super, handlerInstance, value) {
    if (handlerInstance._editable != true) 
      this._setViewerValue(handlerInstance, value);
    else {
      if (this.isDisabled(handlerInstance))
        this._updateDisabledHiddenElement(handlerInstance, value);
      
      handlerInstance.value = value;
    }
  },
  destroyEditor: function ($super, handlerInstance) {
    handlerInstance.remove();
  },
  isDisabled: function ($super, handlerInstance) {
    return handlerInstance.disabled;
  },
  getDataType: function () {
    return "number";  
  },
  getMode: function () { 
    return IxTableControllers.EDITMODE_EDITABLE;
  },
  _onEditorValueChange: function (event) {
    var handlerInstance = Event.element(event);
    this._fireValueChange(handlerInstance, handlerInstance.value);
  }
});

IxTableControllers.registerController(new IxNumberTableEditorController());

IxHiddenTableEditorController = Class.create(IxTableEditorController, {
  buildEditor: function ($super, name, columnDefinition) {
    // Event.observe(cellEditor, "change", this._fieldValueChangeListener);
    return this._createEditorElement("input", name, undefined, {type: "hidden", name: name}, columnDefinition);
  },
  getEditorValue: function ($super, handlerInstance) {
    return handlerInstance.value;
  },
  setEditorValue: function ($super, handlerInstance, value) {
    handlerInstance.value = value;
  },
  destroyEditor: function ($super, handlerInstance) {
    handlerInstance.remove();
  },
  getDataType: function ($super) {
    return "hidden";  
  },
  isDisabled: function ($super, handlerInstance) {
    return handlerInstance.disabled;
  },
  getMode: function () { 
    return IxTableControllers.EDITMODE_ONLY_EDITABLE;
  }
});

IxTableControllers.registerController(new IxHiddenTableEditorController());

IxSelectTableEditorController = Class.create(IxTableEditorController, {
  buildEditor: function ($super, name, columnDefinition) {
    var cellEditor = this._createEditorElement("select", name, "ixTableCellEditorSelect", {name: name}, columnDefinition);
    
    this._editorValueChangeListener = this._onEditorValueChange.bindAsEventListener(this);
    Event.observe(cellEditor, "change", this._editorValueChangeListener);
    
    cellEditor._dynamicOptions = columnDefinition.dynamicOptions || false;
    
    if (columnDefinition.options) {
      var options = columnDefinition.options;

      for (var j = 0, l = options.length; j < l; j++) {
        var option = options[j];
        this.addOption(cellEditor, option.value, option.text);
      }
    }
    
    return cellEditor;
  },
  removeAllOptions: function (handlerInstance) {
    if (handlerInstance._editable) {
      for (var i = handlerInstance.options.length - 1; i >= 0; i--) {
        $(handlerInstance.options[i]).remove();
      }  
    }
  },
  addOption: function (handlerInstance, value, text) {
    this.addOption(handlerInstance, value, text, false);
  },
  addOption: function (handlerInstance, value, text, selected) {
    if (handlerInstance._editable) {
      var optionNode;
      if (!selected)
        optionNode = new Element("option", {value: value})
      else
        optionNode = new Element("option", {value: value, selected: "selected"});
      if (text)
        optionNode.update(text);
      
      handlerInstance.appendChild(optionNode);
    }
  },
  buildViewer: function ($super, name, columnDefinition) {
    return this._createViewerElement("div", name, "ixTableCellViewerSelect", {}, columnDefinition);
  },
  disableEditor: function ($super, handlerInstance) {
    if (handlerInstance._editable != true)
      handlerInstance.addClassName("ixTableCellViewerDisabled");
    else {
      this._addDisabledHiddenElement(handlerInstance);
      handlerInstance.disabled = true;
    }
  },
  enableEditor: function ($super, handlerInstance) {
    if (handlerInstance._editable != true)
      handlerInstance.removeClassName("ixTableCellViewerDisabled");
    else {
      handlerInstance.disabled = false;
      this._removeDisabledHiddenElement(handlerInstance);
    }
  },
  getEditorValue: function ($super, handlerInstance) {
    if (handlerInstance._editable != true) 
      return this._getViewerValue(handlerInstance);
    else
      return handlerInstance.value;
  },
  setEditorValue: function ($super, handlerInstance, value) {
    if (handlerInstance._editable != true) {
      var displayValue = value;
      var options = handlerInstance._columnDefinition.options;
      for (var i = 0; i < options.length; i++) {
        if (options[i].value == value) {
          displayValue = options[i].text;
          break;
        }
      }
      this._setViewerValue(handlerInstance, value, displayValue);
    } else {
      if (this.isDisabled(handlerInstance))
        this._updateDisabledHiddenElement(handlerInstance, value);
      
      handlerInstance.value = value;
    }
  },
  destroyEditor: function ($super, handlerInstance) {
    handlerInstance.remove();
  },
  isDisabled: function ($super, handlerInstance) {
    return handlerInstance.disabled;
  },
  isDynamicOptions: function ($super, handlerInstance) {
    return handlerInstance._dynamicOptions;
  },
  getDataType: function ($super) {
    return "select";  
  },
  getMode: function ($super) { 
    return IxTableControllers.EDITMODE_EDITABLE;
  },
  _onEditorValueChange: function (event) {
    var handlerInstance = Event.element(event);
    this._fireValueChange(handlerInstance, handlerInstance.value);
  },
  _copyState: function ($super, target, source) {
    $super(target, source);
    
    if (source._dynamicOptions && target._dynamicOptions) {
      this.removeAllOptions(target);

      for (var i = 0; i < source.options.length; i++) {
        var option = source.options[i];
        this.addOption(target, option.value, option.innerHTML);
      }
    }
  }  
});

IxTableControllers.registerController(new IxSelectTableEditorController());

IxCheckboxTableEditorController = Class.create(IxTableEditorController, {
  buildEditor: function ($super, name, columnDefinition) {
    var cellEditor = this._createEditorElement("input", name, "ixTableCellEditorCheckbox", {name: name, value: "1", type: "checkbox"}, columnDefinition);
    this._editorValueChangeListener = this._onEditorValueChange.bindAsEventListener(this);
    Event.observe(cellEditor, "change", this._editorValueChangeListener);
    return cellEditor;
  },
  buildViewer: function ($super, name, columnDefinition) {
    return this._createViewerElement("div", name, "ixTableCellViewerCheckbox", {}, columnDefinition);
  },
  disableEditor: function ($super, handlerInstance) {
    if (handlerInstance._editable != true)
      handlerInstance.addClassName("ixTableCellViewerDisabled");
    else {
      this._addDisabledHiddenElement(handlerInstance);
      handlerInstance.disabled = true;
    }
  },
  enableEditor: function ($super, handlerInstance) {
    if (handlerInstance._editable != true)
      handlerInstance.removeClassName("ixTableCellViewerDisabled");
    else {
      handlerInstance.disabled = false;
      this._removeDisabledHiddenElement(handlerInstance);
    }
  },
  getEditorValue: function ($super, handlerInstance) {
    if (handlerInstance._editable != true)
      return this._getViewerValue(handlerInstance) == 1;
    else
      return handlerInstance.checked;
  },
  setEditorValue: function ($super, handlerInstance, value) {
    var isChecked;
    if (Object.isString(value))
      isChecked = value == '1';
    else if (Object.isNumber(value))
      isChecked = value == 1;
    else 
      isChecked = value;
    
    if (handlerInstance._editable != true)
      this._setViewerValue(handlerInstance, isChecked ? 1 : 0, '<div class="ixTableViewerCheckbox' + (isChecked ? 'Checked' : 'NotChecked') + '"/>');
    else {
      if (this.isDisabled(handlerInstance))
        this._updateDisabledHiddenElement(handlerInstance, isChecked ? 1 : 0);
      
      handlerInstance.checked = isChecked;
    }
  },
  destroyEditor: function ($super, handlerInstance) {
    handlerInstance.remove();
  },
  isDisabled: function ($super, handlerInstance) {
    return handlerInstance.disabled;
  },
  getDataType: function ($super) {
    return "checkbox";  
  },
  getMode: function ($super) { 
    return IxTableControllers.EDITMODE_EDITABLE;
  },
  _onEditorValueChange: function (event) {
    var handlerInstance = Event.element(event);
    this._fireValueChange(handlerInstance, handlerInstance.value);
  }
});

IxTableControllers.registerController(new IxCheckboxTableEditorController());

IxRadioButtonTableEditorController = Class.create(IxTableEditorController, {
  buildEditor: function ($super, name, columnDefinition) {
    var cellEditor = this._createEditorElement("input", name, "ixTableCellEditorCheckbox", {name: name, value: 'true', type: "radio", title: columnDefinition.tooltip ? columnDefinition.tooltip : ''}, columnDefinition);
    this._editorValueChangeListener = this._onEditorValueChange.bindAsEventListener(this);
    Event.observe(cellEditor, "change", this._editorValueChangeListener);
    return cellEditor;
  },
  buildViewer: function ($super, name, columnDefinition) {
    return this._createViewerElement("div", name, "ixTableCellViewerCheckbox", {}, columnDefinition);
  },
  disableEditor: function ($super, handlerInstance) {
    if (handlerInstance._editable != true)
      handlerInstance.addClassName("ixTableCellViewerDisabled");
    else {
      this._addDisabledHiddenElement(handlerInstance);
      handlerInstance.disabled = true;
    }
  },
  enableEditor: function ($super, handlerInstance) {
    if (handlerInstance._editable != true)
      handlerInstance.removeClassName("ixTableCellViewerDisabled");
    else {
      handlerInstance.disabled = false;
      this._removeDisabledHiddenElement(handlerInstance);
    }
  },
  getEditorValue: function ($super, handlerInstance) {
    if (handlerInstance._editable != true)
      return this._getViewerValue(handlerInstance) == 1;
    else
      return handlerInstance.checked;
  },
  setEditorValue: function ($super, handlerInstance, value) {
    var isChecked;
    if (Object.isString(value))
      isChecked = value == '1';
    else if (Object.isNumber(value))
      isChecked = value == 1;
    else 
      isChecked = value;
    
    if (handlerInstance._editable != true)
      this._setViewerValue(handlerInstance, isChecked ? 1 : 0, '<div class="ixTableViewerCheckbox' + (isChecked ? 'Checked' : 'NotChecked') + '"/>');
    else {
      if (this.isDisabled(handlerInstance))
        this._updateDisabledHiddenElement(handlerInstance, isChecked ? 1 : 0);
      
      handlerInstance.checked = isChecked;
    }
  },
  destroyEditor: function ($super, handlerInstance) {
    handlerInstance.remove();
  },
  isDisabled: function ($super, handlerInstance) {
    return handlerInstance.disabled;
  },
  getDataType: function ($super) {
    return "radiobutton";  
  },
  getMode: function ($super) { 
    return IxTableControllers.EDITMODE_EDITABLE;
  },
  _onEditorValueChange: function (event) {
    var handlerInstance = Event.element(event);
    var row = handlerInstance._row;
    var column = handlerInstance._column;
    
    if (this.getEditorValue(handlerInstance)) {
      var tableComponent = handlerInstance._table;
      var rows = tableComponent.getRowCount();
      for (var i = 0; i < rows; i++) {
        if (i != row) {
          if ((tableComponent.getCellDataType(i, column) == 'radiobutton') && (tableComponent.isCellEditable(i, column))) {
            tableComponent.setCellValue(i, column, false);
          }
        }
      }
    }
    
    this._fireValueChange(handlerInstance, handlerInstance.value);
  }/**,
  _onCellValueChanged: function (event) {
    var tableComponent = event.tableComponent;
    var handlerInstance = tableComponent.getCellEditor(event.row, event.column);
    if (this._isChecked(event.value)) {
      for (var i = 0; i < tableComponent.getRowCount(); i++) {
        if (i != event.row) {
          tableComponent.setCellValue(i, column, false);
        }
      }
    }
  },
  _isChecked: function(value) {
    if (Object.isString(value)) {
      return value == '1';
    }
    else if (Object.isNumber(value)) {
      return value == 1;
    }
    else {
      return value == true;
    }
  }**/
});

IxTableControllers.registerController(new IxRadioButtonTableEditorController());

IxDateTableEditorController = Class.create(IxTableEditorController, {
  buildEditor: function ($super, name, columnDefinition) {
    var cellEditor = this._createEditorElement("input", name, "ixTableCellEditorDate", {name: name, type: "text"}, columnDefinition);
    
    /***
     * TODO: Change listener !!!
     ***/
    
    // Event.observe(cellEditor, "change", this._fieldValueChangeListener);
    return cellEditor;
  },
  buildViewer: function ($super, name, columnDefinition) {
    return this._createViewerElement("div", name, "ixTableCellViewerDate", {}, columnDefinition);
  },
  disableEditor: function ($super, handlerInstance) {
    if (handlerInstance._editable == false)
      handlerInstance.addClassName("ixTableCellViewerDisabled");
    else
      handlerInstance._component.disable();
  },
  enableEditor: function ($super, handlerInstance) {
    if (handlerInstance._editable == false)
      handlerInstance.removeClassName("ixTableCellViewerDisabled");
    else
      handlerInstance._component.enable();
  },
  getEditorValue: function ($super, handlerInstance) {
    if (handlerInstance._editable != true)
      return this._getViewerValue(handlerInstance);
    else
      return handlerInstance._component.getTimestamp();  
  },
  setEditorValue: function ($super, handlerInstance, value) {
    if (handlerInstance._editable != true) {
      if (value) {
        var date = new Date();
        date.setTime(value);
        // TODO: move dateformatting to conf ...
        this._setViewerValue(handlerInstance, value, date.getDate().toPaddedString(2) + '.' + (date.getMonth() + 1).toPaddedString(2) + '.' + date.getFullYear());
      }
      else {
        this._setViewerValue(handlerInstance, value, '');
      }
    } else {
      if (handlerInstance._component)
        handlerInstance._component.setTimestamp(value);
      else {
        handlerInstance._pendingValue = value;
      }
    }
  },
  attachContentHandler: function ($super, table, column, row, parentNode, handlerInstance) {
    var handlerInstance = $super(table, column, row, parentNode, handlerInstance);
    
    if (handlerInstance._editable == true) {
      handlerInstance._cell = parentNode;
      var _this = this;
      
      var row = handlerInstance.up('.ixTableRow');
      if (!row) {
        handlerInstance._onRowAdd = function (event) {
          if (this._row == event.row) {
            event.tableObject.removeListener("rowAdd", this);
            this._onRowAdd = undefined;
            this._component = replaceDateField(this);
            if (this._pendingValue) {
              _this.setEditorValue(this, this._pendingValue);
              this._pendingValue = undefined;
            }
          } 
        };

        table.addListener("rowAdd", handlerInstance, handlerInstance._onRowAdd);
      } else {
        handlerInstance._component = replaceDateField(handlerInstance);
      }
    }
  },
  detachContentHandler: function ($super, handlerInstance) {
    if (handlerInstance._editable == true) {
      handlerInstance._table._unsetCellContentHandler(handlerInstance._row, handlerInstance._column);
      handlerInstance._row = undefined;
      handlerInstance._column = undefined;
      handlerInstance._table = undefined;
      handlerInstance._component.destroy();
    } else {
      $super(handlerInstance);
    }
  }, 
  isDisabled: function ($super, handlerInstance) {
    // TODO: 
    // return handlerInstance.disabled;
  },
  getDataType: function ($super) {
    return "date";  
  },
  getMode: function ($super) { 
    return IxTableControllers.EDITMODE_EDITABLE;
  }
});

IxTableControllers.registerController(new IxDateTableEditorController());

IxButtonTableEditorButtonController = Class.create(IxTableEditorController, {
  buildViewer: function ($super, name, columnDefinition) {
    if (columnDefinition.imgsrc) {  
      var cellViewer = new Element("img", { src: columnDefinition.imgsrc, title: columnDefinition.tooltip ? columnDefinition.tooltip : '', className: "ixTableCellViewer ixTableCellEditorButton"});
      
      if (columnDefinition.viewerClassNames) {
        var classNames = columnDefinition.viewerClassNames.split(' ');
        for (var i = 0, l = classNames.length; i < l; i++) {
          cellViewer.addClassName(classNames[i]);
        }
      }
      
      cellViewer._editable = false;
      cellViewer._dataType = this.getDataType();
      cellViewer._name = name;
      cellViewer._columnDefinition = columnDefinition;
      
      return cellViewer;
    } else {
      throw new Error("Unable to build button without image");
    }
  },
  destroyHandler: function ($super, handlerInstance) { 
    $super(handlerInstance);
  },
  attachContentHandler: function ($super, table, column, row, parentNode, handlerInstance) {
    var handlerInstance = $super(table, column, row, parentNode, handlerInstance);
    handlerInstance._clickListener = this._onClick.bindAsEventListener(this);
    Event.observe(handlerInstance, "click", handlerInstance._clickListener); 
  },
  detachContentHandler: function ($super, handlerInstance) {
    Event.stopObserving(handlerInstance, "click", handlerInstance._clickListener);
    handlerInstance._clickListener = undefined;
    $super(handlerInstance);
  }, 
  disableEditor: function ($super, handlerInstance) {
    handlerInstance._disabled = true;
    handlerInstance.addClassName("ixTableButtonDisabled");
  },
  enableEditor: function ($super, handlerInstance) {
    handlerInstance._disabled = false;
    handlerInstance.removeClassName("ixTableButtonDisabled");
  },
  destroyEditor: function ($super, handlerInstance) {
    handlerInstance.remove();
  },
  isDisabled: function ($super, handlerInstance) {
    return handlerInstance._disabled;
  },
  getDataType: function ($super) {
    return "button";  
  },
  getMode: function ($super) { 
    return IxTableControllers.EDITMODE_NOT_EDITABLE;
  },
  _onClick: function (event) {
    var handlerInstance = Event.element(event);
    if (handlerInstance._columnDefinition.onclick) {
      if (this.isDisabled(handlerInstance) != true) { 
        handlerInstance._columnDefinition.onclick.call(window, {
          tableObject: handlerInstance._table,
          row: handlerInstance._row,
          column: handlerInstance._column                
        });
      }
    }
  }
});

IxTableControllers.registerController(new IxButtonTableEditorButtonController());

IxTextTableEditorController = Class.create(IxTableEditorController, {
  buildEditor: function ($super, name, columnDefinition) {
    var editor = this._createEditorElement("input", name, "ixTableCellEditorText", {name: name, type: "text"}, columnDefinition);
    this._editorValueChangeListener = this._onEditorValueChange.bindAsEventListener(this);
    Event.observe(editor, "change", this._editorValueChangeListener);
    return editor;
  },
  buildViewer: function ($super, name, columnDefinition) {
    return this._createViewerElement("div", name, "ixTableCellViewerText", {}, columnDefinition);
  },
  attachContentHandler: function ($super, table, column, row, parentNode, handlerInstance) {
    var handlerInstance = $super(table, column, row, parentNode, handlerInstance);
    handlerInstance._clickListener = this._onClick.bindAsEventListener(this);
    Event.observe(handlerInstance, "click", handlerInstance._clickListener); 
  },
  detachContentHandler: function ($super, handlerInstance) {
    Event.stopObserving(handlerInstance, "click", handlerInstance._clickListener);
    handlerInstance._clickListener = undefined;
    $super(handlerInstance);
  }, 
  disableEditor: function ($super, handlerInstance) {
    if (handlerInstance._editable == false)
      handlerInstance.addClassName("ixTableCellViewerDisabled");
    else {
      this._addDisabledHiddenElement(handlerInstance);
    }
    handlerInstance.disabled = true;
  },
  enableEditor: function ($super, handlerInstance) {
    if (handlerInstance._editable != true)
      handlerInstance.removeClassName("ixTableCellViewerDisabled");
    else {
      this._removeDisabledHiddenElement(handlerInstance);
    }
    handlerInstance.disabled = false;
  },
  getEditorValue: function ($super, handlerInstance) {
    if (handlerInstance._editable != true) 
      return this._getViewerValue(handlerInstance);
    else
      return handlerInstance.value;
  },
  setEditorValue: function ($super, handlerInstance, value) {
    if (handlerInstance._editable != true) {
      this._setViewerValue(handlerInstance, value);
    } else {
      if (this.isDisabled(handlerInstance))
        this._updateDisabledHiddenElement(handlerInstance, value);
      
      handlerInstance.value = value;
    }
  },
  destroyEditor: function ($super, handlerInstance) {
    handlerInstance.remove();
  },
  isDisabled: function ($super, handlerInstance) {
    return handlerInstance.disabled;
  },
  getDataType: function ($super) {
    return "text";  
  },
  getMode: function ($super) { 
    return IxTableControllers.EDITMODE_EDITABLE;
  },
  _onClick: function (event) {
    var handlerInstance = Event.element(event);
    
    if (!handlerInstance.hasClassName("ixTableCellEditorText") && !handlerInstance.hasClassName("ixTableCellViewerText")) {
      var e = handlerInstance.up(".ixTableCellViewerText");
      if (e)
        handlerInstance = e;
      else {
        var e = handlerInstance.up(".ixTableCellEditorText");
        if (e)
          handlerInstance = e;
      }
    }
    
    if (handlerInstance._columnDefinition.onclick) {
      if (this.isDisabled(handlerInstance) != true) { 
        handlerInstance._columnDefinition.onclick.call(window, {
          tableObject: handlerInstance._table,
          row: handlerInstance._row,
          column: handlerInstance._column                
        });
      }
    }
  },
  _onEditorValueChange: function (event) {
    var handlerInstance = Event.element(event);
    this._fireValueChange(handlerInstance, handlerInstance.value);
  }
});

IxTableControllers.registerController(new IxTextTableEditorController());