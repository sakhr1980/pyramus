<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
  <head>
    <title><fmt:message key="settings.courseStates.pageTitle"/></title>

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

      function addCourseStatesTableRow() {
        var table = getIxTableById('courseStatesTable');
        var rowIndex = table.addRow(['', false, '', '', '', -1]);
        for (var i = 0; i < table.getColumnCount(); i++) {
          table.setCellEditable(rowIndex, i, true);
        }
        
        if (rowIndex == 0) {
          table.setCellValue(0, table.getNamedColumnIndex("initialState"), true);
        }
        
        $('noCourseStatesAddedMessageContainer').setStyle({
          display: 'none'
        });
        table.showCell(rowIndex, table.getNamedColumnIndex('removeButton'));
      }
      
      function onLoad(event) {
        tabControl = new IxProtoTabs($('tabs'));

        var courseStatesTable = new IxTable($('courseStatesTable'), {
          id : "courseStatesTable",
          columns : [ {
            left: 8,
            width: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
            tooltip: '<fmt:message key="settings.courseStates.courseStatesTableEditTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              for (var i = 0; i < table.getColumnCount(); i++) {
                table.setCellEditable(event.row, i, table.isCellEditable(event.row, i) == false);
              }
            }
          }, {
            header : '<fmt:message key="settings.courseStates.courseStatesTableInitialStateHeader"/>',
            left : 38,
            width : 80,
            dataType: 'checkbox',
            editable: false,
            paramName: 'initialState'
          }, {
            header : '<fmt:message key="settings.courseStates.courseStatesTableNameHeader"/>',
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
            tooltip: '<fmt:message key="settings.courseStates.courseStatesTableArchiveTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var courseStateId = table.getCellValue(event.row, table.getNamedColumnIndex('courseStateId'));
              var courseStateName = table.getCellValue(event.row, table.getNamedColumnIndex('name'));
              var url = GLOBAL_contextPath + "/simpledialog.page?localeId=settings.courseStates.courseStateArchiveConfirmDialogContent&localeParams=" + encodeURIComponent(courseStateName);

              archivedRowIndex = event.row; 
                 
              var dialog = new IxDialog({
                id : 'confirmRemoval',
                contentURL : url,
                centered : true,
                showOk : true,  
                showCancel : true,
                autoEvaluateSize: true,
                title : '<fmt:message key="settings.courseStates.courseStateArchiveConfirmDialogTitle"/>',
                okLabel : '<fmt:message key="settings.courseStates.courseStateArchiveConfirmDialogOkLabel"/>',
                cancelLabel : '<fmt:message key="settings.courseStates.courseStateArchiveConfirmDialogCancelLabel"/>'
              });
            
              dialog.addDialogListener(function(event) {
                switch (event.name) {
                  case 'okClick':
                    JSONRequest.request("settings/archivecoursestate.json", {
                      parameters: {
                        courseStateId: courseStateId
                      },
                      onSuccess: function (jsonResponse) {
                        getIxTableById('courseStatesTable').deleteRow(archivedRowIndex);
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
            tooltip: '<fmt:message key="settings.courseStates.courseStatesTableRemoveTooltip"/>',
            onclick: function (event) {
              event.tableObject.deleteRow(event.row);
              if (event.tableObject.getRowCount() == 0) {
                $('noCourseStatesAddedMessageContainer').setStyle({
                  display: ''
                });
              }
            },
            paramName: 'removeButton',
            hidden: true
          }, {
            dataType: 'hidden',
            paramName: 'courseStateId'
          }]
        });
        
        courseStatesTable.addListener("cellValueChange", function (event) {
          if ((event.value == true) && !this._settingValue) {
            var initialStateColumn = event.tableComponent.getNamedColumnIndex("initialState");
            
            if (initialStateColumn == event.column) {
              this._settingValue = true;
              try {
                for (var i = 0, l = event.tableComponent.getRowCount(); i < l; i++) {
                  if (i != event.row) {
                    event.tableComponent.setCellValue(i, initialStateColumn, false);
                  } else {
                    event.tableComponent.setCellValue(i, initialStateColumn, true);
                  }
                }
              } finally {
                this._settingValue = false;
              }
            }
          }
        });

        var rowIndex;
        <c:forEach var="courseState" items="${courseStates}">
          rowIndex = courseStatesTable.addRow([
            '',
            ${courseState eq initalCourseState},
            '${fn:replace(courseState.name, "'", "\\'")}',
            '',
            '',
            ${courseState.id}
          ]);
          courseStatesTable.showCell(rowIndex, courseStatesTable.getNamedColumnIndex('archiveButton'));
        </c:forEach>

        if (courseStatesTable.getRowCount() > 0) {
          $('noCourseStatesAddedMessageContainer').setStyle({
            display: 'none'
          });
        }
      }
        
    </script>
  </head>
  
  <body onload="onLoad(event);" ix:enabledrafting="true">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
    
    <h1 class="genericPageHeader"><fmt:message key="settings.courseStates.pageTitle"/></h1>
    
    <div id="manageCourseStatesFormContainer"> 
	    <div class="genericFormContainer"> 
	      <form action="savecoursestates.json" method="post" ix:jsonform="true" ix:useglasspane="true">
	  
	        <div class="tabLabelsContainer" id="tabs">
	          <a class="tabLabel" href="#manageCourseStates">
	            <fmt:message key="settings.courseStates.tabLabelCourseStates"/>
	          </a>
	        </div>
          
          <div id="manageCourseStates" class="tabContentixTableFormattedData">
            <div class="genericTableAddRowContainer">
              <span class="genericTableAddRowLinkContainer" onclick="addCourseStatesTableRow();"><fmt:message key="settings.courseStates.addCourseStateLink"/></span>
            </div>
              
            <div id="noCourseStatesAddedMessageContainer" class="genericTableNotAddedMessageContainer">
              <span><fmt:message key="settings.courseStates.noCourseStatesAddedPreFix"/> <span onclick="addCourseStatesTableRow();" class="genericTableAddRowLink"><fmt:message key="settings.courseStates.noCourseStatesAddedClickHereLink"/></span>.</span>
            </div>
            
            <div id="courseStatesTable"></div>
          </div>
	  
          <div class="genericFormSubmitSectionOffTab">
            <input type="submit" class="formvalid" value="<fmt:message key="settings.courseStates.saveButton"/>">
          </div>

	      </form>
	    </div>
	  </div>
    
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>