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
import java.util.Set;
import java.util.Iterator;


/**
 * Klasse zum Parsen der titleNorm-Datei, die das Ergebnis der Title-Gruppe des Textmining-Praktikums war
 * Ziel ist es, mehrere (im Moment drei) Dateien im CSV-Format zu erstellen, die möglichst konsistent sind.
 * Zudem wird eine Blacklist hinzugenommen, um erste unerwünschte Namen zu filtern.
 * @author Simon Bordewisch
 */
public class ParseTitleNorm {
    /**
     * Gibt an, mit welchem Tag die Personen getaggt werden sollen.
     */
    public static final String PERSON_LABEL = "Person";


    /**
     * Gibt an, mit welchem Tag die Organisationen getaggt werden sollen.
     */
    public static final String ORGANISATION_LABEL = "Organisation";


    /**
     * Gibt an, mit welchem Tag die Orte getaggt werden sollen.
     */
    public static final String LOCATION_LABEL = "Ort";


    /**
     * Menge, in die die Personen zwischengespeichert werden sollen.
     */
    static Set<String> person = new HashSet<>();

    /**
     * Menge, in die die Organisationen zwischengespeichert werden sollen.
     */
    static Set<String> organisation = new HashSet<>();


    /**
     * Menge, in die die Orte zwischengespeichert werden sollen.
     */
    static Set<String> location = new HashSet<>();

    /**
     * Menge mit allen Wörtern, die nicht in die Vergleichsdaten gespeichert werden sollen
     */
    static Set<String> blacklist = new HashSet<>();


    /**
     * Pfad zu den Ressourcen-Dateien.
     */
    private static final String PATH_DIR_SRC = "Ressourcen";


    /**
     * Pfad zu den Ergebnis-Dateien.
     */
    private static final String PATH_DIR_DEST = "Vergleichsdaten";


    /**
     * Name der Eingabedatei (titleNorm)
     */
    private static final String PATH_INPUT = "titleNorm.txt";


    /**
     * Name der Blacklist-Datei
     */
    private static final String PATH_BLACKLIST = "blacklist.txt";


    /**
     * Liest die Eingabedatei und Blacklistdatei ein und speichert sie in den Mengen.
     *
     * @param pathInput Pfad zur Eingabedatei (titleNorm).
     * @param pathBlacklist Pfad zur Blacklist-Datei
     */
    public static void fileToHashSets(String pathInput, String pathBlacklist)
    {
        // zunächst sicherstellen, dass Blacklist gefüllt ist
        fillBlacklist(pathBlacklist);
        fileToHashSets(pathInput);
    }


    /**
     * Liest die Eingabedatei und Blacklistdatei ein und speichert sie in den Mengen.
     *
     * @param pathInput Pfad zur Eingabedatei (titleNorm)
     */
    public static void fileToHashSets(String pathInput) {

        System.out.println("Einlesen der CSV-Ressourcen...");
        try {
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(pathInput), "UTF8"));
            int notParsed = 0;
            int parsed = 0;
            while ((line = br.readLine()) != null) {
                if (line.indexOf(" --> ") > 1) {
                    String[] entry = line.split(" --> ");
                    if (entry.length > 1) //sonst corrupted, da kein "Tag" eingetragen
                    {
                        switch (entry[1]) {
                            case "Person":
                                if (!blacklist.contains(entry[0])) {
                                    person.add(entry[0]);
                                    parsed++;
                                }
                                break;
                            case "K" + "\u00f6" + "rperschaft": //Unicode entspricht ö
                                String[] splitOrga = OrganisationNameExtractor.extract(entry[0]);
                                for (String orga : splitOrga) {
                                    if (!blacklist.contains(orga)) {
                                        organisation.add(orga);
                                        parsed++;
                                    }
                                }
                                break;
                            case "Geografikum":
                                if (!blacklist.contains(entry[0])) {
                                    location.add(entry[0]);
                                    parsed++;
                                }
                                break;
                            default:
                                System.out.println("Eintrag für " + entry[0] + ":'" + entry[1] + "' nicht gefunden.");
                        }
                    } else {
                        notParsed++;
                    }
                }
            }
            System.out.println(parsed + " Einträge wurden geparst.");
            System.out.println(notParsed + " Einträge konnten nicht geparst werden (korrupt).");

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }

    /**
     * Erstellt aus übergebenen Mengen CSV-Dateien mit den Namen und der dazugehörigen Kategorie als Inhalt.
     * Datei trägt Name der übergebenen Kategorie
     * Beispiel: "Angela Merkel;Person"
     * @param set Menge mit Namen, die in Datei geschrieben soll
     * @param category Kategorie, mit der die Namen "getaggt" werden sollen
     */
    public static void createCategoryCSV(Set<String> set, String category) {
        Iterator<String> it = set.iterator();
        try {
            BufferedWriter file = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(PATH_DIR_DEST + "/" + category + ".csv"), "UTF8"));
            while (it.hasNext()) {
                String entry = it.next();
                entry = entry.replaceAll("\\(.*\\)", ""); // Klammern entfernen
                entry.trim();
                file.append(entry + ";" + category);
                file.append(System.getProperty("line.separator"));
            }
            file.flush();
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Liest die übergebene Datei aus und speichert sie in der blacklist-Menge.
     * @param path Pfad zur auszulesenden Datei
     */
    public static void fillBlacklist(String path) {
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


    /**
     * Liest Input-Dateien und erstellt Output-Dateien automatisch.
     * @param args ein Argument, Pfad zur titleNorm-Datei
     */
    public static void main(String[] args) {
        String pathInput;
        if (args.length != 0) {
            pathInput = args[0];
        } else {
            pathInput = PATH_DIR_SRC + "/" + PATH_INPUT;
        }
        String pathBlacklist = PATH_DIR_SRC + "/" + PATH_BLACKLIST;


        fillBlacklist(pathBlacklist);
        fileToHashSets(pathInput);

        createCategoryCSV(person, PERSON_LABEL);
        createCategoryCSV(organisation, ORGANISATION_LABEL);
        createCategoryCSV(location, LOCATION_LABEL);
    }
}
