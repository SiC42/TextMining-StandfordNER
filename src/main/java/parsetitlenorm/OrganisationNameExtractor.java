package parsetitlenorm;
import java.util.LinkedList;
import java.util.HashMap;

public class OrganisationNameExtractor {


    private static final String DEFAULT_PATH
        = "Organisation.csv";

    public static String[] extract(String name)
    {
      if(name.matches(".*\\([A-Z]*\\)"))
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

    public static String printArray(String[] nameArray)
    {
      String output = "{";
      for(String name : nameArray)
      {
        output+= " " + name + ",";
      }
      return output.substring(0, output.length()-1) + "}";

    }


    public static void main(String[] args) {
      System.out.println(printArray(extract("Sozialdemokratische Partei Deutschlands (SPD)")));
      System.out.println(printArray(extract("Christlich Demokratische Union (CDU)")));
      System.out.println(printArray(extract("Testfalsch (ÖST)")));
      System.out.println(printArray(extract("Testfalsch2 (te-ta)")));
      String[] testString = {"Angelina Jolie",
                         "Archimedes",
                         "Al-Biruni"};
      System.out.println(printArray(testString));
    }
}
