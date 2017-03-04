/**
 * Created by Aleksandr Tukallo on 03.03.17.
 */
public class HgmdHtmlProcessor extends AbstractFileProcessor {

    @Override
    protected String getChromosome(String curLine) {
        int index = curLine.indexOf("Chr");
        return substring(index + 3, ':', curLine);
    }

    @Override
    protected String getGeneName(String curLine) {
        return substring(0, ' ', curLine);
    }

    @Override
    protected String getPositionInGenome(String curLine) {
        int index = curLine.indexOf("Chr");
        while (curLine.charAt(index) != ':') {
            index++;
        }
        index++;
        return substring(index, ' ', curLine);
    }

    /**
     * If no rs in file, "NA" is returned
     */
    @Override
    protected String getRsIndex(String curLine) {
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
            } else {
                if (spaceCounter == 2) {
                    outString.append(curLine.charAt(i));
                }
            }
        }
        return outString.toString();
    }

    /**
     * No ref value in hgmd
     */
    @Override
    protected String getRef(String curLine) {
        return "NA";
    }

    /**
     * No alt value in hgmd
     */
    @Override
    protected String getAlt(String curLine) {
        return "NA";
    }

    /**
     * @param curLine String in which nucleotide change is searched for
     * @return String with nucleotide change is returned if found, else NA
     */
    @Override
    protected String getCDNA(String curLine) {
        final String startOfNucleotideChange = " c.";
        if (numberOfOccurrences(curLine, startOfNucleotideChange) == 0) {
            return "NA";
        }

        int index = curLine.indexOf(startOfNucleotideChange);
        return substring(index + 1, ' ', curLine);
    }

    /**
     * No gDNA value in hgmd
     */
    @Override
    protected String getGDNA(String curLine) {
        return "NA";
    }
}
