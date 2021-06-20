package searcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Searcher {

    public static final int LIMIT_QUERY_RESULT = 10;

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("No directory given to index.");
        }

        if (args.length != 1) {
            throw new IllegalArgumentException("Invalid number of arguments provided");
        }

        String directory = parseDirectoryFromArg(args[0]);

        var allPathFileNames = FilesystemReader.listAllFilesRecursively(directory);

        // todo validation, what if we cannot read the directory or some exception is thrown, I just unsafely unwrap...
        var dataStructure = buildInMemoryDataStructure(allPathFileNames.get());

        // If we wanted to add more characteristics to the cli, we could parse the inputByUser and have some sort of ADT
        // For instance Commands = SEARCH_0 | SEARCH_1 | QUIT
        // To support multiple searches algorithms and then a quit command
        String rawInputByUser = "";
        try (Scanner keyboard = new Scanner(System.in)) {
            while (userInputIsNotQuit(rawInputByUser)) {
                printOutInfo();

                // todo again validation of raw input, assuming its safe and sane
                rawInputByUser = keyboard.nextLine().strip();

                if (rawInputByUser.isEmpty()) {
                    System.out.println("Searcher> Invalid input, empty strings are invalid");
                    continue;
                }

                // being pragmatic here, i'd have pattern matching + adt if the lang supports it
                // if not tagged union with a switch for each command/case
                if (rawInputByUser.equals(":quit")) {
                    System.out.println("Searcher> Closing the Searcher cli");
                    closeAllInMemoryFileReader(dataStructure);
                    return;
                }

                var results = runAlgorithmOnDocuments(rawInputByUser, dataStructure);

                List<List<Algorithm.Result>> sortedResults = results.stream()
                        .sorted()
                        .collect(Collectors.toList());

                sortedResults.stream()
                        .limit(LIMIT_QUERY_RESULT)
                        .forEach(System.out::println);
            }

        }


    }

    private static void printOutInfo() {
        System.out.println("Searcher> Enter a text to run the algorithm. :quit to exit");
        System.out.println("Searcher> :quit to exit");
    }

    private static String parseDirectoryFromArg(String arg) {
        // todo validation, any criteria on parsing the input? assuming sane and valid
        return arg;
    }

    private static boolean userInputIsNotQuit(String rawInputByUser) {
        return !rawInputByUser.equals(":quit");
    }

    /**
     * From a given list of path file names, builds an InMemoryFileReader for each one
     * Notice that if we had some sort of LRU and we wanted to keep file contents in memory and delete based
     * on last access / modifications etc
     * In this step we could parametrize the behaviour of our instances
     *
     * @param allPathFileNames
     * @return
     */
    private static List<InMemoryFileReader> buildInMemoryDataStructure(List<String> allPathFileNames) {
        List<Optional<InMemoryFileReader>> inMemoryFileReaderStructure = allPathFileNames.stream()
                .map(InMemoryFileReader::newInstance)
                .collect(Collectors.toList());

        // todo validation again, what if files have been exclusively been opened previously / by another thread
        //  or something else fails
        List<InMemoryFileReader> dataStructure = inMemoryFileReaderStructure.stream()
                .map(Optional::get)
                .collect(Collectors.toList());

        return dataStructure;
    }

    /**
     * attempts to closes all resources
     *
     * @param dataStructure
     */
    private static void closeAllInMemoryFileReader(List<InMemoryFileReader> dataStructure) {
        try {
            for (InMemoryFileReader inMemoryFileReader : dataStructure) {
                inMemoryFileReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * attempts to run the selected algorithm on all documents in dataStructure
     *
     * @param textInputByUser base document for all rankings
     * @param dataStructure   list of fileReader contents
     * @return a list of algorithm.result
     */
    public static Optional<List<Algorithm.Result>> runAlgorithmOnDocuments(String textInputByUser, List<InMemoryFileReader> dataStructure) {
        String normalizedTextInputByUser = Algorithm.normalizeText(textInputByUser);
        List<Algorithm.Result> results = new ArrayList<>();
        try {
            for (InMemoryFileReader inMemoryFileReader : dataStructure) {
                String fileContent = inMemoryFileReader.readFileContent();
                inMemoryFileReader.resetPosition();

                String normalizedFileContent = Algorithm.normalizeText(fileContent);

                Algorithm.Input userInput = new Algorithm.Input("", normalizedTextInputByUser);
                Algorithm.Input fileInput = new Algorithm.Input(inMemoryFileReader.fileName, normalizedFileContent);

                Algorithm.Result result = Algorithm.rankSimilarity(userInput, fileInput);
                results.add(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
        return Optional.of(results);
    }

}

