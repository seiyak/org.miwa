package org.rboaop.refactor.engine.generic;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ClassGen;

/**
 * This class contains information about what refactoring to be done.
 * 
 * @author seiyak
 * 
 */
public class Refactoring {

	private ClassGen classGen;

	public Refactoring(String fullyQualifiedClassName) {
		this.classGen = new ClassGen(this.getJavaClass(fullyQualifiedClassName));
	}

	/**
	 * Gets JavaClass object for the parameter, fullyQualifiedClassName.
	 * 
	 * @param fullyQualifiedClassName
	 * @return JavaClass for the class name.
	 */
	private JavaClass getJavaClass(String fullyQualifiedClassName) {

		return Repository.lookupClass(fullyQualifiedClassName);
	}

	/**
	 * Gets classGen property.
	 * 
	 * @return ClassGen classGen property.
	 */
	public ClassGen getClassGen() {
		return this.classGen;
	}
}
