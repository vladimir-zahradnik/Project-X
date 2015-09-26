/*
 * @(#)StreamParser
 *
 * Copyright (c) 2005 by dvb.matt, All rights reserved.
 * 
 * This file is part of ProjectX, a free Java based demux utility.
 * By the authors, ProjectX is intended for educational purposes only, 
 * as a non-commercial test project.
 * 
 *
 * This program is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package sk.vzahradn.dvb.projectx.parser;

import sk.vzahradn.dvb.projectx.common.JobCollection;

import sk.vzahradn.dvb.projectx.xinput.XInputFile;


/**
 * main thread
 */
public class StreamParserESSubpicture extends StreamParserBase {

	/**
	 * 
	 */
	public StreamParserESSubpicture()
	{
		super();
	}

	/**
	 * subpicture elementary stream
	 */
	public String parseStream(JobCollection collection, XInputFile xInputFile, int pes_streamtype, int action, String vptslog)
	{
		// is elementary
		new StreamProcess(CommonParsing.SUBPICTURE, collection, xInputFile, "-1", "sp", vptslog, CommonParsing.ES_TYPE);

		return null;
	}

}
