package parsing;

/**
 * 
 * @author Sebastian Gottwald
 */
public class Page {

    private String title;
    private String ns;
    private String id;
    private String id2;
    private String parentid;
    private String timestamp;
    private String username;
    private String id3;
    private String contributor;
    private String minor;
    private String comment;
    private String model;
    private String format;
    private String text;
    private String sha1;
    private String revision;
    private String type;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setNs(String ns) {
        this.ns = ns;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setId2(String id2) {
        this.id2 = id2;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId3(String id3) {
        this.id3 = id3;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getTitle() {
        return title;
    }

    public String getNs() {
        return ns;
    }

    public String getId() {
        return id;
    }

    public String getId2() {
        return id2;
    }

    public String getParentid() {
        return parentid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getUsername() {
        return username;
    }

    public String getId3() {
        return id3;
    }

    public String getContributor() {
        return contributor;
    }

    public String getMinor() {
        return minor;
    }

    public String getComment() {
        return comment;
    }

    public String getModel() {
        return model;
    }

    public String getFormat() {
        return format;
    }

    public String getText() {
        return text;
    }

    public String getSha1() {
        return sha1;
    }

    public String getRevision() {
        return revision;
    }

    public String getType() {
        return type;
    }

    public Page() {
    }

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
