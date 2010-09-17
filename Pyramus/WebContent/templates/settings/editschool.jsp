<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title><fmt:message key="settings.editSchool.pageTitle"/></title>

    <jsp:include page="/templates/generic/head_generic.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonrequest_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonform_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/scriptaculous_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/tabs_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/table_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/draftapi_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/validation_support.jsp"></jsp:include>

    <script type="text/javascript">
      function addAddressTableRow(addressTable) {
        addressTable.addRow([-1, '', '', '', '', '', '', '', '', '']);
      }
  
      function addEmailTableRow() {
        getIxTableById('emailTable').addRow([-1, '', '', '', '', '']);
      }
  
      function addPhoneTableRow(phoneTable) {
        phoneTable.addRow([-1, '', '', '', '', '']);
      }

      function onLoad(event) {
        var tabControl = new IxProtoTabs($('tabs'));

        var addressTable = new IxTable($('addressTable'), {
          id : "addressTable",
          columns : [{
            dataType : 'hidden',
            left : 0,
            width : 0,
            paramName : 'addressId'
          }, {
            left : 0,
            width : 30,
            dataType: 'radiobutton',
            editable: true,
            paramName: 'defaultAddress',
            tooltip: '<fmt:message key="settings.editSchool.addressTableDefaultTooltip"/>',
          }, {
            header : '<fmt:message key="settings.editSchool.addressTableTypeHeader"/>',
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
            header : '<fmt:message key="settings.editSchool.addressTableNameHeader"/>',
            left : 188,
            width : 150,
            dataType: 'text',
            editable: true,
            paramName: 'name'
          }, {
            header : '<fmt:message key="settings.editSchool.addressTableStreetHeader"/>',
            left : 344,
            width : 150,
            dataType: 'text',
            editable: true,
            paramName: 'street'
          }, {
            header : '<fmt:message key="settings.editSchool.addressTablePostalCodeHeader"/>',
            left : 502,
            width : 100,
            dataType: 'text',
            editable: true,
            paramName: 'postal'
          }, {
            header : '<fmt:message key="settings.editSchool.addressTableCityHeader"/>',
            left : 610,
            width : 150,
            dataType: 'text',
            editable: true,
            paramName: 'city'
          }, {
            header : '<fmt:message key="settings.editSchool.addressTableCountryHeader"/>',
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
            tooltip: '<fmt:message key="settings.editSchool.addressTableAddTooltip"/>',
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
            tooltip: '<fmt:message key="settings.editSchool.addressTableRemoveTooltip"/>',
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

        <c:forEach var="address" items="${school.contactInfo.addresses}">
          addressTable.addRow([
            ${address.id},
            ${address.defaultAddress},
            ${address.contactType.id},
            '${fn:replace(address.name, "'", "\\'")}',
            '${fn:replace(address.streetAddress, "'", "\\'")}',
            '${fn:replace(address.postalCode, "'", "\\'")}',
            '${fn:replace(address.city, "'", "\\'")}',
            '${fn:replace(address.country, "'", "\\'")}',
            '',
            '']);
        </c:forEach>
  
        if (addressTable.getRowCount() == 0) {
          addAddressTableRow(addressTable);
          addressTable.setCellValue(0, 1, true);
        }

        var emailTable = new IxTable($('emailTable'), {
          id : "emailTable",
          columns : [ {
            dataType : 'hidden',
            left : 0,
            width : 0,
            paramName : 'emailId'
          }, {
            left : 0,
            width : 30,
            dataType: 'radiobutton',
            editable: true,
            paramName: 'defaultAddress',
            tooltip: '<fmt:message key="settings.editSchool.emailTableDefaultTooltip"/>',
          }, {
            header : '<fmt:message key="settings.editSchool.emailTableTypeHeader"/>',
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
            header : '<fmt:message key="settings.editSchool.emailTableAddressHeader"/>',
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
            tooltip: '<fmt:message key="settings.editSchool.emailTableAddTooltip"/>',
            onclick: function (event) {
              addEmailTableRow(event.tableObject);
            }
          }, {
            width: 30,
            left: 396,
            dataType: 'button',
            paramName: 'removeButton',
            hidden: true,
            imgsrc: GLOBAL_contextPath + '/gfx/list-remove.png',
            tooltip: '<fmt:message key="settings.editSchool.emailTableRemoveTooltip"/>',
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

        <c:forEach var="email" items="${school.contactInfo.emails}">
          emailTable.addRow([
            ${email.id},
            ${email.defaultAddress},
            ${email.contactType.id},
            '${fn:replace(email.address, "'", "\\'")}',
            '',
            '']);
        </c:forEach>

        if (emailTable.getRowCount() == 0) {
          addEmailTableRow();
          emailTable.setCellValue(0, 1, true);
        }

        var phoneTable = new IxTable($('phoneTable'), {
          id : "phoneTable",
          columns : [ {
            dataType : 'hidden',
            left : 0,
            width : 0,
            paramName : 'phoneId'
          }, {
            left : 0,
            width : 30,
            dataType: 'radiobutton',
            editable: true,
            paramName: 'defaultNumber',
            tooltip: '<fmt:message key="settings.editSchool.phoneTableDefaultTooltip"/>',
          }, {
            header : '<fmt:message key="settings.editSchool.phoneTableTypeHeader"/>',
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
            header : '<fmt:message key="settings.editSchool.phoneTableNumberHeader"/>',
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
            tooltip: '<fmt:message key="settings.editSchool.phoneTableAddTooltip"/>',
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
            tooltip: '<fmt:message key="settings.editSchool.phoneTableRemoveTooltip"/>',
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

        <c:forEach var="phone" items="${school.contactInfo.phoneNumbers}">
          phoneTable.addRow([
            ${phone.id},
            ${phone.defaultNumber},
            ${phone.contactType.id},
            '${fn:replace(phone.number, "'", "\\'")}',
            '',
            '']);
        </c:forEach>

        if (phoneTable.getRowCount() == 0) {
          addPhoneTableRow(phoneTable);
          phoneTable.setCellValue(0, 1, true);
        }

        var variablesTable = new IxTable($('variablesTable'), {
          id : "variablesTable",
          columns : [{
            left: 8,
            width: 30,
            dataType: 'button',
            imgsrc: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
            tooltip: '<fmt:message key="settings.editSchool.variablesTableEditTooltip"/>',
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
        var value;
        <c:forEach var="variableKey" items="${variableKeys}">
          value = '${fn:replace(school.variablesAsStringMap[variableKey.variableKey], "'", "\\'")}';
          var rowNumber = variablesTable.addRow([
            '',
            '${fn:replace(variableKey.variableKey, "'", "\\'")}',
            '${fn:replace(variableKey.variableName, "'", "\\'")}',
            value
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
  
    <h1 class="genericPageHeader"><fmt:message key="settings.editSchool.pageTitle" /></h1>
  
    <div class="genericFormContainer"> 
      <div class="tabLabelsContainer" id="tabs">
        <a class="tabLabel" href="#basic">
          <fmt:message key="settings.editSchool.tabLabelBasic"/>
        </a>
      </div>
      
      <form action="editschool.json" method="post" ix:jsonform="true" ix:useglasspane="true">

        <input type="hidden" name="schoolId" value="${school.id}"></input>
  
        <div id="basic" class="tabContent">
          <div class="genericFormSection">
            <jsp:include page="/templates/generic/fragments/formtitle.jsp">
              <jsp:param name="titleLocale" value="settings.editSchool.codeTitle"/>
              <jsp:param name="helpLocale" value="settings.editSchool.codeHelp"/>
            </jsp:include> 
            <input type="text" name="code" class="required" size="20" value="${fn:escapeXml(school.code)}"/>
          </div>
  
          <div class="genericFormSection">
            <jsp:include page="/templates/generic/fragments/formtitle.jsp">
              <jsp:param name="titleLocale" value="settings.editSchool.nameTitle"/>
              <jsp:param name="helpLocale" value="settings.editSchool.nameHelp"/>
            </jsp:include> 

            <input type="text" name="name" class="required" size="40" value="${fn:escapeXml(school.name)}"/>
          </div>

          <div class="genericFormSection">                
            <jsp:include page="/templates/generic/fragments/formtitle.jsp">
              <jsp:param name="titleLocale" value="settings.editSchool.addressesTitle"/>
              <jsp:param name="helpLocale" value="settings.editSchool.addressesHelp"/>
            </jsp:include>
            <div id="addressTable"></div>
          </div>

          <div class="genericFormSection">               
            <jsp:include page="/templates/generic/fragments/formtitle.jsp">
              <jsp:param name="titleLocale" value="settings.editSchool.emailTableEmailsTitle"/>
              <jsp:param name="helpLocale" value="settings.editSchool.emailTableEmailsHelp"/>
            </jsp:include>
            <div id="emailTable"></div>
          </div>

          <div class="genericFormSection">                
            <jsp:include page="/templates/generic/fragments/formtitle.jsp">
              <jsp:param name="titleLocale" value="settings.editSchool.phoneNumbersTitle"/>
              <jsp:param name="helpLocale" value="settings.editSchool.phoneNumbersHelp"/>
            </jsp:include>
            <div id="phoneTable"></div>
          </div>

          <div class="genericFormSection">  
            <jsp:include page="/templates/generic/fragments/formtitle.jsp">
              <jsp:param name="titleLocale" value="settings.editSchool.variablesTitle"/>
              <jsp:param name="helpLocale" value="settings.editSchool.variablesHelp"/>
            </jsp:include> 
            <div id="variablesTable"></div>
          </div>

        </div>
        <div class="genericFormSubmitSectionOffTab">
          <input type="submit" class="formvalid" value="<fmt:message key="settings.editSchool.saveButton"/>">
        </div>
      </form>
    </div>
    
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>