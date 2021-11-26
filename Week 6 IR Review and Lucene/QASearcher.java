/**
 * @author axsun
 * This code is provided solely as sample code for using Lucene.
 * 
 */

package TestIndex;

import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.QueryBuilder;

public class QASearcher {

	private IndexSearcher lSearcher;
	private IndexReader lReader;

	public QASearcher(String dir) {
		try {
			//create an index reader and index searcher
			lReader = DirectoryReader.open(FSDirectory.open(Paths.get(dir)));
			lSearcher = new IndexSearcher(lReader);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//report the number of documents indexed 
	public int getCollectionSize() {
		return this.lReader.numDocs();
	}

	//search for keywords in specified field, with the number of top results 
	public ScoreDoc[] search(String field, String keywords, int numHits) {
		
		//the query has to be analyzed the same way as the documents being index 
		//using the same Analyzer 
		QueryBuilder builder = new QueryBuilder(new StandardAnalyzer());
		Query query = builder.createBooleanQuery(field, keywords);
		ScoreDoc[] hits = null;
		try {
			//Create a TopScoreDocCollector 
			TopScoreDocCollector collector = TopScoreDocCollector.create(numHits);
			
			//search index
			lSearcher.search(query, collector);
			
			//collect results
			hits = collector.topDocs().scoreDocs;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hits;
	}

	//present the search results
	public void printResult(ScoreDoc[] hits) throws Exception {
		int i = 1;
		for (ScoreDoc hit : hits) {
			System.out.println("\nResult " + i + "\tDocID: " + hit.doc + "\t Score: " + hit.score);
			try {
				System.out.println("Q: " + lReader.document(hit.doc).get("question"));
				System.out.println("A: " + lReader.document(hit.doc).get("answer"));
			} catch (Exception e) {
				e.printStackTrace();
			}

			if(i==1) {
				Terms terms=getTermVector(hit.doc, "answer");
				System.out.println("doc: "+hit.doc);
				
				TermsEnum iterator = terms.iterator();
				BytesRef term = null;
				System.out.print("List of Terms: ");
				while ((term = iterator.next()) != null) {
					String termText = term.utf8ToString();
     			    long termFreq = iterator.totalTermFreq(); // term freq in doc with docID 
     			    System.out.print(termText+":"+termFreq+"\t");			    
				}
				System.out.println();
			}
			i++;

		}
	}

	
	//get term vector 
	public Terms getTermVector (int docID, String field) throws Exception {
		return lReader.getTermVector(docID, field);
	}
	
	public void close() {
		try {
			if (lReader != null) {
				lReader.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
