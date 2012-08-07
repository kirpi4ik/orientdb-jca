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

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;

import org.grossbook.orient.jca.api.OrientDBConnection;
import org.grossbook.orient.jca.api.OrientDBConnectionFactory;
import org.grossbook.orient.jca.api.OrientDBManagedConnectionFactory;

/**
 * 
 * @author Dumitru Ciubenco
 * @since 0.0.1
 * @created August 05, 2012
 */
public class OrientDBConnectionFactoryImpl implements OrientDBConnectionFactory {
	private static final long					serialVersionUID	= 1L;
	private Reference							reference;
	private OrientDBManagedConnectionFactory	mcf;
	private ConnectionManager					connectionManager;
	private OrientDBConnection					connection;

	/**
	 * Default constructor
	 * 
	 * @param mcf
	 *            ManagedConnectionFactory
	 * 
	 * @param cxManager
	 *            ConnectionManager
	 */
	public OrientDBConnectionFactoryImpl(OrientDBManagedConnectionFactory mcf, ConnectionManager cxManager) {
		this.mcf = mcf;
		this.connectionManager = cxManager;
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
		connection = (OrientDBConnection) connectionManager.allocateConnection(mcf, null);
		return connection;
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
