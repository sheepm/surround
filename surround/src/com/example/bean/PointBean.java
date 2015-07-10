package com.example.bean;

public class PointBean {

	public String name;
	public LocationBean location;
	public String address;
	public String telephone;
	public boolean hasPhone;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocationBean getLocation() {
		return location;
	}

	public void setLocation(LocationBean location) {
		this.location = location;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public boolean isHasPhone() {
		return hasPhone;
	}

	public void setHasPhone(boolean hasPhone) {
		this.hasPhone = hasPhone;
	}

}
