package org.rboaop.refactor.engine.rename.clazz;

import java.io.File;
import java.io.IOException;

import org.apache.bcel.generic.ClassGen;
import org.rboaop.refactor.engine.generic.Refactoring;
import org.rboaop.refactor.engine.generic.RenameEngine;

public class RenameClassEngine extends RenameEngine {

	private String targetClassName;
	private String newClassName;
	private static final String CLASS_EXTENSION = ".class";

	public RenameClassEngine(Refactoring refactoring) {
		super();
		this.addRefactoring(this);
		this.setClassGen(refactoring.getClassGen());
		this.setTargetClass(this.getClassGen().getClassName());
	}

	public RenameClassEngine(Refactoring refactoring, String targetClassName,
			String newClassName) {
		super();
		this.addRefactoring(this);
		this.setClassGen(refactoring.getClassGen());
		this.setTargetClass(refactoring.getClassGen().getClassName());
		this.newClassName = newClassName;
		this.targetClassName = targetClassName;
	}

	@Override
	protected ClassGen refactor(ClassGen classGen) {

		String oldInternalName = this.targetClassName.replace(".", "/");
		String newInternalName = this.newClassName.replace(".", "/");
		this.rename(classGen, oldInternalName, newInternalName);
		this.rename(classGen, OBJECT_TYPE_PREFIX + oldInternalName
				+ OBJECT_TYPE_SUFFIX, OBJECT_TYPE_PREFIX + newInternalName
				+ OBJECT_TYPE_SUFFIX);
		this.setTargetClass(this.newClassName);
		return classGen;
	}

	protected void deleteOldClassFile(String oldClassPath) {

		File classFile = new File(oldClassPath);
		if (!classFile.exists()) {
			throw new RuntimeException("specified class doesn't exist.");
		}

		classFile.delete();
	}

	/**
	 * Rewrites the original class file with the rename refactoring.
	 * 
	 * @throws IOException
	 *             Throws the exception when can't rewrite the class file.
	 */
	@Override
	public void rewrite(String outputLocation) throws IOException {

		this.getClassGen()
				.getJavaClass()
				.dump(outputLocation + this.getClassName(this.getTargetClass())
						+ CLASS_EXTENSION);
		this.deleteOldClassFile(outputLocation
				+ this.getClassName(this.targetClassName) + CLASS_EXTENSION);
	}
}
