package org.rboaop.refactor.engine.generic;

import java.io.IOException;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.Method;

/**
 * This class takes care of common tasks among the refactoring.
 * @author Seiya Kawashima
 *
 */
public abstract class CommonRefactoringEngine extends RefactoringEngine {

	private String targetClassName;

	/**
	 * Sets targetClassName property.
	 * 
	 * @param targetClassName
	 *            Target class name to be set.
	 */
	public void setTargetClass(String targetClassName) {
		this.targetClassName = targetClassName;

	}

	/**
	 * Gets targetClassName property.
	 * 
	 * @return targetClassName property.
	 */
	public String getTargetClass() {
		return this.targetClassName;
	}

	/**
	 * Rewrites the original class file with the rename refactoring.
	 * 
	 * @throws IOException
	 *             Throws the exception when can't rewrite the class file.
	 */
	public void rewrite(String outputLocation) throws IOException {

		this.getClassGen()
				.getJavaClass()
				.dump(outputLocation + this.getClassName(this.getTargetClass())
						+ ".class");
	}

	/**
	 * Gets class name from the fully qualified class name.
	 * 
	 * @param fullyQualifiedName
	 *            Fully qualified class name.
	 * @return Class name extracted from the qualified class name.
	 */
	public String getClassName(String fullyQualifiedName) {
		return fullyQualifiedName
				.substring(fullyQualifiedName.lastIndexOf(".") + 1);
	}
	
	/**
	 * Convenient method to search a method.
	 * 
	 * @param methodName
	 *            Method name to be searched
	 * @param methods
	 *            Stores all the methods.
	 * @return True when the target method is found. False otherwise.
	 */
	protected boolean checkExistingMethod(String methodName, Method[] methods) {

		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Convenient method to search a method.
	 * 
	 * @param methodName
	 *            Method name to be searched
	 * @param methods
	 *            Stores all the methods.
	 * @return Method object when the target method is found. Null otherwise.
	 */
	protected Method getExistingMethod(String methodName, Method[] methods) {

		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				return method;
			}
		}

		return null;
	}
	
	/**
	 * Checks if the renamed field doesn't create duplicated field name or not.
	 * 
	 * @param renamedFieldName
	 *            New field name that is used to rename the original field name.
	 * @param fields
	 *            Stores all the filed names in the class.
	 * @return True if it creates duplicated field names. False otherwise.
	 */
	protected boolean checkExsitingField(String renamedFieldName, Field[] fields) {

		for (Field field : fields) {
			if (field.getName().equals(renamedFieldName)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Gets a field.
	 * 
	 * @param renamedFieldName
	 *            New field name that is used to rename the original field name.
	 * @return Field object if a field with the name exists. Null otherwise.
	 */
	protected Field getExsitingField(String renamedFieldName, Field[] fields) {

		for (Field field : fields) {
			if (field.getName().equals(renamedFieldName)) {
				return field;
			}
		}

		return null;
	}
	
	/**
	 * Checks if a specific Utf8 entry exists or not.
	 * 
	 * @param newName
	 *            Name to be examined
	 * @return True when the new name entry exists. False otherwise
	 */
	protected boolean checkExistingSpecificUtf8(String newName) {

		int size = this.getClassGen().getConstantPool().getSize();
		for (int i = 0; i < size; i++) {
			Constant c = this.getClassGen().getConstantPool().getConstant(i);
			if (c instanceof ConstantUtf8) {
				ConstantUtf8 utf8 = (ConstantUtf8) c;
				if (utf8.getBytes().equals(newName)) {
					return true;
				}
			}
		}

		return false;
	}
}
