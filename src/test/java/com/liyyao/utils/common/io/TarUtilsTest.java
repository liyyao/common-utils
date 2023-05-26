package com.liyyao.utils.common.io;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TarUtilsTest {

    @Test
    public void tarFileTest() throws IOException {
        String filePath = "D:\\liyyao\\temp\\tarTest";
        String tarPath = "D:\\liyyao\\temp\\tarTest.tar";
        TarUtils.tarFile(filePath, tarPath);
    }

    @Test
    public void unTarFileTest() throws IOException {
        String filePath = "D:\\liyyao\\temp\\unTarTest";
        String tarPath = "D:\\liyyao\\temp\\tarTest.tar";
        TarUtils.unTarFile(tarPath, filePath);
    }

}
