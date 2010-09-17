package fi.pyramus.json.modules;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ModuleDAO;
import fi.pyramus.dao.UserDAO;
import fi.pyramus.domainmodel.base.CourseEducationType;
import fi.pyramus.domainmodel.base.EducationSubtype;
import fi.pyramus.domainmodel.base.EducationType;
import fi.pyramus.domainmodel.base.EducationalTimeUnit;
import fi.pyramus.domainmodel.base.Subject;
import fi.pyramus.domainmodel.modules.Module;
import fi.pyramus.UserRole;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.json.JSONRequestController;

/**
 * The controller responsible of creating a new module.
 * 
 * @see fi.pyramus.views.users.EditUserViewController
 */
public class CreateModuleJSONRequestController implements JSONRequestController {

  /**
   * Processes the request to create a module.
   * 
   * @param requestContext The JSON request context
   */
  public void process(JSONRequestContext requestContext) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();

    String name = requestContext.getString("name");
    String description = requestContext.getString("description");
    Subject subject = baseDAO.getSubject(requestContext.getLong("subject"));
    Integer courseNumber = requestContext.getInteger("courseNumber"); 
    User loggedUser = userDAO.getUser(requestContext.getLoggedUserId());
    Long moduleLengthTimeUnitId = requestContext.getLong("moduleLengthTimeUnit");
    EducationalTimeUnit moduleLengthTimeUnit = baseDAO.getEducationalTimeUnit(moduleLengthTimeUnitId);
    Double moduleLength = requestContext.getDouble("moduleLength");
    Module module = moduleDAO.createModule(name, subject, courseNumber, moduleLength, moduleLengthTimeUnit, description, loggedUser);

    // Module components

    int rowCount = requestContext.getInteger("componentsTable.rowCount");
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "componentsTable." + i;
      String componentName = requestContext.getString(colPrefix + ".name");
      Double componentLength = requestContext.getDouble(colPrefix + ".length");
      String componentDescription = requestContext.getString(colPrefix + ".description");
      // TODO Component length; should be just hours but it currently depends on the default time unit - ok?  
      EducationalTimeUnit componentTimeUnit = baseDAO.getDefaults().getBaseTimeUnit();
      moduleDAO.createModuleComponent(module, componentLength, componentTimeUnit, componentName, componentDescription)
          .getId();
    }

    // Education types and subtypes submitted from the web page

    Map<Long, Vector<Long>> chosenEducationTypes = new HashMap<Long, Vector<Long>>();
    Enumeration<String> parameterNames = requestContext.getRequest().getParameterNames();
    while (parameterNames.hasMoreElements()) {
      name = (String) parameterNames.nextElement();
      if (name.startsWith("educationType.")) {
        String[] nameElements = name.split("\\.");
        Long educationTypeId = new Long(nameElements[1]);
        Long educationSubtypeId = new Long(nameElements[2]);
        Vector<Long> v = chosenEducationTypes.containsKey(educationTypeId) ? chosenEducationTypes.get(educationTypeId)
            : new Vector<Long>();
        v.add(educationSubtypeId);
        if (!chosenEducationTypes.containsKey(educationTypeId)) {
          chosenEducationTypes.put(educationTypeId, v);
        }
      }
    }

    // Add education types and subtypes

    for (Long educationTypeId : chosenEducationTypes.keySet()) {
      EducationType educationType = baseDAO.getEducationType(educationTypeId);
      CourseEducationType courseEducationType;
      if (!module.contains(educationType)) {
        courseEducationType = courseDAO.addCourseEducationType(module, educationType);
      }
      else {
        courseEducationType = module.getCourseEducationTypeByEducationTypeId(educationTypeId);
      }
      for (Long educationSubtypeId : chosenEducationTypes.get(educationTypeId)) {
        EducationSubtype educationSubtype = educationType.getEducationSubtypeById(educationSubtypeId);

        if (!courseEducationType.contains(educationSubtype)) {
          courseDAO.addCourseEducationSubtype(courseEducationType, educationSubtype);
        }
      }
    }

    String redirectURL = requestContext.getRequest().getContextPath() + "/modules/editmodule.page?module=" + module.getId();
    String refererAnchor = requestContext.getRefererAnchor();
    
    if (!StringUtils.isBlank(refererAnchor))
      redirectURL += "#" + refererAnchor;
        
    requestContext.setRedirectURL(redirectURL);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
