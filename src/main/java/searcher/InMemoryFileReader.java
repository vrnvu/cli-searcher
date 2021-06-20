package searcher;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

public class InMemoryFileReader implements Closeable {
    public final String fileName;

    // fd
    private final RandomAccessFile reader;
    // channel to read content
    private final FileChannel channel;
    // lock to avoid concurrent access
    private final FileLock lock;

    /**
     * An searcher.InMemoryFileReader manually manages resources to read files.
     * Only one initial syscall is needed to get the file descriptor of the file.
     * A lock is used to assert that no one can modify the file while reading, also we can guarantee no one modifies
     * the file while the algorithm has not processed ALL files.  One strategy to not fully block the files could be
     * to allow a modification only after the algorithm has processed the file, this could be easily achieved by
     * choosing when to release the lock.
     * We initially consider that the algorithm performs a ranking by having the full document in memory, which is the
     * usual in the soa. Though we could have incremental applications strategies by reading chunks of the files by
     * modifying the readFileContent() method.
     *
     * @param path to hold in memory and open a fd FileReader to
     * @throws IOException if path is invalid or inaccessible
     */
    private InMemoryFileReader(String path) throws IOException {
        fileName = path;
        reader = new RandomAccessFile(path, "r");
        channel = reader.getChannel();
        lock = channel.tryLock(0L, Long.MAX_VALUE, true);
    }

    /**
     * Attempts to create a new searcher.InMemoryFileReader instance
     * Fails if path is invalid or inaccessible
     *
     * @param path to hold in memory and open a fd FileReader to
     * @return searcher.InMemoryFileReader instance of the path
     */
    public static Optional<InMemoryFileReader> newInstance(String path) {
        try {
            return Optional.of(new InMemoryFileReader(path));
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * readFileContent fully reads a file content (document) and return its value
     * Assuming UTF_8, since Java does not properly handle Strings, I did not worry too much...
     * Though easily fixable and parameterizable if needed
     * (#ref https://www.reddit.com/r/rust/comments/2b08l5/uft8_and_string_why_vecu8/)
     *
     * @return content of the file as string
     * @throws IOException
     */
    public String readFileContent() throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            int bufferSize = 1024;
            if (bufferSize > channel.size()) {
                bufferSize = (int) channel.size();
            }
            ByteBuffer buff = ByteBuffer.allocate(bufferSize);

            while (channel.read(buff) > 0) {
                out.write(buff.array(), 0, buff.position());
                buff.clear();
            }

            return out.toString(StandardCharsets.UTF_8);
        }
    }

    /**
     * resetPosition sets the FileChannel buffer pointer back to the start (0).
     *
     * @throws IOException
     */
    public void resetPosition() throws IOException {
        channel.position(0L);
    }

    /**
     * Closes all resources being handled by the searcher.InMemoryFileReader reference
     * In this case, the assumption that all of them would be freed at the end is made.
     * In case you wanted to modify some behaviour, this function should be modified accordingly.
     * I.e we do not want to hold the lock the whole execution, we should close it in another point
     * The trade off of this design vs a fully encapsulated OOPy style one is that we make our API users manually close
     * all the resources. Which imo is fine in the scope of this code, this is as internal representation anyway.
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        Objects.requireNonNull(lock).close();
        Objects.requireNonNull(channel).close();
        Objects.requireNonNull(reader).close();
    }
}
