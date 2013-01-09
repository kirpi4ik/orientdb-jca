##############################
## OrientDB JCA connector by Dumitru Ciubenco
##############################
Version 1.3.0
-----------------------------------

Features:
- OrientDB 1.3.0 upgrade


Version 1.2.0
-----------------------------------

Features:
- POJO save
- POJO retrieve
- Bug fixing
- Refactoring
- OrientDB 1.2.0 upgrade

Version 1.1.0
-----------------------------------

Features:
- OGraphDatabasePool support
- OGraphDatabase support
- Transaction support

Tested only on JBoss 7.1.1

Usage
--------------------------------------
 1. Build the source code using maven
 1. Deploy the orientdb-jca-connector-1.2.0.rar to your jboss
 1. Add resource addapter definition in your standalone.xml
<pre>
<code>
&lt;resource-adapter&gt;
	&lt;archive&gt;
		orientdb-jca-connector-1.2.0.rar
	&lt;/archive&gt;
	&lt;transaction-support&gt;XATransaction&lt;/transaction-support&gt;
	&lt;connection-definitions&gt;
		&lt;connection-definition class-name=&quot;eu.devexpert.orient.jca.OrientDBManagedConnectionFactoryImpl&quot; jndi-name=&quot;java:/jca/stylysh-orientdb&quot; enabled=&quot;true&quot; use-java-context=&quot;true&quot; pool-name=&quot;stylysh-orient-db-jca-pool&quot;&gt;
			&lt;config-property name=&quot;username&quot;&gt;
				admin
			&lt;/config-property&gt;
			&lt;config-property name=&quot;configFileLevel&quot;&gt;
				info
			&lt;/config-property&gt;
			&lt;config-property name=&quot;configProfiler&quot;&gt;
				false
			&lt;/config-property&gt;
			&lt;config-property name=&quot;connectionUrl&quot;&gt;
				local:${jboss.server.config.dir}/orient-db/stylysh
			&lt;/config-property&gt;
			&lt;config-property name=&quot;configDump&quot;&gt;
				true
			&lt;/config-property&gt;
			&lt;config-property name=&quot;configConsoleLevel&quot;&gt;
				info
			&lt;/config-property&gt;
			&lt;config-property name=&quot;configPoolMaxSize&quot;&gt;
				20
			&lt;/config-property&gt;
			&lt;config-property name=&quot;xa&quot;&gt;
				true
			&lt;/config-property&gt;
			&lt;config-property name=&quot;configPoolMinSize&quot;&gt;
				3
			&lt;/config-property&gt;
			&lt;config-property name=&quot;password&quot;&gt;
				admin
			&lt;/config-property&gt;
			&lt;config-property name=&quot;configEncoding&quot;&gt;
				utf8
			&lt;/config-property&gt;
			&lt;xa-pool&gt;
				&lt;min-pool-size&gt;3&lt;/min-pool-size&gt;
				&lt;max-pool-size&gt;15&lt;/max-pool-size&gt;
				&lt;prefill&gt;true&lt;/prefill&gt;
				&lt;use-strict-min&gt;false&lt;/use-strict-min&gt;
			&lt;/xa-pool&gt;
			&lt;security&gt;
				&lt;application/&gt;
			&lt;/security&gt;
		&lt;/connection-definition&gt;
	&lt;/connection-definitions&gt;
&lt;/resource-adapter&gt;
</code>
</pre>

 1. Add the deployment dependency to your(pom.xml if you are using maven) or to the MANIFEST.MF: deployment.orientdb-jca-connector-1.1.0-SNAPSHOT.rar
 1. Add the dependency to your project:
<pre>
<code>
  	&lt;dependency&gt;
			&lt;groupId&gt;eu.devexpert&lt;/groupId&gt;
			&lt;artifactId&gt;orientdb-jca-api&lt;/artifactId&gt;
			&lt;version&gt;1.3.0&lt;/version&gt;
		&lt;/dependency&gt;
		&lt;dependency&gt;
			&lt;groupId&gt;org.jboss.ironjacamar&lt;/groupId&gt;
			&lt;artifactId&gt;ironjacamar-spec-api&lt;/artifactId&gt;
			&lt;version&gt;1.0.0.Final&lt;/version&gt;
			&lt;scope&gt;provided&lt;/scope&gt;
		&lt;/dependency&gt;		
</code>
</pre>

 1. Use it in your application:
<pre>
<code>
@Named("graphService")
@Stateless
public class GraphService implements Serializable {
	@Resource(mappedName = "java:/jca/orientDb")
	private OrientDBConnectionFactory	odb;
		
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void initMyClasses() {
		try {
			odb.graph().createVertexType("NodeClass1");
			odb.graph().createVertexType("NodeClass2");
		} catch (ResourceException e) {
			e.printStackTrace();
		}
	}
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void createNode(Map<String, Object> fields) {
		try {
			ODocument node = odb.graph().createVertex("NodeClass1");
			node.fields(fields);
			node.save();
		} catch (ResourceException e) {
			e.printStackTrace();
		}
	}	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void createNewNode(MyBean object) {
		try {
			odb.graph().saveNode(object);
		} catch (ResourceException e) {
			e.printStackTrace();
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void createOrUpdateMyNode(MyBean object) {
		try {
			odb.graph().saveOrUpdateNode(object,"beanField1","beanField2"...);
		} catch (ResourceException e) {
			e.printStackTrace();
		}
	}	
}
</code>
</pre>
