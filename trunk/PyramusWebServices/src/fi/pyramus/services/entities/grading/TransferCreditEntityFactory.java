package fi.pyramus.services.entities.grading;

import fi.pyramus.domainmodel.grading.TransferCredit;
import fi.pyramus.persistence.usertypes.CourseOptionality;
import fi.pyramus.services.entities.EntityFactory;

public class TransferCreditEntityFactory implements EntityFactory<TransferCreditEntity> {

  public TransferCreditEntity buildFromDomainObject(Object domainObject) {
    if (domainObject == null)
      return null;
    
    TransferCredit transferCredit = (TransferCredit) domainObject;
    
    Boolean optinal = transferCredit.getOptionality() != null ? transferCredit.getOptionality() == CourseOptionality.MANDATORY ? false : true : null;

    return new TransferCreditEntity(transferCredit.getId(), transferCredit.getStudent().getId(), transferCredit.getDate(), transferCredit.getGrade().getId(),
        transferCredit.getVerbalAssessment(), transferCredit.getAssessingUser().getId(), transferCredit.getArchived(), transferCredit.getCourseName(), transferCredit
            .getCourseLength().getUnits(), transferCredit.getCourseLength().getUnit().getId(), transferCredit.getSchool().getId(), transferCredit.getSubject().getId(), optinal);
  }
}
