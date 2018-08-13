/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.intygsbestallning.service.export;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyleInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Besok;
import se.inera.intyg.intygsbestallning.persistence.model.Bestallning;
import se.inera.intyg.intygsbestallning.persistence.model.Handelse;
import se.inera.intyg.intygsbestallning.persistence.model.Handling;
import se.inera.intyg.intygsbestallning.persistence.model.Intyg;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandelseTyp;
import se.inera.intyg.intygsbestallning.service.patient.Gender;
import se.inera.intyg.intygsbestallning.service.stateresolver.BesokStatusResolver;
import se.inera.intyg.intygsbestallning.service.stateresolver.ErsattsResolver;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysBean;
import se.inera.intyg.intygsbestallning.service.utredning.BaseUtredningService;
import se.inera.intyg.intygsbestallning.service.utredning.BestallningService;
import se.inera.intyg.intygsbestallning.service.utredning.UtredningService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.AvslutadBestallningListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.BestallningListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.ListAvslutadeBestallningarRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.ListBestallningRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.BaseUtredningListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.ListAvslutadeUtredningarRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.ListUtredningRequest;
import se.inera.intyg.schemas.contract.Personnummer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class XlsxExportServiceImpl extends BaseUtredningService implements XlsxExportService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final int EXTRA_WIDTH_DROPDOWN_BUTTON = 1000;
    private static final String SHEET_TITLE_UTREDNINGAR = "Utredningar";
    private static final String JA = "Ja";
    private static final String NEJ = "Nej";
    private static final String ROBOTO = "Roboto";
    private static final String ROBOTO_MEDIUM = "Roboto Medium";
    private static final int PERSONID_YEAR_END_INDEX = 4;
    private static final int WARNING_FONT_SIZE = 12;
    private static final int MAINHEADER_FONT_SIZE = 12;
    private static final int SUBHEADER_FONT_SIZE = 11;
    private static final int VALUE_FONT_SIZE = 11;

    private static final XSSFColor WC_COLOR_00 = new XSSFColor(new java.awt.Color(255, 255, 255));
    private static final XSSFColor WC_COLOR_07 = new XSSFColor(new java.awt.Color(33, 33, 33));
    private static final XSSFColor WC_COLOR_11 = new XSSFColor(new java.awt.Color(218, 68, 83));

    private static final XSSFColor[] TABLEHEADER_COLORS = {
            new XSSFColor(new java.awt.Color(221, 235, 247)),
            new XSSFColor(new java.awt.Color(252, 228, 214)),
            new XSSFColor(new java.awt.Color(226, 239, 218))
    };

    private static final ImmutableList<TableHeaderDef> TABLE_HEADER_DEF_LIST = ImmutableList.of(
            new TableHeaderDef("ID", ImmutableList.of(
                    "ID"
            )),
            new TableHeaderDef("Landsting", ImmutableList.of(
                    "Landsting"
            )),
            new TableHeaderDef("Utförare", ImmutableList.of(
                    "Vårdenhet namn",
                    "Vårdenhet HSA id",
                    "Vårdgivare namn",
                    "Vårdgivare HSA id"
            )),
            new TableHeaderDef("Invånare", ImmutableList.of(
                    "Invånare NN",
                    "Invånare kön",
                    "Invånare födelseår",
                    "Invånare postort"
            )),
            new TableHeaderDef("Beställning", ImmutableList.of(
                    "Beställning inkom",
                    "Skickat handlingar",
                    "Behov av tolk",
                    "Tolkspråk"
            )),
            new TableHeaderDef("Handlingar", ImmutableList.of(
                    "Handlingar mottogs"
            )),
            new TableHeaderDef("Utredning", ImmutableList.of(
                    "Utredningstyp",
                    "Utredning slutdatum",
                    "Utredning status",
                    "Orsak avslut",
                    "Utredningen ersätts"
            )),
            new TableHeaderDef("Utlåtande", ImmutableList.of(
                    "Utlåtande skickat",
                    "Utlåtande mottaget")),
            new TableHeaderDef("Komplettering", ImmutableList.of(
                    "Komplettering begärd",
                    "Komplettering skickad",
                    "Komplettering mottagen",
                    "Komplettreing slutdatum"
            )),
            new TableHeaderDef("Besök", ImmutableList.of(
                    "Besök id",
                    "Besök kallelse skickad",
                    "Besök starttid",
                    "Besök sluttid",
                    "Besök profession",
                    "Besök profession namn",
                    "Besök kallelsedatum",
                    "Besök kallelse form",
                    "Tolk status",
                    "Tolk språk",
                    "Besök status",
                    "Besök ersätts"
            )),
            new TableHeaderDef("Avvikelse", ImmutableList.of(
                    "Avvikelse tidpunkt",
                    "Avvikelse orsakad av",
                    "Invånare uteblev"
            ))
    );

    private XSSFCellStyle warningStyle;
    private List<XSSFCellStyle> mainHeaderStyleList;
    private XSSFCellStyle subHeaderStyle;
    private XSSFCellStyle valueStyle;
    private XSSFCellStyle dateValueStyle;
    private XSSFCellStyle dateTimeValueStyle;
    private XSSFFont mainHeaderFont;
    private XSSFFont warningFont;
    private XSSFFont subHeaderFont;
    private XSSFFont valueFont;

    @Autowired
    private UtredningService utredningService;

    @Autowired
    private BestallningService bestallningService;

    @Autowired
    private BusinessDaysBean businessDays;

    @Override
    public byte[] export(String landstingHsaId, ListUtredningRequest request) {
        request.setPerformPaging(false);
        return export(utredningService.findExternForfraganByLandstingHsaIdWithFilter(landstingHsaId, request).getUtredningar());
    }

    @Override
    public byte[] export(String landstingHsaId, ListAvslutadeUtredningarRequest request) {
        request.setPerformPaging(false);
        return export(utredningService.findAvslutadeExternForfraganByLandstingHsaIdWithFilter(landstingHsaId, request).getUtredningar());
    }

    @Override
    public byte[] export(String loggedInAtHsaId, ListBestallningRequest request) {
        request.setPerformPaging(false);
        List<BestallningListItem> bestallningar = bestallningService
                .findOngoingBestallningarForVardenhet(loggedInAtHsaId, request)
                .getBestallningar();
        // findOngoingBestallningarForVardenhet doesn't set vardenhet, so we need to do that here
        setVardenhetOnUtredningList(bestallningar);
        return export(bestallningar);
    }

    @Override
    public byte[] export(String loggedInAtHsaId, ListAvslutadeBestallningarRequest request) {
        request.setPerformPaging(false);
        List<AvslutadBestallningListItem> bestallningar = bestallningService
                .findAvslutadeBestallningarForVardenhet(loggedInAtHsaId, request)
                .getBestallningar();
        // findAvslutadeBestallningarForVardenhet doesn't set vardenhet, so we need to do that here
        setVardenhetOnUtredningList(bestallningar);
        return export(bestallningar);
    }

    private void setVardenhetOnUtredningList(List<? extends BaseUtredningListItem> bestallningar) {
        bestallningar.forEach(b -> {
            b.setVardenhetHsaId(b.getUtredning().getBestallning().map(Bestallning::getTilldeladVardenhetHsaId).orElse(null));
        });
        enrichWithVardenhetNames(bestallningar);
    }

    private byte[] export(List<? extends BaseUtredningListItem> utredningList) {
        XSSFWorkbook wb = new XSSFWorkbook();
        setupStyles(wb);

        XSSFSheet sheet = wb.createSheet(SHEET_TITLE_UTREDNINGAR);

        int rowNumber = 0;
        // Warning row
        XSSFRow row = sheet.createRow(rowNumber++);
        XSSFCell cell = row.createCell(0);
        cell.setCellValue("OBS! I tabellen utgör en rad ett besök. En utredningen med flera besök visas alltså på flera rader.");
        cell.setCellStyle(warningStyle);

        // Empty row
        rowNumber++;

        // Main header row
        addMainHeader(sheet, rowNumber++);

        // Sub header row
        int subHeaderRow = rowNumber++;
        int lastHeaderCellIndex = addSubHeader(sheet, subHeaderRow);

        for (BaseUtredningListItem utredningListItem : utredningList) {
            if (utredningListItem.getUtredning().getBesokList().size() == 0) {
                row = sheet.createRow(rowNumber++);
                addUtredningValues(sheet, row, utredningListItem);
            } else {
                for (Besok besok : utredningListItem.getUtredning().getBesokList()) {
                    row = sheet.createRow(rowNumber++);
                    int lastUtredningCellIndex = addUtredningValues(sheet, row, utredningListItem);
                    addBesokValues(row, lastUtredningCellIndex, utredningListItem.getUtredning(), besok);
                }
            }
        }

        if (utredningList.size() > 0) {
            XSSFTable table = sheet.createTable();
            table.setDisplayName("Table1");
            table.setName("Table1");
            for (TableHeaderDef tableHeaderDef : TABLE_HEADER_DEF_LIST) {
                for (int c = 0; c < tableHeaderDef.subHeaderList.size(); c++) {
                    table.addColumn();
                }
            }

            CTTable ctTable = table.getCTTable();
            CTTableStyleInfo tableStyle = ctTable.addNewTableStyleInfo();
            tableStyle.setName("TableStyleMedium9");
            tableStyle.setShowColumnStripes(false);
            tableStyle.setShowRowStripes(true);

            ctTable.addNewAutoFilter();

            AreaReference reference = wb.getCreationHelper().createAreaReference(
                    new CellReference(subHeaderRow, 0), new CellReference(rowNumber - 1, lastHeaderCellIndex - 1));
            table.setCellReferences(reference);
        }

        for (int i = 1; i < lastHeaderCellIndex; i++) {
            sheet.autoSizeColumn(i);
            // Include width of drop down button
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + EXTRA_WIDTH_DROPDOWN_BUTTON);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            wb.write(baos);
            baos.flush();
        } catch (IOException e) {
            LOG.error("IOException while generating excel report", e);
            throw new IbServiceException(IbErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM, "IOException while generating excel report");
        }
        return baos.toByteArray();
    }

    private void addMainHeader(XSSFSheet sheet, int rowIndex) {
        XSSFRow row = sheet.createRow(rowIndex);
        int cellIndex = 0, styleIndex = 0;
        for (TableHeaderDef tableHeaderDef : TABLE_HEADER_DEF_LIST) {
            addMainHeaderColumn(sheet, row, cellIndex, tableHeaderDef.getSubHeaderList().size(), tableHeaderDef.getmainHeader(),
                    mainHeaderStyleList.get(styleIndex % mainHeaderStyleList.size()));
            cellIndex += tableHeaderDef.getSubHeaderList().size();
            styleIndex++;
        }
    }

    private void addMainHeaderColumn(XSSFSheet sheet, XSSFRow row, int fromColumn, int columnSpan, String value, XSSFCellStyle style) {
        XSSFCell cell = row.createCell(fromColumn);
        cell.setCellValue(value);
        cell.setCellStyle(style);
        if (columnSpan > 1) {
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), fromColumn, fromColumn + columnSpan - 1));
        }
    }

    private int addSubHeader(XSSFSheet sheet, int rowIndex) {
        XSSFRow row = sheet.createRow(rowIndex);
        int cellIndex = 0;
        for (TableHeaderDef tableHeaderDef : TABLE_HEADER_DEF_LIST) {
            for (String subHeader : tableHeaderDef.getSubHeaderList()) {
                XSSFCell cell = row.createCell(cellIndex++);
                cell.setCellValue(subHeader);
                cell.setCellStyle(subHeaderStyle);
            }
        }
        return cellIndex;
    }

    private int addUtredningValues(XSSFSheet sheet, XSSFRow row, BaseUtredningListItem utredningListItem) {

        Utredning utredning = utredningListItem.getUtredning();

        int cellIndex = 0;

        // Id
        XSSFCell cell = row.createCell(cellIndex++);
        cell.setCellValue(utredningListItem.getUtredningsId());
        cell.setCellStyle(valueStyle);

        cellIndex = addLandstingValues(utredning, row, cellIndex);
        cellIndex = addUtforareValues(utredningListItem, row, cellIndex);
        cellIndex = addInvanareValues(utredning, row, cellIndex);
        cellIndex = addBestallningValues(utredning, row, cellIndex);
        cellIndex = addHandlingarValues(utredning, row, cellIndex);
        cellIndex = addUtredningValues(utredning, row, cellIndex);
        cellIndex = addUtlatandeValues(utredning, row, cellIndex);
        cellIndex = addKompletteringValues(utredning, row, cellIndex);

        return cellIndex;
    }

    private int addLandstingValues(Utredning utredning, XSSFRow row, int cellIndex) {
        int localCellIndex = cellIndex;
        XSSFCell cell;
        cell = row.createCell(localCellIndex++);
        if (utredning.getExternForfragan().isPresent()
                && StringUtils.isNotBlank(utredning.getExternForfragan().get().getLandstingHsaId())) {
            try {
                cell.setCellValue(hsaOrganizationsService.getVardgivareInfo(
                        utredning.getExternForfragan().get().getLandstingHsaId()).getNamn());
            } catch (RuntimeException re) {
                LOG.warn("Failed getVardgivareInfo", utredning.getExternForfragan().get().getLandstingHsaId(), re);
               cell.setCellValue(utredning.getExternForfragan().get().getLandstingHsaId());
            }
        }
        cell.setCellStyle(valueStyle);
        return localCellIndex;
    }

    private int addUtforareValues(BaseUtredningListItem utredningListItem, XSSFRow row, int cellIndex) {
        int localCellIndex = cellIndex;
        XSSFCell cell;
        cell = row.createCell(localCellIndex++);
        cell.setCellValue(utredningListItem.getVardenhetNamn());
        cell.setCellStyle(valueStyle);

        cell = row.createCell(localCellIndex++);
        cell.setCellValue(utredningListItem.getVardenhetHsaId());
        cell.setCellStyle(valueStyle);

        String vardgivareHsaId = "";
        String vardgivareHsaIdError = null;
        if (StringUtils.isNotBlank(utredningListItem.getVardenhetHsaId())) {
            try {
                vardgivareHsaId = hsaOrganizationsService.getVardgivareOfVardenhet(utredningListItem.getVardenhetHsaId());
            } catch (RuntimeException re) {
                LOG.warn("Failed getVardgivareOfVardenhet", utredningListItem.getVardenhetHsaId(), re);
                vardgivareHsaIdError = "Misslyckades slå upp vårdgivare i hsa";
            }
        }

        String vardgivareNamn = "";
        if (StringUtils.isNotBlank(vardgivareHsaId)) {
            try {
                vardgivareNamn = hsaOrganizationsService.getVardgivareInfo(vardgivareHsaId).getNamn();
            } catch (RuntimeException re) {
                LOG.warn("Failed getVardgivareInfo", vardgivareHsaId, re);
                vardgivareNamn = "Misslyckades slå upp vårdgivare i hsa";
            }
        }
        cell = row.createCell(localCellIndex++);
        cell.setCellValue(vardgivareNamn);
        cell.setCellStyle(valueStyle);

        cell = row.createCell(localCellIndex++);
        if (vardgivareHsaIdError != null) {
            cell.setCellValue(vardgivareHsaIdError);
        } else {
            cell.setCellValue(vardgivareHsaId);
        }
        cell.setCellStyle(valueStyle);
        return localCellIndex;
    }

    private int addInvanareValues(Utredning utredning, XSSFRow row, int cellIndex) {
        int localCellIndex = cellIndex;
        XSSFCell cell;
        cell = row.createCell(localCellIndex++);
        if (!StringUtils.isBlank(utredning.getInvanare().getFornamn()) && !StringUtils.isBlank(utredning.getInvanare().getEfternamn())) {
            cell.setCellValue(utredning.getInvanare().getFornamn().substring(0, 1).toUpperCase()
                    + utredning.getInvanare().getEfternamn().substring(0, 1).toUpperCase());
        }
        cell.setCellStyle(valueStyle);

        Optional<Personnummer> personnummer = Optional.empty();
        if (!StringUtils.isBlank(utredning.getInvanare().getPersonId())) {
            personnummer = Personnummer.createPersonnummer(utredning.getInvanare().getPersonId());
        }

        cell = row.createCell(localCellIndex++);
        if (personnummer.isPresent()) {
            cell.setCellValue(Gender.getGenderFromPersonnummer(personnummer.get()).getDescription());
        }
        cell.setCellStyle(valueStyle);

        cell = row.createCell(localCellIndex++);
        if (personnummer.isPresent()) {
            cell.setCellValue(personnummer.get().getPersonnummer().substring(0, PERSONID_YEAR_END_INDEX));
        }
        cell.setCellStyle(valueStyle);

        cell = row.createCell(localCellIndex++);
        cell.setCellValue(utredning.getInvanare().getPostort());
        cell.setCellStyle(valueStyle);
        return localCellIndex;
    }

    private int addBestallningValues(Utredning utredning, XSSFRow row, int cellIndex) {
        int localCellIndex = cellIndex;
        XSSFCell cell;
        cell = row.createCell(localCellIndex++);
        if (utredning.getBestallning().isPresent()) {
            cell.setCellValue(getExcelDate(utredning.getBestallning().get().getOrderDatum()));
        }
        cell.setCellStyle(dateValueStyle);

        cell = row.createCell(localCellIndex++);
        cell.setCellValue(utredning.getHandlingList().isEmpty() ? NEJ : JA);
        cell.setCellStyle(valueStyle);

        cell = row.createCell(localCellIndex++);
        cell.setCellValue(BooleanUtils.toBoolean(utredning.getTolkBehov()) ? JA : NEJ);
        cell.setCellStyle(valueStyle);

        cell = row.createCell(localCellIndex++);
        cell.setCellValue(utredning.getTolkSprak());
        cell.setCellStyle(valueStyle);
        return localCellIndex;
    }

    private int addHandlingarValues(Utredning utredning, XSSFRow row, int cellIndex) {
        int localCellIndex = cellIndex;
        final XSSFCell inkomCell = row.createCell(localCellIndex++);
        utredning.getHandlingList().stream()
                .filter(handling -> handling.getInkomDatum() != null)
                .max(Comparator.comparing(Handling::getInkomDatum))
                .map(handling -> getExcelDate(handling.getInkomDatum()))
                .ifPresent((excelDate) -> inkomCell.setCellValue(excelDate));
        inkomCell.setCellStyle(dateValueStyle);
        return localCellIndex;
    }

    private int addUtredningValues(Utredning utredning, XSSFRow row, int cellIndex) {
        int localCellIndex = cellIndex;
        XSSFCell cell;
        cell = row.createCell(localCellIndex++);
        cell.setCellValue(utredning.getUtredningsTyp().getLabel());
        cell.setCellStyle(valueStyle);

        final XSSFCell intygSistaDatumCell = row.createCell(localCellIndex++);
        utredning.getIntygList().stream()
                .filter(i -> !i.isKomplettering())
                .max(Comparator.comparing(Intyg::getSistaDatum))
                .map(intyg -> getExcelDate(intyg.getSistaDatum()))
                .ifPresent((excelDate) -> intygSistaDatumCell.setCellValue(excelDate));
        intygSistaDatumCell.setCellStyle(dateValueStyle);

        cell = row.createCell(localCellIndex++);
        cell.setCellValue(utredning.getStatus().getLabel());
        cell.setCellStyle(valueStyle);

        cell = row.createCell(localCellIndex++);
        if (utredning.getAvbrutenOrsak() != null) {
            cell.setCellValue(utredning.getAvbrutenOrsak().getLabel());
        }
        cell.setCellStyle(valueStyle);

        cell = row.createCell(localCellIndex++);
        cell.setCellValue(ErsattsResolver.resolveUtredningErsatts(utredning, businessDays) ? JA : NEJ);
        cell.setCellStyle(valueStyle);
        return localCellIndex;
    }

    private int addUtlatandeValues(Utredning utredning, XSSFRow row, int cellIndex) {
        int localCellIndex = cellIndex;
        final XSSFCell intygSkickatDatumCell = row.createCell(localCellIndex++);
        utredning.getIntygList().stream()
                .filter(i -> !i.isKomplettering() && i.getSkickatDatum() != null)
                .max(Comparator.comparing(Intyg::getSkickatDatum))
                .map(intyg -> getExcelDate(intyg.getSkickatDatum()))
                .ifPresent((excelDate) -> intygSkickatDatumCell.setCellValue(excelDate));
        intygSkickatDatumCell.setCellStyle(dateValueStyle);

        final XSSFCell intygMottagetDatumCell = row.createCell(localCellIndex++);
        utredning.getIntygList().stream()
                .filter(i -> !i.isKomplettering() && i.getMottagetDatum() != null)
                .max(Comparator.comparing(Intyg::getMottagetDatum))
                .map(intyg -> getExcelDate(intyg.getMottagetDatum()))
                .ifPresent((excelDate) -> intygMottagetDatumCell.setCellValue(excelDate));
        intygMottagetDatumCell.setCellStyle(dateValueStyle);
        return localCellIndex;
    }

    private int addKompletteringValues(Utredning utredning, XSSFRow row, int cellIndex) {
        int localCellIndex = cellIndex;
        final XSSFCell kompletteringBegardDatumCell = row.createCell(localCellIndex++);
        utredning.getHandelseList().stream()
                .filter(handelse -> handelse.getHandelseTyp() == HandelseTyp.KOMPLETTERINGSBEGARAN_MOTTAGEN)
                .max(Comparator.comparing(Handelse::getSkapad))
                .map(handelse -> getExcelDate(handelse.getSkapad()))
                .ifPresent((excelDate) -> kompletteringBegardDatumCell.setCellValue(excelDate));
        kompletteringBegardDatumCell.setCellStyle(dateValueStyle);

        final XSSFCell kompletteringSkickadDatumCell = row.createCell(localCellIndex++);
        utredning.getIntygList().stream()
                .filter(i -> i.isKomplettering() && i.getSkickatDatum() != null)
                .max(Comparator.comparing(Intyg::getSkickatDatum))
                .map(intyg -> getExcelDate(intyg.getSkickatDatum()))
                .ifPresent((excelDate) -> kompletteringSkickadDatumCell.setCellValue(excelDate));
        kompletteringSkickadDatumCell.setCellStyle(dateValueStyle);

        final XSSFCell kompletteringMottagenDatumCell = row.createCell(localCellIndex++);
        utredning.getIntygList().stream()
                .filter(i -> i.isKomplettering() && i.getMottagetDatum() != null)
                .max(Comparator.comparing(Intyg::getMottagetDatum))
                .map(intyg -> getExcelDate(intyg.getMottagetDatum()))
                .ifPresent((excelDate) -> kompletteringMottagenDatumCell.setCellValue(excelDate));
        kompletteringMottagenDatumCell.setCellStyle(dateValueStyle);

        final XSSFCell kompletteringSistaDatumCell = row.createCell(localCellIndex++);
        utredning.getIntygList().stream()
                .filter(i -> i.isKomplettering())
                .max(Comparator.comparing(Intyg::getSistaDatum))
                .map(intyg -> getExcelDate(intyg.getSistaDatum()))
                .ifPresent((excelDate) -> kompletteringSistaDatumCell.setCellValue(excelDate));
        kompletteringSistaDatumCell.setCellStyle(dateValueStyle);
        return localCellIndex;
    }

    private int addBesokValues(XSSFRow row, int cellIndex, Utredning utredning, Besok besok) {
        int localCellIndex = cellIndex;
        XSSFCell cell = row.createCell(localCellIndex++);
        cell.setCellValue(besok.getId());
        cell.setCellStyle(valueStyle);

        cell = row.createCell(localCellIndex++);
        cell.setCellValue(getExcelDate(besok.getKallelseDatum()));
        cell.setCellStyle(dateValueStyle);

        cell = row.createCell(localCellIndex++);
        cell.setCellValue(getExcelDate(besok.getBesokStartTid()));
        cell.setCellStyle(dateTimeValueStyle);

        cell = row.createCell(localCellIndex++);
        cell.setCellValue(getExcelDate(besok.getBesokSlutTid()));
        cell.setCellStyle(dateTimeValueStyle);

        cell = row.createCell(localCellIndex++);
        cell.setCellValue(besok.getDeltagareProfession().getLabel());
        cell.setCellStyle(valueStyle);

        cell = row.createCell(localCellIndex++);
        cell.setCellValue(besok.getDeltagareFullstandigtNamn());
        cell.setCellStyle(valueStyle);

        cell = row.createCell(localCellIndex++);
        cell.setCellValue(getExcelDate(besok.getKallelseDatum()));
        cell.setCellStyle(dateValueStyle);

        cell = row.createCell(localCellIndex++);
        cell.setCellValue(besok.getKallelseForm().getLabel());
        cell.setCellStyle(valueStyle);

        cell = row.createCell(localCellIndex++);
        cell.setCellValue(besok.getTolkStatus().getLabel());
        cell.setCellStyle(valueStyle);

        cell = row.createCell(localCellIndex++);
        cell.setCellValue(utredning.getTolkSprak());
        cell.setCellStyle(valueStyle);

        cell = row.createCell(localCellIndex++);
        cell.setCellValue(BesokStatusResolver.resolveStaticStatus(besok).getLabel());
        cell.setCellStyle(valueStyle);

        cell = row.createCell(localCellIndex++);
        cell.setCellValue(BooleanUtils.toBoolean(besok.getErsatts()) ? JA : NEJ);
        cell.setCellStyle(valueStyle);

        if (besok.getAvvikelse() != null) {
            cell = row.createCell(localCellIndex++);
            cell.setCellValue(getExcelDate(besok.getAvvikelse().getTidpunkt()));
            cell.setCellStyle(dateTimeValueStyle);

            cell = row.createCell(localCellIndex++);
            cell.setCellValue(besok.getAvvikelse().getOrsakatAv().getLabel());
            cell.setCellStyle(valueStyle);

            cell = row.createCell(localCellIndex++);
            cell.setCellValue(BooleanUtils.toBoolean(besok.getAvvikelse().getInvanareUteblev()) ? JA : NEJ);
            cell.setCellStyle(valueStyle);
        }
        return localCellIndex;
    }

    private double getExcelDate(LocalDateTime dateTime) {
        return DateUtil.getExcelDate(Date.from(dateTime.toInstant(ZoneOffset.UTC)));
    }

    private void setupStyles(XSSFWorkbook wb) {
        warningFont = buildFont(wb, WARNING_FONT_SIZE, ROBOTO_MEDIUM, WC_COLOR_11, false, false);
        mainHeaderFont = buildFont(wb, MAINHEADER_FONT_SIZE, ROBOTO_MEDIUM, WC_COLOR_07, false, false);
        subHeaderFont = buildFont(wb, SUBHEADER_FONT_SIZE, ROBOTO_MEDIUM, WC_COLOR_00, false, false);
        valueFont = buildFont(wb, VALUE_FONT_SIZE, ROBOTO, WC_COLOR_07, false, false);

        warningStyle = wb.createCellStyle();
        warningStyle.setFont(warningFont);

        mainHeaderStyleList = new ArrayList<>();
        for (XSSFColor color : TABLEHEADER_COLORS) {
            XSSFCellStyle style = wb.createCellStyle();
            style.setFont(mainHeaderFont);
            style.setFillForegroundColor(color);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            mainHeaderStyleList.add(style);
        }

        subHeaderStyle = wb.createCellStyle();
        subHeaderStyle.setFont(subHeaderFont);

        valueStyle = wb.createCellStyle();
        valueStyle.setFont(valueFont);

        XSSFDataFormat df1 = wb.createDataFormat();
        dateValueStyle = wb.createCellStyle();
        dateValueStyle.setFont(valueFont);
        dateValueStyle.setDataFormat(df1.getFormat("m/d/yy")); // Is converted to user locale when viewed in excel.

        XSSFDataFormat df2 = wb.createDataFormat();
        dateTimeValueStyle = wb.createCellStyle();
        dateTimeValueStyle.setFont(valueFont);
        dateTimeValueStyle.setDataFormat(df2.getFormat("m/d/yy h:mm"));
    }

    private XSSFFont buildFont(XSSFWorkbook wb, int heightInPoints, String fontName, XSSFColor color, boolean bold, boolean underline) {
        XSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short) heightInPoints);
        font.setFontName(fontName);
        font.setColor(color);
        font.setBold(bold);
        font.setUnderline(underline ? XSSFFont.U_SINGLE : XSSFFont.U_NONE);
        return font;
    }

    private static class TableHeaderDef {
        private String mainHeader;
        private List<String> subHeaderList;
        TableHeaderDef(String mainHeader, List<String> subHeaderList) {
            this.mainHeader = mainHeader;
            this.subHeaderList = subHeaderList;
        }

        public String getmainHeader() {
            return mainHeader;
        }

        public List<String> getSubHeaderList() {
            return subHeaderList;
        }
    }

}
