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
 *
 * @author Sebastian Gottwald
 */
public class GetDataFromWikiDump {
    
    private final String source;
    private final int place_articles;
    private final int person_articles;
    private final int organisation_articles;
    
    /**
     * Konstruktor
     * @param source
     * @param place_articles
     * @param person_articles
     * @param organisation_articles 
     */
    public GetDataFromWikiDump(String source, int place_articles, int person_articles , int organisation_articles){
        this.source = source;
        this.person_articles = person_articles;
        this.place_articles =  place_articles;
        this.organisation_articles =  organisation_articles ;        
    }

    /**
     * lädt und verarbeitet Wikipedia-Damp und schreibt extrahierte Dateien in seperate Datei
     * @throws CompressorException 
     */
    public void getData () throws CompressorException{
        try {
            // XMLReader erzeugen
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            

            // Pfad zur XML Datei
            System.out.println("Öffne Wikipedia-Dump");
            //BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/sebastian/Dokumente/Text-Mining/Praktikum/wiki_100mb.xml"), "UTF-8"));
            //BufferedReader reader = new BufferedReader(UtilFunction.getBufferedReaderForCompressedFile("/home/sebastian/Dokumente/Text-Mining/Praktikum/dewiki-latest-pages-articles.xml.bz2"));
            BufferedReader reader = new BufferedReader(UtilFunction.getBufferedReaderForCompressedFile(source));

            //FileReader reader = new FileReader("/home/sebastian/Dokumente/Text-Mining/Praktikum/wiki_100mb.xml");
            InputSource inputSource = new InputSource(reader);

            System.out.println("Beginne Extraktion... ");
            // PersonenContentHandler wird übergeben
            xmlReader.setContentHandler(new PersonenContentHandler(place_articles, person_articles, organisation_articles));

            // Parsen wird gestartet
            xmlReader.parse(inputSource);
            
        } catch (FileNotFoundException e) {
            System.err.println(" -------------------------------------------------------------------------------");
            System.err.println("DATEI NICHT GEFUNDEN!!! \n"    );
            System.err.println("-----------------------------------------------------------------------------\n");
        } catch (IOException | SAXException e) {
            System.err.println(e);
        } 
        
    }

   
}
    
   