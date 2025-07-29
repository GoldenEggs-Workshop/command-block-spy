package gold.eggs.commandblockspy

import org.bukkit.plugin.java.JavaPlugin
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.iterator

/**
 * 国际化工具类，用于加载语言文件并提供多语言支持。
 */
object I18nManager {
    private var messages: Map<String, String> = emptyMap()

    /**
     * 初始化语言内容
     * @param langCode 语言代码，如 "en_US"
     * @param plugin 插件主类（用于获取资源）
     */
    fun init(langCode: String, plugin: JavaPlugin) {
        val path = "lang/$langCode.lang"
        val stream = plugin.getResource(path)
            ?: throw IllegalArgumentException("Missing i18n file: $path")

        val props = Properties()
        InputStreamReader(stream, Charsets.UTF_8).use { reader ->
            props.load(reader)
        }

        messages = props.entries.associate { (k, v) -> k.toString() to v.toString() }
    }

    /**
     * 获取翻译文本，支持 & 颜色码 和 {占位符} 替换
     */
    fun get(key: String, placeholders: Map<String, String> = emptyMap()): String {
        val raw = messages[key] ?: key
        return applyPlaceholders(applyColorCodes(raw), placeholders)
    }

    /** 将 &e 转换为 §e，&& 转义为 & */
    private fun applyColorCodes(input: String): String {
        return input.replace("&&", "\u0000")
            .replace("&([0-9a-fklmnor])".toRegex(), "§$1")
            .replace("\u0000", "&")
    }

    /** 替换 {name} 形式的占位符 */
    private fun applyPlaceholders(text: String, placeholders: Map<String, String>): String {
        var result = text
        for ((key, value) in placeholders) {
            result = result.replace("{$key}", value)
        }
        return result
    }
}
