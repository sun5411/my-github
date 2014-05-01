/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.nimbula.qa.ha.serviceFailureResiliency;

import com.oracle.colt.commandline_service.commandline_service_api.CommandLineService;
import com.oracle.colt.commandline_service.commandline_service_impl.RemoteSSHCommandlineService;
import com.oracle.colt.result.Result;
import com.oracle.colt.sshagent.SSHSession;
import com.oracle.nimbula.modules.utils.StorageTestUtils;
import com.oracle.nimbula.qa.ha.FunctionalUtils;
import com.oracle.nimbula.qa.ha.HAUtil;
import com.oracle.nimbula.qa.ha.OrchestrationUtil;
import com.oracle.nimbula.qa.ha.common.HAConstantDef;
import com.oracle.nimbula.test_framework.BaseTestCase;
import com.oracle.nimbula.test_framework.NimbulaPropertiesReader;
import com.oracle.nimbula.test_framework.helpers.NimbulaHelper;
import com.oracle.nimbula.test_framework.nimbula.api.NimbulaCLI;
import com.oracle.nimbula.test_framework.nimbula.impl.NimbulaCLIClientImpl;
import com.oracle.nimbula.test_framework.resource.types.StorageVolume;
import com.oracle.nimbula.test_framework.result.api.NimbulaResult;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author ctyagi
 */
public class addVolumeKillBstoragemanager extends BaseTestCase {
    String defaultCustomer, defaultCustomerPassword;
    HAUtil util;
    FunctionalUtils func;
    OrchestrationUtil orch;
    private NimbulaCLI nimbula;
    private NimbulaHelper nimhelper;
    private final int volumesNum = 3;
    private String stProperty;
    private final String storageVolumeName = "/qa/stVol";
    protected static NimbulaPropertiesReader propertiesReader = NimbulaPropertiesReader.getInstance();
    private final String NIMBULA_API = propertiesReader.getNimbulaAPI();
    private CommandLineService commandlineService;
    /*
    private Properties nimbulaProperties;
    nimbulaProperties = new Properties();
    nimbulaProperties.put("NIMBULA_API", NIMBULA_API);
    */
    private SSHSession session = null;


    @BeforeClass
    public void setup() {
        logger.log(Level.INFO,"###### Setup ...");
        util = new HAUtil();
        func = new FunctionalUtils();
        nimhelper = new NimbulaHelper(HAConstantDef.ROOT_USER,HAConstantDef.ROOT_PASSWORD);
        stProperty = HAConstantDef.STORAGE_PROP;

        // Create storage property/server/storagepool on nimbula site
        try {
            //Storage property
            func.createStorageProperty();
            
            //Storage Server
            func.createStorageServer();
            
            //Storage Pool
            func.createStoragePool();
        } catch (InterruptedException ex) {
            Logger.getLogger(addVolumeKillBstoragemanager.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Create ssh session for  nimbula_api command
        session = new SSHSession(
                        propertiesReader.getNimbulaClientSSHHost(),
                        propertiesReader.getNimbulaClientSSHPort(),
                        propertiesReader.getNimbulaClientSSHUser(),
                        propertiesReader.getNimbulaClientSSHPassword());
        commandlineService = new RemoteSSHCommandlineService(session);
    }
    
    @Test(alwaysRun=true, timeOut=900000)
    public void addVolume() throws InterruptedException{
        logger.log(Level.INFO,"###### addVolume testing ...");
        for(int i = 0;i<volumesNum;i++){
            //Result result = null;
           // result = nimbula.executeNimbulaAPICommand(command, this.NIMBULA_USER, this.NIMBULA_PASSWORD_FILE, argsAndOperand);
           // Result result = commandlineService.runCommand("oracle-compute-api -a " + NIMBULA_API + " -u " + user + " -p " + passwordFile + " " + command + " " + argsAndOperand, nimbulaProperties, null, TIMEOUT);

            //nimbulaHelper.NIMBULA_PASSWORD_FILE
            //passwordFile = HAConstantDef.NIMBULA_PASSWORD_FILE;
            String command = "oracle-compute-api -a " + NIMBULA_API + " -u " + HAConstantDef.ROOT_USER + " -p " + HAConstantDef.NIMBULA_PASSWORD_FILE + " add storagevolume " + storageVolumeName + i + " 1G " + stProperty + " -f json";
            Result result = commandlineService.runCommand(command);
            //Result result = commandlineService.runCommand("oracle-compute-api -a " + NIMBULA_API + " -u " + HAConstantDef.ROOT_USER + " -p " + passwordFile + " " + command);


            //cli = NimbulaCLIClientImpl.getInstance();
            //cli.executeNimbulaAPICommand("add storagevolume " + storageVolumeName + i + " 1G" + stProperty, HAConstantDef.ROOT_USER,HAConstantDef.ROOT_PASSWORD, "-f json");
            //NimbulaResult<StorageVolume> result;
            //result = nimhelper.addStorageVolume(storageVolumeName + i, "1G", stProperty, true,"--tags " + "volumeTag" + i);
        }

        Thread.sleep(60000);
        for(int i=0;i<volumesNum;i++) {
            StorageVolume volumeStatus;
            volumeStatus = nimhelper.getStorageVolume(storageVolumeName + i, true).getObject();
            if ( null == volumeStatus ){
                    logger.log(Level.WARNING, "{0} : Can not get the Volume info.", storageVolumeName + i);
            } else if (!"online".equals(volumeStatus.getStatus().toLowerCase())){
                    logger.log(Level.SEVERE, "Volume is {0}", volumeStatus);
            } else { 
                    logger.log(Level.INFO,"Volume {0} is online",storageVolumeName + i);
            }
        }
    }

    @Test(alwaysRun=true,timeOut=900000)
    public void last_bStoragemanagerFailure() throws InterruptedException{
        logger.log(Level.INFO,"###### bStoragemanagerFailure ...");
        Thread.sleep(10000);
        util.killNDService("bstoragemanager");
    }
    
    @AfterClass
    public void tearDown() {
        logger.log(Level.INFO,"###### tearDown ...");
        ///////////////////delete Instance// //////////////////////////////
        //StorageTestUtils.deleteInstance(customerNimbulaHelper, instance);
        ///////////////////delete StorageVolumes/////////////////////////
        for(int i=0;i<volumesNum;i++){
            StorageTestUtils.deleteStorageVolumes(nimhelper, storageVolumeName + i);
        }
        //func.deleteAllSystemVolumes();
    }
}
