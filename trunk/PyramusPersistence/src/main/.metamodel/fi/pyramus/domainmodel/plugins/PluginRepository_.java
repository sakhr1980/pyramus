package fi.pyramus.domainmodel.plugins;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(PluginRepository.class)
public abstract class PluginRepository_ {

	public static volatile SingularAttribute<PluginRepository, Long> id;
	public static volatile SingularAttribute<PluginRepository, String> url;

}

