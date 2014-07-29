package org.exoplatform.notifications.rest.service;

import java.security.Principal;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.notifications.model.Device;
import org.exoplatform.notifications.storage.RegistrationJCRDataStorage;
import org.exoplatform.notifications.storage.StorageUtils;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.IdentityRegistry;
import org.json.JSONException;
import org.json.JSONObject;

@Path("/mobile-notifications-registrations")
public class RegistrationService implements ResourceContainer  {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response registerDeviceOfUser(@Context SecurityContext sc, Device device)
	{
		Principal user = sc.getUserPrincipal();
				
		JSONObject registration = new JSONObject();
		CacheControl cacheControl = new CacheControl();
		cacheControl.setNoCache(true);
	    cacheControl.setNoStore(true);
	    
	    if (user == null || !isInPlatformUsersGroup(user.getName()))
	    {
	    	return Response.status(Status.FORBIDDEN).build();
	    }
	    else
	    {
	    	
	    	try {
	    		
	    		if (StorageUtils.getService(RegistrationJCRDataStorage.class).registerDeviceWithUsername(user.getName(), device))
	    		{
					registration.put("username", user.getName());
					registration.put("device_id", device.id);
					registration.put("platform", device.platform);
	    		}
	    		else
	    		{
	    			return Response.serverError().build();
	    		}
				
				
			} catch (JSONException e) {
				return Response.serverError().build();
			}
	    	
	    }
	    return Response.ok(registration.toString(), MediaType.APPLICATION_JSON).build();
	}
	
	public List<Device> getDevicesOfUser(String username) {
		return StorageUtils.getService(RegistrationJCRDataStorage.class).getDevicesOfUsername(username);
	}
	
	private boolean isInPlatformUsersGroup(String username) {
	    ExoContainer container = ExoContainerContext.getCurrentContainer();
	    IdentityRegistry identityRegistry = (IdentityRegistry) container.getComponentInstanceOfType(IdentityRegistry.class);
	    Identity identity = identityRegistry.getIdentity(username);
	    return identity.isMemberOf("/platform/users");
	  }
	
}
