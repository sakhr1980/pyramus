<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
  <head>
    <title><fmt:message key="settings.educationSubtypes.pageTitle"/></title>

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

      function addEducationSubtypesTableRow() {
        var table = getIxTableById('educationSubtypesTable');
        var rowIndex = table.addRow(['', '', '', '', '', '', -1, 1]);
        var educationTypeColumn = table.getNamedColumnIndex('educationTypeId');
        for (var i = 0; i < table.getColumnCount(); i++) {
          table.setCellEditable(rowIndex, i, true);
        }
        $('noEducationSubtypesAddedMessageContainer').setStyle({
          display: 'none'
        });
        table.showCell(rowIndex, table.getNamedColumnIndex('removeButton'));
        table.hideCell(rowIndex, table.getNamedColumnIndex('archiveButton'));
      }
      
      function onLoad(event) {
        tabControl = new IxProtoTabs($('tabs'));

        var educationSubtypesTable = new IxTable($('educationSubtypesTable'), {
          id : "educationSubtypesTable",
          columns : [ {
            left: 8,
            width: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
            tooltip: '<fmt:message key="settings.educationSubtypes.educationSubtypesTableEditTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var educationTypeColumn = table.getNamedColumnIndex('educationTypeId');
              var existingSubtype = table.getCellValue(event.row, table.getNamedColumnIndex('educationSubtypeId')) != -1;
              for (var i = 0; i < table.getColumnCount(); i++) {
                if (!existingSubtype || i != educationTypeColumn) {
                  table.setCellEditable(event.row, i, table.isCellEditable(event.row, i) == false);
                }
              }
              table.setCellValue(event.row, table.getNamedColumnIndex('modified'), 1);
            }
          }, {
            header : '<fmt:message key="settings.educationSubtypes.educationSubtypesTableEducationTypeHeader"/>',
            left : 38,
            width: 300,
            dataType : 'select',
            editable: false,
            paramName: 'educationTypeId',
            options: [
              <c:forEach var="educationType" items="${educationTypes}" varStatus="vs">
                {text: "${fn:escapeXml(educationType.name)}", value: ${educationType.id}}
                <c:if test="${not vs.last}">,</c:if>
              </c:forEach>
            ]
          }, {
            header : '<fmt:message key="settings.educationSubtypes.educationSubtypesTableNameHeader"/>',
            left : 338,
            width : 300,
            dataType: 'text',
            editable: false,
            paramName: 'name',
            required: true
          }, {
            header : '<fmt:message key="settings.educationSubtypes.educationSubtypesTableCodeHeader"/>',
            left : 646,
            right: 46,
            dataType: 'text',
            editable: false,
            paramName: 'code',
            required: true
          }, {
            right: 8,
            width: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/edit-delete.png',
            tooltip: '<fmt:message key="settings.educationSubtypes.educationSubtypesTableArchiveTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var educationSubtypeId = table.getCellValue(event.row, table.getNamedColumnIndex('educationSubtypeId'));
              var educationSubtypeName = table.getCellValue(event.row, table.getNamedColumnIndex('name'));
              var url = GLOBAL_contextPath + "/simpledialog.page?localeId=settings.educationSubtypes.educationSubtypeArchiveConfirmDialogContent&localeParams=" + encodeURIComponent(educationSubtypeName);

              archivedRowIndex = event.row; 
                 
              var dialog = new IxDialog({
                id : 'confirmRemoval',
                contentURL : url,
                centered : true,
                showOk : true,  
                showCancel : true,
                autoEvaluateSize: true,
                title : '<fmt:message key="settings.educationSubtypes.educationSubtypeArchiveConfirmDialogTitle"/>',
                okLabel : '<fmt:message key="settings.educationSubtypes.educationSubtypeArchiveConfirmDialogOkLabel"/>',
                cancelLabel : '<fmt:message key="settings.educationSubtypes.educationSubtypeArchiveConfirmDialogCancelLabel"/>'
              });
            
              dialog.addDialogListener(function(event) {
                switch (event.name) {
                  case 'okClick':
                    JSONRequest.request("settings/archiveeducationsubtype.json", {
                      parameters: {
                      educationSubtypeId: educationSubtypeId
                      },
                      onSuccess: function (jsonResponse) {
                        getIxTableById('educationSubtypesTable').deleteRow(archivedRowIndex);
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
            tooltip: '<fmt:message key="settings.educationSubtypes.educationSubtypesTableRemoveTooltip"/>',
            onclick: function (event) {
              event.tableObject.deleteRow(event.row);
              if (event.tableObject.getRowCount() == 0) {
                $('noEducationSubtypesAddedMessageContainer').setStyle({
                  display: ''
                });
              }
            },
            paramName: 'removeButton',
            hidden: true
          }, {
            dataType: 'hidden',
            paramName: 'educationSubtypeId'
          }, {
            dataType: 'hidden',
            paramName: 'modified'
          }]
        });

        var rows = new Array();
        <c:forEach var="educationType" items="${educationTypes}">
          <c:forEach var="educationSubtype" items="${educationType.unarchivedSubtypes}">
            rows.push([
              '',
              ${educationType.id},
              '${fn:escapeXml(educationSubtype.name)}',
              '${fn:escapeXml(educationSubtype.code)}',
              '',
              '',
              ${educationSubtype.id},
              0 
            ]);
          </c:forEach>
        </c:forEach>
        
        educationSubtypesTable.addRows(rows);
        
        if (educationSubtypesTable.getRowCount() > 0) {
          $('noEducationSubtypesAddedMessageContainer').setStyle({
            display: 'none'
          });
        }
      }
        
    </script>
  </head>
  
  <body onload="onLoad(event);" ix:enabledrafting="true">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
    
    <h1 class="genericPageHeader"><fmt:message key="settings.educationSubtypes.pageTitle"/></h1>
    
    <div id="manageEducationSubtypesFormContainer"> 
	    <div class="genericFormContainer"> 
	      <form action="saveeducationsubtypes.json" method="post" ix:jsonform="true" ix:useglasspane="true">
	  
	        <div class="tabLabelsContainer" id="tabs">
	          <a class="tabLabel" href="#manageEducationSubtypes">
	            <fmt:message key="settings.educationSubtypes.tabLabelEducationSubtypes"/>
	          </a>
	        </div>
	        
          <div id="manageEducationSubtypes" class="tabContentixTableFormattedData">
            <div class="genericTableAddRowContainer">
              <span class="genericTableAddRowLinkContainer" onclick="addEducationSubtypesTableRow();"><fmt:message key="settings.educationSubtypes.addEducationSubtypeLink"/></span>
            </div>
              
            <div id="noEducationSubtypesAddedMessageContainer" class="genericTableNotAddedMessageContainer">
              <span><fmt:message key="settings.educationSubtypes.noEducationSubtypesAddedPreFix"/> <span onclick="addEducationSubtypesTableRow();" class="genericTableAddRowLink"><fmt:message key="settings.educationSubtypes.noEducationSubtypesAddedClickHereLink"/></span>.</span>
            </div>
            
            <div id="educationSubtypesTable"></div>
          </div>
	  
          <div class="genericFormSubmitSectionOffTab">
            <input type="submit" class="formvalid" value="<fmt:message key="settings.educationSubtypes.saveButton"/>">
          </div>

	      </form>
	    </div>
	  </div>
    
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>