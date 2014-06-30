import java.util.ArrayList;

import org.apache.log4j.BasicConfigurator;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.common.io.FileSystemUtils;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.node.internal.InternalSettingsPreparer;
import org.elasticsearch.plugins.PluginManager;
import static org.elasticsearch.common.settings.ImmutableSettings.Builder.EMPTY_SETTINGS;


public class testing {
	public static void main(String[] args) throws Exception{
		BasicConfigurator.configure();
		ParseInputFile obj = new ParseInputFile("input.txt");
		ArrayList<UserDAO> userList = obj.parse();
		for(UserDAO user :userList){
			System.out.println(user.toString());
		}
		indexSearch ES = new indexSearch();
		String indexName = "demographic",docType = "data",clusterName="elasticsearch",hostName = "localhost";
		int port = 9300;
		Client client = ES.createClient(clusterName,hostName,port);
		Tuple<Settings, Environment> initialSettings = InternalSettingsPreparer.prepareSettings(EMPTY_SETTINGS, true);

        if (!initialSettings.v2().pluginsFile().exists()) {
            FileSystemUtils.mkdirs(initialSettings.v2().pluginsFile());
        }
        PluginManager pluginManager = new PluginManager(initialSettings.v2(), null, null, null);
        pluginManager.getListInstalledPlugins();
        initialSettings.v1().getClassLoader().loadClass("org.apache.log4j.Logger");
        initialSettings.v1().getClassLoader().loadClass("org.elasticsearch.index.analysis.PhoneticTokenFilterFactory");
		ES.deleteIndex(client, indexName);
		ES.createIndex(client, indexName);
		ES.addDoc(client,indexName, docType,userList);
		System.out.println("\n"+ES.getNumberDocs(client, indexName, docType));
		SearchResponse response = ES.searchDoc(client, indexName, docType, userList.get(10));
	}
}
