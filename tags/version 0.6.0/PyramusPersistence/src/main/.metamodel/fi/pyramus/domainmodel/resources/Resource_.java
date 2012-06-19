package fi.pyramus.domainmodel.resources;

import fi.pyramus.domainmodel.base.Tag;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Resource.class)
public abstract class Resource_ {

	public static volatile SetAttribute<Resource, Tag> tags;
	public static volatile SingularAttribute<Resource, Long> id;
	public static volatile SingularAttribute<Resource, ResourceCategory> category;
	public static volatile SingularAttribute<Resource, Boolean> archived;
	public static volatile SingularAttribute<Resource, String> name;
	public static volatile SingularAttribute<Resource, Long> version;

}

