package com.ExpenceManager.expencemanager.service;

import com.ExpenceManager.expencemanager.constants.Constants;
import com.ExpenceManager.expencemanager.dto.CategoryTransactionDto;
import com.ExpenceManager.expencemanager.dto.ChartData;
import com.ExpenceManager.expencemanager.dto.ViewDto;
import com.ExpenceManager.expencemanager.repo.TransactionRepo;
import io.quickchart.QuickChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
@Transactional
public class ChartServiceImpl implements ChartService {

    @Autowired
    TransactionRepo transactionRepo;

    @Autowired
    UtilityService utilityService;

    @Override
    public void setCategoryChartData(ViewDto viewDto) {

        Map<String, Double> incomeGraphData = new TreeMap<>();
        Map<String, Double> expenseGraphData = new TreeMap<>();
        for (CategoryTransactionDto categoryTransaction : viewDto.getCategoryTransactionDtoList()) {
            if (categoryTransaction.getType().equals(Constants.TRANSACTION_TYPE_INCOME)) {
                incomeGraphData.put(categoryTransaction.getCategory().getName(), categoryTransaction.getUsedAmount());
            } else {
                expenseGraphData.put(categoryTransaction.getCategory().getName(), categoryTransaction.getUsedAmount());
            }
        }
        viewDto.setIncomeGraphData(incomeGraphData);
        viewDto.setExpenseGraphData(expenseGraphData);
    }

    @Override
    public void setYearlyChartData(ViewDto viewDto) {
        Map<String, Double> incomeGraphData = new TreeMap<>();
        Map<String, Double> expenseGraphData = new TreeMap<>();
        List<ChartData>incomeChartData=transactionRepo.getChartData(Constants.TRANSACTION_TYPE_INCOME);
        List<ChartData>expenseChartData=transactionRepo.getChartData(Constants.TRANSACTION_TYPE_EXPENSE);
        for (ChartData chartdata:incomeChartData) {
            incomeGraphData.put(String.valueOf(chartdata.getDate()),chartdata.getAmount());
        }
        for (ChartData chartdata:expenseChartData) {
            expenseGraphData.put(String.valueOf(chartdata.getDate()),chartdata.getAmount());
        }
        viewDto.setYearlyIncomeChart(incomeGraphData);
        viewDto.setYearlyExpenseChart(expenseGraphData);
    }

    @Override
    public void getMothlyChart(ViewDto viewDto, int year) {
        Map<String, Double> incomeGraphData = new LinkedHashMap<>();
        Map<String, Double> expenseGraphData = new LinkedHashMap<>();
        List<ChartData>monthwiseIncomeList=transactionRepo.getChartMontData(Constants.TRANSACTION_TYPE_INCOME,year+1900);
        List<ChartData>monthwiseExpenseList=transactionRepo.getChartMontData(Constants.TRANSACTION_TYPE_EXPENSE,year+1900);
        for (ChartData chartdata:monthwiseIncomeList) {
            incomeGraphData.put( utilityService.getMonthString(chartdata.getDate()),chartdata.getAmount());
        }
        for (ChartData chartdata:monthwiseExpenseList) {
            expenseGraphData.put(utilityService.getMonthString(chartdata.getDate()),chartdata.getAmount());
        }
        viewDto.setMonthlyIncomeChart(incomeGraphData);
        viewDto.setMonthlyExpenseChart(expenseGraphData);
    }

    @Override
    public String setLineChartData(String type) throws IOException {
        List<ChartData>data= transactionRepo.getLineChartData(Integer.parseInt(utilityService.getCurrentYear()),Integer.parseInt(utilityService.getCurrentYear())-1,type);
        double[] data1=getCumulativeData(data);
        String currentDataString=getDataString(Arrays.copyOfRange(data1,12,24));
        String previousDataString=getDataString(Arrays.copyOfRange(data1,0,12));
        return crateLineChart(currentDataString,previousDataString);

    }

    private String crateLineChart(String currentDataString, String previousDataString) {
        QuickChart chart = new QuickChart();
        chart.setWidth(500);
        chart.setHeight(300);
        chart.setVersion("2.9.4");
        chart.setConfig("{"
                            + "type: 'line',"
                                + "data: {"
                                    + "labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'],"
                                        + "datasets: ["
                                             + "{"
                                                + "label: 'Last Year',"
                                                    + "lineTension: 0.4,"
                                                        + "data: ["+previousDataString+"],"
                                                             + "fill: false,"
                                                                 + "borderColor: 'blue',"
                                                                     + "},"
                                                                     + "{"
                                                             + "label: 'Current Year',"
                                                        + "lineTension: 0.4,"
                                                    + "data: ["+currentDataString+"],"
                                                + "fill: false,"
                                             + "borderColor: 'green',"//getGradientFillHelper('horizontal', ['red', 'blue'])
                                        + "},"
                                    + "],"
                                  + "},"
                /*+"options: {"

                +"plugins: {"
                +"datalabels: {"
                +"display: true,"
                +"align: 'bottom',"
                +"backgroundColor: '#ccc',"
                +"borderRadius: 3"
                +"},"
                +"}"
                +"}"*/
                            + "}"
        );

        // Print the chart image URL
        System.out.println(chart.getUrl());

        // viewDto.setChartURL(chart.getUrl());

        // Or get the image
       /* byte[] imageBytes = chart.toByteArray();
        // Or write it to disk
        chart.toFile("chart.png");

        String imagePath = "example1.png";

        try {
            // Convert byte array to image and save to file
            byteToImage(imageBytes, imagePath);
            System.out.println("Image created successfully at: " + imagePath);
           // System.out.println("Image created successfully at: " + System.getProperty("java.class.path"));
        } catch (IOException e) {
            System.err.println("Error creating image: " + e.getMessage());
        }*/
        return chart.getUrl();
    }

    private String getDataString(double[] doubles) {
        //todo:check for no values in in-between months
        String dataStr="";
        for (int i=0;i<doubles.length;i++){
            if(doubles[i]==0d && i!=0){
                doubles[i]=doubles[i-1];
            }
            dataStr+=Double.toString( doubles[i]);
            dataStr+=", ";
        }
        return dataStr;
    }

    private double[] getCumulativeData(List<ChartData> dataList) {
        double[] monthlyAmount=new double[24];
        for (ChartData data:dataList
             ) {
            if(data.getYear() == Integer.parseInt( utilityService.getCurrentYear())){
                if(data.getMonth()==1){
                    monthlyAmount[data.getMonth()+11]=data.getAmount();
                    continue;
                }
                monthlyAmount[data.getMonth()+11]=monthlyAmount[data.getMonth()+10]+data.getAmount();
            }
            else {
                if(data.getMonth()==1){
                    monthlyAmount[data.getMonth()-1]=data.getAmount();
                    continue;
                }
                monthlyAmount[data.getMonth()-1]=monthlyAmount[data.getMonth()-2]+data.getAmount();
            }
        }
        return monthlyAmount;
    }

    private static void byteToImage(byte[] imageBytes, String imagePath) throws IOException {
        // Create a BufferedImage from the byte array
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));

        // Save the BufferedImage to a file
        ImageIO.write(bufferedImage, "png", new File(imagePath));
    }
}
