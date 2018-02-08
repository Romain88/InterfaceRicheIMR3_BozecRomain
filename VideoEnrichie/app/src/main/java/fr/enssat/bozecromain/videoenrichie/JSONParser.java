package fr.enssat.bozecromain.videoenrichie;

/**
 * Created by romainbozec on 11/01/2018.
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class JSONParser {

    private ByteArrayOutputStream byteArrayOutputStream;

    public JSONParser(InputStream inputStreamR) {
        this.byteArrayOutputStream = new ByteArrayOutputStream();

        // Lecture des chapitres
        int lecture;
        try {
            lecture = inputStreamR.read();
            while (lecture != -1) {
                byteArrayOutputStream.write(lecture);
                lecture = inputStreamR.read();
            }
            inputStreamR.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ByteArrayOutputStream getByteArrayOutputStream() {
        return this.byteArrayOutputStream;
    }
}