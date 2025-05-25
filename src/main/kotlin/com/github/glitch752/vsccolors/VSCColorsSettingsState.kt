import com.intellij.openapi.components.SerializablePersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.immutableMapOf

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
            // Update the keyword color map
            keywordColorMap = getKeywordColorMap(value)
        }

    private fun getKeywordColorMap(keywordsWithColors: Map<String, KeywordGroup>): Map<String, String> {
        val keywordColorMapBuilder = mutableMapOf<String, String>()
        for ((groupName, group) in keywordsWithColors) {
            for (keyword in group.keywords) {
                keywordColorMapBuilder[keyword] = groupName
            }
        }
        return keywordColorMapBuilder.toMap()
    }

    var keywordColorMap: Map<String, String> = mapOf()

    // Once the state is loaded, initialize the keyword color map
    override fun initializeComponent() {
        super.initializeComponent()

        keywordColorMap = getKeywordColorMap(state.keywordsWithColors)
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