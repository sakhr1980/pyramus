package fi.pyramus.json.projects;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ProjectDAO;
import fi.pyramus.domainmodel.projects.StudentProject;
import fi.pyramus.json.JSONRequestController;

public class ArchiveStudentProjectJSONRequestController implements JSONRequestController {
  
  public void process(JSONRequestContext requestContext) {
    ProjectDAO projectDAO = DAOFactory.getInstance().getProjectDAO();

    Long studentProjectId = requestContext.getLong("studentProjectId");
    StudentProject studentProject = projectDAO.findStudentProjectById(studentProjectId);
    projectDAO.archiveStudentProject(studentProject);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
