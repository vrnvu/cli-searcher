package searcher;

import org.junit.Test;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.OverlappingFileLockException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InMemoryFileReaderTest {
    String VALID_PATH = "filesystem\\a.txt";
    String INVALID_PATH = "invalid";
    String EXPECTED_TEXT_IN_VALID_PATH = "some text to find and rank.";

    @Test
    public void newInstance() throws IOException {
        Optional<InMemoryFileReader> inMemoryFileReader = InMemoryFileReader.newInstance(VALID_PATH);
        assertTrue(inMemoryFileReader.isPresent());
        inMemoryFileReader.get().close();
    }

    @Test
    public void newInstanceOfInvalidPath() {
        Optional<InMemoryFileReader> inMemoryFileReader = InMemoryFileReader.newInstance(INVALID_PATH);
        assertTrue(inMemoryFileReader.isEmpty());
    }

    @Test
    public void readFileContent() throws IOException {
        InMemoryFileReader inMemoryFileReader = InMemoryFileReader.newInstance(VALID_PATH).get();
        String content = inMemoryFileReader.readFileContent();
        inMemoryFileReader.close();
        assertEquals(content, EXPECTED_TEXT_IN_VALID_PATH);
    }

    @Test
    public void readFileContentWithTry() {
        // did you notice the close()? actually...
        // notice that our object is closable so we could use it in a try with
        // sadly java does not play well with modern patterns like if let Some(x)
        // so I keep to keep adding the .get() or if we used another type safe holder like either/result :(
        try (InMemoryFileReader inMemoryFileReader = InMemoryFileReader.newInstance(VALID_PATH).get()) {
            String content = inMemoryFileReader.readFileContent();
            assertEquals(content, EXPECTED_TEXT_IN_VALID_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void resetPosition() {
        // Since this method is just a wrapper we wrote for convenience we do not need to test it deeply
        // I'm just going to proof my POC by double reading an already open fd/channel
        // It would be cool to have a LRU and keep the content open in memory
        try (InMemoryFileReader inMemoryFileReader = InMemoryFileReader.newInstance(VALID_PATH).get()) {
            String firstRead = inMemoryFileReader.readFileContent();

            inMemoryFileReader.resetPosition();

            String secondRead = inMemoryFileReader.readFileContent();

            assertEquals(firstRead, EXPECTED_TEXT_IN_VALID_PATH);
            assertEquals(secondRead, EXPECTED_TEXT_IN_VALID_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = ClosedChannelException.class)
    public void close() throws IOException {
        // one basic test of error would be attempting to double close some resources
        // also note worthy that if we attempt to refactor this function in the future because we change the behaviour
        // we should be able to detect memory leaks (unclosed fds i.e) through testing
        InMemoryFileReader inMemoryFileReader = InMemoryFileReader.newInstance(VALID_PATH).get();
        inMemoryFileReader.close();
        inMemoryFileReader.close();
    }

    @Test(expected = OverlappingFileLockException.class)
    public void attemptingToOpenLockedResource() {
        InMemoryFileReader.newInstance(VALID_PATH);
        // now this call will fail since we have not closed the lock reference
        InMemoryFileReader.newInstance(VALID_PATH);
    }

}