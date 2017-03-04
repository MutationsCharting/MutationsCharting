import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Aleksandr Tukallo on 04.03.17.
 */
public abstract class AbstractFileProcessor implements FileProcessor {
    protected final String HEADER = "line.num\tchr\tgene.name\tposition.in.genome\trs.index\tref\talt\tcDNA\tgDNA";
    protected final String NA = "NA";

    @Override
    public void processFile(String inputFileName, String outputFileName) {
        Path input = Paths.get(inputFileName);
        Path output = Paths.get(outputFileName);

        try (BufferedReader bufferedReader = Files.newBufferedReader(input, StandardCharsets.UTF_8);
             PrintWriter outWriter = new PrintWriter(
                     new OutputStreamWriter(
                             new FileOutputStream(output.toString()),
                             StandardCharsets.UTF_8))) {

            outWriter.println(HEADER);

            String curLine;
            int counter = 0;
            while ((curLine = bufferedReader.readLine()) != null) {
                //if comment line
                if (curLine.charAt(0) == '#') {
                    continue;
                }

                counter++;
                outWriter.println(getLineToOutput(counter, curLine));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String getLineToOutput(int lineNum, String curLine) {
        return Integer.toString(lineNum) + "\t" +
                getChromosome(curLine) + "\t" +
                getGeneName(curLine) + "\t" +
                getPositionInGenome(curLine) + "\t" +
                getRsIndex(curLine) + "\t" +
                getRef(curLine) + "\t" +
                getAlt(curLine) + "\t" +
                getCDNA(curLine) + "\t" +
                getGDNA(curLine);
    }

    /**
     * Method skips n tabs from start
     *
     * @param curLine line to skip tabs in
     * @param n       number of tabs to skip
     * @return index of first element after n'th tab is returned
     */
    protected int skipTabs(String curLine, int n) {
        int counter = 0;
        int index = 0;
        while (counter < n) {
            index = curLine.indexOf("\t", index);
            index += "\t".length();
            counter++;
        }
        return index;
    }

    /**
     * Method returns substring from beginIndex until terminateSymbol is met
     */
    protected String substring(int beginIndex, char terminateSymbol, String curLine) {
//        System.out.println(Integer.toString(beginIndex) + " " + terminateSymbol + " " + curLine);
        StringBuilder sb = new StringBuilder();
        try {
            while (curLine.charAt(beginIndex) != terminateSymbol) {
                sb.append(curLine.charAt(beginIndex));
                beginIndex++;
            }
        } catch (StringIndexOutOfBoundsException e) {
            int a = 5;
            System.out.println("alsdjf;lasdfj");
        }
        return sb.toString();
    }

    protected abstract String getChromosome(String curLine);

    protected abstract String getGeneName(String curLine);

    protected abstract String getPositionInGenome(String curLine);

    protected abstract String getRsIndex(String curLine);

    protected abstract String getRef(String curLine);

    protected abstract String getAlt(String curLine);

    protected abstract String getCDNA(String curLine);

    protected abstract String getGDNA(String curLine);

    //method is used to check code invariants
    protected static int numberOfOccurrences(String text, String substring) {
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
}
