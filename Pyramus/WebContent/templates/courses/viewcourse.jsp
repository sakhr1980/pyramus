<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title><fmt:message key="courses.viewCourse.pageTitle" /></title>
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
      // Course components
      
      function setupComponentsTable() {
        var componentsTable = new IxTable($('viewCourseComponentsTableContainer'), {
          id : "componentsTable",
          columns : [{
            header : '<fmt:message key="courses.viewCourse.componentsTableNameHeader"/>',
            left : 8,
            width : 236,
            dataType: 'text',
            editable: false,
            paramName: 'name'
          }, {
            header : '<fmt:message key="courses.viewCourse.componentsTableLengthHeader"/>',
            left : 248,
            width : 30,
            dataType : 'number',
            editable: false,
            paramName: 'length'
          }, {
            header : '<fmt:message key="courses.viewCourse.componentsTableDescriptionHeader"/>',
            left: 290,
            right : 30,
            dataType: 'text',
            editable: false,
            paramName: 'description'
          }]
        });
        
        <c:forEach var="component" items="${courseComponents}">
          componentsTable.addRow([
            '${fn:replace(component.name, "'", "\\'")}',
            ${component.length.units},
            '${fn:replace(component.description, "'", "\\'")}'
          ]);
        </c:forEach>
      }
     
      function setupStudentsTable() {
        var studentsTable = new IxTable($('viewCourseStudentsTableContainer'), {
          id : "studentsTable",
          columns : [{
            width: 30,
            left: 8,
            dataType: 'button',
            paramName: 'studentInfoButton',
            imgsrc: GLOBAL_contextPath + '/gfx/info.png',
            tooltip: '<fmt:message key="courses.viewCourse.studentsTableStudentInfoTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var abstractStudentId = table.getCellValue(event.row, table.getNamedColumnIndex('abstractStudentId'));
              var button = table.getCellEditor(event.row, table.getNamedColumnIndex('studentInfoButton'));
              openStudentInfoPopupOnElement(button, abstractStudentId);
            } 
          }, {
            header : '<fmt:message key="courses.viewCourse.studentsTableNameHeader"/>',
            left : 38,
            right : 900,
            dataType : 'text',
            paramName: 'studentName',
            editable: false
          }, {
            header : '<fmt:message key="courses.viewCourse.studentsTableStudyProgrammeHeader"/>',
            width: 140,
            right : 752,
            dataType : 'text', 
            paramName: 'studyProgramme',
            editable: false
          }, {
            header : '<fmt:message key="courses.viewCourse.studentsTableParticipationTypeHeader"/>',
            width: 200,
            right : 546,
            dataType : 'text',
            editable: false,
            paramName: 'participationType'
          }, {
            header : '<fmt:message key="courses.viewCourse.studentsTableEnrolmentDateHeader"/>',
            width: 200,
            right : 338,
            dataType: 'date',
            editable: false,
            paramName: 'enrolmentDate'
          }, {
            header : '<fmt:message key="courses.viewCourse.studentsTableEnrolmentTypeHeader"/>',
            width: 174,
            right : 146,
            dataType: 'text', 
            editable: false,
            paramName: 'enrolmentType'
          }, {
            header : '<fmt:message key="courses.viewCourse.studentsTableLodgingHeader"/>',
            width: 100,
            right : 68,
            dataType: 'text', 
            editable: false,
            paramName: 'lodging'
          }, {
            dataType: 'hidden', 
            paramName: 'abstractStudentId'
          }, {
            width: 30,
            right: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/eye.png',
            tooltip: '<fmt:message key="courses.viewCourse.studentsTableViewStudentTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var abstractStudentId = table.getCellValue(event.row, table.getNamedColumnIndex('abstractStudentId'));
              redirectTo(GLOBAL_contextPath + '/students/viewstudent.page?abstractStudent=' + abstractStudentId);
            }
          }, {
            width: 30,
            right: 00,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
            tooltip: '<fmt:message key="courses.viewCourse.studentsTableEditStudentTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var abstractStudentId = table.getCellValue(event.row, table.getNamedColumnIndex('abstractStudentId'));
              redirectTo(GLOBAL_contextPath + '/students/editstudent.page?abstractStudent=' + abstractStudentId);
            }
          }]        
        });

        <c:forEach var="courseStudent" items="${courseStudents}">
          <c:choose>
            <c:when test="${courseStudent.lodging}">
              <c:set var="lodgingText"><fmt:message key="courses.viewCourse.studentsTableLodgingYes"/></c:set>
            </c:when>
            <c:otherwise>
              <c:set var="lodgingText"><fmt:message key="courses.viewCourse.studentsTableLodgingNo"/></c:set>
            </c:otherwise>
          </c:choose>

          <c:choose>
            <c:when test="${courseStudent.student.studyProgramme ne null}">
              <c:set var="studyProgrammeName">${fn:escapeXml(courseStudent.student.studyProgramme.name)}</c:set>
            </c:when>
            <c:otherwise>
              <c:set var="studyProgrammeName"><fmt:message key="courses.viewCourse.studentsTableNoStudyProgramme"/></c:set>
            </c:otherwise>
          </c:choose>
          
          studentsTable.addRow([
            "", 
            "${fn:escapeXml(courseStudent.student.lastName)}, ${fn:escapeXml(courseStudent.student.firstName)}", 
            "${studyProgrammeName}",
            "${fn:escapeXml(courseStudent.participationType.name)}",
            ${courseStudent.enrolmentTime.time}, 
            "${fn:escapeXml(courseStudent.courseEnrolmentType.name)}",
            "${lodgingText}",
            ${courseStudent.student.abstractStudent.id},
            '',
            ''
          ]);
        </c:forEach>
      }

      function setupRelatedCommands() {
        var basicRelatedActionsHoverMenu = new IxHoverMenu($('basicRelatedActionsHoverMenuContainer'), {
          text: '<fmt:message key="courses.viewCourse.basicTabRelatedActionsLabel"/>'
        });
    
        basicRelatedActionsHoverMenu.addItem(new IxHoverMenuClickableItem({
          iconURL: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
          text: '<fmt:message key="courses.viewCourse.editCourseRelatedActionLabel"/>',
          onclick: function (event) {
            redirectTo(GLOBAL_contextPath + '/courses/editcourse.page?course=${course.id}');
          }
        }));          
      }
      
      // onLoad

      function onLoad(event) {
        var tabControl = new IxProtoTabs($('tabs'));
        setupRelatedCommands();
        setupComponentsTable();
        setupStudentsTable();
      }

    </script>
  </head>
  <body onload="onLoad(event);">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
    <h1 class="genericPageHeader"><fmt:message key="courses.viewCourse.pageTitle" /></h1>
  
    <div class="genericFormContainer">
      <div class="tabLabelsContainer" id="tabs">
        <a class="tabLabel" href="#basic"><fmt:message key="courses.viewCourse.basicTabTitle" /></a>
        <a class="tabLabel" href="#components"><fmt:message key="courses.viewCourse.componentsTabTitle" /></a> 
        <a class="tabLabel" href="#students"><fmt:message key="courses.viewCourse.studentsTabTitle" /></a>
      </div>
        
        <div id="basic" class="tabContent">
          <div id="basicRelatedActionsHoverMenuContainer" class="tabRelatedActionsContainer"></div>
          <div class="genericFormSection">
            <jsp:include page="/templates/generic/fragments/formtitle.jsp">
              <jsp:param name="titleLocale" value="courses.viewCourse.moduleTitle" />
              <jsp:param name="helpLocale" value="courses.viewCourse.moduleHelp" />
            </jsp:include>
            <div>${course.module.name}</div>
          </div>
          
          <div class="genericFormSection">
            <jsp:include page="/templates/generic/fragments/formtitle.jsp">
              <jsp:param name="titleLocale" value="courses.viewCourse.nameTitle" />
              <jsp:param name="helpLocale" value="courses.viewCourse.nameHelp" />
            </jsp:include> 
            
            <c:choose>
              <c:when test="${fn:length(course.nameExtension) gt 0}">
                <div>${course.name} (${course.nameExtension})</div>
              </c:when>
              <c:otherwise>
                <div>${course.name}</div>
              </c:otherwise>
            </c:choose>
          </div>
  
          <div class="genericFormSection">
            <jsp:include page="/templates/generic/fragments/formtitle.jsp">
              <jsp:param name="titleLocale" value="courses.viewCourse.stateTitle" />
              <jsp:param name="helpLocale" value="courses.viewCourse.stateHelp" />
            </jsp:include>
            <div>${course.state.name}</div>
          </div>
          
          <c:choose>
            <c:when test="${fn:length(course.courseEducationTypes) gt 0}">
              <div class="genericFormSection">
                <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                  <jsp:param name="titleLocale" value="courses.viewCourse.educationTypesTitle" />
                  <jsp:param name="helpLocale" value="courses.viewCourse.educationTypesHelp" />
                </jsp:include>
                
                <c:forEach var="courseEducationType" items="${course.courseEducationTypes}">
                  <c:forEach var="courseEducationSubtype" items="${courseEducationType.courseEducationSubtypes}">
                    <div>${courseEducationType.educationType.name} - ${courseEducationSubtype.educationSubtype.name}</div>
                  </c:forEach>
                </c:forEach>
              </div>
            </c:when>
          </c:choose>
  
          <div class="genericFormSection">
            <jsp:include page="/templates/generic/fragments/formtitle.jsp">
              <jsp:param name="titleLocale" value="courses.viewCourse.subjectTitle" />
              <jsp:param name="helpLocale" value="courses.viewCourse.subjectHelp" />
            </jsp:include>
            <div>${course.subject.name}</div>
          </div>
  
          <c:choose>
            <c:when test="${course.courseNumber ne null}">
              <div class="genericFormSection">
                <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                  <jsp:param name="titleLocale" value="courses.viewCourse.courseNumberTitle" />
                  <jsp:param name="helpLocale" value="courses.viewCourse.courseNumberHelp" />
                </jsp:include>
                <div>${course.courseNumber}</div>
              </div>
            </c:when>
          </c:choose> 
          
          <c:choose>
            <c:when test="${course.beginDate ne null}">
              <div class="genericFormSection">
                <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                  <jsp:param name="titleLocale" value="courses.viewCourse.beginsTitle" />
                  <jsp:param name="helpLocale" value="courses.viewCourse.beginsHelp" />
                </jsp:include>
                <div><fmt:formatDate value="${course.beginDate}" /></div>
              </div>
            </c:when>
          </c:choose> 
          
          <c:choose>
            <c:when test="${course.endDate ne null}">
              <div class="genericFormSection">
                <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                  <jsp:param name="titleLocale" value="courses.viewCourse.endsTitle" />
                  <jsp:param name="helpLocale" value="courses.viewCourse.endsHelp" />
                </jsp:include>
                <div><fmt:formatDate value="${course.endDate}" /></div>
              </div>
            </c:when>
          </c:choose> 
          
          <c:choose>
            <c:when test="${course.courseLength ne null}">
              <div class="genericFormSection">
                <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                  <jsp:param name="titleLocale" value="courses.viewCourse.lengthTitle" />
                  <jsp:param name="helpLocale" value="courses.viewCourse.lengthHelp" />
                </jsp:include>
                <div>${course.courseLength.units} ${course.courseLength.unit.name}</div>
              </div>
            </c:when>
          </c:choose> 
          
          <c:choose>
            <c:when test="${course.localTeachingDays gt 0}">
              <div class="genericFormSection">
                <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="courses.viewCourse.localTeachingDaysTitle" />
                <jsp:param name="helpLocale" value="courses.viewCourse.localTeachingDaysHelp" />
              </jsp:include>
              <div>${course.localTeachingDays}</div>
            </div>
          </c:when>
        </c:choose> 
        
        <c:choose>
          <c:when test="${course.distanceTeachingDays gt 0}">
            <div class="genericFormSection">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="courses.viewCourse.distanceTeachingDaysTitle" />
                <jsp:param name="helpLocale" value="courses.viewCourse.distanceTeachingDaysHelp" />
              </jsp:include>
              <div>${course.distanceTeachingDays}</div>
            </div>
          </c:when>
        </c:choose> 
        
        <c:choose>
          <c:when test="${course.distanceTeachingDays gt 0}">
            <div class="genericFormSection">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="courses.viewCourse.teachingHoursTitle" />
                <jsp:param name="helpLocale" value="courses.viewCourse.teachingHoursHelp" />
              </jsp:include>
              <div>${course.teachingHours}</div>
            </div>
          </c:when>
        </c:choose>
        
        <div class="genericFormSection">
          <jsp:include page="/templates/generic/fragments/formtitle.jsp">
            <jsp:param name="titleLocale" value="courses.viewCourse.personnelTitle" />
            <jsp:param name="helpLocale" value="courses.viewCourse.personnelHelp" />
          </jsp:include>
          <div>
            <c:forEach var="courseUser" items="${course.courseUsers}">
              <div>${courseUser.user.fullName} - ${courseUser.role.name}</div>
            </c:forEach>
          </div>
        </div>        
  
        <div class="genericFormSection">
          <jsp:include page="/templates/generic/fragments/formtitle.jsp">
            <jsp:param name="titleLocale" value="courses.viewCourse.descriptionTitle" />
            <jsp:param name="helpLocale" value="courses.viewCourse.descriptionHelp" />
          </jsp:include>
          <div>${course.description}</div>
        </div>
      </div>
  
      <div id="components" class="tabContent hiddenTab">
        <div id="viewCourseComponentsTableContainer"></div>
        <c:choose>
          <c:when test="${fn:length(courseComponents) le 0}">
            <div class="genericTableNotAddedMessageContainer">
              <fmt:message key="courses.viewCourse.noCourseComponents" /></div>
          </c:when>
        </c:choose>
      </div>
  
      <div id="students" class="tabContent hiddenTab">
        <div id="viewCourseStudentsTableContainer"></div>
  
        <c:choose>
          <c:when test="${fn:length(courseStudents) le 0}">
            <div class="genericTableNotAddedMessageContainer">
              <fmt:message key="courses.viewCourse.noCourseStudents" />
            </div>
          </c:when>
        </c:choose>
      </div>
    </div>
  
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>
