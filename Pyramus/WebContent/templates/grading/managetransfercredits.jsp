<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
  <head>
    <title><fmt:message key="grading.manageTransferCredits.pageTitle"/></title>

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

      function addTransferCreditsTableRow() {
        var table = getIxTableById('transferCreditsTable');
        
        rowIndex = table.addRow(['', '', -1, -1, 0, -1, 0, -1, -1, ${loggedUserId}, new Date().getTime(), '', '', -1], true);
        $('noTransferCreditsAddedMessageContainer').setStyle({
          display: 'none'
        });
      }
      
      function onAddTemplateButtonClick(event) {
        Event.stop(event);
        
        var transferCreditTemplateId = $('transferCreditTemplateSelect').value;
        loadTransferCreditTemplates(transferCreditTemplateId);
      }
      
      function loadTransferCreditTemplates(transferCreditTemplateId) {
        var glassPane = new IxGlassPane(document.body, { });
        glassPane.show();

        JSONRequest.request('grading/loadtransfercredittemplates.json', {
          parameters: {
            transferCreditTemplateId: transferCreditTemplateId
          },
          onSuccess: function (jsonResponse) {
            var table = getIxTableById('transferCreditsTable');
            var results = jsonResponse.results;
            var rowDatas = new Array();
            
            for (var i = 0, l = results.length; i < l; i++) {
              var template = results[i];
              
              rowDatas.push([
	              '',
	              template.courseName,
	              template.courseOptionality,                                  
	              template.courseNumber,
	              -1,           
	              template.subjectId,         
	              template.courseUnits, 
	              template.courseUnit,          
	              -1,                              
                ${loggedUserId},           
	              new Date().getTime(), 
	              '',
	              '',
	              -1
	            ]);
            }
            
            table.addRows(rowDatas, true);
            
            var userColumnIndex = table.getNamedColumnIndex('user');
            var subjectColumnIndex = table.getNamedColumnIndex('subject');
            
            for (var i = table.getRowCount() - 1 , l = table.getRowCount() - 1 - results.length; i > l; i--) {
              var template = results[i];
              
              var userCellEditor = table.getCellEditor(i, userColumnIndex);
              var subjectCellEditor = table.getCellEditor(i, subjectColumnIndex);
              
              IxTableControllers.getController('autoComplete').setDisplayValue(userCellEditor, '${fn:replace(loggedUserName, "'", "\\'")}');
              IxTableControllers.getController('autoComplete').setDisplayValue(subjectCellEditor, template.subjectName);
            }
            
            if (table.getRowCount() > 0) {
              $('noTransferCreditsAddedMessageContainer').setStyle({
                display: 'none'
              });
            }
            
            glassPane.hide();
            delete glassPane;
          }
        });
      }
      
      function onLoad(event) {
        tabControl = new IxProtoTabs($('tabs'));

        var transferCreditsTable = new IxTable($('transferCreditsTable'), {
          id : "transferCreditsTable",
          columns : [ {
            left: 4,
            width: 22,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
            tooltip: '<fmt:message key="grading.manageTransferCredits.transferCreditsTableEditTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              for (var i = 0; i < table.getColumnCount(); i++) {
                table.setCellEditable(event.row, i, table.isCellEditable(event.row, i) == false);
              }
            }
          }, {
            header : '<fmt:message key="grading.manageTransferCredits.transferCreditsTableCourseNameHeader"/>',
            left: 4 + 22 + 4,
            right: 4 + 22 + 4 + 100 + 4 + 110 + 3 + 120 + 3 + 100 + 4 + 60 + 3 + 120 + 3 + 70 + 4 + 87 + 3 + 90 + 4,
            dataType: 'text',
            editable: false,
            paramName: 'courseName'
          }, {
            header : '<fmt:message key="grading.manageTransferCredits.transferCreditsTableCourseOptionalityHeader"/>',
            width: 90,
            right: 4 + 22 + 4 + 100 + 4 + 110 + 3 + 120 + 3 + 100 + 4 + 60 + 3 + 120 + 3 + 70 + 4 + 87 + 3,
            dataType: 'select',
            editable: false,
            overwriteColumnValues : true,
            paramName: 'courseOptionality',
            options: [
              {text: '<fmt:message key="grading.manageTransferCredits.transferCreditsTableCourseOptionalityOptional"/>', value: 'OPTIONAL'},
              {text: '<fmt:message key="grading.manageTransferCredits.transferCreditsTableCourseOptionalityMandatory"/>', value: 'MANDATORY'}
            ]
          }, {
            header : '<fmt:message key="grading.manageTransferCredits.transferCreditsTableCourseNumberHeader"/>',
            width : 87,
            right: 4 + 22 + 4 + 100 + 4 + 110 + 3 + 120 + 3 + 100 + 4 + 60 + 3 + 120 + 3 + 70 + 4,
            dataType: 'number',
            editable: false,
            paramName: 'courseNumber'
          }, {
            header : '<fmt:message key="grading.manageTransferCredits.transferCreditsTableGradeHeader"/>',
            width : 70,
            right: 4 + 22 + 4 + 100 + 4 + 110 + 3 + 120 + 3 + 100 + 4 + 60 + 3 + 120 + 3, 
            dataType: 'select',
            editable: false,
            paramName: 'grade',
            overwriteColumnValues : true,
            options: [
              <c:forEach var="gradingScale" items="${gradingScales}" varStatus="vs">
                {text: "${fn:replace(gradingScale.name, "'", "\\'")}", optionGroup: true, 
                  options: [
                    <c:forEach var="grade" items="${gradingScale.grades}" varStatus="vs2">
                      {text: "${fn:replace(grade.name, "'", "\\'")}", value: ${grade.id}}
                      <c:if test="${not vs2.last}">,</c:if>
                    </c:forEach>
                  ]
                } 
                <c:if test="${not vs.last}">,</c:if>
              </c:forEach>
            ] 
          }, {
            header : '<fmt:message key="grading.manageTransferCredits.transferCreditsTableSubjectHeader"/>',
            width : 120,
            right: 4 + 22 + 4 + 100 + 4 + 110 + 3 + 120 + 3 + 100 + 4 + 60 + 3,
            dataType: 'autoComplete',
            editable: false,
            overwriteColumnValues : true,
            paramName: 'subject',
            autoCompleteUrl: GLOBAL_contextPath + '/settings/subjectsautocomplete.binary',
            autoCompleteProgressUrl: '${pageContext.request.contextPath}/gfx/progress_small.gif'
          }, {
            header : '<fmt:message key="grading.manageTransferCredits.transferCreditsTableLengthHeader"/>',
            width : 60,
            right: 4 + 22 + 4 + 100 + 4 + 110 + 3 + 120 + 3 + 100 + 4,
            dataType: 'number',
            editable: false,
            overwriteColumnValues : true,
            paramName: 'courseLength'
          }, {
            header : '<fmt:message key="grading.manageTransferCredits.transferCreditsTableLengthUnitHeader"/>',
            width : 100,
            right: 4 + 22 + 4 + 100 + 4 + 110 + 3 + 120 + 3,
            dataType: 'select',
            editable: false,
            overwriteColumnValues : true,
            paramName: 'courseLengthUnit',
            options: [
              <c:forEach var="timeUnit" items="${timeUnits}" varStatus="vs">
                {text: "${fn:replace(timeUnit.name, "'", "\\'")}", value: ${timeUnit.id}}
                <c:if test="${not vs.last}">,</c:if>
              </c:forEach>
            ]            
          }, {
            header : '<fmt:message key="grading.manageTransferCredits.transferCreditsTableSchoolHeader"/>',
            width : 120,
            right: 4 + 22 + 4 + 100 + 4 + 110 + 3,
            dataType: 'autoComplete',
            editable: false,
            paramName: 'school',
            overwriteColumnValues : true,
            autoCompleteUrl: GLOBAL_contextPath + '/settings/schoolsautocomplete.binary',
            autoCompleteProgressUrl: '${pageContext.request.contextPath}/gfx/progress_small.gif'
          }, {
            header : '<fmt:message key="grading.manageTransferCredits.transferCreditsTableUserHeader"/>',
            width : 100,
            right: 4 + 22 + 4 + 110 + 4,
            dataType: 'autoComplete',
            editable: false,
            paramName: 'user',
            overwriteColumnValues : true,
            autoCompleteUrl: GLOBAL_contextPath + '/users/usersautocomplete.binary',
            autoCompleteProgressUrl: '${pageContext.request.contextPath}/gfx/progress_small.gif'
          }, {
            header : '<fmt:message key="grading.manageTransferCredits.transferCreditsTableDateHeader"/>',
            width : 110,
            right: 4 + 22 + 4,
            dataType: 'date',
            overwriteColumnValues : true,
            editable: false,
            paramName: 'date'
          }, {
            right: 4,
            width: 22,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/edit-delete.png',
            tooltip: '<fmt:message key="grading.manageTransferCredits.transferCreditsTableArchiveTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var gradeId = table.getCellValue(event.row, table.getNamedColumnIndex('gradeId'));
              var courseName = table.getCellValue(event.row, table.getNamedColumnIndex('courseName'));
              var url = GLOBAL_contextPath + "/simpledialog.page?localeId=grading.manageTransferCredits.transferCreditArchiveConfirmDialogContent&localeParams=" + encodeURIComponent(courseName);

              archivedRowIndex = event.row; 
                 
              var dialog = new IxDialog({
                id : 'confirmRemoval',
                contentURL : url,
                centered : true,
                showOk : true,  
                showCancel : true,
                autoEvaluateSize: true,
                title : '<fmt:message key="grading.manageTransferCredits.transferCreditArchiveConfirmDialogTitle"/>',
                okLabel : '<fmt:message key="grading.manageTransferCredits.transferCreditArchiveConfirmDialogOkLabel"/>',
                cancelLabel : '<fmt:message key="grading.manageTransferCredits.transferCreditArchiveConfirmDialogCancelLabel"/>'
              });
            
              dialog.addDialogListener(function(event) {
                switch (event.name) {
                  case 'okClick':
                    JSONRequest.request("grading/archivegrade.json", {
                      parameters: {
                        gradeId: gradeId
                      },
                      onSuccess: function (jsonResponse) {
                        getIxTableById('transferCreditsTable').deleteRow(archivedRowIndex);
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
            right: 4,
            width: 22,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/list-remove.png',
            tooltip: '<fmt:message key="grading.manageTransferCredits.transferCreditsTableRemoveTooltip"/>',
            onclick: function (event) {
              event.tableObject.deleteRow(event.row);
              if (event.tableObject.getRowCount() == 0) {
                $('noTransferCreditsAddedMessageContainer').setStyle({
                  display: ''
                });
              }
            },
            paramName: 'removeButton',
            hidden: false
          }, {
            dataType: 'hidden',
            paramName: 'creditId'
          }]
        });

        var subjectColumnIndex = transferCreditsTable.getNamedColumnIndex('subject');
        var schoolColumnIndex = transferCreditsTable.getNamedColumnIndex('school');
        var userColumnIndex = transferCreditsTable.getNamedColumnIndex('user');
        var removeButtonColumnIndex = transferCreditsTable.getNamedColumnIndex('removeButton');
        var archiveButtonColumnIndex = transferCreditsTable.getNamedColumnIndex('archiveButton');
        
        var rowIndex;
        <c:forEach var="transferCredit" items="${transferCredits}">
          rowIndex = transferCreditsTable.addRow([
            '',
            '${fn:replace(transferCredit.courseName, "'", "\\'")}',
            '${transferCredit.optionality}',                                  
            ${transferCredit.courseNumber},
            ${transferCredit.grade.id},           
            ${transferCredit.subject.id},         
            ${transferCredit.courseLength.units}, 
            ${transferCredit.courseLength.unit.id},          
            ${transferCredit.school.id},                     
            ${transferCredit.assessingUser.id},
            ${transferCredit.date.time},                     
            '',
            '',
            ${transferCredit.id}
          ]);
          
          transferCreditsTable.hideCell(rowIndex, removeButtonColumnIndex);
          transferCreditsTable.showCell(rowIndex, archiveButtonColumnIndex);
          IxTableControllers.getController('autoComplete').setDisplayValue(transferCreditsTable.getCellEditor(rowIndex, subjectColumnIndex), '${fn:replace(transferCredit.subject.name, "'", "\\'")}');
          IxTableControllers.getController('autoComplete').setDisplayValue(transferCreditsTable.getCellEditor(rowIndex, schoolColumnIndex), '${fn:replace(transferCredit.school.name, "'", "\\'")}');
          IxTableControllers.getController('autoComplete').setDisplayValue(transferCreditsTable.getCellEditor(rowIndex, userColumnIndex), '${fn:replace(transferCredit.assessingUser.fullName, "'", "\\'")}');
          
        </c:forEach>

        if (transferCreditsTable.getRowCount() > 0) {
          $('noTransferCreditsAddedMessageContainer').setStyle({
            display: 'none'
          });
        }
      }
        
    </script>
  </head>
  
  <body onload="onLoad(event);" ix:enabledrafting="true">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
    
    <h1 class="genericPageHeader"><fmt:message key="grading.manageTransferCredits.pageTitle"/></h1>
    
    <div id="manageTransferCreditsFormContainer"> 
	    <div class="genericFormContainer"> 
	      <form action="savetransfercredits.json" method="post" ix:jsonform="true" ix:useglasspane="true">
	        <input type="hidden" value="${student.id}" name="studentId"/>
	  
	        <div class="tabLabelsContainer" id="tabs">
	          <a class="tabLabel" href="#manageTransferCredits">
	            <fmt:message key="grading.manageTransferCredits.tabLabelTransferCredits"/>
	          </a>
	        </div>
          
          <div id="manageTransferCredits" class="tabContentixTableFormattedData">
          
            <div class="genericFormSection">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="grading.manageTransferCredits.templateTitle"/>
                <jsp:param name="helpLocale" value="grading.manageTransferCredits.templateHelp"/>
              </jsp:include>
              <select id="transferCreditTemplateSelect">
                <c:forEach var="transferCreditTemplate" items="${transferCreditTemplates}">
                   <option value="${transferCreditTemplate.id}">${transferCreditTemplate.name}</option>
                </c:forEach>
              </select>     
              
              <button onclick="onAddTemplateButtonClick(event);">
                <fmt:message key="grading.manageTransferCredits.addTemplateButton"/>
              </button>   
            </div>
          
            
            
            <div class="genericTableAddRowContainer">
              <span class="genericTableAddRowLinkContainer" onclick="addTransferCreditsTableRow();"><fmt:message key="grading.manageTransferCredits.addTransferCreditLink"/></span>
            </div>
              
            <div id="noTransferCreditsAddedMessageContainer" class="genericTableNotAddedMessageContainer">
              <span><fmt:message key="grading.manageTransferCredits.noTransferCreditsAddedPreFix"/> <span onclick="addTransferCreditsTableRow();" class="genericTableAddRowLink"><fmt:message key="grading.manageTransferCredits.noTransferCreditsAddedClickHereLink"/></span>.</span>
            </div>
            
            <div id="transferCreditsTable"></div>
          </div>
	  
          <div class="genericFormSubmitSectionOffTab">
            <input type="submit" class="formvalid" value="<fmt:message key="grading.manageTransferCredits.saveButton"/>">
          </div>

	      </form>
	    </div>
	  </div>
    
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>