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
public class addAttachmentKillBstoragemanager extends BaseTestCase {
    String defaultCustomer, defaultCustomerPassword;
    HAUtil util;
    FunctionalUtils func;
    OrchestrationUtil orch;
    InstanceUtil vm;

    protected static final Logger logger = Logger.getLogger(NimbulaTestSpaceLogger.TESTS_LOGGER);
    
    @BeforeClass
    public void setup() {
    	defaultCustomer = nimbulaPropertiesReader.getNimbulaDefaultCustomer();
	defaultCustomerPassword = nimbulaPropertiesReader.getNimbulaDefaultCustomerPassword();
        util = new HAUtil();                
        func = new FunctionalUtils();
        vm = new InstanceUtil();
    }
    
    @Test(alwaysRun=true, timeOut=900000)
    public void addAttachment() throws InterruptedException{
        //vm.launchNSimpleVMs(1);
        vm.launchSimple();
        //vm.launchVM();
        List<String> uuid = vm.getVMsWithStorageAttachment();
        vm.getStorageAttachmentNames(uuid.get(0));
        vm.getStorageAttachment(uuid.get(0));

        func.createVolumes(20);
    }
    
    @Test(alwaysRun=true,timeOut=900000)
    public void last_bStoragemanagerFailure() throws InterruptedException{                     
        util.killNDService("bstoragemanager");               
        Thread.sleep(60000);
    }  
    
    //Keep the volume for deleteVolumeKillBstoragemanager test
    /*
    @AfterClass
    public void tearDown() {        
        func.deleteVolume();
    }        
    */
}
