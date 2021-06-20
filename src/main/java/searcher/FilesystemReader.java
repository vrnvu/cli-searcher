package searcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilesystemReader {
    /**
     * Attempts to list all files in the directory provided by parameter.
     * NOTE: Since java does not have a Result type and I did not want to use one, I return an optional
     * A Result<T, Err> would be more explicit and nicer
     *
     * @param dir full directory path to look into recursively
     * @return list of available files on path
     */
    public static Optional<List<String>> listAllFilesRecursively(String dir) {
        try {
            return Optional.of(Files.walk(Paths.get(dir))
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .collect(Collectors.toList()));
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static Optional<List<String>> listAllFiles(String dir) {
        try (Stream<Path> stream = Files.list(Paths.get(dir))) {
            return Optional.of(stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::toString)
                    .collect(Collectors.toUnmodifiableList()));
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
