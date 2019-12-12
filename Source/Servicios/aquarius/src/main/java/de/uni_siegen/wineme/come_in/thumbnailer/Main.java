/*
 * regain/Thumbnailer - A file search engine providing plenty of formats (Plugin)
 * Copyright (C) 2011  Come_IN Computerclubs (University of Siegen)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Contact: Come_IN-Team <come_in-team@listserv.uni-siegen.de>
 */

package de.uni_siegen.wineme.come_in.thumbnailer;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.f2m.aquarius.parameters.ThumbParams;

//import de.uni_siegen.wineme.come_in.thumbnailer.thumbnailers.JODExcelConverterThumbnailer;
//import de.uni_siegen.wineme.come_in.thumbnailer.thumbnailers.JODHtmlConverterThumbnailer;
//import de.uni_siegen.wineme.come_in.thumbnailer.thumbnailers.JODPowerpointConverterThumbnailer;
//import de.uni_siegen.wineme.come_in.thumbnailer.thumbnailers.JODWordConverterThumbnailer;
import de.uni_siegen.wineme.come_in.thumbnailer.thumbnailers.NativeImageThumbnailer;
import de.uni_siegen.wineme.come_in.thumbnailer.thumbnailers.OpenOfficeThumbnailer;
import de.uni_siegen.wineme.come_in.thumbnailer.thumbnailers.PDFBoxThumbnailer;
//TODO
//import de.uni_siegen.wineme.come_in.thumbnailer.thumbnailers.ScratchThumbnailer;

/**
 * Little Command-line Application to illustrate the usage of this library.
 * 
 * @author Benjamin
 *
 */
public class Main {
	protected static Logger mLog = Logger.getLogger(Main.class);

	protected static void loadExistingThumbnailers(
			ThumbnailerManager thumbnailer) {

		if (classExists("de.uni_siegen.wineme.come_in.thumbnailer.thumbnailers.NativeImageThumbnailer"))
			thumbnailer.registerThumbnailer(new NativeImageThumbnailer());

		thumbnailer.registerThumbnailer(new OpenOfficeThumbnailer());
		thumbnailer.registerThumbnailer(new PDFBoxThumbnailer());
		//TODO: Regresar:
		/*
		try {
			
			thumbnailer.registerThumbnailer(new JODWordConverterThumbnailer());
			thumbnailer.registerThumbnailer(new JODExcelConverterThumbnailer());
			thumbnailer.registerThumbnailer(new JODPowerpointConverterThumbnailer());
			thumbnailer.registerThumbnailer(new JODHtmlConverterThumbnailer());
			
		} catch (IOException e) {
			mLog.error("Could not initialize JODConverter:", e);
		}
		*/
		//TODO: Regresar:
		/*
		thumbnailer.registerThumbnailer(new ScratchThumbnailer());
		*/
	}
	
	public static boolean classExists(String qualifiedClassname)
	{
		try {
			Class.forName(qualifiedClassname);
		} catch (ClassNotFoundException e) {
			return false;
		}
		return true;
	}
	
	public static void main(String[] params) throws Exception
	{
		File in = null;
		if (params.length == 0)
		{
			String strIn = "/opt/ecm/sample/pdf-2hojas.pdf";
			in = new File(strIn);
		} else {
			in = new File(params[0]);
		}
		
		ThumbnailerManager thumbnailer = new ThumbnailerManager();
	
		loadExistingThumbnailers(thumbnailer);
		thumbnailer.setImageSize(2000, 2000, 0);
		thumbnailer.setThumbnailFolder(ThumbParams.THUMB_DEFAULT_FOLDER);
		
		File out = null;
		out = thumbnailer.createThumbnail(in);
		System.out.println("SUCCESS: Thumbnail created:\n" + out.getAbsolutePath());
	}
}
