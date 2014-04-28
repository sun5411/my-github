/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.nimbula.qa.ha.serviceFailureResiliency;

import com.oracle.nimbula.qa.ha.FunctionalUtils;
import com.oracle.nimbula.qa.ha.HAUtil;
import com.oracle.nimbula.qa.ha.OrchestrationUtil;
import com.oracle.nimbula.qa.ha.common.HAConstantDef;
import com.oracle.nimbula.test_framework.BaseTestCase;
import com.oracle.nimbula.test_framework.NimbulaTestSpaceLogger;
import java.util.logging.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.oracle.nimbula.qa.ha.InstanceUtil;
/**
 *
 * @author nsun
 */
public class attachVolumeKillBstoragemanager extends BaseTestCase {
    //String defaultCustomer, defaultCustomerPassword;
    HAUtil util;
    FunctionalUtils func;
    OrchestrationUtil orch;
    InstanceUtil vm;
    private int volumesNum = 20;

    protected static final Logger logger = Logger.getLogger(NimbulaTestSpaceLogger.TESTS_LOGGER);
    
    @BeforeClass
    public void setup() {
    	//defaultCustomer = nimbulaPropertiesReader.getNimbulaDefaultCustomer();
	    //defaultCustomerPassword = nimbulaPropertiesReader.getNimbulaDefaultCustomerPassword();
        util = new HAUtil();                
        func = new FunctionalUtils();
        vm = new InstanceUtil();

        func.createVolumes(volumesNum);

        //vm.launchVMwithStorage();
        vm.launchSimple();
        vmUUID = vm.getCreatedInstancesUUID().get(0);
        while (!vm.isVMup(vmUUID)){
            logger.log(Level.INFO,"VM is not up yet")
            Thread.sleep(30000);
        }

        String hostingNodeUUID = util.getVMnode(vmUUID);
        String hostingNodeIP = util.getNodeIP(hostingNodeUUID); 
    }
    
    @Test(alwaysRun=true, timeOut=900000)
    public void attachVolume() throws InterruptedException{
        //attach storage volumes on the running vm
        for (int i=0;i < volumesNum; i++){
            String volumeName = func.getCreatedVolumeNames().get(i);
            vm.addAttachment(vmUUID,volumeName);
            Assert.assertTrue(vm.addStorageAttachment(vmUUID, volumeName), "Storage volume could not be attached to the VM");
        }
    }
    
    @Test(alwaysRun=true,timeOut=900000)
    public void last_bStoragemanagerFailure() throws InterruptedException{                     
        Thread.sleep(60000);
        util.killNDService("bstoragemanager");               
    }  
    

    @AfterClass
    public void tearDown() {        
        func.deleteVolume();
        vm.deleteAllCreatedVMs();
        //func.deleteStoragePool();
        //func.deleteStorageServer();
    }        
}
