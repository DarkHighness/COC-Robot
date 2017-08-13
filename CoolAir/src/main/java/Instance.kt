import java.net.URI
import kotlin.properties.Delegates

/**
 * Created by Darkhighness on 2017/8/12 For CoolAir.
 */

var url: String by Delegates.notNull<String>()

fun main(args: Array<String>): Unit {
    url = "ws://localhost:25303"
    Robot.init(URI.create(url))
}