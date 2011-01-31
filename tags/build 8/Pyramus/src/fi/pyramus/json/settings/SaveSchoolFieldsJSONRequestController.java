package fi.pyramus.json.settings;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.base.SchoolField;
import fi.pyramus.json.JSONRequestController;

public class SaveSchoolFieldsJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();

    int rowCount = NumberUtils.createInteger(jsonRequestContext.getRequest().getParameter("schoolFieldsTable.rowCount")).intValue();
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "schoolFieldsTable." + i;
      Long schoolFieldId = jsonRequestContext.getLong(colPrefix + ".id");
      String name = jsonRequestContext.getString(colPrefix + ".name");
      boolean modified = new Long(1).equals(jsonRequestContext.getLong(colPrefix + ".modified"));
      if (schoolFieldId == -1) {
        baseDAO.createSchoolField(name); 
      }
      else if (modified) {
        SchoolField schoolField = baseDAO.findSchoolFieldById(schoolFieldId);
        baseDAO.updateSchoolField(schoolField, name);
      }
    }
    jsonRequestContext.setRedirectURL(jsonRequestContext.getReferer(true));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
