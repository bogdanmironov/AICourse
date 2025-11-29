from ucimlrepo import fetch_ucirepo
import numpy as np
from math import log2


def encode_features(df):
    for column in df.columns:
        df[column] = df[column].astype("category").cat.codes
    return df


def train_test_split(x, y):
    classes = np.unique(y)
    train_idx = []
    test_idx = []
    for cls in classes:
        idx = np.where(y == cls)[0]
        np.random.shuffle(idx)
        split = int(len(idx) * 0.8)
        train_idx.extend(idx[:split])
        test_idx.extend(idx[split:])
    return x.iloc[train_idx], x.iloc[test_idx], y.iloc[train_idx], y.iloc[test_idx]


def entropy(y):
    if len(y) == 0:
        return 0
    proportions = np.bincount(y) / len(y)
    return -np.sum([p * log2(p) for p in proportions if p > 0])


def information_gain(x_col, y):
    parent_entropy = entropy(y)
    values, counts = np.unique(x_col, return_counts=True)
    weighted_entropy = np.sum([
        (counts[i] / len(y)) * entropy(y[x_col == values[i]])
        for i in range(len(values))
    ])
    return parent_entropy - weighted_entropy


class Node:
    def __init__(self, feature=None, left=None, children=None, value=None):
        self.feature = feature
        self.left = left
        self.children = children
        self.value = value


def build_tree(x, y, depth=0, max_depth=None, min_samples_split=1, min_gain=0.0):
    if len(set(y)) == 1 or len(y) < min_samples_split or (max_depth and depth >= max_depth):
        return Node(value=np.bincount(y).argmax())

    best_gain = -1
    best_feature = None
    for feature in range(x.shape[1]):
        gain = information_gain(x.iloc[:, feature], y)
        if gain > best_gain:
            best_gain = gain
            best_feature = feature

    if best_gain < min_gain:
        return Node(value=np.bincount(y).argmax())

    unique_values = np.unique(x.iloc[:, best_feature])
    if len(unique_values) <= 1:
        return Node(value=np.bincount(y).argmax())

    children = {}
    for value in unique_values:
        subset_indices = x.iloc[:, best_feature] == value
        if subset_indices.sum() == 0:
            children[value] = Node(value=np.bincount(y).argmax())
        else:
            child = build_tree(
                x[subset_indices],
                y[subset_indices],
                depth + 1,
                max_depth,
                min_samples_split,
                min_gain,
            )
            children[value] = child

    return Node(feature=best_feature, children=children)


def predict(tree, x):
    if tree.value is not None:
        return tree.value

    feature_value = x.iloc[tree.feature]

    if feature_value in tree.children:
        return predict(tree.children[feature_value], x)
    else:
        return tree.value


def predict_batch(tree, x):
    return np.array([predict(tree, row) for _, row in x.iterrows()])


def evaluate_accuracy(tree, x, y):
    y_pred = predict_batch(tree, x)
    return np.mean(y == y_pred) * 100


def reduced_error_pruning(tree, x_val, y_val):
    if tree.children is None or len(tree.children) == 0:
        return tree

    for value, child in tree.children.items():
        tree.children[value] = reduced_error_pruning(
            child,
            x_val[x_val.iloc[:, tree.feature] == value],
            y_val[x_val.iloc[:, tree.feature] == value]
        )

    current_accuracy = evaluate_accuracy(tree, x_val, y_val)
    majority_class = np.bincount(y_val).argmax()
    pruned_tree = Node(value=majority_class)
    pruned_accuracy = evaluate_accuracy(pruned_tree, x_val, y_val)

    if pruned_accuracy >= current_accuracy:
        return pruned_tree
    else:
        return tree


def ten_fold_split_ids(data_len):
    indices = np.arange(data_len)
    np.random.shuffle(indices)
    folds = np.array_split(indices, 10)
    return folds


def cross_validate(x, y, max_depth=None, min_samples_split=2, min_gain=0.0, use_post_pruning=False):
    folds = ten_fold_split_ids(len(y))
    accuracies = []

    for i in range(10):
        test_idx = folds[i]
        train_idx = np.concatenate(folds[:i] + folds[i + 1:])

        x_train, x_test = x.iloc[train_idx], x.iloc[test_idx]
        y_train, y_test = y.iloc[train_idx], y.iloc[test_idx]

        tree = build_tree(x_train, y_train, max_depth=max_depth, min_samples_split=min_samples_split, min_gain=min_gain)
        if use_post_pruning:
            tree = reduced_error_pruning(tree, x_train, y_train)
        y_pred = predict_batch(tree, x_test)
        accuracy = np.mean(y_test == y_pred)
        print(f"    Accuracy Fold: {accuracy * 100:.2f}%")
        accuracies.append(accuracy)

    return np.mean(accuracies), np.std(accuracies)


pre_pruning_params = {"max_depth": None, "min_samples_split": 1, "min_gain": 0.01}
use_post_pruning = False

user_input = input()

if user_input.startswith("0"):
    if "N" in user_input:
        pre_pruning_params["max_depth"] = 4
    if "K" in user_input:
        pre_pruning_params["min_samples_split"] = 30
    if "G" in user_input:
        pre_pruning_params["min_gain"] = 0.1
elif user_input.startswith("1"):
    use_post_pruning = True
elif user_input.startswith("2"):
    pre_pruning_params["max_depth"] = 4
    pre_pruning_params["min_samples_split"] = 30
    pre_pruning_params["min_gain"] = 0.1
    use_post_pruning = True

# load
dataframe = fetch_ucirepo(id=14).data.original

# prep
dataframe.replace("?", np.nan, inplace=True)

for column in dataframe.columns:
    dataframe.fillna({column: dataframe[column].mode()[0]}, inplace=True)

x = dataframe.drop(columns=["Class"])
y = dataframe["Class"]

x = encode_features(x)
y = y.map({"no-recurrence-events": 0, "recurrence-events": 1})

x_train, x_test, y_train, y_test = train_test_split(x, y)

# train
tree = build_tree(x_train, y_train, max_depth=pre_pruning_params["max_depth"],
                  min_samples_split=pre_pruning_params["min_samples_split"], min_gain=pre_pruning_params["min_gain"])
if use_post_pruning:
    tree = reduced_error_pruning(tree, x_train, y_train)

# test
train_accuracy = evaluate_accuracy(tree, x_train, y_train)
print(f"Train Accuracy: {train_accuracy:.2f}%")
test_accuracy = evaluate_accuracy(tree, x_test, y_test)
print(f"Test Accuracy: {test_accuracy:.2f}%")

# cross-validate
print(f"10-Fold Cross-Validation Results:")
mean_accuracy, std_accuracy = cross_validate(x_train, y_train, max_depth=pre_pruning_params["max_depth"],
                                             min_samples_split=pre_pruning_params["min_samples_split"],
                                             min_gain=pre_pruning_params["min_gain"], use_post_pruning=use_post_pruning)
print(f"Mean Accuracy: {mean_accuracy * 100:.2f}%")
print(f"Standard Deviation: {std_accuracy * 100:.2f}%")
