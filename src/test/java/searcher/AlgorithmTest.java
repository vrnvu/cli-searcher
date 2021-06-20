package searcher;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AlgorithmTest {

    @Test
    public void rankSimilarity100() {
        Algorithm.Input input = new Algorithm.Input("", "hello world");
        Algorithm.Input fileInput = new Algorithm.Input("", "hello world");
        Algorithm.Result result = Algorithm.rankSimilarity(input, fileInput);
        assertEquals(result.ranking, 100);
    }

    @Test
    public void rankSimilarity0() {
        Algorithm.Input input = new Algorithm.Input("", "hello world");
        Algorithm.Input fileInput = new Algorithm.Input("", "not matching");
        Algorithm.Result result = Algorithm.rankSimilarity(input, fileInput);
        assertEquals(result.ranking, 0);
    }

    @Test
    public void rankSimilarity33() {
        // we will have a union = hello world nope
        // our document only has one element matching, hello
        // so thats 1/3 ~ 33%
        Algorithm.Input input = new Algorithm.Input("", "hello world");
        Algorithm.Input fileInput = new Algorithm.Input("", "hello nope");
        Algorithm.Result result = Algorithm.rankSimilarity(input, fileInput);
        assertEquals(result.ranking, 33);
    }

    @Test
    public void normalizeText() {
        // for ml models the input is really important, for big scenarios we should have pre cached / computed
        // layers of the normalization pipeline, sometimes you have models just for feature extraction before
        // applying a class feature model or such
        String expected = "0nly alph4numer1c ";
        String actual = Algorithm.normalizeText("0nly!alph4numer1C?");
        assertEquals(actual, expected);
    }
}