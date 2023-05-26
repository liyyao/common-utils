package com.liyyao.utils.common.io;

import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.apache.tools.tar.TarOutputStream;

import java.io.*;

/**
 * tar工具类
 */
public class TarUtils {

    /**
     * 压缩tar包
     * @param filePath  文件路径
     * @param tarPath   tar包生成路径
     * @throws IOException
     */
    public static void tarFile(String filePath, String tarPath) throws IOException {
        File sourceFile = new File(filePath);
        File targetTarFile = new File(tarPath);
        boolean success = false;    //是否压缩成功
        try (OutputStream os = new FileOutputStream(targetTarFile);
            OutputStream bos = new BufferedOutputStream(os);
            TarOutputStream tos = new TarOutputStream(bos)) {
            tarFile(sourceFile, tos, "", true);
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //清理操作
        if (!success && targetTarFile.exists()) {
            targetTarFile.delete();
        }
    }

    public static void tarFile(File file, TarOutputStream tos, String dir, boolean tarEmptyFile) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null || (files.length == 0 && tarEmptyFile)) { //空目录归档
                tos.putNextEntry(new TarEntry(dir + file.getName() + File.separator)); //将实体放入输出Tar流中
                return;
            }
            for (File f : files) {
                if (f.isDirectory()) {
                    tarFile(f, tos, dir + f.getName() + File.separator, tarEmptyFile);  //递归归档
                } else {
                    tarFile(f, tos, dir, tarEmptyFile);
                }
            }
        } else {
            byte[] data = new byte[1024 * 2];
            try (FileInputStream fis = new FileInputStream(file)) {
                // 1.构建tar实体
                TarEntry te = new TarEntry(dir + file.getName());
                // 2.设置压缩前的文件大小
                te.setSize(file.length());
                //te.setName(file.getName());   //设置实体名称，使用默认名称
                tos.setLongFileMode(TarOutputStream.LONGFILE_GNU);
                tos.putNextEntry(te);   //将实体放入输出Tar流中
                int i = 0;
                while ((i = fis.read(data)) != -1) {    //循环读出并写入输出Tar流中
                    tos.write(data, 0, i);
                }
            } catch (Exception e) {
                throw new RuntimeException("写入归档文件出现异常", e);
            } finally {
                try {
                    tos.closeEntry();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void unTarFile(String unTarPath, String tarPath) throws IOException {
        File tmpFile = null;
        File file = new File(unTarPath);
        try (InputStream is = new FileInputStream(file);
             TarInputStream tis = new TarInputStream(is)) {
            TarEntry tae = null;
            while ((tae = tis.getNextEntry()) != null) {
                if (tae.isDirectory()) {
                    createDirectory(tarPath, tae.getName());
                } else {
                    tmpFile = new File(tarPath + File.separator + tae.getName());
                    //如果解压的当前路径不存在，就创建
                    createDirectory(tmpFile.getParent() + File.separator, null);
                    try (OutputStream outputStream = new FileOutputStream(tmpFile)) {
                        int count;
                        byte[] data = new byte[1024];
                        while ((count = tis.read(data, 0, 1024)) != -1) {
                            outputStream.write(data, 0, count);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createDirectory(String outputDir, String subDir) {
        File file = new File(outputDir);
        //如果子目录不为空则创建子目录
        if (subDir != null && !"".equals(subDir.trim())) {
            file = new File(outputDir + File.separator + subDir);
        }
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.mkdirs();
        }
    }
}
