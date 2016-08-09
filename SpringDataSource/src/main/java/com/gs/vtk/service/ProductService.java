package com.gs.vtk.service;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.gs.vtk.Product;
import com.gs.vtk.dao.ProductDao;

@Service
public interface ProductService {
	public void setTransactionManager(PlatformTransactionManager transactionManager);

    public void setProductDao(ProductDao productDao) ;

    public void increasePriceOfAllProductsInCategory(final String category) ;
        
}
