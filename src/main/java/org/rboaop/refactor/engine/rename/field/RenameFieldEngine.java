package org.rboaop.refactor.engine.rename.field;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ClassGen;
import org.rboaop.refactor.engine.aspects.renamefield.IRenameFieldAspect.IRenameField;
import org.rboaop.refactor.engine.generic.Refactoring;
import org.rboaop.refactor.engine.generic.RenameEngine;
import org.rboaop.refactor.engine.renamefield.convention.IJavaBeanFieldNameConvention;

/**
 * This class takes care of rename field refactoring.
 * 
 * @author Seiya Kawashima
 * 
 */
public class RenameFieldEngine extends RenameEngine implements IRenameField {

	private String targetFieldName;
	private String newFieldName;
	private IJavaBeanFieldNameConvention javaBeanFieldNameConvention;

	/**
	 * One argument constructor.classGen property must be set before calling any
	 * refactoring. There are two constructors for this class. but it must be
	 * set whichever the constructor is used.
	 * 
	 * @param refactoring
	 */
	public RenameFieldEngine(Refactoring refactoring) {
		super();
		this.addRefactoring(this);
		this.setClassGen(refactoring.getClassGen());
	}

	/**
	 * Two argument constructor. Each of the arguments initializes the
	 * respective fields.
	 * 
	 * @param refactoring
	 * @param targetFieldName
	 *            Field name that is renamed by this refactoring engine.
	 * @param fieldNameConvention
	 */
	public RenameFieldEngine(Refactoring refactoring, String targetFieldName,
			String newFieldName,
			IJavaBeanFieldNameConvention fieldNameConvention) {
		super();
		this.addRefactoring(this);
		this.setTargetClass(refactoring.getClassGen().getClassName());
		this.targetFieldName = targetFieldName;
		this.newFieldName = newFieldName;
		this.javaBeanFieldNameConvention = fieldNameConvention;
		this.setClassGen(refactoring.getClassGen());
	}

	/**
	 * Sets targetFieldName property.
	 * 
	 * @param targetFieldName
	 *            Name used to be set.
	 */
	public void setRenameField(String targetFieldName) {
		this.targetFieldName = targetFieldName;

	}

	/**
	 * Gets targetField property.
	 * 
	 * @return targetFieldName property.
	 */
	public String getRenameField() {
		return this.targetFieldName;
	}

	/**
	 * Sets newFieldName property.
	 * 
	 * @param newFieldName
	 *            New name for the field.
	 */
	public void setNewFieldName(String newFieldName) {
		this.newFieldName = newFieldName;

	}

	/**
	 * Gets newFieldName property.
	 * 
	 * @return newFieldName property.
	 */
	public String getNewFieldName() {
		return this.newFieldName;
	}

	/**
	 * Sets javaBeanFieldNameConvention property.
	 * 
	 * @param fieldNameConvention
	 *            Java Bean name convention object to be used as the name
	 *            convention.
	 */
	public void setJavaBeanFieldNameConvention(
			IJavaBeanFieldNameConvention fieldNameConvention) {
		this.javaBeanFieldNameConvention = fieldNameConvention;
	}

	/**
	 * Gets javaBeanFieldNameConvention property.
	 * 
	 * @return javaBeanFieldNameConvention property.
	 */
	public IJavaBeanFieldNameConvention getJavaBeanFieldNameConvention() {
		return this.javaBeanFieldNameConvention;
	}

	/**
	 * Executes rename field refactoring.
	 * 
	 * @param classGen
	 *            Stores target class information to work on rename field
	 *            refactoring.
	 * @return Stores refactored target class information.
	 */
	@Override
	protected ClassGen refactor(ClassGen classGen) {
		JavaClass javaClass = Repository.lookupClass(this.getTargetClass());
		Field[] fields = javaClass.getFields();

		return this.renameField(classGen, fields);

	}

	/**
	 * Renames the target field name with a new field name.
	 * 
	 * @param javaClass
	 *            Object that abstracts the class file.
	 * @param fields
	 *            Stores all the field in the class.
	 * @return Writable class object storing the rename information.
	 */
	private ClassGen renameField(ClassGen cg, Field[] fields) {

		// ClassGen cg = new ClassGen(javaClass);
		boolean done = false;

		for (Field f : fields) {
			logger.info("all field names: " + f.getName());
			if (f.getName().equals(this.getRenameField())) {

				if (this.checkExsitingField(this.getNewFieldName(), fields)) {
					throw new RuntimeException("Duplicated field name detected");
				} else {
					this.rename(cg, this.getRenameField(),
							this.getNewFieldName());
				}
			}
		}

		if (!done) {
			logger.warn("could not find the specified target field name: "
					+ this.getRenameField()
					+ ". Rename refactoring didn't take place");
		}

		return cg;
	}
}
