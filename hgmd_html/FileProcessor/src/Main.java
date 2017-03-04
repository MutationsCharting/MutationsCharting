/**
 * Created by Aleksandr Tukallo on 03.03.17.
 */
public class Main {
    public static void main(String[] args) {
        HgmdHtmlProcessor hgmd = new HgmdHtmlProcessor();
        hgmd.processFile(
                "./../../hgmd_html.txt",
                "./../../hgmd_html_processed_new.txt");

        ClinvarProcessor clinvarHg38 = new ClinvarProcessor();
        clinvarHg38.processFile(
                "./../../clinvar_hg38.vcf",
                "./../../clinvar_hg38_processed_new.txt");

        ClinvarProcessor clinvarHg19 = new ClinvarProcessor();
        clinvarHg19.processFile(
                "./../../clinvar_hg19.vcf",
                "./../../clinvar_hg19_processed_new.txt");
    }

}
