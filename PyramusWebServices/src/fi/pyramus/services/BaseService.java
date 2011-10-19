package fi.pyramus.services;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.base.AcademicTerm;
import fi.pyramus.domainmodel.base.Address;
import fi.pyramus.domainmodel.base.ContactType;
import fi.pyramus.domainmodel.base.EducationSubtype;
import fi.pyramus.domainmodel.base.EducationType;
import fi.pyramus.domainmodel.base.EducationalTimeUnit;
import fi.pyramus.domainmodel.base.Language;
import fi.pyramus.domainmodel.base.Municipality;
import fi.pyramus.domainmodel.base.Nationality;
import fi.pyramus.domainmodel.base.School;
import fi.pyramus.domainmodel.base.Subject;
import fi.pyramus.services.entities.EntityFactoryVault;
import fi.pyramus.services.entities.base.AcademicTermEntity;
import fi.pyramus.services.entities.base.EducationSubtypeEntity;
import fi.pyramus.services.entities.base.EducationTypeEntity;
import fi.pyramus.services.entities.base.EducationalTimeUnitEntity;
import fi.pyramus.services.entities.base.LanguageEntity;
import fi.pyramus.services.entities.base.MunicipalityEntity;
import fi.pyramus.services.entities.base.NationalityEntity;
import fi.pyramus.services.entities.base.SchoolEntity;
import fi.pyramus.services.entities.base.StudyProgrammeEntity;
import fi.pyramus.services.entities.base.SubjectEntity;
import fi.pyramus.util.StringAttributeComparator;

public class BaseService extends PyramusService {

  public NationalityEntity getNationalityByCode(String code) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    return EntityFactoryVault.buildFromDomainObject(baseDAO.getNationalityByCode(code));
  }

  public NationalityEntity getNationalityById(Long nationalityId) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    return EntityFactoryVault.buildFromDomainObject(baseDAO.getNationality(nationalityId));
  }

  public NationalityEntity[] listNationalities() {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    List<Nationality> nationalities = baseDAO.listNationalities();
    Collections.sort(nationalities, new StringAttributeComparator("getName"));
    return (NationalityEntity[]) EntityFactoryVault.buildFromDomainObjects(nationalities);
  }

  public LanguageEntity getLanguageByCode(String code) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    return EntityFactoryVault.buildFromDomainObject(baseDAO.getLanguageByCode(code));
  }

  public LanguageEntity getLanguageById(Long languageId) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    return EntityFactoryVault.buildFromDomainObject(baseDAO.getLanguage(languageId));
  }

  public LanguageEntity[] listLanguages() {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    List<Language> languages = baseDAO.listLanguages();
    Collections.sort(languages, new StringAttributeComparator("getName"));
    return (LanguageEntity[]) EntityFactoryVault.buildFromDomainObjects(languages);
  }

  public MunicipalityEntity getMunicipalityByCode(String code) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    return EntityFactoryVault.buildFromDomainObject(baseDAO.getMunicipalityByCode(code));
  }

  public MunicipalityEntity getMunicipalityById(Long municipalityId) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    return EntityFactoryVault.buildFromDomainObject(baseDAO.getMunicipality(municipalityId));
  }

  public MunicipalityEntity[] listMunicipalities() {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    List<Municipality> municipalities = baseDAO.listMunicipalities();
    Collections.sort(municipalities, new StringAttributeComparator("getName"));
    return (MunicipalityEntity[]) EntityFactoryVault.buildFromDomainObjects(municipalities);
  }

  public EducationalTimeUnitEntity getEducationalTimeUnitById(Long educationalTimeUnitId) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    return EntityFactoryVault.buildFromDomainObject(baseDAO.findEducationalTimeUnitById(educationalTimeUnitId));
  }

  public EducationalTimeUnitEntity[] listEducationalTimeUnits() {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    List<EducationalTimeUnit> educationalTimeUnits = baseDAO.listEducationalTimeUnits();
    Collections.sort(educationalTimeUnits, new StringAttributeComparator("getName"));
    return (EducationalTimeUnitEntity[]) EntityFactoryVault.buildFromDomainObjects(educationalTimeUnits);
  }

  public EducationalTimeUnitEntity createEducationalTimeUnit(Double baseUnits, String name) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    EducationalTimeUnit educationalTimeUnit = baseDAO.createEducationalTimeUnit(baseUnits, name);
    validateEntity(educationalTimeUnit);
    return EntityFactoryVault.buildFromDomainObject(educationalTimeUnit);
  }

  public AcademicTermEntity getAcademicTermById(Long academicTermId) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    return EntityFactoryVault.buildFromDomainObject(baseDAO.getAcademicTerm(academicTermId));
  }

  public AcademicTermEntity[] listAcademicTerms() {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    List<AcademicTerm> academicTerms = baseDAO.listAcademicTerms();

    Collections.sort(academicTerms, new Comparator<AcademicTerm>() {
      public int compare(AcademicTerm o1, AcademicTerm o2) {
        return o1.getStartDate() == null ? -1 : o2.getStartDate() == null ? 1 : o1.getStartDate().compareTo(o2.getStartDate());
      }
    });
    
    return (AcademicTermEntity[]) EntityFactoryVault.buildFromDomainObjects(academicTerms);
  }

  /*Dateformat: [-]CCYY-MM-DDThh:mm:ss[Z|(+|-)hh:mm] */
  public AcademicTermEntity createAcademicTerm(String name, Date startDate, Date endDate) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    AcademicTerm academicTerm = baseDAO.createAcademicTerm(name, startDate, endDate);
    validateEntity(academicTerm);
    return EntityFactoryVault.buildFromDomainObject(academicTerm);
  }

  public void updateAcademicTerm(Long academicTermId, String name, Date startDate, Date endDate) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    AcademicTerm academicTerm = baseDAO.getAcademicTerm(academicTermId);
    baseDAO.updateAcademicTerm(academicTerm, name, startDate, endDate);
    validateEntity(academicTerm);
  }

  public EducationSubtypeEntity createEducationSubtype(Long educationTypeId, String name, String code) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    EducationType educationType = baseDAO.getEducationType(educationTypeId);
    EducationSubtype educationSubtype = baseDAO.createEducationSubtype(educationType, name, code);
    validateEntity(educationSubtype);
    return EntityFactoryVault.buildFromDomainObject(educationSubtype);
  }

  public void updateEducationSubtype(Long educationSubtypeId, String name, String code) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    EducationSubtype educationSubtype = baseDAO.getEducationSubtype(educationSubtypeId);
    baseDAO.updateEducationSubtype(educationSubtype, name, code);
    validateEntity(educationSubtype);
  }

  public EducationTypeEntity createEducationType(String name, String code) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    EducationType educationType = baseDAO.createEducationType(name, code);
    validateEntity(educationType);
    return EntityFactoryVault.buildFromDomainObject(educationType);
  }

  public void updateEducationType(Long educationTypeId, String name, String code) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    EducationType educationType = baseDAO.getEducationType(educationTypeId);
    baseDAO.updateEducationType(educationType, name, code);
    validateEntity(educationType);
  }

  public EducationSubtypeEntity getEducationSubtypeById(Long educationSubtypeId) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    return EntityFactoryVault.buildFromDomainObject(baseDAO.getEducationSubtype(educationSubtypeId));
  }

  public EducationSubtypeEntity getEducationSubtypeByCode(String code) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    return EntityFactoryVault.buildFromDomainObject(baseDAO.getEducationSubtype(code));
  }

  public EducationTypeEntity getEducationTypeById(Long educationTypeId) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    return EntityFactoryVault.buildFromDomainObject(baseDAO.getEducationType(educationTypeId));
  }

  public EducationTypeEntity getEducationTypeByCode(String code) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    return EntityFactoryVault.buildFromDomainObject(baseDAO.getEducationType(code));
  }

  public EducationTypeEntity[] listEducationTypes() {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    List<EducationType> educationTypes = baseDAO.listEducationTypes();
    Collections.sort(educationTypes, new StringAttributeComparator("getName"));
    return (EducationTypeEntity[]) EntityFactoryVault.buildFromDomainObjects(educationTypes);
  }

  public EducationSubtypeEntity[] listEducationSubtypesByEducationType(Long educationTypeId) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    EducationType educationType = baseDAO.getEducationType(educationTypeId);
    List<EducationSubtype> educationSubtypes = baseDAO.listEducationSubtypes(educationType);
    Collections.sort(educationSubtypes, new StringAttributeComparator("getName"));
    return (EducationSubtypeEntity[]) EntityFactoryVault.buildFromDomainObjects(educationSubtypes);
  }

  public SubjectEntity getSubjectById(Long subjectId) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    return EntityFactoryVault.buildFromDomainObject(baseDAO.getSubject(subjectId));
  }

  public SubjectEntity getSubjectByCode(String code) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    return EntityFactoryVault.buildFromDomainObject(baseDAO.getSubjectByCode(code));
  }

  public SchoolEntity[] listSchools() {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    List<School> schools = baseDAO.listSchools();
    Collections.sort(schools, new StringAttributeComparator("getName"));
    return (SchoolEntity[]) EntityFactoryVault.buildFromDomainObjects(schools);
  }

  public SchoolEntity[] listSchoolsByVariable(String key, String value) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    List<School> schools = baseDAO.listSchoolsByVariable(key, value);
    Collections.sort(schools, new StringAttributeComparator("getName"));
    return (SchoolEntity[]) EntityFactoryVault.buildFromDomainObjects(schools);
  }

  public StudyProgrammeEntity[] listStudyProgrammes() {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    return (StudyProgrammeEntity[]) EntityFactoryVault.buildFromDomainObjects(baseDAO.listStudyProgrammes());
  }

  public SubjectEntity[] listSubjects() {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    List<Subject> subjects = baseDAO.listSubjects();
    Collections.sort(subjects, new StringAttributeComparator("getName"));
    return (SubjectEntity[]) EntityFactoryVault.buildFromDomainObjects(subjects);
  }

  public SubjectEntity createSubject(String code, String name, Long educationTypeId) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    EducationType educationType = educationTypeId != null ? baseDAO.getEducationType(educationTypeId) : null;
    Subject subject = baseDAO.createSubject(code, name, educationType);
    validateEntity(subject);
    return EntityFactoryVault.buildFromDomainObject(subject);
  }

  public void updateSubject(Long subjectId, String code, String name, Long educationTypeId) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    Subject subject = baseDAO.getSubject(subjectId);
    EducationType educationType = educationTypeId != null ? baseDAO.getEducationType(educationTypeId) : null;
    baseDAO.updateSubject(subject, code, name, educationType);
    validateEntity(subject);
  }

  public SchoolEntity createSchool(String code, String name) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    // TODO: schoolField parameter
    School school = baseDAO.createSchool(code, name, null);
    validateEntity(school);
    return EntityFactoryVault.buildFromDomainObject(school);
  }

  public SchoolEntity getSchoolById(Long schoolId) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    return EntityFactoryVault.buildFromDomainObject(baseDAO.getSchool(schoolId));
  }

  public void updateSchool(Long schoolId, String code, String name) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    School school = baseDAO.getSchool(schoolId);
    // TODO: schoolField parameter
    baseDAO.updateSchool(school, code, name, school.getField());
    validateEntity(school);
  }

  public String getSchoolVariable(Long schoolId, String key) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    School school = baseDAO.getSchool(schoolId);
    return baseDAO.getSchoolVariable(school, key);
  }

  public void setSchoolVariable(Long schoolId, String key, String value) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    School school = baseDAO.getSchool(schoolId);
    baseDAO.setSchoolVariable(school, key, value);
  }

  public void updateAddress(Long addressId, Boolean defaultAddress, Long contactTypeId, 
      String name, String streetAddress, String postalCode, String city, String country) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();

    Address address = baseDAO.getAddressById(addressId);
    ContactType contactType = baseDAO.getContactTypeById(contactTypeId);
    
    baseDAO.updateAddress(address, defaultAddress, contactType, name, streetAddress, postalCode, city, country);
  }
}
