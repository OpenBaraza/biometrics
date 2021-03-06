/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2011.0329
 * @since       1.6
 * website		www.dewcis.come
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */
package com.dewcis.swing;

import com.dewcis.utils.DBio;

import javax.swing.JPanel;

import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

public class DImagePanel extends JPanel {

	private BufferedImage img = null;
	private int iw, ih;

	public DImagePanel(String imgFileName) {
		super(null);
		initPanel(imgFileName);
	}

	public void initPanel(String imgFileName) {
		DBio io = new DBio();
		img = io.loadImage(imgFileName);

		iw = img.getWidth();
		ih = img.getHeight();
		
		super.setOpaque(false);
	}

	protected void paintComponent(Graphics g) {
		if(img != null) {
			Dimension d = getSize();
			int w = (int)d.getWidth();
			int h = (int)d.getHeight();

			g.drawImage(img, 0, 0, w, h, 0, 0, iw, ih, null);
		}

		super.paintComponent(g);
	}
}
