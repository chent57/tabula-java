package chent57;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Test;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.PageIterator;
import technology.tabula.Rectangle;
import technology.tabula.Table;
import technology.tabula.UtilsForTesting;
import technology.tabula.detectors.NurminenDetectionAlgorithm;
import technology.tabula.extractors.BasicExtractionAlgorithm;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TestExtractTable {
    @Test
    public void extractOnePageOneTable() throws IOException {


        SpreadsheetExtractionAlgorithm bea = new SpreadsheetExtractionAlgorithm();
        Page page = UtilsForTesting.getPage("src/test/resources/chent57/alipay.pdf",
                1);

        // extractTableRectangle可以获取到这列值
        Page area = page.getArea(147.99987499999997f,61.00024999999998f,792.937375f,532.7502499999999f);

        List<Table> table = bea.extract(area);
        System.out.println("table size: " + table.size());

        Table t = table.get(0);

        List<List<String>> result = new ArrayList<>();
        for (int i = 0; i < t.getRowCount(); i++) {
            List<String> singleTran = new ArrayList<>();
            for (int j = 0; j < t.getColCount(); j++) {
                singleTran.add(t.getCell(i,j).getText(false));
            }
            result.add(singleTran);
        }


        for (List<String> list : result) {
            System.out.println("size: " + list.size());
            System.out.println(list);
        }
    }



    /**
     * 解析有多页pdf，且每页只有一个表格的情况的pdf文件
     * @throws IOException IoException
     */
    @Test
    public void extractFileSingleTable() throws IOException {
        // 1. 读取文件
        File pdf = new File("src/test/resources/chent57/wechatpay.pdf");

        // 2. pdfbox读取PDDocument
        PDDocument pdfDocument = PDDocument.load(pdf);

        // 3. tabula新建ObjectExtractor和NurminenDetectionAlgorithm，同时准备接收表格Rectangle的结构
        ObjectExtractor extractor = new ObjectExtractor(pdfDocument);
        NurminenDetectionAlgorithm detectionAlgorithm = new NurminenDetectionAlgorithm();
        Map<Integer, List<Rectangle>> detectedTables = new HashMap<>();

        // 4. 获取每页的PageIterator
        PageIterator pages = extractor.extract();

        // 5. 解析每页的Rectangle(table的位置)
        while (pages.hasNext()) {
            Page page = pages.next();
            List<Rectangle> tablesOnPage = detectionAlgorithm.detect(page);
            if (tablesOnPage.size() > 0) {
                detectedTables.put(new Integer(page.getPageNumber()), tablesOnPage);
            }
        }

        // 6.
        List<List<String>> result = new ArrayList<>();
        SpreadsheetExtractionAlgorithm bea = new SpreadsheetExtractionAlgorithm();

        for (Map.Entry<Integer, List<Rectangle>> entry : detectedTables.entrySet()) {
            Page page = UtilsForTesting.getPage("src/test/resources/chent57/wechatpay.pdf",
                    entry.getKey());
            Rectangle rectangle = entry.getValue().get(0);
            Page area = page.getArea(rectangle.getTop(), rectangle.getLeft(),rectangle.getBottom(),rectangle.getRight());

            List<Table> table = bea.extract(area);

            Table t = table.get(0);


            for (int i = 0; i < t.getRowCount(); i++) {
                List<String> singleTran = new ArrayList<>();
                for (int j = 0; j < t.getColCount(); j++) {
                    singleTran.add(t.getCell(i,j).getText(false));
                }
                result.add(singleTran);
            }
        }

        System.out.println("total size: " + result.size());
        for (List<String> list : result) {
            System.out.println(list.toString());
        }
    }

    /**
     * 解析有密码的有多页pdf，且每页只有一个表格的情况的pdf文件
     * @throws IOException IoException
     */
    @Test
    public void extractFileSingleTableWithPassword() throws IOException {
        // 1. 读取文件
        File pdf = new File("src/test/resources/chent57/tax.pdf");

        // 2. pdfbox读取PDDocument
        PDDocument pdfDocument = PDDocument.load(pdf, "03705X");

        // 3. tabula新建ObjectExtractor和NurminenDetectionAlgorithm，同时准备接收表格Rectangle的结构
        ObjectExtractor extractor = new ObjectExtractor(pdfDocument);
        NurminenDetectionAlgorithm detectionAlgorithm = new NurminenDetectionAlgorithm();
        Map<Integer, List<Rectangle>> detectedTables = new HashMap<>();

        // 4. 获取每页的PageIterator
        PageIterator pages = extractor.extract();

        // 5. 解析每页的Rectangle(table的位置)
        while (pages.hasNext()) {
            Page page = pages.next();
            List<Rectangle> tablesOnPage = detectionAlgorithm.detect(page);
            if (tablesOnPage.size() > 0) {
                detectedTables.put(new Integer(page.getPageNumber()), tablesOnPage);
            }
        }

        // 6.
        List<List<String>> result = new ArrayList<>();
//        SpreadsheetExtractionAlgorithm bea = new SpreadsheetExtractionAlgorithm();
        BasicExtractionAlgorithm bea = new BasicExtractionAlgorithm();

        for (Map.Entry<Integer, List<Rectangle>> entry : detectedTables.entrySet()) {
            Page page = UtilsForTesting.getPage("src/test/resources/chent57/tax.pdf", "03705X",
                    entry.getKey());
            Rectangle rectangle = entry.getValue().get(0);
            Page area = page.getArea(rectangle.getTop(), rectangle.getLeft(),rectangle.getBottom(),rectangle.getRight());

            List<Table> table = bea.extract(area);

            Table t = table.get(0);


            for (int i = 0; i < t.getRowCount(); i++) {
                List<String> singleTran = new ArrayList<>();
                for (int j = 0; j < t.getColCount(); j++) {
                    singleTran.add(t.getCell(i,j).getText(false));
                }
                result.add(singleTran);
            }
        }

        System.out.println("total size: " + result.size());
        for (List<String> list : result) {
            System.out.println(list.toString());
        }
    }


    /**
     * 解析单页有多个表格的pdf文件
     * @throws IOException IoException
     */
    @Test
    public void extractFileMultiTable() throws IOException {
        // 1. 读取文件
        File pdf = new File("src/test/resources/technology/tabula/twotables.pdf");

        // 2. pdfbox读取PDDocument
        PDDocument pdfDocument = PDDocument.load(pdf);

        // 3. tabula新建ObjectExtractor和NurminenDetectionAlgorithm，同时准备接收表格Rectangle的结构
        ObjectExtractor extractor = new ObjectExtractor(pdfDocument);
        NurminenDetectionAlgorithm detectionAlgorithm = new NurminenDetectionAlgorithm();
        Map<Integer, List<Rectangle>> detectedTables = new HashMap<>();

        // 4. 获取每页的PageIterator
        PageIterator pages = extractor.extract();

        // 5. 解析每页的Rectangle(table的位置)
        while (pages.hasNext()) {
            Page page = pages.next();
            List<Rectangle> tablesOnPage = detectionAlgorithm.detect(page);
            if (tablesOnPage.size() > 0) {
                detectedTables.put(new Integer(page.getPageNumber()), tablesOnPage);
            }
        }

        // 6.
        List<List<String>> result = new ArrayList<>();
        SpreadsheetExtractionAlgorithm bea = new SpreadsheetExtractionAlgorithm();

        for (Map.Entry<Integer, List<Rectangle>> entry : detectedTables.entrySet()) {
            Page page = UtilsForTesting.getPage("src/test/resources/technology/tabula/twotables.pdf",
                    entry.getKey());

            // 获取一个page中多个rectangle
            for (Rectangle rectangle : entry.getValue()) {
                Page area = page.getArea(rectangle.getTop(), rectangle.getLeft(),rectangle.getBottom(),rectangle.getRight());

                List<Table> table = bea.extract(area);

                // 获取一个page中多个table的数据
                for (Table t : table) {
                    for (int i = 0; i < t.getRowCount(); i++) {
                        List<String> singleTran = new ArrayList<>();
                        for (int j = 0; j < t.getColCount(); j++) {
                            singleTran.add(t.getCell(i,j).getText(false));
                        }
                        result.add(singleTran);
                    }
                }
            }
        }

        System.out.println("total size: " + result.size());
        for (List<String> list : result) {
            System.out.println(list.toString());
        }
    }


}
