package org.tues.fileshare.Controller;

import org.apache.tomcat.util.http.fileupload.IOUtils;
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
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    @GetMapping("/profile-directory/unshare-file/{file_id}")
    public String unshareFile(@PathVariable("file_id") long file_id,
                              HttpServletRequest request){
        Object un = request.getSession().getAttribute("username");

        if (un == null || !un.toString().equals(fileService.findById(file_id).get().getOwner().getUsername())){
            return "redirect:/";
        }

        File file = fileService.findById(file_id).get();
        file.getSharedWith().forEach(u -> {
            u.getFilesSharedTo().remove(file);
            userService.save(u);
        });
        file.getSharedWith().clear();
        fileService.save(file);

        return "redirect:/profile-directory/?path=" + un.toString() + "/";
    }

    @GetMapping("/profile-directory/rename-file/{file_id}/")
    public String renameFileShow(@PathVariable("file_id") long file_id, Model model){

        model.addAttribute("file_id", file_id);
        return "rename_file";
    }

    @PostMapping("/profile-directory/rename-file/{file_id}/")
    public String renameFile(@PathVariable("file_id") long file_id, @RequestParam("new_filename") String new_filename, HttpServletRequest request){
        File file = fileService.findById(file_id).get();
        java.io.File f = new java.io.File(STORAGE_PATH + '/' + file.getFilePath() + '/' + file.getFilename());
        java.io.File f1 = new java.io.File(STORAGE_PATH + '/' + file.getFilePath() + '/' + new_filename);

        try {
            Files.move(f.toPath(), f1.toPath());
            file.setFilename(new_filename);
            fileService.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @GetMapping("/profile-directory/delete-file/{file_id}/")
    public String deleteFile(@PathVariable("file_id") long file_id, HttpServletRequest request){
        File file = fileService.findById(file_id).get();

        try {
            Files.delete(Paths.get(STORAGE_PATH + '/' + file.getFilePath() + '/' + file.getFilename()));
            fileService.delete(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @GetMapping("/profile-directory/{user_id}/file/**")
    public String displayFile(@PathVariable("user_id") long user_id, @RequestParam("path") String path,
                              Model model, HttpServletRequest request) {
        Object username = request.getSession().getAttribute("username");

        if (username == null || userService.findById(user_id).get() != userService.findByUsername(username.toString())){
            return "redirect:/";
        }

        try {
            path = java.net.URLDecoder.decode(path, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

            return "redirect:/profile-directory/?path=" + username.toString() + "/";
        }

        String[] pathComponents = path.split("/");

        File file = fileService.findByNameAndPath(
                pathComponents[pathComponents.length - 1],
                path.replace("/" + pathComponents[pathComponents.length - 1], ""));

        model.addAttribute("user", userService.findByUsername(username.toString()));
        model.addAttribute("file", file);

        return "show_file";
    }

    @PostMapping("/profile-directory/share-file/{file_id}/")
    public String shareFile(@PathVariable("file_id") long file_id, @RequestParam("username") String username,
                            HttpServletRequest request){
        Object un = request.getSession().getAttribute("username");

        if (un == null){
            return "redirect:/";
        }

        User user = userService.findByUsername(username);
        File file = fileService.findById(file_id).get();

        user.getFilesSharedTo().add(file);
        file.getSharedWith().add(user);
        userService.save(user);
        fileService.save(file);

        return "redirect:/profile-directory/" + user.getId() + "/file/?path=" + file.getFilePath() + "/" + file.getFilename();
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

            return "redirect:/profile-directory/?path=" + username.toString() + "/";
        }

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
        User user = userService.findByUsername(username.toString());
        user.getFiles().add(file);
        userService.save(user);

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
                                  Model model, HttpServletRequest request){
        Object un = request.getSession().getAttribute("username");

        if (un == null){
            return "redirect:/";
        }

        if (folderPath == null || folderPath.equals("")){
            return "redirect:/";
        }

        String destination = folderPath.replace("src/main/resources/static/storage/", "");

        model.addAttribute("directory", destination);
        model.addAttribute("user_id", userService.findByUsername(un.toString()).getId());
        model.addAttribute("fileService", fileService);

        System.out.println(destination);
        System.out.println(new java.io.File(destination).list());

        return "show_profile";
    }

    @GetMapping("profile-directory/file/{file_id}/download")
    public void download(@PathVariable("file_id") long file_id,
                           HttpServletRequest request, HttpServletResponse response){
        try {
            File f = fileService.findById(file_id).get();
            java.io.File file = new java.io.File(STORAGE_PATH + "/" + f.getFilePath() + "/" + f.getFilename());

            System.out.println(STORAGE_PATH + "/" + f.getFilePath() + "/" + f.getFilename());

            InputStream is = new FileInputStream(file);

            IOUtils.copy(is, response.getOutputStream());
            is.close();
            response.flushBuffer();
        } catch (IOException ex) {
            throw new RuntimeException("IOError writing file to output stream");
        }
    }
}
