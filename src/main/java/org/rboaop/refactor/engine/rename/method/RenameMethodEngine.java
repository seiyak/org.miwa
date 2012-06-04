package org.rboaop.refactor.engine.rename.method;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.rboaop.refactor.engine.generic.Refactoring;
import org.rboaop.refactor.engine.generic.RenameEngine;

/**
 * This class takes care of rename method refactoring.
 * 
 * @author Seiya Kawashima
 * 
 */
public class RenameMethodEngine extends RenameEngine {

	private String targetMethodName;
	private String newMethodName;
	protected static final String METHOD_PREFIX_IS = "is";
	protected static final String METHOD_PREFIX_GET = "get";
	protected static final String METHOD_PREFIX_SET = "set";

	public RenameMethodEngine(Refactoring refactoring) {
		super();
		this.addRefactoring(this);
		this.setClassGen(refactoring.getClassGen());
		this.setTargetClass(refactoring.getClassGen().getClassName());
	}

	public RenameMethodEngine(Refactoring refactoring, String targetMethodName,
			String newMethodName) {
		super();
		this.addRefactoring(this);
		this.setClassGen(refactoring.getClassGen());
		this.targetMethodName = targetMethodName;
		this.newMethodName = newMethodName;
		this.setTargetClass(refactoring.getClassGen().getClassName());
	}

	public void setNewMethodName(String newMethodName) {
		this.newMethodName = newMethodName;
	}

	public String getNewMethodName() {
		return newMethodName;
	}

	public void setTargetMethodName(String targetMethodName) {
		this.targetMethodName = targetMethodName;
	}

	public String getTargetMethodName() {
		return targetMethodName;
	}

	@Override
	protected ClassGen refactor(ClassGen classGen) {

		return this.renameMethod(classGen);
	}

	protected boolean checkMethodArguments(){
		return false;
	}
	
	protected ClassGen renameMethod(ClassGen cg) {

		Method[] methods = cg.getMethods();
		for (Method m : methods) {
			logger.info("all methods on " + cg.getClassName() + " : " + m.getName());
			if (m.getName().equals(this.targetMethodName)) {

				if (this.checkExsitingMethod(this.targetMethodName,this.newMethodName, methods)) {
					
					throw new RuntimeException("Duplicated method name detected on " + cg.getClassName() + " for " + this.newMethodName);
				} else {
					super.rename(cg, this.targetMethodName, this.newMethodName);
				}
			}
		}

		return cg;
	}
}
