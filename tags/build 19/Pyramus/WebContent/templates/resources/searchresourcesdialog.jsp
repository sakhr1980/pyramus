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
    
    <script type="text/javascript">

      /**
       * Convenience method to return the row index of the given resource in the given resource table.
       *
       * @param tableId The table identifier
       * @param resourceId The resource identifier
       *
       * @return The row index of the given resource in the given resource table. Returns -1 if not found.
       */
      function getResourceRowIndex(tableId, resourceId) {
        var table = getIxTableById(tableId);
        if (table) {
          for (var i = 0; i < table.getRowCount(); i++) {
            var tableResourceId = table.getCellValue(i, table.getNamedColumnIndex('resourceId'));
            if (tableResourceId == resourceId) {
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
        var searchResourcesForm = $("searchResourcesForm");
        JSONRequest.request("resources/searchresources.json", {
          parameters: {
            name: searchResourcesForm.name.value,
            resourceType: searchResourcesForm.resourceType.value,
            resourceCategory: searchResourcesForm.resourceCategory.value,
            page: page
          },
          onSuccess: function (jsonResponse) {
            var resultsTable = getIxTableById('searchResultsTable');
            resultsTable.detachFromDom();
            resultsTable.deleteAllRows();
            var results = jsonResponse.results;
            for (var i = 0; i < results.length; i++) {
              resultsTable.addRow([results[i].name.escapeHTML(), results[i].id, results[i].unitCost, results[i].hourlyCost]);
              var rowIndex = getResourceRowIndex('resourcesTable', results[i].id);
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
      function onSearchResources(event) {
        Event.stop(event);
        doSearch(0);
      }

      /**
       * Returns the identifiers of the resources selected in this dialog.
       *
       * @return The resources selected in this dialog
       */
      function getResults() {
        var results = new Array();
        var table = getIxTableById('resourcesTable');
        for (var i = 0; i < table.getRowCount(); i++) {
          var resourceName = table.getCellValue(i, table.getNamedColumnIndex('name'));
          var resourceId = table.getCellValue(i, table.getNamedColumnIndex('resourceId'));
          var resourceUnitCost = table.getCellValue(i, table.getNamedColumnIndex('unitCost'));
          var resourceHourlyCost = table.getCellValue(i, table.getNamedColumnIndex('hourlyCost'));
          results.push({
            name: resourceName,
            id: resourceId,
            unitCost: resourceUnitCost,
            hourlyCost: resourceHourlyCost});
        }
        return {
          resources: results
        };
      }

      /**
       * Called when this dialog loads. Initializes the search navigation and resource tables.
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
          columns : [ {
            left: 8,
            right: 8,
            dataType: 'text',
            editable: false,
            selectable: false,
            paramName: 'name',
            onclick: function (event) {
              var table = event.tableComponent;
              table.disableRow(event.row);
              var resourceId = table.getCellValue(event.row, table.getNamedColumnIndex('resourceId'));
              var resourceName = table.getCellValue(event.row, table.getNamedColumnIndex('name'));
              var resourceUnitCost = table.getCellValue(event.row, table.getNamedColumnIndex('unitCost'));
              var resourceHourlyCost = table.getCellValue(event.row, table.getNamedColumnIndex('hourlyCost'));
              getIxTableById('resourcesTable').addRow([resourceName, resourceId, resourceUnitCost, resourceHourlyCost]);
            }
          }, {
            dataType: 'hidden',
            paramName: 'resourceId'
          }, {
            dataType: 'hidden',
            paramName: 'unitCost'
          }, {
            dataType: 'hidden',
            paramName: 'hourlyCost'
          }]
        });
        searchResultsTable.domNode.addClassName("modalDialogSearchResultsIxTable");
        
        var resourcesTable = new IxTable($('resourcesTableContainer'), {
          id: 'resourcesTable',
          columns : [ {
            left: 8,
            right: 8,
            dataType: 'text',
            editable: false,
            selectable: false,
            paramName: 'name',
            onclick: function (event) {
              var table = event.tableComponent;
              var resourceId = table.getCellValue(event.row, table.getNamedColumnIndex('resourceId'));
              table.deleteRow(event.row);
              var rowIndex = getResourceRowIndex('searchResultsTable', resourceId);
              if (rowIndex != -1) {
                var resultsTable = getIxTableById('searchResultsTable');
                resultsTable.enableRow(rowIndex);
              }
            }
          }, {
            dataType: 'hidden',
            paramName: 'resourceId'
          }, {
            dataType: 'hidden',
            paramName: 'unitCost'
          }, {
            dataType: 'hidden',
            paramName: 'hourlyCost'
          }]
        });
        resourcesTable.domNode.addClassName("modalDialogResourcesIxTable");

        $('searchResourcesForm').name.focus();
      }
    </script>

  </head>
  <body onload="onLoad(event);">

    <div id="searchResourcesDialogSearchContainer" class="modalSearchContainer">
      <div class="modalSearchTabLabel"><fmt:message key="resources.searchResourcesDialog.searchTitle"/></div> 
      <div class="modalSearchTabContent">
	    <div class="genericFormContainer"> 
          <form id="searchResourcesForm" method="post" onsubmit="onSearchResources(event);">
            <div class="genericFormSection columnLeft">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="resources.searchResourcesDialog.nameTitle"/>
                <jsp:param name="helpLocale" value="resources.searchResourcesDialog.nameHelp"/>
              </jsp:include>          
              <input type="text" name="name" size="35"/>
            </div>
          
            <div class="genericFormSection">
              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                <jsp:param name="titleLocale" value="resources.searchResourcesDialog.resourceTypeTitle"/>
                <jsp:param name="helpLocale" value="resources.searchResourcesDialog.resourceTypeHelp"/>
              </jsp:include>          
              <div class="searchResourcesResourceTypeContainer">
                <select name="resourceType">
                  <option></option>
                  <c:forEach var="resourceType" items="${resourceTypes}">
                    <option value="${resourceType}"><fmt:message key="resources.searchResourcesDialog.resourceType_${resourceType}"/></option>
                  </c:forEach>
                </select>
              </div>
            </div>
      
            <div class="columnClear">
              <div class="genericFormSection">
                <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                  <jsp:param name="titleLocale" value="resources.searchResourcesDialog.resourceCategoryTitle"/>
                  <jsp:param name="helpLocale" value="resources.searchResourcesDialog.resourceCategoryHelp"/>
                </jsp:include>          
                <div class="searchResourcesResourceCategoryContainer">
                  <select name="resourceCategory">
                    <option></option>
                    <c:forEach var="resourceCategory" items="${resourceCategories}">
                      <option value="${resourceCategory.id}">${resourceCategory.name}</option>
                    </c:forEach>
                  </select>
                </div>
              </div>
            </div>
  
	          <div class="genericFormSubmitSection">
	            <input type="submit" value="<fmt:message key="resources.searchResourcesDialog.searchButton"/>"/>
	          </div>
	    
	        </form>
	      </div>
      </div>
      
      <div id="searchResultsContainer" class="modalSearchResultsContainer">
        <div class="modalSearchResultsTabLabel"><fmt:message key="resources.searchResourcesDialog.searchResultsTitle"/></div>
        <div id="modalSearchResultsStatusMessageContainer" class="modalSearchResultsMessageContainer"></div>    
        <div id="searchResultsTableContainer" class="modalSearchResultsTabContent"></div>
        <div id="modalSearchResultsPagesContainer" class="modalSearchResultsPagesContainer"></div>
      </div>
      
    </div>
    
    <div id="resourcesContainer" class="modalSelectedItemsContainer">
      <div class="modalSelectedItemsTabLabel"><fmt:message key="resources.searchResourcesDialog.selectedResourcesTitle"/></div>
      <div id="resourcesTableContainer" class="modalSelectedItemsTabContent"></div>
    </div>

  </body>
</html>