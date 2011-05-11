package fi.pyramus.json.settings;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.courses.CourseDescriptionCategory;
import fi.pyramus.json.JSONRequestController;

public class SaveCourseDescriptionCategoriesJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();

    int rowCount = jsonRequestContext.getInteger("courseDescriptionCategoriesTable.rowCount").intValue();
    for (int i = 0; i < rowCount; i++) {
      CourseDescriptionCategory category;
      
      String colPrefix = "courseDescriptionCategoriesTable." + i;
      String name = jsonRequestContext.getRequest().getParameter(colPrefix + ".name");
      Long categoryId = jsonRequestContext.getLong(colPrefix + ".categoryId");
      
      if (categoryId == -1) {
        category = courseDAO.createCourseDescriptionCategory(name); 
      } else {
        category = courseDAO.findCourseDescriptionCategoryById(categoryId);
        courseDAO.updateCourseDescriptionCategory(category, name);
      }
    }
    
    jsonRequestContext.setRedirectURL(jsonRequestContext.getReferer(true));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
