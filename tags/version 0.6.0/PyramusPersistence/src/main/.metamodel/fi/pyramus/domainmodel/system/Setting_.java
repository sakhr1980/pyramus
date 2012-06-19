package fi.pyramus.domainmodel.system;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Setting.class)
public abstract class Setting_ {

	public static volatile SingularAttribute<Setting, Long> id;
	public static volatile SingularAttribute<Setting, String> value;
	public static volatile SingularAttribute<Setting, SettingKey> key;

}

