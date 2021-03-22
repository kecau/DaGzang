package com.universal.recommender.util;

import com.universal.recommender.constant.CommonConstant;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    private List <String> fileList;

    public ZipUtils() {
        fileList = new ArrayList < String > ();
    }

    public static void zipSource() {
        ZipUtils appZip = new ZipUtils();
        appZip.generateFileList(new File(CommonConstant.SOURCE_CODE_PATH));
        appZip.zipIt(CommonConstant.ZIP_FILE_PATH);
    }

    public void zipIt(String zipFile) {
        byte[] buffer = new byte[1024];
        String source = new File(CommonConstant.SOURCE_CODE_PATH).getName();
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(fos);

            FileInputStream in = null;

            for (String file: this.fileList) {
                ZipEntry ze = new ZipEntry(source + File.separator + file);
                zos.putNextEntry(ze);
                try {
                    in = new FileInputStream(CommonConstant.SOURCE_CODE_PATH + File.separator + file);
                    int len;
                    while ((len = in .read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                } finally {
                    in.close();
                }
            }

            zos.closeEntry();

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                zos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void generateFileList(File node) {
        if (node.isFile()) {
            fileList.add(generateZipEntry(node.toString()));
        }

        if (node.isDirectory()) {
            String[] subNote = node.list();
            for (String filename: subNote) {
                generateFileList(new File(node, filename));
            }
        }
    }

    private String generateZipEntry(String file) {
        return file.substring(CommonConstant.SOURCE_CODE_PATH.length() + 1, file.length());
    }

    public static ByteArrayInputStream getZipFile() {
        try {
            File zipFile = new File(CommonConstant.ZIP_FILE_PATH);
            return new ByteArrayInputStream(FileUtils.readFileToByteArray(zipFile));
        } catch (Exception e) {
            return null;
        }

    }
}
