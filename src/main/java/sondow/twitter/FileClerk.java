package sondow.twitter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileClerk {

    public File getFile(String fileNameOrPath) {
        File file;
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileNameOrPath);
        if (resource == null) {
            // Looking by name with the classloader failed so try looking by path.
            File foundFile = new File(fileNameOrPath);
            if (foundFile.exists()) {
                file = foundFile;
            } else {
                file = null;
            }
        } else {
            String filePath = resource.getFile();
            file = new File(filePath);
        }
        return file;
    }

    public String readTextFile(String fileNameOrPath) {
        String contents = null;
        File file = getFile(fileNameOrPath);
        if (file != null) {
            try {
                Path path = Paths.get(file.getPath());
                contents = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return contents;
    }
}
