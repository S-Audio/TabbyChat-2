package mnm.mods.tabbychat.extra.spell

import com.swabunga.spell.engine.SpellDictionaryHashMap
import java.io.BufferedReader
import java.io.IOException
import java.io.Reader
import java.io.UncheckedIOException

/**
 * Dictionary that supports `#` comments
 */
class UserDictionary
@Throws(IOException::class)
internal constructor(reader: Reader) : SpellDictionaryHashMap(reader) {

    @Throws(IOException::class)
    override fun createDictionary(reader: BufferedReader) {
        try {
            reader.lineSequence()
                    .filter { line -> line.startsWith("#") && line.trim { it <= ' ' }.isNotEmpty() }
                    .forEach { this.putWord(it) }
        } catch (e: UncheckedIOException) {
            throw e.cause!!
        }

    }
}
