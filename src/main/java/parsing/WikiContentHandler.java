package parsing;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Implementiere ContentHandler zur verarbetung des XML-Dumps
 *
 * @author Sebastian Gottwald
 */
public class WikiContentHandler implements ContentHandler {

    /**
     * ArrayList in der Personen Artikel gespeichert werden
     */
    private final ArrayList<Page> person_Pages = new ArrayList<>();
    /**
     * ArrayList in der Organisationen Artikel gespeichert werden
     */
    private final ArrayList<Page> organisation_Pages = new ArrayList<>();
    /**
     * ArrayList in der Orts Artikel gespeichert werden
     */
    private final ArrayList<Page> places_Pages = new ArrayList<>();
    /**
     * Text des zu behandelten Artikels
     */
    private String currentValue;
    /**
     * Jeder Artikel wird als Obbjekt abgespeichert
     */
    private Page page;
    /**
     * Buffer zum extrahieren eines Wikipediarartikels
     */
    private StringBuilder buffer;
    /**
     * Anzahl der zu extrahierenden Personen Artikel
     */
    private int number_person_articles;
    /**
     * Anzahl der zu extrahierenden Orts Artikel
     */
    private int number_places_articles;
    /**
     * Anzahl der zu extrahierenden Organisationen Artikel
     */
    private int number_organisation_articles;
    /**
     * Pfad zu AusgabeDatei
     */
    private static final String PATH_OUTPUT = "Ergebnisse/ExtractedArticles.xml";
    /**
     * Anfang der XML Datei, wird am Anfang der AusgabeDokuments eingefügt
     */
    private static final String documentheader = "<mediawiki xmlns=\"http://www.mediawiki.org/xml/export-0.10/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.mediawiki.org/xml/export-0.10/ http://www.mediawiki.org/xml/export-0.10.xsd\" version=\"0.10\" xml:lang=\"de\">"
            + "<siteinfo>"
            + "<sitename>Wikipedia</sitename>"
            + "<dbname>dewiki</dbname>"
            + "<base>https://de.wikipedia.org/wiki/Wikipedia:Hauptseite</base>"
            + "<generator>MediaWiki 1.27.0-wmf.7</generator>"
            + "<case>first-letter</case>"
            + "<namespaces>"
            + "<namespace key=\"-2\" case=\"first-letter\">Medium</namespace>"
            + "<namespace key=\"-1\" case=\"first-letter\">Spezial</namespace>"
            + "<namespace key=\"0\" case=\"first-letter\" />"
            + "<namespace key=\"1\" case=\"first-letter\">Diskussion</namespace>"
            + "<namespace key=\"2\" case=\"first-letter\">Benutzer</namespace>"
            + "<namespace key=\"3\" case=\"first-letter\">Benutzer Diskussion</namespace>"
            + "<namespace key=\"4\" case=\"first-letter\">Wikipedia</namespace>"
            + "<namespace key=\"5\" case=\"first-letter\">Wikipedia Diskussion</namespace>"
            + "<namespace key=\"6\" case=\"first-letter\">Datei</namespace>\n"
            + "      <namespace key=\"7\" case=\"first-letter\">Datei Diskussion</namespace>\n"
            + "      <namespace key=\"8\" case=\"first-letter\">MediaWiki</namespace>\n"
            + "      <namespace key=\"9\" case=\"first-letter\">MediaWiki Diskussion</namespace>\n"
            + "      <namespace key=\"10\" case=\"first-letter\">Vorlage</namespace>\n"
            + "      <namespace key=\"11\" case=\"first-letter\">Vorlage Diskussion</namespace>\n"
            + "      <namespace key=\"12\" case=\"first-letter\">Hilfe</namespace>\n"
            + "      <namespace key=\"13\" case=\"first-letter\">Hilfe Diskussion</namespace>\n"
            + "      <namespace key=\"14\" case=\"first-letter\">Kategorie</namespace>\n"
            + "      <namespace key=\"15\" case=\"first-letter\">Kategorie Diskussion</namespace>\n"
            + "      <namespace key=\"100\" case=\"first-letter\">Portal</namespace>\n"
            + "      <namespace key=\"101\" case=\"first-letter\">Portal Diskussion</namespace>\n"
            + "      <namespace key=\"828\" case=\"first-letter\">Modul</namespace>\n"
            + "      <namespace key=\"829\" case=\"first-letter\">Modul Diskussion</namespace>\n"
            + "      <namespace key=\"2300\" case=\"first-letter\">Gadget</namespace>\n"
            + "      <namespace key=\"2301\" case=\"first-letter\">Gadget Diskussion</namespace>\n"
            + "      <namespace key=\"2302\" case=\"case-sensitive\">Gadget-Definition</namespace>\n"
            + "      <namespace key=\"2303\" case=\"case-sensitive\">Gadget-Definition Diskussion</namespace>\n"
            + "      <namespace key=\"2600\" case=\"first-letter\">Thema</namespace>\n"
            + "    </namespaces>\n"
            + "  </siteinfo>";

    /**
     * Intitialisiert WikiContentHandler mit der zu extrahierenden Anzahl an
     * Kategoriespeziefischen Artikeln
     *
     * @param place_articles Anzahl der zu extrahierenden Orts-Artikel
     * @param person_articles Anzahl der zu extrahierenden Personen-Artikel
     * @param organisation_articles Anzahl der zu extrahierenden
     * Organisationen-Artikel
     */
    WikiContentHandler(int place_articles, int person_articles, int organisation_articles) {
        this.number_person_articles = person_articles;
        this.number_places_articles = place_articles;
        this.number_organisation_articles = organisation_articles;
    }

    /**
     * Aktuelle Zeichen die gelesen werden, werden in eine Zwischenvariable
     * gespeichert
     *
     * @param ch
     * @param start
     * @param length
     * @throws SAXException
     */
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        for (int i = start; i < start + length; i++) {
            buffer.append(ch[i]);
        }
        currentValue = buffer.toString();
    }

    /**
     * Methode wird aufgerufen wenn der Parser zu einem Start-Tag kommt
     *
     * @param uri
     * @param localName Starttag
     * @param qName
     * @param atts
     * @throws SAXException
     */
    public void startElement(String uri, String localName, String qName,
            Attributes atts) throws SAXException {
        //neuen Buffer erzeugen welches den Inhald des jeweiligen Textes zwischenspeichert
        buffer = new StringBuilder();
        // Wenn Tag <page> wird ein neues Artikel-Objekt erzeugt
        if (localName.equals("page")) {
            // Neues ArtikelObjekt erzeugen
            page = new Page();
        }
    }

    /**
     * Methode wird aufgerufen wenn der Parser zu einem End-Tag kommt Komplette
     * Kopie eines Artikels aus dem WikipediaDumps wird als Page Objekt erstellt
     *
     * @param uri
     * @param localName Name des Endtags
     * @param qName
     * @throws SAXException
     */
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        //Wenn Endtag </title> setze alles was im Buffer ist, also alles von Starttag <title> bis Endtag </title>, als title Attribut des Objektes Page
        if (localName.equals("title")) {
            page.setTitle(currentValue);
        }
        if (localName.equals("ns")) {
            page.setNs(currentValue);
        }
        if (localName.equals("id")) {
            page.setId(currentValue);
        }
        if (localName.equals("id")) {
            page.setId2(currentValue);
        }
        if (localName.equals("parentid")) {
            page.setParentid(currentValue);
        }
        if (localName.equals("timestamp")) {
            page.setTimestamp(currentValue);
        }
        if (localName.equals("username")) {
            page.setUsername(currentValue);
        }
        if (localName.equals("id")) {
            page.setId3(currentValue);
        }
        if (localName.equals("contributor")) {
            page.setContributor(currentValue);
        }
        if (localName.equals("minor")) {
            page.setMinor(currentValue);
        }
        if (localName.equals("comment")) {
            page.setComment(currentValue);
        }
        if (localName.equals("model")) {
            page.setModel(currentValue);
        }
        if (localName.equals("format")) {
            page.setFormat(currentValue);
        }
        if (localName.equals("text")) {
            page.setText(currentValue);
            //Wenn im Text der String "Typ=p" vorkommt, handelt es Sich um einen Personenbezogenen Artikel
            if (currentValue.contains("TYP=p")) {
                page.setType("PERSON");
            }
            //Wenn im Text der String "Typ=g" vorkommt, handelt es Sich um einen Ortsbezogenen Artikel
            if (currentValue.contains("TYP=g")) {
                page.setType("GEOGRAFIKUM");
            }
            //Wenn im Text der String "Typ=k" vorkommt, handelt es Sich um einen Organisationsbezogenen Artikel
            if (currentValue.contains("TYP=k")) {
                page.setType("ORGANISATION");
            }

        }
        if (localName.equals("shal")) {
            page.setSha1(currentValue);
        }
        if (localName.equals("revision")) {
            page.setRevision(currentValue);
        }

        // Artikel in Personen-ArrayList abspeichern falls </page> End-Tag erreicht wurde und Pagetyp = "PERSON" ist
        if (localName.equals("page") && page.getType() == "PERSON" && person_Pages.size() < number_person_articles) {
            person_Pages.add(page);
            //System.out.println("Neue Person");   
            // Artikel in OrtsArrayList abspeichern falls <page> End-Tag erreicht wurde und Pagetyp = "ORT" ist
        } else if (localName.equals("page") && page.getType() == "GEOGRAFIKUM" && places_Pages.size() < number_places_articles) {
            places_Pages.add(page);
            //System.out.println("Neue Ort");
            // Artikel in Artikel OrganisationenList abspeichern falls <page> End-Tag erreicht wurde und Pagetyp = "ORGANISATION" ist
        } else if (localName.equals("page") && page.getType() == "ORGANISATION" && organisation_Pages.size() < number_organisation_articles) {
            organisation_Pages.add(page);
            //System.out.println("Neue Organisation");
        }

        //Wenn genügend Artikel gefunden, brich bearbeitung ab
        if (person_Pages.size() == number_person_articles && places_Pages.size() == number_places_articles && organisation_Pages.size() == number_organisation_articles) {
            endDocument();
            throw new MySAXTerminatorException(); //Spezielle Exception
        }

    }

    /**
     * Wird ausgeführd wenn das Ende des zu parsenden Dokuments erreicht ist
     * oder MySAXTerminatorException gewurfen wurde, also die benötigte Anzahl
     * der Artikel gefunden wurde Schreibt die Extrahierten
     * Kategoriespeziefischen Artikel in Ausgabe-Datei
     *
     * @throws SAXException
     */
    public void endDocument() throws SAXException {
        try {
            System.out.println("Extraktion beendet... Beginne in Datei zu schreiben");
            //System.out.println("Anzahl der Extrahierten Personen-Artikel: " + person_Pages.size());
            //System.out.println("Anzahl der Extrahierten Orts-Artikel: " + places_Pages.size());
            //System.out.println("Anzahl der Extrahierten Organisationen-Artikel: " + organisation_Pages.size());

            BufferedWriter extractedData = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(PATH_OUTPUT), "UTF8"));
            extractedData.append(documentheader);
            for (Page p : places_Pages) {
                extractedData.append(p.toString());
                extractedData.newLine();
                extractedData.flush();
            }
            for (Page q : person_Pages) {
                extractedData.append(q.toString());
                extractedData.newLine();
                extractedData.flush();
            }
            for (Page r : organisation_Pages) {
                extractedData.append(r.toString());
                extractedData.newLine();
                extractedData.flush();
            }
            extractedData.flush();

            //Möglichkeit auch Artikel je Kategorie in separate Datei zu schreiben
            /*
            //System.out.println(person_Pages.toString());
            BufferedWriter places_file = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Orte.xml"), "UTF8"));
            BufferedWriter person_file = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Personen.xml"), "UTF8"));
            BufferedWriter organisation_file = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Organisationen.xml"), "UTF8"));

            places_file.newLine();
            places_file.append(documentheader);
            for (Page p : places_Pages) {
                places_file.append(p.toString());
                places_file.newLine();
                places_file.flush();
            }
            places_file.flush();

            person_file.append(documentheader);
            person_file.newLine();
            for (Page q : person_Pages) {
                person_file.append(q.toString());
                person_file.newLine();
            }
            person_file.flush();

            organisation_file.append(documentheader);
            organisation_file.newLine();
            for (Page r : organisation_Pages) {
                organisation_file.append(r.toString());
                organisation_file.newLine();
            }
            organisation_file.flush();
             */
        } catch (FileNotFoundException e) {
            System.out.println("Fehler: Schreiben in die Datei nicht möglich.");
            e.printStackTrace();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(WikiContentHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WikiContentHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Nicht benutze Funtkion
     *
     * @param prefix
     * @throws SAXException
     */
    public void endPrefixMapping(String prefix) throws SAXException {
    }

    /**
     * Nicht benutze Funtkion
     *
     * @param ch
     * @param start
     * @param length
     * @throws SAXException
     */
    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {
    }

    /**
     * Nicht benutze Funtkion
     *
     * @param target
     * @param data
     * @throws SAXException
     */
    public void processingInstruction(String target, String data)
            throws SAXException {
    }

    /**
     * Nicht benutze Funtkion
     *
     * @param locator
     */
    public void setDocumentLocator(Locator locator) {
    }

    /**
     * Nicht benutze Funtkion
     *
     * @param name
     * @throws SAXException
     */
    public void skippedEntity(String name) throws SAXException {
    }

    /**
     * Nicht benutze Funtkion
     *
     * @throws SAXException
     */
    public void startDocument() throws SAXException {
    }

    /**
     * Nicht benutze Funtkion
     *
     * @param prefix
     * @param uri
     * @throws SAXException
     */
    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
    }

    /**
     * Exception wird geworfen wenn Alle Benötigten Artikel gefunden wurden aber
     * das Ende des Dokumentes noch nicht erreicht wurde
     */
    private static class MySAXTerminatorException extends SAXException {

        public MySAXTerminatorException() {
        }
    }
}
