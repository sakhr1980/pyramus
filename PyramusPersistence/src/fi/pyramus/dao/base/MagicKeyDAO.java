package fi.pyramus.dao.base;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.pyramus.dao.PyramusEntityDAO;
import fi.pyramus.domainmodel.base.MagicKey;
import fi.pyramus.domainmodel.base.MagicKey_;

public class MagicKeyDAO extends PyramusEntityDAO<MagicKey> {

  public MagicKey create(String name) {
    EntityManager entityManager = getEntityManager();

    Date now = new Date(System.currentTimeMillis());

    MagicKey magicKey = new MagicKey();
    magicKey.setCreated(now);
    magicKey.setName(name);

    entityManager.persist(magicKey);

    return magicKey;
  }

  public MagicKey findByName(String name) {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<MagicKey> criteria = criteriaBuilder.createQuery(MagicKey.class);
    Root<MagicKey> root = criteria.from(MagicKey.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.equal(root.get(MagicKey_.name), name)
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  public void deleteDeprecatedMagicKeys() {
    Calendar c = Calendar.getInstance();
    c.setTime(new Date());
    c.roll(Calendar.DATE, -1);

//    List<MagicKey> deprecatedMagicKeys = s.createCriteria(MagicKey.class).add(Restrictions.lt("created", c.getTime())).list();

    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<MagicKey> criteria = criteriaBuilder.createQuery(MagicKey.class);
    Root<MagicKey> root = criteria.from(MagicKey.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.lessThan(root.get(MagicKey_.created), c.getTime())
    );
    
    List<MagicKey> deprecatedMagicKeys = entityManager.createQuery(criteria).getResultList();
    
    for (MagicKey deprecatedMagicKey : deprecatedMagicKeys) {
      super.delete(deprecatedMagicKey);
    }
  }

  @Override
  public void delete(MagicKey magicKey) {
    super.delete(magicKey);
  }

}
