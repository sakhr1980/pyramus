package fi.pyramus.json.projects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ProjectDAO;
import fi.pyramus.domainmodel.courses.Course;
import fi.pyramus.domainmodel.courses.CourseParticipationType;
import fi.pyramus.domainmodel.courses.CourseStudent;
import fi.pyramus.domainmodel.modules.Module;
import fi.pyramus.domainmodel.projects.StudentProject;
import fi.pyramus.domainmodel.projects.StudentProjectCourse;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.json.JSONRequestController;

/**
 * The controller returning a list of all modules in a project.
 */
public class GetStudentProjectCoursesJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    ProjectDAO projectDAO = DAOFactory.getInstance().getProjectDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();

    Long studentProjectId = jsonRequestContext.getLong("studentProject");
    StudentProject studentProject = projectDAO.getStudentProject(studentProjectId);

    List<Map<String, Object>> studentProjectCourses = new ArrayList<Map<String, Object>>();
    
    Student student = studentProject.getStudent();
    
    for (StudentProjectCourse studentProjectCourse : studentProject.getStudentProjectCourses()) {
      Course course = studentProjectCourse.getCourse();
      Module module = course.getModule();
      CourseStudent courseStudent = courseDAO.findCourseStudentByCourseAndStudent(course, student);
      
      StringBuilder nameBuilder = new StringBuilder(course.getName());
      if (!StringUtils.isBlank(course.getNameExtension())) {
        nameBuilder
          .append(" (")
          .append(course.getNameExtension())
          .append(')');
      }
      
      Map<String, Object> courseInfo = new HashMap<String, Object>();
      courseInfo.put("id", studentProjectCourse.getId());
      courseInfo.put("courseId", course.getId());
      courseInfo.put("moduleId", module.getId());
      courseInfo.put("name", nameBuilder.toString());
      if (course.getBeginDate() != null)
        courseInfo.put("beginDate", course.getBeginDate().getTime());
      if (course.getEndDate() != null)
        courseInfo.put("endDate", course.getEndDate().getTime());
     
      if (courseStudent != null) {
        CourseParticipationType courseParticipationType = courseStudent.getParticipationType();
        courseInfo.put("participationType", courseParticipationType.getName());
      } 
      
      studentProjectCourses.add(courseInfo);
    }

    jsonRequestContext.addResponseParameter("studentProjectCourses", studentProjectCourses);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR  };
  }

}
