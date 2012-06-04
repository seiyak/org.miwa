package org.rboaop.refactor.engine.renamefield.convention;

public interface IJavaBeanFieldNameConvention {
	public boolean isFieldBeanConvention(String field);
	public String enforceFieldConvention(String newFieldName);
	public String capitalizeFieldName(String newFieldName);
}
