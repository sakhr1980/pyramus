package fi.pyramus.services.entities.base;

import fi.pyramus.domainmodel.base.School;
import fi.pyramus.services.entities.EntityFactory;

public class SchoolEntityFactory implements EntityFactory<SchoolEntity> {

  public SchoolEntity buildFromDomainObject(Object domainObject) {
    if (domainObject == null)
      return null;
    
    School school = (School) domainObject;
    return new SchoolEntity(school.getId(), school.getCode(), school.getName(), school.getArchived());
  }

}
