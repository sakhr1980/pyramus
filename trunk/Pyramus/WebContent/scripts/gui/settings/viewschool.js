var variableKeys = JSDATA["variableKeys"].evalJSON();

function onLoad(event) {
  var tabControl = new IxProtoTabs($('tabs'));
  var variablesTable = new IxTable($('variablesTable'), {
    id : "variablesTable",
    columns : [ {
      dataType : 'hidden',
      editable : false,
      paramName : 'key'
    }, {
      left : 0,
      width : 150,
      dataType : 'text',
      editable : false,
      paramName : 'name'
    }, {
      left : 150,
      width : 750,
      dataType : 'text',
      editable : false,
      paramName : 'value'
    } ]
  });
  variablesTable.detachFromDom();
  for ( var i = 0, l = variableKeys.length; i < l; i++) {
    var rowNumber = variablesTable.addRow([ variableKeys[i].variableKey.escapeHTML(), variableKeys[i].variableName.escapeHTML(),
        variableKeys[i].variableValue.escapeHTML() ]);
    var dataType;
    switch (variableKeys[i].variableType) {
      case 'NUMBER':
        dataType = 'number';
      break;
      case 'DATE':
        dataType = 'date';
      break;
      case 'BOOLEAN':
        dataType = 'boolean';
      break;
      default:
        dataType = 'text';
      break;
    }
    variablesTable.setCellDataType(rowNumber, 3, dataType);
  }
  variablesTable.reattachToDom();
};