package com.robertconstantindinescu.my_doctor_app.utils.emailSender

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

class ByteArrayDataSource : DataSource {
    private var data: ByteArray
    private var type: String? = null

    constructor(data: ByteArray, type: String?) : super() {
        this.data = data
        this.type = type
    }

    constructor(data: ByteArray) : super() {
        this.data = data
    }

    fun setType(type: String?) {
        this.type = type
    }

    @Throws(IOException::class)
    override fun getInputStream(): InputStream {
        return ByteArrayInputStream(data)
    }

    @Throws(IOException::class)
    override fun getOutputStream(): OutputStream {
        throw IOException("Not Supported")
    }

    override fun getContentType(): String {
        return if (type == null) "application/octet-stream" else type as String
    }

    override fun getName(): String {
        return "ByteArrayDataSource"
    }
}