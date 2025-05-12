package com.github.glitch752.vsccolors.settings

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.PlainTextSyntaxHighlighterFactory
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import com.intellij.openapi.util.NlsContexts
import javax.swing.Icon
import org.jetbrains.annotations.NonNls

internal class VSCColorSettings : ColorSettingsPage {
    override fun getIcon(): Icon? = null

    override fun getHighlighter(): SyntaxHighlighter =
        PlainTextSyntaxHighlighterFactory().getSyntaxHighlighter(null, null)

    override fun getDemoText(): @NonNls String =
        """<l1>(</l1><l2>(</l2><l3>(</l3><l3>)</l3><l2>)</l2><l1>)</l1>"""

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String?, TextAttributesKey?>? =
        mapOf(
            "l1" to TextAttributesKey.createTextAttributesKey("BRACKET_LEVEL_1"),
            "l2" to TextAttributesKey.createTextAttributesKey("BRACKET_LEVEL_2"),
            "l3" to TextAttributesKey.createTextAttributesKey("BRACKET_LEVEL_3"),
        )

    override fun getAttributeDescriptors(): Array<out AttributesDescriptor?> {
        val config = VSCColorsSettingsState.instance
        
        val keywordGroupColors = config.keywordGroups.map { (name, _) ->
            AttributesDescriptor("Keyword group: $name", TextAttributesKey.createTextAttributesKey("KEYWORD_HIGHLIGHT_$name"))
        }.toTypedArray()

        return arrayOf(
            AttributesDescriptor(
                "Bracket Level 1",
                TextAttributesKey.createTextAttributesKey("BRACKET_LEVEL_1"),
            ),
            AttributesDescriptor(
                "Bracket Level 2",
                TextAttributesKey.createTextAttributesKey("BRACKET_LEVEL_2"),
            ),
            AttributesDescriptor(
                "Bracket Level 3",
                TextAttributesKey.createTextAttributesKey("BRACKET_LEVEL_3"),
            ),
        ) + keywordGroupColors
    }

    override fun getColorDescriptors(): Array<out ColorDescriptor?> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName(): @NlsContexts.ConfigurableName String = "VSCode Colors"
}
