package fi.pyramus.views.modules;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fi.pyramus.PageRequestContext;
import fi.pyramus.I18N.Messages;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.base.EducationSubtype;
import fi.pyramus.domainmodel.base.EducationType;
import fi.pyramus.domainmodel.base.Subject;
import fi.pyramus.UserRole;
import fi.pyramus.util.StringAttributeComparator;
import fi.pyramus.views.PyramusViewController;

public class SearchModulesViewController implements PyramusViewController, Breadcrumbable {

  public void process(PageRequestContext requestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    
    List<EducationType> educationTypes = baseDAO.listEducationTypes();
    Collections.sort(educationTypes, new StringAttributeComparator("getName"));

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
    
    requestContext.getRequest().setAttribute("educationTypes", educationTypes);
    requestContext.getRequest().setAttribute("educationSubtypes", educationSubtypesByEduType);
    requestContext.getRequest().setAttribute("subjectsByNoEducationType", subjectsByNoEducationType);
    requestContext.getRequest().setAttribute("subjectsByEducationType", subjectsByEducationType);

    requestContext.setIncludeJSP("/templates/modules/searchmodules.jsp");
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
    return Messages.getInstance().getText(locale, "modules.searchModules.pageTitle");
  }

}
