package fi.pyramus.views.modules;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.PageRequestContext;
import fi.pyramus.I18N.Messages;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ModuleDAO;
import fi.pyramus.domainmodel.base.CourseEducationSubtype;
import fi.pyramus.domainmodel.base.CourseEducationType;
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.domainmodel.modules.Module;
import fi.pyramus.UserRole;
import fi.pyramus.views.PyramusViewController;

/**
 * The controller responsible of the Edit Module view of the application.
 * 
 * @see fi.pyramus.json.users.EditModuleJSONRequestController
 */
public class EditModuleViewController implements PyramusViewController, Breadcrumbable {

  /**
   * Processes the page request by including the corresponding JSP page to the response.
   * <p/>
   * In order for the JSP page to build the module editing view, a list of all education types and
   * subjects are added as request attributes.
   * <p/>
   * In addition, a hashmap containing the education types and education subtypes checked in the
   * module is constructed. In that hashmap, the key is in the form of
   * <code>educationTypeId.educationSubtypeId</code> and the value is <code>Boolean.TRUE</code>. The
   * JSP page could probably figure out the checked education types and subtypes on its own but the
   * hashmap makes it a little bit easier and more streamlined.
   * 
   * @param pageRequestContext Page request context
   */
  public void process(PageRequestContext pageRequestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();

    Long moduleId = NumberUtils.createLong(pageRequestContext.getRequest().getParameter("module"));
    Module module = moduleDAO.getModule(moduleId);
    
    StringBuilder tagsBuilder = new StringBuilder();
    Iterator<Tag> tagIterator = module.getTags().iterator();
    while (tagIterator.hasNext()) {
      Tag tag = tagIterator.next();
      tagsBuilder.append(tag.getText());
      if (tagIterator.hasNext())
        tagsBuilder.append(' ');
    }
    
    pageRequestContext.getRequest().setAttribute("tags", tagsBuilder.toString());
    pageRequestContext.getRequest().setAttribute("module", module);
    pageRequestContext.getRequest().setAttribute("subjects", baseDAO.listSubjects());
    pageRequestContext.getRequest().setAttribute("moduleLengthTimeUnits", baseDAO.listEducationalTimeUnits());
    pageRequestContext.getRequest().setAttribute("moduleComponents", moduleDAO.listModuleComponents(module));

    pageRequestContext.getRequest().setAttribute("educationTypes", baseDAO.listEducationTypes());
    Map<String, Boolean> enabledEducationTypes = new HashMap<String, Boolean>();
    for (CourseEducationType courseEducationType : module.getCourseEducationTypes()) {
      for (CourseEducationSubtype moduleEducationSubtype : courseEducationType.getCourseEducationSubtypes()) {
        enabledEducationTypes.put(courseEducationType.getEducationType().getId() + "."
            + moduleEducationSubtype.getEducationSubtype().getId(), Boolean.TRUE);
      }
    }
    
    pageRequestContext.getRequest().setAttribute("enabledEducationTypes", enabledEducationTypes);
    pageRequestContext.getRequest().setAttribute("courseDescriptions", courseDAO.listCourseDescriptionsByCourseBase(module));
    pageRequestContext.getRequest().setAttribute("courseDescriptionCategories", courseDAO.listCourseDescriptionCategories());
    pageRequestContext.setIncludeJSP("/templates/modules/editmodule.jsp");
  }

  /**
   * Returns the roles allowed to access this page. Editing modules is available for users with
   * {@link Role#MANAGER} or {@link Role#ADMINISTRATOR} privileges.
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
    return Messages.getInstance().getText(locale, "modules.editModule.breadcrumb");
  }

}
