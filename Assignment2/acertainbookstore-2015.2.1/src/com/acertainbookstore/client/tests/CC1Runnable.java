package com.acertainbookstore.client.tests;

import java.util.Set;

import com.acertainbookstore.business.BookCopy;
import com.acertainbookstore.interfaces.BookStore;
import com.acertainbookstore.interfaces.StockManager;
import com.acertainbookstore.utils.BookStoreException;

public class CC1Runnable implements Runnable {

	
	private BookStore bookStore;
	private StockManager stockStore;
	int buyTimes;
	Set<BookCopy> booksToBuy;
	
	public CC1Runnable(BookStore bookStore, StockManager stockStore, int buyTimes, Set<BookCopy> booksToBuy)
	{
		this.bookStore = bookStore;
		this.stockStore = stockStore;
		this.buyTimes = buyTimes;
		this.booksToBuy = booksToBuy;
	}
	
	@Override
	public void run() {
		
		for(int i = 0; i < buyTimes; i++)
		{
			
			try {
				stockStore.addCopies(booksToBuy);
				bookStore.buyBooks(booksToBuy);
			} catch (BookStoreException e) {

			}
		}
		
		
	}

}
