package fi.pyramus.domainmodel.file;

import fi.pyramus.domainmodel.users.User;
import java.util.Date;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(File.class)
public abstract class File_ {

	public static volatile SingularAttribute<File, Long> id;
	public static volatile SingularAttribute<File, FileType> fileType;
	public static volatile SingularAttribute<File, Date> lastModified;
	public static volatile SingularAttribute<File, Boolean> archived;
	public static volatile SingularAttribute<File, Date> created;
	public static volatile SingularAttribute<File, String> name;
	public static volatile SingularAttribute<File, User> lastModifier;
	public static volatile SingularAttribute<File, byte[]> data;
	public static volatile SingularAttribute<File, String> fileName;
	public static volatile SingularAttribute<File, String> contentType;
	public static volatile SingularAttribute<File, User> creator;

}

