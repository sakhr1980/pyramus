package fi.pyramus.domainmodel.courses;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(CourseUserRole.class)
public abstract class CourseUserRole_ {

	public static volatile SingularAttribute<CourseUserRole, Long> id;
	public static volatile SingularAttribute<CourseUserRole, String> name;
	public static volatile SingularAttribute<CourseUserRole, Long> version;

}

