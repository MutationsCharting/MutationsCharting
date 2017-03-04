/**
 * Created by Aleksandr Tukallo on 03.03.17.
 */
public class ClinvarProcessor extends AbstractFileProcessor {

    private final boolean isHg19;

    public ClinvarProcessor(boolean isHg19) {
        this.isHg19 = isHg19;
    }

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
        return NA;
    }

    @Override
    protected String getGDNA(String curLine) {
        int index = curLine.indexOf("CLNHGVS=");
        return substring(index + "CLNHGVS=".length(),
                ';', curLine);
    }

    @Override
    protected String getRefUsedForPosition(String curLine) {
        return isHg19 ? "hg19" : "hg38";
    }

    @Override
    protected String getCoordsInHg19(String curLine) {
        return isHg19 ? getPositionInGenome(curLine) : NA;
    }

    @Override
    protected String getCoordsInHg38(String curLine) {
        return isHg19 ? NA : getPositionInGenome(curLine);
    }
}
