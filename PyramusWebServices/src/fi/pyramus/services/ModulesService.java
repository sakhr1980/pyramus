package fi.pyramus.services;

import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ModuleDAO;
import fi.pyramus.dao.UserDAO;
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
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();

    Subject subject = baseDAO.getSubject(subjectId);
    User creatingUser = userDAO.getUser(creatingUserId);
    EducationalTimeUnit moduleLengthTimeUnit = moduleLengthTimeUnitId == null ? null : baseDAO
        .findEducationalTimeUnitById(moduleLengthTimeUnitId);

    Module module = moduleDAO.createModule(name, subject, courseNumber, moduleLength, moduleLengthTimeUnit,
        description, null, creatingUser);

    validateEntity(module);

    return EntityFactoryVault.buildFromDomainObject(module);
  }

  public void updateModule(Long moduleId, String name, Long subjectId, Integer courseNumber, Double length,
      Long lengthTimeUnitId, String description, Long modifyingUserId) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();

    Module module = moduleDAO.getModule(moduleId);
    Subject subject = baseDAO.getSubject(subjectId);
    User modifyingUser = userDAO.getUser(modifyingUserId);
    EducationalTimeUnit moduleLengthTimeUnit = lengthTimeUnitId == null ? null : baseDAO
        .findEducationalTimeUnitById(lengthTimeUnitId);

    moduleDAO.updateModule(module, name, subject, courseNumber, length, moduleLengthTimeUnit, description, null,
        modifyingUser);
    
    validateEntity(module);
  }

  public ModuleEntity getModuleById(Long moduleId) {
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();
    return EntityFactoryVault.buildFromDomainObject(moduleDAO.getModule(moduleId));
  }

  public void archiveModule(Long moduleId) {
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();
    Module module = moduleDAO.getModule(moduleId);
    moduleDAO.archiveModule(module);
  }

  public ModuleComponentEntity getModuleComponentById(Long moduleComponentId) {
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();
    return EntityFactoryVault.buildFromDomainObject(moduleDAO.getModuleComponent(moduleComponentId));
  }

  public ModuleComponentEntity createModuleComponent(Long moduleId, Double length, Long lengthTimeUnitId, String name,
      String description) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();

    Module module = moduleDAO.getModule(moduleId);
    EducationalTimeUnit lengthTimeUnit = lengthTimeUnitId == null ? null : baseDAO
        .findEducationalTimeUnitById(lengthTimeUnitId);

    ModuleComponent moduleComponent = moduleDAO.createModuleComponent(module, length, lengthTimeUnit,
        name, description);
    
    validateEntity(moduleComponent);
    
    return EntityFactoryVault.buildFromDomainObject(moduleComponent);
  }

  public ModuleComponentEntity updateModuleComponent(Long moduleComponentId, Double length, Long lengthTimeUnitId,
      String name, String description) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();

    ModuleComponent moduleComponent = moduleDAO.getModuleComponent(moduleComponentId);
    EducationalTimeUnit lengthTimeUnit = lengthTimeUnitId == null ? null : baseDAO
        .findEducationalTimeUnitById(lengthTimeUnitId);
    
    moduleDAO.updateModuleComponent(moduleComponent, length, lengthTimeUnit, name, description);
    
    validateEntity(moduleComponent);

    return EntityFactoryVault.buildFromDomainObject(moduleComponent);
  }

  public ModuleEntity[] listModules() {
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();
    return (ModuleEntity[]) EntityFactoryVault.buildFromDomainObjects(moduleDAO.listModules());
  }

  public ModuleEntity[] listModulesByEducationType(Long educationTypeId) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();

    EducationType educationType = baseDAO.getEducationType(educationTypeId);
    return (ModuleEntity[]) EntityFactoryVault.buildFromDomainObjects(moduleDAO
        .listModulesByEducationType(educationType));
  }

  public CourseDescriptionEntity[] listModuleDescriptionsByModuleId(Long moduleId) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    CourseBase courseBase = courseDAO.getCourse(moduleId);
    return (CourseDescriptionEntity[]) EntityFactoryVault.buildFromDomainObjects(courseDAO.listCourseDescriptionsByCourseBase(courseBase));
  }

  public CourseDescriptionEntity getModuleDescriptionByModuleIdAndCategoryId(Long moduleId, Long categoryId) {
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();
    CourseBase courseBase = moduleDAO.getModule(moduleId);
    CourseDescriptionCategory category = courseDAO.findCourseDescriptionCategoryById(categoryId);
    
    return (CourseDescriptionEntity) EntityFactoryVault.buildFromDomainObject(courseDAO.findCourseDescriptionByCourseAndCategory(courseBase, category));
  }
  
}
