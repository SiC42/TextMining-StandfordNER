package mapping;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import java.util.TreeSet;

public class Mapping {

    private static final String DIR_RESSOURCE = "Ressourcen";
    private static final String FILE_PERSON_CSV = "Person.csv";
    private static final String FILE_ORGA_CSV = "Organisation.csv";
    private static final String FILE_LOC_CSV = "Ort.csv";
    private static final String FILE_BLACKLIST = "blacklist.txt";

    /**
     * Regulärer Ausdruck für alle Zeichen, die nicht Buchstaben oder Leerzeichen sind.
     * I.e. alle Satzzeichen außer Leerzeichen.
     */
    private static final String REPLACE_CHARS = "[.,;'\"()]";
    static Dictionary dict;

    public static TreeSet<String> listToBlacklist(String path) {
        TreeSet<String> blacklist = new TreeSet<>();
        try {
            String line;
            BufferedReader br = new BufferedReader(new FileReader(path));
            while ((line = br.readLine()) != null) {
                    blacklist.add(line);
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return blacklist;
    }

    public static void listToDict(String path) {
        try {
            String line;
            BufferedReader br = new BufferedReader(new FileReader(path));
            while ((line = br.readLine()) != null) {
                if (line.indexOf(";") > 1) {
                    dict.add(line.split(";"));
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }


    public static LinkedList<String> mapping(Dictionary dict, LinkedList<String> text) {
        int maxEntrySize = dict.getMaxEntrySize();
        int stelle = 0;
        while (stelle < text.size()) {
            if (dict.contains(text.get(stelle).replaceAll(REPLACE_CHARS,""))) {
                String textToTest = "";
                for (int i = 0; i < maxEntrySize && stelle + i < text.size(); i++) {
                    textToTest += text.get(i + stelle) + " ";
                }
                System.out.println(textToTest.replaceAll(REPLACE_CHARS,""));
                Match match = dict.findEntryMatch(textToTest.replaceAll(REPLACE_CHARS,""));
                if (match != null) {
                    text.add(stelle, "<" + match.getCategory()  + ":" + match.getComparedPhrase() + ">");
                    stelle += match.getNumberOfWords() + 1;
                    text.add(stelle, "</" + match.getCategory() + ">");
                }
            }
            stelle++;
        }
        return text;
    }

    /**
     * Vergleicht Wörte aus Text mit Wörtern aus kategorisierten listen und makiert Wörter mit tags
     *
     * @param path Pfad zum zu untersuchendem Dokument
     */
    public static void startMapping(String path) {
        System.out.println("Fülle Blacklist");
        TreeSet<String> blacklist = listToBlacklist(DIR_RESSOURCE + "/" + FILE_BLACKLIST);
        dict = new Dictionary(blacklist);
        try {
            System.out.println("Fülle Wörterbuch mit Daten");
            listToDict(DIR_RESSOURCE + "/" + FILE_ORGA_CSV);
            listToDict(DIR_RESSOURCE + "/" + FILE_LOC_CSV);
            listToDict(DIR_RESSOURCE + "/" + FILE_PERSON_CSV);
            System.out.println("Wörterbuch gefüllt." +
                    "Anzahl der Einträge im Schlüssel: " + dict.size());


            String line = "";
            LinkedList<String> text = new LinkedList<String>();

            BufferedReader br = new BufferedReader(new FileReader(path));
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
            mapping(dict, text);
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            System.out.println("Mappen beendet Schreibe in Datei (" + elapsedTime/1000f + " sec).");
            BufferedWriter file = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Ergebnisse/Mapped.out"), "UTF8"));
            for (String word : text) {
                file.append(word + " ");
            }
            file.flush();
            file.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }


    public static void main(String[] args) {
        startMapping("test.txt");
    }
}
