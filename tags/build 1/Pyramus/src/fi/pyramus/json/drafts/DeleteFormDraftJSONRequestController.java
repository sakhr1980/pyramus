package fi.pyramus.json.drafts;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.DraftDAO;
import fi.pyramus.dao.UserDAO;
import fi.pyramus.domainmodel.drafts.FormDraft;
import fi.pyramus.UserRole;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.json.JSONRequestController;

public class DeleteFormDraftJSONRequestController implements JSONRequestController {
  
  public void process(JSONRequestContext requestContext) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    DraftDAO draftDAO = DAOFactory.getInstance().getDraftDAO();

    String url = requestContext.getRequest().getHeader("Referer");
    User loggedUser = userDAO.getUser(requestContext.getLoggedUserId());
    
    FormDraft formDraft = draftDAO.getFormDraftByURL(loggedUser, url);
    if (formDraft != null) {
      draftDAO.deleteFormDraft(formDraft);
    } 
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.GUEST, UserRole.USER, UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }
}
