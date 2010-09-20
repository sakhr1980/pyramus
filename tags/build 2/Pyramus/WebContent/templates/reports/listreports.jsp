<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title><fmt:message key="reports.listReports.pageTitle"></fmt:message></title>
    <jsp:include page="/templates/generic/head_generic.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonrequest_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/dialog_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/table_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/tabs_support.jsp"></jsp:include>
    
    <script type="text/javascript">
      function onLoad(event) {
        var tabControl = new IxProtoTabs($('tabs'));

        var reportsTable = new IxTable($('listReportsTableContainer'), {
          id : "reportsTable",
          rowClasses: ['ixTableClickableRow'],
          columns : [{
            header : '<fmt:message key="reports.listReports.reportsTableNameHeader"/>',
            left : 8,
            right : 76,
            dataType: 'text',
            editable: false,
            paramName: 'name'
          }, {
            dataType: 'hidden',
            paramName: 'reportId'
          }]
        });
        
        reportsTable.addListener("rowClick", function (event) {
          var reportId = event.tableObject.getCellValue(event.row, event.tableObject.getNamedColumnIndex('reportId'));
          redirectTo(window.location.href = GLOBAL_contextPath + '/reports/viewreport.page?reportId=' + reportId);
        });

        <c:forEach var="report" items="${reports}">
          reportsTable.addRow(['${report.name}', '${report.id}']);
        </c:forEach>
      };
    </script>
    
  </head>
  <body onload="onLoad(event);">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
  
    <h1 class="genericPageHeader"><fmt:message key="reports.listReports.pageTitle"/></h1>
  
    <div id="listReportsEditFormContainer">
      <div class="genericFormContainer"> 
      <div class="tabLabelsContainer" id="tabs">
        <a class="tabLabel" href="#listReports">
          <span class="tabLabelLeftTopCorner">
            <span class="tabLabelRightTopCorner">
              <fmt:message key="reports.listReports.tabLabelReports"/>
            </span>
          </span>
        </a>
      </div>
      
      <div id="listReports" class="tabContentixTableFormattedData">
        <div id="listReportsTableContainer"></div>
      </div>
    </div>
  </div>
    
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>