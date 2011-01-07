package fi.pyramus.json.projects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.I18N.Messages;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.courses.Course;
import fi.pyramus.json.JSONRequestController;
import fi.pyramus.persistence.search.SearchResult;

public class SearchStudentProjectCoursesJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext requestContext) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();

    Integer resultsPerPage = NumberUtils.createInteger(requestContext.getRequest().getParameter("maxResults"));
    if (resultsPerPage == null) {
      resultsPerPage = 10;
    }

    Integer page = NumberUtils.createInteger(requestContext.getRequest().getParameter("page"));
    if (page == null) {
      page = 0;
    }

    String name = requestContext.getString("name");
    String tags = requestContext.getString("tags");
    
    SearchResult<Course> searchResult = courseDAO.searchCourses(resultsPerPage, page, name, tags, null, null, null, null, null, null, null, true);

    List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
    List<Course> courses = searchResult.getResults();
    for (Course course : courses) {
      Map<String, Object> courseInfo = new HashMap<String, Object>();
      courseInfo.put("id", course.getId());
      courseInfo.put("name", course.getName());
      courseInfo.put("moduleId", course.getModule().getId());
      if (course.getBeginDate() != null)
        courseInfo.put("beginDate", course.getBeginDate().getTime());
      if (course.getEndDate() != null)
        courseInfo.put("endDate", course.getEndDate().getTime());
        
      results.add(courseInfo);
    }

    String statusMessage = "";
    Locale locale = requestContext.getRequest().getLocale();
    if (searchResult.getTotalHitCount() > 0) {
      statusMessage = Messages.getInstance().getText(
          locale,
          "projects.searchStudentProjectCoursesDialog.searchStatus",
          new Object[] { searchResult.getFirstResult() + 1, searchResult.getLastResult() + 1,
              searchResult.getTotalHitCount() });
    }
    else {
      statusMessage = Messages.getInstance().getText(locale, "projects.searchStudentProjectCoursesDialog.searchStatusNoMatches");
    }

    requestContext.addResponseParameter("results", results);
    requestContext.addResponseParameter("statusMessage", statusMessage);
    requestContext.addResponseParameter("pages", searchResult.getPages());
    requestContext.addResponseParameter("page", searchResult.getPage());
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR  };
  }

}
