package org.girlscouts.vtk.dao;

import org.girlscouts.vtk.models.Contact;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;

public interface ContactDAO {

    void save(User user, Troop troop, Contact contact)
            throws IllegalStateException, IllegalAccessException;

    Contact retreive(User user, Troop troop, String contactId)
            throws IllegalStateException, IllegalAccessException;

}
