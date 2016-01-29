package parsing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.compress.compressors.CompressorException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Erzeugt Ausschnitt aus WikipediaDump welcher mit WikiExtractor.py
 * weiterverarbeitet werden kann
 * Ruft WikiContentHandler auf
 *
 * @author Sebastian Gottwald
 */
public class GetDataFromWikiDump {
    
    /**
     * Dateipfad der zu bearbeitenden Datei
     */
    private final String source;
    /**
     * Anzahl der OrtsArtikel
     */
    private final int place_articles;
    /**
     * Anzahl der Personen Artikel
     */
    private final int person_articles;
    /**
     * Anzahl der Organisationen Artikel
     */
    private final int organisation_articles;
    
    /**
     * Konstruktor
     * @param source Quelle der zu verarbeitenden XML-datei
     * @param place_articles Anzahl der Orts-Artikel
     * @param person_articles Anzahl der Personen Artikel
     * @param organisation_articles Anzahl der Organisationen Artikel
     */
    public GetDataFromWikiDump(String source, int place_articles, int person_articles , int organisation_articles){
        this.source = source;
        this.person_articles = person_articles;
        this.place_articles =  place_articles;
        this.organisation_articles =  organisation_articles ;        
    }

    /**
     * lädt Wikipedia-Damp und ruft WikiContentHandler auf um XML-Datei zu verarbeiten
     * @throws CompressorException 
     */
    public void getData () throws CompressorException{
        try {
            // XMLReader erzeugen
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            
            System.out.println("Öffne Wikipedia-Dump");
            //BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/sebastian/Dokumente/Text-Mining/Praktikum/wiki_100mb.xml"), "UTF-8"));
            //BufferedReader reader = new BufferedReader(UtilFunction.getBufferedReaderForCompressedFile("/home/sebastian/Dokumente/Text-Mining/Praktikum/dewiki-latest-pages-articles.xml.bz2"));
            BufferedReader reader = new BufferedReader(UtilFunction.getBufferedReaderForCompressedFile(source));

            //FileReader reader = new FileReader("/home/sebastian/Dokumente/Text-Mining/Praktikum/wiki_100mb.xml");
            InputSource inputSource = new InputSource(reader);

            System.out.println("Beginne Extraktion... ");
            // PersonenContentHandler wird übergeben
            xmlReader.setContentHandler(new WikiContentHandler(place_articles, person_articles, organisation_articles));

            // Parsen wird gestartet
            xmlReader.parse(inputSource);
            
        } catch (FileNotFoundException e) {
            System.err.println(" -------------------------------------------------------------------------------");
            System.err.println("DATEI NICHT GEFUNDEN!!!"    );
            System.err.println("-----------------------------------------------------------------------------\n");
        } catch (IOException | SAXException e) {
            //System.err.println(e);
        } 
        
    }

   
}
    
   