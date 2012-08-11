/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grossbook.orient.jca;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;

import javax.resource.ResourceException;
import javax.resource.spi.ConfigProperty;
import javax.resource.spi.ConnectionDefinition;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ResourceAdapter;
import javax.security.auth.Subject;

import org.grossbook.orient.jca.api.OrientDBConnection;
import org.grossbook.orient.jca.api.OrientDBConnectionFactory;
import org.grossbook.orient.jca.api.OrientDBManagedConnection;
import org.grossbook.orient.jca.api.OrientDBManagedConnectionFactory;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.db.graph.OGraphDatabasePool;
import com.orientechnologies.orient.core.exception.OStorageException;

/**
 * 
 * @author Dumitru Ciubenco
 * @since 0.0.1
 * @created August 05, 2012
 */
@ConnectionDefinition(connectionFactory = OrientDBConnectionFactory.class, connectionFactoryImpl = OrientDBConnectionFactoryImpl.class, connection = OrientDBConnection.class, connectionImpl = OrientDBConnectionImpl.class)
public class OrientDBManagedConnectionFactoryImpl implements OrientDBManagedConnectionFactory {
	private static final String		LOCAL_DATABASES_TEMP_ORIENTDB	= "local:../databases/temp-orientdb";
	private static final long		serialVersionUID				= 1L;
	/** The resource adapter */
	private OrientDBResourceAdapter	ra;
	private PrintWriter				logwriter;
	@ConfigProperty(type = String.class, defaultValue = LOCAL_DATABASES_TEMP_ORIENTDB)
	private String					connectionUrl;
	@ConfigProperty(defaultValue = "true")
	private boolean					xa;
	private int						connectionsCreated				= 0;
	private OGraphDatabasePool		databasePool;

	public OrientDBManagedConnectionFactoryImpl() {
		this.logwriter = new PrintWriter(System.out);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.grossbook.orient.jca.OrientDBManagedConnectionFactory#start()
	 */
	public void start() {
		// FIXME: Will be moved to standalone.xml as config property
		databasePool = new OGraphDatabasePool(connectionUrl, "admin", "admin");
		databasePool.setup(3, 20);
		try {
			databasePool.acquire();
		} catch (OStorageException notExist) {
			(new OGraphDatabase(connectionUrl)).create();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.grossbook.orient.jca.OrientDBManagedConnectionFactory#stop()
	 */
	public void stop() {
		databasePool.close();
	}

	private OGraphDatabase getDatabase() {
		return databasePool.acquire();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.grossbook.orient.jca.OrientDBManagedConnectionFactory#getTransactionSupport()
	 */
	public TransactionSupportLevel getTransactionSupport() {
		if (isXa()) {
			return TransactionSupportLevel.XATransaction;
		} else {
			return TransactionSupportLevel.LocalTransaction;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.grossbook.orient.jca.OrientDBManagedConnectionFactory#getResourceAdapter()
	 */
	public ResourceAdapter getResourceAdapter() {
		return ra;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.grossbook.orient.jca.OrientDBManagedConnectionFactory#setResourceAdapter(javax.resource.spi.ResourceAdapter)
	 */
	public void setResourceAdapter(ResourceAdapter ra) throws ResourceException {
		this.ra = (OrientDBResourceAdapter) ra;
		this.ra.addFactory(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.grossbook.orient.jca.OrientDBManagedConnectionFactory#createConnectionFactory()
	 */
	public Object createConnectionFactory() throws ResourceException {
		throw new ResourceException("This resource adapter doesn't support non-managed environments");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.grossbook.orient.jca.OrientDBManagedConnectionFactory#createConnectionFactory(javax.resource.spi.
	 * ConnectionManager)
	 */
	public Object createConnectionFactory(ConnectionManager cxManager) throws ResourceException {
		return new OrientDBConnectionFactoryImpl(this, cxManager);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.grossbook.orient.jca.OrientDBManagedConnectionFactory#createManagedConnection(javax.security.auth.Subject,
	 * javax.resource.spi.ConnectionRequestInfo)
	 */
	public ManagedConnection createManagedConnection(Subject arg0, ConnectionRequestInfo arg1) throws ResourceException {
		connectionsCreated++;
		return new OrientDBManagedConnectionImpl(this, getDatabase());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.grossbook.orient.jca.OrientDBManagedConnectionFactory#getLogWriter()
	 */
	public PrintWriter getLogWriter() throws ResourceException {
		logwriter.append("getLogWriter()");
		return logwriter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.grossbook.orient.jca.OrientDBManagedConnectionFactory#matchManagedConnections(java.util.Set,
	 * javax.security.auth.Subject, javax.resource.spi.ConnectionRequestInfo)
	 */
	@SuppressWarnings("unchecked")
	public ManagedConnection matchManagedConnections(@SuppressWarnings("rawtypes") Set connectionSet, Subject subject, ConnectionRequestInfo cxRequestInfo)
			throws ResourceException {
		logwriter.append("matchManagedConnections()");
		ManagedConnection result = null;
		Iterator<ManagedConnection> it = connectionSet.iterator();
		while (result == null && it.hasNext()) {
			ManagedConnection mc = it.next();
			if (mc instanceof OrientDBManagedConnectionImpl) {
				result = mc;
			}

		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.grossbook.orient.jca.OrientDBManagedConnectionFactory#setLogWriter(java.io.PrintWriter)
	 */
	public void setLogWriter(PrintWriter out) throws ResourceException {
		logwriter.append("setLogWriter()");
		logwriter = out;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.grossbook.orient.jca.OrientDBManagedConnectionFactory#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ra == null) ? 0 : ra.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.grossbook.orient.jca.OrientDBManagedConnectionFactory#getConnectionUrl()
	 */
	public String getConnectionUrl() {
		return connectionUrl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.grossbook.orient.jca.OrientDBManagedConnectionFactory#setConnectionUrl(java.lang.String)
	 */
	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.grossbook.orient.jca.OrientDBManagedConnectionFactory#isXa()
	 */
	public boolean isXa() {
		return xa;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.grossbook.orient.jca.OrientDBManagedConnectionFactory#setXa(boolean)
	 */
	public void setXa(boolean xa) {
		this.xa = xa;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.grossbook.orient.jca.OrientDBManagedConnectionFactory#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (obj instanceof OrientDBManagedConnectionFactory) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.grossbook.orient.jca.OrientDBManagedConnectionFactory#destroyManagedConnection(org.grossbook.orient.jca.api
	 * .OrientDBManagedConnection)
	 */
	public void destroyManagedConnection(OrientDBManagedConnection orientDBManagedConnection) {
		connectionsCreated--;
		if (connectionsCreated <= 0) {
			// shutdown database
		}
	}

}
