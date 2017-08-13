package handler

import beans.MsgBean
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


/**
 * Created by Darkhighness on 2017/8/12 For CoolAir.
 */
class MessageHandler {
    var all_running_group = HashMap<Long,Long>()

    fun handle(msg: String?): ArrayList<String> {
        val result = ArrayList<String>()
        if (msg.isNullOrEmpty()) return result
        val json = Gson().fromJson<MsgBean>(msg, MsgBean::class.java)

        if (checkMsgInRule(json.msg)) {
            val AT = callbackMsg(generateAT(json.fromQQ), json.fromGroup)
            var SM = handleInRuleMsg(json.msg, json)
            if (!SM.isNullOrBlank() && !SM.isNullOrEmpty()) {
                result.add(AT)
                SM = callbackMsg(SM, json.fromGroup)
                result.add(SM)
            }
        }
        return result
    }

    fun handleInRuleMsg(msg: String, data: MsgBean): String {
        val args = divideArgs(msg)
        val builder = StringBuilder()
        if (args[0].equals("start")) {
            if (data.fromGroup.toLong() in all_running_group.keys) {
                builder.append("本群已经有一场正在进行的游戏了")
            } else {
                all_running_group.put(data.fromGroup.toLong(),data.fromQQ.toLong())
                builder.append("成功在 ${data.fromGroupName} 开始一场游戏,主办者 " +
                        "${if (data.nick.isNullOrEmpty()) data.username
                          else data.nick}")
            }
        }

        if (args[0].equals("stop")){
            if (data.fromGroup.toLong() in all_running_group.keys){
                if (data.fromQQ.toLong() == all_running_group[data.fromGroup.toLong()]){
                    builder.append("成功停止了在本群的游戏")
                    all_running_group.remove(data.fromGroup.toLong())
                }
                else{
                    builder.append("只有本场游戏的主办者才能停止本场游戏")
                    builder.append("\r\n")
                    builder.append("本场主办者: ${generateAT(all_running_group[data.fromGroup.toLong()].toString())}")
                }
            }
            else{
                builder.append("本群没有正在进行的游戏")
            }
        }

        if (data.fromGroup.toLong() in all_running_group) {
            if (args[0].contains("d") || args[0].all { arg -> arg.isDigit() } || args[0].contains("{")) {
                builder.append("您掷骰的结果是: ")
                if (args[0].all { arg -> arg.isDigit() } || args[0].contains("{")) {
                    if (args[0].contains("{") && args[0].contains("}")) {
                        val after = args[0].substringAfter("{").substringBefore("}")
                        val min = after.substringBefore(",").toInt()
                        val max = after.substringAfter(",").toInt()
                        builder.append(rand(min = min, max = max))
                    } else {
                        val max = args[0].toInt()
                        builder.append(rand(max = max))
                    }
                } else {
                    val times = args[0].substringBefore("d").toInt()
                    if (args[0].contains("{") && args[0].contains("}")) {
                        val after = args[0].substringAfter("{").substringBefore("}")
                        val min = after.substringBefore(",").toInt()
                        val max = after.substringAfter(",").toInt()
                        builder.append(rand(times = times, min = min, max = max))
                    } else {
                        val max = args[0].substringAfter("d").toInt()
                        builder.append(rand(times = times, max = max))
                    }
                }
            }
        } else if (args[0] != "stop"){
            builder.append("本群还没有正在进行的游戏,使用 .d start 来开始一场游戏")
        }

        return builder.toString()
    }

    fun generateAT(QQ: String): String {
        return "[CQ:at,qq=$QQ]"
    }

    fun rand(times: Int = 1, min: Int = 0, max: Int): String {
        val builder = StringBuilder("{")
        val random = Random()
        var total = 0
        for (i in 1..times) {
            var temp = random.nextInt(max - min) + min
            builder.append(temp)
            if (i != times) {
                builder.append(",")
            }
            total += temp
        }
        builder.append("} = ")
        builder.append(total)
        return builder.toString()
    }

    fun checkMsgInRule(msg: String): Boolean {
        if (!msg.isNullOrBlank() && !msg.isNullOrEmpty()) {
            if (msg.startsWith(".d", true) or msg.startsWith(".dx", true)) {
                return true
            }
        }
        return false
    }

    fun divideArgs(msg: String): List<String> {
        val after = msg.substringAfter(" ")
        if (after == msg) return emptyList()
        val split = after.split(" ")
        return split
    }

    fun callbackMsg(msg: String, group: String): String {
        var callback = JsonObject()
        callback.addProperty("act", "101")
        callback.addProperty("groupid", group)
        callback.addProperty("msg", msg)
        return callback.toString()
    }
}