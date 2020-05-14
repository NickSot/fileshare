package org.tues.fileshare.Controller;

import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.tues.fileshare.Entity.File;
import org.tues.fileshare.Entity.User;
import org.tues.fileshare.Service.IFileService;
import org.tues.fileshare.Service.IUserService;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@Controller
public class FileController {
    @Autowired
    private IFileService fileService;

    @Autowired
    private IUserService userService;
    private String STORAGE_PATH = "C://Users//NIKI//Desktop//fileshare//src//main//resources//static//storage//";

    @GetMapping("/user/{user_id}/file/{file_id}")
    public String displayFile(@PathVariable("user_id") long user_id, @PathVariable("file_id") long file_id,
                              Model model) {
        model.addAttribute("file", fileService.findById(file_id));
        model.addAttribute("user", userService.findById(user_id));

        return "show_file";
    }

    @GetMapping("/profile-directory/upload-file/**")
    public String showCreateFile(@RequestParam("path") String path, Model model){
        model.addAttribute("file", new File());

        return "upload_file";
    }

    @PostMapping("/profile-directory/upload-file/**")
    public String createFile(@RequestParam("filePath") String filePath,
                             @ModelAttribute File file,
                             @RequestParam("theFile") MultipartFile theFile,
                             HttpServletRequest request, Model model){
        Object username = request.getSession().getAttribute("username");

        if (username == null){
            return "redirect:/";
        }

        filePath = filePath.replace("path=", "");

        try {
            filePath = java.net.URLDecoder.decode(filePath, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

            return "redirect:/profile-directory/?path=" + username.toString();
        }


        model.addAttribute("theFile", theFile);
        model.addAttribute("file", file);

        file.setFilename(theFile.getOriginalFilename());
        file.setFilePath(filePath);
        file.setOwner(userService.findByUsername(username.toString()));

        try {
            byte [] fileBytes = theFile.getBytes();
            Path path = Paths.get(STORAGE_PATH + file.getFilePath() + "\\" + file.getFilename());
            Files.createFile(path);
            Files.write(path, fileBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/profile-directory/?path=" + filePath;
        }

        fileService.save(file);

        return "redirect:/profile-directory/?path=" + filePath;
    }

    @GetMapping("/profile-directory/create-folder/**")
    public String createFolder(@RequestParam("path") String path, HttpServletRequest request, Model model){
        Object un = request.getSession().getAttribute("username");

        if (un == null){
            return "redirect:/";
        }

        model.addAttribute("path", path);
        return "create_directory";
    }

    @PostMapping("/profile-directory/create-folder/**")
    public String createFolder(@RequestParam("path") String path,
                               @RequestParam("folderName") String folderName, Model model, HttpServletRequest request){
        Object un = request.getSession().getAttribute("username");

        if (un == null){
            return "redirect:/";
        }

        path = path.replace("path=", "");

        try {
            path = java.net.URLDecoder.decode(path, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        model.addAttribute("folderName", folderName);
        String destination = path + "/" + folderName;
        Path folderPath = Paths.get(STORAGE_PATH + destination);

        try {
            Files.createDirectory(folderPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/profile-directory/?path=" + destination;
    }

    @GetMapping("/profile-directory/**")
    public String navigateProfile(@RequestParam("path") String folderPath,
                                  Model model){

        if (folderPath == null || folderPath.equals("")){
            return "redirect:/";
        }

        String destination = folderPath.replace("src/main/resources/static/storage/", "");

//        if (folderPath.contains("src/main/resources/static/storage/")){
//            destination = folderPath;
//        }

        model.addAttribute("directory", destination);

        return "show_profile";
    }
}
