package mapping;

import java.util.*;

import org.simmetrics.StringDistance;
import org.simmetrics.metrics.StringDistances;


/**
 * Stellt Wörterbuch und Vergleichsfunktionen
 * auf diesem Wörterbuch zur Verfügung.
 *
 * @author Simon Bordewisch
 */
public class Dictionary {
    /**
     * Wörterbuch-Daten.
     * Key-Feld enthält erstes Wort der Einträge.
     * LinkedList enthält die Einträge, die mit dem Key-Wort anfangen.
     */
    Map<String, LinkedList<Entry>> dictionary;

    /**
     * Liste mit Wörtern, die nicht als Key hinzugefügt werden sollen
     */
    TreeMap<String, String> blacklist;


    /**
     * Gibt Anzahl der Wörtern im längsten Eintrag des Wörterbuchs aus.
     */
    int maxEntrySize;

    /**
     * Gibt Anzahl der Wörtern im kürzesten Eintrag des Wörterbuchs aus.
     */
    int minEntrySize;

    /**
     * Gibt an, ob eine Person als Person behandelt wird (true), oder generalisiert wird.
     * Bei der Generalisierung werden die Namen der Personen nicht nach Vor- und Nachname getrennt,
     * sondern als ganzes behandelt.
     */
    boolean personFlag;

    /**
     * Regulärer Ausdruck für alle Zeichen, die nicht Buchstaben oder Leerzeichen sind.
     * I.e. alle Satzzeichen außer Leerzeichen.
     */
    public static final String REPLACE_CHARS = "[.,;'\"()]";


    /**
     * Initialisiert leeres Wörterbuch mit leerer Blacklist.
     */
    public Dictionary(boolean personFlag) {
        this.personFlag = personFlag;
        dictionary = new TreeMap<>(new WordComparator());
        maxEntrySize = 0;
        minEntrySize = Integer.MAX_VALUE;
        blacklist = new TreeMap<>(new WordComparator());
    }

    /**
     * Initialisiert leeres Wörterbuch. Blacklist wird mit parameter gefüllt.
     *
     * @param blacklist Liste der Wörter, die nicht als Key im Wörterbuch aufgenommen werden sollen
     */
    public Dictionary(TreeMap<String, String> blacklist, boolean personFlag) {
        this(personFlag);
        this.blacklist.putAll(blacklist);
    }


    /**
     * Gibt die benutzte HashMap des Wörterbuchs aus.
     *
     * @return Wörterbuch
     */
    public Map<String, LinkedList<Entry>> getDictionary() {
        return dictionary;
    }


    /**
     * Fügt Einträge und Kategorie zu dem Wörterbuch hinzu
     *
     * @param entryAndCategory Array, in dem der alle Feldwerte Einträge sind, außer der letzte, welcher die Kategorie angibt
     */
    public void add(String[] entryAndCategory) {
        String[] entry = new String[entryAndCategory.length - 1];
        for (int i = 0; i < entry.length; i++) {
            entry[i] = entryAndCategory[i];
        }
        add(entry, entryAndCategory[entryAndCategory.length - 1]);
    }


    /**
     * Fügt übergebene Einträge zum Wörterbuch hinzu.
     *
     * @param entries  Einträge, ein Eintrag pro Array-Feldeintrag
     * @param category mit den Einträgen assoziierte Kategorie
     */
    public void add(String[] entries, String category) {
        for (String entry : entries)
            add(entry, category);
    }


    /**
     * Fügt einzelnen Eintrag zum Wörterbuch hinzu.
     *
     * @param entryStr Eintrag, der hinzugefügt werden soll
     * @param category mit den Einträgen assoziierte Kategorie
     */
    public void add(String entryStr, String category) {
        Entry entry;
        switch (category) {
            case "Person":
                if (personFlag) {
                    entry = new PersonEntry(entryStr, category);
                } else {
                    entry = new Entry(entryStr, category);
                }
                break;
            case "Organisation":
                entry = new OrganisationEntry(entryStr, category);
                break;
            case "Ort":
                entry = new LocationEntry(entryStr, category);
                break;
            default:
                System.out.println(String.format("Konnte nicht eingefügt werden, Kategorie %s unbekannt.", category));
                return;
        }
        if(entry.size() > 0) {
            for (String entryKey : entry.getKeys()) {
                if (!blacklist.containsKey(entryKey) && !entryKey.equals("")) { //Blacklist-Wörter & leere Wörter abfangen
                    if (dictionary.containsKey(entryKey)) //erstes Wort als Schlüssel im Wörterbuch
                    {
                        dictionary.get(entryKey).add(entry);
                    } else //erstes Wort des Eintrags nicht im Wörterbuch
                    {
                        //erstelle neue Liste von Einträgen und füge sie zum Wörterbuch hinzu
                        LinkedList<Entry> followWords = new LinkedList<>();
                        followWords.add(entry);
                        dictionary.put(entryKey, followWords);
                    }
                }
            }
            updateMaxEntrySize(entry.size());
            updateMinEntrySize(entry.size());
        }
    }


    /**
     * Gibt true zurück, wenn übergebenes Wort als Key (erstes Wort) im Wörterbuch steht.
     *
     * @param word Wort, das überprüft werden soll
     * @return true, wenn Wort im Wörterbuch, false sonst
     */
    public boolean contains(String word) {
        return dictionary.containsKey(word.replaceAll(REPLACE_CHARS, ""));
    }

    /**
     * Gibt alle Entries zu einem Übergebenen Wort zurück
     *
     * @param word zu untersuchendes Wort
     * @return Liste der Einträge, die dieses Wort (als erstes Wort) enthalten.
     */
    public LinkedList<Entry> getEntries(String word) {
        return dictionary.get(word);
    }


    /**
     * Gibt Match zurück, wenn ein Eintrag mit den ersten Wörtern des Satzes
     * übereinstimmt.
     *
     * @param sentences Satz, der auf Übereinstimmung geprüft werden soll
     * @return Match, wenn gefunden, sonst null
     */
    public Match findEntryMatch(String sentences) {
        String[] sentencesSplit = sentences.split(" ");
        LinkedList<String> sentencesList = new LinkedList<String>();
        for (int i = 0; i < sentencesSplit.length; i++) {
            sentencesList.add(sentencesSplit[i]);
        }
        return findEntryMatch(sentencesList);
    }


    /**
     * Gibt Match zurück, wenn ein Eintrag mit den ersten Wörtern des Satzes exakt
     * übereinstimmt.
     *
     * @param sentencesList List einzelner Wörter des Satzes, der auf Übereinstimmung geprüft werden soll
     * @return Match, wenn gefunden, sonst null
     */
    public Match findEntryMatch(LinkedList<String> sentencesList) {
        LinkedList<String> sentencesListCleared = new LinkedList<>();
        for(String word : sentencesList)
        {
            sentencesListCleared.add(word.replaceAll(REPLACE_CHARS, ""));
        }
        LinkedList<Entry> followWordList = dictionary.get(sentencesListCleared.get(0));
        TreeMap<Float, Match> matches = new TreeMap<>();
        if (followWordList != null) {
            // Vergleiche jeden Eintrag im zum gefundenen Schlüssel
            for (Entry followWords : followWordList) {
                Match match = followWords.compareTo(sentencesListCleared);
                if(match !=null && match.getMatchValue()!=1.0f)
                {
                    if(blacklist.containsKey(sentencesListCleared.peekFirst()))
                        match = null;
                }
                if (match != null) {
                    boolean betterMatch = false;
                    if (matches.containsKey(match.getMatchValue())) {
                        Match compareMatch = matches.get(match.getMatchValue());
                        if (compareMatch.getNumberOfWords() > match.getNumberOfWords()) {
                            betterMatch = true;
                        }
                    }
                    if (!betterMatch) {
                        matches.put(match.getMatchValue(), match);
                    }
                }
            }
        }
        if (matches.size() != 0) {
            return matches.lastEntry().getValue();
        } else {
            return null;
        }
    }


    /**
     * Gibt Anzahl der Wörter des größten Eintrags aus.
     *
     * @return Anzahl der Wörter des größten Eintrags
     */
    public int getMaxEntrySize() {
        return maxEntrySize;
    }


    /**
     * Gibt Anzahl der Wörter des kleinsten Eintrags aus.
     *
     * @return Anzahl der Wörter des kleinsten Eintrags
     */
    public int getMinEntrySize() {
        return minEntrySize;
    }


    /**
     * Aktualisiert den Wert für die Anzahl der Wörter des größten Eintrags, falls nötig.
     *
     * @param newMax Anzahl der Wörter des neuen Eintrags
     */
    private void updateMaxEntrySize(int newMax) {
        if (maxEntrySize < newMax) {
            maxEntrySize = newMax;
        }
    }

    /**
     * Aktualisiert den Wert für die Anzahl der Wörter des kleinsten Eintrags, falls nötig.
     *
     * @param newMin Anzahl der Wörter des neuen Eintrags
     */
    private void updateMinEntrySize(int newMin) {
        if (minEntrySize > newMin) {
            minEntrySize = newMin;
        }
    }


    /**
     * Gibt eine String-Repräsentation dieses Wörterbuchs zurück.
     * Die String-Repräsentation besteht aus Schlüsselwerten mit Listen von Einträgen, die mit "{}" umschlossen sind.
     * Jedes Schlüsselwert-Listen-Paar wird mit "," getrennt.
     * Für Repräsentation der Keys siehe {@link String#valueOf(Object)}.
     * Für Repäsentation der Einträge siehe {@link Entry#toString()}
     *
     * @return String-Repräsentation dieses Wörterbuchs
     */
    @Override
    public String toString() {
        Iterator it = dictionary.entrySet().iterator();
        String categoryString = "{\n";
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            categoryString += pair.getKey() + ": \n";
            LinkedList<Entry> entries = (LinkedList<Entry>) pair.getValue();
            for (Entry entry : entries) {
                categoryString += "\t" + entry + "\n";
            }
            categoryString += ",";
        }
        categoryString += "}";
        return categoryString;
    }


    /**
     * Gibt Größe des Wörterbuchs (Anzahl der Schlüsseleinträge) zurück.
     *
     * @return Anzahl der Schlüsselwörter im Wörterbuch
     */
    public int size() {
        return dictionary.size();
    }


    /**
     * Hauptsächlich als Test-Methode für diese Klasse gedacht.
     * Enthält hauptsächlich Beispiele, die die Methoden dieser Klasse testen sollen.
     *
     * @param args obsolete
     */
    public static void main(String[] args) {
        Dictionary dict = new Dictionary(true);
    /*dict.add("1. FC Köln", "Organisation");
    dict.add("1. FC Bayern München", "Organisation");
    dict.add("Club der Denker", "Organisation");
    dict.add("Angela Merkel", "Person");
    dict.add("Gerhard Schröder", "Person");
    dict.add("Schroder", "Organisation");
    dict.add("Joachim Deutschland", "Person");
    dict.add("Deutschland", "Ort");
    dict.add("Hans am Ende", "Person");
    dict.add("BASF", "Organisation");
    System.out.println(dict);
    System.out.println("Maximale Eintragsgröße: " + dict.getMaxEntrySize());
    System.out.println("====================");
    System.out.println("Der Dümmer ist groß. || " + dict.findEntryMatch("Der Dümmer ist groß"));
    System.out.println("Club der Denker. Er wurde überfallen || " + dict.findEntryMatch("Club der Denker. Er wurde überfallen"));
    System.out.println("1. FC Köln ist fantastisch || " + dict.findEntryMatch("1. FC Köln ist fantastisch"));
    System.out.println("BASF ist eines der großen Unternehmen || " + dict.findEntryMatch("BASF ist eines der großen Unternehmen"));
    System.out.println("Merkels Geburtstag war großartig || " + dict.findEntryMatch("Angela Merkels Geburtstag war großartig"));
    System.out.println("Gerhard Schröders Geburtstag war großartig || " + dict.findEntryMatch("Gerhard Schröders Geburtstag war großartig"));
    System.out.println("Schröders Geburtstag war großartig || " + dict.findEntryMatch("Schröders Geburtstag war großartig"));
    System.out.println("Schroders Geburtstag war großartig || " + dict.findEntryMatch("Schroders Geburtstag war großartig"));
    System.out.println("====================");*/
        dict.blacklist.put("Der", "");
        dict.add("Angela Merkel", "Person");
        dict.add("Merkur", "Ort");
        dict.add("Der Schwabenhansel", "Person");

        System.out.println(dict.getDictionary());
    }

}
