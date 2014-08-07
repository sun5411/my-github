package com.oracle.nimbula.qa.ha.hw.FailureResiliency;

import java.net.UnknownHostException;
import java.util.logging.Level;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Sun Ning
 */
public class addImageZfsTakeover extends TakeoverBaseClass { 
    @BeforeClass
    public void addStServer_setup() throws InterruptedException, UnknownHostException{
        super.setup();
        Assert.assertTrue(func.downloadImage(),"Error : Download machine image failed!");
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_addMachineImage() throws InterruptedException{
        func.uploadImage();

        if (!func.isMachineImageUploaded()){
            logger.log(Level.INFO, "Machineimage failed to upload when NoSQL Replica was killed");
            Thread.sleep(30000);
            Assert.assertTrue(func.uploadImage(),"Image upload failed");
        }
        Assert.assertTrue(func.isMachineImageUploaded(),"Machine image is not uploaded");
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_zfsTakeover() throws InterruptedException{
        Assert.assertTrue(super.takeover(), "Error : Storage takeover failed!");
    }  
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        func.deleteMachineImage();
        func.deleteMachineImageDownloadDirectory();
    }
}
