package com.acertainbookstore.client.tests;

import java.util.HashSet;
import java.util.Set;

import com.acertainbookstore.interfaces.BookStore;
import com.acertainbookstore.utils.BookStoreException;
import com.acertainbookstore.business.BookCopy;


/*
 * Atoimiticy client 1 runnable 
 * Buy books runnable
 */
public class AC1Runnable implements Runnable {

	private BookStore store;
	int buyTimes = 0;
	Set<BookCopy> booksToBuy;
	
	public AC1Runnable(BookStore store, int buyTimes, Set<BookCopy> booksToBuy)
	{
		this.store = store;
		this.buyTimes = buyTimes;
		this.booksToBuy = booksToBuy;
	}
	
	@Override
	public void run() {
		Set<BookCopy> booksToBuy = new HashSet<>();
		
		for(int i = 0; i < buyTimes; i++)
		{
			
			try {
				store.buyBooks(booksToBuy);
			} catch (BookStoreException e) {
				
			}
		}

	}

}
