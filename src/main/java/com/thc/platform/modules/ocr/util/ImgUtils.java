package com.thc.platform.modules.ocr.util;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.UUID;

public class ImgUtils {

    private static String RESOURCE_PATH;

    static {
        try {
            RESOURCE_PATH = ResourceUtils.getURL("classpath:").getPath() + "img";
        } catch (FileNotFoundException e) {
            RESOURCE_PATH = "./img";
        }
        File file = new File(RESOURCE_PATH);
        if (!file.exists()) file.mkdirs();
    }

    private static String getTargetImgFileName() {
        return UUID.randomUUID().toString() + ".jpg";
    }

    private static void transferImg(String path) throws IOException{
        Thumbnails.of(path).size(1044, 1044).outputFormat("jpg").toFile(path);
    }

    public static String getImgFile(InputStream inputStream) {
        String targetImgFileName = getTargetImgFileName();
        String filePath = RESOURCE_PATH + "/" + targetImgFileName;
        FileOutputStream fileOutputStream = null;
        try {
            File imgFile = new File(filePath);
            fileOutputStream = new FileOutputStream(imgFile);
            byte[] b = new byte[1024];
            int offset;
            while ((offset = inputStream.read(b)) != -1) {
                fileOutputStream.write(b, 0, offset);
            }
            fileOutputStream.flush();
            long length = imgFile.length();
            if (length >= 1024 * 1024) {
                transferImg(filePath);
            }
            return filePath;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("文件生成失败");
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != fileOutputStream) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

//    public static void main(String[] args) throws Exception {
////        String path = "/Users/zouyu/Documents/workspace/base/thc-platform-extend/branches/4.3.1/target/classes/img/IMG_0662.HEIC";
////        String path = "/Users/zouyu/Documents/workspace/base/thc-platform-extend/branches/4.3.1/target/classes/img/51579071436_.pic.jpg";
////        String path = "/Users/zouyu/Documents/workspace/base/thc-platform-extend/branches/4.3.1/target/classes/img/success.jpg";
////        String path = "/Users/zouyu/Documents/workspace/base/thc-platform-extend/branches/4.3.1/target/classes/img/fail2.jpg";
//        String path = "/Users/zouyu/Desktop/北京协和双列OCR模板/2.jpg";
//
//        FileInputStream fileInputStream = new FileInputStream(path);
//        getImgFile(fileInputStream);
////        Thumbnails.of(fileInputStream).scale(1f).outputFormat("jpg").toFile(path1);
////        File file = new File(path);
////        BufferedImage srcFile = ImageIO.read(file);
////        System.out.println(srcFile.getWidth());
////        Mat src = Imgcodecs.imread(path);
////        Mat dst = src.clone();
////        Imgproc.resize(src, dst, new Size(src.width() * 1f, src.height() * 1f));
////        Imgcodecs.imwrite(path1, dst);
//
//    }


}
