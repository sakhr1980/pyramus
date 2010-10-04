<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title><fmt:message key="users.createUser.pageTitle"></fmt:message></title>
    <jsp:include page="/templates/generic/head_generic.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonrequest_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonform_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/scriptaculous_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/table_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/tabs_support.jsp"></jsp:include>
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
            tooltip: '<fmt:message key="users.createUser.emailTableDefaultTooltip"/>',
          }, {
            header : '<fmt:message key="users.createUser.emailTableTypeHeader"/>',
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
            header : '<fmt:message key="users.createUser.emailTableAddressHeader"/>',
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
            tooltip: '<fmt:message key="users.createUser.emailTableAddTooltip"/>',
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
            tooltip: '<fmt:message key="users.createUser.emailTableRemoveTooltip"/>',
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
            tooltip: '<fmt:message key="users.createUser.addressTableDefaultTooltip"/>',
          }, {
            header : '<fmt:message key="users.createUser.addressTableTypeHeader"/>',
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
            header : '<fmt:message key="users.createUser.addressTableNameHeader"/>',
            left : 188,
            width : 150,
            dataType: 'text',
            editable: true,
            paramName: 'name'
          }, {
            header : '<fmt:message key="users.createUser.addressTableStreetHeader"/>',
            left : 344,
            width : 150,
            dataType: 'text',
            editable: true,
            paramName: 'street'
          }, {
            header : '<fmt:message key="users.createUser.addressTablePostalCodeHeader"/>',
            left : 502,
            width : 100,
            dataType: 'text',
            editable: true,
            paramName: 'postal'
          }, {
            header : '<fmt:message key="users.createUser.addressTableCityHeader"/>',
            left : 610,
            width : 150,
            dataType: 'text',
            editable: true,
            paramName: 'city'
          }, {
            header : '<fmt:message key="users.createUser.addressTableCountryHeader"/>',
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
            tooltip: '<fmt:message key="users.createUser.addressTableAddTooltip"/>',
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
            tooltip: '<fmt:message key="users.createUser.addressTableRemoveTooltip"/>',
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
            tooltip: '<fmt:message key="users.createUser.phoneTableDefaultTooltip"/>',
          }, {
            header : '<fmt:message key="users.createUser.phoneTableTypeHeader"/>',
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
            header : '<fmt:message key="users.createUser.phoneTableNumberHeader"/>',
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
            tooltip: '<fmt:message key="users.createUser.phoneTableAddTooltip"/>',
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
            tooltip: '<fmt:message key="users.createUser.phoneTableRemoveTooltip"/>',
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
      };
    </script>
    
  </head>
  <body onload="onLoad(event);">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
  
    <h1 class="genericPageHeader"><fmt:message key="users.createUser.pageTitle" /></h1>
  
    <div id="createUserCreateFormContainer"> 
      <div class="genericFormContainer"> 
        <div class="tabLabelsContainer" id="tabs">
          <a class="tabLabel" href="#createUser">
            <fmt:message key="users.createUser.tabLabelCreateUser"/>
          </a>
        </div>
        
        <form action="createuser.json" method="post" ix:jsonform="true">
          <div id="createUser" class="tabContent">

              <div class="genericFormSection">
                <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                  <jsp:param name="titleLocale" value="users.createUser.firstNameTitle"/>
                  <jsp:param name="helpLocale" value="users.createUser.firstNameHelp"/>
                </jsp:include>                  
                <input type="text" name="firstName" size="20" class="required"/>
              </div>
              <div class="genericFormSection">  
                <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                  <jsp:param name="titleLocale" value="users.createUser.lastNameTitle"/>
                  <jsp:param name="helpLocale" value="users.createUser.lastNameHelp"/>
                </jsp:include>                  
                <input type="text" name="lastName" size="30" class="required"/>
              </div>
              
              <div class="genericFormSection">
	              <jsp:include page="/templates/generic/fragments/formtitle.jsp">
	                <jsp:param name="titleLocale" value="users.createUser.tagsTitle"/>
	                <jsp:param name="helpLocale" value="users.createUser.tagsHelp"/>
	              </jsp:include>
	              <input type="text" id="tags" name="tags" size="40"/>
	              <div id="tags_choices" class="autocomplete_choises"></div>
	            </div>

              <div class="genericFormSection">  
                <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                  <jsp:param name="titleLocale" value="users.createUser.addressesTitle"/>
                  <jsp:param name="helpLocale" value="users.createUser.addressesHelp"/>
                </jsp:include>                                         
                <div id="addressTable"></div>
              </div>
  
              <div class="genericFormSection">  
                <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                  <jsp:param name="titleLocale" value="users.createUser.emailTableEmailsTitle"/>
                  <jsp:param name="helpLocale" value="users.createUser.emailTableEmailsHelp"/>
                </jsp:include>                                         
                <div id="emailTable"></div>
              </div>
  
              <div class="genericFormSection">  
                <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                  <jsp:param name="titleLocale" value="users.createUser.phoneNumbersTitle"/>
                  <jsp:param name="helpLocale" value="users.createUser.phoneNumbersHelp"/>
                </jsp:include>                                         
                <div id="phoneTable"></div>
              </div>

              <c:choose>
                <c:when test="${loggedUserRole == 'ADMINISTRATOR'}">
                  <div class="genericFormSection">  
                    <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                      <jsp:param name="titleLocale" value="users.createUser.roleTitle"/>
                      <jsp:param name="helpLocale" value="users.createUser.roleHelp"/>
                    </jsp:include>                  
                    <select name="role">
                      <option value="1"><fmt:message key="users.createUser.roleGuestTitle"/></option>
                      <option value="2"><fmt:message key="users.createUser.roleUserTitle"/></option>
                      <option value="3"><fmt:message key="users.createUser.roleManagerTitle"/></option>
                      <option value="4"><fmt:message key="users.createUser.roleAdministratorTitle"/></option>
                    </select>
                  </div>
                </c:when>
                <c:otherwise>
                  <input type="hidden" name="role" value="1"/>
                </c:otherwise>
              </c:choose>

          </div>
          <div class="genericFormSubmitSectionOffTab">
            <input type="submit" name="login" value="<fmt:message key="users.createUser.submitButton"/>" class="formvalid">
          </div>
        </form>
      </div>
    </div>
    
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>