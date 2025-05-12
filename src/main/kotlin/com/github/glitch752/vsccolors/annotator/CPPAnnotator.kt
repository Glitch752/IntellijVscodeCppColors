package com.github.glitch752.vsccolors.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement

class CPPAnnotator : Annotator {
    private val bracketColorizer = BracketColorizer.withCustomBrackets(setOf("[", "{", "("), setOf("]", "}", ")"))
    private val keywordColorizer = KeywordColorizer.default()

    /**
     * This function is called by the plugin to annotate a given element of the PSI structure.
     *
     * @param element the element to annotate
     * @param holder the annotation holder to which color annotations are added
     */
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        bracketColorizer.colorize(element, holder)
        keywordColorizer.colorize(element, holder)
    }
}
