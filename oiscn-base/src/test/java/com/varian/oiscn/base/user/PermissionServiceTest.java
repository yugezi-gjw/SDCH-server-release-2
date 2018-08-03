/**
 * 
 */
package com.varian.oiscn.base.user;

import com.varian.oiscn.config.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PermissionService.class, File.class, Yaml.class})
public class PermissionServiceTest {

	protected Configuration rootConfig;
	protected InputStream fileInputStream;
	protected Yaml yaml;
	protected LinkedHashMap content = new LinkedHashMap<>();
	protected FileInputStream fis;
	
	@Before
	public void setUp() throws Exception {
		rootConfig = new Configuration();
		rootConfig.setPermissionConfigFile("permissionConfigFile");
		
        PowerMockito.mockStatic(File.class);
        File mockFile = PowerMockito.mock(File.class);
        PowerMockito.whenNew(File.class).withArguments(Mockito.anyString()).thenReturn(mockFile);
        PowerMockito.when(mockFile.exists()).thenReturn(true);
        PowerMockito.when(mockFile.isFile()).thenReturn(true);
        
        PowerMockito.whenNew(File.class).withArguments(Mockito.anyString()).thenReturn(mockFile);
        
        yaml = PowerMockito.mock(Yaml.class);
        PowerMockito.whenNew(Yaml.class).withNoArguments().thenReturn(yaml);
        
        fis = PowerMockito.mock(FileInputStream.class);
        PowerMockito.whenNew(FileInputStream.class).withAnyArguments().thenReturn(fis);
        
	}

	/**
	 * Test method for {@link com.varian.oiscn.base.user.PermissionService#init(com.varian.oiscn.config.Configuration)}.
	 */
	@Test
	public void testInitBlank() {
		rootConfig.setPermissionConfigFile(null);
		content = new LinkedHashMap<>();
		PowerMockito.when(yaml.load(Mockito.any(FileInputStream.class))).thenReturn(content);		
		try{
			PermissionService.init(rootConfig);
		} catch(Exception e) {
			Assert.assertTrue(e instanceof RuntimeException);
		}
	}


	/**
	 * Test method for {@link com.varian.oiscn.base.user.PermissionService#init(com.varian.oiscn.config.Configuration)}.
	 */
	@Test
	public void testInitNormal() {
		rootConfig.setPermissionConfigFile("permissionConfigFile");
		
		content = new LinkedHashMap<>();

        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put("operation", "ViewPatientList");
        linkedHashMap.put("resourceGroups", Arrays.asList("onlogist_001", "onlogist_002"));

        content.put("group01", Arrays.asList(linkedHashMap));
		
		PowerMockito.when(yaml.load(Mockito.any(FileInputStream.class))).thenReturn(content);	
		PermissionService.init(rootConfig);


        Assert.assertNotNull(PermissionService.groupPermissionMap);
        Assert.assertTrue(PermissionService.groupPermissionMap.size() > 0);
	}
	
	@Test
    public void testGetOperationListByGroup() {
        rootConfig.setPermissionConfigFile("permissionConfigFile");
        content = new LinkedHashMap<>();
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put("operation", "ViewPatientList");
        linkedHashMap.put("resourceGroups", Arrays.asList("onlogist_001", "onlogist_002"));
        content.put("group01", Arrays.asList(linkedHashMap));
        PowerMockito.when(yaml.load(Mockito.any(FileInputStream.class))).thenReturn(content);
        PermissionService.init(rootConfig);

        Assert.assertTrue(PermissionService.getOperationListByGroup("group01").size() > 0);
    }

    @Test
    public void testGetResourceGroupsOfViewPatientList() {
        rootConfig.setPermissionConfigFile("permissionConfigFile");
        content = new LinkedHashMap<>();
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put("operation", "ViewPatientList");
        linkedHashMap.put("resourceGroups", Arrays.asList("onlogist_001", "onlogist_002"));
        content.put("group01", Arrays.asList(linkedHashMap));
        PowerMockito.when(yaml.load(Mockito.any(FileInputStream.class))).thenReturn(content);
        PermissionService.init(rootConfig);

        Assert.assertTrue(PermissionService.getResourceGroupsOfViewPatientList("group01").size() > 0);
    }
}
