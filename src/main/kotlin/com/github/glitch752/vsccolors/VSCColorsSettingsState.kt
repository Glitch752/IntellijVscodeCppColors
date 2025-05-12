import com.intellij.openapi.components.SerializablePersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

/**
 * Data for a keyword group. The keyword colors are handled separately in the color settings.
 */
data class KeywordGroup(
    var keywords: Set<String> = emptySet()
)

@Service
@State(
    name = "VSCColorsSettings",
    storages = [Storage("VSCColorsSettings.xml")]
)
class VSCColorsSettingsState : SerializablePersistentStateComponent<VSCColorsSettingsState.State>(State()) {
    var keywordGroups: Map<String, KeywordGroup>
        get() = state.keywordsWithColors
        set(value) {
            updateState {
                it.copy(keywordsWithColors = value)
            }
        }

    data class State (
        @JvmField var keywordsWithColors: Map<String, KeywordGroup> = mapOf() // keyword -> hex color
    )

    companion object {
        val instance: VSCColorsSettingsState
            get() {
                return service<VSCColorsSettingsState>()
            }
    }
}