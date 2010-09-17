<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title><fmt:message key="students.viewStudentGroup.pageTitle" /></title>
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
      function setupStudentsTable() {
        var studentsTable = new IxTable($('studentsTable'), {
          id : "studentsTable",
          columns : [{
            width: 30,
            left: 8,
            dataType: 'button',
            paramName: 'studentInfoButton',
            imgsrc: GLOBAL_contextPath + '/gfx/info.png',
            tooltip: '<fmt:message key="students.viewStudentGroup.studentsTableStudentInfoTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var abstractStudentId = table.getCellValue(event.row, table.getNamedColumnIndex('abstractStudentId'));
              var button = table.getCellEditor(event.row, table.getNamedColumnIndex('studentInfoButton'));
              openStudentInfoPopupOnElement(button, abstractStudentId);
            } 
          }, {
            header : '<fmt:message key="students.viewStudentGroup.studentsTableNameHeader"/>',
            left : 38,
            dataType : 'text',
            paramName: 'studentName',
            editable: false
          }, {
            header : '<fmt:message key="students.viewStudentGroup.studentsTableStudyProgrammeHeader"/>',
            width: 140,
            left : 230,
            dataType : 'text', 
            paramName: 'studyProgramme',
            editable: false
          }, {
            dataType: 'hidden', 
            paramName: 'abstractStudentId'
          }, {
            width: 30,
            right: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/eye.png',
            tooltip: '<fmt:message key="students.viewStudentGroup.studentsTableViewStudentTooltip"/>',
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
            tooltip: '<fmt:message key="students.viewStudentGroup.studentsTableEditStudentTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var abstractStudentId = table.getCellValue(event.row, table.getNamedColumnIndex('abstractStudentId'));
              redirectTo(GLOBAL_contextPath + '/students/editstudent.page?abstractStudent=' + abstractStudentId);
            }
          }]        
        });

        <c:forEach var="student" items="${studentGroupStudents}">
          <c:choose>
            <c:when test="${student.student.studyProgramme ne null}">
              <c:set var="studyProgrammeName">${fn:escapeXml(student.student.studyProgramme.name)}</c:set>
            </c:when>
            <c:otherwise>
              <c:set var="studyProgrammeName"><fmt:message key="students.viewStudentGroup.studentsTableNoStudyProgramme"/></c:set>
            </c:otherwise>
          </c:choose>
          
          studentsTable.addRow([
            "", 
            "${fn:escapeXml(student.student.lastName)}, ${fn:escapeXml(student.student.firstName)}", 
            "${studyProgrammeName}",
            ${student.student.abstractStudent.id},
            '',
            ''
          ]);
        </c:forEach>
      }

      function setupRelatedCommands() {
        var basicRelatedActionsHoverMenu = new IxHoverMenu($('basicRelatedActionsHoverMenuContainer'), {
          text: '<fmt:message key="students.viewStudentGroup.basicTabRelatedActionsLabel"/>'
        });
    
        basicRelatedActionsHoverMenu.addItem(new IxHoverMenuClickableItem({
          iconURL: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
          text: '<fmt:message key="students.viewStudentGroup.editStudentGroupRelatedActionLabel"/>',
          onclick: function (event) {
            redirectTo(GLOBAL_contextPath + '/students/editstudentgroup.page?studentgroup=${studentGroup.id}');
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
  <body onload="onLoad(event);">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
    <h1 class="genericPageHeader"><fmt:message key="students.viewStudentGroup.pageTitle" /></h1>
  
    <div class="genericFormContainer">
      <div class="tabLabelsContainer" id="tabs">
        <a class="tabLabel" href="#basic"><fmt:message key="students.viewStudentGroup.basicTabTitle" /></a>
        <a class="tabLabel" href="#students"><fmt:message key="students.viewStudentGroup.studentsTabTitle" /></a>
      </div>
        
      <div id="basic" class="tabContent">
        <div id="basicRelatedActionsHoverMenuContainer" class="tabRelatedActionsContainer"></div>
        
        <div class="genericFormSection">
          <jsp:include page="/templates/generic/fragments/formtitle.jsp">
            <jsp:param name="titleLocale" value="students.viewStudentGroup.nameTitle" />
            <jsp:param name="helpLocale" value="students.viewStudentGroup.nameHelp" />
          </jsp:include> 
          
          <div>${studentGroup.name}</div>
        </div>
        
        <div class="genericFormSection">
          <jsp:include page="/templates/generic/fragments/formtitle.jsp">
            <jsp:param name="titleLocale" value="students.viewStudentGroup.beginsTitle" />
            <jsp:param name="helpLocale" value="students.viewStudentGroup.beginsHelp" />
          </jsp:include>
          <div><fmt:formatDate pattern="dd.MM.yyyy" value="${studentGroup.beginDate}" /></div>
        </div>

        <div class="genericFormSection">
          <jsp:include page="/templates/generic/fragments/formtitle.jsp">
            <jsp:param name="titleLocale" value="students.viewStudentGroup.descriptionTitle" />
            <jsp:param name="helpLocale" value="students.viewStudentGroup.descriptionHelp" />
          </jsp:include>
          <div>${fn:replace(studentGroup.description, newLineChar, "<br/>")}</div>
        </div>

        <div class="genericFormSection">
          <jsp:include page="/templates/generic/fragments/formtitle.jsp">
            <jsp:param name="titleLocale" value="students.viewStudentGroup.usersTitle" />
            <jsp:param name="helpLocale" value="students.viewStudentGroup.usersHelp" />
          </jsp:include>
          <div>
            <c:forEach var="user" items="${studentGroup.users}">
              <div>${user.user.fullName}</div>
            </c:forEach>
          </div>
        </div>          
      </div>
  
      <div id="students" class="tabContent hiddenTab">
        <div id="studentsTable"></div>
  
        <c:choose>
          <c:when test="${fn:length(studentGroup.students) le 0}">
            <div class="genericTableNotAddedMessageContainer">
              <fmt:message key="students.viewStudentGroup.noStudents" />
            </div>
          </c:when>
        </c:choose>
      </div>
    </div>
  
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>