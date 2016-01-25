package mapping;

import java.util.LinkedList;

/**
 * Repräsentiert einzelne Ortseinträge des Wörterbuchs
 */
public class LocationEntry extends Entry {

    /**
     * Konstruiert Eintrag mit Liste von Wörtern.
     * Übergibt Liste und Kategorie.
     *
     * @param wordsList Liste von Wörtern
     * @param category  Kategorie der Wörter
     */
    public LocationEntry(LinkedList<String> wordsList, String category) {
        super(wordsList, category);
    }


    /**
     * Konstruiert Eintrag mit einem einzelnen Wort.
     * Erstellt Liste und ruft
     * {@link #LocationEntry(LinkedList, String) Listen-Kontruktor} auf.
     *
     * @param words Wort, das als Eintrag hinzugefügt werden soll
     * @param category Kategorie des einzutragenden Wortes
     */
    public LocationEntry(String words, String category) {
        super(words,category);
    }


    /**
     * Konstruiert Eintrag aus Wörter-Array.
     * Wandelt Wörter-Array zu Liste um und ruft
     * {@link #LocationEntry(LinkedList, String) Listen-Kontruktor} auf.
     *
     * @param wordsArray Array mit Wörtern für den Eintrag
     * @param category   Kategorie des Eintrags
     */
    public LocationEntry(String[] wordsArray, String category) {
        super(wordsArray, category);
    }
}
