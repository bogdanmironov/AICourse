## AI Coursework — Implemented Algorithms

This repository contains a collection of implementations from the FMI Artificial Intelligence course.  
The following eight practical assignments cover core AI paradigms, including search, probabilistic models, decision trees, clustering, and neural networks (implemented manually from scratch).

### 1. NxN Puzzle Solver (Homework 1)
Algorithms & Summary:
- Implements **IDA\*** (Iterative Deepening A\*) search.
- Solves the NxN sliding tile puzzle optimally using heuristic search.
- Demonstrates classical AI search, admissible heuristics, and memory-efficient tree exploration.

---

### 2. N-Queens with Min-Conflicts (Homework 2)
Algorithms & Summary:
- Uses the **Min-Conflicts** heuristic—a local search algorithm for CSPs.
- Efficiently solves the N-Queens constraint satisfaction problem.

---

### 3. Genetic Algorithm for TSP (Homework 3)
Algorithms & Summary:
- Full **Genetic Algorithm** solver for the Traveling Salesman Problem.
- Includes crossover, mutation, selection, and fitness evaluation.
- Reads coordinate datasets for cities (`uk12_name.csv`, `uk12_xy.csv`).
---

### 4. Tic-Tac-Toe Decision Algorithm (Homework 4)
Algorithms & Summary:
- Implements a decision-based AI player for Tic-Tac-Toe.
- Demonstrates adversarial reasoning and basic game AI foundations.

---

### 5. Naive Bayes Classifier (Homework 5)
Summary:
- Complete implementation of a **multinomial Naive Bayes** classifier.
- Includes Laplace smoothing, log-probability inference, and k-fold cross-validation.
- Trained on the classic *House Votes 84* dataset.

---

### 6. Decision Tree with Pre- & Post-Pruning (Homework 6)
Summary:
- Manually implemented **decision tree classifier** with:
  - Information gain (entropy-based)
  - Max-depth / min-samples pre-pruning
  - Reduced-error post-pruning
  - Full 10-fold cross-validation
- Trained on the UCI *Breast Cancer* dataset.

---

### 7. K-Means and K-Means++ (Homework 7)
Summary:
- Implements **K-Means clustering** with:
  - Random initialization  
  - **K-Means++** smart seeding  
  - WCSS evaluation  
  - Silhouette score computation  
- Supports both "balanced" and "unbalanced" datasets.

---

### 8. Fully-Handwritten Neural Network (Homework 8)
**Manually implemented — no frameworks used.**

Summary:
- Complete implementation of a feedforward neural network, including:
  - Arbitrary number of hidden layers
  - Sigmoid or tanh activation
  - Manual forward propagation
  - Full **backpropagation** with derivative computations
  - Training on boolean logic functions (AND, OR, XOR)
---


## These assignments collectively demonstrate:
- Search algorithms  
- Heuristic optimization  
- Probabilistic models  
- Decision trees  
- Unsupervised clustering  
- Neural networks implemented from scratch  
