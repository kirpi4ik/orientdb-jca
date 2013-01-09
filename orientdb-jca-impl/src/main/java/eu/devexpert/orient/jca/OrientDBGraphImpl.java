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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import javax.resource.ResourceException;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.orientechnologies.orient.core.cache.OLevel1RecordCache;
import com.orientechnologies.orient.core.cache.OLevel2RecordCache;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseComplex;
import com.orientechnologies.orient.core.db.ODatabaseListener;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.intent.OIntent;
import com.orientechnologies.orient.core.iterator.ORecordIteratorClass;
import com.orientechnologies.orient.core.iterator.ORecordIteratorCluster;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.query.OQuery;
import com.orientechnologies.orient.core.record.ORecordInternal;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.storage.OStorage;
import com.orientechnologies.orient.core.storage.OStorage.CLUSTER_TYPE;
import com.orientechnologies.orient.core.type.tree.OMVRBTreeRIDSet;

import eu.devexpert.orient.jca.api.OrientDBGraph;
import eu.devexpert.orient.jca.api.OrientDBManagedConnection;
import eu.devexpert.orient.jca.api.OrientDBManagedConnectionFactory;

/**
 * 
 * @author Dumitru Ciubenco
 * @since 0.0.1
 * @created August 05, 2012
 */
public class OrientDBGraphImpl implements OrientDBGraph {
	/** The logger */
	private static Logger						log	= Logger.getLogger(OrientDBGraphImpl.class.getName());
	/** ManagedConnection */
	private OrientDBManagedConnection			mc;
	/** ManagedConnectionFactory */
	private OrientDBManagedConnectionFactory	mcf;
	private OGraphDatabase						graphDatabase;
	private static ObjectMapper					mapper;
	static {
		mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, Visibility.PUBLIC_ONLY);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.EAGER_DESERIALIZER_FETCH, true);
		mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
		mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
		mapper.configure(SerializationFeature.EAGER_SERIALIZER_FETCH, true);
		// ISO8601DateFormat
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS"));
	}

	/**
	 * 
	 * Default constructor
	 * 
	 * @param mc
	 *            OrientDBManagedConnection
	 * 
	 * @param mcf
	 *            OrientDBManagedConnectionFactory
	 * @param database
	 */
	public OrientDBGraphImpl(final OrientDBManagedConnection mc, final OrientDBManagedConnectionFactory mcf, final OGraphDatabase database) {
		this.graphDatabase = database;
		ODatabaseRecordThreadLocal.INSTANCE.set(this.graphDatabase);
		this.mc = mc;
		this.mcf = mcf;
		log.info("New connection to the : " + database.getName());
	}

	/**
	 * Close
	 */
	public void close() {
		graphDatabase.close();
		mc.closeHandle(this);
		log.info("Closed connection to the : " + graphDatabase.getName());
	}

	public void reload() {
		log.info("Reload connection to the : " + graphDatabase.getName());
		graphDatabase.reload();

	}

	public void drop() {
		log.info("Drop connection to the : " + graphDatabase.getName());
		graphDatabase.drop();

	}

	@Deprecated
	public void delete() {
		// TODO Auto-generated method stub

	}

	public boolean declareIntent(OIntent iIntent) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean exists() {
		return graphDatabase.exists();
	}

	public STATUS getStatus() {
		return graphDatabase.getStatus();
	}

	public long getSize() {
		return graphDatabase.getSize();
	}

	public <DB extends ODatabase> DB setStatus(STATUS iStatus) {
		return graphDatabase.setStatus(iStatus);
	}

	public String getName() {
		return graphDatabase.getName();
	}

	public String getURL() {
		return graphDatabase.getURL();
	}

	public OStorage getStorage() {
		return graphDatabase.getStorage();
	}

	public void replaceStorage(OStorage iNewStorage) {
		graphDatabase.replaceStorage(iNewStorage);

	}

	public OLevel1RecordCache getLevel1Cache() {
		return graphDatabase.getLevel1Cache();
	}

	public OLevel2RecordCache getLevel2Cache() {
		return graphDatabase.getLevel2Cache();
	}

	public int getDataSegmentIdByName(String iDataSegmentName) {
		return graphDatabase.getDataSegmentIdByName(iDataSegmentName);
	}

	public String getDataSegmentNameById(int dataSegmentId) {
		return graphDatabase.getDataSegmentNameById(dataSegmentId);
	}

	public int getDefaultClusterId() {
		return graphDatabase.getDefaultClusterId();
	}

	public int getClusters() {
		return graphDatabase.getClusters();
	}

	public boolean existsCluster(String iClusterName) {
		return graphDatabase.existsCluster(iClusterName);
	}

	public Collection<String> getClusterNames() {
		return graphDatabase.getClusterNames();
	}

	public int getClusterIdByName(String iClusterName) {
		return graphDatabase.getClusterIdByName(iClusterName);
	}

	public String getClusterType(String iClusterName) {
		return graphDatabase.getClusterType(iClusterName);
	}

	public String getClusterNameById(int iClusterId) {
		return graphDatabase.getClusterNameById(iClusterId);
	}

	public long getClusterRecordSizeByName(String iClusterName) {
		return graphDatabase.getClusterRecordSizeByName(iClusterName);
	}

	public long getClusterRecordSizeById(int iClusterId) {
		return graphDatabase.getClusterRecordSizeById(iClusterId);
	}

	public boolean isClosed() {
		return graphDatabase.isClosed();
	}

	public long countClusterElements(int iCurrentClusterId) {
		return graphDatabase.countClusterElements(iCurrentClusterId);
	}

	public long countClusterElements(int[] iClusterIds) {
		return graphDatabase.countClusterElements(iClusterIds);
	}

	public long countClusterElements(String iClusterName) {
		return graphDatabase.countClusterElements(iClusterName);
	}

	public int addCluster(String iClusterName, CLUSTER_TYPE iType, Object... iParameters) {
		return graphDatabase.addCluster(iClusterName, iType, iParameters);
	}

	public int addCluster(String iType, String iClusterName, String iLocation, String iDataSegmentName, Object... iParameters) {
		return graphDatabase.addCluster(iType, iClusterName, iLocation, iDataSegmentName, iParameters);
	}

	@Deprecated
	public int addPhysicalCluster(String iClusterName, String iLocation, int iStartSize) {
		return graphDatabase.addPhysicalCluster(iClusterName, iLocation, iStartSize);
	}

	public boolean dropCluster(String iClusterName) {
		return graphDatabase.dropCluster(iClusterName);
	}

	public boolean dropCluster(int iClusterId) {
		return graphDatabase.dropCluster(iClusterId);
	}

	public int addDataSegment(String iSegmentName, String iLocation) {
		return graphDatabase.addDataSegment(iSegmentName, iLocation);
	}

	public boolean dropDataSegment(String name) {
		return graphDatabase.dropDataSegment(name);
	}

	public Object setProperty(String iName, Object iValue) {
		return graphDatabase.setProperty(iName, iValue);
	}

	public Object getProperty(String iName) {
		return graphDatabase.getProperty(iName);
	}

	public Iterator<Entry<String, Object>> getProperties() {
		return graphDatabase.getProperties();
	}

	public Object get(ATTRIBUTES iAttribute) {
		return graphDatabase.get(iAttribute);
	}

	public <DB extends ODatabase> DB set(ATTRIBUTES iAttribute, Object iValue) {
		return graphDatabase.set(iAttribute, iValue);
	}

	public void registerListener(ODatabaseListener iListener) {
		graphDatabase.registerListener(iListener);
	}

	public void unregisterListener(ODatabaseListener iListener) {
		graphDatabase.unregisterListener(iListener);

	}

	public <V> V callInLock(Callable<V> iCallable, boolean iExclusiveLock) {
		return graphDatabase.callInLock(iCallable, iExclusiveLock);
	}

	public <THISDB extends ODatabase> THISDB open(String iUserName, String iUserPassword) {
		return graphDatabase.open(iUserName, iUserPassword);
	}

	public <THISDB extends ODatabase> THISDB create() {
		return graphDatabase.create();
	}

	public long countVertexes() {
		return graphDatabase.countVertexes();
	}

	public long countEdges() {
		return graphDatabase.countEdges();
	}

	public ODocument createVertex() {
		return graphDatabase.createVertex();
	}

	public ODocument createVertex(String iClassName) {
		return graphDatabase.createVertex(iClassName);
	}

	public ODocument createVertex(String iClassName, Object... iFields) {
		return graphDatabase.createVertex(iClassName, iFields);
	}

	public ODocument createEdge(ORID iSourceVertexRid, ORID iDestVertexRid) {
		return graphDatabase.createEdge(iSourceVertexRid, iDestVertexRid);
	}

	public ODocument createEdge(ORID iSourceVertexRid, ORID iDestVertexRid, String iClassName) {
		return graphDatabase.createEdge(iSourceVertexRid, iDestVertexRid, iClassName);
	}

	public void removeVertex(ODocument iVertex) {
		graphDatabase.removeVertex(iVertex);
	}

	public void removeEdge(ODocument iEdge) {
		graphDatabase.removeEdge(iEdge);

	}

	public ODocument createEdge(ODocument iSourceVertex, ODocument iDestVertex) {
		return graphDatabase.createEdge(iSourceVertex, iDestVertex);
	}

	public ODocument createEdge(ODocument iOutVertex, ODocument iInVertex, String iClassName) {
		return graphDatabase.createEdge(iOutVertex, iInVertex, iClassName);
	}

	public ODocument createEdge(ODocument iOutVertex, ODocument iInVertex, String iClassName, Object... iFields) {
		return graphDatabase.createEdge(iOutVertex, iInVertex, iClassName, iFields);
	}

	public Set<OIdentifiable> getEdgesBetweenVertexes(ODocument iVertex1, ODocument iVertex2) {
		return graphDatabase.getEdgesBetweenVertexes(iVertex1, iVertex2);
	}

	public Set<OIdentifiable> getEdgesBetweenVertexes(ODocument iVertex1, ODocument iVertex2, String[] iLabels) {
		return graphDatabase.getEdgesBetweenVertexes(iVertex1, iVertex2, iLabels);
	}

	public Set<OIdentifiable> getEdgesBetweenVertexes(ODocument iVertex1, ODocument iVertex2, String[] iLabels, String[] iClassNames) {
		return graphDatabase.getEdgesBetweenVertexes(iVertex1, iVertex2, iLabels, iClassNames);
	}

	public Set<OIdentifiable> getOutEdges(OIdentifiable iVertex) {
		return graphDatabase.getOutEdges(iVertex);
	}

	public Set<OIdentifiable> getOutEdges(OIdentifiable iVertex, String iLabel) {
		return graphDatabase.getOutEdges(iVertex, iLabel);
	}

	public Set<OIdentifiable> getOutEdgesHavingProperties(OIdentifiable iVertex, Map<String, Object> iProperties) {
		return graphDatabase.getOutEdgesHavingProperties(iVertex, iProperties);
	}

	public Set<OIdentifiable> getOutEdgesHavingProperties(OIdentifiable iVertex, Iterable<String> iProperties) {
		return graphDatabase.getOutEdgesHavingProperties(iVertex, iProperties);
	}

	public Set<OIdentifiable> getInEdges(OIdentifiable iVertex) {
		return graphDatabase.getInEdges(iVertex);
	}

	public Set<OIdentifiable> getInEdges(OIdentifiable iVertex, String iLabel) {
		return graphDatabase.getInEdges(iVertex, iLabel);
	}

	public Set<OIdentifiable> getInEdgesHavingProperties(OIdentifiable iVertex, Iterable<String> iProperties) {
		return graphDatabase.getInEdgesHavingProperties(iVertex, iProperties);
	}

	public Set<OIdentifiable> getInEdgesHavingProperties(ODocument iVertex, Map<String, Object> iProperties) {
		return graphDatabase.getInEdgesHavingProperties(iVertex, iProperties);
	}

	public ODocument getInVertex(OIdentifiable iEdge) {
		return graphDatabase.getInVertex(iEdge);
	}

	public ODocument getOutVertex(OIdentifiable iEdge) {
		return graphDatabase.getOutVertex(iEdge);
	}

	public ODocument getRoot(String iName) {
		return graphDatabase.getRoot(iName);
	}

	public ODocument getRoot(String iName, String iFetchPlan) {
		return graphDatabase.getRoot(iName, iFetchPlan);
	}

	public OGraphDatabase setRoot(String iName, ODocument iNode) {
		return graphDatabase.setRoot(iName, iNode);
	}

	public OClass createVertexType(String iClassName) {
		return graphDatabase.createVertexType(iClassName);
	}

	public OClass createVertexType(String iClassName, String iSuperClassName) {
		return graphDatabase.createVertexType(iClassName, iSuperClassName);
	}

	public OClass createVertexType(String iClassName, OClass iSuperClass) {
		return graphDatabase.createVertexType(iClassName, iSuperClass);
	}

	public OClass getVertexType(String iClassName) {
		return graphDatabase.getVertexType(iClassName);
	}

	public OClass createEdgeType(String iClassName) {
		return graphDatabase.createEdgeType(iClassName);
	}

	public OClass createEdgeType(String iClassName, String iSuperClassName) {
		return graphDatabase.createEdgeType(iClassName, iSuperClassName);
	}

	public OClass createEdgeType(String iClassName, OClass iSuperClass) {
		return graphDatabase.createEdgeType(iClassName, iSuperClass);
	}

	public OClass getEdgeType(String iClassName) {
		return graphDatabase.getEdgeType(iClassName);
	}

	public boolean isSafeMode() {
		return graphDatabase.isSafeMode();
	}

	public void setSafeMode(boolean safeMode) {
		graphDatabase.setSafeMode(safeMode);
	}

	public OClass getVertexBaseClass() {
		return graphDatabase.getVertexBaseClass();
	}

	public OClass getEdgeBaseClass() {
		return graphDatabase.getEdgeBaseClass();
	}

	public Set<OIdentifiable> filterEdgesByProperties(OMVRBTreeRIDSet iEdges, Iterable<String> iPropertyNames) {
		return graphDatabase.filterEdgesByProperties(iEdges, iPropertyNames);
	}

	public Set<OIdentifiable> filterEdgesByProperties(OMVRBTreeRIDSet iEdges, Map<String, Object> iProperties) {
		return graphDatabase.filterEdgesByProperties(iEdges, iProperties);
	}

	public boolean isUseCustomTypes() {
		return graphDatabase.isUseCustomTypes();
	}

	public void setUseCustomTypes(boolean useCustomTypes) {
		graphDatabase.setUseCustomTypes(useCustomTypes);
	}

	public boolean isVertex(ODocument iRecord) {
		return graphDatabase.isVertex(iRecord);
	}

	public boolean isEdge(ODocument iRecord) {
		return graphDatabase.isEdge(iRecord);
	}

	public void freeze(boolean throwException) {
		graphDatabase.freeze(throwException);
	}

	public void freeze() {
		graphDatabase.freeze();
	}

	public void release() {
		graphDatabase.release();

	}

	public ODocument newInstance() {
		return graphDatabase.newInstance();
	}

	public ODocument newInstance(String iClassName) {
		return graphDatabase.newInstance(iClassName);
	}

	public ORecordIteratorClass<ODocument> browseClass(String iClassName) {
		return graphDatabase.browseClass(iClassName);
	}

	public ORecordIteratorClass<ODocument> browseClass(String iClassName, boolean iPolymorphic) {
		return graphDatabase.browseClass(iClassName, iPolymorphic);
	}

	public ORecordIteratorCluster<ODocument> browseCluster(String iClusterName) {
		return graphDatabase.browseCluster(iClusterName);
	}

	public <RET extends ORecordInternal<?>> RET save(ORecordInternal<?> iRecord) {
		return graphDatabase.save(iRecord);
	}

	public <RET extends ORecordInternal<?>> RET save(ORecordInternal<?> iRecord, String iClusterName) {
		return graphDatabase.save(iRecord, iClusterName);
	}

	public ODatabaseDocumentTx delete(ODocument iRecord) {
		return graphDatabase.delete(iRecord);
	}

	public long countClass(String iClassName) {
		return graphDatabase.countClass(iClassName);
	}

	public ODatabaseComplex<ORecordInternal<?>> commit() {
		log.info("Commit chamges");
		return graphDatabase.commit();
	}

	public ODatabaseComplex<ORecordInternal<?>> rollback() {
		log.info("Rollback chamges");
		return graphDatabase.rollback();
	}

	public String getType() {
		return graphDatabase.getType();
	}

	public OrientDBManagedConnection getMc() {
		return mc;
	}

	public void setMc(OrientDBManagedConnection mc) {
		this.mc = mc;
	}

	public OrientDBManagedConnectionFactory getMcf() {
		return mcf;
	}

	public <RET extends List<?>> RET query(final OQuery<? extends Object> iCommand, final Object... iArgs) {
		return graphDatabase.query(iCommand, iArgs);
	}

	public <RET extends OCommandRequest> RET command(OSQLSynchQuery<ODocument> osqlSynchQuery) {
		return graphDatabase.command(osqlSynchQuery);
	}

	@SuppressWarnings("unchecked")
	public ODocument getNodeByField(String vertexClass, String field, Object value) throws ResourceException {
		List<ODocument> result = (List<ODocument>) graphDatabase.command(new OSQLSynchQuery<ODocument>("select * from " + vertexClass + " where " + field + " = ?")).execute(value);
		if(result.size() > 0) {
			return result.get(0);
		}else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<ODocument> getAllNodes(String vertexClass) {
		return (List<ODocument>) graphDatabase.command(new OSQLSynchQuery<ODocument>("select * from " + vertexClass)).execute();
	}

	public ODocument saveNode(Object obj) throws ResourceException {
		try {
			ODocument node = createVertex(obj.getClass().getName());
			String jsonStr = mapper.writeValueAsString(obj);
			log.info("Save POJO : " + jsonStr);
			return node.fromJSON(jsonStr).save();
		}
		catch(IOException iox) {
			throw new ResourceException();
		}
	}

	public <T> T getNodeById(Class<T> clazz, Long id) throws ResourceException {
		try {
			String jsonStr = getNodeByField(clazz.getName(), "id", id).toJSON();
			log.info("Get POJO by id : " + jsonStr);
			return mapper.readValue(jsonStr, clazz);
		}
		catch(IOException e) {
			throw new ResourceException();
		}
	}

	@Override
	public <T> T getNodeByField(Class<T> clazz, String field, Object value) throws ResourceException {
		try {
			String jsonStr = getNodeByField(clazz.getName(), field, value).toJSON();
			log.info("Get POJO by field : " + jsonStr);
			return mapper.readValue(jsonStr, clazz);
		}
		catch(IOException e) {
			throw new ResourceException();
		}
	}

	public List<ODocument> getNodesByFields(Object obj, String... constraints) throws ResourceException {
		try {
			Object[] values = new Object[constraints.length];
			StringBuffer query = new StringBuffer("select * from ");
			query.append(obj.getClass().getName());
			query.append(" where ");
			int counter = 0;
			for(String attributeName : constraints) {
				values[counter] = BeanUtils.getObjectAttribute(obj, attributeName);
				query.append(attributeName);
				query.append(" = ? ");
				if(++counter < constraints.length) {
					query.append("and ");
				}
			}

			return (List<ODocument>) graphDatabase.command(new OSQLSynchQuery<ODocument>(query.toString())).execute(values);
		}
		catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException ie) {
			throw new ResourceException(ie.getMessage());
		}
	}

	@Override
	public void saveOrUpdateNode(Object obj, String... constraints) throws ResourceException {
		try {
			List<ODocument> existingNodes = getNodesByFields(obj, constraints);
			if(existingNodes.size() == 0) {
				saveNode(obj);
			}else if(existingNodes.size() == 1) {
				ODocument node = existingNodes.get(0);
				String jsonStr = mapper.writeValueAsString(obj);
				node.fromJSON(jsonStr).save();
			}else {
				throw new ResourceException("You have to pass unique fields, otherwise it is not possible to update the node, now we have found more nodes with your criteria : "
						+ existingNodes.size());
			}
		}
		catch(IOException iox) {
			throw new ResourceException(iox.getMessage());
		}
	}
}
