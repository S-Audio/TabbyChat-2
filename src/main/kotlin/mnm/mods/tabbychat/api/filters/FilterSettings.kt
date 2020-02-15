package mnm.mods.tabbychat.api.filters

import com.electronwill.nightconfig.core.Config
import mnm.mods.tabbychat.util.ConfigView
import java.util.regex.Pattern

/**
 * Defines the settings used by filters.
 */
class FilterSettings(config: Config) : ConfigView(config) {

    // destinations
    var channels by definingList<String>()
    var isRemove by defining(false)
    var isRaw by defining(true)
    var isRegex by defining( false)
    var isCaseInsensitive by defining(false)

    // notifications
    var isSoundNotification by defining(false)
    var soundName by defining("")

    val flags: Int
        get() {
            var flags = 0
            if (isCaseInsensitive) {
                flags = flags or Pattern.CASE_INSENSITIVE
            }
            return flags
        }
}