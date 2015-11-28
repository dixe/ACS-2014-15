package com.acertainbookstore.client.tests;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.acertainbookstore.business.Book;
import com.acertainbookstore.business.BookCopy;
import com.acertainbookstore.business.ConcurrentCertainBookStore;
import com.acertainbookstore.business.ImmutableStockBook;
import com.acertainbookstore.business.StockBook;
import com.acertainbookstore.client.BookStoreHTTPProxy;
import com.acertainbookstore.client.StockManagerHTTPProxy;
import com.acertainbookstore.interfaces.BookStore;
import com.acertainbookstore.interfaces.StockManager;
import com.acertainbookstore.utils.BookStoreConstants;
import com.acertainbookstore.utils.BookStoreException;

/**
 * Test class to test the BookStore interface
 * 
 */
public class BookStoreTest {

	private static final int TEST_ISBN = 3044560;
	private static final int NUM_COPIES = 5;
	private static boolean localTest = true;
	private static StockManager storeManager;
	private static BookStore client;

	@BeforeClass
	public static void setUpBeforeClass() {
		try {
			String localTestProperty = System
					.getProperty(BookStoreConstants.PROPERTY_KEY_LOCAL_TEST);
			localTest = (localTestProperty != null) ? Boolean
					.parseBoolean(localTestProperty) : localTest;
			if (localTest) {
                ConcurrentCertainBookStore store = new ConcurrentCertainBookStore();
				storeManager = store;
				client = store;
			} else {
				storeManager = new StockManagerHTTPProxy(
						"http://localhost:8081/stock");
				client = new BookStoreHTTPProxy("http://localhost:8081");
			}
			storeManager.removeAllBooks();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Helper method to add some books
	 */
	public void addBooks(int isbn, int copies) throws BookStoreException {
		Set<StockBook> booksToAdd = new HashSet<StockBook>();
		StockBook book = new ImmutableStockBook(isbn, "Test of Thrones",
				"George RR Testin'", (float) 10, copies, 0, 0, 0, false);
		booksToAdd.add(book);
		storeManager.addBooks(booksToAdd);
	}

	/**
	 * Helper method to get the default book used by initializeBooks
	 */
	public StockBook getDefaultBook() {
		return new ImmutableStockBook(TEST_ISBN, "Harry Potter and JUnit",
				"JK Unit", (float) 10, NUM_COPIES, 0, 0, 0, false);
	}

	/**
	 * Method to add a book, executed before every test case is run
	 */
	@Before
	public void initializeBooks() throws BookStoreException {
		Set<StockBook> booksToAdd = new HashSet<StockBook>();
		booksToAdd.add(getDefaultBook());
		storeManager.addBooks(booksToAdd);
	}

	/**
	 * Method to clean up the book store, execute after every test case is run
	 */
	@After
	public void cleanupBooks() throws BookStoreException {
		storeManager.removeAllBooks();
	}

	/**
	 * Tests basic buyBook() functionality
	 */
	@Test
	public void testBuyAllCopiesDefaultBook() throws BookStoreException {
		// Set of books to buy
		Set<BookCopy> booksToBuy = new HashSet<BookCopy>();
		booksToBuy.add(new BookCopy(TEST_ISBN, NUM_COPIES));

		// Try to buy books
		client.buyBooks(booksToBuy);

		List<StockBook> listBooks = storeManager.getBooks();
		assertTrue(listBooks.size() == 1);
		StockBook bookInList = listBooks.get(0);
		StockBook addedBook = getDefaultBook();

		assertTrue(bookInList.getISBN() == addedBook.getISBN()
				&& bookInList.getTitle().equals(addedBook.getTitle())
				&& bookInList.getAuthor().equals(addedBook.getAuthor())
				&& bookInList.getPrice() == addedBook.getPrice()
				&& bookInList.getSaleMisses() == addedBook.getSaleMisses()
				&& bookInList.getAverageRating() == addedBook
						.getAverageRating()
				&& bookInList.getTimesRated() == addedBook.getTimesRated()
				&& bookInList.getTotalRating() == addedBook.getTotalRating()
				&& bookInList.isEditorPick() == addedBook.isEditorPick());

	}

	/**
	 * Tests that books with invalid ISBNs cannot be bought
	 */
	@Test
	public void testBuyInvalidISBN() throws BookStoreException {
		List<StockBook> booksInStorePreTest = storeManager.getBooks();

		// Try to buy a book with invalid isbn
		HashSet<BookCopy> booksToBuy = new HashSet<BookCopy>();
		booksToBuy.add(new BookCopy(TEST_ISBN, 1)); // valid
		booksToBuy.add(new BookCopy(-1, 1)); // invalid

		// Try to buy the books
		try {
			client.buyBooks(booksToBuy);
			fail();
		} catch (BookStoreException ex) {
			;
		}

		List<StockBook> booksInStorePostTest = storeManager.getBooks();
		// Check pre and post state are same
		assertTrue(booksInStorePreTest.containsAll(booksInStorePostTest)
				&& booksInStorePreTest.size() == booksInStorePostTest.size());

	}

	/**
	 * Tests that books can only be bought if they are in the book store
	 */
	@Test
	public void testBuyNonExistingISBN() throws BookStoreException {
		List<StockBook> booksInStorePreTest = storeManager.getBooks();

		// Try to buy a book with isbn which does not exist
		HashSet<BookCopy> booksToBuy = new HashSet<BookCopy>();
		booksToBuy.add(new BookCopy(TEST_ISBN, 1)); // valid
		booksToBuy.add(new BookCopy(100000, 10)); // invalid

		// Try to buy the books
		try {
			client.buyBooks(booksToBuy);
			fail();
		} catch (BookStoreException ex) {
			;
		}

		List<StockBook> booksInStorePostTest = storeManager.getBooks();
		// Check pre and post state are same
		assertTrue(booksInStorePreTest.containsAll(booksInStorePostTest)
				&& booksInStorePreTest.size() == booksInStorePostTest.size());

	}

	/**
	 * Tests that you can't buy more books than there are copies
	 */
	@Test
	public void testBuyTooManyBooks() throws BookStoreException {
		List<StockBook> booksInStorePreTest = storeManager.getBooks();

		// Try to buy more copies than there are in store
		HashSet<BookCopy> booksToBuy = new HashSet<BookCopy>();
		booksToBuy.add(new BookCopy(TEST_ISBN, NUM_COPIES + 1));

		try {
			client.buyBooks(booksToBuy);
			fail();
		} catch (BookStoreException ex) {
			;
		}

		List<StockBook> booksInStorePostTest = storeManager.getBooks();
		assertTrue(booksInStorePreTest.containsAll(booksInStorePostTest)
				&& booksInStorePreTest.size() == booksInStorePostTest.size());

	}

	/**
	 * Tests that you can't buy a negative number of books
	 */
	@Test
	public void testBuyNegativeNumberOfBookCopies() throws BookStoreException {
		List<StockBook> booksInStorePreTest = storeManager.getBooks();

		// Try to buy a negative number of copies
		HashSet<BookCopy> booksToBuy = new HashSet<BookCopy>();
		booksToBuy.add(new BookCopy(TEST_ISBN, -1));

		try {
			client.buyBooks(booksToBuy);
			fail();
		} catch (BookStoreException ex) {
			;
		}

		List<StockBook> booksInStorePostTest = storeManager.getBooks();
		assertTrue(booksInStorePreTest.containsAll(booksInStorePostTest)
				&& booksInStorePreTest.size() == booksInStorePostTest.size());

	}

    /**
	 * Tests that all books can be retrieved
	 */
	@Test
	public void testGetBooks() throws BookStoreException {
		Set<StockBook> booksAdded = new HashSet<StockBook>();
		booksAdded.add(getDefaultBook());

		Set<StockBook> booksToAdd = new HashSet<StockBook>();
		booksToAdd.add(new ImmutableStockBook(TEST_ISBN + 1,
				"The Art of Computer Programming", "Donald Knuth", (float) 300,
				NUM_COPIES, 0, 0, 0, false));
		booksToAdd.add(new ImmutableStockBook(TEST_ISBN + 2,
				"The C Programming Language",
				"Dennis Ritchie and Brian Kerninghan", (float) 50, NUM_COPIES,
				0, 0, 0, false));

		booksAdded.addAll(booksToAdd);

		storeManager.addBooks(booksToAdd);

		// Get books in store
		List<StockBook> listBooks = storeManager.getBooks();

		// Make sure the lists equal each other
		assertTrue(listBooks.containsAll(booksAdded)
				&& listBooks.size() == booksAdded.size());
	}

    /**
	 * Tests that a list of books with a certain feature can be retrieved
	 */
	@Test
	public void testGetCertainBooks() throws BookStoreException {
		Set<StockBook> booksToAdd = new HashSet<StockBook>();
		booksToAdd.add(new ImmutableStockBook(TEST_ISBN + 1,
				"The Art of Computer Programming", "Donald Knuth", (float) 300,
				NUM_COPIES, 0, 0, 0, false));
		booksToAdd.add(new ImmutableStockBook(TEST_ISBN + 2,
				"The C Programming Language",
				"Dennis Ritchie and Brian Kerninghan", (float) 50, NUM_COPIES,
				0, 0, 0, false));

		storeManager.addBooks(booksToAdd);

		// Get a list of ISBNs to retrieved
		Set<Integer> isbnList = new HashSet<Integer>();
		isbnList.add(TEST_ISBN + 1);
		isbnList.add(TEST_ISBN + 2);

		// Get books with that ISBN

		List<Book> books = client.getBooks(isbnList);
		// Make sure the lists equal each other
		assertTrue(books.containsAll(booksToAdd)
				&& books.size() == booksToAdd.size());

	}

	/**
	 * Tests that books cannot be retrieved if ISBN is invalid
	 */
	@Test
	public void testGetInvalidIsbn() throws BookStoreException {
		List<StockBook> booksInStorePreTest = storeManager.getBooks();

		// Make an invalid ISBN
		HashSet<Integer> isbnList = new HashSet<Integer>();
		isbnList.add(TEST_ISBN); // valid
		isbnList.add(-1); // invalid

		HashSet<BookCopy> booksToBuy = new HashSet<BookCopy>();
		booksToBuy.add(new BookCopy(TEST_ISBN, -1));

		try {
			client.getBooks(isbnList);
			fail();
		} catch (BookStoreException ex) {
			;
		}

		List<StockBook> booksInStorePostTest = storeManager.getBooks();
		assertTrue(booksInStorePreTest.containsAll(booksInStorePostTest)
				&& booksInStorePreTest.size() == booksInStorePostTest.size());

	}

	/**
	 * Tests that operations are atomic
	 * @throws InterruptedException
	 * @throws BookStoreException
	 */
	@Test
	public void testAtomicity() throws BookStoreException, InterruptedException {
		int operations = 100;
		List<StockBook> stockBooksPre = storeManager.getBooks();
		HashSet<BookCopy> bookCopies = new HashSet<BookCopy>();
		for (StockBook stockBook : stockBooksPre) {
			int isbn = stockBook.getISBN();
			BookCopy bookCopy = new BookCopy(isbn, operations);
			bookCopies.add(bookCopy);
		}
		storeManager.addCopies(bookCopies);
		stockBooksPre = storeManager.getBooks();
		HashSet<StockBook> stockBooks = new HashSet<StockBook>();
		stockBooks.addAll(stockBooksPre);
		bookCopies = new HashSet<BookCopy>();
		for (StockBook stockBook : stockBooksPre) {
			int isbn = stockBook.getISBN();
			BookCopy bookCopy = new BookCopy(isbn, 1);
			bookCopies.add(bookCopy);
		}
		Thread T1 = new Thread(new AC1Runnable(client, operations, bookCopies));
		Thread T2 = new Thread(new AC2Runnable(storeManager, operations, bookCopies));
		T1.start();
		T2.start();
		T1.join();
		T2.join();
		List<StockBook> stockBooksPost = storeManager.getBooks();
		assertEquals(stockBooksPost.size(), stockBooksPre.size());
		for (int i = 0; i < stockBooksPre.size(); i++) {
			StockBook stockBookPre = stockBooksPre.get(i);
			StockBook stockBookPost = stockBooksPost.get(i);
			int copiesPre = stockBookPre.getNumCopies();
			int copiesPost = stockBookPost.getNumCopies();
			assertEquals(copiesPre, copiesPost);
		}
	}
	
	/**
	 * Tests that snapshots are consistent
	 * @throws InterruptedException
	 * @throws BookStoreException
	 */
	@Test
	public void testConsistency() throws BookStoreException, InterruptedException {
		int operations = 100;
		List<StockBook> stockBooks = storeManager.getBooks();
		HashSet<BookCopy> bookCopies = new HashSet<BookCopy>();
		for (StockBook stockBook : stockBooks) {
			int isbn = stockBook.getISBN();
			BookCopy bookCopy = new BookCopy(isbn, operations);
			bookCopies.add(bookCopy);
		}
		storeManager.addCopies(bookCopies);
		stockBooks = storeManager.getBooks();
		bookCopies = new HashSet<BookCopy>();
		for (StockBook stockBook : stockBooks) {
			int isbn = stockBook.getISBN();
			BookCopy bookCopy = new BookCopy(isbn, 1);
			bookCopies.add(bookCopy);
		}
		CC1Runnable cc1Runnable = new CC1Runnable(client, storeManager, operations, bookCopies);
		CC2Runnable cc2Runnable = new CC2Runnable(storeManager, operations, 1, stockBooks);
		Thread T1 = new Thread(cc1Runnable);
		Thread T2 = new Thread(cc2Runnable);
		T1.start();
		T2.start();
		T1.join();
		T2.join();
		assertTrue(cc2Runnable.success);
	}
	
	
	/**
	 * Tests that operations are isolated
	 * @throws InterruptedException
	 * @throws BookStoreException
	 */
	@Test
	public void testIsolation() throws InterruptedException, BookStoreException {
		int operations = 100;
		List<StockBook> stockBooks = storeManager.getBooks();
		HashSet<BookCopy> bookCopies = new HashSet<BookCopy>();
		int isbn = 0;
		for (StockBook stockBook : stockBooks) {
			isbn = stockBook.getISBN();
			BookCopy bookCopy = new BookCopy(isbn, operations);
			bookCopies.add(bookCopy);
		}
		storeManager.addCopies(bookCopies);
		addBooks(1, operations);
		List<StockBook> booksPre = storeManager.getBooks();
		
		BookCopy bookCopy1 = new BookCopy(isbn, 1);
		BookCopy bookCopy2 = new BookCopy(1, 1);
		HashSet<BookCopy> booksToBuyC1 = new HashSet<BookCopy>();
		HashSet<BookCopy> bookCopiesC1 = new HashSet<BookCopy>();
		HashSet<BookCopy> booksToBuyC2 = new HashSet<BookCopy>();
		HashSet<BookCopy> bookCopiesC2 = new HashSet<BookCopy>();
		booksToBuyC1.add(bookCopy1);
		bookCopiesC1.add(bookCopy2);
		booksToBuyC2.add(bookCopy2);
		bookCopiesC2.add(bookCopy1);
		
		Thread T1 = new Thread(new ICRunnable(client, storeManager, operations, booksToBuyC1, bookCopiesC1));
		Thread T2 = new Thread(new ICRunnable(client, storeManager, operations, booksToBuyC2, bookCopiesC2));
		T1.start();
		T2.start();
		T1.join();
		T2.join();
		
		List<StockBook> booksPost = storeManager.getBooks();
		assertEquals(booksPre.size(), booksPost.size());
		for (int i = 0; i < booksPre.size(); i++) {
			StockBook bookPre = booksPre.get(i);
			StockBook bookPost = booksPost.get(i);
			int copiesPre = bookPre.getNumCopies();
			int copiesPost = bookPost.getNumCopies();
			assertEquals(copiesPre, copiesPost);
		}
	}
	
	
	/**
	 * Tests that operations cannot deadlock
	 * @throws InterruptedException
	 * @throws BookStoreException
	 */
	@Test
	public void testDeadlock() throws InterruptedException, BookStoreException {
		int operations = 100;
		addBooks(1, operations);
		List<StockBook> stockBooks = storeManager.getBooks();
		HashSet<BookCopy> bookCopies = new HashSet<BookCopy>();
		for (StockBook stockBook : stockBooks) {
			int isbn = stockBook.getISBN();
			BookCopy bookCopy = new BookCopy(isbn, operations);
			bookCopies.add(bookCopy);
		}
		storeManager.addCopies(bookCopies);
		BookCopy bookCopy1 = new BookCopy(TEST_ISBN, 1);
		BookCopy bookCopy2 = new BookCopy(1, 1);
		HashSet<BookCopy> booksToBuyC1 = new HashSet<BookCopy>();
		HashSet<BookCopy> bookCopiesC1 = new HashSet<BookCopy>();
		HashSet<BookCopy> booksToBuyC2 = new HashSet<BookCopy>();
		HashSet<BookCopy> bookCopiesC2 = new HashSet<BookCopy>();
		booksToBuyC1.add(bookCopy1);
		bookCopiesC1.add(bookCopy2);
		booksToBuyC2.add(bookCopy2);
		bookCopiesC2.add(bookCopy1);
		Thread T1 = new Thread(new DLCRunnable(client, storeManager, operations, booksToBuyC1, bookCopiesC1));
		Thread T2 = new Thread(new DLCRunnable(client, storeManager, operations, booksToBuyC2, bookCopiesC2));
		T1.start();
		T2.start();
		T1.join();
		T2.join();
		assertTrue(true);
	}
	
	
	@AfterClass
	public static void tearDownAfterClass() throws BookStoreException {
		storeManager.removeAllBooks();
		if (!localTest) {
			((BookStoreHTTPProxy) client).stop();
			((StockManagerHTTPProxy) storeManager).stop();
		}
	}

}
