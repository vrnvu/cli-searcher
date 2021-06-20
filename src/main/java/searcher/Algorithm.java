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
     * https://towardsdatascience.com/the-best-document-similarity-algorithm-in-2020-a-beginners-guide-a01b9ef8cf05
     * I kept it similar to Jaccard's algorithm, set intersections and unions, conceptually easy to follow as first version
     * https://github.com/massanishi/document_similarity_algorithms_experiments/blob/master/jaccard/process_jaccard_similarity.py
     *
     * @param userInput to this algorithm
     * @param fileInput to this algorithm
     * @return a Result data struct containing the fileName and its score
     */
    public static Result rankSimilarity(Algorithm.Input userInput, Algorithm.Input fileInput) {
        String textInputByUser = userInput.document;
        String textDocumentToEvaluate = fileInput.document;

        //  This should be precached
        Set<String> setByUser = new HashSet<>(Arrays.asList(textInputByUser.split(" ")));
        Set<String> setByDocument = new HashSet<>(Arrays.asList(textDocumentToEvaluate.split(" ")));

        Set<String> union = new HashSet<>();
        union.addAll(setByDocument);
        union.addAll(setByUser);

        // intersection stored in setByDocument reference
        setByDocument.retainAll(setByUser);
        int score = (setByDocument.size() * 100) / union.size();

        return new Result(fileInput.fileName, (int) score);
    }

    /**
     * Depending on the algorithm we need to apply a normalization
     * For algorithms similar to TF-IDF a vectorization
     * Also ML algorithms will expect a full document as an input, we won't be able to process them in chunks
     *
     * @param rawInput original string to normalize
     * @return normalized document according to some rules, this rules can be algorithm depedent
     */
    public static String normalizeText(String rawInput) {
        String alphaText = rawInput.replaceAll("[^a-zA-Z0-9]", " ");
        return alphaText.toLowerCase(Locale.ROOT);
    }

}
