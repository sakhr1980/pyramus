package fi.pyramus.domainmodel.courses;

import fi.pyramus.domainmodel.users.User;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(CourseUser.class)
public abstract class CourseUser_ {

	public static volatile SingularAttribute<CourseUser, Course> course;
	public static volatile SingularAttribute<CourseUser, Long> id;
	public static volatile SingularAttribute<CourseUser, CourseUserRole> role;
	public static volatile SingularAttribute<CourseUser, User> user;
	public static volatile SingularAttribute<CourseUser, Long> version;

}

