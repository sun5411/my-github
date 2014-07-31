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
public class downloadImageZfsReboot extends TakeoverBaseClass {
    @BeforeClass
    public void addStServer_setup() throws InterruptedException, UnknownHostException{
        super.setup();
    }
    
    @Test(alwaysRun=true, timeOut=129600000)
    public void aa_downloadMachineImage() throws InterruptedException{
        func.downloadImage();
        if (!func.isMachineImageDownloaded()){
            logger.log(Level.INFO, "Machineimage failed to download when bImage was killed");
            Thread.sleep(30000);
            Assert.assertTrue(func.downloadImage(),"Machine image failed to download - 2nd try");
        }
        Assert.assertTrue(func.isMachineImageDownloaded(),"Machine image is not downloaded");
    }
    
    @Test(alwaysRun=true,timeOut=129600000)
    public void bb_zfsTakeover() throws InterruptedException{
        String master = zfs1.getMaster();
        if (master.equalsIgnoreCase(zfs1.getHostname())){
            Assert.assertTrue(super.zfs1.reboot(), "Error : Storage reboot failed!");
        } else if(master.equalsIgnoreCase(zfs2.getMaster())) {
            Assert.assertTrue(super.zfs2.reboot(), "Error : Storage reboot failed!");
        }else {
            logger.log(Level.SEVERE, "Error : No master found");            
        }
    }  
    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        func.deleteMachineImage();
        func.deleteMachineImageDownloadDirectory();

    }
}
