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

import java.io.Serializable;
import java.util.logging.Logger;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;

/**
 * 
 * @author Dumitru Ciubenco
 * @since 0.0.1
 * @created August 05, 2012
 */
public class OrientDBXAResource implements XAResource, Serializable {
	private static final long				serialVersionUID	= 1L;
	private static Logger					logger				= Logger.getLogger(OrientDBXAResource.class.getName());
	private int								timeout;
	private OGraphDatabase					database;
	private OrientDBManagedConnectionImpl	connection;
	private boolean							ending;

	public OrientDBXAResource(OrientDBManagedConnectionImpl orientDBManagedConnection, OGraphDatabase oGraphDatabase) {
		this.database = oGraphDatabase;
		this.connection = orientDBManagedConnection;
		logger.info("Graph DBXA resource instantiated");
	}

	public void commit(Xid arg0, boolean arg1) throws XAException {
		database.commit();
		logger.info("xa commit");
	}

	public void end(Xid arg0, int arg1) throws XAException {

		if(!ending) {
			this.ending = true;
			try {
				logger.info("xa end : " + ending);
				database.getTransaction().close();
			}
			finally {
				logger.info("xa close");
				connection.closeHandles();
			}
			this.ending = false;
		}

	}

	public void forget(Xid arg0) throws XAException {
		logger.info("xa forget");
	}

	public int getTransactionTimeout() throws XAException {
		return this.timeout;
	}

	public boolean isSameRM(XAResource arg0) throws XAException {
		return this == arg0;
	}

	public int prepare(Xid arg0) throws XAException {
		return XA_OK;
	}

	public Xid[] recover(int arg0) throws XAException {
		return new Xid[0];
	}

	public void rollback(Xid arg0) throws XAException {
		database.rollback();
		logger.info("xa rollback");
	}

	public boolean setTransactionTimeout(int arg0) throws XAException {
		this.timeout = arg0;
		return true;
	}

	public void start(Xid arg0, int arg1) throws XAException {
		database.begin();
		logger.info("xa begin transaction");
	}

}
