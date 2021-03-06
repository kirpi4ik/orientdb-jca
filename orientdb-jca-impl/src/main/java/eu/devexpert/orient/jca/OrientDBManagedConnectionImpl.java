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
package eu.devexpert.orient.jca;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ConnectionEventListener;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.LocalTransaction;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionMetaData;
import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;

import eu.devexpert.orient.jca.api.OrientDBGraph;
import eu.devexpert.orient.jca.api.OrientDBManagedConnection;
import eu.devexpert.orient.jca.api.OrientDBManagedConnectionFactory;

/**
 * 
 * @author Dumitru Ciubenco
 * @since 0.0.1
 * @created August 05, 2012
 */
public class OrientDBManagedConnectionImpl implements ManagedConnection, OrientDBManagedConnection {
	private static Logger						log	= Logger.getLogger(OrientDBManagedConnectionImpl.class.getName());
	/** MCF */
	private OrientDBManagedConnectionFactory	mcf;
	/** Log writer */
	private PrintWriter							logWriter;
	/** Listeners */
	private LinkedList<ConnectionEventListener>	listeners;
	private LinkedList<OrientDBGraph>		handles;
	/** Connection */
	private OrientDBTransactionXA				xaResource;
	private LocalTransaction					localTransaction;
	private OGraphDatabase						database;

	/**
	 * 
	 * default constructor
	 * 
	 * @param mcf
	 * @param oGraphDatabase
	 * 
	 */
	public OrientDBManagedConnectionImpl(OrientDBManagedConnectionFactory mcf, OGraphDatabase oGraphDatabase) {
		log.info("Create new managed connection");
		this.mcf = mcf;
		this.database = oGraphDatabase;
		this.listeners = new LinkedList<ConnectionEventListener>();
		this.handles = new LinkedList<OrientDBGraph>();
		this.xaResource = new OrientDBTransactionXA(this, this.database);
		this.localTransaction = new OrientDBTransactionLocal(this, database.getTransaction());
	}

	/**
	 * Send event.
	 */
	private void sendEvent(int type, OrientDBGraph handle, Exception cause) {
		ConnectionEvent event = new ConnectionEvent(this, type, cause);
		if(handle != null) {
			event.setConnectionHandle(handle);
		}

		sendEvent(event);
	}

	/**
	 * Send connection closed event.
	 */
	void sendClosedEvent(OrientDBGraph handle) {
		log.info("send-event-close");
		sendEvent(ConnectionEvent.CONNECTION_CLOSED, handle, null);
	}

	/**
	 * Send connection error event.
	 */
	void sendErrorEvent(OrientDBGraph handle, Exception cause) {
		log.info("send-event-error");
		sendEvent(ConnectionEvent.CONNECTION_ERROR_OCCURRED, handle, cause);
	}

	/**
	 * Send transaction committed event.
	 */
	void sendTxCommittedEvent(OrientDBGraph handle) {
		log.info("send-event-tx-commited");
		sendEvent(ConnectionEvent.LOCAL_TRANSACTION_COMMITTED, handle, null);
	}

	/**
	 * Send transaction rolledback event.
	 */
	void sendTxRolledbackEvent(OrientDBGraph handle) {
		log.info("send-event-tx-rollback");
		sendEvent(ConnectionEvent.LOCAL_TRANSACTION_ROLLEDBACK, handle, null);
	}

	/**
	 * Send transaction started event.
	 */
	void sendTxStartedEvent(OrientDBGraph handle) {
		log.info("send-event-tx-started");
		sendEvent(ConnectionEvent.LOCAL_TRANSACTION_STARTED, handle, null);
	}

	/**
	 * 
	 * Close handle
	 * 
	 * @param handle
	 *            The handle
	 */

	public void closeHandle(OrientDBGraph handle) {
		if(handle != null) {
			removeHandle(handle);
			sendClosedEvent(handle);
		}
	}

	/**
	 * Send event.
	 */
	private void sendEvent(ConnectionEvent event) {
		log.info("JCA received event : " + event.getId());
		synchronized(listeners) {
			for(Iterator<ConnectionEventListener> i = listeners.iterator(); i.hasNext();) {
				ConnectionEventListener listener = i.next();

				switch(event.getId()){
					case ConnectionEvent.CONNECTION_CLOSED:
						listener.connectionClosed(event);
						break;
					case ConnectionEvent.CONNECTION_ERROR_OCCURRED:
						listener.connectionErrorOccurred(event);
						break;
					case ConnectionEvent.LOCAL_TRANSACTION_COMMITTED:
						listener.localTransactionCommitted(event);
						break;
					case ConnectionEvent.LOCAL_TRANSACTION_ROLLEDBACK:
						listener.localTransactionRolledback(event);
						break;
					case ConnectionEvent.LOCAL_TRANSACTION_STARTED:
						listener.localTransactionStarted(event);
						break;
					default:
						// Unknown event, skip
				}
			}
		}
	}

	public void closeHandles() {
		synchronized(handles) {
			OrientDBGraph[] handlesArray = new OrientDBGraph[handles.size()];
			handles.toArray(handlesArray);
			for(int i = 0; i < handlesArray.length; i++) {
				this.closeHandle(handlesArray[i]);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see eu.devexpert.orient.jca.OrientDBManagedConnectionApi#getConnection(javax.security.auth.Subject, javax.resource.spi.ConnectionRequestInfo)
	 */

	public Object getConnection(Subject subject, ConnectionRequestInfo cxRequestInfo) throws ResourceException {
		OrientDBGraph connection = new OrientDBGraphImpl(this, mcf, database);
		addHandle(connection);
		return connection;
	}

	/*
	 * (non-Javadoc)
	 * @see eu.devexpert.orient.jca.OrientDBManagedConnectionApi#associateConnection(java.lang.Object)
	 */

	public void associateConnection(Object connection) throws ResourceException {
		OrientDBGraph handle = (OrientDBGraph) connection;
		if(handle.getMc() != this) {
			handle.getMc().removeHandle(handle);
			handle.setMc(this);
			addHandle(handle);
		}
		log.info("associateConnection()");
	}

	public void addHandle(OrientDBGraph handle) {
		synchronized(handles) {
			handles.addFirst(handle);
		}
	}

	public void removeHandle(OrientDBGraph handle) {
		synchronized(handles) {
			handles.addFirst(handle);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see eu.devexpert.orient.jca.OrientDBManagedConnectionApi#cleanup()
	 */
	public void cleanup() throws ResourceException {
		synchronized(handles) {
			this.localTransaction = null;
			this.handles.clear();
		}
		log.info("Cleanup managed connection resources");
	}

	/*
	 * (non-Javadoc)
	 * @see eu.devexpert.orient.jca.OrientDBManagedConnectionApi#destroy()
	 */

	public void destroy() throws ResourceException {
		cleanup();
		mcf.destroyManagedConnection(this);
		log.info("Destroy managed connection resources");
	}

	/*
	 * (non-Javadoc)
	 * @see eu.devexpert.orient.jca.OrientDBManagedConnectionApi#addConnectionEventListener(javax.resource.spi. ConnectionEventListener)
	 */

	public void addConnectionEventListener(ConnectionEventListener listener) {
		synchronized(listeners) {
			if(!listeners.contains(listener)) {
				listeners.add(listener);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see eu.devexpert.orient.jca.OrientDBManagedConnectionApi#removeConnectionEventListener(javax.resource.spi. ConnectionEventListener)
	 */
	public void removeConnectionEventListener(ConnectionEventListener listener) {
		if(listener == null)
			throw new IllegalArgumentException("Listener is null");
		listeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see eu.devexpert.orient.jca.OrientDBManagedConnectionApi#getLogWriter()
	 */

	public PrintWriter getLogWriter() throws ResourceException {
		return logWriter;
	}

	/*
	 * (non-Javadoc)
	 * @see eu.devexpert.orient.jca.OrientDBManagedConnectionApi#setLogWriter(java.io.PrintWriter)
	 */

	public void setLogWriter(PrintWriter out) throws ResourceException {
		this.logWriter = out;
	}

	/*
	 * (non-Javadoc)
	 * @see eu.devexpert.orient.jca.OrientDBManagedConnectionApi#getLocalTransaction()
	 */
	public LocalTransaction getLocalTransaction() throws ResourceException {
		return localTransaction;
	}

	/*
	 * (non-Javadoc)
	 * @see eu.devexpert.orient.jca.OrientDBManagedConnectionApi#getXAResource()
	 */

	public XAResource getXAResource() throws ResourceException {
		return this.xaResource;
	}

	/*
	 * (non-Javadoc)
	 * @see eu.devexpert.orient.jca.OrientDBManagedConnectionApi#getMetaData()
	 */

	public ManagedConnectionMetaData getMetaData() throws ResourceException {
		return new OrientDBManagedConnectionMetaData();
	}

	/*
	 * (non-Javadoc)
	 * @see eu.devexpert.orient.jca.OrientDBManagedConnectionApi#getMcf()
	 */
	public OrientDBManagedConnectionFactory getMcf() {
		return mcf;
	}

}
