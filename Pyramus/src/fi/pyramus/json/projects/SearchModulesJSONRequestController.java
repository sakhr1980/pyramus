package fi.pyramus.json.projects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import fi.pyramus.JSONRequestContext;
import fi.pyramus.I18N.Messages;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ModuleDAO;
import fi.pyramus.domainmodel.modules.Module;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;
import fi.pyramus.persistence.search.SearchResult;

/**
 * The controller responsible of searching modules.
 * 
 * @see fi.pyramus.views.modules.SearchModulesViewController
 */
public class SearchModulesJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext requestContext) {
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();

    Integer resultsPerPage = NumberUtils.createInteger(requestContext.getRequest().getParameter("maxResults"));
    if (resultsPerPage == null) {
      resultsPerPage = 10;
    }

    Integer page = NumberUtils.createInteger(requestContext.getRequest().getParameter("page"));
    if (page == null) {
      page = 0;
    }

    // Gather the search terms

    String name = requestContext.getString("name");
    String projectName = requestContext.getString("projectName");

    // Search via the DAO object

    SearchResult<Module> searchResult = moduleDAO.searchModules(resultsPerPage, page, projectName, name, null, null, null, null, null, true);

    List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
    List<Module> modules = searchResult.getResults();
    for (Module module : modules) {
      Map<String, Object> moduleInfo = new HashMap<String, Object>();
      moduleInfo.put("id", module.getId());
      moduleInfo.put("name", module.getName());
      results.add(moduleInfo);
    }

    String statusMessage = "";
    Locale locale = requestContext.getRequest().getLocale();
    if (searchResult.getTotalHitCount() > 0) {
      statusMessage = Messages.getInstance().getText(
          locale,
          "modules.searchModules.searchStatus",
          new Object[] { searchResult.getFirstResult() + 1, searchResult.getLastResult() + 1,
              searchResult.getTotalHitCount() });
    }
    else {
      statusMessage = Messages.getInstance().getText(locale, "modules.searchModules.searchStatusNoMatches");
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
