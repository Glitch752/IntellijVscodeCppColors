# VSCode colors
<!-- Plugin description -->
Makes IntelliJ IDEs color code like VSCode, including:
- Customizable bracket pair colors
- Customizable per-keyword colors

Currently, this plugin only support C++ and C#. I may add support for other languages in the
future.
<!-- Plugin description end -->

## Installation
Currently, I'm not publishing builds since this plugin is a work in progress. You can
build the plugin from source using the following steps:

Clone the repository:
```bash
git clone https://github.com/Glitch752/IntellijVscodeCppColors/
```

Build the plugin:
```bash
./gradlew buildPlugin
```

This will create a zip file in the `build/distributions` directory. You can install the plugin by
going to `File > Settings > Plugins > Install Plugin from Disk...` and selecting the zip file.

This plugin is developed primarily for my own personal use, so I may not support it very well. Feel free to
contribute if you want to fix something or add a feature.

## Acknowledgements

This plugin is derived from [brackets](https://github.com/j-d-ha/brackets), which in itself is based on
the [intellij-rainbow-brackets](https://github.com/izhangzhihao/intellij-rainbow-brackets) JetBrains
plugin and [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template).
It is licensed under GPL 3, so the same license is used here.
