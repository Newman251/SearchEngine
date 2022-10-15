package search;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

class SearchQuery {
    
    // Limit the number of search results we get
	private static int topHits = 25;

    private static String path = "corpus/cran.qry";

    String similarityChoice;

    // Array list to hold the queries
    private ArrayList<QueryStore> queries = new ArrayList<QueryStore>();
    // Array list to hold search results
    private ArrayList<ResultStore> results = new ArrayList<ResultStore>();

    public SearchQuery(Analyzer analyzer, Similarity similarity, String scorer) {
        try {
            // Parse the query data
            parseCorpus(path);

            similarityChoice = scorer;
            // Start the querying then save file
            queryIndexFromQueries(analyzer, similarity);

            // Holds the path to the file we want to save
            String filePath = "QueryResults/Scores."+similarityChoice;

            // Create a new file
            FileWriter fileWriter = new FileWriter(filePath);
            // Create a new print writer
            PrintWriter printWriter = new PrintWriter(fileWriter);

            // Loop through the results and print them to the file (in TREC format)
            for (ResultStore result: getResults()) {
                printWriter.println(result.toTrecEvalFormat());
            }
            printWriter.close();
        
            System.out.println("*** RESULTS SAVED TO QUERYRESULTS FOLDER ***");

        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseCorpus(String path) throws IOException {
        // Setup buffered reader and open file
        BufferedReader bufread = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
        String strLine;

        int queryId = 1;
        String text = "";
        Boolean firstIter = true;

        System.out.println("*** STARTED QUERY PARSING ***");

        //Read each line
        while ((strLine = bufread.readLine()) != null) {
            if(strLine.startsWith(".I")) {
                // skip the first iteration 
                if(!firstIter) {
                    // Add the query to the array list
                    QueryStore query = new QueryStore(queryId, text);
                    // Add the previous query to the array of queries
                    addQuery(query);
                    queryId++;
                    text = "";
                }
                firstIter = false;
            }
            // Save query content itself if the content doesnt start with .W
            else if(!strLine.startsWith(".W")) {
                if(text == "") {
                    text = strLine;
                }
                else {
                    text = text + "\n" + strLine;
                }
            }
        }
        
        // Add final query item
        QueryStore query = new QueryStore(queryId, text);
        addQuery(query);

        System.out.println("*** FINISHED PARSING ***");
        bufread.close();

    }

    public void queryIndexFromQueries(Analyzer analyzer, Similarity similarity) throws IOException, ParseException {
        // Open index folder
		Directory directory = FSDirectory.open(Paths.get("../index"));
		
        // Create a reader to read the index
		DirectoryReader ireader = DirectoryReader.open(directory);
        // Create a searcher to search the index
		IndexSearcher isearcher = new IndexSearcher(ireader);

        // Set the similarity to the one defined in App.java
        isearcher.setSimilarity(similarity);

        // Create a query parset
		QueryParser parser = new QueryParser("Body", analyzer);

        System.out.println("*** STARTED QUERYING INDEX ***");

        for (QueryStore cranQuery: this.queries) {
            // Parse the query
            String searchBody = removeSpecialChars(cranQuery.getContent());
            Query query = parser.parse(searchBody);
            // Search the index
            ScoreDoc[] hits = isearcher.search(query, topHits).scoreDocs;
            System.out.println("QUERY ID " + cranQuery.getId() + " COMPLETE");
            // Add the results to the array list using results class
            for (int i = 0; i < topHits; i++) {
                Document hitDoc = isearcher.doc(hits[i].doc);
                ResultStore searchResult = new ResultStore(cranQuery.getId(), Integer.valueOf(hitDoc.get("ID")), i+1, hits[i].score);
                    this.results.add(searchResult);
		    }
        }

        // Close the reader and directory
		ireader.close();
		directory.close();
        System.out.println("*** FINISHED QUERYING INDEX ***");
    }

    public String removeSpecialChars(String query) {
        return query.replaceAll("\\?", "");
    }

    public void addQuery(QueryStore query) {
        this.queries.add(query);
    }

    public ArrayList<ResultStore> getResults() {
        return this.results;
    }

}