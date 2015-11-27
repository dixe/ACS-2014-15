package com.acertainbookstore.client.tests;

import java.util.Set;

import com.acertainbookstore.interfaces.BookStore;
import com.acertainbookstore.interfaces.StockManager;
import com.acertainbookstore.utils.BookStoreException;
import com.acertainbookstore.business.BookCopy;


/*
 * Isolation client runnable 
 */
public class ICRunnable implements Runnable {

	private BookStore store;
	private StockManager manager;
	private int operations;
	private Set<BookCopy> booksToBuy;
	private Set<BookCopy> bookCopies;
	
	public ICRunnable(BookStore store, StockManager manager, int operations, Set<BookCopy> booksToBuy, Set<BookCopy> bookCopies)
	{
		this.store = store;
		this.manager = manager;
		this.operations = operations;
		this.booksToBuy = booksToBuy;
		this.bookCopies = bookCopies;
	}
	
	@Override
	public void run() {
		for (int i = 0; i < operations; i++) {
			try {
				store.buyBooks(booksToBuy);
				manager.addCopies(bookCopies);		
			} catch (BookStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
