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
import fi.pyramus.domainmodel.resources.MaterialResource;
import fi.pyramus.domainmodel.resources.ResourceCategory;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

public class CreateMaterialResourceJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) { 
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    ResourceDAO resourceDAO = DAOFactory.getInstance().getResourceDAO();

    String name = jsonRequestContext.getRequest().getParameter("name");
    Double unitCost = NumberUtils.createDouble(jsonRequestContext.getRequest().getParameter("unitCost"));
    ResourceCategory resourceCategory = resourceDAO.findResourceCategoryById(NumberUtils.createLong(jsonRequestContext.getRequest().getParameter("category")));
    String tagsText = jsonRequestContext.getString("tags");
    
    Set<Tag> tagEntities = new HashSet<Tag>();
    if (!StringUtils.isBlank(tagsText)) {
      List<String> tags = Arrays.asList(tagsText.split("[\\ ,]"));
      for (String tag : tags) {
        Tag tagEntity = baseDAO.findTagByText(tag.trim());
        if (tagEntity == null)
          tagEntity = baseDAO.createTag(tag);
        tagEntities.add(tagEntity);
      }
    }
    
    MaterialResource materialResource = resourceDAO.createMaterialResource(name, resourceCategory, unitCost);
    resourceDAO.setResourceTags(materialResource, tagEntities);
    
    String redirectURL = jsonRequestContext.getRequest().getContextPath() + "/resources/editmaterialresource.page?resource=" + materialResource.getId();
    String refererAnchor = jsonRequestContext.getRefererAnchor();
    
    if (!StringUtils.isBlank(refererAnchor))
      redirectURL += "#" + refererAnchor;

    jsonRequestContext.setRedirectURL(redirectURL);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
