package com.example.chapter6.board.controller;

import com.example.chapter6.Util.MediaUtil;
import com.example.chapter6.file.FileMapService;
import com.example.chapter6.file.UploadFileService;
import com.example.chapter6.model.*;
import com.example.chapter6.payload.response.ApiResponse;
import com.example.chapter6.service.BoardService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/board")
public class BoardController {

    private static final Logger logger = LoggerFactory.getLogger(BoardController.class);

    private BoardService boardService;
    private UploadFileService uploadFileService;
    private FileMapService fileMapService;

    public BoardController(BoardService boardService, UploadFileService uploadFileService, FileMapService fileMapService) {
        this.boardService = boardService;
        this.uploadFileService = uploadFileService;
        this.fileMapService = fileMapService;
    }

    @RequestMapping("/list")
    public String boardList(
            @ModelAttribute SearchHelper searchHelper,
            Model model
    ) throws Exception {

        HashMap<String, Object> result = boardService.selectBoardVO(searchHelper);

        logger.info(searchHelper.toString());

        model.addAttribute("searchHelper", result.get("searchHelper"));
        model.addAttribute("result", result.get("list"));

        return "board/list";
    }

    @RequestMapping("/write")
    public String boardWrite(
            Model model,
            @ModelAttribute SearchHelper searchHelper
    ) {
        BoardVO boardVO = new BoardVO();
        boardVO.setCode(1000);
        model.addAttribute("boardVO", boardVO);
        model.addAttribute("searchHelper", searchHelper);
        return "board/write";
    }

    @RequestMapping("/modify")
    public String boardModify(
            @RequestParam(value = "id", defaultValue = "0") int id,
            @ModelAttribute SearchHelper searchHelper,
            Model model
    ) throws Exception {
        if(id > 0) {
            // ????????? ??????
            Optional<BoardVO> boardVO = boardService.selectBoardVOById(id);
            model.addAttribute("boardVO", boardVO.get());
            model.addAttribute("searchHelper", searchHelper);

            if (boardVO.isPresent()) {
                List<UploadFileVO> fileList = uploadFileService.selectFileByBoardId(boardVO.get().getId());
                model.addAttribute("uploadFileVO", fileList);
            } else {
                model.addAttribute("uploadFileVO", null);
            }
        } else {
            Message message = new Message();
            message.setMessage("???????????? ????????????.");
            message.setHref("/board/list?srchCode=" + searchHelper.getSrchCode() + "&srchType=" + searchHelper.getSrchType() + "&srchKeyword=" + searchHelper.getSrchKeyword());
            model.addAttribute("data", message);
            model.addAttribute("searchHelper", searchHelper);
        }

        return "board/write";
    }

    @RequestMapping("/view")
    public String boardView(
            @RequestParam(value = "id", defaultValue = "0") int id,
            @ModelAttribute SearchHelper searchHelper,
            Model model
    ) throws Exception {

        if(id > 0) {
            // ????????? ??????
            Optional<BoardVO> boardVO = boardService.selectBoardVOById(id);
            model.addAttribute("boardVO", boardVO.get());
            model.addAttribute("searchHelper", searchHelper);

            logger.info(boardVO.toString());

            if (boardVO.isPresent()) {
                List<UploadFileVO> fileList = uploadFileService.selectFileByBoardId(boardVO.get().getId());
                model.addAttribute("uploadFileVO", fileList);
            } else {
                model.addAttribute("uploadFileVO", null);
            }
        } else {
            Message message = new Message();
            message.setMessage("???????????? ????????????.");
            message.setHref("/board/list");
            model.addAttribute("data", message);
            model.addAttribute("searchHelper", searchHelper);
            return "message/message";
        }

        return "board/view";
    }

    @PostMapping("/save")
    public String boardSave(
            @ModelAttribute BoardVO boardVO,
            @RequestParam("file") List<MultipartFile> multipartFile,
            HttpServletRequest request,
            Model model
            ) throws Exception {

        HttpSession session = request.getSession();
        MemberVO sessionResult = (MemberVO) session.getAttribute("memberVO");

        if (sessionResult != null) {
            // ??????
            String userId = sessionResult.getUserId();

            //code, title, content, userId
            boardVO.setRegId(userId);

            if (boardVO.getId() > 0) {
                // ??????
                String regId = boardVO.getRegId();
                boardService.updateBoardVO(boardVO);
            } else {
                // ??????
                boardService.insertBoardVO(boardVO);
            }

            for(int i = 0; i < multipartFile.size(); i++) {
                UploadFileVO uploadFileVO = uploadFileService.store(multipartFile.get(i));
                logger.info("uploadFileVO - {}", uploadFileVO);
                FileMapVO fileMapVO = new FileMapVO();
                fileMapVO.setFileId(uploadFileVO.getId());
                fileMapVO.setBoardId(boardVO.getId());
                fileMapService.insertFileMap(fileMapVO);
            }
        } else {
            // ?????? ??????
            model.addAttribute("data", new Message("????????? ??? ???????????????.", "/member/login"));
            return "message/message";
        }

        return "redirect:/board/list";
    }

    /**
     * ?????? ????????? ????????? ??????
     * @param del
     * @param model
     * @return
     */
    @PostMapping("/delete")
    public String delete(
            @RequestParam(value = "del[]", defaultValue = "") int[] del,
            Model model
    ) throws Exception {

        logger.info("???????????? -{}", del);

        Message message = new Message();
        message.setHref("/board/list");

        if (del.length > 0) {
            for (int i = 0; i < del.length; i++) {
                boardService.deleteById(del[i]);
            }
            message.setMessage("?????????????????????.");
        } else {
            message.setMessage("????????? ???????????? ???????????????.");
        }

        model.addAttribute("data", message);
        return "message/message";
    }

    @PostMapping("/boardSaveTest")
    public String boardSaveTest(@RequestParam("file") MultipartFile multipartFile) throws IOException {

        logger.info(multipartFile.getName());
        logger.info(multipartFile.getContentType());
        logger.info(multipartFile.getOriginalFilename());
        logger.info(String.valueOf(multipartFile.getSize()));

        Path path = Paths.get("C:/upload_file/").toAbsolutePath().normalize();
        Files.createDirectories(path);

        String generateFileName = UUID.randomUUID().toString();

        logger.info("?????? ??? - {}", generateFileName);

        // ?????? ????????? . ?????? ?????? >>>> lastIndexOf abc.jpg abc.def.jpg
        int dot = multipartFile.getOriginalFilename().lastIndexOf(".");

        logger.info("dot - {}", dot);

        String extention="";

        if (-1 != dot && multipartFile.getOriginalFilename().length() - 1 > dot) {
            extention = multipartFile.getOriginalFilename().substring(dot + 1);
        }


        logger.info("????????? - {}", extention);

        File file  = new File("C:/upload_file/" + generateFileName + "." + extention);

        multipartFile.transferTo(file);

        return "test";
    }

    @GetMapping("/file/download")
    @ResponseBody
    public ResponseEntity fileDownload(
            @RequestParam(value = "name", defaultValue = "") String name
    ) throws UnsupportedEncodingException, MalformedURLException {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + new String(name.getBytes("UTF-8"), "ISO-8859-1") + "\"");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        Path path = Paths.get("C:/upload_file/" + name).toAbsolutePath().normalize();
        logger.info(String.valueOf(path));
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok().headers(headers).body(resource);
    }

    @GetMapping("/file/{fileId}")
    @ResponseBody
    public ResponseEntity showFile(@PathVariable int fileId) {
        try {
            UploadFileVO uploadFileVO = uploadFileService.load(fileId);

            if (uploadFileVO == null) return ResponseEntity.badRequest().build();

            String fileName = uploadFileVO.getFileName();

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + "\"");

            if (MediaUtil.containsMediaType(uploadFileVO.getContentType())) {
                headers.setContentType(MediaType.valueOf(uploadFileVO.getContentType()));
            } else {
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            }

            Resource resource = uploadFileService.loadAsResource(uploadFileVO.getFileName());

            return ResponseEntity.ok().headers(headers).body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

    @GetMapping("/deleteFile/{fileId}")
    @ResponseBody
    public ApiResponse deleteFile(@PathVariable int fileId) {
        Boolean result = uploadFileService.deleteFileById(fileId);
        if (result) {
            return new ApiResponse(true, "?????? ??????");
        } else {
            return new ApiResponse(false, "?????? ??????");
        }
    }

}
