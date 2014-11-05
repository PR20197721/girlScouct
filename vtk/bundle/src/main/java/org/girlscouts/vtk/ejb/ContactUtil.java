package org.girlscouts.vtk.ejb;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.dao.ContactDAO;
import org.girlscouts.vtk.models.Contact;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;

@Component
@Service(value=ContactUtil.class)
public class ContactUtil {

	@Reference 
	ContactDAO contactDAO;
	
	public void saveContact(User user, Troop troop, Contact contact) throws IllegalStateException, IllegalAccessException{
		contactDAO.save(user, troop, contact);
	}
	
	public Contact getContact(User user, Troop troop, String contactId)throws IllegalStateException, IllegalAccessException{
		return contactDAO.retreive(user, troop, contactId);
	}
}
