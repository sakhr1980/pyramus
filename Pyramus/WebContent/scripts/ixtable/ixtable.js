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
    this._filters = new Array();
  
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
    
    var classNames = "ixTable";
    
    if (options.rowHoverEffect === true)
      classNames += " ixTableRowHoverEffect"
    
    this.domNode = Builder.node("div", {
      className : classNames
    }, [ this._headerRow, this._content, this._rowCount]);

    this._headerCells = new Object();
    this._cellEditors = new Hash();
    parentNode.appendChild(this.domNode);
    
    this._hasHeader = false;
    
    this.options = options;
    for (var i = 0; i < options.columns.length; i++) {
      var column = options.columns[i];
      
      this._hasHeader = this._hasHeader || !((column.header == '') || (!column.header));
      
      var headerTextNode = Builder.node("div", {
        className : "ixTableHeaderCellText"
      }, [ column.header ]);
      
      var headerCell = Builder.node("div", {
        className : "ixTableHeaderCell"
      }, [ headerTextNode ]);
      
      if (column.sortAttributes) {
        this._sortColumnClickListener = this._onSortColumnClick.bindAsEventListener(this);

        if (column.sortAttributes.sortAscending) {
          var sortAscendingBtn = new Element("span", { className : "ixTableHeaderSortButton", title: column.sortAttributes.sortAscending.toolTip });
          sortAscendingBtn.innerHTML = "▲";
          sortAscendingBtn._sort = column.sortAttributes.sortAscending; 
          sortAscendingBtn._column = i; 
          headerCell.appendChild(sortAscendingBtn);

          Event.observe(sortAscendingBtn, "click", this._sortColumnClickListener);
        }
        if (column.sortAttributes.sortDescending) {
          var sortDescendingBtn = new Element("span", { className : "ixTableHeaderSortButton", title: column.sortAttributes.sortDescending.toolTip });
          sortDescendingBtn.innerHTML = "▼";
          sortDescendingBtn._sort = column.sortAttributes.sortDescending; 
          sortDescendingBtn._column = i; 
          headerCell.appendChild(sortDescendingBtn);

          Event.observe(sortDescendingBtn, "click", this._sortColumnClickListener);
        }
      }
      
      if (column.overwriteColumnValues) {
        var copyNode = Builder.node("div", {
          className : "ixTableHeaderColumValueButton",
          title: getLocale().getText('generic.ixTable.overwriteColumnValues.tooltip')
        }, []);
        var _this = this;
        Event.observe(copyNode, "click", function (event) {
          var columnNode = Event.element(event).parentNode;
          var index = 0;
          while (columnNode = columnNode.previousSibling) {
            ++index;
          }

          for (var rowIndex = 1, rowCount = _this.getRowCount(); rowIndex < rowCount; rowIndex++)
            _this.copyCellValue(index, 0, rowIndex);
        });
        headerCell.appendChild(copyNode);
      }

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

    this._contextMenuButtonClickListener = this._onContextMenuButtonClick.bindAsEventListener(this);
    this._contextMenuItemClickListener = this._onContextMenuItemClick.bindAsEventListener(this);
    this._contextMenuMouseLeaveListener = this._onContextMenuMouseLeave.bindAsEventListener(this);
  },
  addRow : function(values, editable) {
    return this.addRows([values], editable);
  },
  addRows: function (rowDatas, editable) {
    var rowNumber = this.getRowCount() - 1;
    var rowElements = new Array(); 
    
    for (var rowIndex = 0, rowCount = rowDatas.length; rowIndex < rowCount; rowIndex++) {
      rowNumber++;
      
      var values = rowDatas[rowIndex];
      
      if (values.length != this.options.columns.length) {
        throw new Error("Value array length (" + values.length + ") != table column length (" + this.options.columns.length + ")");
      }
      
      var rowContent = new Element("div", { className : "ixTableRowContent" });
      var row = new Element("div", { className : "ixTableRow" });
      row._rowNumber = rowNumber;
      row.appendChild(rowContent);
      
      if (this.options.rowClasses) {
        for (var i = 0, l = this.options.rowClasses.length; i < l; i++) {
          row.addClassName(this.options.rowClasses[i]);
        }
      }

      for (var i = 0; i < this.options.columns.length; i++) {
        var column = this.options.columns[i];
        var name = this.options.id ? this.options.id + '.' + rowNumber + '.' + (column.paramName ? column.paramName : i) : '';
        
        var cell = new Element("div", { className : "ixTableCell" });
        cell._column = i;
        
        var cellStyles = {};
        var hasStyles = false;

        if (column.contextMenu) {
          var contextMenuButton = new Element("span", {className: "ixTableCellContextMenuButton"});
          contextMenuButton.innerHTML = "▼";
          cell.appendChild(contextMenuButton);
          
          Event.observe(contextMenuButton, "click", this._contextMenuButtonClickListener);
        }
        
        if ((column.left != undefined) && (column.left != NaN)) {
          cellStyles.left = column.left + 'px';
          hasStyles = true;
        }
        
        if ((column.right != undefined) && (column.right != NaN)) {
          cellStyles.right = column.right + 'px';
          hasStyles = true;
        }
        
        if ((column.width != undefined) && (column.width != NaN)) {
          cellStyles.width = column.width + 'px';
          hasStyles = true;
        }
        
        if (hasStyles)
          cell.setStyle(cellStyles);
        
        var cellContentHandler = this._createCellContentHandler(name, column, editable); 
        rowContent.appendChild(cell);
        IxTableControllers.getController(column.dataType).attachContentHandler(this, cell, cellContentHandler);
        
        this.setCellValue(rowNumber, i, values[i]);

        if (this._hasHeader == true) {
          this._headerRow.setStyle({
            display: ''
          });
        }
      }    
      
      if (this.options.removeRowBtns == true) {
        var delRowButton = new Element("div", {className: "ixTableDelRowButton ixTableRowButton"});
        rowContent.appendChild(delRowButton);
        
        var _this = this;
        Event.observe(delRowButton, "click", function (event) {
          _this.deleteRow(row._rowNumber);
        });
      }
      
      this._setRowCount(this.getRowCount() + 1);
      
      Event.observe(row, "click", this._rowClickListener);
      
      rowElements.push(row);
    }
    
    for (var i = 0, l = rowElements.length; i < l; i++) {
      this._content.appendChild(rowElements[i]);
      
      this.fire("rowAdd", {
        tableObject: this,
        row: rowElements[i]._rowNumber
      });
    }
    
    return rowNumber;
  },
  deleteRow: function (rowNumber) {
    this._deleteRow(rowNumber);
    this._redoFilters();
    this._redoSort();
  },
  hideRow: function (rowNumber) {
    this.getRowElement(rowNumber).hide();

    if (this.getVisibleRowCount() == 0 && this._hasHeader == true) {
      this._headerRow.setStyle({
        display: 'none'
      });
    }
  },
  hideRows: function (rowNumbers) {
    for (var i = 0, len = rowNumbers.length; i < len; i++) {
      this.getRowElement(rowNumbers[i]).hide();
    }

    if (this.getVisibleRowCount() == 0 && this._hasHeader == true) {
      this._headerRow.setStyle({
        display: 'none'
      });
    }
  },
  showRow: function (rowNumber) {
    this.getRowElement(rowNumber).show();

    this._headerRow.setStyle({
      display: ''
    });
  },
  showAllRows: function () {
    for (var i = 0, len = this.getRowCount(); i < len; i++) {
      var rowElement = this.getRowElement(i); 
      if (!rowElement.visible())
        rowElement.show();
    }

    if (this.getVisibleRowCount() > 0 && this._hasHeader == true) {
      this._headerRow.setStyle({
        display: ''
      });
    }
  },
  isRowVisible: function (rowNumber) {
    return this.getRowElement(rowNumber).visible();
  },
  getVisibleRowCount: function () {
    var result = 0;
    
    for (var i = 0, l = this.getRowCount(); i < l; i++) {
      if (this.isRowVisible(i))
        result++;
    }
    
    return result;
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
      this._deleteRow(this.getRowCount() - 1);
    this._clearFilters();
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
    this.fire("cellValueChange", {
      tableComponent: this,
      row: row,
      column: column, 
      value: value
    });
  },
  copyCellValue: function(column, fromRow, toRow) {
    var fromInstance = this.getCellEditor(fromRow, column);
    var toInstance = this.getCellEditor(toRow, column);
    
    IxTableControllers.getController(toInstance._dataType).copyCellValue(toInstance, fromInstance);
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
      var column = this._getCellEditorColumn(editor);
      var row = this._getCellEditorRow(editor);
      var cell = editor._cell;
      
      oldController.detachContentHandler(editor);
      oldController.destroyHandler(editor);
      
      if (editable) {
        var editor = newController.buildEditor(name, columnDefinition);
        newController.attachContentHandler(this, cell, editor);
        newController.setEditorValue(editor, value);
      } else {
        var viewer = newController.buildViewer(name, columnDefinition);
        newController.attachContentHandler(this, cell, viewer);
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
  focusCell: function (row, column) {
    var editor = this.getCellEditor(row, column);
    var controller = IxTableControllers.getController(editor._dataType);
    controller.focus(editor);
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
  _addFilter: function (filter) {
    this._filters.push(filter);
    filter.execute({ 
      tableComponent: this 
    });
  },
  _redoFilters: function () {
    this.showAllRows();
    var filters = this._filters.toArray();
    for (var i = 0, len = filters.length; i < len; i++) {
      var filter = filters[i];
      
      filter.execute({ 
        tableComponent: this 
      });
    }
  },
  _clearFilters: function () {
    this._filters.clear();
    this.showAllRows();
  },
  _setSortMethod: function (sortMethod) {
    this._sortMethod = sortMethod;
    this._redoSort();
  },
  _redoSort: function () {
    if (this._sortMethod) {
      this._sortMethod.execute({ 
        tableComponent: this 
      });
    }
  },
  _deleteRow: function (rowNumber) {
    this.fire("beforeRowDelete", {
      tableObject: this,
      row: rowNumber
    });
    
    for (var row = rowNumber; row < (this.getRowCount() - 1); row++) {
      for (var column = 0; column < this.options.columns.length; column++) {
        var cellEditor = this.getCellEditor(row, column);
        var nextCellEditor = this.getCellEditor(row + 1, column); 
        IxTableControllers.getController(cellEditor._dataType)._copyState(cellEditor, nextCellEditor);
      }
    }
    
    this._setRowCount(this.getRowCount() - 1);
    var rowElements = this.domNode.select('.ixTableRow');
    for (var i = this.getRowCount(); i >= 0; i--) {
      if (rowElements[i]._rowNumber == this.getRowCount()) {

        for (var j = 0, len = this.options.columns.length; j < len; j++) {
          if (this.options.columns.contextMenu) {
            var editorInstance = this.getCellEditor(i, j);
            var contextMenuButton = $(editorInstance.parentNode).down(".ixTableCellContextMenuButton");
            Event.stopObserving(contextMenuButton, "click", this._contextMenuButtonClickListener);
          }
        }
      
        rowElements[i].remove();
      }
    }
    
    this.fire("rowDelete", {
      tableObject: this, 
      row: rowNumber
    });

    if (this.getVisibleRowCount() == 0 && this._hasHeader == true) {
      this._headerRow.setStyle({
        display: 'none'
      });
    }
  },
  _getCellEditorRow: function (editorInstance) {
    return this._getCellRow($(editorInstance.parentNode));
  },
  _getCellEditorColumn: function (editorInstance) {
    return this._getCellColumn($(editorInstance.parentNode));
  },
  _getCellRow: function (cell) {
    var rowContent = $(cell.parentNode);
    if (rowContent) {
      var rowElement = $(rowContent.parentNode);
      if (rowElement)
        return rowElement._rowNumber;
    }
    
    return -1;
  },
  _getCellColumn: function (cell) {
    return cell._column;
  },  
  changeTableId: function (newId) {
    var oldId = this.options.id;
    
    if (this.fire("tableIdChange", { oldId: oldId, newId: newId})) {
      // Update options id
      this.options.id = newId;
      // Update global hash
      _ixTables.unset(oldId);
      _ixTables.set(newId, this);
      // Update rowCount element
      this._rowCount.name = newId + '.rowCount';
      // Update ix:tableid attribute
      this.domNode.setAttribute("ix:tableid", newId);
      // Update field ids
      for (var i = 0; i < this.options.columns.length; i++) {
        var column = this.options.columns[i];
        for (var row = 0; row < this.getRowCount(); row++) {
          var cellEditor = this.getCellEditor(row, i);
          var name = newId ? newId + '.' + row + '.' + (column.paramName ? column.paramName : i) : '';
          IxTableControllers.getController(cellEditor._dataType).changeParamName(cellEditor, name);
        }
      }
    };
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
  _createCellContentHandler: function (name, columnDefinition, editable) {
    var controller = IxTableControllers.getController(columnDefinition.dataType);
    
    var cellEditable = editable||columnDefinition.editable;
    
    if (controller.getMode() == IxTableControllers.EDITMODE_NOT_EDITABLE)
      cellEditable = false;
    else if (controller.getMode() == IxTableControllers.EDITMODE_ONLY_EDITABLE)
      cellEditable = true;
    
    if (cellEditable) {
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
  },
  _onSortColumnClick: function (event) {
    var sortButton = Event.element(event);
    if (sortButton._sort) {
      var column = sortButton._column;
      sortButton._sort.sortAction.execute.call(window, {
        tableComponent: this,
        sortAction: sortButton._sort,
        column: column
      });
    }
  },
  _onContextMenuButtonClick: function (event) {
    var contextMenuButton = Event.element(event);
    var cell = $(contextMenuButton.parentNode);
    var row = this._getCellRow(cell);
    var column = this._getCellColumn(cell);
    
    var columnOptions = this.options.columns[column];
    if (columnOptions && columnOptions.contextMenu) {
      var menuContainer = new Element("div", {className: "ixTableCellContextMenu"} );
      
      for (var i = 0, l = columnOptions.contextMenu.length; i < l; i++) {
        var menuItem = columnOptions.contextMenu[i];
        var menuElement = new Element("div", {className: "ixTableCellContextMenuItem"} );
        menuElement._menuItem = menuItem;
        
        menuElement.innerHTML = menuItem.text;
        var _this = this;
        Event.observe(menuElement, "click", this._contextMenuItemClickListener);
        menuContainer.appendChild(menuElement);
        
        // TODO: Ota menuitem listener puis
      }
      Event.observe(menuContainer, "mouseleave", this._contextMenuMouseLeaveListener);
      
      cell.appendChild(menuContainer);
    }
  },
  _onContextMenuItemClick: function (event) {
    var menuElement = Event.element(event);
    var contextMenu = menuElement.parentNode;
    
    Event.stopObserving(contextMenu, "mouseleave", this._contextMenuMouseLeaveListener);
    Event.stopObserving(menuElement, "click", this._contextMenuItemClickListener);
    
    var menuItem = menuElement._menuItem;
    var cell = $(contextMenu.parentNode);
    var row = this._getCellRow(cell);
    var column = this._getCellColumn(cell);

    contextMenu.remove();
    
    menuItem.onclick.execute({
      tableComponent: this,
      row: row,
      column: column,
      menuItem: menuItem
    });
//    menuItem.onclick.execute.call(window, {
//      tableComponent: this,
//      row: row,
//      column: column,
//      menuItem: menuItem
//    });
  },
  _onContextMenuMouseLeave: function (event) {
    var menuElement = Event.element(event);
    Event.stopObserving(menuElement, "mouseleave", this._contextMenuMouseLeaveListener);
    menuElement.remove();
  }
});

Object.extend(IxTable.prototype,fni.events.FNIEventSupport);

function getIxTableById(id) {
  return _ixTables.get(id);
};

function getIxTables() {
  return _ixTables.values();
};

IxTableEditorController = Class.create({
  buildEditor: function (name, columnDefinition) { },
  buildViewer: function (name, columnDefinition) { },
  attachContentHandler: function (table, parentNode, handlerInstance) {
    handlerInstance._table = table;
    handlerInstance._cell = parentNode;
    
    parentNode.appendChild(handlerInstance);
    var row = this.getEditorRow(handlerInstance);
    var column = this.getEditorColumn(handlerInstance);
    table._setCellContentHandler(row, column, handlerInstance);
    
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
    var row = this.getEditorRow(handlerInstance);
    var column = this.getEditorColumn(handlerInstance);
    handlerInstance._table._unsetCellContentHandler(row, column);
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
      return handlerInstance;
    if ((this.getMode(handlerInstance) == IxTableControllers.EDITMODE_ONLY_EDITABLE) && (editable == false))
      return handlerInstance;
    if ((this.getMode(handlerInstance) == IxTableControllers.EDITMODE_NOT_EDITABLE) && (editable == true))
      return handlerInstance;
    
    var table = handlerInstance._table;
    var parentNode = handlerInstance._cell;
    var visible = this.isVisible(handlerInstance); 
    
    this.detachContentHandler(handlerInstance);
    
    var newHandler = editable == true ? this.buildEditor(handlerInstance._name, handlerInstance._columnDefinition) : this.buildViewer(handlerInstance._name, handlerInstance._columnDefinition);
    this.attachContentHandler(table, parentNode, newHandler);
    
    if (visible) 
      this.show(newHandler);
    else 
      this.hide(newHandler);
    
    this.setEditorValue(newHandler, this.getEditorValue(handlerInstance));
    this.destroyHandler(handlerInstance);

    return newHandler;
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
  changeParamName: function (handlerInstance, name) {
    if (handlerInstance._editable) {
      handlerInstance.name = name;
    } else {
      handlerInstance._name = name;
      if (handlerInstance._fieldValue) {
        handlerInstance._fieldValue.name = name;
      }
    }
  },
  focus: function (handlerInstance) {
    Form.Element.focus(handlerInstance);
  },
  getEditorRow: function (handlerInstance) {
    return handlerInstance._table._getCellEditorRow(handlerInstance);
  },
  getEditorColumn: function (handlerInstance) {
    return handlerInstance._table._getCellEditorColumn(handlerInstance);
  },
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
      tableComponent: handlerInstance._table,
      fieldType: handlerInstance._dataType,
      column: this.getEditorColumn(handlerInstance),
      row: this.getEditorRow(handlerInstance),
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
  isDynamicOptions: function (handlerInstance) {
    return false;
  },
  copyCellValue: function(target, source) {
    this.setEditorValue(target, this.getEditorValue(source));
  },
  _copyState: function (target, source) {
    this.copyCellValue(target, source);
    return this.setEditable(target, this.getEditable(source));
    // TODO: disabled, datatype yms tiedot
  }
});

Object.extend(IxTableEditorController.prototype,fni.events.FNIEventSupport);

IxNumberTableEditorController = Class.create(IxTableEditorController, {
  buildEditor: function ($super, name, columnDefinition) {
    var editor = this._createEditorElement("input", name, "ixTableCellEditorNumber", {type: "text", name: name}, columnDefinition);
    
    if (columnDefinition.required)
      editor.addClassName("required");
    
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
    handlerInstance.value = value||'';
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
    
    if (columnDefinition.required)
      cellEditor.addClassName("required");
    
    this._editorValueChangeListener = this._onEditorValueChange.bindAsEventListener(this);
    Event.observe(cellEditor, "change", this._editorValueChangeListener);
    
    cellEditor._dynamicOptions = columnDefinition.dynamicOptions || false;
    
    if (columnDefinition.options) {
      var options = columnDefinition.options;
      var elements = new Array();

      for (var j = 0, l = options.length; j < l; j++) {
        var option = options[j];
        if (option.optionGroup == true) {
          
          var optionGroup = this._createOptionGroup(option.text);

          var groupOptions = option.options;
          for (var groupIndex = 0; groupIndex < groupOptions.length; groupIndex++) {
            var optionElement = this._createOption(groupOptions[groupIndex].value, groupOptions[groupIndex].text, false);
            optionGroup.appendChild(optionElement);
          }
          
          elements.push(optionGroup);
        } else {
          elements.push(this._createOption(option.value, option.text, false));
        }
      }

      for (var i = 0, l = elements.length; i < l;i++) {
        cellEditor.appendChild(elements[i]);
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
  addOptionGroup: function (handlerInstance, text) {
    if (handlerInstance._editable) {
      var optGroupElement = this._createOptionGroup(text);
      
      handlerInstance.appendChild(optGroupElement);
      return optGroupElement;
    }
  },
  addOption: function (handlerInstance, value, text) {
    return this.addOption(handlerInstance, value, text, false);
  },
  addOption: function (handlerInstance, value, text, selected) {
    if (handlerInstance._editable) {
      var optionNode = this._createOption(value, text, selected);
      handlerInstance.appendChild(optionNode);
      return optionNode;
    }
  },
  _createOptionGroup: function (text) {
    return new Element("optgroup", {label:text});
  },
  _createOption: function (value, text, selected) {
    var optionNode;
    
    if (!selected)
      optionNode = new Element("option", {value: value});
    else
      optionNode = new Element("option", {value: value, selected: "selected"});
    
    if (text)
      optionNode.update(text);
    
    return optionNode;
  },
  buildViewer: function ($super, name, columnDefinition) {
    var cellViewer = this._createViewerElement("div", name, "ixTableCellViewerSelect", {}, columnDefinition);
    cellViewer._dynamicOptions = columnDefinition.dynamicOptions || false;
    return cellViewer;
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
  setEditorValue: function ($super, handlerInstance, value, displayValue) {
    if (handlerInstance._editable != true) {
      if (displayValue == undefined) {
        displayValue = value;
        var options = handlerInstance._columnDefinition.options;
        if (options) {
          for (var i = 0; i < options.length; i++) {
            if (options[i].optionGroup == true) {
              for (var j = 0; j < options[i].options.length;j++) {
                if (options[i].options[j].value == value) {
                  displayValue = options[i].options[j].text;
                  break;
                }
              }
            } else {
              if (options[i].value == value) {
                displayValue = options[i].text;
                break;
              }
            }
          }
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
    // TODO copying state can cause the target to change since it may switch between editor/viewer :(
    target = $super(target, source);
    if (source._dynamicOptions && target._dynamicOptions) {
      if (source._editable && target._editable) {
        var sourceValue = this.getEditorValue(source);
        this.removeAllOptions(target);
        for (var i = 0; i < source.options.length; i++) {
          var option = source.options[i];
          this.addOption(target, option.value, option.innerHTML, option.value == sourceValue);
        }
      }
      else {
        // TODO scrap editor/viewer model and implement a simple getValue/getDisplayValue :(
        this.setEditorValue(target, source._fieldValue.value, source._fieldContent.innerHTML);
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
    var row = this.getEditorRow(handlerInstance);
    var column = this.getEditorColumn(handlerInstance);
    
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
    
    // TODO: implement columnDefinition.required
    
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
  attachContentHandler: function ($super, table, parentNode, handlerInstance) {
    var handlerInstance = $super(table, parentNode, handlerInstance);
    
    if (handlerInstance._editable == true) {
      handlerInstance._cell = parentNode;
      var _this = this;
      
      var row = handlerInstance.up('.ixTableRow');
      if (!row) {
        handlerInstance._onRowAdd = function (event) {
          var row = _this.getEditorRow(this);
          if (row == event.row) {
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
      // TODO: Click support for editor
    } else {
      handlerInstance._clickListener = this._onClick.bindAsEventListener(this);
      Event.observe(handlerInstance, "click", handlerInstance._clickListener); 
    }
  },
  detachContentHandler: function ($super, handlerInstance) {
    if (handlerInstance._editable == true) {
      var row = this.getEditorRow(handlerInstance);
      var column = this.getEditorColumn(handlerInstance);
      handlerInstance._table._unsetCellContentHandler(row, column);
      handlerInstance._table = undefined;
      handlerInstance._component.destroy();
      // TODO: Click support for editor
    } else {    
      Event.stopObserving(handlerInstance, "click", handlerInstance._clickListener);
      handlerInstance._clickListener = undefined;
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
  },
  _onClick: function (event) {
    // TODO: Click support for editor
    var handlerInstance = Event.element(event);
    if (!handlerInstance.hasClassName("ixTableCellViewerDate")) {
      handlerInstance = handlerInstance.up(".ixTableCellViewerDate");
    }
    
    if (handlerInstance) {
      if (handlerInstance._columnDefinition.onclick) {
        Event.stop(event);
        
        if (this.isDisabled(handlerInstance) != true) { 
          handlerInstance._columnDefinition.onclick.call(window, {
            tableObject: handlerInstance._table,
            row: this.getEditorRow(handlerInstance),
            column: this.getEditorColumn(handlerInstance)
          });
        }
      }
    }
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
  attachContentHandler: function ($super, table, parentNode, handlerInstance) {
    var handlerInstance = $super(table, parentNode, handlerInstance);
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
      Event.stop(event);
      
      if (this.isDisabled(handlerInstance) != true) { 
        handlerInstance._columnDefinition.onclick.call(window, {
          tableObject: handlerInstance._table,
          row: this.getEditorRow(handlerInstance),
          column: this.getEditorColumn(handlerInstance)
        });
      }
    }
  }
});

IxTableControllers.registerController(new IxButtonTableEditorButtonController());

IxTextTableEditorController = Class.create(IxTableEditorController, {
  buildEditor: function ($super, name, columnDefinition) {
    var editor = this._createEditorElement("input", name, "ixTableCellEditorText", {name: name, type: "text"}, columnDefinition);

    if (columnDefinition.required)
      editor.addClassName("required");

    this._editorValueChangeListener = this._onEditorValueChange.bindAsEventListener(this);
    Event.observe(editor, "change", this._editorValueChangeListener);
    return editor;
  },
  buildViewer: function ($super, name, columnDefinition) {
    return this._createViewerElement("div", name, "ixTableCellViewerText", {}, columnDefinition);
  },
  attachContentHandler: function ($super, table, parentNode, handlerInstance) {
    var handlerInstance = $super(table, parentNode, handlerInstance);
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
          row: this.getEditorRow(handlerInstance),
          column: this.getEditorColumn(handlerInstance)
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

IxAutoCompleteSelectTableEditorController = Class.create(IxTableEditorController, {
  buildEditor: function ($super, name, columnDefinition) {
    var cellEditor = this._createEditorElement("div", name, "ixTableCellEditorAutoCompleteSelect tableAutoCompleteSelect", {}, columnDefinition);
    
    var classNames = "ixTableCellEditorAutoCompleteSelectText";
    if (columnDefinition.required)
      classNames += ' required';
    
    var inputElement = new Element("input", {type: "text", className: classNames, name: name + '.text'});
    var idElement = new Element("input", {type: "hidden", name: name, className: "ixTableCellEditorAutoCompleteSelectId"});
    var indicatorElement = new Element("span", {className: "autocomplete_progress_indicator", style: "display: none"}).update('<img src="' + columnDefinition.autoCompleteProgressUrl + '"/>');
    var choicesElement = new Element("div", {className: "autocomplete_choices"});  
    
    if (columnDefinition.displayValue)
      inputElement.value = columnDefinition.displayValue;
     
    cellEditor.appendChild(inputElement);
    cellEditor.appendChild(idElement);
    cellEditor.appendChild(indicatorElement);
    cellEditor.appendChild(choicesElement);
    
    var _this = this;
    new Ajax.Autocompleter(inputElement, choicesElement, columnDefinition.autoCompleteUrl, {
      paramName: 'text', 
      minChars: 1, 
      indicator: indicatorElement,
      afterUpdateElement : function getSelectionId(text, li) {
        var li = $(li);
        idElement.value = li.down('input[name="id"]').value;
        inputElement.validate(false);
      }
    });
    
    return cellEditor;
  },
  buildViewer: function ($super, name, columnDefinition) {
    return this._createViewerElement("div", name, "ixTableCellViewerAutoCompleteSelect", {}, columnDefinition);
  },
  attachContentHandler: function ($super, table, parentNode, handlerInstance) {
    var handlerInstance = $super(table, parentNode, handlerInstance);
    
    if (handlerInstance._editable == true) {
      var textInput = handlerInstance.down('input.ixTableCellEditorAutoCompleteSelectText');
      textInput._keyUpListener = this._onTextInputKeyUp.bindAsEventListener(this);
      Event.observe(textInput, "keyup", textInput._keyUpListener);
    } 
  },
  detachContentHandler: function ($super, handlerInstance) {
    if (handlerInstance._editable == true) {
      var textInput = handlerInstance.down('input.ixTableCellEditorAutoCompleteSelectText');
      Event.stopObserving(textInput, "keyup", textInput._keyUpListener);
      textInput._keyUpListener = undefined;
    } 

    $super(handlerInstance);
  }, 
  getEditorValue: function ($super, handlerInstance) {
    if (handlerInstance._editable != true) 
      return this._getViewerValue(handlerInstance);
    else {
      return handlerInstance.down('input.ixTableCellEditorAutoCompleteSelectId').value;
    }
  },
  setEditorValue: function ($super, handlerInstance, value) {
    if (handlerInstance._editable != true) {
      this._setViewerValue(handlerInstance, value);
    } else {
      if (this.isDisabled(handlerInstance))
        this._updateDisabledHiddenElement(handlerInstance, value);
      
      var idInput = handlerInstance.down('input.ixTableCellEditorAutoCompleteSelectId');
      idInput.value = value;
    }
  },
  getDisplayValue: function (handlerInstance) {
    if (handlerInstance._editable != true) {
      return handlerInstance._fieldContent.innerHTML;
    } else {
      return handlerInstance.down('input.ixTableCellEditorAutoCompleteSelectText').value;
    }
  },
  setDisplayValue: function (handlerInstance, displayValue) {
    if (handlerInstance._editable) {
      var textInput = handlerInstance.down('input.ixTableCellEditorAutoCompleteSelectText');
      textInput.value = displayValue;
      textInput.validate(true);
    } else {
      this._setViewerValue(handlerInstance, this.getEditorValue(handlerInstance), displayValue); 
    }
  },
  destroyEditor: function ($super, handlerInstance) {
    handlerInstance.remove();
  },
  isDisabled: function ($super, handlerInstance) {
    // TODO: 
    return false;
//     return handlerInstance.disabled;
  },
  getDataType: function ($super) {
    return "autoCompleteSelect";  
  },
  getMode: function ($super) { 
    return IxTableControllers.EDITMODE_EDITABLE;
  },
  copyCellValue: function($super, target, source) {
    this.setEditorValue(target, this.getEditorValue(source));
    this.setDisplayValue(target, this.getDisplayValue(source));
  },
  setEditable: function ($super, handlerInstance, editable) {
    if (handlerInstance._editable == editable)
      return handlerInstance;
    if ((this.getMode(handlerInstance) == IxTableControllers.EDITMODE_ONLY_EDITABLE) && (editable == false))
      return handlerInstance;
    if ((this.getMode(handlerInstance) == IxTableControllers.EDITMODE_NOT_EDITABLE) && (editable == true))
      return handlerInstance;
    
    var table = handlerInstance._table;
    var parentNode = handlerInstance._cell;
    var visible = this.isVisible(handlerInstance); 
    var displayValue = this.getDisplayValue(handlerInstance);
    
    this.detachContentHandler(handlerInstance);
    
    var newHandler = editable == true ? this.buildEditor(handlerInstance._name, handlerInstance._columnDefinition) : this.buildViewer(handlerInstance._name, handlerInstance._columnDefinition);
    this.attachContentHandler(table, parentNode, newHandler);
    
    if (visible) 
      this.show(newHandler);
    else 
      this.hide(newHandler);
    
    this.setEditorValue(newHandler, this.getEditorValue(handlerInstance));
    this.destroyHandler(handlerInstance);
    
    this.setDisplayValue(newHandler, displayValue);

    return newHandler;
  },
  _onTextInputKeyUp: function (event) {
    var textInput = Event.element(event);
    var handlerInstance = textInput.parentNode;
    var hiddenInput = handlerInstance.down('input.ixTableCellEditorAutoCompleteSelectId');
    hiddenInput.value = -1;
  }
});

IxTableControllers.registerController(new IxAutoCompleteSelectTableEditorController());

IxAutoCompleteTextTableEditorController = Class.create(IxTableEditorController, {
  buildEditor: function ($super, name, columnDefinition) {
    var cellEditor = this._createEditorElement("div", name, "ixTableCellEditorAutoComplete tableAutoComplete", {}, columnDefinition);
    
    var classNames = "ixTableCellEditorAutoCompleteText";
    if (columnDefinition.required)
      classNames += ' required';
    
    var inputElement = new Element("input", {type: "text", className: classNames, name: name});
    var indicatorElement = new Element("span", {className: "autocomplete_progress_indicator", style: "display: none"}).update('<img src="' + columnDefinition.autoCompleteProgressUrl + '"/>');
    var choicesElement = new Element("div", {className: "autocomplete_choices"});  
    
    if (columnDefinition.displayValue)
      inputElement.value = columnDefinition.displayValue;
     
    cellEditor.appendChild(inputElement);
    cellEditor.appendChild(indicatorElement);
    cellEditor.appendChild(choicesElement);
    
    var _this = this;
    new Ajax.Autocompleter(inputElement, choicesElement, columnDefinition.autoCompleteUrl, {
      paramName: 'text', 
      minChars: 1, 
      indicator: indicatorElement
    });
    
    return cellEditor;
  },
  buildViewer: function ($super, name, columnDefinition) {
    return this._createViewerElement("div", name, "ixTableCellViewerAutoComplete", {}, columnDefinition);
  },
  getEditorValue: function ($super, handlerInstance) {
    if (handlerInstance._editable != true) 
      return this._getViewerValue(handlerInstance);
    else {
      return handlerInstance.down('input.ixTableCellEditorAutoCompleteText').value;
    }
  },
  setEditorValue: function ($super, handlerInstance, value) {
    if (handlerInstance._editable != true) {
      this._setViewerValue(handlerInstance, value);
    } else {
      if (this.isDisabled(handlerInstance))
        this._updateDisabledHiddenElement(handlerInstance, value);
      
      var textInput = handlerInstance.down('input.ixTableCellEditorAutoCompleteText');
      textInput.value = value;
    }
  },
  destroyEditor: function ($super, handlerInstance) {
    handlerInstance.remove();
  },
  isDisabled: function ($super, handlerInstance) {
    // TODO: 
    return false;
//     return handlerInstance.disabled;
  },
  getDataType: function ($super) {
    return "autoComplete";  
  },
  getMode: function ($super) { 
    return IxTableControllers.EDITMODE_EDITABLE;
  }
});

IxTableControllers.registerController(new IxAutoCompleteTextTableEditorController());

_IxTable_TABLESTRINGSORT = Class.create({
  initialize : function(sortDirection, column) {
    this._sortDirection = sortDirection;
    this._column = column;
  },
  execute: function (event) {
    var table = event.tableComponent;
  
    var _this = this;
    var rowElements = $(table._content).select('.ixTableRow').sortBy(
      function(element) {
        var row = element._rowNumber;
        var val = table.getCellValue(row, _this._column);
        return String(val).toUpperCase();
      });

    if (this._sortDirection == "desc") {
      for (var i = rowElements.length - 1; i >= 0; i--) {
        table._content.appendChild(rowElements[i]);
      }
    } else {
      for (var i = 0, l = rowElements.length; i < l; i++) {
        table._content.appendChild(rowElements[i]);
      }
    }
  },
  _sortDirection: "asc",
  _column: -1
});
 
IxTable_ROWSTRINGSORT = Class.create({
  initialize : function(sortDirection) {
    this._sortDirection = sortDirection;
  },
  execute: function (event) {
    var table = event.tableComponent;
    var column = event.column;
    var sortAction = event.sortAction;
    var sortDirection = sortAction.sortAction._sortDirection;
    
    table._setSortMethod(new _IxTable_TABLESTRINGSORT(sortDirection, column));
  }
});

_IxTable_TABLESTRINGFILTER = Class.create({
  initialize : function(column, filterValue, rowFilterableFunc) {
    this._column = column; 
    this._filterValue = filterValue;
    this._rowFilterableFunc = rowFilterableFunc;
  },
  execute: function (event) {
    var table = event.tableComponent;
    var filterFunc = this._rowFilterableFunc;
    var hasFilterFunc = !(this._rowFilterableFunc == undefined);
    
    var hideArray = new Array();
    
    for (var i = table.getRowCount() - 1; i >= 0; i--) {
      var rowValue = table.getCellValue(i, this._column);
      if (rowValue != this._filterValue) {
        
        if ((!hasFilterFunc) || (filterFunc(table, i) === true))
          hideArray.push(i);
      }
    }

    table.hideRows(hideArray.toArray());
  },
  _rowFilterableFunc: undefined,
  _column: undefined,
  _filterValue: undefined  
});

IxTable_ROWSTRINGFILTER = Class.create({
  initialize : function(rowFilterableFunc) {
    this._rowFilterableFunc = rowFilterableFunc;
  },
  execute: function (event) {
    var table = event.tableComponent;
    var row = event.row;
    var column = event.column;
    var filterValue = table.getCellValue(row, column);
    var filter = new _IxTable_TABLESTRINGFILTER(column, filterValue, this._rowFilterableFunc);

    table._addFilter(filter);
  },
  _rowFilterableFunc: undefined
});

_IxTable_TABLEDATEFILTER = Class.create({
  initialize : function(column, filterValue, filterEarlier, rowFilterableFunc) {
    this._column = column;
    this._filterValue = filterValue;
    this._filterEarlier = filterEarlier;
    this._rowFilterableFunc = rowFilterableFunc;
  },
  execute: function (event) {
    var table = event.tableComponent;
    var filterFunc = this._rowFilterableFunc;
    var hasFilterFunc = !((filterFunc == undefined) || (filterFunc == null));
    
    var hideArray = new Array();

    if (this._filterEarlier) {
      for (var i = table.getRowCount() - 1; i >= 0; i--) {
        var rowValue = table.getCellValue(i, this._column);
        if ((rowValue) && (rowValue > this._filterValue)) { 
          if ((!hasFilterFunc) || (filterFunc(table, i) === true))
            hideArray.push(i);
        }
      }
    } else {
      for (var i = table.getRowCount() - 1; i >= 0; i--) {
        var rowValue = table.getCellValue(i, this._column);
        if ((rowValue) && (rowValue < this._filterValue)) { 
          if ((!hasFilterFunc) || (filterFunc(table, i) === true))
            hideArray.push(i);
        }
      }
    }
  
    table.hideRows(hideArray.toArray());
  },
  _column: undefined, 
  _filterValue: undefined, 
  _filterEarlier: undefined, 
  _rowFilterableFunc: undefined
});

IxTable_ROWDATEFILTER = Class.create({
  initialize : function(filterEarlier, rowFilterableFunc) {
    this._filterEarlier = filterEarlier;
    this._rowFilterableFunc = rowFilterableFunc;
  },
  execute: function (event) {
    var table = event.tableComponent;
    var row = event.row;
    var column = event.column;
    var filterValue = table.getCellValue(row, column);

    var filter = new _IxTable_TABLEDATEFILTER(column, filterValue, this._filterEarlier, this._rowFilterableFunc);
    table._addFilter(filter);
  },
  _filterEarlier: undefined,
  _rowFilterableFunc: undefined
});

IxTable_ROWCLEARFILTER = Class.create({
  initialize : function() {
  },
  execute: function (event) {
    var table = event.tableComponent;
    table._clearFilters();
  }  
});

IxTable_COPYVALUESTOCOLUMNACTION = Class.create({
  initialize : function(onlyModifiable) {
    this._onlyModifiable = onlyModifiable;
  },
  execute: function (event) {
    var table = event.tableComponent;
    var column = event.column;
    var row = event.row;
    
    var value = table.getCellValue(row, column);
    var controller = IxTableControllers.getController(table.getCellEditor(row, column)._dataType);
    
    for (var i = 0, len = table.getRowCount(); i < len; i++) {
      var cellEditor = table.getCellEditor(i, column);
      var editable = controller.getEditable(cellEditor); 

      if ((!this._onlyModifiable) || (editable))
        table.setCellValue(i, column, value);
    }
  }
});
