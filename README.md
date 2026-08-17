# FastML

FastML is the classical Machine Learning library of the FastJava ecosystem.

It provides a common place for ML algorithms, pattern recognition, feature extraction, training, prediction, and model storage.

FastML is designed for small, efficient, deterministic ML workloads such as handwriting recognition, pattern matching, classification, regression, clustering, and custom learning algorithms.

---

## Purpose

FastML is not an LLM runtime.
It is the library for:
* classical ML algorithms
* pattern recognition
* trainable pattern models
* feature extraction
* classification
* regression
* clustering
* online and incremental learning
* model persistence
* custom ML algorithms

The goal is to build a growing collection of ML algorithms that can be reused throughout the FastJava ecosystem.

---

## Example

A handwriting recognizer can represent a character as a square raster:
```text
0 0 1 1 1 0 0
0 1 0 0 0 1 0
1 0 0 0 0 0 1
1 1 1 1 1 1 1
1 0 0 0 0 0 1
1 0 0 0 0 0 1
```

FastML can store these patterns, learn from multiple samples, and later identify an unknown pattern.
```java
  model.train(pattern, 'A');
  char result = model.predict(input);
```
---

## Pattern Types

| Pattern           | Description                                 |
| ----------------- | ------------------------------------------- |
| Raster Pattern    | Fixed-size grid such as 8×8, 16×16 or 32×32 |
| Binary Pattern    | 0/1 representation                          |
| Grayscale Pattern | Pixel values such as 0–255                  |
| Vector Pattern    | Numeric feature vector                      |
| Stroke Pattern    | Lines, curves and directions                |
| Shape Pattern     | Contours, geometry and bounding boxes       |
| Temporal Pattern  | Time-based sequences and gestures           |
| Frequency Pattern | Frequency-domain representations            |
| Histogram Pattern | Distribution-based features                 |
| Custom Pattern    | Application-specific representations        |

---

## Algorithms

### Classification
* k-Nearest Neighbors
* Support Vector Machine
* Decision Tree
* Random Forest
* Logistic Regression
* Naive Bayes
* Template Matching
* Hamming Distance Classification
* Manhattan Distance Classification
* Euclidean Distance Classification
* Custom Pattern Classifiers

### Regression
* Linear Regression
* Polynomial Regression
* Ridge Regression
* Lasso Regression

### Clustering
* K-Means
* DBSCAN
* Hierarchical Clustering

### Dimensionality Reduction
* PCA
* LDA

### Learning
* Batch Learning
* Online Learning
* Incremental Learning
* Retraining
* Custom Learning Algorithms

---

## Feature Extraction

FastML can provide reusable feature extractors such as:
* Pixel Count
* Pixel Histogram
* Horizontal Lines
* Vertical Lines
* Diagonal Lines
* Stroke Direction
* Bounding Box
* Center of Mass
* Symmetry
* Edge Features
* Gradient Features
* Fourier Features
* Wavelet Features
* Custom Features

---

## Models

FastML models can contain:
* trained patterns
* feature vectors
* classifier parameters
* regression parameters
* cluster information
* feature pipelines
* model metadata
* model versions

Models should be lightweight and optimized for fast loading and inference.

---

## Architecture

```text
&#x20;                   FastAI
&#x20;                      │
&#x20;                      ▼
&#x20;                FastAIService
&#x20;                 /           \\
&#x20;                ▼             ▼
&#x20;          FastModel         FastML
&#x20;             │                │
&#x20;            LLMs        Classical ML
&#x20;                              │
&#x20;                ┌─────────────┼─────────────┐
&#x20;                ▼             ▼             ▼
&#x20;             Patterns      Algorithms     Models
&#x20;                │             │             │
&#x20;                └─────────────┼─────────────┘
&#x20;                              ▼
&#x20;                        FastML Runtime
```

---

## FastJava Integration

FastML is designed to work with other FastJava modules:
| Module        | Purpose                          |
| ------------- | -------------------------------- |
| FastAI        | High-level AI API                |
| FastAIService | AI task routing                  |
| FastModel     | LLM/model runtime                |
| FastGPU       | GPU acceleration                 |
| FastMath      | Mathematical and SIMD operations |
| FastIO        | Efficient data access            |
| FastBytes     | Compact binary data processing   |

FastML should remain independent from LLM-specific functionality.

---

## Example Structure

```text
FastML/
├── src/
│   └── main/
│       └── java/
│           └── fastml/
│               ├── FastML.java
│               ├── FastMLModel.java
│               ├── FastMLTrainer.java
│               ├── FastMLPredictor.java
│               ├── FastMLStorage.java
│               │
│               ├── patterns/
│               │   ├── RasterPattern.java
│               │   ├── BinaryPattern.java
│               │   ├── VectorPattern.java
│               │   └── StrokePattern.java
│               │
│               ├── algorithms/
│               │   ├── knn/
│               │   ├── svm/
│               │   ├── tree/
│               │   ├── randomforest/
│               │   ├── clustering/
│               │   └── regression/
│               │
│               ├── features/
│               │   ├── HistogramFeatures.java
│               │   ├── GradientFeatures.java
│               │   └── ShapeFeatures.java
│               │
│               └── learning/
│                   ├── OnlineLearner.java
│                   └── IncrementalLearner.java
│
└── native/
   ├── fastml\_runtime.cpp
   └── fastml\_simd.cpp
```

---

## Design Goals

* Java-first
* Low memory usage
* Fast inference
* Minimal allocations
* Reusable algorithms
* Small model files
* Deterministic execution
* Incremental learning
* Extensible architecture
* Optional native acceleration

---

## Scope

FastML is intended to become the central ML toolbox of FastJava.

New algorithms, pattern representations, feature extractors and learning techniques can be added over time without changing the overall architecture.

The library can contain both established ML algorithms and custom algorithms developed specifically for FastJava.

---

## Summary

\*\*FastML is the Machine Learning toolbox of FastJava.\*\*

It stores, learns and recognizes patterns and provides reusable algorithms for classical Machine Learning.

\*\*FastModel\*\* handles LLM-oriented models.

\*\*FastML\*\* handles classical Machine Learning.

\*\*FastAI\*\* provides the high-level AI layer.

\*\*FastGPU\*\* and \*\*FastMath\*\* provide optional acceleration.



