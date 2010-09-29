package fi.pyramus.dao;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import fi.pyramus.domainmodel.help.HelpFolder;
import fi.pyramus.domainmodel.help.HelpItem;
import fi.pyramus.domainmodel.help.HelpPage;
import fi.pyramus.domainmodel.help.HelpPageContent;
import fi.pyramus.domainmodel.help.HelpItemTitle;
import fi.pyramus.domainmodel.users.User;

public class HelpDAO extends PyramusDAO {

  /* HelpItem */

  public HelpItem findHelpItemById(Long id) {
    EntityManager entityManager = getEntityManager();
    return entityManager.find(HelpItem.class, id);
  }
  
  @SuppressWarnings("unchecked")
  public List<HelpItem> listHelpItemsByParent(HelpFolder parent) {
    Session session = getHibernateSession();
    
    if (parent == null) {
      return session.createCriteria(HelpItem.class)
        .add(Restrictions.isNull("parent"))
        .addOrder(Order.asc("indexColumn"))
        .list();
    } else {
      return session.createCriteria(HelpItem.class)
        .add(Restrictions.eq("parent", parent))
        .addOrder(Order.asc("indexColumn"))
        .list();
      
    }
  }
  
  public void updateHelpItemParent(HelpItem helpItem, HelpFolder parent) {
    EntityManager entityManager = getEntityManager();
    
    if (helpItem.getParent() != null) {
      HelpFolder oldParent = helpItem.getParent();
      oldParent.removeChild(helpItem);
      entityManager.persist(oldParent);
    } 

    helpItem.setParent(parent);
    
    entityManager.persist(helpItem);
  }
  
  public void deleteHelpItem(HelpItem helpItem) {
    EntityManager entityManager = getEntityManager();
    
    HelpItemTitle[] titles = helpItem.getTitles().toArray(new HelpItemTitle[0]);
    for (int i = 0, l = titles.length; i < l; i++) {
      deleteHelpItemTitle(titles[i]);
    }
    
    entityManager.remove(helpItem);
  }
  
  /* HelpFolder */

  public HelpFolder findHelpFolderById(Long id) {
    EntityManager entityManager = getEntityManager();
    return entityManager.find(HelpFolder.class, id);
  }
  
  public HelpFolder createHelpFolder(HelpFolder parent, Integer indexColum, User creatingUser) {
    EntityManager entityManager = getEntityManager();
    
    Date now = new Date(System.currentTimeMillis());
    
    HelpFolder helpFolder = new HelpFolder();
    helpFolder.setCreated(now);
    helpFolder.setLastModified(now);
    helpFolder.setCreator(creatingUser);
    helpFolder.setLastModifier(creatingUser);
    helpFolder.setParent(parent);
    helpFolder.setIndexColumn(indexColum);
    
    entityManager.persist(helpFolder);
    
    return helpFolder;
  }
  
  public void deleteHelpFolder(HelpFolder helpFolder) {
    EntityManager entityManager = getEntityManager();
    
    if (helpFolder.getParent() != null) {
      HelpFolder parentFolder = helpFolder.getParent();
      parentFolder.removeChild(helpFolder);
      entityManager.persist(parentFolder);
    }
    
    entityManager.remove(helpFolder);
  }
  
  /* HelpPage */

  public HelpPage findHelpPageById(Long id) {
    EntityManager entityManager = getEntityManager();
    return entityManager.find(HelpPage.class, id);
  }
   
  public HelpPage createHelpPage(HelpFolder parent, Integer indexColumn, User creatingUser) {
    EntityManager entityManager = getEntityManager();
    
    Date now = new Date(System.currentTimeMillis());
    
    HelpPage helpPage = new HelpPage();
    helpPage.setCreated(now);
    helpPage.setLastModified(now);
    helpPage.setCreator(creatingUser);
    helpPage.setLastModifier(creatingUser);
    helpPage.setParent(parent);
    helpPage.setIndexColumn(indexColumn);
    
    entityManager.persist(helpPage);
    
    return helpPage;
  }
  
  public void deleteHelpPage(HelpPage helpPage) {
    HelpPageContent[] contents = helpPage.getContents().toArray(new HelpPageContent[0]);
    for (int i = 0, l = contents.length; i < l; i++) {
      deleteHelpPageContent(contents[i]);
    }
    
    deleteHelpItem(helpPage);
  }
  
  /* HelpItemTitle*/

  public HelpItemTitle findHelpItemTitleById(Long id) {
    EntityManager entityManager = getEntityManager();
    return entityManager.find(HelpItemTitle.class, id);
  }
  
  public HelpItemTitle findHelpItemTitleByItemAndLocale(HelpItem item, Locale locale) {
    Session session = getHibernateSession();
    
    return (HelpItemTitle) session.createCriteria(HelpItemTitle.class)
      .add(Restrictions.eq("item", item))
      .add(Restrictions.eq("locale", locale))
      .uniqueResult();
  }
  
  public HelpItemTitle createHelpItemTitle(HelpItem item, Locale locale, String title, User creatingUser) {
    EntityManager entityManager = getEntityManager();
    
    Date now = new Date(System.currentTimeMillis());
    
    HelpItemTitle helpItemTitle = new HelpItemTitle();
    helpItemTitle.setCreated(now);
    helpItemTitle.setLastModified(now);
    helpItemTitle.setCreator(creatingUser);
    helpItemTitle.setLastModifier(creatingUser);
    helpItemTitle.setLocale(locale);
    helpItemTitle.setTitle(title);
    
    entityManager.persist(helpItemTitle);
    
    item.addTitle(helpItemTitle);
    
    entityManager.persist(item);
    
    return helpItemTitle;
  }
  
  public void updateHelpItemTitle(HelpItemTitle helpItemTitle, String title, User updatingUser) {
    EntityManager entityManager = getEntityManager();
    
    Date now = new Date(System.currentTimeMillis());
    
    helpItemTitle.setLastModified(now);
    helpItemTitle.setLastModifier(updatingUser);
    helpItemTitle.setTitle(title);
    
    entityManager.persist(helpItemTitle);
  }
  
  public void deleteHelpItemTitle(HelpItemTitle helpItemTitle) {
    EntityManager entityManager = getEntityManager();
    
    if (helpItemTitle.getItem() != null) {
      HelpItem helpItem = helpItemTitle.getItem();
      helpItem.removeTitle(helpItemTitle);
      entityManager.persist(helpItem);
    }
      
    entityManager.remove(helpItemTitle);
  }
  
  /* HelpPageContent */

  public HelpPageContent findHelpPageContentById(Long id) {
    EntityManager entityManager = getEntityManager();
    return entityManager.find(HelpPageContent.class, id);
  }
  
  public HelpPageContent findHelpPageContentByPageAndLocale(HelpPage page, Locale locale) {
    Session session = getHibernateSession();
    
    return (HelpPageContent) session.createCriteria(HelpPageContent.class)
      .add(Restrictions.eq("page", page))
      .add(Restrictions.eq("locale", locale))
      .uniqueResult();
  }
  
  public HelpPageContent createHelpPageContent(HelpPage page, Locale locale, String content, User creatingUser) {
    EntityManager entityManager = getEntityManager();
    
    Date now = new Date(System.currentTimeMillis());
    
    HelpPageContent helpPageContent = new HelpPageContent();
    helpPageContent.setCreated(now);
    helpPageContent.setLastModified(now);
    helpPageContent.setCreator(creatingUser);
    helpPageContent.setLastModifier(creatingUser);
    helpPageContent.setLocale(locale);
    helpPageContent.setContent(content);
    
    entityManager.persist(helpPageContent);
    
    page.addContent(helpPageContent);
    
    entityManager.persist(page);
    
    return helpPageContent;
  }
  
  public void updateHelpPageContent(HelpPageContent helpPageContent, String content, User updatingUser) {
    EntityManager entityManager = getEntityManager();
    
    Date now = new Date(System.currentTimeMillis());
    
    helpPageContent.setLastModified(now);
    helpPageContent.setLastModifier(updatingUser);
    helpPageContent.setContent(content);
    
    entityManager.persist(helpPageContent);
  }
  
  public void deleteHelpPageContent(HelpPageContent helpPageContent) {
    EntityManager entityManager = getEntityManager();
    
    if (helpPageContent.getPage() != null) {
      HelpPage helpPage = helpPageContent.getPage();
      helpPage.removeContent(helpPageContent);
      entityManager.persist(helpPage);
    }
      
    entityManager.remove(helpPageContent);
  }
}
