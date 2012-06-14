package fi.pyramus.domainmodel.resources;

import fi.pyramus.persistence.usertypes.MonetaryAmount;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(WorkResource.class)
public abstract class WorkResource_ extends fi.pyramus.domainmodel.resources.Resource_ {

	public static volatile SingularAttribute<WorkResource, MonetaryAmount> costPerUse;
	public static volatile SingularAttribute<WorkResource, MonetaryAmount> hourlyCost;

}

