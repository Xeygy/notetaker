## Project Structure
The main extension code is stored inside 
`src/main/java/org/intellij/sdk/notetaker/`.  

Inside you'll find:
- `storage/`, which handles persistence for notes and saving/
representing Notetaker state 
- `visitors/`, which holds the visitors used for 
finding method declarations from links
- `window/`, which holds all the code relating to the UI

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
- essentially a singleton to get access to the stored note list
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
(i.e. [NoteModelStorageConverter](#### `NoteModelStorageConverter`))
