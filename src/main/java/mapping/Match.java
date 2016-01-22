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

  String comparedPhrase;

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

  public String getComparedPhrase() { return comparedPhrase;}

  /**
   * Gibt Kategorie des gematchten Eintrags zurück.
   * @return Kategorie des gematchten Eintrags
   */
  public String getCategory()
  {
    return category;
  }


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


  /**
   * Test-Methode zum Testen der String-Repräsentation.
   * @param args obsolete
   */
  public static void main(String[] args)
  {
    /*Match match = new Match(5,"test", "comp");
    System.out.println(match);*/
  }
}
