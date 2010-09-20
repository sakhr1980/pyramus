<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head> 
    <title><fmt:message key="students.viewStudent.pageTitle"></fmt:message></title>
    <jsp:include page="/templates/generic/head_generic.jsp"></jsp:include>
    <jsp:include page="/templates/generic/tabs_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/datefield_support.jsp"></jsp:include>
    
    <jsp:include page="/templates/generic/scriptaculous_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/table_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/dialog_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/hovermenu_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonrequest_support.jsp"></jsp:include>

    <!-- Used to render memo values with line breaks; for some reason this is the only approach that works -->
    <% pageContext.setAttribute("newLineChar", "\n"); %>

    <script type="text/javascript">
      function setupBasicTab(abstractStudentId, studentId, studentFullName, studentArchived) {
        var basicTabRelatedActionsHoverMenu = new IxHoverMenu($('basicTabRelatedActionsHoverMenuContainer.' + studentId), {
          text: '<fmt:message key="students.viewStudent.basicTabRelatedActionsLabel"/>'
        });
    
        basicTabRelatedActionsHoverMenu.addItem(new IxHoverMenuLinkItem({
          iconURL: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
          text: '<fmt:message key="students.viewStudent.basicTabRelatedActionsEditStudentLabel"/>',
          link: GLOBAL_contextPath + '/students/editstudent.page?abstractStudent=' + abstractStudentId  
        }));

        var gradesTabRelatedActionsHoverMenu = new IxHoverMenu($('gradesTabRelatedActionsHoverMenuContainer.' + studentId), {
          text: '<fmt:message key="students.viewStudent.gradesTabRelatedActionsLabel"/>'
        });
        
        gradesTabRelatedActionsHoverMenu.addItem(new IxHoverMenuClickableItem({
          iconURL: GLOBAL_contextPath + '/gfx/list-add.png',
          text: '<fmt:message key="students.viewStudent.gradesTabRelatedActionsAddTransferCreditLabel"/>',
          onclick: function (event) {
            alert("TODO");
          }
        }));
      }

      function setupCoursesTab(studentId) {
        var relatedContainer = $('tabRelatedActionsContainer.' + studentId);
    
        var coursesTable = new IxTable($('coursesTableContainer.' + studentId), {
          id: 'coursesTable.' + studentId,
          columns : [{
            header : '<fmt:message key="students.viewStudent.coursesTableNameHeader"/>',
            left: 8,
            right: 38, 
            dataType: 'text',
            editable: false
          }, {
            dataType: 'hidden',
            paramName: 'courseId'
          }, {
            width: 30,
            right: 00,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/eye.png',
            tooltip: '<fmt:message key="students.viewStudent.courseTableViewTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var courseId = table.getCellValue(event.row, table.getNamedColumnIndex('courseId'));
              redirectTo(GLOBAL_contextPath + '/courses/viewcourse.page?course=' + courseId);
            }
          }]
        });

        return coursesTable;
      }

      function setupTransferCreditsTab(studentId) {
        /* TODO: Oppilaitos */
        
        var transferCreditsTable = new IxTable($('transferCreditsTableContainer.' + studentId), {
          id: 'transferCreditsTable.' + studentId,
          columns : [{
            header : '<fmt:message key="students.viewStudent.transferCreditsTableNameHeader"/>',
            left: 8,
            right: 800, 
            dataType: 'text',
            editable: false
          }, {
            header : '<fmt:message key="students.viewStudent.transferCreditsTableSubjectHeader"/>',
            right: 592, 
            width: 200,
            dataType: 'text',
            editable: false
          }, {
            header : '<fmt:message key="students.viewStudent.transferCreditsTableGradingDateHeader"/>',
            right: 484, 
            width: 100,
            dataType: 'date',
            editable: false
          }, {
            header : '<fmt:message key="students.viewStudent.transferCreditsTableCourseLengthHeader"/>',
            right: 376, 
            width: 100,
            dataType: 'number',
            editable: false
          }, {
            header : '<fmt:message key="students.viewStudent.transferCreditsTableGradeHeader"/>',
            right: 268, 
            width: 100,
            dataType: 'number',
            editable: false
          }, {
            header : '<fmt:message key="students.viewStudent.transferCreditsTableGradingScaleHeader"/>',
            right: 160, 
            width: 100,
            dataType: 'text',
            editable: false
          }, {
            header : '<fmt:message key="students.viewStudent.transferCreditsTableAssessingUserHeader"/>',
            right: 38,
            width: 126,
            dataType: 'text',
            editable: false
          }, {
            width: 30,
            right: 00,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
            tooltip: '<fmt:message key="students.viewStudent.studentTableEditTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var studentId = table.getCellValue(event.row, table.getNamedColumnIndex('studentId'));
              alert("TODO: modify transfer credits");
            }
          }]
        });

        return transferCreditsTable;
      }
      
      function setupCourseAssessmentsTab(studentId) {
        var courseAssesmentsTable = new IxTable($('courseAssessmentsTableContainer.' + studentId), {
          id: 'courseAssessmentsTable.' + studentId,
          columns : [{
            header : '<fmt:message key="students.viewStudent.courseAssessmentsTableNameHeader"/>',
            left: 8,
            right: 800, 
            dataType: 'text',
            editable: false
          }, {
            header : '<fmt:message key="students.viewStudent.courseAssessmentsTableSubjectHeader"/>',
            right: 592, 
            width: 200,
            dataType: 'text',
            editable: false
          }, {
            header : '<fmt:message key="students.viewStudent.courseAssessmentsTableGradingDateHeader"/>',
            right: 484, 
            width: 100,
            dataType: 'date',
            editable: false
          }, {
            header : '<fmt:message key="students.viewStudent.courseAssessmentsTableCourseLengthHeader"/>',
            right: 376, 
            width: 100,
            dataType: 'number',
            editable: false
          }, {
            header : '<fmt:message key="students.viewStudent.courseAssessmentsTableGradeHeader"/>',
            right: 268, 
            width: 100,
            dataType: 'number',
            editable: false
          }, {
            header : '<fmt:message key="students.viewStudent.courseAssessmentsTableGradingScaleHeader"/>',
            right: 160, 
            width: 100,
            dataType: 'text',
            editable: false
          }, {
            header : '<fmt:message key="students.viewStudent.courseAssessmentsTableAssessingUserHeader"/>',
            right: 38,
            width: 126,
            dataType: 'text',
            editable: false
          }, {
            width: 30,
            right: 00,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
            tooltip: '<fmt:message key="students.viewStudent.studentTableEditTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var studentId = table.getCellValue(event.row, table.getNamedColumnIndex('studentId'));
              alert("TODO: modify assesments");
            }
          }]
        });
  
        return courseAssesmentsTable;
      }

      function onLoad(event) {
        var coursesTable;
        var transferCreditsTable;
        var courseAssesmentsTable;
        
        <c:forEach var="student" items="${abstractStudent.students}">
          // Setup basics
          setupBasicTab(${abstractStudent.id}, ${student.id}, '${student.fullName}', ${student.archived}); 

          // Setup course tabs
          coursesTable = setupCoursesTab(${student.id});

          <c:forEach var="studentCourse" items="${courses[student.id]}">
            coursesTable.addRow(['${studentCourse.course.name}', ${studentCourse.course.id}, '']);
          </c:forEach>

          // Setup grade tabs
          transferCreditsTable = setupTransferCreditsTab(${student.id});

          <c:forEach var="studentTransferCredit" items="${transferCredits[student.id]}">
            transferCreditsTable.addRow([
              '${studentTransferCredit.name}',
              '${studentTransferCredit.subject.name}',
              '${studentTransferCredit.date.time}',
              '${studentTransferCredit.length.units}',
              '${studentTransferCredit.grade.name}',
              '${studentTransferCredit.grade.gradingScale.name}',
              '${studentTransferCredit.assessingUser.fullName}',
              '']);
          </c:forEach>

          courseAssesmentsTable = setupCourseAssessmentsTab(${student.id});

          <c:forEach var="studentCourseAssesment" items="${courseAssesments[student.id]}">
             courseAssesmentsTable.addRow([
              '${studentCourseAssesment.course.name}',
              '${studentCourseAssesment.course.subject.name}',
              '${studentCourseAssesment.date.time}',
              '${studentCourseAssesment.course.courseLength.units}',
              '${studentCourseAssesment.grade.name}',
              '${studentCourseAssesment.grade.gradingScale.name}',
              '${studentCourseAssesment.assessingUser.fullName}',
              '']);
          </c:forEach>
        </c:forEach>
        
             
        var tabControl2 = new IxProtoTabs($('studentTabs'));

        <c:forEach var="student" items="${abstractStudent.students}">
          var tabControl = new IxProtoTabs($('tabs.${student.id}'));

          resetEntryForm(${student.id});
        </c:forEach>

        <c:if test="${!empty param.activeTab}">
          tabControl.setActiveTab("${param.activeTab}");  
        </c:if>
      }
  
      function resetEntryForm(studentId) {
        var entryForm = $("newContactEntryForm." + studentId);
        entryForm.entryType.value = '0';
        entryForm.entryCreator.value = '${loggedUserName}';
        var dField = getIxDateField("entryDate." + studentId);
        if (dField != null)
          dField.setTimestamp(new Date().getTime());
        entryForm.entryText.value = '';
      }

      /**
       * 
       *
       * @param event The submit event
       */
      function newContactEntryFormSubmit(event) {
        Event.stop(event);

        var entryForm = Event.element(event);
        JSONRequest.request("students/createnewcontactentry.json", {
          parameters: {
            entryType: entryForm.entryType.value,
            entryCreator: entryForm.entryCreator.value,
            entryDate: entryForm.entryDate.value,
            entryText: entryForm.entryText.value,
            studentId: entryForm.studentId.value
          },
          onSuccess: function (jsonResponse) {
            var studentId = jsonResponse.results.studentId;
            var listDiv = $('studentContactEntryList.' + studentId);
            var newEntryDiv = listDiv.appendChild(Builder.node("div", {id: "studentContactEntryItem"}));

            var results = jsonResponse.results;
            var date = new Date(results.timestamp);
            var dateStr = date.getDate() + "." + (date.getMonth() + 1) + "." + date.getFullYear();

            // TODO: lokalisointi ja tyylittely
            listDiv.appendChild(Builder.node("div", {id: ""}, ['<' + results.type + '> by ' + results.creator + ' at ' + dateStr]));
            listDiv.appendChild(Builder.node("div", {id: ""}, [results.text]));
                                    
            resetEntryForm(studentId);
          } 
        });
      }
    </script>
  </head>

  <body onload="onLoad(event);">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
  
    <h1 class="genericPageHeader"><fmt:message key="students.viewStudent.pageTitle" /></h1>
  
    <div id="viewStudentViewContainer"> 
      <div class="genericFormContainer"> 
        <div class="tabLabelsContainer" id="studentTabs">
          <c:forEach var="student" items="${abstractStudent.students}">
            <a class="tabLabel" href="#student.${student.id}">
              <c:choose>
                <c:when test="${student.studyProgramme == null}">
                   <fmt:message key="students.viewStudent.noStudyProgrammeTabLabel"/>
                </c:when>
                <c:otherwise>
                  ${student.studyProgramme.name}
                </c:otherwise>
              </c:choose>
              <c:if test="${student.archived}">
                *
              </c:if>
            </a>
          </c:forEach>
        </div>
    
        <c:forEach var="student" items="${abstractStudent.students}">
          <div id="student.${student.id}" class="tabContent">    
  
            <div id="viewStudentViewContainer"> 
              <div class="genericFormContainer"> 
                <div class="tabLabelsContainer" id="tabs.${student.id}">
                  <a class="tabLabel" href="#basic.${student.id}">
                    <fmt:message key="students.viewStudent.basicTabLabel"/>
                  </a>
                  <a class="tabLabel" href="#courses.${student.id}">
                    <fmt:message key="students.viewStudent.coursesTabLabel"/>
                  </a>
                  <a class="tabLabel" href="#grades.${student.id}">
                    <fmt:message key="students.viewStudent.gradesTabLabel"/>
                  </a>
                  <a class="tabLabel" href="#contactlog.${student.id}">
                    <fmt:message key="students.viewStudent.contactLogTabLabel"/>
                  </a>
                </div>
            
                <div id="basic.${student.id}" class="tabContent">    
                  <div id="basicTabRelatedActionsHoverMenuContainer.${student.id}" class="tabRelatedActionsContainer"></div>
                  
                  <c:choose>
                    <c:when test="${!empty abstractStudent.birthday}">
                      <div class="genericFormSection">  
                        <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                          <jsp:param name="titleLocale" value="students.viewStudent.birthdayTitle"/>
                          <jsp:param name="helpLocale" value="students.viewStudent.birthdayHelp"/>
                        </jsp:include>                     
                        <fmt:formatDate pattern="dd.MM.yyyy" value="${abstractStudent.birthday}" />
                      </div>
                    </c:when>
                  </c:choose>
              
                  <c:choose>
                    <c:when test="${!empty abstractStudent.socialSecurityNumber}">
                      <div class="genericFormSection"> 
                        <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                          <jsp:param name="titleLocale" value="students.viewStudent.ssecIdTitle"/>
                          <jsp:param name="helpLocale" value="students.viewStudent.ssecIdHelp"/>
                        </jsp:include>                                        
                        ${abstractStudent.socialSecurityNumber}
                      </div>
                    </c:when>
                  </c:choose>
              
                  <div class="genericFormSection">  
                    <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                      <jsp:param name="titleLocale" value="students.viewStudent.genderTitle"/>
                      <jsp:param name="helpLocale" value="students.viewStudent.genderHelp"/>
                    </jsp:include>                                        
                    <c:choose>
                      <c:when test="${abstractStudent.sex != 'FEMALE'}">
                        <fmt:message key="students.viewStudent.genderMaleTitle"/>
                      </c:when>
                      <c:otherwise>
                        <fmt:message key="students.viewStudent.genderFemaleTitle"/>
                      </c:otherwise>
                    </c:choose>
                  </div>
                
                  <div class="genericFormSection">
                    <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                      <jsp:param name="titleLocale" value="students.viewStudent.firstNameTitle"/>
                      <jsp:param name="helpLocale" value="students.viewStudent.firstNameHelp"/>
                    </jsp:include>                                        
                    <div>${student.firstName}</div>
                  </div>
        
                  <div class="genericFormSection">  
                    <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                      <jsp:param name="titleLocale" value="students.viewStudent.lastNameTitle"/>
                      <jsp:param name="helpLocale" value="students.viewStudent.lastNameHelp"/>
                    </jsp:include>                                        
                    <div>${student.lastName}</div>
                  </div>
                 
                  <c:choose>
                    <c:when test="${!empty student.nickname}">
                      <div class="genericFormSection">  
                        <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                          <jsp:param name="titleLocale" value="students.viewStudent.nicknameTitle"/>
                          <jsp:param name="helpLocale" value="students.viewStudent.nicknameHelp"/>
                        </jsp:include>                                        
                        <div>${student.nickname}</div>
                      </div>
                    </c:when>
                  </c:choose> 
        
                  <c:if test="${!empty student.contactInfo.addresses}">
                    <div class="genericFormSection">  
                      <c:forEach var="address" items="${student.contactInfo.addresses}">
                        <div class="genericFormTitle">
                          <div class="genericFormTitleText">
                            <div>${address.contactType.name}</div>
                          </div>
                        </div>
                        <div>${address.name}</div>
                        <div>${address.streetAddress}</div>
                        <div>${address.postalCode} ${address.city}</div>
                        <div>${address.country}</div>
                      </c:forEach>
                    </div>
                  </c:if>
        
                  <div class="genericFormSection">  
                    <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                      <jsp:param name="titleLocale" value="students.viewStudent.emailTitle"/>
                      <jsp:param name="helpLocale" value="students.viewStudent.emailHelp"/>
                    </jsp:include>  
                    <c:forEach var="email" items="${student.contactInfo.emails}">
                      <div>${email.address}</div>
                    </c:forEach>
                  </div>
        
                  <c:choose>
                    <c:when test="${!empty student.contactInfo.phoneNumbers}">
                      <div class="genericFormSection">    
                        <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                          <jsp:param name="titleLocale" value="students.viewStudent.phoneNumberTitle"/>
                          <jsp:param name="helpLocale" value="students.viewStudent.phoneNumberHelp"/>
                        </jsp:include>  
                        <c:forEach var="phone" items="${student.contactInfo.phoneNumbers}">
                          <div>${phone.number}</div>
                        </c:forEach>
                      </div>
                    </c:when>
                  </c:choose>
        
                  <c:choose>
                    <c:when test="${!empty student.municipality}">
                      <div class="genericFormSection">      
                        <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                          <jsp:param name="titleLocale" value="students.viewStudent.municipalityTitle"/>
                          <jsp:param name="helpLocale" value="students.viewStudent.municipalityHelp"/>
                        </jsp:include>  
                        <div>${student.municipality.name}</div>
                      </div>
                    </c:when>
                  </c:choose>
        
                  <c:choose>
                    <c:when test="${!empty student.language}">
                      <div class="genericFormSection">      
                        <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                          <jsp:param name="titleLocale" value="students.viewStudent.languageTitle"/>
                          <jsp:param name="helpLocale" value="students.viewStudent.languageHelp"/>
                        </jsp:include>    
                        <div>${student.language.name}</div>
                      </div>
                    </c:when>
                  </c:choose>

                  <c:choose>
                    <c:when test="${!empty student.nationality}">
                      <div class="genericFormSection">        
                        <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                          <jsp:param name="titleLocale" value="students.viewStudent.nationalityTitle"/>
                          <jsp:param name="helpLocale" value="students.viewStudent.nationalityHelp"/>
                        </jsp:include>    
                        <div>${student.nationality.name}</div>
                      </div>
                    </c:when>
                  </c:choose>
        
                  <c:choose>
                    <c:when test="${!empty student.activityType}">
                      <div class="genericFormSection">          
                        <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                          <jsp:param name="titleLocale" value="students.viewStudent.activityTypeTitle"/>
                          <jsp:param name="helpLocale" value="students.viewStudent.activityTypeHelp"/>
                        </jsp:include>    
                        <div>${student.activityType.name}</div>
                      </div>
                    </c:when>
                  </c:choose>
        
                  <c:choose>
                    <c:when test="${!empty student.examinationType}">
                      <div class="genericFormSection">         
                        <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                          <jsp:param name="titleLocale" value="students.viewStudent.examinationTypeTitle"/>
                          <jsp:param name="helpLocale" value="students.viewStudent.examinationTypeHelp"/>
                        </jsp:include>    
                        <div>${student.examinationType.name}</div>
                      </div>
                    </c:when>
                  </c:choose>
        
                  <c:choose>
                    <c:when test="${!empty student.educationalLevel}">
                      <div class="genericFormSection">         
                        <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                          <jsp:param name="titleLocale" value="students.viewStudent.educationalLevelTitle"/>
                          <jsp:param name="helpLocale" value="students.viewStudent.educationalLevelHelp"/>
                        </jsp:include>      
                        <div>${student.educationalLevel.name}</div>
                      </div>
                    </c:when>
                  </c:choose>

                  <c:choose>
                    <c:when test=">${!empty student.education}">
                      <div class="genericFormSection">             
                        <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                          <jsp:param name="titleLocale" value="students.viewStudent.educationTitle"/>
                          <jsp:param name="helpLocale" value="students.viewStudent.educationHelp"/>
                        </jsp:include>
                        <div>${student.education}</div>
                      </div>
                    </c:when>
                  </c:choose>
        
                  <c:choose>
                    <c:when test="${!empty student.studyProgramme}">
                      <div class="genericFormSection">             
                        <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                          <jsp:param name="titleLocale" value="students.viewStudent.studyProgrammeTitle"/>
                          <jsp:param name="helpLocale" value="students.viewStudent.studyProgrammeHelp"/>
                        </jsp:include>
                        <div>${student.studyProgramme.name}</div>
                      </div>
                    </c:when>
                  </c:choose>

                  <c:choose>
                    <c:when test="${fn:length(studentGroups[student.id]) gt 0}">
                      <div class="genericFormSection">             
                        <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                          <jsp:param name="titleLocale" value="students.viewStudent.studentGroupTitle"/>
                          <jsp:param name="helpLocale" value="students.viewStudent.studentGroupHelp"/>
                        </jsp:include>
                        
                        <div>
	                        <c:forEach var="studentGroup" items="${studentGroups[student.id]}" varStatus="sgStat">
	                          ${studentGroup.name}<c:if test="${!sgStat.last}">, </c:if>
	                        </c:forEach>
                        </div>
                      </div>
                    </c:when>
                  </c:choose>

                  <c:choose>
                    <c:when test="${!empty student.school}">
                      <div class="genericFormSection">             
                        <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                          <jsp:param name="titleLocale" value="students.viewStudent.schoolTitle"/>
                          <jsp:param name="helpLocale" value="students.viewStudent.schoolHelp"/>
                        </jsp:include>
                        <div>${student.school.name}</div>
                      </div>
                    </c:when>
                  </c:choose>
        
                  <c:choose>
                    <c:when test="${!empty student.additionalInfo}">
                      <div class="genericFormSection">               
                        <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                          <jsp:param name="titleLocale" value="students.viewStudent.additionalInformationTitle"/>
                          <jsp:param name="helpLocale" value="students.viewStudent.additionalInformationHelp"/>
                        </jsp:include>
                        <div>${student.additionalInfo}</div>
                      </div>
                    </c:when>
                  </c:choose>

                  <c:choose>
                    <c:when test="${!empty student.contactInfo.additionalInfo}">
                      <div class="genericFormSection">                 
                        <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                          <jsp:param name="titleLocale" value="students.viewStudent.additionalContactInfoTitle"/>
                          <jsp:param name="helpLocale" value="students.viewStudent.additionalContactInfoHelp"/>
                        </jsp:include>
                        <div>${fn:replace(student.contactInfo.additionalInfo, newLineChar, "<br/>")}</div>
                      </div>
                    </c:when>
                  </c:choose>

                  <c:choose>
                    <c:when test="${!empty student.previousStudies}">
                      <div class="genericFormSection">                   
                        <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                          <jsp:param name="titleLocale" value="students.viewStudent.previousStudiesTitle"/>
                          <jsp:param name="helpLocale" value="students.viewStudent.previousStudiesHelp"/>
                        </jsp:include>
                        <div>${student.previousStudies}</div>
                      </div>
                    </c:when>
                  </c:choose>
        
                  <c:choose>
                    <c:when test="${!empty student.studyTimeEnd}">
                      <div class="genericFormSection">                     
                        <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                          <jsp:param name="titleLocale" value="students.viewStudent.studyTimeEndTitle"/>
                          <jsp:param name="helpLocale" value="students.viewStudent.studyTimeEndHelp"/>
                        </jsp:include>
                        <div>${student.studyTimeEnd}</div>
                      </div>
                    </c:when>
                  </c:choose>
        
                  <c:choose>
                    <c:when test="${!empty student.studyStartDate}">
                      <div class="genericFormSection">                       
                        <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                          <jsp:param name="titleLocale" value="students.viewStudent.studyStartDateTitle"/>
                          <jsp:param name="helpLocale" value="students.viewStudent.studyStartDateHelp"/>
                        </jsp:include>
                        <div>${student.studyStartDate}</div>
                      </div>
                    </c:when>
                  </c:choose>
        
                  <c:choose>
                    <c:when test="${!empty student.studyEndDate}">
                      <div class="genericFormSection">                        
                        <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                          <jsp:param name="titleLocale" value="students.viewStudent.studyEndDateTitle"/>
                          <jsp:param name="helpLocale" value="students.viewStudent.studyEndDateHelp"/>
                        </jsp:include> 
                        <div>${student.studyEndDate}</div>
                      </div>
                    </c:when>
                  </c:choose>
        
                  <c:choose>
                    <c:when test="${!empty student.studyEndReason}">
                      <div class="genericFormSection">                          
                        <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                          <jsp:param name="titleLocale" value="students.viewStudent.studyEndReasonTitle"/>
                          <jsp:param name="helpLocale" value="students.viewStudent.studyEndReasonHelp"/>
                        </jsp:include> 
                        <div>${student.studyEndReason.name}</div>
                      </div>
                    </c:when>
                  </c:choose>
        
                  <c:choose>
                    <c:when test="${!empty student.studyEndText}">
                      <div class="genericFormSection">                            
                        <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                          <jsp:param name="titleLocale" value="students.viewStudent.studyEndTextTitle"/>
                          <jsp:param name="helpLocale" value="students.viewStudent.studyEndTextHelp"/>
                        </jsp:include> 
                        <div>${student.studyEndText}</div>
                      </div>
                    </c:when>
                  </c:choose>
                  
                  <div class="genericFormSection">                            
                    <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                      <jsp:param name="titleLocale" value="students.viewStudent.lodgingTitle"/>
                      <jsp:param name="helpLocale" value="students.viewStudent.lodgingHelp"/>
                    </jsp:include> 
                    <div>
                      <c:choose>
                        <c:when test="${student.lodging}">
                          <fmt:message key="students.viewStudent.lodgingYes"/>
                        </c:when>
                        <c:otherwise>
                          <fmt:message key="students.viewStudent.lodgingNo"/>
                        </c:otherwise> 
                      </c:choose>
                    </div>
                  </div>
                </div>
        
                <div id="courses.${student.id}" class="tabContent">
                  <div id="coursesTabRelatedActionsHoverMenuContainer.${student.id}" class="tabRelatedActionsContainer"></div>
                  
                  <div class="genericFormSection">                              
                    <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                      <jsp:param name="titleLocale" value="students.viewStudent.coursesTitle"/>
                      <jsp:param name="helpLocale" value="students.viewStudent.coursesHelp"/>
                    </jsp:include> 
                    <div><div id="coursesTableContainer.${student.id}"></div></div>
                  </div>
                </div>
  
                <div id="grades.${student.id}" class="tabContent">
                  <div id="gradesTabRelatedActionsHoverMenuContainer.${student.id}" class="tabRelatedActionsContainer"></div>
                  
                  <div class="genericFormSection">                                
                    <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                      <jsp:param name="titleLocale" value="students.viewStudent.courseAssessmentsTitle"/>
                      <jsp:param name="helpLocale" value="students.viewStudent.courseAssessmentsHelp"/>
                    </jsp:include> 
                    <div><div id="courseAssessmentsTableContainer.${student.id}"></div></div>
                  </div>
                  
                  <div class="genericFormSection">                                  
                    <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                      <jsp:param name="titleLocale" value="students.viewStudent.transferCreditsTitle"/>
                      <jsp:param name="helpLocale" value="students.viewStudent.transferCreditsHelp"/>
                    </jsp:include> 
                    <div><div id="transferCreditsTableContainer.${student.id}"></div></div>
                  </div>
                </div> 
        
                <div id="contactlog.${student.id}" class="tabContent">
                  <form method="post" id="newContactEntryForm.${student.id}" onsubmit="newContactEntryFormSubmit(event);">
                    <input type="hidden" name="studentId" value="${student.id}"/>
        
                    <div id="studentContactEntryList.${student.id}">
                      <c:forEach var="contactEntry" items="${contactEntries[student.id]}">
                        <div id="studentContactEntryItem">
                          <div>
                            <c:choose>
                              <c:when test="${contactEntry.type eq 'OTHER'}">
                                &lt;<fmt:message key="students.viewStudent.contactEntry.types.other"/>&gt;
                              </c:when>
                              <c:when test="${contactEntry.type eq 'LETTER'}">
                                &lt;<fmt:message key="students.viewStudent.contactEntry.types.letter"/>&gt;
                              </c:when>
                              <c:when test="${contactEntry.type eq 'EMAIL'}">
                                &lt;<fmt:message key="students.viewStudent.contactEntry.types.email"/>&gt;
                              </c:when>
                              <c:when test="${contactEntry.type eq 'PHONE'}">
                                &lt;<fmt:message key="students.viewStudent.contactEntry.types.phone"/>&gt;
                              </c:when>
                              <c:when test="${contactEntry.type eq 'CHATLOG'}">
                                &lt;<fmt:message key="students.viewStudent.contactEntry.types.chatlog"/>&gt;
                              </c:when>
                              <c:when test="${contactEntry.type eq 'SKYPE'}">
                                &lt;<fmt:message key="students.viewStudent.contactEntry.types.skype"/>&gt;
                              </c:when>
                              <c:when test="${contactEntry.type eq 'FACE2FACE'}">
                                &lt;<fmt:message key="students.viewStudent.contactEntry.types.face2face"/>&gt;
                              </c:when>
                            </c:choose>
                            by ${contactEntry.creator} 
                            at <fmt:formatDate pattern="dd.MM.yyyy" value="${contactEntry.entryDate}" />
                          </div>              
                          <div>
                            ${contactEntry.text}
                          </div>
                        </div>
                      </c:forEach>
                    </div>
        
                    <div class="genericFormSection">                            
                      <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                        <jsp:param name="titleLocale" value="students.viewStudent.contactEntry.typeTitle"/>
                        <jsp:param name="helpLocale" value="students.viewStudent.contactEntry.typeHelp"/>
                      </jsp:include> 
                      <select name="entryType">
                        <option value="0"><fmt:message key="students.viewStudent.contactEntry.types.other"/></option>
                        <option value="1"><fmt:message key="students.viewStudent.contactEntry.types.letter"/></option>
                        <option value="2"><fmt:message key="students.viewStudent.contactEntry.types.email"/></option>
                        <option value="3"><fmt:message key="students.viewStudent.contactEntry.types.phone"/></option>
                        <option value="4"><fmt:message key="students.viewStudent.contactEntry.types.chatlog"/></option>
                        <option value="5"><fmt:message key="students.viewStudent.contactEntry.types.skype"/></option>
                        <option value="6"><fmt:message key="students.viewStudent.contactEntry.types.face2face"/></option>
                      </select>
                    </div>            
                    <div class="genericFormSection">                            
                      <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                        <jsp:param name="titleLocale" value="students.viewStudent.contactEntry.fromTitle"/>
                        <jsp:param name="helpLocale" value="students.viewStudent.contactEntry.fromHelp"/>
                      </jsp:include> 
                      <input type="text" name="entryCreator"/>
                    </div> 
                    <div class="genericFormSection">                            
                      <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                        <jsp:param name="titleLocale" value="students.viewStudent.contactEntry.dateTitle"/>
                        <jsp:param name="helpLocale" value="students.viewStudent.contactEntry.dateHelp"/>
                      </jsp:include> 
                      <input type="text" name="entryDate" ix:datefieldid="entryDate.${student.id}" ix:datefield="true"/>
                    </div>
                    <div class="genericFormSection">                            
                      <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                        <jsp:param name="titleLocale" value="students.viewStudent.contactEntry.textTitle"/>
                        <jsp:param name="helpLocale" value="students.viewStudent.contactEntry.textHelp"/>
                      </jsp:include> 
                      <textarea name="entryText" cols="60" rows="6"></textarea>
                    </div>            
                    <div>
                      <input type="submit" name="newContactLogEntryButton" value="<fmt:message key="students.viewStudent.newContactLogEntry"/>">
                    </div>            
                  </form>
                </div>
              </div>
            </div>  
          </div>
        </c:forEach>
      </div>
    </div>  

    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>