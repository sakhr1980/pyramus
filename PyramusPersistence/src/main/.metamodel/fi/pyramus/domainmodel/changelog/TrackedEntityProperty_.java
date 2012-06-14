package fi.pyramus.domainmodel.changelog;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(TrackedEntityProperty.class)
public abstract class TrackedEntityProperty_ {

	public static volatile SingularAttribute<TrackedEntityProperty, Long> id;
	public static volatile SingularAttribute<TrackedEntityProperty, String> entity;
	public static volatile SingularAttribute<TrackedEntityProperty, String> property;

}

