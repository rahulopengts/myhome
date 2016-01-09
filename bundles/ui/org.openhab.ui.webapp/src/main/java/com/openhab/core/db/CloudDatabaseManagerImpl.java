package com.openhab.core.db;

import java.sql.Connection;

import javax.sql.DataSource;

import org.openhab.model.core.ModelRepository;

public class CloudDatabaseManagerImpl implements ICloudDataBaseManager {

	private Connection	dataConnection	=	null;
	
	@Override
	public Connection getDataConnection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void closeConnection() {
		// TODO Auto-generated method stub
		
	}


	
	

}
