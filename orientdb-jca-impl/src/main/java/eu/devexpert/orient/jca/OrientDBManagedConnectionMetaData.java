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

import javax.resource.ResourceException;
import javax.resource.spi.ManagedConnectionMetaData;

/**
 * 
 * @author Dumitru Ciubenco
 * @since 0.0.1
 * @created August 05, 2012
 */
public class OrientDBManagedConnectionMetaData implements ManagedConnectionMetaData {
	private static Logger	log	= Logger.getLogger(OrientDBManagedConnectionMetaData.class.getName());

	public OrientDBManagedConnectionMetaData() {
		super();
		log.info("constructor");
	}

	/**
	 * Returns Product name of the underlying EIS instance connected through the ManagedConnection.
	 * 
	 * @return Product name of the EIS instance
	 * @throws ResourceException
	 *             Thrown if an error occurs
	 */
	public String getEISProductName() throws ResourceException {
		return "OrientDB graph resource adapter";
	}

	/**
	 * Returns Product version of the underlying EIS instance connected through the ManagedConnection.
	 * 
	 * @return Product version of the EIS instance
	 * @throws ResourceException
	 *             Thrown if an error occurs
	 */
	public String getEISProductVersion() throws ResourceException {
		return "1.2.0-SNAPSHOT";
	}

	/**
	 * Returns maximum limit on number of active concurrent connections
	 * 
	 * @return Maximum limit for number of active concurrent connections
	 * @throws ResourceException
	 *             Thrown if an error occurs
	 */
	public int getMaxConnections() throws ResourceException {
		log.info("getMaxConnections()");
		return 0;
	}

	/**
	 * Returns name of the user associated with the ManagedConnection instance
	 * 
	 * @return Name of the user
	 * @throws ResourceException
	 *             Thrown if an error occurs
	 */
	public String getUserName() throws ResourceException {
		log.info("getUserName()");
		return null;
	}
}