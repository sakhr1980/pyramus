<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
  <head>
    <title><fmt:message key="settings.academicTerms.pageTitle"/></title>

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

      var archivedRowIndex;

      function addTermsTableRow() {
        var table = getIxTableById('termsTable');
        var rowIndex = table.addRow(['', '', 0, 0, '', '', -1, 1]);
        for (var i = 0; i < table.getColumnCount(); i++) {
          table.setCellEditable(rowIndex, i, true);
        }
        $('noTermsAddedMessageContainer').setStyle({
          display: 'none'
        });
        table.showCell(rowIndex, table.getNamedColumnIndex('removeButton'));
      }
      
      function onLoad(event) {
        tabControl = new IxProtoTabs($('tabs'));

        var termsTable = new IxTable($('termsTable'), {
          id : "termsTable",
          columns : [ {
            left: 8,
            width: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
            tooltip: '<fmt:message key="settings.academicTerms.academicTermTableEditAcademicTermTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              for (var i = 0; i < table.getColumnCount(); i++) {
                table.setCellEditable(event.row, i, table.isCellEditable(event.row, i) == false);
              }
              table.setCellValue(event.row, table.getNamedColumnIndex('modified'), 1);
            }
          }, {
            header : '<fmt:message key="settings.academicTerms.termsTableNameHeader"/>',
            left : 38,
            width : 250,
            dataType: 'text',
            editable: false,
            paramName: 'name',
            editorClassNames: 'required'
          }, {
            header : '<fmt:message key="settings.academicTerms.termsTableStartDateHeader"/>',
            left : 296,
            width : 150,
            dataType : 'date',
            editable: false,
            paramName: 'startDate',
            editorClassNames: 'required'
          }, {
            header : '<fmt:message key="settings.academicTerms.termsTableEndDateHeader"/>',
            left: 438,
            right : 30,
            dataType: 'date',
            editable: false,
            paramName: 'endDate',
            editorClassNames: 'required'
          }, {
            right: 8,
            width: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/edit-delete.png',
            tooltip: '<fmt:message key="settings.academicTerms.termsTableArchiveRowTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var termId = table.getCellValue(event.row, table.getNamedColumnIndex('termId'));
              var termName = table.getCellValue(event.row, table.getNamedColumnIndex('name'));
              var url = GLOBAL_contextPath + "/simpledialog.page?localeId=settings.academicTerms.termArchiveConfirmDialogContent&localeParams=" + encodeURIComponent(termName);

              archivedRowIndex = event.row; 
                 
              var dialog = new IxDialog({
                id : 'confirmRemoval',
                contentURL : url,
                centered : true,
                showOk : true,  
                showCancel : true,
                autoEvaluateSize: true,
                title : '<fmt:message key="settings.academicTerms.termArchiveConfirmDialogTitle"/>',
                okLabel : '<fmt:message key="settings.academicTerms.termArchiveConfirmDialogOkLabel"/>',
                cancelLabel : '<fmt:message key="settings.academicTerms.termArchiveConfirmDialogCancelLabel"/>'
              });
            
              dialog.addDialogListener(function(event) {
                switch (event.name) {
                  case 'okClick':
                    JSONRequest.request("settings/archiveacademicterm.json", {
                      parameters: {
                        academicTermId: termId
                      },
                      onSuccess: function (jsonResponse) {
                        getIxTableById('termsTable').deleteRow(archivedRowIndex);
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
            tooltip: '<fmt:message key="settings.academicTerms.termsTableRemoveRowTooltip"/>',
            onclick: function (event) {
              event.tableObject.deleteRow(event.row);
              if (event.tableObject.getRowCount() == 0) {
                $('noTermsAddedMessageContainer').setStyle({
                  display: ''
                });
              }
            },
            paramName: 'removeButton',
            hidden: true
          }, {
            dataType: 'hidden',
            paramName: 'termId'
          }, {
            dataType: 'hidden',
            paramName: 'modified'
          }]
        });

        var rowIndex;
        <c:forEach var="academicTerm" items="${academicTerms}">
          rowIndex = termsTable.addRow([
            '',
            '${fn:replace(academicTerm.name, "'", "\\'")}',
            ${academicTerm.startDate.time},
            ${academicTerm.endDate.time},
            '',
            '',
            ${academicTerm.id},
            0
          ]);
          termsTable.showCell(rowIndex, termsTable.getNamedColumnIndex('archiveButton'));
        </c:forEach>

        if (termsTable.getRowCount() > 0) {
          $('noTermsAddedMessageContainer').setStyle({
            display: 'none'
          });
        }
      }
        
    </script>
  </head>
  
  <body onload="onLoad(event);" ix:enabledrafting="true">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
    
    <h1 class="genericPageHeader"><fmt:message key="settings.academicTerms.pageTitle"/></h1>
    
    <div id="manageAcademicTermsFormContainer"> 
	    <div class="genericFormContainer"> 
	      <form action="saveacademicterms.json" method="post" ix:jsonform="true"  ix:useglasspane="true">
	  
	        <div class="tabLabelsContainer" id="tabs">
	          <a class="tabLabel" href="#manageAcademicTermsListTerms">
	            <fmt:message key="settings.academicTerms.tabLabelAcademicTerms"/>
	          </a>
	        </div>
	        
          <div id="manageAcademicTermsListTerms" class="tabContentixTableFormattedData">
            <div class="genericTableAddRowContainer">
              <span class="genericTableAddRowLinkContainer" onclick="addTermsTableRow();"><fmt:message key="settings.academicTerms.addTermLink"/></span>
            </div>
              
            <div id="noTermsAddedMessageContainer" class="genericTableNotAddedMessageContainer">
              <span><fmt:message key="settings.academicTerms.noTermsAddedPreFix"/> <span onclick="addTermsTableRow();" class="genericTableAddRowLink"><fmt:message key="settings.academicTerms.noTermsAddedClickHereLink"/></span>.</span>
            </div>
            
            <div id="termsTable"></div>
          </div>
	  
          <div class="genericFormSubmitSectionOffTab">
            <input type="submit" class="formvalid" value="<fmt:message key="settings.academicTerms.saveButton"/>">
          </div>

	      </form>
	    </div>
	  </div>
    
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>