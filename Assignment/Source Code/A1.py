import random, json, nltk
from nltk.tokenize import word_tokenize
from collections import Counter
import matplotlib.pyplot as plt

from nltk.stem import PorterStemmer
from nltk.stem import LancasterStemmer
from nltk.stem.snowball import SnowballStemmer

from nltk.corpus import stopwords

stop_words = stopwords.words('english')

# First randomly choose a business b1
# Then form a dataset B1 that contains only the reviews for b1
def r_dataset(filename):
    with open(filename,'r') as f:
        lines = f.readlines()
    n_lines = len(lines)
    print(f"> Success! There are in total:{n_lines} lines")
    random_choice = random.randint(0, n_lines-1)
    # random_business = json.loads(lines[random_choice])['business_id']
    random_business = "PEnMU_He_qHoCfdoAKmjDQ" # hardcoding business
    print(f"> Success! The randomly chosen business_id is {random_business}")
    all_data = [json.loads(line) for line in lines]
    selected_business_reviews = \
    [one_data['text'] for one_data in all_data if one_data['business_id'] == random_business]
    print(len(selected_business_reviews))
    return selected_business_reviews

def break_words(reviews):
    words = []
    for s in reviews:
        words += [word for word in word_tokenize(s) if word not in stop_words and word.isalnum()]
    return words

def generate_plot_data(words):
    word_freq_d = dict(Counter(words))
    inv_dict = {}
    for k,v in word_freq_d.items():
        inv_dict[v] = inv_dict.get(v, []) + [k]
    kv_pairs = sorted([(k,v) for k,v in inv_dict.items()])
    print(kv_pairs[-10:])
    xy_pairs = sorted([(k,len(v)) for k,v in inv_dict.items()])
    x,y = zip(*xy_pairs)
    return x,y


def porter(words):
    ps = PorterStemmer()
    return [ps.stem(word) for word in words]

def lancaster(words):
    lc = LancasterStemmer()
    return [lc.stem(word) for word in words]

def snowball(words):
    ss = SnowballStemmer('english')
    return [ss.stem(word) for word in words]

if __name__ == "__main__":

    # read data
    reviews = r_dataset('yelp_academic_dataset_review.json')

    # generate words and stemmed words
    words = break_words(reviews)

    # generate plot data 
    x,y = generate_plot_data(words)
    ps_x,ps_y = generate_plot_data(porter(words))
    lc_x,lc_y = generate_plot_data(lancaster(words))
    ss_x,ss_y = generate_plot_data(snowball(words))

    # plot
    fig, ax = plt.subplots(4,2)
    ax[0,0].set_title('Word Frequency Distributions')
    ax[0,0].set_ylabel('# of occurrence')
    ax[1,0].set_ylabel('# of occurrence')
    ax[2,0].set_ylabel('# of occurrence')
    ax[3,0].set_ylabel('# of occurrence')
    ax[3,0].set_xlabel('Number of unique words')

    ax[0,0].plot(x,y, color="r", label="Before Stemming")
    ax[1,0].plot(ps_x,ps_y, color="g", label="PorterStemmer")
    ax[2,0].plot(lc_x,lc_y, color="b", label="LancasterStemmer")
    ax[3,0].plot(ss_x,ss_y, color="k", label="SnowballStemmer")

    ax[0,1].set_title('Word Frequency log-log Distributions')
    ax[0,1].set_ylabel('# of occurrence')
    ax[1,1].set_ylabel('# of occurrence')
    ax[2,1].set_ylabel('# of occurrence')
    ax[3,1].set_ylabel('# of occurrence')
    ax[3,1].set_xlabel('Number of unique words')

    ax[0,1].loglog(x,y, color="r", label="Before Stemming")
    ax[1,1].loglog(ps_x,ps_y, color="g", label="PorterStemmer")
    ax[2,1].loglog(lc_x,lc_y, color="b", label="LancasterStemmer")
    ax[3,1].loglog(ss_x,ss_y, color="k", label="SnowballStemmer")


    ax[0,0].legend()
    ax[1,0].legend()
    ax[2,0].legend()
    ax[3,0].legend()
    ax[0,1].legend()
    ax[1,1].legend()
    ax[2,1].legend()
    ax[3,1].legend()
    plt.savefig("result.png")
    plt.show()