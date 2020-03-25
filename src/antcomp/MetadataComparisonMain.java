/* This Source Code Form is subject to the terms of the hermA Licence.
 * If a copy of the licence was not distributed with this file, You have
 * received this Source Code Form in a manner that does not comply with
 * the terms of the licence.
 */
package antcomp;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class MetadataComparisonMain {
	
	private static final Function<? super String, String[]> SIMPLE_TITLE_SPLITTER = Pattern.compile("\\p{Z}+")::split;
	
	public static void main(final String[] args) {
		if ((args.length != 3) && (args.length != 5)) {
			printUsage();
			System.exit(1);
			return;
		}
		
		final boolean raw;
		switch (args[0]) {
			case "raw":
				raw = true;
				break;
			case "decide":
				raw = false;
				break;
			default:
				System.err.println("unknown mode, must be \"raw\" or \"decide\"");
				System.exit(1);
				return;
		}
		
		final long authorThreshold;
		final long titleThreshold;
		if (args.length > 3) {
			authorThreshold = parseThreshold(args[3]);
			titleThreshold = parseThreshold(args[4]);
			if ((authorThreshold < 0L) || (titleThreshold < 0L)) {
				System.exit(1);
				return;
			}
		} else {
			authorThreshold = 2L;
			titleThreshold = 2L;
		}
		
		final FileSystem fs = FileSystems.getDefault();
		
		final Path metadataFile = makePath(fs, args[1]);
		final Path outputFile = makePath(fs, args[2]);
		
		final ArrayList<MetadataLine> metadata;
		try {
			metadata = loadMetadata(metadataFile);
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
		
		try (final BufferedWriter writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
			if (raw)
				compareAllPairsRaw(metadata, writer);
			else
				compareAllPairsDecide(metadata, writer, authorThreshold, titleThreshold);
			writer.flush();
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	private static void printUsage() {
		System.err.println("expecting three or five arguments:");
		System.err.println("mode (\"raw\" or \"decide\")");
		System.err.println("metadata input file");
		System.err.println("output file name");
		System.err.println();
		System.err.println("optional (ignored if mode is \"raw\"):");
		System.err.println("author threshold (default: 2)");
		System.err.println("title threshold (default: 2)");
	}
	
	private static long parseThreshold(final String thresholdString) {
		final long result;
		try {
			result = Long.parseLong(thresholdString);
		} catch (final NumberFormatException e) {
			System.err.println("Threshold string is not a valid number (or out of range): " + thresholdString);
			return -1;
		}
		if (result < 0L)
			System.err.println(thresholdString + " is not a valid threshold. Thresholds must be >= 0.");
		return result;
	}
	
	private static Path makePath(final FileSystem fs, final String pathString) {
		return fs.getPath(pathString).toAbsolutePath().normalize();
	}
	
	private static ArrayList<MetadataLine> loadMetadata(final Path metadataFile) throws IOException {
		try (final Stream<String> lines = Files.lines(metadataFile, StandardCharsets.UTF_8)) {
			return lines.map(MetadataComparisonMain::parseLine).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
		}
	}
	
	private static void compareAllPairsRaw(final ArrayList<MetadataLine> metadata, final Appendable appendable) throws IOException {
		final ToAppenableDistancesOutput output = new ToAppenableDistancesOutput(appendable);
		compareAllPairs(metadata, new OutputDistancesMetadataComparer(SIMPLE_TITLE_SPLITTER, output), output);
	}
	
	private static void compareAllPairsDecide(final ArrayList<MetadataLine> metadata, final Appendable appendable, final long authorThreshold, final long titleThreshold) throws IOException {
		for (final MetadataLine metadataLine : metadata)
			appendable.append(metadataLine.getFilename()).append('\n');
		appendable.append('\n');
		
		final ToAppendableFulltextComparisonPlanOutput output2 = new ToAppendableFulltextComparisonPlanOutput(appendable);
		compareAllPairs(metadata, new OutputPairsForFullTextComparisonMetadataComparer(SIMPLE_TITLE_SPLITTER, authorThreshold, titleThreshold, output2), output2);
	}
	
	private static void compareAllPairs(final ArrayList<MetadataLine> metadata, final MetadataComparer metadataComparer, final Output output) {
		final int n = metadata.size();
		for (int j = 1; j < n; j++) {
			output.setSecondIndex(j);
			metadataComparer.load(metadata.get(j));
			for (int i = 0; i < j; i++) {
				output.setFirstIndex(i);
				metadataComparer.compareWith(metadata.get(i));
			}
		}
	}
	
	private static MetadataLine parseLine(final String line) {
		final int endAuthor = line.indexOf('\t');
		if (endAuthor < 0)
			throw new IllegalArgumentException("line has wrong format: " + line);
		final int startTitle = endAuthor + 1;
		final int endTitle = line.indexOf('\t', startTitle);
		if (endTitle < 0)
			throw new IllegalArgumentException("line has wrong format: " + line);
		final int startPath = endTitle + 1;
		if (line.indexOf('\t', startPath) < 0)
			return new MetadataLine(line.substring(0, endAuthor), line.substring(startTitle, endTitle), line.substring(startPath));
		throw new IllegalArgumentException("line has wrong format: " + line);
	}
	
}
