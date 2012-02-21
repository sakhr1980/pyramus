package fi.pyramus.views.projects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;

import fi.internetix.smvc.controllers.PageRequestContext;
import fi.pyramus.I18N.Messages;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.base.AcademicTermDAO;
import fi.pyramus.dao.base.EducationalTimeUnitDAO;
import fi.pyramus.dao.courses.CourseStudentDAO;
import fi.pyramus.dao.grading.GradingScaleDAO;
import fi.pyramus.dao.grading.ProjectAssessmentDAO;
import fi.pyramus.dao.projects.StudentProjectDAO;
import fi.pyramus.dao.students.StudentDAO;
import fi.pyramus.dao.users.UserDAO;
import fi.pyramus.domainmodel.base.AcademicTerm;
import fi.pyramus.domainmodel.base.EducationalTimeUnit;
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.domainmodel.courses.CourseStudent;
import fi.pyramus.domainmodel.grading.GradingScale;
import fi.pyramus.domainmodel.grading.ProjectAssessment;
import fi.pyramus.domainmodel.projects.StudentProject;
import fi.pyramus.domainmodel.projects.StudentProjectModule;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.users.Role;
import fi.pyramus.framework.PyramusViewController;
import fi.pyramus.framework.UserRole;
import fi.pyramus.util.StringAttributeComparator;

/**
 * The controller responsible of the Edit Student Project view of the application.
 */
public class EditStudentProjectViewController extends PyramusViewController implements Breadcrumbable {
  
  /**
   * Processes the page request by including the corresponding JSP page to the response.
   * 
   * @param pageRequestContext Page request context
   */
  public void process(PageRequestContext pageRequestContext) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    CourseStudentDAO courseStudentDAO = DAOFactory.getInstance().getCourseStudentDAO();
    StudentProjectDAO studentProjectDAO = DAOFactory.getInstance().getStudentProjectDAO();
    GradingScaleDAO gradingScaleDAO = DAOFactory.getInstance().getGradingScaleDAO();
    ProjectAssessmentDAO projectAssessmentDAO = DAOFactory.getInstance().getProjectAssessmentDAO();
    AcademicTermDAO academicTermDAO = DAOFactory.getInstance().getAcademicTermDAO();
    EducationalTimeUnitDAO educationalTimeUnitDAO = DAOFactory.getInstance().getEducationalTimeUnitDAO();

    Long studentProjectId = pageRequestContext.getLong("studentproject");
    List<GradingScale> gradingScales = gradingScaleDAO.listUnarchived();

    StudentProject studentProject = studentProjectDAO.findById(studentProjectId);
    List<CourseStudent> courseStudents = courseStudentDAO.listByStudent(studentProject.getStudent());
    
    StringBuilder tagsBuilder = new StringBuilder();
    Iterator<Tag> tagIterator = studentProject.getTags().iterator();
    while (tagIterator.hasNext()) {
      Tag tag = tagIterator.next();
      tagsBuilder.append(tag.getText());
      if (tagIterator.hasNext())
        tagsBuilder.append(' ');
    }
    
    Set<Long> studentProjectCourseModuleIds = new HashSet<Long>(); 
    for (CourseStudent courseStudent : courseStudents) {
      studentProjectCourseModuleIds.add(courseStudent.getCourse().getModule().getId());
    }
    
    List<StudentProjectModuleBean> studentProjectModules = new ArrayList<StudentProjectModuleBean>();
    for (StudentProjectModule studentProjectModule : studentProject.getStudentProjectModules()) {
      StudentProjectModuleBean studentProjectModuleBean = new StudentProjectModuleBean(studentProjectModule, studentProjectCourseModuleIds.contains(studentProjectModule.getModule().getId()));
      studentProjectModules.add(studentProjectModuleBean);
    }

    List<Student> students = studentDAO.listByAbstractStudent(studentProject.getStudent().getAbstractStudent());

    List<EducationalTimeUnit> educationalTimeUnits = educationalTimeUnitDAO.listUnarchived();
    Collections.sort(educationalTimeUnits, new StringAttributeComparator("getName"));

    List<AcademicTerm> academicTerms = academicTermDAO.listUnarchived();
    Collections.sort(academicTerms, new Comparator<AcademicTerm>() {
      public int compare(AcademicTerm o1, AcademicTerm o2) {
        return o1.getStartDate() == null ? -1 : o2.getStartDate() == null ? 1 : o1.getStartDate().compareTo(o2.getStartDate());
      }
    });
    
    List<ProjectAssessment> assessments = projectAssessmentDAO.listByProject(studentProject);
    Collections.sort(assessments, new Comparator<ProjectAssessment>() {
      @Override
      public int compare(ProjectAssessment o1, ProjectAssessment o2) {
        return o2.getDate().compareTo(o1.getDate());
      }
    });

    Map<Long, String> verbalAssessments = new HashMap<Long, String>();

    for (ProjectAssessment pAss : assessments) {
      // Shortened descriptions
      String description = pAss.getVerbalAssessment();
      if (description != null) {
        description = StringEscapeUtils.unescapeHtml(description.replaceAll("\\<.*?>",""));
        description = description.replaceAll("\\n", "");
        
        verbalAssessments.put(pAss.getId(), description);
      }
    }
    
    pageRequestContext.getRequest().setAttribute("projectAssessments", assessments);
    pageRequestContext.getRequest().setAttribute("verbalAssessments", verbalAssessments);
    pageRequestContext.getRequest().setAttribute("studentProjectModules", studentProjectModules);
    pageRequestContext.getRequest().setAttribute("courseStudents", courseStudents);
    pageRequestContext.getRequest().setAttribute("tags", tagsBuilder.toString());
    pageRequestContext.getRequest().setAttribute("studentProject", studentProject);
    pageRequestContext.getRequest().setAttribute("students", students);
    pageRequestContext.getRequest().setAttribute("optionalStudiesLengthTimeUnits", educationalTimeUnits);
    pageRequestContext.getRequest().setAttribute("academicTerms", academicTerms);
    pageRequestContext.getRequest().setAttribute("users", userDAO.listAll());
    pageRequestContext.getRequest().setAttribute("gradingScales", gradingScales);

    pageRequestContext.setIncludeJSP("/templates/projects/editstudentproject.jsp");
  }

  /**
   * Returns the roles allowed to access this page. Editing student projects is available for users with
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
    return Messages.getInstance().getText(locale, "projects.editStudentProject.breadcrumb");
  }

  public class StudentProjectModuleBean {
    
    public StudentProjectModuleBean(StudentProjectModule studentProjectModule, Boolean hasCourseEquivalent) {
      this.studentProjectModule = studentProjectModule;
      this.hasCourseEquivalent = hasCourseEquivalent;
    }
    
    public Boolean getHasCourseEquivalent() {
      return hasCourseEquivalent;
    }
    
    public StudentProjectModule getStudentProjectModule() {
      return studentProjectModule;
    }
    
    private StudentProjectModule studentProjectModule;
    private Boolean hasCourseEquivalent;
  }
}
