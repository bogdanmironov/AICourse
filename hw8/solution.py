import random

import numpy as np


def sigmoid(x: float) -> float:
    return 1 / (1 + np.exp(-x))


def sigmoid_derivative(x: float) -> float:
    return x * (1 - x)


def tanh(x: float) -> float:
    return np.tanh(x)


def tanh_derivative(x: float) -> float:
    return 1 - np.tanh(x) ** 2


class MyNN:
    def __init__(self, *, num_of_hidden_layers: int, hidden_size: int, activation_function: int) -> None:
        self.num_of_layers = num_of_hidden_layers + 2
        self.layer_sizes = [2] + num_of_hidden_layers * [hidden_size] + [1]

        self.weights = [
            [
                [random.uniform(-0.5, 0.5) for end in range(self.layer_sizes[layer_i + 1])]
                for start in range(self.layer_sizes[layer_i])
            ]
            for layer_i in range(self.num_of_layers - 1)
        ]

        self.biases = [
            [random.uniform(-0.5, 0.5) for end in range(self.layer_sizes[layer_i + 1])]
            for layer_i in range(self.num_of_layers - 1)
        ]

        self.activation_function = sigmoid if activation_function == 0 else tanh
        self.activation_derivative = sigmoid_derivative if activation_function == 0 else tanh_derivative

    # Input - Input of the NN
    # Return - All neuron values for NN
    def feedforward(self, inputs: tuple[int, int]) -> list[list[float]]:
        layer_results = [inputs]
        for layer in range(self.num_of_layers - 1):
            results_for_next_layer = [
                self.activation_function(
                    sum(layer_results[-1][start_neuron] * self.weights[layer][start_neuron][end_neuron] for start_neuron
                        in range(self.layer_sizes[layer]))
                    + self.biases[layer][end_neuron]
                )
                for end_neuron in range(self.layer_sizes[layer + 1])
            ]

            layer_results += [results_for_next_layer]

        return layer_results

    def backpropagation(self, inputs: tuple[int, int], expected_output: int, learning_rate: float) -> None:
        layer_results = self.feedforward(inputs)

        last_layer_deviation = expected_output - layer_results[-1][0]
        layer_errors = [[self.activation_derivative(layer_results[-1][0]) * last_layer_deviation]]

        for neuron in range(self.layer_sizes[-2]):
            self.weights[-1][neuron][0] += learning_rate * layer_errors[0][0] * layer_results[-2][neuron]
        self.biases[-1][0] += learning_rate * layer_errors[0][0]

        for layer in range(self.num_of_layers - 2, 0, -1):
            curr_layer_errors = [
                self.activation_derivative(layer_results[layer][curr_neuron]) \
                * sum(
                    self.weights[layer][curr_neuron][right_neuron] * layer_errors[0][right_neuron]
                    for right_neuron in range(self.layer_sizes[layer + 1])
                )
                for curr_neuron in range(self.layer_sizes[layer])
            ]

            layer_errors = [curr_layer_errors] + layer_errors

            for end_neuron in range(self.layer_sizes[layer]):
                for start_neuron in range(self.layer_sizes[layer - 1]):
                    self.weights[layer - 1][start_neuron][end_neuron] += learning_rate * curr_layer_errors[end_neuron] * layer_results[layer - 1][start_neuron]

                self.biases[layer - 1][end_neuron] += learning_rate * curr_layer_errors[end_neuron]

    def train(self, training_data: list[tuple[tuple[int, int], int]], epochs: int, learning_rate: float) -> None:
        for epoch in range(epochs):
            for inputs, expected_output in training_data:
                self.backpropagation(inputs, expected_output, learning_rate)

    def predict(self, inputs: tuple[int, int]) -> float:
        network_matrix = self.feedforward(inputs)
        return network_matrix[-1][0]


def boolean_function(name: str, inputs: tuple[int, int]) -> int:
    if name == "AND":
        return int(inputs[0] and inputs[1])
    elif name == "OR":
        return int(inputs[0] or inputs[1])
    elif name == "XOR":
        return int(inputs[0] != inputs[1])


def test():
    # bool_input = input("bool: ")
    # activ_input = int(input("activ[0, 1]: "))
    # hid_layers_count = int(input("hidden layers: "))
    # hid_neurons_count = int(input("hidden neurons: "))
    #
    # if bool_input == "AND":
    #     funcs = ["AND"]
    # elif bool_input == "OR":
    #     funcs = ["OR"]
    # elif bool_input == "XOR":
    #     funcs = ["XOR"]
    # else:
    #     funcs = ["AND", "OR", "XOR"]

    funcs = ["AND", "OR", "XOR"]
    hid_neurons_count = 10
    hid_layers_count = 1
    activ_input = 1

    for func_name in funcs:
        nn = MyNN(num_of_hidden_layers=hid_layers_count, hidden_size=hid_neurons_count, activation_function=activ_input)

        training_data = [
            ((0, 0), boolean_function(func_name, (0, 0))),
            ((0, 1), boolean_function(func_name, (0, 1))),
            ((1, 0), boolean_function(func_name, (1, 0))),
            ((1, 1), boolean_function(func_name, (1, 1)))
        ]

        nn.train(training_data, epochs=10000, learning_rate=0.05)

        print(f"Results for {func_name}:")
        for inputs, _ in training_data:
            print(f"Input: {inputs}, Output: {nn.predict(inputs)}")


test()
