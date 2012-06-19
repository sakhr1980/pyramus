package fi.pyramus.domainmodel.courses;

import fi.pyramus.domainmodel.resources.Resource;
import fi.pyramus.persistence.usertypes.MonetaryAmount;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(BasicCourseResource.class)
public abstract class BasicCourseResource_ {

	public static volatile SingularAttribute<BasicCourseResource, Course> course;
	public static volatile SingularAttribute<BasicCourseResource, Long> id;
	public static volatile SingularAttribute<BasicCourseResource, MonetaryAmount> hourlyCost;
	public static volatile SingularAttribute<BasicCourseResource, MonetaryAmount> unitCost;
	public static volatile SingularAttribute<BasicCourseResource, Double> hours;
	public static volatile SingularAttribute<BasicCourseResource, Resource> resource;
	public static volatile SingularAttribute<BasicCourseResource, Integer> units;
	public static volatile SingularAttribute<BasicCourseResource, Long> version;

}

