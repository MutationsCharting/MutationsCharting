package converter;

/**
 * Created by Aleksandr Tukallo on 03.03.17.
 */
public class ConvertToSameFormat {
    public static void main(String[] args) {
        HgmdHtmlProcessor hgmd = new HgmdHtmlProcessor();
        hgmd.processFile(
                "./../../hgmd_html.txt",
                "./../../temporary/hgmd_html_processed.txt");

        ClinvarProcessor clinvarHg38 = new ClinvarProcessor(false);
        clinvarHg38.processFile(
                "./../../clinvar_hg38.vcf",
                "./../../temporary/clinvar_hg38_processed.txt");

        ClinvarProcessor clinvarHg19 = new ClinvarProcessor(true);
        clinvarHg19.processFile(
                "./../../clinvar_hg19.vcf",
                "./../../temporary/clinvar_hg19_processed.txt");
    }

}
