package fi.pyramus.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
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
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;

import fi.pyramus.domainmodel.base.CourseEducationType;
import fi.pyramus.domainmodel.base.EducationType;
import fi.pyramus.domainmodel.base.EducationalTimeUnit;
import fi.pyramus.domainmodel.base.Subject;
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.domainmodel.modules.Module;
import fi.pyramus.domainmodel.modules.ModuleComponent;
import fi.pyramus.domainmodel.projects.Project;
import fi.pyramus.domainmodel.projects.ProjectModule;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.persistence.search.SearchResult;

/**
 * The Data Access Object for module related operations.  
 */
public class ModuleDAO extends PyramusDAO {

  public Module createModule(String name, Subject subject, Integer courseNumber, Double moduleLength, EducationalTimeUnit moduleLengthTimeUnit, String description,
      User creatingUser) {
    Session s = getHibernateSession();

    Date now = new Date(System.currentTimeMillis());

    Module module = new Module();
    module.setName(name);
    module.setDescription(description);
    module.setSubject(subject);
    module.setCourseNumber(courseNumber);
    module.getCourseLength().setUnit(moduleLengthTimeUnit);
    module.getCourseLength().setUnits(moduleLength);

    module.setCreator(creatingUser);
    module.setCreated(now);
    module.setLastModifier(creatingUser);
    module.setLastModified(now);

    s.saveOrUpdate(module);

    return module;
  }
  
  public Module setModuleTags(Module module, Set<Tag> tags) {
    EntityManager entityManager = getEntityManager();
    
    module.setTags(tags);
    
    entityManager.persist(module);
    
    return module;
  }

  public void updateModule(Module module, String name, Subject subject, Integer courseNumber, Double length, EducationalTimeUnit lengthTimeUnit, String description, User user) {
    Session s = getHibernateSession();
    Date now = new Date(System.currentTimeMillis());
    
    module.setName(name);
    module.setDescription(description);
    module.setSubject(subject);
    module.setCourseNumber(courseNumber);
    module.getCourseLength().setUnit(lengthTimeUnit);
    module.getCourseLength().setUnits(length);
    module.setLastModifier(user);
    module.setLastModified(now);
    s.saveOrUpdate(module);
  }

  public Module getModule(Long moduleId) {
    Session s = getHibernateSession();
    return (Module) s.load(Module.class, moduleId);
  }

  public void deleteModule(Module module) {
    Session s = getHibernateSession();
    s.delete(module);
  }

  /**
   * Archives a module.
   * 
   * @param module The module to be archived
   */
  public void archiveModule(Module module) {
    Session s = getHibernateSession();
    module.setArchived(Boolean.TRUE);
    s.saveOrUpdate(module);
  }

  /**
   * Archives a module component.
   * 
   * @param moduleComponent The module component to be archived
   */
  public void archiveModuleComponent(ModuleComponent moduleComponent) {
    Session s = getHibernateSession();
    moduleComponent.setArchived(Boolean.TRUE);
    s.saveOrUpdate(moduleComponent);
  }

  public ModuleComponent getModuleComponent(Long moduleComponentId) {
    Session s = getHibernateSession();
    return (ModuleComponent) s.load(ModuleComponent.class, moduleComponentId);
  }
  
  @SuppressWarnings("unchecked")
  public SearchResult<Module> searchModulesBasic(int resultsPerPage, int page, String text) {
    int firstResult = page * resultsPerPage;
    StringBuilder queryBuilder = new StringBuilder();
    
    if (!StringUtils.isBlank(text)) {
      queryBuilder.append("+(");
      addTokenizedSearchCriteria(queryBuilder, "name", text, false);
      addTokenizedSearchCriteria(queryBuilder, "tags.text", text, false);
      addTokenizedSearchCriteria(queryBuilder, "description", text, false);
      addTokenizedSearchCriteria(queryBuilder, "moduleComponents.name", text, false);
      addTokenizedSearchCriteria(queryBuilder, "moduleComponents.description", text, false);
      queryBuilder.append(")");
    }

    Session s = getHibernateSession();
    FullTextSession fullTextSession = Search.getFullTextSession(s);

    QueryParser parser = new QueryParser(Version.LUCENE_29, "", new StandardAnalyzer(Version.LUCENE_29));
    String queryString = queryBuilder.toString();
    Query luceneQuery;
    
    try {
      if (StringUtils.isBlank(queryString)) {
        luceneQuery = new MatchAllDocsQuery();
      } else {
        luceneQuery = parser.parse(queryString);
      }
  
      FullTextQuery query = fullTextSession.createFullTextQuery(luceneQuery, Module.class)
          .setSort(new Sort(new SortField[]{SortField.FIELD_SCORE, new SortField("nameSortable", SortField.STRING)}))
          .setFirstResult(firstResult)
          .setMaxResults(resultsPerPage);
  
      query.enableFullTextFilter("ArchivedModule").setParameter("archived", Boolean.FALSE);
    
      int hits = query.getResultSize();
      int pages = hits / resultsPerPage;
      if (hits % resultsPerPage > 0) {
        pages++;
      }
  
      int lastResult = Math.min(firstResult + resultsPerPage, hits) - 1;
  
      return new SearchResult<Module>(page, pages, hits, firstResult, lastResult, query.list());
    } catch (ParseException e) {
      throw new PersistenceException(e);
    }
  }
 
  @SuppressWarnings("unchecked")
  public SearchResult<Module> searchModules(int resultsPerPage, int page, String projectName, String name, String tags, String description, String componentName, String componentDescription, Long ownerId, boolean filterArchived) {
    int firstResult = page * resultsPerPage;

    StringBuilder queryBuilder = new StringBuilder();
    
    boolean hasName = !StringUtils.isBlank(name);
    boolean hasTags = !StringUtils.isBlank(tags);
    boolean hasDescription = !StringUtils.isBlank(description);
    boolean hasComponentName = !StringUtils.isBlank(componentName);
    boolean hasComponentDescription = !StringUtils.isBlank(componentDescription);
    
    if (hasName||hasTags||hasDescription||hasComponentName||hasComponentDescription) {
      queryBuilder.append("+(");
      
      if (hasName)
        addTokenizedSearchCriteria(queryBuilder, "name", name, false);
      
      if (hasTags)
        addTokenizedSearchCriteria(queryBuilder, "tags.text", tags, false);
      
      if (hasDescription)
        addTokenizedSearchCriteria(queryBuilder, "description", description, false);
      
      if (hasComponentName)
        addTokenizedSearchCriteria(queryBuilder, "moduleComponents.name", componentName, false);
      
      if (hasComponentDescription)
        addTokenizedSearchCriteria(queryBuilder, "moduleComponents.description", componentDescription, false);
      
      
      queryBuilder.append(")");
    }
    
    // If project text is given, only include modules that are in project(s) having the given name
    // (only the first ten matching projects, though, to prevent the search from becoming too gigantic...) 
    
    Set<Long> moduleIds = new HashSet<Long>();
    if (!StringUtils.isBlank(projectName)) {
      ProjectDAO projectDAO = new ProjectDAO();
      SearchResult<Project> searchResults = projectDAO.searchProjectsBasic(10, 0, projectName);
      List<Project> projects = searchResults.getResults();
      for(Project project : projects) {
        List<ProjectModule> projectModules = project.getProjectModules();
        for (ProjectModule projectModule : projectModules) {
          moduleIds.add(projectModule.getModule().getId());
        }
      }
      if (!moduleIds.isEmpty()) {
        queryBuilder.append(" +(");
        for (Long moduleId : moduleIds) {
          queryBuilder.append(" id: " + moduleId);
        }
        queryBuilder.append(")");
      }
      else {
        // Search condition by project name didn't yield any projects, so there cannot be any results
        return new SearchResult<Module>(0, 0, 0, 0, 0, new ArrayList<Module>());
      }
    }
    
    Session s = getHibernateSession();
    FullTextSession fullTextSession = Search.getFullTextSession(s);

    try {
      if (ownerId != null && ownerId > 0) {
        queryBuilder.append(" +creator.id: ").append(ownerId);
      }

      QueryParser parser = new QueryParser(Version.LUCENE_29, "", new StandardAnalyzer(Version.LUCENE_29));
      String queryString = queryBuilder.toString();
      Query luceneQuery;

      if (StringUtils.isBlank(queryString)) {
        luceneQuery = new MatchAllDocsQuery();
      }
      else {
        luceneQuery = parser.parse(queryString);
      }

      FullTextQuery query = fullTextSession.createFullTextQuery(luceneQuery, Module.class)
          .setSort(new Sort(new SortField[]{SortField.FIELD_SCORE, new SortField("nameSortable", SortField.STRING)}))
          .setFirstResult(firstResult)
          .setMaxResults(resultsPerPage);

      if (filterArchived)
        query.enableFullTextFilter("ArchivedModule").setParameter("archived", Boolean.FALSE);

      int hits = query.getResultSize();
      int pages = hits / resultsPerPage;
      if (hits % resultsPerPage > 0) {
        pages++;
      }

      int lastResult = Math.min(firstResult + resultsPerPage, hits) - 1;

      return new SearchResult<Module>(page, pages, hits, firstResult, lastResult, query.list());

    } catch (ParseException e) {
      throw new PersistenceException(e);
    }
  }

  public ModuleComponent createModuleComponent(Module module, Double length, EducationalTimeUnit lengthTimeUnit, String name, String description) {
    Session s = getHibernateSession();

    ModuleComponent moduleComponent = new ModuleComponent();
    moduleComponent.getLength().setUnit(lengthTimeUnit);
    moduleComponent.getLength().setUnits(length);
    moduleComponent.setName(name);
    moduleComponent.setDescription(description);

    s.save(moduleComponent);

    module.addModuleComponent(moduleComponent);

    s.saveOrUpdate(module);

    return moduleComponent;
  }

  public ModuleComponent updateModuleComponent(ModuleComponent moduleComponent, Double length, EducationalTimeUnit lengthTimeUnit, String name,
      String description) {
    Session s = getHibernateSession();

    moduleComponent.setName(name);
    moduleComponent.getLength().setUnit(lengthTimeUnit);
    moduleComponent.getLength().setUnits(length);
    moduleComponent.setDescription(description);

    s.saveOrUpdate(moduleComponent);

    return moduleComponent;
  }

  @SuppressWarnings("unchecked")
  public List<ModuleComponent> listModuleComponents(Module module) {
    Session s = getHibernateSession();
    return s.createCriteria(ModuleComponent.class).add(Restrictions.eq("module", module)).add(
        Restrictions.eq("archived", Boolean.FALSE)).list();
  }

  @SuppressWarnings("unchecked")
  public List<Module> listModules() {
    Session s = getHibernateSession();
    return s.createCriteria(Module.class).add(Restrictions.eq("archived", Boolean.FALSE)).list();
  }

  @SuppressWarnings("unchecked")
  public List<Module> listModulesByEducationType(EducationType educationType) {
    Session s = getHibernateSession();
    
    return s.createCriteria(CourseEducationType.class) 
      .add(Restrictions.in("courseBase.id", s.createCriteria(Module.class).setProjection(Projections.id()).list()))
      .add(Restrictions.eq("educationType", educationType))
      .setProjection(Projections.property("courseBase"))
      .list();
  }

  public void deleteModuleComponent(ModuleComponent moduleComponent) {
    Session s = getHibernateSession();
    if (moduleComponent.getModule() != null)
      moduleComponent.getModule().removeModuleComponent(moduleComponent);
    s.delete(moduleComponent);
  }
}
