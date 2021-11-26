from textblob import TextBlob
import json,random


def r_dataset(filename, business_id):
    with open(filename,'r') as f:
        lines = f.readlines()
    n_lines = len(lines)
    print(f"> Success! There are in total:{n_lines} lines")
    business_id = json.loads(lines[random.randint(0, n_lines-1)])['business_id']
    # random_business = "PEnMU_He_qHoCfdoAKmjDQ" # hardcoding business
    print(f"> Success! The randomly chosen business_id is {business_id}")
    all_data = [json.loads(line) for line in lines]
    selected_business_reviews = \
    [one_data['text'] for one_data in all_data if one_data['business_id'] == business_id]
    print(f"> Success! The application has already processed {len(selected_business_reviews)} lines of comments")
    return selected_business_reviews


def sentiment_analysis(all_data):
	subjective_sentiment = []
	for line in all_data:
		tb = TextBlob(line)
		if tb.sentiment.subjectivity >= 0.6:
			subjective_sentiment.append(tb.sentiment.polarity)
	return sum(subjective_sentiment)

if __name__ == "__main__":
	all_review_data = r_dataset("review.json","PEnMU_He_qHoCfdoAKmjDQ")
	sub = sentiment_analysis(all_review_data)
	print(f"> Subjective sentiment score:{sub}")
