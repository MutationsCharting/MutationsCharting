package mutalyzer;

/**
 * Created by Aleksandr Tukallo on 04.03.17.
 */
public class MergeMutalyzerWithHgmd {
    public static void main(String[] args) {
        Merger.mergeMutalyzerWithHgmd("../../data_base_from_mutalyzer_sorted.txt",
                "../../temporary/hgmd_html_processed_sorted_rs.txt",
                "../../temporary/hgmd_html_processed_sorted_rs_final.txt");
    }
}
