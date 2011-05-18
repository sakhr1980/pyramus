<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>
      <fmt:message key="courses.viewCourse.pageTitle">
        <fmt:param value="${course.name}"/>
      </fmt:message>
    </title>
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
              var table = event.tableComponent;
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
            editable: false,
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
            header : '<fmt:message key="courses.viewCourse.studentsTableStudyProgrammeHeader"/>',
            width: 140,
            right : 752,
            dataType : 'text', 
            paramName: 'studyProgramme',
            editable: false,
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
            header : '<fmt:message key="courses.viewCourse.studentsTableParticipationTypeHeader"/>',
            width: 200,
            right : 546,
            dataType : 'text',
            editable: false,
            paramName: 'participationType',
            contextMenu: [
              {
                text: '<fmt:message key="generic.filter.byValue"/>',
                onclick: new IxTable_ROWSTRINGFILTER()
              },
              {
                text: '<fmt:message key="generic.filter.clear"/>',
                onclick: new IxTable_ROWCLEARFILTER()
              }
            ],            
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
            header : '<fmt:message key="courses.viewCourse.studentsTableEnrolmentDateHeader"/>',
            width: 200,
            right : 338,
            dataType: 'date',
            editable: false,
            paramName: 'enrolmentDate',
            contextMenu: [
              {
                text: '<fmt:message key="generic.filter.earlier"/>',
                onclick: new IxTable_ROWDATEFILTER(true)
              },
              {
                text: '<fmt:message key="generic.filter.later"/>',
                onclick: new IxTable_ROWDATEFILTER(false)
              },
              {
                text: '<fmt:message key="generic.filter.clear"/>',
                onclick: new IxTable_ROWCLEARFILTER()
              }
            ],            
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
            paramName: 'lodging',
            contextMenu: [
              {
                text: '<fmt:message key="generic.filter.byValue"/>',
                onclick: new IxTable_ROWSTRINGFILTER()
              },
              {
                text: '<fmt:message key="generic.filter.clear"/>',
                onclick: new IxTable_ROWCLEARFILTER()
              }
            ],            
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
            dataType: 'hidden', 
            paramName: 'abstractStudentId'
          }, {
            width: 30,
            right: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/eye.png',
            tooltip: '<fmt:message key="courses.viewCourse.studentsTableViewStudentTooltip"/>',
            onclick: function (event) {
              var table = event.tableComponent;
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
              var table = event.tableComponent;
              var abstractStudentId = table.getCellValue(event.row, table.getNamedColumnIndex('abstractStudentId'));
              redirectTo(GLOBAL_contextPath + '/students/editstudent.page?abstractStudent=' + abstractStudentId);
            }
          }]        
        });

        var rows = new Array();
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

          <c:if test="${!courseStudent.student.active}">
            <c:set var="studyProgrammeName">${studyProgrammeName} *</c:set>
          </c:if>
          
          rows.push([
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
        studentsTable.addRows(rows);
        if (studentsTable.getRowCount() > 0) {
          $('viewCourseStudentsTotalValue').innerHTML = studentsTable.getRowCount(); 
        }
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

        basicRelatedActionsHoverMenu.addItem(new IxHoverMenuClickableItem({
          iconURL: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
          text: '<fmt:message key="courses.viewCourse.manageCourseAssessmentsRelatedActionLabel"/>',
          onclick: function (event) {
            redirectTo(GLOBAL_contextPath + '/courses/managecourseassessments.page?course=${course.id}');
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
    <h1 class="genericPageHeader">
      <fmt:message key="courses.viewCourse.pageTitle">
        <fmt:param value="${course.name}"/>
      </fmt:message>
    </h1>
  
    <div class="genericFormContainer">
      <div class="tabLabelsContainer" id="tabs">
        <a class="tabLabel" href="#basic"><fmt:message key="courses.viewCourse.basicTabTitle" /></a>
        <a class="tabLabel" href="#components"><fmt:message key="courses.viewCourse.componentsTabTitle" /></a> 
        <a class="tabLabel" href="#students"><fmt:message key="courses.viewCourse.studentsTabTitle" /></a>
      </div>
        
        <div id="basic" class="tabContent">
          <div id="basicRelatedActionsHoverMenuContainer" class="tabRelatedActionsContainer"></div>
          
          <!--  Course Basic Info Starts -->
          <div class="genericViewInfoWapper" id="courseViewBasicInfoWrapper">
          
            <div class="genericFormSection">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="courses.viewCourse.moduleTitle" />
                <jsp:param name="helpLocale" value="courses.viewCourse.moduleHelp" />
              </jsp:include>
              <div class="genericViewFormDataText">${course.module.name}</div>
            </div>
            
            <div class="genericFormSection">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="courses.viewCourse.nameTitle" />
                <jsp:param name="helpLocale" value="courses.viewCourse.nameHelp" />
              </jsp:include> 
              <div class="genericViewFormDataText">
              <c:choose>
                <c:when test="${fn:length(course.nameExtension) gt 0}">
                  <div>${course.name} (${course.nameExtension})</div>
                </c:when>
                <c:otherwise>
                  <div>${course.name}</div>
                </c:otherwise>
              </c:choose>
              </div>
            </div>
            
            <div class="genericFormSection">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="courses.viewCourse.descriptionTitle" />
                <jsp:param name="helpLocale" value="courses.viewCourse.descriptionHelp" />
              </jsp:include>
              <div class="genericViewFormDataText">${course.description}</div>
            </div>

            <c:forEach var="cDesc" items="${courseDescriptions}">
              <div class="genericFormSection">
                <c:set var="descLocaleText">
                  <fmt:message key="courses.viewCourse.courseDescriptionTitle"/> (${cDesc.category.name})
                </c:set>
              
                <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                  <jsp:param name="titleText" value="${descLocaleText}" />
                  <jsp:param name="helpLocale" value="courses.viewCourse.courseDescriptionHelp" />
                </jsp:include>
                <div class="genericViewFormDataText">${cDesc.description}</div>
              </div>
            </c:forEach>
            
          </div>
          <!--  Course Basic Info Ends -->
          
          <!--  Course Detailed Info Starts -->
          <div class="genericViewInfoWapper" id="courseViewDetailedInfoWrapper">
            
            <c:choose>
              <c:when test="${not empty course.tags}">
                <div class="genericFormSection">
                  <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                    <jsp:param name="titleLocale" value="courses.viewCourse.tagsTitle" />
                    <jsp:param name="helpLocale" value="courses.viewCourse.tagsHelp" />
                  </jsp:include>
                  <div class="genericViewFormDataText">
                  <c:forEach var="tag" items="${course.tags}" varStatus="vs">
                    <c:out value="${tag.text}"/>
                    <c:if test="${not vs.last}"><c:out value=" "/></c:if>
                  </c:forEach>
                  </div>
                </div>
              </c:when>
            </c:choose> 
    
            <div class="genericFormSection">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="courses.viewCourse.stateTitle" />
                <jsp:param name="helpLocale" value="courses.viewCourse.stateHelp" />
              </jsp:include>
              <div class="genericViewFormDataText">${course.state.name}</div>
            </div>
            
            <c:choose>
              <c:when test="${fn:length(course.courseEducationTypes) gt 0}">
                <div class="genericFormSection">
                  <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                    <jsp:param name="titleLocale" value="courses.viewCourse.educationTypesTitle" />
                    <jsp:param name="helpLocale" value="courses.viewCourse.educationTypesHelp" />
                  </jsp:include>
                  <div class="genericViewFormDataText">
                  <c:forEach var="courseEducationType" items="${course.courseEducationTypes}">
                    <c:forEach var="courseEducationSubtype" items="${courseEducationType.courseEducationSubtypes}">
                      <div>${courseEducationType.educationType.name} - ${courseEducationSubtype.educationSubtype.name}</div>
                    </c:forEach>
                  </c:forEach>
                  </div>
                </div>
              </c:when>
            </c:choose>
    
            <div class="genericFormSection">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="courses.viewCourse.subjectTitle" />
                <jsp:param name="helpLocale" value="courses.viewCourse.subjectHelp" />
              </jsp:include>
              <div class="genericViewFormDataText">${course.subject.name}</div>
            </div>
    
            <c:choose>
              <c:when test="${course.courseNumber ne null}">
                <div class="genericFormSection">
                  <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                    <jsp:param name="titleLocale" value="courses.viewCourse.courseNumberTitle" />
                    <jsp:param name="helpLocale" value="courses.viewCourse.courseNumberHelp" />
                  </jsp:include>
                  <div class="genericViewFormDataText">${course.courseNumber}</div>
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
                  <div class="genericViewFormDataText"><fmt:formatDate value="${course.beginDate}" /></div>
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
                  <div class="genericViewFormDataText"><fmt:formatDate value="${course.endDate}" /></div>
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
                  <div class="genericViewFormDataText">${course.courseLength.units} ${course.courseLength.unit.name}</div>
                </div>
              </c:when>
            </c:choose> 
            
            <c:choose>
              <c:when test="${course.assessingHours gt 0}">
                <div class="genericFormSection">
                  <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                    <jsp:param name="titleLocale" value="courses.viewCourse.assessingHoursTitle" />
                    <jsp:param name="helpLocale" value="courses.viewCourse.assessingHoursHelp" />
                  </jsp:include>
                  <div class="genericViewFormDataText">${course.assessingHours}</div>
                </div>
              </c:when>
            </c:choose> 
            
            <c:choose>
              <c:when test="${course.planningHours gt 0}">
                <div class="genericFormSection">
                  <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                    <jsp:param name="titleLocale" value="courses.viewCourse.planningHoursTitle" />
                    <jsp:param name="helpLocale" value="courses.viewCourse.planningHoursHelp" />
                  </jsp:include>
                  <div class="genericViewFormDataText">${course.planningHours}</div>
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
                <div class="genericViewFormDataText">${course.localTeachingDays}</div>
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
                <div class="genericViewFormDataText">${course.distanceTeachingDays}</div>
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
                <div class="genericViewFormDataText">${course.teachingHours}</div>
              </div>
            </c:when>
          </c:choose>
          
          <div class="genericFormSection">
            <jsp:include page="/templates/generic/fragments/formtitle.jsp">
              <jsp:param name="titleLocale" value="courses.viewCourse.personnelTitle" />
              <jsp:param name="helpLocale" value="courses.viewCourse.personnelHelp" />
            </jsp:include>
            <div class="genericViewFormDataText">
              <c:forEach var="courseUser" items="${courseUsers}">
                <div>${courseUser.user.lastName}, ${courseUser.user.firstName} - ${courseUser.role.name}</div>
              </c:forEach>
            </div>
          </div>        
  
        </div>
        <!--  Course Detailed Info Ends -->

      </div>
  
      <div id="components" class="tabContent hiddenTab">
        <c:choose>
          <c:when test="${fn:length(courseComponents) le 0}">
            <div class="genericTableNotAddedMessageContainer">
              <fmt:message key="courses.viewCourse.noCourseComponents" />
            </div>
          </c:when>
          <c:otherwise>
            <div class="viewCourseComponentsContainer">
              <div class="viewCourseComponentsTitlesContainer">
                <div class="viewCourseComponentsNameTitle">
                  <fmt:message key="courses.viewCourse.componentsNameHeader"/>
                </div>
                <div class="viewCourseComponentsLengthTitle">
                  <fmt:message key="courses.viewCourse.componentsLengthHeader"/>
                </div>
                <div class="viewCourseComponentsDescriptionTitle">
                  <fmt:message key="courses.viewCourse.componentsDescriptionHeader"/>
                </div>
              </div>
            
              <div class="viewCourseComponentsInnerContainer">
                <c:forEach var="component" items="${courseComponents}" varStatus="componentsVs">
                  <div class="viewCourseComponentContainer">
                    <div class="viewCourseComponentHeaderRow">
                      <div class="viewCourseComponentName">${fn:escapeXml(component.name)}</div>
                      <div class="viewCourseComponentLengthTitle">${fn:escapeXml(component.length.units)}</div>
                      <div class="viewCourseComponentDescriptionTitle">${fn:escapeXml(component.description)}</div>
                    </div>
                    <div class="viewCourseComponentResourcesWrapper">
                      <c:forEach var="componentResource" items="${component.resources}">
                        <div class="viewCourseComponentResourcesContainer">
                          <div class="viewCourseComponentResourceName">${fn:escapeXml(componentResource.resource.name)}</div>
                          
                          <c:choose>
                            <c:when test="${componentResource.resource.resourceType eq 'MATERIAL_RESOURCE'}">
                              <div class="viewCourseComponentResourceUsage">${componentResource.usagePercent / 100}</div>
                              <div class="viewCourseComponentResourceUsageUnit"><fmt:message key="courses.viewCourse.componentsMaterialResourceUnit"/></div>
                              <div class="viewCourseComponentResourceResourceType"><fmt:message key="courses.viewCourse.componentsMaterialResource"/></div>
                            </c:when>
                            <c:when test="${componentResource.resource.resourceType eq 'WORK_RESOURCE'}">
                              <div class="viewCourseComponentResourceUsage">${componentResource.usagePercent}</div>
                              <div class="viewCourseComponentResourceUsageUnit"><fmt:message key="courses.viewCourse.componentsWorkResourceUnit"/></div>
                              <div class="viewCourseComponentResourceResourceType"><fmt:message key="courses.viewCourse.componentsWorkResource"/></div>
                            </c:when>
                          </c:choose>
                          
                          <div class="viewCourseComponentResourceCategory">${fn:escapeXml(componentResource.resource.category.name)}</div>
                        </div>
                      </c:forEach>
                    </div>
                  </div>
                </c:forEach>
              </div>
            </div>
          
          </c:otherwise>
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
          <c:otherwise>
            <div id="viewCourseStudentsTotalContainer">
              <fmt:message key="courses.viewCourse.studentsTotal"/> <span id="viewCourseStudentsTotalValue"></span>
            </div>
          </c:otherwise>
        </c:choose>
      </div>
    </div>
  
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>
