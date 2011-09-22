package fi.pyramus.views.courses;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fi.pyramus.PageRequestContext;
import fi.pyramus.I18N.Messages;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.base.EducationSubtype;
import fi.pyramus.domainmodel.base.EducationType;
import fi.pyramus.domainmodel.base.Subject;
import fi.pyramus.UserRole;
import fi.pyramus.util.StringAttributeComparator;
import fi.pyramus.views.PyramusViewController;

public class SearchCoursesViewController implements PyramusViewController, Breadcrumbable {

  public void process(PageRequestContext pageRequestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    List<EducationType> educationTypes = baseDAO.listEducationTypes();
    Collections.sort(educationTypes, new StringAttributeComparator("getName"));

    // Subjects
    Map<Long, List<Subject>> subjectsByEducationType = new HashMap<Long, List<Subject>>();
    List<Subject> subjectsByNoEducationType = baseDAO.listSubjectsByEducationType(null);
    Collections.sort(subjectsByNoEducationType, new StringAttributeComparator("getName"));
    
    Map<Long, List<EducationSubtype>> educationSubtypesByEduType = new HashMap<Long, List<EducationSubtype>>();
    
    for (EducationType educationType : educationTypes) {
      List<Subject> subjectsOfType = baseDAO.listSubjectsByEducationType(educationType);
      if ((subjectsOfType != null) && (subjectsOfType.size() > 0)) {
        Collections.sort(subjectsOfType, new StringAttributeComparator("getName"));
        subjectsByEducationType.put(educationType.getId(), subjectsOfType);
      }

      List<EducationSubtype> educationSubtypes = baseDAO.listEducationSubtypes(educationType);
      educationSubtypesByEduType.put(educationType.getId(), educationSubtypes);
    }

    pageRequestContext.getRequest().setAttribute("educationTypes", educationTypes);
    pageRequestContext.getRequest().setAttribute("educationSubtypes", educationSubtypesByEduType);
    pageRequestContext.getRequest().setAttribute("subjectsByNoEducationType", subjectsByNoEducationType);
    pageRequestContext.getRequest().setAttribute("subjectsByEducationType", subjectsByEducationType);
    pageRequestContext.getRequest().setAttribute("states", courseDAO.listCourseStates());
    pageRequestContext.setIncludeJSP("/templates/courses/searchcourses.jsp");
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.GUEST, UserRole.USER, UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

  /**
   * Returns the localized name of this page. Used e.g. for breadcrumb navigation.
   * 
   * @param locale The locale to be used for the name
   * 
   * @return The localized name of this page
   */
  public String getName(Locale locale) {
    return Messages.getInstance().getText(locale, "courses.searchCourses.pageTitle");
  }
  
}
