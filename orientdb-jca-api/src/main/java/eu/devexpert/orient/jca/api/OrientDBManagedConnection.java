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
package eu.devexpert.orient.jca.api;

import java.io.PrintWriter;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionEventListener;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.LocalTransaction;
import javax.resource.spi.ManagedConnectionMetaData;
import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;

/**
 * 
 * @author Dumitru Ciubenco
 * @since 0.0.1
 * @created August 05, 2012
 */
public interface OrientDBManagedConnection {

	/**
	 * 
	 * Creates a new connection handle for the underlying physical connection represented by the ManagedConnection
	 * instance.
	 * 
	 * @param subject
	 *            Security context as JAAS subject
	 * 
	 * @param cxRequestInfo
	 *            ConnectionRequestInfo instance
	 * 
	 * @return generic Object instance representing the connection handle.
	 * 
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */

	public Object getConnection(Subject subject, ConnectionRequestInfo cxRequestInfo) throws ResourceException;

	/**
	 * 
	 * Used by the container to change the association of an application-level connection handle with a
	 * ManagedConneciton instance.
	 * 
	 * @param connection
	 *            Application-level connection handle
	 * 
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */

	public void associateConnection(Object connection) throws ResourceException;

	/**
	 * 
	 * Application server calls this method to force any cleanup on
	 * 
	 * the ManagedConnection instance.
	 * 
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public void cleanup() throws ResourceException;

	/**
	 * 
	 * Destroys the physical connection to the underlying resource manager.
	 * 
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */

	public void destroy() throws ResourceException;

	/**
	 * 
	 * Adds a connection event listener to the ManagedConnection instance.
	 * 
	 * @param listener
	 *            A new ConnectionEventListener to be registered
	 */

	public void addConnectionEventListener(ConnectionEventListener listener);

	/**
	 * 
	 * Removes an already registered connection event listener
	 * 
	 * from the ManagedConnection instance.
	 * 
	 * @param listener
	 *            Already registered connection event listener to be removed
	 */
	public void removeConnectionEventListener(ConnectionEventListener listener);

	/**
	 * 
	 * Gets the log writer for this ManagedConnection instance.
	 * 
	 * 
	 * 
	 * @return Character ourput stream associated with this
	 * 
	 *         Managed-Connection instance
	 * 
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */

	public PrintWriter getLogWriter() throws ResourceException;

	/**
	 * 
	 * Sets the log writer for this ManagedConnection instance.
	 * 
	 * 
	 * 
	 * @param out
	 *            Character Output stream to be associated
	 * 
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */

	public void setLogWriter(PrintWriter out) throws ResourceException;

	/**
	 * 
	 * Returns an <code>javax.resource.spi.LocalTransaction</code> instance.
	 * 
	 * @return LocalTransaction instance
	 * 
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */
	public LocalTransaction getLocalTransaction() throws ResourceException;

	/**
	 * 
	 * Returns an <code>javax.transaction.xa.XAresource</code> instance.
	 * 
	 * 
	 * 
	 * @return XAResource instance
	 * 
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */

	public XAResource getXAResource() throws ResourceException;

	/**
	 * 
	 * Gets the metadata information for this connection's underlying
	 * 
	 * EIS resource manager instance.
	 * 
	 * 
	 * 
	 * @return ManagedConnectionMetaData instance
	 * 
	 * @throws ResourceException
	 *             generic exception if operation fails
	 */

	public ManagedConnectionMetaData getMetaData() throws ResourceException;

	public OrientDBManagedConnectionFactory getMcf();

	public abstract void removeHandle(OrientDBGraph handle);

	public abstract void addHandle(OrientDBGraph handle);

	public abstract void closeHandle(OrientDBGraph handle);

}