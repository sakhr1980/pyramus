package fi.pyramus.json.courses;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.I18N.Messages;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.base.Subject;
import fi.pyramus.domainmodel.courses.Course;
import fi.pyramus.domainmodel.courses.CourseState;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;
import fi.pyramus.persistence.search.SearchResult;
import fi.pyramus.persistence.search.SearchTimeFilterMode;

/**
 * The controller responsible of searching course.
 * 
 * @see fi.pyramus.views.modules.SearchCoursesViewController
 */
public class SearchCoursesJSONRequestController implements JSONRequestController {

  /**
   * Processes the request to search courses.
   * 
   * @param jsonRequestContext The JSON request context
   */
  public void process(JSONRequestContext requestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();

    // Determine the number of results shown per page. If not defined, default to ten results per page

    Integer resultsPerPage = NumberUtils.createInteger(requestContext.getRequest().getParameter("maxResults"));
    if (resultsPerPage == null)
      resultsPerPage = 10;

    // Determine the result page to be shown. If not defined, default to the first page

    Integer page = NumberUtils.createInteger(requestContext.getRequest().getParameter("page"));
    if (page == null) {
      page = 0;
    }

    SearchResult<Course> searchResult;
    if ("advanced".equals(requestContext.getRequest().getParameter("activeTab"))) {
      
      String name = requestContext.getString("name");
      String tags = requestContext.getString("tags");
      if (!StringUtils.isBlank(tags))
        tags = tags.replace(',', ' ');
        
      String nameExtension = requestContext.getString("nameExtension");
      String description = requestContext.getString("description");

      CourseState courseState = null;
      Long courseStateId = requestContext.getLong("state");
      if (courseStateId != null) {
        courseState = courseDAO.getCourseState(courseStateId);
      }

      Subject subject = null;
      Long subjectId = requestContext.getLong("subject");
      if (subjectId != null) {
        subject = baseDAO.getSubject(subjectId);
      }

      Date timeframeStart = null;
      String value = requestContext.getString("timeframeStart");
      if (NumberUtils.isNumber(value)) {
        timeframeStart = new Date(NumberUtils.createLong(value));
      }

      Date timeframeEnd = null;
      value = requestContext.getString("timeframeEnd");
      if (NumberUtils.isNumber(value)) {
        timeframeEnd = new Date(NumberUtils.createLong(value));
      }
      
      SearchTimeFilterMode timeFilterMode = (SearchTimeFilterMode) requestContext.getEnum("timeframeMode", SearchTimeFilterMode.class);
      
      searchResult = courseDAO.searchCourses(resultsPerPage, page, name, tags, nameExtension, description, courseState,
          subject, timeFilterMode, timeframeStart, timeframeEnd, true, false);
    }
    else {
      String text = requestContext.getRequest().getParameter("text");
      searchResult = courseDAO.searchCoursesBasic(resultsPerPage, page, text, true, false);
    }

    List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();

    List<Course> courses = searchResult.getResults();
    for (Course course : courses) {
      Map<String, Object> courseInfo = new HashMap<String, Object>();
      courseInfo.put("id", course.getId());
      courseInfo.put("name", course.getName());
      courseInfo.put("nameExtension", course.getNameExtension());
      if (course.getBeginDate() != null) {
        courseInfo.put("beginDate", course.getBeginDate().getTime());
      }
      if (course.getEndDate() != null) {
        courseInfo.put("endDate", course.getEndDate().getTime());
      }
      results.add(courseInfo);
    }

    String statusMessage = "";
    Locale locale = requestContext.getRequest().getLocale();
    if (searchResult.getTotalHitCount() > 0) {
      statusMessage = Messages.getInstance().getText(
          locale,
          "courses.searchCourses.searchStatus",
          new Object[] { searchResult.getFirstResult() + 1, searchResult.getLastResult() + 1,
              searchResult.getTotalHitCount() });
    }
    else {
      statusMessage = Messages.getInstance().getText(locale, "courses.searchCourses.searchStatusNoMatches");
    }

    requestContext.addResponseParameter("results", results);
    requestContext.addResponseParameter("statusMessage", statusMessage);
    requestContext.addResponseParameter("pages", searchResult.getPages());
    requestContext.addResponseParameter("page", searchResult.getPage());
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.EVERYONE };
  }

}
