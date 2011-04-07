package fi.pyramus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

/**
 * Request context for all binary requests in the application.
 */
public class BinaryRequestContext extends RequestContext {

  public BinaryRequestContext(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
    super(servletRequest, servletResponse);
  }

  public void setResponseContent(byte[] content, String contentType) {
    this.content = content;
    this.contentType = contentType;
  }
  
  public void setContentUrl(String contentUrl) {
    this.contentUrl = contentUrl;
  }
  
  public String getContentUrl() {
    return contentUrl;
  }
  
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  @Override
  public void writePreCommitResponse() throws Exception {
  }

  /**
   * Writes the response to the binary request.
   * 
   * @throws Exception If writing the response fails for any reason
   */
  @Override
  public void writePostCommitResponse() throws Exception {
    if (getStatusCode() == StatusCode.OK) {
      if (fileName != null) {
        getResponse().setHeader("Content-Disposition", "attachment; filename=" + fileName);
      }
      
      if (contentType != null && content != null) {
        getResponse().setContentType(contentType);
        getResponse().getOutputStream().write(content);
      } else if (!StringUtils.isBlank(contentUrl)) {
        handleContentUrl(); 
      }
    } else {
      // TODO: Better error handing
      
      PrintWriter responseWriter = getResponse().getWriter();
      try {
        responseWriter.write(getErrorMessage());
      }
      finally {
        responseWriter.close();
      }
      
      switch (getStatusCode()) {
        case PERMISSION_DENIED:
        case NOT_LOGGED_IN:          
          getResponse().setStatus(403);
        break;
        default:
          getResponse().setStatus(500);
        break;
      }
    }
  }
  
  private void handleContentUrl() throws IOException {
    String contentUrl = getContentUrl();
    boolean isRelative = !contentUrl.startsWith("/");
    
    // TODO: This is not quite correct because if this request is secure it doesn't mean that include request would be 
    String protocol = "http";
    if (getRequest().isSecure()) {
      protocol = "https";
    }
    
    // TODO: Do we need support for external urls?
    String serverName = getRequest().getServerName();
    
    StringBuilder urlBuilder = new StringBuilder();
    if (isRelative) {
      urlBuilder
        .append(getRequest().getContextPath())
        .append(getRequest().getPathInfo())
        .append('/')
        .append(contentUrl);
    } else {
      urlBuilder.append(contentUrl);
    }
      
    URL includeURL = new URL(protocol, serverName, getRequest().getLocalPort(), urlBuilder.toString());
    
    HttpURLConnection connection = (HttpURLConnection) includeURL.openConnection(); 
    
    connection.setDoInput(true);
    connection.setDoOutput(true);
    connection.connect();
    
    getResponse().setContentType(connection.getContentType());
    OutputStream outputStream = getResponse().getOutputStream();
    
    InputStream inputStream = connection.getInputStream();
    byte[] buf = new byte[1024];
    int l = 0;
    while ((l = inputStream.read(buf)) > 0) {
      outputStream.write(buf, 0, l);
    }
    
    outputStream.flush();
    outputStream.close();
  }

  private String fileName;
  private byte[] content;
  private String contentType;
  private String contentUrl;
}
