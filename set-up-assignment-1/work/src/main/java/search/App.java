package search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;


public class App 
{
    public static void main( String[] args )
    {   

        String scorer = " ";
        Similarity similarity;
        // Here we select our analyzer
        // Uncomment either of the other analyzing approaches to test other ones

        // Analyzer analyzer = new StandardAnalyzer();
        //Analyzer analyzer = new WhitespaceAnalyzer();
        Analyzer analyzer = new EnglishAnalyzer();
        //Analyzer analyzer = new SimpleAnalyzer();
            
        //Scoring approach selection
                    switch(args[0]) {
                case "vsm":
                    similarity = new ClassicSimilarity();
                    scorer = "vsm";
                    break;
                case "bm25":
                    similarity = new BM25Similarity();
                    scorer = "bm25";
                    break;
                case "boolean":
                    similarity = new BooleanSimilarity();
                    scorer = "boolean";
                    break;
                default:
                    similarity = new BM25Similarity();
                    break;
            }

        System.out.println("*** CREATING INDEX ***");
        new Index(analyzer);
        System.out.println("*** CREATING QUERY INDEX ***");
        new SearchQuery(analyzer, similarity, scorer);

    }
}
