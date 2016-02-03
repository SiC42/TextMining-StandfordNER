package mapping;

import java.io.*;
import java.util.*;

public class Mapping {

    /**
     * Standard-Pfad für die Ressourcen, nötig für die Blacklist
     */
    private static final String DIR_RESSOURCE = "Ressourcen";

    /**
     *  Dateiname der Blacklist-Datei
     */
    private static final String FILE_BLACKLIST = "blacklist.txt";

    /**
     * Standard-Ordner, in dem die Vergleichsdaten (CSV-Dateien) liegen
     */

    private static final String DIR_RESSOURCE_CSV = "Vergleichsdaten";

    /**
     * Dateiname der CSV-Dateien mit den Personen-Daten
     */
    private static final String FILE_PERSON_CSV = "Person.csv";

    /**
     * Dateiname der CSV-Dateien mit den Organisationen-Daten
     */
    private static final String FILE_ORGA_CSV = "Organisation.csv";

    /**
     * Dateiname der CSV-Dateien mit den Ortschaft-Daten
     */
    private static final String FILE_LOC_CSV = "Ort.csv";

    private static final float STEP_SIZE_FEEDBACK = 0.1f;

    /**
     * Wörterbuch, mit dem getaggt wird
     */
    static Dictionary dict;

    /**
     * Liest aus der angegebenen Blacklist-Datei die Wörter aus und speichert sie.
     *
     * @param path Pfad zu der Blacklist-Datei
     * @return sortierte Menge der Wörter
     */
    public static TreeMap<String, String> listToBlacklist(String path) {
        TreeMap<String,String> blacklist = new TreeMap<>(new WordComparator());
        try {
            String line;
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(path), "UTF8"));
            while ((line = br.readLine()) != null) {
                blacklist.put(line, "");
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return blacklist;
    }

    /**
     * Liest aus der angegebenen CSV-Datei die Worttupel (Eintrag;Kategorie) aus und speichert sie im Wörterbuch.
     *
     * @param path Pfad zur auszulesenden CSV-Datei
     */
    public static void listToDict(String path) {
        try {
            String line;
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(path), "UTF8"));
            while ((line = br.readLine()) != null) {
                if (line.indexOf(";") > 1) {
                    dict.add(line.split(";"));
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }


    /**
     * Eigentlicher Mapping-Prozess. erstellt aus übergebenen Text und dem Wörterbuch einen Text, in dem die
     * Wörter für die relevanten Kategorien getaggt werden. Diese werden in den übergebenen Datei-Stream geschrieben.
     * @param dict Wörterbuch, mit dem der Text markiert werden soll
     * @param text Eingelesener Text, der markiert werden soll
     * @param file Datei-Stream, in dem die Ausgangsdaten geschrieben werden sollen
     * @throws IOException
     */
    public static void mapping(Dictionary dict, LinkedList<String> text, BufferedWriter file) throws IOException {
        int maxEntrySize = dict.getMaxEntrySize();
        float progress = STEP_SIZE_FEEDBACK;
        int initialSize = text.size();
        String word;
        while( (word= text.poll()) != null)
        {
            if(dict.contains(word))
            {
                LinkedList<String> textToTest = new LinkedList<>();
                textToTest.add(word);
                for(int i = 0; i< maxEntrySize && (word= text.poll()) != null;i++) {
                    textToTest.add(word);
                }
                Match match = dict.findEntryMatch(textToTest);
                if(match != null)
                {
                    for(int i=textToTest.size()-1; i>=match.getNumberOfWords(); i--)
                    {
                        text.addFirst(textToTest.get(i));
                    }
                    for(int i=textToTest.size()-1; i>=match.getNumberOfWords(); i--)
                    {
                        textToTest.remove(i);
                    }
                    String sonderzeichenAnfang = "";
                    String sonderzeichenEnde = "";
                    if(textToTest.peekFirst().matches("^" + Dictionary.REPLACE_CHARS + ".*"))
                    {
                        sonderzeichenAnfang = textToTest.peekFirst();
                        textToTest.addFirst(sonderzeichenAnfang.substring(1));
                        sonderzeichenAnfang = sonderzeichenAnfang.substring(0,1);
                    }
                    if(textToTest.peekLast().matches(".*"+Dictionary.REPLACE_CHARS))
                    {
                        sonderzeichenEnde = textToTest.pollLast();
                        textToTest.addLast(sonderzeichenEnde.substring(0,sonderzeichenEnde.length()-2));
                        sonderzeichenEnde = sonderzeichenEnde.substring(sonderzeichenEnde.length()-1);
                    }
                    textToTest.addFirst( sonderzeichenAnfang + "<" + match.getCategory() + ":" + match.getComparedPhrase() + ">");
                    textToTest.addLast("</" + match.getCategory() + ">" + sonderzeichenEnde);
                    for(String foundWord : textToTest)
                    {
                        file.append(foundWord + " ");
                    }
                } else {
                    file.append(textToTest.poll() + " ");
                    for(int i=textToTest.size()-1; i>=0; i--)
                    {
                        text.addFirst(textToTest.get(i));
                    }
                }
            } else{
                file.append(word + " ");
            }
            if(progress < (float)(initialSize - text.size())/initialSize)
            {
                System.out.println(Math.round(progress*100) + "%");
                progress += STEP_SIZE_FEEDBACK;
            }
        }

    }

    /**
     * Vergleicht Wörte aus Text mit Wörtern aus kategorisierten listen und makiert Wörter mit tags
     *
     * @param path Pfad zum zu untersuchendem Dokument
     */
    public static void startMapping(String path, boolean personEntry) {
        System.out.println("Fülle Blacklist");
        TreeMap<String,String> blacklist = listToBlacklist(DIR_RESSOURCE + "/" + FILE_BLACKLIST);
        System.out.println("Blacklist gefüllt. \n" +
                "Anzahl der Einträge: " + blacklist.size());
        dict = new Dictionary(blacklist, personEntry);
        try {
            System.out.println("Fülle Wörterbuch mit Daten");
            listToDict(DIR_RESSOURCE_CSV + "/" + FILE_ORGA_CSV);
            listToDict(DIR_RESSOURCE_CSV + "/" + FILE_LOC_CSV);
            listToDict(DIR_RESSOURCE_CSV + "/" + FILE_PERSON_CSV);
            System.out.println("Wörterbuch gefüllt. \n" +
                    "Anzahl der Einträge im Schlüssel: " + dict.size() +
                    "\n Längster Eintrag: " + dict.getMaxEntrySize() +
                    "\n Kürzester Eintrag: " + dict.getMinEntrySize());

            String line = "";
            LinkedList<String> text = new LinkedList<>();


            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(path), "UTF8"));
            while ((line = br.readLine()) != null) {
                if (!(line.startsWith("<doc ") ||
                        (line.startsWith("</doc>")))) {
                    String[] lineArray = line.split(" ");
                    for (String word : lineArray) {
                        text.add(word);
                    }
                }
            }
            br.close();

            System.out.println("Text einlesen fertig...");
            System.out.println("Starte Mappen und highlighten...");
            long startTime = System.currentTimeMillis();
            BufferedWriter file = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Ergebnisse/Mapped.out"), "UTF8"));
            int textSize = text.size();
            mapping(dict, text, file);
            file.flush();
            file.close();
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            System.out.println("Mappen und schreiben beendet (" + elapsedTime / 1000f + " sec).");
            System.out.println("Geschwindigkeit: " + textSize / (elapsedTime / 1000f) + " Words/sec");
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }

}
