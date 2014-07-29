package org.exoplatform.notifications.storage;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.exoplatform.notifications.model.Device;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;

public class RegistrationJCRDataStorage {

	private final String PARENT_NODE_LOCATION = "MobileNotificationsRegistrations";
	
	private NodeHierarchyCreator creator;
	
	public RegistrationJCRDataStorage(NodeHierarchyCreator creator, RepositoryService repositoryService)
	{
		this.creator = creator;
	}
	
	/**
	 * Adds a new device to the list of devices registered by the given user
	 * @param username The username of the user who is registering a new device
	 * @param device The details of the device to register
	 * @return true if the device was registered, false otherwise
	 */
	public boolean registerDeviceWithUsername(String username, Device device)
	{
		if (getDevicesOfUsername(username).contains(device)) {
			// return true if the device is already registered
			return true;
		}
		
		Node userNode = getUserRootNode(username);
		
		if (userNode != null) {
			
			try {
				Node registration = userNode.addNode(device.id, StorageUtils.REGISTRATION_NODE_TYPE);
				registration.setProperty(StorageUtils.REGISTRATION_PROP_ID, device.id);
				registration.setProperty(StorageUtils.REGISTRATION_PROP_PLATFORM, device.platform);
				registration.setProperty(StorageUtils.REGISTRATION_PROP_USERNAME, username);
				userNode.save();
				return true; // saves the Device info in JCR and returns true
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// if we got here, there was a problem in the operations above, therefore we return false
		return false;
	}
	
	/**
	 * Retrieves the list of devices registered by the given user
	 * @param username
	 * @return An ArrayList of Device objects registered by the user, or an empty ArrayList
	 */
	public List<Device> getDevicesOfUsername(String username)
	{
		ArrayList<Device> results = new ArrayList<Device>(5);
		Node userNode = getUserRootNode(username);
		try {
			NodeIterator it = userNode.getNodes();
			while (it.hasNext()) {
				// Each Node under the user's node represents a registered device 
				Node node = it.nextNode();
				Device dev = new Device(node.getProperty(StorageUtils.REGISTRATION_PROP_ID).getValue().getString(),
						                node.getProperty(StorageUtils.REGISTRATION_PROP_PLATFORM).getValue().getString());
				results.add(dev);
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
			results = new ArrayList<Device>(0); // The result ArrayList is emptied in case an error occurred in the try block
		}
		return results;
	}
	
	
	private Node getUserRootNode(String username) {
		try {
			return getProjectRootNode().getNode(username);
		} catch (PathNotFoundException e) {
			try {
				Node rootNode = getProjectRootNode();
				Node userNode = rootNode.addNode(username);
				rootNode.save();
				return userNode;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Node getProjectRootNode() {
	    SessionProvider sessionProvider = StorageUtils.getSystemSessionProvider();
	    try {
	      return creator.getPublicApplicationNode(sessionProvider).getNode(PARENT_NODE_LOCATION);
	    } catch (PathNotFoundException e) {
	      try {
	        Node appNode = creator.getPublicApplicationNode(sessionProvider);
	        Node ret = appNode.addNode(PARENT_NODE_LOCATION);
	        appNode.save();
	        return ret;
	      } catch(Exception ex) {
	        return null;
	      }
	    } catch (Exception e) {
	      return null;
	    }
	  }
}
