package fi.pyramus.dao;

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
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.util.Version;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;

import fi.pyramus.domainmodel.base.AcademicTerm;
import fi.pyramus.domainmodel.base.EducationalTimeUnit;
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.domainmodel.courses.Course;
import fi.pyramus.domainmodel.modules.Module;
import fi.pyramus.domainmodel.projects.Project;
import fi.pyramus.domainmodel.projects.ProjectModule;
import fi.pyramus.domainmodel.projects.StudentProject;
import fi.pyramus.domainmodel.projects.StudentProjectCourse;
import fi.pyramus.domainmodel.projects.StudentProjectModule;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.persistence.search.SearchResult;
import fi.pyramus.persistence.usertypes.ProjectModuleOptionality;
import fi.pyramus.persistence.usertypes.CourseOptionality;

/**
 * The Data Access Object for project related operations.  
 */
public class ProjectDAO extends PyramusDAO {

  /**
   * Returns the project corresponding to the given identifier.
   * 
   * @param projectId The project identifier
   * 
   * @return The project corresponding to the given identifier
   */
  public Project getProject(Long projectId) {
    Session s = getHibernateSession();
    return (Project) s.load(Project.class, projectId);
  }

  /**
   * Returns the student project corresponding to the given identifier.
   * 
   * @param studentProjectId The student project identifier
   * 
   * @return The student project corresponding to the given identifier
   */
  public StudentProject getStudentProject(Long studentProjectId) {
    Session s = getHibernateSession();
    return (StudentProject) s.load(StudentProject.class, studentProjectId);
  }

  /*
   * public boolean projectExists(Long projectId) { Session s =
   * getHibernateSession(); return
   * s.createCriteria(Project.class).add
   * (Restrictions.idEq(projectId)).setProjection(Projections.id()).uniqueResult() != null; }
   */

  public Project createProject(String name, String description, Double optionalStudiesLength,
      EducationalTimeUnit optionalStudiesLengthTimeUnit, User creatingUser) {
    Session s = getHibernateSession();

    Date now = new Date(System.currentTimeMillis());

    Project project = new Project();
    project.setName(name);
    project.setDescription(description);
    project.getOptionalStudiesLength().setUnit(optionalStudiesLengthTimeUnit);
    project.getOptionalStudiesLength().setUnits(optionalStudiesLength);
    project.setCreator(creatingUser);
    project.setCreated(now);
    project.setLastModifier(creatingUser);
    project.setLastModified(now);

    s.save(project);

    return project;
  }
  
  public Project setProjectTags(Project project, Set<Tag> tags) {
    EntityManager entityManager = getEntityManager();
    
    project.setTags(tags);
    
    entityManager.persist(project);
    
    return project;
  }

  public StudentProject createStudentProject(Student student, String name, String description,
      Double optionalStudiesLength, EducationalTimeUnit optionalStudiesLengthTimeUnit, User user) {
    Session s = getHibernateSession();

    Date now = new Date(System.currentTimeMillis());

    StudentProject studentProject = new StudentProject();
    studentProject.setStudent(student);
    studentProject.setName(name);
    studentProject.setDescription(description);
    studentProject.getOptionalStudiesLength().setUnit(optionalStudiesLengthTimeUnit);
    studentProject.getOptionalStudiesLength().setUnits(optionalStudiesLength);
    studentProject.setCreator(user);
    studentProject.setCreated(now);
    studentProject.setLastModifier(user);
    studentProject.setLastModified(now);

    s.save(studentProject);

    return studentProject;
  }
  
  public StudentProject setStudentProjectTags(StudentProject studentProject, Set<Tag> tags) {
    EntityManager entityManager = getEntityManager();
    
    studentProject.setTags(tags);
    
    entityManager.persist(studentProject);
    
    return studentProject;
  }

  public void updateProject(Project project, String name, String description, Double optionalStudiesLength,
      EducationalTimeUnit optionalStudiesLengthTimeUnit, User user) {
    Session s = getHibernateSession();

    Date now = new Date(System.currentTimeMillis());

    project.setName(name);
    project.setDescription(description);
    project.getOptionalStudiesLength().setUnit(optionalStudiesLengthTimeUnit);
    project.getOptionalStudiesLength().setUnits(optionalStudiesLength);
    project.setLastModifier(user);
    project.setLastModified(now);

    s.saveOrUpdate(project);
  }

  public void updateStudentProject(StudentProject studentProject, String name, String description,
      Double optionalStudiesLength, EducationalTimeUnit optionalStudiesLengthTimeUnit, User user) {
    Session s = getHibernateSession();

    Date now = new Date(System.currentTimeMillis());

    studentProject.setName(name);
    studentProject.setDescription(description);
    studentProject.getOptionalStudiesLength().setUnit(optionalStudiesLengthTimeUnit);
    studentProject.getOptionalStudiesLength().setUnits(optionalStudiesLength);
    studentProject.setLastModifier(user);
    studentProject.setLastModified(now);

    s.saveOrUpdate(studentProject);
  }

  @SuppressWarnings("unchecked")
  public SearchResult<Project> searchProjectsBasic(int resultsPerPage, int page, String text) {
    int firstResult = page * resultsPerPage;

    StringBuilder queryBuilder = new StringBuilder();

    if (!StringUtils.isBlank(text)) {
      queryBuilder.append("+(");
      addTokenizedSearchCriteria(queryBuilder, "name", text, false);
      addTokenizedSearchCriteria(queryBuilder, "description", text, false);
      addTokenizedSearchCriteria(queryBuilder, "tags.text", text, false);
      queryBuilder.append(")");
    }

    Session s = getHibernateSession();
    FullTextSession fullTextSession = Search.getFullTextSession(s);

    try {
      QueryParser parser = new QueryParser(Version.LUCENE_29, "", new StandardAnalyzer(Version.LUCENE_29));
      String queryString = queryBuilder.toString();
      Query luceneQuery;

      if (StringUtils.isBlank(queryString)) {
        luceneQuery = new MatchAllDocsQuery();
      } else {
        luceneQuery = parser.parse(queryString);
      }

      FullTextQuery query = fullTextSession.createFullTextQuery(luceneQuery, Project.class)
          .setSort(new Sort(new SortField[]{SortField.FIELD_SCORE, new SortField("nameSortable", SortField.STRING)}))
          .setFirstResult(firstResult)
          .setMaxResults(resultsPerPage);

      query.enableFullTextFilter("ArchivedProject").setParameter("archived", Boolean.FALSE);
      
      int hits = query.getResultSize();
      int pages = hits / resultsPerPage;
      if (hits % resultsPerPage > 0) {
        pages++;
      }

      int lastResult = Math.min(firstResult + resultsPerPage, hits) - 1;

      return new SearchResult<Project>(page, pages, hits, firstResult, lastResult, query.list());

    }
    catch (ParseException e) {
      throw new PersistenceException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public SearchResult<StudentProject> searchStudentProjectsBasic(int resultsPerPage, int page, String projectText, String studentText) {
    int firstResult = page * resultsPerPage;

    StringBuilder queryBuilder = new StringBuilder();

    if (!StringUtils.isBlank(projectText)) {
      queryBuilder.append("+(");
      addTokenizedSearchCriteria(queryBuilder, "name", projectText, false);
      addTokenizedSearchCriteria(queryBuilder, "description", projectText, false);
      addTokenizedSearchCriteria(queryBuilder, "tags.text", projectText, false);
      queryBuilder.append(')');
    }

    if (!StringUtils.isBlank(studentText)) {
      queryBuilder.append("+(");
      addTokenizedSearchCriteria(queryBuilder, "student.fullName", studentText, false); 
      addTokenizedSearchCriteria(queryBuilder, "student.tags.text", studentText, false); 
      queryBuilder.append(')');
    }
    
    Session s = getHibernateSession();
    FullTextSession fullTextSession = Search.getFullTextSession(s);

    try {
      QueryParser parser = new QueryParser(Version.LUCENE_29, "", new StandardAnalyzer(Version.LUCENE_29));
      String queryString = queryBuilder.toString();
      Query luceneQuery;

      if (StringUtils.isBlank(queryString)) {
        luceneQuery = new MatchAllDocsQuery();
      }
      else {
        luceneQuery = parser.parse(queryString);
      }

      FullTextQuery query = fullTextSession.createFullTextQuery(luceneQuery, StudentProject.class)
          .setSort(new Sort(new SortField[]{SortField.FIELD_SCORE, new SortField("nameSortable", SortField.STRING)}))
          .setFirstResult(firstResult)
          .setMaxResults(resultsPerPage);

      query.enableFullTextFilter("ArchivedStudentProject").setParameter("archived", Boolean.FALSE);

      int hits = query.getResultSize();
      int pages = hits / resultsPerPage;
      if (hits % resultsPerPage > 0) {
        pages++;
      }

      int lastResult = Math.min(firstResult + resultsPerPage, hits) - 1;

      return new SearchResult<StudentProject>(page, pages, hits, firstResult, lastResult, query.list());

    }
    catch (ParseException e) {
      throw new PersistenceException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public SearchResult<StudentProject> searchStudentProjects(int resultsPerPage, int page, String name, String tags, String description, String studentName, boolean filterArchived) {
    int firstResult = page * resultsPerPage;

    StringBuilder queryBuilder = new StringBuilder();

    if (!StringUtils.isBlank(name))
      addTokenizedSearchCriteria(queryBuilder, "name", name, true);
    if (!StringUtils.isBlank(description))
      addTokenizedSearchCriteria(queryBuilder, "description", description, true);
    if (!StringUtils.isBlank(tags))
      addTokenizedSearchCriteria(queryBuilder, "tags.text", tags, true);
    if (!StringUtils.isBlank(description))
      addTokenizedSearchCriteria(queryBuilder, "student.fullName", studentName, true); 

    Session s = getHibernateSession();
    FullTextSession fullTextSession = Search.getFullTextSession(s);

    try {
      QueryParser parser = new QueryParser(Version.LUCENE_29, "", new StandardAnalyzer(Version.LUCENE_29));
      String queryString = queryBuilder.toString();
      Query luceneQuery;

      if (StringUtils.isBlank(queryString)) {
        luceneQuery = new MatchAllDocsQuery();
      }
      else {
        luceneQuery = parser.parse(queryString);
      }

      FullTextQuery query = fullTextSession.createFullTextQuery(luceneQuery, StudentProject.class)
          .setSort(new Sort(new SortField[]{SortField.FIELD_SCORE, new SortField("nameSortable", SortField.STRING)}))
          .setFirstResult(firstResult)
          .setMaxResults(resultsPerPage);

      if (filterArchived) {
        query.enableFullTextFilter("ArchivedStudentProject").setParameter("archived", Boolean.FALSE);
      }

      int hits = query.getResultSize();
      int pages = hits / resultsPerPage;
      if (hits % resultsPerPage > 0) {
        pages++;
      }

      int lastResult = Math.min(firstResult + resultsPerPage, hits) - 1;

      return new SearchResult<StudentProject>(page, pages, hits, firstResult, lastResult, query.list());

    }
    catch (ParseException e) {
      throw new PersistenceException(e);
    }
  }

  public ProjectModule createProjectModule(Project project, Module module, ProjectModuleOptionality optionality) {
    Session s = getHibernateSession();

    ProjectModule projectModule = new ProjectModule();
    projectModule.setProject(project);
    projectModule.setModule(module);
    projectModule.setOptionality(optionality);
    s.save(projectModule);

    project.addProjectModule(projectModule);
    s.saveOrUpdate(project);

    return projectModule;
  }

  public void updateProjectModule(ProjectModule projectModule, ProjectModuleOptionality optionality) {
    Session s = getHibernateSession();

    projectModule.setOptionality(optionality);

    s.saveOrUpdate(projectModule);
  }

  public ProjectModule getProjectModule(Long projectModuleId) {
    Session s = getHibernateSession();
    return (ProjectModule) s.load(ProjectModule.class, projectModuleId);
  }

  public void deleteProjectModule(ProjectModule projectModule) {
    Session s = getHibernateSession();
    if (projectModule.getProject() != null) {
      projectModule.getProject().removeProjectModule(projectModule);
    }
    s.delete(projectModule);
  }

  @SuppressWarnings("unchecked")
  public List<ProjectModule> listProjectModules(Long projectId) {
    Session s = getHibernateSession();
    return s.createCriteria(ProjectModule.class).add(Restrictions.eq("project", getProject(projectId))).list();
  }

  public void archiveProject(Project project) {
    Session s = getHibernateSession();
    project.setArchived(Boolean.TRUE);
    s.saveOrUpdate(project);
  }

  public void unarchiveProject(Project project) {
    Session s = getHibernateSession();
    project.setArchived(Boolean.FALSE);
    s.saveOrUpdate(project);
  }
  
  public void archiveStudentProject(StudentProject studentProject) {
    Session s = getHibernateSession();
    studentProject.setArchived(Boolean.TRUE);
    s.saveOrUpdate(studentProject);
  }

  public void unarchiveStudentProject(StudentProject studentProject) {
    Session s = getHibernateSession();
    studentProject.setArchived(Boolean.FALSE);
    s.saveOrUpdate(studentProject);
  }
  
  /* StudentProjectModule */

  public StudentProjectModule findStudentProjectModuleById(Long studentProjectModuleId) {
    Session s = getHibernateSession();
    return (StudentProjectModule) s.load(StudentProjectModule.class, studentProjectModuleId);
  }
  
  public StudentProjectModule createStudentProjectModule(StudentProject studentProject, Module module,
      AcademicTerm academicTerm, CourseOptionality optionality) {
    Session s = getHibernateSession();

    StudentProjectModule studentProjectModule = new StudentProjectModule();
    studentProjectModule.setModule(module);
    studentProjectModule.setAcademicTerm(academicTerm);
    studentProjectModule.setOptionality(optionality);
    s.save(studentProjectModule);

    studentProject.addStudentProjectModule(studentProjectModule);
    s.saveOrUpdate(studentProject);

    return studentProjectModule;
  }

  public void updateStudentProjectModule(StudentProjectModule studentProjectModule,
      AcademicTerm academicTerm, CourseOptionality optionality) {
    Session s = getHibernateSession();

    studentProjectModule.setAcademicTerm(academicTerm);
    studentProjectModule.setOptionality(optionality);

    s.saveOrUpdate(studentProjectModule);
  }

  @SuppressWarnings("unchecked")
  public List<StudentProjectModule> listStudentProjectModulesByStudentProject(StudentProject studentProject) {
    Session s = getHibernateSession();
    return s.createCriteria(StudentProjectModule.class).add(
        Restrictions.eq("studentProject", studentProject)).list();
  }

  public void deleteStudentProjectModule(StudentProjectModule studentProjectModule) {
    Session s = getHibernateSession();
    
    StudentProject studentProject = studentProjectModule.getStudentProject();
    if (studentProject != null) {
      studentProject.removeStudentProjectModule(studentProjectModule);
      s.saveOrUpdate(studentProject);
    }

    s.delete(studentProjectModule);
  }
  
  /* StudentProjectCourse */
  
  public StudentProjectCourse findStudentProjectCourseById(Long id) {
    EntityManager entityManager = getEntityManager();
    return entityManager.find(StudentProjectCourse.class, id);
  }
  
  public StudentProjectCourse findStudentProjectCourseByProjectAndCourse(StudentProject studentProject, Course course) {
    Session session = getHibernateSession();
    
    return (StudentProjectCourse) session.createCriteria(StudentProjectCourse.class)
      .add(Restrictions.eq("studentProject", studentProject))
      .add(Restrictions.eq("course", course))
      .uniqueResult();
  }
  
  public StudentProjectCourse createStudentProjectCourse(StudentProject studentProject, Course course) {
    EntityManager entityManager = getEntityManager();
    
    StudentProjectCourse studentProjectCourse = new StudentProjectCourse(); 
    studentProjectCourse.setCourse(course);
    entityManager.persist(studentProjectCourse);

    studentProject.addStudentProjectCourse(studentProjectCourse);
    entityManager.persist(studentProject);
    
    return studentProjectCourse;
  }

  @SuppressWarnings("unchecked")
  public List<StudentProjectCourse> listStudentProjectCoursesByStudentProject(StudentProject studentProject) {
    Session s = getHibernateSession();
    return s.createCriteria(StudentProjectCourse.class)
      .add(Restrictions.eq("studentProject", studentProject))
      .list();
  }
  
  public void deleteStudentProjectCourse(StudentProjectCourse studentProjectCourse) {
    EntityManager entityManager = getEntityManager();
  
    StudentProject studentProject = studentProjectCourse.getStudentProject();
    if (studentProject != null) {
      studentProject.removeStudentProjectCourse(studentProjectCourse);
      entityManager.persist(studentProject);
    }
    
    entityManager.remove(studentProjectCourse);
  }
  
}


