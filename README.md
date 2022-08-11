# notetaker

![Build](https://github.com/Xeygy/notetaker/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)

<!-- Plugin description -->
Notetaker is an IntelliJ extension which allows you to take 
notes about your IntelliJ project. It's a custom notepad that
could work as a scratchpad for observations, help you consolidate information 
about a project, or work as a personal to-do list.

Notes in notetaker allow you to link to method declarations for easy
reference right from your note.

<!-- Plugin description end -->
## Getting Started
Notetaker is divided into two tabs, a text editing window and a
note management window. The text editing window is used for writing 
your notes. The note management window allows you to create, rename, 
and delete notes.

### Creating a note
Go to the "Notes" tab and click on the plus (+) 
sign or use Ctrl/Cmd + N to add a new note to Notetaker.

### Opening an existing note
In the "Notes" tab, double-click on the note you want to open.

### Renaming a note
In the "Notes" tab, select the note you want to rename and click the Edit
icon (or press Enter) to rename your note.

### Deleting a note
In the Notes tab, click on the minus symbol or use Ctrl/Cmd + Del to
delete your note.

### Linking to a method declaration
In the text editor tab, type `\{methodName}`, where `methodName` is
the name of the method and select the specific method declaration
from the autocomplete popup with **Space**.

To go to the declaration, **double-click** on the link.

## Installation
- To get a build of the extension:
  - Run intellij/buildPlugin using Gradle
  - Find the plugin .zip under build/distributions/ (should be notetaker-0.0.1.zip or similar)
  - Install the .zip manually using<kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>
- To test out the extension:
  - clone the repo in IntelliJ and run the project

---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
