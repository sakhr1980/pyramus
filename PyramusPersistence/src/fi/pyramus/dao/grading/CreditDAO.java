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
//    return s.createCriteria(Credit.class)
//      .add(Restrictions.eq("student", student))
//      .add(Restrictions.eq("archived", Boolean.FALSE)).list();
//  }
  
}
