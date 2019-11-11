package mnm.mods.tabbychat.client.gui.settings

import mnm.mods.tabbychat.client.gui.component.GuiPanel
import mnm.mods.tabbychat.util.config.SettingsFile

/**
 * Base class for a setting panel.
 */
abstract class SettingPanel<T : SettingsFile<T>> : GuiPanel() {

    abstract val displayString: String

    /**
     * Gets the [SettingsFile] used for this category. Used for loading
     * and saving the settings file.
     *
     * @return The settings
     */
    abstract val settings: T

    /**
     * Called when this category is activated and displayed.
     */
    open fun initGUI() {}

}
