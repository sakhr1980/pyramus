package fi.pyramus.services.entities.base;

import fi.pyramus.domainmodel.base.Subject;
import fi.pyramus.services.entities.EntityFactory;

public class SubjectEntityFactory implements EntityFactory<SubjectEntity> {

  public SubjectEntity buildFromDomainObject(Object domainObject) {
    if (domainObject == null)
      return null;
    
    Subject subject = (Subject) domainObject; 
    return new SubjectEntity(subject.getId(), subject.getCode(), subject.getName(), subject.getArchived());
  }


}
