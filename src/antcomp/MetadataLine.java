/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package antcomp;

/**
 * Stores the pieces of meta-data information
 * used by this program for a single work.
 */
public class MetadataLine {
	
	private final String pAuthor;
	private final String pTitle;
	private final String pFilename;
	
	/**
	 * Initializes a new instance of this class.
	 * 
	 * @param author
	 * the (name of the) author of the work; not {@code null}
	 * 
	 * @param title
	 * the title of the work; not {@code null}
	 * 
	 * @param filename
	 * the name of the file associated with the work;
	 * not {@code null}
	 */
	public MetadataLine(final String author, final String title, final String filename) {
		pAuthor = author;
		pTitle = title;
		pFilename = filename;
	}
	
	/**
	 * Returns the name of the author of the work.
	 * 
	 * @return
	 * the name of the author of the work
	 */
	public String getAuthor() {
		return pAuthor;
	}
	
	/**
	 * Returns the title of the work.
	 * 
	 * @return
	 * the title of the work
	 */
	public String getTitle() {
		return pTitle;
	}
	
	/**
	 * Returns the filename associated with the work.
	 * 
	 * @return
	 * the filename associated with the work
	 */
	public String getFilename() {
		return pFilename;
	}
	
}
