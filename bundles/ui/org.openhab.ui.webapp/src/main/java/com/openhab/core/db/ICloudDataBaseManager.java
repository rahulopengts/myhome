package com.openhab.core.db;

import java.sql.Connection;

import org.openhab.model.core.ModelRepository;

public interface ICloudDataBaseManager {

	public Connection getDataConnection();
	
	public void closeConnection();
	

}
