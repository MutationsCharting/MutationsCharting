package separator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static utils.ParserUtils.NA;
import static utils.ParserUtils.skipTabs;
import static utils.ParserUtils.substring;


/**
 * Created by Aleksandr Tukallo on 04.03.17.
 */
public class HgmdSeparator {
    private static boolean isRsNA(String curLine) {
        int index = skipTabs(curLine, 4);
        return substring(index, '\t', curLine).equals(NA);
    }

    /**
     * Method separates inputFile (ie hgmd_html_processed) into two: first with all the mutations, where there is rs,
     * second, where there is no rs
     */
    public static void separateFileInTwo(String inputFileName, String outputRsName, String outputNoRsName) {
        Path input = Paths.get(inputFileName);
        Path outputRs = Paths.get(outputRsName);
        Path outputNoRs = Paths.get(outputNoRsName);

        try (BufferedReader bufferedReader = Files.newBufferedReader(input, StandardCharsets.UTF_8);
             PrintWriter outWriterRs = new PrintWriter(
                     new OutputStreamWriter(
                             new FileOutputStream(outputRs.toString()),
                             StandardCharsets.UTF_8));
             PrintWriter outWriterNoRs = new PrintWriter(
                     new OutputStreamWriter(
                             new FileOutputStream(outputNoRs.toString()),
                             StandardCharsets.UTF_8))) {

            String header = bufferedReader.readLine();
            outWriterNoRs.println(header);
            outWriterRs.println(header);

            String curLine;
            int counter = 0;
            while ((curLine = bufferedReader.readLine()) != null) {
                counter++;
                if (isRsNA(curLine)) {
                    outWriterNoRs.println(curLine);
                } else {
                    outWriterRs.println(curLine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
