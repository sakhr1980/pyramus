<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
  <head>
    <title><fmt:message key="resources.resourceCategories.pageTitle"/></title>

    <jsp:include page="/templates/generic/head_generic.jsp"></jsp:include>
    <jsp:include page="/templates/generic/ckeditor_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/dialog_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/scriptaculous_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/tabs_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/table_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonrequest_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonform_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/validation_support.jsp"></jsp:include>

    <script type="text/javascript">

      var archivedRowIndex;

      function addResourceCategoriesTableRow() {
        var table = getIxTableById('resourceCategoriesTable');
        var rowIndex = table.addRow(['', '', '', '', -1, 1]);
        for (var i = 0; i < table.getColumnCount(); i++) {
          table.setCellEditable(rowIndex, i, true);
        }
        $('noResourceCategoriesAddedMessageContainer').setStyle({
          display: 'none'
        });
        table.showCell(rowIndex, table.getNamedColumnIndex('removeButton'));
      }
      
      function onLoad(event) {
        tabControl = new IxProtoTabs($('tabs'));

        var resourceCategoriesTable = new IxTable($('resourceCategoriesTable'), {
          id : "resourceCategoriesTable",
          columns : [ {
            left: 8,
            width: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
            tooltip: '<fmt:message key="resources.resourceCategories.resourceCategoriesTableEditTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              for (var i = 0; i < table.getColumnCount(); i++) {
                table.setCellEditable(event.row, i, table.isCellEditable(event.row, i) == false);
              }
              table.setCellValue(event.row, table.getNamedColumnIndex('modified'), 1);
            }
          }, {
            header : '<fmt:message key="resources.resourceCategories.resourceCategoriesTableNameHeader"/>',
            left : 38,
            width : 750,
            dataType: 'text',
            editable: false,
            paramName: 'name',
            editorClassNames: 'required'
          }, {
            right: 8,
            width: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/edit-delete.png',
            tooltip: '<fmt:message key="resources.resourceCategories.resourceCategoriesTableArchiveTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var resourceCategoryId = table.getCellValue(event.row, table.getNamedColumnIndex('resourceCategoryId'));
              var resourceCategoryName = table.getCellValue(event.row, table.getNamedColumnIndex('name'));
              var url = GLOBAL_contextPath + "/simpledialog.page?localeId=resources.resourceCategories.resourceCategoryArchiveConfirmDialogContent&localeParams=" + encodeURIComponent(resourceCategoryName);

              archivedRowIndex = event.row; 
                 
              var dialog = new IxDialog({
                id : 'confirmRemoval',
                contentURL : url,
                centered : true,
                showOk : true,  
                showCancel : true,
                autoEvaluateSize: true,
                title : '<fmt:message key="resources.resourceCategories.resourceCategoryArchiveConfirmDialogTitle"/>',
                okLabel : '<fmt:message key="resources.resourceCategories.resourceCategoryArchiveConfirmDialogOkLabel"/>',
                cancelLabel : '<fmt:message key="resources.resourceCategories.resourceCategoryArchiveConfirmDialogCancelLabel"/>'
              });
            
              dialog.addDialogListener(function(event) {
                switch (event.name) {
                  case 'okClick':
                    JSONRequest.request("settings/archiveresourcecategory.json", {
                      parameters: {
                        resourceCategoryId: resourceCategoryId
                      },
                      onSuccess: function (jsonResponse) {
                        getIxTableById('resourceCategoriesTable').deleteRow(archivedRowIndex);
                      }
                    });   
                  break;
                }
              });
            
              dialog.open();
            },
            paramName: 'archiveButton',
            hidden: true
          }, {
            right: 8,
            width: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/list-remove.png',
            tooltip: '<fmt:message key="resources.resourceCategories.resourceCategoriesTableRemoveTooltip"/>',
            onclick: function (event) {
              event.tableObject.deleteRow(event.row);
              if (event.tableObject.getRowCount() == 0) {
                $('noResourceCategoriesAddedMessageContainer').setStyle({
                  display: ''
                });
              }
            },
            paramName: 'removeButton',
            hidden: true
          }, {
            dataType: 'hidden',
            paramName: 'resourceCategoryId'
          }, {
            dataType: 'hidden',
            paramName: 'modified'
          }]
        });

        var rowIndex;
        <c:forEach var="resourceCategory" items="${resourceCategories}">
          rowIndex = resourceCategoriesTable.addRow([
            '',
            '${fn:replace(resourceCategory.name, "'", "\\'")}',
            '',
            '',
            ${resourceCategory.id},
            0
          ]);
          resourceCategoriesTable.showCell(rowIndex, resourceCategoriesTable.getNamedColumnIndex('archiveButton'));
        </c:forEach>

        if (resourceCategoriesTable.getRowCount() > 0) {
          $('noResourceCategoriesAddedMessageContainer').setStyle({
            display: 'none'
          });
        }
      }
        
    </script>
  </head>
  
  <body onload="onLoad(event);">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>

    <h1 class="genericPageHeader"><fmt:message key="resources.resourceCategories.pageTitle"/></h1>
    
    <div id="manageResourceCategoriesFormContainer"> 
	    <div class="genericFormContainer"> 
	      <form action="saveresourcecategories.json" method="post" ix:jsonform="true" ix:useglasspane="true">
	  
	        <div class="tabLabelsContainer" id="tabs">
	          <a class="tabLabel" href="#manageResourceCategories">
	            <fmt:message key="resources.resourceCategories.tabLabelResourceCategories"/>
	          </a>
	        </div>
	        
          <div id="manageResourceCategories" class="tabContentixTableFormattedData">
            <div class="genericTableAddRowContainer">
              <span class="genericTableAddRowLinkContainer" onclick="addResourceCategoriesTableRow();"><fmt:message key="resources.resourceCategories.addResourceCategoryLink"/></span>
            </div>
              
            <div id="noResourceCategoriesAddedMessageContainer" class="genericTableNotAddedMessageContainer">
              <span><fmt:message key="resources.resourceCategories.noResourceCategoriesAddedPreFix"/> <span onclick="addeEucationTypesTableRow();" class="genericTableAddRowLink"><fmt:message key="resources.resourceCategories.noResourceCategoriesAddedClickHereLink"/></span>.</span>
            </div>
            
            <div id="resourceCategoriesTable"></div>
          </div>
	  
          <div class="genericFormSubmitSectionOffTab">
            <input type="submit" class="formvalid" value="<fmt:message key="resources.resourceCategories.saveButton"/>">
          </div>

	      </form>
	    </div>
	  </div>
    
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>