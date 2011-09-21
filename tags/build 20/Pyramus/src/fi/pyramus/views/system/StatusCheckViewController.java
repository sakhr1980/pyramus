package fi.pyramus.views.system;

import java.io.IOException;
import fi.pyramus.PageRequestContext;
import fi.pyramus.PyramusRuntimeException;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.base.Defaults;
import fi.pyramus.UserRole;
import fi.pyramus.views.PyramusViewController;

public class StatusCheckViewController implements PyramusViewController {

  public void process(PageRequestContext requestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    Defaults defaults = baseDAO.getDefaults();
    if (defaults != null) {
      try {
        requestContext.getResponse().getWriter().print("OK");
      }
      catch (IOException ioe) {
        throw new PyramusRuntimeException(ioe);
      }
    }
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.EVERYONE };
  }

}
