/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.nimbula.qa.ha;

import com.oracle.colt.commandline_service.commandline_service_api.CommandLineService;
import com.oracle.colt.commandline_service.commandline_service_impl.RemoteSSHCommandlineService;
import com.oracle.colt.result.Result;
import com.oracle.colt.sshagent.SSHSession;
import com.oracle.nimbula.modules.utils.StorageTestUtils;
import com.oracle.nimbula.qa.ha.common.HAConstantDef;
import com.oracle.nimbula.test_framework.NimbulaPropertiesReader;
import com.oracle.nimbula.test_framework.NimbulaTestSpaceLogger;
import com.oracle.nimbula.test_framework.helpers.NimbulaHelper;
import com.oracle.nimbula.test_framework.nimbula.impl.NimbulaCLIClientImpl;
import com.oracle.nimbula.test_framework.resource.types.Instance;
import com.oracle.nimbula.test_framework.resource.types.Property;
import com.oracle.nimbula.test_framework.resource.types.Snapshot;
import com.oracle.nimbula.test_framework.resource.types.StoragePool;
import com.oracle.nimbula.test_framework.resource.types.StorageServer;
import com.oracle.nimbula.test_framework.resource.types.StorageVolume;
import com.oracle.nimbula.test_framework.resource.types.User;
import com.oracle.nimbula.test_framework.result.api.NimbulaResult;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.testng.Assert;

/**
 *
 * @author ctyagi
 */
public class FunctionalUtils {
    
    private NimbulaHelper nimhelper;
    private Instance instance;
    private StorageVolume volume;
    private List<StorageVolume> listVolumes;
    private List<StorageVolume> bootVolumes;
    private StoragePool stPool;
    private StorageServer stServer;
    private Property stProperty;
    private String groupname;
    private String addUserUsername;           
    private String machineImage;
    private String seclist;
    
    private  NimbulaPropertiesReader nimbulaPropertiesReader = NimbulaPropertiesReader.getInstance();    
    protected static final Logger logger = Logger.getLogger(NimbulaTestSpaceLogger.TESTS_LOGGER);
    NimbulaCLIClientImpl cli;
    
    public FunctionalUtils(){
        nimhelper = new NimbulaHelper(HAConstantDef.ROOT_USER,HAConstantDef.ROOT_PASSWORD); 
        groupname = HAConstantDef.GROUP_NAME;
        addUserUsername = HAConstantDef.ADD_USER_NAME;        
    }
    
    public void launchSmallVM(){               
	NimbulaResult<List<Instance>> instanceResult = nimhelper.launchSimple(HAConstantDef.IMAGE_NAME, HAConstantDef.VM_SIZE, "1",true);
        logger.log(Level.INFO, instanceResult.getOutput());
        Assert.assertTrue((instanceResult.getExitValue() == 0),
                            "## Some failure during launch Simple Plan. Execution of the command returned with error:"
                            + instanceResult.getError());        
        this.instance = instanceResult.getObject().get(0);
        logger.log(Level.INFO,"InstanceName: "+ this.instance.getInstanceName());
        instanceResult = nimhelper.getInstance(this.instance.getInstanceName(), true);
        logger.log(Level.INFO, instanceResult.getOutput());
        Assert.assertTrue((instanceResult.getExitValue() == 0),
                            "## Some failure during launchLaunchPlan. Execution of the command returned with error:"
                            + instanceResult.getError());
        this.instance = instanceResult.getObject().get(0);
        logger.log(Level.INFO,"InstanceName: " + this.instance.getInstanceName());
    }
    
    public String getVMstate(){
        if (null == this.instance){
            return null;
        }
        return nimhelper.getInstance(this.instance.getName(), true).getObject().get(0).getState();        
    }
    
    public boolean isVMup(){
        if (null == this.instance){
            return false;
        }
        String output = nimhelper.getInstance(this.instance.getName(), true).getObject().get(0).getState();
        logger.log(Level.INFO, "\nVM state is " + output);        
        if (output.toLowerCase().equals("running")){
            return true;
        }
        return false;
    }
    
    public void deleteVM(){        
        String instanceName = instance.getInstanceName();
	Result result = nimhelper.deleteInstance(instanceName, true);
        logger.log(Level.INFO, (result.getExitValue() == 0) ? "Delete "	+ instanceName + 
                "executed successfully" : " Error occured while executing delete instance " 
                + instanceName + result.getError());
        this.instance = null;
    }
    
    public boolean deleteVolume(){
        Result res = null;
        
        if ( !isVolumeOnline() ){
            logger.log(Level.INFO, "It seems the volume has been deleted");
            return false;
        }   
        this.volume = nimhelper.getStorageVolume(HAConstantDef.STORAGE_SERVER_VOLUME, true).getObject();
        res = nimhelper.deleteStorageVolume(this.volume.getStorageVolumeName(), true);
        do{            
            try {
                Thread.sleep(30000);
            } catch (InterruptedException ex) {
                logger.log(Level.SEVERE, ex.getMessage());
            }    
            res = nimhelper.getStorageVolume(this.volume.getStorageVolumeName(), true);
        }while(res.getOutput().contains("Volume is deleting"));
        
        if ( 0 != res.getExitValue() ){
            logger.log(Level.SEVERE, res.getOutput());
            return false;
        }
        this.volume = null;
        return true;
    }
    
    public boolean deleteStoragePool(){
        if ( 0 != nimhelper.getStoragePool(HAConstantDef.STORAGE_SERVER_POOL, true).getExitValue() ){
            logger.log(Level.INFO, "It seems the storage pool has been deleted");
            return false;
        }
        this.stPool = nimhelper.getStoragePool(HAConstantDef.STORAGE_SERVER_POOL, true).getObject();
        Result res = nimhelper.deleteStoragePool(this.stPool.getStoragePoolName(), true);
        if ( 0 != res.getExitValue() ){
            logger.log(Level.SEVERE, res.getOutput());
            return false;
        }
        this.stPool = null;
        return true;
    }
    
     public boolean deleteStorageServer(){
        if ( 0 != nimhelper.getStorageServer(HAConstantDef.STORAGE_SERVER, true).getExitValue()  ){
            logger.log(Level.INFO, "It seems the storage server has been deleted");
            return false;
        }
        this.stServer = nimhelper.getStorageServer(HAConstantDef.STORAGE_SERVER, true).getObject();
        Result res = nimhelper.deleteStorageServer(this.stServer.getStorageServerName(), true);
        if ( 0 != res.getExitValue() ){
            logger.log(Level.SEVERE, res.getOutput());
            return false;
        }
        this.stServer = null;
        // delete storage prop too
        res = nimhelper.deleteProperty("storage", HAConstantDef.STORAGE_PROP, true);
        if ( 0 != res.getExitValue() ){
            logger.log(Level.SEVERE, res.getOutput());
            return false;
        }
        return true;
    }
     
    public boolean createStorageServer() throws InterruptedException{
        NimbulaResult<StorageServer> storageServer = nimhelper.addStorageServer(HAConstantDef.STORAGE_SERVER, 
                                                        NimbulaPropertiesReader.getInstance().getStorageAddresses(), 
                                                        NimbulaPropertiesReader.getInstance().getStorageAddresses(), "nfs", true);
        if ( (0 != storageServer.getExitValue())&& (!storageServer.getOutput().contains("already exists"))){
            logger.log(Level.SEVERE, "Storage server could not be added");
            return false;
        }        
        String serverStatus = nimhelper.getStorageServer(HAConstantDef.STORAGE_SERVER, true).getObject().getStatus().toLowerCase();
        while(!serverStatus.equals("online")){            
            logger.log(Level.INFO, "Storage server {0} is not online yet", storageServer.getObject().getStorageServerName());
            Thread.sleep(10000);
            serverStatus = nimhelper.getStorageServer(storageServer.getObject().getStorageServerName(), true).getObject().getStatus().toLowerCase();
            logger.log(Level.INFO, "Storage server is {0}", serverStatus);
            if (serverStatus.equals("offline")){
                logger.log(Level.SEVERE, "Storage server is offline! Check your storage.");
                return false;
            }
        }
        this.stServer = nimhelper.getStorageServer(HAConstantDef.STORAGE_SERVER, true).getObject();
        return true;
    }
    
    public boolean createStoragePool() throws InterruptedException{
        String nfsShareName = nimbulaPropertiesReader.getStorageProtocolFieldsNfsShare();
        NimbulaResult<StoragePool> storagePool = nimhelper.addStoragePool(HAConstantDef.STORAGE_SERVER_POOL, 
                                                    this.stServer.getStorageServerName(), 
                                                    HAConstantDef.STORAGE_PROP, "nfs", 
                                                    StorageTestUtils.formatStorageProtocolFieldsNfsShare(nfsShareName), true);
        if ( (0 != storagePool.getExitValue()) && (!storagePool.getOutput().contains("existing pool"))){
            logger.log(Level.SEVERE, "Storage pool could not be added");
            return false;
        }
        this.stPool = nimhelper.getStoragePool(HAConstantDef.STORAGE_SERVER_POOL, true).getObject();
        while (nimhelper.getStoragePool(HAConstantDef.STORAGE_SERVER_POOL, true).getObject().getStatus().toString().equals("Initializing")){
            Thread.sleep(60000);
        }
        Assert.assertTrue(nimhelper.getStoragePool(HAConstantDef.STORAGE_SERVER_POOL, true).getObject().getStatus().toLowerCase().equals("online"), "Server pool is not online");
        return true;
    }
    
    public boolean createStorageProperty(){
        NimbulaResult<Property> storageProp = nimhelper.addProperty("storage", HAConstantDef.STORAGE_PROP , "automation_added_prop", true);
        if ( 0 != storageProp.getExitValue() && (!storageProp.getOutput().contains("already exists"))){
            logger.log(Level.SEVERE, "Storage prop could not be added");
            return false;
        }
        this.stProperty = nimhelper.getProperty("storage", HAConstantDef.STORAGE_PROP, true).getObject();
        return true;
    }
    
    private boolean createRequierdVolumeContainers() throws InterruptedException{
        /**
         * Storage property
         */
        if (!createStorageProperty()){
            return false;
        }       
        
        /**
         * Storage Server
         */
        if (!createStorageServer()){
            return false;
        }
        
        /**
         * Storage Pool
         */
        if (!createStoragePool()){
            return false;
        }
        return true;
    }
    
    public boolean createBootVolume(int numberOfVolumes) throws InterruptedException{
        return privateCreateBootVolume(numberOfVolumes, false);
    }
    
    public boolean createBootVolumeSpeedup(int numberOfVolumes) throws InterruptedException{
        return privateCreateBootVolume(numberOfVolumes, true);
    }
    
    private boolean privateCreateBootVolume(int numberOfVolumes,boolean speedup) throws InterruptedException{                
        if (!createRequierdVolumeContainers()){
            return false;
        }  
        this.bootVolumes = new LinkedList<>();
        for( int i = 0 ; i < numberOfVolumes; i++){
            if (speedup){
                NimbulaResult<StorageVolume> result = this.nimhelper.addStorageVolume(HAConstantDef.STORAGE_SERVER_VOLUME.concat("_").concat(Integer.toString(i)), 
                                                                                      "6g", this.stProperty.getPropertyName(), true, "--bootable true --imagelist " + HAConstantDef.IMAGE_NAME);
                logger.log(Level.INFO, result.getOutput());
                if (result.getExitValue() != 0){
                    logger.log(Level.SEVERE, "FAILED: Could NOT add storage volume successfully" + result.getError());
                    return false;
                }		
                this.bootVolumes.add(result.getObject());
            } else {
                StorageVolume tmpVol = StorageTestUtils.addStorageVolume(nimhelper, HAConstantDef.STORAGE_SERVER_VOLUME.concat("_").concat(Integer.toString(i)), "1g", this.stProperty.getPropertyName(), "--bootable true --imagelist " + HAConstantDef.IMAGE_NAME);        
                if ( null == tmpVol ){
                    return false;
                }
                this.bootVolumes.add(tmpVol);
            }
        }
        return true;
    }
    
    private boolean privateCreateVolume(int numberOfVolumes,boolean speedup) throws InterruptedException{                
        if (!createRequierdVolumeContainers()){
            return false;
        }            
        /**
         * Storage Volume
         */
        listVolumes = new LinkedList<>();
        for(int i = 0; i < numberOfVolumes; i++){
            String volName = HAConstantDef.STORAGE_SERVER_VOLUME.concat("_").concat(Integer.toString(i));                    
            if ( 1 == numberOfVolumes ){
                volName = HAConstantDef.STORAGE_SERVER_VOLUME;
            }
            if (speedup){
                NimbulaResult<StorageVolume> result = this.nimhelper.addStorageVolume(volName, "1g", this.stProperty.getPropertyName(), true);
                logger.log(Level.INFO, result.getOutput());
                if (result.getExitValue() != 0){
                    logger.log(Level.SEVERE, "FAILED: Could NOT add storage volume successfully" + result.getError());
                    return false;
                }		
                this.listVolumes.add(result.getObject());
            } else {
                StorageVolume tmpVol = StorageTestUtils.addStorageVolume(nimhelper, volName, "1g", this.stProperty.getPropertyName());        
                this.listVolumes.add(tmpVol);
            }
        }
        if ( 1 == numberOfVolumes ){
            this.volume = this.listVolumes.get(0);
            this.listVolumes.clear();
        }
        return true;
    }
    
    public List<String> getCreatedVolumeNames(){
        List<String> volumeNames = new LinkedList<>();
        if ( null != this.volume ){
            volumeNames.add(this.volume.getStorageVolumeName());
        }
        if ( null != this.listVolumes ){
            Iterator<StorageVolume> it = this.listVolumes.iterator();
            while (it.hasNext()){
                StorageVolume vol = it.next();
                volumeNames.add(vol.getStorageVolumeName());
            }
        }
        return volumeNames;
    }
    
    public boolean createVolume() throws InterruptedException{
        return privateCreateVolume(1,false);
    }
    
    public boolean createVolumes(int n) throws InterruptedException{
        return privateCreateVolume(n,false);
    }
    
    /**
     * This method will create 1 volume, but it will not wait for the volume
     * to be created if speedup is true
     * @param speedup
     * @return
     * @throws InterruptedException 
     */
    public boolean createVolume(boolean speedup) throws InterruptedException{
        return privateCreateVolume(1,speedup);
    }
    
    /**
     * This method will create "n" volumes, and it will not wait for the volumes
     * to be created if speedup is true
     * @param n
     * @param speedup
     * @return
     * @throws InterruptedException 
     */
    public boolean createVolumes(int n, boolean speedup) throws InterruptedException{
        return privateCreateVolume(n,speedup);
    }
    
    private boolean checkVolumeOnlineStatus(String volumeName){
        StorageVolume volumeStatus = nimhelper.getStorageVolume(volumeName, true).getObject();
        if ( null == volumeStatus ){
            return false;
        }
        if (!volumeStatus.getStatus().equalsIgnoreCase("Online")){
            logger.log(Level.SEVERE, "Volume is " + volumeStatus);
            return false;
        }
        return true;
    }    
    
    public boolean isVolumeOnline(){
        return checkVolumeOnlineStatus(HAConstantDef.STORAGE_SERVER_VOLUME);    
    }
    
    public boolean areVolumesOnline(){
        List<String> volumes = this.getCreatedVolumeNames();        
        HashMap<String,String> results = new HashMap<>();
        Iterator<String> it = volumes.iterator();
        while(it.hasNext()){
            String status = "";
            String vol = it.next();
            if (this.checkVolumeOnlineStatus(vol)){
                status = "online";
            } else{
                status = "offline";
            }
            results.put(vol, status);
        }
        if (results.containsValue("offline")){
            return false;
        }
        return true;
    }
    
    public boolean isStoragePoolOnline(){
        StoragePool storagePoolStatus = nimhelper.getStoragePool(HAConstantDef.STORAGE_SERVER_POOL, true).getObject();
        if ( null == storagePoolStatus ){
            return false;
        }
        if (!storagePoolStatus.getStatus().toLowerCase().equals("online")){
            logger.log(Level.SEVERE, "Storage pool is " + storagePoolStatus);
            return false;
        }
        return true;
    }
    
    public boolean isStorageServerOnline(){
        StoragePool storageServerStatus = nimhelper.getStoragePool(HAConstantDef.STORAGE_SERVER, true).getObject();
        if ( null == storageServerStatus ){
            return false;
        }
        if (!storageServerStatus.getStatus().toLowerCase().equals("online")){
            logger.log(Level.SEVERE, "Storage server is " + storageServerStatus);
            return false;
        }
        return true;
    }
   
    public boolean addGroup(){                
        String group = "/root/".concat(this.groupname);
        String cmd = "add group " + group + " someAutomatedDescription";
        NimbulaPropertiesReader propertiesReader = NimbulaPropertiesReader.getInstance(); 
        cli = NimbulaCLIClientImpl.getInstance();        
        Result res = cli.executeNimbulaAPICommand(cmd, 
                            propertiesReader.getNimbulaRootUser(),
                            HAConstantDef.NIMBULA_PASSWORD_FILE, "-Fname");        
        return true;
    }
    
    public boolean removeGroup(){
        String group = "/root/".concat(this.groupname);
        NimbulaPropertiesReader propertiesReader = NimbulaPropertiesReader.getInstance(); 
        cli = NimbulaCLIClientImpl.getInstance();
        Result res = cli.executeNimbulaAPICommand("delete group " + group, 
                            propertiesReader.getNimbulaRootUser(),
                            HAConstantDef.NIMBULA_PASSWORD_FILE, "-Fname");
        return true;
    }
    
    public boolean groupExists(){
        int found = 0;
        NimbulaPropertiesReader propertiesReader = NimbulaPropertiesReader.getInstance();        
        cli = NimbulaCLIClientImpl.getInstance();
        Result res = cli.executeNimbulaAPICommand("list group /", propertiesReader.getNimbulaRootUser(),
                            HAConstantDef.NIMBULA_PASSWORD_FILE, "-Fname");        
        logger.log(Level.INFO, res.getOutput());
        String[] groups = res.getOutput().split("\n");
        for ( int i = 0 ; i < groups.length; i++){
            String regex = this.groupname;
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(groups[i]); 
            if (m.find()){
                found = 1;
            }
        }
        if (1 == found){
            return true;
        } 
        return false;
    }
    
    public boolean addUser(){
        nimhelper.getNimbulaClientImpl().executeCommand("echo 'OracleCloud9' > " + HAConstantDef.ADD_USER_PASSWORD_FILE, 300);
        nimhelper.getNimbulaClientImpl().executeCommand("chmod 750 " + HAConstantDef.ADD_USER_PASSWORD_FILE, 300);
                
        NimbulaResult<User> result = nimhelper.addUser("/root/".concat(this.addUserUsername), 
                "AutoUser", "test@oracle.com", "", true,"--password ".concat(HAConstantDef.ADD_USER_PASSWORD_FILE));
        logger.log(Level.INFO, ">>{0}", result.getOutput());
        if (0 != result.getExitValue()){    
            logger.log(Level.SEVERE, result.getOutput());
            return false;
        }
        return true;
    }
    
    public boolean deleteUser(){
        Result res = nimhelper.deleteUser("/root/".concat(this.addUserUsername));
        if ( 0 != res.getExitValue()){
            logger.log(Level.SEVERE, res.getOutput());
            return false;
        }
        return true;
    }
    
    public boolean userExists(){
        int found = 0;
        cli = NimbulaCLIClientImpl.getInstance();
        Result res = cli.executeNimbulaAPICommand("list user /", HAConstantDef.ROOT_USER,HAConstantDef.NIMBULA_PASSWORD_FILE, "-Fusername");        
        logger.log(Level.INFO, res.getOutput());
        String[] users = res.getOutput().split("\n");
        for ( int i = 0 ; i < users.length; i++){
            String regex = this.addUserUsername;
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(users[i]); 
            if (m.find()){
                found = 1;
            }
        }
        if (1 == found){
            return true;
        } 
        return false;
    }
    
    public boolean updateUser(){
        cli = NimbulaCLIClientImpl.getInstance();
        Result res = cli.executeNimbulaAPICommand("update user ".concat(this.addUserUsername).concat(" AutoUserUpdated ").concat(" test@oracle.com "), 
                            HAConstantDef.ROOT_USER,HAConstantDef.NIMBULA_PASSWORD_FILE, "-f json");        
        logger.log(Level.INFO, ">>{0}", res.getOutput());
        if (0 != res.getExitValue()){      
            logger.log(Level.SEVERE, res.getOutput());
            return false;
        }
        return true;
    }
    
    public boolean isUserUpdated(){
        int found = 0;
        cli = NimbulaCLIClientImpl.getInstance();
        Result res = cli.executeNimbulaAPICommand("list user /", HAConstantDef.ROOT_USER,HAConstantDef.NIMBULA_PASSWORD_FILE, "-Fusername");        
        logger.log(Level.INFO, res.getOutput());
        String[] users = res.getOutput().split("\n");
        for ( int i = 0 ; i < users.length; i++){
            String regex = "AutoUserUpdated";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(users[i]); 
            if (m.find()){
                found = 1;
            }
        }
        if (1 == found){
            return true;
        } 
        return false;
    }
    
    public boolean isMachineImageUploaded(){
        int found = 0;
        cli = NimbulaCLIClientImpl.getInstance();
        Result res = cli.executeNimbulaAPICommand("list machineimage /", HAConstantDef.ROOT_USER,HAConstantDef.NIMBULA_PASSWORD_FILE, "-Fname");        
        logger.log(Level.INFO, res.getOutput());
        String[] users = res.getOutput().split("\n");
        for ( int i = 0 ; i < users.length; i++){
            String regex = HAConstantDef.UPLOADED_IMAGE;
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(users[i]); 
            if (m.find()){
                found = 1;
            }
        }
        if (1 == found){
            return true;
        } 
        return false;
    }
    
    public boolean downloadImage(){
        Result res = nimhelper.downloadMachineimage(HAConstantDef.IMAGE_NAME, true, "--outputdir downloadedImage");
        if ( 0 != res.getExitValue()){
            logger.log(Level.SEVERE, res.getOutput());
            return false;
        }        
        this.machineImage = "downloadedImage/oracle/public/oel6.tar.gz";
        return true;
    }
    
    public boolean isMachineImageDownloaded(){
        if ( null == this.machineImage){
            logger.log(Level.SEVERE, "Machine image must be downloaded first");
            return false;
        }
        
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("PreferredAuthentications", "publickey,keyboard-interactive,password");
        
        String cmd = "ls -l ".concat(this.machineImage);
        
        SSHSession ssh = new SSHSession(nimbulaPropertiesReader.getNimbulaClientSSHHost(),22,
                                        nimbulaPropertiesReader.getNimbulaClientSSHUser(),
                                        nimbulaPropertiesReader.getNimbulaClientSSHPassword(),config);
        ssh.connect();
        RemoteSSHCommandlineService nodeCli = new RemoteSSHCommandlineService(ssh);
        Result nodeResult = nodeCli.runCommand(cmd);
        String output = nodeResult.getOutput();
        logger.log(Level.INFO, output);
        if (!output.contains(this.machineImage)){
            return false;
        }        
        return true;
    }
    
    public boolean deleteMachineImageDownloadDirectory(){
        if ( null == this.machineImage){
            logger.log(Level.SEVERE, "Machine image must be downloaded first");
            return false;
        }
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("PreferredAuthentications", "publickey,keyboard-interactive,password");
            
        String cmd = "rm -rf ".concat(this.machineImage);
        
        SSHSession ssh = new SSHSession(nimbulaPropertiesReader.getNimbulaClientSSHHost(),22,
                                        nimbulaPropertiesReader.getNimbulaClientSSHUser(),
                                        nimbulaPropertiesReader.getNimbulaClientSSHPassword(), config);
        ssh.connect();
        RemoteSSHCommandlineService nodeCli = new RemoteSSHCommandlineService(ssh);
        Result nodeResult = nodeCli.runCommand(cmd);        
        if ( 0 != nodeResult.getExitValue()){
            logger.log(Level.INFO, "Machine image download directory not removed successfully");
            return false;
        }
        return true;
    }
    
    public boolean uploadImage(){        
        cli = NimbulaCLIClientImpl.getInstance();
        NimbulaPropertiesReader propertiesReader = NimbulaPropertiesReader.getInstance();
        String NIMBULA_PASSWORD_FILE_BASENAME = "/tmp/nimbulaPasswdFile";
        String NIMBULA_PASSWORD_FILE = NIMBULA_PASSWORD_FILE_BASENAME + propertiesReader.getNimbulaRootUser().replace('/', '_');
        Result res = cli.executeNimbulaAPICommand("add machineimage ".concat(HAConstantDef.UPLOADED_IMAGE).concat(" ").concat(this.machineImage), 
                            propertiesReader.getNimbulaRootUser(), NIMBULA_PASSWORD_FILE, "-f json"); 
        System.out.println(">>" + res.getOutput());
        if ( 0!= res.getExitValue()){      
            logger.log(Level.SEVERE, res.getOutput());
            return false;
        }
        return true; 
    }
    
    public boolean deleteMachineImage(){        
        cli = NimbulaCLIClientImpl.getInstance();
        NimbulaPropertiesReader propertiesReader = NimbulaPropertiesReader.getInstance();
        String NIMBULA_PASSWORD_FILE_BASENAME = "/tmp/nimbulaPasswdFile";
        String NIMBULA_PASSWORD_FILE = NIMBULA_PASSWORD_FILE_BASENAME + propertiesReader.getNimbulaRootUser().replace('/', '_');
        Result res = cli.executeNimbulaAPICommand("delete machineimage ".concat(HAConstantDef.UPLOADED_IMAGE), 
                            propertiesReader.getNimbulaRootUser(), NIMBULA_PASSWORD_FILE, "-f json"); 
        System.out.println(">>" + res.getOutput());
        if ( 0!= res.getExitValue()){      
            logger.log(Level.SEVERE, res.getOutput());
            return false;
        }
        return true; 
    }
    
    public boolean createSnapshot(){        
        NimbulaResult<Snapshot> result = nimhelper.addSnapshot(this.instance.getName(), true);
        if ( 0 != result.getExitValue()){
            logger.log(Level.SEVERE, result.getOutput());
            return false;
        }
        return true;
    }
    
    public boolean addSeclist(){
        Result result = nimhelper.addSecList(HAConstantDef.SECLIST_NAME, "PERMIT", "PERMIT", true);
        if ( 0 != result.getExitValue() ){
            logger.log(Level.SEVERE,result.getOutput());
            return false;
        }
        this.seclist = HAConstantDef.SECLIST_NAME;
        return true;
    }
    
    public boolean updateSeclist(){
        return updateSeclist(this.seclist, "PERMIT");
    }
    
    public boolean updateSeclist(String seclistName, String policy){
        Result result = nimhelper.updateSecList(seclistName, policy, true, "--description \"updated seclist \"");
        if ( 0 != result.getExitValue() ){
            logger.log(Level.SEVERE,result.getOutput());
            return false;
        }            
        return true;
    }
    
    public boolean deleteSeclist(){
        Result res = nimhelper.deleteSecList(this.seclist, true);
        if ( 0 != res.getExitValue() ){
            logger.log(Level.SEVERE,res.getOutput());
            return false;
        }
        this.seclist = null;
        return true;
    }    
    
    public boolean seclistExists(){
        NimbulaPropertiesReader propertiesReader = NimbulaPropertiesReader.getInstance();
        cli = NimbulaCLIClientImpl.getInstance();
        Result res = cli.executeNimbulaAPICommand("list seclist / ", 
                            propertiesReader.getNimbulaRootUser(),
                            HAConstantDef.NIMBULA_PASSWORD_FILE, "-Fname");
        System.out.println(">>" + res.getOutput());
        if ( 0 != res.getExitValue()){      
            logger.log(Level.SEVERE, res.getOutput());
            return false;
        }
        if (!res.getOutput().contains(this.seclist)){
            return false;
        }
        return true;
    }
    
    public boolean deleteCreatedVolumes(){
        if ( null == this.listVolumes){
            logger.log(Level.SEVERE, "There were no created volumes this this object");
            return false;
        }
        Iterator<StorageVolume> it = this.listVolumes.iterator();
        while(it.hasNext()){
            StorageVolume vol = it.next();
            String volumeName = vol.getStorageVolumeName();
            Result res = nimhelper.deleteStorageVolume(volumeName, true);
            if ( 0 != res.getExitValue()){
                logger.log(Level.SEVERE, "Unable to delete the volume : {0}", res.getOutput());
                return false;
            }
            do{            
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException ex) {
                    logger.log(Level.SEVERE, ex.getMessage());
                }    
                res = nimhelper.getStorageVolume(volumeName, true);
            } while(res.getOutput().contains("Volume is deleting"));
        }
        return true;
    }
    
    public boolean deleteCreatedBootVolumes(){
        if ( null == this.bootVolumes){
            logger.log(Level.SEVERE, "There were no created boot volumes this this object");
            return false;
        }
        Iterator<StorageVolume> it = this.bootVolumes.iterator();
        while(it.hasNext()){
            StorageVolume vol = it.next();
            String volumeName = vol.getStorageVolumeName();
            Result res = nimhelper.deleteStorageVolume(volumeName, true);
            if ( 0 != res.getExitValue()){
                logger.log(Level.SEVERE, "Unable to delete the volume : {0}", res.getOutput());
                return false;
            }
            do{            
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException ex) {
                    logger.log(Level.SEVERE, ex.getMessage());
                }    
                res = nimhelper.getStorageVolume(volumeName, true);
            } while(res.getOutput().contains("Volume is deleting"));
        }
        return true;
    }
    
    public boolean deleteAllSystemVolumes(){
        List<StorageVolume> volumes = nimhelper.listStorageVolume("/", true).getObject();
        if ( null == volumes ){
            logger.log(Level.SEVERE, "No volumes to be deleted");
        }
        Iterator<StorageVolume> it = volumes.iterator();
        while ( it.hasNext() ){
            StorageVolume vol = it.next();
            Result res = nimhelper.deleteStorageVolume(vol.getStorageVolumeName(), true);
            if ( 0 != res.getExitValue()){
                logger.log(Level.SEVERE, "Unable to delete the volume : " + res.getOutput());
                return false;
            }
            do{            
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException ex) {
                    logger.log(Level.SEVERE, ex.getMessage());
                }    
                res = nimhelper.getStorageVolume(vol.getStorageVolumeName(), true);
            }while(res.getOutput().contains("Volume is deleting"));
            
        }
        return true;
    }
    
    public boolean speedup_createVolumes(int number){
        NimbulaPropertiesReader propertiesReader = NimbulaPropertiesReader.getInstance();
        CommandLineService commandlineService;
        String NIMBULA_API = propertiesReader.getNimbulaAPI();
        SSHSession session;
        
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("PreferredAuthentications", "publickey,keyboard-interactive,password");
        session = new SSHSession(
                        propertiesReader.getNimbulaClientSSHHost(),
                        propertiesReader.getNimbulaClientSSHPort(),
                        propertiesReader.getNimbulaClientSSHUser(),
                        propertiesReader.getNimbulaClientSSHPassword(),config);
        commandlineService = new RemoteSSHCommandlineService(session);
        String property = HAConstantDef.STORAGE_PROP;
        for ( int i=0;i<number;i++ ){
            String stVol=HAConstantDef.STORAGE_SERVER_VOLUME.concat("_").concat(Integer.toString(i));
            String cmd = "oracle-compute-api -a " + NIMBULA_API + " -u " + HAConstantDef.ROOT_USER + " -p " + HAConstantDef.NIMBULA_PASSWORD_FILE + " add storagevolume " + stVol + " 1g " + property + " -f json &";
            logger.log(Level.INFO, cmd);
            Result result = commandlineService.runCommand(cmd);
            if ( 0 != result.getExitValue() ){
                logger.log(Level.SEVERE, cmd + "Error : command exec failed!");
                return false;
            }
        }
        return true;
    }

    public boolean speedup_deleteVolumes(int number){
        NimbulaPropertiesReader propertiesReader = NimbulaPropertiesReader.getInstance();
        CommandLineService commandlineService;
        String NIMBULA_API = propertiesReader.getNimbulaAPI();
        SSHSession session;
        
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("PreferredAuthentications", "publickey,keyboard-interactive,password");
        session = new SSHSession(
                        propertiesReader.getNimbulaClientSSHHost(),
                        propertiesReader.getNimbulaClientSSHPort(),
                        propertiesReader.getNimbulaClientSSHUser(),
                        propertiesReader.getNimbulaClientSSHPassword(),config);
        commandlineService = new RemoteSSHCommandlineService(session);
        for ( int i=0;i<number;i++ ){
            String stVol=HAConstantDef.STORAGE_SERVER_VOLUME.concat("_").concat(Integer.toString(i));
            String cmd = "oracle-compute-api -a " + NIMBULA_API + " -u " + HAConstantDef.ROOT_USER + " -p " + HAConstantDef.NIMBULA_PASSWORD_FILE + " delete storagevolume " + stVol + " -f json &";
            logger.log(Level.INFO, cmd);
            Result result = commandlineService.runCommand(cmd);
            if ( 0 != result.getExitValue() ){
                logger.log(Level.SEVERE, cmd + "Error : command exec failed!");
                return false;
            }
        }
        return true;
    }
}

