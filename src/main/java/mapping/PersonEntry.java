package mapping;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Repräsentiert einzelne Einträge des Wörterbuchs.
 * @author Simon Bordewisch
 */
public class PersonEntry extends Entry {

    String fullName;

    public final float MISSING_NAME_PENALITY = 0.1f;
    public final float MIN_MATCH_VALUE = 0.5f;
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

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
        cleanName();
    }

    private void cleanName()
    {
        splitName();
        postCleanName();
    }

    private void splitName()
    {
        words = splitName(fullName);
    }

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

    private String[] extract(String fullName)
    {
        LinkedList<String> names = splitName(fullName);
        String[] nameArray = new String[names.size()];
        names.toArray(nameArray);
        return nameArray;
    }

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

    private LinkedList<String> postCleanName()
    {
        for(String name : words)
        {
            if(name.matches("^[a-z][\\p{L}]*")) {
                words.remove(name);
            }
        }
        return words;
    }

    /**
     * Parst Anredeprädikate
     *
     *
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

    public Match compareTo(LinkedList<String> sentencesList)
    {
        float matchValue = 0;
        int numberOfWords = 0;
        WordComparator comp = new WordComparator();
        for(int i=0; i<sentencesList.size();i++)
        {
            boolean contains=false;
            for(String name : words)
            {
                if(comp.compare(name,sentencesList.get(i)) == 0)
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
            return new Match(numberOfWords, category, getWordsAsString(), matchValue);
        }
        else {
            return null;
        }
    }

    public Match compareTo(String sentences) {
        LinkedList<String> sentencesList = new LinkedList<>(Arrays.asList(sentences.split(" ")));
        return compareTo(sentencesList);
    }

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