import joblib, numpy as np
from flask import Flask, request, jsonify

app = Flask(__name__)
enc_cat = joblib.load('encoder_cat.pkl')
le_dict = joblib.load('encoder_labels.pkl')
model   = joblib.load('xgb_meal_model.pkl')

@app.route('/recommend', methods=['POST'])
def recommend():
    d = request.json
    raw = [
      d['gender'], d['activity_level'], d['dietary_preference'],
      d['age'], d['height'], d['weight'], d['daily_calorie_target'],
      d['nuts'], d['dairy'], d['gluten'],
      d['heart'], d['diabetes'], d['hypertension']
    ]
    # split & encode
    cats = np.array(raw[:3]).reshape(1,-1)
    nums = np.array(raw[3:]).reshape(1,-1)
    X_in = np.hstack([nums, enc_cat.transform(cats)])
    preds = model.predict(X_in)[0]
    days = []
    for day in range(1,8):
        days.append({
            "day": day,
            "breakfast": le_dict['Breakfast Suggestion'] .inverse_transform([preds[0]])[0],
            "lunch":     le_dict['Lunch Suggestion']     .inverse_transform([preds[1]])[0],
            "dinner":    le_dict['Dinner Suggestion']    .inverse_transform([preds[2]])[0],
            "snack":     le_dict['Snack Suggestion']     .inverse_transform([preds[3]])[0],
        })
    return jsonify({"days": days})
