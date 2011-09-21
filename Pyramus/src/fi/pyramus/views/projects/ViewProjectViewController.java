package fi.pyramus.views.projects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.PageRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.I18N.Messages;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.GradingDAO;
import fi.pyramus.dao.ProjectDAO;
import fi.pyramus.dao.UserDAO;
import fi.pyramus.domainmodel.base.EducationalTimeUnit;
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.domainmodel.courses.CourseStudent;
import fi.pyramus.domainmodel.grading.CourseAssessment;
import fi.pyramus.domainmodel.grading.ProjectAssessment;
import fi.pyramus.domainmodel.grading.TransferCredit;
import fi.pyramus.domainmodel.projects.Project;
import fi.pyramus.domainmodel.projects.StudentProject;
import fi.pyramus.domainmodel.projects.StudentProjectModule;
import fi.pyramus.domainmodel.users.Role;
import fi.pyramus.persistence.usertypes.CourseOptionality;
import fi.pyramus.util.StringAttributeComparator;
import fi.pyramus.views.PyramusViewController;

/**
 * The controller responsible of the Edit Project view of the application.
 */
public class ViewProjectViewController implements PyramusViewController, Breadcrumbable {
  
  /**
   * Processes the page request by including the corresponding JSP page to the response.
   * 
   * @param pageRequestContext Page request context
   */
  public void process(PageRequestContext pageRequestContext) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();
    ProjectDAO projectDAO = DAOFactory.getInstance().getProjectDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();

    Long projectId = NumberUtils.createLong(pageRequestContext.getRequest().getParameter("project"));
    Project project = projectDAO.findProjectById(projectId);

    StringBuilder tagsBuilder = new StringBuilder();
    Iterator<Tag> tagIterator = project.getTags().iterator();
    while (tagIterator.hasNext()) {
      Tag tag = tagIterator.next();
      tagsBuilder.append(tag.getText());
      if (tagIterator.hasNext())
        tagsBuilder.append(' ');
    }
    
    List<EducationalTimeUnit> educationalTimeUnits = baseDAO.listEducationalTimeUnits();
    Collections.sort(educationalTimeUnits, new StringAttributeComparator("getName"));

    List<StudentProject> studentProjectsByProject = projectDAO.listStudentProjectsByProject(project);
    List<StudentProjectBean> studentProjectBeans = new ArrayList<StudentProjectBean>();
    Collections.sort(studentProjectsByProject, new Comparator<StudentProject>() {
      @Override
      public int compare(StudentProject o1, StudentProject o2) {
        int v = o1.getStudent().getLastName().compareToIgnoreCase(o2.getStudent().getLastName());
        
        return v != 0 ? v : o1.getStudent().getFirstName().compareToIgnoreCase(o2.getStudent().getFirstName());
      }
    });
    
    for (StudentProject sp : studentProjectsByProject) {
      studentProjectBeans.add(beanify(sp, gradingDAO, courseDAO));
    }
    
    pageRequestContext.getRequest().setAttribute("tags", tagsBuilder.toString());
    pageRequestContext.getRequest().setAttribute("project", project);
    pageRequestContext.getRequest().setAttribute("studentProjects", studentProjectBeans);
    pageRequestContext.getRequest().setAttribute("optionalStudiesLengthTimeUnits", educationalTimeUnits);
    pageRequestContext.getRequest().setAttribute("users", userDAO.listUsers());

    pageRequestContext.setIncludeJSP("/templates/projects/viewproject.jsp");
  }

  /**
   * Returns the roles allowed to access this page. Editing projects is available for users with
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
    return Messages.getInstance().getText(locale, "projects.viewProject.breadcrumb");
  }

  private StudentProjectBean beanify(StudentProject studentProject, GradingDAO gradingDAO, CourseDAO courseDAO) {
    int mandatoryModuleCount = 0;
    int optionalModuleCount = 0;
    int passedMandatoryModuleCount = 0;
    int passedOptionalModuleCount = 0;

    /**
     * Go through project modules to
     *  a) count mandatory/optional modules
     *  b) count mandatory/optional modules that have passing grade on them
     *  c) create beans to be passed to jsp
     */
    
    for (StudentProjectModule studentProjectModule : studentProject.getStudentProjectModules()) {
      boolean hasPassingGrade = false;
      List<CourseStudent> courseStudentList = courseDAO.listCourseStudentsByModuleAndStudent(studentProjectModule.getModule(), studentProject.getStudent());

      List<TransferCredit> transferCreditsByStudent = gradingDAO.listTransferCreditsByStudent(studentProject.getStudent());
      
      // Find out if there is a course that has passing grade for the module
      if (courseStudentList != null) {
        for (CourseStudent cs : courseStudentList) {
          CourseAssessment ca = gradingDAO.findCourseAssessmentByCourseStudent(cs); 
          if (ca != null && ca.getGrade() != null && ca.getGrade().getPassingGrade()) {
            hasPassingGrade = true; 
            break;
          }
        }
      }
      
      if (!hasPassingGrade) {
        if ((studentProjectModule.getModule().getCourseNumber() != null) && (studentProjectModule.getModule().getCourseNumber() != -1) && (studentProjectModule.getModule().getSubject() != null)) {
          for (TransferCredit tc : transferCreditsByStudent) {
            if ((tc.getCourseNumber() != null) && (tc.getCourseNumber() != -1) && (tc.getSubject() != null)) {
              if (tc.getCourseNumber().equals(studentProjectModule.getModule().getCourseNumber()) && tc.getSubject().equals(studentProjectModule.getModule().getSubject())) {
                if (tc.getGrade() != null && tc.getGrade().getPassingGrade()) {
                  hasPassingGrade = true;
                  break;
                }
              }
            }
          }
        }
      }
      
      if (studentProjectModule.getOptionality() == CourseOptionality.MANDATORY) {
        mandatoryModuleCount++;
        if (hasPassingGrade)
          passedMandatoryModuleCount++;
      } else if (studentProjectModule.getOptionality() == CourseOptionality.OPTIONAL) {
        optionalModuleCount++;
        if (hasPassingGrade)
          passedOptionalModuleCount++;
      }
    }
    
    List<ProjectAssessment> projectAssessments = gradingDAO.listProjectAssessmentByProject(studentProject);
    
    Collections.sort(projectAssessments, new Comparator<ProjectAssessment>() {
      @Override
      public int compare(ProjectAssessment o1, ProjectAssessment o2) {
        return o2.getDate().compareTo(o1.getDate());
      }
    });
    
    return new StudentProjectBean(studentProject, mandatoryModuleCount, optionalModuleCount, passedMandatoryModuleCount, passedOptionalModuleCount, projectAssessments);
  }
  
  public class StudentProjectBean {
    private final StudentProject studentProject;
    private final int passedOptionalModuleCount;
    private final int mandatoryModuleCount;
    private final int optionalModuleCount;
    private final int passedMandatoryModuleCount;
    private final List<ProjectAssessment> assessments;

    public StudentProjectBean(StudentProject studentProject, int mandatoryModuleCount, int optionalModuleCount,
        int passedMandatoryModuleCount, int passedOptionalModuleCount, List<ProjectAssessment> assessments) {
      this.studentProject = studentProject;
      this.mandatoryModuleCount = mandatoryModuleCount;
      this.optionalModuleCount = optionalModuleCount;
      this.passedOptionalModuleCount = passedOptionalModuleCount;
      this.passedMandatoryModuleCount = passedMandatoryModuleCount;
      this.assessments = assessments;
    }

    public StudentProject getStudentProject() {
      return studentProject;
    }

    public int getPassedOptionalModuleCount() {
      return passedOptionalModuleCount;
    }

    public int getMandatoryModuleCount() {
      return mandatoryModuleCount;
    }

    public int getOptionalModuleCount() {
      return optionalModuleCount;
    }

    public int getPassedMandatoryModuleCount() {
      return passedMandatoryModuleCount;
    }

    public List<ProjectAssessment> getAssessments() {
      return assessments;
    }
  }
}
