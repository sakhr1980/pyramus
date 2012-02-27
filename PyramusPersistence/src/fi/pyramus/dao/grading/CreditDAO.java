package fi.pyramus.dao.grading;

import javax.ejb.Stateless;

import fi.pyramus.dao.PyramusEntityDAO;
import fi.pyramus.domainmodel.grading.Credit;

@Stateless
public class CreditDAO extends PyramusEntityDAO<Credit> {
    
//  /**
//   * Lists all student's credits excluding archived ones
//   * 
//   * @return list of all students credits
//   */
//  @SuppressWarnings("unchecked")
//  public List<Credit> listCreditsByStudent(Student student) {
//    EntityManager entityManager = getEntityManager();
//
//    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//    CriteriaQuery<Credit> criteria = criteriaBuilder.createQuery(Credit.class);
//    Root<Credit> root = criteria.from(Credit.class);
//    criteria.select(root);
//    criteria.where(
//        criteriaBuilder.and(
//            criteriaBuilder.equal(root.get(Credit_.archived), Boolean.FALSE),
//            criteriaBuilder.equal(root.get(Credit_.student), student)
//        ));
//    
//    return entityManager.createQuery(criteria).getResultList();
//  }
  
}
