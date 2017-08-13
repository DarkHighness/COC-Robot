import handler.MessageHandler
import org.java_websocket.drafts.Draft
import org.java_websocket.drafts.Draft_17
import socket.Client
import java.net.URI
import java.util.*
import kotlin.properties.Delegates

/**
 * Created by Darkhighness on 2017/8/13 For CoolAir.
 */
object Robot {
    var url: URI by Delegates.notNull<URI>()
    var timer: Timer by Delegates.notNull<Timer>()
    var current_client: Client by Delegates.notNull<Client>()
    var message_handler = MessageHandler()
    fun init(url: URI): Unit {
        this.url = url
        this.timer = Timer()
        this.current_client = Client(url, Draft_17() as Draft, message_handler)
        this.current_client.connect()
    }

    fun schedule(task: TimerTask) {
        timer.schedule(task, 3000)
    }

    @Synchronized fun reconnect() {
        if (!current_client.connected) {
            this.current_client = Client(url, Draft_17() as Draft, message_handler)
            this.current_client.connect()
        }
    }
}