package fi.pyramus.jobs;

import javax.ejb.Schedule;
import javax.ejb.Stateless;

import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.drafts.DraftDAO;

@Stateless
public class RemoveDeprecatedDrafts {

  @Schedule(second = "0", minute = "0/5", hour = "*", persistent = false)
  public void removeDeprecatedDrafts(){
    DraftDAO draftDAO = DAOFactory.getInstance().getDraftDAO();
    draftDAO.removeDeprecatedDrafts();
  }
  
}
