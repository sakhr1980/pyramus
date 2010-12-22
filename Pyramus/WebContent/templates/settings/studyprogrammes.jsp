<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
  <head>
    <title><fmt:message key="settings.studyProgrammes.pageTitle"/></title>

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

      function addStudyProgrammesTableRow() {
        var table = getIxTableById('studyProgrammesTable');
        var rowIndex = table.addRow(['', '', '', '', '', '', -1, 1]);
        for (var i = 0; i < table.getColumnCount(); i++) {
          table.setCellEditable(rowIndex, i, true);
        }
        $('noStudyProgrammesAddedMessageContainer').setStyle({
          display: 'none'
        });
        table.showCell(rowIndex, table.getNamedColumnIndex('removeButton'));
      }
      
      function onLoad(event) {
        tabControl = new IxProtoTabs($('tabs'));

        var studyProgrammeTable = new IxTable($('studyProgrammesTableContainer'), {
          id : "studyProgrammesTable",
          columns : [ {
            left: 8,
            width: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
            tooltip: '<fmt:message key="settings.studyProgrammes.studyProgrammesTableEditTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              for (var i = 0; i < table.getColumnCount(); i++) {
                table.setCellEditable(event.row, i, table.isCellEditable(event.row, i) == false);
              }
              table.setCellValue(event.row, table.getNamedColumnIndex('modified'), 1);
            }
          }, {
            header : '<fmt:message key="settings.studyProgrammes.studyProgrammesTableNameHeader"/>',
            left : 38,
            width : 300,
            dataType: 'text',
            editable: false,
            paramName: 'name',
            required: true
          }, {
            header : '<fmt:message key="settings.studyProgrammes.studyProgrammesTableCategoryHeader"/>',
            width: 200,
            left : 346,
            dataType: 'select',
            editable: false,
            paramName: 'category',
            options: [
              <c:forEach var="category" items="${categories}" varStatus="vs">
                {text: "${category.name}", value: ${category.id}}
                <c:if test="${not vs.last}">,</c:if>
              </c:forEach>
            ]
          }, {
            header : '<fmt:message key="settings.studyProgrammes.studyProgrammesTableCodeHeader"/>',
            left: 554,
            right: 44,
            dataType: 'text',
            editable: false,
            paramName: 'code'
          }, {
            right: 8,
            width: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/edit-delete.png',
            tooltip: '<fmt:message key="settings.studyProgrammes.studyProgrammesTableArchiveTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var studyProgrammeId = table.getCellValue(event.row, table.getNamedColumnIndex('studyProgrammeId'));
              var studyProgrammeName = table.getCellValue(event.row, table.getNamedColumnIndex('name'));
              var url = GLOBAL_contextPath + "/simpledialog.page?localeId=settings.studyProgrammes.studyProgrammeArchiveConfirmDialogContent&localeParams=" + encodeURIComponent(studyProgrammeName);

              archivedRowIndex = event.row; 
                 
              var dialog = new IxDialog({
                id : 'confirmRemoval',
                contentURL : url,
                centered : true,
                showOk : true,  
                showCancel : true,
                autoEvaluateSize: true,
                title : '<fmt:message key="settings.studyProgrammes.studyProgrammeArchiveConfirmDialogTitle"/>',
                okLabel : '<fmt:message key="settings.studyProgrammes.studyProgrammeArchiveConfirmDialogOkLabel"/>',
                cancelLabel : '<fmt:message key="settings.studyProgrammes.studyProgrammeArchiveConfirmDialogCancelLabel"/>'
              });
            
              dialog.addDialogListener(function(event) {
                switch (event.name) {
                  case 'okClick':
                    JSONRequest.request("settings/archivestudyprogramme.json", {
                      parameters: {
                        studyProgrammeId: studyProgrammeId
                      },
                      onSuccess: function (jsonResponse) {
                        getIxTableById('studyProgrammesTable').deleteRow(archivedRowIndex);
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
            tooltip: '<fmt:message key="settings.studyProgrammes.studyProgrammesTableRemoveTooltip"/>',
            onclick: function (event) {
              event.tableObject.deleteRow(event.row);
              if (event.tableObject.getRowCount() == 0) {
                $('noStudyProgrammesAddedMessageContainer').setStyle({
                  display: ''
                });
              }
            },
            paramName: 'removeButton',
            hidden: true
          }, {
            dataType: 'hidden',
            paramName: 'studyProgrammeId'
          }, {
            dataType: 'hidden',
            paramName: 'modified'
          }]
        });

        var rowIndex;
        <c:forEach var="studyProgramme" items="${studyProgrammes}">
          rowIndex = studyProgrammeTable.addRow([
            '',
            '${fn:replace(studyProgramme.name, "'", "\\'")}',
            ${studyProgramme.category.id},
            '${fn:replace(studyProgramme.code, "'", "\\'")}',
            '',
            '',
            ${studyProgramme.id},
            0
          ]);
          studyProgrammeTable.showCell(rowIndex, studyProgrammeTable.getNamedColumnIndex('archiveButton'));
        </c:forEach>

        if (studyProgrammeTable.getRowCount() > 0) {
          $('noStudyProgrammesAddedMessageContainer').setStyle({
            display: 'none'
          });
        }
      }
        
    </script>
  </head>
  
  <body onload="onLoad(event);" ix:enabledrafting="true">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
    
    <h1 class="genericPageHeader"><fmt:message key="settings.studyProgrammes.pageTitle"/></h1>
    
    <div id="manageStudyProgrammesFormContainer"> 
	    <div class="genericFormContainer"> 
	      <form action="savestudyprogrammes.json" method="post" ix:jsonform="true" ix:useglasspane="true">
	  
	        <div class="tabLabelsContainer" id="tabs">
	          <a class="tabLabel" href="#manageStudyProgrammes">
	            <fmt:message key="settings.studyProgrammes.tabLabelStudyProgrammes"/>
	          </a>
	        </div>
	        
          <div id="manageStudyProgrammes" class="tabContentixTableFormattedData">
            <div class="genericTableAddRowContainer">
              <span class="genericTableAddRowLinkContainer" onclick="addStudyProgrammesTableRow();"><fmt:message key="settings.studyProgrammes.addStudyProgrammeLink"/></span>
            </div>
              
            <div id="noStudyProgrammesAddedMessageContainer" class="genericTableNotAddedMessageContainer">
              <span>
               <fmt:message key="settings.studyProgrammes.noStudyProgrammesAddedPrefix"/> <span onclick="addStudyProgrammesTableRow();" class="genericTableAddRowLink"> <fmt:message key="settings.studyProgrammes.noStudyProgrammesAddedClickHereLink"/></span>.</span>
            </div>
            
            <div id="studyProgrammesTableContainer"></div>
          </div>
	  
          <div class="genericFormSubmitSectionOffTab">
            <input type="submit" class="formvalid" value="<fmt:message key="settings.studyProgrammes.saveButton"/>">
          </div>

	      </form>
	    </div>
	  </div>
    
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>