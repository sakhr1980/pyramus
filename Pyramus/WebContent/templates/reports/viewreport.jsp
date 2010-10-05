<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title><fmt:message key="reports.viewReport.pageTitle"></fmt:message></title>
    <jsp:include page="/templates/generic/head_generic.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonrequest_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/dialog_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/table_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/tabs_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/dialog_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/glasspane_support.jsp"></jsp:include>
    
    <script type="text/javascript">
      var _loaderGlassPane;

      function viewStudent(studentId) {
        var url = '${pageContext.request.contextPath}/students/viewstudent.page?student=' + studentId;
        redirectTo(url);
      }

      function startContentsLoading() {
        _loaderGlassPane.show();
      }

      function stopContentsLoading() {
        _loaderGlassPane.hide();
      }
      
      function onReportContentsFrameLoad(event) {
        stopContentsLoading();
      }

      function loadReportContentsFrame(url) {
        startContentsLoading();
        var viewReportViewerFrame = $('viewReportViewerFrame');
        viewReportViewerFrame.src = url;
      }

      function updateDownloadLinks(baseURL) {
        $('downloadPDFLink').href = baseURL + '&format=PDF';
        $('downloadXLSLink').href = baseURL + '&format=XLS';
        $('downloadRTFLink').href = baseURL + '&format=DOC';
      }
    
      function showReportContent(reportId, urlParams) {
        updateDownloadLinks('${pageContext.request.contextPath}/reports/downloadreport.binary?reportId=' + reportId + urlParams);
        loadReportContentsFrame('${pageContext.request.contextPath}/reports/viewreportcontents.page?reportId=' + reportId + urlParams);
      }
      
      function onLoad(event) {
        _loaderGlassPane = new IxGlassPane(document.body, { });
        
        var tabControl = new IxProtoTabs($('tabs'));
        
        var dialog = new IxDialog({
          id : 'parametersDialog',
          contentURL : '${pageContext.request.contextPath}/reports/viewreportparameters.page?reportId=${report.id}',
          centered : true,
          showOk : true,
          showCancel : true,
          hideOnly: true,
          appearDuration: 0,
          title : '<fmt:message key="reports.viewReport.parametersDialog.dialogTitle"/>',
          okLabel : '<fmt:message key="reports.viewReport.parametersDialog.okLabel"/>', 
          cancelLabel : '<fmt:message key="reports.viewReport.parametersDialog.cancelLabel"/>'
        });  

        dialog.setSize("520px", "427px");
        dialog.getDialogElement().setStyle({
          marginLeft: '-2000px'
        });
        
        dialog.addDialogListener(function(event) {
          var dlg = event.dialog;

          switch (event.name) {
            case 'onLoad':
              
              var birtParameterDialog = dlg.getContentWindow().birtParameterDialog;
              if (birtParameterDialog) {
                if (birtParameterDialog.collect_parameter()) {
                  var hasParameters = false;

                  var parameters = birtParameterDialog.__parameter;
                  for (var i = 0, l = parameters.length; i < l; i++) {
                    var parameter = parameters[i];
                    if ((parameter.name) && (parameter.name[0] != '_')) {
                      hasParameters = true;
                      break;    
                    }
                  }

                  if (!hasParameters) {
                    dlg.hide();
                    showReportContent('${report.id}', '');
                  }
                  
                  dlg.getDialogElement().setStyle({
                    marginLeft: '0px'
                  });
                }
              }
            break;
            case 'okClick':
           	  var urlParams = '';

              var birtParameterDialog = dlg.getContentWindow().birtParameterDialog;
              if (birtParameterDialog) {
                if (birtParameterDialog.collect_parameter()) {
                  var hasParameters = false;

                  var parameters = birtParameterDialog.__parameter;
                  for (var i = 0, l = parameters.length; i < l; i++) {
                    var parameter = parameters[i];
                    if ((parameter.name) && (parameter.name[0] != '_')) {
                      urlParams += '&' + parameter.name + '=' + parameter.value;    
                    }
                  }
                }
              }

              $('reportParametersCancelled').setStyle({
                display: 'none'
              });
              showReportContent('${report.id}', urlParams);
            break;
            case 'cancelClick':
              var viewReportViewerFrame = $('viewReportViewerFrame');
              if (viewReportViewerFrame.src == 'about:blank') {
	              $('reportParametersCancelled').setStyle({
	                display: ''
	              });
              }
            break;
          }
        });
        
        dialog.open();

        Event.observe($('viewReportViewerFrame'), "load", onReportContentsFrameLoad);
      };

      function showParameterDialog() {
        var dialog = getDialog('parametersDialog');
        dialog.show();
      }
    </script>
    
  </head>
  <body onload="onLoad(event);">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
  
    <h1 class="genericPageHeader"><fmt:message key="reports.viewReport.pageTitle"/></h1>
  
    <div id="viewReportContainer">
      <div class="genericFormContainer"> 
      <div class="tabLabelsContainer" id="tabs">
        <a class="tabLabel" href="#viewReport">
          <span class="tabLabelLeftTopCorner">
            <span class="tabLabelRightTopCorner">
              <fmt:message key="reports.viewReport.tabLabelViewReport"/>
            </span>
          </span>
        </a>
      </div>
      
      <div id="viewReport" class="tabContent">
        <div id="viewReportControlsContainer">
          <a title="<fmt:message key="reports.listReports.reportsTableXLSTooltip"/>" id="downloadXLSLink" class="viewReportControlButton" style="background-image: url(${pageContext.request.contextPath}/gfx/x-office-spreadsheet.png);"></a>
          <a title="<fmt:message key="reports.listReports.reportsTablePDFTooltip"/>" id="downloadPDFLink" class="viewReportControlButton" style="background-image: url(${pageContext.request.contextPath}/gfx/pdficon_small.gif);"></a>
          <a title="<fmt:message key="reports.listReports.reportsTableDOCTooltip"/>" id="downloadRTFLink" class="viewReportControlButton" style="background-image: url(${pageContext.request.contextPath}/gfx/x-office-document.png);"></a>
          <a title="<fmt:message key="reports.viewReport.viewReportParameters"/>" id="reportParameters" class="viewReportControlButton" style="margin-left: 8px; background-image: url(${pageContext.request.contextPath}/gfx/kdb_form.png);" href="javascript:showParameterDialog()"></a>
        </div>
        <div id="reportParametersCancelled" style="display: none; text-align: center;">
          <fmt:message key="reports.viewReport.viewReportCancelled"/>
          <a href="javascript:showParameterDialog()"><fmt:message key="reports.viewReport.viewReportCancelledLink"/></a>
        </div>
        <div id="viewReportViewerContainer">
          <iframe id="viewReportViewerFrame" src="about:blank" frameborder="0" style="border: 0px; width: 100%; height: 800px;">
            
          </iframe>
        </div>
      </div>
    </div>
  </div>
    
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>