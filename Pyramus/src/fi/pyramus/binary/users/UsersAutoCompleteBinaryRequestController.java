package fi.pyramus.binary.users;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryParser.QueryParser;

import fi.pyramus.BinaryRequestContext;
import fi.pyramus.PyramusRuntimeException;
import fi.pyramus.UserRole;
import fi.pyramus.binary.BinaryRequestController;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.UserDAO;
import fi.pyramus.domainmodel.users.User;

public class UsersAutoCompleteBinaryRequestController implements BinaryRequestController {

  public void process(BinaryRequestContext binaryRequestContext) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();

    String text = binaryRequestContext.getString("text");

    StringBuilder resultBuilder = new StringBuilder();
    resultBuilder.append("<ul>");

    if (!StringUtils.isBlank(text)) {
      text = QueryParser.escape(StringUtils.trim(text)) + '*';
      
      List<User> users = userDAO.searchUsersBasic(100, 0, text).getResults();
      
      for (User user : users) {
        addUser(resultBuilder, user);
      }
    }
    
    resultBuilder.append("</ul>");

    try {
      binaryRequestContext.setResponseContent(resultBuilder.toString().getBytes("UTF-8"), "text/html;charset=UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new PyramusRuntimeException(e);
    }
  }
  
  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.USER, UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }
  
  private void addUser(StringBuilder resultBuilder, User user) {
    resultBuilder
      .append("<li>")
      .append("<span>")
      .append(StringEscapeUtils.escapeHtml(user.getFullName()))
      .append("</span>")
      .append("<input type=\"hidden\" name=\"id\" value=\"")
      .append(user.getId())
      .append("\"/>")
      .append("</li>");
  }
}
