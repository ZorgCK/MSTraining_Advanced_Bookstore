package one.microstream.storage;

import java.time.Duration;

import one.microstream.storage.embedded.configuration.types.EmbeddedStorageConfiguration;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;


public class DB
{
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
}
