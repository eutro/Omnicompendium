package eutros.omnicompendium.loader;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BookLoader {

    public static List<File> getEntries() {
        try {
            return Files.walk(new File(GitLoader.DIR, "guides").toPath(), FileVisitOption.FOLLOW_LINKS)
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .filter(f -> f.toString().endsWith(".md"))
                    .collect(Collectors.toList());
        } catch(IOException e) {
            return Collections.emptyList();
        }
    }

}
