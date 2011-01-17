package fi.pyramus.json.settings;

import java.util.Locale;

import fi.pyramus.ErrorLevel;
import fi.pyramus.JSONRequestContext;
import fi.pyramus.PyramusRuntimeException;
import fi.pyramus.StatusCode;
import fi.pyramus.UserRole;
import fi.pyramus.I18N.Messages;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ReportDAO;
import fi.pyramus.domainmodel.reports.ReportCategory;
import fi.pyramus.json.JSONRequestController;

public class DeleteReportCategoryJSONRequestController implements JSONRequestController {
  
  public void process(JSONRequestContext requestContext) {
    ReportDAO reportDAO = DAOFactory.getInstance().getReportDAO();
    Long reportCategoryId = requestContext.getLong("reportCategory");
    ReportCategory reportCategory = reportDAO.findReportCategoryById(reportCategoryId);
    if (reportDAO.isReportCategoryInUse(reportCategory)) {
      Locale locale = requestContext.getRequest().getLocale();
      String msg = Messages.getInstance().getText(locale, "settings.deleteReportCategory.categoryInUse");
      throw new PyramusRuntimeException(ErrorLevel.ERROR, StatusCode.VALIDATION_FAILURE, msg);
    }
    else {
      reportDAO.deleteReportCategory(reportCategory);
    }
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
