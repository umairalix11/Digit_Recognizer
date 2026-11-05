package com.example.digitrecognizer;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private DrawingView drawingView;
    private Button btnPredict, btnClear, btnExit;
    private TextView tvResult;
    private Interpreter tfliteInterpreter;
    private static final String MODEL_FILE = "digit_model.tflite";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        drawingView = findViewById(R.id.drawingView);
        btnPredict = findViewById(R.id.btnPredict);
        btnClear = findViewById(R.id.btnClear);
        btnExit = findViewById(R.id.btnExit);
        tvResult = findViewById(R.id.tvResult);

        // Load pre-trained digit_model.tflite from the assets folder and show shapes
        try {
            tfliteInterpreter = new Interpreter(loadModelFile(this, MODEL_FILE));
            
            StringBuilder info = new StringBuilder("Model Loaded!\n");
            for (int i = 0; i < tfliteInterpreter.getInputTensorCount(); i++) {
                info.append("In[").append(i).append("]: ").append(Arrays.toString(tfliteInterpreter.getInputTensor(i).shape())).append(" ");
            }
            for (int i = 0; i < tfliteInterpreter.getOutputTensorCount(); i++) {
                info.append("Out[").append(i).append("]: ").append(Arrays.toString(tfliteInterpreter.getOutputTensor(i).shape()));
            }
            tvResult.setText(info.toString());
            Toast.makeText(this, "TFLite Model Loaded Successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            tvResult.setText("Model load error: Please place digit_model.tflite in assets/");
            Toast.makeText(this, "Model not found in assets/" + MODEL_FILE, Toast.LENGTH_LONG).show();
        }

        btnClear.setOnClickListener(v -> {
            drawingView.clear();
            tvResult.setText("Prediction: None");
        });

        btnPredict.setOnClickListener(v -> {
            if (tfliteInterpreter == null) {
                tvResult.setText("Interpreter not initialized");
                return;
            }
            classifyDrawing();
        });

        // App Exit Feature with finishAffinity() and smooth closing transition
        btnExit.setOnClickListener(v -> {
            finishAffinity();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    private MappedByteBuffer loadModelFile(Context context, String modelFileName) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(modelFileName);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void classifyDrawing() {
        Bitmap originalBitmap = drawingView.getBitmap();
        if (originalBitmap == null) {
            return;
        }

        try {
            // Dynamically preprocess bitmap to match the model's expected input shape
            Object inputArray = preprocessBitmap(originalBitmap);

            // Get output tensor shape dynamically
            int[] outputShape = tfliteInterpreter.getOutputTensor(0).shape();
            Object outputArray;

            if (outputShape.length == 2) {
                outputArray = new float[outputShape[0]][outputShape[1]];
            } else if (outputShape.length == 1) {
                outputArray = new float[outputShape[0]];
            } else {
                outputArray = new float[1][10];
            }

            // Run TFLite inference
            tfliteInterpreter.run(inputArray, outputArray);

            // Parse output probabilities
            float[] probabilities;
            if (outputShape.length == 2) {
                probabilities = ((float[][]) outputArray)[0];
            } else {
                probabilities = (float[]) outputArray;
            }

            if (probabilities.length != 10) {
                tvResult.setText("Model output shape " + Arrays.toString(outputShape) + " != 10 classes.");
                return;
            }

            // Find predicted digit with highest confidence probability
            int predictedDigit = 0;
            float maxConfidence = probabilities[0];
            for (int i = 1; i < probabilities.length; i++) {
                if (probabilities[i] > maxConfidence) {
                    maxConfidence = probabilities[i];
                    predictedDigit = i;
                }
            }

            // Display predicted digit result on TextView
            tvResult.setText(String.format("Prediction: %d (Confidence: %.1f%%)", predictedDigit, maxConfidence * 100));
        } catch (Exception e) {
            e.printStackTrace();
            tvResult.setText("Inference error: " + e.getMessage());
        }
    }

    private Object preprocessBitmap(Bitmap originalBitmap) {
        int[] inputShape = tfliteInterpreter.getInputTensor(0).shape();
        
        int targetHeight = 28;
        int targetWidth = 28;
        int channels = 1;

        if (inputShape.length == 4) {
            targetHeight = inputShape[1];
            targetWidth = inputShape[2];
            channels = inputShape[3];
        } else if (inputShape.length == 3) {
            targetHeight = inputShape[1];
            targetWidth = inputShape[2];
        } else if (inputShape.length == 2 && inputShape[1] != 784) {
            int totalSize = inputShape[1];
            int dim = (int) Math.sqrt(totalSize);
            if (dim * dim == totalSize) {
                targetHeight = dim;
                targetWidth = dim;
            }
        }

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true);
        int[] pixels = new int[targetWidth * targetHeight];
        scaledBitmap.getPixels(pixels, 0, targetWidth, 0, 0, targetWidth, targetHeight);

        float[] flatPixels = new float[targetWidth * targetHeight];
        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];
            int r = (pixel >> 16) & 0xFF;
            int g = (pixel >> 8) & 0xFF;
            int b = pixel & 0xFF;
            flatPixels[i] = (r + g + b) / 3.0f / 255.0f;
        }

        if (inputShape.length == 4) {
            float[][][][] input4D = new float[inputShape[0]][targetHeight][targetWidth][channels];
            int idx = 0;
            for (int h = 0; h < targetHeight; h++) {
                for (int w = 0; w < targetWidth; w++) {
                    for (int c = 0; c < channels; c++) {
                        input4D[0][h][w][c] = (c == 0) ? flatPixels[idx] : 0f;
                    }
                    idx++;
                }
            }
            return input4D;
        } else if (inputShape.length == 3) {
            float[][][] input3D = new float[inputShape[0]][targetHeight][targetWidth];
            int idx = 0;
            for (int h = 0; h < targetHeight; h++) {
                for (int w = 0; w < targetWidth; w++) {
                    input3D[0][h][w] = flatPixels[idx++];
                }
            }
            return input3D;
        } else {
            float[][] input2D = new float[1][flatPixels.length];
            System.arraycopy(flatPixels, 0, input2D[0], 0, flatPixels.length);
            return input2D;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tfliteInterpreter != null) {
            tfliteInterpreter.close();
        }
    }
}
