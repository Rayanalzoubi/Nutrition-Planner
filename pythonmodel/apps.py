from flask import Flask, request, jsonify
import joblib, numpy as np
import pandas as pd  # pandas

app = Flask(__name__)
enc_cat = joblib.load('encoder_cat.pkl')
le_dict = joblib.load('encoder_labels.pkl')
model   = joblib.load('xgb_meal_model.pkl')

@app.route('/recommend', methods=['POST'])
def recommend():
    data = request.get_json(force=True) or {}

    # 1) Flags
    has_nuts         = bool(data.get('nuts'))
    has_dairy        = bool(data.get('dairy'))
    has_gluten       = bool(data.get('gluten'))
    has_heart        = bool(data.get('heart'))
    has_diabetes     = bool(data.get('diabetes'))
    has_hypertension = bool(data.get('hypertension'))

    # 2) Diet flags → preference string
    is_veg   = bool(data.get('vegetarian'))
    is_omni  = bool(data.get('omnivore'))
    is_pesca = bool(data.get('pescatarian'))
    if is_veg:   diet_pref = 'vegetarian'
    elif is_omni: diet_pref = 'omnivore'
    elif is_pesca: diet_pref = 'pescatarian'
    else:         diet_pref = 'none'

    # 3) Build raw feature list (insert diet_pref as categorical 3rd)
    raw = [
      data.get('gender'),
      data.get('activity_level'),
      diet_pref,
      data.get('age'),
      data.get('height'),
      data.get('weight'),
      data.get('daily_calorie_target'),
      data.get('nuts'),
      data.get('dairy'),
      data.get('gluten'),
      data.get('heart'),
      data.get('diabetes'),
      data.get('hypertension')
    ]

    # 4) Split into categorical vs numeric
    cats = np.array(raw[:3], dtype=object).reshape(1, -1)
    nums = np.array(raw[3:], dtype=float).reshape(1, -1)

    # 5) Тransform categoricals via DataFrame to preserve feature names
    df_cats = pd.DataFrame(cats, columns=enc_cat.feature_names_in_)
    onehot  = enc_cat.transform(df_cats)

    # 6) دمج الأرقام مع one-hot
    X_in = np.hstack([nums, onehot])

    # 7) توقع الاحتمالات
    probas = model.predict_proba(X_in)

    # تجنب الكلمات التي تدل على انواع الحساسيه و الامراض
    ALLERGENS = {
      'nuts':   ['nut','almond','peanut','cashew','walnut'],
      'dairy':  ['milk','cheese','yogurt','cream','butter'],
      'gluten': ['bread','pasta','flour','wheat','cracker']
    }
    HEALTH = {
      'heart':        ['fried','bacon','sausage','butter','cream','fatty'],
      'diabetes':     ['sugar','syrup','honey','dessert','cake','cookie'],
      'hypertension': ['salt','salty','pickles','soy sauce','cured','brine']
    }
    def violates(text):
        low = text.lower()
        if has_nuts    and any(k in low for k in ALLERGENS['nuts']):   return True
        if has_dairy   and any(k in low for k in ALLERGENS['dairy']):  return True
        if has_gluten  and any(k in low for k in ALLERGENS['gluten']): return True
        if has_heart        and any(k in low for k in HEALTH['heart']):        return True
        if has_diabetes     and any(k in low for k in HEALTH['diabetes']):     return True
        if has_hypertension and any(k in low for k in HEALTH['hypertension']): return True
        return False

    np.random.seed()
    seen = set()
    days = []
    while len(days) < 7:
        pick = {}
        for i, meal in enumerate([
            'Breakfast Suggestion','Lunch Suggestion',
            'Dinner Suggestion','Snack Suggestion'
        ]):
            p      = probas[i][0]
            classes= le_dict[meal].classes_
            choice = np.random.choice(len(classes), p=p)
            pick[meal] = classes[choice]
        if any(violates(m) for m in pick.values()):
            continue
        key = tuple(pick[m] for m in [
            'Breakfast Suggestion','Lunch Suggestion',
            'Dinner Suggestion','Snack Suggestion'
        ])
        if key in seen:
            continue
        seen.add(key)
        days.append({
            'day':       len(days)+1,
            'breakfast': pick['Breakfast Suggestion'],
            'lunch':     pick['Lunch Suggestion'],
            'dinner':    pick['Dinner Suggestion'],
            'snack':     pick['Snack Suggestion']
        })

    return jsonify({'days': days})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
