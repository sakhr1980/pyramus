<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/ix" prefix="ix"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title><fmt:message key="courses.editCourse.pageTitle" /></title>
    <jsp:include page="/templates/generic/head_generic.jsp"></jsp:include>
    <jsp:include page="/templates/generic/ckeditor_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/dialog_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/scriptaculous_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/tabs_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/table_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/datefield_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/studentinfopopup_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonrequest_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonform_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/draftapi_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/validation_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/hovermenu_support.jsp"></jsp:include>

    <script type="text/javascript">

      var archivedStudentRowIndex;
      var archivedComponentRowIndex;

      // Generic resource related functions

      function openSearchResourcesDialog(targetTableId) {

        var selectedResources = new Array();
        var resourcesTable = getIxTableById(targetTableId);
        for (var i = 0; i < resourcesTable.getRowCount() - 1; i++) {
          var resourceId = resourcesTable.getCellValue(i, resourcesTable.getNamedColumnIndex('resourceId'));
          selectedResources.push({
            id: resourceId});
        }
        // TODO selectedResources -> dialog

        var dialog = new IxDialog({
          id : 'searchResourcesDialog',
          contentURL : GLOBAL_contextPath + '/resources/searchresourcesdialog.page',
          centered : true,
          showOk : true,
          showCancel : true,
          title : '<fmt:message key="resources.searchResourcesDialog.searchResourcesDialogTitle"/>',
          okLabel : '<fmt:message key="resources.searchResourcesDialog.okLabel"/>', 
          cancelLabel : '<fmt:message key="resources.searchResourcesDialog.cancelLabel"/>' 
        });
        
        dialog.setSize("800px", "600px");
        dialog.addDialogListener(function(event) {
          var dlg = event.dialog;
          switch (event.name) {
            case 'okClick':
              for (var i = 0, len = event.results.resources.length; i < len; i++) {
                var resourceId = event.results.resources[i].id;
                var resourceName = event.results.resources[i].name;
                var resourceUnitCost = event.results.resources[i].unitCost;
                var resourceHourlyCost = event.results.resources[i].hourlyCost;
                var index = getResourceRowIndex(targetTableId, resourceId);
                if (index == -1) {
                  var resourcesTable = getIxTableById(targetTableId);
                  resourcesTable.addRow([-1, resourceId, resourceName, 0, resourceHourlyCost, 0, resourceUnitCost, 0, '']);
                }
              }
            break;
          }
        });
        dialog.open();
      }

      function addNewCourseStudent(studentsTable, abstractStudentId, studentId, studentName) {
        JSONRequest.request("students/getstudentstudyprogrammes.json", {
          parameters: {
            abstractStudentId: abstractStudentId
          },
          onSuccess: function (jsonResponse) {
            var rowIndex = studentsTable.addRow(['', studentName, studentId, 10, new Date().getTime(), 0, 'false', abstractStudentId, -1, '', '']);
            var cellEditor = studentsTable.getCellEditor(rowIndex, studentsTable.getNamedColumnIndex('studentId'));
            for (var j = 0, l = jsonResponse.studentStudyProgrammes.length; j < l; j++) {
              IxTableControllers.getController('select').addOption(cellEditor , jsonResponse.studentStudyProgrammes[j].studentId, jsonResponse.studentStudyProgrammes[j].studyProgrammeName);
            }

            if (studentsTable.getRowCount() > 0) {
              $('noStudentsAddedMessageContainer').setStyle({
                display: 'none'
              });
            }
          }
        });   
      };

      function openSearchStudentsDialog() {
        var selectedStudents = new Array();
        var studentsTable = getIxTableById('studentsTable');
        for (var i = 0; i < studentsTable.getRowCount() - 1; i++) {
          var studentId = studentsTable.getCellValue(i, studentsTable.getNamedColumnIndex('studentId'));
          selectedStudents.push({
            id: studentId});
        }
        // TODO selectedStudents -> dialog

        var dialog = new IxDialog({
          id : 'searchStudentsDialog',
          contentURL : GLOBAL_contextPath + '/students/searchstudentsdialog.page',
          centered : true,
          showOk : true,
          showCancel : true,
          title : '<fmt:message key="students.searchStudentsDialog.searchStudentsDialogTitle"/>',
          okLabel : '<fmt:message key="students.searchStudentsDialog.okLabel"/>', 
          cancelLabel : '<fmt:message key="students.searchStudentsDialog.cancelLabel"/>' 
        });
        
        dialog.setSize("800px", "600px");
        dialog.addDialogListener(function(event) {
          var dlg = event.dialog;
          switch (event.name) {
            case 'okClick':
              var studentsTable = getIxTableById('studentsTable');
              for (var i = 0, len = event.results.students.length; i < len; i++) {
                var abstractStudentId = event.results.students[i].abstractStudentId;
                var studentId = event.results.students[i].id;
                var studentName = event.results.students[i].name;
                var index = getStudentRowIndex(studentId);
                if (index == -1) {
                  addNewCourseStudent(studentsTable, abstractStudentId, studentId, studentName);
                } 
              }
            break;
          }
        });
        dialog.open();
      }

      function getResourceRowIndex(tableId, resourceId) {
        var table = getIxTableById(tableId);
        if (table) {
          for (var i = 0; i < table.getRowCount(); i++) {
            var tableResourceId = table.getCellValue(i, table.getNamedColumnIndex('resourceId'));
            if (tableResourceId == resourceId) {
              return i;
            }
          }
        }
        return -1;
      }

      function getStudentRowIndex(studentId) {
        var table = getIxTableById('studentsTable');
        if (table) {
          for (var i = 0; i < table.getRowCount(); i++) {
            var tableStudentId = table.getCellValue(i, table.getNamedColumnIndex('studentId'));
            if (tableStudentId == studentId) {
              return i;
            }
          }
        }
        return -1;
      }

      function updateCosts(tableId, rowId, tableTotalContainerId) {

        // Get the table in question

        var table = getIxTableById(tableId);

        // Update the row total, if a row was given
        
        if (rowId != -1) {
          var hours = table.getCellValue(rowId, table.getNamedColumnIndex('hours'));
          var hourlyCost = table.getCellValue(rowId, table.getNamedColumnIndex('hourlyCost'));
          var units = table.getCellValue(rowId, table.getNamedColumnIndex('units'));
          var unitCost = table.getCellValue(rowId, table.getNamedColumnIndex('unitCost'));
          table.setCellValue(rowId, table.getNamedColumnIndex('total'), (hours * hourlyCost) + (units * unitCost));
        }

        // Update the table total
        
        var sum = 0;
        for (var row = 0; row < table.getRowCount(); row++) {
          sum += parseInt(table.getCellValue(row, table.getNamedColumnIndex('total')));
        }
        $(tableTotalContainerId).innerHTML = sum;

        // Update the overall total

        updateTotalCosts();
      }

      function updateTotalCosts() {
        var totalCosts = 0;
      
        var basicResourcesTable = getIxTableById('basicResourcesTable');
        var studentResourcesTable = getIxTableById('studentResourcesTable');
        var gradeResourcesTable = getIxTableById('gradeResourcesTable');
        var otherCostsTable = getIxTableById('otherCostsTable');
      
        for (var row = 0; row < basicResourcesTable.getRowCount(); row++)
          totalCosts += parseInt(basicResourcesTable.getCellValue(row, basicResourcesTable.getNamedColumnIndex('total')));
      
        for (var row = 0; row < studentResourcesTable.getRowCount(); row++)
          totalCosts += parseInt(studentResourcesTable.getCellValue(row, studentResourcesTable.getNamedColumnIndex('total')));
      
        for (var row = 0; row < gradeResourcesTable.getRowCount(); row++)
          totalCosts += parseInt(gradeResourcesTable.getCellValue(row, gradeResourcesTable.getNamedColumnIndex('total')));
      
        for (var row = 0; row < otherCostsTable.getRowCount(); row++)
          totalCosts += parseInt(otherCostsTable.getCellValue(row, otherCostsTable.getNamedColumnIndex('cost')));
      
        $('courseCostsTotalValue').innerHTML = totalCosts;
      }

      function setupResources() {

        // Base course resources

        var basicResourcesTable = getIxTableById('basicResourcesTable');
        <c:forEach var="courseResource" items="${course.basicCourseResources}">
          basicResourcesTable.addRow([
            ${courseResource.id},
            ${courseResource.resource.id},
            '${fn:replace(courseResource.resource.name, "'", "\\'")}',
            ${courseResource.hours},
            ${courseResource.hourlyCost.amount},
            ${courseResource.units},
            ${courseResource.unitCost.amount},
            '',
            '']);
        </c:forEach>

        // Student course resources

        var studentResourcesTable = getIxTableById('studentResourcesTable');
        <c:forEach var="courseResource" items="${course.studentCourseResources}">
          studentResourcesTable.addRow([
            ${courseResource.id},
            ${courseResource.resource.id},
            '${fn:replace(courseResource.resource.name, "'", "\\'")}',
            ${courseResource.hours},
            ${courseResource.hourlyCost.amount},
            ${courseResource.units},
            ${courseResource.unitCost.amount},
            '',
            '']);
        </c:forEach>

        // Grade course resources

        var gradeResourcesTable = getIxTableById('gradeResourcesTable');
        <c:forEach var="courseResource" items="${course.gradeCourseResources}">
          gradeResourcesTable.addRow([
            ${courseResource.id},
            ${courseResource.resource.id},
            '${fn:replace(courseResource.resource.name, "'", "\\'")}',
            ${courseResource.hours},
            ${courseResource.hourlyCost.amount},
            ${courseResource.units},
            ${courseResource.unitCost.amount},
            '',
            '']);
        </c:forEach>

        // Other costs

        var otherCostsTable = getIxTableById('otherCostsTable');
        <c:forEach var="otherCost" items="${course.otherCosts}">
          otherCostsTable.addRow([
            ${otherCost.id},
            '${fn:replace(otherCost.name, "'", "\\'")}',
            ${otherCost.cost.amount},
            '']);
        </c:forEach>
      }

      // Personnel
      
      function setupPersonnelTable() {
        var personnelTable = new IxTable($('personnelTable'), {
          id : "personnelTable",
          columns : [{
            dataType: 'hidden',
            paramName: 'courseUserId'
          }, {
            dataType: 'hidden',
            paramName: 'userId'
          }, {
            header : '<fmt:message key="courses.editCourse.personnelTablePersonHeader"/>',
            left : 8,
            width: 250,
            dataType : 'text',
            editable: false,
            paramName: 'userName'
          }, {
            header : '<fmt:message key="courses.editCourse.personnelTableRoleHeader"/>',
            width: 200,
            left : 266,
            dataType: 'select',
            editable: true,
            paramName: 'roleId',
            options: [
              <c:forEach var="role" items="${roles}" varStatus="vs">
                {text: "${role.name}", value: ${role.id}}
                <c:if test="${not vs.last}">,</c:if>
              </c:forEach>
            ]
          }, {
            left: 474,
            width: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/list-remove.png',
            tooltip: '<fmt:message key="courses.editCourse.personnelTableRemoveRowTooltip"/>',
            onclick: function (event) {
              event.tableObject.deleteRow(event.row);
            } 
          }]        
        });
        <c:forEach var="courseUser" items="${course.courseUsers}">
          personnelTable.addRow([
            ${courseUser.id},
            ${courseUser.user.id},
            '${fn:replace(courseUser.user.fullName, "'", "\\'")}',
            ${courseUser.role.id},
            ''
          ]);
        </c:forEach>
      }

      function openSearchUsersDialog() {
        var dialog = new IxDialog({
          id : 'searchUsersDialog',
          contentURL : GLOBAL_contextPath + '/users/searchusersdialog.page',
          centered : true,
          showOk : true,
          showCancel : true,
          title : '<fmt:message key="users.searchUsersDialog.searchUsersDialogTitle"/>',
          okLabel : '<fmt:message key="users.searchUsersDialog.okLabel"/>', 
          cancelLabel : '<fmt:message key="users.searchUsersDialog.cancelLabel"/>' 
        });
        
        dialog.setSize("800px", "600px");
        dialog.addDialogListener(function(event) {
          var dlg = event.dialog;
          switch (event.name) {
            case 'okClick':
              var personnelTable = getIxTableById('personnelTable');
              for (var i = 0, len = event.results.users.length; i < len; i++) {
                var userId = event.results.users[i].id;
                var userName = event.results.users[i].name;
                var index = getUserRowIndex(userId);
                if (index == -1) {
                  personnelTable.addRow([-1, userId, userName, '', '']);
                } 
              }
            break;
          }
        });
        dialog.open();
      }

      function getUserRowIndex(userId) {
        var table = getIxTableById('personnelTable');
        if (table) {
          for (var i = 0; i < table.getRowCount(); i++) {
            var tableUserId = table.getCellValue(i, table.getNamedColumnIndex('userId'));
            if (tableUserId == userId) {
              return i;
            }
          }
        }
        return -1;
      }

      // Course components
      
      function setupComponentsTable() {
        var componentsTable = new IxTable($('componentsTable'), {
          id : "componentsTable",
          columns : [ {
            dataType: 'hidden',
            paramName: 'componentId'
          }, {
            header : '<fmt:message key="courses.editCourse.componentsTableNameHeader"/>',
            left : 8,
            width : 236,
            dataType: 'text',
            editable: true,
            paramName: 'name',
            editorClassNames: 'required'
          }, {
            header : '<fmt:message key="courses.editCourse.componentsTableLengthHeader"/>',
            left : 248,
            width : 60,
            dataType : 'number',
            editable: true,
            paramName: 'length',
            editorClassNames: 'required'
          }, {
            header : '<fmt:message key="courses.editCourse.componentsTableDescriptionHeader"/>',
            left: 312,
            right : 30,
            dataType: 'text',
            editable: true,
            paramName: 'description'
          }, {
            width: 26,
            right: 0,
            dataType: 'button',
            paramName: 'removeButton',
            hidden: true,
            imgsrc: GLOBAL_contextPath + '/gfx/list-remove.png',
            tooltip: '<fmt:message key="courses.editCourse.componentsTableRemoveRowTooltip"/>',
            onclick: function (event) {
              event.tableObject.deleteRow(event.row);
              if (event.tableObject.getRowCount() == 0) {
                $('noComponentsAddedMessageContainer').setStyle({
                  display: ''
                });
                $('componentHoursTotalContainer').setStyle({
                  display: 'none'
                });
              }
            }
          }, {
            width: 26,
            right: 0,
            dataType: 'button',
            paramName: 'archiveButton',
            hidden: true,
            imgsrc: GLOBAL_contextPath + '/gfx/edit-delete.png',
            tooltip: '<fmt:message key="courses.editCourse.componentsTableArchiveRowTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var componentId = table.getCellValue(event.row, table.getNamedColumnIndex('componentId'));
              var componentName = table.getCellValue(event.row, table.getNamedColumnIndex('name'));
              var url = GLOBAL_contextPath + "/simpledialog.page?localeId=courses.editCourse.archiveComponentConfirmDialogContent&localeParams=" + encodeURIComponent(componentName);

              archivedComponentRowIndex = event.row; 
                 
              var dialog = new IxDialog({
                id : 'confirmRemoval',
                contentURL : url,
                centered : true,
                showOk : true,  
                showCancel : true,
                autoEvaluateSize: true,
                title : '<fmt:message key="courses.editCourse.archiveComponentConfirmDialogTitle"/>',
                okLabel : '<fmt:message key="courses.editCourse.archiveComponentConfirmDialogOkLabel"/>',
                cancelLabel : '<fmt:message key="courses.editCourse.archiveComponentConfirmDialogCancelLabel"/>'
              });
            
              dialog.addDialogListener(function(event) {
                switch (event.name) {
                  case 'okClick':
                    JSONRequest.request("courses/archivecoursecomponent.json", {
                      parameters: {
                        componentId: componentId
                      },
                      onSuccess: function (jsonResponse) {
                        var table = getIxTableById('componentsTable');
                        table.deleteRow(archivedComponentRowIndex);
                        if (table.getRowCount() == 0) {
                          $('noComponentsAddedMessageContainer').setStyle({
                            display: ''
                          });
                          $('componentHoursTotalContainer').setStyle({
                            display: 'none'
                          });
                        }
                      }
                    });   
                  break;
                }
              });
            
              dialog.open();
            }
          }]
        });
        componentsTable.addListener("cellValueChange", function (event) {
          updateComponentHours();
        });
        componentsTable.addListener("rowAdd", function (event) {
          updateComponentHours();
        });
        componentsTable.addListener("rowDelete", function(event) {
          updateComponentHours();
        });
        <c:forEach var="component" items="${courseComponents}">
          componentsTable.addRow([
            ${component.id},
            '${fn:replace(component.name, "'", "\\'")}',
            ${component.length.units},
            '${fn:replace(component.description, "'", "\\'")}',
            '',
            ''
          ]);
          componentsTable.showCell(componentsTable.getRowCount() - 1, componentsTable.getNamedColumnIndex("archiveButton"));
        </c:forEach>

        if (componentsTable.getRowCount() > 0) {
          $('noComponentsAddedMessageContainer').setStyle({
            display: 'none'
          });
          $('componentHoursTotalContainer').setStyle({
            display: ''
          });
        }
      }

      function addComponentsTableRow() {
        var table = getIxTableById('componentsTable');
        table.addRow([-1, '', 0, '', '', '']);
        table.showCell(table.getRowCount() - 1, table.getNamedColumnIndex("removeButton"));
        $('noComponentsAddedMessageContainer').setStyle({
          display: 'none'
        });
        $('componentHoursTotalContainer').setStyle({
          display: ''
        });
      }

      function updateComponentHours() {
        var table = getIxTableById('componentsTable');
        var sum = 0;
        for (var row = 0; row < table.getRowCount(); row++) {
          sum += parseFloat(table.getCellValue(row, table.getNamedColumnIndex('length')).replace(',','.'));
        }
        $('componentHoursTotalValueContainer').innerHTML = sum;
      }

      // Basic course resources 
      
      function setupBasicResourcesTable() {
        var basicResourcesTable = new IxTable($('basicResourcesTable'), {
          id : "basicResourcesTable",
          columns : [ {
            dataType : 'hidden',
            paramName : 'basicResourceId'
          }, {
            dataType : 'hidden',
            paramName : 'resourceId'
          }, {
            header : '<fmt:message key="courses.editCourse.basicResourcesTableNameHeader"/>',
            dataType : 'text',
            editable  : false,
            left : 0,
            right : 350,
            paramName : 'resourceName'
          }, {
            header : '<fmt:message key="courses.editCourse.basicResourcesTableHoursHeader"/>',
            dataType : 'number',
            editable: true,
            right : 310,
            width : 30,
            paramName : 'hours'
          }, {
            header : '<fmt:message key="courses.editCourse.basicResourcesTableHourlyCostHeader"/>',
            dataType : 'number',
            editable: true,
            right : 225,
            width : 70,
            paramName : 'hourlyCost'
          }, {
            header : '<fmt:message key="courses.editCourse.basicResourcesTableUnitsHeader"/>',
            dataType : 'number',
            editable: true,
            right : 180,
            width : 30,
            paramName : 'units'
          }, {
            header : '<fmt:message key="courses.editCourse.basicResourcesTableCostPerUnitHeader"/>',
            dataType : 'number',
            editable: true,
            right : 85,
            width : 80,
            paramName : 'unitCost'
          }, {
            header : '<fmt:message key="courses.editCourse.basicResourcesTableTotalHeader"/>',
            dataType : 'number',
            editable: false,
            right : 30,
            width : 40,
            paramName : 'total'
          }, {
            right: 0,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/list-remove.png',
            tooltip: '<fmt:message key="courses.editCourse.basicResourcesTableRemoveRowTooltip"/>',
            onclick: function (event) {
              event.tableObject.deleteRow(event.row);
            } 
          } ]
        });

        basicResourcesTable.addListener("cellValueChange", function (event) {
          updateCosts('basicResourcesTable', event.row, 'basicResourcesTableTotal');
        });
        basicResourcesTable.addListener("rowAdd", function (event) {
          updateCosts('basicResourcesTable', event.row, 'basicResourcesTableTotal');
        });
        basicResourcesTable.addListener("rowDelete", function(event) {
          updateCosts('basicResourcesTable', -1, 'basicResourcesTableTotal');
        });
      }

      // Student resources
      
      function setupStudentResourcesTable() {
        var studentResourcesTable = new IxTable($('studentResourcesTable'), {
          id : "studentResourcesTable",
          columns : [ {
            dataType : 'hidden',
            paramName : 'studentResourceId'
          }, {
            dataType : 'hidden',
            paramName : 'resourceId'
          }, {
            header : '<fmt:message key="courses.editCourse.studentResourcesTableNameHeader"/>',
            dataType : 'text',
            editable  : false,
            left : 0,
            right : 350,
            paramName : 'resourceName'
          }, {
            header : '<fmt:message key="courses.editCourse.studentResourcesTableHoursHeader"/>',
            dataType : 'number',
            editable: true,
            right : 310,
            width : 30,
            paramName : 'hours'
          }, {
            header : '<fmt:message key="courses.editCourse.studentResourcesTableHourlyCostHeader"/>',
            dataType : 'number',
            editable: true,
            right : 225,
            width : 70,
            paramName : 'hourlyCost'
          }, {
            header : '<fmt:message key="courses.editCourse.studentResourcesTableUnitsHeader"/>',
            dataType : 'number',
            editable: true,
            right : 180,
            width : 30,
            paramName : 'units'
          }, {
            header : '<fmt:message key="courses.editCourse.studentResourcesTableCostPerUnitHeader"/>',
            dataType : 'number',
            editable: true,
            right : 85,
            width : 80,
            paramName : 'unitCost'
          }, {
            header : '<fmt:message key="courses.editCourse.studentResourcesTableTotalHeader"/>',
            dataType : 'number',
            editable: false,
            right : 30,
            width : 40,
            paramName : 'total'
          }, {
            right: 0,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/list-remove.png',
            tooltip: '<fmt:message key="courses.editCourse.studentResourcesTableRemoveRowTooltip"/>',
            onclick: function (event) {
              event.tableObject.deleteRow(event.row);
            } 
          } ]
        });
        
        studentResourcesTable.addListener("cellValueChange", function (event) {
          updateCosts('studentResourcesTable', event.row, 'studentResourcesTableTotal');
        });
        studentResourcesTable.addListener("rowAdd", function (event) {
          updateCosts('studentResourcesTable', event.row, 'studentResourcesTableTotal');
        });
        studentResourcesTable.addListener("rowDelete", function(event) {
          updateCosts('studentResourcesTable', -1, 'studentResourcesTableTotal');
        });
      }

      // Grade resources
      
      function setupGradeResourcesTable() {
        var gradeResourcesTable = new IxTable($('gradeResourcesTable'), {
          id : "gradeResourcesTable",
          columns : [ {
            dataType : 'hidden',
            paramName : 'gradeResourceId'
          }, {
            dataType : 'hidden',
            paramName : 'resourceId'
          }, {
            header : '<fmt:message key="courses.editCourse.gradeResourcesTableNameHeader"/>',
            dataType : 'text',
            editable  : false,
            left : 0,
            right : 350,
            paramName : 'resourceName'
          }, {
            header : '<fmt:message key="courses.editCourse.gradeResourcesTableHoursHeader"/>',
            dataType : 'number',
            editable: true,
            right : 310,
            width : 30,
            paramName : 'hours'
          }, {
            header : '<fmt:message key="courses.editCourse.gradeResourcesTableHourlyCostHeader"/>',
            dataType : 'number',
            editable: true,
            right : 225,
            width : 70,
            paramName : 'hourlyCost'
          }, {
            header : '<fmt:message key="courses.editCourse.gradeResourcesTableUnitsHeader"/>',
            dataType : 'number',
            editable: true,
            right : 180,
            width : 30,
            paramName : 'units'
          }, {
            header : '<fmt:message key="courses.editCourse.gradeResourcesTableCostPerUnitHeader"/>',
            dataType : 'number',
            editable: true,
            right : 85,
            width : 80,
            paramName : 'unitCost'
          }, {
            header : '<fmt:message key="courses.editCourse.gradeResourcesTableTotalHeader"/>',
            dataType : 'number',
            editable: false,
            right : 30,
            width : 40,
            paramName : 'total'
          }, {
            right: 0,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/list-remove.png',
            tooltip: '<fmt:message key="courses.editCourse.gradeResourcesTableRemoveRowTooltip"/>',
            onclick: function (event) {
              event.tableObject.deleteRow(event.row);
            } 
          } ]
        });
        
        gradeResourcesTable.addListener("cellValueChange", function (event) {
          updateCosts('gradeResourcesTable', event.row, 'gradeResourcesTableTotal');
        });
        gradeResourcesTable.addListener("rowAdd", function (event) {
          updateCosts('gradeResourcesTable', event.row, 'gradeResourcesTableTotal');
        });
        gradeResourcesTable.addListener("rowDelete", function(event) {
          updateCosts('gradeResourcesTable', -1, 'gradeResourcesTableTotal');
        });
      }

      // Other costs
      
      function setupOtherCostsTable() {
        var otherCostsTable = new IxTable($('otherCostsTable'), {
          id : "otherCostsTable",
          columns : [ {
            dataType : 'hidden',
            paramName : 'otherCostId'
          }, {
            header : '<fmt:message key="courses.editCourse.otherCostsTableNameHeader"/>',
            dataType : 'text',
            editable: true,
            left : 0,
            right : 120,
            paramName : 'name',
            editorClassNames: 'required'
          }, {
            header : '<fmt:message key="courses.editCourse.otherCostsTableCostHeader"/>',
            dataType : 'number',
            editable: true,
            right : 37,
            width : 78,
            paramName : 'cost'
          }, {
            right: 0,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/list-remove.png',
            tooltip: '<fmt:message key="courses.editCourse.otherCostsTableRemoveRowTooltip"/>',
            onclick: function (event) {
              event.tableObject.deleteRow(event.row);
            } 
          } ]
        });

        var updateOtherCostsFunction = function(event) {
          var sum = 0;
          for (var row = 0; row < event.tableObject.getRowCount(); row++) {
            sum += parseInt(event.tableObject.getCellValue(row, otherCostsTable.getNamedColumnIndex('cost')));
          }
          $('otherCostsTableTotal').innerHTML = sum;
          updateTotalCosts();
        }
        otherCostsTable.addListener("cellValueChange", updateOtherCostsFunction);
        otherCostsTable.addListener("rowAdd", updateOtherCostsFunction);
        otherCostsTable.addListener("rowDelete", updateOtherCostsFunction);
      }
      
      function addOtherCostRow() {
        getIxTableById('otherCostsTable').addRow( [ -1, '', 0, '' ]);
      }

      function setupStudentsTable() {
        var studentsTable = new IxTable($('courseStudentsTable'), {
          id : "studentsTable",
          columns : [{
            width: 30,
            left: 8,
            dataType: 'button',
            paramName: 'studentInfoButton',
            imgsrc: GLOBAL_contextPath + '/gfx/info.png',
            tooltip: '<fmt:message key="courses.editCourse.studentsTableStudentInfoTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var abstractStudentId = table.getCellValue(event.row, table.getNamedColumnIndex('abstractStudentId'));
              var button = table.getCellEditor(event.row, table.getNamedColumnIndex('studentInfoButton'));
              openStudentInfoPopupOnElement(button, abstractStudentId);
            } 
          }, {
            header : '<fmt:message key="courses.editCourse.studentsTableNameHeader"/>',
            left : 38,
            right : 860,
            dataType : 'text',
            paramName: 'studentName',
            editable: false
          }, {
            header : '<fmt:message key="courses.editCourse.studentsTableStudyProgrammeHeader"/>',
            width: 100,
            right : 752,
            dataType : 'select',
            editable: true,
            dynamicOptions: true,
            paramName: 'studentId',
            options: [
            ]
          }, {
            header : '<fmt:message key="courses.editCourse.studentsTableParticipationTypeHeader"/>',
            width: 200,
            right : 546,
            dataType : 'select',
            editable: true,
            overwriteColumnValues : true,
            paramName: 'participationType',
            options: [
              <c:forEach var="courseParticipationType" items="${courseParticipationTypes}" varStatus="vs">
                {text: "${courseParticipationType.name}", value: ${courseParticipationType.id}}
                <c:if test="${not vs.last}">,</c:if>
              </c:forEach>
            ]
          }, {
            header : '<fmt:message key="courses.editCourse.studentsTableEnrolmentDateHeader"/>',
            width: 200,
            right : 338,
            dataType: 'date',
            editable: true,
            overwriteColumnValues : true,
            paramName: 'enrolmentDate'
          }, {
            header : '<fmt:message key="courses.editCourse.studentsTableEnrolmentTypeHeader"/>',
            width: 174,
            right : 146,
            dataType: 'select', 
            editable: true,
            paramName: 'enrolmentType',
            options: [
            <c:forEach var="courseEnrolmentType" items="${courseEnrolmentTypes}" varStatus="vs">
              {text: "${courseEnrolmentType.name}", value: ${courseEnrolmentType.id}}
              <c:if test="${not vs.last}">,</c:if>
            </c:forEach>
            ]
          }, {
            header : '<fmt:message key="courses.editCourse.studentsTableLodgingHeader"/>',
            width: 100,
            right : 38,
            dataType: 'select', 
            editable: true,
            overwriteColumnValues : true,
            paramName: 'lodging',
            options: [
              {text: '<fmt:message key="courses.editCourse.studentsTableLodgingYes"/>', value: 'true'},
              {text: '<fmt:message key="courses.editCourse.studentsTableLodgingNo"/>', value: 'false'}
            ]
          }, {
            dataType: 'hidden', 
            paramName: 'abstractStudentId'
          }, {
            dataType: 'hidden', 
            paramName: 'courseStudentId'
          }, {
            width: 30,
            right: 0,
            dataType: 'button',
            paramName: 'removeButton',
            hidden: true,
            imgsrc: GLOBAL_contextPath + '/gfx/list-remove.png',
            tooltip: '<fmt:message key="courses.editCourse.studentsTableRemoveRowTooltip"/>',
            onclick: function (event) {
              event.tableObject.deleteRow(event.row);
              if (studentsTable.getRowCount() == 0) {
                $('noStudentsAddedMessageContainer').setStyle({
                  display: ''
                });
              }
            } 
          }, {
            width: 30,
            right: 0,
            dataType: 'button',
            paramName: 'archiveButton',
            hidden: true,
            imgsrc: GLOBAL_contextPath + '/gfx/edit-delete.png',
            tooltip: '<fmt:message key="courses.editCourse.studentsTableArchiveRowTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var courseStudentId = table.getCellValue(event.row, table.getNamedColumnIndex('courseStudentId'));
              var studentName = table.getCellValue(event.row, table.getNamedColumnIndex('studentName'));
              var url = GLOBAL_contextPath + "/simpledialog.page?localeId=courses.editCourse.studentArchiveConfirmDialogContent&localeParams=" + encodeURIComponent(studentName);

              archivedStudentRowIndex = event.row; 
                 
              var dialog = new IxDialog({
                id : 'confirmRemoval',
                contentURL : url,
                centered : true,
                showOk : true,  
                showCancel : true,
                autoEvaluateSize: true,
                title : '<fmt:message key="courses.editCourse.studentArchiveConfirmDialogTitle"/>',
                okLabel : '<fmt:message key="courses.editCourse.studentArchiveConfirmDialogOkLabel"/>',
                cancelLabel : '<fmt:message key="courses.editCourse.studentArchiveConfirmDialogCancelLabel"/>'
              });
            
              dialog.addDialogListener(function(event) {
                switch (event.name) {
                  case 'okClick':
                    JSONRequest.request("courses/archivecoursestudent.json", {
                      parameters: {
                        courseStudentId: courseStudentId
                      },
                      onSuccess: function (jsonResponse) {
                        var table = getIxTableById('studentsTable');
                        table.deleteRow(archivedStudentRowIndex);
                        if (table.getRowCount() == 0) {
                          $('noStudentsAddedMessageContainer').setStyle({
                            display: ''
                          });
                        }
                      }
                    });   
                  break;
                }
              });
            
              dialog.open();
            } 
          }]        
        });

        <c:forEach var="courseStudent" items="${courseStudents}" varStatus="status">
          loadStudentStudyProgrammes(
              studentsTable,
              ${courseStudent.student.abstractStudent.id},
              "${fn:escapeXml(courseStudent.student.lastName)}, ${fn:escapeXml(courseStudent.student.firstName)}", 
              ${courseStudent.student.id},
              ${courseStudent.participationType.id}, 
              ${courseStudent.enrolmentTime.time}, 
              ${courseStudent.courseEnrolmentType.id},
              ${courseStudent.lodging},
              ${courseStudent.id}
          );
        </c:forEach>

        studentsTable.addListener("rowAdd", function (event) {
          var studentsTable = event.tableObject;
          studentsTable.showCell(event.row, studentsTable.getNamedColumnIndex("removeButton"));
        });

        <c:if test="${fn:length(courseStudents) gt 0}">
          $('noStudentsAddedMessageContainer').setStyle({
            display: 'none'
          });
        </c:if>
      }

      function loadStudentStudyProgrammes(studentsTable, abstractStudentId, fullName, studentId, participationTypeId, enrolmentTime,
          courseEnrolmentTypeId, lodging, courseStudentId) {
        JSONRequest.request("students/getstudentstudyprogrammes.json", {
          asynchronous: false,
          parameters: {
            abstractStudentId: abstractStudentId
          },
          onSuccess: function (jsonResponse) {
            var rowIndex = studentsTable.addRow([
              '',
              fullName, 
              studentId,
              participationTypeId, 
              enrolmentTime, 
              courseEnrolmentTypeId,
              lodging,
              abstractStudentId, 
              courseStudentId,
              '',
              '']);

            var cellEditor = studentsTable.getCellEditor(rowIndex, studentsTable.getNamedColumnIndex('studentId'));
            for (var j = 0, l = jsonResponse.studentStudyProgrammes.length; j < l; j++) {
              IxTableControllers.getController('select').addOption(cellEditor , 
                  jsonResponse.studentStudyProgrammes[j].studentId, 
                  jsonResponse.studentStudyProgrammes[j].studyProgrammeName, 
                  jsonResponse.studentStudyProgrammes[j].studentId == studentId);
            }
            studentsTable.showCell(studentsTable.getRowCount() - 1, studentsTable.getNamedColumnIndex("archiveButton"));
          }
        });   
      }
      
      function initializeDraftListener() {
        Event.observe(document, "ix:draftRestore", function (event) {
          if (getIxTableById('studentsTable').getRowCount() > 0) {
            $('noStudentsAddedMessageContainer').setStyle({
              display: 'none'
            });
          }
        });
      }

      function setupRelatedCommands() {
        var basicRelatedActionsHoverMenu = new IxHoverMenu($('basicRelatedActionsHoverMenuContainer'), {
          text: '<fmt:message key="courses.editCourse.basicTabRelatedActionsLabel"/>'
        });
    
        basicRelatedActionsHoverMenu.addItem(new IxHoverMenuClickableItem({
          iconURL: GLOBAL_contextPath + '/gfx/eye.png',
          text: '<fmt:message key="courses.editCourse.viewCourseRelatedActionLabel"/>',
          onclick: function (event) {
            redirectTo(GLOBAL_contextPath + '/courses/viewcourse.page?course=${course.id}');
          }
        }));          
      }
            
      // onLoad

      function onLoad(event) {
        var tabControl = new IxProtoTabs($('tabs'));
        initializeDraftListener();
        setupRelatedCommands();
        setupPersonnelTable();
        setupComponentsTable();
        setupBasicResourcesTable();
        setupStudentResourcesTable();
        setupGradeResourcesTable();
        setupOtherCostsTable();
        setupResources();
        setupStudentsTable();
      }

    </script>
  </head>
  <body onload="onLoad(event);" ix:enabledrafting="true">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
    
    <h1 class="genericPageHeader"><fmt:message key="courses.editCourse.pageTitle" /></h1>
    
    <div id="editCourseEditFormContainer">
	    <div class="genericFormContainer">
	      <form action="editcourse.json" method="post" ix:jsonform="true" ix:useglasspane="true">
	        <input type="hidden" name="course" value="${course.id}"/>
	        <div class="tabLabelsContainer" id="tabs">
	          <a class="tabLabel" href="#basic"><fmt:message key="courses.editCourse.basicTabTitle" /></a>
	          <a class="tabLabel" href="#components"><fmt:message key="courses.editCourse.componentsTabTitle" /></a>
	          <a class="tabLabel" href="#costplan"><fmt:message key="courses.editCourse.costPlanTabTitle" /></a>
	          <a class="tabLabel" href="#students"><fmt:message key="courses.editCourse.StudentsTabTitle" /></a>
            <ix:extensionHook name="courses.editCourse.tabLabels"/>
	        </div>
	
	        <div id="basic" class="tabContent">
	          <div id="basicRelatedActionsHoverMenuContainer" class="tabRelatedActionsContainer"></div>
          
	          <div class="genericFormSection">
                <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                  <jsp:param name="titleLocale" value="courses.editCourse.moduleTitle"/>
                  <jsp:param name="helpLocale" value="courses.editCourse.moduleHelp"/>
                </jsp:include>    
	            <i>${course.module.name}</i>
	          </div>
	    
	          <div class="genericFormSection">
                <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                  <jsp:param name="titleLocale" value="courses.editCourse.nameTitle"/>
                  <jsp:param name="helpLocale" value="courses.editCourse.nameHelp"/>
                </jsp:include>    
	            <input type="text" class="required" name="name" value="${fn:escapeXml(course.name)}" size="40">
	          </div>
	          
	          <div class="genericFormSection">
                <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                  <jsp:param name="titleLocale" value="courses.editCourse.nameExtensionTitle"/>
                  <jsp:param name="helpLocale" value="courses.editCourse.nameExtensionHelp"/>
                </jsp:include>    
	            <input type="text" name="nameExtension" value="${fn:escapeXml(course.nameExtension)}" size="40">
	          </div>
	    
            <div class="genericFormSection">  
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="courses.editCourse.stateTitle"/>
                <jsp:param name="helpLocale" value="courses.editCourse.stateHelp"/>
              </jsp:include>    
              <select name="state">           
                <c:forEach var="state" items="${states}">
                  <c:choose>
                    <c:when test="${state.id == course.state.id}">
                      <option value="${state.id}" selected="selected">${state.name}</option>
                    </c:when>
                    <c:otherwise>
                      <option value="${state.id}">${state.name}</option> 
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
              </select>
            </div>

            <div class="genericFormSection">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="courses.editCourse.educationTypesTitle"/>
                <jsp:param name="helpLocale" value="courses.editCourse.educationTypesHelp"/>
              </jsp:include>
  	          <div class="editCourseFormSectionEducationType">
                <c:forEach var="educationType" items="${educationTypes}">
                  <div class="editCourseFormSectionEducationTypeCell">
                    <div class="editCourseFormSectionEducationTypeTitle">
                      <div class="editCourseFormSectionEducationTypeTitleText">${educationType.name}</div>
                    </div>
                    <c:forEach var="educationSubtype" items="${educationType.unarchivedSubtypes}">
                      <c:set var="key" value="${educationType.id}.${educationSubtype.id}"/>
                      <c:choose>
                        <c:when test="${enabledEducationTypes[key]}">
                          <input type="checkbox" name="educationType.${key}" checked="checked"/>                      
                        </c:when>
                        <c:otherwise>
                          <input type="checkbox" name="educationType.${key}"/>                      
                        </c:otherwise>
                      </c:choose>
                      ${educationSubtype.name}<br/>
                    </c:forEach>
                  </div>
                </c:forEach>
  	          </div>
            </div>
	
	          <div class="genericFormSection">  
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="courses.editCourse.subjectTitle"/>
                <jsp:param name="helpLocale" value="courses.editCourse.subjectHelp"/>
              </jsp:include>    
	            <select name="subject">           
	              <c:forEach var="subject" items="${subjects}">
                  <c:choose>
                    <c:when test="${empty subject.code}">
                      <c:choose>
                        <c:when test="${subject.id == course.subject.id}">
                          <option value="${subject.id}" selected="selected">${subject.name}</option>
                        </c:when>
                        <c:otherwise>
                          <option value="${subject.id}">${subject.name}</option> 
                        </c:otherwise>
                      </c:choose>
                    </c:when>
                    <c:otherwise>
                      <c:choose>
                        <c:when test="${subject.id == course.subject.id}">
                          <option value="${subject.id}" selected="selected">${subject.name} (${subject.code})</option>
                        </c:when>
                        <c:otherwise>
                          <option value="${subject.id}">${subject.name} (${subject.code})</option> 
                        </c:otherwise>
                      </c:choose>
                    </c:otherwise>
                  </c:choose>
	              </c:forEach>
                <c:if test="${course.subject.archived == true}">
                  <option value="${course.subject.id}" selected="selected">${course.subject.name} (${course.subject.code})*</option>
                </c:if>
	            </select>
	          </div>
	          
            <div class="genericFormSection">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="courses.editCourse.courseNumberTitle"/>
                <jsp:param name="helpLocale" value="courses.editCourse.courseNumberHelp"/>
              </jsp:include>    
              <input type="text" name="courseNumber" value="${course.courseNumber}" size="2">
            </div>

	          <table>
	            <tr>
	              <td><b><fmt:message key="courses.editCourse.beginsTitle"/></b></td>
	              <td><b><fmt:message key="courses.editCourse.endsTitle"/></b></td>
	              <td><b><fmt:message key="courses.editCourse.lengthTitle"/></b></td>
	            </tr>
	            <tr>
	              <td>
	                <input type="text" name="beginDate" ix:datefield="true" value="${course.beginDate.time}"/>
	              </td>
	              <td>
	                <input type="text" name="endDate" ix:datefield="true" value="${course.endDate.time}"/>
	              </td>
	              <td>
	                <input type="text" name="courseLength" class="required" value="${course.courseLength.units}" size="15"/>
	                <select name="courseLengthTimeUnit">           
	                  <c:forEach var="courseLengthTimeUnit" items="${courseLengthTimeUnits}">
	                    <option value="${courseLengthTimeUnit.id}" <c:if test="${course.courseLength.unit.id == courseLengthTimeUnit.id}">selected="selected"</c:if>>${courseLengthTimeUnit.name}</option> 
	                  </c:forEach>
	                </select>   
	              </td>
	            </tr>
	          </table>
  
            <div class="genericFormSection">  
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="courses.editCourse.planningHoursTitle"/>
                <jsp:param name="helpLocale" value="courses.editCourse.planningHoursHelp"/>
              </jsp:include>    
            
              <input type="text" class="float" name="planningHours" value="${fn:escapeXml(course.planningHours)}" size="5">
            </div>
	
            <div class="genericFormSection">  
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="courses.editCourse.localTeachingDaysTitle"/>
                <jsp:param name="helpLocale" value="courses.editCourse.localTeachingDaysHelp"/>
              </jsp:include>    
            
              <input type="text" class="float" name="localTeachingDays" value="${fn:escapeXml(course.localTeachingDays)}" size="5">
            </div>

            <div class="genericFormSection">  
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="courses.editCourse.distanceTeachingDaysTitle"/>
                <jsp:param name="helpLocale" value="courses.editCourse.distanceTeachingDaysHelp"/>
              </jsp:include>    
              <input type="text" class="float" name="distanceTeachingDays" value="${fn:escapeXml(course.distanceTeachingDays)}" size="5">
            </div>

            <div class="genericFormSection">  
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="courses.editCourse.teachingHoursTitle"/>
                <jsp:param name="helpLocale" value="courses.editCourse.teachingHoursHelp"/>
              </jsp:include>    
              <input type="text" class="float" name="teachingHours" value="${fn:escapeXml(course.teachingHours)}" size="5">
            </div>
  
            <div class="genericFormSection">  
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="courses.editCourse.assessingHoursTitle"/>
                <jsp:param name="helpLocale" value="courses.editCourse.assessingHoursHelp"/>
              </jsp:include>    
            
              <input type="text" class="float" name="assessingHours" value="${fn:escapeXml(course.assessingHours)}" size="5">
            </div>

	          <div class="genericFormSection">
                <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                  <jsp:param name="titleLocale" value="courses.editCourse.descriptionTitle"/>
                  <jsp:param name="helpLocale" value="courses.editCourse.descriptionHelp"/>
                </jsp:include>    
	            <textarea ix:cktoolbar="courseDescription" name="description" ix:ckeditor="true">${course.description}</textarea>
	          </div>

            <div class="genericTableAddRowContainer">
              <span class="genericTableAddRowLinkContainer" onclick="openSearchUsersDialog();"><fmt:message key="courses.editCourse.addPersonLink"/></span>
            </div>
            <div id="personnelTable"> </div>

            <ix:extensionHook name="courses.editCourse.tabs.basic"/>
	        </div>
	    
	        <div id="components" class="tabContentixTableFormattedData hiddenTab">
	          <div class="genericTableAddRowContainer">
	             <span class="genericTableAddRowLinkContainer" onclick="addComponentsTableRow();"><fmt:message key="courses.editCourse.addComponentLink"/></span>
	           </div>
	             
	           <div id="noComponentsAddedMessageContainer" class="genericTableNotAddedMessageContainer">
	             <span><fmt:message key="courses.editCourse.noComponentsAddedPreFix"/> <span onclick="addComponentsTableRow();" class="genericTableAddRowLink"><fmt:message key="courses.editCourse.noComponentsAddedClickHereLink"/></span>.</span>
	           </div>
	          <div id="componentsTable"> </div>
            <div id="componentHoursTotalContainer" style="display:none;">
              <span><fmt:message key="courses.editCourse.totalComponentHoursLabel"/></span>
              <span id="componentHoursTotalValueContainer">0</span>
            </div>
            <ix:extensionHook name="courses.editCourse.tabs.components"/>
	        </div>
	        
	        <div id="costplan" class="tabContent hiddenTab">
            <div id="courseIncomeContainer">
	            <div id="courseIncomesTitle" class="genericFormTitle">
	              <div class="genericFormTitleText"><fmt:message key="courses.editCourse.courseIncomesTitle"/></div>
	            </div>
	          
		          <div class="courseCostsPlanTableContainer">
		            <div class="genericFormTitle courseCostsPlanTableTitle">
                      <div class="genericFormTitleText"><fmt:message key="courses.editCourse.incomePerCourseIncomesTitle" /></div>
                    </div>
		            <div class="courseCostsPlanTableAddRowContainer" onclick="addCourseIncomeRow();"></div>
		          
		            <div id="courseIncomesTable"> </div>
		            <div class="courseCostsPlanTableTotal"> 
		              <div class="courseCostsPlanTableTotalTitle">
		                <fmt:message key="courses.editCourse.courseIncomesTableTotalTitle"/>
		              </div> 
		              <div class="courseCostsPlanTableTotalValue" id="courseIncomesTableTotal"> 0 </div> 
		            </div>
		          </div>
		      
		          <div class="courseCostsPlanTableContainer">
		            <div class="genericFormTitle courseCostsPlanTableTitle">
                      <div class="genericFormTitleText"><fmt:message key="courses.editCourse.incomePerStudentIncomesTitle" /></div>
                    </div>
		            <div class="courseCostsPlanTableAddRowContainer" onclick="addStudentIncomeRow();"></div>
		          
		            <div id="studentIncomesTable"> </div>
		            <div class="courseCostsPlanTableTotal"> 
		              <div class="courseCostsPlanTableTotalTitle">
		                <fmt:message key="courses.editCourse.studentIncomesTableTotalTitle"/>
		              </div> 
		              <div class="courseCostsPlanTableTotalValue" id="studentIncomesTableTotal"> 0 </div> 
		            </div>
		          </div>
		          
		          <div class="courseCostsPlanTableContainer">
		            <div class="genericFormTitle courseCostsPlanTableTitle">
                      <div class="genericFormTitleText"><fmt:message key="courses.editCourse.incomePerGradeIncomesTitle" /></div>
                    </div>
		            <div class="courseCostsPlanTableAddRowContainer" onclick="addGradeIncomeRow();"></div>
		          
		            <div id="gradeIncomesTable"> </div>
		            <div class="courseCostsPlanTableTotal"> 
		              <div class="courseCostsPlanTableTotalTitle">
		                <fmt:message key="courses.editCourse.gradeIncomesTableTotalTitle"/>
		              </div> 
		              <div class="courseCostsPlanTableTotalValue" id="gradeIncomesTableTotal"> 0 </div> 
		            </div>
		          </div>
		    
		          <div class="courseCostsPlanTableContainer">
		            <div class="genericFormTitle courseCostsPlanTableTitle">
                      <div class="genericFormTitleText"><fmt:message key="courses.editCourse.otherIncomesTitle" /></div>
                    </div>
		            <div class="courseCostsPlanTableAddRowContainer" onclick="addOtherIncomeRow();"></div>
		          
		            <div id="otherIncomesTable"> </div>
		            <div class="courseCostsPlanTableTotal"> 
		              <div class="courseCostsPlanTableTotalTitle">
		                <fmt:message key="courses.editCourse.otherIncomesTableTotalTitle"/>
		              </div> 
		              <div class="courseCostsPlanTableTotalValue" id="otherIncomesTableTotal"> 0 </div> 
		            </div>
		          </div>
		          
		          <div id="courseIncomeTotalContainer">
		            <div id="courseIncomeTotalTitle">
		              <fmt:message key="courses.editCourse.courseIncomeTotalTitle"/>
		            </div>
		            <div id="courseIncomeTotalValue"> 0 </div>
		          </div>
		          
	          </div>
	          
            <div id="courseCostContainer">
            
              <div id="courseCostsTitle" class="genericFormTitle">
                <div class="genericFormTitleText"><fmt:message key="courses.editCourse.courseCostsTitle"/></div>
              </div>
           
              <div class="courseCostsPlanTableContainer">
                <div class="genericFormTitle courseCostsPlanTableTitle">
                  <div class="genericFormTitleText"><fmt:message key="courses.editCourse.resourcesPerCourseCostTitle" /></div>
                </div>
                <div class="courseCostsPlanTableAddRowContainer" onclick="openSearchResourcesDialog('basicResourcesTable');"></div>
                <div id="basicResourcesTable"> </div>
                <div class="courseCostsPlanTableTotal"> 
                  <div class="courseCostsPlanTableTotalTitle"> 
                    <fmt:message key="courses.editCourse.basicResourcesTableTotalTitle"/>
                   </div> 
                  <div class="courseCostsPlanTableTotalValue" id="basicResourcesTableTotal"> 0 </div> 
                </div>
              </div>
           
              <div class="courseCostsPlanTableContainer">
                <div class="genericFormTitle courseCostsPlanTableTitle">
                  <div class="genericFormTitleText"><fmt:message key="courses.editCourse.resourcesPerStudentCostTitle" /></div>
                </div>
                <div class="courseCostsPlanTableAddRowContainer" onclick="openSearchResourcesDialog('studentResourcesTable');"></div>
              
                <div id="studentResourcesTable"> </div>
                <div class="courseCostsPlanTableTotal"> 
                  <div class="courseCostsPlanTableTotalTitle"> 
                     <fmt:message key="courses.editCourse.studentResourcesTableTotalTitle"/>
                  </div> 
                  <div class="courseCostsPlanTableTotalValue" id="studentResourcesTableTotal"> 0 </div> 
                </div>
              </div>
          
              <div class="courseCostsPlanTableContainer">
                <div class="genericFormTitle courseCostsPlanTableTitle">
                  <div class="genericFormTitleText"><fmt:message key="courses.editCourse.resourcesPerGradeCostTitle" /></div>
                </div>
                <div class="courseCostsPlanTableAddRowContainer" onclick="openSearchResourcesDialog('gradeResourcesTable');"></div>
                <div id="gradeResourcesTable"> </div>
                <div class="courseCostsPlanTableTotal"> 
                  <div class="courseCostsPlanTableTotalTitle">
                    <fmt:message key="courses.editCourse.gradeResourcesTableTotalTitle"/>
                  </div> 
                  <div class="courseCostsPlanTableTotalValue" id="gradeResourcesTableTotal"> 0 </div> 
                </div>
              </div>
          
              <div class="courseCostsPlanTableContainer">
                <div class="genericFormTitle courseCostsPlanTableTitle">
                  <div class="genericFormTitleText"><fmt:message key="courses.editCourse.otherCostsTitle" /></div>
                </div>
                <div class="courseCostsPlanTableAddRowContainer" onclick="addOtherCostRow();"></div>
              
                <div id="otherCostsTable"> </div>
                <div class="courseCostsPlanTableTotal"> 
                  <div class="courseCostsPlanTableTotalTitle">
                    <fmt:message key="courses.editCourse.otherCostsTableTotalTitle"/>
                  </div> 
                  <div class="courseCostsPlanTableTotalValue" id="otherCostsTableTotal"> 0 </div> 
                </div>
              </div>
          
              <div id="courseCostsTotalContainer">
                <div id="courseCostsTotalTitle">
                  <fmt:message key="courses.editCourse.courseCostsTotalTitle"/>
                </div>
                <div id="courseCostsTotalValue"> 0 </div>
              </div>
              
            </div>
            <div style="clear:both; height:1px;"></div>
            <ix:extensionHook name="courses.editCourse.tabs.costPlan"/>
	        </div>
	    
	        <div id="students" class="tabContentixTableFormattedData hiddenTab">
	          <div class="courseStudentsTableContainer">
		          <div class="genericTableAddRowContainer">
	              <span class="genericTableAddRowLinkContainer" onclick="openSearchStudentsDialog();"><fmt:message key="courses.editCourse.addStudentLink"/></span>
	            </div>
	              
	            <div id="noStudentsAddedMessageContainer" class="genericTableNotAddedMessageContainer">
	              <span><fmt:message key="courses.editCourse.noStudentsAddedPreFix"/> <span onclick="openSearchStudentsDialog();" class="genericTableAddRowLink"><fmt:message key="courses.editCourse.noStudentsAddedClickHereLink"/></span>.</span>
	            </div>
	          
	            <div id="courseStudentsTable"> </div>
	          </div>
            <ix:extensionHook name="courses.editCourse.tabs.students"/>
	        </div>
	    
          <ix:extensionHook name="courses.editCourse.tabs"/>

	        <div class="genericFormSubmitSectionOffTab">
	          <input type="submit" class="formvalid" value="<fmt:message key="courses.editCourse.saveButton"/>">
	        </div>
	
	      </form>
	    </div>
	  </div>
    
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>
