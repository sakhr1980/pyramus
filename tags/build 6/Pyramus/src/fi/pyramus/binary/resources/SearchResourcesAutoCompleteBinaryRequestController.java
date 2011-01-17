package fi.pyramus.binary.resources;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import fi.pyramus.BinaryRequestContext;
import fi.pyramus.PyramusRuntimeException;
import fi.pyramus.UserRole;
import fi.pyramus.binary.BinaryRequestController;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ResourceDAO;
import fi.pyramus.domainmodel.resources.Resource;
import fi.pyramus.domainmodel.resources.ResourceCategory;
import fi.pyramus.persistence.search.SearchResult;

public class SearchResourcesAutoCompleteBinaryRequestController implements BinaryRequestController {

  public void process(BinaryRequestContext binaryRequestContext) {
    ResourceDAO resourceDAO = DAOFactory.getInstance().getResourceDAO();
    
    String query = binaryRequestContext.getString("query");
    
    StringBuilder resultBuilder = new StringBuilder();
    
    resultBuilder.append("<ul>");
    
    List<ResourceCategory> resourceCategories = resourceDAO.listResourceCategories();
    for (ResourceCategory resourceCategory : resourceCategories) {
      SearchResult<Resource> searchResult = resourceDAO.searchResources(5, 0, query + '*', null, null, resourceCategory, true);
      if (searchResult.getTotalHitCount() > 0) {
        addResourceCategory(resultBuilder, resourceCategory);
        
        for (Resource resource : searchResult.getResults()) {
          addResource(resultBuilder, resource);
        }
      }
    }
    
    resultBuilder.append("</ul>");
    
    try {
      binaryRequestContext.setResponseContent(resultBuilder.toString().getBytes("UTF-8"), "text/html;charset=UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new PyramusRuntimeException(e);
    }
  }
  
  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.USER, UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }
  
  private void addResourceCategory(StringBuilder resultBuilder, ResourceCategory resourceCategory) {
    resultBuilder
      .append("<li class=\"autocompleteGroupTitle\">")
      .append("<span>") 
      .append(StringEscapeUtils.escapeHtml(resourceCategory.getName()))
      .append("</span>")
      .append("<input type=\"hidden\" name=\"categoryId\" value=\"")
      .append(resourceCategory.getId())
      .append("\"/>")
      .append("</li>");
  }
  
  private void addResource(StringBuilder resultBuilder, Resource resource) {
    resultBuilder
      .append("<li>")
      .append("<span>")
      .append(StringEscapeUtils.escapeHtml(resource.getName()))
      .append("</span>")
      .append("<input type=\"hidden\" name=\"resourceId\" value=\"")
      .append(resource.getId())
      .append("\"/>")
      .append("<input type=\"hidden\" name=\"resourceType\" value=\"")
      .append(resource.getResourceType())
      .append("\"/>")
      .append("</li>");
  }
}
