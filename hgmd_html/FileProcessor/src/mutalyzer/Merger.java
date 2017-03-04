package mutalyzer;

import converter.AbstractFileProcessor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static utils.ParserUtils.skipTabs;
import static utils.ParserUtils.substring;

/**
 * Created by Aleksandr Tukallo on 04.03.17.
 */
public class Merger {
    static void mergeMutalyzerWithHgmd(String inputMutalyzerName,
                                       String inputHgmdRsProcessedName, String outputFileName) {
        Path inputMutalyzer = Paths.get(inputMutalyzerName);
        Path inputHgmd = Paths.get(inputHgmdRsProcessedName);
        Path outputMutalyzer = Paths.get(outputFileName);

        try (BufferedReader brMutalyzer = Files.newBufferedReader(inputMutalyzer, StandardCharsets.UTF_8);
             PrintWriter outWriter = new PrintWriter(
                     new OutputStreamWriter(
                             new FileOutputStream(outputMutalyzer.toString()),
                             StandardCharsets.UTF_8))) {
            //read header
            brMutalyzer.readLine();
            outWriter.println(AbstractFileProcessor.HEADER);

//            todo mb write not in O(n^2) but in O(n)
            String curMutLine;
            int counter = 1;
            while ((curMutLine = brMutalyzer.readLine()) != null) {
                if (counter % 500 == 0)
                    break;
                counter++;

                try (BufferedReader brHgmd = Files.newBufferedReader(inputHgmd, StandardCharsets.UTF_8)) {
                    String curHgmdLine;
                    while ((curHgmdLine = brHgmd.readLine()) != null) {
                        //if same rs in both files
                        if (substring(skipTabs(curMutLine, 1), '\t', curMutLine).equals(
                                substring(skipTabs(curHgmdLine, 4), '\t', curHgmdLine))) {
                            StringBuilder sb = new StringBuilder();
                            sb.append(curHgmdLine.substring(0, skipTabs(curHgmdLine, 11)));

                            //now should append hg19 coordinates:
                            int index = skipTabs(curMutLine, 3);
                            sb.append(substring(index, '\t', curMutLine));

                            //now should append hg38 coordinates
                            index = skipTabs(curMutLine, 4);
                            sb.append("\t" + substring(index, '\t', curMutLine));

                            outWriter.println(sb.toString());
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
