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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.Connector;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.TransactionSupport;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;

import eu.devexpert.orient.jca.api.OrientDBManagedConnectionFactory;

/**
 * 
 * @author Dumitru Ciubenco
 * @since 0.0.1
 * @created August 05, 2012
 */
@Connector(
	reauthenticationSupport = false, transactionSupport = TransactionSupport.TransactionSupportLevel.XATransaction, version = "1.2.0", vendorName = "eu.devexpert",
	eisType = "OrientDB graph database")
public class OrientDBResourceAdapter implements ResourceAdapter {
	private static Logger								logger		= Logger.getLogger(OrientDBResourceAdapter.class.getName());

	private final Set<OrientDBManagedConnectionFactory>	factories	= new HashSet<OrientDBManagedConnectionFactory>();

	/**
	 * This is called during the activation of a message endpoint.
	 * 
	 * @param endpointFactory
	 *            A message endpoint factory instance.
	 * @param spec
	 *            An activation spec JavaBean instance.
	 * @throws ResourceException
	 *             generic exception
	 */
	public void endpointActivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) throws ResourceException {
		logger.info("Resource endpoint activation");
	}

	/**
	 * This is called when a message endpoint is deactivated.
	 * 
	 * @param endpointFactory
	 *            A message endpoint factory instance.
	 * @param spec
	 *            An activation spec JavaBean instance.
	 */
	public void endpointDeactivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) {
		logger.info("Resource endpoint deactivation");
	}

	public void addFactory(OrientDBManagedConnectionFactory factory) {
		factories.add(factory);
	}

	/**
	 * * This method is called by the application server during crash recovery.
	 */
	public XAResource[] getXAResources(ActivationSpec[] arg0) throws ResourceException {
		logger.info("Get XA resource endpoint during crash recovery");
		return null;
	}

	public void start(BootstrapContext arg0) throws ResourceAdapterInternalException {
		for(OrientDBManagedConnectionFactory factory : factories) {
			factory.start();
			logger.info("ConnectionFactory was started");
		}
	}

	public void stop() {
		for(OrientDBManagedConnectionFactory factory : factories) {
			factory.stop();
			logger.info("ConnectionFactory was sopped");
		}
	}

}
