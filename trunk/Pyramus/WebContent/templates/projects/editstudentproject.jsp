<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>
      <fmt:message key="projects.editStudentProject.pageTitle">
        <fmt:param value="${studentProject.student.fullName}"/>
        <fmt:param value="${studentProject.name}"/>
      </fmt:message>
    </title>
    <jsp:include page="/templates/generic/head_generic.jsp"></jsp:include>
    <jsp:include page="/templates/generic/ckeditor_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/dialog_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/scriptaculous_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/table_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonrequest_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonform_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/tabs_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/draftapi_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/validation_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/hovermenu_support.jsp"></jsp:include>

    <script type="text/javascript">
      function redoFilters() {
        var table1 = getIxTableById('modulesTable');
        var table2 = getIxTableById('coursesTable');
        var table3 = getIxTableById('outOfProjectCoursesTable');
        
        table1.applyFilters();
        table2.applyFilters();
        table3.applyFilters();
      }
    
      function checkModulesMessage() {
        var table = getIxTableById('modulesTable');
        var allMessageVisible = false;
        var noMessageVisible = false;
      
        if (table.getRowCount() > 0) {
          if (table.getVisibleRowCount() == 0) {
            allMessageVisible = true;
          }
        } else {
          noMessageVisible = true;
        }
        
        $('noModulesAddedMessageContainer').setStyle({
          display: noMessageVisible ? '' : 'none'
        });
      
        $('allModulesAddedMessageContainer').setStyle({
          display: allMessageVisible ? '' : 'none'
        });
        
        if (table.getVisibleRowCount() > 0) {
          $('editStudentProjectModulesTotalContainer').setStyle({
            display: ''
          });
          $('editStudentProjectModulesTotalValue').innerHTML = table.getVisibleRowCount(); 
        }
        else {
          $('editStudentProjectModulesTotalContainer').setStyle({
            display: 'none'
          });
        }
      }

      function checkCoursesMessage() {
        var coursesTable = getIxTableById('coursesTable');
        var outOfProjectCoursesTable = getIxTableById('outOfProjectCoursesTable');

        $('noCoursesAddedMessageContainer').setStyle({
          display: ((coursesTable.getVisibleRowCount() > 0) || (outOfProjectCoursesTable.getVisibleRowCount() > 0)) ? 'none' : ''
        });
        $('noOOPCoursesAddedMessageContainer').setStyle({
          display: ((coursesTable.getVisibleRowCount() > 0) || (outOfProjectCoursesTable.getVisibleRowCount() > 0)) ? 'none' : ''
        });
        
        if (coursesTable.getVisibleRowCount() > 0) {
          $('editStudentProjectCoursesTotalContainer').setStyle({
            display: ''
          });
          $('editStudentProjectCoursesTotalValue').innerHTML = coursesTable.getVisibleRowCount(); 
          $('coursesContainer').setStyle({
            display: ''
          });
        }
        else {
          $('editStudentProjectCoursesTotalContainer').setStyle({
            display: 'none'
          });
          $('coursesContainer').setStyle({
            display: 'none'
          });
        }

        if (outOfProjectCoursesTable.getVisibleRowCount() > 0) {
          $('editStudentProjectOOPCoursesTotalContainer').setStyle({
            display: ''
          });
          $('editStudentProjectOOPCoursesTotalValue').innerHTML = outOfProjectCoursesTable.getVisibleRowCount();
          $('oopCoursesContainer').setStyle({
            display: ''
          });
        }
        else {
          $('editStudentProjectOOPCoursesTotalContainer').setStyle({
            display: 'none'
          });
          $('oopCoursesContainer').setStyle({
            display: 'none'
          });
        }
      }
      
      function openSearchModulesDialog() {

        var selectedModules = new Array();
        var modulesTable = getIxTableById('modulesTable');
        for (var i = 0; i < modulesTable.getRowCount(); i++) {
          var moduleName = modulesTable.getCellValue(i, modulesTable.getNamedColumnIndex('name'));
          var moduleId = modulesTable.getCellValue(i, modulesTable.getNamedColumnIndex('moduleId'));
          selectedModules.push({
            name: moduleName,
            id: moduleId
          });
        }

        var dialog = new IxDialog({
          id : 'searchModulesDialog',
          contentURL : GLOBAL_contextPath + '/projects/searchmodulesdialog.page',
          centered : true,
          showOk : true,
          showCancel : true,
          title : '<fmt:message key="projects.searchModulesDialog.searchModulesDialog.dialogTitle"/>',
          okLabel : '<fmt:message key="projects.searchModulesDialog.okLabel"/>', 
          cancelLabel : '<fmt:message key="projects.searchModulesDialog.cancelLabel"/>' 
        });
      
        dialog.setSize("800px", "660px");
        dialog.addDialogListener(function(event) {
          var dlg = event.dialog;
          switch (event.name) {
            case 'okClick':
              var modulesTable = getIxTableById('modulesTable');
              var coursesTable = getIxTableById('coursesTable');
              var oopCoursesTable = getIxTableById('outOfProjectCoursesTable');
              modulesTable.detachFromDom();              
              for (var i = 0, len = event.results.modules.length; i < len; i++) {
                var moduleId = event.results.modules[i].id;
                var moduleName = event.results.modules[i].name;
                var index = getModuleTableModuleRowIndex(modulesTable, moduleId);
                if (index == -1) {
                  var rowNumber = modulesTable.addRow([moduleName, -1, 0, '', '', '', moduleId, -1]);
                  
                  var courseRowIndex = getCourseTableModuleRowIndex(coursesTable, moduleId);
                  if (courseRowIndex >= 0) {
                    redoFilters();
                  }
                }
              }
              modulesTable.reattachToDom();              
              
              checkModulesMessage();
              checkCoursesMessage();
            break;
          }
        });
        dialog.open();
      }
      
      function openSearchCoursesDialog() {
        var dialog = new IxDialog({
          id : 'searchCoursesDialog',
          contentURL : GLOBAL_contextPath + '/projects/searchstudentprojectcoursesdialog.page',
          centered : true,
          showOk : true,
          showCancel : true,
          title : '<fmt:message key="projects.searchStudentProjectCoursesDialog.dialogTitle"/>',
          okLabel : '<fmt:message key="projects.searchStudentProjectCoursesDialog.okLabel"/>', 
          cancelLabel : '<fmt:message key="projects.searchStudentProjectCoursesDialog.cancelLabel"/>' 
        });
        
        dialog.setSize("800px", "600px");
        dialog.addDialogListener(function(event) {
          var dlg = event.dialog;
          switch (event.name) {
            case 'okClick':
              var moduleTables = getIxTableById('modulesTable');
              var coursesTable = getIxTableById('coursesTable');
              var oopCoursesTable = getIxTableById('outOfProjectCoursesTable');
              coursesTable.detachFromDom();
              for (var i = 0, len = event.results.courses.length; i < len; i++) {
                var courseId = event.results.courses[i].id;
                var moduleId = event.results.courses[i].moduleId;
                var courseName = event.results.courses[i].name;
                var participationType = '<fmt:message key="projects.editStudentProject.unsavedStudentParticipationType"/>' ;
                var beginDate = event.results.courses[i].beginDate;
                var endDate = event.results.courses[i].endDate;
                
                var moduleTablesRow = getModuleTableModuleRowIndex(moduleTables, moduleId);
                var index = getCourseTableCourseRowIndex(coursesTable, courseId);
                if (index == -1) {
                  var c1row = coursesTable.addRow([
                    courseName,
                    participationType,
                    beginDate||'',
                    endDate||'',
                    'OPTIONAL',
                    '',
                    '',
                    moduleId,
                    courseId,
                    -1]);
                  var c2row = oopCoursesTable.addRow([
                     courseName,
                     participationType,
                     beginDate||'',
                     endDate||'',
                     'OPTIONAL',
                     '',
                     '',
                     moduleId,
                     courseId,
                     -1]);
                }
              }
              coursesTable.reattachToDom();
              
              redoFilters();
              checkModulesMessage();
              checkCoursesMessage();
            break;
          }
        });
        dialog.open();
      }
  
      function getCourseTableCourseRowIndex(table, courseId) {
        for (var i = 0; i < table.getRowCount(); i++) {
          var tableCourseId = table.getCellValue(i, table.getNamedColumnIndex('courseId'));
          if (tableCourseId == courseId) {
            return i;
          }
        }
        return -1;
      }
  
      function getModuleTableModuleRowIndex(table, moduleId) {
        for (var i = 0; i < table.getRowCount(); i++) {
          var tableModuleId = table.getCellValue(i, table.getNamedColumnIndex('moduleId'));
          if (tableModuleId == moduleId) {
            return i;
          }
        }
        return -1;
      }

      function getCourseTableModuleRowIndex(table, moduleId) {
        for (var i = 0; i < table.getRowCount(); i++) {
          var tableModuleId = table.getCellValue(i, table.getNamedColumnIndex('moduleId'));
          if (tableModuleId == moduleId) {
            return i;
          }
        }
        return -1;
      }

      function setupTags() {
        JSONRequest.request("tags/getalltags.json", {
          onSuccess: function (jsonResponse) {
            new Autocompleter.Local("tags", "tags_choices", jsonResponse.tags, {
              tokens: [',', '\n', ' ']
            });
          }
        });   
      }

      function onLoad(event) {
        var tabControl = new IxProtoTabs($('tabs'));
        setupTags();
        var modulesTable = new IxTable($('modulesTableContainer'), {
          id : "modulesTable",
          columns : [ {
            header : '<fmt:message key="projects.editStudentProject.moduleTableNameHeader"/>',
            left : 8,
            right : 8 + 22 + 8 + 22 + 8 + 22 + 8 + 100 + 8 + 100 + 8,
            dataType: 'text',
            editable: false,
            paramName: 'name',
            sortAttributes: {
              sortAscending: {
                toolTip: '<fmt:message key="generic.sort.ascending"/>',
                sortAction: IxTable_ROWSTRINGSORT 
              },
              sortDescending: {
                toolTip: '<fmt:message key="generic.sort.descending"/>',
                sortAction: IxTable_ROWSTRINGSORT
              }
            }
          }, {
            header : '<fmt:message key="projects.editStudentProject.moduleTableStudyTermHeader"/>',
            right : 8 + 22 + 8 + 22 + 8 + 22 + 8 + 100 + 8,
            width: 100,
            dataType : 'select',
            paramName: 'academicTerm',
            editable: true,
            options: [
              {text: ""}
              <c:if test="${not empty academicTerms}">,</c:if>
              <c:forEach var="academicTerm" items="${academicTerms}" varStatus="vs">
                {text: "${academicTerm.name}", value: ${academicTerm.id}}
                <c:if test="${not vs.last}">,</c:if>
              </c:forEach>
            ],
            sortAttributes: {
              sortAscending: {
                toolTip: '<fmt:message key="generic.sort.ascending"/>',
                sortAction: IxTable_ROWSELECTSORT 
              },
              sortDescending: {
                toolTip: '<fmt:message key="generic.sort.descending"/>',
                sortAction: IxTable_ROWSELECTSORT
              }
            },
            contextMenu: [
              {
                text: '<fmt:message key="generic.action.copyValues"/>',
                onclick: new IxTable_COPYVALUESTOCOLUMNACTION(true)
              }
            ]
          }, {
            header : '<fmt:message key="projects.editStudentProject.moduleTableOptionalityHeader"/>',
            right : 8 + 22 + 8 + 22 + 8 + 22 + 8,
            width: 100,
            dataType : 'select',
            paramName: 'optionality',
            editable: true,
            options: [
              {text: '<fmt:message key="projects.editStudentProject.optionalityMandatory"/>', value: 'MANDATORY'},
              {text: '<fmt:message key="projects.editStudentProject.optionalityOptional"/>', value: 'OPTIONAL'}
            ],
            sortAttributes: {
              sortAscending: {
                toolTip: '<fmt:message key="generic.sort.ascending"/>',
                sortAction: IxTable_ROWSELECTSORT 
              },
              sortDescending: {
                toolTip: '<fmt:message key="generic.sort.descending"/>',
                sortAction: IxTable_ROWSELECTSORT
              }
            },
            contextMenu: [
              {
                text: '<fmt:message key="generic.action.copyValues"/>',
                onclick: new IxTable_COPYVALUESTOCOLUMNACTION(true)
              }
            ]
          }, {
            width: 22,
            right: 8 + 22 + 8 + 22 + 8,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/icons/16x16/actions/link-to-editor.png',
            tooltip: '<fmt:message key="projects.editStudentProject.moduleTableEditModuleRowTooltip"/>',
            onclick: function (event) {
              var table = event.tableComponent;
              var moduleId = table.getCellValue(event.row, table.getNamedColumnIndex('moduleId'));
              redirectTo(GLOBAL_contextPath + '/modules/editmodule.page?module=' + moduleId);
            } 
          }, {
            width: 22,
            right: 8 + 22 + 8,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/icons/16x16/actions/search.png',
            tooltip: '<fmt:message key="projects.editStudentProject.moduleTableFindCourseRowTooltip"/>',
            onclick: function (event) {
              var table = event.tableComponent;
              var row = event.row;
              
              var academicTermId = table.getCellValue(row, table.getNamedColumnIndex('academicTerm'));
              var moduleId = table.getCellValue(row, table.getNamedColumnIndex('moduleId'));
              var studentId = ${studentProject.student.id}; 
              
              var dialog = new IxDialog({
                id : 'searchStudentProjectModuleCoursesDialog',
                contentURL : GLOBAL_contextPath + '/projects/searchstudentprojectmodulecoursesdialog.page?moduleId=' + moduleId + '&academicTermId=' + academicTermId + "&studentId=" + studentId,
                centered : true,
                showOk : false,
                showCancel : true,
                title : '<fmt:message key="projects.searchStudentProjectModuleCoursesDialog.dialogTitle"/>',
                cancelLabel : '<fmt:message key="projects.searchStudentProjectModuleCoursesDialog.cancelLabel"/>' 
              });
              
              dialog.setSize("800px", "600px");
              dialog.addDialogListener(function(event) {
                var dlg = event.dialog;
                switch (event.name) {
                  case 'okClick':
                    var moduleId = table.getCellValue(row, table.getNamedColumnIndex('moduleId'));
                    var courseId = event.results.courseId;
                    var name = event.results.name;
                    var beginDate = event.results.beginDate;
                    var endDate = event.results.endDate;
                    var optionality = table.getCellValue(row, table.getNamedColumnIndex('optionality'));
                    var participationType = '<fmt:message key="projects.editStudentProject.unsavedStudentParticipationType"/>' ;

                    var coursesTable = getIxTableById('coursesTable'); 
                    coursesTable.addRow([name, participationType, beginDate, endDate, optionality, '', '', moduleId, courseId, -1]);
                    var oopCoursesTable = getIxTableById('outOfProjectCoursesTable'); 
                    oopCoursesTable.addRow([name, participationType, beginDate, endDate, optionality, '', '', moduleId, courseId, -1]);
                  
                    redoFilters();
                    checkModulesMessage();
                    checkCoursesMessage();
                  break;
                }
              });
              dialog.open();
            } 
          }, {
            width: 22,
            right: 8,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/list-remove.png',
            tooltip: '<fmt:message key="projects.editStudentProject.moduleTableDeleteRowTooltip"/>',
            onclick: function (event) {
              var moduleId = event.tableComponent.getCellValue(event.row, event.tableComponent.getNamedColumnIndex('moduleId'));
              var coursesTable = getIxTableById('coursesTable');
              var oopCoursesTable = getIxTableById('outOfProjectCoursesTable');
              var courseRow = getCourseTableModuleRowIndex(coursesTable, moduleId);
              
              if (courseRow >= 0) { 
                redoFilters();
              }
              
              event.tableComponent.deleteRow(event.row);
              checkModulesMessage();
              checkCoursesMessage();
            } 
          }, {
            dataType: 'hidden',
            paramName: 'moduleId'
          }, {
            dataType: 'hidden',
            paramName: 'studentProjectModuleId'
          }]
        });
        
        var coursesTable = new IxTable($('coursesTableContainer'), {
          id: 'coursesTable',
          columns : [ {
            header : '<fmt:message key="projects.editStudentProject.coursesTableNameHeader"/>',
            left: 8,
            right: 8 + 22 + 8 + 22 + 8 + 100 + 8 + 22 + 8 + 150 + 8 + 150 + 8 + 150 + 8,
            dataType : 'text',
            editable: false,
            paramName: 'name',
            sortAttributes: {
              sortAscending: {
                toolTip: '<fmt:message key="generic.sort.ascending"/>',
                sortAction: IxTable_ROWSTRINGSORT 
              },
              sortDescending: {
                toolTip: '<fmt:message key="generic.sort.descending"/>',
                sortAction: IxTable_ROWSTRINGSORT
              }
            }
          }, {
            header : '<fmt:message key="projects.editStudentProject.coursesTableStudentsParticipationTypeHeader"/>',
            right: 8 + 22 + 8 + 22 + 8 + 100 + 8 + 22 + 8 + 150 + 8 + 150 + 8,
            width : 150,
            dataType : 'text',
            editable: false,
            paramName: 'name'
          }, {
            header : '<fmt:message key="projects.editStudentProject.coursesTableBeginDateHeader"/>',
            right: 8 + 22 + 8 + 22 + 8 + 100 + 8 + 22 + 8 + 150 + 8,
            width : 150,
            dataType : 'date',
            editable: false
          }, {
            header : '<fmt:message key="projects.editStudentProject.coursesTableEndDateHeader"/>',
            width: 150,
            right : 8 + 22 + 8 + 22 + 8 + 100 + 8 + 22 + 8,
            dataType : 'date',
            editable: false
          }, {
            header : '<fmt:message key="projects.editStudentProject.coursesTableOptionalityHeader"/>',
            right : 8 + 22 + 8 + 22 + 8,
            width: 100,
            dataType : 'select',
            paramName: 'optionality',
            editable: true,
            options: [
              {text: '', value: ''},
              {text: '<fmt:message key="projects.editStudentProject.optionalityMandatory"/>', value: 'MANDATORY'},
              {text: '<fmt:message key="projects.editStudentProject.optionalityOptional"/>', value: 'OPTIONAL'}
            ]
          }, {
            width: 22,
            right: 8 + 22 + 8,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/icons/16x16/actions/link-to-editor.png',
            tooltip: '<fmt:message key="projects.editStudentProject.coursesTableEditCourseRowTooltip"/>',
            onclick: function (event) {
              var table = event.tableComponent;
              var courseId = table.getCellValue(event.row, table.getNamedColumnIndex('courseId'));
              redirectTo(GLOBAL_contextPath + '/courses/editcourse.page?course=' + courseId);
            } 
          }, {
            width: 22,
            right: 8,
            dataType: 'button',
            paramName: 'removeButton',
            imgsrc: GLOBAL_contextPath + '/gfx/list-remove.png',
            tooltip: '<fmt:message key="projects.editStudentProject.coursesTableDeleteRowTooltip"/>',
            onclick: function (event) {
              var moduleId = event.tableComponent.getCellValue(event.row, event.tableComponent.getNamedColumnIndex('moduleId'));
              var moduleTables = getIxTableById('modulesTable');
              var moduleTablesRow = getModuleTableModuleRowIndex(moduleTables, moduleId);
              if (moduleTablesRow >= 0) { 
                moduleTables.showRow(moduleTablesRow);
              }
              
              var oopCoursesTable = getIxTableById('outOfProjectCoursesTable');
              oopCoursesTable.deleteRow(event.row);
              event.tableComponent.deleteRow(event.row);

              checkModulesMessage();
              checkCoursesMessage();
            } 
          }, {
            dataType: 'hidden',
            paramName: 'moduleId'
          }, {
            dataType: 'hidden',
            paramName: 'courseId'
          }, {
            dataType: 'hidden',
            paramName: 'id'
          }]
        });

        var outOfProjectCoursesTable = new IxTable($('outOfProjectCoursesTableContainer'), {
          id: 'outOfProjectCoursesTable',
          columns : [ {
            header : '<fmt:message key="projects.editStudentProject.oopCoursesTableNameHeader"/>',
            left: 8,
            right: 8 + 22 + 8 + 22 + 8 + 100 + 8 + 22 + 8 + 150 + 8 + 150 + 8 + 150 + 8,
            dataType : 'text',
            editable: false,
            paramName: 'name',
            sortAttributes: {
              sortAscending: {
                toolTip: '<fmt:message key="generic.sort.ascending"/>',
                sortAction: IxTable_ROWSTRINGSORT 
              },
              sortDescending: {
                toolTip: '<fmt:message key="generic.sort.descending"/>',
                sortAction: IxTable_ROWSTRINGSORT
              }
            }
          }, {
            header : '<fmt:message key="projects.editStudentProject.oopCoursesTableStudentsParticipationTypeHeader"/>',
            right: 8 + 22 + 8 + 22 + 8 + 100 + 8 + 22 + 8 + 150 + 8 + 150 + 8,
            width : 150,
            dataType : 'text',
            editable: false,
            paramName: 'name'
          }, {
            header : '<fmt:message key="projects.editStudentProject.oopCoursesTableBeginDateHeader"/>',
            right: 8 + 22 + 8 + 22 + 8 + 100 + 8 + 22 + 8 + 150 + 8,
            width : 150,
            dataType : 'date',
            editable: false
          }, {
            header : '<fmt:message key="projects.editStudentProject.oopCoursesTableEndDateHeader"/>',
            width: 150,
            right : 8 + 22 + 8 + 22 + 8 + 100 + 8 + 22 + 8,
            dataType : 'date',
            editable: false
          }, {
            header : '<fmt:message key="projects.editStudentProject.oopCoursesTableOptionalityHeader"/>',
            right : 8 + 22 + 8 + 22 + 8,
            width: 100,
            dataType : 'select',
            paramName: 'optionality',
            editable: true,
            options: [
              {text: '', value: ''},
              {text: '<fmt:message key="projects.editStudentProject.optionalityMandatory"/>', value: 'MANDATORY'},
              {text: '<fmt:message key="projects.editStudentProject.optionalityOptional"/>', value: 'OPTIONAL'}
            ]
          }, {
            width: 22,
            right: 8 + 22 + 8,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/icons/16x16/actions/link-to-editor.png',
            tooltip: '<fmt:message key="projects.editStudentProject.oopCoursesTableEditCourseRowTooltip"/>',
            onclick: function (event) {
              var table = event.tableComponent;
              var courseId = table.getCellValue(event.row, table.getNamedColumnIndex('courseId'));
              redirectTo(GLOBAL_contextPath + '/courses/editcourse.page?course=' + courseId);
            } 
          }, {
            width: 22,
            right: 8,
            dataType: 'button',
            paramName: 'removeButton',
            imgsrc: GLOBAL_contextPath + '/gfx/list-remove.png',
            tooltip: '<fmt:message key="projects.editStudentProject.oopCoursesTableDeleteRowTooltip"/>',
            onclick: function (event) {
              var moduleId = event.tableComponent.getCellValue(event.row, event.tableComponent.getNamedColumnIndex('moduleId'));
              var moduleTables = getIxTableById('modulesTable');
              var moduleTablesRow = getModuleTableModuleRowIndex(moduleTables, moduleId);
              if (moduleTablesRow >= 0) { 
                moduleTables.showRow(moduleTablesRow);
              }
              
              var coursesTable = getIxTableById('coursesTable');
              coursesTable.deleteRow(event.row);
              event.tableComponent.deleteRow(event.row);
              
              checkModulesMessage();
              checkCoursesMessage();
            } 
          }, {
            dataType: 'hidden',
            paramName: 'moduleId'
          }, {
            dataType: 'hidden',
            paramName: 'courseId'
          }, {
            dataType: 'hidden',
            paramName: 'id'
          }]
        });
        
        var rowId;
        modulesTable.detachFromDom();
        <c:forEach var="studentProjectModule" items="${studentProjectModules}">
          rowId = modulesTable.addRow([
            '${fn:escapeXml(studentProjectModule.studentProjectModule.module.name)}',
            ${studentProjectModule.studentProjectModule.academicTerm.id},
            '${studentProjectModule.studentProjectModule.optionality}',
            '',
            '',
            '',
            ${studentProjectModule.studentProjectModule.module.id},
            ${studentProjectModule.studentProjectModule.id}]);
        </c:forEach>
        modulesTable.reattachToDom();
        
        coursesTable.detachFromDom();
        outOfProjectCoursesTable.detachFromDom();
        <c:forEach var="courseStudent" items="${courseStudents}">
          <c:choose>
            <c:when test="${fn:length(courseStudent.course.nameExtension) gt 0}">
              <c:set var="courseName">${courseStudent.course.name} (${courseStudent.course.nameExtension})</c:set>  
            </c:when>
            <c:otherwise>
              <c:set var="courseName">${courseStudent.course.name}</c:set>  
            </c:otherwise>
          </c:choose>
        
          rowId = coursesTable.addRow([
            '${fn:escapeXml(courseName)}',
            '${courseStudent.participationType.name}',
            ${courseStudent.course.beginDate.time},
            ${courseStudent.course.endDate.time},
            '${courseStudent.optionality}',
            '',
            '',
            ${courseStudent.course.module.id},
            ${courseStudent.course.id},
            ${courseStudent.id}]);
          
          coursesTable.disableCellEditor(rowId, coursesTable.getNamedColumnIndex("removeButton"));
          
          rowId = outOfProjectCoursesTable.addRow([
             '${fn:escapeXml(courseName)}',
             '${courseStudent.participationType.name}',
             ${courseStudent.course.beginDate.time},
             ${courseStudent.course.endDate.time},
             '${courseStudent.optionality}',
             '',
             '',
             ${courseStudent.course.module.id},
             ${courseStudent.course.id},
             ${courseStudent.id}]);
          outOfProjectCoursesTable.disableCellEditor(rowId, coursesTable.getNamedColumnIndex("removeButton"));
          
        </c:forEach>
        outOfProjectCoursesTable.reattachToDom();
        coursesTable.reattachToDom();

        modulesTable.addFilter({
          execute: function (event) {
            var table = event.tableComponent;
            var coursesTable = getIxTableById('coursesTable');
            if (coursesTable) {
              var colIndex = coursesTable.getNamedColumnIndex('moduleId');
              
              var hideArray = new Array();
              
              for (var i = 0; i < coursesTable.getRowCount(); i++) {
                var moduleId = coursesTable.getCellValue(i, colIndex);
                var moduleTableRow = getModuleTableModuleRowIndex(table, moduleId);
                if (moduleTableRow >= 0) { 
                  hideArray.push(moduleTableRow);
                }
              }
  
              if (hideArray.size() > 0)
                table.hideRows(hideArray.toArray());
            }
          },
          getColumn: function() {
            return -1;
          }
        });

        coursesTable.addFilter({
          execute: function (event) {
            var table = event.tableComponent;
            var modulesTable = getIxTableById('modulesTable');
            if (modulesTable) {
              var colIndex = modulesTable.getNamedColumnIndex('moduleId');
              
              var hideArray = new Array();
              var tempArray = new Array();
              
              for (var i = modulesTable.getRowCount() - 1; i >= 0; i--) {
                var moduleId = modulesTable.getCellValue(i, colIndex);
                var coursesTableRow = getCourseTableModuleRowIndex(table, moduleId);
                if (coursesTableRow >= 0) { 
                  tempArray.push(coursesTableRow);
                }
              }

              for (var i = 0; i < table.getRowCount(); i++) {
                if (tempArray.indexOf(i) == -1)
                  hideArray.push(i);
              }
              
              if (hideArray.size() > 0)
                table.hideRows(hideArray.toArray());
            }
          },
          getColumn: function() {
            return -1;
          }
        });

        outOfProjectCoursesTable.addFilter({
          execute: function (event) {
            var table = event.tableComponent;
            var modulesTable = getIxTableById('modulesTable');
            if (modulesTable) {
              var colIndex = modulesTable.getNamedColumnIndex('moduleId');
              
              var hideArray = new Array();
              
              for (var i = modulesTable.getRowCount() - 1; i >= 0; i--) {
                var moduleId = modulesTable.getCellValue(i, colIndex);
                var coursesTableRow = getCourseTableModuleRowIndex(table, moduleId);
                if (coursesTableRow >= 0) { 
                   hideArray.push(coursesTableRow);
                }
              }
  
              if (hideArray.size() > 0)
                table.hideRows(hideArray.toArray());
            }
          },
          getColumn: function() {
            return -1;
          }
        });

        coursesTable.addListener("cellValueChange", function (event) {
          var column = event.column;
          var coursesTable = event.tableComponent;
          var oopCoursesTable = getIxTableById('outOfProjectCoursesTable');
          var tableColumnIndex = coursesTable.getNamedColumnIndex('optionality');
          
          if ((column == tableColumnIndex) && (coursesTable.getRowCount() == oopCoursesTable.getRowCount())) {
            var row = event.row;
            var value = event.value;

            var oopValue = oopCoursesTable.getCellValue(row, column);
            
            if (oopValue != value)
              oopCoursesTable.setCellValue(row, column, value);
          }
        });

        outOfProjectCoursesTable.addListener("cellValueChange", function (event) {
          var column = event.column;
          var coursesTable = getIxTableById('coursesTable');
          var oopCoursesTable = event.tableComponent;
          var tableColumnIndex = coursesTable.getNamedColumnIndex('optionality');
          
          if ((column == tableColumnIndex) && (coursesTable.getRowCount() == oopCoursesTable.getRowCount())) {
            var row = event.row;
            var value = event.value;

            var cValue = coursesTable.getCellValue(row, column);
            
            if (cValue != value)
              coursesTable.setCellValue(row, column, value);
          }
        });
        
        checkModulesMessage();
        checkCoursesMessage();
        
        var basicTabRelatedActionsHoverMenu = new IxHoverMenu($('basicTabRelatedActionsHoverMenuContainer'), {
          text: '<fmt:message key="projects.editStudentProject.basicTabRelatedActionsLabel"/>'
        });
        basicTabRelatedActionsHoverMenu.addItem(new IxHoverMenuLinkItem({
          iconURL: GLOBAL_contextPath + '/gfx/icons/16x16/actions/link-to-editor.png',
          text: '<fmt:message key="projects.editStudentProject.basicTabRelatedActionsEditStudentLabel"/>',
          link: '../students/editstudent.page?abstractStudent=${studentProject.student.abstractStudent.id}'  
        }));
      }

    </script>
  </head>
  <body onLoad="onLoad(event);" ix:enabledrafting="true">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
    
    <h1 class="genericPageHeader">
      <fmt:message key="projects.editStudentProject.pageTitle">
        <fmt:param value="${studentProject.student.fullName}"/>
        <fmt:param value="${studentProject.name}"/>
      </fmt:message>
    </h1>
    
    <form id="studentProjectForm" action="editstudentproject.json" method="post" ix:jsonform="true" ix:useglasspane="true">
      <input type="hidden" name="version" value="${studentProject.version}"/>
      
      <div id="editStudentProjectEditFormContainer"> 
        <div class="genericFormContainer"> 
          
          <div class="tabLabelsContainer" id="tabs">
            <a class="tabLabel" href="#basic">
              <fmt:message key="projects.editStudentProject.tabLabelBasic"/>
            </a>
            <a class="tabLabel" href="#coursesmodules">
              <fmt:message key="projects.editStudentProject.tabLabelCoursesAndModules"/>
            </a>
          </div>
          
          <!--  Basic tab -->
        
          <div id="basic" class="tabContent">
            <div id="basicTabRelatedActionsHoverMenuContainer" class="tabRelatedActionsContainer"></div>
            <input type="hidden" name="studentProject" value="${studentProject.id}"/>
            
            <!--  TODO italic tags to css -->
  
            <div class="genericFormSection">  
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="projects.editStudentProject.createdTitle"/>
                <jsp:param name="helpLocale" value="projects.editStudentProject.createdHelp"/>
              </jsp:include>
              <span><i>${studentProject.creator.fullName} <fmt:formatDate pattern="dd.MM.yyyy hh:mm" value="${studentProject.created}"/></i></span>    
            </div>

            <div class="genericFormSection">  
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="projects.editStudentProject.modifiedTitle"/>
                <jsp:param name="helpLocale" value="projects.editStudentProject.modifiedHelp"/>
              </jsp:include>
              <span><i>${studentProject.lastModifier.fullName} <fmt:formatDate pattern="dd.MM.yyyy hh:mm" value="${studentProject.lastModified}"/></i></span>    
            </div>
  
            <div class="genericFormSection">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="projects.editStudentProject.studentTitle"/>
                <jsp:param name="helpLocale" value="projects.editStudentProject.studentHelp"/>
              </jsp:include>
              <div>${studentProject.student.lastName}, ${studentProject.student.firstName}</div>
            </div>
  
            <div class="genericFormSection">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="projects.editStudentProject.studyProgrammeTitle"/>
                <jsp:param name="helpLocale" value="projects.editStudentProject.studyProgrammeHelp"/>
              </jsp:include>     
              
              <select name="student">
                <c:forEach var="student" items="${students}">
                  <c:choose>
                    <c:when test="${student.studyProgramme.id == studentProject.student.studyProgramme.id}">
                      <option value="${student.id}" selected="selected">${student.studyProgramme.name}</option> 
                    </c:when>
                    <c:otherwise>
                      <option value="${student.id}">${student.studyProgramme.name}</option> 
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
                <c:if test="${studentProject.student.studyProgramme.archived == true}">
                  <option value="${studentProject.student.id}" selected="selected">${studentProject.student.studyProgramme.name}***</option>
                </c:if>
              </select>
            </div>

            <div class="genericFormSection">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="projects.editStudentProject.nameTitle"/>
                <jsp:param name="helpLocale" value="projects.editStudentProject.nameHelp"/>
              </jsp:include>
              <input type="text" class="required" name="name" value="${fn:escapeXml(studentProject.name)}" size="40"/>
            </div>
                 
            <div class="genericFormSection">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="projects.editStudentProject.tagsTitle"/>
                <jsp:param name="helpLocale" value="projects.editStudentProject.tagsHelp"/>
              </jsp:include>
              <input type="text" id="tags" name="tags" size="40" value="${fn:escapeXml(tags)}"/>
              <div id="tags_choices" class="autocomplete_choices"></div>
            </div>
    
            <div class="genericFormSection">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="projects.editStudentProject.descriptionTitle"/>
                <jsp:param name="helpLocale" value="projects.editStudentProject.descriptionHelp"/>
              </jsp:include>
              <textarea ix:cktoolbar="studentProjectDescription" name="description" ix:ckeditor="true">${studentProject.description}</textarea>
            </div>
  
            <div class="genericFormSection">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="projects.editStudentProject.optionalStudiesTitle"/>
                <jsp:param name="helpLocale" value="projects.editStudentProject.optionalStudiesHelp"/>
              </jsp:include>
              <input type="text" name="optionalStudiesLength" class="required" value="${studentProject.optionalStudiesLength.units}" size="15"/>
              <select name="optionalStudiesLengthTimeUnit">           
                <c:forEach var="optionalStudiesLengthTimeUnit" items="${optionalStudiesLengthTimeUnits}">
                  <option value="${optionalStudiesLengthTimeUnit.id}" <c:if test="${studentProject.optionalStudiesLength.unit.id == optionalStudiesLengthTimeUnit.id}">selected="selected"</c:if>>${optionalStudiesLengthTimeUnit.name}</option> 
                </c:forEach>
              </select>            
            </div>
          </div>
          
          <!--  Courses and Modules tab -->
  
          <div id="coursesmodules" class="tabContentixTableFormattedData">
            <div class="genericFormSection editStudentProjectModuleListTitle">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="projects.editStudentProject.moduleListTitle"/>
                <jsp:param name="helpLocale" value="projects.editStudentProject.moduleListHelp"/>
              </jsp:include>
            </div>
              
            <div class="genericTableAddRowContainer">
              <span class="genericTableAddRowLinkContainer" onclick="openSearchModulesDialog();"><fmt:message key="projects.editStudentProject.addModuleLink"/></span>
            </div>
          
            <div id="noModulesAddedMessageContainer" class="genericTableNotAddedMessageContainer">
              <span><fmt:message key="projects.editStudentProject.noModulesAddedPreFix"/> <span onclick="openSearchModulesDialog();" class="genericTableAddRowLink"><fmt:message key="projects.editStudentProject.noModulesAddedClickHereLink"/></span>.</span>
            </div>
  
            <div id="allModulesAddedMessageContainer" class="genericTableNotAddedMessageContainer">
              <span><fmt:message key="projects.editStudentProject.allModulesAddedPreFix"/></span>
            </div>
            
            <div id="modulesContainer">
              <div id="modulesTableContainer"></div>
            </div>
  
            <div id="editStudentProjectModulesTotalContainer">
              <fmt:message key="projects.editStudentProject.modulesTotal"/> <span id="editStudentProjectModulesTotalValue"></span>
            </div>
  
            <div class="genericFormSection editStudentProjectCourseListTitle">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="projects.editStudentProject.courseListTitle"/>
                <jsp:param name="helpLocale" value="projects.editStudentProject.courseListHelp"/>
              </jsp:include>
            </div> 
            
            <div class="genericTableAddRowContainer">
              <span class="genericTableAddRowLinkContainer" onclick="openSearchCoursesDialog();"><fmt:message key="projects.editStudentProject.addCourseLink"/></span>
            </div>
  
            <div id="noCoursesAddedMessageContainer" class="genericTableNotAddedMessageContainer">
              <span><fmt:message key="projects.editStudentProject.noCoursesAddedPreFix"/> <span onclick="openSearchCoursesDialog();" class="genericTableAddRowLink"><fmt:message key="projects.editStudentProject.noCoursesAddedClickHereLink"/></span>.</span>
            </div>
  
            <div id="coursesContainer">
              <div id="coursesTableContainer"></div>
            </div>

            <div id="editStudentProjectCoursesTotalContainer">
              <fmt:message key="projects.editStudentProject.coursesTotal"/> <span id="editStudentProjectCoursesTotalValue"></span>
            </div>

            <div class="genericFormSection editStudentProjectCourseListTitle">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="projects.editStudentProject.outOfProjectCourseListTitle"/>
                <jsp:param name="helpLocale" value="projects.editStudentProject.outOfProjectCourseListHelp"/>
              </jsp:include>
            </div> 

            <div class="genericTableAddRowContainer">
              <span class="genericTableAddRowLinkContainer" onclick="openSearchCoursesDialog();"><fmt:message key="projects.editStudentProject.addCourseLink"/></span>
            </div>

            <div id="noOOPCoursesAddedMessageContainer" class="genericTableNotAddedMessageContainer">
              <span><fmt:message key="projects.editStudentProject.noCoursesAddedPreFix"/> <span onclick="openSearchCoursesDialog();" class="genericTableAddRowLink"><fmt:message key="projects.editStudentProject.noCoursesAddedClickHereLink"/></span>.</span>
            </div>

            <div id="oopCoursesContainer">
              <div id="outOfProjectCoursesTableContainer"></div>
            </div>

            <div id="editStudentProjectOOPCoursesTotalContainer">
              <fmt:message key="projects.editStudentProject.coursesTotal"/> <span id="editStudentProjectOOPCoursesTotalValue"></span>
            </div>
          </div>
        </div>
      </div>
    
      <div class="genericFormSubmitSection">
        <input type="submit" class="formvalid" value="<fmt:message key="projects.editStudentProject.saveButton"/>">
      </div>
    
    </form>

    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>