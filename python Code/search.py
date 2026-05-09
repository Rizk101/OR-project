import pandas as pd
import json
import re
from collections import Counter
from nltk.corpus import stopwords
from nltk.tokenize import word_tokenize
from nltk.stem import WordNetLemmatizer

# 1. Function to clean the user's search query
def preprocess_query(query):
    query = str(query).lower()
    query = re.sub(r'[^a-z\s]', '', query)
    tokens = word_tokenize(query)
    stop_words = set(stopwords.words('english'))
    tokens = [word for word in tokens if word not in stop_words]
    lemmatizer = WordNetLemmatizer()
    tokens = [lemmatizer.lemmatize(word) for word in tokens]
    return tokens

print("Loading search engine data...")
# 2. Load the inverted index we built earlier
with open('inverted_index.json', 'r') as f:
    inverted_index = json.load(f)

# 3. Load the movies data just to get the titles for displaying results
df = pd.read_csv('cleaned_movies_data.csv')
movies_dict = dict(zip(df['id'], df['title']))

# 4. The main search function
def search(query, top_k=5):
    query_tokens = preprocess_query(query)
    
    if not query_tokens:
        print("Please enter a valid query.")
        return
    
    matched_movie_ids = []
    # Find all movies that contain any of the query words
    for token in query_tokens:
        if token in inverted_index:
            matched_movie_ids.extend(inverted_index[token])
    
    if not matched_movie_ids:
        print("No movies found matching your query.")
        return
    
    # Count matches to rank movies (Movies with more matching words rank higher)
    movie_scores = Counter(matched_movie_ids)
    top_movies = movie_scores.most_common(top_k)
    
    # Display the results
    print(f"\nTop {top_k} Results for: '{query}'")
    print("-" * 45)
    for rank, (movie_id, score) in enumerate(top_movies, 1):
        title = movies_dict.get(movie_id, "Unknown Title")
        print(f"{rank}. {title} (Matches: {score})")

# 5. Terminal Interface for testing
print("\n" + "="*45)
print("   Welcome to the Movie Search Engine!   ")
print("="*45)

while True:
    user_query = input("\nEnter your search query (or type 'exit' to quit): ")
    if user_query.lower() == 'exit':
        print("Goodbye!")
        break
    search(user_query)