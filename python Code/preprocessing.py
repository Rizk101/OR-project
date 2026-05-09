import pandas as pd
import nltk
import re
from nltk.corpus import stopwords
from nltk.tokenize import word_tokenize
from nltk.stem import WordNetLemmatizer

# Download required NLTK resources
nltk.download('punkt')
nltk.download('punkt_tab')  # Added to fix the LookupError
nltk.download('stopwords')
nltk.download('wordnet')

def preprocess_text(text):
    # 1. Text Normalization: Lowercase and remove punctuation/numbers
    text = str(text).lower()
    text = re.sub(r'[^a-z\s]', '', text)
    
    # 2. Tokenization: Split text into words
    tokens = word_tokenize(text)
    
    # 3. Stop Word Removal: Remove common words
    stop_words = set(stopwords.words('english'))
    tokens = [word for word in tokens if word not in stop_words]
    
    # 4. Lemmatization: Convert words to their base form
    lemmatizer = WordNetLemmatizer()
    tokens = [lemmatizer.lemmatize(word) for word in tokens]
    
    # Join tokens back into a single clean string
    return ' '.join(tokens)

# Load the dataset
print("Loading dataset...")
df = pd.read_csv('tmdb_5000_movies.csv')

# Select only the necessary columns (id, title, overview)
df = df[['id', 'title', 'overview']]

# Drop movies with missing overviews
df = df.dropna(subset=['overview'])

# Apply preprocessing to the overview column
print("Preprocessing text data (This might take a minute)...")
df['cleaned_overview'] = df['overview'].apply(preprocess_text)

# Save the cleaned dataset to a new CSV file
df.to_csv('cleaned_movies_data.csv', index=False)
print("\nCleaned data saved to 'cleaned_movies_data.csv' successfully!")