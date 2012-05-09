<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
  <head>
    <title><fmt:message key="settings.fileTypes.pageTitle"/></title>

    <jsp:include page="/templates/generic/head_generic.jsp"></jsp:include>
    <jsp:include page="/templates/generic/ckeditor_support.jsp"></jsp:include>
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

      function addTableRow() {
        var table = getIxTableById('fileTypesTable');
        var rowIndex = table.addRow(['', '', '', '', -1, 1]);
        for (var i = 0; i < table.getColumnCount(); i++) {
          table.setCellEditable(rowIndex, i, true);
        }
        $('noFileTypesAddedMessageContainer').setStyle({
          display: 'none'
        });
        table.showCell(rowIndex, table.getNamedColumnIndex('removeButton'));
        table.hideCell(rowIndex, table.getNamedColumnIndex('archiveButton'));
      }
      
      function onLoad(event) {
        tabControl = new IxProtoTabs($('tabs'));

        var fileTypesTable = new IxTable($('fileTypesTable'), {
          id : "fileTypesTable",
          columns : [ {
            left: 8,
            width: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
            tooltip: '<fmt:message key="settings.fileTypes.tableEditTooltip"/>',
            onclick: function (event) {
              var table = event.tableComponent;
              for (var i = 0; i < table.getColumnCount(); i++) {
                table.setCellEditable(event.row, i, table.isCellEditable(event.row, i) == false);
              }
              table.setCellValue(event.row, table.getNamedColumnIndex('modified'), 1);
            }
          }, {
            header : '<fmt:message key="settings.fileTypes.tableNameHeader"/>',
            left : 38,
            width : 300,
            dataType: 'text',
            editable: false,
            paramName: 'name',
            required: true
          }, {
            right: 8,
            width: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/edit-delete.png',
            tooltip: '<fmt:message key="settings.fileTypes.tableArchiveTooltip"/>',
            onclick: function (event) {
              var table = event.tableComponent;
              var fileTypeId = table.getCellValue(event.row, table.getNamedColumnIndex('id'));
              var fileTypeName = table.getCellValue(event.row, table.getNamedColumnIndex('name'));
              var url = GLOBAL_contextPath + "/simpledialog.page?localeId=settings.fileTypes.archiveConfirmDialogContent&localeParams=" + encodeURIComponent(fileTypeName);

              archivedRowIndex = event.row; 
                 
              var dialog = new IxDialog({
                id : 'confirmRemoval',
                contentURL : url,
                centered : true,
                showOk : true,  
                showCancel : true,
                autoEvaluateSize: true,
                title : '<fmt:message key="settings.fileTypes.archiveConfirmDialogTitle"/>',
                okLabel : '<fmt:message key="settings.fileTypes.archiveConfirmDialogOkLabel"/>',
                cancelLabel : '<fmt:message key="settings.fileTypes.archiveConfirmDialogCancelLabel"/>'
              });
            
              dialog.addDialogListener(function(event) {
                switch (event.name) {
                  case 'okClick':
                    JSONRequest.request("settings/archivefiletype.json", {
                      parameters: {
                        fileTypeId: fileTypeId
                      },
                      onSuccess: function (jsonResponse) {
                        getIxTableById('fileTypesTable').deleteRow(archivedRowIndex);
                      }
                    });   
                  break;
                }
              });
            
              dialog.open();
            },
            paramName: 'archiveButton'
          }, {
            right: 8,
            width: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/list-remove.png',
            tooltip: '<fmt:message key="settings.fileTypes.tableRemoveTooltip"/>',
            onclick: function (event) {
              event.tableComponent.deleteRow(event.row);
              if (event.tableComponent.getRowCount() == 0) {
                $('noFileTypesAddedMessageContainer').setStyle({
                  display: ''
                });
              }
            },
            paramName: 'removeButton',
            hidden: true
          }, {
            dataType: 'hidden',
            paramName: 'id'
          }, {
            dataType: 'hidden',
            paramName: 'modified'
          }]
        });

        var rows = new Array();
        <c:forEach var="fileType" items="${fileTypes}">
          rows.push([
            '',
            '${fn:escapeXml(fileType.name)}',
            '',
            '',
            ${fileType.id},
            0
          ]);
        </c:forEach>

        fileTypesTable.addRows(rows);
        
        if (fileTypesTable.getRowCount() > 0) {
          $('noFileTypesAddedMessageContainer').setStyle({
            display: 'none'
          });
        }
      }
        
    </script>
  </head>
  
  <body onload="onLoad(event);" ix:enabledrafting="true">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
    
    <h1 class="genericPageHeader"><fmt:message key="settings.fileTypes.pageTitle"/></h1>
    
    <div id="manageFileTypesFormContainer"> 
	    <div class="genericFormContainer"> 
	      <form action="savefiletypes.json" method="post" ix:jsonform="true" ix:useglasspane="true">
	  
	        <div class="tabLabelsContainer" id="tabs">
	          <a class="tabLabel" href="#manageFileTypes">
	            <fmt:message key="settings.fileTypes.tabLabel"/>
	          </a>
	        </div>
	        
          <div id="manageFileTypes" class="tabContentixTableFormattedData">
            <div class="genericTableAddRowContainer">
              <span class="genericTableAddRowLinkContainer" onclick="addTableRow();"><fmt:message key="settings.fileTypes.addFileTypeLink"/></span>
            </div>
              
            <div id="noFileTypesAddedMessageContainer" class="genericTableNotAddedMessageContainer">
              <span><fmt:message key="settings.fileTypes.noFileTypesAddedPreFix"/> <span onclick="addTableRow();" class="genericTableAddRowLink"><fmt:message key="settings.fileTypes.noFileTypesAddedClickHereLink"/></span>.</span>
            </div>
            
            <div id="fileTypesTable"></div>
          </div>
	  
          <div class="genericFormSubmitSectionOffTab">
            <input type="submit" class="formvalid" value="<fmt:message key="settings.fileTypes.saveButton"/>">
          </div>

	      </form>
	    </div>
	  </div>
    
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>