package searcher;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({FilesystemReaderTest.class, InMemoryFileReaderTest.class, AlgorithmTest.class})
public class TestSuite {
}
