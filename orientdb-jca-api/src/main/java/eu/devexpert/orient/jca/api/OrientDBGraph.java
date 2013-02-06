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

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.resource.ResourceException;

import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseComplex;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.exception.OConcurrentModificationException;
import com.orientechnologies.orient.core.exception.OValidationException;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.iterator.ORecordIteratorClass;
import com.orientechnologies.orient.core.iterator.ORecordIteratorCluster;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.query.OQuery;
import com.orientechnologies.orient.core.record.ORecordInternal;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.type.tree.OMVRBTreeRIDSet;

/**
 * 
 * @author Dumitru Ciubenco
 * @since 0.0.1
 * @created August 05, 2012
 */
public interface OrientDBGraph extends ODatabase {

	public <THISDB extends ODatabase> THISDB open(String iUserName, String iUserPassword);

	public <THISDB extends ODatabase> THISDB create();

	public void close();

	public long countVertexes();

	public long countEdges();

	public ODocument createVertex();

	public ODocument createVertex(String iClassName);

	public ODocument createVertex(String iClassName, Object... iFields);

	public ODocument createEdge(ORID iSourceVertexRid, ORID iDestVertexRid);

	public ODocument createEdge(ORID iSourceVertexRid, ORID iDestVertexRid, String iClassName);

	public void removeVertex(OIdentifiable iVertex);

	public void removeEdge(OIdentifiable iEdge);

	public ODocument createEdge(ODocument iSourceVertex, ODocument iDestVertex);

	public ODocument createEdge(ODocument iOutVertex, ODocument iInVertex, String iClassName);

	public ODocument createEdge(ODocument iOutVertex, ODocument iInVertex, String iClassName, Object... iFields);

	/**
	 * Returns all the edges between the vertexes iVertex1 and iVertex2.
	 * 
	 * @param iVertex1
	 *            First Vertex
	 * @param iVertex2
	 *            Second Vertex
	 * @return The Set with the common Edges between the two vertexes. If edges aren't found the set is empty
	 */
	public Set<OIdentifiable> getEdgesBetweenVertexes(ODocument iVertex1, ODocument iVertex2);

	/**
	 * Returns all the edges between the vertexes iVertex1 and iVertex2 with label between the array of labels passed as iLabels.
	 * 
	 * @param iVertex1
	 *            First Vertex
	 * @param iVertex2
	 *            Second Vertex
	 * @param iLabels
	 *            Array of strings with the labels to get as filter
	 * @return The Set with the common Edges between the two vertexes. If edges aren't found the set is empty
	 */
	public Set<OIdentifiable> getEdgesBetweenVertexes(ODocument iVertex1, ODocument iVertex2, String[] iLabels);

	/**
	 * Returns all the edges between the vertexes iVertex1 and iVertex2 with label between the array of labels passed as iLabels and with class between the array of class names
	 * passed as iClassNames.
	 * 
	 * @param iVertex1
	 *            First Vertex
	 * @param iVertex2
	 *            Second Vertex
	 * @param iLabels
	 *            Array of strings with the labels to get as filter
	 * @param iClassNames
	 *            Array of strings with the name of the classes to get as filter
	 * @return The Set with the common Edges between the two vertexes. If edges aren't found the set is empty
	 */
	public Set<OIdentifiable> getEdgesBetweenVertexes(ODocument iVertex1, ODocument iVertex2, String[] iLabels, String[] iClassNames);

	public Set<OIdentifiable> getOutEdges(OIdentifiable iVertex);

	/**
	 * Retrieves the outgoing edges of vertex iVertex having label equals to iLabel.
	 * 
	 * @param iVertex
	 *            Target vertex
	 * @param iLabel
	 *            Label to search
	 * @return
	 */
	public Set<OIdentifiable> getOutEdges(OIdentifiable iVertex, String iLabel);

	/**
	 * Retrieves the outgoing edges of vertex iVertex having the requested properties iProperties set to the passed values
	 * 
	 * @param iVertex
	 *            Target vertex
	 * @param iProperties
	 *            Map where keys are property names and values the expected values
	 * @return
	 */
	public Set<OIdentifiable> getOutEdgesHavingProperties(OIdentifiable iVertex, Map<String, Object> iProperties);

	/**
	 * Retrieves the outgoing edges of vertex iVertex having the requested properties iProperties
	 * 
	 * @param iVertex
	 *            Target vertex
	 * @param iProperties
	 *            Map where keys are property names and values the expected values
	 * @return
	 */
	public Set<OIdentifiable> getOutEdgesHavingProperties(OIdentifiable iVertex, Iterable<String> iProperties);

	public Set<OIdentifiable> getInEdges(OIdentifiable iVertex);

	public Set<OIdentifiable> getInEdges(OIdentifiable iVertex, String iLabel);

	/**
	 * Retrieves the incoming edges of vertex iVertex having the requested properties iProperties
	 * 
	 * @param iVertex
	 *            Target vertex
	 * @param iProperties
	 *            Map where keys are property names and values the expected values
	 * @return
	 */
	public Set<OIdentifiable> getInEdgesHavingProperties(OIdentifiable iVertex, Iterable<String> iProperties);

	/**
	 * Retrieves the incoming edges of vertex iVertex having the requested properties iProperties set to the passed values
	 * 
	 * @param iVertex
	 *            Target vertex
	 * @param iProperties
	 *            Map where keys are property names and values the expected values
	 * @return
	 */
	public Set<OIdentifiable> getInEdgesHavingProperties(ODocument iVertex, Map<String, Object> iProperties);

	public ODocument getInVertex(OIdentifiable iEdge);

	public ODocument getOutVertex(OIdentifiable iEdge);

	public ODocument getRoot(String iName);

	public ODocument getRoot(String iName, String iFetchPlan);

	public OGraphDatabase setRoot(String iName, ODocument iNode);

	public OClass createVertexType(String iClassName);

	public OClass createVertexType(String iClassName, String iSuperClassName);

	public OClass createVertexType(String iClassName, OClass iSuperClass);

	public OClass getVertexType(String iClassName);

	public OClass createEdgeType(String iClassName);

	public OClass createEdgeType(String iClassName, String iSuperClassName);

	public OClass createEdgeType(String iClassName, OClass iSuperClass);

	public OClass getEdgeType(String iClassName);

	public boolean isSafeMode();

	public void setSafeMode(boolean safeMode);

	public OClass getVertexBaseClass();

	public OClass getEdgeBaseClass();

	public Set<OIdentifiable> filterEdgesByProperties(OMVRBTreeRIDSet iEdges, Iterable<String> iPropertyNames);

	public Set<OIdentifiable> filterEdgesByProperties(OMVRBTreeRIDSet iEdges, Map<String, Object> iProperties);

	public boolean isUseCustomTypes();

	public void setUseCustomTypes(boolean useCustomTypes);

	/**
	 * Returns true if the document is a vertex (its class is OGraphVertex or any subclasses)
	 * 
	 * @param iRecord
	 *            Document to analyze.
	 * @return true if the document is a vertex (its class is OGraphVertex or any subclasses)
	 */
	public boolean isVertex(ODocument iRecord);

	/**
	 * Returns true if the document is an edge (its class is OGraphEdge or any subclasses)
	 * 
	 * @param iRecord
	 *            Document to analyze.
	 * @return true if the document is a edge (its class is OGraphEdge or any subclasses)
	 */
	public boolean isEdge(ODocument iRecord);

	public void freeze(boolean throwException);

	public void freeze();

	public void release();

	/**
	 * Creates a new ODocument.
	 */
	public ODocument newInstance();

	public ODocument newInstance(String iClassName);

	public ORecordIteratorClass<ODocument> browseClass(String iClassName);

	public ORecordIteratorClass<ODocument> browseClass(String iClassName, boolean iPolymorphic);

	public ORecordIteratorCluster<ODocument> browseCluster(String iClusterName);

	/**
	 * Saves a document to the database. Behavior depends by the current running transaction if any. If no transaction is running then changes apply immediately. If an Optimistic
	 * transaction is running then the record will be changed at commit time. The current transaction will continue to see the record as modified, while others not. If a
	 * Pessimistic transaction is running, then an exclusive lock is acquired against the record. Current transaction will continue to see the record as modified, while others
	 * cannot access to it since it's locked.
	 * <p/>
	 * If MVCC is enabled and the version of the document is different by the version stored in the database, then a {@link OConcurrentModificationException} exception is
	 * thrown.Before to save the document it must be valid following the constraints declared in the schema if any (can work also in schema-less mode). To validate the document the
	 * {@link ODocument#validate()} is called.
	 * 
	 * @param iRecord
	 *            Record to save.
	 * @return The Database instance itself giving a "fluent interface". Useful to call multiple methods in chain.
	 * @throws OConcurrentModificationException
	 *             if the version of the document is different by the version contained in the database.
	 * @throws OValidationException
	 *             if the document breaks some validation constraints defined in the schema
	 * @see #setMVCC(boolean), {@link #isMVCC()}
	 */
	public <RET extends ORecordInternal<?>> RET save(ORecordInternal<?> iRecord);

	/**
	 * Saves a document specifying a cluster where to store the record. Behavior depends by the current running transaction if any. If no transaction is running then changes apply
	 * immediately. If an Optimistic transaction is running then the record will be changed at commit time. The current transaction will continue to see the record as modified,
	 * while others not. If a Pessimistic transaction is running, then an exclusive lock is acquired against the record. Current transaction will continue to see the record as
	 * modified, while others cannot access to it since it's locked.
	 * <p/>
	 * If MVCC is enabled and the version of the document is different by the version stored in the database, then a {@link OConcurrentModificationException} exception is thrown.
	 * Before to save the document it must be valid following the constraints declared in the schema if any (can work also in schema-less mode). To validate the document the
	 * {@link ODocument#validate()} is called.
	 * 
	 * @param iRecord
	 *            Record to save
	 * @param iClusterName
	 *            Cluster name where to save the record
	 * @return The Database instance itself giving a "fluent interface". Useful to call multiple methods in chain.
	 * @throws OConcurrentModificationException
	 *             if the version of the document is different by the version contained in the database.
	 * @throws OValidationException
	 *             if the document breaks some validation constraints defined in the schema
	 * @see #setMVCC(boolean), {@link #isMVCC()}, ORecordSchemaAware#validate()
	 */
	public <RET extends ORecordInternal<?>> RET save(ORecordInternal<?> iRecord, String iClusterName);

	/**
	 * Deletes a document. Behavior depends by the current running transaction if any. If no transaction is running then the record is deleted immediately. If an Optimistic
	 * transaction is running then the record will be deleted at commit time. The current transaction will continue to see the record as deleted, while others not. If a Pessimistic
	 * transaction is running, then an exclusive lock is acquired against the record. Current transaction will continue to see the record as deleted, while others cannot access to
	 * it since it's locked.
	 * <p/>
	 * If MVCC is enabled and the version of the document is different by the version stored in the database, then a {@link OConcurrentModificationException} exception is thrown.
	 * 
	 * @param iRecord
	 * @return The Database instance itself giving a "fluent interface". Useful to call multiple methods in chain.
	 * @see #setMVCC(boolean), {@link #isMVCC()}
	 */
	public ODatabaseDocumentTx delete(ODocument iRecord);

	/**
	 * Returns the number of the records of the class iClassName.
	 */
	public long countClass(String iClassName);

	public ODatabaseComplex<ORecordInternal<?>> commit();

	public ODatabaseComplex<ORecordInternal<?>> rollback();

	public String getType();

	public OrientDBManagedConnectionFactory getMcf();

	public OrientDBManagedConnection getMc();

	public void setMc(OrientDBManagedConnection orientDBManagedConnection);

	public <RET extends List<?>> RET query(final OQuery<? extends Object> iCommand, final Object... iArgs);

	public <RET extends OCommandRequest> RET command(OSQLSynchQuery<ODocument> osqlSynchQuery);

	public ODocument getNodeByField(String vertexClass, String field, Object value) throws ResourceException;

	public List<ODocument> getAllNodes(String vertexClass);

	public <T> T getNodeById(Class<T> clazz, Long id) throws ResourceException;

	/**
	 * Save the NEW object as json-serialized node in the graph
	 * 
	 * @param obj
	 *            - java bean object
	 * @return - saved node
	 * @throws ResourceException
	 */
	public abstract ODocument saveNode(Object obj) throws ResourceException;

	public abstract <T> T getNodeByField(Class<T> clazz, String field, Object value) throws ResourceException;

	public List<ODocument> getNodesByFields(Object obj, String... constraints) throws ResourceException;

	/**
	 * Save New object or update the node if it is already exist in the graph.
	 * 
	 * @param obj
	 *            - java bean object to persist
	 * @param constraints
	 *            - array with the fields which MUST be unique with AND operator. <br/>
	 *            E.g: <code> saveOrUpdateNode(user,"id","username");// where (id AND name) are unique fields</code>
	 * @throws ResourceException
	 */
	public void saveOrUpdateNode(Object obj, String... constraints) throws ResourceException;
}
