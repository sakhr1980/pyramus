package fi.pyramus.json.projects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ProjectDAO;
import fi.pyramus.domainmodel.base.AcademicTerm;
import fi.pyramus.domainmodel.projects.StudentProject;
import fi.pyramus.domainmodel.projects.StudentProjectModule;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

/**
 * The controller returning a list of all modules in a project.
 */
public class GetStudentProjectModulesJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    ProjectDAO projectDAO = DAOFactory.getInstance().getProjectDAO();

    Long studentProjectId = NumberUtils.createLong(jsonRequestContext.getRequest().getParameter("studentProject"));
    StudentProject studentProject = projectDAO.getStudentProject(studentProjectId);

    List<Map<String, Object>> studentProjectModules = new ArrayList<Map<String, Object>>();
    for (StudentProjectModule studentProjectModule : studentProject.getStudentProjectModules()) {
      AcademicTerm academicTerm = studentProjectModule.getAcademicTerm();
      Map<String, Object> moduleInfo = new HashMap<String, Object>();
      moduleInfo.put("id", studentProjectModule.getId());
      moduleInfo.put("academicTermId", academicTerm == null ? -1 : academicTerm.getId());
      moduleInfo.put("moduleId", studentProjectModule.getModule().getId());
      moduleInfo.put("name", studentProjectModule.getModule().getName());
      moduleInfo.put("optionality", studentProjectModule.getOptionality().getValue());
      studentProjectModules.add(moduleInfo);
    }

    jsonRequestContext.addResponseParameter("studentProjectModules", studentProjectModules);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.EVERYONE };
  }

}
