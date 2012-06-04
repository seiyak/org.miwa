package org.rboaop.refactor.engine.rename.method;

import java.util.List;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.rboaop.refactor.engine.generic.Refactoring;
import org.rboaop.refactor.engine.renamefield.convention.IJavaBeanFieldNameConvention;

public class RenameGetterSetterMethodsEngine extends RenameMethodEngine {

	private String oldFieldName;
	private String newFieldName;
	private IJavaBeanFieldNameConvention nameConvention;
	private Refactoring refactoring;
	private RenameIsMethodEngine isMethodEngine;
	private RenameGetMethodEngine getMethodEngine;

	public RenameGetterSetterMethodsEngine(Refactoring refactoring,
			String oldFieldName, String newFieldName,
			IJavaBeanFieldNameConvention nameConvention) {

		super(refactoring);
		this.refactoring = refactoring;
		this.oldFieldName = oldFieldName;
		this.newFieldName = newFieldName;
		this.nameConvention = nameConvention;

		if (this.isSignatureBoolean(oldFieldName)) {
			this.isMethodEngine = new RenameIsMethodEngine(this.refactoring,
					this.oldFieldName, this.newFieldName, this.nameConvention);
		} else {

			this.getMethodEngine = new RenameGetMethodEngine(this.refactoring,
					this.oldFieldName, this.newFieldName, this.nameConvention);
		}
	}

	private boolean isSignatureBoolean(String oldFieldName) {

		if (this.getSignatureOf(oldFieldName).equals("Z")) {
			return true;
		}

		return false;
	}

	@Override
	protected ClassGen refactor(ClassGen classGen) {

		List<String> oldMethodNames = this.getMethodsWith(METHOD_PREFIX_SET,
				this.getMethodsWithOldFieldName(this.oldFieldName,
						this.nameConvention));
		if (oldMethodNames.size() != 1) {
			throw new RuntimeException("the size should be 1 for the field: "
					+ this.oldFieldName + " but found " + oldMethodNames.size());
		}
		this.setTargetMethodName(oldMethodNames.get(0));
		this.setNewMethodName(METHOD_PREFIX_SET
				+ this.nameConvention.capitalizeFieldName(this.newFieldName));

		return this.renameMethod(classGen);
	}
}
