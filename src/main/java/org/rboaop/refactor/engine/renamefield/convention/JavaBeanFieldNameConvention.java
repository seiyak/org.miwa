package org.rboaop.refactor.engine.renamefield.convention;

/**
 * Applies Java Bean field name convention for rename field refactoring.
 * 
 * @author Seiya Kawashima
 * 
 */
public class JavaBeanFieldNameConvention implements
		IJavaBeanFieldNameConvention {

	/**
	 * Checks if the target field name follows the Java Bean field name
	 * convention or not.
	 * 
	 * @throws RuntimeException
	 *             Throws the exception when the program can't determine it's
	 *             following the convention or not.
	 * @param field
	 *            Field name that is used to rename the original field name.
	 * @return True if the field name is following the convention. False
	 *         otherwise.
	 */
	public boolean isFieldBeanConvention(String field) {

		char[] fieldName = field.toCharArray();
		if (Character.isLowerCase(fieldName[0])
				|| (Character.isUpperCase(fieldName[0]) && Character
						.isUpperCase(fieldName[1]))) {
			// // already field name follows bean convention
			// // or upper case is used for the first two letters and leave as
			// it
			// is.
			return true;
		} else if (Character.isUpperCase(fieldName[0])
				&& Character.isLowerCase(fieldName[1])) {
			// // doesn't follow bean convention yet
			return false;
		}

		throw new RuntimeException("unknwon convention for field name found");

	}

	/**
	 * Enforces the Java Bean field convention on the new field name.
	 * 
	 * @param newFieldName
	 *            New field name that is used to rename the original field name.
	 * @return New field name following the convention.
	 */
	public String enforceFieldConvention(String newFieldName) {

		if (!this.isFieldBeanConvention(newFieldName)) {
			char[] fieldNameInChar = newFieldName.toCharArray();
			fieldNameInChar[0] = Character.toLowerCase(fieldNameInChar[0]);

			return new String(fieldNameInChar);
		}

		return newFieldName;

	}

	/**
	 * Capitalizes the first letter of the parameter, newFieldName to use it as
	 * a part of getXXX and setXXX. If the first letter is already in upper
	 * case, then do nothing. Otherwise, capitalizes the first letter.
	 * 
	 * @param newFieldName
	 *            Used to be a part of getter and setter method name.
	 * @return Capitalized field name.
	 */
	public String capitalizeFieldName(String newFieldName) {

		char[] newFieldNameInChar = newFieldName.toCharArray();
		if (Character.isLowerCase(newFieldNameInChar[0])) {
			newFieldNameInChar[0] = Character
					.toUpperCase(newFieldNameInChar[0]);
			return new String(newFieldNameInChar);
		}

		return newFieldName;
	}

}
