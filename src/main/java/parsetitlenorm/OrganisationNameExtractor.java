package parsetitlenorm;


/**
 * Ziel der Klasse ist es die Organisationen der titleNorm zu parsen, sodass nicht nur die vollen Namen aufgenommen
 * werden, sondern auch evtuelle Abkürzungen, die hinter der Organisation in Klammern stehen.
 * Dabei werden verschiedene Filter verwendet um sicher zu stellen, dass die in Klammern stehenden Begriffe auch
 * möglichst wirklich Abkürzungen sind.
 * @author Simon Bordewisch
 */
public class OrganisationNameExtractor {


    /**
     * Extrahiert die in Klammern stehenden Abkürzungen aus dem übergebenen String.
     * Falls keine Abkürzung in den Klammern erkannt wird, werden die Klammern samt Inhalt entfernt.
     * @param name String, der bearbeitet werden soll.
     * @return
     */
    public static String[] extract(String name)
    {
      if(name.matches(".*\\([A-Z]*\\)")) // Klammern mit Großbuchstaben
      {
        int openBracketIndex = name.indexOf("(");
        int closeBracketIndex = name.indexOf(")");
        String abbr = name.substring(openBracketIndex+1, closeBracketIndex);
        name = name.replaceAll("\\(.*\\)", "");
        String[] output ={name, abbr};
        return output;
      } else
      {
        String[] output = {name.replaceAll("\\(.*\\)", "")};
        return output;
      }
    }

}
