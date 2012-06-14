package fi.pyramus.domainmodel.courses;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(CourseComponent.class)
public abstract class CourseComponent_ extends fi.pyramus.domainmodel.base.ComponentBase_ {

	public static volatile SingularAttribute<CourseComponent, Course> course;
	public static volatile ListAttribute<CourseComponent, CourseComponentResource> resources;

}

