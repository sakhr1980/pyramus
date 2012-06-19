package fi.pyramus.domainmodel.base;

import fi.pyramus.domainmodel.courses.CourseEnrolmentType;
import fi.pyramus.domainmodel.courses.CourseParticipationType;
import fi.pyramus.domainmodel.courses.CourseState;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Defaults.class)
public abstract class Defaults_ {

	public static volatile SingularAttribute<Defaults, Long> id;
	public static volatile SingularAttribute<Defaults, CourseParticipationType> initialCourseParticipationType;
	public static volatile SingularAttribute<Defaults, CourseEnrolmentType> initialCourseEnrolmentType;
	public static volatile SingularAttribute<Defaults, EducationalTimeUnit> baseTimeUnit;
	public static volatile SingularAttribute<Defaults, CourseState> initialCourseState;
	public static volatile SingularAttribute<Defaults, Long> version;

}

