package fi.pyramus.json.grading;

import fi.internetix.smvc.controllers.JSONRequestContext;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.grading.TransferCreditDAO;
import fi.pyramus.domainmodel.grading.TransferCredit;
import fi.pyramus.framework.JSONRequestController;
import fi.pyramus.framework.UserRole;

public class ArchiveTransferCreditJSONRequestController extends JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    TransferCreditDAO transferCreditDAO = DAOFactory.getInstance().getTransferCreditDAO();
    
    Long transferCreditId = jsonRequestContext.getLong("transferCreditId");
    TransferCredit transferCredit = transferCreditDAO.findById(transferCreditId);
    transferCreditDAO.archive(transferCredit);
    
    jsonRequestContext.setRedirectURL(jsonRequestContext.getReferer(true));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
