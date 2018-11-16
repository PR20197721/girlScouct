package org.girlscouts.vtk.ejb;

import java.io.IOException;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.dao.TroopDAO;
import org.girlscouts.vtk.models.Finance;
import org.girlscouts.vtk.models.FinanceConfiguration;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;

import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import com.day.text.csv.Csv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Service(value = FinanceUtil.class)
public class FinanceUtil {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Reference
	TroopDAO troopDAO;

	@Reference
	private MessageGatewayService messageGatewayService;

	@Reference
	UserUtil userUtil;

	public Finance getFinances(User user, Troop troop, int qtr,
			String currentYear) throws IllegalAccessException {

		if (troop != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_VIEW_FINANCE_ID))
			throw new IllegalAccessException();

		return troopDAO.getFinances(troop, qtr, currentYear);
	}

	public void updateFinances(User user, Troop troop, String currentYear,
			java.util.Map<String, String[]> params)
			throws IllegalAccessException {

		if (user != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_EDIT_FINANCE_ID))
			throw new IllegalAccessException();

		Finance finance = new Finance();
		Set<String> keySet = params.keySet();
		for (String temp : keySet) {
			String[] tempArray = params.get(temp);
		}
		int quarter = Integer.parseInt(params.get("qtr")[0]);
		troopDAO.setFinances(troop, quarter, currentYear, params);
		// TODO NOTIFY Council here
	}

	public FinanceConfiguration getFinanceConfig(User user, Troop troop,
			String currentYear) throws IllegalAccessException {

		if (troop != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_EDIT_FINANCE_FORM_ID)
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_VIEW_FINANCE_ID))
			throw new IllegalAccessException();

		return troopDAO.getFinanceConfiguration(troop, currentYear);
	}

	public void updateFinanceConfiguration(User user, Troop troop,
			String currentYear,
			java.util.Map<java.lang.String, java.lang.String[]> params)
			throws IllegalAccessException {

		if (user != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_EDIT_FINANCE_FORM_ID)
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_VIEW_FINANCE_ID))
			throw new IllegalAccessException();

		FinanceConfiguration financeConfig = new FinanceConfiguration();
		String expenses = params.get(Finance.EXPENSES)[0];
		String income = params.get(Finance.INCOME)[0];
		String period = params.get(Finance.PERIOD)[0];
		String recipient = params.get(FinanceConfiguration.RECIPIENT)[0];
		troopDAO.setFinanceConfiguration(troop, currentYear, income, expenses,
				period, recipient);
		// TODO NOTIFY Council here
	}

	public void sendFinanceDataEmail(User user, Troop troop, int qtr,
			String currentYear) throws IllegalAccessException {

		ArrayList<InternetAddress> emailRecipients = new ArrayList<InternetAddress>();
		logger.info("VTK Finance Email Attempt Begin.");
		try {
			if (user != null
					&& !userUtil.hasPermission(troop,
							Permission.PERMISSION_EDIT_FINANCE_ID))
				throw new IllegalAccessException();

			Finance finance = getFinances(user, troop, qtr, currentYear);
			FinanceConfiguration financeConfig = getFinanceConfig(user, troop,
					currentYear);

			MessageGateway<HtmlEmail> messageGateway = messageGatewayService
					.getGateway(HtmlEmail.class);

			StringWriter writer = new StringWriter();
			Csv csvWriter = new Csv();
			csvWriter.writeInit(writer);

			csvWriter.writeRow("Troop Name: ", troop.getSfTroopName(),
					"Troop Id: ", troop.getSfTroopId());

			if (qtr != 0) {
				csvWriter.writeRow("Finaces for Year:", currentYear,
						"Quarter: ", Integer.toString(qtr));
			} else {
				csvWriter.writeRow("Finaces for Year:", currentYear);
			}

			csvWriter.writeRow("Income", "", "Expenses", "");

			List<String> incomeFields = financeConfig.getIncomeFields();
			List<String> expenseFields = financeConfig.getExpenseFields();

			NumberFormat formatter = NumberFormat.getCurrencyInstance();
			double totalIncome = 0.0;
			double totalExpenses = 0.0;
			double balance = 0.0;

			for (int i = 0; i < incomeFields.size() || i < expenseFields.size(); i++) {
				String incomeField = "";
				String incomeValue = "";
				if (i < incomeFields.size()) {
					incomeField = incomeFields.get(i);
					double moneyValue = finance.getIncomeByName(incomeField);
					incomeValue = formatter.format(moneyValue);
					totalIncome += moneyValue;
				}
				String expenseField = "";
				String expenseValue = "";
				if (i < expenseFields.size()) {
					expenseField = expenseFields.get(i);
					double moneyValue = finance.getExpenseByName(expenseField);
					expenseValue = formatter.format(moneyValue);
					totalExpenses += moneyValue;
				}
				csvWriter.writeRow(incomeField, incomeValue, expenseField,
						expenseValue);

			}

			balance = totalIncome - totalExpenses;
			csvWriter.writeRow("", "", "", "");
			csvWriter.writeRow("", "", "", "Total Income",
					formatter.format(totalIncome));
			csvWriter.writeRow("", "", "", "Total Expenses",
					formatter.format(totalExpenses));
			csvWriter.writeRow("", "", "", "Current Balance",
					formatter.format(balance));

			csvWriter.close();

			writer.close();
			String csvContents = writer.toString();

			String recipient = financeConfig.getRecipient();
			HtmlEmail email = new HtmlEmail();
			emailRecipients.add(new InternetAddress(recipient));
			email.setFrom("NOREPLY@girlscouts.org");
			email.setTo(emailRecipients);
			email.setSubject("Troop financial Report \""
					+ troop.getTroop().getTroopName() + "\" - "
					+ user.getApiConfig().getUser().getFirstName() + " : "
					+ user.getApiConfig().getUser().getLastName());
			email.attach(new ByteArrayDataSource(csvContents.getBytes(),
					"text/csv"), "finances.csv", "Finances Data");
			if (messageGateway == null) {
				
			} else {
				messageGateway.send(email);
			}
			logger.info("VTK Finance Email Success!  Sent to: " + emailRecipients.stream().map(InternetAddress::getAddress).collect(Collectors.joining(" : ")));
		}catch(IllegalAccessException iae){
			logger.error("VTK Finance Email Error: Recipients: " + emailRecipients.stream().map(InternetAddress::getAddress).collect(Collectors.joining(" : ")));
			logger.error("VTK Finance Email Error: ", iae);
			throw iae;
		}catch (Throwable e) {
			logger.error("VTK Finance Email Error: Recipients: " + emailRecipients.stream().map(InternetAddress::getAddress).collect(Collectors.joining(" : ")));
			logger.error("VTK Finance Email Error: ", e);
		} finally{
			logger.info("VTK Finance Email Attempt End.");
		}
	}
}
