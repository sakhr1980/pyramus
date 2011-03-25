<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
  <head>
    <title><fmt:message key="settings.reportCategories.pageTitle"/></title>

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

      var deletedRowIndex;

      function addReportCategoriesTableRow() {
        var table = getIxTableById('reportCategoriesTable');
        var rowIndex = table.addRow(['', '', '', '', -1, 1]);
        for (var i = 0; i < table.getColumnCount(); i++) {
          table.setCellEditable(rowIndex, i, true);
        }
        $('noReportCategoriesAddedMessageContainer').setStyle({
          display: 'none'
        });
        table.showCell(rowIndex, table.getNamedColumnIndex('removeButton'));
        table.hideCell(rowIndex, table.getNamedColumnIndex('deleteButton'));
      }
      
      function onLoad(event) {
        tabControl = new IxProtoTabs($('tabs'));
        
        var reportCategoriesTable = new IxTable($('reportCategoriesTableContainer'), {
          id : "reportCategoriesTable",
          columns : [ {
            left: 8,
            width: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
            tooltip: '<fmt:message key="settings.reportCategories.reportCategoriesTableEditTooltip"/>',
            onclick: function (event) {
              var table = event.tableComponent;
              for (var i = 0; i < table.getColumnCount(); i++) {
                table.setCellEditable(event.row, i, table.isCellEditable(event.row, i) == false);
              }
              table.setCellValue(event.row, table.getNamedColumnIndex('modified'), 1);
            }
          }, {
            header : '<fmt:message key="settings.reportCategories.reportCategoriesTableNameHeader"/>',
            left : 38,
            width : 300,
            dataType: 'text',
            editable: false,
            paramName: 'name',
            required: true
          }, {
            left: 346,
            width: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/edit-delete.png',
            tooltip: '<fmt:message key="settings.reportCategories.reportCategoriesTableDeleteTooltip"/>',
            onclick: function (event) {
              var table = event.tableComponent;
              var reportCategoryId = table.getCellValue(event.row, table.getNamedColumnIndex('reportCategoryId'));
              var reportCategoryName = table.getCellValue(event.row, table.getNamedColumnIndex('name'));
              var url = GLOBAL_contextPath + "/simpledialog.page?localeId=settings.reportCategories.reportCategoryDeleteConfirmDialogContent&localeParams=" + encodeURIComponent(reportCategoryName);

              deletedRowIndex = event.row; 
                 
              var dialog = new IxDialog({
                id : 'confirmRemoval',
                contentURL : url,
                centered : true,
                showOk : true,  
                showCancel : true,
                autoEvaluateSize: true,
                title : '<fmt:message key="settings.reportCategories.reportCategoryDeleteConfirmDialogTitle"/>',
                okLabel : '<fmt:message key="settings.reportCategories.reportCategoryDeleteConfirmDialogOkLabel"/>',
                cancelLabel : '<fmt:message key="settings.reportCategories.reportCategoryDeleteConfirmDialogCancelLabel"/>'
              });
            
              dialog.addDialogListener(function(event) {
                switch (event.name) {
                  case 'okClick':
                    JSONRequest.request("settings/deletereportcategory.json", {
                      parameters: {
                        reportCategory: reportCategoryId
                      },
                      onSuccess: function (jsonResponse) {
                        getIxTableById('reportCategoriesTable').deleteRow(deletedRowIndex);
                      }
                    });   
                  break;
                }
              });
            
              dialog.open();
            },
            paramName: 'deleteButton'
          }, {
            left: 346,
            width: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/list-remove.png',
            tooltip: '<fmt:message key="settings.reportCategories.reportCategoriesTableRemoveTooltip"/>',
            onclick: function (event) {
              event.tableComponent.deleteRow(event.row);
              if (event.tableComponent.getRowCount() == 0) {
                $('noReportCategoriesAddedMessageContainer').setStyle({
                  display: ''
                });
              }
            },
            paramName: 'removeButton',
            hidden: true
          }, {
            dataType: 'hidden',
            paramName: 'reportCategoryId'
          }, {
            dataType: 'hidden',
            paramName: 'modified'
          }]
        });

        var rows = new Array();
        <c:forEach var="category" items="${categories}">
          rows.push([
            '',
            '${fn:escapeXml(category.name)}',
            '',
            '',
            ${category.id},
            0
          ]);
        </c:forEach>
        reportCategoriesTable.addRows(rows);
        
        if (reportCategoriesTable.getRowCount() > 0) {
          $('noReportCategoriesAddedMessageContainer').setStyle({
            display: 'none'
          });
        }
      }
        
    </script>
  </head>
  
  <body onload="onLoad(event);" ix:enabledrafting="true">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
    
    <h1 class="genericPageHeader"><fmt:message key="settings.reportCategories.pageTitle"/></h1>
    
    <div id="manageReportCategoriesFormContainer"> 
	    <div class="genericFormContainer"> 
	      <form action="savereportcategories.json" method="post" ix:jsonform="true" ix:useglasspane="true">
	  
	        <div class="tabLabelsContainer" id="tabs">
	          <a class="tabLabel" href="#manageReportCategories">
	            <fmt:message key="settings.reportCategories.tabLabelReportCategories"/>
	          </a>
	        </div>
	        
            <div id="manageReportCategories" class="tabContentixTableFormattedData">
              <div class="genericTableAddRowContainer">
                <span class="genericTableAddRowLinkContainer" onclick="addReportCategoriesTableRow();"><fmt:message key="settings.reportCategories.addReportCategoryLink"/></span>
              </div>

              <div id="noReportCategoriesAddedMessageContainer" class="genericTableNotAddedMessageContainer">
                <span>
                 <fmt:message key="settings.reportCategories.noReportCategoriesAddedPrefix"/> <span onclick="addReportCategoriesTableRow();" class="genericTableAddRowLink"> <fmt:message key="settings.reportCategories.noReportCategoriesAddedClickHereLink"/></span>.</span>
              </div>

              <div id="reportCategoriesTableContainer"></div>
            </div>
  	  
            <div class="genericFormSubmitSectionOffTab">
              <input type="submit" class="formvalid" value="<fmt:message key="settings.reportCategories.saveButton"/>">
            </div>

	      </form>
	    </div>
	  </div>
    
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>