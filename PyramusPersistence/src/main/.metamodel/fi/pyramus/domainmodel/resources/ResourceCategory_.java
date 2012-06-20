package fi.pyramus.domainmodel.resources;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ResourceCategory.class)
public abstract class ResourceCategory_ {

	public static volatile SingularAttribute<ResourceCategory, Long> id;
	public static volatile SingularAttribute<ResourceCategory, Boolean> archived;
	public static volatile SingularAttribute<ResourceCategory, String> name;
	public static volatile SingularAttribute<ResourceCategory, Long> version;

}

