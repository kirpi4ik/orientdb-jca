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
package org.grossbook.orient.jca.api;

import java.io.PrintWriter;
import java.util.Set;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterAssociation;
import javax.resource.spi.TransactionSupport;
import javax.security.auth.Subject;

/**
 * 
 * @author Dumitru Ciubenco
 * @since 0.0.1
 * @created August 05, 2012
 */
public interface OrientDBManagedConnectionFactory extends ManagedConnectionFactory, ResourceAdapterAssociation, TransactionSupport {

	public void start();

	public void stop();

	public TransactionSupportLevel getTransactionSupport();

	public ResourceAdapter getResourceAdapter();

	public void setResourceAdapter(ResourceAdapter ra) throws ResourceException;

	public Object createConnectionFactory() throws ResourceException;

	public Object createConnectionFactory(ConnectionManager cxManager) throws ResourceException;

	public ManagedConnection createManagedConnection(Subject arg0, ConnectionRequestInfo arg1) throws ResourceException;

	public PrintWriter getLogWriter() throws ResourceException;

	public ManagedConnection matchManagedConnections(Set connectionSet, Subject subject, ConnectionRequestInfo cxRequestInfo) throws ResourceException;

	public void setLogWriter(PrintWriter out) throws ResourceException;

	public int hashCode();

	public String getConnectionUrl();

	public void setConnectionUrl(String connectionUrl);

	public boolean isXa();

	public void setXa(boolean xa);

	public boolean equals(Object obj);

	public void destroyManagedConnection(OrientDBManagedConnection orientDBManagedConnection);

}