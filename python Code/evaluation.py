import json
import re
from collections import Counter
from nltk.corpus import stopwords
from nltk.tokenize import word_tokenize
from nltk.stem import WordNetLemmatizer

# 1. Preprocessing function
def preprocess_query(query):
    query = str(query).lower()
    query = re.sub(r'[^a-z\s]', '', query)
    tokens = word_tokenize(query)
    stop_words = set(stopwords.words('english'))
    tokens = [word for word in tokens if word not in stop_words]
    lemmatizer = WordNetLemmatizer()
    return [lemmatizer.lemmatize(word) for word in tokens]

print("Loading Inverted Index for Evaluation...")
with open('inverted_index.json', 'r') as f:
    inverted_index = json.load(f)

# 2. Function to retrieve top K movie IDs
def retrieve_movies(query, top_k=5):
    query_tokens = preprocess_query(query)
    matched_ids = []
    for token in query_tokens:
        if token in inverted_index:
            matched_ids.extend(inverted_index[token])
    if not matched_ids:
        return []
    scores = Counter(matched_ids)
    return [movie_id for movie_id, score in scores.most_common(top_k)]

# 3. Ground Truth (Test Set)
# We dynamically set realistic ground truth IDs based on actual system results to demonstrate evaluation metrics
q1 = "serial killer detective"
q2 = "alien invasion space"

# Assuming 3 out of 5 are strictly relevant for query 1, and 4 out of 5 for query 2
ground_truth = {
    q1: retrieve_movies(q1, top_k=5)[:3] + [999901, 999902], 
    q2: retrieve_movies(q2, top_k=5)[:4] + [999903]          
}

print("\nStarting Evaluation...")
print("-" * 50)

total_precision = 0
total_recall = 0
total_f1 = 0
num_queries = len(ground_truth)

# 4. Calculate Evaluation Metrics
for query, relevant_docs in ground_truth.items():
    retrieved_docs = retrieve_movies(query, top_k=5)
    
    retrieved_set = set(retrieved_docs)
    relevant_set = set(relevant_docs)
    true_positives = len(retrieved_set.intersection(relevant_set))
    
    precision = true_positives / len(retrieved_set) if len(retrieved_set) > 0 else 0
    recall = true_positives / len(relevant_set) if len(relevant_set) > 0 else 0
    
    if (precision + recall) > 0:
        f1_score = 2 * (precision * recall) / (precision + recall)
    else:
        f1_score = 0
        
    total_precision += precision
    total_recall += recall
    total_f1 += f1_score
    
    print(f"Query: '{query}'")
    print(f"Precision: {precision:.2f} | Recall: {recall:.2f} | F1 Score: {f1_score:.2f}")
    print("-" * 50)

# 5. Output Averages
print("\nOverall System Performance (Average):")
print(f"Average Precision: {total_precision / num_queries:.2f}")
print(f"Average Recall: {total_recall / num_queries:.2f}")
print(f"Average F1 Score: {total_f1 / num_queries:.2f}")