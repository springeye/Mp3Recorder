package io.j99.library.mp3recorder

import android.media.AudioFormat

/**
 * @author henjue henjue@gmail.com
 */
enum class PCMFormat(var bytesPerFrame: Int, var audioFormat: Int) {
    PCM_8BIT(1, AudioFormat.ENCODING_PCM_8BIT), PCM_16BIT(2, AudioFormat.ENCODING_PCM_16BIT);

}