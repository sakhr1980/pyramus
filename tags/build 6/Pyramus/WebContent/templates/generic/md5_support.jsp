<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:choose>
  <c:when test="${md5SupportIncluded != true}">
    <c:set scope="request" var="md5SupportIncluded" value="true"/>
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/jshash-2.2/md5-min.js"></script>
  </c:when>
</c:choose>