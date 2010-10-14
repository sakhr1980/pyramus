package fi.pyramus.dao;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;

import fi.pyramus.domainmodel.base.AcademicTerm;
import fi.pyramus.domainmodel.base.Address;
import fi.pyramus.domainmodel.base.ComponentBase;
import fi.pyramus.domainmodel.base.ContactInfo;
import fi.pyramus.domainmodel.base.ContactType;
import fi.pyramus.domainmodel.base.ContactURL;
import fi.pyramus.domainmodel.base.ContactURLType;
import fi.pyramus.domainmodel.base.CourseBase;
import fi.pyramus.domainmodel.base.Defaults;
import fi.pyramus.domainmodel.base.EducationSubtype;
import fi.pyramus.domainmodel.base.EducationType;
import fi.pyramus.domainmodel.base.EducationalTimeUnit;
import fi.pyramus.domainmodel.base.Email;
import fi.pyramus.domainmodel.base.Language;
import fi.pyramus.domainmodel.base.MagicKey;
import fi.pyramus.domainmodel.base.Municipality;
import fi.pyramus.domainmodel.base.Nationality;
import fi.pyramus.domainmodel.base.PhoneNumber;
import fi.pyramus.domainmodel.base.School;
import fi.pyramus.domainmodel.base.SchoolVariable;
import fi.pyramus.domainmodel.base.SchoolVariableKey;
import fi.pyramus.domainmodel.base.StudyProgramme;
import fi.pyramus.domainmodel.base.StudyProgrammeCategory;
import fi.pyramus.domainmodel.base.Subject;
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.persistence.search.SearchResult;

/**
 * The Data Access Object for base operations.
 */
public class BaseDAO extends PyramusDAO {

  /* MagicKey */

  public MagicKey findMagicKeyById(Long id) {
    EntityManager entityManager = getEntityManager();

    return entityManager.find(MagicKey.class, id);
  }

  public MagicKey findMagicKeyByName(String name) {
    Session session = getHibernateSession();

    return (MagicKey) session.createCriteria(MagicKey.class).add(Restrictions.eq("name", name)).uniqueResult();
  }

  public MagicKey createMagicKey(String name) {
    EntityManager entityManager = getEntityManager();

    Date now = new Date(System.currentTimeMillis());

    MagicKey magicKey = new MagicKey();
    magicKey.setCreated(now);
    magicKey.setName(name);

    entityManager.persist(magicKey);

    return magicKey;
  }

  @SuppressWarnings("unchecked")
  public void deleteDeprecatedMagicKeys() {
    Session s = getHibernateSession();
    Calendar c = Calendar.getInstance();
    c.setTime(new Date());
    c.roll(Calendar.DATE, -1);

    List<MagicKey> deprecatedMagicKeys = s.createCriteria(MagicKey.class).add(Restrictions.lt("created", c.getTime())).list();

    for (MagicKey deprecatedMagicKey : deprecatedMagicKeys) {
      s.delete(deprecatedMagicKey);
    }
  }

  public void deleteMagicKey(MagicKey magicKey) {
    EntityManager entityManager = getEntityManager();

    entityManager.remove(magicKey);
  }

  /* ContactType */

  public ContactType getContactTypeById(Long id) {
    EntityManager entityManager = getEntityManager();
    return entityManager.find(ContactType.class, id);
  }

  @SuppressWarnings("unchecked")
  public List<ContactType> listContactTypes() {
    Session s = getHibernateSession();
    List<ContactType> contactTypes = s.createCriteria(ContactType.class).add(Restrictions.eq("archived", Boolean.FALSE)).list();
    Collections.sort(contactTypes, new Comparator<ContactType>() {
      public int compare(ContactType o1, ContactType o2) {
        return o1.getName() == null ? -1 : o2.getName() == null ? 1 : o1.getName().compareTo(o2.getName());
      }
    });
    return contactTypes;
  }

  public ContactType createContactType(String name) {
    Session s = getHibernateSession();
    ContactType contactType = new ContactType();
    contactType.setName(name);
    s.persist(contactType);
    return contactType;
  }

  public ContactType updateContactType(ContactType contactType, String name) {
    Session s = getHibernateSession();
    contactType.setName(name);
    s.saveOrUpdate(contactType);
    return contactType;
  }

  public ContactType archiveContactType(ContactType contactType) {
    Session s = getHibernateSession();
    contactType.setArchived(Boolean.TRUE);
    s.saveOrUpdate(contactType);
    return contactType;
  }

  public ContactType unarchiveContactType(ContactType contactType) {
    Session s = getHibernateSession();
    contactType.setArchived(Boolean.FALSE);
    s.saveOrUpdate(contactType);
    return contactType;
  }
  
  /* ContactInfo */
  
  public ContactInfo updateContactInfo(ContactInfo contactInfo, String additionalInfo) {
    Session s = getHibernateSession();
    contactInfo.setAdditionalInfo(additionalInfo);
    s.saveOrUpdate(contactInfo);
    return contactInfo;
  }
  
  /* ContactURLType */

  public ContactURLType getContactURLTypeById(Long id) {
    EntityManager entityManager = getEntityManager();
    return entityManager.find(ContactURLType.class, id);
  }

  @SuppressWarnings("unchecked")
  public List<ContactURLType> listContactURLTypes() {
    Session s = getHibernateSession();
    List<ContactURLType> contactURLTypes = s.createCriteria(ContactURLType.class).add(Restrictions.eq("archived", Boolean.FALSE)).list();
    Collections.sort(contactURLTypes, new Comparator<ContactURLType>() {
      public int compare(ContactURLType o1, ContactURLType o2) {
        return o1.getName() == null ? -1 : o2.getName() == null ? 1 : o1.getName().compareTo(o2.getName());
      }
    });
    return contactURLTypes;
  }

  public ContactURLType createContactURLType(String name) {
    Session s = getHibernateSession();
    ContactURLType contactURLType = new ContactURLType();
    contactURLType.setName(name);
    s.persist(contactURLType);
    return contactURLType;
  }

  public ContactURLType updateContactURLType(ContactURLType contactURLType, String name) {
    Session s = getHibernateSession();
    contactURLType.setName(name);
    s.saveOrUpdate(contactURLType);
    return contactURLType;
  }

  public ContactURLType archiveContactURLType(ContactURLType contactURLType) {
    Session s = getHibernateSession();
    contactURLType.setArchived(Boolean.TRUE);
    s.saveOrUpdate(contactURLType);
    return contactURLType;
  }

  public ContactURLType unarchiveContactURLType(ContactURLType contactURLType) {
    Session s = getHibernateSession();
    contactURLType.setArchived(Boolean.FALSE);
    s.saveOrUpdate(contactURLType);
    return contactURLType;
  }

  /* Email */
  
  public Email getEmailById(Long id) {
    EntityManager entityManager = getEntityManager();
    return entityManager.find(Email.class, id);
  }
  
  public Email createEmail(ContactInfo contactInfo, ContactType contactType, Boolean defaultAddress, String address) {
    Session s = getHibernateSession();
    
    Email email = new Email();
    email.setContactInfo(contactInfo);
    email.setContactType(contactType);
    email.setDefaultAddress(defaultAddress);
    email.setAddress(address);
    s.saveOrUpdate(email);
    
    contactInfo.addEmail(email);
    s.saveOrUpdate(contactInfo);
    
    return email;
  }
  
  public Email updateEmail(Email email, ContactType contactType, Boolean defaultAddress, String address) {
    Session s = getHibernateSession();
    
    email.setContactType(contactType);
    email.setDefaultAddress(defaultAddress);
    email.setAddress(address);
    s.saveOrUpdate(email);
    
    return email;
  }
  
  public void removeEmail(Email email) {
    Session s = getHibernateSession();
    if (email.getContactInfo() != null) {
      email.getContactInfo().removeEmail(email);
    }
    s.delete(email);
  }
  
  /* Phone number */

  public PhoneNumber getPhoneNumberById(Long id) {
    EntityManager entityManager = getEntityManager();
    return entityManager.find(PhoneNumber.class, id);
  }
  
  public PhoneNumber createPhoneNumber(ContactInfo contactInfo, ContactType contactType, Boolean defaultNumber, String number) {
    Session s = getHibernateSession();
    
    PhoneNumber phoneNumber = new PhoneNumber();
    phoneNumber.setContactInfo(contactInfo);
    phoneNumber.setContactType(contactType);
    phoneNumber.setDefaultNumber(defaultNumber);
    phoneNumber.setNumber(number);
    s.saveOrUpdate(phoneNumber);
    
    contactInfo.addPhoneNumber(phoneNumber);
    s.saveOrUpdate(contactInfo);
    
    return phoneNumber;
  }
  
  public PhoneNumber updatePhoneNumber(PhoneNumber phoneNumber, ContactType contactType, Boolean defaultNumber, String number) {
    Session s = getHibernateSession();
    
    phoneNumber.setContactType(contactType);
    phoneNumber.setDefaultNumber(defaultNumber);
    phoneNumber.setNumber(number);
    s.saveOrUpdate(phoneNumber);
    
    return phoneNumber;
  }
  
  public void removePhoneNumber(PhoneNumber phoneNumber) {
    Session s = getHibernateSession();
    if (phoneNumber.getContactInfo() != null) {
      phoneNumber.getContactInfo().removePhoneNumber(phoneNumber);
    }
    s.delete(phoneNumber);
  }

  /* Address */

  public Address getAddressById(Long id) {
    EntityManager entityManager = getEntityManager();
    return entityManager.find(Address.class, id);
  }
  
  public Address createAddress(ContactInfo contactInfo, ContactType contactType, String name, String streetAddress, String postalCode, String city, String country, Boolean defaultAddress) {
    Session s = getHibernateSession();
    
    Address address = new Address();
    address.setContactInfo(contactInfo);
    address.setContactType(contactType);
    address.setName(name);
    address.setStreetAddress(streetAddress);
    address.setPostalCode(postalCode);
    address.setCity(city);
    address.setCountry(country);
    address.setDefaultAddress(defaultAddress);
    s.saveOrUpdate(address);
    
    contactInfo.addAddress(address);
    s.saveOrUpdate(contactInfo);
    
    return address;
  }
  
  public void removeAddress(Address address) {
    Session s = getHibernateSession();
    if (address.getContactInfo() != null) {
      address.getContactInfo().removeAddress(address);
    }
    s.delete(address);
  }
  
  /* Contact URL */

  public ContactURL getContactURLById(Long id) {
    EntityManager entityManager = getEntityManager();
    return entityManager.find(ContactURL.class, id);
  }
  
  public ContactURL createContactURL(ContactInfo contactInfo, ContactURLType contactURLType, String url) {
    Session s = getHibernateSession();
    
    ContactURL contactURL = new ContactURL();
    contactURL.setContactInfo(contactInfo);
    contactURL.setURL(url);
    s.saveOrUpdate(contactURL);
    
    contactInfo.addContactURL(contactURL);
    s.saveOrUpdate(contactInfo);
    
    return contactURL;
  }
  
  public ContactURL updateContactURL(ContactURL contactURL, ContactURLType contactURLType, String url) {
    Session s = getHibernateSession();
    
    contactURL.setContactURLType(contactURLType);
    contactURL.setURL(url);
    s.saveOrUpdate(contactURL);
    
    return contactURL;
  }
  
  public void removeContactURL(ContactURL contactURL) {
    Session s = getHibernateSession();
    if (contactURL.getContactInfo() != null) {
      contactURL.getContactInfo().removeContactURL(contactURL);
    }
    s.delete(contactURL);
  }

  /**
   * Archives the given academic term.
   * 
   * @param academicTerm
   *          The academic term to be archived
   */
  public void archiveAcademicTerm(AcademicTerm academicTerm) {
    Session s = getHibernateSession();
    academicTerm.setArchived(Boolean.TRUE);
    s.saveOrUpdate(academicTerm);
  }

  public void unarchiveAcademicTerm(AcademicTerm academicTerm) {
    Session s = getHibernateSession();
    academicTerm.setArchived(Boolean.FALSE);
    s.saveOrUpdate(academicTerm);
  }
  
  /**
   * Archives the given education subtype.
   * 
   * @param educationSubtype
   *          The education subtype to be archived
   */
  public void archiveEducationSubtype(EducationSubtype educationSubtype) {
    Session s = getHibernateSession();
    educationSubtype.setArchived(Boolean.TRUE);
    s.saveOrUpdate(educationSubtype);
  }

  public void unarchiveEducationSubtype(EducationSubtype educationSubtype) {
    Session s = getHibernateSession();
    educationSubtype.setArchived(Boolean.FALSE);
    s.saveOrUpdate(educationSubtype);
  }
  
  /**
   * Archives the given education type.
   * 
   * @param educationType
   *          The education type to archive
   */
  public void archiveEducationType(EducationType educationType) {
    Session s = getHibernateSession();
    educationType.setArchived(Boolean.TRUE);
    s.saveOrUpdate(educationType);
  }

  public void unarchiveEducationType(EducationType educationType) {
    Session s = getHibernateSession();
    educationType.setArchived(Boolean.FALSE);
    s.saveOrUpdate(educationType);
  }
  
  /**
   * Archives the given school.
   * 
   * @param school
   *          The school to be archived
   */
  public void archiveSchool(School school) {
    Session s = getHibernateSession();
    school.setArchived(Boolean.TRUE);
    s.saveOrUpdate(school);
  }

  public void unarchiveSchool(School school) {
    Session s = getHibernateSession();
    school.setArchived(Boolean.FALSE);
    s.saveOrUpdate(school);
  }
  
  /**
   * Archives the given subject.
   * 
   * @param subject
   *          The subject to be archived
   */
  public void archiveSubject(Subject subject) {
    Session s = getHibernateSession();
    subject.setArchived(Boolean.TRUE);
    s.saveOrUpdate(subject);
  }

  public void unarchiveSubject(Subject subject) {
    Session s = getHibernateSession();
    subject.setArchived(Boolean.FALSE);
    s.saveOrUpdate(subject);
  }
  
  /**
   * Creates a new academic term.
   * 
   * @param name
   *          The name of the academic term
   * @param startDate
   *          The beginning date of the academic term
   * @param endDate
   *          The ending date of the academic term
   * 
   * @return The created academic term
   */
  public AcademicTerm createAcademicTerm(String name, Date startDate, Date endDate) {
    Session s = getHibernateSession();

    AcademicTerm term = new AcademicTerm();
    term.setName(name);
    term.setStartDate(startDate);
    term.setEndDate(endDate);
    s.save(term);

    return term;
  }

  /**
   * Creates a new educational time unit.
   * 
   * @param baseUnits
   *          The number of base units this unit is
   * @param name
   *          The unit name
   * 
   * @return The created education time unit
   */
  public EducationalTimeUnit createEducationalTimeUnit(Double baseUnits, String name) {
    EducationalTimeUnit educationalTimeUnit = new EducationalTimeUnit();
    educationalTimeUnit.setArchived(Boolean.FALSE);
    educationalTimeUnit.setBaseUnits(baseUnits);
    educationalTimeUnit.setName(name);

    Session s = getHibernateSession();
    s.saveOrUpdate(educationalTimeUnit);

    return educationalTimeUnit;
  }

  /**
   * Creates a new education subtype.
   * 
   * @param educationType
   *          The education type of the subtype
   * @param name
   *          The name of the education subtype
   * @param code
   *          The code of the education subtype
   * 
   * @return The created education subtype
   */
  public EducationSubtype createEducationSubtype(EducationType educationType, String name, String code) {
    Session s = getHibernateSession();
    EducationSubtype educationSubtype = new EducationSubtype(educationType);
    educationSubtype.setName(name);
    educationSubtype.setCode(code);
    s.save(educationSubtype);
    return educationSubtype;
  }

  /**
   * Creates a new education type.
   * 
   * @param name
   *          The name of the education type
   * @param code
   *          The code of the education type
   * 
   * @return The created education type
   */
  public EducationType createEducationType(String name, String code) {
    Session s = getHibernateSession();
    EducationType educationType = new EducationType();
    educationType.setName(name);
    educationType.setCode(code);
    s.save(educationType);
    return educationType;
  }

  /**
   * Creates a new school.
   * 
   * @param code
   *          The school code
   * @param name
   *          The school name
   * 
   * @return The created school
   */
  public School createSchool(String code, String name) {
    Session s = getHibernateSession();
    School school = new School();
    school.setCode(code);
    school.setName(name);
    s.saveOrUpdate(school);
    return school;
  }
  
  public School setSchoolTags(School school, Set<Tag> tags) {
    EntityManager entityManager = getEntityManager();
    
    school.setTags(tags);
    
    entityManager.persist(school);
    
    return school;
  }

  private SchoolVariable createSchoolVariable(School school, SchoolVariableKey key, String value) {
    Session s = getHibernateSession();

    SchoolVariable schoolVariable = new SchoolVariable();
    schoolVariable.setSchool(school);
    schoolVariable.setKey(key);
    schoolVariable.setValue(value);
    s.saveOrUpdate(schoolVariable);

    school.getVariables().add(schoolVariable);
    s.saveOrUpdate(school);

    return schoolVariable;
  }

  /**
   * Creates a new subject.
   * 
   * @param code
   *          The subject code
   * @param name
   *          The subject name
   * 
   * @return The created subject
   */
  public Subject createSubject(String code, String name) {
    Session s = getHibernateSession();
    Subject subject = new Subject();
    subject.setName(name);
    subject.setCode(code);
    s.save(subject);
    return subject;
  }

  /**
   * Permanently deletes the given education time unit. This method is only present for unit testing purposes.
   * 
   * @param educationalTimeUnit
   *          The educational time unit to be deleted
   */
  public void deleteEducationalTimeUnit(EducationalTimeUnit educationalTimeUnit) {
    Session s = getHibernateSession();
    s.delete(educationalTimeUnit);
  }

  /**
   * Permanently deletes the given education subtype. This method is only present for unit testing purposes.
   * 
   * @param educationSubtype
   *          The educational subtype to be deleted
   */
  public void deleteEducationSubtype(EducationSubtype educationSubtype) {
    Session s = getHibernateSession();
    s.delete(educationSubtype);
  }

  /**
   * Permanently deletes the given education type. This method is only present for unit testing purposes.
   * 
   * @param educationType
   *          The educational type to be deleted
   */
  public void deleteEducationType(EducationType educationType) {
    Session s = getHibernateSession();
    s.delete(educationType);
  }

  /**
   * Permanently deletes the given subject. This method is only present for unit testing purposes.
   * 
   * @param subject
   *          The subject to be deleted
   */
  public void deleteSubject(Subject subject) {
    Session s = getHibernateSession();
    s.delete(subject);
  }

  /**
   * Returns the academic term corresponding to the given identifier.
   * 
   * @param termId
   *          The academic term identifier
   * 
   * @return The academic term corresponding to the given identifier
   */
  public AcademicTerm getAcademicTerm(Long termId) {
    Session s = getHibernateSession();
    return (AcademicTerm) s.load(AcademicTerm.class, termId);
  }

  public Defaults getDefaults() {
    Session s = getHibernateSession();
    return (Defaults) s.load(Defaults.class, new Long(1));
  }

  public boolean isPyramusInitialized() {
    Session s = getHibernateSession();
    return s.get(Defaults.class, new Long(1)) != null;
  }

  /**
   * Returns the educational time unit corresponding to the given identifier.
   * 
   * @param educationalTimeUnitId
   *          The educational time unit identifier
   * 
   * @return The educational time unit corresponding to the given identifier
   */
  public EducationalTimeUnit getEducationalTimeUnit(Long educationalTimeUnitId) {
    Session s = getHibernateSession();
    return (EducationalTimeUnit) s.load(EducationalTimeUnit.class, educationalTimeUnitId);
  }

  /**
   * Returns the education subtype corresponding to the given identifier.
   * 
   * @param educationSubtypeId
   *          The education subtype identifier
   * 
   * @return The education subtype corresponding to the given identifier
   */
  public EducationSubtype getEducationSubtype(Long educationSubtypeId) {
    Session s = getHibernateSession();
    return (EducationSubtype) s.load(EducationSubtype.class, educationSubtypeId);
  }

  /**
   * Returns the education subtype corresponding to the given code.
   * 
   * @param code
   *          code of the subtype
   * 
   * @return The education subtype corresponding to the given code
   */
  public EducationSubtype getEducationSubtype(String code) {
    Session s = getHibernateSession();
    return (EducationSubtype) s.createCriteria(EducationSubtype.class).add(Restrictions.eq("code", code)).uniqueResult();
  }

  /**
   * Returns the education type corresponding to the given education type identifier.
   * 
   * @param educationTypeId
   *          The education type identifier
   * 
   * @return The education type corresponding to the given education type identifier
   */
  public EducationType getEducationType(Long educationTypeId) {
    Session s = getHibernateSession();
    return (EducationType) s.load(EducationType.class, educationTypeId);
  }

  /**
   * Returns the education type corresponding to the given code.
   * 
   * @param code
   *          code of the type
   * 
   * @return The education type corresponding to the given code
   */
  public EducationType getEducationType(String code) {
    Session s = getHibernateSession();
    return (EducationType) s.createCriteria(EducationType.class).add(Restrictions.eq("code", code)).uniqueResult();
  }

  /**
   * Returns the language corresponding to the given identifier.
   * 
   * @param languageId
   *          The language identifier
   * 
   * @return The language corresponding to the given identifier
   */
  public Language getLanguage(Long languageId) {
    Session s = getHibernateSession();
    return (Language) s.load(Language.class, languageId);
  }

  /**
   * Returns the language corresponding to the given code.
   * 
   * @param code
   *          The language code
   * 
   * @return The language corresponding to the given code
   */
  public Language getLanguageByCode(String code) {
    Session s = getHibernateSession();
    return (Language) s.createCriteria(Language.class).add(Restrictions.eq("code", code)).uniqueResult();
  }

  /**
   * Returns the municipality corresponding to the given identifier.
   * 
   * @param municipalityId
   *          The municipality identifier
   * 
   * @return The municipality corresponding to the given identifier
   */
  public Municipality getMunicipality(Long municipalityId) {
    Session s = getHibernateSession();
    return (Municipality) s.load(Municipality.class, municipalityId);
  }

  /**
   * Returns the municipality corresponding to the given code.
   * 
   * @param code
   *          The municipality code
   * 
   * @return The municipality corresponding to the given code
   */
  public Municipality getMunicipalityByCode(String code) {
    Session s = getHibernateSession();
    return (Municipality) s.createCriteria(Municipality.class).add(Restrictions.eq("code", code)).uniqueResult();
  }

  /**
   * Returns the nationality corresponding to the given identifier.
   * 
   * @param nationalityId
   *          The nationality identifier
   * 
   * @return The nationality corresponding to the given identifier
   */
  public Nationality getNationality(Long nationalityId) {
    Session s = getHibernateSession();
    return (Nationality) s.load(Nationality.class, nationalityId);
  }

  /**
   * Returns the nationality corresponding to the given code.
   * 
   * @param code
   *          The nationality code
   * 
   * @return The nationality corresponding to the given code
   */
  public Nationality getNationalityByCode(String code) {
    Session s = getHibernateSession();
    return (Nationality) s.createCriteria(Nationality.class).add(Restrictions.eq("code", code)).uniqueResult();
  }

  /**
   * Returns the school corresponding to the given identifier.
   * 
   * @param schoolId
   *          The school identifier
   * 
   * @return The school corresponding to the given identifier
   */
  public School getSchool(Long schoolId) {
    Session s = getHibernateSession();
    return (School) s.load(School.class, schoolId);
  }

  /**
   * Returns the subject corresponding to the given identifier.
   * 
   * @param subjectId
   *          The school identifier
   * 
   * @return The subject corresponding to the given identifier
   */
  public Subject getSubject(Long subjectId) {
    Session s = getHibernateSession();
    return (Subject) s.load(Subject.class, subjectId);
  }

  /**
   * Returns the subject corresponding to the given code.
   * 
   * @param code
   *          The subject code
   * 
   * @return The subject corresponding to the given code
   */
  public Subject getSubjectByCode(String code) {
    Session s = getHibernateSession();
    // TODO How to add a case sensitive restriction with Hibernate as current
    // implementation is for MySQL?
    return (Subject) s.createCriteria(Subject.class).add(Restrictions.sqlRestriction("binary code = '" + code + "'")).uniqueResult();
  }

  /**
   * Returns a list of all non-archived academic terms from the database, sorted by their beginning date.
   * 
   * @return A list of all academic terms
   */
  @SuppressWarnings("unchecked")
  public List<AcademicTerm> listAcademicTerms() {
    Session s = getHibernateSession();

    List<AcademicTerm> academicTerms = s.createCriteria(AcademicTerm.class).add(Restrictions.eq("archived", Boolean.FALSE)).list();

    Collections.sort(academicTerms, new Comparator<AcademicTerm>() {
      public int compare(AcademicTerm o1, AcademicTerm o2) {
        return o1.getStartDate() == null ? -1 : o2.getStartDate() == null ? 1 : o1.getStartDate().compareTo(o2.getStartDate());
      }
    });

    return academicTerms;
  }

  /**
   * Returns a list of all non-archived educational time units from the database, sorted by their name.
   * 
   * @return A list of all education time units
   */
  @SuppressWarnings("unchecked")
  public List<EducationalTimeUnit> listEducationalTimeUnits() {
    Session s = getHibernateSession();

    List<EducationalTimeUnit> educationalTimeUnits = s.createCriteria(EducationalTimeUnit.class).add(Restrictions.eq("archived", Boolean.FALSE)).list();

    Collections.sort(educationalTimeUnits, new Comparator<EducationalTimeUnit>() {
      public int compare(EducationalTimeUnit o1, EducationalTimeUnit o2) {
        return o1.getName() == null ? -1 : o2.getName() == null ? 1 : o1.getName().compareTo(o2.getName());
      }
    });

    return educationalTimeUnits;
  }

  /**
   * Returns a list of all education types from the database, sorted by their name.
   * 
   * @return A list of all education types
   */
  @SuppressWarnings("unchecked")
  public List<EducationType> listEducationTypes() {
    Session s = getHibernateSession();

    List<EducationType> educationTypes = s.createCriteria(EducationType.class).add(Restrictions.eq("archived", Boolean.FALSE)).list();

    Collections.sort(educationTypes, new Comparator<EducationType>() {
      public int compare(EducationType o1, EducationType o2) {
        return o1.getName() == null ? -1 : o2.getName() == null ? 1 : o1.getName().compareTo(o2.getName());
      }
    });

    return educationTypes;
  }

  /**
   * Returns a list of all education subtypes from the database, sorted by their name.
   * 
   * @param educationType
   *          The education type
   * 
   * @return A list of all education subtypes
   */
  @SuppressWarnings("unchecked")
  public List<EducationSubtype> listEducationSubtypes(EducationType educationType) {
    Session s = getHibernateSession();

    List<EducationSubtype> educationSubtypes = s.createCriteria(EducationSubtype.class).add(Restrictions.eq("educationType", educationType))
        .add(Restrictions.eq("archived", Boolean.FALSE)).list();

    Collections.sort(educationSubtypes, new Comparator<EducationSubtype>() {
      public int compare(EducationSubtype o1, EducationSubtype o2) {
        return o1.getName() == null ? -1 : o2.getName() == null ? 1 : o1.getName().compareTo(o2.getName());
      }
    });

    return educationSubtypes;
  }

  /**
   * Returns a list of all non-archived languages from the database, sorted by their name.
   * 
   * @return A list of all languages
   */
  @SuppressWarnings("unchecked")
  public List<Language> listLanguages() {
    Session s = getHibernateSession();

    List<Language> languages = s.createCriteria(Language.class).add(Restrictions.eq("archived", Boolean.FALSE)).list();

    Collections.sort(languages, new Comparator<Language>() {
      public int compare(Language o1, Language o2) {
        return o1.getName() == null ? -1 : o2.getName() == null ? 1 : o1.getName().compareTo(o2.getName());
      }
    });

    return languages;
  }

  /**
   * Returns a list of all non-archived municipalities from the database, sorted by their name.
   * 
   * @return A list of all municipalities
   */
  @SuppressWarnings("unchecked")
  public List<Municipality> listMunicipalities() {
    Session s = getHibernateSession();

    List<Municipality> municipalities = s.createCriteria(Municipality.class).add(Restrictions.eq("archived", Boolean.FALSE)).list();

    Collections.sort(municipalities, new Comparator<Municipality>() {
      public int compare(Municipality o1, Municipality o2) {
        return o1.getName() == null ? -1 : o2.getName() == null ? 1 : o1.getName().compareTo(o2.getName());
      }
    });

    return municipalities;
  }

  /**
   * Returns a list of all non-archived nationalities from the database, sorted by their name.
   * 
   * @return A list of all nationalities
   */
  @SuppressWarnings("unchecked")
  public List<Nationality> listNationalities() {
    Session s = getHibernateSession();

    List<Nationality> nationalities = s.createCriteria(Nationality.class).add(Restrictions.eq("archived", Boolean.FALSE)).list();

    Collections.sort(nationalities, new Comparator<Nationality>() {
      public int compare(Nationality o1, Nationality o2) {
        return o1.getName() == null ? -1 : o2.getName() == null ? 1 : o1.getName().compareTo(o2.getName());
      }
    });

    return nationalities;
  }

  /**
   * Returns a list of all non-archived schools from the database, sorted by their name.
   * 
   * @return A list of all schools
   */
  @SuppressWarnings("unchecked")
  public List<School> listSchools() {
    Session s = getHibernateSession();

    List<School> schools = s.createCriteria(School.class).add(Restrictions.eq("archived", Boolean.FALSE)).list();

    Collections.sort(schools, new Comparator<School>() {
      public int compare(School o1, School o2) {
        return o1.getName() == null ? -1 : o2.getName() == null ? 1 : o1.getName().compareTo(o2.getName());
      }
    });

    return schools;
  }

  /**
   * Returns a list of all schools having a school variable with the given key and value.
   * 
   * @param key
   *          The school variable key
   * @param value
   *          The school variable value
   * 
   * @return A list of all schools having a school variable with the given key and value
   */
  @SuppressWarnings("unchecked")
  public List<School> listSchoolsByVariable(String key, String value) {
    Session s = getHibernateSession();

    SchoolVariableKey schoolVariableKey = getSchoolVariableKey(key);
    return (List<School>) s.createCriteria(SchoolVariable.class).add(Restrictions.eq("key", schoolVariableKey)).add(Restrictions.eq("value", value))
        .setProjection(Projections.property("school")).list();
  }

  /**
   * Returns a list of all school variable keys from the database, sorted by their user interface name.
   * 
   * @return A list of all school variable keys
   */
  @SuppressWarnings("unchecked")
  public List<SchoolVariableKey> listSchoolVariableKeys() {
    Session s = getHibernateSession();

    List<SchoolVariableKey> schoolVariableKeys = s.createCriteria(SchoolVariableKey.class).list();

    Collections.sort(schoolVariableKeys, new Comparator<SchoolVariableKey>() {
      public int compare(SchoolVariableKey o1, SchoolVariableKey o2) {
        return o1.getVariableName() == null ? -1 : o2.getVariableName() == null ? 1 : o1.getVariableName().compareTo(o2.getVariableName());
      }
    });

    return schoolVariableKeys;
  }

  /**
   * Returns a list of user editable school variable keys from the database, sorted by their user interface name.
   * 
   * @return A list of user editable school variable keys
   */
  @SuppressWarnings("unchecked")
  public List<SchoolVariableKey> listSchoolUserEditableVariableKeys() {
    Session s = getHibernateSession();

    List<SchoolVariableKey> schoolVariableKeys = s.createCriteria(SchoolVariableKey.class).add(Restrictions.eq("userEditable", Boolean.TRUE)).list();

    Collections.sort(schoolVariableKeys, new Comparator<SchoolVariableKey>() {
      public int compare(SchoolVariableKey o1, SchoolVariableKey o2) {
        return o1.getVariableName() == null ? -1 : o2.getVariableName() == null ? 1 : o1.getVariableName().compareTo(o2.getVariableName());
      }
    });

    return schoolVariableKeys;
  }

  /**
   * Returns a list of all non-archived subjects from the database, sorted by their name.
   * 
   * @return A list of all subjects
   */
  @SuppressWarnings("unchecked")
  public List<Subject> listSubjects() {
    Session s = getHibernateSession();

    List<Subject> subjects = s.createCriteria(Subject.class).add(Restrictions.eq("archived", Boolean.FALSE)).list();

    Collections.sort(subjects, new Comparator<Subject>() {
      public int compare(Subject o1, Subject o2) {
        return o1.getName() == null ? -1 : o2.getName() == null ? 1 : o1.getName().compareTo(o2.getName());
      }
    });

    return subjects;
  }

  @SuppressWarnings("unchecked")
  public SearchResult<School> searchSchoolsBasic(int resultsPerPage, int page, String text) {

    int firstResult = page * resultsPerPage;

    StringBuilder queryBuilder = new StringBuilder();

    if (!StringUtils.isBlank(text)) {
      queryBuilder.append("+(");
      addTokenizedSearchCriteria(queryBuilder, "code", text, false);
      addTokenizedSearchCriteria(queryBuilder, "name", text, false);
      addTokenizedSearchCriteria(queryBuilder, "tags.text", text, false);
      queryBuilder.append(")");
    }

    Session s = getHibernateSession();
    FullTextSession fullTextSession = Search.getFullTextSession(s);

    try {
      String queryString = queryBuilder.toString();
      Query luceneQuery;
      QueryParser parser = new QueryParser(Version.LUCENE_29, "", new StandardAnalyzer(Version.LUCENE_29));
      if (StringUtils.isBlank(queryString)) {
        luceneQuery = new MatchAllDocsQuery();
      } else {
        luceneQuery = parser.parse(queryString);
      }

      FullTextQuery query = fullTextSession.createFullTextQuery(luceneQuery, School.class).setFirstResult(firstResult).setMaxResults(resultsPerPage);
      query.enableFullTextFilter("ArchivedSchool").setParameter("archived", Boolean.FALSE);

      int hits = query.getResultSize();
      int pages = hits / resultsPerPage;
      if (hits % resultsPerPage > 0) {
        pages++;
      }

      int lastResult = Math.min(firstResult + resultsPerPage, hits) - 1;

      return new SearchResult<School>(page, pages, hits, firstResult, lastResult, query.list());

    } catch (ParseException e) {
      throw new PersistenceException(e);
    }
  }

  /**
   * Returns a list of schools matching the given search terms.
   * 
   * @param resultsPerPage
   *          The amount of schools per page
   * @param page
   *          The search results page
   * @param code
   *          The school code
   * @param name
   *          The school name
   * @param tags
   *          The schools tags
   * @param filterArchived
   *          <code>true</code> if archived schools should be omitted, otherwise <code>false</code>
   * 
   * @return A list of schools matching the given search terms
   */
  @SuppressWarnings("unchecked")
  public SearchResult<School> searchSchools(int resultsPerPage, int page, String code, String name, String tags, boolean filterArchived) {

    int firstResult = page * resultsPerPage;

    StringBuilder queryBuilder = new StringBuilder();
    if (!StringUtils.isBlank(code)) {
      addTokenizedSearchCriteria(queryBuilder, "code", code, false);
    }
    if (!StringUtils.isBlank(name)) {
      addTokenizedSearchCriteria(queryBuilder, "name", name, false);
    }
    if (!StringUtils.isBlank(tags)) {
      addTokenizedSearchCriteria(queryBuilder, "tags.text", tags, false);
    }

    Session s = getHibernateSession();
    FullTextSession fullTextSession = Search.getFullTextSession(s);

    try {
      String queryString = queryBuilder.toString();
      Query luceneQuery;
      QueryParser parser = new QueryParser(Version.LUCENE_29, "", new StandardAnalyzer(Version.LUCENE_29));
      if (StringUtils.isBlank(queryString)) {
        luceneQuery = new MatchAllDocsQuery();
      } else {
        luceneQuery = parser.parse(queryString);
      }

      FullTextQuery query = fullTextSession.createFullTextQuery(luceneQuery, School.class).setFirstResult(firstResult).setMaxResults(resultsPerPage);

      if (filterArchived) {
        query.enableFullTextFilter("ArchivedSchool").setParameter("archived", Boolean.FALSE);
      }

      int hits = query.getResultSize();
      int pages = hits / resultsPerPage;
      if (hits % resultsPerPage > 0) {
        pages++;
      }

      int lastResult = Math.min(firstResult + resultsPerPage, hits) - 1;

      return new SearchResult<School>(page, pages, hits, firstResult, lastResult, query.list());

    } catch (ParseException e) {
      throw new PersistenceException(e);
    }
  }

  public void setSchoolVariable(School school, String key, String value) {
    SchoolVariableKey schoolVariableKey = getSchoolVariableKey(key);
    if (schoolVariableKey != null) {
      SchoolVariable schoolVariable = getSchoolVariable(school, schoolVariableKey);
      if (StringUtils.isBlank(value)) {
        if (schoolVariable != null) {
          deleteSchoolVariable(schoolVariable);
        }
      } else {
        if (schoolVariable == null) {
          schoolVariable = createSchoolVariable(school, schoolVariableKey, value);
        } else {
          updateSchoolVariable(schoolVariable, value);
        }
      }
    } else {
      throw new PersistenceException("Unknown VariableKey");
    }
  }

  private SchoolVariable getSchoolVariable(School school, SchoolVariableKey key) {
    Session s = getHibernateSession();
    SchoolVariable schoolVariable = (SchoolVariable) s.createCriteria(SchoolVariable.class).add(Restrictions.eq("school", school))
        .add(Restrictions.eq("key", key)).uniqueResult();
    return schoolVariable;
  }

  public String getSchoolVariable(School school, String key) {
    SchoolVariableKey schoolVariableKey = getSchoolVariableKey(key);
    if (schoolVariableKey != null) {
      SchoolVariable schoolVariable = getSchoolVariable(school, schoolVariableKey);
      return schoolVariable == null ? null : schoolVariable.getValue();
    } else {
      throw new PersistenceException("Unknown VariableKey");
    }
  }

  private SchoolVariableKey getSchoolVariableKey(String key) {
    Session s = getHibernateSession();
    SchoolVariableKey schoolVariableKey = (SchoolVariableKey) s.createCriteria(SchoolVariableKey.class).add(Restrictions.eq("variableKey", key)).uniqueResult();
    return schoolVariableKey;
  }

  /**
   * Updates the given academic term with the given data.
   * 
   * @param term
   *          The academic term to be updated
   * @param name
   *          The academic term name
   * @param startDate
   *          The academic term beginning date
   * @param endDate
   *          The academic term end date
   */
  public void updateAcademicTerm(AcademicTerm term, String name, Date startDate, Date endDate) {
    Session s = getHibernateSession();

    term.setName(name);
    term.setStartDate(startDate);
    term.setEndDate(endDate);
    
    s.saveOrUpdate(term);
  }

  /**
   * Updates and returns the given address.
   * 
   * @param address
   *          The address to update
   * @param defaultAddress
   *          Default address
   * @param contactType
   *          Contact type
   * @param name
   *          Name
   * @param streetAddress
   *          Street address
   * @param postalCode
   *          Postal code
   * @param city
   *          City
   * @param country
   *          Country
   * 
   * @return The updated address
   */
  public Address updateAddress(Address address, Boolean defaultAddress, ContactType contactType, String name, String streetAddress, String postalCode,
      String city, String country) {
    Session s = getHibernateSession();

    address.setDefaultAddress(defaultAddress);
    address.setContactType(contactType);
    address.setName(name);
    address.setStreetAddress(streetAddress);
    address.setPostalCode(postalCode);
    address.setCity(city);
    address.setCountry(country);

    s.saveOrUpdate(address);

    return address;
  }

  /**
   * Updates the given education subtype with the given data.
   * 
   * @param educationSubtype
   *          The education subtype to be updated
   * @param name
   *          The education subtype name
   */
  public void updateEducationSubtype(EducationSubtype educationSubtype, String name, String code) {
    Session s = getHibernateSession();
    educationSubtype.setName(name);
    educationSubtype.setCode(code);
    s.saveOrUpdate(educationSubtype);
  }

  /**
   * Updates the given education type with the given data.
   * 
   * @param educationType
   *          The education type to be updated
   * @param name
   *          The education type name
   */
  public void updateEducationType(EducationType educationType, String name, String code) {
    Session s = getHibernateSession();
    educationType.setName(name);
    educationType.setCode(code);
    s.saveOrUpdate(educationType);
  }

  /**
   * Updates the given school with the given data.
   * 
   * @param school
   *          The school to be updated
   * @param code
   *          The school code
   * @param name
   *          The school name
   */
  public void updateSchool(School school, String code, String name) {
    Session s = getHibernateSession();

    school.setCode(code);
    school.setName(name);
    s.saveOrUpdate(school);
  }

  private void updateSchoolVariable(SchoolVariable schoolVariable, String value) {
    Session s = getHibernateSession();
    schoolVariable.setValue(value);
    s.saveOrUpdate(schoolVariable);
  }

  /**
   * Updates the given subject with the given data.
   * 
   * @param subject
   *          The subject to be updated
   * @param code
   *          The subject code
   * @param name
   *          The subject name
   */
  public void updateSubject(Subject subject, String code, String name) {
    Session s = getHibernateSession();
    subject.setCode(code);
    subject.setName(name);
    s.saveOrUpdate(subject);
  }

  public StudyProgramme createStudyProgramme(String name, StudyProgrammeCategory category, String code) {
    Session s = getHibernateSession();

    StudyProgramme studyProgramme = new StudyProgramme();
    studyProgramme.setName(name);
    studyProgramme.setCategory(category);
    studyProgramme.setCode(code);
    s.saveOrUpdate(studyProgramme);

    return studyProgramme;
  }

  public void updateStudyProgramme(StudyProgramme studyProgramme, String name, StudyProgrammeCategory category, String code) {
    Session s = getHibernateSession();
    studyProgramme.setName(name);
    studyProgramme.setCategory(category);
    studyProgramme.setCode(code);
    s.saveOrUpdate(studyProgramme);
  }

  public void archiveStudyProgramme(StudyProgramme studyProgramme) {
    Session s = getHibernateSession();
    studyProgramme.setArchived(Boolean.TRUE);
    s.saveOrUpdate(studyProgramme);
  }

  public void unarchiveStudyProgramme(StudyProgramme studyProgramme) {
    Session s = getHibernateSession();
    studyProgramme.setArchived(Boolean.FALSE);
    s.saveOrUpdate(studyProgramme);
  }

  private void deleteSchoolVariable(SchoolVariable schoolVariable) {
    Session s = getHibernateSession();
    s.delete(schoolVariable);
  }

  public void deleteStudyProgramme(StudyProgramme studyProgramme) {
    Session s = getHibernateSession();
    s.delete(studyProgramme);
  }

  public StudyProgramme getStudyProgramme(Long id) {
    Session s = getHibernateSession();

    return (StudyProgramme) s.load(StudyProgramme.class, id);
  }

  public StudyProgramme getStudyProgrammeByCode(String code) {
    Session s = getHibernateSession();

    return (StudyProgramme) s.createCriteria(StudyProgramme.class).add(Restrictions.eq("code", code)).uniqueResult();
  }

  public StudyProgrammeCategory getStudyProgrammeCategory(Long id) {
    Session s = getHibernateSession();
    return (StudyProgrammeCategory) s.load(StudyProgrammeCategory.class, id);
  }

  public StudyProgrammeCategory createStudyProgrammeCategory(String name) {
    EntityManager entityManager = getEntityManager();

    StudyProgrammeCategory studyProgrammeCategory = new StudyProgrammeCategory();
    studyProgrammeCategory.setArchived(Boolean.FALSE);
    studyProgrammeCategory.setName(name);

    entityManager.persist(studyProgrammeCategory);

    return studyProgrammeCategory;
  }

  public void updateStudyProgrammeCategory(StudyProgrammeCategory studyProgrammeCategory, String name) {
    EntityManager entityManager = getEntityManager();

    studyProgrammeCategory.setName(name);

    entityManager.persist(studyProgrammeCategory);
  }

  public void archiveStudyProgrammeCategory(StudyProgrammeCategory studyProgrammeCategory) {
    EntityManager entityManager = getEntityManager();

    studyProgrammeCategory.setArchived(Boolean.TRUE);

    entityManager.persist(studyProgrammeCategory);
  }

  public void unarchiveStudyProgrammeCategory(StudyProgrammeCategory studyProgrammeCategory) {
    EntityManager entityManager = getEntityManager();

    studyProgrammeCategory.setArchived(Boolean.FALSE);

    entityManager.persist(studyProgrammeCategory);
  }

  @SuppressWarnings("unchecked")
  public List<StudyProgramme> listStudyProgrammes() {
    Session s = getHibernateSession();

    return s.createCriteria(StudyProgramme.class).add(Restrictions.ne("archived", Boolean.TRUE)).list();
  }

  @SuppressWarnings("unchecked")
  public List<StudyProgrammeCategory> listStudyProgrammeCategories() {
    Session s = getHibernateSession();
    return s.createCriteria(StudyProgrammeCategory.class).add(Restrictions.eq("archived", Boolean.FALSE)).list();
  }
  
  /* Tag */

  public Tag findTagById(Long id) {
    EntityManager entityManager = getEntityManager();
    
    return entityManager.find(Tag.class, id);
  }

  public void createMunicipality(String name, String code) {
    EntityManager entityManager = getEntityManager();

    Municipality municipality = new Municipality();
    municipality.setName(name);
    municipality.setCode(code);
    
    entityManager.persist(municipality);
  }

  public void updateMunicipality(Municipality municipality, String name, String code) {
    Session s = getHibernateSession();
    municipality.setName(name);
    municipality.setCode(code);
    s.saveOrUpdate(municipality);
  }

  public void archiveMunicipality(Municipality municipality) {
    Session s = getHibernateSession();
    municipality.setArchived(Boolean.TRUE);
    s.saveOrUpdate(municipality);
  }

  public void unarchiveMunicipality(Municipality municipality) {
    Session s = getHibernateSession();
    municipality.setArchived(Boolean.FALSE);
    s.saveOrUpdate(municipality);
  }
  
  public Tag findTagByText(String text) {
    Session s = getHibernateSession();
    
    return (Tag) s.createCriteria(Tag.class)
      .add(Restrictions.eq("text", text))
      .uniqueResult();
  }
  
  @SuppressWarnings("unchecked")
  public List<Tag> listTags() {
    Session s = getHibernateSession();
    return s.createCriteria(Tag.class).list();
  }
  
  public Tag createTag(String text) {
    EntityManager entityManager = getEntityManager();
    
    Tag tag = new Tag();
    tag.setText(text);
    
    entityManager.persist(tag);
    
    return tag;
  }
  
  public void updateTagText(Tag tag, String text) {
    EntityManager entityManager = getEntityManager();
    
    tag.setText(text);
    
    entityManager.persist(tag);
  }
  
  public void deleteTag(Tag tag) {
    EntityManager entityManager = getEntityManager();
    entityManager.remove(tag);
  }
  
  public void archiveComponentBase(ComponentBase componentBase) {
    Session s = getHibernateSession();
    componentBase.setArchived(Boolean.TRUE);
    s.saveOrUpdate(componentBase);
  }

  public void unarchiveComponentBase(ComponentBase componentBase) {
    Session s = getHibernateSession();
    componentBase.setArchived(Boolean.FALSE);
    s.saveOrUpdate(componentBase);
  }

  public void archiveCourseBase(CourseBase courseBase) {
    Session s = getHibernateSession();
    courseBase.setArchived(Boolean.TRUE);
    s.saveOrUpdate(courseBase);
  }

  public void unarchiveCourseBase(CourseBase courseBase) {
    Session s = getHibernateSession();
    courseBase.setArchived(Boolean.FALSE);
    s.saveOrUpdate(courseBase);
  }

  public void archiveEducationalTimeUnit(EducationalTimeUnit educationalTimeUnit) {
    Session s = getHibernateSession();
    educationalTimeUnit.setArchived(Boolean.TRUE);
    s.saveOrUpdate(educationalTimeUnit);
  }

  public void unarchiveEducationalTimeUnit(EducationalTimeUnit educationalTimeUnit) {
    Session s = getHibernateSession();
    educationalTimeUnit.setArchived(Boolean.FALSE);
    s.saveOrUpdate(educationalTimeUnit);
  }

  public void archiveLanguage(Language language) {
    Session s = getHibernateSession();
    language.setArchived(Boolean.TRUE);
    s.saveOrUpdate(language);
  }

  public void unarchiveLanguage(Language language) {
    Session s = getHibernateSession();
    language.setArchived(Boolean.FALSE);
    s.saveOrUpdate(language);
  }

  public void archiveNationality(Nationality nationality) {
    Session s = getHibernateSession();
    nationality.setArchived(Boolean.TRUE);
    s.saveOrUpdate(nationality);
  }

  public void unarchiveNationality(Nationality nationality) {
    Session s = getHibernateSession();
    nationality.setArchived(Boolean.FALSE);
    s.saveOrUpdate(nationality);
  }

}
