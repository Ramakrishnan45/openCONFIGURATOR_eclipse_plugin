/*******************************************************************************
 * @file   AddChildModuleWizardPage.java
 *
 * @author Sree Hari Vignesh, Kalycito Infotech Private Limited.
 *
 * @copyright (c) 2016, Kalycito Infotech Private Limited
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

package org.epsg.openconfigurator.wizards;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.epsg.openconfigurator.model.HeadNodeInterface;
import org.epsg.openconfigurator.model.Module;
import org.epsg.openconfigurator.model.Node;
import org.epsg.openconfigurator.util.IPowerlinkConstants;
import org.epsg.openconfigurator.validation.ModuleAddressVerifyListener;
import org.epsg.openconfigurator.xmlbinding.projectfile.InterfaceList;
import org.epsg.openconfigurator.xmlbinding.projectfile.InterfaceList.Interface;
import org.epsg.openconfigurator.xmlbinding.projectfile.TCN;
import org.epsg.openconfigurator.xmlbinding.xdd.ISO15745Profile;
import org.epsg.openconfigurator.xmlbinding.xdd.ISO15745ProfileContainer;
import org.epsg.openconfigurator.xmlbinding.xdd.ModuleType;
import org.epsg.openconfigurator.xmlbinding.xdd.ModuleTypeList;
import org.epsg.openconfigurator.xmlbinding.xdd.ProfileBodyDataType;
import org.epsg.openconfigurator.xmlbinding.xdd.ProfileBodyDevicePowerlinkModularChild;
import org.epsg.openconfigurator.xmlbinding.xdd.TModuleInterface;

/**
 * Wizard page to add name of the Module
 *
 * @author SreeHari
 *
 */
public class AddChildModuleWizardPage extends WizardPage {

    // labels of wizard page.
    private static final String DIALOG_PAGE_NAME = "AddCNModulewizardPage"; //$NON-NLS-1$
    public static final String DIALOG_TILE = "POWERLINK module";
    public static final String DIALOG_DESCRIPTION = "Add a POWERLINK module to the network.";

    private HeadNodeInterface interfaceObj;
    private Spinner position;
    private Label positionlabel;
    private Button enabled;
    private Label positionRange;
    private Label positionRangevalue;
    private Node node;
    private Label addressLabel;

    private Spinner addressText;

    private ModuleAddressVerifyListener addressVerifyListener = new ModuleAddressVerifyListener();

    ModifyListener addressModifyListener = new ModifyListener() {

        @Override
        public void modifyText(ModifyEvent e) {
            getWizard().getContainer().updateButtons();
            setErrorMessage(null);
            setPageComplete(true);

            if (!isAddressValid(addressText.getText())) {

                System.err.println("Address invalid modifier..");
                setErrorMessage("Invalid address.");
                setPageComplete(false);
            }

        }

    };

    // ModifyListener positionModifyListener = new ModifyListener() {
    //
    // @Override
    // public void modifyText(ModifyEvent e) {
    // getWizard().getContainer().updateButtons();
    // setErrorMessage(null);
    // setPageComplete(true);
    // if ((position.getSelection() > position.getMaximum())
    // || (position.getSelection() < position.getMinimum())) {
    // setErrorMessage("Enter a valid position");
    // setPageComplete(false);
    // }
    // if (position.getText() == null) {
    // setErrorMessage("Enter a valid position");
    // setPageComplete(false);
    // }
    // }
    //
    // };

    protected AddChildModuleWizardPage(HeadNodeInterface selectedNodeObj) {

        super(DIALOG_PAGE_NAME);
        interfaceObj = selectedNodeObj;
        System.out.println("Constructor called........");
        node = selectedNodeObj.getNode();

        setTitle(DIALOG_TILE);
        setDescription(DIALOG_DESCRIPTION);
    }

    @Override
    public void createControl(Composite parent) {
        System.out.println("Create Part control called........");
        Composite container = new Composite(parent, SWT.NULL);

        setControl(container);

        position = new Spinner(container, SWT.BORDER);
        position.setBounds(169, 7, 47, 22);
        position.setMaximum(interfaceObj.getMaxModules().intValue());
        // position.addModifyListener(positionModifyListener);
        position.setEnabled(false);
        // position.setSelection(getNewPosition());

        if (!interfaceObj.isUnUsedSlots()) {
            position.setEnabled(false);
        }

        positionlabel = new Label(container, SWT.NONE);
        positionlabel.setBounds(71, 10, 55, 15);
        positionlabel.setText("Position:");

        enabled = new Button(container, SWT.CHECK);
        enabled.setBounds(71, 96, 93, 16);
        enabled.setText("Enabled");
        enabled.setSelection(true);

        positionRange = new Label(container, SWT.NONE);
        positionRange.setBounds(71, 54, 86, 15);
        positionRange.setText("Max Address:");

        positionRangevalue = new Label(container, SWT.NONE);
        positionRangevalue.setBounds(169, 54, 30, 15);
        positionRangevalue.setText(String.valueOf(position.getMaximum()));

        addressLabel = new Label(container, SWT.NONE);
        addressLabel.setBounds(71, 136, 55, 15);
        addressLabel.setText("Address:");

        addressText = new Spinner(container, SWT.BORDER);
        addressText.setBounds(169, 130, 76, 21);
        addressText.setMaximum(interfaceObj.getMaxModules().intValue());
        addressText.addModifyListener(addressModifyListener);

    }

    /**
     * @return The address of module
     */
    public int getAddress() {
        return Integer.valueOf(addressText.getText());
    }

    /**
     * @return The instance of TModuleInterface fron the XDD model
     */
    public TModuleInterface getModuleInterface() {
        if (getXDDModel() != null) {
            List<ISO15745Profile> profiles = getXDDModel().getISO15745Profile();
            for (ISO15745Profile profile : profiles) {
                ProfileBodyDataType profileBodyDatatype = profile
                        .getProfileBody();
                if (profileBodyDatatype instanceof ProfileBodyDevicePowerlinkModularChild) {
                    ProfileBodyDevicePowerlinkModularChild modChild = (ProfileBodyDevicePowerlinkModularChild) profileBodyDatatype;
                    TModuleInterface modInterface = modChild.getDeviceManager()
                            .getModuleManagement().getModuleInterface();
                    return modInterface;
                }
            }
        }
        return null;
    }

    @Deprecated
    private List<Integer> getModulePositionList() {
        Object nodeModel = node.getNodeModel();
        List<InterfaceList.Interface> interfaceList = new ArrayList<InterfaceList.Interface>();
        List<Integer> positionList = new ArrayList<Integer>();
        if (nodeModel instanceof TCN) {

            TCN cn = (TCN) nodeModel;
            if (cn.getInterfaceList() != null) {
                interfaceList.addAll(cn.getInterfaceList().getInterface());
            } else {
                return null;
            }
            for (Interface interfaces : interfaceList) {
                List<InterfaceList.Interface.Module> moduleList = interfaces
                        .getModule();
                for (InterfaceList.Interface.Module module : moduleList) {
                    positionList.add(module.getPosition().intValue());
                }
            }
        }
        return positionList;
    }

    /**
     * @return Instance of ModuleTypeList.
     */
    public ModuleTypeList getModuleTypeList() {
        IWizardPage previousPage = getPreviousPage();
        if (previousPage instanceof ValidateXddModuleWizardPage) {
            ValidateXddModuleWizardPage adModuleXddPage = (ValidateXddModuleWizardPage) previousPage;
            return adModuleXddPage.getModuleTypeList();
        }
        return null;
    }

    private int getNewPosition() {
        setErrorMessage(null);
        List<InterfaceList.Interface> interfaceList = new ArrayList<InterfaceList.Interface>();
        if (interfaceObj.getModuleCollection().size() == 0) {
            // errorField = true;
            System.err.println("Module Collection size .. "
                    + interfaceObj.getModuleCollection().size());
            setErrorMessage("ERROR_MODEL_NOT_AVAILABLE");
            // setPageComplete(false);
            return getModuleInterface().getMinPosition().intValue();
        } else {
            Object nodeModel = node.getNodeModel();
            if (nodeModel instanceof TCN) {
                System.err.println("Module Collection size .. "
                        + interfaceObj.getModuleCollection().size());
                TCN cn = (TCN) nodeModel;
                interfaceList.addAll(cn.getInterfaceList().getInterface());
            }

            return getNextValidModulePosition(interfaceList);
        }
    }

    private int getNextValidModulePosition(
            List<InterfaceList.Interface> interfacelist) {
        setErrorMessage(null);

        List<Integer> positionList = new ArrayList<Integer>();

        for (Interface interfaces : interfacelist) {
            List<InterfaceList.Interface.Module> moduleList = interfaces
                    .getModule();
            for (InterfaceList.Interface.Module module : moduleList) {
                positionList.add(module.getPosition().intValue());
            }
        }

        if (!isPositionAvailable(positionList, position.getSelection())) {
            return position.getSelection();
        }

        // errorField = true;
        String errorMessage = MessageFormat.format(
                "ERROR_MAXIMUM_NODE_ID_LIMIT_REACHED",
                IPowerlinkConstants.CN_MAX_NODE_ID);
        setErrorMessage(errorMessage);
        setPageComplete(false);
        return IPowerlinkConstants.INVALID_NODE_ID;
    }

    /**
     * @return The position of Module
     */
    public int getPosition() {
        return Integer.valueOf(position.getText());
    }

    /**
     * @return Instance of XDD model
     */
    public ISO15745ProfileContainer getXDDModel() {
        IWizardPage previousPage = getPreviousPage();
        if (previousPage instanceof ValidateXddModuleWizardPage) {
            ValidateXddModuleWizardPage xddPage = (ValidateXddModuleWizardPage) previousPage;
            return xddPage.getXddModel();
        }
        return null;
    }

    private boolean isAddressValid(String addressValue) {
        System.err.println("ADdress valid boolean...");
        boolean retval = false;

        if (addressValue == null) {
            return retval;
        }

        if (addressValue.length() == 0) {
            setErrorMessage("Enter address value to the module.");
            return retval;
        }

        int maxModules = Integer.valueOf(positionRangevalue.getText());

        int address = Integer.valueOf(addressValue);
        if (getModuleInterface().getMinAddress() != null) {
            if (address < getModuleInterface().getMinAddress().intValue()) {
                setErrorMessage("Invalid address value.");
                return retval;
            }
        }

        if (getModuleInterface().getMaxAddress() != null) {
            if (address > getModuleInterface().getMaxAddress().intValue()) {
                setErrorMessage("Invalid address value.");
                return retval;
            }
        }

        if (address > maxModules) {
            setErrorMessage("Invalid address value.");
            return retval;
        } else {
            System.err.println("Valid value...." + address + " maxModules...."
                    + maxModules);
        }

        return true;
    }

    public boolean isEnabled() {
        return enabled.getSelection();
    }

    @Override
    public boolean isPageComplete() {
        boolean isMaxPositionValid = true;
        boolean isMinPositionValid = true;
        boolean isMinAddressValid = true;
        boolean isMaxaddressValid = true;
        boolean validModuleType = true;
        boolean valid = true;
        boolean validAddress = true;
        int moduleSize = interfaceObj.getModuleCollection().size();
        int minimumValue = getModuleInterface().getMinPosition().intValue();
        int minimumAddress = getModuleInterface().getMinAddress().intValue();
        if (getModuleInterface() != null) {
            if (getModuleInterface().getMaxPosition() != null) {
                isMaxPositionValid = Integer
                        .valueOf(position.getText()) <= getModuleInterface()
                                .getMaxPosition().intValue();
            } else {
                if (interfaceObj.getMaxModules() != null) {
                    isMaxPositionValid = Integer
                            .valueOf(position.getText()) <= interfaceObj
                                    .getMaxModules().intValue();
                }
            }

            if (getModuleInterface().getMaxPosition() != null) {
                isMinPositionValid = Integer
                        .valueOf(position.getText()) >= minimumValue;
            }

            if (getModuleInterface().getMinAddress() != null) {
                isMinAddressValid = Integer
                        .valueOf(addressText.getText()) >= getModuleInterface()
                                .getMinAddress().intValue();

            }

            if (getModuleInterface().getMaxAddress() != null) {
                isMaxaddressValid = Integer
                        .valueOf(addressText.getText()) <= getModuleInterface()
                                .getMaxAddress().intValue();
            } else {
                isMaxaddressValid = Integer
                        .valueOf(addressText.getText()) <= interfaceObj
                                .getMaxModules().intValue();
            }

        } else {
            System.err.println("Moduleinterface is null");
        }

        if (String.valueOf(interfaceObj.getModuleAddressing())
                .equalsIgnoreCase("POSITION")) {
            // position.removeModifyListener(positionModifyListener);
            addressText.removeModifyListener(addressModifyListener);
            // addressText.removeVerifyListener(addressVerifyListener);
            // position.setEnabled(false);
            addressText.setEnabled(false);
            if (moduleSize != 0) {
                System.err.println("Key set of module position"
                        + interfaceObj.getModuleCollection().keySet());
                Set<Integer> positionSet = interfaceObj.getModuleCollection()
                        .keySet();
                for (Integer position1 : positionSet) {
                    position.setMinimum(position1 + 1);
                }

                Set<Integer> addressSet = interfaceObj.getAddressCollection()
                        .keySet();
                for (Integer address1 : addressSet) {
                    int nextAddress = address1 + 1;
                    addressText.setMinimum(nextAddress);
                }
            } else {
                position.setMinimum(minimumValue);
                addressText.setMinimum(minimumValue);
            }
        } else {
            if (moduleSize != 0) {
                Set<Integer> positionSet = interfaceObj.getModuleCollection()
                        .keySet();
                System.err.println("POsitionSet = " + positionSet);
                for (Integer position1 : positionSet) {
                    minimumValue = position1 + 1;
                }
                Collection<Module> moduleCollection = interfaceObj
                        .getModuleCollection().values();
                List<Integer> addressList = new ArrayList<Integer>();
                for (Module module : moduleCollection) {
                    System.err.println(
                            "Module name == " + module.getModuleName());
                    int address = module.getAddress();
                    addressList.add(address);
                }
                System.err.println("Address List = " + addressList);
                for (Integer addres : addressList) {
                    if (minimumAddress == addres) {
                        minimumAddress = addres + 1;

                    }
                    if (addressList.contains(minimumAddress)) {
                        minimumAddress = minimumAddress + 1;
                    }

                }
                System.err.println("Minimum address = " + minimumAddress);
                position.setMinimum(minimumValue);
                addressText.setMinimum(minimumAddress);

                if ((Integer.valueOf(position.getText()) == 1)
                        || (Integer.valueOf(addressText.getText()) == 1)) {
                    String moduleInterfaceType = getModuleInterface().getType();
                    List<ModuleType> moduleTypeList = getModuleTypeList()
                            .getModuleType();
                    for (ModuleType moduleType : moduleTypeList) {
                        if ((interfaceObj.getInterfaceType()
                                .equalsIgnoreCase(moduleType.getType()))
                                || interfaceObj.getInterfaceType()
                                        .contains(moduleInterfaceType)) {
                            System.out.println("Valid Module type");
                            validModuleType = true;
                        } else {
                            validModuleType = false;
                            setErrorMessage("The module type "
                                    + moduleType.getType()
                                    + " does not match the interface module type "
                                    + interfaceObj.getInterfaceType()
                                    + " on interface "
                                    + interfaceObj.getInterfaceUId() + ".");
                        }
                    }
                }

                int positionValue = Integer.valueOf(position.getText());
                int minPosition = 0;
                List<Integer> positionToBeChecked = new ArrayList<>();
                for (Module mod : moduleCollection) {
                    int positions = mod.getPosition();
                    if (positions < positionValue) {
                        minPosition = positions;
                    }
                    if (positions > positionValue) {
                        positionToBeChecked.add(positions);
                    }
                }

                Module previousPositionModule = interfaceObj
                        .getModuleCollection().get(minPosition);
                List<ModuleType> moduleTypeList = getModuleTypeList()
                        .getModuleType();
                if (previousPositionModule != null) {
                    for (ModuleType moduleType : moduleTypeList) {
                        System.err.println("Previous position module = "
                                + previousPositionModule.getModuleType());
                        System.err.println("current position module = "
                                + getModuleInterface().getType());
                        if (previousPositionModule.getModuleType()
                                .equals(getModuleInterface().getType())) {
                            System.err.println("Min position = " + minPosition);
                            if (positionToBeChecked != null) {
                                for (Integer positionVal : positionToBeChecked) {
                                    Module nextPositionModule = interfaceObj
                                            .getModuleCollection()
                                            .get(positionVal);
                                    String nextPositionModuleType = nextPositionModule
                                            .getModuleInterface().getType();
                                    String addedModuleType = moduleType
                                            .getType();
                                    if (nextPositionModule.isEnabled()) {
                                        if (addedModuleType.equals(
                                                nextPositionModuleType)) {
                                            validModuleType = true;
                                            break;
                                        } else {
                                            setErrorMessage("The module type "
                                                    + addedModuleType
                                                    + " does not match the interface module type "
                                                    + nextPositionModuleType
                                                    + " on module "
                                                    + nextPositionModule
                                                            .getModuleName()
                                                    + ".");
                                            return false;
                                        }
                                    }
                                }
                            }
                            validModuleType = true;
                        } else {
                            validModuleType = false;
                            setErrorMessage("The module type "
                                    + moduleType.getType()
                                    + " does not match the interface module type "
                                    + previousPositionModule.getModuleType()
                                    + " on module "
                                    + previousPositionModule.getModuleName()
                                    + ".");
                        }

                    }
                }

            } else {
                position.setMinimum(minimumValue);
                addressText.setMinimum(minimumAddress);

                if (Integer.valueOf(position.getText()) == 1) {
                    String moduleInterfaceType = getModuleInterface().getType();
                    List<ModuleType> moduleTypeList = getModuleTypeList()
                            .getModuleType();
                    for (ModuleType moduleType : moduleTypeList) {
                        if ((interfaceObj.getInterfaceType()
                                .equalsIgnoreCase(moduleType.getType()))
                                || interfaceObj.getInterfaceType()
                                        .contains(moduleInterfaceType)) {
                            System.out.println("Valid Module type");
                            validModuleType = true;
                        } else {
                            validModuleType = false;
                            setErrorMessage("The module type "
                                    + moduleType.getType()
                                    + " does not match the interface module type "
                                    + interfaceObj.getInterfaceType()
                                    + " on interface "
                                    + interfaceObj.getInterfaceUId() + ".");
                        }
                    }
                }

            }
        }
        boolean pageComplete = (super.isPageComplete());
        pageComplete = true;
        if (!isMaxPositionValid) {
            setErrorMessage(
                    "Module cannot be placed in the specified position.");
            return false;
        } else {
            setErrorMessage(null);
            pageComplete = true;
        }

        if (!isMaxaddressValid) {
            setErrorMessage(
                    "Module cannot be placed in the specified address.");
            return false;
        } else {
            setErrorMessage(null);
            pageComplete = true;
        }

        if (!isMinAddressValid) {
            setErrorMessage(
                    "Module cannot be placed in the specified address.");
            return false;
        } else {
            setErrorMessage(null);
            pageComplete = true;
        }

        if (!isMinPositionValid) {
            setErrorMessage(
                    "Module cannot be placed in the specified position.");
            return false;
        } else {
            setErrorMessage(null);
            pageComplete = true;
        }

        if (!interfaceObj.isUnUsedSlots()) {
            setErrorMessage("Unused slots not available.");
            return false;
        } else {
            pageComplete = true;
        }

        Set<Integer> positionList = interfaceObj.getModuleCollection().keySet();
        if (positionList == null) {
            valid = true;
        } else {
            for (int positions : positionList) {
                if (positions == position.getSelection()) {
                    valid = false;
                }
            }
        }

        Collection<Module> moduleList = interfaceObj.getModuleCollection()
                .values();
        List<Integer> addressList = new ArrayList<>();
        if (moduleList == null) {
            validAddress = true;
        } else {
            for (Module module : moduleList) {
                int address = module.getAddress();
                addressList.add(address);
                if (address == addressText.getSelection()) {
                    validAddress = false;
                }
            }
        }

        if (positionList != null) {
            if (positionList.contains(Integer.valueOf(position.getText()))) {
                valid = false;
            }
        }

        if (!valid) {
            setErrorMessage("The Module position already exists.");
            return false;
        }

        if (moduleList != null) {
            if (addressList != null) {
                if (addressList
                        .contains(Integer.valueOf(addressText.getText()))) {
                    validAddress = false;
                }
            }
        }

        if (!validAddress) {
            setErrorMessage("The Module address already exists.");
            return false;
        } else {
            pageComplete = true;
        }

        if (isAddressValid(addressText.getText())) {

            pageComplete = true;
        } else {
            pageComplete = false;
        }

        if (validModuleType) {
            pageComplete = true;
        } else {
            setErrorMessage("The module cannot be placed at position "
                    + position.getText() + ".");
            // getContainer().showPage(getPreviousPage());
            pageComplete = false;
        }

        if (!(interfaceObj.hasModules())) {
            System.err.println("POsition...." + position.getText());
            if ((position.getText()) != "1") {
                if (!(position.getMinimum() == 1)) {
                    setErrorMessage("Module cannot be placed at position "
                            + position.getText() + ". The interface ('"
                            + interfaceObj.getInterfaceUId()
                            + "') does not contain module at position 1.");

                    pageComplete = false;
                } else {
                    setErrorMessage(null);
                    pageComplete = true;
                }
            } else {
                setErrorMessage(null);
                pageComplete = true;
            }
        }

        System.err.println("PageComplete == " + pageComplete);
        return pageComplete;

    }

    private boolean isPositionAvailable(List<Integer> positionList,
            int positionTobeChecked) {
        boolean positionAvailable = false;
        for (int position : positionList) {
            if (position == positionTobeChecked) {
                positionAvailable = true;
            }
        }
        return positionAvailable;
    }

    public boolean isPositionValid(int positionValue) {
        return true;
    }

    private boolean isValidPosition(int position) {
        System.err.println("valid position..." + position);
        if ((position >= getModuleInterface().getMinPosition().intValue())
                && (position <= getModuleInterface().getMaxPosition()
                        .intValue())) {
            System.err.println("valid position... inside loop...");
            return true;
        }
        return false;
    }

}
