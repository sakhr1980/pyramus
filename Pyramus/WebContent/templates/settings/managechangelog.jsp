<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
  <head>
    <title><fmt:message key="settings.manageChangeLog.pageTitle"/></title>

    <jsp:include page="/templates/generic/head_generic.jsp"></jsp:include>
    <jsp:include page="/templates/generic/dialog_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/scriptaculous_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/tabs_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/table_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonrequest_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonform_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/validation_support.jsp"></jsp:include>

    <script type="text/javascript">

      function onLoad(event) {
        tabControl = new IxProtoTabs($('tabs'));
        
        var settingsTable = new IxTable($('settingsTableContainer'), {
          id : "settingsTable",
          rowHoverEffect: true,
          columns : [{
            header : '',
            width: 22,
            left: 8,
            dataType: 'checkbox',
            editable: true,
            paramName: 'track'
          }, {
            header : '<fmt:message key="settings.manageChangeLog.settingsTableName"/>',
            left : 8 + 22 + 8,
            rigth: 8,
            dataType: 'text',
            editable: false,
            paramName: 'name'
          }, {
            dataType: 'hidden',
            paramName: 'entity'
          }, {
            dataType: 'hidden',
            paramName: 'property'
          }]
        });

        var rowIndex;
        var trackColumnIndex = settingsTable.getNamedColumnIndex('track');
        settingsTable.detachFromDom();
        
        <c:forEach var="entity" items="${entities}">
          rowIndex = settingsTable.addRow([false, '${fn:escapeXml(entity.displayName)}', '', '']);
          settingsTable.hideCell(rowIndex, trackColumnIndex);
          
          <c:forEach var="property" items="${entity.properties}">
            settingsTable.addRow([${property.track}, '&nbsp;&nbsp;&nbsp;&nbsp;${fn:escapeXml(property.displayName)}', '${fn:escapeXml(entity.name)}', '${fn:escapeXml(property.name)}']);
          </c:forEach>
        </c:forEach>
        
        settingsTable.reattachToDom();
      }
        
    </script>
  </head>
  
  <body onload="onLoad(event);">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
    
    <h1 class="genericPageHeader"><fmt:message key="settings.manageChangeLog.pageTitle"/></h1>
    
    <div class="genericFormContainer"> 
      <form action="managechangelog.page" method="post">
  
        <div class="tabLabelsContainer" id="tabs">
          <a class="tabLabel" href="#settings">
            <fmt:message key="settings.manageChangeLog.tabLabelSettings"/>
          </a>
        </div>
        
        <div id="settings" class="tabContent">
          <div id="settingsTableContainer"></div>
        </div>
  
        <div class="genericFormSubmitSectionOffTab">
          <input type="submit" class="formvalid" value="<fmt:message key="settings.manageChangeLog.saveButton"/>">
        </div>

      </form>
    </div>
    
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>