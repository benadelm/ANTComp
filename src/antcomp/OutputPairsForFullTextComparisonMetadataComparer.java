/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package antcomp;

import java.util.function.Function;

/**
 * {@link MetadataComparer} implementation that checks
 * author and title distance against given thresholds
 * and outputs those pairs where they are both below
 * their respective threshold, using an
 * {@link IndicesOutput}.
 * <p>
 * Thresholds are <i>inclusive</i>:
 * For example, if the author threshold is {@code 2}
 * and the title threshold is {@code 3},
 * then {@link IndicesOutput#outputIndexPair()}
 * will be called for all pairs where
 * the author name distance is smaller than or equal to {@code 2}
 * and the title distance is smaller than or equal to {@code 3}.
 * </p>
 * <p>
 * If one distance is already above its threshold,
 * the other distance need not be checked,
 * therefore this implementation may be faster than
 * implementations which have to always compute
 * both distances.
 * </p>
 */
public class OutputPairsForFullTextComparisonMetadataComparer implements MetadataComparer {
	
	private final Function<? super String, String[]> pTitleSplitter;
	private final IndicesOutput pOutput;
	
	private final long pAuthorThreshold;
	private final long pTitleThreshold;
	
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
	 * @param authorThreshold
	 * the threshold for author name distances
	 * 
	 * @param titleThreshold
	 * the threshold for title distances
	 * 
	 * @param output
	 * an {@link IndicesOutput} for outputting index pairs;
	 * not {@code null}
	 */
	public OutputPairsForFullTextComparisonMetadataComparer(final Function<? super String, String[]> titleSplitter, final long authorThreshold, final long titleThreshold, final IndicesOutput output) {
		pTitleSplitter = titleSplitter;
		pOutput = output;
		
		if (authorThreshold < 0L)
			throw new IllegalArgumentException("Author threshold (" + Long.toString(authorThreshold) + ") has to be at least 0.");
		pAuthorThreshold = authorThreshold;
		
		if (titleThreshold < 0L)
			throw new IllegalArgumentException("Title threshold (" + Long.toString(titleThreshold) + ") has to be at least 0.");
		pTitleThreshold = titleThreshold;
	}
	
	@Override
	public void load(final MetadataLine metadataLine) {
		pAuthor = metadataLine.getAuthor();
		pTitle = metadataLine.getTitle();
		
		pTitleWords = pTitleSplitter.apply(pTitle);
	}
	
	@Override
	public void compareWith(final MetadataLine metadataLine) {
		if (authorDistanceOk(metadataLine) && titleDistanceOk(metadataLine))
			pOutput.outputIndexPair();
	}
	
	private boolean authorDistanceOk(final MetadataLine metadataLine) {
		final String author = metadataLine.getAuthor();
		if (pAuthor.equals(author))
			return true;
		
		return EditDistances.editDistance(author, pAuthor) <= pAuthorThreshold;
	}
	
	private boolean titleDistanceOk(final MetadataLine metadataLine) {
		final String title = metadataLine.getTitle();
		if (pTitle.equals(title))
			return true;
		
		final String[] titleWords = pTitleSplitter.apply(title);
		
		return (titleWordsDistance(titleWords, pTitleWords) <= pTitleThreshold)
				|| (titleWordsDistance(pTitleWords, titleWords) <= pTitleThreshold);
	}
	
	private long titleWordsDistance(final String[] title1words, final String[] title2words) {
		return EditDistances.substringEditDistance(title1words, title2words, EditDistances::codePointCount, EditDistances::codePointCount, EditDistances::editDistance);
	}
	
}
