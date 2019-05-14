package org.girlscouts.vtk.osgi.component.impl;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Number;
import jxl.write.*;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.osgi.component.util.CouncilRpt;
import org.girlscouts.vtk.models.Contact;
import org.girlscouts.vtk.models.CouncilRptBean;
import org.girlscouts.vtk.osgi.component.AdminReportExcelGenerator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component(service = {AdminReportExcelGenerator.class}, immediate = true, name = "org.girlscouts.vtk.osgi.component.impl.AdminReportExcelGeneratorImpl")
public class AdminReportExcelGeneratorImpl implements AdminReportExcelGenerator {
    private static Logger log = LoggerFactory.getLogger(AdminReportExcelGeneratorImpl.class);
    @Reference
    CouncilRpt councilRpt;

    @Override
    public void generateReport(ApiConfig config, OutputStream outputStream) {
        WritableWorkbook myFirstWbook = null;
        try {
            myFirstWbook = Workbook.createWorkbook(outputStream);
            WritableSheet excelSheet = myFirstWbook.createSheet("Report", 0);
            addHeaders(excelSheet);
            addData(config, excelSheet);
            myFirstWbook.write();
        } catch (Exception e) {
            log.error("Error occurred generating Excel: ", e);
        } finally {
            if (myFirstWbook != null) {
                try {
                    myFirstWbook.close();
                } catch (Exception e) {
                    log.error("Error occurred: ", e);
                }
            }
        }
    }

    private void addData(ApiConfig config, WritableSheet excelSheet) throws WriteException {
        String cid = String.valueOf(config.getUser().getAdminCouncilId());
        List<CouncilRptBean> container = councilRpt.getRpt(cid, config);
        List<String> ageGroups = new ArrayList<>();
        ageGroups.add("brownie");
        ageGroups.add("daisy");
        ageGroups.add("junior");
        ageGroups.add("cadette");
        ageGroups.add("senior");
        ageGroups.add("ambassador");
        ageGroups.add("multi-level");
        try {
            container.sort((CouncilRptBean o1, CouncilRptBean o2) -> String.valueOf(o1.getAgeGroup()).toLowerCase().compareTo(String.valueOf(o2.getAgeGroup()).toLowerCase()));
        } catch (Exception e) {
            log.error("Failed to sort troops by age for admin report:", e);
        }
        int count = 0;
        for (String ageGroup : ageGroups) {
            try {
                List<CouncilRptBean> brownies = councilRpt.getCollection_byAgeGroup(container, ageGroup);
                Map<String, String> yearPlanNames = councilRpt.getDistinctPlanNamesPath(brownies);
                count++;
                int y = 0;
                Iterator itr = yearPlanNames.keySet().iterator();
                while (itr.hasNext()) {
                    String yearPlanPath = (String) itr.next();
                    String yearPlanName = yearPlanNames.get(yearPlanPath);
                    List<CouncilRptBean> yearPlanNameBeans = councilRpt.getCollection_byYearPlanPath(brownies, yearPlanPath);
                    StringBuilder troopLeadersInfo = new StringBuilder();
                    try {
                        List<Contact> troopLeaders = yearPlanNameBeans.get(0).getTroopLeaders();
                        for (Contact troopLeader : troopLeaders) {
                            String userName = troopLeader.getFirstName() + " " + troopLeader.getLastName();
                            String userEmail = troopLeader.getEmail();
                            troopLeadersInfo.append(userName + ", ");
                        }
                    } catch (Exception e) {
                    }
                    int countAltered = councilRpt.countAltered(yearPlanNameBeans);
                    int countActivity = councilRpt.countActivity(yearPlanNameBeans);
                    y++;
                    Label label = new Label(0, count, ageGroup);
                    label.setCellFormat(getDataStyle());
                    excelSheet.addCell(label);
                    label = new Label(1, count, yearPlanName.replaceAll(",", ""));
                    label.setCellFormat(getDataStyle());
                    excelSheet.addCell(label);
                    Number number = new Number(2, count, yearPlanNameBeans.size());
                    number.setCellFormat(getDataStyle());
                    excelSheet.addCell(number);
                    number = new Number(3, count, countAltered);
                    number.setCellFormat(getDataStyle());
                    excelSheet.addCell(number);
                    number = new Number(4, count, countActivity);
                    number.setCellFormat(getDataStyle());
                    excelSheet.addCell(number);
                }
            } catch (Exception e) {
                log.error("Error occurred: ", e);
            }
        }
    }

    private void addHeaders(WritableSheet excelSheet) throws WriteException {
        Label label = new Label(0, 0, "Grade Level");
        label.setCellFormat(getHeaderStyle());
        excelSheet.addCell(label);
        label = new Label(1, 0, "Year Plan");
        label.setCellFormat(getHeaderStyle());
        excelSheet.addCell(label);
        label = new Label(2, 0, "Troops Adopted");
        label.setCellFormat(getHeaderStyle());
        excelSheet.addCell(label);
        label = new Label(3, 0, "Plans Customized");
        label.setCellFormat(getHeaderStyle());
        excelSheet.addCell(label);
        label = new Label(4, 0, "Plans with Added Activities");
        label.setCellFormat(getHeaderStyle());
        excelSheet.addCell(label);
    }

    private WritableCellFormat getHeaderStyle() {
        WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
        try {
            font.setColour(Colour.BLUE);
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
        return getCellFormat(font);
    }

    private WritableCellFormat getDataStyle() {
        WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD);
        try {
            font.setColour(Colour.BLACK);
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
        return getCellFormat(font);
    }

    private WritableCellFormat getCellFormat(WritableFont font) {
        WritableCellFormat format = new WritableCellFormat(font);
        try {
            format.setAlignment(Alignment.LEFT);
            format.setVerticalAlignment(VerticalAlignment.CENTRE);
            format.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
        return format;
    }
}
