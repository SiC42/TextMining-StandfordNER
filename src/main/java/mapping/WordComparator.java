package mapping;

import org.simmetrics.StringDistance;
import org.simmetrics.metrics.StringDistances;

import java.util.Comparator;

/**
 * Vergleicht Wörter nicht auf "exact match", sondern vergleicht die "Prefixe" der Wörter (i.e. Nicht-Suffixe)
 * auf "exact match" und die Suffix-Wörter nur auf Ähnlichkeit.
 * @author Simon Bordewisch
 */
public class WordComparator implements Comparator<String> {

    /**
     * Maximale Längendifferenz beim Vergleich von zwei Strings.
     */
    public final int MAX_LEN_DIFF = 2;


    /**
     * Suffix-Länge, die betrachtet werden soll.
     */
    public final int SUFFIX_SIZE = 3;

    /**
     * Minimaler Ähnlichkeitsgrad um als "Match" zu gelten.
     */
    public final float MIN_MATCH_VALUE = 0.50f;

    /**
     * Vergleicht zwei Strings auf Ähnlichkeit.
     *
     * @param s1 erster zu vergleichender String
     * @param s2 zweiter zu vergleichender String
     * @return 0 wenn ähnlich, sonst die herkömmliche String-Rückgabe der Werte {@link String#compareTo(String)}
     */
    @Override
    public int compare(String s1, String s2) {
        if (getSuffixSimilarity(s1, s2) > MIN_MATCH_VALUE) {
            return 0;
        }
        return s1.compareTo(s2);
    }

    /**
     * Gibt Ähnlichkeitsgrad der Suffixe zurück.
     *
     * @param s1 erster zu vergleichender String
     * @param s2 zweiter zu vergleichender String
     * @return Ähnlichkeitsgrad der Suffixe
     */
    public float getSuffixSimilarity(String s1, String s2) {
        return getSimilarity(s1, s2, SUFFIX_SIZE);
    }

    /**
     * Gibt Ähnlichkeitsgrad der Wörter zurück.
     *
     * @param s1 erster zu vergleichender String
     * @param s2 zweiter zu vergleichender String
     * @return Ähnlichkeitsgrad der Wörter
     */
    public float getWordSimilarity(String s1, String s2) {
        int maxWordLen = Math.max(s1.length(), s2.length());
        return getSimilarity(s1, s2, maxWordLen);
    }

    /**
     * Eigentliche Ähnlichkeitsgrad-Funktion.
     * Prüft zunächst Prefix auf Gleichheit und anschließend Suffix auf Ähnlichkeit.
     *
     * @param s1               erster zu vergleichender String
     * @param s2               zweiter zu vergleichender String
     * @param lengthComparison Länge, die beim Ähnlichkeitsgrad einbezogen werden soll
     * @return
     */
    private float getSimilarity(String s1, String s2, int lengthComparison) {
        int minWordLen = Math.min(s1.length(), s2.length());
        if (Math.abs(s1.length() - s2.length()) < MAX_LEN_DIFF) {
            if (minWordLen < SUFFIX_SIZE) {
                if (s1.equals(s2)) {
                    return 1;
                } else {
                    return 0;
                }
            }
            int maxWordLen = Math.max(s1.length(), s2.length());
            int prefixSize = maxWordLen - SUFFIX_SIZE;
            String prefix1 = s1.substring(0, prefixSize);
            String prefix2 = s2.substring(0, prefixSize);
            if (prefix1.equals(prefix2)) {
                StringDistance metric = StringDistances.levenshtein();
                String suffix1 = s1.substring(prefixSize);
                String suffix2 = s2.substring(prefixSize);
                float distance = metric.distance(suffix1, suffix2);
                float relDistance = 1.0f - (distance / lengthComparison);
                return relDistance;
            }
        }
        return 0;

    }


}
