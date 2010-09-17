package fi.pyramus.services.entities.grading;

import fi.pyramus.domainmodel.grading.GradingScale;
import fi.pyramus.services.entities.EntityFactory;
import fi.pyramus.services.entities.EntityFactoryVault;

public class GradingScaleEntityFactory implements EntityFactory<GradingScaleEntity> {

  public GradingScaleEntity buildFromDomainObject(Object domainObject) {
    if (domainObject == null)
      return null;
    
    GradingScale gradingScale = (GradingScale) domainObject;
    GradeEntity[] grades = (GradeEntity[]) EntityFactoryVault.buildFromDomainObjects(gradingScale.getGrades());
    return new GradingScaleEntity(gradingScale.getId(), gradingScale.getName(), gradingScale.getDescription(), gradingScale.getArchived(), grades);
  }

}
