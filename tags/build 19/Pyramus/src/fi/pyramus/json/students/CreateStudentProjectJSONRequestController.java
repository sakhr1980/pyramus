package fi.pyramus.json.students;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ProjectDAO;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.dao.UserDAO;
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.domainmodel.projects.Project;
import fi.pyramus.domainmodel.projects.ProjectModule;
import fi.pyramus.domainmodel.projects.StudentProject;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.json.JSONRequestController;
import fi.pyramus.persistence.usertypes.CourseOptionality;

public class CreateStudentProjectJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    ProjectDAO projectDAO = DAOFactory.getInstance().getProjectDAO();

    User loggedUser = userDAO.getUser(jsonRequestContext.getLoggedUserId());

    Long studentId = jsonRequestContext.getLong("studentId");
    Long projectId = jsonRequestContext.getLong("projectId");
    CourseOptionality projectOptionality = (CourseOptionality) jsonRequestContext.getEnum("optionality", CourseOptionality.class);

    Student student = studentDAO.getStudent(studentId);
    Project project = projectDAO.findProjectById(projectId);

    StudentProject studentProject = projectDAO.createStudentProject(student, project.getName(), project.getDescription(), 
        project.getOptionalStudiesLength().getUnits(), project.getOptionalStudiesLength().getUnit(), projectOptionality, loggedUser, project);
    
    Set<Tag> tags = new HashSet<Tag>();
    for (Tag tag : project.getTags()) {
      tags.add(tag);
    }
    projectDAO.setStudentProjectTags(studentProject, tags);
    
    List<ProjectModule> projectModules = project.getProjectModules();
    for (ProjectModule projectModule : projectModules) {
      projectDAO.createStudentProjectModule(studentProject, projectModule.getModule(), null,
          CourseOptionality.getOptionality(projectModule.getOptionality().getValue()));
    }
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
