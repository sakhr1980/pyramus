package fi.pyramus.util.dataimport;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import fi.pyramus.dao.StudentDAO;
import fi.pyramus.domainmodel.base.Address;
import fi.pyramus.domainmodel.base.ContactInfo;
import fi.pyramus.domainmodel.base.Email;
import fi.pyramus.domainmodel.base.PhoneNumber;
import fi.pyramus.domainmodel.courses.Course;
import fi.pyramus.domainmodel.courses.CourseStudent;
import fi.pyramus.domainmodel.modules.Module;
import fi.pyramus.domainmodel.students.AbstractStudent;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.users.User;

@SuppressWarnings("rawtypes")
public class DataImportStrategyProvider {

  protected DataImportStrategyProvider() {
  }
  
  public static DataImportStrategyProvider instance() {
    return _instance;
  }
  
  public void registerFieldHandler(String entityStrategy, String fieldName, FieldHandlingStrategy strategy) {
    EntityFieldHandlerProvider entityHandler = getEntityFieldHandlerProvider(entityStrategy);

    if (entityHandler == null) {
      entityHandler = new EntityFieldHandlerProvider();
      registerEntityFieldHandler(entityStrategy, entityHandler);
    }
    
    entityHandler.registerFieldHandler(fieldName, strategy);
  }
  
  public FieldHandlingStrategy getFieldHandler(String entityStrategy, String fieldName) {
    EntityFieldHandlerProvider entityHandler = getEntityFieldHandlerProvider(entityStrategy);
    
    if (entityHandler != null)
      return entityHandler.getFieldHandlingStrategy(fieldName);
    else
      return null;
  }
  
  private EntityFieldHandlerProvider getEntityFieldHandlerProvider(String entityStrategy) {
    return entityFieldHandlers.get(entityStrategy);
  }
  
  public EntityHandlingStrategy getEntityHandler(String entityStrategy) {
    return entityHandlers.get(entityStrategy);
  }
  
  public void registerEntityHandler(String entityStrategy, EntityHandlingStrategy entityHandler) {
    entityHandlers.put(entityStrategy, entityHandler);
  }

  private void registerEntityFieldHandler(String entityStrategy, EntityFieldHandlerProvider prov) {
    entityFieldHandlers.put(entityStrategy, prov);
  }

  private class EntityFieldHandlerProvider {
  
    public void registerFieldHandler(String fieldName, FieldHandlingStrategy strategy) {
      fieldHandlers.put(fieldName, strategy);
    }
    
    public FieldHandlingStrategy getFieldHandlingStrategy(String fieldName) {
      return fieldHandlers.get(fieldName);
    }
    
    private Map<String, FieldHandlingStrategy> fieldHandlers = new HashMap<String, FieldHandlingStrategy>();  
  }

  private Map<String, EntityFieldHandlerProvider> entityFieldHandlers = new HashMap<String, EntityFieldHandlerProvider>();
  private Map<String, EntityHandlingStrategy> entityHandlers = new HashMap<String, EntityHandlingStrategy>();
  private static DataImportStrategyProvider _instance;
  
  static {
    _instance = new DataImportStrategyProvider();

    String entityStrategyName;
    Class subClass;
    
    entityStrategyName = "Student";
    subClass = Student.class;
    instance().registerEntityHandler(entityStrategyName, new DefaultEntityHandlingStrategy(Student.class, entityStrategyName) {
      
      private ContactInfo getStudentContactInfo(Student student) {
        ContactInfo result = student.getContactInfo();
        if (result == null) {
          result = new ContactInfo();
          student.setContactInfo(result);
        }
        return result;
      }
      
      @Override
      public void initializeContext(DataImportContext context) {
        super.initializeContext(context);
        
        if (context.hasField("socialSecurityNumber")) {
          String ssn = context.getFieldValue("socialSecurityNumber");

          if (ssn != null) {
            StudentDAO studentDAO = new StudentDAO();
            AbstractStudent abstractStudent = studentDAO.getAbstractStudentBySSN(ssn);
            context.addEntity(AbstractStudent.class, abstractStudent);
          }
        }
      }
      
      @Override
      protected void bindEntities(DataImportContext context) {
        super.bindEntities(context);
        
        // Bind Address, Email, Phone, AbstractStudent etc. to Student or create new ones where needed
        Student student = (Student) context.getEntity(Student.class);
        
        AbstractStudent abstractStudent = (AbstractStudent) context.getEntity(AbstractStudent.class);
        if (abstractStudent == null) {
          abstractStudent = new AbstractStudent();
          context.addEntity(AbstractStudent.class, abstractStudent);
        }
        
        abstractStudent.addStudent(student);

        // Adress        
        Address address = (Address) context.getEntity(Address.class);
        if (address != null) {
          ContactInfo contactInfo = getStudentContactInfo(student);
          contactInfo.addAddress(address);
        }

        // Email
        Email email = (Email) context.getEntity(Email.class);
        if (email != null) {
          ContactInfo contactInfo = getStudentContactInfo(student);
          contactInfo.addEmail(email);
        }

        // PhoneNumber
        PhoneNumber phone = (PhoneNumber) context.getEntity(PhoneNumber.class);
        if (phone != null) {
          ContactInfo contactInfo = getStudentContactInfo(student);
          contactInfo.addPhoneNumber(phone);
        }
      }
    });
    instance().registerFieldHandler(entityStrategyName, "firstName", new DefaultFieldHandingStrategy(subClass));
    instance().registerFieldHandler(entityStrategyName, "lastName", new DefaultFieldHandingStrategy(subClass));

    // Address
    subClass = Address.class;
    instance().registerFieldHandler(entityStrategyName, "city", new DefaultFieldHandingStrategy(subClass));
    instance().registerFieldHandler(entityStrategyName, "country", new DefaultFieldHandingStrategy(subClass));
    instance().registerFieldHandler(entityStrategyName, "postalCode", new DefaultFieldHandingStrategy(subClass));
    instance().registerFieldHandler(entityStrategyName, "streetAddress", new DefaultFieldHandingStrategy(subClass));

    // Email
    subClass = Email.class;
    instance().registerFieldHandler(entityStrategyName, "email", new DefaultFieldHandingStrategy(subClass, "address"));

    // Phone
    subClass = PhoneNumber.class;
    instance().registerFieldHandler(entityStrategyName, "phoneNumber", new DefaultFieldHandingStrategy(subClass, "number"));

    // AbstractStudent
    subClass = AbstractStudent.class;
    instance().registerFieldHandler(entityStrategyName, "birthday", new DefaultFieldHandingStrategy(subClass));
    instance().registerFieldHandler(entityStrategyName, "socialSecurityNumber", new DefaultFieldHandingStrategy(subClass));// new SocialSecurityNumberHandlingStrategy(subClass, true));
    instance().registerFieldHandler(entityStrategyName, "sex", new DefaultFieldHandingStrategy(subClass));

    // Course
    entityStrategyName = "Course";
    subClass = Course.class;
    instance().registerEntityHandler(entityStrategyName, new DefaultEntityHandlingStrategy(Course.class, entityStrategyName) {
      @Override
      protected void bindEntities(DataImportContext context) {
        super.bindEntities(context);

        Date now = new Date();
        User user = context.getLoggedUser();
        
        Course course = (Course) context.getEntity(Course.class);
        course.setCreated(now);
        course.setLastModified(now);
        course.setCreator(user);
        course.setLastModifier(user);
        
        Module module = new Module();
        module.setName(course.getName());
        module.setDescription(course.getDescription());
        module.setCreated(course.getCreated());
        module.setLastModified(course.getLastModified());
        module.setCreator(course.getCreator());
        module.setLastModifier(course.getLastModifier());
        
        course.setModule(module);
        
        context.addEntity(Module.class, module);
      }
    });
    instance().registerFieldHandler(entityStrategyName, "name", new DefaultFieldHandingStrategy(subClass));
    instance().registerFieldHandler(entityStrategyName, "description", new DefaultFieldHandingStrategy(subClass));
    instance().registerFieldHandler(entityStrategyName, "beginDate", new DefaultFieldHandingStrategy(subClass));
    instance().registerFieldHandler(entityStrategyName, "endDate", new DefaultFieldHandingStrategy(subClass));

    // CourseStudent
    entityStrategyName = "CourseStudent";
    subClass = CourseStudent.class;
    instance().registerEntityHandler(entityStrategyName, new DefaultEntityHandlingStrategy(CourseStudent.class, entityStrategyName) {
      @Override
      protected void bindEntities(DataImportContext context) {
        super.bindEntities(context);
        CourseStudent courseStudent = (CourseStudent) context.getEntity(CourseStudent.class);
        courseStudent.setEnrolmentTime(new Date());
      }      
    });
    instance().registerFieldHandler(entityStrategyName, "student", new ReferenceFieldHandlingStrategy(subClass, "student", Student.class));
    instance().registerFieldHandler(entityStrategyName, "course", new ReferenceFieldHandlingStrategy(subClass, "course", Course.class));
  }
}