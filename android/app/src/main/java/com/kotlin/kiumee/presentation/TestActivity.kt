package com.kotlin.kiumee.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import androidx.core.app.ActivityCompat
import com.kotlin.kiumee.R
import com.kotlin.kiumee.core.base.BindingActivity
import com.kotlin.kiumee.databinding.ActivityTestBinding
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

open class TestActivity : BindingActivity<ActivityTestBinding>(R.layout.activity_test), Runnable {
    private lateinit var mModuleEncoder: Module

    private val REQUEST_RECORD_AUDIO = 13
    private val AUDIO_LEN_IN_SECOND = 6
    private val SAMPLE_RATE = 16000
    private val RECORDING_LENGTH = SAMPLE_RATE * AUDIO_LEN_IN_SECOND

    private var mStart = 1
    private var mTimerThread: HandlerThread? = null
    private var mTimerHandler: Handler? = null
    private val mRunnable: Runnable = object : Runnable {
        override fun run() {
            mTimerHandler?.postDelayed(this, 1000)

            runOnUiThread {
                binding.btnTest.text =
                    String.format("Listening - %ds left", AUDIO_LEN_IN_SECOND - mStart)
                mStart += 1
            }
        }
    }

    override fun initView() {
        binding.btnTest.setOnClickListener {
            binding.btnTest.text = String.format("Listening - %ds left", AUDIO_LEN_IN_SECOND)
            binding.btnTest.isEnabled = false

            val thread = Thread(this)
            thread.start()

            mTimerThread = HandlerThread("Timer")
            mTimerThread?.start()
            mTimerHandler = Handler(mTimerThread!!.looper)
            mTimerHandler?.postDelayed(mRunnable, 1000)
        }

        initializeWAV2Vec2Model()
        requestMicrophonePermission()
    }

    private fun initializeWAV2Vec2Model() {
        // WAV2Vec2 모델을 한 번만 로드하도록 합니다.
        if (!::mModuleEncoder.isInitialized) {
            val moduleFileAbsoluteFilePath = File(assetFilePath(this, "wav2vec2.pt")).absolutePath
            mModuleEncoder = Module.load(moduleFileAbsoluteFilePath)
        }
    }

    private fun requestMicrophonePermission() {
        requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO)
    }

    private fun stopTimerThread() {
        mTimerThread?.quitSafely()
        try {
            mTimerThread?.join()
            mTimerThread = null
            mTimerHandler = null
            mStart = 1
        } catch (e: InterruptedException) {
            Log.e(TAG, "Error on stopping background thread", e)
        }
    }

    override fun run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO)

        initializeWAV2Vec2Model()

        val bufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val record = if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO
            )
            return
        } else {
            AudioRecord(
                MediaRecorder.AudioSource.DEFAULT,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )
        }

        if (record.state != AudioRecord.STATE_INITIALIZED) {
            Log.e(TAG, "Audio Record can't initialize!")
            return
        }
        record.startRecording()

        var shortsRead: Long = 0
        var recordingOffset = 0
        val audioBuffer = ShortArray(bufferSize / 2)
        val recordingBuffer = ShortArray(RECORDING_LENGTH)

        while (shortsRead < RECORDING_LENGTH) {
            val numberOfShort = record.read(audioBuffer, 0, audioBuffer.size)
            shortsRead += numberOfShort
            System.arraycopy(audioBuffer, 0, recordingBuffer, recordingOffset, numberOfShort)
            recordingOffset += numberOfShort
        }

        record.stop()
        record.release()
        stopTimerThread()

        runOnUiThread {
            binding.btnTest.text = "Recognizing..."
        }

        val floatInputBuffer = FloatArray(RECORDING_LENGTH)

        // feed in float values between -1.0f and 1.0f by dividing the signed 16-bit inputs.
        for (i in 0 until RECORDING_LENGTH) {
            floatInputBuffer[i] = recordingBuffer[i] / Short.MAX_VALUE.toFloat()
        }

        val result = recognize(floatInputBuffer)

        runOnUiThread {
            showTranslationResult(result)
            binding.btnTest.isEnabled = true
            binding.btnTest.text = "Start"
        }
    }

    private fun recognize(floatInputBuffer: FloatArray): String {
        if (mModuleEncoder == null) {
            val moduleFileAbsoluteFilePath = File(assetFilePath(this, "wav2vec2.pt")).absolutePath
            mModuleEncoder = Module.load(moduleFileAbsoluteFilePath)
        }

        val wav2vecinput = DoubleArray(RECORDING_LENGTH)
        for (n in 0 until RECORDING_LENGTH) {
            wav2vecinput[n] = floatInputBuffer[n].toDouble()
        }

        val inTensorBuffer = Tensor.allocateFloatBuffer(RECORDING_LENGTH)
        for (w in wav2vecinput) {
            inTensorBuffer.put(w.toFloat())
        }

        val inTensor = Tensor.fromBlob(inTensorBuffer, longArrayOf(1, RECORDING_LENGTH.toLong()))
        val result = mModuleEncoder!!.forward(IValue.from(inTensor)).toStr()

        return result
    }

    private fun assetFilePath(context: Context, assetName: String): String? {
        val file = File(context.filesDir, assetName)
        if (file.exists() && file.length() > 0) {
            return file.absolutePath
        }

        try {
            context.assets.open(assetName).use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    val buffer = ByteArray(4 * 1024)
                    var read: Int
                    while (inputStream.read(buffer).also { read = it } != -1) {
                        outputStream.write(buffer, 0, read)
                    }
                    outputStream.flush()
                }
            }
            return file.absolutePath
        } catch (e: IOException) {
            Log.e(TAG, "$assetName: ${e.localizedMessage}")
        }
        return null
    }

    private fun showTranslationResult(result: String) {
        binding.tvTest.text = result
    }

    companion object {
        private const val TAG = "Test"
    }
}
