package socket

import Robot
import handler.MessageHandler
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft
import org.java_websocket.handshake.ServerHandshake
import timer.AutoReconnect
import java.lang.Exception
import java.net.URI
import kotlin.properties.Delegates

/**
 * Created by Darkhighness on 2017/8/12 For CoolAir.
 */
class Client(serverUri: URI?, protocolDraft: Draft?, handler: MessageHandler) : WebSocketClient(serverUri, protocolDraft) {
    var connected: Boolean = false
    var handler: MessageHandler by Delegates.notNull<MessageHandler>()

    init {
        this.handler = handler
    }

    override fun onOpen(handshake: ServerHandshake?) {
        println("成功连接到 ${getURI()}")
        connected = true
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        println("断开与 ${getURI()} 的连接;原因: $reason Code: $code")
        connected = false
        if (code == 1006) {
            Robot.schedule(AutoReconnect())
        }
    }

    override fun onMessage(message: String?) {
        val list_str = handler.handle(message)
        println("Receive Msg: $message")
        list_str.forEach { msg ->
            if (!msg.isNullOrEmpty()) {
                println("Send Msg: $msg")
                send(msg)
            }
        }
    }

    override fun onError(ex: Exception?) {
        println("与 ${getURI()} 的连接发生错误;错误原因: ${ex.toString()}")
    }

}