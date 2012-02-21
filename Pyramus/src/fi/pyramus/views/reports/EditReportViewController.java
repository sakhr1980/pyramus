package fi.pyramus.views.reports;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import fi.internetix.smvc.controllers.PageRequestContext;
import fi.pyramus.I18N.Messages;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.reports.ReportCategoryDAO;
import fi.pyramus.dao.reports.ReportDAO;
import fi.pyramus.domainmodel.reports.ReportCategory;
import fi.pyramus.framework.PyramusViewController;
import fi.pyramus.framework.UserRole;

/**
 * The controller responsible of the Edit Report view.
 */
public class EditReportViewController extends PyramusViewController implements Breadcrumbable {
  
  /**
   * Processes the page request by including the corresponding JSP page to the response.
   * 
   * @param pageRequestContext Page request context
   */
  public void process(PageRequestContext pageRequestContext) {
    ReportDAO reportDAO = DAOFactory.getInstance().getReportDAO();
    ReportCategoryDAO categoryDAO = DAOFactory.getInstance().getReportCategoryDAO();
    Long reportId = pageRequestContext.getLong("reportId");
    
    List<ReportCategory> categories = categoryDAO.listAll();
    
    Collections.sort(categories, new Comparator<ReportCategory>() {
      public int compare(ReportCategory o1, ReportCategory o2) {
        if (o1.getIndexColumn() == o2.getIndexColumn() || o1.getIndexColumn().equals(o2.getIndexColumn())) {
          return o1.getName() == null ? -1 : o2.getName() == null ? 1 : o1.getName().compareTo(o2.getName());
        }
        else {
          return o1.getIndexColumn() == null ? -1 : o2.getIndexColumn() == null ? 1 : o1.getIndexColumn().compareTo(o2.getIndexColumn());
        }
      }
    });
    
    pageRequestContext.getRequest().setAttribute("report", reportDAO.findById(reportId));
    pageRequestContext.getRequest().setAttribute("reportCategories", categories);
    
    pageRequestContext.setIncludeJSP("/templates/reports/editreport.jsp");
  }

  /**
   * Returns the roles allowed to access this page. Reports are available for users with
   * {@link Role#USER}, {@link Role#MANAGER} and {@link Role#ADMINISTRATOR} privileges.
   * 
   * @return The roles allowed to access this page
   */
  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

  /**
   * Returns the localized name of this page. Used e.g. for breadcrumb navigation.
   * 
   * @param locale The locale to be used for the name
   * 
   * @return The localized name of this page
   */
  public String getName(Locale locale) {
    return Messages.getInstance().getText(locale, "reports.editReport.pageTitle");
  }

}
