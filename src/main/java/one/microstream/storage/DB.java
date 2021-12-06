package one.microstream.storage;

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
			.setStorageDirectoryInUserHome("microstream-data/AdvancedTrainingMaster")
			.createEmbeddedStorageFoundation()
			.createEmbeddedStorageManager(root).start();
	}
}
