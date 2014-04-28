package com.oracle.nimbula.functional.storage;

import java.util.List;
import java.util.logging.Level;


import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.oracle.colt.result.Result;
import com.oracle.nimbula.modules.utils.StorageTestUtils;
import com.oracle.nimbula.test_framework.BaseTestCase;
import com.oracle.nimbula.test_framework.helpers.NimbulaHelper;
import com.oracle.nimbula.test_framework.resource.types.Instance;
import com.oracle.nimbula.test_framework.resource.types.StorageVolume;
import com.oracle.nimbula.test_framework.result.api.NimbulaResult;
import java.util.logging.Logger;

public class addBootVolumeKillRabbitmq extends BaseTestCase {	

	StorageVolume storageVolume = null;
	Instance instance = null;
	String storageTags;
    private int bootVolumesNum = 10;
	
	private String storageImageList = nimbulaPropertiesReader.getStorageImageList();
    
		public addBootVolumeKillRabbitmq() {
		customerNimbulaHelper = new NimbulaHelper(getUser(), getUserPassword());
	}

    @BeforeClass
    public void setup() {
        util = new HAUtil();

		for(int i=0;i<bootVolumesNum;i++){
		    String storageVolumeLocal = getUser() + "/storageBootVolume"+i;
		    storageTags = StorageTestUtils.formatStorageTags("tag1");
		    storageVolume =StorageTestUtils.addStorageVolume(customerNimbulaHelper, storageVolumeLocal, "7G", nimbulaPropertiesReader.getStorageProperty(),"--tags "+ storageTags, "--imagelist " + storageImageList );
		    checkPoint("StorageVolume : " + storageVolume.getStorageVolumeName());
		    logger.log(Level.INFO, "Storage Volume: "+storageVolume.getStorageVolumeName());
			}
    }

    @test
	public void addBootVolumes() {
		for(int i=0;i<bootVolumesNum;i++){
		    String storageVolumeLocal = getUser() + "/storageBootVolume"+i;
		    StorageTestUtils.deleteStorageVolumes(customerNimbulaHelper,storageVolume);
        }
    }
		

	@Test
    public void RabbitmqFailure() throws InterruptedException{
        util.killNDService("rabbitmq");
        thread.sleep(60000);
    }

    /*
	@AfterClass(alwaysRun=true)
	public void tearDownSetup() {
			
		
		StorageTestUtils.deleteInstance(customerNimbulaHelper, instance); 		
		
		StorageTestUtils.deleteStorageVolumes(customerNimbulaHelper,storageVolume);
	

	}
    */
}
