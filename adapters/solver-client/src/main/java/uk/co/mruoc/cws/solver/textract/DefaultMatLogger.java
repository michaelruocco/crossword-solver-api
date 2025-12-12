package uk.co.mruoc.cws.solver.textract;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
public class DefaultMatLogger implements IMatLogger {

    private final ImageDirectory directory;
    private final AtomicInteger id;

    public DefaultMatLogger(String folderPath) {
        this(new ImageDirectory(folderPath), new AtomicInteger(0));
    }

    @Override
    public void init() {
        id.set(0);
        directory.init();
        log.info("mat logger will write debug images to {}", directory.absolutePath());
    }

    @Override
    public void debug(Mat mat, String name) {
        var path = directory.writePng(mat, prefixIndex(name));
        log.debug("debug mat written to {}", path);
    }

    @Override
    public void deleteAll() {
        directory.delete();
    }

    private String prefixIndex(String name) {
        return String.format("%d-%s", id.getAndIncrement(), name);
    }
}
