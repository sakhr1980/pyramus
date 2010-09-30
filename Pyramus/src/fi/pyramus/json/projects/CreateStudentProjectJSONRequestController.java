package fi.pyramus.json.projects;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.I18N.Messages;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ProjectDAO;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.dao.UserDAO;
import fi.pyramus.domainmodel.base.EducationalTimeUnit;
import fi.pyramus.domainmodel.projects.Project;
import fi.pyramus.domainmodel.projects.ProjectModule;
import fi.pyramus.domainmodel.projects.StudentProject;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.UserRole;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.json.JSONRequestController;
import fi.pyramus.persistence.usertypes.StudentProjectModuleOptionality;

public class CreateStudentProjectJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    ProjectDAO projectDAO = DAOFactory.getInstance().getProjectDAO();

    User loggedUser = userDAO.getUser(jsonRequestContext.getLoggedUserId());

    Long studentId = NumberUtils.createLong(jsonRequestContext.getRequest().getParameter("studentId"));
    Student student = studentDAO.getStudent(studentId);

    Long projectId = NumberUtils.createLong(jsonRequestContext.getRequest().getParameter("projectId"));
    Project project = projectId == -1 ? null : projectDAO.getProject(projectId);

    String name;
    String description;
    EducationalTimeUnit unit;
    Double units;

    if (project == null) {
      name = Messages.getInstance().getText(jsonRequestContext.getRequest().getLocale(),
          "projects.createStudentProject.newStudentProject");
      description = null;
      // TODO default educational time unit?
      unit = baseDAO.getEducationalTimeUnit((long) 1);
      units = 0.0;
    }
    else {
      name = project.getName();
      description = project.getDescription();
      unit = project.getOptionalStudiesLength().getUnit();
      units = project.getOptionalStudiesLength().getUnits();
    }

    StudentProject studentProject = projectDAO
        .createStudentProject(student, name, description, units, unit, loggedUser);

    if (project != null) {
      List<ProjectModule> projectModules = project.getProjectModules();
      for (ProjectModule projectModule : projectModules) {
        projectDAO.createStudentProjectModule(studentProject, projectModule.getModule(), null,
            StudentProjectModuleOptionality.getOptionality(projectModule.getOptionality().getValue()));
      }
    }
    
    String redirectURL = jsonRequestContext.getRequest().getContextPath() + "/projects/editstudentproject.page?studentproject=" + studentProject.getId();
    String refererAnchor = jsonRequestContext.getRefererAnchor();
    
    if (!StringUtils.isBlank(refererAnchor))
      redirectURL += "#" + refererAnchor;

    jsonRequestContext.setRedirectURL(redirectURL);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}