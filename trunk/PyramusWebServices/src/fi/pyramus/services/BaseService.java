package fi.pyramus.services;

import java.util.Date;

import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.base.AcademicTerm;
import fi.pyramus.domainmodel.base.EducationSubtype;
import fi.pyramus.domainmodel.base.EducationType;
import fi.pyramus.domainmodel.base.EducationalTimeUnit;
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
    return (NationalityEntity[]) EntityFactoryVault.buildFromDomainObjects(baseDAO.listNationalities());
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
    return (LanguageEntity[]) EntityFactoryVault.buildFromDomainObjects(baseDAO.listLanguages());
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
    return (MunicipalityEntity[]) EntityFactoryVault.buildFromDomainObjects(baseDAO.listMunicipalities());
  }

  public EducationalTimeUnitEntity getEducationalTimeUnitById(Long educationalTimeUnitId) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    return EntityFactoryVault.buildFromDomainObject(baseDAO.findEducationalTimeUnitById(educationalTimeUnitId));
  }

  public EducationalTimeUnitEntity[] listEducationalTimeUnits() {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    return (EducationalTimeUnitEntity[]) EntityFactoryVault.buildFromDomainObjects(baseDAO.listEducationalTimeUnits());
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
    return (AcademicTermEntity[]) EntityFactoryVault.buildFromDomainObjects(baseDAO.listAcademicTerms());
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
    return (EducationTypeEntity[]) EntityFactoryVault.buildFromDomainObjects(baseDAO.listEducationTypes());
  }

  public EducationSubtypeEntity[] listEducationSubtypesByEducationType(Long educationTypeId) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    EducationType educationType = baseDAO.getEducationType(educationTypeId);
    return (EducationSubtypeEntity[]) EntityFactoryVault.buildFromDomainObjects(baseDAO.listEducationSubtypes(educationType));
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
    return (SchoolEntity[]) EntityFactoryVault.buildFromDomainObjects(baseDAO.listSchools());
  }

  public SchoolEntity[] listSchoolsByVariable(String key, String value) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    return (SchoolEntity[]) EntityFactoryVault.buildFromDomainObjects(baseDAO.listSchoolsByVariable(key, value));
  }

  public StudyProgrammeEntity[] listStudyProgrammes() {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    return (StudyProgrammeEntity[]) EntityFactoryVault.buildFromDomainObjects(baseDAO.listStudyProgrammes());
  }

  public SubjectEntity[] listSubjects() {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    return (SubjectEntity[]) EntityFactoryVault.buildFromDomainObjects(baseDAO.listSubjects());
  }

  public SubjectEntity createSubject(String code, String name) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    Subject subject = baseDAO.createSubject(code, name);
    validateEntity(subject);
    return EntityFactoryVault.buildFromDomainObject(subject);
  }

  public void updateSubject(Long subjectId, String code, String name) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    Subject subject = baseDAO.getSubject(subjectId);
    baseDAO.updateSubject(subject, code, name);
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

}
