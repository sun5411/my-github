/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oracle.nimbula.qa.ha.hw.FailureResiliency;

import com.oracle.nimbula.qa.ha.OrchestrationUtil;
import com.oracle.nimbula.qa.ha.common.ControlPlaneBaseTest;
import com.oracle.nimbula.qa.ha.common.HAConstantDef;
import com.oracle.nimbula.qa.ha.common.NimbulaHAPropertiesReader;
import com.oracle.nimbula.qa.ha.hardware.IBSwitch;
import com.oracle.nimbula.qa.ha.hardware.Zfs;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.testng.Assert;

/**
 *
 * @author ctyagi
 */
public class TakeoverBaseClass extends ControlPlaneBaseTest {
    
    protected Zfs zfs1;
    protected Zfs zfs2;
    protected IBSwitch sw1;
    protected IBSwitch sw2;
    
    public void setup() throws InterruptedException{
        super.setup();
        NimbulaHAPropertiesReader haProp = NimbulaHAPropertiesReader.getInstance();
        /*
        String domain = haProp.getZFS_DOMAIN();
        
        String hostname1 = haProp.getZFS1_HOSTNAME();
        String ip1 = haProp.getZFS1_IP();
        String username1 = haProp.getZFS1_USERNAME();
        String password1 = haProp.getZFS1_PASSWORD();
        String console1 = haProp.getZFS1_CONSOLE();
                
        String hostname2 = haProp.getZFS2_HOSTNAME();
        String ip2 = haProp.getZFS2_IP();
        String username2 = haProp.getZFS2_USERNAME();
        String password2 = haProp.getZFS2_PASSWORD();
        String console2 = haProp.getZFS2_CONSOLE();
        
        try {
            zfs1 = new Zfs(username1,password1,hostname1,console1,domain);
            zfs2 = new Zfs(username2,password2,hostname2,console2,domain);            
        } catch (UnknownHostException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }
        
        String sw1_username = haProp.getSW1username();
        String sw1_passwd = haProp.getSW1password();
        String sw1_host = haProp.getSW1hostname();
        
        String sw2_username = haProp.getSW2username();        
        String sw2_passwd = haProp.getSW2password();        
        String sw2_host = haProp.getSW2hostname();
        domain = haProp.getZFS_DOMAIN();
        
        try {
            sw1 = new IBSwitch(sw1_username,sw1_passwd,sw1_host,domain);
            sw2 = new IBSwitch(sw2_username,sw2_passwd,sw2_host,domain);
        } catch (UnknownHostException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }
        */
    }
    
    protected boolean takeover() throws InterruptedException{
        boolean result = false;
        while (!zfs1.isAlive()){            
            logger.log(Level.SEVERE, "Storage head 1 is not up yet");
            Thread.sleep(30000);
        }
        while (!zfs2.isAlive()){            
            logger.log(Level.SEVERE, "Storage head 2 is not up yet");
            Thread.sleep(30000);
        }
        String master = zfs1.getMaster();
        if (master.equalsIgnoreCase(zfs1.getHostname())){
            //zfs1 is the master
            result = zfs2.takeover();
            Thread.sleep(120000);
        } else if (master.equalsIgnoreCase(zfs2.getMaster())){
            //zfs2 is the master
            result = zfs1.takeover();
            Thread.sleep(120000);
        } else {
            logger.log(Level.SEVERE, "No master found");            
            result = false;
        }
        return result;
    }    
    
    protected boolean rebootZFSmaster() throws InterruptedException{
        while (!zfs1.isAlive()){            
            logger.log(Level.SEVERE, "Storage head 1 is not up yet");
            Thread.sleep(30000);
        }
        while (!zfs2.isAlive()){            
            logger.log(Level.SEVERE, "Storage head 2 is not up yet");
            Thread.sleep(30000);
        }
        String master = zfs1.getMaster();
        if (master.equalsIgnoreCase(zfs1.getHostname())){
            return zfs1.reboot();
        } else if (master.equalsIgnoreCase(zfs2.getMaster())){
            return zfs2.reboot();
        } else {
            logger.log(Level.SEVERE, "No master found");                        
        }
        return false;
    }    
    
    public List<String> add_N_Shares(int n){
        String master = zfs1.getMaster();
        if (master.equalsIgnoreCase(zfs1.getHostname())){            
            return zfs1.addNShares(n);
        } else if (master.equalsIgnoreCase(zfs2.getMaster())){
            return zfs2.addNShares(n);
        } else {
            logger.log(Level.SEVERE, "No master found");                        
        }
        return null;
    }
    
    public boolean destroyProject(){
        String master = zfs1.getMaster();
        if (master.equalsIgnoreCase(zfs1.getHostname())){            
            return zfs1.destroyProject();
        } else if (master.equalsIgnoreCase(zfs2.getMaster())){
            return zfs2.destroyProject();
        } else {
            logger.log(Level.SEVERE, "No master found");                        
        }
        return false;
    }
   
    /**
     * This method will simulate a storage appliance network outage by shutting
     * down both heads for a period of "mins" minutes. 
     * @param mins
     * @return true if shutdown/startup completed successfully.
     * @throws InterruptedException 
     */
    protected boolean networkFailure(int mins) throws InterruptedException{
        if (!zfs1.stop()){ return false; }
        if (!zfs2.stop()){ return false; }
        
        Thread.sleep(mins * 60000);
        
        if (!zfs1.start()){ return false; }
        if (!zfs2.start()){ return false; }
       
        return true;
    }
    
    /**
     * This method will add a list of orchestrations
     * @param orchestrationJsonFiles
     * @return List<OrchestrationUtil>  
     */
    public List<OrchestrationUtil> addListOfOrchestrations(List<String> orchestrationJsonFiles){        
        List<OrchestrationUtil> res = new LinkedList<>();     
        Assert.assertNotNull(orchestrationJsonFiles, "orchestrationJsonFiles is null in TakeoverBaseClass -> addStartListOfOrchestrations");        
        for (int i = 0 ; i < orchestrationJsonFiles.size(); i++){
            res.add(new OrchestrationUtil(HAConstantDef.ROOT_USER, HAConstantDef.ROOT_PASSWORD));                  
            Assert.assertTrue(0 == res.get(i).addOrchestration(orchestrationJsonFiles.get(i)),"Failed to add " + orchestrationJsonFiles.get(i));         
        }       
        return res;
    }
    
    /**
     * This method will start a list of orchestrations
     * @param orchestrationJsonFiles
     * @return List<OrchestrationUtil>  
     */
    public int startListOfOrchestrations(List<OrchestrationUtil> orch){     
        Assert.assertNotNull(orch,"orch is null in TakeoverBaseClass -> startListOfOrchestrations");
        Iterator<OrchestrationUtil> it = orch.iterator();
        while (it.hasNext()){
            OrchestrationUtil or = it.next();
            Assert.assertTrue(0 == or.startOrchestration(),"Failed to start " +  or.getName());        
        }
        return 0;
    }
    
    /**
     * This method will update a list of orchestrations
     * @param orchestrationJsonFiles
     * @return List<OrchestrationUtil>  
     */
    public void updateListOfOrchestrations(List<OrchestrationUtil> orch, List<String> orchestrationJsonFiles){                 
        Assert.assertNotNull(orch, "orch is null in TakeoverBaseClass -> updateListOfOrchestrations");        
        Assert.assertNotNull(orchestrationJsonFiles, "orchestrationJsonFiles is null in TakeoverBaseClass -> updateListOfOrchestrations");        
        for (int i = 0 ; i < orchestrationJsonFiles.size(); i++){                          
            Assert.assertTrue(0 == orch.get(i).updateOrchestration(orchestrationJsonFiles.get(i)),"Failed to add " + orchestrationJsonFiles.get(i));         
        }       
    }
    
    /**
     * This method will get a list of orchestrations
     * @param orchestrationJsonFiles
     * @return List<OrchestrationUtil>  
     */
    public void getListOfOrchestrations(List<OrchestrationUtil> orch){                 
        Assert.assertNotNull(orch, "orch is null in TakeoverBaseClass -> updateListOfOrchestrations");        
        Iterator<OrchestrationUtil> it = orch.iterator();
        while(it.hasNext()){
            OrchestrationUtil or = it.next();
            Assert.assertTrue(0 == or.getOrchestration(),"Get orchestration request failed");
        }
    }
    
    /**
     * This method will stop a List<OrchestrationUtil> of orchestrations
     * @param orchObj 
     */
    public void stopListOfOrchestrations(List<OrchestrationUtil> orchObj){        
        Assert.assertNotNull(orchObj,"orchObj is null in TakeoverBaseClass -> stopListOfOrchestrations");
        
        Iterator <OrchestrationUtil> it = orchObj.iterator();
        while(it.hasNext()){
            OrchestrationUtil o = it.next();
            Assert.assertTrue( 0 == o.stopOrchestration(),"Orchestration could not be stopped");            
        }
    }
    
    /**
     * This method will delete a List<OrchestrationUtil> of orchestrations
     * @param orchObj 
     */
    public void deleteListOfOrchestrations(List<OrchestrationUtil> orchObj){        
        Assert.assertNotNull(orchObj,"orchObj is null in TakeoverBaseClass -> deleteListOfOrchestrations");
        
        Iterator <OrchestrationUtil> it = orchObj.iterator();
        while(it.hasNext()){
            OrchestrationUtil o = it.next();
            Assert.assertTrue( 0 == o.deleteOrchestration(),"Orchestration could not be deleted");
        }
    }
    
    public boolean switchFailover(){
        if(sw1.isMasterSwitch()){
            if (!sw1.reboot()){
                logger.severe("Failed to reboot sw1");
                return false;
            }
        } else if (sw2.isMasterSwitch()){
            if (!sw2.reboot()){
                logger.severe("Failed to reboot sw2");
                return false;
            }
        } else {
            logger.severe("No switch is the master");
            return false;
        }
        return true;
    }
     public boolean switchFailover(String portName){
        if("eth0".equals(portName)){
            if (!sw1.reboot()){
                logger.severe("Failed to reboot sw1");
                return false;
            }
        } else if ("eth1".equals(portName)){
            if (!sw2.reboot()){
                logger.severe("Failed to reboot sw2");
                return false;
            }
        }
        return true;
     }
}
