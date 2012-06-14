package fi.pyramus.domainmodel.courses;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(CourseEnrolmentType.class)
public abstract class CourseEnrolmentType_ {

	public static volatile SingularAttribute<CourseEnrolmentType, Long> id;
	public static volatile SingularAttribute<CourseEnrolmentType, String> name;
	public static volatile SingularAttribute<CourseEnrolmentType, Long> version;

}

