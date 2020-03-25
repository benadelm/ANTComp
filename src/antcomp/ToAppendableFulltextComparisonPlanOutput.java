/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package antcomp;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * {@link IndicesOutput} implementation that outputs index pairs
 * by appending them to an {@link Appendable} as one line
 * (terminated by {@code "\n"}) with the two indices separated
 * by {@code "\t"}.
 * <p>
 * The mechanism for loading the indices is an implementation of
 * {@link Output}.
 * </p>
 */
public class ToAppendableFulltextComparisonPlanOutput implements Output, IndicesOutput {
	
	private final Appendable pAppendable;
	
	private String pIndex1String;
	private String pIndex2String;
	
	/**
	 * Initializes a new instance of this class.
	 * 
	 * @param appendable
	 * the {@link Appendable} to append data to; not {@code null}
	 */
	public ToAppendableFulltextComparisonPlanOutput(final Appendable appendable) {
		pAppendable = appendable;
	}
	
	@Override
	public void outputIndexPair() {
		try {
			pAppendable.append(pIndex1String);
			pAppendable.append('\t');
			pAppendable.append(pIndex2String);
			pAppendable.append('\n');
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	@Override
	public void setFirstIndex(final int index) {
		pIndex1String = Integer.toString(index);
	}
	
	@Override
	public void setSecondIndex(final int index) {
		pIndex2String = Integer.toString(index);
	}
	
}
