package fi.pyramus.services.entities.grading;

import fi.pyramus.domainmodel.grading.Grade;
import fi.pyramus.services.entities.EntityFactory;

public class GradeEntityFactory implements EntityFactory<GradeEntity> {

  public GradeEntity buildFromDomainObject(Object domainObject) {
    if (domainObject == null)
      return null;
    
    Grade grade = (Grade) domainObject;
    return new GradeEntity(grade.getId(), grade.getName(), grade.getDescription(), grade.getGradingScale().getId(), grade.getPassingGrade(), grade
        .getArchived(), grade.getQualification(), grade.getGPA());
  }

}
