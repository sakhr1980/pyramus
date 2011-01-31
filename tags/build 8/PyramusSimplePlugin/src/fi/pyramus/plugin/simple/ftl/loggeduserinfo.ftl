<#assign fmt=JspTaglibs["http://java.sun.com/jsp/jstl/fmt"]>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>	  
    <@include_page path="/templates/generic/head_generic.jsp"/> 
	  <title><@fmt.message key="users.loggedUserInfo.pageTitle"/></title>
  </head>
  <body onload="onLoad(event)">
    <@include_page path="/templates/generic/header.jsp"/> 
	  <h1 class="genericPageHeader"><@fmt.message key="users.loggedUserInfo.pageTitle" /></h1>
	
	  <div style="font-weight: bold"><@fmt.message key="users.loggedUserInfo.firstNameLabel" /></div>  
    <div>${loggedUser.firstName?html}</div>
    <br/>
    <div style="font-weight: bold"><@fmt.message key="users.loggedUserInfo.lastNameLabel" /></div>
    <div>${loggedUser.lastName?html}</div>
    
    <@include_page path="/templates/generic/footer.jsp"/> 
  </body>
</html>