## Setting up plugin development environment ##

  * TODO: Eclipse
  * TODO: Required projects from Google Code

## Creating new plugin project ##

_New_ > _Project ..._ > _Java EE_ > _Utility Project_ and fill up the Project name. In normal situation all other options should be fine.

**Depencies**

Project > Properties > Java Build Path > Projects > Add
  * PyramusFramework
  * PyramusPluginCore
  * PyramusPersistence

**Package structure**

It is encouraged to encapsulate all your plugin classes and templates into their own package e.g. within _fi.pyramus.plugin.simple_. besides that package we need to create folder called _services_ inside _WEB-INF_ folder.

**Plugin descriptor**

Plugin descriptor is a class that acts as an entry point to plugin and describes the plugin itself. Plugin descriptor class has to implement _PluginDescriptor_ interface and return plugin's name in getName -method. All other methods are optional and should return null if they are not used.

Besides _getName_ descriptor class contains five methods that all return <String, Class> maps:

  * public Map<String, Class<?>> getBinaryRequestControllers()
  * public Map<String, Class<?>> getJSONRequestControllers()
  * public Map<String, Class<?>> getPageRequestControllers()
  * public Map<String, Class<?>> getPageHookControllers()
  * public Map<String, Class<?>> getAuthenticationProviders()

These methods describe what plugin contains by returning appropriate controllers for page controllers, binary controllers, json controllers, hook controllers and authorization providers.

To register plugin descriptor we need to create a text file called _fi.pyramus.plugin.PluginDescriptor_ within the _WEB-INF/services_ folder. File's first line should contain full name of the Descriptor class. <br />_Note that you can add comments by using # character._

**_Example:_**
<br /><br />
_fi.pyramus.plugin.PluginDescriptor:_
```
fi.pyramus.plugin.simple.SimplePluginDescriptor # plugin descriptor class for Simple plugin
```

## Deployment ##

Plugin deployment is handled by Maven. Install the plugin into a
Maven repository, and reference it from Pyramus
(Settings->Plugins->Repositories).

## Extending database ##

Instead of extending the database, use the variable mechanism. Most important entities have variable keys and variables that can be used for extending the entities.

## Plugin Localization ##

Plugins are localized using Freemarker localization: if a Freemarker
template is named `template.ftl`, its localized variants are called
`template_en_US.ftl` and `template_fi_FI.ftl`, for example. No other
setup is required for localization.

## View controllers ##

Plugin developers may add new views into Pyramus. Pyramus considers all HTML pages as views so basically view can be anything from normal webpage to embedded content.

Pyramus has two basic view controller types: Normal view controllers and Form view controllers. Both of them work pretty much the same way with an exception that Form view controllers handle GET and POST requests separately. Form controllers are quite rare because in most of the cases even form pages are made with normal view controllers and "posting" the form is handled with json requests.

Normal view controllers have to implement _PyramusViewController_ interface and FormControllers are done by extending view class from _PyramusFormViewController_ which is derivative of PyramusViewController interface.

_PyramusViewController_ interface requires class to implement two methods: _process_ and _getAllowedRoles_.

Both methods do exactly what they suggest. _getAllowedRoles_ returns a list of roles in witch users has to be in order to use view and _process_ processes the request. Within process method there are two ways to embed content into target page: by setting either included Freemarker template or included url. <br />_Note that even thought it is possible define included JSP page they will not work because JSPs are designed to work within core only_.

included Freemarker template or URL is defined by calling either setIncludeFtl or setIncludeUrl method. <br />_Note both of them cannot be defined at the same request._

**_Example:_**

_fi.pyramus.plugin.simple.views.LoggedUserInfoViewController:_
```
  @Override
  public void process(PageRequestContext pageRequestContext) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    User loggedUser = userDAO.getUser(pageRequestContext.getLoggedUserId());
  
    pageRequestContext.getRequest().setAttribute("loggedUser", loggedUser);
    pageRequestContext.setIncludeFtl("/plugin/simple/ftl/loggeduserinfo.ftl");
  }

  @Override
  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }
```
_/fi/pyramus/plugin/simple/ftl/loggeduserinfo.ftl:_
```
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
      <div>${user.firstName?html}</div>
      <br/>
      <div style="font-weight: bold"><@fmt.message key="users.loggedUserInfo.lastNameLabel" /></div>
      <div>${user.lastName?html}</div>
      
      <@include_page path="/templates/generic/footer.jsp"/> 
    </body>
  </html>
```

## Extending view controllers ##

To extend existing views, define a new view controller with the
same address than an existing view (e.g. `projects/editstudentproject`).
The new controller masks the old one, but the old one is saved with
a `.masked` suffix, e.g. `projects/editstudentproject.page.masked`.
Call the masked controller from your new one:

```
@Override
  public void process(PageRequestContext pageRequestContext) {
    ((PageController) RequestControllerMapper.getRequestController("projects/editstudentproject.page.masked")).process(pageRequestContext);
    setJsDataVariable(pageRequestContext, "hasFoobar", "true");
  }
```

## Adding JSON controllers ##

  * TODO: Adding JSON controllers

## Adding binary controllers ##

  * TODO: Adding binary controllers

## Adding static resources (images, JavaScript etc) ##

Plugins can provide external resources by placing them in
`/fi/pyramus/plugin/static/`_plugin name_. The resources
can be accessed as `/plugin/static/`_plugin name_`/`resource.

## Extending views ##

Plugin may also extend existing views via "extension hooks". All extension hooks are listed in [ExtensionHooks](ExtensionHooks.md).

Before extending views you should read [[ElementCheatsheet](ElementCheatsheet.md)] to learn correct html syntax for Pyramus.

All hook providers must be introduced in plugin descriptor.

_**Example:**_

_MySimplePlugin.java:_
```
public class MySimplePlugin implements PluginDescriptor {
  ...
  
  public Map<String, Class<?>> getPageHooks() {
    Map<String, Class<?>> viewControllers = new HashMap<String, Class<?>>();
    
    viewControllers.put("courses.editCourse.tabLabels", EditCourseSimpleTabLabelHook.class);
    viewControllers.put("courses.editCourse.tabs", EditCourseSimpleTabHook.class);

    return viewControllers;
  }
}
```

_fi.pyramus.plugin.simple.hooks.EditCourseSimpleTabLabelHook:_
```
public class EditCourseSimpleTabLabelHook implements PageHookController {
  
  @Override
  public void execute(PageHookContext pageHookContext) {
    pageHookContext.setIncludeFtl("/plugin/simple/ftl/editcoursesimpletablabelhook.ftl");
  }
}
```

_fi.pyramus.plugin.simple.hooks.EditCourseSimpleTabHook:_
```
public class EditCourseSimpleTabHook implements PageHookController {
  
  @Override
  public void execute(PageHookContext pageHookContext) {
    pageHookContext.setIncludeFtl("/plugin/simple/ftl/editcoursesimpletabhook.ftl");
  }
}
```
_fi.pyramus.plugin.simple.hooks.ftl.editcoursesimpletablabelhook.ftl:_
```
<#assign fmt=JspTaglibs["http://java.sun.com/jsp/jstl/fmt"]>

<a class="tabLabel" href="#simple"><fmt:message key="courses.editCourse.simpleTabTitle" /></a>
```
_fi.pyramus.plugin.simple.hooks.ftl.editcoursesimpletabhook.ftl:_
```
<#assign fmt=JspTaglibs["http://java.sun.com/jsp/jstl/fmt"]>

<div id="simple" class="tabContent">
  <div>Simple tab content...</div>
</div>
```

## Authrorization providers ##

Pyramus authorization providers are divided into two separate types: Internal and External. Internal authorization providers represent providers that handle the authorization within Pyramus (e.g. LDAP) and External providers handle their authorization in external webpage (e.g. OpenId)

All authrorization providers must be introduced in plugin descriptor.

_**Example:**_
```
public class MySimplePlugin implements PluginDescriptor {
  ...
  
  public Map<String, Class<?>> getAuthenticationProviders() {
    Map<String, Class<?>> authenticationProviders = new HashMap<String, Class<?>>();
    
    authenticationProviders.put("simple", SimpleAuthorizationStrategy.class);
    
    return authenticationProviders;
  }
}
```

### Implementing _internal_ authorization providers ###

Internal authorization providers have to implement _fi.pyramus.plugin.auth.InternalAuthorizationProvider_ interface.

The heart of any internal authorization provider is _getUser_ method which simply returns fi.pyramus.domainmodel.users.User object by given username and password.


_**Example:**_

If you'd have your users authorization data stored in _SimpleAuth_ entity which contains id, username, password and user fields then the _getUser_ method would look something like this:
```
public class SimpleAuthProvider implements InternalAuthorizationProvider {
  public User getUser(String username, String password) {
    SimpleAuthDAO simpleAuthDAO = new SimpleAuthDAO();
    
    SimpleAuth simpleAuth = simpleAuthDAO.findSimpleAuthByUserNameAndPassword(username, password);
    return simpleAuth != null ? simpleAuth.getUser() : null;
  }
  ...
}    
```

In addition to that depending on whatever Pyramus should be able to update user credentials plugin should return true or false from canUpdateCredentials method.

If plugin declares that it can update credentials, plugin developer should also implement updateCredentials method otherwise that method should be left blank.

### Implementing **external** authorization providers ###

Internal authorization providers have to implement  _fi.pyramus.plugin.auth.ExternalAuthorizationProvider_ interface. Interface it self is quite simple because it contains only two methods: performDiscovery and processResponse.

_performDiscovery_ method's function is to prepare authorization request and redirect user to appropriate login page.

In normal situation external repository should be instructed to redirect browser back to users/externallogin.page beacuse it handles the response and calls for authorization request's handleResponse method but it is also possible for plugin developer to implement it's own response handler.

_**Example:**_

```
public void performDiscovery(RequestContext requestContext) {
  String redirectUrl = getExternalLoginUrl(requestContext);
  requestContext.setRedirectURL(redirectUrl);
}
```

ProcessResponse method handles the response from external repository and returns either _fi.pyramus.domainmodel.users.User_ object or null depending on the result of the logging in.

_**Example:**_
```
public User (RequestContext requestContext) throws AuthorizationException {
  HttpSession session = requestContext.getRequest().getSession();
  UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
  String externalId = getExtenalId(requestContext);	
  return externalId != null ? userDAO.getUser(externalId, getName()) : null;
}
```
_Note that you need to run following SQL script in order to use examples in this section:_

```
create table SimpleAuth (id bigint not null, username varchar(255) unique not null, password varchar(255) not null, primary key (id));
```