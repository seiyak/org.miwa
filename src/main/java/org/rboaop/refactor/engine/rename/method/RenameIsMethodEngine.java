package org.rboaop.refactor.engine.rename.method;

import java.util.List;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.rboaop.refactor.engine.generic.Refactoring;
import org.rboaop.refactor.engine.renamefield.convention.IJavaBeanFieldNameConvention;

/**
 * This class takes care of renaming getter and setter method for boolean type
 * following to rename of the respective field. This is a convenient class to
 * rename both of getter and setter method names at the same time.
 * 
 * @author Seiya Kawashima
 * 
 */
public class RenameIsMethodEngine extends RenameMethodEngine {

	private String oldFieldName;
	private String newFieldName;
	private IJavaBeanFieldNameConvention nameConvention;

	public RenameIsMethodEngine(Refactoring refactoring,
			IJavaBeanFieldNameConvention nameConvention) {
		super(refactoring);
		this.nameConvention = nameConvention;
	}

	public RenameIsMethodEngine(Refactoring refactoring, String oldFieldName,
			String newFieldName, IJavaBeanFieldNameConvention nameConvention) {
		super(refactoring);
		this.oldFieldName = oldFieldName;
		this.newFieldName = newFieldName;
		this.nameConvention = nameConvention;
	}

	@Override
	protected ClassGen refactor(ClassGen classGen) {

		List<String> oldMethodNames = this.getMethodsWith(METHOD_PREFIX_IS,
				this.getMethodsWithOldFieldName(this.oldFieldName,
						this.nameConvention));
		if (oldMethodNames.size() != 1) {
			throw new RuntimeException("the size should be 1 for the field: "
					+ this.oldFieldName + " but found " + oldMethodNames.size());
		}

		this.setTargetMethodName(oldMethodNames.get(0));
		this.setNewMethodName(METHOD_PREFIX_IS
				+ this.nameConvention.capitalizeFieldName(this.newFieldName));

		return this.renameMethod(classGen);
	}
}
