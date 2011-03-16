package fi.pyramus.json.tags;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.json.JSONRequestController;

public class GetAllTagsJSONRequestController implements JSONRequestController {
  
  public void process(JSONRequestContext requestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    
    Set<String> tagTexts = new HashSet<String>();
    
    List<Tag> tags = baseDAO.listTags();
    for (Tag tag : tags) {
      tagTexts.add(tag.getText());
    }
    
    requestContext.addResponseParameter("tags", tagTexts);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.EVERYONE };
  }
}

