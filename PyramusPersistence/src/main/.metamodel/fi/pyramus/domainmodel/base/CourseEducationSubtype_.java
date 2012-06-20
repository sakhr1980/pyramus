package fi.pyramus.domainmodel.base;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(CourseEducationSubtype.class)
public abstract class CourseEducationSubtype_ {

	public static volatile SingularAttribute<CourseEducationSubtype, Long> id;
	public static volatile SingularAttribute<CourseEducationSubtype, EducationSubtype> educationSubtype;
	public static volatile SingularAttribute<CourseEducationSubtype, CourseEducationType> courseEducationType;
	public static volatile SingularAttribute<CourseEducationSubtype, Long> version;

}

