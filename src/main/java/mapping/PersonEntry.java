package mapping;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Repräsentiert einzelne Personen-Einträge des Wörterbuchs.
 * @author Simon Bordewisch
 */
public class PersonEntry extends Entry {

    /**
     * Voller Name der Person
     */
    String fullName;


    /**
     * Maximale Anzahl der fehlenden Wörter im Teilsatz, um noch als Match zu gelten
     */
    public final int MAX_MISSING_WORDS = 2;


    /**
     * Konstruiert Eintrag mit Liste von Wörtern.
     * Übergibt Liste und Kategorie.
     *
     * @param wordsList Liste von Wörtern
     * @param category  Kategorie der Wörter
     */
    public PersonEntry(LinkedList<String> wordsList, String category) {
        super(wordsList, category);
    }


    /**
     * Konstruiert Eintrag mit einem einzelnen Wort.
     * Erstellt Liste und ruft
     * {@link #PersonEntry(LinkedList, String) Listen-Kontruktor} auf.
     *
     * @param words Wort, das als Eintrag hinzugefügt werden soll
     * @param category Kategorie des einzutragenden Wortes
     */
    public PersonEntry(String words, String category) {
        super(category);
        setFullName(words);
    }


    /**
     * Konstruiert Eintrag aus Wörter-Array.
     * Wandelt Wörter-Array zu Liste um und ruft
     * {@link #PersonEntry(LinkedList, String) Listen-Kontruktor} auf.
     *
     * @param wordsArray Array mit Wörtern für den Eintrag
     * @param category   Kategorie des Eintrags
     */
    public PersonEntry(String[] wordsArray, String category) {
        super(wordsArray, category);
    }


    /**
     * Speichert vollen Namen.
     * @param fullName zu speichernder Name
     */
    public void setFullName(String fullName)
    {
        this.fullName = fullName;
        cleanName();
    }


    /**
     * Prozedur zur bereinigung des Namens.
     * Siehe {@link #splitName()} und {@link #postCleanName()}
     */
    private void cleanName()
    {
        splitName();
        postCleanName();
    }

    /**
     * Teilt Wörter in Teil-Namen auf und übergibt diese an Variable words.
     * Vergleiche {@link #splitName(String)}.
     */
    private void splitName()
    {
        words = splitName(fullName);
    }


    /**
     * Teilt Wörter in Teil-Namen auf und gibt Liste mit diesen auf.
     * Beispiel: "Karl-Theodor zu Guttenberg" wird zu ["Karl-Theodor", "zu Guttenberg"].
     * @param fullName voller Name der Person
     * @return Liste der Teil-Namen
     */
    private LinkedList<String> splitName(String fullName)
    {
        LinkedList<String> names = new LinkedList<String>(); // Liste zum speichern
        String[] nameSplit = preCleanName(fullName);
        if(nameSplit.length<3) // i.e. max. nur Vor- & Nachname
        {
            for(String name : nameSplit)
            {
                names.add(name);
            }
        } else  //mehr Namen, Tests für Anredeprädikate
        {
            //Suche Prädikat in Array
            for(int i=0;i<nameSplit.length;i++)
            {
                if(nameSplit[i].matches("^[a-z].*")) //gefunden, Stelle markieren & abbrechen
                {
                    return particleParser(nameSplit, i);
                }
            }
            for(String name : nameSplit)
            {
                names.add(name);
            }

        }
        return names;
    }


    /**
     * Extrhiert Teil-Namen des übergebenen Strings als Array.
     * @param fullName zu extrahierender String
     * @return Array mit Teil-Namen des übergebenen Strings
     */
    private String[] extract(String fullName)
    {
        LinkedList<String> names = splitName(fullName);
        String[] nameArray = new String[names.size()];
        names.toArray(nameArray);
        return nameArray;
    }


    /**
     * Vor-Bereinigung des Strings, entfernt Klammern,
     * Abkürzungen (mit ".") und Namen, die nur Sonderzeichen beinhalten.
     * @param fullName String, der vorbereinigt werden soll
     * @return vorbereinigter String als Array
     */
    private String[] preCleanName(String fullName)
    {
        fullName = fullName.replaceAll("\\(.*\\)", ""); // Klammern entfernen

        String[] nameSplit = fullName.split(" ");
        LinkedList<String> nameSplitList = new LinkedList<String>();
        for(String name : nameSplit)
        {
            if(!name.contains(".") && !name.matches("[^\\p{L}]*") && name.length() > 1)
                nameSplitList.add(name);
        }
        String[] nameArray = new String[nameSplitList.size()];
        nameSplitList.toArray(nameArray);
        return nameArray;
    }


    /**
     * Bereinigt Wörter, die nur klein geschrieben werden
     * @return Liste mit bereinigten Wörtern
     */
    private LinkedList<String> postCleanName()
    {
        for(String name : words)
        {
            if(name.matches("^[a-zäöü][\\p{L}]*")) {
                words.remove(name);
            }
        }
        return words;
    }

    /**
     * Parst Anredeprädikate (von, zu, ...).
     * @param nameSplit Array mit zu bearbeitenden Wörtern
     * @param locParticle Zeiger auf die Stelle, in der das Anredeprädikat steht
     * @return Liste, in der die Anredeprädikate und Nachnamen als ein Eintrag gelten
     */
    private LinkedList<String> particleParser(String[] nameSplit, int locParticle)
    {
        LinkedList<String> names = new LinkedList<String>(); // Liste zum speichern
        //Teil des Arrays ohne Prädikat
        for(int i=0; i<locParticle;i++)
        {
            names.add(nameSplit[i]);
        }
        String remainName=nameSplit[locParticle];

        //Teil des Arrays mit Prädikat, füge alle Namen zu einem zusammen
        for(int i=locParticle+1;i<nameSplit.length;i++)
        {
            remainName += " " + nameSplit[i];
        }
        if(!remainName.isEmpty())
        {
            names.add(remainName);
        }
        return names;
    }

    /**
     * Vergleichsfunktion, die einen Teilsatz auf den Eintrag vergleicht und ein Match zurückgibt, falls Ähnlichkeit
     * hoch genug.
     * @param sentencesList zu vergleichender Teilsatz als Liste
     * @return Match-Objekt, falls Match-Wert (@see MIN_MATCH_VALUE) hoch genug, sonst null
     */
    @Override public Match compareTo(LinkedList<String> sentencesList)
    {
        float matchValue = 0;
        int numberOfWords = 0;
        WordComparator comp = new WordComparator();
        for(int i=0; i<sentencesList.size();i++)
        {
            boolean contains=false;
            for(String name : words)
            {
                if(comp.compare(name,sentencesList.get(i)) == 0) //match
                {
                    contains = true;
                    numberOfWords++;
                    matchValue+=comp.getWordSimilarity(name, sentencesList.get(i));
                }
            }
            if(!contains) {
                break;
            }
        }
        if(numberOfWords>0) {
            matchValue /= numberOfWords;
        }
        if(size() > numberOfWords) {
            if (size() > MAX_MISSING_WORDS + numberOfWords) // es fehlen mehr Wörter als dürfen
            {
                return null;
            } else
            {
                matchValue -= MISSING_NAME_PENALITY * (size() - numberOfWords);
            }
        }
        if(matchValue > MIN_MATCH_VALUE) {
            return new Match(numberOfWords, category, getEntryAsString(), matchValue);
        }
        else {
            return null;
        }
    }

    /**
     * Vergleichsfunktion, die einen Teilsatz auf den Eintrag vergleicht und ein Match zurückgibt, falls Ähnlichkeit
     * hoch genug.
     * @param sentences zu vergleichender Teil-Satz als ein String
     * @return Match-Objekt, falls Match-Wert (@see MIN_MATCH_VALUE) hoch genug, sonst null
     */
    public Match compareTo(String sentences) {
        LinkedList<String> sentencesList = new LinkedList<>(Arrays.asList(sentences.split(" ")));
        return compareTo(sentencesList);
    }


    /**
     * Gibt alle Keys des Wörterbuchs, also das erste Wort der Einträge, als Array aus
     * @return alle Keys des Wörterbuchs als Array
     */
    public String[] getKeys ()
    {
        String[] keys = new String[words.size()];
        for(int i=0;i<words.size();i++)
        {
            keys[i] = words.get(i);
        }
        return keys;
    }


    /**
     * Main-Methode, hauptsächlich für Testzwecke dieser Klasse gedacht.
     */
    public static void main(String[] args) {
        /*String[] array = {"Angelina Jolie",
                "Archimedes",
                "Al-Biruni",
                "Andrei Dmitrijewitsch Linde",
                "Annette von Droste-Hülshoff",
                "H. P. Lovecraft",
                "Angela Merkel (Bundeskanzlerin)",
                "Obama (Präsident USA)",
                "Meister von 1473",
                "Otto Merker"};
        String[] array2 = {"Annette von Droste-Hülshoff", "Hans am Ende"};
        System.out.println("Test-Ausgabe:");
        for(String name : array2)
        {
            Entry entry = new PersonEntry(name, "Person");
            System.out.println(name + ": " +  entry.getWords());
            for(String word : entry.getKeys())
            {
                System.out.println(word);
            }
        }*/
        PersonEntry schroeder = new PersonEntry("Merkel", "Person");
        System.out.println(schroeder.compareTo("Merkels"));
    }
}