package fi.pyramus.domainmodel.resources;

import fi.pyramus.persistence.usertypes.MonetaryAmount;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(MaterialResource.class)
public abstract class MaterialResource_ extends fi.pyramus.domainmodel.resources.Resource_ {

	public static volatile SingularAttribute<MaterialResource, MonetaryAmount> unitCost;

}

