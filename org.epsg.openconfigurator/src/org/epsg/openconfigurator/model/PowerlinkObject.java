/*******************************************************************************
 * @file   PowerlinkObject.java
 *
 * @author Ramakrishnan Periyakaruppan, Kalycito Infotech Private Limited.
 *
 * @copyright (c) 2015, Kalycito Infotech Private Limited
 *                    All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   * Neither the name of the copyright holders nor the
 *     names of its contributors may be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

package org.epsg.openconfigurator.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.epsg.openconfigurator.util.OpenConfiguratorProjectUtils;
import org.epsg.openconfigurator.xmlbinding.xdd.TObject;
import org.epsg.openconfigurator.xmlbinding.xdd.TObjectAccessType;
import org.epsg.openconfigurator.xmlbinding.xdd.TObjectExtensionHead;
import org.epsg.openconfigurator.xmlbinding.xdd.TObjectPDOMapping;
import org.jdom2.JDOMException;

/**
 * Wrapper class for a POWERLINK object.
 *
 * @author Ramakrishnan P
 *
 */
public class PowerlinkObject extends AbstractPowerlinkObject
        implements IPowerlinkObject {

    /**
     * Associated Eclipse project.
     */
    private final IProject project;

    /**
     * List of sub-objects available in the node.
     */
    private final List<PowerlinkSubobject> subObjectsList = new ArrayList<PowerlinkSubobject>();

    /**
     * TPDO mappable subobjects list.
     */
    private final List<PowerlinkSubobject> tpdoMappableObjectList = new ArrayList<PowerlinkSubobject>();

    /**
     * RPDO mappable subobjects list.
     */
    private final List<PowerlinkSubobject> rpdoMappableObjectList = new ArrayList<PowerlinkSubobject>();

    /**
     * Object ID in hex without 0x.
     */
    private final String objectIdRaw;

    /**
     * Index of Object
     */
    private final byte[] idByte;

    /**
     * Object ID.
     */
    private final long objectIdL;

    /**
     * Object ID in hex with 0x.
     */
    private final String objectId;

    /**
     * XPath to find this object in the XDC.
     */
    private final String xpath;

    /**
     * Datatype in the human readable format.
     */
    private final String dataTypeReadable;

    /**
     * Flag to indicate that this object is TPDO mappable or not.
     */
    private boolean isTpdoMappable = false;

    /**
     * Flag to indicate that this object is RPDO mappable or not.
     */
    private boolean isRpdoMappable = false;

    /**
     * Error message string to identify configuration error of POWERLINK object
     */
    private String configurationError;

    /**
     * POWERLINK object capable of mapping with PDO channels
     */
    private TObjectPDOMapping pdoMapping;

    /**
     * Access type of POWERLINK object from XDC model.
     */
    private TObjectAccessType accessType;

    /**
     * Actual value of POWERLINK object from the TObject model.
     */
    private String actualValue;

    /**
     * Default value of POWERLINK object from the TObject model
     */
    private final String defaultValue;

    /**
     * Name of the SubObject.
     */
    private final String name;

    /**
     * Unique ID value of POWERLINK object.
     */
    private final Object uniqueIDRef;

    /**
     * Higher value limit of POWERLINK object given in the XDD/XDC file.
     */
    private final String highLimit;

    /**
     * Lower value of limit of POWERLINK object given in the XDD/XDC file.
     */
    private final String lowLimit;

    /**
     * Type of POWERLINK object given in the XDD/XDC file.
     */
    private final short objectType;

    /**
     * The data type of POWERLINK object given in the XDD/XDC file.
     */
    private final byte[] dataType;

    /**
     * The denotation variable of POWERLINK object.
     */
    private final String denotation;

    /**
     * The flag object for POWERLINK object.
     */
    private final byte[] objFlags;

    /**
     * Constructs a POWERLINK object based on TObject model.
     *
     * @param nodeInstance Node linked with the object.
     * @param object The Object model available in the XDC.
     */
    public PowerlinkObject(Node nodeInstance, TObject object) {
        super(nodeInstance);

        if ((nodeInstance == null) || (object == null)) {
            throw new IllegalArgumentException();
        }

        project = nodeInstance.getProject();

        idByte = object.getIndex();
        objectIdRaw = DatatypeConverter.printHexBinary(idByte);
        objectIdL = Long.parseLong(objectIdRaw, 16);
        objectId = "0x" + objectIdRaw;
        xpath = "//plk:Object[@index='" + objectIdRaw + "']";

        name = object.getName();

        denotation = object.getDenotation();
        objFlags = object.getObjFlags();

        objectType = object.getObjectType();
        dataType = object.getDataType();
        if (dataType != null) {
            dataTypeReadable = ObjectDatatype.getDatatypeName(
                    DatatypeConverter.printHexBinary(dataType));
        } else {
            dataTypeReadable = StringUtils.EMPTY;
        }

        // Calculate the subobjects available in this object.
        for (TObject.SubObject subObject : object.getSubObject()) {
            PowerlinkSubobject obj = new PowerlinkSubobject(nodeInstance, this,
                    subObject);
            subObjectsList.add(obj);

            if (obj.isRpdoMappable()) {
                rpdoMappableObjectList.add(obj);
            } else if (obj.isTpdoMappable()) {
                tpdoMappableObjectList.add(obj);
            }
        }

        highLimit = object.getHighLimit();
        lowLimit = object.getLowLimit();

        actualValue = object.getActualValue();
        defaultValue = object.getDefaultValue();

        pdoMapping = object.getPDOmapping();
        accessType = object.getAccessType();
        if (((pdoMapping == TObjectPDOMapping.DEFAULT)
                || (pdoMapping == TObjectPDOMapping.OPTIONAL)
                || (pdoMapping == TObjectPDOMapping.RPDO))) {

            if (object.getUniqueIDRef() != null) {
                isRpdoMappable = true;
            } else {
                if ((accessType == TObjectAccessType.RW)
                        || (accessType == TObjectAccessType.WO)) {
                    isRpdoMappable = true;
                }
            }
        } else if (((pdoMapping == TObjectPDOMapping.DEFAULT)
                || (pdoMapping == TObjectPDOMapping.OPTIONAL)
                || (pdoMapping == TObjectPDOMapping.TPDO))) {

            if (object.getUniqueIDRef() != null) {
                isTpdoMappable = true;
            } else {

                if ((accessType == TObjectAccessType.RO)
                        || (accessType == TObjectAccessType.RW)) {
                    isTpdoMappable = true;
                }
            }
        }

        uniqueIDRef = object.getUniqueIDRef();
    }

    /**
     * Constructs a POWERLINK object based on TObjectExtensionHead model.
     *
     * @param nodeInstance Node linked with the object.
     * @param object The Object model available in the XDC.
     */
    public PowerlinkObject(Node nodeInstance, TObjectExtensionHead object) {
        super(nodeInstance);

        if ((nodeInstance == null) || (object == null)) {
            throw new IllegalArgumentException();
        }

        project = nodeInstance.getProject();

        idByte = object.getIndex();
        objectIdRaw = DatatypeConverter.printHexBinary(idByte);
        objectIdL = Long.parseLong(objectIdRaw, 16);
        objectId = "0x" + objectIdRaw;
        xpath = "//plk:Object[@index='" + objectIdRaw + "']";

        name = object.getName();

        denotation = object.getDenotation();
        objFlags = object.getObjFlags();

        objectType = object.getObjectType();
        dataType = object.getDataType();
        if (dataType != null) {
            dataTypeReadable = ObjectDatatype.getDatatypeName(
                    DatatypeConverter.printHexBinary(dataType));
        } else {
            dataTypeReadable = StringUtils.EMPTY;
        }

        // Calculate the subobjects available in this object.
        for (TObjectExtensionHead.SubObject subObject : object.getSubObject()) {
            PowerlinkSubobject obj = new PowerlinkSubobject(nodeInstance, this,
                    subObject);
            subObjectsList.add(obj);

            if (obj.isRpdoMappable()) {
                rpdoMappableObjectList.add(obj);
            } else if (obj.isTpdoMappable()) {
                tpdoMappableObjectList.add(obj);
            }
        }

        highLimit = object.getHighLimit();
        lowLimit = object.getLowLimit();

        actualValue = object.getActualValue();
        defaultValue = object.getDefaultValue();

        pdoMapping = object.getPDOmapping();
        accessType = object.getAccessType();
        if (((pdoMapping == TObjectPDOMapping.DEFAULT)
                || (pdoMapping == TObjectPDOMapping.OPTIONAL)
                || (pdoMapping == TObjectPDOMapping.RPDO))) {

            if (object.getUniqueIDRef() != null) {
                isRpdoMappable = true;
            } else {
                if ((accessType == TObjectAccessType.RW)
                        || (accessType == TObjectAccessType.WO)) {
                    isRpdoMappable = true;
                }
            }
        } else if (((pdoMapping == TObjectPDOMapping.DEFAULT)
                || (pdoMapping == TObjectPDOMapping.OPTIONAL)
                || (pdoMapping == TObjectPDOMapping.TPDO))) {

            if (object.getUniqueIDRef() != null) {
                isTpdoMappable = true;
            } else {

                if ((accessType == TObjectAccessType.RO)
                        || (accessType == TObjectAccessType.RW)) {
                    isTpdoMappable = true;
                }
            }
        }

        uniqueIDRef = object.getUniqueIDRef();
    }

    /**
     * Note: This does not delete from the XML file.
     */
    public void deleteActualValue() {
        actualValue = null;
    }

    /**
     * Add the force configurations to the project.
     *
     * @param force True to add and false to remove.
     * @param writeToProjectFile True to write the changes to the project file.
     * @throws IOException Errors with XDC file modifications.
     * @throws JDOMException Errors with time modifications.
     */
    public synchronized void forceActualValue(boolean force,
            boolean writeToProjectFile) throws JDOMException, IOException {

        if (writeToProjectFile) {
            OpenConfiguratorProjectUtils.forceActualValue(getNode(), this, null,
                    force);
        }

        org.epsg.openconfigurator.xmlbinding.projectfile.Object forcedObj = new org.epsg.openconfigurator.xmlbinding.projectfile.Object();
        forcedObj.setIndex(getIndex());
        nodeInstance.forceObjectActualValue(forcedObj, force);
    }

    /**
     * Access type of POWERLINK object from XDC model.
     */
    @Override
    public TObjectAccessType getAccessType() {
        return accessType;
    }

    /**
     * @return Actual or Default value of the object
     */
    @Override
    public String getActualDefaultValue() {
        if ((actualValue != null) && !actualValue.isEmpty()) {
            return actualValue;
        }

        if (defaultValue != null) {
            return defaultValue;
        }

        return StringUtils.EMPTY;
    }

    /**
     * @return Actual value of the object
     */
    @Override
    public String getActualValue() {
        String value = actualValue;
        if (value == null) {
            value = StringUtils.EMPTY;
        }
        return value;
    }

    /**
     * Returns error message string to identify configuration error of POWERLINK
     * object
     */
    @Override
    public String getConfigurationError() {
        String cfgError = configurationError;
        if (cfgError == null) {
            cfgError = StringUtils.EMPTY;
        }
        return cfgError;
    }

    /**
     * @return Data type of POWERLINK object given in the XDD/XDC file.
     */
    public byte[] getDataType() {
        return dataType;
    }

    /**
     * @return Data type of the object
     */
    @Override
    public String getDataTypeReadable() {
        return dataTypeReadable;
    }

    /**
     * @return Default value of the object
     */
    @Override
    public String getDefaultValue() {
        String value = defaultValue;
        if (value == null) {
            value = StringUtils.EMPTY;
        }
        return value;
    }

    /**
     * @return Denotation variable of POWERLINK object given in the XDD/XDC
     *         file.
     */
    public String getDenotation() {
        return denotation;
    }

    /**
     * @return High limit value of object.
     */
    @Override
    public String getHighLimit() {
        String value = highLimit;
        if (value == null) {
            value = StringUtils.EMPTY;
        }
        return value;
    }

    /**
     * Returns the ID of POWERLINK object.
     */
    @Override
    public long getId() {
        return objectIdL;
    }

    /**
     * Returns POWERLINK object ID in hexa decimal format
     */
    @Override
    public String getIdHex() {
        return objectId;
    }

    /**
     * Returns POWERLINK object ID without 0x.
     */
    @Override
    public String getIdRaw() {
        return objectIdRaw;
    }

    /**
     * @return Index of POWERLINK object in bytes.
     */
    public byte[] getIndex() {
        return idByte;
    }

    /**
     * @return Low limit value of object
     */
    @Override
    public String getLowLimit() {
        String value = lowLimit;
        if (value == null) {
            value = StringUtils.EMPTY;
        }
        return value;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.epsg.openconfigurator.model.IPowerlinkBaseObject#getModel()
     */
    @Override
    public Object getModel() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @return Name of the object from the object model.
     */
    @Override
    public String getName() {
        String objectName = name;
        if (objectName == null) {
            objectName = StringUtils.EMPTY;
        }
        return objectName;
    }

    /**
     * @return Name with its ID of object.
     */
    @Override
    public String getNameWithId() {
        return (getName() + " (" + objectId + ")");
    }

    /**
     * @return name of the project.
     */
    @Override
    public String getNetworkId() {
        return project.getName();
    }

    /**
     * @return Instance of node from the node model.
     */
    @Override
    public Node getNode() {
        return nodeInstance;
    }

    /**
     * @return Id of the node from the node model.
     */
    @Override
    public short getNodeId() {
        return nodeInstance.getNodeId();
    }

    /**
     * @return the node model from the node.
     */
    public Object getNodeModel() {
        return nodeInstance.getNodeModel();
    }

    /**
     * @return DataType of the given object.
     */
    @Override
    public short getObjectType() {
        return objectType;
    }

    /**
     * @return Flag variable of POWERLINK object from the given XDD/XDC file.
     */
    public byte[] getObjFlags() {
        return objFlags;
    }

    /**
     * Returns Mapping object from the XDC model
     */
    @Override
    public TObjectPDOMapping getPdoMapping() {
        return pdoMapping;
    }

    /**
     * @return Instance of project.
     */
    @Override
    public IProject getProject() {
        return project;
    }

    /**
     * @return List of RPDO mappable objects from the available node.
     */
    public List<PowerlinkSubobject> getRpdoMappableObjectList() {
        return rpdoMappableObjectList;
    }

    /**
     * Get the sub object from the given sub object ID.
     *
     * @param subObjectId The sub-object id.
     * @return The POWERLINK sub-object based on the given sub-object ID.
     */
    public PowerlinkSubobject getSubObject(final byte[] subObjectId) {
        if (subObjectId == null) {
            return null;
        }

        String subobjectIdRaw = DatatypeConverter.printHexBinary(subObjectId);
        short subobjectIdShort = 0;
        try {
            subobjectIdShort = Short.parseShort(subobjectIdRaw, 16);
        } catch (NumberFormatException ex) {
            return null;
        }

        return getSubObject(subobjectIdShort);
    }

    /**
     * The subObject instance for the given ID, null if the sub object ID is not
     * found.
     *
     * @param subObjectId SubObject ID ranges from 0x00 to 0xFE
     * @return The subObject instance.
     */
    public PowerlinkSubobject getSubObject(short subObjectId) {
        for (PowerlinkSubobject subObj : getSubObjects()) {
            if (subObj.getId() == subObjectId) {
                return subObj;
            }
        }
        return null;
    }

    /**
     * @return The list of sub objects from the given node.
     */
    public List<PowerlinkSubobject> getSubObjects() {
        return subObjectsList;
    }

    /**
     * @return List of TPDO mappable objects from the given node.
     */
    public List<PowerlinkSubobject> getTpdoMappableObjectList() {
        return tpdoMappableObjectList;
    }

    /**
     * Returns value of Unique Id reference from the POWERLINK object attribute
     * in XDC.
     */
    @Override
    public Object getUniqueIDRef() {
        return uniqueIDRef;
    }

    /**
     * @return The XPath to find this object in the XDC.
     */
    @Override
    public String getXpath() {
        return xpath;
    }

    /**
     * Checks for the RPDO mappable subObjects.
     *
     * @return <code>true</code> if RPDO mappable sub-objects are available.
     *         <code>false</code> if RPDO mappable sub-objects is empty.
     */
    public boolean hasRpdoMappableSubObjects() {
        return !rpdoMappableObjectList.isEmpty();
    }

    /**
     * Checks for the TPDO mappable subObjects.
     *
     * @return <code>true</code> if TPDO mappable sub-objects are available.
     *         <code>false</code> if TPDO mappable sub-objects is empty.
     */
    public boolean hasTpdoMappableSubObjects() {
        return !tpdoMappableObjectList.isEmpty();
    }

    /**
     * Checks for forced objects.
     *
     * @return <code>true</code> if object is forced. <code>false</code> if
     *         object is not forced
     */
    @Override
    public boolean isObjectForced() {
        return nodeInstance.isObjectIdForced(idByte, null);
    }

    /**
     * @return <code>true</code> if object is RPDO mappable. <code>false</code>
     *         if object is not.
     */
    @Override
    public boolean isRpdoMappable() {
        return isRpdoMappable;
    }

    /**
     * @return <code>true</code> if object is TPDO mappable. <code>false</code>
     *         if object is not.
     */
    @Override
    public boolean isTpdoMappable() {
        return isTpdoMappable;
    }

    /**
     * Set the actual value to this object.
     *
     * @param actualValue The value to be set.
     * @param writeToXdc Writes the value to the XDC immediately.
     * @throws IOException Errors with XDC file modifications.
     * @throws JDOMException Errors with time modifications.
     */
    @Override
    public void setActualValue(final String actualValue, boolean writeToXdc)
            throws JDOMException, IOException {

        TObjectAccessType accessType = getAccessType();
        if (accessType != null) {
            if ((accessType == TObjectAccessType.RO)
                    || (accessType == TObjectAccessType.CONST)) {
                throw new RuntimeException("Restricted access to object " + "'"
                        + getName() + "'" + " to set the actual value.");
            }
        }

        this.actualValue = actualValue;

        if (writeToXdc) {
            OpenConfiguratorProjectUtils.updateObjectAttributeActualValue(
                    getNode(), this, actualValue);
        }
    }

    /**
     * Updates the given message string as a POWERLINK object configuration
     * error.
     */
    @Override
    public void setError(final String errorMessage) {
        configurationError = errorMessage;
    }

}
