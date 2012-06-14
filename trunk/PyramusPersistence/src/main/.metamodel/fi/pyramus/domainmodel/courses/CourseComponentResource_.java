package fi.pyramus.domainmodel.courses;

import fi.pyramus.domainmodel.resources.Resource;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(CourseComponentResource.class)
public abstract class CourseComponentResource_ {

	public static volatile SingularAttribute<CourseComponentResource, Double> usagePercent;
	public static volatile SingularAttribute<CourseComponentResource, Long> id;
	public static volatile SingularAttribute<CourseComponentResource, CourseComponent> courseComponent;
	public static volatile SingularAttribute<CourseComponentResource, Resource> resource;

}

