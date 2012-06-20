package fi.pyramus.domainmodel.changelog;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ChangeLogEntryEntity.class)
public abstract class ChangeLogEntryEntity_ {

	public static volatile SingularAttribute<ChangeLogEntryEntity, Long> id;
	public static volatile SingularAttribute<ChangeLogEntryEntity, String> name;

}

