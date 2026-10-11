package com.plagiarism.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class TextExtractionService {

    public String extractText(MultipartFile file) throws IOException {

        String filename = file.getOriginalFilename();

        if (filename == null || !filename.contains(".")) {
            throw new IOException("Invalid file name");
        }

        String extension =
                filename.substring(filename.lastIndexOf(".") + 1)
                        .toLowerCase();

        switch (extension) {

            case "txt":
                return new String(file.getBytes(), StandardCharsets.UTF_8);

            case "pdf":
                return extractPdf(file);

            case "docx":
                return extractDocx(file);

            default:
                throw new IOException(
                        "Only PDF, DOCX and TXT files are supported"
                );
        }
    }

    private String extractPdf(MultipartFile file) throws IOException {

        try (PDDocument document = Loader.loadPDF(file.getBytes())) {

            PDFTextStripper stripper = new PDFTextStripper();

            return stripper.getText(document);
        }
    }

    private String extractDocx(MultipartFile file) throws IOException {

        try (XWPFDocument document = new XWPFDocument(file.getInputStream());
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {

            return extractor.getText();
        }
    }
}