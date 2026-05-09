import pandas as pd
import json
from collections import defaultdict

print("Loading cleaned dataset...")
df = pd.read_csv('cleaned_movies_data.csv')

# Drop any rows that might have become empty after cleaning
df = df.dropna(subset=['cleaned_overview'])

print("Building the Inverted Index...")
# We use defaultdict to automatically create a list for new words
inverted_index = defaultdict(list)

# Iterate over every movie in our cleaned data
for index, row in df.iterrows():
    movie_id = int(row['id']) 
    text = str(row['cleaned_overview'])
    
    # Get unique words in this specific movie's overview
    words = set(text.split())
    
    # Add the movie_id to the list of each word
    for word in words:
        inverted_index[word].append(movie_id)

print(f"Index built successfully with {len(inverted_index)} unique words.")

print("Saving the index to 'inverted_index.json'...")
# Save the dictionary as a JSON file for future retrieval
with open('inverted_index.json', 'w') as f:
    json.dump(inverted_index, f)

print("Done! The dictionary (index) is ready.")