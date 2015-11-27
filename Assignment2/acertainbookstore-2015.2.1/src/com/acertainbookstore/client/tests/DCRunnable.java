package com.acertainbookstore.client.tests;

import java.util.Set;

import com.acertainbookstore.interfaces.BookStore;
import com.acertainbookstore.interfaces.StockManager;
import com.acertainbookstore.utils.BookStoreException;
import com.acertainbookstore.business.BookCopy;


/*
 * Isolation client runnable 
 */
public class DCRunnable implements Runnable {

	private BookStore store;
//	private StockManager manager;
	private Set<BookCopy> booksToBuy;
//	private Set<BookCopy> bookCopies;
	
	public DCRunnable(BookStore store, StockManager manager, Set<BookCopy> booksToBuy, Set<BookCopy> bookCopies)
	{
		this.store = store;
//		this.manager = manager;
		this.booksToBuy = booksToBuy;
//		this.bookCopies = bookCopies;
	}
	
	@Override
	public void run() {
		try {
			store.buyBooks(booksToBuy);
			throw new BookStoreException();
			//manager.addCopies(bookCopies);		
		} catch (BookStoreException e) {}
	}

}
