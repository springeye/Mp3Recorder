/* 
 * Copyright (c) 2011-2012 Yuichi Hirano
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.henjue.lame;

import androidx.annotation.Keep

/**
 * LAME interface class
 *
 * @author henjue henjue@gmail.com
 */
@Keep
object LameEncoder {
    init {
        System.loadLibrary("mp3lame");
    }

    /**
     * Initialize LAME.
     *
     * @param inSamplerate  input sample rate in Hz.
     * @param outChannel    number of channels in input stream.
     * @param outSamplerate output sample rate in Hz.
     * @param outBitrate    brate compression ratio in KHz.
     */
    @JvmStatic
    fun init(
        inSamplerate: Int, outChannel: Int,
        outSamplerate: Int, outBitrate: Int
    ) {
        init(inSamplerate, outChannel, outSamplerate, outBitrate, 7);
    }

    /**
     * Initialize LAME.
     *
     * @param inSamplerate  input sample rate in Hz.
     * @param outChannel    number of channels in input stream.
     * @param outSamplerate output sample rate in Hz.
     * @param outBitrate    brate compression ratio in KHz.
     * @param quality       quality=0..9. 0=best (very slow). 9=worst.<br >
     *                      recommended:<br >
     *                      2 near-best quality, not too slow<br >
     *                      5 good quality, fast<br >
     *                      7 ok quality, really fast
     */
    @JvmStatic
    external fun init(
        inSamplerate: Int, outChannel: Int,
        outSamplerate: Int, outBitrate: Int, quality: Int
    );

    /**
     * Encode buffer to mp3.
     *
     * @param buffer_l PCM data for left channel.
     * @param buffer_r PCM data for right channel.
     * @param samples  number of samples per channel.
     * @param mp3buf   result encoded MP3 stream. You must specified
     *                 "7200 + (1.25 * buffer_l.length)" length array.
     * @return number of bytes output in mp3buf. Can be 0.<br >
     * -1: mp3buf was too small<br >
     * -2: malloc() problem<br >
     * -3: lame_init_params() not called<br >
     * -4: psycho acoustic problems
     */
    @JvmStatic
    external fun encode(
        buffer_l: ShortArray, buffer_r: ShortArray,
        samples: Int, mp3buf: ByteArray
    ): Int

    /**
     * Flush LAME buffer.
     *
     * @param mp3buf result encoded MP3 stream. You must specified at least 7200
     *               bytes.
     * @return number of bytes output to mp3buf. Can be 0.
     */
    @JvmStatic
    external fun flush(mp3buf: ByteArray): Int

    /**
     * Close LAME.
     */
    @JvmStatic
    external fun close();
}
