package fi.pyramus.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.drafts.DraftDAO;

public class RemoveDeprecatedDrafts implements Job {

  public RemoveDeprecatedDrafts() {
  }
  
  public void execute(JobExecutionContext context) throws JobExecutionException {
    DraftDAO draftDAO = DAOFactory.getInstance().getDraftDAO();
    draftDAO.removeDeprecatedDrafts();
  }
  
}
