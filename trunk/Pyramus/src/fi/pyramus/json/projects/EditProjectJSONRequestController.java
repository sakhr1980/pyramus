package fi.pyramus.json.projects;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ModuleDAO;
import fi.pyramus.dao.ProjectDAO;
import fi.pyramus.dao.UserDAO;
import fi.pyramus.domainmodel.base.EducationalTimeUnit;
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.domainmodel.modules.Module;
import fi.pyramus.domainmodel.projects.Project;
import fi.pyramus.domainmodel.projects.ProjectModule;
import fi.pyramus.UserRole;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.json.JSONRequestController;
import fi.pyramus.persistence.usertypes.ProjectModuleOptionality;

public class EditProjectJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();
    ProjectDAO projectDAO = DAOFactory.getInstance().getProjectDAO();

    // Project

    Long projectId = NumberUtils.createLong(jsonRequestContext.getRequest().getParameter("project"));
    Project project = projectDAO.getProject(projectId);
    String name = jsonRequestContext.getRequest().getParameter("name");
    String description = jsonRequestContext.getRequest().getParameter("description");
    User user = userDAO.getUser(jsonRequestContext.getLoggedUserId());
    Long optionalStudiesLengthTimeUnitId = NumberUtils.createLong(jsonRequestContext.getRequest().getParameter(
        "optionalStudiesLengthTimeUnit"));
    EducationalTimeUnit optionalStudiesLengthTimeUnit = baseDAO
        .getEducationalTimeUnit(optionalStudiesLengthTimeUnitId);
    Double optionalStudiesLength = NumberUtils.createDouble(jsonRequestContext.getRequest().getParameter(
        "optionalStudiesLength"));
    String tagsText = jsonRequestContext.getString("tags");
    
    Set<Tag> tagEntities = new HashSet<Tag>();
    if (!StringUtils.isBlank(tagsText)) {
      List<String> tags = Arrays.asList(tagsText.split("[\\ ,]"));
      for (String tag : tags) {
        Tag tagEntity = baseDAO.findTagByText(tag.trim());
        if (tagEntity == null)
          tagEntity = baseDAO.createTag(tag);
        tagEntities.add(tagEntity);
      }
    }
    
    projectDAO.updateProject(project, name, description, optionalStudiesLength, optionalStudiesLengthTimeUnit, user);

    // Tags

    projectDAO.setProjectTags(project, tagEntities);

    // Project modules

    Set<Long> existingIds = new HashSet<Long>();
    int rowCount = NumberUtils.createInteger(
        jsonRequestContext.getRequest().getParameter("modulesTable.rowCount")).intValue();
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "modulesTable." + i;
      int optionality = new Integer(jsonRequestContext.getRequest().getParameter(colPrefix + ".optionality"))
          .intValue();
      Long projectModuleId = NumberUtils.createLong(jsonRequestContext.getRequest().getParameter(
          colPrefix + ".projectModuleId"));
      if (projectModuleId == -1) {
        Long moduleId = NumberUtils.createLong(jsonRequestContext.getRequest().getParameter(colPrefix + ".moduleId"));
        Module module = moduleDAO.getModule(moduleId);
        projectModuleId = projectDAO.createProjectModule(project, module,
            ProjectModuleOptionality.getOptionality(optionality)).getId();
      }
      else {
        projectDAO.updateProjectModule(projectDAO.getProjectModule(projectModuleId), ProjectModuleOptionality
            .getOptionality(optionality));
      }
      existingIds.add(projectModuleId);
    }
    List<ProjectModule> projectModules = projectDAO.listProjectModules(projectId);
    for (ProjectModule projectModule : projectModules) {
      if (!existingIds.contains(projectModule.getId())) {
        projectDAO.deleteProjectModule(projectModule);
      }
    }
    jsonRequestContext.setRedirectURL(jsonRequestContext.getReferer(true));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
