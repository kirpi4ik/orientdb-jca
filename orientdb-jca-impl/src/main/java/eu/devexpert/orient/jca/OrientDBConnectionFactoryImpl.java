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

import java.util.logging.Logger;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;

import eu.devexpert.orient.jca.api.OrientDBConnection;
import eu.devexpert.orient.jca.api.OrientDBConnectionFactory;
import eu.devexpert.orient.jca.api.OrientDBManagedConnectionFactory;

/**
 * 
 * @author Dumitru Ciubenco
 * @since 0.0.1
 * @created August 05, 2012
 */
public class OrientDBConnectionFactoryImpl implements OrientDBConnectionFactory {
	private static final long					serialVersionUID	= 1L;
	private static Logger						logger				= Logger.getLogger(OrientDBConnectionFactoryImpl.class.getName());
	private Reference							reference;
	private OrientDBManagedConnectionFactory	mcf;
	private ConnectionManager					txConnectionManager;

	/**
	 * Default constructor
	 * 
	 * @param mcf
	 *            ManagedConnectionFactory - E.g: OrientDBManagedConnectionFactoryImpl
	 * 
	 * @param cxManager
	 *            ConnectionManager (E.g: for JBoss7 it will be org.jboss.jca.core.connectionmanager.tx.TxConnectionManagerImpl)
	 */
	public OrientDBConnectionFactoryImpl(OrientDBManagedConnectionFactory mcf, ConnectionManager cxManager) {
		this.mcf = mcf;
		this.txConnectionManager = cxManager;
		logger.info("OrientDBConnectionFactoryImpl instantiated.");
	}

	/**
	 * Get connection from factory
	 * 
	 * @return OrientDBConnection instance
	 * 
	 * @exception ResourceException
	 *                Thrown if a connection can't be obtained
	 */
	public OrientDBConnection getConnection() throws ResourceException {
		logger.info("Get connection from ");
		return (OrientDBConnection) txConnectionManager.allocateConnection(mcf, null);
	}

	/**
	 * 
	 * Get the Reference instance.
	 * 
	 * @return Reference instance
	 * 
	 * @exception NamingException
	 *                Thrown if a reference can't be obtained
	 */
	public Reference getReference() throws NamingException {
		return reference;
	}

	/**
	 * 
	 * Set the Reference instance.
	 * 
	 * @param reference
	 *            A Reference instance
	 */
	public void setReference(Reference reference) {
		this.reference = reference;
	}
}
