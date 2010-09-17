<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title><fmt:message key="projects.editStudentProject.pageTitle" /></title>
    <jsp:include page="/templates/generic/head_generic.jsp"></jsp:include>
    <jsp:include page="/templates/generic/ckeditor_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/dialog_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/scriptaculous_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/table_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonrequest_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonform_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/tabs_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/draftapi_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/validation_support.jsp"></jsp:include>

    <script type="text/javascript">

    function openSearchModulesDialog() {

      var selectedModules = new Array();
      var modulesTable = getIxTableById('modulesTable');
      for (var i = 0; i < modulesTable.getRowCount() - 1; i++) {
        var moduleName = modulesTable.getCellValue(i, modulesTable.getNamedColumnIndex('name'));
        var moduleId = modulesTable.getCellValue(i, modulesTable.getNamedColumnIndex('moduleId'));
        selectedModules.push({
          name: moduleName,
          id: moduleId});
      }
      // TODO selectedModules -> dialog

      var dialog = new IxDialog({
        id : 'searchModulesDialog',
        contentURL : GLOBAL_contextPath + '/projects/searchmodulesdialog.page',
        centered : true,
        showOk : true,
        showCancel : true,
        title : '<fmt:message key="projects.searchModulesDialog.searchModulesDialogTitle"/>',
        okLabel : '<fmt:message key="projects.searchModulesDialog.okLabel"/>', 
        cancelLabel : '<fmt:message key="projects.searchModulesDialog.cancelLabel"/>' 
      });
      
      dialog.setSize("800px", "600px");
      dialog.addDialogListener(function(event) {
        var dlg = event.dialog;
        switch (event.name) {
          case 'okClick':
            for (var i = 0, len = event.results.modules.length; i < len; i++) {
              var moduleId = event.results.modules[i].id;
              var moduleName = event.results.modules[i].name;
              var index = getModuleRowIndex('modulesTable', moduleId);
              if (index == -1) {
                var modulesTable = getIxTableById('modulesTable');
                modulesTable.addRow([moduleName, -1, 0, '', moduleId, -1]);
              }
            }
            if (getIxTableById('modulesTable').getRowCount() > 0) {
              $('noModulesAddedMessageContainer').setStyle({
                display: 'none'
              });
            }
          break;
        }
      });
      dialog.open();
    }
  
      function getModuleRowIndex(tableId, moduleId) {
        var table = getIxTableById(tableId);
        if (table) {
          for (var i = 0; i < table.getRowCount(); i++) {
            var tableModuleId = table.getCellValue(i, table.getNamedColumnIndex('moduleId'));
            if (tableModuleId == moduleId) {
              return i;
            }
          }
        }
        return -1;
      }

      function onLoad(event) {
        var tabControl = new IxProtoTabs($('tabs'));
        var modulesTable = new IxTable($('modulesTableContainer'), {
          id : "modulesTable",
          columns : [ {
            header : '<fmt:message key="projects.editStudentProject.moduleTableNameHeader"/>',
            left : 8,
            dataType: 'text',
            editable: false,
            paramName: 'name'
          }, {
            header : '<fmt:message key="projects.editStudentProject.moduleTableStudyTermHeader"/>',
            right : 140,
            dataType : 'select',
            paramName: 'studyTerm',
            editable: true,
            overwriteColumnValues : true,
            options: [
              {text: "", value: -1}
              <c:if test="${not empty academicTerms}">,</c:if>
              <c:forEach var="academicTerm" items="${academicTerms}" varStatus="vs">
                {text: "${academicTerm.name}", value: ${academicTerm.id}}
                <c:if test="${not vs.last}">,</c:if>
              </c:forEach>
            ]
          }, {
            header : '<fmt:message key="projects.editStudentProject.moduleTableOptionalityHeader"/>',
            right : 40,
            dataType : 'select',
            paramName: 'optionality',
            editable: true,
            overwriteColumnValues : true,
            options: [
              {text: '<fmt:message key="projects.editStudentProject.optionalityMandatory"/>', value: 0},
              {text: '<fmt:message key="projects.editStudentProject.optionalityOptional"/>', value: 1}
            ]
          }, {
            width: 30,
            right: 0,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/list-remove.png',
            tooltip: '<fmt:message key="projects.editStudentProject.moduleTableDeleteRowTooltip"/>',
            onclick: function (event) {
              event.tableObject.deleteRow(event.row);
              if (event.tableObject.getRowCount() == 0) {
                $('noModulesAddedMessageContainer').setStyle({
                  display: ''
                });
              }
            } 
          }, {
            dataType: 'hidden',
            paramName: 'moduleId'
          }, {
            dataType: 'hidden',
            paramName: 'studentProjectModuleId'
          }]
        });
        JSONRequest.request("projects/getstudentprojectmodules.json", {
          parameters: {
            studentProject: ${studentProject.id}
          },
          onSuccess: function (jsonResponse) {
            var studentProjectModules = jsonResponse.studentProjectModules;
            for (var i = 0; i < studentProjectModules.length; i++) {
              modulesTable.addRow([
                  studentProjectModules[i].name,
                  studentProjectModules[i].academicTermId,
                  studentProjectModules[i].optionality,
                  '',
                  studentProjectModules[i].moduleId,
                  studentProjectModules[i].id]);
            }
            if(modulesTable.getRowCount() > 0){
              $('noModulesAddedMessageContainer').setStyle({
                display: 'none'
              });
            }
          } 
        });
      }

    </script>
  </head>
  <body onLoad="onLoad(event);" ix:enabledrafting="true">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
    
    <h1 class="genericPageHeader"><fmt:message key="projects.editStudentProject.pageTitle" /></h1>
    
    <form id="studentProjectForm" action="editstudentproject.json" method="post" ix:jsonform="true" ix:useglasspane="true">
      <div id="editStudentProjectEditFormContainer"> 
        <div class="genericFormContainer"> 
          <div class="tabLabelsContainer" id="tabs">
            <a class="tabLabel" href="#basic">
              <fmt:message key="projects.editStudentProject.tabLabelBasic"/>
            </a>
            <a class="tabLabel" href="#modules">
              <fmt:message key="projects.editStudentProject.tabLabelModules"/>
            </a>
          </div>
          
          <!--  Basic tab -->
        
          <div id="basic" class="tabContent">
	          <input type="hidden" name="studentProject" value="${studentProject.id}"/>
	          
	          <div class="genericFormSection">
	            <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                  <jsp:param name="titleLocale" value="projects.editStudentProject.studentTitle"/>
                  <jsp:param name="helpLocale" value="projects.editStudentProject.studentHelp"/>
                </jsp:include>
	            <div>${studentProject.student.firstName} ${studentProject.student.lastName}</div>
	          </div>
	
	          <div class="genericFormSection">
                <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                  <jsp:param name="titleLocale" value="projects.editStudentProject.nameTitle"/>
                  <jsp:param name="helpLocale" value="projects.editStudentProject.nameHelp"/>
                </jsp:include>
	            <input type="text" class="required" name="name" value="${fn:escapeXml(studentProject.name)}" size="40"/>
	          </div>
	      
	          <div class="genericFormSection">
                <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                  <jsp:param name="titleLocale" value="projects.editStudentProject.descriptionTitle"/>
                  <jsp:param name="helpLocale" value="projects.editStudentProject.descriptionHelp"/>
                </jsp:include>
	            <textarea ix:cktoolbar="studentProjectDescription" name="description" ix:ckeditor="true">${studentProject.description}</textarea>
	          </div>
	
	          <div class="genericFormSection">
                <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                  <jsp:param name="titleLocale" value="projects.editStudentProject.optionalStudiesTitle"/>
                  <jsp:param name="helpLocale" value="projects.editStudentProject.optionalStudiesHelp"/>
                </jsp:include>
	            <input type="text" name="optionalStudiesLength" class="required" value="${studentProject.optionalStudiesLength.units}" size="15"/>
	            <select name="optionalStudiesLengthTimeUnit">           
	              <c:forEach var="optionalStudiesLengthTimeUnit" items="${optionalStudiesLengthTimeUnits}">
	                <option value="${optionalStudiesLengthTimeUnit.id}" <c:if test="${studentProject.optionalStudiesLength.unit.id == optionalStudiesLengthTimeUnit.id}">selected="selected"</c:if>>${optionalStudiesLengthTimeUnit.name}</option> 
	              </c:forEach>
	            </select>            
	          </div>
          </div>
          
          <!--  Modules tab -->
  
          <div id="modules" class="tabContentixTableFormattedData">
	          <div class="genericTableAddRowContainer">
	            <span class="genericTableAddRowLinkContainer" onclick="openSearchModulesDialog();"><fmt:message key="projects.editStudentProject.addModuleLink"/></span>
	          </div>
	          
	          <div id="noModulesAddedMessageContainer" class="genericTableNotAddedMessageContainer">
	            <span><fmt:message key="projects.editStudentProject.noModulesAddedPreFix"/> <span onclick="openSearchModulesDialog();" class="genericTableAddRowLink"><fmt:message key="projects.editStudentProject.noModulesAddedClickHereLink"/></span>.</span>
	          </div>
	          <div id="modulesContainer">
	            <div id="modulesTableContainer"></div>
	          </div>
          </div>

	      </div>
  		</div>
    
      <div class="genericFormSubmitSection">
        <input type="submit" class="formvalid" value="<fmt:message key="projects.editStudentProject.saveButton"/>">
      </div>
    
    </form>

    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>