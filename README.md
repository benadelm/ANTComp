# ANTComp

An implementation of a heuristic approach to identifying possible duplicates (or duplicate candidates) based on author name and title similarity in a corpus of literary works, developed in the research project [hermA](https://www.herma.uni-hamburg.de/en.html). The heuristic can for example be used to perform a (relatively fast) reduction of the amount of works to be more thoroughly compared using the (computationally much more expensive) [BatchSED](https://github.com/benadelm/BatchSED).

Given a list of author names and titles, the program compares all (unordered) pairs of list entries by computing the following distance measures:

* the *edit distance* between author names: the smallest number of character (letter) insertions, deletions and substitutions necessary to transform the first author name into the second author name (or the other way round);
* a modified *substring edit distance* between titles: the smallest number of character (letter) insertions, deletions and substitutions necessary to transform one of the two titles into a *part* of the other, with the additional restriction that whole words must be fully matched (implemented as two substring edit distance computations at word level with zero-cost insertions at the beginning and end of the sequence, character-based standard edit distances as substitution costs and word lengths as insertion/deletion costs otherwise).

The output is either a list of all unordered pairs with the corresponding distance values or a list of all pairs with distance values below a threshold (see below for details).

# Installation

The software has been tested with Windows 10 and Linux. Being written in Java, it should run on any platform Java supports; you will need a Java runtime to run the software. It has been developed and tested with Java 8, but newer versions may also work.

# Input

The program expects either three or five positional command-line arguments. The required first three arguments are:

1. mode flag:
    * `raw` to output distance values for all unordered pairs;
    * `decide` to output only those pairs where author and title distance are below a threshold.
2. path to the input file (absolute or relative to the working directory)
3. path to the output file (absolute or relative to the working directory)

After that two optional arguments can follow (either both of them or none):

4. author threshold
5. title threshold

In `raw` mode these optional arguments are unnecessary and will be ignored. They are only needed in `decide` mode: In `decide` mode the program will output all pairs where the author name distance is smaller than or equal to the author threshold *and* the title distance is smaller than or equal to the title threshold. The default value for both thresholds is 2 (at most two insertions, deletions and substitutions in total), covering most spelling differences (such as *Hermann*/*Herrmann* or *Maier*/*Meier*/*Meyer*). The thresholds have to be whole numbers and negative values are not allowed. An author threshold of 0 (zero) means that the author name has to match exactly; a title threshold of 0 (zero) means that the word sequence of one of the titles has to be an exact sub-sequence of the word sequence of the other title. (Using substring edit distances accounts for cases where some parts, like subtitles, may be missing in one version of the title but not in the other.)

Titles are turned into word sequences by simply splitting where one or more whitespace characters (Unicode category `Z`) occur.

## Input File

The input file is a UTF-8 plain text file with every line corresponding to one list item and contains the following fields, separated by tabulator characters (U+0009):

1. author name
2. title
3. path to a file containing the full text

In `raw` mode the third field is not used (and may be empty; in that case the line ends with a tabulator character).

In `decide` mode the contents of the third field are copied to the output file (but not otherwise interpreted).

Example:

	Aldous Huxley	Brave New World	brave_new_world.txt
	George Orwell	1984	1984.txt
	Lewis Carroll	Alice in Wonderland	alice_in_wonderland.txt

## Output File

The output file is a UTF-8 plain text file.

### `raw` Mode

Every line in the output file corresponds to one unordered pair of lines (list items) from the input file and contains the following fields, separated by tabulator characters (U+0009):

1. 0-based index of the first list item
2. 0-based index of the second list item
3. author name distance
4. title distance

For the example input file above, the output would be:

	0	1	12	4
	0	2	12	12
	1	2	9	4

In this example,

* the author distance between list entry 0 and list entry 1 (*Aldous Huxley* and *George Orwell*) is 12; the title distance (*Brave New World* and *1984*) is 4 (the four digits in *1984* have to be changed to make it a substring of *Brave New World*);
* the author distance between list entry 0 and list entry 2 (*Aldous Huxley* and *Lewis Carroll*) is 12; the title distance (*Brave New World* and *Alice in Wonderland*) is 12;
* the author distance between list entry 1 and list entry 2 (*George Orwell* and *Lewis Carroll*) is 9; the title distance (*1984* and *Alice in Wonderland*) is 4 (again, the four digits in *1984* have to be changed to make it a substring of *Alice in Wonderland*).

### `decide` Mode

The output file has two sections, separated by an empty line.

In the first section, every line contains the full text path of a list item (third field of a line in the input file). The order is the same as in the input file.

In the second section, every line contains two numbers separated by a tabulator character (U+0009). These numbers are the 0-based indices of those list items where author name and title distances are below the thresholds, as described above.

Such an output file can be used as a *comparison plan input file* for [BatchSED](https://github.com/benadelm/BatchSED).

For the example input file above and the default thresholds (2), the output would be:

	brave_new_world.txt
	1984.txt
	alice_in_wonderland.txt
	

The second section is empty because no pair of list items has author name and title distances smaller than or equal to 2.

If the author name threshold is set to 12 and the title threshold to 4, the output will be:

	brave_new_world.txt
	1984.txt
	alice_in_wonderland.txt
	
	0	1
	1	2

Now only the pair *Brave New World* vs. *Alice in Wonderland* is above the thresholds (because the title distance is too high).