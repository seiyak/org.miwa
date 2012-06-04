package org.rboaop.refactor.engine.generic;

import java.util.ArrayList;
import java.util.List;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.log4j.Logger;
import org.rboaop.refactor.engine.renamefield.convention.IJavaBeanFieldNameConvention;

public abstract class ChangeEngine extends CommonRefactoringEngine {

	protected static int PUBLIC_STATIC_FINAL = 25;
	protected static int PRIVATE_STATIC_FINAL = 26;
	protected static String INSTRUCTION_ALOAD_0 = "aload_0";
	protected static String INSTRUCTION_PUTFIELD = "putfield";
	protected static String INSTRUCTION_GETFIELD = "getfield";
	protected static String PATTERN_ANY_NUMBER_OF_WORD = " .* ";
	protected static final String BASE_TYPE_BYTE = "B";
	protected static final String BASE_TYPE_CHAR = "C";
	protected static final String BASE_TYPE_DOUBLE = "D";
	protected static final String BASE_TYPE_FLOAT = "F";
	protected static final String BASE_TYPE_INTEGER = "I";
	protected static final String BASE_TYPE_LONG = "J";
	protected static final String BASE_TYPE_SHORT = "S";
	protected static final String BASE_TYPE_BOOLEAN = "Z";
	protected static final String SIGNATURE_STRING = "Ljava/lang/String;";
	protected static final String BOOLEAN_TRUE = "true";
	protected static final String BOOLEAN_FALSE = "false";

	Logger logger = Logger.getLogger(ChangeEngine.class.getName());

	private IJavaBeanFieldNameConvention nameConvention;

	protected ChangeEngine(IJavaBeanFieldNameConvention nameConvention) {
		this.nameConvention = nameConvention;
	}

	/**
	 * Checks if the input is 'true' or 'false' as boolean initial value.
	 * 
	 * @param initialValue
	 *            Used to be the initial value for boolean type
	 * @return True or false depending on the initial value.
	 */
	private boolean setInitialBooleanValue(String initialValue) {

		if (initialValue.equals(BOOLEAN_TRUE)) {
			return true;
		} else if (initialValue.equals(BOOLEAN_FALSE)) {
			return false;
		}

		throw new RuntimeException(
				"could not set the initial value as boolean, " + initialValue);
	}

	private char setInitialCharValue(String initialValue) {

		if (initialValue.length() == 1) {
			return initialValue.charAt(0);
		} else {
			throw new RuntimeException(
					"initial value, "
							+ initialValue
							+ " could not be convertered to char type. initial value must be length of 1, but "
							+ initialValue.length());
		}
	}

	/**
	 * Converts String initial value to the specific type for the initial value
	 * 
	 * @param initialValue
	 *            Used to be the initial value as String
	 * @param fieldGen
	 *            Stores information about field to be a constant field.
	 */
	protected void convertInitialValue(String initialValue, FieldGen fieldGen) {
		if (fieldGen.getSignature().equals(BASE_TYPE_BOOLEAN)) {
			fieldGen.setInitValue(this.setInitialBooleanValue(initialValue));
		} else if (fieldGen.getSignature().equals(BASE_TYPE_BYTE)) {
			fieldGen.setInitValue(Byte.parseByte(initialValue));
		} else if (fieldGen.getSignature().equals(BASE_TYPE_CHAR)) {
			fieldGen.setInitValue(this.setInitialCharValue(initialValue));
		} else if (fieldGen.getSignature().equals(BASE_TYPE_DOUBLE)) {
			fieldGen.setInitValue(new Double(initialValue));
		} else if (fieldGen.getSignature().equals(BASE_TYPE_FLOAT)) {
			fieldGen.setInitValue(new Float(initialValue));
		} else if (fieldGen.getSignature().equals(BASE_TYPE_INTEGER)) {
			fieldGen.setInitValue(new Integer(initialValue));
		} else if (fieldGen.getSignature().equals(BASE_TYPE_LONG)) {
			fieldGen.setInitValue(new Long(initialValue));
		} else if (fieldGen.getSignature().equals(BASE_TYPE_SHORT)) {
			fieldGen.setInitValue(new Short(initialValue));
		} else if (fieldGen.getSignature().startsWith("L")
				&& fieldGen.getSignature().contains("java")
				&& fieldGen.getSignature().contains("lang")
				&& fieldGen.getSignature().contains("String")
				&& fieldGen.getSignature().endsWith(";")) {
			fieldGen.setInitValue(initialValue);
		} else {

			throw new RuntimeException(
					"the type for the initial value is not supported, "
							+ fieldGen.getSignature());
		}
	}

	/**
	 * Removes getter and setter field accesses.
	 * 
	 * @param classGen
	 *            Stores information about the target class including the getter
	 *            and setter.
	 * @param fieldName
	 *            Used to examine each method to remove.
	 */
	protected void removeGetterSetterFieldAccess(ClassGen classGen,
			String fieldName) {

		for (Method method : classGen.getMethods()) {

			if (method.getName().contains(
					this.nameConvention.capitalizeFieldName(fieldName))) {
				this.getClassGen().removeMethod(method);
			}
		}
	}
}
