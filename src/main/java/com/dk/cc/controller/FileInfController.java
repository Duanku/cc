package com.dk.cc.controller;

import com.dk.cc.common.lang.Result;
import com.dk.cc.common.util.FileUtil;
import com.dk.cc.common.util.JwtUtils;
import com.dk.cc.entity.FileInf;
import com.dk.cc.service.FileInfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author dk
 * @since 2020-12-22
 */
@RestController
@RequestMapping("/file-inf")
public class FileInfController {

    @Autowired
    private FileInfService fileInfService;

    // 设置固定的日期格式
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    // 将 yml 中的自定义配置注入到这里
    @Value("${upPath.filePath}")
    private String filePath;

    @Autowired
    JwtUtils jwtUtils;


    @PostMapping("/upload")
    public Result uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        if (Objects.isNull(file) || file.isEmpty()) {
            return Result.fail("文件为空，请重新上传");
        }
        FileInf fileInf = new FileInf();

        //获取上传用户的id
        String userId = jwtUtils.getUserId(request.getHeader("Authorization"));

        //获取上传时间
        String format = formatter.format(LocalDateTime.now());

        //获取文件上传名称
        String fileName = file.getOriginalFilename();

        //生成唯一文件名防止文件冲突
        String newFileName = format + "_" + fileName;

        //文件存放位置
        File dest = new File(filePath + newFileName);

        //打印文件存放位置
        System.out.println(dest.getPath());

        //判断文件是否存在
        if (dest.exists()) {
            return Result.fail("文件上传,请重新选择文件");
        }
        //储存文件信息
        try {
            //保存文件
            file.transferTo(dest);
            //文件信息存入数据库
            fileInf.setFileName(newFileName);
            fileInf.setFilePath(dest.getPath());
            fileInf.setFileSize((int) file.getSize());
            fileInf.setUserId(userId);
            fileInf.setFileUploadTime(format);
            fileInf.setFileStatus(1);
            fileInfService.save(fileInf);
            System.out.println("上传成功");
            return Result.succ(200, "上传成功", null);
        } catch (IOException e) {
            System.out.println("上传失败");
            System.out.println(e.toString());
            return Result.fail("上传失败");
        }
    }

    //下载文件
    @RequestMapping("/download/{file_id}")
    public Result download(HttpServletResponse response, @PathVariable("file_id") Long file_id) throws Exception {
        FileInf fileInf = fileInfService.getById(file_id);
        // 文件地址，真实环境是存放在数据库中的
        File file = new File(fileInf.getFilePath());
        System.out.println(file.getPath());
        //判断文件是否存在
        if (!file.exists()) {
            return Result.fail("文件不存在");
        }
        // 穿件输入对象
        FileInputStream fis = new FileInputStream(file);
        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");
        String fileName = fileInf.getFileName();
        fileName = fileName.substring(fileName.indexOf("_") + 1, fileName.length());
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        // 创建输出对象
        OutputStream os = response.getOutputStream();
        // 常规操作
        byte[] buf = new byte[1024];
        int len = 0;
        while ((len = fis.read(buf)) != -1) {
            os.write(buf, 0, len);
        }
        fis.close();
        return Result.succ(200, "文件下载成功", null);
    }

    @RequestMapping("/deleteFile/{id}")
    public Result deleteFile(@PathVariable("id") Long id) {
        FileInf fileInf = fileInfService.getById(id);
        File file = new File(fileInf.getFilePath());

        if (file.exists()) {
            file.delete();
            fileInfService.removeById(id);
            return Result.succ(200, "删除成功", null);
        } else {
            return Result.fail("文件不存在");
        }
    }

    /**
     * 视屏播放
     */
    @RequestMapping("/playVideo/{file_id}")
    public void playVideo(HttpServletResponse response, HttpServletRequest request, @PathVariable("file_id") Long file_id) {
        System.out.println("开始播放视频");
        /*File tep = new File(fileInfService.getById(file_id).getFilePath());
        byte[] data = null;
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(tep));
            BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/mp4");//设置返回类型
            data = new byte[1024];
            int count = 0;
            while ((count = in.read(data)) != -1) {
                out.write(data, 0, count);
            }
            in.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        String path = fileInfService.getById(file_id).getFilePath();
        FileUtil fileUtil = new FileUtil();
        fileUtil.FileUtil(path, request, response);
/*        BufferedInputStream bis = null;
        try {
            File file = new File(path);
            if (file.exists()) {
                long p = 0L;
                long toLength = 0L;
                long contentLength = 0L;
                int rangeSwitch = 0; // 0,从头开始的全文下载；1,从某字节开始的下载（bytes=27000-）；2,从某字节开始到某字节结束的下载（bytes=27000-39000）
                long fileLength;
                String rangBytes = "";
                fileLength = file.length();

                // 获取文件内容
                InputStream ins = new FileInputStream(file);
                bis = new BufferedInputStream(ins);

                // 告诉客户端允许accept-range
                response.reset();
                response.setHeader("Accept-Ranges", "bytes");

                // client requests a file block download start byte|客户端请求一个文件块下载开始字节
                String range = request.getHeader("Range");
                if (range != null && range.trim().length() > 0 && !"null".equals(range)) {
                    response.setStatus(javax.servlet.http.HttpServletResponse.SC_PARTIAL_CONTENT);
                    rangBytes = range.replaceAll("bytes=", "");
                    if (rangBytes.endsWith("-")) { // bytes=270000-
                        rangeSwitch = 1;
                        p = Long.parseLong(rangBytes.substring(0, rangBytes.indexOf("-")));
                        contentLength = fileLength - p; // 客户端请求的是270000之后的字节（包括bytes下标索引为270000的字节）
                    } else { // bytes=270000-320000
                        rangeSwitch = 2;
                        String temp1 = rangBytes.substring(0, rangBytes.indexOf("-"));
                        String temp2 = rangBytes.substring(rangBytes.indexOf("-") + 1, rangBytes.length());
                        p = Long.parseLong(temp1);
                        toLength = Long.parseLong(temp2);
                        contentLength = toLength - p + 1; // 客户端请求的是 270000-320000 之间的字节
                    }
                } else {
                    contentLength = fileLength;
                }

                // 如果设设置了Content-Length，则客户端会自动进行多线程下载。如果不希望支持多线程，则不要设置这个参数。
                // Content-Length: [文件的总大小] - [客户端请求的下载的文件块的开始字节]
                response.setHeader("Content-Length", new Long(contentLength).toString());

                // 断点开始
                // 响应的格式是:
                // Content-Range: bytes [文件块的开始字节]-[文件的总大小 - 1]/[文件的总大小]
                if (rangeSwitch == 1) {
                    String contentRange = new StringBuffer("bytes ").append(new Long(p).toString()).append("-")
                            .append(new Long(fileLength - 1).toString()).append("/")
                            .append(new Long(fileLength).toString()).toString();
                    response.setHeader("Content-Range", contentRange);
                    bis.skip(p);
                } else if (rangeSwitch == 2) {
                    String contentRange = range.replace("=", " ") + "/" + new Long(fileLength).toString();
                    response.setHeader("Content-Range", contentRange);
                    bis.skip(p);
                } else {
                    String contentRange = new StringBuffer("bytes ").append("0-").append(fileLength - 1).append("/")
                            .append(fileLength).toString();
                    response.setHeader("Content-Range", contentRange);
                }

                String fileName = file.getName();
                response.setContentType("application/octet-stream");
                response.addHeader("Content-Disposition", "attachment;filename=" + fileName);

                OutputStream out = response.getOutputStream();
                int n = 0;
                long readLength = 0;
                int bsize = 1024;
                byte[] bytes = new byte[bsize];
                if (rangeSwitch == 2) {
                    // 针对 bytes=27000-39000 的请求，从27000开始写数据
                    while (readLength <= contentLength - bsize) {
                        n = bis.read(bytes);
                        readLength += n;
                        out.write(bytes, 0, n);
                    }
                    if (readLength <= contentLength) {
                        n = bis.read(bytes, 0, (int) (contentLength - readLength));
                        out.write(bytes, 0, n);
                    }
                } else {
                    while ((n = bis.read(bytes)) != -1) {
                        out.write(bytes, 0, n);
                    }
                }
                out.flush();
                out.close();
                bis.close();
            }
        } catch (IOException ie) {
            // 忽略 ClientAbortException 之类的异常
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
    }
}
