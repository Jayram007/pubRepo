package com.ExpenceManager.expencemanager.service;

import com.ExpenceManager.expencemanager.constants.Constants;
import com.ExpenceManager.expencemanager.dto.BudgetDto;
import com.ExpenceManager.expencemanager.dto.CategoryDto;
import com.ExpenceManager.expencemanager.entity.Budget;
import com.ExpenceManager.expencemanager.entity.Transaction;
import com.ExpenceManager.expencemanager.repo.BudgetRepo;
import com.ExpenceManager.expencemanager.repo.CategoryRepo;
import com.ExpenceManager.expencemanager.repo.TransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class BudgetServiceImpl implements BudgetService {
    @Autowired
    BudgetRepo budgetRepo;

    @Autowired
    CategoryRepo categoryRepo;

    @Autowired
    CategoryService categoryService;

    @Autowired
    ImpValueService impValueService;

    @Autowired
    UtilityService utilityService;

    @Autowired
    TransactionRepo transactionRepo;

    @Override
    public Budget dtoToEntity(BudgetDto budgetDto) {

        Budget budget=new Budget();
        budget.setCategory(categoryRepo.findById(budgetDto.getCategoryId()));
        if(budgetDto.getId() != 0){
        budget.setId(budgetDto.getId());
        }
        budget.setYear(budgetDto.getYear());
        budget.setMonth(budgetDto.getMonth());
        if(budgetDto.getUsedAmount()==null || budgetDto.getUsedAmount()== 0){
            Double usedAmount=transactionRepo.sumAmount("expense",utilityService.getCurrentMonthsFirstDate(),utilityService.getCurrentMonthsLastDate());
           if(usedAmount !=null)
            budget.setUsedAmount(usedAmount);
           else
            budget.setUsedAmount(0D);
        }
        budget.setBudgetAmount(budgetDto.getBudgetAmount());
        budget.setTotalBudgetAmount(budgetDto.getTotalBudgetAmount());
        return budget;
    }

    @Override
    public BudgetDto entityToDto(Budget budget) {

        BudgetDto budgetDto=new BudgetDto();
        budgetDto.setId(budget.getId());
        budgetDto.setMonth(budget.getMonth());
        budgetDto.setYear(budget.getYear());
        if(budget.getCategory()!=null) {
        budgetDto.setCategoryId(budget.getCategory().getId());
            budgetDto.setCategoryDto(categoryService.entityToDto(budget.getCategory()));
        }
        budgetDto.setTotalBudgetAmount(budget.getTotalBudgetAmount());
        if(budget.getBudgetAmount() == null){
            budgetDto.setBudgetAmount(0D);
        }
        else {
            budgetDto.setBudgetAmount(budget.getBudgetAmount());
        }
        if(budget.getUsedAmount()==null){
            budgetDto.setUsedAmount(0D);
        }
        else {
            budgetDto.setUsedAmount(budget.getUsedAmount());
        }
        budgetDto.setMessage(getBudgetHomPageMessage(budget));
        budgetDto.setPercentage(getPercentage(budget).toString()+" %" );
        if(budgetDto.getPercentage()==null)
            budgetDto.setPercentage(" 0%");

        return budgetDto;
    }

    private String getBudgetHomPageMessage(Budget budget) {
        if(budget.getUsedAmount()!=null){
            int day=Integer.parseInt(utilityService.getCurrentMonthsLastDate().toString().substring(8));
            int perDayUse=utilityService.getPerDayUse(budget.getUsedAmount(),utilityService.getCurrentDate());
            int expense=perDayUse;
            if(budget.getBudgetAmount()<budget.getUsedAmount()){
                return "You have exceed the limit by "+(budget.getBudgetAmount()-budget.getUsedAmount());
            }
            for(int i=1;i<day;i++){
                if(expense>budget.getBudgetAmount()){
                    if(i==1) {
                        return Constants.LIMIT_WILL_EXCEED_ON + i + " st of this month";
                    }
                    else if (i==2) {
                        return Constants.LIMIT_WILL_EXCEED_ON + i + " nd of this month";
                    } else if (i==3) {
                        return Constants.LIMIT_WILL_EXCEED_ON + i + " rd of this month";
                    }
                    else
                        return Constants.LIMIT_WILL_EXCEED_ON + i +" th of this month";
                }
                expense+=perDayUse;
            }
        }
        return "in limit";
    }

    private Long getPercentage(Budget budget) {
        if(budget.getUsedAmount()==null || budget.getTotalBudgetAmount()==null || budget.getBudgetAmount()==null ){
            return 0L;
        } else if (budget.getUsedAmount()==0D || budget.getTotalBudgetAmount()==0D || budget.getBudgetAmount()==0D) {
            return 0L;
        }
        else
            return  Math.round((budget.getUsedAmount()/budget.getBudgetAmount())*100);
    }

    public String getMessage(Budget budget){
        return Constants.NO_MESSAGE;
    }

    @Override
    public List<BudgetDto> getCurrentBudgetList() {
        String year=utilityService.getCurrentYear();
        String month=utilityService.getCurrentMonth();
        BudgetDto budgetDto=new BudgetDto();
       List<Budget> budgetList=budgetRepo.findByYearAndMonth(year,month);
       List<BudgetDto> budgetDtoList=new ArrayList<>();
       if(!budgetList.isEmpty()) {
           for (Budget budget : budgetList) {
               if (budget.getBudgetAmount()!=null && budget.getUsedAmount()!=null && (budget.getBudgetAmount() != 0 || budget.getUsedAmount() != 0)) {
                   budgetDto = entityToDto(budget);
                   budgetDtoList.add(budgetDto);
               }
           }
       }
        return budgetDtoList;
    }

    @Override
    public List<BudgetDto> getCategoryList() {

        return null;
    }

    @Override
    public List<BudgetDto> createNewBudget(String year, String month, Double totalBudgetAmount) {
        if(isValidBudget(year,month)) {
            List<BudgetDto> budgetDtoList = new ArrayList<BudgetDto>();
            List<CategoryDto> listOfCategory = categoryService.getCategoryList();
            for (CategoryDto categoryDto : listOfCategory) {
                BudgetDto newBudgetDto = new BudgetDto();
                newBudgetDto.setCategoryId(categoryDto.getId());
                newBudgetDto.setYear(year);
                newBudgetDto.setMonth(month);
                newBudgetDto.setCategoryDto(categoryDto);
                newBudgetDto.setBudgetAmount(0D);
                newBudgetDto.setTotalBudgetAmount(totalBudgetAmount);
                budgetDtoList.add(newBudgetDto);
            }
            return budgetDtoList;
        }
        return null;
    }

    private boolean isValidBudget(String year, String month) {
        if(utilityService.getCurrentMonth().compareTo(month)<1) {
           /* int record = budgetRepo.count(year, month);
            return record < 1;*/
            return  true;
        }
        return false;
    }

    @Override
    public String getBudgetPageMessage(String month) {

        switch (month) {
            case "01":
                return "January ";
            case "02":
                return "February ";
            case "03":
                return "March ";
            case "04":
                return "April ";
            case "05":
                return "May ";
            case "06":
                return "June ";
            case "07":
                return "July ";
            case "08":
                return "August ";
            case "09":
                return "September  ";
            case "10":
                return "October ";
            case "11":
                return "November ";
            case "12":
                return "December ";
            default:
                return Constants.NO_MESSAGE;
        }
    }

    @Override
    public String getBudgetAmountLeft(BudgetDto budgetDto) {
        return budgetDto.getTotalBudgetAmount().toString() + Constants.RS_LEFT_IN_BUDGET;
    }

    @Override
    public String createBudgetEntry(BudgetDto budgetDto) {
        double amountLeft=0D;
        Double amountAllocated= budgetRepo.sumTotalBudget(budgetDto.getMonth(),budgetDto.getYear());
        if(amountAllocated == null) amountAllocated = 0D;
        if(isValidBudgetEntry(budgetDto,amountAllocated)) {
            Budget budget=dtoToEntity(budgetDto);
            budget.setUsedAmount(transactionRepo.findTransactionsSumOnDate(budget.getCategory(),utilityService.getCurrentMonthsFirstDate(),utilityService.getCurrentMonthsLastDate(),"expense"));
            if(budget.getUsedAmount()==null) budget.setUsedAmount(0D);
            budgetRepo.save(budget);
        }
        else {
            amountLeft=getAmountLeftInBudget(budgetDto.getTotalBudgetAmount());
            return "Exceed the the limit. "+ Double.toString(amountLeft) +Constants.RS_LEFT_IN_BUDGET;
        }
         amountLeft=getAmountLeftInBudget(budgetDto.getTotalBudgetAmount());
        return  Double.toString(amountLeft) + Constants.RS_LEFT_IN_BUDGET;
    }

    private double getAmountLeftInBudget(Double totalBudgetAmount) {
        Double amountAllocated=budgetRepo.sumTotalBudget(utilityService.getCurrentMonth(),utilityService.getCurrentYear());
        return Math.abs(totalBudgetAmount-amountAllocated);
    }

    private boolean isValidBudgetEntry(BudgetDto budgetDto,Double amountAllocated) {
        Budget budget=budgetRepo.findByYearAndMonthAndCategory(utilityService.getCurrentYear(),utilityService.getCurrentMonth(),categoryRepo.findById(budgetDto.getCategoryId()));
        if(budget!=null){
            return (budgetDto.getTotalBudgetAmount()> (amountAllocated+budgetDto.getBudgetAmount()-budget.getBudgetAmount()));
        }
        else {
            return true;
        }
    }

    @Override
    public List<BudgetDto> getbudgetCategoriesList(String month, String year, Double totalBudgetAmount) {
        List<Budget>budgetList= budgetRepo.findByYearAndMonth(year,month);
        List<BudgetDto>budgetDtoList=getDtoList(budgetList);
        budgetDtoList=getNewBudgetDtoList(budgetDtoList, year,month,totalBudgetAmount);
        return budgetDtoList;
    }

    @Override
    public void addBudgetTransaction(Transaction transaction) {
        if(transaction.getType().equals(Constants.TRANSACTION_TYPE_EXPENSE)) {
            String year=utilityService.getYearByDate(transaction.getDate());
            String month=utilityService.getMonthByDate(transaction.getDate());
            Budget budget= budgetRepo.findByYearAndMonthAndCategory(year,month,transaction.getCategory());
           Double usedAmount= transactionRepo.findTransactionsSumOnDate(transaction.getCategory(),utilityService.getMonthFirstDate(transaction.getDate()),utilityService.getLastDate(transaction.getDate()),transaction.getType());
           if(budget != null) {
               budget.setUsedAmount(usedAmount);
               budgetRepo.save(budget);
           }
           else {
               Budget newBudget=new Budget();
               newBudget.setBudgetAmount(0D);
               newBudget.setUsedAmount(usedAmount);
               newBudget.setYear(year);
               newBudget.setMonth(month);
               newBudget.setCategory(transaction.getCategory());
               newBudget.setTotalBudgetAmount(budgetRepo.totalBudget(year,month));
               budgetRepo.save(newBudget);
           }
        }
    }

    @Override
    public boolean isValidBudget(BudgetDto budgetDto) {
        String year=budgetDto.getMonth().substring(0,4);
        String month=budgetDto.getMonth().substring(5,7);
        if(utilityService.getCurrentMonth().compareTo(month)<1) {
            int record = budgetRepo.count(year, month);
            return record < 1;
        }
        return false;
    }

    @Override
    public BudgetDto getCurrentMonthlyBudgetDto() {
        Budget budget=new Budget();
        //MonthlyBudgetDto monthlyBudgetDto = budgetRepo.findTotalMonthlyBudget(utilityService.getCurrentYear(),utilityService.getCurrentMonth());
       Double budgetUsedAmount =budgetRepo.sumOfUsedAmount(utilityService.getCurrentMonth(),utilityService.getCurrentYear());
       Double budgetTotalAmount =budgetRepo.totalBudget(utilityService.getCurrentYear(),utilityService.getCurrentMonth());
        if(budgetTotalAmount != null){
            budget.setId(0L);
            if(budgetUsedAmount==null){
                budgetUsedAmount=0D;
            }
            budget.setUsedAmount(budgetUsedAmount);
            budget.setTotalBudgetAmount(budgetTotalAmount);
            budget.setBudgetAmount(budgetTotalAmount);
            budget.setYear(utilityService.getCurrentMonth());
            budget.setMonth(utilityService.getCurrentMonth());
            return entityToDto(budget);
        }
        else{
           BudgetDto budgetDto=new BudgetDto();
            budgetDto.setPercentage("0%");
            budgetDto.setBudgetAmount(0D);
            budgetDto.setTotalBudgetAmount(0D);
            budgetDto.setMessage("no budget set");
            return budgetDto;
        }
    }

    @Override
    public BudgetDto getCurrentMonthlyBudgetToEdit() {
        BudgetDto currentBudget=new BudgetDto();
        currentBudget.setMonth(utilityService.getCurrentMonth());
        currentBudget.setYear(utilityService.getCurrentYear());
        currentBudget.setTotalBudgetAmount(budgetRepo.totalBudget(utilityService.getCurrentYear(),utilityService.getCurrentMonth()));
        return currentBudget;
    }

    @Override
    public Double editCurrentBudgetTotalAmount(BudgetDto budgetDto) {
        Double previousBudgetAllocatedAmount=budgetRepo.sumTotalBudget(utilityService.getCurrentMonth(),utilityService.getCurrentYear());
        Double amountLeft=0D;
        if (budgetDto.getTotalBudgetAmount()>= previousBudgetAllocatedAmount) {
          List<Budget>currentBudgetList = budgetRepo.findByYearAndMonth(utilityService.getCurrentYear(), utilityService.getCurrentMonth());
            for (Budget budgetCategory: currentBudgetList
                 ) {
                budgetCategory.setTotalBudgetAmount(budgetDto.getTotalBudgetAmount());
                budgetRepo.save(budgetCategory);
            }
        }
        amountLeft=budgetDto.getTotalBudgetAmount()- budgetRepo.sumTotalBudget(utilityService.getCurrentMonth(),utilityService.getCurrentYear());
        return  amountLeft;
    }

    private List<BudgetDto> getNewBudgetDtoList(List<BudgetDto> budgetDtoList,String year,String month,Double totalBudgetAmount) {
        List<BudgetDto>budgetCategoryList=createNewBudget(year,month,totalBudgetAmount);
        List<Long> tempList=new ArrayList<>();
        for (BudgetDto budgetDto:budgetDtoList) {
            tempList.add(budgetDto.getCategoryId());
        }
        for (BudgetDto budgetDto:budgetCategoryList) {
            if(!(tempList.contains(budgetDto.getCategoryId())))
                budgetDtoList.add(budgetDto);
        }
        return budgetDtoList;
    }

    private List<BudgetDto> getDtoList(List<Budget> budgetList) {
        List<BudgetDto> budgetDtoList=new ArrayList<>();
        for (Budget budget:budgetList
             ) {
            BudgetDto budgetDto=entityToDto(budget);
            budgetDto.setCategoryDto(categoryService.entityToDto(budget.getCategory()));
            budgetDtoList.add(budgetDto);
        }
        return budgetDtoList;
    }
}
