import os
import pandas as pd
import numpy as np
from sklearn.model_selection      import train_test_split
from sklearn.preprocessing       import OneHotEncoder, LabelEncoder
from sklearn.multioutput         import MultiOutputClassifier
from xgboost                     import XGBClassifier
import joblib

# 0. (Sanity check) print working directory and contents
print("CWD:", os.getcwd())
print("Files:", os.listdir(os.getcwd()))

# 1. Load data — use a raw string or forward‑slashes
df = pd.read_excel(r"C:\Users\lenovo\Desktop\pythonn\Cleaned_Data_20.xlsx")

# 2. Define features X and raw targets y_text
feature_cols = [
    'Gender','Activity Level','Dietary Preference',
    'Ages','Height','Weight','Daily Calorie Target',
    'nuts','dairy','gluten',
    'HeartPatient','Diabetes','Hypertension'
]
X = df[feature_cols]

target_cols = [
    'Breakfast Suggestion',
    'Lunch Suggestion',
    'Dinner Suggestion',
    'Snack Suggestion'
]
y_text = df[target_cols]

# 3. One‑hot encode the categorical inputs
cat_cols = ['Gender','Activity Level','Dietary Preference']
enc_cat = OneHotEncoder(sparse_output=False, handle_unknown='ignore')

X_cat = enc_cat.fit_transform(X[cat_cols])
X_num = X.drop(columns=cat_cols).values
X_pre = np.hstack([X_num, X_cat])

# 4. Label‑encode each target column
le_dict = {}
y_encoded = np.empty_like(y_text.values, dtype=int)
for i, col in enumerate(target_cols):
    le = LabelEncoder()
    y_encoded[:, i] = le.fit_transform(y_text[col])
    le_dict[col] = le

# 5. Train/test split
X_train, X_test, y_train, y_test = train_test_split(
    X_pre, y_encoded, test_size=0.2, random_state=42
)

# 6. Build & fit multi‑output XGBoost
base = XGBClassifier(
    objective='multi:softprob',
    use_label_encoder=False,
    eval_metric='mlogloss'
)
model = MultiOutputClassifier(base)
model.fit(X_train, y_train)

# 7. Quick evaluation
print("Train score:", model.score(X_train, y_train))
print(" Test score:", model.score(X_test, y_test))

# 8. Save everything
joblib.dump(enc_cat,   'encoder_cat.pkl')
joblib.dump(le_dict,   'encoder_labels.pkl')
joblib.dump(model,     'xgb_meal_model.pkl')