package one.microstream.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import one.microstream.domain.Author;
import one.microstream.domain.Book;
import one.microstream.persistence.types.Storer;
import one.microstream.storage.DB;
import one.microstream.utils.BinaryPersistenceReloader;
import one.microstream.utils.MockupUtils;


@Controller("/books")
public class BookController
{
	@Get("/create")
	public HttpResponse<?> createBooks()
	{
		List<Book> loadMockupData = MockupUtils.loadMockupData();
		
		DB.root.getBooks().addAll(loadMockupData);
		DB.storageManager.store(DB.root.getBooks());
		
		return HttpResponse.ok("Books successfully created!");
	}
	
	@Get
	public List<Book> getBook()
	{
		return DB.root.getBooks();
	}
	
	@Get("/createbooksloop")
	public HttpResponse<?> createBooksLoop()
	{
		IntStream.rangeClosed(1, 10).forEach(i ->
		{
			List<Book> allCreatedBooks = MockupUtils.loadMockupData();
			allCreatedBooks.forEach(b ->
			{
				DB.root.getBooks().add(b);
				DB.storageManager.store(DB.root.getBooks());
			});
			
			System.out.println("Durchlauf " + i);
		});
		
		return HttpResponse.ok("Books successfully created!");
	}
	
	@Get("/updatebookstartswithA")
	public HttpResponse<?> updateBooksStartWithA()
	{
		Storer es = DB.storageManager.createEagerStorer();
		
		DB.root.getBooks().stream().filter(b -> b.getName().startsWith("A")).forEach(b ->
		{
			// Reduces price of books starting with an A by 10%
			b.setPrice(b.getPrice().multiply(new BigDecimal(0.9)));
			es.store(b);
		});
		
		es.commit();
		
		return HttpResponse.ok("Books successfully updated!");
	}
	
	@Get("/updatebookandauthor")
	public HttpResponse<?> updateBookAndAuthor()
	{
		Storer ls = DB.storageManager.createLazyStorer();
		
		Book book = DB.root.getBooks().get(0);
		
		book.setName("This is a book with a changed name");
		ls.store(book);
		
		Author author = book.getAuthor();
		author.setLastname("This is a book with a changed name");
		ls.store(author);
		
		ls.commit();
		
		return HttpResponse.ok("Books successfully updated!");
	}
	
	@Get("/updateNonStore")
	public HttpResponse<?> updateBookNonStore()
	{
		Book book =
			DB.root.getBooks().stream().filter(b -> b.getIsbn().equalsIgnoreCase("498123138-5")).findFirst().get();
		String oldname = book.getName();
		book.setName("Java, The Good Parts");
		
		return HttpResponse.ok("Name of book successfully changed from " + oldname + " to " + book.getName());
	}
	
	@Get("/updateNonStoreDeep")
	public HttpResponse<?> updateAuthorNonStore()
	{
		Book book =
			DB.root.getBooks().stream().filter(b -> b.getIsbn().equalsIgnoreCase("498123138-5")).findFirst().get();
		String oldname = book.getAuthor().getLastname();
		book.getAuthor().setLastname("Travolta");
		
		return HttpResponse.ok(
			"Name of author successfully changed from " + oldname + " to " + book.getAuthor().getLastname());
	}
	
	@Get("/rollbackFlat")
	public HttpResponse<?> rollbackBookFlat()
	{
		Book book =
			DB.root.getBooks().stream().filter(b -> b.getIsbn().equalsIgnoreCase("498123138-5")).findFirst().get();
		System.out.println(book.getName());
		
		final BinaryPersistenceReloader reloader =
			BinaryPersistenceReloader.New(DB.storageManager.persistenceManager());
		
		reloader.reloadFlat(book);
		System.out.println(book.getName());
		
		return HttpResponse.ok("Book successfully rollbacked!");
	}
	
	@Get("/rollbackDeep")
	public HttpResponse<?> rollbackBookDeep()
	{
		Book book =
			DB.root.getBooks().stream().filter(b -> b.getIsbn().equalsIgnoreCase("498123138-5")).findFirst().get();
		System.out.println(book.getAuthor().getLastname());
		
		final BinaryPersistenceReloader reloader =
			BinaryPersistenceReloader.New(DB.storageManager.persistenceManager());
		
		try
		{
			reloader.reloadDeep(book);
		}
		catch(Exception e)
		{
			System.out.println(book.getAuthor().getLastname());
		}
		
		return HttpResponse.ok("Author successfully rollbacked!");
	}
}
