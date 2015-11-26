package com.acertainbookstore.business;

import java.util.Comparator;

public class BookRatingComparator implements Comparator<BookStoreBook>{

	@Override
	public int compare(BookStoreBook b0, BookStoreBook b1) {
		return (int) (b0.getAverageRating() - b1.getAverageRating());
	}

}
