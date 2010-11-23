package fi.pyramus.json.settings;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.courses.CourseState;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

public class SaveCourseStatesJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    
    CourseState initialCourseState = null;

    int rowCount = jsonRequestContext.getInteger("courseStatesTable.rowCount").intValue();
    for (int i = 0; i < rowCount; i++) {
      CourseState courseState;
      
      String colPrefix = "courseStatesTable." + i;
      Long courseStateId = jsonRequestContext.getLong(colPrefix + ".courseStateId");
      
      Boolean initialState = "1".equals(jsonRequestContext.getString(colPrefix + ".initialState"));
      String name = jsonRequestContext.getRequest().getParameter(colPrefix + ".name");
      
        
      if (courseStateId == -1) {
        courseState = courseDAO.createCourseState(name);
      } else {
        courseState = courseDAO.getCourseState(courseStateId);
        courseDAO.updateCourseState(courseState, name);
      }
      
      if (initialState) {
        initialCourseState = courseState;
      }
    }
    
    if (initialCourseState != null) {
      if (!initialCourseState.equals(baseDAO.getDefaults().getInitialCourseState())) {
        baseDAO.updateDefaultInitialCourseState(initialCourseState);
      }
        
    }
    
    jsonRequestContext.setRedirectURL(jsonRequestContext.getReferer(true));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
