package com.acertainbookstore.client.tests;

import java.util.Set;

import com.acertainbookstore.business.BookCopy;
import com.acertainbookstore.business.StockBook;
import com.acertainbookstore.interfaces.BookStore;
import com.acertainbookstore.interfaces.StockManager;
import com.acertainbookstore.utils.BookStoreException;



/*
 * Atoimiticy client 1 runnable 
 * Buy books runnable
 */
public class AC2Runnable implements Runnable {

	
	private StockManager store;
	int buyTimes = 0;
	Set<BookCopy> bookSet;
	
	
	public AC2Runnable(StockManager store, int buyTimes, Set<BookCopy> bookSet)
	{
		this.store = store;
		this.buyTimes = buyTimes;
		this.bookSet = bookSet;
	}
	
	@Override
	public void run() {

		for(int i = 0; i < buyTimes; i++)
		{
			
			try {
				store.addCopies(bookSet);
			} catch (BookStoreException e) {
				
			}
		}
	}

}
