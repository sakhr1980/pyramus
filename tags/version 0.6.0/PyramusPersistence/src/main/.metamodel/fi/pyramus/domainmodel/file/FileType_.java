package fi.pyramus.domainmodel.file;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(FileType.class)
public abstract class FileType_ {

	public static volatile SingularAttribute<FileType, Long> id;
	public static volatile SingularAttribute<FileType, Boolean> archived;
	public static volatile SingularAttribute<FileType, String> name;

}

