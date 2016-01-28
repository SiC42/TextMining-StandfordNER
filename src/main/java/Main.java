
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import generatetrainingformat.GenerateTrainingFormat;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import mapping.Mapping;
import org.apache.commons.compress.compressors.CompressorException;
import parsetitlenorm.ParseTitleNorm;
import parsing.GetDataFromWikiDump;

/**
 * Hauptprogramm
 *
 * @author sebastian
 */
public class Main {

    /**
     * Default Dateipfad des WikipediaDumps
     */
    private static final String DEFAULT_PATH_WIKIDUMP = "Ressourcen/wikiDump.xml.bz2";
    /**
     * Default Dateipfad des Klartextes
     */
    private static final String DEFAULT_PATH_PLAINTEXT = "Ergebnisse/AA/wiki_00";
    /**
     * Dafault Dateipfad der Datei "titleNorm.txt"
     */
    private static final String DEFAULT_PATH_TITLENORM = "Ressourcen/titleNorm.txt";
    /**
     * Default Dateipfad der Property-file
     */
    private static final String DEFAULT_PATH_PROPERTY = "Ressourcen/default.prop";

    /**
     * Zeigt das Menü in der Komandozeile an und liest Auswahl ein
     *
     * @return choice gibt Buchstaben des ausgwählten Untermenüs zurück
     */
    private static String showMenu() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n Was möchten Sie tun? \n "
                + "a) Klartext aus WikipediaDump extrahieren?\n "
                + "b) Trainingsdaten aus Klartext erstellen?\n "
                + "c) Stanford-NER Klassifikator erstellen?\n "
                + "d) Vergleichsdaten erstellen?\n "
                + "q) Beenden");
        String choice = scanner.next();
        return choice;
    }

    /**
     * verarbeitet Option 'a'
     */
    private static void optionWikiExtraction() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Bitte den Ort des Wikipediadumps angeben, 'd' für default");
            String source_wikipediaDump = scanner.next();
            if (source_wikipediaDump.equals("d")) {
                source_wikipediaDump = DEFAULT_PATH_WIKIDUMP;
            }
            System.out.println("Anzahl der Personen-Artikel ?");
            int person_articles = Integer.parseInt(scanner.next());
            System.out.println("Anzahl der Organisationen-Artikel ?");
            int organisation_articles = Integer.parseInt(scanner.next());
            System.out.println("Anzahl der Orts-Artikel ?");
            int places_articles = Integer.parseInt(scanner.next());

            //System.out.println("Ihre Angaben: \n Speicherort: " + source_wikipediaDump + "\n Personen-Artikel: " + person_articles + "\n Organisationen-Artikel: " + organisation_articles + "\n Orts-Artikel: " + places_articles);
            GetDataFromWikiDump data = new GetDataFromWikiDump(source_wikipediaDump, places_articles, person_articles, organisation_articles);
            data.getData();

            System.out.println("Starte XML bereinigung");
            //Ausführen des Python-Scripts WikiExtractor.py wie auf Kommandozeile
            long startTime = System.currentTimeMillis();
            Runtime.getRuntime().exec("python2 WikiExtractor.py -b 1G -o Ergebnisse Ergebnisse/ExtractedArticles.xml");
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            System.out.println("XML-Bereinigung beendet. Dauer:  " + elapsedTime / 1000f + " sec\n");

        } catch (CompressorException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Verarbeitet Option 'b'
     * @throws UnsupportedEncodingException
     * @throws FileNotFoundException
     * @throws IOException 
     */
    private static void optionGenerateTrainingsformat() throws UnsupportedEncodingException, FileNotFoundException, IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bitte den Speicherort des Klartextes angeben, 'd' für default");
        String source_PlainText = scanner.next();

        if (source_PlainText.equals("d")) {
            source_PlainText = DEFAULT_PATH_PLAINTEXT;
        }
        String personEntryScan;
        do {
            System.out.println("Sollen Personen-Einträge..."
                    + "a) ... als solche betrachtet werden (es wird nach Vor- und Nachnamen unterschieden)?"
                    + "b) ... generalisiert werden (Personen werden nur als ganze Namen betrachtet)?");
            personEntryScan = scanner.next();
        } while(!personEntryScan.equals("a") || !personEntryScan.equals("b"));
        switch(personEntryScan)
        {
            case "a":
                Mapping.startMapping(source_PlainText, true);
                break;
            case "b":
                Mapping.startMapping(source_PlainText, false);
                break;
            default:
        }

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Ergebnisse/Mapped.tok"), "UTF8"));

        //text mit durch stanford-ner mitgelieferten Tokenizer tokenisiert
        PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(new FileReader("Ergebnisse/Mapped.out"),
                new CoreLabelTokenFactory(), "");
        while (ptbt.hasNext()) {
            CoreLabel label = ptbt.next();
            //füge an jede Zeile einen Tab und 'O' an
            bw.append(label.toString() + "\tO");
            bw.newLine();
        }
        bw.close();
        //Bringe tokenisierten Text in richtige Form
        GenerateTrainingFormat.generateTraining("Ergebnisse/Mapped.tok");

    }

    /**
     * Verarbeitet Option 'c'
     * @throws Exception 
     */
    private static void optionCreateClassifier() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bitte Speicherort der property-file angeben oder 'd' drücken um default Datei zu nutzen, 'q' drücken um progrmm abzubrechen");
        String prop = scanner.next();
        if (prop.equals("d")) {
            prop = DEFAULT_PATH_PROPERTY;
        }
        if (prop.equals("q")) {
            scanner.close();
            System.exit(0);
        }
        String[] arg = {"-prop", prop};
        CRFClassifier.main(arg);
    }

    /**
     * verarbeitet Option 'd'
     */
    private static void optionCreateComparisonFiles() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bitte Speicherort \"parseTitleNorm.txt\" angeben, 'd' für default");
        String[] path_TitleNorm = new String[1];
        path_TitleNorm[0] = scanner.next();
        if (path_TitleNorm[0].equals("d")) {
            path_TitleNorm[0] = DEFAULT_PATH_TITLENORM;
        }
        //Main Funktion von ParseTitleNorm ausführen
        ParseTitleNorm.main(path_TitleNorm);
    }

    /**
     * Hauptprogramm zur Steuerung der Unterprogramme
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ClassCastException, ClassNotFoundException, Exception {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String s = showMenu();
            if (s.equals("a")) {
                optionWikiExtraction();
            }
            if (s.equals("b")) {
                optionGenerateTrainingsformat();
            }
            if (s.equals("c")) {
                optionCreateClassifier();
            }
            if (s.equals("d")) {
                optionCreateComparisonFiles();
            }
            if (s.equals("q")) {
                scanner.close();
                System.exit(0);
            }

        }
    }
}
