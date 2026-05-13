package com.example.nyanlog;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

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

            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", System.getenv("CLOUDINARY_CLOUD_NAME"),
                    "api_key", System.getenv("CLOUDINARY_API_KEY"),
                    "api_secret", System.getenv("CLOUDINARY_API_SECRET")
            ));

            Map uploadResult = cloudinary.uploader().upload(
                    photo.getBytes(),
                    ObjectUtils.asMap("folder", "nyan-log")
            );

            String imageUrl = uploadResult.get("secure_url").toString();

            record.setPhotoPath(imageUrl);
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
