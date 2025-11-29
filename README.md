# Photo Sorting Spring Boot App Using Deep Learning Algorithms

This project is a **deep-learning powered photo sorting tool** that helps you organize large collections of images.  
It uses **face detection** and **face matching** models to automatically:

## 🧠 Deep Learning Approach

This application uses two deep learning models working together to detect, analyze, and compare human faces in images. 
These models are loaded and executed using **DJL (Deep Java Library)** with the **PyTorch** engine.

---

### 1️⃣ Face Detection Model

The first step in the pipeline is detecting whether an image contains a human face.  
For this, the app uses DJL’s **pre-trained face detection model** (based on a modern object detection architecture such as RetinaFace or SSD).

**What it does:**

- Identifies faces present in an image
- Returns bounding boxes and confidence scores
- Helps filter out images with **no detectable face**

This model powers the **`noface`** command, which automatically moves images without faces into a separate folder.

---

### 2️⃣ Face Embedding Model (VGGFace2)

For comparing and grouping faces, the application uses a **PyTorch VGGFace2 model**, a deep convolutional neural network trained on millions of face images.

**How it works:**

- When a face is detected, the model generates a **128-D or 512-D embedding vector (`double[]`)**
- This vector captures the **unique features** of the person's face
- Two faces are compared by calculating the **distance between their embedding vectors**

Interpretation:

- **Small distance → same person**
- **Large distance → different people**

This model powers the **`matchface`** command to automatically identify unmatched faces and move them to a separate folder.

---

### 🧬 How Both Models Work Together

1. **Face Detection**
    - Detects whether the image contains a face
    - If no face → image moved to `noface/`

2. **Face Embedding Extraction**
    - For detected faces, the embedding model extracts deep-learning-based feature vectors

3. **Face Comparison**
    - Embeddings are compared with a reference or across images
    - Non-matching images are moved to `unmatched/`

---

### ⭐ Why This Approach Works Well

- Based on large, high-quality datasets (VGGFace2)
- Highly accurate for face matching
- Works with variations in pose, lighting, and background
- Efficient enough to process large image collections
- Fully automated with no manual tagging required


---

## ✨ Features

### ✔️ Face Detection
Uses the built-in DJL model (`face_detection`) to detect faces in images.  
If an image has **no face**, it can be automatically moved to a separate directory.

### ✔️ Face Matching
Uses a **VGGFace2 PyTorch model** to extract a facial embedding (`double[]`) vector.  
This allows the app to compare faces between images and separate images that do not match a reference face.

### ✔️ Command-Line Interface (CLI)
This app exposes commands via **Picocli**:

- `matchface` – Match faces in a directory and move **unmatched** images to a subfolder
- `noface` – Detect images without any face and move them to a subfolder

---

## 🧠 Deep Learning Models Used

### 1. Face Embedding Model (VGGFace2)

```java
Criteria<Image, double[]> criteria = Criteria.builder()
        .setTypes(Image.class, double[].class)
        .optTranslator(new FaceTranslator())
        .optEngine("PyTorch")
        .optModelUrls(tempModelDir.toURI().toString())
        .optModelName("vggface2")
        .build();
```
This model generates a numerical feature vector (double[]) representing a person’s face.

### 2. Face Detection Model
```java
Criteria<Image, DetectedObjects> criteria = Criteria.builder()
.optApplication(Application.CV.OBJECT_DETECTION)
.setTypes(Image.class, DetectedObjects.class)
.optArtifactId("face_detection")
.optTranslator(new FaceTranslator(0.5f, 0.7f))
.optFilter("flavor", "server")
.build();
```
Used to detect whether a face is present in an image.

## 🛠️ Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/vsnemade/photosorting.git
cd photosorting
```
2. Build with Maven
```bash
mvn clean install
```

3. Run the Application
The app exposes commands via Picocli.
```bash
java -jar target/photosorting.jar <command> [options]
```

## 🚀 Usage

### 1️⃣ matchface

Match faces in a directory and move **unmatched** photos to a subfolder.

**Description:**  
Compares each photo in a directory against a reference face.  
If a face does not match, the image is moved into a new folder (e.g., `unmatched/`).

**Command Signature:**
```bash
java -jar photosorting-1.0.jar matchface -i <directory of images used for training the model> -p <directory of images that needs to be evaluated>
```
Example usage:
```bash
java -jar photosorting-1.0.jar matchface -i "C:\Workspace\Vishal\java_projects\photosorting\Input" -p "C:\Workspace\Vishal\java_projects\photosorting\Predict"
```


## 📁 Folder Structure of Train/Input for matchface
Train/  
├── John/  
│ ├── image1.jpg  
│ ├── image2.jpg  
│  
├── Marry/  
│ ├── image1.jpg  
│ ├── image2.jpg  
│  
└── Ketty/  
├── image1.jpg  
├── image2.jpg  

## 📁 Folder Structure of Predict for matchface
Predict/  
├── image1.jpg  
├── image2.jpg  
├── image3.jpg  
├── image4.jpg  
├── image5.jpg  
├── image6.jpg  

### 1️⃣ noface
Detects images with no faces and moves **noface** photos to a subfolder .

**Command Signature:**
```bash
java -jar photosorting-1.0.jar noface -i <path of directory that has jpg/jpeg> images
```
Example usage:
```bash
java -jar photosorting-1.0.jar noface -i "C:\Workspace\Vishal\java_projects\photosorting\Predict"
```
## 📁 Folder Structure of Input for noface
Input/  
├── image1.jpg  
├── image2.jpg  
├── image3.jpg  
├── image4.jpg  
├── image5.jpg  
├── image6.jpg  
