<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
  <head>
    <title><fmt:message key="settings.timeUnits.pageTitle"/></title>

    <jsp:include page="/templates/generic/head_generic.jsp"></jsp:include>
    <jsp:include page="/templates/generic/dialog_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/scriptaculous_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/tabs_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/table_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonrequest_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonform_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/draftapi_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/validation_support.jsp"></jsp:include>

    <script type="text/javascript">

      var archivedRowIndex;

      function addTimeUnitsTableRow() {
        var table = getIxTableById('timeUnitsTable');
        var rowIndex = table.addRow(['', false, 0, '', '', '', -1]);
        for (var i = 0; i < table.getColumnCount(); i++) {
          table.setCellEditable(rowIndex, i, true);
        }
        
        if (rowIndex == 0) {
          table.setCellValue(0, table.getNamedColumnIndex("baseUnit"), true);
          table.setCellValue(0, table.getNamedColumnIndex("baseUnits"), 1);
          table.hideCell(0, table.getNamedColumnIndex("baseUnits"));
        }
        
        $('noTimeUnitsAddedMessageContainer').setStyle({
          display: 'none'
        });
        table.showCell(rowIndex, table.getNamedColumnIndex('removeButton'));
      }
      
      function onLoad(event) {
        tabControl = new IxProtoTabs($('tabs'));

        var timeUnitsTable = new IxTable($('timeUnitsTable'), {
          id : "timeUnitsTable",
          columns : [ {
            left: 8,
            width: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
            tooltip: '<fmt:message key="settings.timeUnits.timeUnitsTableEditTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              for (var i = 0; i < table.getColumnCount(); i++) {
                table.setCellEditable(event.row, i, table.isCellEditable(event.row, i) == false);
              }
            }
          }, {
            header : '<fmt:message key="settings.timeUnits.timeUnitsTableBaseUnitHeader"/>',
            left : 38,
            width : 80,
            dataType: 'checkbox',
            editable: false,
            paramName: 'baseUnit'
          }, {
            header : '<fmt:message key="settings.timeUnits.timeUnitsTableBaseUnitsHeader"/>',
            left: 126,
            width : 100,
            dataType: 'number',
            editable: false,
            paramName: 'baseUnits',
            editorClassNames: 'required'
          }, {
            header : '<fmt:message key="settings.timeUnits.timeUnitsTableNameHeader"/>',
            left: 234,
            right: 46,
            dataType: 'text',
            editable: false,
            paramName: 'name',
            editorClassNames: 'required'
          }, {
            right: 8,
            width: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/edit-delete.png',
            tooltip: '<fmt:message key="settings.timeUnits.timeUnitsTableArchiveTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var timeUnitId = table.getCellValue(event.row, table.getNamedColumnIndex('timeUnitId'));
              var timeUnitName = table.getCellValue(event.row, table.getNamedColumnIndex('name'));
              var url = GLOBAL_contextPath + "/simpledialog.page?localeId=settings.timeUnits.timeUnitArchiveConfirmDialogContent&localeParams=" + encodeURIComponent(timeUnitName);

              archivedRowIndex = event.row; 
                 
              var dialog = new IxDialog({
                id : 'confirmRemoval',
                contentURL : url,
                centered : true,
                showOk : true,  
                showCancel : true,
                autoEvaluateSize: true,
                title : '<fmt:message key="settings.timeUnits.timeUnitArchiveConfirmDialogTitle"/>',
                okLabel : '<fmt:message key="settings.timeUnits.timeUnitArchiveConfirmDialogOkLabel"/>',
                cancelLabel : '<fmt:message key="settings.timeUnits.timeUnitArchiveConfirmDialogCancelLabel"/>'
              });
            
              dialog.addDialogListener(function(event) {
                switch (event.name) {
                  case 'okClick':
                    JSONRequest.request("settings/archivetimeunit.json", {
                      parameters: {
                        timeUnitId: timeUnitId
                      },
                      onSuccess: function (jsonResponse) {
                        getIxTableById('timeUnitsTable').deleteRow(archivedRowIndex);
                        saveFormDraft();
                      }
                    });   
                  break;
                }
              });
            
              dialog.open();
            },
            paramName: 'archiveButton',
            hidden: true
          }, {
            right: 8,
            width: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/list-remove.png',
            tooltip: '<fmt:message key="settings.timeUnits.timeUnitsTableRemoveTooltip"/>',
            onclick: function (event) {
              event.tableObject.deleteRow(event.row);
              if (event.tableObject.getRowCount() == 0) {
                $('noTimeUnitsAddedMessageContainer').setStyle({
                  display: ''
                });
              }
            },
            paramName: 'removeButton',
            hidden: true
          }, {
            dataType: 'hidden',
            paramName: 'timeUnitId'
          }]
        });
        
        timeUnitsTable.addListener("cellValueChange", function (event) {
          if ((event.value == true) && !this._settingValue) {
	          var baseUnitColumn = event.tableComponent.getNamedColumnIndex("baseUnit");
	          var baseUnitsColumn = event.tableComponent.getNamedColumnIndex("baseUnits");
	          
	          if (baseUnitColumn == event.column) {
	            this._settingValue = true;
	            try {
		            for (var i = 0, l = event.tableComponent.getRowCount(); i < l; i++) {
		              if (i != event.row) {
		                event.tableComponent.setCellValue(i, baseUnitColumn, false);
		                event.tableComponent.showCell(i, baseUnitsColumn);
		              } else {
		                event.tableComponent.setCellValue(i, baseUnitColumn, true);
		                event.tableComponent.hideCell(i, baseUnitsColumn);
		              }
		            }
	            } finally {
	              this._settingValue = false;
	            }
	          }
          }
        });

        var rowIndex;
        <c:forEach var="timeUnit" items="${timeUnits}">
          rowIndex = timeUnitsTable.addRow([
            '',
            ${timeUnit eq baseTimeUnit},
            ${timeUnit.baseUnits},
            '${fn:replace(timeUnit.name, "'", "\\'")}',
            '',
            '',
            ${timeUnit.id}
          ]);

          if (${timeUnit eq baseTimeUnit}) {
            timeUnitsTable.hideCell(rowIndex, timeUnitsTable.getNamedColumnIndex("baseUnits"));
          }
          
          timeUnitsTable.showCell(rowIndex, timeUnitsTable.getNamedColumnIndex('archiveButton'));
        </c:forEach>

        if (timeUnitsTable.getRowCount() > 0) {
          $('noTimeUnitsAddedMessageContainer').setStyle({
            display: 'none'
          });
        }
      }
        
    </script>
  </head>
  
  <body onload="onLoad(event);" ix:enabledrafting="true">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
    
    <h1 class="genericPageHeader"><fmt:message key="settings.timeUnits.pageTitle"/></h1>
    
    <div id="manageTimeUnitsFormContainer"> 
	    <div class="genericFormContainer"> 
	      <form action="savetimeunits.json" method="post" ix:jsonform="true" ix:useglasspane="true">
	  
	        <div class="tabLabelsContainer" id="tabs">
	          <a class="tabLabel" href="#manageTimeUnits">
	            <fmt:message key="settings.timeUnits.tabLabelTimeUnits"/>
	          </a>
	        </div>
          
          <div id="manageTimeUnits" class="tabContentixTableFormattedData">
            <div class="genericTableAddRowContainer">
              <span class="genericTableAddRowLinkContainer" onclick="addTimeUnitsTableRow();"><fmt:message key="settings.timeUnits.addTimeUnitLink"/></span>
            </div>
              
            <div id="noTimeUnitsAddedMessageContainer" class="genericTableNotAddedMessageContainer">
              <span><fmt:message key="settings.timeUnits.noTimeUnitsAddedPreFix"/> <span onclick="addTimeUnitsTableRow();" class="genericTableAddRowLink"><fmt:message key="settings.timeUnits.noTimeUnitsAddedClickHereLink"/></span>.</span>
            </div>
            
            <div id="timeUnitsTable"></div>
          </div>
	  
          <div class="genericFormSubmitSectionOffTab">
            <input type="submit" class="formvalid" value="<fmt:message key="settings.timeUnits.saveButton"/>">
          </div>

	      </form>
	    </div>
	  </div>
    
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>