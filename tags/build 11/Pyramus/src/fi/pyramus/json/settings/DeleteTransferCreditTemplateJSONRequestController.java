package fi.pyramus.json.settings;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.GradingDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

public class DeleteTransferCreditTemplateJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();
    Long transferCreditTemplateId = jsonRequestContext.getLong("transferCreditTemplateId");
    gradingDAO.deleteTransferCreditTemplate(gradingDAO.findTransferCreditTemplateById(transferCreditTemplateId));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
