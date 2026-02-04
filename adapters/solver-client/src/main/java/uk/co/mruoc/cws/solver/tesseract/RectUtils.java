package uk.co.mruoc.cws.solver.tesseract;

import java.util.ArrayList;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RectUtils {

  public static Collection<MatOfPoint> removeDuplicates(Collection<MatOfPoint> contours) {
    var deduplicated = new ArrayList<MatOfPoint>();
    for (var contour : contours) {
      var isContained = false;
      var thisBox = Imgproc.boundingRect(contour);
      var otherContours = new ArrayList<>(contours);
      otherContours.remove(contour);
      for (var otherContour : otherContours) {
        var otherBox = Imgproc.boundingRect(otherContour);
        if (contains(otherBox, thisBox)) {
          isContained = true;
        }
      }
      if (!isContained) {
        deduplicated.add(contour);
      }
    }
    return deduplicated;
  }

  public static boolean contains(Rect outer, Rect inner) {
    return outer.x <= inner.x
        && outer.y <= inner.y
        && outer.x + outer.width >= inner.x + inner.width
        && outer.y + outer.height >= inner.y + inner.height;
  }
}
