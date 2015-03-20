package org.girlscouts.vtk.ejb;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.dao.SearchDAO;

//import org.girlscouts.vtk.dao.UserDAO;

@Component
@Service(value = SearchDAO.class)
public class Search implements SearchDAO {

	// private Session session;

	@Reference
	private SessionFactory sessonFactory;

	@Activate
	void activate() {
		// this.session = sessonFactory.getSession();
	}

	private int totalCountries;
	private String data = "Afghanistan,	Albania, Algeria, Andorra, Angola, Antigua & Deps,"
			+ "Argentina,	Armenia, Australia,	Austria,Azerbaijan,Bahamas,Bahrain,Bangladesh,Barbados,"
			+ "Belarus,Belgium,Belize,Benin,Bhutan,Bolivia,Bosnia Herzegovina,Botswana,Brazil,Brunei,"
			+ "Bulgaria,Burkina,Burundi,Cambodia,Cameroon,Canada,Cape Verde,Central African Rep,Chad,"
			+ "Chile,China,Colombia,Comoros,Congo,Congo {Democratic Rep},Costa Rica,Croatia,Cuba,Cyprus,"
			+ "Czech Republic,Denmark,Djibouti,Dominica,Dominican Republic,East Timor,Ecuador,Egypt,El Salvador,"
			+ "Equatorial Guinea,	Eritrea,Estonia,Ethiopia,Fiji,Finland,France,Gabon,Gambia,Georgia,Germany,"
			+ "Ghana,	Greece,	Grenada,Guatemala,Guinea,Guinea-Bissau,Guyana,Haiti,Honduras,Hungary,Iceland,"
			+ "India,	Indonesia,Iran,Iraq,Ireland {Republic},Israel,Italy,Ivory Coast,Jamaica,Japan,"
			+ "Jordan,Kazakhstan,Kenya,Kiribati,Korea North,Korea South,Kosovo,Kuwait,Kyrgyzstan,Laos,"
			+ "Latvia,Lebanon,Lesotho,Liberia,Libya,Liechtenstein,Lithuania,Luxembourg,Macedonia,Madagascar,"
			+ "Malawi,Malaysia,Maldives,Mali,Malta,Marshall Islands,Mauritania,Mauritius,Mexico,Micronesia,"
			+ "Moldova,Monaco,Mongolia,Montenegro,Morocco,Mozambique,Myanmar, {Burma},Namibia,Nauru,Nepal,"
			+ "Netherlands,New Zealand,Nicaragua,Niger,Nigeria,Norway,Oman,Pakistan,Palau,Panama,Papua New Guinea,"
			+ "Paraguay,Peru,Philippines,Poland,Portugal,Qatar,Romania,Russian Federation,Rwanda,St Kitts & Nevis,"
			+ "St Lucia,Saint Vincent & the Grenadines,Samoa,San Marino,Sao Tome & Principe,Saudi Arabia,Senegal,"
			+ "Serbia,Seychelles,Sierra Leone,Singapore,Slovakia,Slovenia,Solomon Islands,Somalia,South Africa,"
			+ "Spain,Sri Lanka,Sudan,Suriname,Swaziland,Sweden,Switzerland,Syria,Taiwan,Tajikistan,Tanzania, "
			+ "Thailand,Togo,Tonga,Trinidad & Tobago,Tunisia,Turkey,Turkmenistan,Tuvalu,Uganda,Ukraine,United Arab Emirates,"
			+ "United Kingdom,United States,Uruguay,Uzbekistan,Vanuatu,Vatican City,Venezuela,Vietnam,Yemen,Zambia,Zimbabwe";
	private List<String> countries;

	/*
	 * public Search() { countries = new ArrayList<String>(); StringTokenizer st
	 * = new StringTokenizer(data, ",");
	 * 
	 * while(st.hasMoreTokens()) { countries.add(st.nextToken().trim()); }
	 * totalCountries = countries.size(); }
	 * 
	 * public List<String> getData1(String query) { String country = null; query
	 * = query.toLowerCase(); List<String> matched = new ArrayList<String>();
	 * for(int i=0; i<totalCountries; i++) { country =
	 * countries.get(i).toLowerCase(); if(country.startsWith(query)) {
	 * matched.add(countries.get(i)); } } return matched; }
	 */
	public List<String> getData(String query) {
		Session session = null;
		List<String> matched = new ArrayList<String>();
		try {
			session = sessonFactory.getSession();
			QueryManager qm = session.getWorkspace().getQueryManager();
			Query q = qm
					.createQuery(
							"select jcr:path, excerpt(.) from nt:resource    where jcr:path like '/content/dam/%' and  contains(., '"
									+ query + "')", Query.SQL);
			QueryResult result = q.execute();
			for (RowIterator it = result.getRows(); it.hasNext();) {
				Row r = it.nextRow();
				Value excerpt = r.getValue("rep:excerpt(.)");

				matched.add(excerpt.getString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					sessonFactory.closeSession(session);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return matched;
	}

}
