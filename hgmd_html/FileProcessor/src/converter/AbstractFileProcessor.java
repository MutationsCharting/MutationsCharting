package converter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Aleksandr Tukallo on 04.03.17.
 */
public abstract class AbstractFileProcessor implements FileProcessor {

    /**
     * Comments about columns:
     * position.in.genome are coordinates from the start of chromosome
     * ref.used.for.pos is either hg19 or hg38. It is reference for which position.in.genome is counted
     * coords.in.hg19 is position in genome for hg19
     * coords.in.hg38 is position in genome for hg38
     * <p>
     * Ref and Alt fields are filled only in ClinVar
     * Codon mutation field is filled only in hgmd
     * <p>
     * I extremely hope, that one of the columns coords.in.hg19, coords.in.hg38 is the same as position.in.genome
     * column
     */
    public static final String HEADER =
            "line.num\tchr\tgene.name\tposition.in.genome\trs.index\tref\talt\tcodon.mutation\tcDNA\tgDNA\t" +
                    "ref.used.for.pos\tcoords.in.hg19\tcoords.in.hg38";

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
                getCodonMutation(curLine) + "\t" +
                getCDNA(curLine) + "\t" +
                getGDNA(curLine) + "\t" +
                getRefUsedForPosition(curLine) + "\t" +
                getCoordsInHg19(curLine) + "\t" +
                getCoordsInHg38(curLine);
    }

    protected abstract String getChromosome(String curLine);

    protected abstract String getGeneName(String curLine);

    protected abstract String getPositionInGenome(String curLine);

    protected abstract String getRsIndex(String curLine);

    protected abstract String getRef(String curLine);

    protected abstract String getAlt(String curLine);

    protected abstract String getCodonMutation(String curLine);

    protected abstract String getCDNA(String curLine);

    protected abstract String getGDNA(String curLine);

    protected abstract String getRefUsedForPosition(String curLine);

    protected abstract String getCoordsInHg19(String curLine);

    protected abstract String getCoordsInHg38(String curLine);
}
