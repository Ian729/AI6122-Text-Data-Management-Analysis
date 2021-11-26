/**
 * @author axsun
 * This code is provided solely as sample code for using Lucene.
 * 
 */

package TestIndex;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.JSONObject;

public class QAIndexer {

	private IndexWriter writer = null;
	
	//for recording time used for indexing 
    private static final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	public QAIndexer(String dir) throws IOException {
		//specify the directory to store the Lucene index
		Directory indexDir = FSDirectory.open(Paths.get(dir));
		
		//specify the analyzer used in indexing
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig cfg = new IndexWriterConfig(analyzer);
		cfg.setOpenMode(OpenMode.CREATE);
		
		//create the IndexWriter
		writer = new IndexWriter(indexDir, cfg);
	}

	//specify what is a document, and how its fields are indexed
	protected Document getDocument(String question, String answer) throws Exception {
		Document doc = new Document();
		
		FieldType ft = new FieldType(TextField.TYPE_STORED);
		//ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
		
		ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
		ft.setStoreTermVectors(true);
		
		doc.add(new TextField("question", question, Field.Store.YES));
		
//		doc.add(new Field("question", question, ft));
		doc.add(new Field("answer", answer, ft));
		return doc;
	}

	
	public void indexQAs(String fileName) throws Exception {
		
		System.out.println("Start indexing "+fileName+" "+sdf.format(new Date()));
		
		//read a JSON file
		Scanner in = new Scanner(new File(fileName));
		int lineNumber = 1;
		String jLine = "";
		
		while (in.hasNextLine()) {
			try {
				jLine = in.nextLine().trim();
				//parse the JSON file and extract the values for "question" and "answer"
				JSONObject jObj = new JSONObject(jLine);
				String question = jObj.getString("question");
				String answer = jObj.getString("answer");

				//create a document for each JSON record 
				Document doc=getDocument(question, answer);
				
				//index the document
				writer.addDocument(doc);
				
				lineNumber++;
			} catch (Exception e) {
				System.out.println("Error at: " + lineNumber + "\t" + jLine);
				e.printStackTrace();
			}
		}
		//close the file reader
		in.close();
		System.out.println("Index completed at " + sdf.format(new Date()));
		System.out.println("Total number of documents indexed: " + writer.maxDoc());
		
		//close the index writer.
		writer.close();
			
	}

}
