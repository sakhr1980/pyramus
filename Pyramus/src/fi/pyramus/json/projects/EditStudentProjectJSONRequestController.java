package fi.pyramus.json.projects;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.StaleObjectStateException;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ModuleDAO;
import fi.pyramus.dao.ProjectDAO;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.dao.UserDAO;
import fi.pyramus.domainmodel.base.AcademicTerm;
import fi.pyramus.domainmodel.base.Defaults;
import fi.pyramus.domainmodel.base.EducationalTimeUnit;
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.domainmodel.courses.Course;
import fi.pyramus.domainmodel.courses.CourseEnrolmentType;
import fi.pyramus.domainmodel.courses.CourseParticipationType;
import fi.pyramus.domainmodel.courses.CourseStudent;
import fi.pyramus.domainmodel.modules.Module;
import fi.pyramus.domainmodel.projects.StudentProject;
import fi.pyramus.domainmodel.projects.StudentProjectModule;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.json.JSONRequestController;
import fi.pyramus.persistence.usertypes.CourseOptionality;

public class EditStudentProjectJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext jsonRequestContext) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    ModuleDAO moduleDAO = DAOFactory.getInstance().getModuleDAO();
    ProjectDAO projectDAO = DAOFactory.getInstance().getProjectDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    
    Defaults defaults = baseDAO.getDefaults();

    // Project

    Long studentProjectId = jsonRequestContext.getLong("studentProject");
    StudentProject studentProject = projectDAO.findStudentProjectById(studentProjectId);
    
    // Version check
    Long version = jsonRequestContext.getLong("version"); 
    if (!studentProject.getVersion().equals(version))
      throw new StaleObjectStateException(StudentProject.class.getName(), studentProject.getId());
    
    String name = jsonRequestContext.getString("name");
    String description = jsonRequestContext.getString("description");
    User user = userDAO.getUser(jsonRequestContext.getLoggedUserId());
    Long optionalStudiesLengthTimeUnitId = jsonRequestContext.getLong("optionalStudiesLengthTimeUnit");
    EducationalTimeUnit optionalStudiesLengthTimeUnit = baseDAO.findEducationalTimeUnitById(optionalStudiesLengthTimeUnitId);
    Double optionalStudiesLength = jsonRequestContext.getDouble("optionalStudiesLength");
    String tagsText = jsonRequestContext.getString("tags");
    Long studentId = jsonRequestContext.getLong("student");
    
    Set<Tag> tagEntities = new HashSet<Tag>();
    if (!StringUtils.isBlank(tagsText)) {
      List<String> tags = Arrays.asList(tagsText.split("[\\ ,]"));
      for (String tag : tags) {
        if (!StringUtils.isBlank(tag)) {
          Tag tagEntity = baseDAO.findTagByText(tag.trim());
          if (tagEntity == null)
            tagEntity = baseDAO.createTag(tag);
          tagEntities.add(tagEntity);
        }
      }
    }
    
    Student student = studentDAO.getStudent(studentId);
    
    // Student
    
    if (!studentProject.getStudent().equals(student)) {
      projectDAO.updateStudentProjectStudent(studentProject, student, user);
    }
    
    projectDAO.updateStudentProject(studentProject, name, description, optionalStudiesLength,
        optionalStudiesLengthTimeUnit, user);

    // Tags

    projectDAO.setStudentProjectTags(studentProject, tagEntities);

    // Student project modules

    Set<Long> existingModuleIds = new HashSet<Long>();
    int rowCount = jsonRequestContext.getInteger("modulesTable.rowCount").intValue();
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "modulesTable." + i;
      
      Long studentProjectModuleId = jsonRequestContext.getLong(colPrefix + ".studentProjectModuleId");
      CourseOptionality optionality = (CourseOptionality) jsonRequestContext.getEnum(colPrefix + ".optionality", CourseOptionality.class);
      Long studyTermId = jsonRequestContext.getLong(colPrefix + ".academicTerm");
      AcademicTerm academicTerm = studyTermId == -1 ? null : baseDAO.getAcademicTerm(studyTermId);
      
      if (studentProjectModuleId == -1) {
        Long moduleId = jsonRequestContext.getLong(colPrefix + ".moduleId");
        Module module = moduleDAO.getModule(moduleId);
        studentProjectModuleId = projectDAO.createStudentProjectModule(studentProject, module, academicTerm, optionality).getId();
      } else {
        projectDAO.updateStudentProjectModule(projectDAO.findStudentProjectModuleById(studentProjectModuleId), academicTerm, optionality);
      }
      
      existingModuleIds.add(studentProjectModuleId);
    }
    
    // Removed Student project modules 
    
    List<StudentProjectModule> studentProjectModules = projectDAO.listStudentProjectModulesByStudentProject(studentProject);
    for (StudentProjectModule studentProjectModule : studentProjectModules) {
      if (!existingModuleIds.contains(studentProjectModule.getId())) {
        projectDAO.deleteStudentProjectModule(studentProjectModule);
      }
    }
    
    // Student project courses

    rowCount = jsonRequestContext.getInteger("coursesTable.rowCount").intValue();
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "coursesTable." + i;
      
      Long courseId = jsonRequestContext.getLong(colPrefix + ".courseId");
      CourseOptionality optionality = (CourseOptionality) jsonRequestContext.getEnum(colPrefix + ".optionality", CourseOptionality.class);
      
      Course course = courseId == -1 ? null : courseDAO.getCourse(courseId);
      CourseStudent courseStudent = courseDAO.findCourseStudentByCourseAndStudent(course, studentProject.getStudent());
      if (courseStudent == null) {
        CourseEnrolmentType courseEnrolmentType = defaults.getInitialCourseEnrolmentType();
        CourseParticipationType participationType = defaults.getInitialCourseParticipationType();
        Date enrolmentDate = new Date(System.currentTimeMillis());
        Boolean lodging = Boolean.FALSE;
        courseStudent = courseDAO.createCourseStudent(course, studentProject.getStudent(), courseEnrolmentType, participationType, enrolmentDate, lodging, optionality);
      } else {
        courseStudent = courseDAO.updateCourseStudent(courseStudent, studentProject.getStudent(), courseStudent.getCourseEnrolmentType(), courseStudent.getParticipationType(), courseStudent.getEnrolmentTime(), courseStudent.getLodging(), optionality);
      }
    }
    
    jsonRequestContext.setRedirectURL(jsonRequestContext.getReferer(true));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
