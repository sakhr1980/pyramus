<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
  <head>
    <title><fmt:message key="courses.searchCourses.pageTitle"/></title>
    <jsp:include page="/templates/generic/head_generic.jsp"></jsp:include>
    <jsp:include page="/templates/generic/scriptaculous_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/table_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/dialog_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonrequest_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/scriptaculous_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/tabs_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/searchnavigation_support.jsp"></jsp:include>
    
    <script type="text/javascript">
 
      function doSearch(page) {
        var searchForm = $("searchForm");
        JSONRequest.request("courses/searchcourses.json", {
          parameters: {
            text: searchForm.text.value,
            name: searchForm.name.value,
            tags: searchForm.tags.value,
            nameExtension: searchForm.nameExtension.value,
            state: searchForm.state.value,
            subject: searchForm.subject.value,
            description: searchForm.description.value,
            timeframeMode: searchForm.timeframeMode.value,
            timeframeStart: searchForm.timeframeStart.value,
            timeframeEnd: searchForm.timeframeEnd.value,
            activeTab: searchForm.activeTab.value,
            page: page
          },
          onSuccess: function (jsonResponse) {
            var resultsTable = getIxTableById('searchResultsTable');
            resultsTable.deleteAllRows();
            var results = jsonResponse.results;
            for (var i = 0; i < results.length; i++) {
              var name = results[i].name;
              var nameExt = results[i].nameExtension;
              if (nameExt && (nameExt.length > 0))
                name += ' (' + nameExt + ')';
              resultsTable.addRow([name, results[i].beginDate, results[i].endDate, '', '', '', results[i].id]);
            }
            getSearchNavigationById('searchResultsNavigation').setTotalPages(jsonResponse.pages);
            getSearchNavigationById('searchResultsNavigation').setCurrentPage(jsonResponse.page);
            $('searchResultsStatusMessageContainer').innerHTML = jsonResponse.statusMessage;
            $('searchResultsWrapper').setStyle({
              display: ''
            });
          } 
        });
      }
  
      /**
      * Invoked when the user submits the search form. We cancel the submit event
      * and delegate the work to the doSearch method.
      *
      * @param event The search form submit event
      */
     function onSearchCourses(event) {
       Event.stop(event);
       doSearch(0);
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
       
       $('searchForm').activeTab.value = tabControl.getActiveTab();
       tabControl.addListener(function (event) {
          if ((event.action == 'tabActivated')||(event.action == 'tabInitialized')) {
            $('searchForm').activeTab.value = event.name;
            $('activeTab').value = event.name;
          } 
        });
        <c:choose>
          <c:when test="${!empty param.activeTab}">
            tabControl.setActiveTab("${param.activeTab}");  
          </c:when>
        </c:choose>

        new IxSearchNavigation($('searchResultsPagesContainer'), {
          id: 'searchResultsNavigation',
          maxNavigationPages: 19,
          onclick: function(event) {
            doSearch(event.page);
          }
        });

        var searchResultsTable = new IxTable($('searchResultsTableContainer'), {
          id: 'searchResultsTable',
          columns : [ {
            header : '<fmt:message key="courses.searchCourses.courseTableNameHeader"/>',
            left: 8,
            right: 506,
            dataType : 'text',
            editable: false,
            paramName: 'name'
          }, {
            header : '<fmt:message key="courses.searchCourses.courseTableBeginDateHeader"/>',
            right: 218,
            width : 180,
            dataType : 'date',
            editable: false
          }, {
            header : '<fmt:message key="courses.searchCourses.courseTableEndDateHeader"/>',
            width: 150,
            right : 90,
            dataType : 'date',
            editable: false
          }, {
            width: 30,
            right : 60,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/eye.png',
            tooltip: '<fmt:message key="courses.searchCourses.courseTableViewRowTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var courseId = table.getCellValue(event.row, table.getNamedColumnIndex('courseId'));
              redirectTo(GLOBAL_contextPath + '/courses/viewcourse.page?course=' + courseId);
            } 
          }, {
            width: 30,
            right : 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
            tooltip: '<fmt:message key="courses.searchCourses.courseTableEditRowTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var courseId = table.getCellValue(event.row, table.getNamedColumnIndex('courseId'));
              redirectTo(GLOBAL_contextPath + '/courses/editcourse.page?course=' + courseId);
            } 
          }, {
            width: 30,
            right : 0,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/edit-delete.png',
            tooltip: '<fmt:message key="courses.searchCourses.courseTableArchiveRowTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var courseId = table.getCellValue(event.row, table.getNamedColumnIndex('courseId'));
              var courseName = table.getCellValue(event.row, table.getNamedColumnIndex('name'));
              var url = GLOBAL_contextPath + "/simpledialog.page?localeId=courses.searchCourses.courseArchiveConfirmDialogContent&localeParams=" + encodeURIComponent(courseName);
                 
              var dialog = new IxDialog({
                id : 'confirmRemoval',
                contentURL : url,
                centered : true,
                showOk : true,  
                showCancel : true,
                autoEvaluateSize: true,
                title : '<fmt:message key="courses.searchCourses.courseArchiveConfirmDialogTitle"/>',
                okLabel : '<fmt:message key="courses.searchCourses.courseArchiveConfirmDialogOkLabel"/>',
                cancelLabel : '<fmt:message key="courses.searchCourses.courseArchiveConfirmDialogCancelLabel"/>'
              });
            
              dialog.addDialogListener( function(event) {
                var dlg = event.dialog;
            
                switch (event.name) {
                  case 'okClick':
                    JSONRequest.request("courses/archivecourse.json", {
                      parameters: {
                        courseId: courseId
                      },
                      onSuccess: function (jsonResponse) {
                        var currentPage = getSearchNavigationById('searchResultsNavigation').getCurrentPage();
                        doSearch(currentPage);
                      }
                    });   
                  break;
                }
              });
            
              dialog.open();
            }
          }, {
            dataType: 'hidden',
            paramName: 'courseId'
          }]
        });
      };
    </script>
    
  </head> 
  <body onload="onLoad(event);">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
    
    <h1 class="genericPageHeader"><fmt:message key="courses.searchCourses.pageTitle" /></h1>
    
    <div id="searchCoursesSearchFormContainer"> 
      <div class="genericFormContainer"> 
        <form id="searchForm" method="post" onsubmit="onSearchCourses(event);">

          <input type="hidden" name="activeTab" id="activeTab" value="basic"/>
  
          <div class="tabLabelsContainer" id="tabs">
            <a class="tabLabel" href="#basic">
             <fmt:message key="courses.searchCourses.tabLabelBasicSearch"/>
            </a>
            <a class="tabLabel" href="#advanced">
             <fmt:message key="courses.searchCourses.tabLabelAdvancedSearch"/>
            </a>
          </div>
  
          <div id="basic" class="tabContent">

            <div class="genericFormSection">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="courses.searchCourses.textTitle"/>
                <jsp:param name="helpLocale" value="courses.searchCourses.textHelp"/>
              </jsp:include>
              <input type="text" name="text" size="40">
            </div>

            <div class="genericFormSubmitSection">
              <input type="submit" value="<fmt:message key="courses.searchCourses.basicSearchButton"/>">
            </div>

          </div>
           
          <div id="advanced" class="tabContent">
            <div id="searchCoursesAdvancedSearchCriterias">

              <div id="searchCoursesAdvancedSearchLeft">

                <div class="genericFormSection">
                  <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                    <jsp:param name="titleLocale" value="courses.searchCourses.nameTitle"/>
                    <jsp:param name="helpLocale" value="courses.searchCourses.nameHelp"/>
                  </jsp:include>
                  <input type="text" name="name" size="40">
                </div>

                <div class="genericFormSection">
                  <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                    <jsp:param name="titleLocale" value="courses.searchCourses.tagsTitle"/>
                    <jsp:param name="helpLocale" value="courses.searchCourses.tagsHelp"/>
                  </jsp:include>
		              <input type="text" id="tags" name="tags" size="40"/>
		              <div id="tags_choices" class="autocomplete_choices"></div>
                </div>

                <div class="genericFormSection">
                  <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                    <jsp:param name="titleLocale" value="courses.searchCourses.nameExtensionTitle"/>
                    <jsp:param name="helpLocale" value="courses.searchCourses.nameExtensionHelp"/>
                  </jsp:include>
                  <input type="text" name="nameExtension" size="40">
                </div>

                <div class="genericFormSection">  
                  <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                    <jsp:param name="titleLocale" value="courses.searchCourses.stateTitle"/>
                    <jsp:param name="helpLocale" value="courses.searchCourses.stateHelp"/>
                  </jsp:include>    
                  <select name="state">           
                    <option></option>           
                    <c:forEach var="state" items="${states}">
                      <option value="${state.id}">${state.name}</option> 
                    </c:forEach>
                  </select>
                </div>
                
                <div class="genericFormSection">  
                  <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                    <jsp:param name="titleLocale" value="courses.searchCourses.subjectTitle"/>
                    <jsp:param name="helpLocale" value="courses.searchCourses.subjectHelp"/>
                  </jsp:include>
                  <select name="subject">
                    <option></option>           
                    <c:forEach var="subject" items="${subjects}">
                      <c:choose>
                        <c:when test="${empty subject.code}">
                          <option value="${subject.id}">${subject.name}</option> 
                        </c:when>
                        <c:otherwise>
                          <option value="${subject.id}">${subject.name} (${subject.code})</option> 
                        </c:otherwise>
                      </c:choose>
                    </c:forEach>
                  </select>
                </div>

              </div>
              
              <div id="searchCoursesAdvancedSearchRight">

                <div class="genericFormSection">
                  <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                    <jsp:param name="titleLocale" value="courses.searchCourses.descriptionTitle"/>
                    <jsp:param name="helpLocale" value="courses.searchCourses.descriptionHelp"/>
                  </jsp:include>
                  <input type="text" name="description" size="40">
                </div>

                <div class="genericFormSection">
                  <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                    <jsp:param name="titleLocale" value="courses.searchCourses.timeFilterModeTitle"/>
                    <jsp:param name="helpLocale" value="courses.searchCourses.timeFilterModeHelp"/>
                  </jsp:include>
    
                  <div class="searchCoursesTimeFilterModeContainer">
                    <select name="timeframeMode">
                      <option value="INCLUSIVE"><fmt:message key="courses.searchCourses.timeframeModeInclusive"/></option>
                      <option value="EXCLUSIVE"><fmt:message key="courses.searchCourses.timeframeModeExclusive"/></option>
                    </select>
                  </div>
                </div>
                
                <div class="genericFormSection">
                  <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                    <jsp:param name="titleLocale" value="courses.searchCourses.timeframeTitle"/>
                    <jsp:param name="helpLocale" value="courses.searchCourses.timeframeHelp"/>
                  </jsp:include>
    
                  <div class="searchCoursesTimeFrameContainer">
                    <div class="searchCoursesTimeFrameStartContainer"> <input type="text" name="timeframeStart" ix:datefield="true"/> </div> 
                    <div class="searchCoursesTimeFrameHyphenContainer">-</div> 
                    <div class="searchCoursesTimeFrameEndContainer"> <input type="text" name="timeframeEnd" ix:datefield="true"/> </div>
                  </div>
                </div>

              </div>

            </div>

            <div class="genericFormSubmitSection">
              <input type="submit" value="<fmt:message key="courses.searchCourses.advancedSearchButton"/>">
            </div>
      
          </div>
        </form>
      </div>
    </div>
    
    <div id="searchResultsWrapper" style="display:none;">
      <div class="searchResultsTitle"><fmt:message key="courses.searchCourses.resultsTitle"/></div>
      <div id="searchResultsContainer" class="searchResultsContainer">
        <div id="searchResultsStatusMessageContainer" class="searchResultsMessageContainer"></div>
        <div id="searchResultsTableContainer"></div>
        <div id="searchResultsPagesContainer" class="searchResultsPagesContainer"></div>
      </div>
    </div>
    
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>