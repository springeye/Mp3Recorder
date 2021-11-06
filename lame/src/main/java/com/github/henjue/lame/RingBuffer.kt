package com.github.henjue.lame

import android.util.Log

/**
 * @author henjue henjue@gmail.com
 */
class RingBuffer(private val size: Int) {
    private val buffer: ByteArray
    private var rp: Int
    private var wp: Int

    /**
     * Check number of bytes left
     *
     * @param writeCheck
     * @return
     */
    private fun checkSpace(writeCheck: Boolean): Int {
        val s: Int
        s = if (writeCheck) {
            if (wp > rp) {
                rp - wp + size - 1
            } else if (wp < rp) {
                rp - wp - 1
            } else size - 1
        } else {
            if (wp > rp) {
                wp - rp
            } else if (wp < rp) {
                wp - rp + size
            } else {
                0
            }
        }
        return s
    }

    /**
     * Read a number of bytes from ring buffer
     *
     * @param buffer buffer
     * @param bytes  bytes
     * @return length
     */
    fun read(buffer: ByteArray, bytes: Int): Int {
        var remaining: Int
        if (checkSpace(false).also { remaining = it } == 0) {
            return 0
        }
        val bytesread = if (bytes > remaining) remaining else bytes
        // copy from ring buffer to buffer
        for (i in 0 until bytesread) {
            buffer[i] = this.buffer[rp++]
            if (rp == size) rp = 0
        }
        return bytesread
    }

    /**
     * Write a number of bytes to ring buffer;
     *
     * @param buffer buffer
     * @param bytes  bytes
     * @return write length
     */
    fun write(buffer: ByteArray, bytes: Int): Int {
        var remaining: Int
        if (checkSpace(true).also { remaining = it } == 0) {
            Log.e(RingBuffer::class.java.simpleName, "Buffer overrun. Data will not be written")
            return 0
        }
        val byteswrite = if (bytes > remaining) remaining else bytes
        for (i in 0 until byteswrite) {
            this.buffer[wp++] = buffer[i]
            if (wp == size) wp = 0
        }
        return byteswrite
    }

    /**
     * Initialize a ring buffer given number of bytes
     *
     * @param size ringsize
     */
    init {
        buffer = ByteArray(size)
        rp = 0
        wp = rp
    }
}