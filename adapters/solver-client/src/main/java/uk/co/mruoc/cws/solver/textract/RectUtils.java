package uk.co.mruoc.cws.solver.textract;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.opencv.core.Rect;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RectUtils {

    public static boolean contains(Rect outer, Rect inner) {
        return outer.x <= inner.x &&
                outer.y <= inner.y &&
                outer.x + outer.width  >= inner.x + inner.width &&
                outer.y + outer.height >= inner.y + inner.height;
    }
}
