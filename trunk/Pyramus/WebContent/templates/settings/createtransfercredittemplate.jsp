<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
  <head>
    <title><fmt:message key="settings.createTransferCreditTemplate.pageTitle"/></title>

    <jsp:include page="/templates/generic/head_generic.jsp"></jsp:include>
    <jsp:include page="/templates/generic/dialog_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/scriptaculous_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/tabs_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/table_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonrequest_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonform_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/draftapi_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/validation_support.jsp"></jsp:include>

    <script type="text/javascript">

      var archivedRowIndex;

      function addCoursesTableRow() {
        var table = getIxTableById('coursesTable');
        rowIndex = table.addRow(['', -1, 0, -1, 0, -1, '']);
        for (var i = 0; i < table.getColumnCount(); i++) {
          table.setCellEditable(rowIndex, i, true);
        }
        
        $('noCoursesAddedMessageContainer').setStyle({
          display: 'none'
        });
        table.showCell(rowIndex, table.getNamedColumnIndex('removeButton'));
      }
      
      function onLoad(event) {
        tabControl = new IxProtoTabs($('tabs'));

        var coursesTable = new IxTable($('coursesTable'), {
          id : "coursesTable",
          columns : [{
            header : '<fmt:message key="settings.createTransferCreditTemplate.coursesTableCourseNameHeader"/>',
            left: 8,
            right: 8 + 22 + 8 + 100 + 8 + 100 + 8 + 200 + 8 + 100 + 8 + 100 + 8,
            dataType: 'text',
            editable: true,
            paramName: 'courseName'
          }, {
            header : '<fmt:message key="settings.createTransferCreditTemplate.coursesTableCourseOptionalityHeader"/>',
            width : 100,
            right: 8 + 22 + 8 + 100 + 8 + 100 + 8 + 200 + 8 + 100 + 8,
            dataType: 'select',
            editable: true,
            overwriteColumnValues : true,
            paramName: 'courseOptionality',
            options: [
              {text: '<fmt:message key="settings.createTransferCreditTemplate.coursesTableCourseOptionalityOptional"/>', value: 'OPTIONAL'},
              {text: '<fmt:message key="settings.createTransferCreditTemplate.coursesTableCourseOptionalityMandatory"/>', value: 'MANDATORY'}
            ]
          }, {
            header : '<fmt:message key="settings.createTransferCreditTemplate.coursesTableCourseNumberHeader"/>',
            width : 100,
            right: 8 + 22 + 8 + 100 + 8 + 100 + 8 + 200 + 8,
            dataType: 'number',
            editable: true,
            paramName: 'courseNumber' 
          }, {
            header : '<fmt:message key="settings.createTransferCreditTemplate.coursesTableSubjectHeader"/>',
            width : 200,
            right: 8 + 22 + 8 + 100 + 8 + 100 + 8,
            dataType: 'select',
            editable: true,
            overwriteColumnValues : true,
            paramName: 'subject',
            options: [
              <c:forEach var="subject" items="${subjects}" varStatus="vs">
                {text: "${fn:replace(subject.name, "'", "\\'")}", value: ${subject.id}}
                <c:if test="${not vs.last}">,</c:if>
              </c:forEach>
            ]
          }, {
            header : '<fmt:message key="settings.createTransferCreditTemplate.coursesTableLengthHeader"/>',
            width : 100,
            right: 8 + 22 + 8 + 100 + 8,
            dataType: 'number',
            editable: true,
            overwriteColumnValues : true,
            paramName: 'courseLength'
          }, {
            header : '<fmt:message key="settings.createTransferCreditTemplate.coursesTableLengthUnitHeader"/>',
            width : 100,
            right: 8 + 22 + 8,
            dataType: 'select',
            editable: true,
            overwriteColumnValues : true,
            paramName: 'courseLengthUnit', 
            options: [
              <c:forEach var="timeUnit" items="${timeUnits}" varStatus="vs">
                {text: "${fn:replace(timeUnit.name, "'", "\\'")}", value: ${timeUnit.id}}
                <c:if test="${not vs.last}">,</c:if>
              </c:forEach>
            ]            
          }, {
            right: 8,
            width: 22,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/list-remove.png',
            tooltip: '<fmt:message key="settings.createTransferCreditTemplate.coursesTableRemoveTooltip"/>',
            onclick: function (event) {
              event.tableObject.deleteRow(event.row);
              if (event.tableObject.getRowCount() == 0) {
                $('noCoursesAddedMessageContainer').setStyle({
                  display: ''
                });
              }
            },
            paramName: 'removeButton',
            hidden: true
          }]
        });
      }
        
    </script>
  </head>
  
  <body onload="onLoad(event);" ix:enabledrafting="true">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
    
    <h1 class="genericPageHeader"><fmt:message key="settings.createTransferCreditTemplate.pageTitle"/></h1>
    
    <div id="createTransferCreditTemplateFormContainer"> 
	    <div class="genericFormContainer"> 
	      <form action="createtransfercredittemplate.json" method="post" ix:jsonform="true" ix:useglasspane="true">
	        <div class="tabLabelsContainer" id="tabs">
	          <a class="tabLabel" href="#transferCreditTemplate">
	            <fmt:message key="settings.createTransferCreditTemplate.tabLabelTransferCreditTemplate"/>
	          </a>
	        </div>
          
          <div id="transferCreditTemplate" class="tabContentixTableFormattedData">
          
	          <div class="genericFormSection">
	            <jsp:include page="/templates/generic/fragments/formtitle.jsp">
	              <jsp:param name="titleLocale" value="settings.createTransferCreditTemplate.nameTitle"/>
	              <jsp:param name="helpLocale" value="settings.createTransferCreditTemplate.nameHelp"/>
	            </jsp:include>
	                    
	            <input type="text" name="name" class="required" size="40">
	          </div>
          
            <div class="genericTableAddRowContainer">
              <span class="genericTableAddRowLinkContainer" onclick="addCoursesTableRow();"><fmt:message key="settings.createTransferCreditTemplate.addCourseLink"/></span>
            </div>
              
            <div id="noCoursesAddedMessageContainer" class="genericTableNotAddedMessageContainer">
              <span><fmt:message key="settings.createTransferCreditTemplate.noCoursesAddedPreFix"/> <span onclick="addCoursesTableRow();" class="genericTableAddRowLink"><fmt:message key="settings.createTransferCreditTemplate.noCoursesAddedClickHereLink"/></span>.</span>
            </div>
            
            <div id="coursesTable"></div>
          </div>
	  
          <div class="genericFormSubmitSectionOffTab">
            <input type="submit" class="formvalid" value="<fmt:message key="settings.createTransferCreditTemplate.saveButton"/>">
          </div>

	      </form>
	    </div>
	  </div>
    
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>