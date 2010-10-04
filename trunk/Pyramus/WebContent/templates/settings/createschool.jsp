<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title><fmt:message key="settings.createSchool.pageTitle"/></title>

    <jsp:include page="/templates/generic/head_generic.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonrequest_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonform_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/scriptaculous_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/tabs_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/table_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/draftapi_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/validation_support.jsp"></jsp:include>

    <script type="text/javascript">

      function addEmailTableRow() {
        getIxTableById('emailTable').addRow(['', '', '', '', '']);
      };
  
      function addPhoneTableRow() {
        getIxTableById('phoneTable').addRow(['', '', '', '', '']);
      };
  
      function addAddressTableRow() {
        getIxTableById('addressTable').addRow(['', '', '', '', '', '', '', '', '']);
      };
          
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
        // E-mail address

        var emailTable = new IxTable($('emailTable'), {
          id : "emailTable",
          columns : [{
            left : 0,
            width : 30,
            dataType: 'radiobutton',
            editable: true,
            paramName: 'defaultAddress',
            tooltip: '<fmt:message key="settings.createSchool.emailTableDefaultTooltip"/>',
          }, {
            header : '<fmt:message key="settings.createSchool.emailTableTypeHeader"/>',
            width: 150,
            left : 30,
            dataType: 'select',
            editable: true,
            paramName: 'contactTypeId',
            options: [
              <c:forEach var="contactType" items="${contactTypes}" varStatus="vs">
                {text: "${contactType.name}", value: ${contactType.id}}
                <c:if test="${not vs.last}">,</c:if>
              </c:forEach>
            ]
          }, {
            header : '<fmt:message key="settings.createSchool.emailTableAddressHeader"/>',
            left : 188,
            width : 200,
            dataType: 'text',
            editable: true,
            paramName: 'email',
            editorClassNames: 'email'
          }, {
            width: 30,
            left: 396,
            dataType: 'button',
            paramName: 'addButton',
            hidden: true,
            imgsrc: GLOBAL_contextPath + '/gfx/list-add.png',
            tooltip: '<fmt:message key="settings.createSchool.emailTableAddTooltip"/>',
            onclick: function (event) {
              addEmailTableRow();
            }
          }, {
            width: 30,
            left: 396,
            dataType: 'button',
            paramName: 'removeButton',
            hidden: true,
            imgsrc: GLOBAL_contextPath + '/gfx/list-remove.png',
            tooltip: '<fmt:message key="settings.createSchool.emailTableRemoveTooltip"/>',
            onclick: function (event) {
              event.tableObject.deleteRow(event.row);
            }
          }]
        });
        emailTable.addListener("rowAdd", function (event) {
          var emailTable = event.tableObject; 
          var enabledButton = event.row == 0 ? 'addButton' : 'removeButton';
          emailTable.showCell(event.row, emailTable.getNamedColumnIndex(enabledButton));
        });
        addEmailTableRow();
        emailTable.setCellValue(0, 0, true);

        // Addresses

        var addressTable = new IxTable($('addressTable'), {
          id : "addressTable",
          columns : [{
            left : 0,
            width : 30,
            dataType: 'radiobutton',
            editable: true,
            paramName: 'defaultAddress',
            tooltip: '<fmt:message key="settings.createSchool.addressTableDefaultTooltip"/>',
          }, {
            header : '<fmt:message key="settings.createSchool.addressTableTypeHeader"/>',
            left : 30,
            width : 150,
            dataType: 'select',
            editable: true,
            paramName: 'contactTypeId',
            options: [
              <c:forEach var="contactType" items="${contactTypes}" varStatus="vs">
                {text: "${contactType.name}", value: ${contactType.id}}
                <c:if test="${not vs.last}">,</c:if>
              </c:forEach>
            ]
          }, {
            header : '<fmt:message key="settings.createSchool.addressTableNameHeader"/>',
            left : 188,
            width : 150,
            dataType: 'text',
            editable: true,
            paramName: 'name'
          }, {
            header : '<fmt:message key="settings.createSchool.addressTableStreetHeader"/>',
            left : 344,
            width : 150,
            dataType: 'text',
            editable: true,
            paramName: 'street'
          }, {
            header : '<fmt:message key="settings.createSchool.addressTablePostalCodeHeader"/>',
            left : 502,
            width : 100,
            dataType: 'text',
            editable: true,
            paramName: 'postal'
          }, {
            header : '<fmt:message key="settings.createSchool.addressTableCityHeader"/>',
            left : 610,
            width : 150,
            dataType: 'text',
            editable: true,
            paramName: 'city'
          }, {
            header : '<fmt:message key="settings.createSchool.addressTableCountryHeader"/>',
            left : 768,
            width : 100,
            dataType: 'text',
            editable: true,
            paramName: 'country'
          }, {
            width: 30,
            left: 874,
            dataType: 'button',
            paramName: 'addButton',
            hidden: true,
            imgsrc: GLOBAL_contextPath + '/gfx/list-add.png',
            tooltip: '<fmt:message key="settings.createSchool.addressTableAddTooltip"/>',
            onclick: function (event) {
              addAddressTableRow(event.tableObject);
            }
          }, {
            width: 30,
            left: 874,
            dataType: 'button',
            paramName: 'removeButton',
            hidden: true,
            imgsrc: GLOBAL_contextPath + '/gfx/list-remove.png',
            tooltip: '<fmt:message key="settings.createSchool.addressTableRemoveTooltip"/>',
            onclick: function (event) {
              event.tableObject.deleteRow(event.row);
            }
          }]
        });
        addressTable.addListener("rowAdd", function (event) {
          var addressTable = event.tableObject; 
          var enabledButton = event.row == 0 ? 'addButton' : 'removeButton';
          addressTable.showCell(event.row, addressTable.getNamedColumnIndex(enabledButton));
        });
        addAddressTableRow();
        addressTable.setCellValue(0, 0, true);

        // Phone numbers

        var phoneTable = new IxTable($('phoneTable'), {
          id : "phoneTable",
          columns : [{
            left : 0,
            width : 30,
            dataType: 'radiobutton',
            editable: true,
            paramName: 'defaultNumber',
            tooltip: '<fmt:message key="settings.createSchool.phoneTableDefaultTooltip"/>',
          }, {
            header : '<fmt:message key="settings.createSchool.phoneTableTypeHeader"/>',
            width: 150,
            left : 30,
            dataType: 'select',
            editable: true,
            paramName: 'contactTypeId',
            options: [
              <c:forEach var="contactType" items="${contactTypes}" varStatus="vs">
                {text: "${contactType.name}", value: ${contactType.id}}
                <c:if test="${not vs.last}">,</c:if>
              </c:forEach>
            ]
          }, {
            header : '<fmt:message key="settings.createSchool.phoneTableNumberHeader"/>',
            left : 188,
            width : 200,
            dataType: 'text',
            editable: true,
            paramName: 'phone'
          }, {
            width: 30,
            left: 396,
            dataType: 'button',
            paramName: 'addButton',
            hidden: true,
            imgsrc: GLOBAL_contextPath + '/gfx/list-add.png',
            tooltip: '<fmt:message key="settings.createSchool.phoneTableAddTooltip"/>',
            onclick: function (event) {
              addPhoneTableRow(event.tableObject);
            }
          }, {
            width: 30,
            left: 396,
            dataType: 'button',
            paramName: 'removeButton',
            hidden: true,
            imgsrc: GLOBAL_contextPath + '/gfx/list-remove.png',
            tooltip: '<fmt:message key="settings.createSchool.phoneTableRemoveTooltip"/>',
            onclick: function (event) {
              event.tableObject.deleteRow(event.row);
            }
          }]
        });
        phoneTable.addListener("rowAdd", function (event) {
          var phoneTable = event.tableObject; 
          var enabledButton = event.row == 0 ? 'addButton' : 'removeButton';
          phoneTable.showCell(event.row, phoneTable.getNamedColumnIndex(enabledButton));
        });
        addPhoneTableRow();
        phoneTable.setCellValue(0, 0, true);

        // Variables

        var variablesTable = new IxTable($('variablesTable'), {
          id : "variablesTable",
          columns : [{
            left: 8,
            width: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
            tooltip: '<fmt:message key="settings.createSchool.variablesTableEditTooltip"/>',
            onclick: function (event) {
              var table = event.tableObject;
              var valueColumn = table.getNamedColumnIndex('value');
              table.setCellEditable(event.row, valueColumn, table.isCellEditable(event.row, valueColumn) == false);
            }
          }, {
            dataType : 'hidden',
            editable: false,
            paramName: 'key'
          },{
            left : 38,
            width: 150,
            dataType : 'text',
            editable: false,
            paramName: 'name'
          }, {
            left : 188,
            width : 750,
            dataType: 'text',
            editable: false,
            paramName: 'value'
          }]
        });
        <c:forEach var="variableKey" items="${variableKeys}">
          var rowNumber = variablesTable.addRow([
            '',
            '${fn:replace(variableKey.variableKey, "'", "\\'")}',
            '${fn:replace(variableKey.variableName, "'", "\\'")}',
            ''
          ]);
  
          var dataType;
          <c:choose>
            <c:when test="${variableKey.variableType == 'NUMBER'}">
              dataType = 'number';
            </c:when>
            <c:when test="${variableKey.variableType == 'DATE'}">
              dataType = 'date';
            </c:when>
            <c:when test="${variableKey.variableType == 'BOOLEAN'}">
              dataType = 'checkbox';
            </c:when>
            <c:otherwise>
              dataType = 'text';
            </c:otherwise>
          </c:choose>
          
          variablesTable.setCellDataType(rowNumber, 3, dataType);
        </c:forEach>
      };
    </script>
    
  </head>
  <body onload="onLoad(event);" ix:enabledrafting="true">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
  
    <h1 class="genericPageHeader"><fmt:message key="settings.createSchool.pageTitle" /></h1>
  
    <div class="genericFormContainer"> 
      <div class="tabLabelsContainer" id="tabs">
        <a class="tabLabel" href="#basic">
          <fmt:message key="settings.createSchool.tabLabelBasic"/>
        </a>
      </div>
      
      <form action="createschool.json" method="post" ix:jsonform="true" ix:useglasspane="true">
        <div id="basic" class="tabContent">

        <div class="genericFormSection">
          <jsp:include page="/templates/generic/fragments/formtitle.jsp">
            <jsp:param name="titleLocale" value="settings.createSchool.codeTitle"/>
            <jsp:param name="helpLocale" value="settings.createSchool.codeHelp"/>
          </jsp:include>           
          <input type="text" name="code" class="required" size="20"/>
        </div>

        <div class="genericFormSection">
          <jsp:include page="/templates/generic/fragments/formtitle.jsp">
            <jsp:param name="titleLocale" value="settings.createSchool.nameTitle"/>
            <jsp:param name="helpLocale" value="settings.createSchool.nameHelp"/>
          </jsp:include>           
          <input type="text" name="name" class="required" size="40"/>
        </div>
        
        <div class="genericFormSection">
          <jsp:include page="/templates/generic/fragments/formtitle.jsp">
            <jsp:param name="titleLocale" value="settings.createSchool.tagsTitle"/>
            <jsp:param name="helpLocale" value="settings.createSchool.tagsHelp"/>
          </jsp:include>
          <input type="text" id="tags" name="tags" size="40"/>
          <div id="tags_choices" class="autocomplete_choices"></div>
        </div>

        <div class="genericFormSection">  
          <jsp:include page="/templates/generic/fragments/formtitle.jsp">
            <jsp:param name="titleLocale" value="settings.createSchool.addressesTitle"/>
            <jsp:param name="helpLocale" value="settings.createSchool.addressesHelp"/>
          </jsp:include>                                         
          <div id="addressTable"></div>
        </div>

        <div class="genericFormSection">  
          <jsp:include page="/templates/generic/fragments/formtitle.jsp">
            <jsp:param name="titleLocale" value="settings.createSchool.emailTableEmailsTitle"/>
            <jsp:param name="helpLocale" value="settings.createSchool.emailTableEmailsHelp"/>
          </jsp:include>                                         
          <div id="emailTable"></div>
        </div>

        <div class="genericFormSection">  
          <jsp:include page="/templates/generic/fragments/formtitle.jsp">
            <jsp:param name="titleLocale" value="settings.createSchool.phoneNumbersTitle"/>
            <jsp:param name="helpLocale" value="settings.createSchool.phoneNumbersHelp"/>
          </jsp:include>                                         
          <div id="phoneTable"></div>
        </div>

        <div class="genericFormSection">  
          <jsp:include page="/templates/generic/fragments/formtitle.jsp">
            <jsp:param name="titleLocale" value="settings.createSchool.variablesTitle"/>
            <jsp:param name="helpLocale" value="settings.createSchool.variablesHelp"/>
          </jsp:include>           
          <div id="variablesTable"></div>
        </div>

      </div>
      <div class="genericFormSubmitSectionOffTab">
        <input type="submit" class="formvalid" value="<fmt:message key="settings.createSchool.saveButton"/>">
      </div>
    </form>
  </div>
    
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>