package parsing;

/**
 * Jedem Wikipedia Artikel wird ein ArtikelObjekt zugeordnet
 * Page Objekt ist quasie Kopie eines XML-Artikels
 * @author Sebastian Gottwald
 */
public class Page {

    /**
     * Inhalt des title tags
     */
    private String title;
    /**
     * Inhalt des ns tags
     */
    private String ns;
    /**
     * Inhalt des id tags
     */
    private String id;
    /**
     * Inhalt des id tags
    */
    private String id2;
    /**
     * Inhalt des parenid tags
     */
    private String parentid;
    /**
     * Inhalt des timestamp tags
     */
    private String timestamp;
    /**
     * Inhalt des username tags
     */
    private String username;
    /**
     * Inhalt des id tags
     */
    private String id3;
    /**
     * Inhalt des cotributor tags
     */
    private String contributor;
    /**
     * Inhalt des minor tags
     */
    private String minor;
    /**
     * Inhalt des comment tags
     */
    private String comment;
    /**
     * Inhalt des model tags
     */
    private String model;
    /**
     * Inhalt des format tags
     */
    private String format;
    /**
     * Inhalt des text tags
     */
    private String text;
    /**
     * Inhalt des shal tags
     */
    private String sha1;
    /**
     * Inhalt des revision tags
     */
    private String revision;
    /**
     * Kategorie eines Artikels, wird in unserem Programm nur bei Organisationen-, Personen- und Orts-Artikeln gesetzt
     */
    private String type;

    /**
     * Setzt den title eines Artikels
     * @param title 
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Setzt den type eines Artikels
     * @param type 
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Setzt den namespace eines Artikels
     * @param ns 
     */
    public void setNs(String ns) {
        this.ns = ns;
    }

    /**
     * Setzt die id eines Artikels
     * @param id 
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Setzt die zweite id eines Artikels
     * @param id2 
     */
    public void setId2(String id2) {
        this.id2 = id2;
    }

    /**
     * Setzt die parentid eines Artikels
     * @param parentid 
     */
    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    /**
     * Setzt den timestamp eines Artikels
     * @param timestamp 
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Setzt den username eines Artikels
     * @param username 
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Setzt die dritte id eines Artikels
     * @param id3 
     */
    public void setId3(String id3) {
        this.id3 = id3;
    }

    /**
     * Setzt den cotributor eines Artikels
     * @param contributor 
     */
    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    /**
     * Setzt den minor eines Artikels
     * @param minor 
     */
    public void setMinor(String minor) {
        this.minor = minor;
    }

    /**
     * Setzt den comment eines Artikels
     * @param comment 
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Sezt das model eines Artikels
     * @param model 
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Setzt das format eines Artikels
     * @param format 
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Setzt den text eines Artikels
     * @param text 
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Setzt den sha1 eines Artikels
     * @param sha1 
     */
    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    /**
     * Setzt den revision eines Artikels
     * @param revision 
     */
    public void setRevision(String revision) {
        this.revision = revision;
    }

    /**
     * Übergibt den type eines Artikels
     * @return type
     */
    public String getType() {
        return type;
    }
    /**
     * toString Methode, welche eine Kopie eines XML-Artikels zurück gibt
     * @return String
     */
    @Override
    public String toString() {
        return  "<page>" + "\n"
                + "<title>" + title + "</title>" + "\n"
                + "<ns>" + ns + "</ns>" + "\n"
                + "<id>" + id + "</id>" + "\n"
                + "<revision>"
                + "<id>" + id2 + "</id>" + "\n"
                + "<parentid>" + parentid + "</parentid>" + "\n"
                + "<timestamp>" + timestamp + "</timestamp>" + "\n"
                + "<contributor>" + "\n"
                + "<username>" + username + "</username>" + "\n"
                + "<id>" + id3 + "</id>" + "\n"
                + "</contributor>" + "\n"
                + "<minor />" + "\n"
                + "<comment>" + comment + "</comment>" + "\n"
                + "<model>" + model + "</model>" + "\n"
                + "<format>" + format + "</format>" + "\n"
                + "<text xml:space=\"preserve\">" + text + "</text>" + "\n"
                + "<sha1>" + sha1 + "</sha1>" + "\n"
                + "</revision>" + "\n"
                + "</page>";
    }
}
