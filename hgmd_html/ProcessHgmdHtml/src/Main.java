/**
 * Created by Aleksandr Tukallo on 03.03.17.
 */
public class Main {
    public static void main(String[] args) {
        FileProcesser.writeFourColumns(
                "./../../hgmd_html.txt",
                "./../../hgmd_html_processed.txt",
                "./../../hgmd_html_processed_odd.txt");
    }

}
