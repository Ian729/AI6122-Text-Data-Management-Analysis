/**
 * @author Group 16
 * This code is provided solely as sample code for using Lucene.
 * 
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.search.ScoreDoc;

public class LuceneTester {

	/** Define the paths for the data file and the lucene index */
	public static final String DATA_FILE="/Users/lingdean/Documents/NTUCourses/AI6122Text/GroupProject/yelp_dataset/yelp_academic_dataset_review.json";
	public static final String INDEX_PATH="/Users/lingdean/Documents/NTUCourses/AI6122Text/GroupProject/luceneIndex";
	
	
	
	public static String Analysis(String query) throws Exception {
		Analyzer analyzer = new StandardAnalyzer();
	    TokenStream token_string = analyzer.tokenStream(null, query);
	    
	    TokenStream lower_string = new LowerCaseFilter(token_string);
	   // TokenStream stem_string = new KStemFilter(lower_string);
	    TokenStream stop_string = new StopFilter(lower_string, EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
	   
	    
	    CharTermAttribute attri = stop_string.addAttribute(CharTermAttribute.class);
	    stop_string.reset();

	    List<String> result_token = new ArrayList<>();
	    while (stop_string.incrementToken()) {
	        result_token.add(attri.toString());
	    }
	    analyzer.close();
	    stop_string.close();   
	    String list_string = String.join(" ", result_token);
	    
	    return list_string;
	}
	
	public static void main (String[] arg) throws Exception{
	
		Scanner s = new Scanner(System.in);
		String user_input;
		System.out.println("Please enter your search content: ");
		user_input = s.nextLine();
		
		int user_result =0;
		boolean input_check;
		System.out.println("Please enter the number of results returned: ");
		do {
			input_check = true;
			try {
				user_result = s.nextInt();
				if (user_result <= 0) {
					System.out.println("You must enter a positive number! Please re-enter: ");
				}
			} catch (Exception e) {
				System.out.println("Input must be a positive integer! Please re-enter:");
				input_check = false;
				s.nextLine();
			}
		} while (input_check == false || user_result <= 0);
		
		
		boolean preformIndex=false;
		
		// To perform indexing. If there is no change to the data file, index only need to be created once 

		if(preformIndex){
			QAIndexer indexer = new QAIndexer(LuceneTester.INDEX_PATH);
			indexer.indexQAs(LuceneTester.DATA_FILE);
		}
		
				
		//search index
		QASearcher searcher=new QASearcher(LuceneTester.INDEX_PATH);
		
		String input_analysis = user_input.toLowerCase();
		if (!user_input.contains("and") && !user_input.contains("or") && !user_input.contains("not")) {
			input_analysis = Analysis(user_input);
		}
		
		
		//search for keywords "iphone" in field "question", and request for the top 20 results
		long query_start = System.currentTimeMillis();
		ScoreDoc[] hits=searcher.search("review", input_analysis, user_result);
		long query_finish = System.currentTimeMillis();
		long time = query_finish - query_start;
		System.out.println("The time to process a query in text field is " + time + " Millisecond(" + time/1000 + " Second)");
		
		System.out.println("\n=================Results for review text search=============\n");
		searcher.printResult(hits);
		
		//search for keywords in "answer" field
		hits=searcher.search("date",user_input, user_result);
		System.out.println("\n=================Results for review date search=============\n");
		searcher.printResult(hits);
		
		hits=searcher.search("business_id",user_input, user_result);
		System.out.println("\n=================Results for review business_id search=============\n");
		searcher.printResult(hits);
		s.close();
	}
	
}