package generatetrainingformat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Generate Training dient dazu den bereits gehighlighteten und tokenisierten Text in das richtige Format zu bringen
 * dafür werden tags entfernt und 'O' mit dem jeweiligen tag getauscht
 * @author Sebastian Gottwald
 */
public class GenerateTrainingFormat {

    /**
     * ersetze 'O' von durch tags markierten Wörtern und lösche tags
     * @param path 
     */
    public static void generateTraining(String path) {
        try {
            String line = "";
            boolean person = false;
            boolean place = false;
            boolean organisation = false;
            BufferedWriter file = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Ergebnisse/TrainingsData.out"), "UTF8"));
            BufferedReader br = new BufferedReader(new FileReader(path));
            while ((line = br.readLine()) != null) {
                if (line.startsWith("<Ort")) {
                    place = true;
                }
                if (line.startsWith("</Ort>")) {
                    place = false;
                }
                if (place) {
                    line = line.replaceAll("\tO", "\tORT");
                }
                if (line.startsWith("<Person")) {
                    person = true;
                }
                if (line.startsWith("</Person>")) {
                    person = false;
                }
                if (person) {
                    line = line.replaceAll("\tO", "\tPERSON");
                }
                if (line.startsWith("<Organisation")) {
                    organisation = true;
                }
                if (line.startsWith("</Organisation>")) {
                    organisation = false;
                }
                if (organisation) {
                    line = line.replaceAll("\tO", "\tORGANISATION");
                }
                if (!(line.startsWith("<"))) {
                    file.append(line + "\n");
                    file.flush();
                }
            }
            br.close();
            file.close();

        } catch (FileNotFoundException e) {
        } catch (IOException e) {

        }
    }
    
    /*
    public static void main(String[] args){
        GenerateTrainingFormat.generateTraining("test.tok");
    }
    */
}
