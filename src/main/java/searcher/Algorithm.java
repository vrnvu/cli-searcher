package searcher;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Algorithm {

    /**
     * I wrote these wrappers just to anticipate to the question
     * How would you handle different algorithm input format/schema requirements?
     */
    static class Input {
        public String fileName;
        public String document;

        public Input(String fileName, String document) {
            this.fileName = fileName;
            this.document = document;
        }
    }

    static class Result implements Comparable<Result> {
        public String fileName;
        public int ranking;

        public Result(String fileName, int ranking) {
            this.fileName = fileName;
            this.ranking = ranking;
        }

        @Override
        public int compareTo(Result o) {
            return -Integer.compare(ranking, o.ranking);
        }

        @Override
        public String toString() {
            return "Result{" +
                    "fileName='" + fileName + '\'' +
                    ", ranking=" + ranking +
                    '}';
        }
    }

    /**
     * Main algorithm execution loop
     *
     * @param userInput
     * @param fileInput
     * @return
     */
    public static Result rankSimilarity(Algorithm.Input userInput, Algorithm.Input fileInput) {
        String textInputByUser = userInput.document;
        String textDocumentToEvaluate = fileInput.document;

        //  This should be precached
        Set<String> setByUser = new HashSet<>(Arrays.asList(textInputByUser.split(" ")));
        Set<String> setByDocument = new HashSet<>(Arrays.asList(textDocumentToEvaluate.split(" ")));

        setByDocument.retainAll(setByUser);
        int score = setByUser.size() - setByDocument.size();

        System.out.println(setByUser);
        System.out.println(setByDocument);
        return new Result(fileInput.fileName, score);
    }

    /**
     * Depending on the algorithm we need to apply a normalization
     * Probably for algorithms similar to TF-IDF a vectorization
     *
     * @param rawInput
     * @return
     */
    public static String normalizeText(String rawInput) {
        String alphaText = rawInput.replaceAll("[^a-zA-Z0-9]", " ");
        return alphaText.toLowerCase(Locale.ROOT);
    }

}
