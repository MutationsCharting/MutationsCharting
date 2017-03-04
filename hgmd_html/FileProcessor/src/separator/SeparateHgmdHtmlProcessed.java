package separator;

/**
 * Created by Aleksandr Tukallo on 04.03.17.
 */
public class SeparateHgmdHtmlProcessed {
    public static void main(String[] args) {
        HgmdSeparator.separateFileInTwo("./../../temporary/hgmd_html_processed_sorted.txt",
                "./../../temporary/hgmd_html_processed_sorted_rs.txt",
                "./../../temporary/hgmd_html_processed_sorted_nors.txt");
    }
}
