package uk.co.mruoc.cws.solver.textract;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.AnalyzeDocumentRequest;
import software.amazon.awssdk.services.textract.model.Document;
import software.amazon.awssdk.services.textract.model.FeatureType;
import uk.co.mruoc.cws.entity.Cells;
import uk.co.mruoc.cws.usecase.CellExtractor;
import uk.co.mruoc.cws.usecase.Image;

@Slf4j
@RequiredArgsConstructor
public class TextractCellExtractor implements CellExtractor {

  private final ProcessedGridImageFactory gridImageFactory;
  private final BlockConverter blockConverter;
  private final TextractClient client;

  public TextractCellExtractor(TextractClient client) {
    this(new ProcessedGridImageFactory(), new BlockConverter(), client);
  }

  @Override
  public Cells extractCells(Image image) {
    var processedGridBytes = gridImageFactory.toProcessedGridImageBytes(image.getBytes());
    return toCells(processedGridBytes);
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
