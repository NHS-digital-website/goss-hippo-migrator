package uk.nhs.digital.gossmigrator.model.hippo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.digital.gossmigrator.model.goss.GossFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;

public class Image extends FileImportable implements AssetReportable {

    private final static Logger LOGGER = LoggerFactory.getLogger(Image.class);

    private int width;
    private int height;

    public Image(String localizedName, String jcrPath, Path sourceFile, GossFile gossFile) {
        super(localizedName, jcrPath, sourceFile, gossFile);

        Iterator<ImageReader> readers = ImageIO.getImageReadersByMIMEType(getMimeType());
        if (!readers.hasNext()) {
            LOGGER.error("Could not create reader for Image:{}.", sourceFile);
        } else {
            ImageReader reader = readers.next();
            try (ImageInputStream stream = new FileImageInputStream(sourceFile.toFile())) {
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

    @SuppressWarnings("unused") // Used in template
    public int getWidth() {
        return width;
    }

    @SuppressWarnings("unused") // Used in template
    public int getHeight() {
        return height;
    }
}
