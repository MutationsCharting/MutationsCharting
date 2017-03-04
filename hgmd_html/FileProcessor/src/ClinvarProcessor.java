/**
 * Created by Aleksandr Tukallo on 03.03.17.
 */
public class ClinvarProcessor extends AbstractFileProcessor {

    @Override
    protected String getChromosome(String curLine) {
        return substring(0, '\t', curLine);
    }

    @Override
    protected String getGeneName(String curLine) {
        int index = curLine.indexOf("GENEINFO=");
        return substring(index + "GENEINFO=".length(),
                ':', curLine);
    }

    @Override
    protected String getPositionInGenome(String curLine) {
        int index = curLine.indexOf("\t");
        return substring(index + 1, '\t', curLine);
    }

    @Override
    protected String getRsIndex(String curLine) {
        int index = skipTabs(curLine, 2);
        return substring(index, '\t', curLine);
    }

    @Override
    protected String getRef(String curLine) {
        int index = skipTabs(curLine, 3);
        return substring(index, '\t', curLine);
    }

    @Override
    protected String getAlt(String curLine) {
        int index = skipTabs(curLine, 4);
        return substring(index, '\t', curLine);
    }

    /**
     * There is no cDNA in vcf file unfortunately
     */
    @Override
    protected String getCDNA(String curLine) {
        return "NA";
    }

    @Override
    protected String getGDNA(String curLine) {
        int index = curLine.indexOf("CLNHGVS=");
        return substring(index + "CLNHGVS=".length(),
                ';', curLine);
    }
}
