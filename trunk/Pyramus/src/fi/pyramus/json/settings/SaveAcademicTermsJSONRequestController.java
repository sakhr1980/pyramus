package fi.pyramus.json.settings;

import java.util.Date;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.base.AcademicTerm;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

public class SaveAcademicTermsJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();

    int rowCount = jsonRequestContext.getInteger("termsTable.rowCount");
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "termsTable." + i;
      Long termId = jsonRequestContext.getLong(colPrefix + ".termId");
      String name = jsonRequestContext.getString(colPrefix + ".name");
      Date startDate =  jsonRequestContext.getDate(colPrefix + ".startDate");
      Date endDate = jsonRequestContext.getDate(colPrefix + ".endDate");
      boolean modified = jsonRequestContext.getInteger(colPrefix + ".modified") == 1; 
      if (termId == -1) {
        baseDAO.createAcademicTerm(name, startDate, endDate); 
      }
      else if (modified) {
        AcademicTerm term = baseDAO.getAcademicTerm(termId);
        baseDAO.updateAcademicTerm(term, name, startDate, endDate);
      }
    }
    jsonRequestContext.setRedirectURL(jsonRequestContext.getReferer(true));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
