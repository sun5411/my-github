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

/**
 *
 * @author nsun
 */
public class deleteVolumeKillGluster extends BaseTestCase {
    String defaultCustomer, defaultCustomerPassword;
    HAUtil util;
    FunctionalUtils func;
    OrchestrationUtil orch;
    protected static final Logger logger = Logger.getLogger(NimbulaTestSpaceLogger.TESTS_LOGGER);
    
    @BeforeClass
    public void setup() {
    	defaultCustomer = nimbulaPropertiesReader.getNimbulaDefaultCustomer();
	defaultCustomerPassword = nimbulaPropertiesReader.getNimbulaDefaultCustomerPassword();
        util = new HAUtil();                
        func = new FunctionalUtils();

        func.createVolumes(20);
    }
    
    @Test(alwaysRun=true, timeOut=900000)
    public void deleteVolume() throws InterruptedException{
        func.deleteVolume();
    }
    
    @Test(alwaysRun=true,timeOut=900000)
    public void GlusterFailure() throws InterruptedException{                     
        Thread.sleep(60000);
        util.killNDService("Gluster");
    }  
    
    @AfterClass
    public void tearDown() {        
        func.deleteVolume();
    }        
}
