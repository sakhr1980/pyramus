package fi.pyramus.views.courses;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import fi.pyramus.domainmodel.base.EducationType;
import fi.pyramus.domainmodel.base.EducationalTimeUnit;
import fi.pyramus.domainmodel.base.Subject;
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.domainmodel.modules.Module;
import fi.pyramus.domainmodel.modules.ModuleComponent;
import fi.pyramus.UserRole;
import fi.pyramus.util.StringAttributeComparator;
import fi.pyramus.views.PyramusViewController;

/**
 * The controller responsible of the Create Course view of the application.
 * 
 * @see fi.pyramus.json.users.CreateGradingScaleJSONRequestController
 */
public class CreateCourseViewController implements PyramusViewController, Breadcrumbable {

  /**
   * Processes the page request by including the corresponding JSP page to the response.
   * 
   * @param pageRequestContext Page request context
   */
  public void process(PageRequestContext pageRequestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();

    // The module acting as the base of the new course
    
    Module module = moduleDAO.getModule(NumberUtils.createLong(pageRequestContext.getRequest().getParameter("module")));
    pageRequestContext.getRequest().setAttribute("module",  module);
    
    // Create a hashmap of the education types and education subtypes selected in the module so that the
    // course to be created has them selected as well
    
    List<EducationType> educationTypes = baseDAO.listEducationTypes();
    Collections.sort(educationTypes, new StringAttributeComparator("getName"));
    pageRequestContext.getRequest().setAttribute("educationTypes", educationTypes);
    Map<String, Boolean> enabledEducationTypes = new HashMap<String, Boolean>();
    for (CourseEducationType courseEducationType : module.getCourseEducationTypes()) {
      for (CourseEducationSubtype moduleEducationSubtype : courseEducationType.getCourseEducationSubtypes()) {
        enabledEducationTypes.put(courseEducationType.getEducationType().getId() + "."
            + moduleEducationSubtype.getEducationSubtype().getId(), Boolean.TRUE);
      }
    }
    pageRequestContext.getRequest().setAttribute("enabledEducationTypes", enabledEducationTypes);
    
    // Module tags for the new course
    
    StringBuilder tagsBuilder = new StringBuilder();
    Iterator<Tag> tagIterator = module.getTags().iterator();
    while (tagIterator.hasNext()) {
      Tag tag = tagIterator.next();
      tagsBuilder.append(tag.getText());
      if (tagIterator.hasNext())
        tagsBuilder.append(' ');
    }
    pageRequestContext.getRequest().setAttribute("tags", tagsBuilder.toString());
    
    List<ModuleComponent> moduleComponents = moduleDAO.listModuleComponents(module);
    
    // Subjects
    Map<Long, List<Subject>> subjectsByEducationType = new HashMap<Long, List<Subject>>();
    List<Subject> subjectsByNoEducationType = baseDAO.listSubjectsByEducationType(null);
    Collections.sort(subjectsByNoEducationType, new StringAttributeComparator("getName"));
    for (EducationType educationType : educationTypes) {
      List<Subject> subjectsOfType = baseDAO.listSubjectsByEducationType(educationType);
      if ((subjectsOfType != null) && (subjectsOfType.size() > 0)) {
        Collections.sort(subjectsOfType, new StringAttributeComparator("getName"));
        subjectsByEducationType.put(educationType.getId(), subjectsOfType);
      }
    }

    // Various lists of base entities from module, course, and resource DAOs
    
    List<EducationalTimeUnit> educationalTimeUnits = baseDAO.listEducationalTimeUnits();
    Collections.sort(educationalTimeUnits, new StringAttributeComparator("getName"));

    pageRequestContext.getRequest().setAttribute("states", courseDAO.listCourseStates());
    pageRequestContext.getRequest().setAttribute("roles", courseDAO.listCourseUserRoles());
    pageRequestContext.getRequest().setAttribute("subjectsByNoEducationType", subjectsByNoEducationType);
    pageRequestContext.getRequest().setAttribute("subjectsByEducationType", subjectsByEducationType);
    pageRequestContext.getRequest().setAttribute("courseParticipationTypes", courseDAO.listCourseParticipationTypes());
    pageRequestContext.getRequest().setAttribute("courseEnrolmentTypes",courseDAO.listCourseEnrolmentTypes());
    pageRequestContext.getRequest().setAttribute("courseLengthTimeUnits", educationalTimeUnits);
    pageRequestContext.getRequest().setAttribute("moduleComponents", moduleComponents);
    pageRequestContext.getRequest().setAttribute("courseDescriptions", courseDAO.listCourseDescriptionsByCourseBase(module));
    pageRequestContext.getRequest().setAttribute("courseDescriptionCategories", courseDAO.listCourseDescriptionCategories());
    
    pageRequestContext.setIncludeJSP("/templates/courses/createcourse.jsp");
  }

  /**
   * Returns the roles allowed to access this page. Creating courses is available for users with
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
    return Messages.getInstance().getText(locale, "courses.createCourse.pageTitle");
  }

}
