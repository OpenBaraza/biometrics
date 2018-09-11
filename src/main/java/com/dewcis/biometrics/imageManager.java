package com.dewcis.biometrics;

import java.util.logging.Logger;
import java.util.Map;
import java.util.Base64;

import java.awt.image.BufferedImage;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.dewcis.utils.webdav;

public class imageManager {
	Logger log = Logger.getLogger(imageManager.class.getName());

	webdav wdv;

	public imageManager(Map<String, String> cfgs) {
		String webdavPath = cfgs.get("webdav_path");
		String webdavUsername = cfgs.get("webdav_username");
		String webdavPassword = cfgs.get("webdav_password");
		webdav wdv = new webdav(webdavPath, webdavUsername, webdavPassword);
	}
		
	public BufferedImage saveImage(String imageStr, String fileName) {
		BufferedImage img = null;
		try {
			byte[] imageArr = Base64.getDecoder().decode(imageStr);
			InputStream is = new ByteArrayInputStream(imageArr);
			wdv.saveFile(is, fileName);
			img = ImageIO.read(is);
		} catch(IOException ex) {
			log.severe("IO Error : " + ex);
		}
		return img;
	}
	
	public InputStream getFile(String fileName) {
		return wdv.getFile(fileName);
	}
	
	public BufferedImage getImage(String fileName) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(wdv.getFile(fileName));
		} catch(IOException ex) {
			log.severe("IO Error : " + ex);
		}
		return img;
	}

}
