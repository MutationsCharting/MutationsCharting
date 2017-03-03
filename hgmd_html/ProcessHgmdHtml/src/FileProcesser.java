import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Aleksandr Tukallo on 03.03.17.
 */
public class FileProcesser {

    private final static String startOfNucleotideChange = " c.";

    private static String getThreeColumns(String curLine) {
        StringBuilder outString = new StringBuilder();

        int spaceCounter = 0;
        for (int i = 0; i < curLine.length(); i++) {
            if (curLine.charAt(i) == ' ') {
                spaceCounter++;
                //if no rs
                if (spaceCounter == 2
                        && curLine.charAt(i + 1) == ' ') {
                    outString.append("\tNA");
                    break;
                }

                //if there is rs, but already near 4th column
                if (spaceCounter == 3) {
                    break;
                }
                //string continues
                outString.append("\t");
            } else {
                outString.append(curLine.charAt(i));
            }
        }

        String ret = outString.toString();
        if (numberOfOccurrences(ret, "Chr") != 1) {
            throw new AssertionError("Chromosome coordinates not found");
        }
        return outString.toString();
    }

    /**
     * Method processes given input file and writes to output file only three columns for every row:
     * Gene name, coordinates of mutation relative to the start of chromosome, rs index
     */
    public static void writeThreeColumns(String inputFileName, String outputFileName) {
        Path input = Paths.get(inputFileName);
        Path output = Paths.get(outputFileName);

        try (BufferedReader bufferedReader = Files.newBufferedReader(input, StandardCharsets.UTF_8);
             PrintWriter outWriter = new PrintWriter(
                     new OutputStreamWriter(
                             new FileOutputStream(output.toString()),
                             StandardCharsets.UTF_8))) {
            outWriter.println("gene.name\tcoordinates.in.chromosome\trs.index");
            String curLine;
            while ((curLine = bufferedReader.readLine()) != null) {
                outWriter.println(getThreeColumns(curLine));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method processes given input file and writes to output file four columns for every row:
     * Gene name, coordinates of mutation relative to the start of chromosome, rs index, nucleotide change
     * <p>
     * Some rows do not have information about nucleotide change (0.002% in given data).
     * They are not changed and are written to odd output file for further analysis.
     * They are also written to usual output file with nucleotide change having value NA
     */
    static void writeFourColumns(String inputFileName, String outputFileName, String oddOutputFileName) {
        Path input = Paths.get(inputFileName);
        Path output = Paths.get(outputFileName);
        Path oddOutput = Paths.get(oddOutputFileName);

        try (BufferedReader bufferedReader = Files.newBufferedReader(input, StandardCharsets.UTF_8);
             PrintWriter outWriter = new PrintWriter(
                     new OutputStreamWriter(
                             new FileOutputStream(output.toString()),
                             StandardCharsets.UTF_8));
             PrintWriter oddWriter = new PrintWriter(
                     new OutputStreamWriter(
                             new FileOutputStream(oddOutput.toString()),
                             StandardCharsets.UTF_8))) {
            outWriter.println("line.num\tgene.name\tcoordinates.in.chromosome\trs.index\tnucleotide.change");
            String curLine;
            int counter = 0;
            while ((curLine = bufferedReader.readLine()) != null) {
                counter++;

                String nucleotideChange;
                //if odd row
                if (numberOfOccurrences(curLine, startOfNucleotideChange) == 0) {
                    nucleotideChange = "NA";
                    oddWriter.println(curLine);
                } else {
                    nucleotideChange = getCodonChange(curLine);
                }
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.toString(counter));
                sb.append("\t");
                sb.append(getThreeColumns(curLine));
                sb.append("\t");
                sb.append(nucleotideChange);
                outWriter.println(sb.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //method is used to check code invariants
    private static int numberOfOccurrences(String text, String substring) {
        int count = 0;
        int lastIndex = 0;

        while (lastIndex != -1) {
            lastIndex = text.indexOf(substring, lastIndex);
            if (lastIndex != -1) {
                count++;
                lastIndex += substring.length();
            }
        }
        return count;
    }

    /**
     * @param curString String in which nucleotide change is searched for.
     *                  Pre: startOfNucleotideChange is met >= 0 times in curString.
     * @return String with nucleotide change is returned
     */
    private static String getCodonChange(String curString) {
        //inv of code below: such substring is encountered once
        if (numberOfOccurrences(curString, startOfNucleotideChange) != 1) {
            throw new AssertionError("Nucleotide change substring is met not once");
        }

        int occ = curString.indexOf(startOfNucleotideChange);
        occ++; //to get rid from space

        StringBuilder nucleotideChange = new StringBuilder();
        for (int i = occ; ; i++) {
            if (curString.charAt(i) != ' ') {
                nucleotideChange.append(curString.charAt(i));
            } else {
                break;
            }
        }
        return nucleotideChange.toString();
    }
}
