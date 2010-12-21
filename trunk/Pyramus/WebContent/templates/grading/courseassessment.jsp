<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title><fmt:message key="users.editUser.pageTitle"></fmt:message></title>
    <jsp:include page="/templates/generic/head_generic.jsp"></jsp:include>
    <jsp:include page="/templates/generic/tabs_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/table_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/scriptaculous_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonrequest_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonform_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/validation_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/hovermenu_support.jsp"></jsp:include>
    
    <script type="text/javascript">

      function onLoad(event) {
        var tabControl = new IxProtoTabs($('tabs'));
        
        setupRelatedCommandsBasic();
      }

      function setupRelatedCommandsBasic() {
        var relatedActionsHoverMenu = new IxHoverMenu($('basicRelatedActionsHoverMenuContainer'), {
          text: '<fmt:message key="users.editUser.basicTabRelatedActionsLabel"/>'
        });
    
        relatedActionsHoverMenu.addItem(new IxHoverMenuClickableItem({
          iconURL: GLOBAL_contextPath + '/gfx/icons/16x16/actions/edit-work-resource.png',
          text: '<fmt:message key="users.editUser.basicTabRelatedActionCreateResourceLabel"/>',
          onclick: function (event) {
            redirectTo(GLOBAL_contextPath + '/resources/createworkresource.page?name=' + encodeURIComponent('${user.lastName}, ${user.firstName}'));
          }
        }));
      }
      
      function openSearchUsersDialog() {
        var dialog = new IxDialog({
          id : 'searchUsersDialog',
          contentURL : GLOBAL_contextPath + '/users/searchuserdialog.page',
          centered : true,
          showOk : true,
          showCancel : true,
          disableOk: true,
          title : '<fmt:message key="users.searchUserDialog.searchUserDialogTitle"/>',
          okLabel : '<fmt:message key="users.searchUserDialog.okLabel"/>', 
          cancelLabel : '<fmt:message key="users.searchUserDialog.cancelLabel"/>' 
        });
        
        dialog.setSize("400px", "600px");
        dialog.addDialogListener(function(event) {
          var dlg = event.dialog;
          switch (event.name) {
            case 'okClick':
              $("assessingUserId").value = event.results.user.id;
              $("assessingUserName").innerHTML = event.results.user.name;
            break;
          }
        });
        dialog.open();
      }
    </script>
  </head>

  <body onload="onLoad(event);">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
  
    <h1 class="genericPageHeader"><fmt:message key="users.editUser.pageTitle" /></h1>
  
    <div id="editUserEditFormContainer"> 
      <div class="genericFormContainer"> 

        <form action="savecourseassessment.json" method="post" ix:jsonform="true" ix:useglasspane="true">
          <div class="tabLabelsContainer" id="tabs">
            <a class="tabLabel" href="#basic">
              <fmt:message key="users.editUser.tabLabelEditUser"/>
            </a>
          </div>
    
          <div id="basic" class="tabContent">    
            <input type="hidden" name="courseStudentId" value="${courseStudent.id}"/>
            
            <div id="basicRelatedActionsHoverMenuContainer" class="tabRelatedActionsContainer"></div>
            
            <div class="genericFormSection">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="users.editUser.firstNameTitle"/>
                <jsp:param name="helpLocale" value="users.editUser.firstNameHelp"/>
              </jsp:include>                  
              ${fn:escapeXml(courseStudent.student.fullName)}
            </div>
  
            <div class="genericFormSection">  
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="users.editUser.lastNameTitle"/>
                <jsp:param name="helpLocale" value="users.editUser.lastNameHelp"/>
              </jsp:include>                  
              ${fn:escapeXml(courseStudent.course.name)}
            </div>
          
            <c:choose>
              <c:when test="${assessment != null}">
                <c:set var="assessingUserId">${assessment.assessingUser.id}</c:set>
                <c:set var="assessingUserName">${fn:escapeXml(assessment.assessingUser.fullName)}</c:set>
                <c:set var="gradeId">${assessment.grade.id}</c:set>
                <c:set var="gradeDate">${assessment.date.time}</c:set>
                <c:set var="verbalAssessment">${assessment.verbalAssessment}</c:set>
              </c:when>
              <c:otherwise>
                <c:set var="assessingUserId">${loggedUserId}</c:set>
                <c:set var="assessingUserName">${fn:escapeXml(loggedUserName)}</c:set>
                <c:set var="gradeId"></c:set>
                <c:set var="gradeDate"></c:set>
                <c:set var="verbalAssessment"></c:set>
              </c:otherwise>
            </c:choose>
            
            <div class="genericFormSection">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="users.editUser.lastNameTitle"/>
                <jsp:param name="helpLocale" value="users.editUser.lastNameHelp"/>
              </jsp:include>
              <input type="hidden" id="assessingUserId" name="assessingUserId" value="${assessingUserId}"/>
              <span id="assessingUserName">${assessingUserName}</span>
              <span><img src="${pageContext.request.contextPath}/gfx/icons/16x16/actions/search.png" onclick="openSearchUsersDialog();"/></span>
            </div>
            
            <div class="genericFormSection">  
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="users.editUser.usernameTitle"/>
                <jsp:param name="helpLocale" value="users.editUser.usernameHelp"/>
              </jsp:include>
              <input type="text" name="assessmentDate" ix:datefieldid="assessmentDate" ix:datefield="true" value="${gradeDate}"/>
            </div>

            <div class="genericFormSection">  
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="users.editUser.usernameTitle"/>
                <jsp:param name="helpLocale" value="users.editUser.usernameHelp"/>
              </jsp:include>
              <select name="gradeId">
                <option value="-1"></option>
              
                <c:forEach var="gradingScale" items="${gradingScales}">
	                <optgroup label="${fn:escapeXml(gradingScale.name)}">
                    <c:forEach var="grade" items="${gradingScale.grades}">
                      <c:choose>
	                      <c:when test="${gradeId eq grade.id}">
	                        <option value="${grade.id}" selected="selected">${fn:escapeXml(grade.name)}</option>
	                      </c:when>
	                      <c:otherwise>
                          <option value="${grade.id}">${fn:escapeXml(grade.name)}</option>
	                      </c:otherwise>
	                    </c:choose>
                    </c:forEach>
	                </optgroup>
                </c:forEach>
              </select>
            </div>

            <div class="genericFormSection">  
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="users.editUser.usernameTitle"/>
                <jsp:param name="helpLocale" value="users.editUser.usernameHelp"/>
              </jsp:include>
              <textarea name="verbalAssessment" cols="60" rows="6" ix:cktoolbar="courseGradeText" ix:ckeditor="true">${verbalAssessment}</textarea>
            </div>
          </div>

          <div class="genericFormSubmitSectionOffTab">
            <input type="submit" value="<fmt:message key="users.editUser.saveButton"/>" class="formvalid">
          </div>

        </form>
      </div>
    </div>  

    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>