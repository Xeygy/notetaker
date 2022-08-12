# Dev Docs
Last updated 8/12/22
## Quick Start
- Clone the project with IntelliJ and click 'Run Plugin' to try an instance of 
extension
- Want to **Persist Something?** — add it to 
[`NoteStorageState.java`](src/main/java/org/intellij/sdk/notetaker/storage/NoteStorageState.java)
and access it through an instance of 
[`NoteStorageManager.java`](src/main/java/org/intellij/sdk/notetaker/storage/NoteStorageManager.java)
- Want to **Add Text Editor Features?** — look at editing `NotePanel` inside 
[`NoteWindow.java`](src/main/java/org/intellij/sdk/notetaker/window/texteditor/NoteWindow.java)
- Want to **Extend Linking Features?** — look at `autoLink()` in 
[`NoteDocumentListener.java`](src/main/java/org/intellij/sdk/notetaker/window/texteditor/NoteDocumentListener.java)
and creating your own visitors.

## Project Structure
The main extension code is stored inside 
`src/main/java/org/intellij/sdk/notetaker/`.  

Inside you'll find:
- `storage/`, which handles persistence for notes and saving/
representing Notetaker state 
- `visitors/`, which holds the visitors used for 
finding method declarations from links
- `window/`, which holds all the code relating to the UI

## Docs
### Storage
#### `NoteModel`
- is the model of a note stored in the system
- contains `name` and `content`
  - `name` is a string representing the note name
  - `content` is the HTML representation of the file
#### `NoteModelStorageConverter` & `NoteModelStorage`
- because IntelliJ Persistent State Components do not support
persisting custom Objects by default, we need to write a converter 
to store our `NoteModels`
- `NoteModelStorageConverter` uses [GSON](https://github.com/google/gson) 
to serialize `NoteModelStorage`.
- `NoteModelStorage` is a class that only contains `List<NoteModel>`
  - There is no reason to have NoteModelStorage, as GSON can serialize 
  lists as well as objects

#### `NoteStorageManager`
- is the interface by which we interact with storage
- a class to get access to the stored note list
or stored open tabs

#### `NoteStorageState` 
- currently stores all notes and all open notes
- is a [Persistent State Component](https://plugins.jetbrains.com/docs/intellij/persisting-state-of-components.html),
which is an interface that tells IntelliJ what you want to persist
between IDE Restarts
- the interface supports:
  - numbers (both primitive types, such as int, and boxed types, such as Integer)
  - booleans
  - strings
  - collections
  - maps
  - enums
- to support persistence of other classes, you need to write a converter 
(i.e. [NoteModelStorageConverter](#notemodelstorageconverter--notemodelstorage))

### Visitors
To search for through the entire project, you need to use `Processors` and
`Visitors`. `Processors` traverse through the project structure and process
files. `Visitors` traversw through individual files' Abstract Syntax Trees. 

[**PSI**](https://plugins.jetbrains.com/docs/intellij/psi.html) is IntelliJ's way 
of [providing an AST](https://groups.google.com/g/lint-dev/c/ss18JfF7_hk/m/G8v_QLzdBQAJ)
representation to the user.

#### `FindIndividualMethodVisitor` & `FindIndividualMethodProcessor`
- Finds a method based on its method signature: enclosingClass, methodName, & params.
- Used to navigate unambiguously to the right method specified by a link.

#### `FindMethodVisitor` & `FindMethodProcessor`
- Finds methods whose names start with a given prefix
- Used for autocomplete

### Window
There are two windows in notetaker, a note index (file system manager) 
and a text editor for actual typing.

#### Note Index
##### `NoteIndexToolWindowFactory`
- ToolWindowFactory is the interface that IntelliJ uses to show the tool window.
- ["When the user clicks on the tool window button, the createToolWindowContent() 
method of the factory class is called"](https://plugins.jetbrains.com/docs/intellij/tool-windows.html#programmatic-setup)

##### `NoteIndexWindow`
- Java Swing UI to show all stored notes.
- Uses a [ToolbarDecorator](https://plugins.jetbrains.com/docs/intellij/lists-and-trees.html#toolbardecorator)
for the add/delete/edit actions
- Actions implemented in `ViewManager`

##### `SetNoteNameDialog`
- Creates a popup dialog that asks for a text input, used for setting note names.

##### `ViewController`
- Responsible for coordinating the view between the text editor and the note index when
notes are added/removed/renamed

#### Text Editor
##### `CustomHTMLEditorKit`
- An extension of the built in HTMLEditorKit that allows hyperlinks to
be clickable with double-click while JTextPane is editable

##### `MethodWrapper`
- A utility class around `PsiMethod` for getting the method signature and
navigating to method

##### `NoteDocumentListener`
- Responsible for:
  - Saving the note every insert
  - Checking to see if the user wants to insert a link with \{

###### `NoteToolWindowFactory`
- Responsible for showing all open tabs at the time
- Contains a `ContentManagerListener` which updates which notes are
open in the tab.

###### `NoteWindow`
- Handles creating the UI for the text editor and styling a given `NoteModel`
- Handles link clicking and link escaping