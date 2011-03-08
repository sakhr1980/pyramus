package fi.pyramus.json.students;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import fi.pyramus.JSONRequestContext;
import fi.pyramus.I18N.Messages;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.domainmodel.students.AbstractStudent;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;
import fi.pyramus.persistence.search.SearchResult;
import fi.pyramus.persistence.search.StudentFilter;

/**
 * Request handler for searching students.
 * 
 * @author antti.viljakainen
 */
public class SearchStudentsDialogJSONRequestContoller implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();

    Integer resultsPerPage = NumberUtils.createInteger(jsonRequestContext.getRequest().getParameter("maxResults"));
    if (resultsPerPage == null) {
      resultsPerPage = 10;
    }

    Integer page = NumberUtils.createInteger(jsonRequestContext.getRequest().getParameter("page"));
    if (page == null) {
      page = 0;
    }
    
    SearchResult<AbstractStudent> searchResult = null;
    
    String query = jsonRequestContext.getRequest().getParameter("query");
    StudentFilter studentFilter = (StudentFilter) jsonRequestContext.getEnum("studentFilter", StudentFilter.class);

    searchResult = studentDAO.searchAbstractStudentsBasic(resultsPerPage, page, query, studentFilter);
    
    List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
    List<AbstractStudent> abstractStudents = searchResult.getResults();
    for (AbstractStudent abstractStudent : abstractStudents) {
    	Student student = abstractStudent.getLatestStudent();
    	if (student != null) {
        Map<String, Object> info = new HashMap<String, Object>();
        info.put("abstractStudentId", abstractStudent.getId());
        info.put("id", student.getId());
        info.put("firstName", student.getFirstName());
        info.put("lastName", student.getLastName());
        info.put("archived", student.getArchived());
        info.put("lodging", Boolean.toString(student.getLodging()));
        results.add(info);
    	}
    }
    
    String statusMessage = "";
    Locale locale = jsonRequestContext.getRequest().getLocale();
    if (searchResult.getTotalHitCount() > 0) {
      statusMessage = Messages.getInstance().getText(
          locale,
          "students.searchStudents.searchStatus",
          new Object[] { searchResult.getFirstResult() + 1, searchResult.getLastResult() + 1,
              searchResult.getTotalHitCount() });
    }
    else {
      statusMessage = Messages.getInstance().getText(locale, "students.searchStudents.searchStatusNoMatches");
    }
    jsonRequestContext.addResponseParameter("results", results);
    jsonRequestContext.addResponseParameter("statusMessage", statusMessage);
    jsonRequestContext.addResponseParameter("pages", searchResult.getPages());
    jsonRequestContext.addResponseParameter("page", searchResult.getPage());
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
