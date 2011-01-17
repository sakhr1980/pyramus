<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
  <head>
    <title><fmt:message key="settings.manageSchoolFields.pageTitle"/></title>

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

      function addRow() {
        var table = getIxTableById('schoolFieldsTable');
        var rowIndex = table.addRow(['', '', '', '', -1, 1]);
        for (var i = 0; i < table.getColumnCount(); i++) {
          table.setCellEditable(rowIndex, i, true);
        }
        $('noSchoolFieldsAddedMessageContainer').setStyle({
          display: 'none'
        });
        table.showCell(rowIndex, table.getNamedColumnIndex('removeButton'));
      }
      
      function onLoad(event) {
        tabControl = new IxProtoTabs($('tabs'));

        var table = new IxTable($('schoolFieldsTable'), {
          id : "schoolFieldsTable",
          columns : [ {
            left: 8,
            width: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
            tooltip: '<fmt:message key="settings.manageSchoolFields.schoolFieldsTableEditTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              for (var i = 0; i < table.getColumnCount(); i++) {
                table.setCellEditable(event.row, i, table.isCellEditable(event.row, i) == false);
              }
              table.setCellValue(event.row, table.getNamedColumnIndex('modified'), 1);
            }
          }, {
            header : '<fmt:message key="settings.manageSchoolFields.schoolFieldsTableNameHeader"/>',
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
            tooltip: '<fmt:message key="settings.manageSchoolFields.schoolFieldsTableArchiveTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var schoolFieldId = table.getCellValue(event.row, table.getNamedColumnIndex('id'));
              var name = table.getCellValue(event.row, table.getNamedColumnIndex('name'));
              var url = GLOBAL_contextPath + "/simpledialog.page?localeId=settings.manageSchoolFields.schoolFieldsArchiveConfirmDialogContent&localeParams=" + encodeURIComponent(name);

              archivedRowIndex = event.row; 
                 
              var dialog = new IxDialog({
                id : 'confirmRemoval',
                contentURL : url,
                centered : true,
                showOk : true,  
                showCancel : true,
                autoEvaluateSize: true,
                title : '<fmt:message key="settings.manageSchoolFields.archiveConfirmDialogTitle"/>',
                okLabel : '<fmt:message key="settings.manageSchoolFields.archiveConfirmDialogOkLabel"/>',
                cancelLabel : '<fmt:message key="settings.manageSchoolFields.archiveConfirmDialogCancelLabel"/>'
              });
            
              dialog.addDialogListener(function(event) {
                switch (event.name) {
                  case 'okClick':
                    JSONRequest.request("settings/archiveschoolfield.json", {
                      parameters: {
                        schoolFieldId: schoolFieldId
                      },
                      onSuccess: function (jsonResponse) {
                        getIxTableById('schoolFieldsTable').deleteRow(archivedRowIndex);
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
            tooltip: '<fmt:message key="settings.manageSchoolFields.schoolFieldsTableRemoveTooltip"/>',
            onclick: function (event) {
              event.tableObject.deleteRow(event.row);
              if (event.tableObject.getRowCount() == 0) {
                $('noSchoolFieldsAddedMessageContainer').setStyle({
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

        var rowIndex;
        <c:forEach var="field" items="${schoolFields}">
          rowIndex = table.addRow([
            '',
            '${fn:replace(field.name, "'", "\\'")}',
            '',
            '',
            ${field.id},
            0
          ]);
          table.showCell(rowIndex, table.getNamedColumnIndex('archiveButton'));
        </c:forEach>

        if (table.getRowCount() > 0) {
          $('noSchoolFieldsAddedMessageContainer').setStyle({
            display: 'none'
          });
        }
      }
        
    </script>
  </head>
  
  <body onload="onLoad(event);" ix:enabledrafting="true">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
    
    <h1 class="genericPageHeader"><fmt:message key="settings.manageSchoolFields.pageTitle"/></h1>
    
    <div id="manageSchoolFieldsFormContainer"> 
	    <div class="genericFormContainer"> 
	      <form action="saveschoolfields.json" method="post" ix:jsonform="true" ix:useglasspane="true">
	  
	        <div class="tabLabelsContainer" id="tabs">
	          <a class="tabLabel" href="#manageSchoolFields">
	            <fmt:message key="settings.manageSchoolFields.tabLabelSchoolFields"/>
	          </a>
	        </div>
	        
          <div id="manageSchoolFields" class="tabContentixTableFormattedData">
            <div class="genericTableAddRowContainer">
              <span class="genericTableAddRowLinkContainer" onclick="addRow();"><fmt:message key="settings.manageSchoolFields.addSchoolFieldLink"/></span>
            </div>
              
            <div id="noSchoolFieldsAddedMessageContainer" class="genericTableNotAddedMessageContainer">
              <span><fmt:message key="settings.manageSchoolFields.noSchoolFieldsAddedPreFix"/> <span onclick="addRow();" class="genericTableAddRowLink"><fmt:message key="settings.manageSchoolFields.noSchoolFieldsAddedClickHereLink"/></span>.</span>
            </div>
            
            <div id="schoolFieldsTable"></div>
          </div>
	  
          <div class="genericFormSubmitSectionOffTab">
            <input type="submit" class="formvalid" value="<fmt:message key="settings.manageSchoolFields.saveButton"/>">
          </div>

	      </form>
	    </div>
	  </div>
    
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>