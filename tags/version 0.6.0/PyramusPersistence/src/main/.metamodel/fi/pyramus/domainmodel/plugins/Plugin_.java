package fi.pyramus.domainmodel.plugins;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Plugin.class)
public abstract class Plugin_ {

	public static volatile SingularAttribute<Plugin, Long> id;
	public static volatile SingularAttribute<Plugin, Boolean> enabled;
	public static volatile SingularAttribute<Plugin, String> groupId;
	public static volatile SingularAttribute<Plugin, String> artifactId;
	public static volatile SingularAttribute<Plugin, String> version;

}

