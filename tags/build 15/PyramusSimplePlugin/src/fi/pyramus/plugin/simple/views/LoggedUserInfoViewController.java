package fi.pyramus.plugin.simple.views;

import fi.pyramus.PageRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.UserDAO;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.views.PyramusViewController;

public class LoggedUserInfoViewController implements PyramusViewController {

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
}
