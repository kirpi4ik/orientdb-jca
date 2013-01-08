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
public class OrientDBTransactionXA implements XAResource, Serializable {
	private static final long				serialVersionUID	= 1L;
	private static Logger					logger				= Logger.getLogger(OrientDBTransactionXA.class.getName());
	private int								timeout;
	private OGraphDatabase					database;
	private OrientDBManagedConnectionImpl	connection;

	public OrientDBTransactionXA(final OrientDBManagedConnectionImpl orientDBManagedConnection, final OGraphDatabase oGraphDatabase) {
		this.database = oGraphDatabase;
		this.connection = orientDBManagedConnection;
		logger.info("Graph DBXA resource instantiated");
	}

	public void start(final Xid xid, final int flags) throws XAException {
		boolean tmJoin = (flags & XAResource.TMJOIN) != 0;
		boolean tmResume = (flags & XAResource.TMRESUME) != 0;
		/* Check flags - only one of TMNOFLAGS, TMJOIN, or TMRESUME. */
		if(xid == null || (tmJoin && tmResume) || (!tmJoin && !tmResume && flags != XAResource.TMNOFLAGS)) {
			throw new XAException(XAException.XAER_INVAL);
		}
		database.begin();
		logger.info("xa begin");
	}

	public void commit(Xid xid, boolean ignore/* onePhase */) throws XAException {
		if(xid == null) {
			return;
		}
		database.commit();
		logger.info("xa commit");
	}

	public void rollback(Xid arg0) throws XAException {
		database.rollback();
		logger.info("xa rollback");
	}

	public void end(Xid arg0, int flags) throws XAException {

		/* flags - One of TMSUCCESS, TMFAIL, or TMSUSPEND. */

		boolean tmFail = (flags & XAResource.TMFAIL) != 0;
		boolean tmSuccess = (flags & XAResource.TMSUCCESS) != 0;
		boolean tmSuspend = (flags & XAResource.TMSUSPEND) != 0;
		if((tmFail && tmSuccess) || ((tmFail || tmSuccess) && tmSuspend)) {
			throw new XAException(XAException.XAER_INVAL);
		}

		if(tmSuspend && database.getTransaction() != null) {
			try {
				database.getTransaction().close();
			}
			finally {
				logger.info("xa close");
				connection.closeHandles();
			}
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

	public int prepare(Xid xid) throws XAException {
		return XA_OK;
	}

	public Xid[] recover(int flag) throws XAException {
		return new Xid[0];
	}

	public boolean setTransactionTimeout(int timeout) throws XAException {
		this.timeout = timeout;
		return true;
	}

}
