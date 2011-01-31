package fi.pyramus.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;

public class RemoveDeprecatedMagicKeys implements Job {

  public RemoveDeprecatedMagicKeys() {
  }
  
  public void execute(JobExecutionContext context) throws JobExecutionException {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    baseDAO.deleteDeprecatedMagicKeys();
  }

}
