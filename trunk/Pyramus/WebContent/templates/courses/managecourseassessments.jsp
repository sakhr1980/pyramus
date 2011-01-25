<%@page import="fi.pyramus.domainmodel.courses.CourseComponent"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/ix" prefix="ix"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title><fmt:message key="courses.manageCourseAssessments.pageTitle" /></title>
    <jsp:include page="/templates/generic/head_generic.jsp"></jsp:include>
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

      function setupStudentsTable() {
        var studentsTable = new IxTable($('studentsTable'), {
          id : "studentsTable",
          columns : [{
            width: 22,
            left: 8,
            dataType: 'button',
            paramName: 'studentInfoButton',
            imgsrc: GLOBAL_contextPath + '/gfx/info.png',
            tooltip: '<fmt:message key="courses.manageCourseAssessments.studentsTableStudentInfoTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var abstractStudentId = table.getCellValue(event.row, table.getNamedColumnIndex('abstractStudentId'));
              var button = table.getCellEditor(event.row, table.getNamedColumnIndex('studentInfoButton'));
              openStudentInfoPopupOnElement(button, abstractStudentId);
            } 
          }, {
            left: 38,
            width: 22,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
            tooltip: '<fmt:message key="courses.manageCourseAssessments.studentsTableEditTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var modifiedCol = studentsTable.getNamedColumnIndex('modified');
              
              if (table.getCellValue(event.row, modifiedCol) == 0) {
                var gradeCol = studentsTable.getNamedColumnIndex('gradeId');
                var participationCol = studentsTable.getNamedColumnIndex('participationType');
                var assessingUserCol = studentsTable.getNamedColumnIndex('assessingUserId');
                var assessmentDateCol = studentsTable.getNamedColumnIndex('assessmentDate');
  
                table.setCellEditable(event.row, gradeCol, table.isCellEditable(event.row, gradeCol) == false);
                table.setCellEditable(event.row, participationCol, table.isCellEditable(event.row, participationCol) == false);
                table.setCellEditable(event.row, assessingUserCol, table.isCellEditable(event.row, assessingUserCol) == false);
                table.setCellEditable(event.row, assessmentDateCol, table.isCellEditable(event.row, assessmentDateCol) == false);

                if (table.getCellValue(event.row, assessmentDateCol) == '')
                  table.setCellValue(event.row, assessmentDateCol, new Date().getTime());
                if (table.getCellValue(event.row, assessingUserCol) == '') {
                  table.setCellValue(event.row, assessingUserCol, '${loggedUserId}');
                  IxTableControllers.getController('autoComplete').setDisplayValue(table.getCellEditor(event.row, assessingUserCol), '${fn:replace(loggedUserName, "'", "\\'")}');
                }

                table.setCellValue(event.row, table.getNamedColumnIndex('modified'), 1);
              }
            }
          }, {
            header : '<fmt:message key="courses.manageCourseAssessments.studentsTableNameHeader"/>',
            left : 8 + 22 + 8 + 22 + 8,
            right : 8 + 140 + 8 + 180 + 8 + 160 + 8 + 120 + 8 + 100 + 8,
            dataType : 'text',
            paramName: 'studentName',
            editable: false
          }, {
            header : '<fmt:message key="courses.manageCourseAssessments.studentsTableStudyProgrammeHeader"/>',
            width: 160,
            right : 8 + 140 + 8 + 180 + 8 + 160 + 8 + 120 + 8,
            dataType : 'text',
            editable: false,
            paramName: 'studyProgrammeName'
          }, {
            header : '<fmt:message key="courses.manageCourseAssessments.studentsTableGradeHeader"/>',
            width: 120,
            right : 8 + 140 + 8 + 180 + 8 + 160 + 8,
            dataType : 'select',
            editable: false,
            overwriteColumnValues : true,
            paramName: 'gradeId',
            options: [
              {text: "-", value: -1}
              <c:if test="${fn:length(gradingScales) gt 0}">,</c:if>
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
            header : '<fmt:message key="courses.manageCourseAssessments.studentsTableParticipationTypeHeader"/>',
            width: 160,
            right : 8 + 140 + 8 + 180 + 8,
            dataType : 'select',
            editable: false,
            overwriteColumnValues : true,
            paramName: 'participationType',
            options: [
              <c:forEach var="courseParticipationType" items="${courseParticipationTypes}" varStatus="vs">
                {text: "${courseParticipationType.name}", value: ${courseParticipationType.id}}
                <c:if test="${not vs.last}">,</c:if>
              </c:forEach>
            ]
          }, {
            header : '<fmt:message key="courses.manageCourseAssessments.studentsTableAssessingUserHeader"/>',
            width : 180,
            right: 8 + 140 + 8,
            dataType: 'autoComplete',
            required: true,
            editable: false,
            paramName: 'assessingUserId',
            overwriteColumnValues : true,
            autoCompleteUrl: GLOBAL_contextPath + '/users/usersautocomplete.binary',
            autoCompleteProgressUrl: '${pageContext.request.contextPath}/gfx/progress_small.gif'
          }, {
            header : '<fmt:message key="courses.manageCourseAssessments.studentsTableAssessmentDateHeader"/>',
            width: 140,
            right : 8,
            dataType: 'date',
            editable: false,
            overwriteColumnValues : true,
            paramName: 'assessmentDate'
          }, {
            dataType: 'hidden', 
            paramName: 'courseStudentId'
          }, {
            dataType: 'hidden', 
            paramName: 'abstractStudentId'
          }, {
            dataType: 'hidden', 
            paramName: 'modified'
          }]        
        });

        var rowIndex;
        var userColumnIndex = studentsTable.getNamedColumnIndex('assessingUserId');
        
        <c:forEach var="courseStudent" items="${courseStudents}" varStatus="status">
          rowIndex = studentsTable.addRow([
            '',
            '',
            "${fn:escapeXml(courseStudent.student.lastName)}, ${fn:escapeXml(courseStudent.student.firstName)}", 
            "${fn:escapeXml(courseStudent.student.studyProgramme.name)}", 
            '${assessments[courseStudent.id].grade.id}',
            '${courseStudent.participationType.id}',
            '${assessments[courseStudent.id].assessingUser.id}',
            '${assessments[courseStudent.id].date.time}',
            '${courseStudent.id}',
            '${courseStudent.student.abstractStudent.id}',
            0]);
          IxTableControllers.getController('autoComplete').setDisplayValue(studentsTable.getCellEditor(rowIndex, userColumnIndex), '${fn:replace(assessments[courseStudent.id].assessingUser.fullName, "'", "\\'")}');
        </c:forEach>
      }
      
      function setupRelatedCommands() {
        var basicRelatedActionsHoverMenu = new IxHoverMenu($('relatedActionsHoverMenuContainer'), {
          text: '<fmt:message key="courses.manageCourseAssessments.basicTabRelatedActionsLabel"/>'
        });
    
        basicRelatedActionsHoverMenu.addItem(new IxHoverMenuClickableItem({
          iconURL: GLOBAL_contextPath + '/gfx/eye.png',
          text: '<fmt:message key="courses.manageCourseAssessments.viewCourseRelatedActionLabel"/>',
          onclick: function (event) {
            redirectTo(GLOBAL_contextPath + '/courses/viewcourse.page?course=${course.id}');
          }
        }));
        
        basicRelatedActionsHoverMenu.addItem(new IxHoverMenuClickableItem({
          iconURL: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
          text: '<fmt:message key="courses.manageCourseAssessments.editCourseRelatedActionLabel"/>',
          onclick: function (event) {
            redirectTo(GLOBAL_contextPath + '/courses/editcourse.page?course=${course.id}');
          }
        }));
      }
            
      // onLoad

      function onLoad(event) {
        var tabControl = new IxProtoTabs($('tabs'));
        setupRelatedCommands();
        setupStudentsTable();
      }

    </script>
  </head>
  <body onload="onLoad(event);" ix:enabledrafting="true">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
    
    <h1 class="genericPageHeader"><fmt:message key="courses.manageCourseAssessments.pageTitle" /></h1>
    
    <div id="manageCourseAssessmentsEditFormContainer">
	    <div class="genericFormContainer">
	      <form action="savecourseassessments.json" method="post" ix:jsonform="true" ix:useglasspane="true">
	        <input type="hidden" name="course" value="${course.id}"/>
          <input type="hidden" name="version" value="${course.version}"/>

	        <div class="tabLabelsContainer" id="tabs">
	          <a class="tabLabel" href="#students"><fmt:message key="courses.manageCourseAssessments.studentsTabTitle" /></a>
	        </div>
	
	        <div id="students" class="tabContent">
            <div id="relatedActionsHoverMenuContainer" class="tabRelatedActionsContainer"></div>

            <div class="genericFormSection">                            
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="courses.manageCourseAssessments.courseNameTitle"/>
                <jsp:param name="helpLocale" value="courses.manageCourseAssessments.courseNameHelp"/>
              </jsp:include> 
              <div>${course.name}</div>
            </div>

	          <div class="courseStudentsTableContainer">
              <c:if test="${fn:length(courseStudents) eq 0}">
  	            <div id="noStudentsAddedMessageContainer" class="genericTableNotAddedMessageContainer">
  	              <span><fmt:message key="courses.manageCourseAssessments.noStudentsAddedMessage"/></span>
  	            </div>
              </c:if>
	          
	            <div id="studentsTable"> </div>
	          </div>
	        </div>
	    
	        <div class="genericFormSubmitSectionOffTab">
	          <input type="submit" class="formvalid" value="<fmt:message key="courses.manageCourseAssessments.saveButton"/>">
	        </div>
	      </form>
	    </div>
	  </div>
    
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>
