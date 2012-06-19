package fi.pyramus.domainmodel.courses;

import fi.pyramus.domainmodel.resources.Resource;
import fi.pyramus.persistence.usertypes.MonetaryAmount;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(StudentCourseResource.class)
public abstract class StudentCourseResource_ {

	public static volatile SingularAttribute<StudentCourseResource, Long> id;
	public static volatile SingularAttribute<StudentCourseResource, Course> course;
	public static volatile SingularAttribute<StudentCourseResource, MonetaryAmount> hourlyCost;
	public static volatile SingularAttribute<StudentCourseResource, MonetaryAmount> unitCost;
	public static volatile SingularAttribute<StudentCourseResource, Double> hours;
	public static volatile SingularAttribute<StudentCourseResource, Resource> resource;
	public static volatile SingularAttribute<StudentCourseResource, Long> version;

}

