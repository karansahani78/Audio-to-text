
# Audio-to-Text Converter

A **Java-based application** that converts audio files into text using **AssemblyAI’s speech-to-text API**. This project demonstrates real-time audio transcription using AssemblyAI services.

---

## 🔹 Features

* **Audio to Text Conversion** – Transcribes audio files into written text using AssemblyAI.
* **Supports Multiple Formats** – Handles WAV, MP3, and other common audio formats.
* **Easy Integration** – Can be used as a standalone tool or integrated into other Java projects.
* **Fast and Accurate** – Leverages AssemblyAI’s advanced speech recognition API.

---

## 🔹 Requirements

* **Java Development Kit (JDK)** 8 or higher
* **Maven** for dependency management and building the project
* **AssemblyAI API Key** – [Sign up here](https://www.assemblyai.com/) to get your free API key
* **Internet Connection** – Required to communicate with AssemblyAI API

---

## 🔹 Installation

1. **Clone the repository**

```bash
git clone https://github.com/karansahani78/Audio-to-text.git
cd Audio-to-text
```

2. **Configure API Key**

* Add your AssemblyAI API key as an environment variable:

```bash
export ASSEMBLYAI_API_KEY="your_api_key_here"
```

3. **Build the project using Maven**

```bash
mvn clean install
```

4. **Run the application**

```bash
mvn exec:java -Dexec.mainClass="com.karan.audiototext.Main"
```

> Replace `com.karan.audiototext.Main` with your actual main class if different.

---

## 🔹 Usage

1. Prepare your audio file (WAV, MP3, etc.).
2. Run the application as described above.
3. Enter the path to your audio file when prompted.
4. The transcribed text will be displayed in the console.

---

## 🔹 Contributing

Contributions are welcome!

1. Fork the repository
2. Create a branch (`git checkout -b feature/YourFeature`)
3. Make your changes
4. Commit your changes (`git commit -m 'Add feature'`)
5. Push to the branch (`git push origin feature/YourFeature`)
6. Open a Pull Request

---

## 🔹 License

This project is licensed under the **MIT License** – see the [LICENSE](LICENSE) file for details.
