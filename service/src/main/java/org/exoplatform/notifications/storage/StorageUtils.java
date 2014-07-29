package org.exoplatform.notifications.storage;



import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;

public class StorageUtils {

	public static final String REGISTRATION_NODE_TYPE = "exo:mobileNotificationsRegistration";
	public static final String REGISTRATION_PROP_ID = "exo:registeredDeviceID";
	public static final String REGISTRATION_PROP_PLATFORM = "exo:registeredDevicePlatform";
	public static final String REGISTRATION_PROP_USERNAME = "exo:registeredUsername";
	
	public static <T> T getService(Class<T> clazz) {
	    return getService(clazz, null);
	  }
	  
	  public static <T> T getService(Class<T> clazz, String containerName) {
	    ExoContainer container = ExoContainerContext.getCurrentContainer();
	    if (containerName != null) {
	      container = RootContainer.getInstance().getPortalContainer(containerName);
	    }
	    if (container.getComponentInstanceOfType(clazz)==null) {
	      containerName = PortalContainer.getCurrentPortalContainerName();
	      container = RootContainer.getInstance().getPortalContainer(containerName);
	    }
	    return clazz.cast(container.getComponentInstanceOfType(clazz));
	  }

	  public static SessionProvider getSystemSessionProvider() {
	    SessionProviderService sessionProviderService = getService(SessionProviderService.class);
	    return sessionProviderService.getSystemSessionProvider(null);
	  }

	  public static SessionProvider getUserSessionProvider() {
	    SessionProviderService sessionProviderService = getService(SessionProviderService.class);
	    return sessionProviderService.getSessionProvider(null);
	  }
	  
	  public static ManageableRepository getRepository() {
	    try {
	      RepositoryService repositoryService = getService(RepositoryService.class);
	      return repositoryService.getCurrentRepository();
	    } catch (Exception e) {
	      // TODO
	    }
	    return null;
	  }
	  
	  public static Session getSystemSession(String workspace) throws LoginException, NoSuchWorkspaceException, RepositoryException {
	    return getSystemSessionProvider().getSession(workspace,
	                                                 getRepository());
	  }

	  public static Session getUserSession(String workspace) throws LoginException, NoSuchWorkspaceException, RepositoryException {
	    return getUserSessionProvider().getSession(workspace,
	                                                 getRepository());
	  }
	  
//	  public static int generateId(String name) {
//		    if (counter.get(name) == null) {
//		      counter.put(name, (int)(new GregorianCalendar().getTimeInMillis()));
//		    }
//		    int ret = counter.get(name);
//		    counter.put(name, ++ret);
//		    return ret;
//		  }

}
