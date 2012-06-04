package org.rboaop.refactor.engine.generic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.bcel.classfile.ConstantMethodref;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.MethodGen;
import org.rboaop.refactor.engine.renamefield.convention.IJavaBeanFieldNameConvention;

/**
 * Abstract class that has common tasks among the rename refactoring.
 * 
 * @author Seiya Kawashima
 * 
 */
public abstract class RenameEngine extends RefactoringEngine {

	private String targetClassName;
	protected static final String OBJECT_TYPE_PREFIX = "L";
	protected static final String OBJECT_TYPE_SUFFIX = ";";

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
	 * Renames the old name with the name on constant pool for NameAndType
	 * entry.
	 * 
	 * @param classGen
	 *            Stores constant pool.
	 * @param oldName
	 *            Name to be renamed by the new name.
	 * @param newName
	 *            Name to be used as the new name.
	 * @throws RuntimeErrorException
	 *             Throws when the old name is not found on constant pool.As the
	 *             result, rename refactoring can't be done.
	 */
	protected void rename(ClassGen classGen, String oldName, String newName) {

		int size = classGen.getConstantPool().getSize();
		boolean done = false;
		for (int i = 0; i < size; i++) {
			Constant c = classGen.getConstantPool().getConstant(i);

			if (c instanceof ConstantNameAndType) {
				ConstantNameAndType nameAndType = (ConstantNameAndType) c;
				if (nameAndType.getName(
						classGen.getConstantPool().getConstantPool()).equals(
						oldName)) {
					logger.info("nameAndType: name: "
							+ nameAndType.getName(classGen.getConstantPool()
									.getConstantPool())
							+ " name_index: "
							+ nameAndType.getNameIndex()
							+ " signature_index: "
							+ nameAndType.getSignatureIndex()
							+ " signature: "
							+ nameAndType.getSignature(classGen
									.getConstantPool().getConstantPool()));

					ConstantUtf8 utf8 = new ConstantUtf8(newName);
					classGen.getConstantPool().setConstant(
							nameAndType.getNameIndex(), utf8);
					done = true;
					break;
				}
			} else if (c instanceof ConstantUtf8) {

				ConstantUtf8 oldUtf8 = (ConstantUtf8) c;

				if (oldUtf8.getBytes().equals(oldName)) {
					ConstantUtf8 utf8 = new ConstantUtf8(newName);

					logger.info("rename from: " + oldName + " to " + newName
							+ " ...");

					classGen.getConstantPool().setConstant(i, utf8);

					logger.info("done renaming");
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

	private boolean checkArgumentList(String[] args1, String[] args2) {

		if (args1.length != args2.length)
			return false;

		int count = 0;
		for (int i = 0; i < args1.length; i++) {
			if (args1[i].equals(args2[i]))
				count++;
		}

		if (count == args1.length)
			return true;

		return false;

	}

	/**
	 * Checks method arguments between two methods.
	 * 
	 * @param
	 * @return True, if they have the same arguments and the same length. False
	 *         otherwise.
	 */
	private boolean checkArguments(List<MethodGen> methodList) {

		String[] arguments = ((MethodGen) (methodList.get(0)))
				.getArgumentNames();

		// // true in the loop means there is a duplicated method.
		for (int i = 1; i < methodList.size(); i++) {
			if (this.checkArgumentList(arguments,
					(methodList.get(i)).getArgumentNames()))
				return true;
		}

		return false;
	}

	/**
	 * Checks if the renamed method doesn't create duplicated method name or
	 * not.
	 * 
	 * @param renamedMethodName
	 *            New method name that is used to rename the original method
	 *            name.
	 * @param methods
	 *            Stores all the method names in the class.
	 * @return True if it creates duplicated method names. False otherwise.
	 */
	protected boolean checkExsitingMethod(String oldName,
			String renamedMethodName, Method[] methods) {

		List<MethodGen> methodList = new ArrayList<MethodGen>();

		for (Method method : methods) {
			if (method.getName().equals(renamedMethodName)) {

				for (Method m : methods) {
					if (m.getName().equals(renamedMethodName)) {
						MethodGen mGen = new MethodGen(m, this.getClassGen()
								.getClassName(), this.getClassGen()
								.getConstantPool());
						methodList.add(mGen);

						// return this.checkArguments(methodGen, mGen);
					}
				}

				if (methodList.size() >= 1) {
					return this.checkArguments(methodList);
				}
			}
		}

		return false;
	}

	/**
	 * Get signature for the entry, name parameter from constant pool.
	 * 
	 * @param name
	 *            Target name to be retrieved for the signature.
	 * @return String Signature corresponding to the entry.
	 */
	protected String getSignatureOf(String name) {

		int size = this.getClassGen().getConstantPool().getSize();
		for (int i = 0; i < size; i++) {
			Constant constant = this.getClassGen().getConstantPool()
					.getConstant(i);
			if (constant instanceof ConstantNameAndType) {
				ConstantNameAndType nameAndType = (ConstantNameAndType) constant;
				if (nameAndType.getName(
						this.getClassGen().getConstantPool().getConstantPool())
						.equals(name)) {
					return nameAndType.getSignature(this.getClassGen()
							.getConstantPool().getConstantPool());
				}
			}
		}

		throw new RuntimeException("could not return signature for " + name);
	}

	/**
	 * Gets getter and setter methods that contain old field name.
	 * 
	 * @param oldFieldName
	 *            Field name to be used to find the corresponding getter and
	 *            setter methods.
	 * @return List<String> Stores the getter and setter methods.
	 */
	protected List<String> getMethodsWithOldFieldName(String oldFieldName,
			IJavaBeanFieldNameConvention nameConvention) {

		List<String> methodNames = new ArrayList<String>();
		int size = this.getClassGen().getConstantPool().getSize();

		for (int i = 0; i < size; i++) {
			Constant constant = this.getClassGen().getConstantPool()
					.getConstant(i);
			if (constant instanceof ConstantUtf8) {
				ConstantUtf8 utf8 = (ConstantUtf8) constant;
				if ((utf8.getBytes().startsWith("get")
						|| utf8.getBytes().startsWith("set") || utf8.getBytes()
						.startsWith("is"))
						&& utf8.getBytes().contains(
								nameConvention
										.capitalizeFieldName(oldFieldName))) {
					methodNames.add(utf8.getBytes());
				}
			}
		}

		if (methodNames.isEmpty()) {
			throw new RuntimeException(
					"could not find any methods contain the old field name: "
							+ oldFieldName);
		}
		return methodNames;
	}

	protected List<String> getMethodsWith(String prefix, List<String> methods) {
		List<String> methodsWithPrefix = new ArrayList<String>();
		for (String methodName : methods) {
			if (methodName.startsWith(prefix)) {
				methodsWithPrefix.add(methodName);
			}
		}

		return methodsWithPrefix;
	}

	protected int getIndexOfLocalNameFor(ClassGen classGen, String targetName) {

		int size = classGen.getConstantPool().getSize();
		for (int i = 0; i < size; i++) {
			Constant c = classGen.getConstantPool().getConstant(i);
			if (c instanceof ConstantUtf8) {
				ConstantUtf8 utf8 = (ConstantUtf8) c;
				if (utf8.getBytes().equals(targetName)) {
					return i;
				}
			}
		}

		return -1;
	}
}
