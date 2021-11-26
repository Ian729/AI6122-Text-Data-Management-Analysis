/**
 * @author axsun
 * This code is provided solely as sample code for using Lucene.
 * 
 */

package TestIndex;

import org.apache.lucene.search.ScoreDoc;

public class LuceneTester {

	/** Define the paths for the data file and the lucene index */
	public static final String DATA_FILE="C:/Users/axsun/Desktop/LuceneTest/qa_Cell_Phones.json";
	public static final String INDEX_PATH="C:/Users/axsun/Desktop/LuceneTest/luceneIndex";
	
	
	public static void main (String[] arg) throws Exception{
	
		boolean preformIndex=false;
		
		// To perform indexing. If there is no change to the data file, index only need to be created once 

		if(preformIndex){
			QAIndexer indexer = new QAIndexer(LuceneTester.INDEX_PATH);
			indexer.indexQAs(LuceneTester.DATA_FILE);
		}
		
				
		//search index
		QASearcher searcher=new QASearcher(LuceneTester.INDEX_PATH);
		
						
		//search for keywords "iphone" in field "question", and request for the top 20 results
		ScoreDoc[] hits=searcher.search("question", "Samsung", 50);
		System.out.println("\n=================Results for question search=============\n");
		searcher.printResult(hits);
		
		//search for keywords in "answer" field
		hits=searcher.search("answer", "sim", 30);
		System.out.println("\n=================Results for answer search=============\n");
		searcher.printResult(hits);
		
	}
	
}
