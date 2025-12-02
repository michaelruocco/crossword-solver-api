package uk.co.mruoc.cws.usecase;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Base64;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ImageCompressor {

  private static final long DEFAULT_MAX_SIZE_BYTES = 4L * 1024 * 1024;
  private static final float DEFAULT_QUALITY = 1f;
  private static final int DEFAULT_MIN_SIZE = 100;

  private final long maxSizeBytes;
  private final float initialQuality;
  private final int minWidth;
  private final int minHeight;

  public ImageCompressor() {
    this(DEFAULT_MAX_SIZE_BYTES, DEFAULT_QUALITY, DEFAULT_MIN_SIZE, DEFAULT_MIN_SIZE);
  }

  public byte[] compressAndResize(BufferedImage image) {
    var rgbImage = toRgb(image);
    var width = image.getWidth();
    var height = image.getHeight();
    var quality = initialQuality;

    byte[] imageData = compressToJpegBytes(rgbImage, quality);
    log.info("initial image data size {} vs max size {}", imageData.length, maxSizeBytes);

    while (imageData.length > maxSizeBytes && (width > minWidth || height > minHeight)) {
      width = (int) (width * 0.9);
      height = (int) (height * 0.9);
      image = resize(image, width, height);
      imageData = compressToJpegBytes(image, quality);
      var size = toMb(imageData.length);
      log.info("compressed to: {}mb using quality {} and size {}x{}", size, quality, width, height);
    }
    return imageData;
  }

  private BufferedImage toRgb(BufferedImage image) {
    if (image.getType() == BufferedImage.TYPE_INT_RGB) {
      return image;
    }
    var rgbImage =
        new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
    var g = rgbImage.createGraphics();
    g.setComposite(AlphaComposite.SrcOver);
    g.drawImage(image, 0, 0, Color.WHITE, null);
    g.dispose();
    return rgbImage;
  }

  private byte[] compressToJpegBytes(BufferedImage image, float quality) {
    var outStream = new ByteArrayOutputStream();
    var writer = getJpgImageWriter();
    try (var ios = ImageIO.createImageOutputStream(outStream)) {
      writer.setOutput(ios);
      var param = buildWriteParam(writer, quality);
      writer.write(null, new IIOImage(image, null, null), param);
      return outStream.toByteArray();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } finally {
      writer.dispose();
    }
  }

  private ImageWriter getJpgImageWriter() {
    return getImageWriter("jpg");
  }

  private ImageWriteParam buildWriteParam(ImageWriter writer, float quality) {
    var param = writer.getDefaultWriteParam();
    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
    param.setCompressionQuality(quality);
    return param;
  }

  private ImageWriter getImageWriter(String formatName) {
    var writers = ImageIO.getImageWritersByFormatName(formatName);
    if (!writers.hasNext()) {
      throw new IllegalStateException(
          String.format("No image writers for format name %s found", formatName));
    }
    return writers.next();
  }

  private static BufferedImage resize(BufferedImage originalImage, int width, int height) {
    var scaled = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    var resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    var g2d = resized.createGraphics();
    g2d.drawImage(scaled, 0, 0, null);
    g2d.dispose();
    return resized;
  }

  private double toMb(int sizeInBytes) {
    return sizeInBytes / 1024.0 / 1024.0;
  }

  private String base64Encode(byte[] bytes) {
    return Base64.getEncoder().encodeToString(bytes);
  }
}
