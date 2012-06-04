package org.rboaop.refactor.engine.aspects.renamefield;

public aspect IRenameFieldAspect {
	public interface IRenameField{
		public void setRenameField(String targetFieldName);
		public String getRenameField();
		public void setNewFieldName(String newFieldName);
		public String getNewFieldName();
	}
}
