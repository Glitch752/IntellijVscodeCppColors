package com.github.glitch752.vsccolors.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement

class KeywordColorizer private constructor() {
    companion object {
        fun default(): KeywordColorizer = KeywordColorizer()
    }

    fun colorize(element: PsiElement, holder: AnnotationHolder) {
        val text = element.text
        val config = VSCColorsSettingsState.instance
        if(text.isEmpty() || element !is LeafPsiElement) return

        val keywordColorMap = config.keywordColorMap
        val groupName = keywordColorMap[text]
        if(groupName != null) {
            val colorKey = TextAttributesKey.createTextAttributesKey("KEYWORD_HIGHLIGHT_$groupName")
            holder.newAnnotation(HighlightSeverity.TEXT_ATTRIBUTES, "")
                .range(element as PsiElement)
                .textAttributes(colorKey)
                .create()
            return
        }
    }
}
