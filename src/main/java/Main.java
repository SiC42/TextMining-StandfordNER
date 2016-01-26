
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import generatetrainingformat.GenerateTrainingFormat;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
    private static final String DEFAULTPATHWIKIDUMP = "Ressourcen/wikiDump.xml.bz2";
    /**
     * Default Dateipfad des Klartextes
     */
    private static final String DEFAULTPATHPLAINTEXT = "Ergebnisse/AA/wiki_00";
    /**
     * Dafault Dateipfad der Datei "titleNorm.txt"
     */
    private static final String DEFAULTPATHTITLENORM = "Ressourcen/titleNorm.txt";
    /**
     * Default Dateipfad der Property-file
     */
    private static final String DEFAULTPATHPORPERTY = "Ressourcen/default.prop";

    /**
     * Zeigt das Menü in der Komandozeile an und liest Auswahl ein
     * @return choice gibt Buchstaben des ausgwählten Untermenüs zurück
     */
    public static String showMenu() {
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
     * Hauptprogramm zur Steuerung der Unterprogramme
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ClassCastException, ClassNotFoundException, Exception {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String s = showMenu();
            if (s.equals("a")) {
                try {
                    System.out.println("Bitte den Ort des Wikipediadumps angeben, 'd' für default");
                    String source_wikipediadump = scanner.next();
                    if (source_wikipediadump.equals("d")) {
                        source_wikipediadump = DEFAULTPATHWIKIDUMP;
                    }
                    System.out.println("Anzahl der Personen-Artikel ?");
                    int person_articles = Integer.parseInt(scanner.next());
                    System.out.println("Anzahl der Organisationen-Artikel ?");
                    int organisation_articles = Integer.parseInt(scanner.next());
                    System.out.println("Anzahl der Orts-Artikel ?");
                    int places_articles = Integer.parseInt(scanner.next());

                    //System.out.println("Ihre Angaben: \n Speicherort: " + source_wikipediadump + "\n Personen-Artikel: " + person_articles + "\n Organisationen-Artikel: " + organisation_articles + "\n Orts-Artikel: " + places_articles);
                    GetDataFromWikiDump data = new GetDataFromWikiDump(source_wikipediadump, places_articles, person_articles, organisation_articles);
                    data.getData();

                    System.out.println("Starte XML bereinigung");
                    //Ausführen des Python-Scripts WikiExtractor.py wie auf Kommandozeile
                    Runtime.getRuntime().exec("python WikiExtractor.py -b 1G -o Ergebnisse Ergebnisse/ExtractedArticles.xml");
                    System.out.println("XML bereinigung beendet \n");

                } catch (CompressorException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            
            if (s.equals("b")) {
                System.out.println("Bitte den Speicherort des Klartextes angeben, 'd' für default");
                String source_PlainText = scanner.next();

                if (source_PlainText.equals("d")) {
                    source_PlainText = DEFAULTPATHPLAINTEXT;
                }

                Mapping.startMapping(source_PlainText);

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
            
            if(s.equals("c")){
                 System.out.println("Bitte Speicherort der property-file angeben oder 'd' drücken um default Datei zu nutzen, 'q' drücken um progrmm abzubrechen");
                String prop = scanner.next();
                if (prop.equals("d")) {
                    prop = DEFAULTPATHPORPERTY;
                }
                if (prop.equals("q")) {
                    scanner.close();
                    System.exit(0);
                }
                String[] arg = {"-prop", prop};
                CRFClassifier.main(arg);
            }
            
            if (s.equals("d")) {
                System.out.println("Bitte Speicherort \"parseTitleNorm.txt\" angeben, 'd' für default");
                String[] path_TitleNorm = new String[1];
                path_TitleNorm[0] = scanner.next();
                if (path_TitleNorm[0].equals("d")) {
                    path_TitleNorm[0] = DEFAULTPATHTITLENORM;
                }
                //Main Funktion von ParseTitleNorm ausführen
                ParseTitleNorm.main(path_TitleNorm);
            }

            if (s.equals("q")) {
                scanner.close();
                System.exit(0);
            }

        }
    }
}
