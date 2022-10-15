package search;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import java.nio.file.Paths;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Index
{
    // Directory where the search index will be saved
    private static String INDEX_DIR = "../index";

    // Index creater
    public Index(Analyzer analyzer) {
        try{
            dataIndexer(analyzer);
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dataIndexer(Analyzer analyzer) throws IOException {

        Directory directory = FSDirectory.open(Paths.get(INDEX_DIR));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        
        IndexWriter iwriter = new IndexWriter(directory, config);
        
        // Setup buffered reader and open file
        BufferedReader bufread = new BufferedReader(new InputStreamReader(new FileInputStream("corpus/cran.all.1400")));
        
        String strLine; // For reading each line
        String currentTag = "ID"; // Since the file starts with and ID tag
        String previousTag = "";
        String contentOfTag = "";
        Boolean newContent = true;
        Boolean firstIter = true;

        // ArrayList of documents in the corpus
		ArrayList<Document> documents = new ArrayList<Document>();
        // Create new document for array
        Document doc = new Document();

        System.out.println("*** STARTED PARSING ***");

        // Read line by line
        while ((strLine = bufread.readLine()) != null)   {
            // If the new line is a new tag (.I / .W etc..)
            if (newTag(strLine)) {
                // Update tags
                previousTag = currentTag;
                currentTag = tagName(strLine);
                // currentTag will become equal to ID, Title, Author, Bibliography or Body
                if(!"ID".equals(previousTag)) {
                    // Add content of previously tagged data
                    doc.add(new TextField(previousTag, contentOfTag, Field.Store.YES));
                }
                // If ID then add created document to array
                if (idTag(strLine)) {
                    // Get document value
                    String docId = strLine.substring(3);
                    if (!firstIter) {
                        // Add previous document to array, provided it is not the first document
                        documents.add(doc);
                    }
                    // For first iteration
                    firstIter = false;
                    // Create new document in array
                    doc = new Document();
                    // Add id to document
                    doc.add(new TextField(currentTag, docId, Field.Store.YES));
                    System.out.println("INDEXED DOCUMENT - " + docId);
                }
                newContent = true;
            }
            else { 
                // Check if new tag has been found with new content, then start generating content for new tag
                if (newContent) {
                    contentOfTag = strLine;
                    newContent = false; 
                }
                else {
                // Add new content to string of current tag content
                    contentOfTag = contentOfTag + "\n" + strLine;
                }
            }
        }

        // The last document
        doc.add(new TextField(currentTag, contentOfTag, Field.Store.YES));
        documents.add(doc);

        // Save to index
        iwriter.addDocuments(documents);

        // Commit and close
        iwriter.close();
        directory.close();
        System.out.println("\n*** FINISHED INDEXING ***");
        bufread.close();

        //System.out.println(documents);
    }

    // For checking if tag changes
    public boolean newTag(String line) {
        return idTag(line) || titleTag(line) || authorTag(line) || bibTag(line) || bodyTag(line);
    }

    // For cranfield interpretation

    public String tagName(String tag) {
        if (idTag(tag)) {
            return "ID";
        } 
        else if (titleTag(tag)) {
            return "Title";
        }
        else if (authorTag(tag)) {
            return "Author";
        }
        else if (bibTag(tag)) {
            return "Bibliography";
        }
        else {
            return "Body";
        }
    }

    // Read first line for checking if it a tag

    public boolean idTag(String line) {
        return line.startsWith(".I");
    }

    public boolean titleTag(String line) {
        return line.startsWith(".T");
    }

    public boolean authorTag(String line) {
        return line.startsWith(".A");
    }

    public boolean bibTag(String line) {
        return line.startsWith(".B");
    }

    public boolean bodyTag(String line) {
        return line.startsWith(".W");
    }
}
