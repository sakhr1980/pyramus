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
import fi.pyramus.domainmodel.modules.Module;
import fi.pyramus.domainmodel.projects.Project;
import fi.pyramus.domainmodel.projects.ProjectModule;
import fi.pyramus.domainmodel.projects.StudentProject;
import fi.pyramus.domainmodel.projects.StudentProjectModule;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.persistence.search.SearchResult;
import fi.pyramus.persistence.usertypes.ProjectModuleOptionality;
import fi.pyramus.persistence.usertypes.StudentProjectModuleOptionality;

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
  public SearchResult<Project> searchProjects(int resultsPerPage, int page, String name, String description,
      Long ownerId, boolean filterArchived, boolean escapeSpecialChars) {
    int firstResult = page * resultsPerPage;

    StringBuilder queryBuilder = new StringBuilder();

    if (!StringUtils.isBlank(name)) {
      queryBuilder.append(escapeSpecialChars ? QueryParser.escape(name) : name);
    }

    if (!StringUtils.isBlank(description)) {
      queryBuilder.append(" description: ").append(escapeSpecialChars ? QueryParser.escape(description) : description);
    }

    Session s = getHibernateSession();
    FullTextSession fullTextSession = Search.getFullTextSession(s);

    try {
      if (ownerId != null && ownerId > 0) {
        queryBuilder.append(" +creator.id: ").append(ownerId);
      }

      QueryParser parser = new QueryParser(Version.LUCENE_29, "name", new StandardAnalyzer(Version.LUCENE_29));
      String queryString = queryBuilder.toString();
      Query luceneQuery;

      if (StringUtils.isBlank(queryString)) {
        luceneQuery = new MatchAllDocsQuery();
      }
      else {
        luceneQuery = parser.parse(queryString);
      }

      FullTextQuery query = fullTextSession.createFullTextQuery(luceneQuery, Project.class)
          .setSort(new Sort(new SortField[]{SortField.FIELD_SCORE, new SortField("nameSortable", SortField.STRING)}))
          .setFirstResult(firstResult)
          .setMaxResults(resultsPerPage);

      if (filterArchived) {
        query.enableFullTextFilter("ArchivedProject").setParameter("archived", Boolean.FALSE);
      }

      int hits = query.getResultSize();
      int pages = hits / resultsPerPage;
      if (hits % resultsPerPage > 0) {
        pages++;
      }

      int lastResult = Math.min(firstResult + resultsPerPage, hits) - 1;

      return new SearchResult<Project>(page, pages, hits, firstResult, lastResult, query.list());

    }
    catch (ParseException e) {
      if (!escapeSpecialChars) {
        return searchProjects(resultsPerPage, page, name, description, ownerId, filterArchived, true);
      }
      else {
        throw new PersistenceException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  public SearchResult<StudentProject> searchStudentProjects(int resultsPerPage, int page, String name, String description, String studentName, boolean filterArchived, boolean escapeSpecialChars) {
    int firstResult = page * resultsPerPage;

    StringBuilder queryBuilder = new StringBuilder();

    boolean projectSearch = !StringUtils.isBlank(name) || !StringUtils.isBlank(description);
    
    if (projectSearch) {
      queryBuilder.append("+(");
      if (!StringUtils.isBlank(name)) {
        queryBuilder.append("name: " + (escapeSpecialChars ? QueryParser.escape(name) : name));
      }
      if (!StringUtils.isBlank(description)) {
        queryBuilder.append(" description: ").append(escapeSpecialChars ? QueryParser.escape(description) : description);
      }
      queryBuilder.append(")");
    }

    if (!StringUtils.isBlank(studentName)) {
      queryBuilder.append(" +student.fullName: ").append(escapeSpecialChars ? QueryParser.escape(studentName) : studentName);
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
      if (!escapeSpecialChars) {
        return searchStudentProjects(resultsPerPage, page, name, description, studentName, filterArchived, true);
      }
      else {
        throw new PersistenceException(e);
      }
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

  public StudentProjectModule createStudentProjectModule(StudentProject studentProject, Module module,
      AcademicTerm academicTerm, StudentProjectModuleOptionality optionality) {
    Session s = getHibernateSession();

    StudentProjectModule studentProjectModule = new StudentProjectModule();
    studentProjectModule.setStudentProject(studentProject);
    studentProjectModule.setModule(module);
    studentProjectModule.setAcademicTerm(academicTerm);
    studentProjectModule.setOptionality(optionality);
    s.save(studentProjectModule);

    studentProject.addStudentProjectModule(studentProjectModule);
    s.saveOrUpdate(studentProject);

    return studentProjectModule;
  }

  public void updateStudentProjectModule(StudentProjectModule studentProjectModule,
      AcademicTerm academicTerm, StudentProjectModuleOptionality optionality) {
    Session s = getHibernateSession();

    studentProjectModule.setAcademicTerm(academicTerm);
    studentProjectModule.setOptionality(optionality);

    s.saveOrUpdate(studentProjectModule);
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

  public StudentProjectModule getStudentProjectModule(Long studentProjectModuleId) {
    Session s = getHibernateSession();
    return (StudentProjectModule) s.load(StudentProjectModule.class, studentProjectModuleId);
  }

  public void deleteProjectModule(ProjectModule projectModule) {
    Session s = getHibernateSession();
    if (projectModule.getProject() != null) {
      projectModule.getProject().removeProjectModule(projectModule);
    }
    s.delete(projectModule);
  }

  public void deleteStudentProjectModule(StudentProjectModule studentProjectModule) {
    Session s = getHibernateSession();
    if (studentProjectModule.getStudentProject() != null) {
      studentProjectModule.getStudentProject().removeStudentProjectModule(studentProjectModule);
    }
    s.delete(studentProjectModule);
  }

  @SuppressWarnings("unchecked")
  public List<ProjectModule> listProjectModules(Long projectId) {
    Session s = getHibernateSession();
    return s.createCriteria(ProjectModule.class).add(Restrictions.eq("project", getProject(projectId))).list();
  }

  @SuppressWarnings("unchecked")
  public List<StudentProjectModule> listStudentProjectModules(Long studentProjectId) {
    Session s = getHibernateSession();
    return s.createCriteria(StudentProjectModule.class).add(
        Restrictions.eq("studentProject", getStudentProject(studentProjectId))).list();
  }

}
