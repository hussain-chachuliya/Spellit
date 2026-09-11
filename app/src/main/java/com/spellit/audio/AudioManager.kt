package com.spellit.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaRecorder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles voice pronunciation recording with MediaRecorder and playback with
 * MediaPlayer. Audio files live in the app's internal cache directory and do
 * not require any storage permissions.
 */
@Singleton
class AudioManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var currentRecordingPath: String? = null

    private val wordsDir: File
        get() = File(context.cacheDir, "words").apply { mkdirs() }

    /** Creates a fresh recording file path inside the app's cache directory. */
    fun createRecordingPath(): String {
        return File(wordsDir, "word_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(8)}.m4a").absolutePath
    }

    /**
     * Starts a recording session for the provided file path and returns the
     * resolved path. Call [stopRecording] when finished.
     */
    @Suppress("DEPRECATION")
    suspend fun startRecording(outputPath: String) = withContext(Dispatchers.IO) {
        releaseRecorder()
        val outputFile = File(outputPath)
        outputFile.parentFile?.mkdirs()
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioSamplingRate(44100)
            setAudioEncodingBitRate(128000)
            setOutputFile(outputFile.absolutePath)
            prepare()
            start()
        }
        currentRecordingPath = outputPath
        outputPath
    }

    /** Stops recording and returns the path of the saved audio file. */
    suspend fun stopRecording(): String? = withContext(Dispatchers.IO) {
        runCatching {
            recorder?.stop()
            recorder?.release()
        }
        recorder = null
        val path = currentRecordingPath
        currentRecordingPath = null
        path
    }

    /** Cancels an in-progress or completed recording and removes the file. */
    suspend fun cancelRecording() = withContext(Dispatchers.IO) {
        runCatching {
            recorder?.stop()
            recorder?.release()
        }
        recorder = null
        val path = currentRecordingPath
        currentRecordingPath = null
        if (path != null) File(path).delete()
    }

    /** Plays back an audio file located in the words cache directory. */
    fun playWord(fileName: String): Boolean {
        val file = File(wordsDir, fileName)
        if (!file.exists()) return false
        stopPlayback()
        return runCatching {
            player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                setDataSource(file.absolutePath)
                setOnCompletionListener { stopPlayback() }
                prepare()
                start()
            }
            true
        }.getOrElse { false }
    }

    /** Plays any audio file by its absolute path (used for admin preview). */
    fun playAbsolutePath(path: String): Boolean {
        val file = File(path)
        if (!file.exists()) return false
        stopPlayback()
        return runCatching {
            player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                setDataSource(file.absolutePath)
                setOnCompletionListener { stopPlayback() }
                prepare()
                start()
            }
            true
        }.getOrElse { false }
    }

    fun stopPlayback() {
        runCatching { player?.stop() }
        player?.release()
        player = null
    }

    fun isPlaying(): Boolean = player?.isPlaying == true

    /** Removes the audio file for a deleted word. */
    fun deleteAudioFile(fileName: String?) {
        if (fileName.isNullOrBlank()) return
        runCatching { File(wordsDir, fileName).delete() }
    }

    fun releaseRecorder() {
        runCatching { recorder?.release() }
        recorder = null
    }

    fun release() {
        stopPlayback()
        releaseRecorder()
    }
}