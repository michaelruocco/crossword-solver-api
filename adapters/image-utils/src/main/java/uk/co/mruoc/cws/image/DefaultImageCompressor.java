package uk.co.mruoc.cws.image;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.usecase.ImageCompressor;

@RequiredArgsConstructor
@Slf4j
public class DefaultImageCompressor implements ImageCompressor {

  private static final long DEFAULT_MAX_SIZE_BYTES = 4L * 1024 * 1024;
  private static final float DEFAULT_QUALITY = 1f;
  private static final int DEFAULT_MIN_SIZE = 100;
  private static final String DEFAULT_TARGET_FORMAT = "jpg";

  private final ImageConverter converter;
  private final long maxSizeBytes;
  private final float initialQuality;
  private final int minWidth;
  private final int minHeight;
  private final String targetFormat;

  public DefaultImageCompressor() {
    this(
            new ImageConverter(),
            DEFAULT_MAX_SIZE_BYTES,
            DEFAULT_QUALITY,
            DEFAULT_MIN_SIZE,
            DEFAULT_MIN_SIZE,
            DEFAULT_TARGET_FORMAT);
  }

  @Override
  public byte[] compressAndResize(BufferedImage image) {
    var rgbImage = converter.toRgb(image);
    var width = image.getWidth();
    var height = image.getHeight();
    var quality = initialQuality;

    byte[] imageData = compressToBytes(rgbImage, quality);
    log.info("initial image data size {} vs max size {}", imageData.length, maxSizeBytes);

    while (imageData.length > maxSizeBytes && (width > minWidth || height > minHeight)) {
      width = (int) (width * 0.9);
      height = (int) (height * 0.9);
      image = resize(image, width, height);
      imageData = compressToBytes(image, quality);
      log.info("compressed to {} bytes using quality {} and size {}x{}", imageData.length, quality, width, height);
    }
    return imageData;
  }

  private byte[] compressToBytes(BufferedImage image, float quality) {
    var outStream = new ByteArrayOutputStream();
    var writer = getImageWriter();
    try (var ios = ImageIO.createImageOutputStream(outStream)) {
      writer.setOutput(ios);
      var param = buildWriteParam(writer, quality);
      writer.write(null, new IIOImage(image, null, null), param);
      return outStream.toByteArray();
    } catch (IOException e) {
      throw new ImageException(e);
    } finally {
      writer.dispose();
    }
  }

  private ImageWriter getImageWriter() {
    var writers = ImageIO.getImageWritersByFormatName(targetFormat);
    if (!writers.hasNext()) {
      throw new ImageException(
              String.format("No image writers for format name %s found", targetFormat));
    }
    return writers.next();
  }

  private ImageWriteParam buildWriteParam(ImageWriter writer, float quality) {
    var param = writer.getDefaultWriteParam();
    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
    param.setCompressionQuality(quality);
    return param;
  }

  private BufferedImage resize(BufferedImage originalImage, int width, int height) {
    var scaled = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    var resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    var g2d = resized.createGraphics();
    g2d.drawImage(scaled, 0, 0, null);
    g2d.dispose();
    return resized;
  }
}