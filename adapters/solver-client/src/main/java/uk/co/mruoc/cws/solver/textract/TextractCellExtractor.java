package uk.co.mruoc.cws.solver.textract;

import java.awt.image.BufferedImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.AnalyzeDocumentRequest;
import software.amazon.awssdk.services.textract.model.Document;
import software.amazon.awssdk.services.textract.model.FeatureType;
import uk.co.mruoc.cws.entity.Cells;
import uk.co.mruoc.cws.image.DefaultImageDownloader;
import uk.co.mruoc.cws.usecase.CellExtractor;
import uk.co.mruoc.cws.usecase.ImageDownloader;

@Slf4j
@RequiredArgsConstructor
public class TextractCellExtractor implements CellExtractor {

  private final ImageDownloader downloader;
  private final ProcessedGridImageFactory gridImageFactory;
  private final BlockConverter blockConverter;
  private final TextractClient client;

  public TextractCellExtractor(TextractClient client) {
    this(new DefaultImageDownloader(), client);
  }

  public TextractCellExtractor(ImageDownloader imageDownloader, TextractClient client) {
    this(imageDownloader, new ProcessedGridImageFactory(), new BlockConverter(), client);
  }

  @Override
  public Cells extractCells(String imageUrl) {
    var image = downloader.downloadImage(imageUrl);
    return extractCells(image);
  }

  private Cells extractCells(BufferedImage image) {
    var bytes = gridImageFactory.toProcessedGridImageBytes(image);
    return toCells(bytes);
  }

  private Cells toCells(byte[] bytes) {
    var document = Document.builder().bytes(SdkBytes.fromByteArray(bytes)).build();
    var request =
        AnalyzeDocumentRequest.builder()
            .document(document)
            .featureTypes(FeatureType.TABLES)
            .build();
    var response = client.analyzeDocument(request);
    return blockConverter.toCells(response.blocks());
  }
}
