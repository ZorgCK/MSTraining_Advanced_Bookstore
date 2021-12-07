package one.microstream.storage;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import one.microstream.storage.embedded.configuration.types.EmbeddedStorageConfiguration;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;


public class DB
{
	private static final Logger				LOG		= LoggerFactory.getLogger(DB.class);
	
	public static EmbeddedStorageManager	storageManager;
	public final static DataRoot			root	= new DataRoot();
	
	static
	{
		//@formatter:off
		storageManager = EmbeddedStorageConfiguration.Builder()
			.setStorageDirectoryInUserHome("microstream-data/StorageAdvancedTraining")
			
			.setChannelDirectoryPrefix("ck_storage_channel")
			.setDataFilePrefix("ck_datafile_")
			.setTransactionFilePrefix("ck_translog_")
			.setTypeDictionaryFileName("ck_type_dictionary")
			
//			.setChannelCount(4)
			
			.setHousekeepingInterval(Duration.ofMillis(1000))
			.setHousekeepingTimeBudget(Duration.ofMillis(10))
			
//			.setHousekeepingInterval(Duration.ofMillis(500))
//			.setHousekeepingTimeBudget(Duration.ofMillis(100))
			
			.createEmbeddedStorageFoundation()
			.createEmbeddedStorageManager(root).start();
	}
	
	public static synchronized void store(final Object object)
	{
		try
		{
			DB.storageManager.store(object);
		}
		catch(final Throwable t)
		{
			onStorageFailure(t);
		}
	}
	
	public static synchronized void storeAll(final Object... objects)
	{
		try
		{
			DB.storageManager.storeAll(objects);
		}
		catch(final Throwable t)
		{
			onStorageFailure(t);
		}
	}
	
	private static void onStorageFailure(final Throwable t)
	{
		if(DB.storageManager != null && DB.storageManager.isRunning())
		{
			try
			{
				DB.LOG.error("Storage error! Shutting down storage...", t);
				DB.storageManager.shutdown();
			}
			catch(final Throwable tt)
			{
				tt.printStackTrace();
			}
		}
		
		root.clear();
	}
}
