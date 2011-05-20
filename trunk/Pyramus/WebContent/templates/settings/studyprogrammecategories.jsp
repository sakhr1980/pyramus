<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
  <head>
    <title><fmt:message key="settings.studyProgrammeCategories.pageTitle"/></title>

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

      function addStudyProgrammeCategoriesTableRow() {
        var table = getIxTableById('studyProgrammeCategoriesTable');
        var rowIndex = table.addRow(['', '', '', '', '', -1, 1]);
        for (var i = 0; i < table.getColumnCount(); i++) {
          table.setCellEditable(rowIndex, i, true);
        }
        $('noStudyProgrammeCategoriesAddedMessageContainer').setStyle({
          display: 'none'
        });
        table.showCell(rowIndex, table.getNamedColumnIndex('removeButton'));
        table.hideCell(rowIndex, table.getNamedColumnIndex('archiveButton'));
      }
      
      function onLoad(event) {
        tabControl = new IxProtoTabs($('tabs'));
        
        var studyProgrammeCategoriesTable = new IxTable($('studyProgrammeCategoriesTableContainer'), {
          id : "studyProgrammeCategoriesTable",
          columns : [ {
            left: 8,
            width: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
            tooltip: '<fmt:message key="settings.studyProgrammeCategories.studyProgrammeCategoriesTableEditTooltip"/>',
            onclick: function (event) {
              var table = event.tableComponent;
              for (var i = 0; i < table.getColumnCount(); i++) {
                table.setCellEditable(event.row, i, table.isCellEditable(event.row, i) == false);
              }
              table.setCellValue(event.row, table.getNamedColumnIndex('modified'), 1);
            }
          }, {
            header : '<fmt:message key="settings.studyProgrammeCategories.studyProgrammeCategoriesTableNameHeader"/>',
            left : 38,
            width : 300,
            dataType: 'text',
            editable: false,
            paramName: 'name',
            required: true
          }, {
            header : '<fmt:message key="settings.studyProgrammeCategories.educationTypeHeader"/>',
            left : 8 + 22 + 8 + 300 + 8,
            right : 8 + 22 + 8,
            dataType: 'select',
            editable: false,
            paramName: 'educationTypeId',
            options: [
              {text: "-", value: ''}<c:if test="${fn:length(educationTypes) gt 0}">,</c:if>
              <c:forEach var="educationType" items="${educationTypes}" varStatus="vs">
                {text: "${fn:escapeXml(educationType.name)}", value: ${educationType.id}}
                <c:if test="${not vs.last}">,</c:if>
              </c:forEach>
            ]
          }, {
            right: 8,
            width: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/edit-delete.png',
            tooltip: '<fmt:message key="settings.studyProgrammeCategories.studyProgrammeCategoriesTableArchiveTooltip"/>',
            onclick: function (event) {
              var table = event.tableComponent;
              var studyProgrammeCategoryId = table.getCellValue(event.row, table.getNamedColumnIndex('studyProgrammeCategoryId'));
              var studyProgrammeCategoryName = table.getCellValue(event.row, table.getNamedColumnIndex('name'));
              var url = GLOBAL_contextPath + "/simpledialog.page?localeId=settings.studyProgrammeCategories.studyProgrammeCategoryArchiveConfirmDialogContent&localeParams=" + encodeURIComponent(studyProgrammeCategoryName);

              archivedRowIndex = event.row; 
                 
              var dialog = new IxDialog({
                id : 'confirmRemoval',
                contentURL : url,
                centered : true,
                showOk : true,  
                showCancel : true,
                autoEvaluateSize: true,
                title : '<fmt:message key="settings.studyProgrammeCategories.studyProgrammeCategoryArchiveConfirmDialogTitle"/>',
                okLabel : '<fmt:message key="settings.studyProgrammeCategories.studyProgrammeCategoryArchiveConfirmDialogOkLabel"/>',
                cancelLabel : '<fmt:message key="settings.studyProgrammeCategories.studyProgrammeCategoryArchiveConfirmDialogCancelLabel"/>'
              });
            
              dialog.addDialogListener(function(event) {
                switch (event.name) {
                  case 'okClick':
                    JSONRequest.request("settings/archivestudyprogrammecategory.json", {
                      parameters: {
                        studyProgrammeCategory: studyProgrammeCategoryId
                      },
                      onSuccess: function (jsonResponse) {
                        getIxTableById('studyProgrammeCategoriesTable').deleteRow(archivedRowIndex);
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
            tooltip: '<fmt:message key="settings.studyProgrammeCategories.studyProgrammeCategoriesTableRemoveTooltip"/>',
            onclick: function (event) {
              event.tableComponent.deleteRow(event.row);
              if (event.tableComponent.getRowCount() == 0) {
                $('noStudyProgrammeCategoriesAddedMessageContainer').setStyle({
                  display: ''
                });
              }
            },
            paramName: 'removeButton',
            hidden: true
          }, {
            dataType: 'hidden',
            paramName: 'studyProgrammeCategoryId'
          }, {
            dataType: 'hidden',
            paramName: 'modified'
          }]
        });

        var rows = new Array();
        <c:forEach var="studyProgrammeCategory" items="${studyProgrammeCategories}">
          rows.push([
            '',
            '${fn:escapeXml(studyProgrammeCategory.name)}',
            '${studyProgrammeCategory.educationType.id}',
            '',
            '',
            ${studyProgrammeCategory.id},
            0
          ]);
        </c:forEach>
        studyProgrammeCategoriesTable.addRows(rows);
        
        if (studyProgrammeCategoriesTable.getRowCount() > 0) {
          $('noStudyProgrammeCategoriesAddedMessageContainer').setStyle({
            display: 'none'
          });
        }
      }
        
    </script>
  </head>
  
  <body onload="onLoad(event);" ix:enabledrafting="true">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
    
    <h1 class="genericPageHeader"><fmt:message key="settings.studyProgrammeCategories.pageTitle"/></h1>
    
    <div id="manageStudyProgrammesFormContainer"> 
	    <div class="genericFormContainer"> 
	      <form action="savestudyprogrammecategories.json" method="post" ix:jsonform="true" ix:useglasspane="true">
	  
	        <div class="tabLabelsContainer" id="tabs">
	          <a class="tabLabel" href="#manageStudyProgrammeCategories">
	            <fmt:message key="settings.studyProgrammeCategories.tabLabelStudyProgrammeCategories"/>
	          </a>
	        </div>
	        
          <div id="manageStudyProgrammeCategories" class="tabContentixTableFormattedData">
            <div class="genericTableAddRowContainer">
              <span class="genericTableAddRowLinkContainer" onclick="addStudyProgrammeCategoriesTableRow();"><fmt:message key="settings.studyProgrammeCategories.addStudyProgrammeCategoryLink"/></span>
            </div>
              
            <div id="noStudyProgrammeCategoriesAddedMessageContainer" class="genericTableNotAddedMessageContainer">
              <span>
               <fmt:message key="settings.studyProgrammeCategories.noStudyProgrammeCategoriesAddedPrefix"/> <span onclick="addStudyProgrammeCategoriesTableRow();" class="genericTableAddRowLink"> <fmt:message key="settings.studyProgrammeCategories.noStudyProgrammeCategoriesAddedClickHereLink"/></span>.</span>
            </div>
            
            <div id="studyProgrammeCategoriesTableContainer"></div>
          </div>
	  
          <div class="genericFormSubmitSectionOffTab">
            <input type="submit" class="formvalid" value="<fmt:message key="settings.studyProgrammeCategories.saveButton"/>">
          </div>

	      </form>
	    </div>
	  </div>
    
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>