import joblib
import numpy as np

# 1) Load artifacts
enc_cat = joblib.load('encoder_cat.pkl')
le_dict = joblib.load('encoder_labels.pkl')
model   = joblib.load('xgb_meal_model.pkl')

# 2) Sample profile
data = {
    "gender": 0,
    "activity_level": 1,
    "dietary_preference": 2,
    "age": 25,
    "height": 180,
    "weight": 80,
    "daily_calorie_target": 2000,
    "nuts": 0,
    "dairy": 1,
    "gluten": 0,
    "heart": 0,
    "diabetes": 0,
    "hypertension": 0
}

# 3) Build features
raw = [
    data['gender'],
    data['activity_level'],
    data['dietary_preference'],
    data['age'],
    data['height'],
    data['weight'],
    data['daily_calorie_target'],
    data['nuts'],
    data['dairy'],
    data['gluten'],
    data['heart'],
    data['diabetes'],
    data['hypertension']
]
cats = np.array(raw[:3]).reshape(1, -1)
nums = np.array(raw[3:]).reshape(1, -1)
X_in = np.hstack([nums, enc_cat.transform(cats)])

# 4) Get probas for each meal
probas = model.predict_proba(X_in)

# 5) Sample until you have 7 unique combos
np.random.seed(42)
seen = set()
unique_days = []

while len(unique_days) < 7:
    # sample one day
    picks = {}
    key = []
    for i, meal in enumerate([
        'Breakfast Suggestion',
        'Lunch Suggestion',
        'Dinner Suggestion',
        'Snack Suggestion'
    ]):
        prob = probas[i][0]
        classes = le_dict[meal].classes_
        idx = np.random.choice(len(classes), p=prob)
        pick = classes[idx]
        picks[meal] = pick
        key.append(pick)
    key = tuple(key)

    # if new, add it
    if key not in seen:
        seen.add(key)
        unique_days.append(picks)

# 6) Print all 7 unique days
for day, picks in enumerate(unique_days, start=1):
    print(f"Day {day}:")
    print(f"  Breakfast: {picks['Breakfast Suggestion']}")
    print(f"  Lunch:     {picks['Lunch Suggestion']}")
    print(f"  Dinner:    {picks['Dinner Suggestion']}")
    print(f"  Snack:     {picks['Snack Suggestion']}")
    print()
