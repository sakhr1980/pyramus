package fi.pyramus.domainmodel.changelog;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ChangeLogEntryEntityProperty.class)
public abstract class ChangeLogEntryEntityProperty_ {

	public static volatile SingularAttribute<ChangeLogEntryEntityProperty, Long> id;
	public static volatile SingularAttribute<ChangeLogEntryEntityProperty, ChangeLogEntryEntity> entity;
	public static volatile SingularAttribute<ChangeLogEntryEntityProperty, String> name;

}

