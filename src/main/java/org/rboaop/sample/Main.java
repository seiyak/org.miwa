package org.rboaop.sample;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ACONST_NULL;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InstructionTargeter;
import org.apache.bcel.generic.LineNumberGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.NOP;
import org.apache.bcel.generic.TargetLostException;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.InstructionFinder;
import org.apache.log4j.Logger;

public class Main {

	public static void main(String[] args) {
		ClassGen gen = new ClassGen(
				Repository.lookupClass("org.rboaop.sample.Sample2"));
		int size = gen.getConstantPool().getSize();
		for (int i = 0; i < size; i++) {
			Constant c = gen.getConstantPool().getConstant(i);
			if (c instanceof ConstantClass) {
				ConstantClass clazz = (ConstantClass) c;
				System.out.println(clazz.getConstantValue(gen.getConstantPool()
						.getConstantPool()));
			}
		}

		for (Field field : gen.getFields()) {
			System.out.println("field name: " + field.getName()
					+ " access flag: " + field.getAccessFlags());
			FieldGen fieldGen = new FieldGen(field, gen.getConstantPool());
			System.out.println("init value: " + fieldGen.getInitValue()
					+ " signature: " + fieldGen.getSignature() + " type: "
					+ fieldGen.getType());
		}

		boolean changed = false;
		List<InstructionRange> range = new LinkedList<InstructionRange>();
		MethodGen methodGen = null;
		for (Method method : gen.getMethods()) {
			methodGen = new MethodGen(method, gen.getClassName(),
					gen.getConstantPool());
			// System.out.println("instruction list:=================");
			// System.out.println(methodGen.getLocalVariableTable(gen.getConstantPool()));
			// System.out.println(methodGen.getInstructionList());
			// System.out.println("===================\n");

			if (method.getName().contains("ToBeConstant")) {
				gen.removeMethod(method);

			} else {

				InstructionFinder f = new InstructionFinder(
						methodGen.getInstructionList());
				String pattern = "aload_0 .* putfield";

				for (Iterator i = f.search(pattern); i.hasNext();) {
					InstructionHandle[] match = (InstructionHandle[]) i.next();
					int j;
					InstructionRange r = new InstructionRange();
					for (j = 0; j < match.length; j++) {
						// System.out.println("match: " + match[j]);
						if (match[j].toString().contains("aload_0")) {
							System.out.println(match[j]);
							r.setStart(match[j].getPosition());
						} else if (match[j].toString().contains("putfield")
								&& match[j].toString().contains("84")) {
							System.out.println(match[j]);
							r.setEnd(match[j].getPosition());
						}
					}

					if (r.getEnd() != -1 && r.getStart() != -1) {
						// range.add(r);
						try {
							System.out.println("start: " + r.getStart()
									+ " end: " + r.getEnd() + " "
									+ match.length);

							methodGen.getInstructionList().delete(match[0],
									match[match.length - 1]);

						} catch (TargetLostException e) {
							if (!changed) {
								FieldGen fg = new FieldGen(25, Type.STRING,
										"GOOGLE", gen.getConstantPool());
								fg.setInitValue("Yahoo");
								gen.addField(fg.getField());
								changed = true;
							}

							System.out.println("come here");
							InstructionHandle[] targets = e.getTargets();
							for (int k = 0; k < targets.length; k++) {
								InstructionTargeter[] targeters = targets[k]
										.getTargeters();

								for (int l = 0; l < targeters.length; l++) {
									targeters[l].updateTarget(targets[k],
											match[match.length - 1].getNext());
								}
							}
						}
						LineNumberGen[] lineGens = new LineNumberGen[methodGen.getLineNumbers().length];
						for(int z = 0; z < lineGens.length;z++){
							lineGens[z] = methodGen.getLineNumbers()[z];
							
						}
				
						
						gen.replaceMethod(method, methodGen.getMethod());
					}
					System.out.println("==");
				}
			}
		}
		System.out.println("size: " + range.size());
		for (InstructionRange rnge : range) {
			System.out.println(rnge.getStart() + " " + rnge.getEnd());
		}

		try {

			gen.getJavaClass()
					.dump("/home/seiyak/Documents/org.miwa/target/classes/org/rboaop/sample/Sample2.class");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
