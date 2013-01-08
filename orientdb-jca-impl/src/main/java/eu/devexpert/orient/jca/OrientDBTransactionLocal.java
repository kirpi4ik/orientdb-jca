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
import javax.resource.spi.LocalTransaction;

import com.orientechnologies.orient.core.tx.OTransaction;

/**
 * 
 * @author Dumitru Ciubenco
 * @since 0.0.1
 * @created August 05, 2012
 */
public class OrientDBTransactionLocal implements LocalTransaction {
	private OTransaction					transaction;
	private OrientDBManagedConnectionImpl	orientDBManagedConnection;
	private static Logger					logger	= Logger.getLogger(OrientDBTransactionLocal.class.getName());

	public OrientDBTransactionLocal(OrientDBManagedConnectionImpl mc, OTransaction transaction) {
		this.orientDBManagedConnection = mc;
		this.transaction = transaction;
	}

	public boolean isActive() {
		return null != transaction;
	}

	public void rollback() throws ResourceException {
		if(null != transaction) {
			transaction.rollback();
			finish();
			orientDBManagedConnection.sendTxRolledbackEvent(null);
			logger.info("Rollback transaction");
		}else {
			logger.warning("Rollback: Transaction is null");
		}
	}

	public void finish() {
		transaction = null;
	}

	public void commit() throws ResourceException {
		if(null != transaction) {
			transaction.commit();
			finish();
			orientDBManagedConnection.sendTxCommittedEvent(null);
			logger.info("Commit transaction");
		}else {
			logger.warning("Commit: Transaction is null");
		}
	}

	public void begin() throws ResourceException {
		if(null != transaction) {
			transaction.begin();
			orientDBManagedConnection.sendTxStartedEvent(null);
			logger.info("Begin transaction");
		}else {
			logger.warning("Begin transaction: Transaction is null");
		}
	}
}
