package fi.pyramus.json.settings;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.base.Subject;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

public class SaveSubjectsJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();

    int rowCount = NumberUtils.createInteger(jsonRequestContext.getRequest().getParameter("subjectsTable.rowCount")).intValue();
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "subjectsTable." + i;
      Long subjectId = NumberUtils.createLong(jsonRequestContext.getRequest().getParameter(colPrefix + ".subjectId"));
      String code = jsonRequestContext.getRequest().getParameter(colPrefix + ".code");
      String name = jsonRequestContext.getRequest().getParameter(colPrefix + ".name");
      boolean modified = NumberUtils.createInteger(jsonRequestContext.getRequest().getParameter(colPrefix + ".modified")) == 1;
   
      if (subjectId == -1) {
        baseDAO.createSubject(code, name); 
      }
      else if (modified) {
        Subject subject = baseDAO.getSubject(subjectId);
        baseDAO.updateSubject(subject, code, name);
      }
    }
    jsonRequestContext.setRedirectURL(jsonRequestContext.getReferer(true));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
