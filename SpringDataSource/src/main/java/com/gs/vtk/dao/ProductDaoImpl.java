package com.gs.vtk.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.SessionFactory;

import com.gs.vtk.Product;

public class ProductDaoImpl implements ProductDao {

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Collection loadProductsByCategory(String category) {
        return this.sessionFactory.getCurrentSession()
                .createQuery("from test.Product product where product.category=?")
                .setParameter(0, category)
                .list();
    }

	@Override
	public List<Product> getProductList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveProduct(Product prod) {
		// TODO Auto-generated method stub
		
	}
}