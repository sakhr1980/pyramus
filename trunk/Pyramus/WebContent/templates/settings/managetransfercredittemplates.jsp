<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title><fmt:message key="settings.manageTransferCreditTemplates.pageTitle"></fmt:message></title>
    <jsp:include page="/templates/generic/head_generic.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonrequest_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/dialog_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/table_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/tabs_support.jsp"></jsp:include>
    
    <script type="text/javascript">
      function onLoad(event) {
        var tabControl = new IxProtoTabs($('tabs'));

        var transferCreditTemplatesTable = new IxTable($('transferCreditTemplatesTableContainer'), {
          id : "transferCreditTemplatesTable",
          columns : [{
            header : '<fmt:message key="settings.manageTransferCreditTemplates.transferCreditTemplatesTableNameHeader"/>',
            left : 8,
            right : 76,
            dataType: 'text',
            editable: false,
            paramName: 'name'
          }, {
            right : 30,
            width : 30,
            dataType : 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
            tooltip: '<fmt:message key="settings.manageTransferCreditTemplates.transferCreditTemplatesTableEditButtonHeader"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var transferCreditTemplateId = table.getCellValue(event.row, table.getNamedColumnIndex('transferCreditTemplateId'));
              redirectTo(GLOBAL_contextPath + '/settings/edittransfercredittemplate.page?transferCreditTemplate=' + transferCreditTemplateId);
            }
          }, {
            right : 4,
            width : 26,
            dataType : 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/edit-delete.png',
            tooltip: '<fmt:message key="settings.manageTransferCreditTemplates.transferCreditTemplatesTableDeleteButtonHeader"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var transferCreditTemplateName = table.getCellValue(event.row, table.getNamedColumnIndex('name'));
              var transferCreditTemplateId = table.getCellValue(event.row, table.getNamedColumnIndex('transferCreditTemplateId'));
              var url = GLOBAL_contextPath + "/simpledialog.page?localeId=settings.manageTransferCreditTemplates.transferCreditTemplateDeleteConfirmDialogContent&localeParams=" + encodeURIComponent(transferCreditTemplateName);
              
              var dialog = new IxDialog({
                id : 'confirmRemoval',
                contentURL : url,
                centered : true,
                showOk : true,  
                showCancel : true,
                autoEvaluateSize: true,
                title : '<fmt:message key="settings.manageTransferCreditTemplates.transferCreditTemplateDeleteConfirmDialogTitle"/>',
                okLabel : '<fmt:message key="settings.manageTransferCreditTemplates.transferCreditTemplateDeleteConfirmDialogOkLabel"/>',
                cancelLabel : '<fmt:message key="settings.manageTransferCreditTemplates.transferCreditTemplateDeleteConfirmDialogCancelLabel"/>'
              });
            
              dialog.addDialogListener( function(event) {
                var dlg = event.dialog;
            
                switch (event.name) {
                  case 'okClick':
                    JSONRequest.request("settings/deletetransfercredittemplate.json", {
                      parameters: {
                        transferCreditTemplateId: transferCreditTemplateId
                      },
                      onSuccess: function (jsonResponse) {
                        window.location.reload();
                      }
                    });   
                  break;
                }
              });
            
              dialog.open(); 
            }
          }, {
            dataType: 'hidden',
            paramName: 'transferCreditTemplateId'
          }]
        });

        var rows = new Array();
        <c:forEach var="transferCreditTemplate" items="${transferCreditTemplates}">
          rows.push([
            '${fn:escapeXml(transferCreditTemplate.name)}', 
            null, 
            null, 
            '${transferCreditTemplate.id}']);
        </c:forEach>
        transferCreditTemplatesTable.addRows(rows);        
        
        <c:if test="${fn:length(transferCreditTemplates) > 0}">
          $('noTransferCreditTemapltesAddedMessageContainer').setStyle({
            display: 'none'
          });
        </c:if>
      };
    </script>
    
  </head>
  <body onload="onLoad(event);">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
  
    <h1 class="genericPageHeader"><fmt:message key="settings.manageTransferCreditTemplates.pageTitle"/></h1>
  
    <div id="listTransferCreditTemplatesFormContainer">
      <div class="genericFormContainer"> 
		    <div class="tabLabelsContainer" id="tabs">
		      <a class="tabLabel" href="#transferCreditTemplates">
		        <span class="tabLabelLeftTopCorner">
		          <span class="tabLabelRightTopCorner">
		            <fmt:message key="settings.manageTransferCreditTemplates.tabLabelTransferCreditTemplates"/>
		          </span>
		        </span>
		      </a>
		    </div>
              
		    <div id="transferCreditTemplates" class="tabContentixTableFormattedData">
          <div class="genericTableAddRowContainer">
            <a class="genericTableAddRowLinkContainer" href="${pageContext.request.contextPath}/settings/createtransfercredittemplate.page"><fmt:message key="settings.manageTransferCreditTemplates.addTransferCreditTemplate"/></a>
          </div>
		      
	        <div id="noTransferCreditTemapltesAddedMessageContainer" class="genericTableNotAddedMessageContainer">
	          <span><fmt:message key="settings.manageTransferCreditTemplates.noTransferCreditTemplatesAddedPreFix"/> <a href="${pageContext.request.contextPath}/settings/createtransfercredittemplate.page" class="genericTableAddRowLink"><fmt:message key="settings.manageTransferCreditTemplates.noTransferCreditTemplatesAddedClickHereLink"/></a>.</span>
	        </div>

          <div id="transferCreditTemplatesTableContainer"></div>
		    </div>
		  </div>
		</div>
    
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>