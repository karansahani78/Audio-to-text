package com.transcript.Audio.to.text.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
public class AssemblyAIService {

    private final WebClient webClient;

    @Value("${assembly.api.key}")
    private String apiKey;

    @Value("${assembly.api.base-url}")
    private String baseUrl;

    public AssemblyAIService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @PostConstruct
    public void logKeyOnce() {
        log.info("Using AssemblyAI API Key: {}", apiKey != null ? "Provided ✅" : "Missing ❌");
    }

    public String transcribe(byte[] audioData) {
        try {
            // Step 1: Upload the audio file
            log.info("Uploading audio...");
            Map<String, Object> uploadResponse = webClient.post()
                    .uri(baseUrl + "/upload")
                    .header(HttpHeaders.AUTHORIZATION, apiKey)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .bodyValue(audioData)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (uploadResponse == null || !uploadResponse.containsKey("upload_url")) {
                throw new TranscriptionException("Upload failed or no upload_url in response");
            }

            String uploadUrl = (String) uploadResponse.get("upload_url");
            log.info("Upload complete. URL: {}", uploadUrl);

            // Step 2: Request transcription
            Map<String, Object> transcriptRequest = Map.of("audio_url", uploadUrl);
            Map<String, Object> transcriptResponse = webClient.post()
                    .uri(baseUrl + "/transcript")
                    .header(HttpHeaders.AUTHORIZATION, apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(transcriptRequest)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (transcriptResponse == null || !transcriptResponse.containsKey("id")) {
                throw new TranscriptionException("Transcription request failed");
            }

            String transcriptId = (String) transcriptResponse.get("id");
            log.info("Transcription started. ID: {}", transcriptId);

            // Step 3: Poll until transcription is complete
            String transcriptionText = pollTranscriptionResult(transcriptId);
            log.info("Transcription complete: {}", transcriptionText);

            return transcriptionText;

        } catch (Exception e) {
            throw new TranscriptionException("Failed to transcribe audio: " + e.getMessage());
        }
    }

    private String pollTranscriptionResult(String transcriptId) throws InterruptedException {
        String url = baseUrl + "/transcript/" + transcriptId;

        for (int i = 0; i < 30; i++) { // max 30 attempts (~60s)
            Map<String, Object> statusResponse = webClient.get()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, apiKey)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (statusResponse == null) continue;

            String status = (String) statusResponse.get("status");

            if ("completed".equals(status)) {
                return (String) statusResponse.get("text");
            } else if ("error".equals(status)) {
                throw new TranscriptionException("Transcription failed: " + statusResponse.get("error"));
            }

            Thread.sleep(2000); // wait 2s
        }

        throw new TranscriptionException("Timed out waiting for transcription to complete");
    }

    public static class TranscriptionException extends RuntimeException {
        public TranscriptionException(String message) {
            super(message);
        }
    }
}
