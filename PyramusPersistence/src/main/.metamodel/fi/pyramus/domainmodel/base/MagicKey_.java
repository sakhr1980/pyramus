package fi.pyramus.domainmodel.base;

import java.util.Date;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(MagicKey.class)
public abstract class MagicKey_ {

	public static volatile SingularAttribute<MagicKey, Long> id;
	public static volatile SingularAttribute<MagicKey, MagicKeyScope> scope;
	public static volatile SingularAttribute<MagicKey, Date> created;
	public static volatile SingularAttribute<MagicKey, String> name;
	public static volatile SingularAttribute<MagicKey, Long> version;

}

