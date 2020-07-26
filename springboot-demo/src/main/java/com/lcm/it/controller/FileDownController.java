package com.lcm.it.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@RestController
public class FileDownController {

    private String rootPath = "D:\\";

    /**
     * 本地下载文件
     *
     * http://localhost:8080/file/down?fileName=tomcat.keystore
     *
     * @param fileName
     */
    @GetMapping("file/down")
    public void down(@PathParam(value = "fileName") String fileName, HttpServletResponse response) throws IOException {

        String fileNameStr = new String(fileName.getBytes("ISO8859-1"), "UTF-8");
        response.setContentType("application/octet-stream");
        // URLEncoder.encode(fileNameString, "UTF-8") 下载文件名为中文的，文件名需要经过url编码
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileNameStr, "UTF-8"));
        File file;
        FileInputStream fileIn = null;
        ServletOutputStream out = null;
        try {
            //String contextPath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/");
            String filePath = rootPath + File.separator + fileName;
            file = new File(filePath);
            fileIn = new FileInputStream(file);
            out = response.getOutputStream();

            byte[] outputByte = new byte[1024];
            int readTmp = 0;
            while ((readTmp = fileIn.read(outputByte)) != -1) {
                out.write(outputByte, 0, readTmp); //并不是每次都能读到1024个字节，所有用readTmp作为每次读取数据的长度，否则会出现文件损坏的错误
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fileIn.close();
            out.flush();
            out.close();
        }
    }

    public static boolean downloadFile(String fileUrl, String fileLocal) throws Exception {
        boolean flag = false;
        URL url = new URL(fileUrl);
        HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
        urlCon.setConnectTimeout(6000);
        urlCon.setReadTimeout(6000);
        int code = urlCon.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK) {
            throw new Exception("文件读取失败");
        }
        //读文件流
        DataInputStream in = new DataInputStream(urlCon.getInputStream());
        DataOutputStream out = new DataOutputStream(new FileOutputStream(fileLocal));
        byte[] buffer = new byte[2048];
        int count = 0;
        while ((count = in.read(buffer)) > 0) {
            out.write(buffer, 0, count);
        }
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        flag = true;
        return flag;
    }
}
