package parsetitlenorm;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

public class ParseTitleNorm
{
    static final String PERSON_LABEL = "Person";
    static final String ORGANISATION_LABEL = "Organisation";
    static final String LOCATION_LABEL = "Ort";
    static HashSet<String> person = new HashSet<String>();
    static HashSet<String> organisation = new HashSet<String>();
    static HashSet<String> location = new HashSet<String>();
    static HashSet<String> blacklist = new HashSet<String>();

    private static final String PATH_DIR_SRC = "Ressourcen";
    private static final String PATH_DIR_DEST = "Vergleichsdaten";
    private static final String PATH_INPUT = "titleNorm.txt";
    private static final String PATH_BLACKLIST = "blacklist.txt";


    public static void fileToHashSets(String path)
    {
        System.out.println("Einlesen der CSV-Ressourcen...");
        try {
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF8"));
            int notParsed=0;
			int parsed=0;
            while ((line = br.readLine()) != null) {
                if (line.indexOf(" --> ") > 1) {
                    String[] entry = line.split(" --> ");
                    if(entry.length > 1) //sonst corrupted, da kein "Tag" eingetragen
                    {
                      switch(entry[1]) {
                      case "Person":
                            if(!blacklist.contains(entry[0])) {
                                person.add(entry[0]);
                            }
                          person.add(entry[0]);
						              parsed++;
                          break;
                      case "K" + "\u00f6" + "rperschaft": //Unicode entspricht ö
                          String[] splitOrga = OrganisationNameExtractor.extract(entry[0]);
                          for(String orga : splitOrga)
                          {
                            if(!blacklist.contains(orga))
  						              {
                              organisation.add(orga);
  							            parsed++;
                            }
                          }
                          break;
                      case "Geografikum":
                          if(!blacklist.contains(entry[0]))
						  {
                            location.add(entry[0]);
							parsed++;
						  }
                          break;
                      default:
                          System.out.println("Eintrag für " + entry[0]+ ":'" + entry[1] + "' nicht gefunden.");
                      }
                    } else{
                      notParsed++;
                    }
                  }
            }
			System.out.println(parsed + " Einträge wurden geparst.");
            System.out.println(notParsed + " Einträge konnten nicht geparst werden.");

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }


    public static void createCategoryCSV(Set<String> set, String category) {
      Iterator<String> it = set.iterator();
      try{
          BufferedWriter file = new BufferedWriter(
                             new OutputStreamWriter(
                             new FileOutputStream( PATH_DIR_DEST + "/" + category + ".csv" ), "UTF8" ) );
          while (it.hasNext()) {
              String entry = it.next();
              entry = entry.replaceAll("\\(.*\\)", ""); // Klammern entfernen
              entry.trim();
              file.append(entry + ";" + category);
              file.append(System.getProperty("line.separator"));
          }
          file.flush();
          file.close();
      } catch(Exception e)
          {
              e.printStackTrace();
          }
    }


    public static void fillBlacklist(String path)
    {
        System.out.println("Einlesen der Blacklist...");
        try {
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF8"));
             while ((line = br.readLine()) != null) {
                blacklist.add(line);
             }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        System.out.println(blacklist.size() + " Einträge wurden geparst.");
    }


    public static void createCategoryCSV(HashMap<String,String> map, String category) {
      try
      {
        BufferedWriter file = new BufferedWriter(
                             new OutputStreamWriter(
                             new FileOutputStream( category + ".csv" ), "UTF8" ) );
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
          Map.Entry pair = (Map.Entry)it.next();
          String entry = (String) pair.getKey();
          entry = entry.replaceAll("\\(.*\\)", "");
          file.append(entry + ";" + pair.getValue());
          file.append(System.getProperty("line.separator"));
          it.remove(); // avoids a ConcurrentModificationException
        }

        file.flush();
        file.close();
      } catch(Exception e)
          {
              e.printStackTrace();
          }
    }

    public static void main(String[] args)
    {
        String path;
        if(args.length != 0)
        {
            path = args[0];
        } else
        {
            path = PATH_DIR_SRC + "/" + PATH_INPUT;
        }
        fillBlacklist(PATH_DIR_SRC + "/" + PATH_BLACKLIST);
        fileToHashSets(path);

        createCategoryCSV(person, PERSON_LABEL);
        createCategoryCSV(organisation, ORGANISATION_LABEL);
        createCategoryCSV(location, LOCATION_LABEL);
    }
}
