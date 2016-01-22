package parsing;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.*;

//Wird benötigt um aus .bz2 WikipediaDump zu lesen
public class UtilFunction {
    /**
     * Copied from Stackoverflow http://stackoverflow.com/questions/4834721/java-read-bz2-file-and-uncompress-parse-on-the-fly
     * @param fileIn
     * @return
     * @throws FileNotFoundException
     * @throws CompressorException
     */
    public static BufferedReader getBufferedReaderForCompressedFile(String fileIn) throws FileNotFoundException, CompressorException {
        FileInputStream fin = new FileInputStream(fileIn);
        BZip2CompressorInputStream input = null;
        try {
            input = new BZip2CompressorInputStream(fin, true);
        } catch (IOException e) {
        }
        BufferedReader br2 = new BufferedReader(new InputStreamReader(input));
        return br2;
    }
    
    
}