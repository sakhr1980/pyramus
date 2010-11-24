package fi.pyramus.views.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import fi.pyramus.PageRequestContext;
import fi.pyramus.I18N.Messages;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.base.EducationalTimeUnit;
import fi.pyramus.UserRole;
import fi.pyramus.views.PyramusViewController;

/**
 * The controller responsible of the Manage Time Units view of the application.
 * 
 * @see fi.pyramus.json.settings.SaveTimeUnitsJSONRequestController
 */
public class TimeUnitsViewController implements PyramusViewController, Breadcrumbable {

  /**
   * Processes the page request by including the corresponding JSP page to the response.
   * 
   * @param pageRequestContext Page request context
   */
  public void process(PageRequestContext pageRequestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    
    final EducationalTimeUnit baseTimeUnit = baseDAO.getDefaults().getBaseTimeUnit();
    List<EducationalTimeUnit> timeUnits = new ArrayList<EducationalTimeUnit>(baseDAO.listEducationalTimeUnits());
    

    Collections.sort(timeUnits, new Comparator<EducationalTimeUnit>() {
      @Override
      public int compare(EducationalTimeUnit o1, EducationalTimeUnit o2) {
        Double units1 = o1.getBaseUnits();
        Double units2 = o2.getBaseUnits();

        if (units1 == units2)
          return 0;
        
        if (o1.equals(baseTimeUnit))
          return -1;
        if (o2.equals(baseTimeUnit))
          return 1;
        
        return units1 > units2 ? 1 : -1;
      }
    });
    
    pageRequestContext.getRequest().setAttribute("timeUnits", timeUnits);
    pageRequestContext.getRequest().setAttribute("baseTimeUnit", baseTimeUnit);
    
    pageRequestContext.setIncludeJSP("/templates/settings/timeunits.jsp");
  }

  /**
   * Returns the roles allowed to access this page.
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
    return Messages.getInstance().getText(locale, "settings.timeUnits.pageTitle");
  }

}
