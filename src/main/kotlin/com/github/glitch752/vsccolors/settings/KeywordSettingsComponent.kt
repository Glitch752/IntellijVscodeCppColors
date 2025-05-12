package com.github.glitch752.vsccolors.settings

import KeywordGroup
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.TextAttributesKey
import java.awt.*
import java.awt.event.KeyEvent
import javax.swing.*
import javax.swing.event.TreeModelEvent
import javax.swing.event.TreeModelListener
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreePath
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.BorderFactory
import java.awt.FlowLayout
import java.awt.Dimension

class KeywordSettingsComponent {
    val panel: JPanel

    private val root = DefaultMutableTreeNode("root")
    private val treeModel = DefaultTreeModel(root)
    private val tree = JTree(treeModel).apply {
        isRootVisible = false
    }

    private val keywordGroups = mutableMapOf<String, KeywordGroup>()

    init {
        tree.cellRenderer = object : DefaultTreeCellRenderer() {
            override fun getTreeCellRendererComponent(
                tree: JTree?,
                value: Any?,
                selected: Boolean,
                expanded: Boolean,
                leaf: Boolean,
                row: Int,
                hasFocus: Boolean
            ): Component {
                backgroundNonSelectionColor = Color(0, 0, 0, 0)
                backgroundSelectionColor = Color(0, 0, 0, 0)

                val comp = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus)
                val node = value as? DefaultMutableTreeNode ?: return comp

                val userObj = node.userObject
                if (node.parent == root && userObj is String) {
                    val color = EditorColorsManager.getInstance()
                        .globalScheme
                        .getAttributes(TextAttributesKey.createTextAttributesKey("KEYWORD_HIGHLIGHT_$userObj"))
                        ?.foregroundColor ?: Color.GRAY

                    val icon = object : Icon {
                        override fun getIconWidth() = 10
                        override fun getIconHeight() = 10
                        override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
                            g.color = color
                            g.fillRect(x, y, iconWidth, iconHeight)
                            g.color = Color.BLACK
                            g.drawRect(x, y, iconWidth - 1, iconHeight - 1)
                        }
                    }

                    icon.also { this.icon = it }
                } else {
                    this.icon = null
                }
                return comp
            }
        }

        tree.isEditable = true
        tree.cellEditor = DefaultCellEditor(JTextField()).apply {
            clickCountToStart = 2  // Double-click to edit
        }

        tree.registerKeyboardAction(
            { removeSelectedNode() },
            KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
            JComponent.WHEN_FOCUSED
        )

        tree.registerKeyboardAction(
            { startRenameSelectedNode() },
            KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0),
            JComponent.WHEN_FOCUSED
        )

        treeModel.addTreeModelListener(object : TreeModelListener {
            override fun treeNodesChanged(e: TreeModelEvent) {
                val node = e.treePath.lastPathComponent as DefaultMutableTreeNode
                val childIndices = e.childIndices

                if (childIndices != null && childIndices.isNotEmpty()) {
                    val changedNode = node.getChildAt(childIndices[0]) as DefaultMutableTreeNode
                    val userObj = changedNode.userObject.toString()

                    // Node is a category (child of root)
                    if (node === root) {
                        val oldName = e.children[0].toString()
                        if (oldName != userObj && !keywordGroups.containsKey(userObj)) {
                            val group = keywordGroups.remove(oldName) ?: return
                            keywordGroups[userObj] = group
                        }
                    }
                    // Node is a keyword
                    else {
                        val groupName = node.userObject.toString()
                        val oldKeyword = e.children[0].toString()
                        if (oldKeyword != userObj) {
                            keywordGroups[groupName]?.let {
                                it.keywords = it.keywords - oldKeyword + userObj
                            }
                        }
                    }
                }
            }

            override fun treeNodesInserted(e: TreeModelEvent) {}
            override fun treeNodesRemoved(e: TreeModelEvent) {}
            override fun treeStructureChanged(e: TreeModelEvent) {}
        })

        val treeScroll = JScrollPane(tree)

        val addCategoryButton = JButton("Add Category").apply {
            addActionListener {
                val name = "New Category"  // Default placeholder name
                val group = DefaultMutableTreeNode(name)
                treeModel.insertNodeInto(group, root, root.childCount)
                keywordGroups[name] = KeywordGroup(emptySet())
                treeModel.reload()
                expandAllRows()

                // Start editing the new node
                SwingUtilities.invokeLater {
                    val path = TreePath(treeModel.getPathToRoot(group))
                    tree.selectionPath = path
                    tree.startEditingAtPath(path)
                }
            }
        }

        val addKeywordButton = JButton("Add Keyword").apply {
            addActionListener {
                val groupName = getSelectedGroupName() ?: return@addActionListener
                val keyword = "New Keyword"  // Default placeholder
                val groupNode = getSelectedGroupNode() ?: return@addActionListener
                val keywordNode = DefaultMutableTreeNode(keyword)
                treeModel.insertNodeInto(keywordNode, groupNode, groupNode.childCount)
                keywordGroups[groupName]?.keywords =
                    keywordGroups[groupName]!!.keywords + keyword
                expandAllRows()

                // Start editing the new node
                SwingUtilities.invokeLater {
                    val path = TreePath(treeModel.getPathToRoot(keywordNode))
                    tree.selectionPath = path
                    tree.startEditingAtPath(path)
                }
            }
        }

        val removeButton = JButton("Remove Selected").apply {
            addActionListener {
                val path = tree.selectionPath ?: return@addActionListener
                val node = path.lastPathComponent as DefaultMutableTreeNode
                val parent = node.parent as? DefaultMutableTreeNode ?: return@addActionListener

                when {
                    parent === root -> {
                        val name = node.userObject as String
                        keywordGroups.remove(name)
                        treeModel.removeNodeFromParent(node)
                    }
                    else -> {
                        val groupName = parent.userObject as String
                        val keyword = node.userObject as String
                        keywordGroups[groupName]?.keywords =
                            keywordGroups[groupName]?.keywords?.minus(keyword) ?: emptySet()
                        treeModel.removeNodeFromParent(node)
                    }
                }
            }
        }

        tree.componentPopupMenu = JPopupMenu().apply {
            // Rename item - updated to use inline editing
            val editItem = JMenuItem("Rename").apply {
                addActionListener {
                    val path = tree.selectionPath ?: return@addActionListener
                    tree.startEditingAtPath(path)  // Use inline editing instead of dialog
                }
            }
            add(editItem)

            // Add remove item
            val removeItem = JMenuItem("Remove").apply {
                addActionListener {
                    removeSelectedNode()
                }
            }
            add(removeItem)
        }

        tree.addTreeSelectionListener {
            val groupName = getSelectedGroupName()
        }

        val infoIcon = JLabel("ⓘ");
        infoIcon.toolTipText = """
        <html>
            <b>Shortcuts:</b>
            <ul>
                <li>F2: Rename selected item</li>
                <li>DEL: Remove selected item</li>
                <li>Double-click: Edit item</li>
            </ul>
            <b>Color Configuration:</b>
            <ul>
                <li>Category colors are configured under <pre>Settings → Editor → Color Scheme → VSCode Colors</pre></li>
                <li>You must save before you can edit category colors. This is slightly annoying, but it's the easiest
                way to implement this feature.</li>
            </ul>
        </html>
        """.trimIndent()
        infoIcon.font = infoIcon.font.deriveFont(Font.BOLD, 22f)
        infoIcon.border = BorderFactory.createEmptyBorder(0, 4, 0, 0)

        val controlPanel = JPanel(BorderLayout()).apply {
            val buttonPanel = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
                add(addCategoryButton)
                add(addKeywordButton)
                add(removeButton)
            }
            add(buttonPanel, BorderLayout.WEST)

            val infoPanel = JPanel(FlowLayout(FlowLayout.RIGHT)).apply {
                add(infoIcon)
            }
            add(infoPanel, BorderLayout.EAST)
        }

        panel = JPanel(BorderLayout()).apply {
            add(treeScroll, BorderLayout.CENTER)
            add(controlPanel, BorderLayout.NORTH)
        }
    }

    private fun removeSelectedNode() {
        val path = tree.selectionPath ?: return
        val node = path.lastPathComponent as DefaultMutableTreeNode
        val parent = node.parent as? DefaultMutableTreeNode ?: return

        when {
            parent === root -> {
                val name = node.userObject as String
                keywordGroups.remove(name)
                treeModel.removeNodeFromParent(node)
            }
            else -> {
                val groupName = parent.userObject as String
                val keyword = node.userObject as String
                keywordGroups[groupName]?.keywords =
                    keywordGroups[groupName]?.keywords?.minus(keyword) ?: emptySet()
                treeModel.removeNodeFromParent(node)
            }
        }
    }

    private fun startRenameSelectedNode() {
        val path = tree.selectionPath ?: return
        tree.startEditingAtPath(path)
    }

    private fun expandAllRows() {
        SwingUtilities.invokeLater {
            expandNode(root)
        }
    }

    private fun expandNode(node: DefaultMutableTreeNode) {
        val path = TreePath(node.path)
        tree.expandPath(path)

        for (i in 0 until node.childCount) {
            expandNode(node.getChildAt(i) as DefaultMutableTreeNode)
        }
    }

    private fun getSelectedGroupNode(): DefaultMutableTreeNode? {
        val path = tree.selectionPath ?: return null
        val node = path.lastPathComponent as DefaultMutableTreeNode
        return if (node.parent == root) node
        else node.parent as? DefaultMutableTreeNode
    }

    private fun getSelectedGroupName(): String? {
        val node = getSelectedGroupNode() ?: return null
        return node.userObject as? String
    }

    fun getData(): Map<String, KeywordGroup> = keywordGroups.toMap()

    fun setData(data: Map<String, KeywordGroup>) {
        keywordGroups.clear()
        keywordGroups.putAll(data)
        root.removeAllChildren()
        data.forEach { (name, group) ->
            val groupNode = DefaultMutableTreeNode(name)
            group.keywords.forEach { keyword ->
                groupNode.add(DefaultMutableTreeNode(keyword))
            }
            root.add(groupNode)
        }
        treeModel.reload()

        expandAllRows()
    }
}