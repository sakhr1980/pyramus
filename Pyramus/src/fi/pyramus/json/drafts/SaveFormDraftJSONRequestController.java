package fi.pyramus.json.drafts;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.DraftDAO;
import fi.pyramus.dao.UserDAO;
import fi.pyramus.domainmodel.drafts.FormDraft;
import fi.pyramus.UserRole;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.json.JSONRequestController;

public class SaveFormDraftJSONRequestController implements JSONRequestController {
  
  public void process(JSONRequestContext requestContext) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    DraftDAO draftDAO = DAOFactory.getInstance().getDraftDAO();

    String url = requestContext.getRequest().getHeader("Referer");
    String draftData = requestContext.getString("draftData");
    
    if (draftData != null) {
      User loggedUser = userDAO.getUser(requestContext.getLoggedUserId());
      
      FormDraft formDraft = draftDAO.getFormDraftByURL(loggedUser, url);
      if (formDraft == null)
        formDraft = draftDAO.createFormDraft(loggedUser, url, draftData);
      else
        draftDAO.updateFormDraft(formDraft, draftData);
      
      requestContext.addResponseParameter("url", formDraft.getUrl());
      requestContext.addResponseParameter("draftCreated", formDraft.getCreated());
      requestContext.addResponseParameter("draftModified", formDraft.getModified());
    } 
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.GUEST, UserRole.USER, UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
