package mapping;

import org.simmetrics.StringDistance;
import org.simmetrics.metrics.StringDistances;

import java.util.Comparator;

/**
 * Created by SiC on 21.01.2016.
 */
public class WordComparator implements Comparator<String>{
    public final int MAX_LEN_DIFF = 2;
    public final int SUFFIX_SIZE = 3;
    public final float MIN_MATCH_VALUE = 0.50f;
    public int compare(String s1, String s2)
    {
        if(getSuffixSimilarity(s1, s2)>MIN_MATCH_VALUE) {
            return 0;
        }
        return s1.compareTo(s2);
    }

    public float getSuffixSimilarity(String s1, String s2)
    {
        return getSimilarity(s1, s2, SUFFIX_SIZE);
    }

    public float getWordSimilarity(String s1, String s2)
    {
        int maxWordLen = Math.max(s1.length(), s2.length());
        return getSimilarity(s1, s2, maxWordLen);
    }
    public float getSimilarity(String s1, String s2, int lengthComparison)
    {
        int minWordLen = Math.min(s1.length(),s2.length());
        if(Math.abs(s1.length() - s2.length()) < MAX_LEN_DIFF) {
            if(minWordLen < SUFFIX_SIZE)
            {
                if(s1.equals(s2))
                {
                    return 1;
                }
                else {
                    return 0;
                }
            }
            int maxWordLen = Math.max(s1.length(), s2.length());
            int prefixSize = maxWordLen - SUFFIX_SIZE;
            String prefix1 = s1.substring(0, prefixSize);
            String prefix2 = s2.substring(0, prefixSize);
            if (prefix1.equals(prefix2)) {
                StringDistance metric = StringDistances.levenshtein();
                String suffix1 = s1.substring(prefixSize + 1);
                String suffix2 = s2.substring(prefixSize + 1);
                float distance = metric.distance(suffix1, suffix2);
                float relDistance = 1.0f - (distance / lengthComparison);
                return relDistance;
            }
        }
        return 0;

    }


}
