package org.rboaop.refactor.engine.change.constant;

import java.util.Iterator;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionTargeter;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.TargetLostException;
import org.apache.bcel.util.InstructionFinder;
import org.apache.log4j.Logger;
import org.rboaop.refactor.engine.generic.ChangeEngine;
import org.rboaop.refactor.engine.generic.InstructionRange;
import org.rboaop.refactor.engine.generic.Refactoring;
import org.rboaop.refactor.engine.renamefield.convention.IJavaBeanFieldNameConvention;

import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionList;

/**
 * This class takes care of change a field to 'static final' constant.
 * 
 * @author Seiya Kawashima
 * 
 */
public class ChangeFieldConstantEngine extends ChangeEngine {

	private String targetFieldName;
	private String newTargetFieldName;
	private String initialValue;
	private static Logger logger = Logger
			.getLogger(ChangeFieldConstantEngine.class.getName());

	public ChangeFieldConstantEngine(Refactoring refactoring,
			IJavaBeanFieldNameConvention nameConvention) {
		super(nameConvention);
		this.addRefactoring(this);
		this.setClassGen(refactoring.getClassGen());
		this.setTargetClass(this.getClassGen().getClassName());
	}

	public ChangeFieldConstantEngine(Refactoring refactoring,
			String targetFieldName, String newTargetFieldName,
			String initialValue, IJavaBeanFieldNameConvention nameConvention) {
		super(nameConvention);
		this.addRefactoring(this);
		this.setClassGen(refactoring.getClassGen());
		this.setTargetClass(this.getClassGen().getClassName());
		this.targetFieldName = targetFieldName;
		this.newTargetFieldName = newTargetFieldName;
		this.initialValue = initialValue;
	}

	public void setInitialValue(String initialValue) {
		this.initialValue = initialValue;
	}

	public String getInitialValue() {
		return initialValue;
	}

	public String getNewTargetFieldName() {
		return newTargetFieldName;
	}

	public String getTargetFieldName() {
		return this.targetFieldName;
	}

	/**
	 * Checks if the target field is a primitive type or instance of Object.
	 * When it's instance of Object,' then throws Runtime exception. Currently
	 * only supports making primitive types as static final.
	 * 
	 * @param Stores
	 *            data about the target class including the target field.
	 */
	private void checkTargetType(ClassGen classGen) {

		FieldGen fieldGen = new FieldGen(this.getExsitingField(
				this.targetFieldName, classGen.getFields()),
				classGen.getConstantPool());
		if (fieldGen.getType() instanceof Object) {
			if (!fieldGen.getSignature().equals(SIGNATURE_STRING))
				throw new RuntimeException(
						"static final object is not supported,"
								+ fieldGen.getSignature());
		}
	}

	/**
	 * Changes field flag to static final when it's not already.
	 * 
	 * @param classGen
	 *            Stores data about the target class including the target field.
	 * @return FieldGen object having modified access flag when it's not yet
	 *         static final.Otherwise, FieldGen object having unmodified access
	 *         flag.
	 */
	private FieldGen changeFieldFlagToConstant(ClassGen classGen) {

		// this.checkTargetType(classGen);

		if (this.checkExsitingField(this.targetFieldName, classGen.getFields())) {
			if (this.checkExsitingField(this.newTargetFieldName,
					classGen.getFields())) {
				throw new RuntimeException("duplicated field name found,"
						+ this.newTargetFieldName + " on the class,"
						+ classGen.getClassName());
			} else {
				FieldGen fieldGen = new FieldGen(this.getExsitingField(
						this.targetFieldName, classGen.getFields()),
						classGen.getConstantPool());

				// // checks if the access flag is static final
				if (fieldGen.getAccessFlags() != PUBLIC_STATIC_FINAL) {
					fieldGen.setAccessFlags(PUBLIC_STATIC_FINAL);
				}

				return fieldGen;
			}
		}

		throw new RuntimeException(
				"could not find the target field on the class,"
						+ classGen.getClassName() + " field name: "
						+ this.targetFieldName);
	}

	/**
	 * Changes the target field name using newTargetFieldName property. If the
	 * property is null or '', make the original target field name upper case.
	 * 
	 * @param classGen
	 *            Stores data about the target class including the target field.
	 * @param fieldGen
	 *            Stores data about the target field whose access flag may be
	 *            modified to static final prior to this method call.
	 * @return FieldGen object having renamed field.
	 */
	private FieldGen changeNameForConstant(ClassGen classGen, FieldGen fieldGen) {

		if (this.newTargetFieldName == null
				|| this.newTargetFieldName.equals("")) {
			fieldGen.setName(this.targetFieldName.toUpperCase());
		} else {
			fieldGen.setName(this.newTargetFieldName.toUpperCase());
		}

		return fieldGen;
	}

	/**
	 * Finds the starting and ending positions of instruction of interest.
	 * 
	 * @param handles
	 *            Stores all instructions of a method.
	 * @return InstructionRange object stores the starting and ending position.
	 */
	private InstructionRange findInstructionRange(InstructionHandle[] handles) {

		InstructionRange instructionRange = new InstructionRange();

		logger.info(this.getTargetClass());
		logger.info(this.targetFieldName);
		logger.info(this.getExsitingField(this.targetFieldName,
				this.getClassGen().getFields()).getSignature());

		int fieldIndex = this
				.getClassGen()
				.getConstantPool()
				.lookupFieldref(
						this.getTargetClass(),
						this.targetFieldName,
						this.getExsitingField(this.targetFieldName,
								this.getClassGen().getFields()).getSignature());
		if (fieldIndex == -1) {
			throw new RuntimeException("could not find the field, "
					+ this.targetFieldName + " on class,"
					+ this.getTargetClass());
		}

		for (int i = 0; i < handles.length; i++) {
			if (handles[i].toString().contains(INSTRUCTION_ALOAD_0)) {
				instructionRange.setStart(handles[i].getPosition());
			} else if (handles[i].toString().contains(INSTRUCTION_PUTFIELD)
					&& handles[i].toString().contains(
							Integer.toString(fieldIndex))) {
				instructionRange.setEnd(handles[i].getPosition());
			}
		}

		return instructionRange;
	}

	private void replaceGetterToDirectAccess(InstructionFinder finder,
			MethodGen methodGen, Method eachMethod, String pattern) {

		for (Iterator itr = finder.search(pattern); itr.hasNext();) {
			InstructionHandle[] matches = (InstructionHandle[]) itr.next();
			InstructionRange range = this.findInstructionRange(matches);

			if (range.getStart() != -1 && range.getEnd() != -1) {
				logger.info("matched instruction start: " + range.getStart()
						+ " end: " + range.getEnd());

				try {
					methodGen.removeLineNumbers();
					methodGen.getInstructionList().delete(matches[0],
							matches[matches.length - 1]);
				} catch (TargetLostException ex) {

					InstructionHandle[] targets = ex.getTargets();
					InstructionList list = new InstructionList();
					list.append(InstructionConstants.ALOAD_0);
					//list.append(new LDC(this.getClassGen().getConstantPool().lookupUtf8(this.newTargetFieldName)));
					for (int i = 0; i < targets.length; i++) {
						InstructionTargeter[] targeters = targets[i]
								.getTargeters();

						for (int j = 0; j < targeters.length; j++) {
							targeters[j].updateTarget(targets[i],
									matches[matches.length - 1].getNext());
						}
					}

					this.getClassGen().replaceMethod(eachMethod,
							methodGen.getMethod());
				}
			}
		}

	}

	/**
	 * Removes direct access instruction. methodGen and eachMethod is a kind of
	 * redundant. TODO make it only one variable.
	 * 
	 * @param finder
	 *            InstructionFinder object searches the instruction of interest.
	 * @param methodGen
	 *            MethodGen object examined for the instruction of interest.
	 * @param eachMethod
	 *            Method object.
	 * @param pattern
	 *            Used for the instruction of interest.
	 * @param changed
	 *            already changed the field constant or not. Used in the method
	 *            internally.
	 * @return True if already change the field to a constant field. False
	 *         otherwise.
	 */
	private boolean removeDirectAccess(InstructionFinder finder,
			MethodGen methodGen, Method eachMethod, String pattern,
			boolean changed) {

		for (Iterator itr = finder.search(pattern); itr.hasNext();) {
			InstructionHandle[] matches = (InstructionHandle[]) itr.next();
			InstructionRange range = this.findInstructionRange(matches);

			if (range.getStart() != -1 && range.getEnd() != -1) {
				logger.info("matched instruction start: " + range.getStart()
						+ " end: " + range.getEnd());

				try {
					methodGen.removeLineNumbers();
					methodGen.getInstructionList().delete(matches[0],
							matches[matches.length - 1]);
				} catch (TargetLostException ex) {
					if (!changed) {
						logger.info("changed: " + changed);
						FieldGen fieldGen = this.changeNameForConstant(this
								.getClassGen(), this
								.changeFieldFlagToConstant(this.getClassGen()));

						this.convertInitialValue(this.initialValue, fieldGen);

						this.getClassGen().addField(fieldGen.getField());
						changed = true;
					}

					InstructionHandle[] targets = ex.getTargets();
					for (int i = 0; i < targets.length; i++) {
						InstructionTargeter[] targeters = targets[i]
								.getTargeters();

						for (int j = 0; j < targeters.length; j++) {
							targeters[j].updateTarget(targets[i],
									matches[matches.length - 1].getNext());
						}
					}

					this.getClassGen().replaceMethod(eachMethod,
							methodGen.getMethod());
				}
			}
		}

		return changed;
	}

	/**
	 * Executes change field to constant refactoring.
	 */
	@Override
	protected ClassGen refactor(ClassGen classGen) {
		boolean changed = false;
		MethodGen methodGen = null;
		for (Method method : this.getClassGen().getMethods()) {

			methodGen = new MethodGen(method,
					this.getClassGen().getClassName(), this.getClassGen()
							.getConstantPool());

			InstructionFinder finder = new InstructionFinder(
					methodGen.getInstructionList());

			changed = this.removeDirectAccess(finder, methodGen, method,
					INSTRUCTION_ALOAD_0 + PATTERN_ANY_NUMBER_OF_WORD
							+ INSTRUCTION_PUTFIELD, changed);
			this.removeGetterSetterFieldAccess(classGen, this.targetFieldName);
		}
		return classGen;
	}

}
