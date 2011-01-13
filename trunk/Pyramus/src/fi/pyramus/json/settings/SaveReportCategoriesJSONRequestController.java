package fi.pyramus.json.settings;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ReportDAO;
import fi.pyramus.domainmodel.reports.ReportCategory;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

public class SaveReportCategoriesJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    ReportDAO reportDAO = DAOFactory.getInstance().getReportDAO();

    int rowCount = NumberUtils.createInteger(jsonRequestContext.getRequest().getParameter("reportCategoriesTable.rowCount")).intValue();
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "reportCategoriesTable." + i;
      Long reportCategoryId = jsonRequestContext.getLong(colPrefix + ".reportCategoryId");
      String name = jsonRequestContext.getString(colPrefix + ".name");
      
      // TODO category index column support
      boolean modified = new Integer(1).equals(jsonRequestContext.getInteger(colPrefix + ".modified"));
      if (reportCategoryId == -1) {
        reportDAO.createReportCategory(name, null);
      }
      else if (modified) {
        ReportCategory reportCategory = reportDAO.findReportCategoryById(reportCategoryId);
        reportDAO.updateReportCategory(reportCategory, name, null);
      }
    }
    jsonRequestContext.setRedirectURL(jsonRequestContext.getReferer(true));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
