package org.ssp.notification.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CsvUploadController {
    @GetMapping({"/","/upload"})
    public String uploadPage() {
        return "upload";
    }
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
        List<String[]> csvData = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                csvData.add(line.split(","));
            }
        } catch (Exception e) {
            model.addAttribute("message", "File upload failed: " + e.getMessage());
            return "upload";
        }
        model.addAttribute("csvData", csvData);
        return "result"; // Redirect to a result page to display CSV data
    }
}