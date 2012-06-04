package org.rboaop.refactor.engine.generic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.bcel.generic.ClassGen;
import org.rboaop.refactor.engine.aspects.generic.IRefactorAspect.IClass;
import org.rboaop.refactor.engine.aspects.generic.IRefactorAspect.IClassWriter;

/**
 * Abstract class uses Composite pattern internally.
 * 
 * @author Seiya Kawashima
 * 
 */
public abstract class RefactoringEngine implements IClass, IClassWriter {

	private static List<RefactoringEngine> refactoringList = new ArrayList<RefactoringEngine>();
	private ClassGen classGen;

	/**
	 * Sets ClassGen object.
	 * 
	 * @param classGen
	 *            ClassGen object to be set.
	 */
	protected void setClassGen(ClassGen classGen) {
		this.classGen = classGen;
	}

	/**
	 * Gets ClassGen object from this class.
	 * 
	 * @return ClassGen object that this class has currently.
	 */
	protected ClassGen getClassGen() {
		return this.classGen;
	}

	protected abstract ClassGen refactor(ClassGen classGen);

	/**
	 * Adds RefactoringEngine object into the list internally managed by this
	 * class.
	 * 
	 * @param refactoringEngine
	 *            RefactoringEngine object to be added.
	 */
	protected synchronized void addRefactoring(
			RefactoringEngine refactoringEngine) {
		this.refactoringList.add(refactoringEngine);
	}

	/**
	 * Removes RefactoringEngine from the list managed by this class.
	 * 
	 * @param refactoringEngine
	 *            RefactoringEngine object to be removed.
	 */
	protected synchronized void removeRefactoring(
			RefactoringEngine refactoringEngine) {
		refactoringList.remove(refactoringEngine);
	}

	public synchronized void removeAllRefactorings(){
		refactoringList.clear();
	}
	
	/**
	 * Runs all the refactoring currently in the list managed by this class.
	 * 
	 * @return ClassGen object that has the final state of all the refactoring.
	 */
	public synchronized ClassGen runRefactoring() {

 		Iterator<RefactoringEngine> itr = refactoringList.iterator();
			while (itr.hasNext()) {
				RefactoringEngine engine = (RefactoringEngine) itr.next();
				this.classGen = engine.refactor(this.classGen);
			}

		return this.classGen;
	}

}
