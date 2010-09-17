<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
  <head>
    <title><fmt:message key="settings.educationTypes.pageTitle"/></title>

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

      function addEducationTypesTableRow() {
        var table = getIxTableById('educationTypesTable');
        var rowIndex = table.addRow(['', '', '', '', '', -1, 1]);
        for (var i = 0; i < table.getColumnCount(); i++) {
          table.setCellEditable(rowIndex, i, true);
        }
        $('noEducationTypesAddedMessageContainer').setStyle({
          display: 'none'
        });
        table.showCell(rowIndex, table.getNamedColumnIndex('removeButton'));
      }
      
      function onLoad(event) {
        tabControl = new IxProtoTabs($('tabs'));

        var educationTypesTable = new IxTable($('educationTypesTable'), {
          id : "educationTypesTable",
          columns : [ {
            left: 8,
            width: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
            tooltip: '<fmt:message key="settings.educationTypes.educationTypesTableEditTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              for (var i = 0; i < table.getColumnCount(); i++) {
                table.setCellEditable(event.row, i, table.isCellEditable(event.row, i) == false);
              }
              table.setCellValue(event.row, table.getNamedColumnIndex('modified'), 1);
            }
          }, {
            header : '<fmt:message key="settings.educationTypes.educationTypesTableNameHeader"/>',
            left : 38,
            width : 300,
            dataType: 'text',
            editable: false,
            paramName: 'name',
            editorClassNames: 'required'
          }, {
            header : '<fmt:message key="settings.educationTypes.educationTypesTableCodeHeader"/>',
            left: 346,
            right: 44,
            dataType: 'text',
            editable: false,
            paramName: 'code',
            editorClassNames: 'required'
          }, {
            right: 8,
            width: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/edit-delete.png',
            tooltip: '<fmt:message key="settings.educationTypes.educationTypesTableArchiveTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var educationTypeId = table.getCellValue(event.row, table.getNamedColumnIndex('educationTypeId'));
              var educationTypeName = table.getCellValue(event.row, table.getNamedColumnIndex('name'));
              var url = GLOBAL_contextPath + "/simpledialog.page?localeId=settings.educationTypes.educationTypeArchiveConfirmDialogContent&localeParams=" + encodeURIComponent(educationTypeName);

              archivedRowIndex = event.row; 
                 
              var dialog = new IxDialog({
                id : 'confirmRemoval',
                contentURL : url,
                centered : true,
                showOk : true,  
                showCancel : true,
                autoEvaluateSize: true,
                title : '<fmt:message key="settings.educationTypes.educationTypeArchiveConfirmDialogTitle"/>',
                okLabel : '<fmt:message key="settings.educationTypes.educationTypeArchiveConfirmDialogOkLabel"/>',
                cancelLabel : '<fmt:message key="settings.educationTypes.educationTypeArchiveConfirmDialogCancelLabel"/>'
              });
            
              dialog.addDialogListener(function(event) {
                switch (event.name) {
                  case 'okClick':
                    JSONRequest.request("settings/archiveeducationtype.json", {
                      parameters: {
                      educationTypeId: educationTypeId
                      },
                      onSuccess: function (jsonResponse) {
                        getIxTableById('educationTypesTable').deleteRow(archivedRowIndex);
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
            tooltip: '<fmt:message key="settings.educationTypes.educationTypesTableRemoveTooltip"/>',
            onclick: function (event) {
              event.tableObject.deleteRow(event.row);
              if (event.tableObject.getRowCount() == 0) {
                $('noEducationTypesAddedMessageContainer').setStyle({
                  display: ''
                });
              }
            },
            paramName: 'removeButton',
            hidden: true
          }, {
            dataType: 'hidden',
            paramName: 'educationTypeId'
          }, {
            dataType: 'hidden',
            paramName: 'modified'
          }]
        });

        var rowIndex;
        <c:forEach var="educationType" items="${educationTypes}">
          rowIndex = educationTypesTable.addRow([
            '',
            '${fn:replace(educationType.name, "'", "\\'")}',
            '${fn:replace(educationType.code, "'", "\\'")}',
            '',
            '',
            ${educationType.id},
            0
          ]);
          educationTypesTable.showCell(rowIndex, educationTypesTable.getNamedColumnIndex('archiveButton'));
        </c:forEach>

        if (educationTypesTable.getRowCount() > 0) {
          $('noEducationTypesAddedMessageContainer').setStyle({
            display: 'none'
          });
        }
      }
        
    </script>
  </head>
  
  <body onload="onLoad(event);" ix:enabledrafting="true">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
    
    <h1 class="genericPageHeader"><fmt:message key="settings.educationTypes.pageTitle"/></h1>
    
    <div id="manageEducationTypesFormContainer"> 
	    <div class="genericFormContainer"> 
	      <form action="saveeducationtypes.json" method="post" ix:jsonform="true" ix:useglasspane="true">
	  
	        <div class="tabLabelsContainer" id="tabs">
	          <a class="tabLabel" href="#manageEducationTypes">
	            <fmt:message key="settings.educationTypes.tabLabelEducationTypes"/>
	          </a>
	        </div>
	        
          <div id="manageEducationTypes" class="tabContentixTableFormattedData">
            <div class="genericTableAddRowContainer">
              <span class="genericTableAddRowLinkContainer" onclick="addEducationTypesTableRow();"><fmt:message key="settings.educationTypes.addEducationTypeLink"/></span>
            </div>
              
            <div id="noEducationTypesAddedMessageContainer" class="genericTableNotAddedMessageContainer">
              <span><fmt:message key="settings.educationTypes.noEducationTypesAddedPreFix"/> <span onclick="addeEucationTypesTableRow();" class="genericTableAddRowLink"><fmt:message key="settings.educationTypes.noEducationTypesAddedClickHereLink"/></span>.</span>
            </div>
            
            <div id="educationTypesTable"></div>
          </div>
	  
          <div class="genericFormSubmitSectionOffTab">
            <input type="submit" class="formvalid" value="<fmt:message key="settings.educationTypes.saveButton"/>">
          </div>

	      </form>
	    </div>
	  </div>
    
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>