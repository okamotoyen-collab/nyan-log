package com.example.nyanlog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class HomeController {

    private final RecordRepository recordRepository;

    public HomeController(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @GetMapping("/")
    public String index(
            @RequestParam(required = false) Long editId,
            Model model) {

        model.addAttribute("records", recordRepository.findAll());

        model.addAttribute("editId", editId);

        return "index";
    }

    @PostMapping("/add")
    public RedirectView addRecord(

            @RequestParam String careUser,
            @RequestParam String time,
            @RequestParam String category,
            @RequestParam String memo,
            @RequestParam("photo") MultipartFile photo) throws IOException {

        Record record = new Record();

        record.setCareUser(careUser);
        record.setTime(time);
        record.setCategory(category);
        record.setMemo(memo);

        if (!photo.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();

            Path uploadPath = Paths.get("uploads");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);

            Files.copy(photo.getInputStream(), filePath);

            record.setPhotoPath("/uploads/" + fileName);
        }

        recordRepository.save(record);

        return new RedirectView("/");
    }
    @PostMapping("/delete")
    public RedirectView deleteRecord(@RequestParam Long id) {

        recordRepository.deleteById(id);

        return new RedirectView("/");
    }
    @GetMapping("/edit")
    public String editRecord(@RequestParam Long id, Model model) {

        Record record = recordRepository.findById(id).orElse(null);

        model.addAttribute("record", record);

        return "edit";
    }
    @PostMapping("/update")
    public RedirectView updateRecord(

            @RequestParam Long id,
            @RequestParam String careUser,
            @RequestParam String time,
            @RequestParam String category,
            @RequestParam String memo) {

        Record record = recordRepository.findById(id).orElse(null);

        if (record != null) {

            record.setCareUser(careUser);
            record.setTime(time);
            record.setCategory(category);
            record.setMemo(memo);

            recordRepository.save(record);
        }

        return new RedirectView("/");
    }
}