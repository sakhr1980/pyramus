package fi.pyramus.domainmodel.grading;

import fi.pyramus.domainmodel.courses.CourseStudent;
import java.util.Date;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(CourseAssessmentRequest.class)
public abstract class CourseAssessmentRequest_ {

	public static volatile SingularAttribute<CourseAssessmentRequest, Long> id;
	public static volatile SingularAttribute<CourseAssessmentRequest, Boolean> archived;
	public static volatile SingularAttribute<CourseAssessmentRequest, String> requestText;
	public static volatile SingularAttribute<CourseAssessmentRequest, Date> created;
	public static volatile SingularAttribute<CourseAssessmentRequest, CourseStudent> courseStudent;

}

