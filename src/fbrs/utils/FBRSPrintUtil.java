package fbrs.utils;

import fbrs.model.DatabaseModel;
import fbrs.model.Entry;
import fbrs.model.Seller;
import fbrs.model.User;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.apache.poi.xwpf.usermodel.ParagraphAlignment.*;
import static org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation.LANDSCAPE;
import static org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation.PORTRAIT;

public class FBRSPrintUtil {
    /* Colors */
    public static final String BLACK = "000000";
    public static final String DARK_GRAY = "BFBFBF";
    public static final String LIGHT_GRAY = "D0D0D0";
    public static final String LIGHTER_GRAY = "F2F2F2";

    public static final String BORDER_THICKNESS = "15";
    public static final int TABLE_HEADER_FONT_SIZE = 14;
    public static final int ENTRY_HEADER_FONT_SIZE = 10;
    public static final int HEADER_FONT_SIZE = 10;
    public static final int CELL_FONT_SIZE = 10;
    public static final int SELLER_CELL_FONT_SIZE = 12;

    private static final String ENTRY_CELL_NORMAL_FORMAT = "ـ   %1$s×%2$s  %3$s";
    private static final String ENTRY_CELL_DETAILED_FORMAT = "ـ %1$s ـ %2$s×%3$s  %4$s";

    private static final int MAX_ROW_COUNT = 33;
    private static final int MAX_COLUMN_COUNT = 6;
    private static final int MAX_SELLERS_ROW_COUNT = 50;
    private static final int MAX_SELLERS_COLUMN_COUNT = 4;
    private static final int MAX_ENTRY_ROW_COUNT = 50;
    private static final int MAX_ENTRY_COLUMN_COUNT = 8;

    private static final int DOCUMENT_PORTRAIT_WIDTH = 11900;
    private static final int DOCUMENT_PORTRAIT_HEIGHT = 16840;

    private static FBRSPrintUtil instance;

    private final SimpleDateFormat HEADER_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private final SimpleDateFormat CELL_DATE_FORMAT = new SimpleDateFormat("MM/dd");
    private final DatabaseModel model;

    private int column;
    private int row;
    private int dataIndex;
    private int marketBuksaCount;

    private FBRSPrintUtil() {
        model = DatabaseModel.getModel();
    }

    public static FBRSPrintUtil getInstance() {
        if (instance == null)
            instance = new FBRSPrintUtil();
        return instance;
    }

    /**
     * @param path       the path where the result file is printed
     * @param marketData list of all printable sellers to be printed
     * @param isDetailed determines whether the whole table is Detailed or not
     */
    public void printMarketReport(String path, List<FBRSPrintableUserEntry> marketData,
                                  boolean isDetailed, boolean isSingleDay, Date fromDate) {
        freshStartNewReport();

        File file = new File(path);
        try (FileOutputStream out = new FileOutputStream(file)) {
            // Creating & formatting main document
            XWPFDocument document = new XWPFDocument();
            formatDocument(document, LANDSCAPE, BigInteger.valueOf(720L));

            while (dataIndex < marketData.size()) {
                // Creating new table
                XWPFTable table = document.createTable(MAX_ROW_COUNT, MAX_COLUMN_COUNT);
                // Set text direction to RTL
                table.getCTTbl().addNewTblPr().addNewBidiVisual().setVal(STOnOff.ON);
                table.setWidth("100%");

                addMarketHeader(table, model.getMarketByID(
                        ((Seller) marketData.get(0).getUser()).getMarket()).getName(),
                        MAX_COLUMN_COUNT - 1
                );
                addMarketContent(table, marketData, isDetailed, isSingleDay);
                addTableBorders(table, new BigInteger(BORDER_THICKNESS));

                while (!isInvalidCell()) {
                    formatParagraph(table.getRow(row).getCell(column).getParagraphs().get(0),
                            CENTER, 8, BLACK, " ", false
                    );
                    addRow();
                }
                reset();
            }

            addDocHeaderFooter(document, fromDate, true, true, true);

            document.write(out);
            Desktop.getDesktop().print(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param sellers non-zero balanced sellers
     */
    public void printMarketBalanceReport(String path, List<Seller> sellers) {
        freshStartNewReport();

        File file = new File(path);
        try (FileOutputStream out = new FileOutputStream(file)) {
            // Creating & formatting main document
            XWPFDocument document = new XWPFDocument();
            formatDocument(document, PORTRAIT, BigInteger.valueOf(720L));

            while (dataIndex < sellers.size()) {
                // Creating new table
                XWPFTable table = document
                        .createTable(MAX_SELLERS_ROW_COUNT, MAX_SELLERS_COLUMN_COUNT);
                // Set text direction to RTL
                table.getCTTbl().addNewTblPr().addNewBidiVisual().setVal(STOnOff.ON);
                table.setWidth("100%");

                addMarketHeader(table, model.getMarketByID(sellers.get(0).getMarket()).getName(),
                        MAX_SELLERS_COLUMN_COUNT - 1);
                addSellersHeader(table);
                addSellersContent(table, sellers);
                addTableBorders(table, new BigInteger(BORDER_THICKNESS));

                formatEmptyCells(table, MAX_SELLERS_ROW_COUNT, MAX_SELLERS_COLUMN_COUNT);
                reset();
            }

            addDocHeaderFooter(document, null, true, true, true);

            document.write(out);
            Desktop.getDesktop().print(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param path        the path where the result file is printed
     * @param user        printable user
     * @param userEntries all user entries to be printed
     */
    public void printUserEntries(String path, User user, List<Entry> userEntries) {
        freshStartNewReport();

        File file = new File(path);
        try (FileOutputStream out = new FileOutputStream(file)) {
            // Creating & formatting main document
            XWPFDocument document = new XWPFDocument();
            formatDocument(document, PORTRAIT, BigInteger.valueOf(720L));

            while (dataIndex < userEntries.size()) {
                // Creating new table
                XWPFTable table = document.createTable(MAX_ENTRY_ROW_COUNT, MAX_ENTRY_COLUMN_COUNT);
                // Set text direction to RTL
                table.getCTTbl().addNewTblPr().addNewBidiVisual().setVal(STOnOff.ON);
                table.setWidth("100%");

                addEntryHeader(table, user);
                addEntryContent(table, userEntries);
                addTableBorders(table, new BigInteger(BORDER_THICKNESS));

                formatEmptyCells(table, MAX_ENTRY_ROW_COUNT, MAX_ENTRY_COLUMN_COUNT);
                reset();
            }

            addDocHeaderFooter(document, null, true, false, true);

            document.write(out);
            Desktop.getDesktop().print(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void formatEmptyCells(XWPFTable table, int maxRowCount, int maxColumnCount) {
        while (row < maxRowCount) {
            for (int i = 0; i < maxColumnCount; i++) {
                formatParagraph(table.getRow(row).getCell(i).getParagraphs().get(0),
                        CENTER, 8, BLACK, " ", false
                );
            }
            row++;
        }
    }

    private void addDocHeaderFooter(XWPFDocument document, Date fromDate, boolean hasHeader,
                                    boolean withSumHeader, boolean hasFooter) {
        XWPFHeaderFooterPolicy headerFooterPolicy = document.createHeaderFooterPolicy();

        if (hasHeader) {
            XWPFHeader documentHeader = headerFooterPolicy
                    .createHeader(XWPFHeaderFooterPolicy.DEFAULT);
            if (withSumHeader) {
                formatParagraph(documentHeader.createParagraph(), CENTER, HEADER_FONT_SIZE, BLACK,
                        String.format("مجموع أرصدة جميع التجار : %d", marketBuksaCount), false
                );
            }
            formatParagraph(documentHeader.createParagraph(), RIGHT, HEADER_FONT_SIZE, BLACK,
                    fromDate != null ? String.format("من %s إلى %s",
                            HEADER_DATE_FORMAT.format(new Date()),
                            HEADER_DATE_FORMAT.format(fromDate))
                            : String.format("%s", HEADER_DATE_FORMAT.format(new Date())),
                    false
            );
        }

        if (hasFooter) {
            XWPFFooter documentFooter = headerFooterPolicy
                    .createFooter(XWPFHeaderFooterPolicy.DEFAULT);
            XWPFParagraph paragraph = documentFooter.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);

            paragraph.getCTP().addNewFldSimple().setInstr("NUMPAGES \\* MERGEFORMAT");
            XWPFRun run = paragraph.createRun();
            run.setText(" من ");
            paragraph.getCTP().addNewFldSimple().setInstr("PAGE \\* MERGEFORMAT");
            run = paragraph.createRun();
            run.setText(" صفحة");
            run.setFontSize(HEADER_FONT_SIZE);
        }
    }

    private void formatDocument(XWPFDocument document, STPageOrientation.Enum orientation, BigInteger margin) {
        CTBody body = document.getDocument().getBody();
        CTSectPr sectPr = body.addNewSectPr();
        CTPageSz pageSize = sectPr.addNewPgSz();

        pageSize.setOrient(orientation);
        // A4 = 595x842 / multiply 20 since BigInteger represents 1/20 Point
        pageSize.setW(BigInteger.valueOf(
                orientation.equals(PORTRAIT) ? DOCUMENT_PORTRAIT_WIDTH
                        : DOCUMENT_PORTRAIT_HEIGHT)
        );
        pageSize.setH(BigInteger.valueOf(
                orientation.equals(PORTRAIT) ? DOCUMENT_PORTRAIT_HEIGHT
                        : DOCUMENT_PORTRAIT_WIDTH)
        );

        // Adding margins
        CTPageMar pageMar = sectPr.addNewPgMar();
        pageMar.setLeft(margin);
        pageMar.setTop(margin);
        pageMar.setRight(margin);
        pageMar.setBottom(margin);
        pageMar.setHeader(BigInteger.valueOf(500L));
        pageMar.setFooter(BigInteger.valueOf(500L));
    }

    private void addTableBorders(XWPFTable table, BigInteger borderSize) {
        CTTblPr ctTblPr = table.getCTTbl().addNewTblPr();
        CTTblBorders borders = ctTblPr.addNewTblBorders();

        CTBorder border = borders.addNewRight();
        border.setVal(STBorder.THICK);
        border.setSz(borderSize);

        border = borders.addNewBottom();
        border.setVal(STBorder.THICK);
        border.setSz(borderSize);

        border = borders.addNewLeft();
        border.setVal(STBorder.THICK);
        border.setSz(borderSize);

        border = borders.addNewTop();
        border.setVal(STBorder.THICK);
        border.setSz(borderSize);
    }

    private void addMarketHeader(XWPFTable table, String marketName, int columnCount) {
        XWPFTableRow mainHeaderRow = table.getRow(0);
        mergeCellsHorizontal(mainHeaderRow, 0, columnCount);

        XWPFTableCell mainHeaderCell = mainHeaderRow.getCell(0);
        mainHeaderCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        mainHeaderCell.setColor(DARK_GRAY);
        formatParagraph(mainHeaderCell.getParagraphs().get(0), CENTER,
                TABLE_HEADER_FONT_SIZE, BLACK, marketName, true
        );

        if (dataIndex != 0)
            mainHeaderCell.getParagraphs().get(0).setPageBreak(true);

        addRow();
    }

    private void addSellersHeader(XWPFTable table) {
        XWPFTableRow sellersHeaderRow = table.getRow(1);

        XWPFTableCell numberHeaderCell = sellersHeaderRow.getCell(0);
        numberHeaderCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        numberHeaderCell.setColor(LIGHT_GRAY);
        formatParagraph(numberHeaderCell.getParagraphs().get(0), CENTER,
                ENTRY_HEADER_FONT_SIZE, BLACK, "الرقم", true
        );

        XWPFTableCell darshKeyHeaderCell = sellersHeaderRow.getCell(1);
        darshKeyHeaderCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        darshKeyHeaderCell.setColor(LIGHT_GRAY);
        formatParagraph(darshKeyHeaderCell.getParagraphs().get(0), CENTER,
                ENTRY_HEADER_FONT_SIZE, BLACK, "رقم الدرش", true
        );

        XWPFTableCell nameHeaderCell = sellersHeaderRow.getCell(2);
        nameHeaderCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        nameHeaderCell.setColor(LIGHT_GRAY);
        formatParagraph(nameHeaderCell.getParagraphs().get(0), CENTER,
                ENTRY_HEADER_FONT_SIZE, BLACK, "الاسم", true
        );

        XWPFTableCell balanceHeaderCell = sellersHeaderRow.getCell(3);
        balanceHeaderCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        balanceHeaderCell.setColor(LIGHT_GRAY);
        formatParagraph(balanceHeaderCell.getParagraphs().get(0), CENTER,
                ENTRY_HEADER_FONT_SIZE, BLACK, "الرصيد", true
        );

        addRow();
    }

    private void addEntryHeader(XWPFTable table, User user) {
        XWPFTableRow sellerHeaderRow = table.getRow(0);
        mergeCellsHorizontal(sellerHeaderRow, 0, MAX_ENTRY_COLUMN_COUNT - 1);

        XWPFTableCell sellerHeaderCell = sellerHeaderRow.getCell(0);
        sellerHeaderCell.setColor(DARK_GRAY);
        formatParagraph(sellerHeaderCell.getParagraphs().get(0), CENTER,
                TABLE_HEADER_FONT_SIZE, BLACK,
                String.format("الاسم: %s، الرصيد: %d", user.getName(), user.getBalance()), true
        );

        row++;

        XWPFTableRow entryHeaderRow = table.getRow(1);

        XWPFTableCell giverHeaderCell = entryHeaderRow.getCell(0);
        giverHeaderCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        giverHeaderCell.setColor(LIGHT_GRAY);
        formatParagraph(giverHeaderCell.getParagraphs().get(0), CENTER,
                ENTRY_HEADER_FONT_SIZE, BLACK, "المعطي", true
        );

        XWPFTableCell takerHeaderCell = entryHeaderRow.getCell(1);
        takerHeaderCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        takerHeaderCell.setColor(LIGHT_GRAY);
        formatParagraph(takerHeaderCell.getParagraphs().get(0), CENTER,
                ENTRY_HEADER_FONT_SIZE, BLACK, "المتلقي", true
        );

        XWPFTableCell entryTypeHeaderCell = entryHeaderRow.getCell(2);
        entryTypeHeaderCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        entryTypeHeaderCell.setColor(LIGHT_GRAY);
        formatParagraph(entryTypeHeaderCell.getParagraphs().get(0), CENTER,
                ENTRY_HEADER_FONT_SIZE, BLACK, "نوع القيد", true
        );

        XWPFTableCell quantityHeaderCell = entryHeaderRow.getCell(3);
        quantityHeaderCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        quantityHeaderCell.setColor(LIGHT_GRAY);
        formatParagraph(quantityHeaderCell.getParagraphs().get(0), CENTER,
                ENTRY_HEADER_FONT_SIZE, BLACK, "الكمية", true
        );

        XWPFTableCell priceHeaderCell = entryHeaderRow.getCell(4);
        priceHeaderCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        priceHeaderCell.setColor(LIGHT_GRAY);
        formatParagraph(priceHeaderCell.getParagraphs().get(0), CENTER,
                ENTRY_HEADER_FONT_SIZE, BLACK, "السعر", true
        );

        XWPFTableCell dateCreatedHeaderCell = entryHeaderRow.getCell(5);
        dateCreatedHeaderCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        dateCreatedHeaderCell.setColor(LIGHT_GRAY);
        formatParagraph(dateCreatedHeaderCell.getParagraphs().get(0), CENTER,
                ENTRY_HEADER_FONT_SIZE, BLACK, "تاريخ الإنشاء", true
        );

        XWPFTableCell dateUpdatedHeaderCell = entryHeaderRow.getCell(6);
        dateUpdatedHeaderCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        dateUpdatedHeaderCell.setColor(LIGHT_GRAY);
        formatParagraph(dateUpdatedHeaderCell.getParagraphs().get(0), CENTER,
                ENTRY_HEADER_FONT_SIZE, BLACK, "تاريخ التعديل", true
        );

        XWPFTableCell notesHeaderCell = entryHeaderRow.getCell(7);
        notesHeaderCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        notesHeaderCell.setColor(LIGHT_GRAY);
        formatParagraph(notesHeaderCell.getParagraphs().get(0), CENTER,
                ENTRY_HEADER_FONT_SIZE, BLACK, "ملاحظات", true
        );

        row++;
    }

    private void addMarketContent(XWPFTable mainTable, List<FBRSPrintableUserEntry> marketData,
                                  boolean isDetailed, boolean isSingleDay) {
        for (; dataIndex < marketData.size(); dataIndex++) {
            FBRSPrintableUserEntry sellerData = marketData.get(dataIndex);

            // Adding the whole seller into a new column if just one cell available
            if (row == MAX_ROW_COUNT - 1) {
                setCellBorders(mainTable.getRow(row).getCell(column),
                        new boolean[]{false, false, true, false}
                );
                formatParagraph(mainTable.getRow(row).getCell(column).getParagraphs().get(0),
                        CENTER, 8, BLACK, " ", false
                );
                addRow();
            }

            // Checking if table has enough cells for seller entries
            if (isInvalidCell() || sellerData.rowCount(isDetailed) > getRemainingCells()) {
                break;
            }

            XWPFTableRow headerRow = mainTable.getRow(row);

            XWPFTableCell sellerHeaderCell = headerRow.getCell(column);
            sellerHeaderCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            sellerHeaderCell.setColor(LIGHT_GRAY);
            formatParagraph(sellerHeaderCell.getParagraphs().get(0), CENTER, HEADER_FONT_SIZE, BLACK,
                    String.format("%s", sellerData.getUser().getName())
                    , false
            );

            setCellBorders(headerRow.getCell(column),
                    new boolean[]{false, false, true, false}
            );
            addRow();

            XWPFTableRow arrearsCountRow = mainTable.getRow(row);
            XWPFTableCell arrearsCountCell = arrearsCountRow.getCell(column);
            arrearsCountCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            arrearsCountCell.setColor(LIGHTER_GRAY);
            formatParagraph(arrearsCountCell.getParagraphs().get(0), LEFT, HEADER_FONT_SIZE, BLACK,
                    String.format("متأخرات: %7d", sellerData.getArrearsCount())
                    , false
            );

            setCellBorders(arrearsCountRow.getCell(column),
                    new boolean[]{false, false, true, false}
            );
            addRow();

            if (isDetailed || sellerData.isUserDetailed()) {
                for (Entry entry : sellerData.getTodaysEntries()) {
                    XWPFTableRow entryRow = mainTable.getRow(row);

                    boolean isSell = entry.getGiverId() == sellerData.getUser().getId();
                    String otherUserName = model.getUserById(isSell ? entry.getTakerId() : entry.getGiverId()).getName();
                    if (isSell) otherUserName = "بيع لـ " + otherUserName;

                    XWPFTableCell entryCell = entryRow.getCell(column);
                    entryCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    formatParagraph(entryCell.getParagraphs().get(0), LEFT, CELL_FONT_SIZE, BLACK,
                            isSingleDay ? String.format(ENTRY_CELL_NORMAL_FORMAT,
                                    entry.getQuantity(), entry.getPrice(), otherUserName)
                                    : String.format(ENTRY_CELL_DETAILED_FORMAT,
                                    CELL_DATE_FORMAT.format(entry.getDateCreated()),
                                    entry.getQuantity(), entry.getPrice(),
                                    otherUserName),
                            false
                    );

                    setCellBorders(entryCell,
                            new boolean[]{false, false, true, false}
                    );
                    addRow();
                }
            } else {
                XWPFTableRow todaysCountRow = mainTable.getRow(row);
                XWPFTableCell todaysCountCell = todaysCountRow.getCell(column);
                todaysCountCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                todaysCountCell.setColor(LIGHTER_GRAY);
                formatParagraph(todaysCountCell.getParagraphs().get(0), LEFT, HEADER_FONT_SIZE,
                        BLACK, String.format("بُكس اليوم: %5d", sellerData.todaysBuksaCount()),
                        false
                );

                setCellBorders(todaysCountRow.getCell(column),
                        new boolean[]{false, false, true, false}
                );
                addRow();
            }
            int buksaSum = sellerData.getArrearsCount() + sellerData.todaysBuksaCount();
            marketBuksaCount += buksaSum - sellerData.getReturnedToday();

            headerRow = mainTable.getRow(row);
            XWPFTableCell buksaSumCell = headerRow.getCell(column);
            buksaSumCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            formatParagraph(buksaSumCell.getParagraphs().get(0), LEFT, CELL_FONT_SIZE, BLACK,
                    (sellerData.getReturnedToday() != 0) ? String.format("المجموع: %d - %d = %d",
                            buksaSum, sellerData.getReturnedToday(),
                            buksaSum - sellerData.getReturnedToday())
                            : String.format("المجموع: %d", buksaSum),
                    false
            );

            setCellBorders(headerRow.getCell(column),
                    new boolean[]{false, false, true, true}
            );
            addRow();
        }
    }

    private void addSellersContent(XWPFTable table, List<Seller> sellers) {
        for (; dataIndex < sellers.size() && row < MAX_SELLERS_ROW_COUNT; dataIndex++) {
            Seller seller = sellers.get(dataIndex);

            XWPFTableRow sellerRow = table.getRow(row);

            XWPFTableCell numberCell = sellerRow.getCell(0);
            numberCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            formatParagraph(numberCell.getParagraphs().get(0), CENTER,
                    SELLER_CELL_FONT_SIZE, BLACK, String.valueOf(dataIndex + 1), false
            );

            XWPFTableCell darshKeyCell = sellerRow.getCell(1);
            darshKeyCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            formatParagraph(darshKeyCell.getParagraphs().get(0), CENTER,
                    SELLER_CELL_FONT_SIZE, BLACK, String.valueOf(seller.getDarshKey()), false
            );

            XWPFTableCell nameCell = sellerRow.getCell(2);
            nameCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            formatParagraph(nameCell.getParagraphs().get(0), CENTER,
                    SELLER_CELL_FONT_SIZE, BLACK, seller.getName(), false
            );

            XWPFTableCell balanceCell = sellerRow.getCell(3);
            balanceCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            formatParagraph(balanceCell.getParagraphs().get(0), CENTER,
                    SELLER_CELL_FONT_SIZE, BLACK, String.valueOf(seller.getBalance()), false
            );

            marketBuksaCount += seller.getBalance();
            row++;
        }
    }

    private void addEntryContent(XWPFTable table, List<Entry> userEntries) {
        for (; dataIndex < userEntries.size() && row < MAX_ENTRY_ROW_COUNT; dataIndex++) {
            Entry entry = userEntries.get(dataIndex);

            XWPFTableRow entryRow = table.getRow(row);

            XWPFTableCell giverCell = entryRow.getCell(0);
            giverCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            formatParagraph(giverCell.getParagraphs().get(0), CENTER,
                    SELLER_CELL_FONT_SIZE, BLACK, model.getUserById(entry.getGiverId()).getName(),
                    false
            );

            XWPFTableCell takerCell = entryRow.getCell(1);
            takerCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            formatParagraph(takerCell.getParagraphs().get(0), CENTER,
                    SELLER_CELL_FONT_SIZE, BLACK, model.getUserById(entry.getTakerId()).getName(),
                    false
            );

            XWPFTableCell entryTypeCell = entryRow.getCell(2);
            entryTypeCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            formatParagraph(entryTypeCell.getParagraphs().get(0), CENTER,
                    SELLER_CELL_FONT_SIZE, BLACK, model.getEntryTypeName(entry.getType()),
                    false
            );

            XWPFTableCell quantityCell = entryRow.getCell(3);
            quantityCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            formatParagraph(quantityCell.getParagraphs().get(0), CENTER,
                    SELLER_CELL_FONT_SIZE, BLACK, String.valueOf(entry.getQuantity()), false
            );

            XWPFTableCell priceCell = entryRow.getCell(4);
            priceCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            formatParagraph(priceCell.getParagraphs().get(0), CENTER,
                    SELLER_CELL_FONT_SIZE, BLACK, String.valueOf(entry.getPrice()), false
            );

            XWPFTableCell dateCreatedCell = entryRow.getCell(5);
            dateCreatedCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            formatParagraph(dateCreatedCell.getParagraphs().get(0), CENTER,
                    SELLER_CELL_FONT_SIZE, BLACK, HEADER_DATE_FORMAT.format(entry.getDateCreated())
                    , false
            );

            XWPFTableCell dateUpdatedCell = entryRow.getCell(6);
            dateUpdatedCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            formatParagraph(dateUpdatedCell.getParagraphs().get(0), CENTER,
                    SELLER_CELL_FONT_SIZE, BLACK, HEADER_DATE_FORMAT.format(entry.getDateUpdated()),
                    false
            );

            XWPFTableCell commentCell = entryRow.getCell(7);
            commentCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            formatParagraph(commentCell.getParagraphs().get(0), CENTER,
                    SELLER_CELL_FONT_SIZE, BLACK, entry.getComment(), false
            );

            row++;
        }
    }

    /**
     * @param cell    the cell to set its borders.
     * @param borders the borders in the format [left, top, right, bottom]
     */
    private void setCellBorders(XWPFTableCell cell, boolean[] borders) {
        CTTc ctTc = cell.getCTTc();
        CTTcPr tcPr = ctTc.addNewTcPr();
        CTTcBorders allBorders = tcPr.addNewTcBorders();

        BigInteger borderSize = new BigInteger(BORDER_THICKNESS);
        CTBorder border;
        if (borders[0]) {
            border = allBorders.addNewLeft();
            border.setVal(STBorder.THICK);
            border.setSz(borderSize);
        }
        if (borders[1]) {
            border = allBorders.addNewTop();
            border.setVal(STBorder.THICK);
            border.setSz(borderSize);
        }
        if (borders[2]) {
            border = allBorders.addNewRight();
            border.setVal(STBorder.THICK);
            border.setSz(borderSize);
        }
        if (borders[3]) {
            border = allBorders.addNewBottom();
            border.setVal(STBorder.THICK);
            border.setSz(borderSize);
        }
    }

    private int getRemainingCells() {
        return (MAX_ROW_COUNT - row - 1) + (MAX_ROW_COUNT - 1) * (MAX_COLUMN_COUNT - column - 1);
    }

    private void formatParagraph(XWPFParagraph paragraph, ParagraphAlignment alignment, int fontSize,
                                 String colorRGB, String text, boolean bold) {
        paragraph.setAlignment(alignment);
        XWPFRun paragraphRun = paragraph.createRun();
        paragraphRun.setFontSize(fontSize);
        paragraphRun.setColor(colorRGB);
        paragraphRun.setText(text);
        paragraphRun.setBold(bold);
        setSingleLineSpacing(paragraph);

        CTP ctp = paragraph.getCTP();
        CTPPr ctppr = ctp.getPPr();
        if (ctppr == null) ctppr = ctp.addNewPPr();
        ctppr.addNewBidi().setVal(STOnOff.ON);
    }

    private void mergeCellsHorizontal(XWPFTableRow row, int fromCell, int toCell) {
        XWPFTableCell cell = row.getCell(fromCell);
        // The first merged cell is set with RESTART merge value
        cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
        for (int cellIndex = fromCell + 1; cellIndex <= toCell; cellIndex++) {
            cell = row.getCell(cellIndex);
            // Cells which join (merge) the first one, are set with CONTINUE
            cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
        }
    }

    private void mergeCellsVertically(XWPFTable table, int col, int fromRow, int toRow) {
        XWPFTableCell cell = table.getRow(fromRow).getCell(col);
        // The first merged cell is set with RESTART merge value
        cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);
        for (int rowIndex = fromRow + 1; rowIndex <= toRow; rowIndex++) {
            cell = table.getRow(rowIndex).getCell(col);
            // Cells which join (merge) the first one, are set with CONTINUE
            cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
        }
    }

    private void setSingleLineSpacing(XWPFParagraph para) {
        CTPPr ppr = para.getCTP().getPPr();
        if (ppr == null) ppr = para.getCTP().addNewPPr();
        CTSpacing spacing = ppr.isSetSpacing() ? ppr.getSpacing() : ppr.addNewSpacing();
        spacing.setAfter(BigInteger.valueOf(0));
        spacing.setBefore(BigInteger.valueOf(0));
        spacing.setLineRule(STLineSpacingRule.AUTO);
        spacing.setLine(BigInteger.valueOf(240));
    }

    private boolean isInvalidCell() {
        return column > MAX_COLUMN_COUNT - 1;
    }

    private void addRow() {
        row++;
        if (row >= MAX_ROW_COUNT) {
            row = 1;
            column++;
        }
    }

    private void reset() {
        row = 0;
        column = 0;
    }

    private void freshStartNewReport() {
        row = 0;
        column = 0;
        dataIndex = 0;
        marketBuksaCount = 0;
    }

}