<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title><fmt:message key="projects.editStudentProject.pageTitle" /></title>
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
      function openSearchModulesDialog() {
	
	      var selectedModules = new Array();
	      var modulesTable = getIxTableById('modulesTable');
	      for (var i = 0; i < modulesTable.getRowCount() - 1; i++) {
	        var moduleName = modulesTable.getCellValue(i, modulesTable.getNamedColumnIndex('name'));
	        var moduleId = modulesTable.getCellValue(i, modulesTable.getNamedColumnIndex('moduleId'));
	        selectedModules.push({
	          name: moduleName,
	          id: moduleId});
	      }
	
	      var dialog = new IxDialog({
	        id : 'searchModulesDialog',
	        contentURL : GLOBAL_contextPath + '/projects/searchmodulesdialog.page',
	        centered : true,
	        showOk : true,
	        showCancel : true,
	        title : '<fmt:message key="projects.searchModulesDialog.searchModulesDialogTitle"/>',
	        okLabel : '<fmt:message key="projects.searchModulesDialog.okLabel"/>', 
	        cancelLabel : '<fmt:message key="projects.searchModulesDialog.cancelLabel"/>' 
	      });
	      
	      dialog.setSize("800px", "600px");
	      dialog.addDialogListener(function(event) {
	        var dlg = event.dialog;
	        switch (event.name) {
	          case 'okClick':
	            var modulesTable = getIxTableById('modulesTable');
	            
	            for (var i = 0, len = event.results.modules.length; i < len; i++) {
	              var moduleId = event.results.modules[i].id;
	              var moduleName = event.results.modules[i].name;
	              var index = getModuleRowIndex(modulesTable, moduleId);
	              if (index == -1) {
	                modulesTable.addRow([moduleName, -1, 0, '', '', '', moduleId, -1]);
	              }
	            }
	            if (getIxTableById('modulesTable').getRowCount() > 0) {
	              $('noModulesAddedMessageContainer').setStyle({
	                display: 'none'
	              });
	            }
	          break;
	        }
	      });
	      dialog.open();
	    }
      
      function openSearchCoursesDialog() {
        
        var selectedCourses = new Array();
        var coursesTable = getIxTableById('coursesTable');
        for (var i = 0; i < coursesTable.getRowCount() - 1; i++) {
          var courseName = coursesTable.getCellValue(i, coursesTable.getNamedColumnIndex('name'));
          var courseId = coursesTable.getCellValue(i, coursesTable.getNamedColumnIndex('courseId'));
          selectedCourses.push({
            name: courseName,
            id: courseId
          });
        }
        
        var studentId = ${studentProject.student.id}; 
  
        var dialog = new IxDialog({
          id : 'searchCoursesDialog',
          contentURL : GLOBAL_contextPath + '/projects/searchstudentprojectcoursesdialog.page?studentId=' + studentId,
          centered : true,
          showOk : true,
          showCancel : true,
          title : '<fmt:message key="projects.searchStudentProjectCoursesDialogTitle"/>',
          okLabel : '<fmt:message key="projects.searchStudentProjectCoursesDialog.okLabel"/>', 
          cancelLabel : '<fmt:message key="projects.searchStudentProjectCoursesDialog.cancelLabel"/>' 
        });
        
        dialog.setSize("800px", "600px");
        dialog.addDialogListener(function(event) {
          var dlg = event.dialog;
          switch (event.name) {
            case 'okClick':
              var coursesTable = getIxTableById('coursesTable');
              
              for (var i = 0, len = event.results.courses.length; i < len; i++) {
                var courseId = event.results.courses[i].id;
                var moduleId = event.results.courses[i].moduleId;
                var courseName = event.results.courses[i].name;
                var participationType = event.results.courses[i].participationType;
                var beginDate = event.results.courses[i].beginDate;
                var endDate = event.results.courses[i].endDate;
                
                if (!participationType)
                  participationType = '<fmt:message key="projects.editStudentProject.coursesTableStudentsParticipationTypeNone"/>';
                
                var index = getCourseRowIndex(coursesTable, courseId);
                if (index == -1) {
                  coursesTable.addRow([
	                  courseName,
	                  participationType,
	                  beginDate||'',
	                  endDate||'',
	                  '',
	                  '',
	                  moduleId,
	                  courseId,
	                  -1]);
                }
                
                var moduleTables = getIxTableById('modulesTable');
                var moduleTablesRow = getModuleRowIndex(moduleTables, moduleId);
                if (moduleTablesRow >= 0) { 
                  moduleTables.hideRow(moduleTablesRow);
                }

              }
              if (getIxTableById('coursesTable').getRowCount() > 0) {
                $('noCoursesAddedMessageContainer').setStyle({
                  display: 'none'
                });
              }
            break;
          }
        });
        dialog.open();
      }
  
      function getCourseRowIndex(table, courseId) {
        for (var i = 0; i < table.getRowCount(); i++) {
          var tableCourseId = table.getCellValue(i, table.getNamedColumnIndex('courseId'));
          if (tableCourseId == courseId) {
            return i;
          }
        }
        return -1;
      }
  
      function getModuleRowIndex(table, moduleId) {
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
            paramName: 'name'
          }, {
            header : '<fmt:message key="projects.editStudentProject.moduleTableStudyTermHeader"/>',
            right : 8 + 22 + 8 + 22 + 8 + 22 + 8 + 100 + 8,
            width: 100,
            dataType : 'select',
            paramName: 'academicTerm',
            editable: true,
            overwriteColumnValues : true,
            options: [
              {text: "", value: -1}
              <c:if test="${not empty academicTerms}">,</c:if>
              <c:forEach var="academicTerm" items="${academicTerms}" varStatus="vs">
                {text: "${academicTerm.name}", value: ${academicTerm.id}}
                <c:if test="${not vs.last}">,</c:if>
              </c:forEach>
            ]
          }, {
            header : '<fmt:message key="projects.editStudentProject.moduleTableOptionalityHeader"/>',
            right : 8 + 22 + 8 + 22 + 8 + 22 + 8,
            width: 100,
            dataType : 'select',
            paramName: 'optionality',
            editable: true,
            overwriteColumnValues : true,
            options: [
              {text: '<fmt:message key="projects.editStudentProject.optionalityMandatory"/>', value: 'MANDATORY'},
              {text: '<fmt:message key="projects.editStudentProject.optionalityOptional"/>', value: 'OPTIONAL'}
            ]
          }, {
            width: 22,
            right: 8 + 22 + 8 + 22 + 8,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
            tooltip: '<fmt:message key="projects.editStudentProject.moduleTableEditModuleRowTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
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
              var table = event.tableObject;
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
                    table.hideRow(row);
                    
                    var moduleId = table.getCellValue(row, table.getNamedColumnIndex('moduleId'));
                    var courseId = event.results.courseId;
                    var name = event.results.name;
                    var beginDate = event.results.beginDate;
                    var endDate = event.results.endDate;
                    var participationType = event.results.participationType;

                    getIxTableById('coursesTable').addRow([name, participationType, beginDate, endDate, '', '', moduleId, courseId, -1]);
                  
                    $('noCoursesAddedMessageContainer').setStyle({
                      display: 'none'
                    });
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
              event.tableObject.deleteRow(event.row);
              if (event.tableObject.getRowCount() == 0) {
                $('noModulesAddedMessageContainer').setStyle({
                  display: ''
                });
              }
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
            right: 8 + 22 + 8 + 22 + 8 + 150 + 8 + 150 + 8 + 150 + 8,
            dataType : 'text',
            editable: false,
            paramName: 'name'
          }, {
            header : '<fmt:message key="projects.editStudentProject.coursesTableStudentsParticipationTypeHeader"/>',
            right: 8 + 22 + 8 + 22 + 8 + 150 + 8 + 150 + 8,
            width : 150,
            dataType : 'text',
            editable: false,
            paramName: 'name'
          }, {
            header : '<fmt:message key="projects.editStudentProject.coursesTableBeginDateHeader"/>',
            right: 8 + 22 + 8 + 22 + 8 + 150 + 8,
            width : 150,
            dataType : 'date',
            editable: false
          }, {
            header : '<fmt:message key="projects.editStudentProject.coursesTableEndDateHeader"/>',
            width: 150,
            right : 8 + 22 + 8 + 22 + 8,
            dataType : 'date',
            editable: false
          }, {
            width: 22,
            right: 8 + 22 + 8,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
            tooltip: '<fmt:message key="projects.editStudentProject.coursesTableEditCourseRowTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var courseId = table.getCellValue(event.row, table.getNamedColumnIndex('courseId'));
              redirectTo(GLOBAL_contextPath + '/courses/editcourse.page?course=' + courseId);
            } 
          }, {
            width: 22,
            right: 8,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/list-remove.png',
            tooltip: '<fmt:message key="projects.editStudentProject.coursesTableDeleteRowTooltip"/>',
            onclick: function (event) {
              var moduleId = event.tableObject.getCellValue(event.row, event.tableObject.getNamedColumnIndex('moduleId'));
              var moduleTables = getIxTableById('modulesTable');
              var moduleTablesRow = getModuleRowIndex(moduleTables, moduleId);
              if (moduleTablesRow >= 0) { 
                moduleTables.showRow(moduleTablesRow);
              }
              
              event.tableObject.deleteRow(event.row);
              
              if (event.tableObject.getRowCount() == 0) {
                $('noCoursesAddedMessageContainer').setStyle({
                  display: ''
                });
              }
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
        
        JSONRequest.request("projects/getstudentprojectmodules.json", {
          parameters: {
            studentProject: ${studentProject.id}
          },
          onSuccess: function (jsonResponse) {
            var studentProjectModules = jsonResponse.studentProjectModules;
            for (var i = 0; i < studentProjectModules.length; i++) {
              var rowId = modulesTable.addRow([
                  studentProjectModules[i].name,
                  studentProjectModules[i].academicTermId,
                  studentProjectModules[i].optionality,
                  '',
                  '',
                  '',
                  studentProjectModules[i].moduleId,
                  studentProjectModules[i].id]);
              
              if (studentProjectModules[i].hasCourseEquivalent) {
                modulesTable.hideRow(rowId);
              }
            }
            
            if(modulesTable.getRowCount() > 0){
              $('noModulesAddedMessageContainer').setStyle({
                display: 'none'
              });
            }
          } 
        });
        
        JSONRequest.request("projects/getstudentprojectcourses.json", {
          parameters: {
            studentProject: ${studentProject.id}
          },
          onSuccess: function (jsonResponse) {
            var studentProjectCourses = jsonResponse.studentProjectCourses;
            
            for (var i = 0; i < studentProjectCourses.length; i++) {
              var participationType = studentProjectCourses[i].participationType;
              if (!participationType) {
                participationType = '<fmt:message key="projects.editStudentProject.coursesTableStudentsParticipationTypeNone"/>';
              }
              
              coursesTable.addRow([
                  studentProjectCourses[i].name,
                  participationType,
                  studentProjectCourses[i].beginDate,
                  studentProjectCourses[i].endDate,
                  '',
                  '',
                  studentProjectCourses[i].moduleId,
                  studentProjectCourses[i].courseId,
                  studentProjectCourses[i].id]);
            }
            
            if(coursesTable.getRowCount() > 0){
              $('noCoursesAddedMessageContainer').setStyle({
                display: 'none'
              });
            }
          } 
        });
        
        var basicTabRelatedActionsHoverMenu = new IxHoverMenu($('basicTabRelatedActionsHoverMenuContainer'), {
          text: '<fmt:message key="projects.editStudentProject.basicTabRelatedActionsLabel"/>'
        });
        basicTabRelatedActionsHoverMenu.addItem(new IxHoverMenuLinkItem({
          iconURL: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
          text: '<fmt:message key="projects.editStudentProject.basicTabRelatedActionsEditStudentLabel"/>',
          link: '../students/editstudent.page?abstractStudent=${studentProject.student.abstractStudent.id}'  
        }));
      }

    </script>
  </head>
  <body onLoad="onLoad(event);" ix:enabledrafting="true">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
    
    <h1 class="genericPageHeader"><fmt:message key="projects.editStudentProject.pageTitle" /></h1>
    
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
	          
	          <div class="genericFormSection">
	            <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                  <jsp:param name="titleLocale" value="projects.editStudentProject.studentTitle"/>
                  <jsp:param name="helpLocale" value="projects.editStudentProject.studentHelp"/>
                </jsp:include>
	            <div>${studentProject.student.firstName} ${studentProject.student.lastName}</div>
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
	          
	          <div id="modulesContainer">
	            <div id="modulesTableContainer"></div>
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