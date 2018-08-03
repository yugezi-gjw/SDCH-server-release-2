package com.varian.oiscn.appointment.calling;

import com.varian.oiscn.config.CallingSystemConfiguration;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.appointment.calling.CallingConfig;
import com.varian.oiscn.core.appointment.calling.Device;
import com.varian.oiscn.core.appointment.calling.ServerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Calling Service as Call Server's client.<br>
 */
@Slf4j
public class CallingService {

    /**
     * Calling System Server configuration
     */
	protected static ServerConfiguration serverConfig = null;
	protected static List<Device> deviceList = new ArrayList<>();
	protected static boolean isReady = false;

	/**
     * Initialize the Server Configuration.<br>
     *
	 * @param rootConfig Calling Server Configuration
	 * @throws CallingServiceException 
	 */
	public static void init(Configuration rootConfig) throws CallingServiceException {
		String callingConfigFile = rootConfig.getCallingConfigFile();
		if (callingConfigFile == null) {
			isReady = false;
		} else {
			CallingSystemConfiguration callingSystemConfig = loadConfiguration(rootConfig.getCallingConfigFile());
			if (callingSystemConfig != null && rootConfig != null) {
				CallingConfig callingConfig = callingSystemConfig.getConfig();
				rootConfig.setCallingConfig(callingConfig);
				if (callingConfig != null && callingConfig.getCheckInGuide() != null
						&& callingConfig.getCheckInGuide().getDevicesGuide() != null) {
					deviceList.addAll(callingConfig.getCheckInGuide().getDevicesGuide().getDevices());
				}
				serverConfig = callingSystemConfig.getServer();
				if (serverConfig != null) {
					CallingHttpClient.initConfig(serverConfig);
				}
				isReady = true;
			}

		}
	}

	/**
	 * Load Calling Configuration file into CallingSystemConfiguration class.<br>
	 *
	 * @param callingConfigFile Calling Config File Name
	 * @return CallingSystemConfiguration
	 * @throws CallingServiceException 
	 */
	protected static CallingSystemConfiguration loadConfiguration(String callingConfigFile) throws CallingServiceException {
		CallingSystemConfiguration config = null;
		if (callingConfigFile != null) {
			File file = new File(callingConfigFile);
			if (file != null && file.exists() && file.isFile()) {
				try {
					config = new Yaml().loadAs(new FileInputStream(file), CallingSystemConfiguration.class);
				} catch (FileNotFoundException e) {
					log.warn(e.getMessage());
					throw new CallingServiceException(ServerStatusEnum.NO_CONFIGURATION);
				}
			}
		}
		return config;
	}

	/**
	 * Server is Ready.<br>
	 *
	 * @return isReady
	 */
	public static boolean isReady() {
		return isReady;
	}
	
	/**
     * Send GET request and response with status information.<br>
	 *
	 * @param msg Calling Message
	 * @return Server Status
	 */
	public static ServerStatusEnum sendMsg(Object msg) {
		// Establish Http Connection
        ServerStatusEnum status = ServerStatusEnum.NORMAL;
        // Send request
        CallingHttpClient client = null;
		try {
			client = new CallingHttpClient();
			client.sendMsg(msg);
        } catch (CallingServiceException e) {
            log.warn(e.getMessage());
            status = e.getStatus();
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return status;
	}

	/**
	 * Return Calling Device Id by Aria Device Id.<br>
	 * @param ariaDeviceId Aria Device Id
	 * @return Calling Device Id
	 */
	public static String getCallingDeviceIdByAriaDeviceId(String ariaDeviceId) {
		String callingDeviceId = StringUtils.EMPTY;
		if (!StringUtils.isEmpty(ariaDeviceId)) {
			for (Device device : deviceList) {
				if (ariaDeviceId.equals(device.getAriaDeviceId())) {
					callingDeviceId = device.getDeviceId();
					break;
				}
			}
		}
		return callingDeviceId;
	}

	/**
	 * Return Device Room Name by Aria Device Id.<br>
	 *
	 * @param ariaDeviceId Aria Device Id
	 * @return Device Room Name
	 */
	public static String getDeviceRoomByAriaDeviceId(String ariaDeviceId) {
		String deviceRoom = StringUtils.EMPTY;
		if (!StringUtils.isEmpty(ariaDeviceId)) {
			for (Device device : deviceList) {
				if (ariaDeviceId.equals(device.getAriaDeviceId())) {
					deviceRoom = device.getDeviceRoom();
					break;
				}
			}
		}
		return deviceRoom;
	}

	/**
	 * Return Device Room Name by Calling Device Id.<br>
	 *
	 * @param callingDeviceId Calling Device Id
	 * @return Device Room Name
	 */
	public static String getDeviceRoomByCallingDeviceId(String callingDeviceId) {
		String deviceRoom = StringUtils.EMPTY;
		if (!StringUtils.isEmpty(callingDeviceId)) {
			for (Device device : deviceList) {
				if (callingDeviceId.equals(device.getDeviceId())) {
					deviceRoom = device.getDeviceRoom();
					break;
				}
			}
		}
		return deviceRoom;
	}
}
