package fi.pyramus.jobs;

import javax.ejb.Schedule;
import javax.ejb.Stateless;

import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.base.MagicKeyDAO;

@Stateless
public class RemoveDeprecatedMagicKeys {

  @Schedule(second = "0", minute = "0/5", hour = "*", persistent = false)
  public void removeDeprecatedMagicKeys() {
    MagicKeyDAO magicKeyDAO = DAOFactory.getInstance().getMagicKeyDAO();
    magicKeyDAO.deleteDeprecatedMagicKeys();
  }

}
