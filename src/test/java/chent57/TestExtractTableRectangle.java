package chent57;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Test;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.PageIterator;
import technology.tabula.Rectangle;
import technology.tabula.detectors.NurminenDetectionAlgorithm;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestExtractTableRectangle {

    @Test
    public void extractTableRectangle() throws IOException {
        // 1. 读取文件
        File pdf = new File("src/test/resources/chent57/alipay.pdf");

        // 2. pdfbox读取PDDocument
        PDDocument pdfDocument = PDDocument.load(pdf);

        // 3. tabula新建ObjectExtractor和NurminenDetectionAlgorithm，同时准备接收表格Rectangle的结构
        ObjectExtractor extractor = new ObjectExtractor(pdfDocument);
        NurminenDetectionAlgorithm detectionAlgorithm = new NurminenDetectionAlgorithm();
        Map<Integer, List<Rectangle>> detectedTables = new HashMap<>();

        // 4. 获取每页的PageIterator
        PageIterator pages = extractor.extract();

        // 5. 解析每页的Rectangle
        while (pages.hasNext()) {
            Page page = pages.next();
            List<Rectangle> tablesOnPage = detectionAlgorithm.detect(page);
            if (tablesOnPage.size() > 0) {
                detectedTables.put(new Integer(page.getPageNumber()), tablesOnPage);
            }
        }

        // 6. 打印每页的Rectangle，如果一个page有多个表格，会有多个Rectangle
        for (Map.Entry<Integer, List<Rectangle>> entry : detectedTables.entrySet()) {
            System.out.println("page" + entry.getKey() + " : " + entry.getValue().toString());
        }
    }
}
