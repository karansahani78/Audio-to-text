package com.transcript.Audio.to.text.controller;

import com.transcript.Audio.to.text.service.AssemblyAIService;
import com.transcript.Audio.to.text.service.AssemblyAIService.TranscriptionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/transcription")
@RequiredArgsConstructor
public class TranscriptionController {

    private final AssemblyAIService assemblyAIService;

    /**
     * Upload an audio file and return its transcription result.
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> transcribeAudio(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No file uploaded"));
            }

            String contentType = file.getContentType();
            if (!isSupportedAudioType(contentType)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Unsupported file type"));
            }

            log.info("Received file: {}, type: {}, size: {} bytes",
                    file.getOriginalFilename(), contentType, file.getSize());

            byte[] audioBytes = file.getBytes();
            String transcription = assemblyAIService.transcribe(audioBytes);

            return ResponseEntity.ok(Map.of(
                    "filename", file.getOriginalFilename(),
                    "size", file.getSize(),
                    "transcription", transcription
            ));

        } catch (IOException e) {
            log.error("Failed to read file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error reading file"));
        } catch (TranscriptionException e) {
            log.error("Transcription failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected server error"));
        }
    }

    /**
     * Health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }

    private boolean isSupportedAudioType(String contentType) {
        return contentType != null && (
                contentType.startsWith("audio/") ||
                        contentType.equals("application/octet-stream") ||
                        contentType.equals("video/mp4") ||
                        contentType.equals("video/quicktime")
        );
    }
}
