package mnm.mods.tabbychat.api.filters

import net.minecraft.util.StringUtils
import net.minecraft.util.text.ITextComponent

import java.util.regex.Pattern

/**
 * A filter is used to filter chat.
 */
interface Filter {

    val expression: Regex

    fun action(event: FilterEvent)

    /**
     * Used to convert the component to the string.
     *
     * Default implementation also strips any control/color codes.
     *
     * @param string The text component to be processed
     * @return The string which will be used for the
     */
    fun prepareText(string: ITextComponent): String {
        return StringUtils.stripControlCodes(string.string)
    }

}
