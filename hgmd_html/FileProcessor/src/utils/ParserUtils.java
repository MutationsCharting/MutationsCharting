package utils;

/**
 * Created by Aleksandr Tukallo on 04.03.17.
 */
public class ParserUtils {

    public static final String NA = "NA";

    //method is used to check code invariants
    public static int numberOfOccurrences(String text, String substring) {
        int count = 0;
        int lastIndex = 0;

        while (lastIndex != -1) {
            lastIndex = text.indexOf(substring, lastIndex);
            if (lastIndex != -1) {
                count++;
                lastIndex += substring.length();
            }
        }
        return count;
    }

    /**
     * Method skips n tabs from start
     *
     * @param curLine line to skip tabs in
     * @param n       number of tabs to skip
     * @return index of first element after n'th tab is returned
     */
    public static int skipTabs(String curLine, int n) {
        int counter = 0;
        int index = 0;
        while (counter < n) {
            index = curLine.indexOf("\t", index);
            index += "\t".length();
            counter++;
        }
        return index;
    }

    /**
     * Method returns substring from beginIndex until terminateSymbol is met
     */
    public static String substring(int beginIndex, char terminateSymbol, String curLine) {
        StringBuilder sb = new StringBuilder();
        while (beginIndex != curLine.length() && curLine.charAt(beginIndex) != terminateSymbol) {
            sb.append(curLine.charAt(beginIndex));
            beginIndex++;
        }
        return sb.toString();
    }
}
