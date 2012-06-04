package org.rboaop.refactor.engine.rename.method;

import java.io.IOException;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantMethodref;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.generic.ClassGen;
import org.rboaop.refactor.engine.generic.Refactoring;
import org.rboaop.refactor.engine.renamefield.convention.IJavaBeanFieldNameConvention;

/**
 * 
 * This class takes care of rename method refactoring on multiple classes
 * including the class having the target method itself.
 * 
 * @author Seiya Kawashima
 * 
 */
public class RenameMethodInClassesEngine extends RenameMethodEngine {

	private IJavaBeanFieldNameConvention nameConvention;
	private ClassGen[] classGens;

	public RenameMethodInClassesEngine(Refactoring refactoring,
			IJavaBeanFieldNameConvention nameConvention) {
		super(refactoring);
		this.nameConvention = nameConvention;
	}

	public RenameMethodInClassesEngine(Refactoring refactoring,
			String targetMethodName, String newMethodName,
			String[] targetClasses, IJavaBeanFieldNameConvention nameConvention) {

		super(refactoring);
		this.classGens = new ClassGen[targetClasses.length];
		for (int i = 0; i < targetClasses.length; i++)
			this.classGens[i] = new ClassGen(
					Repository.lookupClass(targetClasses[i]));

		this.nameConvention = nameConvention;

		this.setTargetMethodName(targetMethodName);
		this.setNewMethodName(newMethodName);
	}

	public ClassGen[] getClassGens() {
		return classGens;
	}

	public IJavaBeanFieldNameConvention getNameConvention() {
		return nameConvention;
	}

	public void setNameConvention(IJavaBeanFieldNameConvention nameConvention) {
		this.nameConvention = nameConvention;
	}

	@Override
	protected ClassGen refactor(ClassGen classGen) {

		for (ClassGen gen : this.classGens) {
			gen = this.renameMethod(gen);
		}
		
		return  super.renameMethod(classGen);
	}

	@Override
	public void rewrite(String outputLocation) throws IOException {
		
		String targetClassName = this.getTargetClass();
		
		this.setTargetClass(targetClassName);
		logger.info("target class name: " + targetClassName + " " + this.getTargetClass());
		super.rewrite(outputLocation);
		
		for (ClassGen gen : this.classGens) {
			this.setTargetClass(gen.getClassName());
			this.setClassGen(gen);
			logger.info("target class name: " + this.getTargetClass());
			super.rewrite(outputLocation);
		}
	}

	@Override
	protected ClassGen renameMethod(ClassGen cg) {

		if (this.getIndexOfLocalNameFor(cg, this.getTargetMethodName()) > 0) {
			this.rename(cg, this.getTargetMethodName(), this.getNewMethodName());
			//cg = super.renameMethod(cg);
		}

		return cg;
	}

	@Override
	protected void rename(ClassGen classGen, String oldName, String newName) {
		logger.info("target classGen in rename(): " + classGen.getClassName());
		logger.info("oldName: " + oldName + " newName: " + newName);

		if (classGen.getClassName().equals(this.getTargetClass())) {
			super.rename(classGen, oldName, newName);
			return;
		}

		int size = classGen.getConstantPool().getSize();
		boolean done = false;
		for (int i = 0; i < size; i++) {
			Constant c = classGen.getConstantPool().getConstant(i);

			if (c instanceof ConstantMethodref) {
				ConstantMethodref methodRef = (ConstantMethodref) c;
				ConstantNameAndType nameAndType = (ConstantNameAndType) classGen
						.getConstantPool().getConstant(
								methodRef.getNameAndTypeIndex());

				logger.info("found target method name: "
						+ nameAndType.getName(classGen.getConstantPool()
								.getConstantPool()));

				// // checks class name which has the target method
				// // and the target method name with oldName
				if (methodRef.getClass(
						classGen.getConstantPool().getConstantPool()).equals(
						this.getTargetClass())
						&& nameAndType.getName(
								classGen.getConstantPool().getConstantPool())
								.equals(oldName)) {

					if (this.checkExsitingMethod(oldName,newName, classGen.getMethods())) {
						// // need to add another nameAndType
						// // can't just alter the name
						// // because the class has the same method name
						// // as the old name
						if (this.checkExsitingMethod(oldName,newName,
								classGen.getMethods())) {
							// // get name index and set the name index
							// //for the new method name
							
							logger.info("come 1");
							
							nameAndType.setNameIndex(this
									.getIndexOfLocalNameFor(classGen, newName));
						} else {
							// // add another nameAndType
							logger.info("come 2");
							int newNameIndex = classGen.getConstantPool().addUtf8(newName);
							nameAndType.setNameIndex(newNameIndex);
						}

					} else {
						if (this.checkExsitingMethod(oldName,newName,
								classGen.getMethods())) {
							logger.info("come 3");
							// // get name index and
							// set the name for the new method
							classGen.getConstantPool().setConstant(
									nameAndType.getNameIndex(),
									new ConstantUtf8(newName));
						} else {
							logger.info("come 4, newName: " + newName);
							int newNameIndex = classGen.getConstantPool().addUtf8(newName);
							nameAndType.setNameIndex(newNameIndex);
						}
					}
					done = true;
					break;
				}
			}
		}

		logger.info("done: " + done);
		if (!done) {
			throw new RuntimeException("could not rename from " + oldName
					+ " to " + newName);
		}
	}
}
