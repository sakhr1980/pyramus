<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title><fmt:message key="settings.listGradingScales.pageTitle"></fmt:message></title>
    <jsp:include page="/templates/generic/head_generic.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonrequest_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/dialog_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/table_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/tabs_support.jsp"></jsp:include>
    
    <script type="text/javascript">
      function onLoad(event) {
        var tabControl = new IxProtoTabs($('tabs'));

        var gradingScalesTable = new IxTable($('gradingScaleListTableContainer'), {
          id : "gradingScalesTable",
          columns : [{
            header : '<fmt:message key="settings.listGradingScales.gradingScalesTableNameHeader"/>',
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
            tooltip: '<fmt:message key="settings.listGradingScales.gradeTableEditTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var scaleId = table.getCellValue(event.row, table.getNamedColumnIndex('scaleId'));
              redirectTo(GLOBAL_contextPath + '/settings/editgradingscale.page?gradingScaleId=' + scaleId);
            }
          }, {
            right : 4,
            width : 26,
            dataType : 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/edit-delete.png',
            tooltip: '<fmt:message key="settings.listGradingScales.gradeTableArchiveTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var gradeName = table.getCellValue(event.row, table.getNamedColumnIndex('name'));
              var scaleId = table.getCellValue(event.row, table.getNamedColumnIndex('scaleId'));
              var url = GLOBAL_contextPath + "/simpledialog.page?localeId=settings.listGradingScales.gradingScaleArchiveConfirmDialogContent&localeParams=" + encodeURIComponent(gradeName);
              
              var dialog = new IxDialog({
                id : 'confirmRemoval',
                contentURL : url,
                centered : true,
                showOk : true,  
                showCancel : true,
                autoEvaluateSize: true,
                title : '<fmt:message key="settings.listGradingScales.gradingScaleArchiveConfirmDialogTitle"/>',
                okLabel : '<fmt:message key="settings.listGradingScales.gradingScaleArchiveConfirmDialogOkLabel"/>',
                cancelLabel : '<fmt:message key="settings.listGradingScales.gradingScaleArchiveConfirmDialogCancelLabel"/>'
              });
            
              dialog.addDialogListener( function(event) {
                var dlg = event.dialog;
            
                switch (event.name) {
                  case 'okClick':
                    JSONRequest.request("settings/archivegradingscale.json", {
                      parameters: {
                        gradingScaleId: scaleId
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
            paramName: 'scaleId'
          }]
        });

        var rows = new Array();
        <c:forEach var="gradingScale" items="${gradingScales}">
          rows.push(['${gradingScale.name}', null, null, '${gradingScale.id}']);
        </c:forEach>
        gradingScalesTable.addRows(rows);
      };
    </script>
    
  </head>
  <body onload="onLoad(event);">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
  
    <h1 class="genericPageHeader"><fmt:message key="settings.listGradingScales.pageTitle"/></h1>
  
    <div id="listGradingScaleEditFormContainer">
      <div class="genericFormContainer"> 
		    <div class="tabLabelsContainer" id="tabs">
		      <a class="tabLabel" href="#listGradingScales">
		        <span class="tabLabelLeftTopCorner">
		          <span class="tabLabelRightTopCorner">
		            <fmt:message key="settings.listGradingScales.tabLabelGradingScales"/>
		          </span>
		        </span>
		      </a>
		    </div>
		    
		    <div id="listGradingScales" class="tabContentixTableFormattedData">
          <div id="gradingScaleListTableContainer"></div>
		    </div>
		  </div>
		</div>
    
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>