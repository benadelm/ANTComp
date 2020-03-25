/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package antcomp;

/**
 * Can output combinations of author and title distances.
 * <p>
 * Implementations of this interface will provide means to
 * associate the distances with the author names and titles
 * actually compared.
 * </p>
 */
public interface DistancesOutput {
	
	/**
	 * Outputs a combination of author and title distance.
	 * 
	 * @param authorDistance
	 * the author distance
	 * 
	 * @param titleDistance
	 * the title distance
	 */
	void outputDistances(long authorDistance, long titleDistance);
	
}
