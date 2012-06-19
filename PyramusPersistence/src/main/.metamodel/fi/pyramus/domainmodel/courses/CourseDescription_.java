package fi.pyramus.domainmodel.courses;

import fi.pyramus.domainmodel.base.CourseBase;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(CourseDescription.class)
public abstract class CourseDescription_ {

	public static volatile SingularAttribute<CourseDescription, Long> id;
	public static volatile SingularAttribute<CourseDescription, CourseBase> courseBase;
	public static volatile SingularAttribute<CourseDescription, CourseDescriptionCategory> category;
	public static volatile SingularAttribute<CourseDescription, String> description;

}

