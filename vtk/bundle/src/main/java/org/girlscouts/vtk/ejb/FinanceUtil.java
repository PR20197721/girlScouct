package org.girlscouts.vtk.ejb;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.mail.internet.InternetAddress;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.dao.TroopDAO;
import org.girlscouts.vtk.models.Finance;
import org.girlscouts.vtk.models.FinanceConfiguration;
import org.girlscouts.vtk.models.Troop;

import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import com.day.text.csv.Csv;

@Component
@Service(value = FinanceUtil.class)
public class FinanceUtil {

	@Reference
	TroopDAO troopDAO;
	
	@Reference
	private MessageGatewayService messageGatewayService;

	public Finance getFinances(Troop troop, int qtr, String currentYear) {
		return troopDAO.getFinances(troop, qtr, currentYear);
	}

	public void updateFinances(Troop troop, String currentYear, java.util.Map<String, String[]> params) {
		Finance finance = new Finance();
		Set<String> keySet = params.keySet();
		for(String temp : keySet){
			String[] tempArray = params.get(temp);
		}
		int quarter = Integer.parseInt(params.get("qtr")[0]);
		troopDAO.setFinances(troop, quarter, currentYear, params);
		// TODO NOTIFY Council here
	}
	
	public FinanceConfiguration getFinanceConfig(Troop troop, String currentYear) {
		return troopDAO.getFinanceConfiguration(troop, currentYear);
	}

	public void updateFinanceConfiguration(Troop troop, String currentYear, java.util.Map<java.lang.String, java.lang.String[]> params) {
		FinanceConfiguration financeConfig = new FinanceConfiguration();
		String expenses = params.get(Finance.EXPENSES)[0];
		String income = params.get(Finance.INCOME)[0];
		String period = params.get(Finance.PERIOD)[0];
		troopDAO.setFinanceConfiguration(troop, currentYear, income, expenses, period);
		// TODO NOTIFY Council here
	}
	
	public void sendFinanceDataEmail(Troop troop, int qtr, String currentYear){
		Finance finance = getFinances(troop, qtr, currentYear);
		FinanceConfiguration financeConfig = getFinanceConfig(troop, currentYear);
		
		try {
			MessageGateway<HtmlEmail> messageGateway = messageGatewayService.getGateway(HtmlEmail.class);
			
			StringWriter writer = new StringWriter();
			Csv csvWriter = new Csv();
			csvWriter.writeInit(writer);
			
			csvWriter.writeRow("Income", "", "Expenses", "");
			
			List<String> incomeFields = financeConfig.getIncomeFields();
			List<String> expenseFields = financeConfig.getExpenseFields();
			
			for(int i = 0; i < incomeFields.size() || i < expenseFields.size(); i++){
				String incomeField = "";
				String incomeValue = "";
				if(i < incomeFields.size()){
					incomeField = incomeFields.get(i);
					incomeValue = Double.toString(finance.getIncomeByName(incomeField));
				}
				String expenseField = "";
				String expenseValue = "";
				if(i < expenseFields.size()){
					expenseField = incomeFields.get(i);
					expenseValue = Double.toString(finance.getExpenseByName(expenseField));
				}
				csvWriter.writeRow(incomeField, incomeValue, expenseField, expenseValue);
				
			}
			
			csvWriter.close();
			
			writer.close();
			String csvContents = writer.toString();
			
			
			HtmlEmail email = new HtmlEmail();
			ArrayList<InternetAddress> emailRecipients = new ArrayList<InternetAddress>();
			
			email.setTo(emailRecipients);
			email.setSubject("Troop Finances");
			
			
			email.attach(new ByteArrayDataSource(csvContents.getBytes(),"text/csv"), "finances.csv", "Finances Data");
			
			messageGateway.send(email);
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EmailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}


