package fi.pyramus.json.settings;

import java.util.Date;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.base.AcademicTerm;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

public class SaveAcademicTermsJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();

    int rowCount = NumberUtils.createInteger(jsonRequestContext.getRequest().getParameter("termsTable.rowCount")).intValue();
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "termsTable." + i;
      Long termId = NumberUtils.createLong(jsonRequestContext.getRequest().getParameter(colPrefix + ".termId"));
      String name = jsonRequestContext.getRequest().getParameter(colPrefix + ".name");
      Date startDate = new Date(NumberUtils.createLong(jsonRequestContext.getRequest().getParameter(colPrefix + ".startDate")));
      Date endDate = new Date(NumberUtils.createLong(jsonRequestContext.getRequest().getParameter(colPrefix + ".endDate")));
      boolean modified = NumberUtils.createInteger(jsonRequestContext.getRequest().getParameter(colPrefix + ".modified")) == 1;
   
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
