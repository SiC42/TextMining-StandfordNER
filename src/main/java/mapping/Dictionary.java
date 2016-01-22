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
public class Dictionary
{
  /**
   * Eigentliches Wörterbuch.
   * Beispiel für Aufbau:
   * {"1.": [["FC", "Bayern", "München"],[FC, Köln]]}
   */
  Map<String,LinkedList<Entry>> dictionary;
  TreeSet<String> blacklist;


  /**
   * Gibt Anzahl der Wörtern im längsten Eintrag des Wörterbuchs aus.
   */
  int maxEntrySize;

  private final float MATCH_VALUE = 0.85f;

  public Dictionary()
  {
    dictionary = new TreeMap<String,LinkedList<Entry>>(new WordComparator());
    maxEntrySize = 0;
    blacklist = new TreeSet<>();
  }
  /**
   * Initialisiert leeres Wörterbuch.
   */
  public Dictionary(TreeSet<String> blacklist)
  {
    dictionary = new TreeMap<String,LinkedList<Entry>>(new WordComparator());
    maxEntrySize = 0;
    this.blacklist = blacklist;
  }


  /**
   * Initialisert Wörterbuch mit übergebenen Einträgen.
   * @param entry Einträge, ein Eintrag pro Array-Feldeintrag
   * @param category mit den Einträgen assoziierte Kategorie
   */
  public Dictionary(String entry, String category, TreeSet<String> blacklist)
  {
    this(blacklist);
    add(entry, category);
  }


  /**
   * Initialisert Wörterbuch mit übergebenen Einträgen und der Kategorie.
   * @param  entries Einträge, ein Eintrag pro Array-Feldeintrag
   * @param category mit den Einträgen assoziierte Kategorie
   */
  public Dictionary(String[] entries, String category, TreeSet<String> blacklist)
  {
    this(blacklist);
    add(entries, category);
  }


  /**
   * Initialisert Wörterbuch mit übergebenen Einträgen.
   * @param  entryAndCategory Array, in dem der alle Feldwerte Einträge sind, außer der letzte, welcher die Kategorie angibt
   */
  public Dictionary(String[] entryAndCategory)
  {
    this();
    String[] entry = new String[entryAndCategory.length-1];
    for(int i=0;i<entry.length;i++)
    {
      entry[i] = entryAndCategory[i];
    }
    add(entry, entryAndCategory[entryAndCategory.length-1]);
  }


  /**
   * Gibt die benutzte HashMap des Wörterbuchs aus.
   * @return Wörterbuch
   */
  public Map<String,LinkedList<Entry>> getDictionary()
  {
    return dictionary;
  }


  /**
   * Fügt Einträge und Kategorie zu dem Wörterbuch hinzu
   * @param  entryAndCategory Array, in dem der alle Feldwerte Einträge sind, außer der letzte, welcher die Kategorie angibt
   */
  public void add(String[] entryAndCategory)
  {
    String[] entry = new String[entryAndCategory.length-1];
    for(int i=0;i<entry.length;i++)
    {
      entry[i] = entryAndCategory[i];
    }
    add(entry, entryAndCategory[entryAndCategory.length-1]);
  }


  /**
   * Fügt übergebene Einträge zum Wörterbuch hinzu.
   * @param entries Einträge, ein Eintrag pro Array-Feldeintrag
   * @param category mit den Einträgen assoziierte Kategorie
   */
  public void add(String[] entries, String category)
  {
    for(String entry : entries)
      add(entry, category);
  }


  /**
   * Fügt einzelnen Eintrag zum Wörterbuch hinzu.
   * @param entryStr Eintrag, der hinzugefügt werden soll
   * @param category mit den Einträgen assoziierte Kategorie
   */
  public void add(String entryStr, String category)
  {
   /* String[] splitEntry = entryStr.split(" ");
    updateMaxEntrySize(splitEntry.length);
    LinkedList<String> words = new LinkedList<String>();
    for(int i=0;i<splitEntry.length;i++)
    {
      words.add(splitEntry[i]);
    }*/
    Entry entry;
    switch(category) {
      case "Person":
        entry = new PersonEntry(entryStr, category);
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
    for(String entryKey : entry.getKeys()) {
      if(!blacklist.contains(entryKey) && !entryKey.equals("")) {
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
  }


public boolean contains(String word)
{
  return dictionary.containsKey(word);
}

  /**
   * Gibt Match zurück, wenn ein Eintrag mit den ersten Wörtern des Satzes
   * übereinstimmt.
   * @param sentences Satz, der auf Übereinstimmung geprüft werden soll
   * @return Match, wenn gefunden, sonst null
   */
  public Match findEntryMatch(String sentences)
  {
    String[] sentencesSplit = sentences.split(" ");
    LinkedList<String> sentencesList = new LinkedList<String>();
    for(int i=0;i<sentencesSplit.length;i++)
    {
      sentencesList.add(sentencesSplit[i]);
    }
    return findEntryMatch(sentencesList);
  }


  /**
   * Gibt Match zurück, wenn ein Eintrag mit den ersten Wörtern des Satzes exakt
   * übereinstimmt.
   * @param sentencesList List einzelner Wörter des Satzes, der auf Übereinstimmung geprüft werden soll
   * @return Match, wenn gefunden, sonst null
   */
  public Match findEntryMatch(LinkedList<String> sentencesList)
  {
    LinkedList<Entry> followWordList = dictionary.get(sentencesList.get(0));
    TreeMap<Float, Match> matches = new TreeMap<>();
    if(followWordList != null)
    {
      // Vergleiche jeden Eintrag im zum gefundenen Schlüssel
      for(Entry followWords : followWordList)
      {
        Match match = followWords.compareTo(sentencesList);
        if(match != null)
        {
          matches.put(match.getMatchValue(),match);
          //System.out.println(String.format("Found match for %s: %s",sentencesList, match.getComparedPhrase() ));
        }
      }
    }
    if(matches.size()!=0) {
      return matches.lastEntry().getValue();
    } else {
      return null;
    }
  }



  /**
   * Gibt Anzahl der Wörter des größten Eintrags aus.
   * @return Anzahl der Wörter des größten Eintrags
   */
  public int getMaxEntrySize()
  {
    return maxEntrySize;
  }


  /**
   * Aktualisiert den Wert für die Anzahl der Wörter des größten Eintrags, falls nötig.
   * @param newMax Anzahl der Wörter des neuen Eintrags
   */
  private void updateMaxEntrySize(int newMax)
  {
    if(maxEntrySize < newMax)
    {
      maxEntrySize = newMax;
    }
  }


  /**
   * Gibt eine String-Repräsentation dieses Wörterbuchs zurück.
   * Die String-Repräsentation besteht aus Schlüsselwerten mit Listen von Einträgen, die mit "{}" umschlossen sind.
   * Jedes Schlüsselwert-Listen-Paar wird mit "," getrennt.
   * Für Repräsentation der Keys siehe {@link String#valueOf(Object)}.
   * Für Repäsentation der Einträge siehe {@link Entry#toString()}
   * @return String-Repräsentation dieses Wörterbuchs
   */
  @Override public String toString()
  {
    Iterator it = dictionary.entrySet().iterator();
    String categoryString = "{\n";
    while (it.hasNext()) {
        Map.Entry pair = (Map.Entry)it.next();
        categoryString += pair.getKey() + ": \n";
        LinkedList<Entry> entries = (LinkedList<Entry>)pair.getValue();
        for(Entry entry : entries)
        {
          categoryString += "\t" + entry + "\n";
        }
        categoryString+=",";
    }
    categoryString+="}";
    return categoryString;
  }


  /**
   * Gibt Größe des Wörterbuchs (Anzahl der Schlüsseleinträge) zurück.
   * @return Anzahl der Schlüsselwörter im Wörterbuch
   */
  public int size()
  {
    return dictionary.size();
  }


  /**
   * Hauptsächlich als Test-Methode für diese Klasse gedacht.
   * Enthält hauptsächlich Beispiele, die die Methoden dieser Klasse testen sollen.
   * @param args obsolete
   */
  public static void main(String[] args)
  {
    Dictionary dict = new Dictionary();
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
    dict.add("Kurt Mathias von Leers", "Person");
    dict.add("Kurt Tittel","Person");
    dict.add("Kurt Biedenkopf", "Person");
    String satz = "Kurt Biedenkopf die Vorschläge für eine Reform der Unternehmensmitbestimmung unterbreiten sollte.";
    System.out.println(satz + " || " + dict.findEntryMatch(satz));
  }

}
