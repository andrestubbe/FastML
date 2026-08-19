# FastML 0.1.0 [ALPHA-2026-08] — Classical Machine Learning & Pattern Recognition for Java

[![Status](https://img.shields.io/badge/status-0.1.0-brightgreen.svg)](https://github.com/andrestubbe/FastML/releases/tag/0.1.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Cross--Platform-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io/#andrestubbe/FastML)

---

**⚡ Small, deterministic, low-overhead Machine Learning algorithms, pattern matching, and feature extraction for the FastJava ecosystem.**

**FastML** is the classical Machine Learning toolbox of the **FastJava** ecosystem. While **FastModel** and **FastAI** focus on large language models (LLMs) and neural embeddings, **FastML** delivers lightweight, zero-bloat primitives for pattern recognition, centroid learning, classification, clustering, and structural vision feature extraction.

```java
// Quick Start — Example
import fastml.FastML;
import fastml.algorithm.CentroidClassifier;
import fastml.pattern.Pattern;

public class Demo {
    public static void main(String[] args) {
        CentroidClassifier<Character> model = FastML.centroid();

        // 1. Train raster pattern for letter 'A'
        Pattern patternA = FastML.raster("""
            0 0 1 1 1 0 0
            0 1 0 0 0 1 0
            1 0 0 0 0 0 1
            1 1 1 1 1 1 1
            1 0 0 0 0 0 1
            1 0 0 0 0 0 1
        """);
        model.train('A', patternA);

        // 2. Predict character from input pattern
        char prediction = model.predict(patternA);
        System.out.println("Predicted: " + prediction);
    }
}
```

---

## Table of Contents

- [Why FastML?](#why-fastml)
- [Key Features](#key-features)
- [Performance](#performance)
- [Architecture & FastJava Ecosystem](#architecture--fastjava-ecosystem)
- [API Quick Reference](#api-quick-reference)
- [Installation](#installation)
- [Technical Examples & Hero Demos](#technical-examples--hero-demos)
- [Documentation](#documentation)
- [Platform Support](#platform-support)
- [License](#license)
- [Related Projects](#related-projects)

---

## Why FastML?

Modern AI runtimes frequently force heavy Python dependencies, multi-gigabyte models, and high memory footprints even for trivial deterministic tasks like handwriting detection, gesture recognition, or pattern classification.

**FastML** provides:

- **100% Pure JVM Execution** — Zero external C/Python dependencies, small footprint, fast boot time.
- **Deterministic & Trainable** — Simple mathematical models (Nearest Centroid, Geometric Invariant Features) with predictable output and low latency.
- **Incremental & Online Learning** — Learn from single samples on the fly without heavy retraining pipelines.
- **Sub-Millisecond Inference** — Highly optimized feature vectors and compact primitive array storage.

---

## Key Features

- **🎯 Centroid & Nearest-Mean Classifier** — Fast online vector averaging and Euclidean distance prediction.
- **📐 Structural & Geometric Feature Extraction** — 8D normalized feature extractor for handwriting, characters, and image patches (aspect ratio, center of mass, stroke density, segment lengths).
- **🪟 Sliding Window Vision Scanner** — Multi-scale image scanning engine to locate and classify pattern matches across target images.
- **🧩 Flexible Pattern Abstraction** — First-class support for `VectorPattern`, binary `RasterPattern`, and custom numeric descriptors.

---

## Performance

| Operation | Scale / Input | Time / Latency |
|---|---|---|
| Feature Extraction (8D) | 60×60 Image Window | **< 40 µs** |
| Centroid Distance Match | 8-Dimensional Vector | **< 15 ns** |
| Sliding Window Scan | 800×600 Image (15px Stride) | **~18 ms** |

---

## Architecture & FastJava Ecosystem

```text
                    FastAI (High-Level AI API)
                         │
                   ┌─────┴─────────────┐
                   ▼                   ▼
             FastModel               FastML (This Library)
          (LLMs & Embeddings)      (Classical ML & Patterns)
                                       │
                         ┌─────────────┼─────────────┐
                         ▼             ▼             ▼
                      Patterns      Algorithms     Vision
                     (Vectors,       (Centroid,    (Sliding
                      Rasters)       KNN, etc.)     Window)
```

---

## API Quick Reference

| Method | Description |
|---|---|
| `FastML.centroid()` | Creates a new `CentroidClassifier` instance. |
| `FastML.vector(double...)` | Creates a numeric `VectorPattern`. |
| `FastML.raster(String)` | Parses a multiline ASCII grid into a `RasterPattern`. |
| `FastML.extractFeatures(img, x, y, w, h)` | Extracts an 8D normalized feature vector from a sub-rectangle. |
| `FastML.scanner(classifier)` | Creates a `SlidingWindowScanner` for image object detection. |

---

## Installation

### Option 1: Maven (Recommended)

Add the JitPack repository and dependency to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastML</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

### Option 2: Gradle (via JitPack)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:FastML:0.1.0'
}
```

---

## Technical Examples & Hero Demos

| Case | Java Example | Description |
|---|---|---|
| Handwriting Recognition & Sliding Window | [HandwritingDemo.java](examples/Demo/src/main/java/fastml/demo/HandwritingDemo.java) | Interactive GUI app demonstrating single-shot centroid learning and multi-region image scanning |

---

## Platform Support

| Platform | Status |
|---|---|
| **Windows 10/11** | ✅ Fully Supported |
| **Linux** | ✅ Fully Supported |
| **macOS** | ✅ Fully Supported |

---

## License

MIT License — See [LICENSE](LICENSE) for details.

---

## Related Projects

- [FastAI](https://github.com/andrestubbe/FastAI) — High-level unified AI client
- [FastModel](https://github.com/andrestubbe/FastModel) — Local GGUF/ONNX model runtimes
- [FastAIVectorDB](https://github.com/andrestubbe/FastAIVectorDB) — High-speed native vector database
- [FastMath](https://github.com/andrestubbe/FastMath) — SIMD and matrix math primitives
- [FastCore](https://github.com/andrestubbe/FastCore) — Native JNI loader and utilities

---

**Part of the FastJava Ecosystem** — *Making the JVM faster. Small package. Maximum speed. Zero bloat. 🚀*
