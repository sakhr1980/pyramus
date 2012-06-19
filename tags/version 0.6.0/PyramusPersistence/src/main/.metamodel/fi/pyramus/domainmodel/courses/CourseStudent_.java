package fi.pyramus.domainmodel.courses;

import fi.pyramus.domainmodel.base.BillingDetails;
import fi.pyramus.domainmodel.base.CourseOptionality;
import fi.pyramus.domainmodel.students.Student;
import java.util.Date;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(CourseStudent.class)
public abstract class CourseStudent_ {

	public static volatile SingularAttribute<CourseStudent, Course> course;
	public static volatile SingularAttribute<CourseStudent, Long> id;
	public static volatile SingularAttribute<CourseStudent, CourseParticipationType> participationType;
	public static volatile SingularAttribute<CourseStudent, Boolean> archived;
	public static volatile SingularAttribute<CourseStudent, Student> student;
	public static volatile SingularAttribute<CourseStudent, CourseOptionality> optionality;
	public static volatile SingularAttribute<CourseStudent, CourseEnrolmentType> courseEnrolmentType;
	public static volatile SingularAttribute<CourseStudent, Date> enrolmentTime;
	public static volatile SingularAttribute<CourseStudent, BillingDetails> billingDetails;
	public static volatile SingularAttribute<CourseStudent, Boolean> lodging;
	public static volatile SingularAttribute<CourseStudent, Long> version;

}

