package galimimus.org.hlsserver.helpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Helper {
    public static String readFromResourceStream(Path is){
        try {
            String tmp = Files.readString(is);
            System.out.println(tmp);
            return tmp;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
