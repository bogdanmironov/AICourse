import random
from collections import defaultdict
from math import log


class NaiveBayesClassifier:
    def __init__(self):
        self.class_prob = {}
        self.feat_prob = defaultdict(lambda: defaultdict(lambda: defaultdict(float)))
        self.class_set = set()

    def train(self, data):
        class_counts = defaultdict(int)

        for row in data:
            cls = row[0]
            class_counts[cls] += 1
            self.class_set.add(cls)

            for i in range(1, len(row)):
                val = row[i]
                self.feat_prob[cls][i][val] += 1

        total = len(data)

        for cls, count in class_counts.items():
            self.class_prob[cls] = count / total

        for cls in self.feat_prob:
            for feat in self.feat_prob[cls]:
                total_count = sum(self.feat_prob[cls][feat].values())
                for val in self.feat_prob[cls][feat]:
                    self.feat_prob[cls][feat][val] /= total_count

    def predict(self, row):
        best_prob = float("-inf")
        best_class = None

        for cls in self.class_set:
            prob = log(self.class_prob[cls])

            for i in range(1, len(row)):
                feature_value = row[i]
                prob += log(self.feat_prob[cls][i][feature_value])

            if prob > best_prob:
                best_prob = prob
                best_class = cls

        return best_class

    def check_accuracy(self, data):
        correct = 0
        for row in data:
            if row[0] == self.predict(row):
                correct += 1
        return correct / len(data)

    @staticmethod
    def cross_validate(data, k=10):
        random.shuffle(data)
        fold_size = len(data) // k
        accuracies = []

        for i in range(k):
            test_data = data[i * fold_size:(i + 1) * fold_size]
            train_data = data[:i * fold_size] + data[(i + 1) * fold_size:]

            classifier = NaiveBayesClassifier()
            classifier.train(train_data)
            accuracies.append(classifier.check_accuracy(test_data))

        return accuracies


if __name__ == "__main__":
    file_path = "house-votes-84.data"
    data = []
    with open(file_path, "r") as file:
        for line in file:
            row = line.strip().split(",")
            row = ["n" if value == "?" else value for value in row]
            data.append(row)

    random.shuffle(data)
    split_point = int(0.8 * len(data))
    train_data = data[:split_point]
    test_data = data[split_point:]

    classifier = NaiveBayesClassifier()
    classifier.train(train_data)

    training_accuracy = classifier.check_accuracy(train_data)
    test_accuracy = classifier.check_accuracy(test_data)
    print(f"Training Accuracy: {training_accuracy * 100:.2f}%")
    print(f"Test Accuracy: {test_accuracy * 100:.2f}%")

    cross_validation_accuracies = NaiveBayesClassifier.cross_validate(data)
    print("Cross-Validation Accuracies:")
    for i, acc in enumerate(cross_validation_accuracies, start=1):
        print(f"Fold {i}: {acc * 100:.2f}%")
    print(f"Average Cross-Validation Accuracy: {sum(cross_validation_accuracies) / len(cross_validation_accuracies) * 100:.2f}%")

