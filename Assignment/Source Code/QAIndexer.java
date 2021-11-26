
/**
 * @author Group 16
 * This code is provided solely as sample code for using Lucene.
 * 
 */


import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.lucene.analysis.en.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StoredField;
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
	private ArrayList<Long> recorded_time;
	//for recording time used for indexing 
    private static final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	public QAIndexer(String dir) throws IOException {
		this.recorded_time = new ArrayList<Long>();
		
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
	protected Document getDocument(String review, String business_id, int stars, String date, int useful, int funny, int cool) throws Exception {
		Document doc = new Document();
		
		FieldType ft = new FieldType(TextField.TYPE_STORED);
		ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
		ft.setStoreTermVectors(true);
		
		doc.add(new Field("review", review, ft));
		doc.add(new Field("date", date, ft));
		doc.add(new Field("business_id", business_id, ft));
		doc.add (new StoredField ("stars", stars));
		doc.add(new StoredField("useful", useful));
		doc.add(new StoredField("funny", funny));
		doc.add(new StoredField("cool", cool));
		
		return doc;
	}

	
	public void indexQAs(String fileName) throws Exception {
		
		System.out.println("Start indexing "+fileName+" "+sdf.format(new Date()));
		long start_time = new Date().getTime();
		long start = System.currentTimeMillis();
		
		//read a JSON file
		Scanner in = new Scanner(new File(fileName));
		int lineNumber = 1;
		String jLine = "";
		
		while (in.hasNextLine() /* && count <=10000*/) {
			try {
				jLine = in.nextLine().trim();
				//parse the JSON file and extract the values for "question" and "answer"
				JSONObject jObj = new JSONObject(jLine);
				
				String review_id = jObj.getString("review_id");
				String user_id = jObj.getString("user_id");
				String business_id = jObj.getString("business_id");
				int stars = jObj.getInt("stars");
				String date  = jObj.getString("date");
				String review = jObj.getString("text");
				int useful = jObj.getInt("useful");
				int funny =jObj.getInt("funny");
				int cool = jObj.getInt("cool");
				//create a document for each JSON record 
				Document doc=getDocument(review, business_id, stars, date, useful, funny, cool);
				
				//index the document
				writer.addDocument(doc);
				
				if (lineNumber == 863540 || lineNumber == 1727081 || lineNumber==2590621
					|| lineNumber == 3454162 || lineNumber == 4317702 || lineNumber==5181242
					|| lineNumber == 6044783 || lineNumber == 6908323 || lineNumber==7771863) {
					long current = System.currentTimeMillis();
					long time_differ = current - start;
					start = current;
					this.recorded_time.add(time_differ);
				}
				
				lineNumber++;
			} catch (Exception e) {
				System.out.println("Error at: " + lineNumber + "\t" + jLine);
				e.printStackTrace();
			}
		}
		
		this.recorded_time.add(System.currentTimeMillis()-start);
		System.out.println("The total line number is " + lineNumber);
		//close the file reader
		in.close();
		System.out.println("Index completed at " + sdf.format(new Date()));
		System.out.println("Total number of documents indexed: " + writer.getDocStats().maxDoc);
		
		System.out.print("Time needed to index every 10% of the documents: ");
		for (int i=0; i< this.recorded_time.size(); i++) {
			System.out.print(this.recorded_time.get(i)+ " Millisecond("+(this.recorded_time.get(i)/1000)+" Second); \n");
		}
		
		long finish_time = new Date().getTime();
		long index_time = finish_time-start_time;
		System.out.println("Total indexing time: "+ index_time + "Millisecond(" + index_time/100 + " Second)");
		
		//close the index writer.
		writer.close();
			
	}

}