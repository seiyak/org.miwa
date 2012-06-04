package org.rboaop.refactor.engine.aspects.generic;

import java.io.IOException;

import org.rboaop.refactor.engine.rename.field.RenameFieldEngine;
import org.rboaop.refactor.engine.aspects.renamefield.IRenameFieldAspect.IRenameField;

public aspect IRefactorAspect{

	public interface IClass{
		public void setTargetClass(String targetClassName);
		public String getTargetClass();
	}
	
	public interface IClassWriter{
		public void rewrite(String outputLocation) throws IOException;
		public String getClassName(String fullyQualifiedName);
	}
}
