package fi.pyramus.json.settings;

import fi.pyramus.ErrorLevel;
import fi.pyramus.JSONRequestContext;
import fi.pyramus.PyramusRuntimeException;
import fi.pyramus.StatusCode;
import fi.pyramus.UserRole;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.courses.CourseParticipationType;
import fi.pyramus.json.JSONRequestController;

public class SaveCourseParticipationTypesJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();

    CourseParticipationType initialCourseParticipationType = null;
    
    int rowCount = jsonRequestContext.getInteger("courseParticipationTypesTable.rowCount");
    for (int i = 0; i < rowCount; i++) {
      CourseParticipationType courseParticipationType = null;
      
      String colPrefix = "courseParticipationTypesTable." + i;
      
      Boolean initialType = "1".equals(jsonRequestContext.getString(colPrefix + ".initialType"));
      Long id = jsonRequestContext.getLong(colPrefix + ".courseParticipationTypeId");
      String name = jsonRequestContext.getString(colPrefix + ".name");
      
      if (id == -1) {
        courseParticipationType = courseDAO.createCourseParticipationType(name); 
      }
      else {
        courseParticipationType = courseDAO.getCourseParticipationType(id);
        courseDAO.updateCourseParticipationType(courseParticipationType, name);
      }
      

      if (initialType) {
        if (initialCourseParticipationType != null)
          throw new PyramusRuntimeException(ErrorLevel.ERROR, StatusCode.UNDEFINED, "Two or more initial course participation types defined");
        
        initialCourseParticipationType = courseParticipationType;
      }
    }
    
    if (initialCourseParticipationType != null) {
      if (!initialCourseParticipationType.equals(baseDAO.getDefaults().getInitialCourseParticipationType())) {
        baseDAO.updateInitialCourseParticipationType(initialCourseParticipationType);
      }
        
    }
    
    jsonRequestContext.setRedirectURL(jsonRequestContext.getReferer(true));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
