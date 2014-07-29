package org.exoplatform.notifications.model;

public class Device {

	public String id;
	public String platform;
	
	public Device()
	{
		id = "";
		platform = "";
	}
	
	public Device(String _id, String _plf)
	{
		id = (_id == null ? "" : _id);
		platform = (_plf == null ? "" : _plf);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = (id == null ? "" : id);
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = (platform == null ? "" : platform);
	}

	@Override
	public String toString() {
		return "Device { 'id' : '"+id+"' , 'platform' : '"+platform+"' }";
	}

	@Override
	public boolean equals(Object obj) {
		Device other = (Device)obj;
		return this.id.equals(other.id) && this.platform.equals(other.platform);
	}
	
	
	
}
