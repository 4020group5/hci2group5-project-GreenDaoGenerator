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
		private static final String WHERE_TO_GENERATE_DAOS = "../project/src-gen";

		private Schema _schema;

		public SchemaBuilder() {
			_schema = getSchema();
			addBuildingAndDepartment();
		}

		private Schema getSchema() {
			Schema schema = new Schema(SCHEMA_VERSION, DAO_PACKAGE);

			schema.setDefaultJavaPackageDao(DAO_PACKAGE);
			schema.setDefaultJavaPackageTest(DAO_TEST_PACKAGE);

			return schema;
		}

		/**
		 * Builds entities for building and department data.
		 * They are coded together because they have n:1 and 1:n relationship.
		 */
		private void addBuildingAndDepartment() {
			//// Building table
	        Entity building = _schema.addEntity("Building");
	        building.addIdProperty();
	        building.addStringProperty("name").notNull();
	        building.addStringProperty("builtBy").notNull();
	        building.addIntProperty("builtYear").notNull();
	        // building.addListProperty("departments");   doesn't have it... Read on...
	        building.addStringProperty("supplementaryInfo");

	        //// Department table
	        Entity department = _schema.addEntity("Department");
	        department.addIdProperty();
	        Property departmentNameProperty = department.addStringProperty("name").notNull().getProperty();
	        Property buildingIdProperty = department.addLongProperty("buildingId").notNull().getProperty();

	        // department to building is a 1:1 relationship - 1 department is located in 1 building
	        department.addToOne(building, buildingIdProperty, "building");

	        // building to department is a 1:n relationship - 1 building may have multiple departments
	        ToMany buildingToDepartments = building.addToMany(department, buildingIdProperty, "departments");
	        buildingToDepartments.orderAsc(departmentNameProperty);
	    }

		public void run() throws Exception {
			new DaoGenerator().generateAll(_schema, WHERE_TO_GENERATE_DAOS);
		}
	}
}
