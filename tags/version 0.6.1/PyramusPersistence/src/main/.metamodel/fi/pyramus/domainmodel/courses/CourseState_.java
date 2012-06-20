package fi.pyramus.domainmodel.courses;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(CourseState.class)
public abstract class CourseState_ {

	public static volatile SingularAttribute<CourseState, Long> id;
	public static volatile SingularAttribute<CourseState, Boolean> archived;
	public static volatile SingularAttribute<CourseState, String> name;
	public static volatile SingularAttribute<CourseState, Long> version;

}

