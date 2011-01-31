package fi.pyramus.services.entities.grading;

import fi.pyramus.domainmodel.grading.CourseAssessment;
import fi.pyramus.services.entities.EntityFactory;

public class CourseAssessmentEntityFactory implements EntityFactory<CourseAssessmentEntity> {

  public CourseAssessmentEntity buildFromDomainObject(Object domainObject) {
    if (domainObject == null)
      return null;
    
    CourseAssessment courseAssessment = (CourseAssessment) domainObject;

    return new CourseAssessmentEntity(courseAssessment.getId(), courseAssessment.getStudent().getId(), courseAssessment.getDate(), 
        courseAssessment.getGrade().getId(), courseAssessment.getGrade().getGradingScale().getId(), courseAssessment.getVerbalAssessment(), 
        courseAssessment.getAssessingUser().getId(), courseAssessment.getArchived(), courseAssessment.getCourseStudent().getCourse().getId(), 
        courseAssessment.getCourseStudent().getId());
  }

}
