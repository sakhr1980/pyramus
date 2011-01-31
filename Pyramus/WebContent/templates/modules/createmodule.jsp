<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ix" prefix="ix"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
  <head>
    <title><fmt:message key="modules.createModule.pageTitle"></fmt:message></title>
    <jsp:include page="/templates/generic/head_generic.jsp"></jsp:include>
    <jsp:include page="/templates/generic/ckeditor_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/dialog_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/scriptaculous_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/tabs_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/table_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonrequest_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonform_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/draftapi_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/validation_support.jsp"></jsp:include>
    
    <script type="text/javascript">
       function addComponentsTableRow() {
         var table = getIxTableById('componentsTable');
         table.addRow(['', 0, '', '']);
         if (table.getRowCount() > 0) {
           $('noComponentsAddedMessageContainer').setStyle({
             display: 'none'
           });
           $('componentHoursTotalContainer').setStyle({
             display: ''
           });
         }
       }

       function updateComponentHours() {
         var table = getIxTableById('componentsTable');
         var sum = 0;
         for (var row = 0; row < table.getRowCount(); row++) {
           sum += parseFloat(table.getCellValue(row, table.getNamedColumnIndex('length')).replace(',','.'));
         }
         $('componentHoursTotalValueContainer').innerHTML = sum;
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
         var componentsTable = new IxTable($('componentsTable'), {
           id : "componentsTable",
           columns : [ {
             header : '<fmt:message key="modules.createModule.componentsTableNameHeader"/>',
             left : 8,
             width : 236,
             dataType: 'text',
             editable: true,
             paramName: 'name',
             required: true
           }, {
             header : '<fmt:message key="modules.createModule.componentsTableLengthHeader"/>',
             left : 248,
             width : 60,
             dataType : 'number',
             editable: true,
             paramName: 'length',
             required: true
           }, {
             header : '<fmt:message key="modules.createModule.componentsTableDescriptionHeader"/>',
             left: 312,
             right : 30,
             dataType: 'text',
             editable: true,
             paramName: 'description'
           }, {
             width: 26,
             right: 0,
             dataType: 'button',
             imgsrc: GLOBAL_contextPath + '/gfx/list-remove.png',
             tooltip: '<fmt:message key="modules.createModule.componentsTableRemoveRowTooltip"/>',
             onclick: function (event) {
               event.tableObject.deleteRow(event.row);
               if (event.tableObject.getRowCount() == 0) {
                 $('noComponentsAddedMessageContainer').setStyle({
                   display: ''
                 });
                 $('componentHoursTotalContainer').setStyle({
                   display: 'none'
                 });
               }
             } 
           }]
         });
         componentsTable.addListener("cellValueChange", function (event) {
           updateComponentHours();
         });
         componentsTable.addListener("rowAdd", function (event) {
           updateComponentHours();
         });
         componentsTable.addListener("rowDelete", function(event) {
           updateComponentHours();
         });
       }
     </script>
  
  </head>
  <body onload="onLoad(event);" ix:enabledrafting="true">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
    
    <h1 class="genericPageHeader"><fmt:message key="modules.createModule.pageTitle" /></h1>
    
    <div id="createModuleCreateFormContainer"> 
     <div class="genericFormContainer">
       <form action="createmodule.json" method="post" ix:jsonform="true" ix:useglasspane="true"> 
	        <div class="tabLabelsContainer" id="tabs">
	          <a class="tabLabel" href="#basic">
              <fmt:message key="modules.createModule.tabLabelBasic"/>
	          </a>
	          <a class="tabLabel" href="#components">
             <fmt:message key="modules.createModule.tabLabelComponents"/>
	          </a>
            <ix:extensionHook name="modules.createModule.tabLabels"/>
	        </div>
        
	        <div id="basic" class="tabContent">
  	    	  <div class="genericFormSection">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="modules.createModule.nameTitle"/>
                <jsp:param name="helpLocale" value="modules.createModule.nameHelp"/>
              </jsp:include>
              <input type="text" name="name" class="required" size="40">
  	    	  </div>
  	    	  
  	    	  <div class="genericFormSection">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="modules.createModule.tagsTitle"/>
                <jsp:param name="helpLocale" value="modules.createModule.tagsHelp"/>
              </jsp:include>
              <input type="text" id="tags" name="tags" size="40"/>
              <div id="tags_choices" class="autocomplete_choices"></div>
            </div>
          
	          <div class="genericFormSection">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="modules.createModule.educationTypesTitle"/>
                <jsp:param name="helpLocale" value="modules.createModule.educationTypesHelp"/>
              </jsp:include>
                
              <div class="createModuleFormSectionEducationType">
    	          <c:forEach var="educationType" items="${educationTypes}">
    	            <div class="createModuleFormSectionEducationTypeCell">
                    <div class="createModuleFormSectionEducationTypeTitle">
                      <div class="createModuleFormSectionEducationTypeTitleText">${educationType.name}</div>
                    </div>
                        
    	              <c:forEach var="educationSubtype" items="${educationType.unarchivedSubtypes}">
    	                <c:set var="key" value="${educationType.id}.${educationSubtype.id}"/>
    	                <input type="checkbox" name="educationType.${key}"/>                      
    	                ${educationSubtype.name}<br/>
    	              </c:forEach>
    	            </div>
    	          </c:forEach>
    	        </div> 
            </div>
	          
	          <div class="genericFormSection">  
                <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                  <jsp:param name="titleLocale" value="modules.createModule.subjectTitle"/>
                  <jsp:param name="helpLocale" value="modules.createModule.subjectHelp"/>
                </jsp:include>
                
	            <select name="subject">           
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

            <div class="genericFormSection">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="modules.createModule.courseNumberTitle"/>
                <jsp:param name="helpLocale" value="modules.createModule.courseNumberHelp"/>
              </jsp:include>
                
              <input type="text" name="courseNumber" size="2">
            </div>
	    
            <div class="genericFormSection">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="modules.editModule.lengthTitle"/>
                <jsp:param name="helpLocale" value="modules.editModule.lengthHelp"/>
              </jsp:include>
              
              <input type="text" name="moduleLength" class="required" size="15"/>
              <select name="moduleLengthTimeUnit">           
                <c:forEach var="moduleLengthTimeUnit" items="${moduleLengthTimeUnits}">
                  <option value="${moduleLengthTimeUnit.id}" <c:if test="${module.courseLength.unit.id == moduleLengthTimeUnit.id}">selected="selected"</c:if>>${moduleLengthTimeUnit.name}</option> 
                </c:forEach>
              </select>            
            </div>
	    
  	        <div class="genericFormSection">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="modules.createModule.descriptionTitle"/>
                <jsp:param name="helpLocale" value="modules.createModule.descriptionHelp"/>
              </jsp:include>
              <textarea ix:cktoolbar="moduleDescription" name="description" ix:ckeditor="true"></textarea>
    	    	</div>

            <ix:extensionHook name="modules.createModule.tabs.basic"/>
	        </div>
  
	        <div id="components" class="tabContentixTableFormattedData">
            <div class="genericTableAddRowContainer">
              <span class="genericTableAddRowLinkContainer" onclick="addComponentsTableRow();"><fmt:message key="modules.createModule.addComponentLink"/></span>
            </div>
           
            <div id="noComponentsAddedMessageContainer" class="genericTableNotAddedMessageContainer">
              <span><fmt:message key="modules.createModule.noComponentsAddedPreFix"/> <span onclick="addComponentsTableRow();" class="genericTableAddRowLink"><fmt:message key="modules.createModule.noComponentsAddedClickHereLink"/></span>.</span>
            </div>
            <div id="componentsTable"></div>
            <!-- TODO Lankinen taitaa komponenttien yhteistunnit kauniiksi -->
            <div id="componentHoursTotalContainer" style="display:none;">
              <span><fmt:message key="modules.createModule.totalComponentHoursLabel"/></span>
              <span id="componentHoursTotalValueContainer">0</span>
            </div>
            <ix:extensionHook name="modules.createModule.tabs.components"/>
          </div>
  
          <ix:extensionHook name="modules.createModule.tabs"/>

  	  	  <div class="genericFormSubmitSectionOffTab">
	    	    <input type="submit" class="formvalid" name="createmodule" value="<fmt:message key="modules.createModule.saveButton"/>">
	    	  </div>
        </form>
      </div>
    </div>
    
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>