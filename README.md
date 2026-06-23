# LazyTranscriber 🎙️

A native Android application built in Java that converts spoken input into reusable text for situations where typing is inconvenient but voice notes are not appropriate.

The application supports both English and Hinglish transcription, automatically copies recognized text to the clipboard, and provides a clean Material Design interface with dark mode support.

---

## Why I Built This

There are many situations where speaking is faster than typing, but sending a voice message is not always practical.

I built LazyTranscriber as a lightweight Android utility that allows users to dictate text and instantly reuse it anywhere through the clipboard.

The project started as a personal productivity tool and evolved into an exploration of Android speech recognition, UI design, runtime permissions, device compatibility, and dark mode implementation.

---

## Features

* English speech-to-text transcription
* Hinglish transcription support
* Automatic clipboard copy after transcription
* Manual copy and clear actions
* Material Design user interface
* Dark mode support
* Runtime microphone permission handling
* Real-device tested implementation
* Custom launcher icon (Lazy Panda)

---

## Tech Stack

* Java
* Android SDK
* Android SpeechRecognizer API
* Material Components
* ConstraintLayout
* Gradle

---

## Screenshots

### Light Mode

![Light Mode](screenshots/home-light.png)

### Dark Mode

![Dark Mode](screenshots/home-dark.png)

### Listening State

![Listening](screenshots/listening.png)

### Example Transcription

![Transcription](screenshots/transcription.png)

---

## Technical Challenges Solved

### SpeechRecognizer Lifecycle Management

A key challenge was handling repeated speech recognition sessions reliably. Certain devices do not restart recognition sessions correctly after stopping.

This was resolved by carefully managing the SpeechRecognizer lifecycle and recreating recognition sessions when necessary.

### Dark Mode Implementation

Implementing system-driven dark mode required understanding Android themes, resource qualifiers, and proper separation of light and dark resources.

### Real Device Testing

The Android emulator introduced performance and stability issues during development. The application was ultimately tested and refined using a physical Android device.

---

## What I Learned

* Android application architecture fundamentals
* Runtime permissions
* Speech recognition APIs
* Material Design principles
* Resource management and theming
* Debugging on real Android devices
* UI/UX iteration and refinement

---

## Future Improvements

* Offline transcription models
* Export/share functionality
* History of previous transcriptions
* Multiple language support
* Home-screen widget integration

---

## License

MIT License
