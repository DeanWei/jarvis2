package com.mogujie.jarvis.web.controller.api;

import com.mogujie.jarvis.web.controller.jarvis.BaseController;
import com.mogujie.jarvis.web.utils.HdfsUtil;
import com.mogujie.jarvis.web.utils.MessageStatus;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author muming
 */
@Controller
@RequestMapping(value = "/api/file")
public class FileAPIController extends BaseController {

    Logger logger = Logger.getLogger(FileAPIController.class);

    /**
     * 上传jar文件
     *
     * @return
     */
    @RequestMapping("/uploadJar")
    @ResponseBody
    public Object uploadJar(String title, HttpServletRequest request) {

        Map<String, Object> map = new HashMap<>();
        try {
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("标题不能为空");
            }
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile jarFile = multipartRequest.getFile("file");
            if (jarFile == null) {
                throw new IllegalArgumentException("上传文件不能为空");
            }
            String url = HdfsUtil.uploadFile2Hdfs(jarFile, title, user.get().getUname(), true);
            map.put("code", MessageStatus.SUCCESS.getValue());
            map.put("msg", MessageStatus.SUCCESS.getText());
            map.put("data", url);
        } catch (Exception e) {
            if (!(e instanceof IllegalArgumentException)) {
                logger.error("上传jar文件出错", e);
            }
            map.put("code", MessageStatus.FAILED.getValue());
            map.put("msg", e.getMessage());
        }
        return map;
    }

    /**
     * jar文件改名
     *
     * @return
     */
    @RequestMapping("/renameJar")
    @ResponseBody
    public Object renameJar(String newTitle, String curUrl, HttpServletRequest request) {

        Map<String, Object> map = new HashMap<>();
        try {
            if (newTitle == null || newTitle.trim().isEmpty()) {
                throw new IllegalArgumentException("标题不能为空");
            }
            if (curUrl == null || curUrl.trim().isEmpty()) {
                throw new IllegalArgumentException("文件url不能为空");
            }
            String url = HdfsUtil.renameFile4Hdfs(curUrl, newTitle, true);
            map.put("code", MessageStatus.SUCCESS.getValue());
            map.put("msg", MessageStatus.SUCCESS.getText());
            map.put("data", url);
        } catch (Exception e) {
            if (!(e instanceof IllegalArgumentException)) {
                logger.error("jar文件改名", e);
            }
            map.put("code", MessageStatus.FAILED.getValue());
            map.put("msg", e.getMessage());
        }
        return map;
    }

}
