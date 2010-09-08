package fi.pyramus.services.entities.grading;

import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.GradingDAO;
import fi.pyramus.domainmodel.grading.Credit;
import fi.pyramus.services.entities.EntityFactory;
import fi.pyramus.services.entities.EntityFactoryVault;

public class CreditEntityFactory implements EntityFactory<CreditEntity> {

  public CreditEntity buildFromDomainObject(Object domainObject) {
    if (domainObject == null)
      return null;
    
    Credit credit = (Credit) domainObject;
    
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();
    switch (credit.getCreditType()) {
      case CourseAssessment:
        return EntityFactoryVault.buildFromDomainObject(gradingDAO.getCourseAssessment(credit.getId()));
      case TransferCredit:
        return EntityFactoryVault.buildFromDomainObject(gradingDAO.getTransferCredit(credit.getId()));
    }
    
    return null;
  }

}
