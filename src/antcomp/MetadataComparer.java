/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package antcomp;

/**
 * Can compare pairs of {@link MetadataLine} instances.
 * <p>
 * This interface is designed to support efficient comparisons
 * between all pairs in a list of {@link MetadataLine} instances:
 * One {@link MetadataLine} is loaded (using
 * {@link #load(MetadataLine)})
 * and then compared with a lot of other
 * {@link MetadataLine} instances (using many calls of
 * {@link #compareWith(MetadataLine)}).
 * </p>
 */
public interface MetadataComparer {
	
	/**
	 * Loads a {@link MetadataLine}
	 * to be subsequently compared with other
	 * {@link MetadataLine} instances.
	 * 
	 * @param metadataLine
	 * the {@link MetadataLine}; not {@code null}
	 */
	void load(MetadataLine metadataLine);
	
	/**
	 * Compares a {@link MetadataLine} with the
	 * {@link MetadataLine} that was loaded by
	 * the latest call to
	 * {@link #load(MetadataLine)}.
	 * 
	 * @param metadataLine
	 * the {@link MetadataLine}; not {@code null}
	 */
	void compareWith(MetadataLine metadataLine);
	
}
