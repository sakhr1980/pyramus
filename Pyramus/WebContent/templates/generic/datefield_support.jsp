<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:choose>
  <c:when test="${datefieldSupportIncluded != true}">
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/date-picker/js/datepicker.js"></script>
    <link href="${pageContext.request.contextPath}/scripts/date-picker/css/datepicker.css" rel="stylesheet" type="text/css" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/ixdatefield/ixdatefield.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ixdatefield.css"/>
    <script type="text/javascript">
      document.observe("dom:loaded", function(event) {
        replaceDateFields();
      });
    </script>
    <c:set scope="request" var="datefieldSupportIncluded" value="true"/>
  </c:when>
</c:choose>