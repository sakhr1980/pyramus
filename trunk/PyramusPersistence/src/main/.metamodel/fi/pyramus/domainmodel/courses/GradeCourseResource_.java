package fi.pyramus.domainmodel.courses;

import fi.pyramus.domainmodel.resources.Resource;
import fi.pyramus.persistence.usertypes.MonetaryAmount;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(GradeCourseResource.class)
public abstract class GradeCourseResource_ {

	public static volatile SingularAttribute<GradeCourseResource, Course> course;
	public static volatile SingularAttribute<GradeCourseResource, Long> id;
	public static volatile SingularAttribute<GradeCourseResource, MonetaryAmount> hourlyCost;
	public static volatile SingularAttribute<GradeCourseResource, MonetaryAmount> unitCost;
	public static volatile SingularAttribute<GradeCourseResource, Double> hours;
	public static volatile SingularAttribute<GradeCourseResource, Resource> resource;
	public static volatile SingularAttribute<GradeCourseResource, Long> version;

}

