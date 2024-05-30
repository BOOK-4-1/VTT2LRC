package com.yu;

/**
 * @Auther: yu
 * @Date: 2024/5/30 15:52
 * @version: 1.0
 */
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.nio.file.*;
import java.util.regex.*;

@SpringBootApplication
public class VTT2LRCConverter {
    public static void main(String[] args) {
        try {
            // 获取当前目录下所有的.VTT文件
            Files.walk(Paths.get("."))
                    .filter(path -> path.toString().endsWith(".vtt"))
                    .forEach(sourcePath -> {
                        try {
                            // 读取VTT文件内容
                            String content = new String(Files.readAllBytes(sourcePath));

                            // 假设VTT内容格式为: WEBVTT\n\n00:00:01.000 --> 00:00:02.000\nLine one\nLine two
                            // 我们将提取时间戳和文本，并转换为LRC格式: [00:00.000]Line one\n[00:01.000]Line two
                            Pattern pattern = Pattern.compile("(\\d{2}:\\d{2}:\\d{2}\\.\\d{3}) --> (\\d{2}:\\d{2}:\\d{2}\\.\\d{3})(.*?)^\\s*$", Pattern.MULTILINE | Pattern.DOTALL);
                            Matcher matcher = pattern.matcher(content);

                            StringBuilder lrcContent = new StringBuilder();
                            while (matcher.find()) {
                                String startTime = matcher.group(1);
                                String endTime = matcher.group(2);
                                String text = matcher.group(3).trim();

                                // 只取起始时间作为LRC的时间戳
                                String lrcTimestamp = "[" + startTime.replace(":", ".") + "]";
                                lrcContent.append(lrcTimestamp).append(text).append("\n");
                            }

                            // 创建目标文件
                            Path targetPath = sourcePath.getParent().resolve(sourcePath.getFileName().toString().replace(".vtt", ".lrc"));
                            Files.write(targetPath, lrcContent.toString().getBytes());

                            System.out.println("Converted " + sourcePath + " to " + targetPath);
                        } catch (IOException e) {
                            System.err.println("Error processing file " + sourcePath + ": " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            System.err.println("IO error: " + e.getMessage());
        }
    }
}
