package fi.pyramus.dao.students;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.pyramus.dao.PyramusEntityDAO;
import fi.pyramus.domainmodel.students.StudentStudyEndReason;
import fi.pyramus.domainmodel.students.StudentStudyEndReason_;

public class StudentStudyEndReasonDAO extends PyramusEntityDAO<StudentStudyEndReason> {

  public List<StudentStudyEndReason> listTopLevelStudentStudyEndReasons() {
    EntityManager entityManager = getEntityManager(); 
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<StudentStudyEndReason> criteria = criteriaBuilder.createQuery(StudentStudyEndReason.class);
    Root<StudentStudyEndReason> root = criteria.from(StudentStudyEndReason.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.isNull(root.get(StudentStudyEndReason_.parentReason))
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
}
