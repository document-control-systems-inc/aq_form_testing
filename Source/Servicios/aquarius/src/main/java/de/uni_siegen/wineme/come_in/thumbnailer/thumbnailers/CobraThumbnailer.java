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

package de.uni_siegen.wineme.come_in.thumbnailer.thumbnailers;

import java.io.File;
import java.io.IOException;

import de.uni_siegen.wineme.come_in.thumbnailer.ThumbnailerException;

/**
 * This class creates Thumbnails from websites
 * 
 * Depends:
 * <li> Cobra http://lobobrowser.org/cobra/
 *
 */
public class CobraThumbnailer extends AbstractThumbnailer {

	@Override
	public void generateThumbnail(File input, File output) throws IOException, ThumbnailerException {
		
		
/*		
		BufferedInputStream in = null;
		ZipFile zipFile = null;
		
		try {
			 zipFile = new ZipFile(input);
		} catch (ZipException e) {
			throw new ThumbnailerException("This is not a zipped file. Is this really an OpenOffice-File?", e);
		}
		
		try {
			ZipEntry entry = zipFile.getEntry("Thumbnails/thumbnail.png");
			if (entry == null)
				throw new ThumbnailerException("Zip file does not contain 'Thumbnails/thumbnail.png' . Is this really an OpenOffice-File?");
			
			in = new BufferedInputStream(zipFile.getInputStream(entry));				

			ResizeImage resizer = new ResizeImage(thumbWidth, thumbHeight);
			resizer.setInputImage(in);
			resizer.writeOutput(output);
			
			in.close();
		}
		finally {
			IOUtil.quietlyClose(in);
			IOUtil.quietlyClose(zipFile);
		}
*/
	}
	
    /**
     * Get a List of accepted File Types.
     * All OpenOffice Formats are accepted.
     * 
     * @return MIME-Types
     */
	public String[] getAcceptedMIMETypes()
	{
		return new String[] {
			      "text/html"
		};
	}

}
