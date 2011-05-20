<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>
      <fmt:message key="projects.editProject.pageTitle">
        <fmt:param value="${project.name}"/>
      </fmt:message>
    </title>
    <jsp:include page="/templates/generic/head_generic.jsp"></jsp:include>
    <jsp:include page="/templates/generic/ckeditor_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/dialog_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/scriptaculous_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/table_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/datefield_support.jsp"></jsp:include>
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
          title : '<fmt:message key="projects.searchModulesDialog.searchModulesDialog.dialogTitle"/>',
          okLabel : '<fmt:message key="projects.searchModulesDialog.okLabel"/>', 
          cancelLabel : '<fmt:message key="projects.searchModulesDialog.cancelLabel"/>' 
        });
        
        dialog.setSize("800px", "660px");
        dialog.addDialogListener(function(event) {
          var dlg = event.dialog;
          switch (event.name) {
            case 'okClick':
              var modulesTable = getIxTableById('modulesTable');
              modulesTable.detachFromDom();
              for (var i = 0, len = event.results.modules.length; i < len; i++) {
                var moduleId = event.results.modules[i].id;
                var moduleName = event.results.modules[i].name;
                var index = getModuleRowIndex('modulesTable', moduleId);
                if (index == -1) {
                  modulesTable.addRow([moduleName.escapeHTML(), 0, '', moduleId, -1]);
                }
              }
              modulesTable.reattachToDom();
              if (modulesTable.getRowCount() > 0) {
                $('noModulesAddedMessageContainer').setStyle({
                  display: 'none'
                });
                $('editProjectModulesTotalContainer').setStyle({
                  display: ''
                });
                $('editProjectModulesTotalValue').innerHTML = modulesTable.getRowCount(); 
              }
              else {
                $('noModulesAddedMessageContainer').setStyle({
                  display: ''
                });
                $('editProjectModulesTotalContainer').setStyle({
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
        var modulesTable = new IxTable($('modulesTableContainer'), {
          id : "modulesTable",
          columns : [ {
            header : '<fmt:message key="projects.editProject.moduleTableNameHeader"/>',
            left : 8,
            dataType: 'text',
            editable: false,
            paramName: 'name',
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
            header : '<fmt:message key="projects.editProject.moduleTableOptionalityHeader"/>',
            right : 40,
            width : 150,
            dataType : 'select',
            paramName: 'optionality',
            editable: true,
            options: [
              {text: '<fmt:message key="projects.editProject.optionalityMandatory"/>', value: 0},
              {text: '<fmt:message key="projects.editProject.optionalityOptional"/>', value: 1}
            ],
            sortAttributes: {
              sortAscending: {
                toolTip: '<fmt:message key="generic.sort.ascending"/>',
                sortAction: IxTable_ROWSELECTSORT 
              },
              sortDescending: {
                toolTip: '<fmt:message key="generic.sort.descending"/>',
                sortAction: IxTable_ROWSELECTSORT
              }
            },
            contextMenu: [
              {
                text: '<fmt:message key="generic.filter.byValue"/>',
                onclick: new IxTable_ROWSTRINGFILTER()
              },
              {
                text: '<fmt:message key="generic.filter.clear"/>',
                onclick: new IxTable_ROWCLEARFILTER()
              },
              {
                text: '-'
              },
              {
                text: '<fmt:message key="generic.action.copyValues"/>',
                onclick: new IxTable_COPYVALUESTOCOLUMNACTION(true)
              }
            ]
          }, {
            width: 30,
            right: 0,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/list-remove.png',
            tooltip: '<fmt:message key="projects.editProject.moduleTableDeleteRowTooltip"/>',
            onclick: function (event) {
              event.tableComponent.deleteRow(event.row);
              if (event.tableComponent.getRowCount() == 0) {
                $('noModulesAddedMessageContainer').setStyle({
                  display: ''
                });
                $('editProjectModulesTotalContainer').setStyle({
                  display: 'none'
                });
              }
              else {
                $('noModulesAddedMessageContainer').setStyle({
                  display: 'none'
                });
                $('editProjectModulesTotalContainer').setStyle({
                  display: ''
                });
                $('editProjectModulesTotalValue').innerHTML = event.tableComponent.getRowCount(); 
              }
            } 
          }, {
            dataType: 'hidden',
            paramName: 'moduleId'
          }, {
            dataType: 'hidden',
            paramName: 'projectModuleId'
          }]
        });
        JSONRequest.request("projects/getprojectmodules.json", {
          parameters: {
            project: ${project.id}
          },
          onSuccess: function(jsonResponse) {
            var projectModules = jsonResponse.projectModules;
            var rows = new Array();
            for (var i = 0; i < projectModules.length; i++) {
              rows.push([
                  projectModules[i].name.escapeHTML(),
                  projectModules[i].optionality,
                  '',
                  projectModules[i].moduleId,
                  projectModules[i].id]);
            }
            modulesTable.addRows(rows);
            if(modulesTable.getRowCount() > 0){
              $('noModulesAddedMessageContainer').setStyle({
                display: 'none'
              });
              $('editProjectModulesTotalContainer').setStyle({
                display: ''
              });
              $('editProjectModulesTotalValue').innerHTML = modulesTable.getRowCount(); 
            }
            else {
              $('noModulesAddedMessageContainer').setStyle({
                display: ''
              });
              $('editProjectModulesTotalContainer').setStyle({
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
    
    <h1 class="genericPageHeader">
      <fmt:message key="projects.editProject.pageTitle">
        <fmt:param value="${project.name}"/>
      </fmt:message>
    </h1>
    
    <form id="projectForm" action="editproject.json" method="post" ix:jsonform="true" ix:useglasspane="true">
      <input type="hidden" name="version" value="${project.version}"/>
      
      <div id="editProjectEditFormContainer"> 
        <div class="genericFormContainer"> 
          <div class="tabLabelsContainer" id="tabs">
            <a class="tabLabel" href="#basic">
              <fmt:message key="projects.editProject.tabLabelBasic"/>
            </a>
            <a class="tabLabel" href="#modules">
              <fmt:message key="projects.editProject.tabLabelModules"/>
            </a>
          </div>

          <!--  Basic tab -->

          <div id="basic" class="tabContent">
            <input type="hidden" name="project" value="${project.id}"/>
            
            <!--  TODO italic tags to css -->

            <div class="genericFormSection">  
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="projects.editProject.createdTitle"/>
                <jsp:param name="helpLocale" value="projects.editProject.createdHelp"/>
              </jsp:include>
              <span><i>${project.creator.fullName} <fmt:formatDate pattern="dd.MM.yyyy hh:mm" value="${project.created}"/></i></span>    
            </div>

            <div class="genericFormSection">  
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="projects.editProject.modifiedTitle"/>
                <jsp:param name="helpLocale" value="projects.editProject.modifiedHelp"/>
              </jsp:include>
              <span><i>${project.lastModifier.fullName} <fmt:formatDate pattern="dd.MM.yyyy hh:mm" value="${project.lastModified}"/></i></span>    
            </div>

            <div class="genericFormSection">
                <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                  <jsp:param name="titleLocale" value="projects.editProject.nameTitle"/>
                  <jsp:param name="helpLocale" value="projects.editProject.nameHelp"/>
                </jsp:include>
              <input type="text" class="required" name="name" value="${fn:escapeXml(project.name)}" size="40"/>
            </div>

            <div class="genericFormSection">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="projects.editProject.tagsTitle"/>
                <jsp:param name="helpLocale" value="projects.editProject.tagsHelp"/>
              </jsp:include>
              <input type="text" id="tags" name="tags" size="40" value="${fn:escapeXml(tags)}"/>
              <div id="tags_choices" class="autocomplete_choices"></div>
            </div>
        
            <div class="genericFormSection">
                <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                  <jsp:param name="titleLocale" value="projects.editProject.descriptionTitle"/>
                  <jsp:param name="helpLocale" value="projects.editProject.descriptionHelp"/>
                </jsp:include>
              <textarea ix:cktoolbar="projectDescription" name="description" ix:ckeditor="true">${project.description}</textarea>
            </div>
  
            <div class="genericFormSection">
                <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                  <jsp:param name="titleLocale" value="projects.editProject.optionalStudiesTitle"/>
                  <jsp:param name="helpLocale" value="projects.editProject.optionalStudiesHelp"/>
                </jsp:include>
              <input type="text" name="optionalStudiesLength" class="required" value="${project.optionalStudiesLength.units}" size="15"/>
              <select name="optionalStudiesLengthTimeUnit">           
                <c:forEach var="optionalStudiesLengthTimeUnit" items="${optionalStudiesLengthTimeUnits}">
                  <option value="${optionalStudiesLengthTimeUnit.id}" <c:if test="${project.optionalStudiesLength.unit.id == optionalStudiesLengthTimeUnit.id}">selected="selected"</c:if>>${optionalStudiesLengthTimeUnit.name}</option> 
                </c:forEach>
              </select>
            </div>
          </div>

         <!--  Modules tab -->
          
          <div id="modules" class="tabContentixTableFormattedData">
            <div class="genericTableAddRowContainer">
              <span class="genericTableAddRowLinkContainer" onclick="openSearchModulesDialog();"><fmt:message key="projects.editProject.addModuleLink"/></span>
            </div>
            
            <div id="noModulesAddedMessageContainer" class="genericTableNotAddedMessageContainer">
              <span><fmt:message key="projects.editProject.noModulesAddedPreFix"/> <span onclick="openSearchModulesDialog();" class="genericTableAddRowLink"><fmt:message key="projects.editProject.noModulesAddedClickHereLink"/></span>.</span>
            </div>
            
            <div id="modulesContainer">
              <div id="modulesTableContainer"></div>
            </div>

            <div id="editProjectModulesTotalContainer">
              <fmt:message key="projects.editProject.modulesTotal"/> <span id="editProjectModulesTotalValue"></span>
            </div>

          </div>

        </div>
      </div>
      
      <div class="genericFormSubmitSectionOffTab">
        <input type="submit" class="formvalid" value="<fmt:message key="projects.editProject.saveButton"/>">
      </div>

    </form>
  
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>