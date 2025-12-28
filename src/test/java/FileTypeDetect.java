import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;

public class FileTypeDetect {


    public static void main(String[] args) throws IOException {
        Tika tika = new Tika();
        String mimeType = tika.detect(new File("D:\\ComTemp\\model\\origin\\model_L_v3\\weights\\best.pt"));
        System.out.printf(mimeType);
    }
}
