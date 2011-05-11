package fi.pyramus.views.courses;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.PageRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.I18N.Messages;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.GradingDAO;
import fi.pyramus.domainmodel.courses.Course;
import fi.pyramus.domainmodel.courses.CourseStudent;
import fi.pyramus.domainmodel.grading.CourseAssessment;
import fi.pyramus.domainmodel.grading.GradingScale;
import fi.pyramus.domainmodel.users.Role;
import fi.pyramus.views.PyramusViewController;

/**
 * The controller responsible of the Edit Course view of the application.
 * 
 * @see fi.pyramus.json.users.CreateGradingScaleJSONRequestController
 */
public class ManageCourseAssessmentsViewController implements PyramusViewController, Breadcrumbable {

  /**
   * Processes the page request by including the corresponding JSP page to the response.
   * 
   * @param pageRequestContext Page request context
   */
  public void process(PageRequestContext pageRequestContext) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();

    Course course = courseDAO.getCourse(NumberUtils.createLong(pageRequestContext.getRequest().getParameter("course")));
    List<GradingScale> gradingScales = gradingDAO.listGradingScales();
    
    List<CourseStudent> courseStudents = courseDAO.listCourseStudentsByCourse(course);
    Collections.sort(courseStudents, new Comparator<CourseStudent>() {
      @Override
      public int compare(CourseStudent o1, CourseStudent o2) {
        int cmp = o1.getStudent().getLastName().compareToIgnoreCase(o2.getStudent().getLastName());
        if (cmp == 0)
          cmp = o1.getStudent().getFirstName().compareToIgnoreCase(o2.getStudent().getFirstName());
        return cmp;
      }
    });
    
    Map<Long, CourseAssessment> courseAssessments = new HashMap<Long, CourseAssessment>();

    Iterator<CourseStudent> students = courseStudents.iterator();
    while (students.hasNext()) {
      CourseStudent courseStudent = students.next();
      
      CourseAssessment courseAssessment = gradingDAO.findCourseAssessmentByCourseStudent(courseStudent);
      if (courseAssessment != null)
        courseAssessments.put(courseStudent.getId(), courseAssessment);
    }
    
    pageRequestContext.getRequest().setAttribute("course", course);
    pageRequestContext.getRequest().setAttribute("courseStudents", courseStudents);
    pageRequestContext.getRequest().setAttribute("courseParticipationTypes", courseDAO.listCourseParticipationTypes());
    pageRequestContext.getRequest().setAttribute("assessments", courseAssessments);
    pageRequestContext.getRequest().setAttribute("gradingScales", gradingScales);
    
    pageRequestContext.setIncludeJSP("/templates/courses/managecourseassessments.jsp");
  }

  /**
   * Returns the roles allowed to access this page. Editing courses is available for users with
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
    return Messages.getInstance().getText(locale, "courses.manageCourseAssessments.breadcrumb");
  }

}
