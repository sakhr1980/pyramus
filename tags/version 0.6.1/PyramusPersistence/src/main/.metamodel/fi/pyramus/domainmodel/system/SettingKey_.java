package fi.pyramus.domainmodel.system;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(SettingKey.class)
public abstract class SettingKey_ {

	public static volatile SingularAttribute<SettingKey, Long> id;
	public static volatile SingularAttribute<SettingKey, String> name;

}

