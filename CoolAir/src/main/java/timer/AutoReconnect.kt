package timer

import Robot
import java.util.*

/**
 * Created by Darkhighness on 2017/8/13 For CoolAir.
 */
class AutoReconnect : TimerTask() {

    override fun run() {
        println("正在尝试重新链接到服务器...")
        Robot.reconnect()
    }
}