package generatetrainingformat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Generate Training dient dazu den bereits getagten und tokenisierten
 * Text in das richtige Format für den NER zu bringen. Dafür werden tags entfernt und 'O' mit
 * der jeweiligen Kategoriebezeichnung getauscht
 *
 * @author Sebastian Gottwald
 */
public class GenerateTrainingFormat {

    /**
     * ersetze 'O' von durch Kategorien und lösche tags
     * @param path Pfad der zu bearbeitenden Datei (muss in tokenisierter und getagter Form vorliegen
     */
    public static void generateTraining(String path) {
        try {
            String line = "";
            //variablen zur bestimmung welche Kategorie der tag beschreibt
            boolean person = false; 
            boolean place = false;
            boolean organisation = false;
            
            //wenn Tag anfängt bzw schließt wird benötigt da tags mit  Sonderzeichen besonders tokenisiert werden
            boolean inBetweenTags = false;
            
            
            BufferedWriter file = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Ergebnisse/TrainingsData.out"), "UTF8"));
            BufferedReader br = new BufferedReader(new FileReader(path));
            
            //lese Datei Zeilenweise ein
            while ((line = br.readLine()) != null) {                
                //Wenn Orts-tag erkannt wird dann sind alle Zeilen bis zum schließen des Tags Ortsbeschreibungen
                if (line.startsWith("<Ort")) {
                    place = true;
                }
                if (line.startsWith("</Ort>")) {
                    place = false;
                }
                //Wenn Personen-tag erkannt wird dann sind alle Zeilen bis zum schließen des Tags Personenbeschreibungen
                if (line.startsWith("<Person")) {
                    person = true;
                }
                if (line.startsWith("</Person>")) {
                    person = false;
                }
                //Wenn Organisationen-tag erkannt wird dann sind alle Zeilen bis zum schließen des Tags Organisationsbeschreibungen
                if (line.startsWith("<Organisation")) {
                    organisation = true;
                }
                if (line.startsWith("</Organisation>")) {
                    organisation = false;
                }   
                //Wenn Zeile mit '<' anfängt befinden wir uns in einem Tag
                // Sonderfall bei mehrzeiliger tokenisierung
                if (line.startsWith("<")) {
                    inBetweenTags = true;
                }
                if (line.contains(">")) {
                    inBetweenTags = false;
                }    
                //Wenn tag geöffnet aber nich in selber Zeile die Kategorisierung steht lese die daruaffolgende Zeile aus und Kategorsiere
                if(inBetweenTags && (!place && !organisation && !person)){
                    String nextLine = br.readLine();
                    if (nextLine.startsWith("Person")){
                        person = true;
                    }else if(nextLine.startsWith("Organisation")){
                        organisation = true;
                    }else if (nextLine.startsWith("Ort")){
                        place= true;
                    }   
                }
                
                //ersete 'O' mit jeweiliger Kategorie
                if (organisation && !inBetweenTags) {
                    line = line.replaceAll("\tO", "\tORGANISATION");
                }
                if (person && !inBetweenTags) {
                    line = line.replaceAll("\tO", "\tPERSON");
                }
                if (place && !inBetweenTags) {
                    line = line.replaceAll("\tO", "\tORT");
                }
                
                //wenn Zeile nicht Inhalt eines tags ist oder mit Klammer anfängt so füge Sie zur Ausgabedatei hinzu
                //sonst verwerfe die zeile
                if (!line.startsWith("<") && !line.startsWith(">")) {
                    if(!inBetweenTags){
                        file.append(line + "\n");
                        file.flush();
                    }
                }
            }
            br.close();
            file.close();

        } catch (FileNotFoundException e) {
        } catch (IOException e) {

        }
    }

    
    public static void main(String[] args){
        GenerateTrainingFormat.generateTraining("Ergebnisse/Mapped.tok");
    }
}
