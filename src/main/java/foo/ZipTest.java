package foo;

import org.apache.commons.compress.archivers.zip.*;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.zip.Deflater;

public class ZipTest {
    
    public static void main(String[] args) throws Exception {
        File fileZip = new File("foo.zip");
        ZipArchiveOutputStream zipOut = new ZipArchiveOutputStream(fileZip);
        zipOut.setLevel(Deflater.NO_COMPRESSION);
        zipOut.setMethod(ZipArchiveOutputStream.STORED);
        zipOut.setUseLanguageEncodingFlag(false);

        File entryFile = new File("hello");
        String entryName = entryFile.getName();

        ZipArchiveEntry entry = new ZipArchiveEntry(entryName);
        entry.setSize(entryFile.length());
        //entry.setUnixMode(7777);
        entry.setExternalAttributes(0x81ff0000);

        // extended timestamp
        // 55 54 09 00 03  a4 b9 1f 51 9c ba 1f 51
        UnrecognizedExtraField extendedTimestamp = new UnrecognizedExtraField();
        extendedTimestamp.setHeaderId(new ZipShort(new byte[] {0x55, 0x54}));
        extendedTimestamp.setLocalFileDataData(new byte[] {0x03, (byte)0xA4, (byte)0xb9, 0x1f, 0x51, (byte)0x9c, (byte)0xba, 0x1f, 0x51});
        entry.addExtraField(extendedTimestamp);
        
        // info-zip
        // 75 78 0b 00 01 04 00 02  00 00 04 00 02 00 00
        UnrecognizedExtraField infoZip = new UnrecognizedExtraField();
        infoZip.setHeaderId(new ZipShort(new byte[] {0x75, 0x78}));
        infoZip.setLocalFileDataData(new byte[] {01, 04, 00, 02, 00, 00, 04, 00, 02, 00, 00});
        entry.addExtraField(infoZip);

        FileInputStream fInputStream = new FileInputStream(entryFile);
        zipOut.putArchiveEntry(entry);
        zipOut.write(IOUtils.toByteArray(fInputStream));
        zipOut.closeArchiveEntry();

        zipOut.flush();
        zipOut.close();
    }
    
}