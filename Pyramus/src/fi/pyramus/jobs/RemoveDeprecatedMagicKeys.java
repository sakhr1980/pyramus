package fi.pyramus.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.base.MagicKeyDAO;

public class RemoveDeprecatedMagicKeys implements Job {

  public RemoveDeprecatedMagicKeys() {
  }
  
  public void execute(JobExecutionContext context) throws JobExecutionException {
    MagicKeyDAO magicKeyDAO = DAOFactory.getInstance().getMagicKeyDAO();
    magicKeyDAO.deleteDeprecatedMagicKeys();
  }

}
