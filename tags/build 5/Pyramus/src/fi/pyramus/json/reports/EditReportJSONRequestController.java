package fi.pyramus.json.reports;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ReportDAO;
import fi.pyramus.domainmodel.reports.Report;
import fi.pyramus.domainmodel.reports.ReportCategory;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

/**
 * The controller responsible of editing a report. 
 * 
 * @see fi.pyramus.views.reports.EditReportViewController
 */
public class EditReportJSONRequestController implements JSONRequestController {

  /**
   * Processes the request to edit a report.
   * 
   * @param requestContext The JSON request context
   */
  public void process(JSONRequestContext requestContext) {
    ReportDAO reportDAO = DAOFactory.getInstance().getReportDAO();

    Long reportId = requestContext.getLong("reportId");
    Report report = reportDAO.findReportById(reportId);

    Long reportCategoryId = requestContext.getLong("category");
    ReportCategory category = reportCategoryId == null ? null : reportDAO.findReportCategoryById(reportCategoryId);
    
    String name = requestContext.getString("name");
    
    reportDAO.updateReport(report, name, category);

    requestContext.setRedirectURL(requestContext.getReferer(true));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
