<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>
      <fmt:message key="courses.coursePlanner.pageTitle"/>
    </title>
    <jsp:include page="/templates/generic/head_generic.jsp"></jsp:include>
    <jsp:include page="/templates/generic/scriptaculous_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/tabs_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/datefield_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/validation_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/locale_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/hovermenu_support.jsp"></jsp:include>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/courseplanner.css"/>      
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/gui/courses/courseplanner.js"></script>
    
    <script type="text/javascript">
      function onLoad(event) {
        var tabControl = new IxProtoTabs($('tabs'));
        
        var coursePlanner = new CoursePlanner($('coursePlannerContainer'), {
          showMonthLines: true,
          showWeekLines: true,
          showDayLines: true,
          showMonthLabels: true,
          showWeekLabels: true,
          showDayLabels: false,
          showYearLabel: true,
          courseHeight: 50,
          trackHeight: 60,
          trackSpacing: 4,
          initialTimeFrame: 1000 * 60 * 60 * 24 * 30 * 6,
          onYearLabelClick: function(event) {
            var from = new Date();
            from.setFullYear(event.year, 0, 1);
            from.setHours(0, 0, 0, 0);
            var to = new Date();
            to.setFullYear(event.year, 11, 0);
            to.setHours(0, 0, 0, 0);
            this.setDateRange(from, to);
          }, 
          onMonthLabelClick: function(event) {
            var from = new Date();
            from.setFullYear(event.year, event.month, 1);
            from.setHours(0, 0, 0, 0);
            var to = new Date();
            to.setFullYear(event.year, event.month + 1, 0);
            to.setHours(0, 0, 0, 0);
            this.setDateRange(from, to);
          }
        });
        
        <c:forEach var="courseBean" items="${courseBeans}">
          coursePlanner.addCourse(
	          '${fn:escapeXml(courseBean.courseName)}', 
	          new Date(${courseBean.course.beginDate.time}),
            new Date(${courseBean.course.endDate.time}), 
            -1,
	          false,
	          false
	        );
	      </c:forEach>
	      
	      coursePlanner.arrageCoursesToTracks();
      }

    </script>
  </head>
  <body onload="onLoad(event);">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
    <h1 class="genericPageHeader">
      <fmt:message key="courses.coursePlanner.pageTitle"/>
    </h1>
  
    <div class="genericFormContainer">
      <div class="tabLabelsContainer" id="tabs">
        <a class="tabLabel" href="#planner"><fmt:message key="courses.coursePlanner.plannerTabTitle" /></a>
      </div>
        
      <div id="planner" class="tabContent">
        <div id="coursePlannerContainer"></div>
      </div>
    </div>
  
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>