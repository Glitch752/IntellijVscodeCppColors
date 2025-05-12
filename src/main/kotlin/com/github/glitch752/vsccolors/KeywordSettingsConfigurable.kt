package com.github.glitch752.vsccolors

import com.github.glitch752.vsccolors.settings.KeywordSettingsComponent
import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class KeywordSettingsConfigurable : Configurable {
    private var component: KeywordSettingsComponent? = null

    override fun createComponent(): JComponent {
        component = KeywordSettingsComponent()
        return component!!.panel
    }

    override fun isModified(): Boolean {
        val config = VSCColorsSettingsState.instance
        return component!!.getData() != config.keywordGroups
    }

    override fun apply() {
        val config = VSCColorsSettingsState.instance
        config.keywordGroups = component!!.getData()
    }

    override fun reset() {
        val config = VSCColorsSettingsState.instance
        component!!.setData(config.keywordGroups)
    }

    override fun getDisplayName() = "VSCode Colors"
}