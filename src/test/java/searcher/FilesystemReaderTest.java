package searcher;


import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FilesystemReaderTest {

    @Test
    public void listAllFilesRecursivelyInFilesystem() {
        // I like table driven testing like in go
        // i'm wondering if this path representation is OS dependent or if java manages it properly
        // I know in rust we use a std::path library that manages this edge cases
        String[] expected = new String[]{
                "filesystem\\a.txt",
                "filesystem\\b.json",
                "filesystem\\subdirectory\\c.txt"
        };

        var allPathFileNames = FilesystemReader.listAllFilesRecursively("filesystem");
        List<String> paths = allPathFileNames.get();
        for (int i = 0; i < paths.size(); i++) {
            assertEquals(paths.get(i), expected[i]);
        }
    }

    @Test
    public void invalidPathAssertion() {
        // I wanted to use Optional<T> to open up the Result<T, err> discussion
        // In another case we would test assert the checked exception :/
        var allPathFileNames = FilesystemReader.listAllFilesRecursively("not found");
        assertTrue(allPathFileNames.isEmpty());
    }
}