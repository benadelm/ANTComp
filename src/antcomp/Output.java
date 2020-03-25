/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package antcomp;

/**
 * Can associate index pairs with outputs.
 * <p>
 * An index pair is loaded by calling
 * {@link #setFirstIndex(int)}
 * and
 * {@link #setSecondIndex(int)};
 * an index loaded using a call to
 * {@link #setFirstIndex(int)}
 * remains loaded until the next
 * call to
 * {@link #setFirstIndex(int)},
 * and likewise an index loaded using a call to
 * {@link #setSecondIndex(int)}
 * remains loaded until the next
 * call to
 * {@link #setSecondIndex(int)}.
 * </p>
 */
public interface Output {
	
	/**
	 * Sets the first index of the index pair.
	 * 
	 * @param index
	 * the first index
	 */
	void setFirstIndex(int index);
	
	/**
	 * Sets the second index of the index pair.
	 * @param index
	 * the second index
	 */
	void setSecondIndex(int index);
	
}
