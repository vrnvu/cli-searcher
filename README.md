# cli-searcher

## What are we doing

- Line driven text search engine
- fzf + similarity text ranking

## Example

We open up a tree pointing to the specified directory / path.

```
.cli-searcher
+-- filesystem
|   +-- a.txt
|   +-- b.json
|   +-- subdirectory
        |   +-- c.txt
```

Run the cli on /filesystem

```shell
java -jar Rank.jar Ranker /filesystem
```

We obtain a list of files:

```shell
files = [a.txt, b.json, c.txt]
```

We then want to run a query with a text expression.

And find similiarty among all this files based on it.

```shell
Ranker> some text to find and compare among files
a.txt:100%
b.json:90%
c.txt:0%
Ranker> new text to find and compare among files
a.txt:80%
b.json:70%
c.txt:65%
Ranker> :quit
```

## Pipeline

1. We read the filesystem and obtain all documents
2. Read input from user, text_search to base similarity upon
3. Vectorize, apply word embedding for each document
4. Apply similarity algorithm

## Run

With maven support, compile then run

```shell
mvn compile
mvn exec:java -Dexec.mainClass=searcher.Searcher -Dexec.args="filesystem"
mvn test -Dtest=searcher.TestSuite
```