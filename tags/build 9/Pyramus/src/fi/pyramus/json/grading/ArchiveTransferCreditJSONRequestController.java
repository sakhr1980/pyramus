package fi.pyramus.json.grading;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.GradingDAO;
import fi.pyramus.domainmodel.grading.TransferCredit;
import fi.pyramus.json.JSONRequestController;

public class ArchiveTransferCreditJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();
    
    Long transferCreditId = jsonRequestContext.getLong("transferCreditId");
    TransferCredit transferCredit = gradingDAO.findTransferCreditById(transferCreditId);
    gradingDAO.archiveCredit(transferCredit);
    
    jsonRequestContext.setRedirectURL(jsonRequestContext.getReferer(true));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
