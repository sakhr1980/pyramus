package fi.pyramus.json.resources;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.ErrorLevel;
import fi.pyramus.JSONRequestContext;
import fi.pyramus.PyramusRuntimeException;
import fi.pyramus.StatusCode;
import fi.pyramus.I18N.Messages;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ResourceDAO;
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.domainmodel.resources.MaterialResource;
import fi.pyramus.domainmodel.resources.ResourceCategory;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

public class EditMaterialResourceJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {    
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    ResourceDAO resourceDAO = DAOFactory.getInstance().getResourceDAO();

    String name = jsonRequestContext.getRequest().getParameter("name");
    Long resourceId = NumberUtils.createLong(jsonRequestContext.getRequest().getParameter("resource"));
    Double unitCost = NumberUtils.createDouble(jsonRequestContext.getRequest().getParameter("unitCost"));
    Long version = NumberUtils.createLong(jsonRequestContext.getRequest().getParameter("version"));
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
    
    MaterialResource materialResource = resourceDAO.findMaterialResourceById(resourceId);
    if (!version.equals(materialResource.getVersion())) 
      throw new PyramusRuntimeException(ErrorLevel.ERROR, StatusCode.CONCURRENT_MODIFICATION, Messages.getInstance().getText(jsonRequestContext.getRequest().getLocale(), "generic.errors.concurrentModification"));
    
    ResourceCategory resourceCategory = resourceDAO.findResourceCategoryById(NumberUtils.createLong(jsonRequestContext.getRequest().getParameter("category")));
    
    resourceDAO.updateMaterialResource(materialResource, name, resourceCategory, unitCost);
    resourceDAO.setResourceTags(materialResource, tagEntities);
    
    jsonRequestContext.setRedirectURL(jsonRequestContext.getReferer(true));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}