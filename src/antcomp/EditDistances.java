/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package antcomp;

import java.util.Arrays;
import java.util.function.ToLongBiFunction;
import java.util.function.ToLongFunction;

/**
 * Contains the code for edit distance computations.
 */
public class EditDistances {
	
	/**
	 * Returns the total number of code points
	 * in the given {@link CharSequence}.
	 * 
	 * @param cs
	 * a {@link CharSequence}; not {@code null}
	 * 
	 * @return
	 * the total number of code points in the input
	 */
	public static int codePointCount(final CharSequence cs) {
		return codePointCount(cs, cs.length());
	}
	
	/**
	 * Computes the (classical) edit distance between two character sequences,
	 * that is the smallest number of character insertions, deletions and
	 * substitutions to transform the first sequence into the second one
	 * (or vice versa).
	 * <p>
	 * Such edit distances are symmetric: For any two {@link CharSequence}
	 * instances {@code x} and {@code y}
	 * <pre>editDistance(x, y) == editDistance(y, x)</pre>
	 * will hold (given that the contents of the sequences
	 * do not change during or between the two calls).
	 * </p>
	 * <p>
	 * This implementation is Unicode-aware:
	 * The edit distance is not computed for the sequences of {@code char}
	 * values stored in the two {@link CharSequence} instances,
	 * but for the sequences of <i>code points</i>
	 * (see {@link Character#codePointAt(CharSequence, int)}).
	 * For example, the edit distance between
	 * {@code "\uD83D\uDE00"} (U+1F600, &#x1F600;) and
	 * {@code "\u0061"} (U+0061, a)
	 * is {@code 1}, not {@code 2}, although there are two differences
	 * in terms of {@code char} values.
	 * </p>
	 * 
	 * @param str1
	 * the first character sequence; not {@code null}
	 * 
	 * @param str2
	 * the second character sequence; not {@code null}
	 * 
	 * @return
	 * the smallest number of character insertions, deletions and
	 * substitutions to transform the first sequence into the second one
	 */
	public static long editDistance(final CharSequence str1, final CharSequence str2) {
		final int length1 = str1.length();
		final int length2 = str2.length();
		final int size1 = codePointCount(str1, length1);
		
		final long[] table = new long[size1 + 1];
		
		// Initialisierung
		for (int i = 0; i <= size1; i++)
			table[i] = i;
		
		// Iteration
		int codePoint2;
		for (int j = 0; j < length2; j += Character.charCount(codePoint2)) {
			codePoint2 = Character.codePointAt(str2, j);
			long leftAbove = table[0];
			long left = leftAbove + 1;
			table[0] = left;
			int tableIndex = 0;
			int codePoint1;
			for (int i = 0; i < length1; i += Character.charCount(codePoint1)) {
				codePoint1 = Character.codePointAt(str1, i);
				tableIndex++;
				final long above = table[tableIndex];
				left = (above < left ? above : left) + 1L;
				if (codePoint1 != codePoint2)
					leftAbove += 1L;
				if (leftAbove < left)
					left = leftAbove;
				table[tableIndex] = left;
				leftAbove = above;
			}
		}
		
		// Termination
		return table[size1];
	}
	
	private static int codePointCount(final CharSequence cs, final int length) {
		return Character.codePointCount(cs, 0 , length);
	}
	
	/**
	 * Computes the substring edit distance between two sequences,
	 * with arbitrary cost functions.
	 * 
	 * @param subSequence
	 * the sequence to become a sub-sequence of the other;
	 * not {@code null}
	 * 
	 * @param superSequence
	 * the other sequence; not {@code null}
	 * 
	 * @param insertionCost
	 * the cost for adding an item to the second sequence;
	 * not {@code null}
	 * 
	 * @param deletionCost
	 * the cost for deleting an item from the first sequence;
	 * not {@code null}
	 * 
	 * @param substitutionCost
	 * the cost for replacing an item from the first sequence
	 * to obtain an item from the second sequence;
	 * not {@code null}
	 * 
	 * @return
	 * the total cost of the cheapest combination of
	 * insertions into the second sequence, deletions from
	 * the first sequence and replacements of items
	 * between the sequences to make the first sequence
	 * a sub-sequence of the second sequence
	 */
	public static <U, V> long substringEditDistance(final U[] subSequence, final V[] superSequence, final ToLongFunction<? super V> insertionCost, final ToLongFunction<? super U> deletionCost, final ToLongBiFunction<? super U, ? super V> substitutionCost) {
		final int m = subSequence.length;
		final int n = superSequence.length;
		if (m < n)
			return substringEditDistanceVariant2(subSequence, m, superSequence, n, insertionCost, deletionCost, substitutionCost);
		return substringEditDistanceVariant1(subSequence, m, superSequence, n, insertionCost, deletionCost, substitutionCost);
	}
	
	private static <U, V> long substringEditDistanceVariant1(final U[] subSequence, final int m, final V[] superSequence, final int n, final ToLongFunction<? super V> insertionCost, final ToLongFunction<? super U> deletionCost, final ToLongBiFunction<? super U, ? super V> substitutionCost) {
		final long[] table = new long[n + 1];
		Arrays.fill(table, 0L);
		
		long min = 0L;
		
		// Iteration
		for (final U u : subSequence) {
			final long delCost = deletionCost.applyAsLong(u);
			long leftAbove = table[0];
			long left = leftAbove + delCost;
			table[0] = left;
			min = left;
			int j = 0;
			for (final V v : superSequence) {
				j++;
				final long above = table[j];
				left = left + insertionCost.applyAsLong(v);
				final long abovepc = above + delCost;
				if (abovepc < left)
					left = abovepc;
				leftAbove += substitutionCost.applyAsLong(u, v);
				if (leftAbove < left)
					left = leftAbove;
				table[j] = left;
				leftAbove = above;
				if (left < min)
					min = left;
			}
		}
		
		return min;
	}
	
	private static <U, V> long substringEditDistanceVariant2(final U[] subSequence, final int m, final V[] superSequence, final int n, final ToLongFunction<? super V> insertionCost, final ToLongFunction<? super U> deletionCost, final ToLongBiFunction<? super U, ? super V> substitutionCost) {
		final long[] table = new long[m + 1];
		
		// Initialisierung
		long min = 0;
		for (int i = 0; i < m; i++) {
			table[i] = min;
			min += deletionCost.applyAsLong(subSequence[i]);
		}
		table[m] = min;
		
		// Iteration
		for (final V v : superSequence) {
			final long insCost = insertionCost.applyAsLong(v);
			long leftAbove = table[0];
			long left = 0;
			table[0] = 0;
			int i = 0;
			for (final U u : subSequence) {
				i++;
				final long above = table[i];
				left = left + deletionCost.applyAsLong(u);
				final long abovepc = above + insCost;
				if (abovepc < left)
					left = abovepc;
				leftAbove += substitutionCost.applyAsLong(u, v);
				if (leftAbove < left)
					left = leftAbove;
				table[i] = left;
				leftAbove = above;
			}
			if (left < min)
				min = left;
		}
		
		return min;
	}
	
}
