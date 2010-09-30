package fi.pyramus.dao;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

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

import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.domainmodel.resources.MaterialResource;
import fi.pyramus.domainmodel.resources.Resource;
import fi.pyramus.domainmodel.resources.ResourceCategory;
import fi.pyramus.domainmodel.resources.ResourceType;
import fi.pyramus.domainmodel.resources.WorkResource;
import fi.pyramus.persistence.search.SearchResult;
import fi.pyramus.persistence.usertypes.MonetaryAmount;

/**
 * The Data Access Object for resource related operations.  
 */
public class ResourceDAO extends PyramusDAO {

  /* Resource */
  
  public Resource findResourceById(Long resourceId) {
    Session s = getHibernateSession();
    return (Resource) s.load(Resource.class, resourceId);
  }

  @SuppressWarnings("unchecked")
  public List<Resource> listResources() {
    Session s = getHibernateSession();
    return s.createCriteria(Resource.class).add(Restrictions.eq("archived", Boolean.FALSE)).list();
  }
  
  @SuppressWarnings("unchecked")
  public SearchResult<Resource> searchResourcesBasic(int resultsPerPage, int page, String queryText) {
    int firstResult = page * resultsPerPage;

    StringBuilder queryBuilder = new StringBuilder();

    if (!StringUtils.isBlank(queryText)) {
      queryBuilder.append("+(");
      addTokenizedSearchCriteria(queryBuilder, "name", queryText, false, true);
      addTokenizedSearchCriteria(queryBuilder, "tags.text", queryText, false, true);
      queryBuilder.append(")");
    }

    FullTextSession fullTextSession = Search.getFullTextSession(getHibernateSession());

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

      FullTextQuery query = fullTextSession.createFullTextQuery(luceneQuery, WorkResource.class, MaterialResource.class)
        .setSort(new Sort(new SortField[]{SortField.FIELD_SCORE, new SortField("nameSortable", SortField.STRING)}))
        .setFirstResult(firstResult)
        .setMaxResults(resultsPerPage);
    
      query.setFirstResult(firstResult).setMaxResults(resultsPerPage);

      query.enableFullTextFilter("ArchivedResource").setParameter("archived", Boolean.FALSE);

      int hits = query.getResultSize();
      int pages = hits / resultsPerPage;
      if (hits % resultsPerPage > 0) {
        pages++;
      }

      int lastResult = firstResult + resultsPerPage - 1;
      if (lastResult > hits - 1) {
        lastResult = hits - 1;
      }

      return new SearchResult<Resource>(page, pages, hits, firstResult, lastResult, query.list());
    } catch (ParseException e) {
      throw new PersistenceException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public SearchResult<Resource> searchResources(int resultsPerPage, int page, String name, String tags, ResourceType resourceType,
      ResourceCategory resourceCategory, boolean filterArchived) {
    int firstResult = page * resultsPerPage;

    StringBuilder queryBuilder = new StringBuilder();

    if (!StringUtils.isBlank(name)) {
      addTokenizedSearchCriteria(queryBuilder, "name", name, true, true);
    }

    if (!StringUtils.isBlank(tags)) {
      addTokenizedSearchCriteria(queryBuilder, "tags.text", tags, true, true);
    }
    
    if (resourceCategory != null) {
      addTokenizedSearchCriteria(queryBuilder, "category.id", resourceCategory.getId().toString(), true, true);
    }

    FullTextSession fullTextSession = Search.getFullTextSession(getHibernateSession());

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

      FullTextQuery query = null;

      if (resourceType == null) {
        query = fullTextSession.createFullTextQuery(luceneQuery, WorkResource.class, MaterialResource.class)
            .setSort(new Sort(new SortField[]{SortField.FIELD_SCORE, new SortField("nameSortable", SortField.STRING)}))
            .setFirstResult(firstResult)
            .setMaxResults(resultsPerPage);
      }
      else {
        switch (resourceType) {
        case MATERIAL_RESOURCE:
          query = fullTextSession.createFullTextQuery(luceneQuery, MaterialResource.class)
              .setSort(new Sort(new SortField[]{SortField.FIELD_SCORE, new SortField("nameSortable", SortField.STRING)}))
              .setFirstResult(firstResult)
              .setMaxResults(resultsPerPage);
          break;
        case WORK_RESOURCE:
          query = fullTextSession.createFullTextQuery(luceneQuery, WorkResource.class)
              .setSort(new Sort(new SortField[]{SortField.FIELD_SCORE, new SortField("nameSortable", SortField.STRING)}))
              .setFirstResult(firstResult)
              .setMaxResults(resultsPerPage);
          break;
        }
      }

      query.setFirstResult(firstResult).setMaxResults(resultsPerPage);

      if (filterArchived) {
        query.enableFullTextFilter("ArchivedResource").setParameter("archived", Boolean.FALSE);
      }

      int hits = query.getResultSize();
      int pages = hits / resultsPerPage;
      if (hits % resultsPerPage > 0) {
        pages++;
      }

      int lastResult = firstResult + resultsPerPage - 1;
      if (lastResult > hits - 1) {
        lastResult = hits - 1;
      }

      return new SearchResult<Resource>(page, pages, hits, firstResult, lastResult, query.list());
    } catch (ParseException e) {
      throw new PersistenceException(e);
    }
  }
  
  public void setResourceTags(Resource resource, Set<Tag> tags) {
    resource.setTags(tags);
  }

  public void archiveResource(Resource resource) {
    Session s = getHibernateSession();
    resource.setArchived(Boolean.TRUE);
    s.saveOrUpdate(resource);
  }

  public void deleteResource(Resource resource) {
    Session s = getHibernateSession();
    s.delete(resource);
  }
  
  /* ResourceCategory */

  public ResourceCategory findResourceCategoryById(Long resourceCategoryId) {
    Session s = getHibernateSession();
    return (ResourceCategory) s.load(ResourceCategory.class, resourceCategoryId);
  }

  @SuppressWarnings("unchecked")
  public List<ResourceCategory> listResourceCategories() {
    Session s = getHibernateSession();

    List<ResourceCategory> resourceCategories = s.createCriteria(ResourceCategory.class).add(
        Restrictions.eq("archived", Boolean.FALSE)).list();

    Collections.sort(resourceCategories, new Comparator<ResourceCategory>() {
      public int compare(ResourceCategory o1, ResourceCategory o2) {
        return o1.getName() == null ? -1 : o2.getName() == null ? 1 : o1.getName().compareTo(o2.getName());
      }
    });

    return resourceCategories;
  }

  public ResourceCategory createResourceCategory(String name) {
    Session s = getHibernateSession();
    ResourceCategory resourceCategory = new ResourceCategory();
    resourceCategory.setName(name);
    s.save(resourceCategory);

    return resourceCategory;
  }

  public void updateResourceCategory(ResourceCategory resourceCategory, String name) {
    Session s = getHibernateSession();
    resourceCategory.setName(name);
    s.saveOrUpdate(resourceCategory);
  }

  public void archiveResourceCategory(ResourceCategory resourceCategory) {
    Session s = getHibernateSession();
    resourceCategory.setArchived(Boolean.TRUE);
    s.saveOrUpdate(resourceCategory);
  }

  public void deleteResourceCategory(ResourceCategory resourceCategory) {
    Session s = getHibernateSession();
    s.delete(resourceCategory);
  }
  
  /* MaterialResource */

  public MaterialResource findMaterialResourceById(Long resourceId) {
    Session s = getHibernateSession();
    return (MaterialResource) s.load(MaterialResource.class, resourceId);
  }

  @SuppressWarnings("unchecked")
  public List<MaterialResource> listMaterialResources() {
    Session s = getHibernateSession();
    return s.createCriteria(WorkResource.class).list();
  }
  
  public MaterialResource createMaterialResource(String name, ResourceCategory category, Double unitCost) {
    Session s = getHibernateSession();

    MaterialResource materialResource = new MaterialResource();
    materialResource.setName(name);
    materialResource.setUnitCost(unitCost == null ? null : new MonetaryAmount(unitCost));
    materialResource.setCategory(category);
    s.save(materialResource);

    return materialResource;
  }

  public MaterialResource updateMaterialResource(MaterialResource materialResource, String name,
      ResourceCategory category, Double unitCost) {
    Session s = getHibernateSession();

    materialResource.setName(name);
    materialResource.setCategory(category);

    if (unitCost != null)
      materialResource.setUnitCost(new MonetaryAmount(unitCost));

    s.saveOrUpdate(materialResource);

    return materialResource;
  }
  
  /* WorkResource */

  public WorkResource findWorkResourceById(Long resourceId) {
    Session s = getHibernateSession();
    return (WorkResource) s.load(WorkResource.class, resourceId);
  }

  @SuppressWarnings("unchecked")
  public List<WorkResource> listWorkResources() {
    Session s = getHibernateSession();
    return s.createCriteria(MaterialResource.class).list();
  }

  public WorkResource createWorkResource(String name, ResourceCategory category, Double costPerUse, Double hourlyCost) {
    Session s = getHibernateSession();

    WorkResource workResource = new WorkResource();
    workResource.setName(name);
    workResource.setCostPerUse(costPerUse == null ? null : new MonetaryAmount(costPerUse));
    workResource.setHourlyCost(hourlyCost == null ? null : new MonetaryAmount(hourlyCost));
    workResource.setCategory(category);
    s.save(workResource);

    return workResource;
  }

  public WorkResource updateWorkResource(WorkResource workResource, String name, ResourceCategory category,
      Double costPerUse, Double hourlyCost) {
    Session s = getHibernateSession();

    workResource.setName(name);
    workResource.setCategory(category);

    if (costPerUse != null)
      workResource.setCostPerUse(new MonetaryAmount(costPerUse));

    if (hourlyCost != null)
      workResource.setHourlyCost(new MonetaryAmount(hourlyCost));

    s.saveOrUpdate(workResource);

    return workResource;
  }
}
