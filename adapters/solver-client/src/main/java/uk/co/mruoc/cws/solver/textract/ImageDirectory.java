package uk.co.mruoc.cws.solver.textract;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

@RequiredArgsConstructor
public class ImageDirectory {

  private static final String PNG = "png";

  private final File directory;

  public ImageDirectory(String path) {
    this(new File(path));
  }

  public void init() {
    delete();
    create();
  }

  public void delete() {
    FileUtils.deleteQuietly(directory);
  }

  public void create() {
    try {
      FileUtils.forceMkdir(directory);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public String writePng(BufferedImage image, String name) {
    return write(image, name, PNG);
  }

  public String writePng(Mat mat, String name) {
    var path = toPath(name, PNG);
    Imgcodecs.imwrite(path, mat);
    return path;
  }

  public String write(BufferedImage image, String name, String format) {
    try {
      var file = toFile(name, format);
      ImageIO.write(image, format, file);
      return file.getAbsolutePath();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public String absolutePath() {
    return directory.getAbsolutePath();
  }

  private File toFile(String name, String format) {
    return new File(toPath(name, format));
  }

  private String toPath(String name, String format) {
    var directoryPath = directory.getAbsolutePath();
    return String.format("%s/%s.%s", directoryPath, name, format);
  }
}
