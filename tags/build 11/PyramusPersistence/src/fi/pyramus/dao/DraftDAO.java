package fi.pyramus.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import fi.pyramus.domainmodel.drafts.FormDraft;
import fi.pyramus.domainmodel.users.User;

public class DraftDAO extends PyramusDAO {
  
  public FormDraft createFormDraft(User creator, String url, String draftData) {
    Session s = getHibernateSession();
    
    Date now = new Date(System.currentTimeMillis());
    
    FormDraft formDraft = new FormDraft();
    formDraft.setUrl(url);
    formDraft.setData(draftData);
    formDraft.setCreated(now);
    formDraft.setModified(now);
    formDraft.setCreator(creator);
    
    s.saveOrUpdate(formDraft);
    
    return formDraft;
  }
  
  public FormDraft getFormDraft(Long id) {
    Session s = getHibernateSession();
    return (FormDraft) s.load(FormDraft.class, id);
  }
  
  public FormDraft getFormDraftByURL(User creator, String url) {
    Session s = getHibernateSession();
    return (FormDraft) s.createCriteria(FormDraft.class)
      .add(Restrictions.eq("url", url))
      .add(Restrictions.eq("creator", creator))
      .uniqueResult();
  }
  
  @SuppressWarnings("unchecked")
  public void removeDeprecatedDrafts() {
    Session s = getHibernateSession();
    Calendar c = Calendar.getInstance();
    c.setTime(new Date());
    c.roll(Calendar.DATE, -14);
    List<FormDraft> formDrafts = s.createCriteria(FormDraft.class).add(Restrictions.lt("modified", c.getTime())).list();
    for (FormDraft formDraft : formDrafts) {
      s.delete(formDraft);
    }
  }
  
  public void updateFormDraft(FormDraft formDraft, String draftData) {
    Session s = getHibernateSession();
    
    Date now = new Date(System.currentTimeMillis());
    
    formDraft.setData(draftData);
    formDraft.setModified(now);
    
    s.saveOrUpdate(formDraft);
  }
  
  public void deleteFormDraft(FormDraft formDraft) {
    Session s = getHibernateSession();
    
    s.delete(formDraft);
  }
  
}
