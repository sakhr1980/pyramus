package fi.pyramus.domainmodel.base;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(CourseEducationType.class)
public abstract class CourseEducationType_ {

	public static volatile ListAttribute<CourseEducationType, CourseEducationSubtype> courseEducationSubtypes;
	public static volatile SingularAttribute<CourseEducationType, Long> id;
	public static volatile SingularAttribute<CourseEducationType, CourseBase> courseBase;
	public static volatile SingularAttribute<CourseEducationType, EducationType> educationType;
	public static volatile SingularAttribute<CourseEducationType, Long> version;

}

