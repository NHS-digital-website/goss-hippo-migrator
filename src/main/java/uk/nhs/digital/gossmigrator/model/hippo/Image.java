package uk.nhs.digital.gossmigrator.model.hippo;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;

public class Image extends HippoImportable implements AssetReportable {

    private final static Logger LOGGER = LoggerFactory.getLogger(Image.class);
    String filePath;
    Path sourceFilePath;
    String lastModifiedDate;
    int width;
    int height;

    public Image(String localizedName, String jcrPath, Path sourceFile) {
        super(localizedName, jcrPath, localizedName);
        this.filePath = "file:///" + sourceFile.toString();
        lastModifiedDate = "2018-01-19T10:07:03.592Z";
        sourceFilePath = sourceFile;
        Iterator<ImageReader> readers = ImageIO.getImageReadersByMIMEType(getMimeType());
        if (!readers.hasNext()) {
            LOGGER.error("Could not create reader for Image:{}.", sourceFile);
        } else {
            ImageReader reader = readers.next();
            try (ImageInputStream stream = new FileImageInputStream(sourceFile.toFile());){
                reader.setInput(stream);
                width = reader.getWidth(reader.getMinIndex());
                height = reader.getHeight(reader.getMinIndex());
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            } finally {
                reader.dispose();
            }
        }
    }

    public String getFilePath() {
        return filePath;
    }

    @SuppressWarnings("unused") // Used in template
    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    @SuppressWarnings("unused") // Used in template
    public String getMimeType() {
        try {
            Tika tika = new Tika();
            return tika.detect(sourceFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public static boolean isImage(String file) {
        return (file.endsWith(".gif") || file.endsWith(".ico") || file.endsWith(".jpg") || file.endsWith(".png"));
    }
}
