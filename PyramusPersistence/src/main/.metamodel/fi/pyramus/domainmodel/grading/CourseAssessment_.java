package fi.pyramus.domainmodel.grading;

import fi.pyramus.domainmodel.courses.CourseStudent;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(CourseAssessment.class)
public abstract class CourseAssessment_ extends fi.pyramus.domainmodel.grading.Credit_ {

	public static volatile SingularAttribute<CourseAssessment, CourseStudent> courseStudent;

}

