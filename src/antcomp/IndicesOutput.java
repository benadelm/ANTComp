/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package antcomp;

/**
 * Can output pairs of indices (of full texts to compare).
 * <p>
 * Implementations of this interface will provide means
 * to load such index pairs; a client of this interface
 * can then call
 * {@link #outputIndexPair()}
 * to output the loaded index pair.
 * </p>
 */
public interface IndicesOutput {
	
	/**
	 * Outputs the currently loaded pair of indices.
	 */
	void outputIndexPair();
	
}
