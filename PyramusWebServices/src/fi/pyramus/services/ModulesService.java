package fi.pyramus.services;

import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.base.EducationTypeDAO;
import fi.pyramus.dao.base.EducationalTimeUnitDAO;
import fi.pyramus.dao.base.SubjectDAO;
import fi.pyramus.dao.courses.CourseDAO;
import fi.pyramus.dao.courses.CourseDescriptionCategoryDAO;
import fi.pyramus.dao.courses.CourseDescriptionDAO;
import fi.pyramus.dao.modules.ModuleComponentDAO;
import fi.pyramus.dao.modules.ModuleDAO;
import fi.pyramus.dao.users.UserDAO;
import fi.pyramus.domainmodel.base.CourseBase;
import fi.pyramus.domainmodel.base.EducationType;
import fi.pyramus.domainmodel.base.EducationalTimeUnit;
import fi.pyramus.domainmodel.base.Subject;
import fi.pyramus.domainmodel.courses.CourseDescriptionCategory;
import fi.pyramus.domainmodel.modules.Module;
import fi.pyramus.domainmodel.modules.ModuleComponent;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.services.entities.EntityFactoryVault;
import fi.pyramus.services.entities.courses.CourseDescriptionEntity;
import fi.pyramus.services.entities.modules.ModuleComponentEntity;
import fi.pyramus.services.entities.modules.ModuleEntity;

public class ModulesService extends PyramusService {

  public ModuleEntity createModule(String name, Long subjectId, Integer courseNumber, Double moduleLength,
      Long moduleLengthTimeUnitId, String description, Long creatingUserId) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();
    EducationalTimeUnitDAO educationalTimeUnitDAO = DAOFactory.getInstance().getEducationalTimeUnitDAO();
    SubjectDAO subjectDAO = DAOFactory.getInstance().getSubjectDAO();

    Subject subject = subjectDAO.findById(subjectId);
    User creatingUser = userDAO.findById(creatingUserId);
    EducationalTimeUnit moduleLengthTimeUnit = moduleLengthTimeUnitId == null ? null : educationalTimeUnitDAO.findById(moduleLengthTimeUnitId);

    Module module = moduleDAO.create(name, subject, courseNumber, moduleLength, moduleLengthTimeUnit,
        description, null, creatingUser);

    validateEntity(module);

    return EntityFactoryVault.buildFromDomainObject(module);
  }

  public void updateModule(Long moduleId, String name, Long subjectId, Integer courseNumber, Double length,
      Long lengthTimeUnitId, String description, Long modifyingUserId) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();
    EducationalTimeUnitDAO educationalTimeUnitDAO = DAOFactory.getInstance().getEducationalTimeUnitDAO();
    SubjectDAO subjectDAO = DAOFactory.getInstance().getSubjectDAO();

    Module module = moduleDAO.findById(moduleId);
    Subject subject = subjectDAO.findById(subjectId);
    User modifyingUser = userDAO.findById(modifyingUserId);
    EducationalTimeUnit moduleLengthTimeUnit = lengthTimeUnitId == null ? null : educationalTimeUnitDAO.findById(lengthTimeUnitId);

    moduleDAO.update(module, name, subject, courseNumber, length, moduleLengthTimeUnit, description, module.getMaxParticipantCount(),
        modifyingUser);
    
    validateEntity(module);
  }

  public ModuleEntity getModuleById(Long moduleId) {
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();
    return EntityFactoryVault.buildFromDomainObject(moduleDAO.findById(moduleId));
  }

  public void archiveModule(Long moduleId) {
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();
    Module module = moduleDAO.findById(moduleId);
    moduleDAO.archive(module);
  }

  public ModuleComponentEntity getModuleComponentById(Long moduleComponentId) {
    ModuleComponentDAO moduleComponentDAO = DAOFactory.getInstance().getModuleComponentDAO();
    return EntityFactoryVault.buildFromDomainObject(moduleComponentDAO.findById(moduleComponentId));
  }

  public ModuleComponentEntity createModuleComponent(Long moduleId, Double length, Long lengthTimeUnitId, String name,
      String description) {
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();
    ModuleComponentDAO moduleComponentDAO = DAOFactory.getInstance().getModuleComponentDAO();
    EducationalTimeUnitDAO educationalTimeUnitDAO = DAOFactory.getInstance().getEducationalTimeUnitDAO();

    Module module = moduleDAO.findById(moduleId);
    EducationalTimeUnit lengthTimeUnit = lengthTimeUnitId == null ? null : educationalTimeUnitDAO.findById(lengthTimeUnitId);

    ModuleComponent moduleComponent = moduleComponentDAO.create(module, length, lengthTimeUnit,
        name, description);
    
    validateEntity(moduleComponent);
    
    return EntityFactoryVault.buildFromDomainObject(moduleComponent);
  }

  public ModuleComponentEntity updateModuleComponent(Long moduleComponentId, Double length, Long lengthTimeUnitId,
      String name, String description) {
    ModuleComponentDAO moduleComponentDAO = DAOFactory.getInstance().getModuleComponentDAO();
    EducationalTimeUnitDAO educationalTimeUnitDAO = DAOFactory.getInstance().getEducationalTimeUnitDAO();

    ModuleComponent moduleComponent = moduleComponentDAO.findById(moduleComponentId);
    EducationalTimeUnit lengthTimeUnit = lengthTimeUnitId == null ? null : educationalTimeUnitDAO.findById(lengthTimeUnitId);
    
    moduleComponentDAO.update(moduleComponent, length, lengthTimeUnit, name, description);
    
    validateEntity(moduleComponent);

    return EntityFactoryVault.buildFromDomainObject(moduleComponent);
  }

  public ModuleEntity[] listModules() {
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();
    return (ModuleEntity[]) EntityFactoryVault.buildFromDomainObjects(moduleDAO.listUnarchived());
  }

  public ModuleEntity[] listModulesByEducationType(Long educationTypeId) {
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();
    EducationTypeDAO educationTypeDAO = DAOFactory.getInstance().getEducationTypeDAO();    

    EducationType educationType = educationTypeDAO.findById(educationTypeId);
    return (ModuleEntity[]) EntityFactoryVault.buildFromDomainObjects(moduleDAO
        .listByEducationType(educationType));
  }

  public CourseDescriptionEntity[] listModuleDescriptionsByModuleId(Long moduleId) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    CourseBase courseBase = courseDAO.findById(moduleId);
    CourseDescriptionDAO descriptionDAO = DAOFactory.getInstance().getCourseDescriptionDAO();
    return (CourseDescriptionEntity[]) EntityFactoryVault.buildFromDomainObjects(descriptionDAO.listByCourseBase(courseBase));
  }

  public CourseDescriptionEntity getModuleDescriptionByModuleIdAndCategoryId(Long moduleId, Long categoryId) {
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();
    CourseBase courseBase = moduleDAO.findById(moduleId);
    CourseDescriptionCategoryDAO descriptionCategoryDAO = DAOFactory.getInstance().getCourseDescriptionCategoryDAO();
    CourseDescriptionCategory category = descriptionCategoryDAO.findById(categoryId);
    CourseDescriptionDAO descriptionDAO = DAOFactory.getInstance().getCourseDescriptionDAO();
    
    return (CourseDescriptionEntity) EntityFactoryVault.buildFromDomainObject(descriptionDAO.findByCourseAndCategory(courseBase, category));
  }
  
}
