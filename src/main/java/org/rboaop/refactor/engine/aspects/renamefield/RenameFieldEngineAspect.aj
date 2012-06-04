package org.rboaop.refactor.engine.aspects.renamefield;

import org.apache.log4j.Logger;
import org.rboaop.refactor.engine.rename.field.RenameFieldEngine;
import org.rboaop.refactor.engine.renamefield.convention.IJavaBeanFieldNameConvention;

public aspect RenameFieldEngineAspect{

	public static Logger RenameFieldEngine.logger = Logger
			.getLogger(RenameFieldEngine.class);
	private static Logger log = Logger.getLogger(RenameFieldEngine.class);

	pointcut showClassName(Object arg):
		execution(* org.rboaop..*.RenameEngine.getClassName(..)) && args(arg);

	String around(Object arg):showClassName(arg){
		log.info("fully qualified class name: " + (String) arg);
		String className = (String) proceed(arg);
		log.info("after split and got class name only: " + className);

		return className;
	}

	pointcut showFieldNameConvention(IJavaBeanFieldNameConvention fieldNameConvention,
			Object arg):
		execution(* org..*.IJavaBeanFieldNameConvention+.isFieldBeanConvention(..)) && this(fieldNameConvention) && args(arg);

	after(IJavaBeanFieldNameConvention fieldNameConvention, Object arg) returning(boolean result):
	showFieldNameConvention(fieldNameConvention,arg){
		log.info("new field name: " + (String)arg + " is it convention ? : " + result);
	}

	pointcut showEnforcedFieldConvention(IJavaBeanFieldNameConvention fieldNameConvention,
			Object arg):
		execution(* org..*.IJavaBeanFieldNameConvention+.enforceFieldConvention(..)) && this(fieldNameConvention) && args(arg);

	after(IJavaBeanFieldNameConvention fieldNameConvention, Object arg) returning(String fieldName):
		showEnforcedFieldConvention(fieldNameConvention,arg){

		log.info("arg is: " + (String) arg);
		log.info("enforced bean field convention: " + fieldName);
	}
}
