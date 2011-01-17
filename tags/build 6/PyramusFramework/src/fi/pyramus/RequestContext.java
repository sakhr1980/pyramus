package fi.pyramus;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 * An abstract request context associated with each request made to the application.
 * <p/>
 * Wraps in both the HTTP servlet request and response objects, as well as packs some logic for
 * error handling and tracking the currently logged in user.
 */
public abstract class RequestContext {

  /**
   * The constructor specifying the HTTP servlet request and response objects associated with the
   * context.
   * 
   * @param servletRequest The HTTP servlet request
   * @param servletResponse The HTTP servlet response
   * @throws FileUploadException 
   */
  @SuppressWarnings("unchecked")
  protected RequestContext(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
    this.servletRequest = servletRequest;
    this.servletResponse = servletResponse;
    
    if (ServletFileUpload.isMultipartContent(servletRequest)) {
      multipartFields = new HashMap<String, FileItem>();
      
      ServletFileUpload servletFileUpload = new ServletFileUpload(new DiskFileItemFactory());
      servletFileUpload.setHeaderEncoding("UTF-8");

      try {
        List<FileItem> items = servletFileUpload.parseRequest(servletRequest);
        Iterator<FileItem> iter = items.iterator();
        while (iter.hasNext()) {
          FileItem item = iter.next();
          String fieldName = item.getFieldName();
          multipartFields.put(fieldName, item);
        }
      }
      catch (FileUploadException fue) {
        throw new PyramusRuntimeException(fue);
      }
    }

  }

  /**
   * Returns the HTTP servlet request associated with this context.
   * 
   * @return The HTTP servlet request associated with this context
   */
  public HttpServletRequest getRequest() {
    return servletRequest;
  }

  /**
   * Returns the HTTP servlet response associated with this context.
   * 
   * @return The HTTP servlet response associated with this context
   */
  public HttpServletResponse getResponse() {
    return servletResponse;
  }

  /**
   * Returns whether this context is associated with a logged in user.
   * 
   * @return <code>true</code> if a logged in user exists in this context, otherwise
   * <code>false/code>
   */
  public boolean isLoggedIn() {
    return getLoggedUserId() != null;
  }
  
  /**
   * Returns the identifier of the logged in user. If the user hasn't logged in, returns
   * <code>null</code>.
   * 
   * @return The identifier of the logged in user, or <code>null</code> if the user hasn't logged in
   */
  public Long getLoggedUserId() {
    HttpSession session = getRequest().getSession(false);
    return session == null ? null : (Long) session.getAttribute("loggedUserId");
  }
  
  public UserRole getLoggedUserRole() {
    HttpSession session = getRequest().getSession(false);
    return session == null ? null : (UserRole) session.getAttribute("loggedUserRole");
  }

  /**
   * Returns the redirect URL stored to this context. If one hasn't been specified, returns
   * <code>null</code>.
   * 
   * @return The redirect URL stored to this context, or <code>null</code> if one hasn't been set
   */
  public String getRedirectURL() {
    return redirectURL;
  }

  /**
   * Sets the redirect URL of this context.
   * 
   * @param redirectURL The redirect URL of this context
   */
  public void setRedirectURL(String redirectURL) {
    this.redirectURL = redirectURL;
  }
  
  public String getReferer(boolean returnAnchor) {
    StringBuilder refererBuilder = new StringBuilder(servletRequest.getHeader("Referer"));
    
    if (returnAnchor) {
      String refererAnchor = getRefererAnchor();
      if (!StringUtils.isBlank(refererAnchor)) {
        refererBuilder.append('#').append(refererAnchor);
      }
    }
    
    return refererBuilder.toString();
  }
  
  public String getRefererAnchor() {
    return getString("__refererAnchor"); 
  }

  /**
   * Sets an error status to this context.
   * 
   * @param errorLevel The level of the error
   * @param code The error code
   * @param message The error message
   * @param exception The exception itself
   */
  public void setErrorStatus(ErrorLevel errorLevel, StatusCode code, String message, Throwable exception) {
    this.errorLevel = errorLevel;
    this.errorMessage = message;
    this.statusCode = code;
    this.exception = exception;
  }

  /**
   * Returns the error message of this context.
   * 
   * @return The error message of this context
   */
  protected String getErrorMessage() {
    return errorMessage;
  }

  /**
   * Returns the error level of this context.
   * 
   * @return The error level of this context
   */
  protected ErrorLevel getErrorLevel() {
    return errorLevel;
  }

  /**
   * Returns the status code of this context.
   * 
   * @return The status code of this context
   */
  public StatusCode getStatusCode() {
    return statusCode;
  }
  
  public Boolean getBoolean(String paramName) {
    return Boolean.valueOf(getString(paramName));
  }
  
  public Date getDate(String paramName) {
    Long value = getLong(paramName);
    return value == null ? null : new Date(value);
  }
  
  public Date getDateByFormat(String paramName, String format) {
  	Date date = null;
  	SimpleDateFormat sdf = new SimpleDateFormat(format);
  	
  	String dateStr = getString(paramName, true);
  	
  	if (dateStr != null) {
	  	try {
		    date = sdf.parse(dateStr);
	    } catch (ParseException e) {
		    e.printStackTrace();
	    }
  	}
    
    return date;
  }

  public Double getDouble(String paramName) {
    String value = getString(paramName);
    // Since '.' is a universal decimal separator in Java, let's be lenient with ',' as well
    if (value != null && value.indexOf(',') >= 0) {
      value = value.replace(',', '.');
    }
    // TODO Invalid value as a runtime exception?
    return NumberUtils.isNumber(value) ? NumberUtils.toDouble(value) : null;  
  }

  public Integer getInteger(String paramName) {
    String value = getString(paramName);
    return NumberUtils.isNumber(value) ? NumberUtils.toInt(value) : null;  
  }

  public Long getLong(String paramName) {
    String value = getString(paramName);
    return NumberUtils.isNumber(value) ? NumberUtils.toLong(value) : null;  
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public Enum getEnum(String paramName, Class enumClass) {
    String name = getString(paramName);
    if (name != null)
      return Enum.valueOf(enumClass, name);
    else
      return null;
  }
  
  public String getString(String paramName) {
    return getString(paramName, true);
  }

  public String getString(String paramName, boolean emptyAsNull) {
    String value = null;
    if (multipartFields != null) {
      FileItem fileItem = multipartFields.get(paramName);
      if (fileItem != null) {
        try {
          value = fileItem.getString("UTF-8");
        }
        catch (UnsupportedEncodingException uee) {
          throw new PyramusRuntimeException(uee);
        }
      }
    }
    else {
      value = getRequest().getParameter(paramName);
    }
    return emptyAsNull && StringUtils.isBlank(value) ? null : value; 
  }
  
  public FileItem getFile(String paramName) {
    FileItem fileItem = null;
    
    if (multipartFields != null) {
      fileItem = multipartFields.get(paramName);
    }
    
    return fileItem;
  }

  /**
   * Returns the exception of this context.
   * 
   * @return The exception of this context
   */
  public Throwable getException() {
    return exception;
  }

  /**
   * Once a page request has been processed, the context is responsible of writing it back to the
   * user via this method. This response is written BEFORE committing (or rolling back) the database
   * session associated with request.
   * 
   * @throws Exception If writing the response fails for any reason
   */
  public abstract void writePreCommitResponse() throws Exception;

  /**
   * Once a page request has been processed, the context is responsible of writing it back to the
   * user via this method. This response is written AFTER committing (or rolling back) the database
   * session associated with the request.
   * 
   * @throws Exception If writing the response fails for any reason
   */
  public abstract void writePostCommitResponse() throws Exception;

  /**
   * The HTTP servlet request of this context.
   */
  private HttpServletRequest servletRequest;

  /**
   * The HTTP servlet response of this context.
   */
  private HttpServletResponse servletResponse;

  
  private Map<String, FileItem> multipartFields;
  
  /**
   * The redirect URL of this context.
   */
  private String redirectURL;

  /**
   * The status code of this context. Defaults to {@link StatusCode#OK}. Used in error handling.
   */
  private StatusCode statusCode = StatusCode.OK;

  /**
   * The error level of this context. Used in error handling.
   */
  private ErrorLevel errorLevel;

  /**
   * The error message of this context. Used in error handling.
   */
  private String errorMessage;

  /**
   * The exception of this context. Used in error handling.
   */
  private Throwable exception;
}
