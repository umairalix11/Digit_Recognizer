# NumberGuessing App TFLite

## 1. Project Overview
NumberGuessing App TFLite is an Android application that uses Artificial Intelligence (AI) and TensorFlow Lite (TFLite) to recognize handwritten digits.
Users can draw a digit on the screen, and the application analyzes the drawing and predicts which number it represents.

## 2. Features
- Draw handwritten digits on the screen.
- Predict the drawn digit using a machine learning model.
- Display the predicted number.
- Show the prediction confidence score.
- Display accuracy or prediction-related information, as implemented in the application.
- Run AI inference directly on the Android device using TensorFlow Lite.

## 3. Technologies Used
- Android Studio
- Java / Kotlin (according to the source code)
- TensorFlow Lite
- Gradle
- Machine Learning for handwritten digit recognition

## 4. Prerequisites
- Android Studio
- A compatible Android SDK
- An Android device or emulator
- Internet access for initial dependency downloads, if required

## 5. Installation
1. Clone or download this repository from GitHub.
2. Open Android Studio.
3. Select Open and choose the project folder.
4. Wait for Gradle Sync to finish.
5. Connect an Android device or start an emulator.

## 6. How to Run
1. Open the project in Android Studio.
2. Allow Gradle Sync to complete.
3. Run the application on a compatible device or emulator.
4. Draw a digit on the drawing area.
5. Tap the prediction button, if the app provides one.
6. View the predicted digit and confidence score.
7. Repeat the test with different handwritten digits.

## 7. AI Model
The application uses a TensorFlow Lite model to process handwritten digit input and predict the corresponding digit.
The model's input format, output interpretation, and supported digit range depend on the actual model integrated into the project.

## 8. Testing and Results
Test the application using different handwritten digits, including clear and differently written examples.
Capture screenshots showing:
- The application interface.
- A handwritten digit drawn by the user.
- The predicted digit and confidence score.
- Results for multiple test inputs.

## 9. Limitations
Prediction results may vary depending on handwriting quality, drawing size, input preprocessing, and the trained model.

## 10. Conclusion
This project demonstrates how a TensorFlow Lite machine learning model can be integrated into an Android application to recognize handwritten digits and display prediction results.
