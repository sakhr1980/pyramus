package fi.pyramus.dao.base;

import javax.persistence.EntityManager;

import fi.pyramus.dao.PyramusEntityDAO;
import fi.pyramus.domainmodel.base.ContactInfo;

public class ContactInfoDAO extends PyramusEntityDAO<ContactInfo> {

  public ContactInfo update(ContactInfo contactInfo, String additionalInfo) {
    EntityManager entityManager = getEntityManager();
    contactInfo.setAdditionalInfo(additionalInfo);
    entityManager.persist(contactInfo);
    return contactInfo;
  }

}
