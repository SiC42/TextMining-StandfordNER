package mapping;

/**
 * Reine Rückgabe-Klasse, um Anzahl der gematchten Wörter und dazugehörige Kategorie heraus zu finden.
 * @author Simon Bordewisch
 */
public class Match
{

  /**
   * Anzahl der richtig gematchten Wörter, falls ganzer Eintrag matcht.
   */
  int numberOfWords;


  /**
   * Kategorie des richtig gematchten Eintrags.
   */
  String category;

  /**
   * Eintrag, mit dem das Match gefunden wurde.
   */
  String comparedPhrase;


  /**
   * Ähnlichkeitsgrad des Teil-Satzes mit dem Eintrag
   */
  float matchValue;


  /**
   * Konstruiert ein Match mit Anzahl der richtig gematchten Wörter und dazugehörige Kategorie.
   * @param numberOfWords Anzahl der richtig gematchten Wörter
   * @param category dazugehörige Kategorie
   */
  public Match(int numberOfWords, String category, String comparedPhrase, float matchValue)
  {
    this.numberOfWords = numberOfWords;
    this.category = category;
    this.comparedPhrase = comparedPhrase;
    this.matchValue = matchValue;
  }


  /**
   * Konstruiert ein Match mit Anzahl der richtig gematchten Wörter und dazugehörige Kategorie.
   * Redundante Funktion mit getauschten param-Werten
   * @param numberOfWords Anzahl der richtig gematchten Wörter
   * @param category dazugehörige Kategorie
   */
  public Match(String category, int numberOfWords, String comparedPhrase, float matchValue)
  {
    this(numberOfWords,category, comparedPhrase, matchValue);
  }


  /**
   * Gibt Anzahl der richtig gematchten Wörter zurück.
   * @return Anzahl der richtig gematchten Wörter
   */
  public int getNumberOfWords()
  {
    return numberOfWords;
  }


  /**
   * Gibt den gematchten Eintrag zurück.
   * @return gematchter Eintrag
     */
  public String getComparedPhrase() { return comparedPhrase;}


  /**
   * Gibt Kategorie des gematchten Eintrags zurück.
   * @return Kategorie des gematchten Eintrags
   */
  public String getCategory()
  {
    return category;
  }


  /**
   * Gibt den Ähnlichkeitsgrad zurück.
   * @return Ähnlichkeitsgrad
   */
  public float getMatchValue()
  {
    return matchValue;
  }


  /**
   * Gibt String-Repräsentation des Objekts zurück.
   * Aufbau: Kateorie gefolgt von der Anzahl der richtig gematchten Wörter (mit "()" umschlossen)
   * @return String-Repräsentation des Objekts
   */
  @Override public String toString()
  {
    return String.format("Match mit '%s', Kategorie:%s (Länge: %d, Match-Value: %f)",
            comparedPhrase, category, numberOfWords, matchValue);
  }
}
