**Version 0.6.4 (Feb 20, 2013)**

  * Forced SSL connection in web.xml for Pyramus and PyramusReports
  * Fixed Subjects AutoComplete when some fields are empty
  * Moved properties files to web-inf
  * Moved initialdata.xml to updates
  * Fixed issues in Chrome with input fields named "id" which collide with form id property in JavaScript
  * Added link buttons from view project to edit student project and view student
  * Added CourseStudentVariable
  * Added GradingService methods for CreditVariables
  * Added credit variables
  * Added dialog support for nonlocalized messages (produced by plugins)
  * Added sorting and filtering to student course assessment requests in viewstudent courses tab
  * Added more support for plugins
  * BOMified project POMs
  * Changed .escapeHTML calls to external function to check for undefined objects


...


**Version 0.521 (110922.21)**

  * Fixed a bug preventing creating and editing projects containing mandatory modules

**Version 0.52 (110920.20)**

  * Fixed a potential crash in View Project and View Student views containing course assessments without a grade
  * Fixed mandatory validation of dropdown fields
  * Fix for [bug #51](https://code.google.com/p/pyramus/issues/detail?id=51)
  * Fix for [bug #61](https://code.google.com/p/pyramus/issues/detail?id=61)

**Version 0.51 (110815.19)**

  * Added support for student images
  * Added View Module button to View Project view
  * Added Edit Module button to Edit Project view
  * Added a status check page for external software monitoring application uptime ([issue #43](https://code.google.com/p/pyramus/issues/detail?id=#43))
  * Fix for [bug #64](https://code.google.com/p/pyramus/issues/detail?id=64)

**Version 0.5 (110620.18)**

  * Added names of active and inactive study programmes of student to student search view and added tab to search from all students as default (also inactive)
  * Added verbal assessment management to managecourseassessments view (opens in dialog)
  * Added sorting options to subject management
  * Added StringAttributeComparator that can be used for string sorting of simple attributes
  * Added advanced search to modulesearch
  * Added searching by educationtypes to coursesearch
  * Added indexing to education types in coursebase for searching modules and courses by them
  * Added transient methods hasStartedStudies and hasFinishedStudies to Student
  * Added archive course option to related actions in editcourse
  * Added View Module view
  * Added View Project view
  * Added creator and last modifier to the View Course view
  * Added optionality to StudentProject
  * Added ProjectAssessment to StudentProject
  * Added related action to add projects for students in viewstudent
  * Added new information about student projects to viewstudent projects tab
  * Added month and weekday names for javascript localization
  * Added Course Planner
  * Added headerTooltip to ixtable
  * Added related actions to editproject (to viewproject)
  * Added maxParticipantCount to CourseBase
  * Added enrolmentTimeEnd to Course
  * Added CourseDescriptionCategory archiving to management view
  * Added studentcounts to editstudentproject search courses dialogs
  * Added server-side implementation of billing details for users and students
  * Added sorting to BaseService methods that lost it while BaseDAO refactoring
  * Changed Edit/CreateCourse to check if null values are being saved as student participationtype or enrolmenttype on the course
  * Changed some minor localization issues
  * Refactored Comparator sorts out of BaseDAO
  * Fixed a bug in validation\_support.jsp that caused initialization script to crash when ixtable.js was not loaded
  * Fixed paging of searchstudentprojects
  * Fixed managecourseassessments and editstudentproject to show the modified verbal assessment when opening the dialog
  * Fixed erroneous addTag/removeTag methods of all entities supporting tags
  * Removed archive course option from searchcourses

**Version 0.451 (110525.17)**

  * Fixed issue with getSubjectByCode and sqlrestriction

**Version 0.45 (110523.16)**

  * Added new datepicker component
  * Added EducationType for Subject and StudyProgrammeCategory
  * Added support for 1.1.1970 date to ixdatefield (timestamp 0)
  * Added auto-scrolling to Manage Transfer Credit Templates view

  * Changed typeof methods to Object.isFunction() methods in ixprototabs and ixtable
  * Changed listings of Subjects to be grouped by their EducationType if available
  * Changed managetransfercredits to take note of new Subject-EducationType arrangement (to view names correctly)
  * Changed fnilocale library to latest version
  * Replaced ix:datefield="true" attribute with ixDateField class (part of [Issue #37](https://code.google.com/p/pyramus/issues/detail?id=#37))

  * Removed old datepicker component
  * Removed table dependency from validation\_support

  * Fixed broken layout in Search Modules and Search Students dialogs
  * Fixed students table layout on createcourse view
  * Fixed bug that caused input[type="hidden"] elements to be validated

**Version 0.44 (110517.15)**

  * Added CourseDescription & CourseDescriptionCategory entities and their handlers to viewcourse, create/editcourse, create/editmodule and to webservices/dao
  * Added CourseDescriptionCategory management view to settings
  * Added CourseDescription copying to createcourse method of coursesservice (webservices)
  * Added check to ixtable filters so that column does not need to be defined in filter class
  * Added functionality to tab component to support "open new" -tab
  * Added button to managecourseassessments to switch all (max 30) rows to edit mode at once
  * Added methods to list Students by AbstractStudent to StudentsService
  * Added listActiveStudentsByAbstractStudent to StudentDAO
  * Changed EditStudentProject to show out  **of**project courses in separate table instead of same table with courses that are part of the project
  * Changed view title in Manage Course Assessments view
  * Refix for [bug #31](https://code.google.com/p/pyramus/issues/detail?id=31)
  * Fix for [bug #50](https://code.google.com/p/pyramus/issues/detail?id=50)
  * Fix for additional contact information not saved when editing a student
  * Fixed issue in ckeditorsupport that ignored locale settings if toolbar wasn't specified
  * Fixed tab component layout for IE

**Version 0.43 (110426.14)**

  * Fixed a bug causing module and course creation to crash
  * Added more functionality to support third party application integration to Pyramus

**Version 0.42 (110419.13)**

  * Added more detailed page titles to various views of the application
  * Added information about the creator and last modifier to most views
  * Added row count information to most lists in the application
  * Fixed a bug causing unnecessary slowdown when editing a course
  * Fixed a bug that potentially caused the grading view to crash

**Version 0.41 (110405.12)**

  * Fixed a bug in retrieving auto-complete select field values from draft
  * Fixed various numeric value validation issues
  * Added new options for searching new course students by student groups and study programmes.
  * Fix for [Issue #42](https://code.google.com/p/pyramus/issues/detail?id=#42) (Course Component Hours)
  * Fixed a bug that caused searches to crash when the querying with comma(s) followed by spaces
  * Fixed a bug in Manage Student Contact Entries view that no longer showed contact entry formatting
  * Added name extension to the courses of the Search Courses dialog in the Student Project view
  * Upgraded CKEditor to 3.5.2 version
  * Added validation of mandatories to date fields in tables
  * User interface performance improvements
  * Fixed a bug causing the Edit Course view to crash when the course had other costs in its cost plan
  * Fix for [Issue #36](https://code.google.com/p/pyramus/issues/detail?id=#36) (Adding personnel to courses broken)
  * Fix for [Issue #35](https://code.google.com/p/pyramus/issues/detail?id=#35) (Single quote characters in student names break the user interface)

**Version 0.4 (110315.11)**

  * Removed deprecated row delete support from the ixtable component
  * Fixed several layout issues in different views where ixtable is being used
  * Changed all references to ixtable overwritecolumnvalues to use contextmenu instead
  * Added sorting, filtering and value copying support to all tables that may contain a lot of data
  * Added title support for users
  * Added project archiving support
  * Added student project archiving support
  * Added copying of lodging information to adding of new course student in editcourse
  * Added autocompletion of fields in transfer credit management table by selected course
  * Added several performance improvements to ixtable
  * Changed student archiving to display name of studyprogramme instead of student
  * Fixed some minor issues related to breadcrumb handling
  * Added better support for dynamic options in ixtable's select fields
  * Changed the students table of the edit course view to load students in view mode by default
  * Added support for sorting in ixtable
  * Added support for context menus in ixtable
  * Added Course Name Extension and Enrolment Time to courses table in viewstudent
  * Added System Info view that shows information about system attributes
  * Changed user lists to display and sort by last name, first name instead of full name

**Version 0.36 (110214.10)**

  * Added hover effects to tables in viewstudent
  * Added Course State label and Course Evaluation button to courses table in viewstudent
  * Added sorting to courses, course assessments and transfer credits in viewstudent
  * Added sorting to transfer credit management view
  * Added Binary Request Controller for AutoComplete fields to search by name from all transfercredit teplate courses
  * Added hover effect to table on Manage Course Assessments
  * Added basic search for transfercredittempate courses to GradingDAO
  * Added name indexing to TransferCreditTemplateCourse to allow searching
  * Changed name for existing AutoComplete field in ixtable to AutoCompleteSelect and implemented new AutoComplete field that acts as auto completable text field
  * Changed visibility of modify button to false for new rows in managetransfercredits
  * Changed course name field from text field to autocomplete field in managetransfercredits
  * Changed sorting of most lists to be case insensitive


**Version 0.35 (110207.9)**

  * Minor optimizations to PyramusWebServices
  * Disabled Hibernate statistics and SQL logging


**Version 0.33 (110125.7)**

  * Added Manage Course Assessments view, which is accessed through view course or edit course. View can be used to enter assessments to multiple course students at once.
  * Added School Field field to createschool view
  * Added School Field name to school search results
  * Added school sorting by name to school search
  * Added missing optionality to CourseStudentEntity in WebServices
  * Added additional field help texts
  * Fix for [issue #9](https://code.google.com/p/pyramus/issues/detail?id=#9)
  * Minor optimizations to findRowIndex -methods in editcourse
  * Renamed finnish name for School Field management link in menu


**Version 0.32 (110118.6)**

  * Added SchoolField entity which can be used to determine which field of education the school is of. Addition includes management view for SchoolFields and inclusion to School editor
  * Added functionality and localization support to edit course assessment button in viewstudent - course assessments table
  * Added SOAP end point configuration to system variables
  * Added property initialCourseEnrolmentType to Defaults entity (required to fix [issue #28](https://code.google.com/p/pyramus/issues/detail?id=#28))
  * Added default course enrolment type setting to course service's addCourseStudent method
  * Changed GradingDAO.listCourseAssessmentsByStudent to not list assessments that are on archived CourseStudents (affects viewstudent)
  * Changed editcourse to not show course student assessment button for added students until first save
  * Rearranged table for student assessments in viewstudent
  * Fix for [Issue #26](https://code.google.com/p/pyramus/issues/detail?id=#26)
  * Fix to [Issue #27](https://code.google.com/p/pyramus/issues/detail?id=#27)
  * Fix for [Issue #28](https://code.google.com/p/pyramus/issues/detail?id=#28)
  * Fix for [Issue #29](https://code.google.com/p/pyramus/issues/detail?id=#29)
  * Fixed a typo in studentinfopopup which prevented comma separation of old study programmes
  * Fixed broken links in several management JSPs (i.e. municipality management)
  * Fixed version numbering in ChangeLog


**Version 0.31 (110113.5)**

  * Added tags to SchoolEntities, CourseEntities, ModuleEntities, StudentEntities and UserEntities (webservices)
  * Added course search to CourseService (webservices)
  * Fix to viewstudent crash when student has assessments
  * Fix to  [issue #23](https://code.google.com/p/pyramus/issues/detail?id=#23) (report category management crashed when adding new category)
  * Fix to StudentDAO listStudentsByStudentVariable query


**Version 0.3 (110111.4)**

  * Added tags to several search views
  * Added courses to student projects
  * Added related actions menu to student project editor (contains link to student editor)
  * Added default system settings for report context path and authentication provider to initialdata
  * Added optionality property to CourseStudent and TransferCredit entities
  * Added courseNumber property into TransferCredit entity
  * Added management views for transfer credits, transfer credit templates and course participation types
  * Added initial course participation type to Defaults entity
  * Added generic auto complete request controllers for schools, subjects and users
  * Added search indexes to Subject entity
  * Added optimizations, showRow/hideRow methods, support for required parameters and onclick support for date types to ixtable
  * Added study programme selection support to student projects
  * Added archiving support for TransferCredit and TransferCreditTemplate
  * Added validation support to ixtable / auto complete field
  * Added Course Assessment for giving course grades to students on courses. View is accessed through button on student list in edit course view
  * Added Transfer Credits management view

  * Changed BIRT version to 2.6.1
  * Changed editor links to use better icon
  * Changed create student project to use default time unit instead of timeunit id 1
  * Changed clickOk method in dialog so that it would work even when the dialog does not have an ok button
  * Changed StudentDAO.listStudentsByStudentVariable to not list archived students
  * Changed student project editor to use course student data instead of student project course data for determining project courses
  * Changed ixtable to hide column headers when no rows are visible
  * Changed Credit entity's grade join into ManyToOne from OneToOne
  * Changed course participation type to initial participation type in CourseService when undefined participation type was defined
  * Changed student study programme sorting in viewstudent, editstudent and getstudentstudyprogrammesjson, now 'latest' study programme is sorted as first tab also AbstractStudent getLatestStudent now uses same sorting method
  * Changed name for StudentProjectModuleOptionality to CourseOptionality

  * Fixed bug in all editors with tags where user could cause editor to crash when adding tags separated by both spaces and commas
  * Fixed broken listCourseStudents DAO method
  * Fixed minor style errors in ixtable.css
  * Fixed bug in ixtable's autocomplete field that caused setEditable method calls to drop visible values
  * Fix to  [Issue #11](https://code.google.com/p/pyramus/issues/detail?id=#11)
  * Fix for  [issue #12](https://code.google.com/p/pyramus/issues/detail?id=#12)
  * Fix for  [Issue #14](https://code.google.com/p/pyramus/issues/detail?id=#14)
  * Fix for  [Issue #15](https://code.google.com/p/pyramus/issues/detail?id=#15) Added link to user's own user edit view
  * Fix for  [Issue #16](https://code.google.com/p/pyramus/issues/detail?id=#16)
  * Fix for  [Issue #18](https://code.google.com/p/pyramus/issues/detail?id=#18)
  * Fix for  [Issue #20](https://code.google.com/p/pyramus/issues/detail?id=#20) Added student search filter select into student search dialog


**Version 0.2 (101124.3)**

  * Implemented help
  * Added related action to user edit view that creates new work resource from user
  * Separated student contact log management from user view to it's own view
  * Added related actions to student's view and edit views for navigating into contact log management
  * Added email addresses and phone types into student view
  * Added commenting support into student contact log
  * Added report categories
  * Added support for downloading reports as MS Word documents
  * Added cancel button into report parameters dialog
  * Changed date input fields into date picker components in report parameter dialogs
  * Added tagging support for schools, courses, modules, helps, resources, students, student groups and users
  * Added management views for municipalities, time units and course states
  * Reorganized student and course views
  * Support for course component resources
  * CSV import tool
  * Changed student search behavior so that students that have ended their students do not appear in search results anymore by default
  * Removed support for searching archived students from advanced search
  * Added support for searching students who have already ended their studies
  * Added concurrent modification detection to editing views
  * Fixed [Issue #1](https://code.google.com/p/pyramus/issues/detail?id=#1): Unnecessary draft save
  * Fixed report preview container size issues

**Version 0.11 (100920.2)**
  * JavaScript localization support via FNILocale version 0.1
  * Support for overwriting column values in tables
  * Planning and assessing hours for courses
  * Changed the data type of local teaching days from integer to double
  * Upgraded CKEditor to version 3.4
  * Upgraded FNIEvents to version 0.1
  * Fix [issue 2](https://code.google.com/p/pyramus/issues/detail?id=2) : Create user does not save emails
  * Fix [issue 3](https://code.google.com/p/pyramus/issues/detail?id=3) : Missing locale until one has been set the first time
  * Fix [issue 4](https://code.google.com/p/pyramus/issues/detail?id=4) : User-related views don't have breadcrumb navigation

**Version 0.1 (100917.1)**
  * First official build