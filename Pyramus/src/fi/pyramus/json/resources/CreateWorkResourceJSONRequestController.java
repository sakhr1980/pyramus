package fi.pyramus.json.resources;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ResourceDAO;
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.domainmodel.resources.ResourceCategory;
import fi.pyramus.domainmodel.resources.WorkResource;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

public class CreateWorkResourceJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {    
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    ResourceDAO resourceDAO = DAOFactory.getInstance().getResourceDAO();

    String name = jsonRequestContext.getRequest().getParameter("name");
    Double hourlyCost = NumberUtils.createDouble(jsonRequestContext.getRequest().getParameter("hourlyCost"));
    Double costPerUse = NumberUtils.createDouble(jsonRequestContext.getRequest().getParameter("costPerUse"));
    ResourceCategory resourceCategory = resourceDAO.findResourceCategoryById(NumberUtils.createLong(jsonRequestContext.getRequest().getParameter("category")));
    String tagsText = jsonRequestContext.getString("tags");
    
    Set<Tag> tagEntities = new HashSet<Tag>();
    if (!StringUtils.isBlank(tagsText)) {
      List<String> tags = Arrays.asList(tagsText.split("[\\ ,]"));
      for (String tag : tags) {
        if (!StringUtils.isBlank(tag)) {
          Tag tagEntity = baseDAO.findTagByText(tag.trim());
          if (tagEntity == null)
            tagEntity = baseDAO.createTag(tag);
          tagEntities.add(tagEntity);
        }
      }
    }
    
    WorkResource workResource = resourceDAO.createWorkResource(name, resourceCategory, costPerUse, hourlyCost);
    resourceDAO.setResourceTags(workResource, tagEntities);
    
    String redirectURL = jsonRequestContext.getRequest().getContextPath() + "/resources/editworkresource.page?resource=" + workResource.getId();
    String refererAnchor = jsonRequestContext.getRefererAnchor();
    
    if (!StringUtils.isBlank(refererAnchor))
      redirectURL += "#" + refererAnchor;

    jsonRequestContext.setRedirectURL(redirectURL);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
