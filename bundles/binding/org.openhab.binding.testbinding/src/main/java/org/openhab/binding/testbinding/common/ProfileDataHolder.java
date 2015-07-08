package org.openhab.binding.testbinding.common;

import java.sql.Time;

public class ProfileDataHolder {

	
	private String profileName		=	null;
	private String profileId		=	null;
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	public String getProfileId() {
		return profileId;
	}
	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}
	private Time profileInvokeTime	=	null;
	public Time getProfileInvokeTime() {
		return profileInvokeTime;
	}
	public void setProfileInvokeTime(Time profileInvokeTime) {
		this.profileInvokeTime = profileInvokeTime;
	}
}
