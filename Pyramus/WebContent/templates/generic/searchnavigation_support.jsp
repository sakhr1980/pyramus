<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
  <c:when test="${searchNavigationSupportIncluded != true}">
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/ixsearchnavigation/ixsearchnavigation.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/searchnavigation.css"/>
    <c:set scope="request" var="searchNavigationSupportIncluded" value="true"/>
  </c:when>
</c:choose>