package fi.pyramus.domainmodel.courses;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(CourseDescriptionCategory.class)
public abstract class CourseDescriptionCategory_ {

	public static volatile SingularAttribute<CourseDescriptionCategory, Long> id;
	public static volatile SingularAttribute<CourseDescriptionCategory, Boolean> archived;
	public static volatile SingularAttribute<CourseDescriptionCategory, String> name;

}

