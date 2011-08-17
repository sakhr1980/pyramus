package fi.pyramus.json.projects;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ProjectDAO;
import fi.pyramus.domainmodel.projects.Project;
import fi.pyramus.json.JSONRequestController;

public class ArchiveProjectJSONRequestController implements JSONRequestController {
  
  public void process(JSONRequestContext requestContext) {
    ProjectDAO projectDAO = DAOFactory.getInstance().getProjectDAO();

    Long projectId = requestContext.getLong("projectId");
    Project project = projectDAO.findProjectById(projectId);
    projectDAO.archiveProject(project);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
