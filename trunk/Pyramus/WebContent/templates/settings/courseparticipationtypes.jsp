<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
  <head>
    <title><fmt:message key="settings.courseParticipationTypes.pageTitle"/></title>

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

      function addCourseParticipationTypesTableRow() {
        var table = getIxTableById('courseParticipationTypesTable');
        var rowIndex = table.addRow(['', false, '', '', '', -1]);
        for (var i = 0; i < table.getColumnCount(); i++) {
          table.setCellEditable(rowIndex, i, true);
        }
        
        if (rowIndex == 0) {
          table.setCellValue(0, table.getNamedColumnIndex("initialType"), true);
        }
        
        $('noCourseParticipationTypesAddedMessageContainer').setStyle({
          display: 'none'
        });
        table.showCell(rowIndex, table.getNamedColumnIndex('removeButton'));
        table.hideCell(rowIndex, table.getNamedColumnIndex('archiveButton'));
      }
      
      function onLoad(event) {
        tabControl = new IxProtoTabs($('tabs'));

        var courseParticipationTypesTable = new IxTable($('courseParticipationTypesTable'), {
          id : "courseParticipationTypesTable",
          columns : [ {
            left: 8,
            width: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
            tooltip: '<fmt:message key="settings.courseParticipationTypes.courseParticipationTypesTableEditTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              for (var i = 0; i < table.getColumnCount(); i++) {
                table.setCellEditable(event.row, i, table.isCellEditable(event.row, i) == false);
              }
            }
          }, {
            header : '<fmt:message key="settings.courseParticipationTypes.courseParticipationTypesTableInitialTypeHeader"/>',
            left : 38,
            width : 80,
            dataType: 'checkbox',
            editable: false,
            paramName: 'initialType'
          }, {
            header : '<fmt:message key="settings.courseParticipationTypes.courseParticipationTypesTableNameHeader"/>',
            left: 234,
            right: 46,
            dataType: 'text',
            editable: false,
            paramName: 'name',
            required: true
          }, {
            right: 8,
            width: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/edit-delete.png',
            tooltip: '<fmt:message key="settings.courseParticipationTypes.courseParticipationTypesTableArchiveTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var courseParticipationTypeId = table.getCellValue(event.row, table.getNamedColumnIndex('courseParticipationTypeId'));
              var courseParticipationTypeName = table.getCellValue(event.row, table.getNamedColumnIndex('name'));
              var url = GLOBAL_contextPath + "/simpledialog.page?localeId=settings.courseParticipationTypes.courseParticipationTypeArchiveConfirmDialogContent&localeParams=" + encodeURIComponent(courseParticipationTypeName);

              archivedRowIndex = event.row; 
                 
              var dialog = new IxDialog({
                id : 'confirmRemoval',
                contentURL : url,
                centered : true,
                showOk : true,  
                showCancel : true,
                autoEvaluateSize: true,
                title : '<fmt:message key="settings.courseParticipationTypes.courseParticipationTypeArchiveConfirmDialogTitle"/>',
                okLabel : '<fmt:message key="settings.courseParticipationTypes.courseParticipationTypeArchiveConfirmDialogOkLabel"/>',
                cancelLabel : '<fmt:message key="settings.courseParticipationTypes.courseParticipationTypeArchiveConfirmDialogCancelLabel"/>'
              });
            
              dialog.addDialogListener(function(event) {
                switch (event.name) {
                  case 'okClick':
                    JSONRequest.request("settings/archivecourseparticipationtype.json", {
                      parameters: {
                        courseParticipationTypeId: courseParticipationTypeId
                      },
                      onSuccess: function (jsonResponse) {
                        getIxTableById('courseParticipationTypesTable').deleteRow(archivedRowIndex);
                        saveFormDraft();
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
            tooltip: '<fmt:message key="settings.courseParticipationTypes.courseParticipationTypesTableRemoveTooltip"/>',
            onclick: function (event) {
              event.tableObject.deleteRow(event.row);
              if (event.tableObject.getRowCount() == 0) {
                $('noCourseParticipationTypesAddedMessageContainer').setStyle({
                  display: ''
                });
              }
            },
            paramName: 'removeButton',
            hidden: true
          }, {
            dataType: 'hidden',
            paramName: 'courseParticipationTypeId'
          }]
        });
        
        courseParticipationTypesTable.addListener("cellValueChange", function (event) {
          if ((event.value == true) && !this._settingValue) {
            var initialTypeColumn = event.tableComponent.getNamedColumnIndex("initialType");
            
            if (initialTypeColumn == event.column) {
              this._settingValue = true;
              try {
                for (var i = 0, l = event.tableComponent.getRowCount(); i < l; i++) {
                  if (i != event.row) {
                    event.tableComponent.setCellValue(i, initialTypeColumn, false);
                  } else {
                    event.tableComponent.setCellValue(i, initialTypeColumn, true);
                  }
                }
              } finally {
                this._settingValue = false;
              }
            }
          }
        });

        var rows = new Array();
        <c:forEach var="courseParticipationType" items="${courseParticipationTypes}">
          rows.push([
            '',
            ${courseParticipationType eq initialCourseParticipationType},
            '${fn:escapeXml(courseParticipationType.name)}',
            '',
            '',
            ${courseParticipationType.id}
          ]);
        </c:forEach>
        
        courseParticipationTypesTable.addRows(rows);

        if (courseParticipationTypesTable.getRowCount() > 0) {
          $('noCourseParticipationTypesAddedMessageContainer').setStyle({
            display: 'none'
          });
        }
      }
        
    </script>
  </head>
  
  <body onload="onLoad(event);" ix:enabledrafting="true">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
    
    <h1 class="genericPageHeader"><fmt:message key="settings.courseParticipationTypes.pageTitle"/></h1>
    
    <div id="manageCourseParticipationTypesFormContainer"> 
	    <div class="genericFormContainer"> 
	      <form action="savecourseparticipationtypes.json" method="post" ix:jsonform="true" ix:useglasspane="true">
	  
	        <div class="tabLabelsContainer" id="tabs">
	          <a class="tabLabel" href="#manageCourseParticipationTypes">
	            <fmt:message key="settings.courseParticipationTypes.tabLabelCourseParticipationTypes"/>
	          </a>
	        </div>
          
          <div id="manageCourseParticipationTypes" class="tabContentixTableFormattedData">
            <div class="genericTableAddRowContainer">
              <span class="genericTableAddRowLinkContainer" onclick="addCourseParticipationTypesTableRow();"><fmt:message key="settings.courseParticipationTypes.addCourseParticipationTypeLink"/></span>
            </div>
              
            <div id="noCourseParticipationTypesAddedMessageContainer" class="genericTableNotAddedMessageContainer">
              <span><fmt:message key="settings.courseParticipationTypes.noCourseParticipationTypesAddedPreFix"/> <span onclick="addCourseParticipationTypesTableRow();" class="genericTableAddRowLink"><fmt:message key="settings.courseParticipationTypes.noCourseParticipationTypesAddedClickHereLink"/></span>.</span>
            </div>
            
            <div id="courseParticipationTypesTable"></div>
          </div>
	  
          <div class="genericFormSubmitSectionOffTab">
            <input type="submit" class="formvalid" value="<fmt:message key="settings.courseParticipationTypes.saveButton"/>">
          </div>

	      </form>
	    </div>
	  </div>
    
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>