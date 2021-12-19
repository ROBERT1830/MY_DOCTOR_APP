package com.robertconstantindinescu.my_doctor_app.utils.emailSender

import java.lang.Exception
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.*
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

class GMailSender(private val user: String, private val password: String) : Authenticator() {
    private val mailhost = "smtp.gmail.com"
    private val session: Session

    companion object {
        init {
            Security.addProvider(JSSEProvider())
        }
    }

    override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication(user, password)
    }

    @Synchronized
    @Throws(Exception::class)
    fun sendMail(
        subject: String?, body: String,
        sender: String?, recipients: String
    ) {
        val message = MimeMessage(session)
        val handler = DataHandler(ByteArrayDataSource(body.toByteArray(), "text/html"))
        message.sender = InternetAddress(sender)
        message.subject = subject
        message.dataHandler = handler
        if (recipients.indexOf(',') > 0) message.setRecipients(
            Message.RecipientType.TO,
            InternetAddress.parse(recipients)
        ) else message.setRecipient(
            Message.RecipientType.TO, InternetAddress(recipients)
        )
        Transport.send(message)
    }

    init {
        val props = Properties()
        props.setProperty("mail.transport.protocol", "smtp")
        props.setProperty("mail.host", mailhost)
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.port"] = "465"
        props["mail.smtp.socketFactory.port"] = "465"
        props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
        props["mail.smtp.socketFactory.fallback"] = "false"
        props.setProperty("mail.smtp.quitwait", "false")
        session = Session.getDefaultInstance(props, this)
    }
}
