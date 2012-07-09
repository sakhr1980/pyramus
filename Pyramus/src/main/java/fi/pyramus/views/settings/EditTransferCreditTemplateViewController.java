package fi.pyramus.views.settings;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import fi.internetix.smvc.controllers.PageRequestContext;
import fi.pyramus.I18N.Messages;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.base.EducationalTimeUnitDAO;
import fi.pyramus.dao.base.SchoolDAO;
import fi.pyramus.dao.base.SubjectDAO;
import fi.pyramus.dao.grading.TransferCreditTemplateDAO;
import fi.pyramus.domainmodel.base.EducationalTimeUnit;
import fi.pyramus.domainmodel.base.School;
import fi.pyramus.domainmodel.base.Subject;
import fi.pyramus.domainmodel.grading.TransferCreditTemplate;
import fi.pyramus.domainmodel.grading.TransferCreditTemplateCourse;
import fi.pyramus.framework.PyramusViewController;
import fi.pyramus.framework.UserRole;
import fi.pyramus.util.JSONArrayExtractor;
import fi.pyramus.util.StringAttributeComparator;

/**
 * The controller responsible of the Manage Transfer Credit Template view of the application.
 */
public class EditTransferCreditTemplateViewController extends PyramusViewController implements Breadcrumbable {

  /**
   * Processes the page request by including the corresponding JSP page to the response.
   * 
   * @param pageRequestContext Page request context
   */
  public void process(PageRequestContext pageRequestContext) {
    TransferCreditTemplateDAO transferCreditTemplateDAO = DAOFactory.getInstance().getTransferCreditTemplateDAO();
    SubjectDAO subjectDAO = DAOFactory.getInstance().getSubjectDAO();
    SchoolDAO schoolDAO = DAOFactory.getInstance().getSchoolDAO();
    EducationalTimeUnitDAO educationalTimeUnitDAO = DAOFactory.getInstance().getEducationalTimeUnitDAO();

    Long transferCreditTemplateId = pageRequestContext.getLong("transferCreditTemplate");

    TransferCreditTemplate transferCreditTemplate = transferCreditTemplateDAO.findById(transferCreditTemplateId);
    List<Subject> subjects = subjectDAO.listUnarchived();
    Collections.sort(subjects, new StringAttributeComparator("getName"));

    List<EducationalTimeUnit> timeUnits = educationalTimeUnitDAO.listUnarchived();
    Collections.sort(timeUnits, new StringAttributeComparator("getName"));

    List<School> schools = schoolDAO.listUnarchived();
    Collections.sort(schools, new StringAttributeComparator("getName"));
    
    String jsonTimeUnits = new JSONArrayExtractor("name", "id").extractString(timeUnits);
    JSONArray jaSubjects = new JSONArrayExtractor("name", "code", "id").extract(subjects);
    for (int i=0; i<jaSubjects.size(); i++) {
      JSONObject joSubject = jaSubjects.getJSONObject(i);
      if (subjects.get(i).getEducationType() != null) {
        joSubject.put("educationTypeName", subjects.get(i).getEducationType().getName());
      }
    }
    
    List<TransferCreditTemplateCourse> courses = transferCreditTemplate.getCourses();
    JSONArray jaCourses = new JSONArrayExtractor("courseName", "optionality", "courseNumber", "id").extract(courses);
    for (int i=0; i<jaCourses.size(); i++) {
      JSONObject course = jaCourses.getJSONObject(i);
      long courseLengthUnitId = courses.get(i).getCourseLength().getUnit().getId();
      double courseLengthUnits = courses.get(i).getCourseLength().getUnits();
      long subjectId = courses.get(i).getSubject().getId();
      course.put("courseLengthUnitId", courseLengthUnitId);
      course.put("courseLengthUnits", courseLengthUnits);
      course.put("subjectId", subjectId);
    }
    
    this.setJsDataVariable(pageRequestContext, "timeUnits", jsonTimeUnits);
    this.setJsDataVariable(pageRequestContext, "subjects", jaSubjects.toString());
    this.setJsDataVariable(pageRequestContext, "courses", jaCourses.toString());

    pageRequestContext.getRequest().setAttribute("transferCreditTemplate", transferCreditTemplate);
    
    pageRequestContext.setIncludeJSP("/templates/settings/edittransfercredittemplate.jsp");
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
    return Messages.getInstance().getText(locale, "settings.editTransferCreditTemplate.pageTitle");
  }

}
