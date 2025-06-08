package com.example.iris_kotlin_221351666

import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class MainActivity : AppCompatActivity() {

    private lateinit var interpreter: Interpreter
    private val mModelPath = "iris.tflite"

    private lateinit var resultText : TextView
    private lateinit var edtSepalLengthCm : EditText
    private lateinit var edtSepalWidthCm : EditText
    private lateinit var edtPetalLengthCm : EditText
    private lateinit var edtPetalWidthCm : EditText
    private lateinit var checkButton : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultText = findViewById(R.id.txtResult)
        edtSepalLengthCm = findViewById(R.id.edtSepalLengthCm)
        edtSepalWidthCm = findViewById(R.id.edtSepalWidthCm)
        edtPetalLengthCm = findViewById(R.id.edtPetalLengthCm)
        edtPetalWidthCm = findViewById(R.id.edtPetalWidthCm)
        checkButton = findViewById(R.id.btnCheck)

        checkButton.setOnClickListener {
            var result = doInference(
                edtSepalLengthCm.text.toString(),
                edtSepalWidthCm.text.toString(),
                edtPetalLengthCm.text.toString(),
                edtPetalWidthCm.text.toString())
            runOnUiThread {
                if (result == 0) {
                    resultText.text = "iris-setosa"
                }else if (result == 1){
                    resultText.text = "iris-versicolor"
                }else{
                    resultText.text = "iris-virginica"
                }
            }
        }
        initInterpreter()
    }

    private fun initInterpreter(){
        val options = Interpreter.Options()
        options.setNumThreads(5)
        options.setUseNNAPI(true)
        interpreter = Interpreter(loadModelFile(assets, mModelPath), options)
    }

    private fun doInference(input1: String, input2: String, input3: String, input4: String): Int {
        val inputVal = FloatArray(4)
        inputVal[0] = input1.toFloat()
        inputVal[1] = input2.toFloat()
        inputVal[2] = input3.toFloat()
        inputVal[3] = input4.toFloat()
        val output = Array(1) { FloatArray(3) }
        interpreter.run(inputVal, output)

        Log.e("result", (output[0].toList()+" ").toString())

        return output[0].indexOfFirst { it == output[0].maxOrNull() }
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}