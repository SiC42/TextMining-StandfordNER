package mapping;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Repräsentiert einzelne Personen-Einträge des Wörterbuchs.
 *
 * @author Simon Bordewisch
 */
public class PersonEntry extends Entry {

    /**
     * Voller Name der Person
     */
    String fullName;

    /**
     * Liste der Namen.
     */
    LinkedList<String> names;


    /**
     * Maximale Anzahl der fehlenden Wörter im Teilsatz, um noch als Match zu gelten
     */
    public final int MAX_MISSING_WORDS = 2;


    /**
     * Konstruiert Eintrag mit Liste von Wörtern.
     * Übergibt Liste und Kategorie.
     *
     * @param wordsList Liste von Wörtern
     * @param category  Kategorie der Wörter
     */
    public PersonEntry(LinkedList<String> wordsList, String category) {
        super(wordsList, category);
    }


    /**
     * Konstruiert Eintrag mit einem einzelnen Wort.
     * Erstellt Liste und ruft
     * {@link #PersonEntry(LinkedList, String) Listen-Kontruktor} auf.
     *
     * @param words    Wort, das als Eintrag hinzugefügt werden soll
     * @param category Kategorie des einzutragenden Wortes
     */
    public PersonEntry(String words, String category) {
        super(words, category);
        setFullName(words);
    }


    /**
     * Konstruiert Eintrag aus Wörter-Array.
     * Wandelt Wörter-Array zu Liste um und ruft
     * {@link #PersonEntry(LinkedList, String) Listen-Kontruktor} auf.
     *
     * @param wordsArray Array mit Wörtern für den Eintrag
     * @param category   Kategorie des Eintrags
     */
    public PersonEntry(String[] wordsArray, String category) {
        super(wordsArray, category);
        names = new LinkedList<>();
        for (String word : wordsArray) {
            names.add(word);
        }
    }


    /**
     * Speichert vollen Namen.
     *
     * @param fullName zu speichernder Name
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
        cleanName(fullName);
    }


    /**
     * Prozedur zur bereinigung des Namens.
     * Siehe {@link #splitName(String)} und {@link #postCleanName(LinkedList)}
     */
    private void cleanName(String fullName) {
        LinkedList<String> words = splitName(fullName);
        words = postCleanName(words);
        this.names = words;
    }


    /**
     * Teilt Wörter in Teil-Namen auf und gibt Liste mit diesen auf.
     * Beispiel: "Karl-Theodor zu Guttenberg" wird zu ["Karl-Theodor", "zu Guttenberg"].
     *
     * @param fullName voller Name der Person
     * @return Liste der Teil-Namen
     */
    private LinkedList<String> splitName(String fullName) {
        LinkedList<String> names = new LinkedList<>(); // Liste zum speichern
        String[] nameSplit = preCleanName(fullName);
        if (nameSplit.length < 3) // i.e. max. nur Vor- & Nachname
        {
            for (String name : nameSplit) {
                names.add(name);
            }
        } else  //mehr Namen, Tests für Anredeprädikate
        {
            //Suche Prädikat in Array
            for (int i = 0; i < nameSplit.length; i++) {
                if (nameSplit[i].matches("^[a-z].*")) //gefunden, Stelle markieren & abbrechen
                {
                    return particleParser(nameSplit, i);
                }
            }
            names.addAll(Arrays.asList(nameSplit));

        }
        return names;
    }


    /**
     * Extrhiert Teil-Namen des übergebenen Strings als Array.
     *
     * @param fullName zu extrahierender String
     * @return Array mit Teil-Namen des übergebenen Strings
     */
    private String[] extract(String fullName) {
        LinkedList<String> names = splitName(fullName);
        String[] nameArray = new String[names.size()];
        names.toArray(nameArray);
        return nameArray;
    }

    /**
     * Gibt größes des Eintrags, d.h. die Anzahl der Namen, zurück.
     *
     * @return Anzahl der Wörter im Eintrag
     */
    @Override
    public int size() {
        return names.size();
    }


    /**
     * Vor-Bereinigung des Strings, entfernt Klammern,
     * Abkürzungen (mit ".") und Namen, die nur Sonderzeichen beinhalten.
     *
     * @param fullName String, der vorbereinigt werden soll
     * @return vorbereinigter String als Array
     */
    private String[] preCleanName(String fullName) {
        fullName = fullName.replaceAll("\\(.*\\)", ""); // Klammern entfernen

        String[] nameSplit = fullName.split(" ");
        LinkedList<String> nameSplitList = new LinkedList<>();
        for (String name : nameSplit) {
            if (!name.contains(".") && !name.matches("[^\\p{L}]*") && name.length() > 1)
                nameSplitList.add(name);
        }
        String[] nameArray = new String[nameSplitList.size()];
        nameSplitList.toArray(nameArray);
        return nameArray;
    }


    /**
     * Bereinigt Wörter, die nur klein geschrieben werden
     *
     * @return Liste mit bereinigten Wörtern
     */
    private LinkedList<String> postCleanName(LinkedList<String> words) {
        for (String name : words) {
            if (name.matches("^[a-zäöü][\\p{L}]*")) {
                words.remove(name);
            }
        }
        return words;
    }

    /**
     * Parst Anredeprädikate (von, zu, ...).
     *
     * @param nameSplit   Array mit zu bearbeitenden Wörtern
     * @param locParticle Zeiger auf die Stelle, in der das Anredeprädikat steht
     * @return Liste, in der die Anredeprädikate und Nachnamen als ein Eintrag gelten
     */
    private LinkedList<String> particleParser(String[] nameSplit, int locParticle) {
        LinkedList<String> names = new LinkedList<>(); // Liste zum speichern
        //Teil des Arrays ohne Prädikat
        for (int i = 0; i < locParticle; i++) {
            names.add(nameSplit[i]);
        }
        String remainName = nameSplit[locParticle];

        //Teil des Arrays mit Prädikat, füge alle Namen zu einem zusammen
        for (int i = locParticle + 1; i < nameSplit.length; i++) {
            remainName += " " + nameSplit[i];
        }
        if (!remainName.isEmpty()) {
            names.add(remainName);
        }
        return names;
    }

    /**
     * Vergleichsfunktion, die einen Teilsatz auf den Eintrag vergleicht und ein Match zurückgibt, falls Ähnlichkeit
     * hoch genug.
     *
     * @param sentencesList zu vergleichender Teilsatz als Liste
     * @return Match-Objekt, falls Match-Wert (@see MIN_MATCH_VALUE) hoch genug, sonst null
     */
    @Override
    public Match compareTo(LinkedList<String> sentencesList) {
        float matchValue = 0;
        int numberOfWords = 0;
        WordComparator comp = new WordComparator();
        for (int i = 0; i < sentencesList.size(); i++) {
            boolean contains = false;
            for (String name : names) {
                if (comp.compare(name, sentencesList.get(i)) == 0) //match
                {
                    contains = true;
                    numberOfWords++;
                    matchValue += comp.getWordSimilarity(name, sentencesList.get(i));
                    //System.out.println(String.format("%s mit %s verglichen, neuer Match Value ist %f",
                    //        name, sentencesList.get(i), matchValue));
                }
            }
            if (!contains) {
                break;
            }
        }
        if (size() > numberOfWords) {
            if (size() > MAX_MISSING_WORDS + numberOfWords) // es fehlen mehr Wörter als dürfen
            {
                return null;
            } else {
                //System.out.println(String.format("Es fehlen %d Wörter", (size() - numberOfWords)));
                matchValue -= MISSING_NAME_PENALITY * (size() - numberOfWords);
            }
        }
        if (numberOfWords > 0) {
            matchValue /= numberOfWords;
        }
        if (matchValue > MIN_MATCH_VALUE) {
            return new Match(numberOfWords, category, getEntryAsString(), matchValue);
        } else {
            return null;
        }
    }

    /**
     * Vergleichsfunktion, die einen Teilsatz auf den Eintrag vergleicht und ein Match zurückgibt, falls Ähnlichkeit
     * hoch genug.
     *
     * @param sentences zu vergleichender Teil-Satz als ein String
     * @return Match-Objekt, falls Match-Wert (@see MIN_MATCH_VALUE) hoch genug, sonst null
     */
    public Match compareTo(String sentences) {
        LinkedList<String> sentencesList = new LinkedList<>(Arrays.asList(sentences.split(" ")));
        return compareTo(sentencesList);
    }


    /**
     * Gibt alle Keys des Wörterbuchs, also das erste Wort der Einträge, als Array aus
     *
     * @return alle Keys des Wörterbuchs als Array
     */
    @Override
    public String[] getKeys() {
        String[] keys = new String[names.size()];
        for (int i = 0; i < names.size(); i++) {
            keys[i] = names.get(i);
        }
        return keys;
    }
}