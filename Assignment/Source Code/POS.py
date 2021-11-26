import nltk, random
from A1 import r_dataset,break_words, porter, lancaster, snowball
from yellowbrick.text import PosTagVisualizer

if __name__ == "__main__":
    # read data
    reviews = r_dataset('review.json')

    five_sents = random.choices(reviews, k=5)

    # generate words and stemmed words
    words = break_words(five_sents)
    # no need to stem before tagging
    
    # POS Tag
    pt_words = nltk.pos_tag(words)
    # Words
    viz = PosTagVisualizer()
    viz.fit([[pt_words]])
    viz.show()
    # Stanford Tag

    # Spacy