/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package antcomp;

import java.util.function.Function;

/**
 * {@link MetadataComparer} implementation that computes
 * all relevant distances for a given pair of works
 * and outputs them to a {@link DistancesOutput}.
 */
public class OutputDistancesMetadataComparer implements MetadataComparer {
	
	private final Function<? super String, String[]> pTitleSplitter;
	private final DistancesOutput pOutput;
	
	private String pAuthor;
	private String pTitle;
	
	private String[] pTitleWords;
	
	/**
	 * Initializes a new instance of this class.
	 * 
	 * @param titleSplitter
	 * a {@link Function} that splits titles into words;
	 * not {@code null}
	 * 
	 * @param output
	 * a {@link DistancesOutput} for outputting distances;
	 * not {@code null}
	 */
	public OutputDistancesMetadataComparer(final Function<? super String, String[]> titleSplitter, final DistancesOutput output) {
		pTitleSplitter = titleSplitter;
		pOutput = output;
	}
	
	@Override
	public void load(final MetadataLine metadataLine) {
		pAuthor = metadataLine.getAuthor();
		pTitle = metadataLine.getTitle();
		
		pTitleWords = pTitleSplitter.apply(pTitle);
	}
	
	@Override
	public void compareWith(final MetadataLine metadataLine) {
		pOutput.outputDistances(
				computeAuthorDistance(metadataLine),
				computeTitleDistance(metadataLine)
			);
	}
	
	private long computeAuthorDistance(final MetadataLine metadataLine) {
		final String author = metadataLine.getAuthor();
		return pAuthor.equals(author) ? 0L : EditDistances.editDistance(author, pAuthor);
	}
	
	private long computeTitleDistance(final MetadataLine metadataLine) {
		final String title = metadataLine.getTitle();
		if (pTitle.equals(title))
			return 0L;
		
		final String[] titleWords = pTitleSplitter.apply(title);
		
		return Math.min(
				EditDistances.substringEditDistance(titleWords, pTitleWords, EditDistances::codePointCount, EditDistances::codePointCount, EditDistances::editDistance),
				EditDistances.substringEditDistance(pTitleWords, titleWords, EditDistances::codePointCount, EditDistances::codePointCount, EditDistances::editDistance)
			);
	}
	
}
