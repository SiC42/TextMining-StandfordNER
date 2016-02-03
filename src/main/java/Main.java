
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
import org.apache.commons.cli.*;
import org.apache.commons.compress.compressors.CompressorException;
import parsetitlenorm.ParseTitleNorm;
import parsing.GetDataFromWikiDump;


/**
 * Hauptprogramm
 *
 * @author Sebastian Gottwald & Simon Bordewisch
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
                + "a) Kategoriespeziefische Artikel aus dem WikipediaDump extrahieren?\n "
                + "b) XML Datei bereinigen? \n "
                + "c) Trainingsdaten aus Klartext erstellen?\n "
                + "d) Stanford-NER Klassifikator erstellen?\n "
                + "b) Vergleichsdaten erstellen?\n "
                + "q) Beenden");
        String choice = scanner.next();
        return choice;
    }

    /**
     * Öffnet Wikipedia Dump (muss im .bz2 Format vorliegen)
     */
    private static void optionWikiExtraction() {
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
        optionWikiExtraction(source_wikipediaDump, person_articles, organisation_articles, places_articles);
    }

    /**
     * Öffnet Wikipedia Dump (muss im .bz2 Format vorliegen) führt dann Python
     */
    private static void optionWikiExtraction(
            String source_wikipediaDump,
            int person_articles,
            int organisation_articles,
            int places_articles) {
        try {
            long startTime = System.currentTimeMillis();
            GetDataFromWikiDump data = new GetDataFromWikiDump(source_wikipediaDump, places_articles, person_articles, organisation_articles);
            data.getData();
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            System.out.println("Dauer der Extraktion:  " + elapsedTime / 1000f + " sec\n");
        } catch (CompressorException ex) { //Datei muss im .bz2 Format vorliegen
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Extrahiert Klartext aus XML (Ergebnisse/ExtractedArticles.xml)
     * !!! Python 2 wird benötigt !!!
     */
    private static void optionCleanXML() {
        try{
        long startTime = System.currentTimeMillis();
        System.out.println("Starte XML bereinigung");
        //Ausführen des Python-Scripts WikiExtractor.py wie auf Kommandozeile
        Runtime.getRuntime().exec("python2 WikiExtractor.py -b 1G -o Ergebnisse Ergebnisse/ExtractedArticles.xml");
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("XML-Bereinigung beendet. Dauer des Prozesses:  " + elapsedTime / 1000f + " sec\n");
    }catch(IOException e){
        System.err.println(e);
    }
        }

    /**
     * Erstellt TrainingsDaten aus einem Klartext, Ausgangsdaten werden mit
     * Vergleichsdaten gemappt
     *
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
            System.out.println("Sollen Personen-Einträge...\n"
                    + "a) ... als solche betrachtet werden (es wird nach Vor- und Nachnamen unterschieden)?\n"
                    + "b) ... generalisiert werden (Personen werden nur als ganze Namen betrachtet)?");
            personEntryScan = scanner.next();
        } while (!personEntryScan.equals("a") && !personEntryScan.equals("b"));
        switch (personEntryScan) {
            case "a":
                personEntryScan = "true";
                break;
            case "b":
                personEntryScan = "false";
                break;
            default:
        }
        optionGenerateTrainingsformat(source_PlainText, personEntryScan);
    }

    /**
     * Erstellt TrainingsDaten aus einem Klartext, Ausgangsdaten werden mit
     * Vergleichsdaten gemappt
     *
     * @throws UnsupportedEncodingException
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static void optionGenerateTrainingsformat(String source_PlainText, String personEntryScan) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        switch (personEntryScan) {
            case "true":
                Mapping.startMapping(source_PlainText, true);
                break;
            case "false":
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
     * Erstellt den Klassifikator für den Stanford NER
     *
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
     * Erstellt den Klassifikator für den Stanford NER
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
     */
    public static void contextMenu() throws Exception {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String s = showMenu();
            if (s.equals("a")) {
                optionWikiExtraction();
            }
            if(s.equals("b")){
                optionCleanXML();
            }
            if (s.equals("c")) {
                optionGenerateTrainingsformat();
            }
            if (s.equals("d")) {
                optionCreateClassifier();
            }
            if (s.equals("e")) {
                optionCreateComparisonFiles();
            }
            if (s.equals("q")) {
                scanner.close();
                System.exit(0);
            }

        }
    }

    /**
     * Main-Methode. Ruft entweder Menü auf oder arbeitet mit den übergebenen Parametern
     * @param args übergebene Optionen, vergleiche Ausgabe mit Parameter '-h'
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption("h", "help", false, "zeigt diese Hilfe an");
        options.addOption("s", "source", true, "legt Source-Dateien für jeweiligen Prozess fest");
        options.addOption("gp", "generalizePerson", false, "legt fest, ob Personen generalisiert werden");

        OptionGroup oGroup = new OptionGroup();
        oGroup.addOption(new Option(
                "et",
                "extractText",
                true, //Anz Artikel
                "extrahiert angegebene Anzahl an Artikeln aus Wikidump"));
        oGroup.addOption(new Option(
                "cxml",
                "cleanXML",
                false,
                "Erstellt Klartext aus XML-Datei"));
        oGroup.addOption(new Option(
                "ctd",
                "createTrainingData",
                false,
                "taggt Klartext und erstellt Trainingsdaten für den NER"));
        oGroup.addOption(new Option(
                "cc",
                "createClassifier",
                false,
                "erstellt aus Trainingsdaten einen Klartext"));
        oGroup.addOption(new Option(
                "cnd",
                "createNameData",
                false,
                "erstellt aus der parseTitleNorm.txt die CSV-Dateien"));
        oGroup.addOption(new Option(
                "cm",
                "contextMenu",
                false,
                "lässt diese Applikation über ein Kontext-Menü bedienen."));
        options.addOptionGroup(oGroup);

        CommandLineParser parser = new BasicParser();
        CommandLine cmd;
        HelpFormatter formatter = new HelpFormatter();
        try {
            String path;
            cmd = parser.parse(options, args);
            if (cmd.hasOption('h')) {
                formatter.printHelp("Main", options);
                return;
            }
            if (cmd.hasOption("et")) {
                if (cmd.hasOption("s")) {
                    path = cmd.getOptionValue("s");
                } else {
                    path = DEFAULT_PATH_WIKIDUMP;
                }
                System.out.println("|" + cmd.getOptionValue("et") + "|");
                int noArticles = Integer.parseInt(cmd.getOptionValue("et"));
                optionWikiExtraction(path, noArticles, noArticles, noArticles);
            } else if(cmd.hasOption("cxml")){
                optionCleanXML();            
            }else if (cmd.hasOption("ctd")) {
                if (cmd.hasOption("s")) {
                    path = cmd.getOptionValue("s");
                } else {
                    path = DEFAULT_PATH_PLAINTEXT;
                }
                String dontGeneralize;
                if (cmd.hasOption("gp")) {
                    dontGeneralize = "false";
                } else {
                    dontGeneralize = "true";
                }
                optionGenerateTrainingsformat(path, dontGeneralize);
            } else if (cmd.hasOption("cc")) {
                if (cmd.hasOption("s")) {
                    path = cmd.getOptionValue("s");
                } else {
                    path = DEFAULT_PATH_PROPERTY;
                }
                String[] arg = {"-prop", path};
                CRFClassifier.main(arg);
            } else if (cmd.hasOption("cnd")) //createNameData
            {
                if (cmd.hasOption("s")) {
                    path = cmd.getOptionValue("s");
                } else {
                    path = DEFAULT_PATH_TITLENORM;
                }
                String[] pathArray = {path};
                ParseTitleNorm.main(pathArray);

            } else if (cmd.hasOption("cm")) {
                contextMenu();
            } else {
                contextMenu();
            }

        } catch (ParseException pvException) {
            formatter.printHelp("Main", options);
            System.out.println("Parse Fehler:\n" + pvException.getMessage());
            return;
        }

    }
}
