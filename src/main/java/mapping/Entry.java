package mapping;

import java.util.LinkedList;

/**
 * Repräsentiert einzelne Einträge des Wörterbuchs.
 * @author Simon Bordewisch
 */
public abstract class Entry
{
  /**
   * Liste mit den Wörtern eines Eintrags
   */
  protected LinkedList<String> words;


  /**
   * Kateorie des Eintrags
   */
  String category;


  /**
   * Gibt Strafe an, falls ein Wort des Eintrags nicht in den übergebenem Teilsatz steht.
   * Der Wert wird von dem prozentualem Match-Wert abgezogen.
   */
  public final float MISSING_NAME_PENALITY = 0.1f;


  /**
   * Gibt minimalen Wert an, ab dem ein Wort als Match gilt. Hierbei werden die Suffix-Ähnlichkeiten betrachtet!
   */
  public final float MIN_MATCH_VALUE = 0.5f;


  /**
   * Konstruiert Eintrag mit Liste von Wörtern.
   * Übergibt Liste und Kategorie.
   * @param category Kategorie der Wörter
   */
  public Entry(String category)
  {
    this.words = new LinkedList<>();
    this.category = category;
  }


  /**
   * Konstruiert Eintrag mit Liste von Wörtern.
   * Übergibt Liste und Kategorie.
   * @param wordsList Liste von Wörtern
   * @param category Kategorie der Wörter
   */
  public Entry(LinkedList<String> wordsList, String category)
  {
    this.words = wordsList;
    this.category = category;
  }


  /**
   * Konstruiert Eintrag mit einem einzelnen Wort.
   * Erstellt Liste und ruft
   * {@link #Entry(LinkedList, String) Listen-Kontruktor} auf.
   * @param word Wort, das als Eintrag hinzugefügt werden soll
   * @param category Kategorie des einzutragenden Wortes
   */
  public Entry(String word, String category)
  {
    this.words = new LinkedList<String>();
    this.words.add(word);
    this.category = category;
  }


  /**
   * Konstruiert Eintrag aus Wörter-Array.
   * Wandelt Wörter-Array zu Liste um und ruft
   * {@link #Entry(LinkedList, String) Listen-Kontruktor} auf.
   * @param wordsArray Array mit Wörtern für den Eintrag
   * @param category Kategorie des Eintrags
   */
  public Entry(String[] wordsArray, String category)
  {
    this.words = new LinkedList<String>();
    for(String word : wordsArray)
      this.words.add(word);
    this.category = category;
  }


  /**
   * Ändert Kategorie zu übergebener Kategorie.
   * @param newCategory neue Kategorie
   */
  public void updateCategory(String newCategory)
  {
    this.category = newCategory;
  }


  /**
   * Gibt Liste der Wörter des Eintrags zurück.
   * @return Liste der Wörter des Eintrags
   */
  public LinkedList<String> getWords()
  {
    return words;
  }


  /**
   * Gibt Eintrag in String-Repräsentation aus.
   * Beispiel: ["Gerhard", "Schröder"] wird zu "Gerhard Schröder".
   * @return Eintrag als String-Repräsentation
     */
  public String getEntryAsString()
  {
    String wordString = "";
    for(String word : words)
    {
      wordString+=word + " ";
    }
    return wordString.trim();
  }


  /**
   * Gibt Kategorie des Eintrags zurück.
   * @return Kateorie des Eintrags
   */
  public String getCategory()
  {
    return category;
  }


  /**
   * Gibt größes des Eintrags, d.h. die Anzahl der Wörter, zurück.
   * @return Anzahl der Wörter im Eintrag
   *
   */
  public int size()
  {
    return words.size();
  }

  public String[] getKeys()
  {
    String[] keys = {words.get(0)};
    return keys;
  }


  /**
   * Vergleichsfunktion, die einen Teilsatz auf den Eintrag vergleicht und ein Match zurückgibt, falls Ähnlichkeit
   * hoch genug.
   * @param sentencesList zu vergleichender Teilsatz
   * @return Match-Objekt, falls Match-Wert (@see MIN_MATCH_VALUE) hoch genug, sonst null
   */
  public Match compareTo(LinkedList<String> sentencesList) {
    WordComparator comp = new WordComparator();
    float matchValue = 0;
    //darauf achten, dass der Satz nicht zu kurz ist
    if (size() <= sentencesList.size())
    {
      boolean match = true;
      // Vergleiche jedes Wort des Eintrags mit jedem Wort des Satzes
      for (int i = 0; i < size(); i++) {
        String dictWord = getWords().get(i);
        String sentencesWord = sentencesList.get(i);
        if (comp.compare(dictWord, sentencesWord) != 0) {
          match = false;
        } else {
          matchValue+= comp.getWordSimilarity(dictWord,sentencesWord);
        }
      }
      if (match) {
        String sentences = "";
        for (int i = 0; i < size(); i++) {
          sentences += sentencesList.get(i) + " ";
        }
        return new Match(size(), getCategory(), getEntryAsString(), ((matchValue/size())));
      }
    }
    return null;
  }





  /**
   * Gibt String-Repräsentation des Objekts zurück.
   * Darstellung: Wörter als Liste gefolgt von der Kateorie (mit " = " getrennt).
   * @return String-Repräsentation des Objekts
   */
  @Override public String toString()
  {
    return words + " = " + category;
  }
}
