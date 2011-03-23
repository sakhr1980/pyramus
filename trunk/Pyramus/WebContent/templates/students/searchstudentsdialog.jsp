<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <jsp:include page="/templates/generic/head_generic.jsp"></jsp:include>
    <jsp:include page="/templates/generic/table_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonrequest_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/scriptaculous_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/searchnavigation_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/studentinfopopup_support.jsp"></jsp:include>
    
    <script type="text/javascript">

      /**
       * Convenience method to return the row index of the given student in the given student table.
       *
       * @param tableId The table identifier
       * @param studentId The student identifier
       *
       * @return The row index of the given student in the given student table. Returns -1 if not found.
       */
      function getStudentRowIndex(tableId, studentId) {
        var table = getIxTableById(tableId);
        if (table) {
          for (var i = 0; i < table.getRowCount(); i++) {
            var tableStudentId = table.getCellValue(i, table.getNamedColumnIndex('studentId'));
            if (tableStudentId == studentId) {
              return i;
            }
          }
        }
        return -1;
      }

      /**
       * Performs the search and displays the results of the given page.
       *
       * @param page The results page to be shown after the search
       */
      function doSearch(page) {
        var searchStudentsForm = $("searchStudentsForm");
        JSONRequest.request("students/searchstudentsdialog.json", {
          parameters: {
            query: searchStudentsForm.name.value,
            studentFilter: searchStudentsForm.studentFilter.value,
            page: page
          },
          onSuccess: function (jsonResponse) {
            var resultsTable = getIxTableById('searchResultsTable');
            resultsTable.detachFromDom();
            resultsTable.deleteAllRows();
            var results = jsonResponse.results;
            for (var i = 0; i < results.length; i++) {
              var studentName = results[i].lastName + ', ' + results[i].firstName;
              resultsTable.addRow(['', studentName.escapeHTML(), results[i].id, results[i].abstractStudentId, results[i].lodging]);
              var rowIndex = getStudentRowIndex('studentsTable', results[i].id);
              if (rowIndex != -1) {
                resultsTable.disableRow(resultsTable.getRowCount() - 1);
              } 
            }
            resultsTable.reattachToDom();
            getSearchNavigationById('searchResultsNavigation').setTotalPages(jsonResponse.pages);
            getSearchNavigationById('searchResultsNavigation').setCurrentPage(jsonResponse.page);
            $('modalSearchResultsStatusMessageContainer').innerHTML = jsonResponse.statusMessage;
          } 
        });
      }
      
      /**
       * Invoked when the user submits the search form. We cancel the submit event
       * and delegate the work to the doSearch method.
       *
       * @param event The search form submit event
       */
      function onSearchStudents(event) {
        Event.stop(event);
        doSearch(0);
      }

      /**
       * Returns the identifiers of the students selected in this dialog.
       *
       * @return The students selected in this dialog
       */
      function getResults() {
        var results = new Array();
        var table = getIxTableById('studentsTable');
        for (var i = 0; i < table.getRowCount(); i++) {
          var studentName = table.getCellValue(i, table.getNamedColumnIndex('name'));
          var studentId = table.getCellValue(i, table.getNamedColumnIndex('studentId'));
          var abstractStudentId = table.getCellValue(i, table.getNamedColumnIndex('abstractStudentId'));
          var lodging = table.getCellValue(i, table.getNamedColumnIndex('lodging'));

          results.push({
            name: studentName,
            id: studentId,
            abstractStudentId: abstractStudentId,
            lodging: lodging
          });
        }
        return {
          students: results
        };
      }

      /**
       * Called when this dialog loads. Initializes the search navigation and student tables.
       *
       * @param event The page load event
       */
      function onLoad(event) {
        new IxSearchNavigation($('modalSearchResultsPagesContainer'), {
          id: 'searchResultsNavigation',
          maxNavigationPages: 9,
          onclick: function(event) {
            doSearch(event.page);
          }
        });

        var searchResultsTable = new IxTable($('searchResultsTableContainer'), {
          id: 'searchResultsTable',
          columns : [
            {
              width: 30,
              left: 8,
              dataType: 'button',
              paramName: 'studentInfoButton',
              imgsrc: GLOBAL_contextPath + '/gfx/info.png',
              tooltip: '<fmt:message key="students.searchStudentsDialog.searchResultsTableStudentInfoTooltip"/>',
              onclick: function (event) {
                var table = event.tableObject;
                var abstractStudentId = table.getCellValue(event.row, table.getNamedColumnIndex('abstractStudentId'));
                var button = table.getCellEditor(event.row, table.getNamedColumnIndex('studentInfoButton'));
                openStudentInfoPopupOnElement(button, abstractStudentId);
              } 
            }, {
              left: 46,
              right: 8,
              dataType: 'text',
              editable: false,
              selectable: false,
              paramName: 'name',
              onclick: function (event) {
                var table = event.tableObject;
                table.disableRow(event.row);
                var studentId = table.getCellValue(event.row, table.getNamedColumnIndex('studentId'));
                var studentName = table.getCellValue(event.row, table.getNamedColumnIndex('name'));
                var abstractStudentId = table.getCellValue(event.row, table.getNamedColumnIndex('abstractStudentId'));
                var lodging = table.getCellValue(event.row, table.getNamedColumnIndex('lodging'));
                getIxTableById('studentsTable').addRow([studentName, studentId, abstractStudentId, lodging]);
              }
            }, {
              dataType: 'hidden',
              paramName: 'studentId'
            }, {
              dataType: 'hidden',
              paramName: 'abstractStudentId'
            }, {
              dataType: 'hidden',
              paramName: 'lodging'
            }
          ]
        });
        searchResultsTable.domNode.addClassName("modalDialogSearchResultsIxTable");
        
        var studentsTable = new IxTable($('studentsTableContainer'), {
          id: 'studentsTable',
          columns : [ 
            {
              left: 8,
              right: 8,
              dataType: 'text',
              editable: false,
              selectable: false,
              paramName: 'name',
              onclick: function (event) {
                var table = event.tableObject;
                var studentId = table.getCellValue(event.row, table.getNamedColumnIndex('studentId'));
                table.deleteRow(event.row);
                var rowIndex = getStudentRowIndex('searchResultsTable', studentId);
                if (rowIndex != -1) {
                  var resultsTable = getIxTableById('searchResultsTable');
                  resultsTable.enableRow(rowIndex);
                }
              }
            }, {
              dataType: 'hidden',
              paramName: 'studentId'
            }, {
              dataType: 'hidden',
              paramName: 'abstractStudentId'
            }, {
              dataType: 'hidden',
              paramName: 'lodging'
            }
          ]
        });
        studentsTable.domNode.addClassName("modalDialogStudentsIxTable");

        $('searchStudentsForm').name.focus();
      }
    </script>

  </head>
  <body onload="onLoad(event);">

    <div id="searchStudentsDialogSearchContainer" class="modalSearchContainer">
      <div class="modalSearchTabLabel"><fmt:message key="students.searchStudentsDialog.searchTitle"/></div> 
      <div class="modalSearchTabContent">
	      <div class="genericFormContainer"> 
	        
	        <form id="searchStudentsForm" method="post" onsubmit="onSearchStudents(event);">

	          <div class="genericFormSection">
                <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                  <jsp:param name="titleLocale" value="students.searchStudentsDialog.nameTitle"/>
                  <jsp:param name="helpLocale" value="students.searchStudentsDialog.nameHelp"/>
                </jsp:include>            
	            <input type="text" name="name" size="40"/>
	          </div>
	          
	          <div class="genericFormSection">  
		          <jsp:include page="/templates/generic/fragments/formtitle.jsp">
		            <jsp:param name="titleLocale" value="students.searchStudentsDialog.studentFilterTitle"/>
		            <jsp:param name="helpLocale" value="students.searchStudentsDialog.studentFilterHelp"/>
		          </jsp:include>                                     
		          <select name="studentFilter">
		            <option value="SKIP_INACTIVE"><fmt:message key="students.searchStudentsDialog.studentFilterSkipInactiveOption"/></option>
		            <option value="INCLUDE_INACTIVE"><fmt:message key="students.searchStudentsDialog.studentFilterIncludeInactiveOption"/></option>
		            <option value="ONLY_INACTIVE"><fmt:message key="students.searchStudentsDialog.studentFilterOnlyInactiveOption"/></option>
		          </select>
		        </div>
  
	          <div class="genericFormSubmitSection">
	            <input type="submit" value="<fmt:message key="students.searchStudentsDialog.searchButton"/>"/>
	          </div>
	    
	        </form>
	      </div>
      </div>
      
      <div id="searchResultsContainer" class="modalSearchResultsContainer">
        <div class="modalSearchResultsTabLabel"><fmt:message key="students.searchStudentsDialog.searchResultsTitle"/></div>
        <div id="modalSearchResultsStatusMessageContainer" class="modalSearchResultsMessageContainer"></div>    
        <div id="searchResultsTableContainer" class="modalSearchResultsTabContent"></div>
        <div id="modalSearchResultsPagesContainer" class="modalSearchResultsPagesContainer"></div>
      </div>
      
    </div>
    
    <div id="studentsContainer" class="modalSelectedItemsContainer">
      <div class="modalSelectedItemsTabLabel"><fmt:message key="students.searchStudentsDialog.selectedStudentsTitle"/></div>
      <div id="studentsTableContainer" class="modalSelectedItemsTabContent"></div>
    </div>

  </body>
</html>