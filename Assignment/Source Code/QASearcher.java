

import java.io.IOException;

/**
 * @author Group 16
 * This code is provided solely as sample code for using Lucene.
 * 
 */



import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
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
		Query phrasequery = builder.createPhraseQuery(field, keywords);
							
		ScoreDoc[] hits = null;
		try {
			//Create a TopScoreDocCollector 
			TopScoreDocCollector collector = TopScoreDocCollector.create(numHits,300);
			if ((!keywords.contains("and") && !keywords.contains("or") && !keywords.contains("not")) && keywords.contains(" ")) {
				lSearcher.search(phrasequery, collector);
			}
			else {
				lSearcher.search(query, collector);
			}
			//collect results
			hits = collector.topDocs().scoreDocs;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hits;
	}
	
	
	public ScoreDoc[] sortResult(ScoreDoc[] hits, String field) throws NumberFormatException, IOException {
		int n = hits.length;
		int a;
		int b;
        for (int i = 0; i < n-1; i++) {
            for (int j = 0; j < n-i-1; j++) {
            	a = Integer.parseInt(this.lReader.document(hits[j].doc).get(field));
        		b = Integer.parseInt(this.lReader.document(hits[j+1].doc).get(field));
        		
                if (a< b) {
                    ScoreDoc temp = hits[j];
                    hits[j] = hits[j+1];
                    hits[j+1] = temp;
                }
            }
        }
        return hits;
	}

	//present the search results
	public void printResult(ScoreDoc[] hits) throws Exception {
		
		Scanner s = new Scanner(System.in);
		String [] field_arr = new String []{"stars","useful", "funny","cool"};
		int user_sort =0;
		boolean input_check;
		System.out.println("Would you like the results be sorted by? 0:Lucene Score, 1:stars, 2:useful, 3:funny, 4:cool. ");
		System.out.println("Please press 0 or 1 or 2 or 3 or 4: ");
		do {
			input_check = true;
			try {
				user_sort = s.nextInt();
				if (user_sort < 0 || user_sort > 4) {
					System.out.println("You must enter 0 or 1 or 2 or 3 or 4! Please re-enter: ");
					input_check=false;
				}
			} catch (Exception e) {
				System.out.println("Input must be a positive integer! Please re-enter:");
				input_check = false;
				s.nextLine();
			}
		} while (input_check == false);
		
		if (user_sort>=1 && user_sort <=4) {
			hits = this.sortResult(hits, field_arr[user_sort-1]);
		}
		
		int i = 1;
		for (ScoreDoc hit : hits) {
			System.out.println("\nResult " + i + "\tDocID: " + hit.doc + "\t Score: " + hit.score);
			try {
				System.out.println("Review Text: "+lReader.document(hit.doc).get("review"));
				System.out.print("Review Date: "+ lReader.document(hit.doc).get("date")+ "   ");
				System.out.print("Review Business_id: "+lReader.document(hit.doc).get("business_id")+"\n");
				System.out.println("Stars: "+lReader.document(hit.doc).get("stars")+"; Useful: " + lReader.document(hit.doc).get("useful")
						             +"; Funny: "+lReader.document(hit.doc).get("funny") + "; Cool: "+lReader.document(hit.doc).get("cool"));
			} catch (Exception e) {
				e.printStackTrace();
			}

//			if(i==1) {
//				Terms terms=getTermVector(hit.doc, "review");
//				System.out.println("doc: "+hit.doc);
//				
//				TermsEnum iterator = terms.iterator();
//				BytesRef term = null;
//				System.out.print("List of Terms: ");
//				while ((term = iterator.next()) != null) {
//					String termText = term.utf8ToString();
//     			    long termFreq = iterator.totalTermFreq(); // term freq in doc with docID 
//     			    System.out.print(termText+":"+termFreq+"  ");			    
//				}
//				System.out.println();
//			}
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