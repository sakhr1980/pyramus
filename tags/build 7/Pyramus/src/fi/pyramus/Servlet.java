package fi.pyramus;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.UserTransaction;

import fi.pyramus.I18N.Messages;
import fi.pyramus.binary.BinaryRequestController;
import fi.pyramus.breadcrumbs.BreadcrumbHandler;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.json.JSONRequestController;
import fi.pyramus.views.PyramusViewController;

/**
 * The main servlet of the Pyramus application, responsible of handling all application requests and
 * delegating them to their corresponding page and JSON controllers.
 * 
 * @see fi.pyramus.RequestContext
 * @see fi.pyramus.RequestController
 * @see fi.pyramus.RequestControllerMapper
 * @see fi.pyramus.json.JSONRequestController
 * @see fi.pyramus.views.PyramusViewController
 */
public class Servlet extends HttpServlet {
  
  @Resource 
  private UserTransaction userTransaction;

  @PersistenceContext(name="persistence/pyramusEntityManager", unitName="pyramusManager")
  private EntityManager entityManager;
  
  /**
   * The serial version UID of this class.
   */
  private static final long serialVersionUID = 4074155508397800515L;

  /**
   * Processes all application requests, delegating them to their corresponding page and JSON
   * controllers.
   */
  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
      java.io.IOException {
    
    RequestContext requestContext = null;
    
    // Start a transaction for the request

    try {
      userTransaction.begin();
      entityManager.setFlushMode(FlushModeType.COMMIT);
    }
    catch (Exception e) {
      throw new ServletException(e);
    } 
    
    try {

      // Request controller

      RequestController requestController = RequestControllerMapper.getRequestController(request);
      if (requestController == null) {
        throw new PyramusRuntimeException(ErrorLevel.CRITICAL, StatusCode.UNDEFINED,
            "No registered request controller for " + request.getRequestURI());
      }

      // Request context
      
      requestContext = requestContextFactory.createRequestContext(requestController, request, response);

      // First check that the user is authorized to access the controller.

      boolean authorized = authorized(requestContext, requestController);
      if (authorized && requestController instanceof CustomAuthorizationSupport) {
        CustomAuthorizationSupport customAuthorizationSupport = (CustomAuthorizationSupport) requestController;
        authorized = customAuthorizationSupport.authorize(requestContext);
      }

      // If the user is not allowed to access the request controller, either send a
      // HTTP 403 (Forbidden) or when not logged in and trying to access a page,
      // redirect them to the login page 

      if (!authorized) {
        if (!requestContext.isLoggedIn()) { 
          if (requestController instanceof PyramusViewController) {
            HttpSession session = requestContext.getRequest().getSession(true);
            session.setAttribute("loginFollowupURL", getURL(requestContext.getRequest()));
            response.sendRedirect(request.getContextPath() + "/users/login.page");
          } else {
            requestContext.setErrorStatus(ErrorLevel.INFORMATION, StatusCode.NOT_LOGGED_IN, Messages.getInstance().getText(request.getLocale(), "generic.errors.notLoggedIn"));
          }
        } else {
          requestContext.setErrorStatus(ErrorLevel.INFORMATION, StatusCode.PERMISSION_DENIED, Messages.getInstance().getText(request.getLocale(), "generic.errors.permissionDenied"));
        }
      }
      else {

        // Process the request
  
        try {
          if (requestController instanceof PyramusViewController) {
  
            // Handle breadcrumbs for get requests
  
            BreadcrumbHandler breadcrumbHandler = getBreadcrumbHandler(request);
            if (request.getParameter("resetbreadcrumb") != null) {
              breadcrumbHandler.clear();
            }
            if (requestController instanceof Breadcrumbable && "GET".equals(request.getMethod())) {
              if (!breadcrumbHandler.contains(request) && request.getHeader("Referer") == null) {
                breadcrumbHandler.clear();
              }
              Breadcrumbable breadcrumbable = (Breadcrumbable) requestController;
              breadcrumbHandler.process(request, breadcrumbable);
            }
  
            ((PyramusViewController) requestController).process((PageRequestContext) requestContext);
          }
          else if (requestController instanceof JSONRequestController) {
            ((JSONRequestController) requestController).process((JSONRequestContext) requestContext);
          }
          else if (requestController instanceof BinaryRequestController) {
            ((BinaryRequestController) requestController).process((BinaryRequestContext) requestContext);
          }
        }
        catch (PyramusRuntimeException pre) {
  
          // Pyramus runtime exceptions are added to the request context so that they can
          // be displayed in an error dialog
  
          requestContext.setErrorStatus(pre.getLevel(), pre.getStatusCode(), pre.getMessage(), pre);
        }
        catch (Exception e) {
  
          // All other exceptions are considered to be fatal and unexpected, so the request
          // transaction is rolled back, the stack trace of the exception is printed out, and
          // an error view is shown
  
          e.printStackTrace();
          requestContext.setErrorStatus(ErrorLevel.CRITICAL, StatusCode.UNDEFINED, e.getMessage(), e);
        }
      }
    }
    finally {
      try {

        // Pre-commit response

        requestContext.writePreCommitResponse();

        // Request complete, so commit or rollback based on the status of the request

        if (requestContext.getStatusCode() == StatusCode.OK) {
          userTransaction.commit();
        }
        else {
          userTransaction.rollback();
        }

        // Post-commit response

        requestContext.writePostCommitResponse();
      }
      catch (Exception e) {
        throw new ServletException(e);
      }
    }
  }

  /**
   * Returns whether the user, possibly contained in the request context, has access to the
   * request controller.
   * <p/>
   * If the request controller allows everyone access, no login is required.
   * <p/>
   * If everyone access is denied, the roles allowed by the controller are compared to the
   * role of the logged in user.
   * 
   * @param requestContext The request context containing the logged in user, if any
   * @param requestController The request controller the user is trying to access
   * 
   * @return <code>true</code> if the user is authorized to access the request controller,
   * otherwise <code>false</code>
   */
  private boolean authorized(RequestContext requestContext, RequestController requestController) {
    UserRole[] roles = requestController.getAllowedRoles();
    if (contains(roles, UserRole.EVERYONE)) {
      return true;
    }
    return !requestContext.isLoggedIn() ? false : contains(roles, requestContext.getLoggedUserRole());
  }

  /**
   * Returns whether the given role is included in the given role array.
   * 
   * @param roles The roles
   * @param role The role
   * 
   * @return <code>true</code> if the roles array contains the given role, otherwise
   * <code>false</code>
   */
  private boolean contains(UserRole[] roles, UserRole role) {
    for (int i = 0; i < roles.length; i++) {
      if (roles[i] == role) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the complete URL of the given servlet request, i.e. possible query parameters included.
   * 
   * @param httpServletRequest The servlet request
   * 
   * @return The complete URL of the given servlet request
   */
  private static String getURL(HttpServletRequest httpServletRequest) {
    String reqUrl = httpServletRequest.getRequestURL().toString();
    String queryString = httpServletRequest.getQueryString();
    if (queryString != null) {
      reqUrl += "?" + queryString;
    }
    return reqUrl;
  }

  private BreadcrumbHandler getBreadcrumbHandler(HttpServletRequest request) {
    HttpSession session = request.getSession(true);
    BreadcrumbHandler breadcrumbHandler = (BreadcrumbHandler) session.getAttribute("breadcrumbHandler");
    if (breadcrumbHandler == null) {
      breadcrumbHandler = new BreadcrumbHandler();
      session.setAttribute("breadcrumbHandler", breadcrumbHandler);
    }
    return breadcrumbHandler;
  }
  
  private RequestContextFactory requestContextFactory = new RequestContextFactory();
}
