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
import fi.pyramus.domainmodel.base.AcademicTerm;
import fi.pyramus.domainmodel.base.EducationalTimeUnit;
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.domainmodel.modules.Module;
import fi.pyramus.domainmodel.projects.StudentProject;
import fi.pyramus.domainmodel.projects.StudentProjectModule;
import fi.pyramus.UserRole;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.json.JSONRequestController;
import fi.pyramus.persistence.usertypes.StudentProjectModuleOptionality;

public class EditStudentProjectJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();
    ProjectDAO projectDAO = DAOFactory.getInstance().getProjectDAO();

    // Project

    Long studentProjectId = NumberUtils.createLong(jsonRequestContext.getRequest().getParameter("studentProject"));
    StudentProject studentProject = projectDAO.getStudentProject(studentProjectId);
    String name = jsonRequestContext.getRequest().getParameter("name");
    String description = jsonRequestContext.getRequest().getParameter("description");
    User user = userDAO.getUser(jsonRequestContext.getLoggedUserId());
    Long optionalStudiesLengthTimeUnitId = NumberUtils.createLong(jsonRequestContext.getRequest().getParameter(
        "optionalStudiesLengthTimeUnit"));
    EducationalTimeUnit optionalStudiesLengthTimeUnit = baseDAO.getEducationalTimeUnit(optionalStudiesLengthTimeUnitId);
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
    
    projectDAO.updateStudentProject(studentProject, name, description, optionalStudiesLength,
        optionalStudiesLengthTimeUnit, user);

    // Tags

    projectDAO.setStudentProjectTags(studentProject, tagEntities);

    // Student project modules

    Set<Long> existingIds = new HashSet<Long>();
    int rowCount = NumberUtils.createInteger(
        jsonRequestContext.getRequest().getParameter("modulesTable.rowCount")).intValue();
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "modulesTable." + i;
      int optionality = new Integer(jsonRequestContext.getRequest().getParameter(colPrefix + ".optionality"))
          .intValue();
      Long studyTermId = NumberUtils.createLong(jsonRequestContext.getRequest().getParameter(colPrefix + ".studyTerm"));
      AcademicTerm academicTerm = studyTermId == -1 ? null : baseDAO.getAcademicTerm(studyTermId);
      Long studentProjectModuleId = NumberUtils.createLong(jsonRequestContext.getRequest().getParameter(
          colPrefix + ".studentProjectModuleId"));
      if (studentProjectModuleId == -1) {
        Long moduleId = NumberUtils.createLong(jsonRequestContext.getRequest().getParameter(colPrefix + ".moduleId"));
        Module module = moduleDAO.getModule(moduleId);
        studentProjectModuleId = projectDAO.createStudentProjectModule(studentProject, module, academicTerm,
            StudentProjectModuleOptionality.getOptionality(optionality)).getId();
      }
      else {
        projectDAO.updateStudentProjectModule(projectDAO.getStudentProjectModule(studentProjectModuleId), academicTerm,
            StudentProjectModuleOptionality.getOptionality(optionality));
      }
      existingIds.add(studentProjectModuleId);
    }
    List<StudentProjectModule> studentProjectModules = projectDAO.listStudentProjectModules(studentProjectId);
    for (StudentProjectModule studentProjectModule : studentProjectModules) {
      if (!existingIds.contains(studentProjectModule.getId())) {
        projectDAO.deleteStudentProjectModule(studentProjectModule);
      }
    }
    jsonRequestContext.setRedirectURL(jsonRequestContext.getReferer(true));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
