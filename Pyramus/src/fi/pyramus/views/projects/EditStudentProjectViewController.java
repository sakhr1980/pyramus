package fi.pyramus.views.projects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import fi.pyramus.PageRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.I18N.Messages;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ProjectDAO;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.dao.UserDAO;
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.domainmodel.courses.CourseStudent;
import fi.pyramus.domainmodel.projects.StudentProject;
import fi.pyramus.domainmodel.projects.StudentProjectModule;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.users.Role;
import fi.pyramus.views.PyramusViewController;

/**
 * The controller responsible of the Edit Student Project view of the application.
 */
public class EditStudentProjectViewController implements PyramusViewController, Breadcrumbable {
  
  /**
   * Processes the page request by including the corresponding JSP page to the response.
   * 
   * @param pageRequestContext Page request context
   */
  public void process(PageRequestContext pageRequestContext) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    ProjectDAO projectDAO = DAOFactory.getInstance().getProjectDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    
    Long studentProjectId = pageRequestContext.getLong("studentproject");

    StudentProject studentProject = projectDAO.findStudentProjectById(studentProjectId);
    List<CourseStudent> courseStudents = courseDAO.listCourseStudentsByStudent(studentProject.getStudent());
    
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
    
    List<Student> students = studentDAO.listStudentsByAbstractStudent(studentProject.getStudent().getAbstractStudent());

    pageRequestContext.getRequest().setAttribute("studentProjectModules", studentProjectModules);
    pageRequestContext.getRequest().setAttribute("courseStudents", courseStudents);
    pageRequestContext.getRequest().setAttribute("tags", tagsBuilder.toString());
    pageRequestContext.getRequest().setAttribute("studentProject", studentProject);
    pageRequestContext.getRequest().setAttribute("students", students);
    pageRequestContext.getRequest().setAttribute("optionalStudiesLengthTimeUnits", baseDAO.listEducationalTimeUnits());
    pageRequestContext.getRequest().setAttribute("academicTerms", baseDAO.listAcademicTerms());
    pageRequestContext.getRequest().setAttribute("users", userDAO.listUsers());

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
    return Messages.getInstance().getText(locale, "projects.editStudentProject.pageTitle");
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
