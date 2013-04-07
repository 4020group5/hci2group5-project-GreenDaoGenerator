package hci2.group5.project.greenDaoGenerator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

/**
 * Generates entities and DAOs.
 *
 * Run it as a Java application (not Android).
 */
public class GreenDaoGenerator {

	public static void main(String[] args) throws Exception {
		new SchemaBuilder().run();
    }

	/**
	 * Builds the database schema & generates Java code for the DAO classes.
	 *
	 * @see <a href="http://greendao-orm.com/documentation/modelling-entities/">Modelling Entities (Documentation page)</a>
	 *
	 */
	private static class SchemaBuilder {

		private static final int SCHEMA_VERSION = 1;

		// we wanna customize the dao packages to be generated
		private static final String DAO_TEST_PACKAGE = "hci2.group5.project.dao.test";
		private static final String DAO_PACKAGE = "hci2.group5.project.dao";

		// we also wanna customize the root where the dao classes to be generated
		// .. is the workspace directory so don't forget to import this project to your workspace
		private static final String WHERE_TO_GENERATE_DAOS = "../project/src-gen";

		private Schema _schema;

		private Entity location, faculty;

		private Entity building;

		public SchemaBuilder() {
			_schema = getSchema();
			addFaculties();
			addLocations();
			addBuildingAndDepartment();

			addLibraries();
			addFoodServices();
		}

		private Schema getSchema() {
			Schema schema = new Schema(SCHEMA_VERSION, DAO_PACKAGE);

			schema.setDefaultJavaPackageDao(DAO_PACKAGE);
			schema.setDefaultJavaPackageTest(DAO_TEST_PACKAGE);

			schema.enableKeepSectionsByDefault();

			return schema;
		}

		private void addFaculties() {
			faculty = _schema.addEntity("Faculty");
			faculty.addIdProperty();
			faculty.addStringProperty("name").notNull();
		}

		private void addLocations() {
			location = _schema.addEntity("Location");
			location.addIdProperty();
			location.addDoubleProperty("latitude").notNull();
			location.addDoubleProperty("longitude").notNull();
		}

		/**
		 * Builds entities for building and department data.
		 * They are coded together because they have n:1 and 1:n relationship.
		 */
		private void addBuildingAndDepartment() {
			//// Building table
	        building = _schema.addEntity("Building");
	        building.addIdProperty();
	        building.addStringProperty("name").notNull();
	        // building to location is a 1:1 relationship - 1 building has 1 locations
	        Property locationIdProperty = building.addLongProperty("locationId").notNull().getProperty();
	        building.addToOne(location, locationIdProperty, "location");

	        building.addStringProperty("builtBy").notNull();
	        building.addIntProperty("builtYear").notNull();
	        // building.addListProperty("departments");   doesn't have it... Read on...
	        building.addStringProperty("supplementaryInfo");


	        //// Department table
	        Entity department = _schema.addEntity("Department");
	        department.addIdProperty();
	        Property facultyIdProperty = department.addLongProperty("facultyId").notNull().getProperty();
	        Property departmentNameProperty = department.addStringProperty("name").notNull().getProperty();
	        Property buildingIdProperty = department.addLongProperty("buildingId").notNull().getProperty();
	        // department to faculty is a 1:1 relationship - 1 department is in 1 faculty
	        department.addToOne(faculty, facultyIdProperty, "faculty");
	        // department to building is a 1:1 relationship - 1 department is located in 1 building
	        department.addToOne(building, buildingIdProperty, "building");

	        // building to department is a 1:n relationship - 1 building may have multiple departments
	        ToMany buildingToDepartments = building.addToMany(department, buildingIdProperty, "departments");
	        buildingToDepartments.orderAsc(departmentNameProperty);
	    }

		private void addLibraries() {
			Entity library = _schema.addEntity("Library");
			library.addIdProperty();
			library.addStringProperty("name").notNull();
			library.addStringProperty("room").notNull();
			Property buildingIdProperty = library.addLongProperty("buildingId").notNull().getProperty();
			library.addToOne(building, buildingIdProperty, "building");
		}

		private void addFoodServices() {
			Entity foodService = _schema.addEntity("FoodService");
			foodService.addIdProperty();
			foodService.addStringProperty("name").notNull();
			foodService.addStringProperty("floor").notNull();
			Property buildingIdProperty = foodService.addLongProperty("buildingId").notNull().getProperty();
			// food to building is a 1:1 relationship - 1 food service is located in 1 building
			foodService.addToOne(building, buildingIdProperty, "building");
			foodService.addDoubleProperty("latitude").notNull();
			foodService.addDoubleProperty("longitude").notNull();
		}

		public void run() throws Exception {
			new DaoGenerator().generateAll(_schema, WHERE_TO_GENERATE_DAOS);
		}
	}
}
