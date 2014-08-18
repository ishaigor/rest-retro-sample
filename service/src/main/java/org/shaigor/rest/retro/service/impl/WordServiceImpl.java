/**
 * Copyright 2014 the original author or authors.
 * 
 * This file is part of Retro-fitting Security into REST web services sample (the sample).
 * The sample is part of the talk presented at Java One 2014 
 * 	https://oracleus.activeevents.com/2014/connect/sessionDetail.ww?SESSION_ID=1765&amp;tclass=popup
 * 
 * The sample is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The sample is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the sample.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.shaigor.rest.retro.service.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.shaigor.rest.retro.service.IWordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * A REST web service that will need securing
 * @author Irena Shaigorodsky
 */
@RestController
@RequestMapping("/word")
public class WordServiceImpl implements IWordService {
	
	private static Logger log = LoggerFactory.getLogger(WordServiceImpl.class);
	
	@Value("${words.charset}") 	private String charset;
	@Value("${words.path}") 	private String path;

	private List<String> words;
	
	/**
	 * Reads the content of the file on start-up using the path property 'path'
	 * and the charset 'charset' property provided 
	 * through resource injection
	 */
	@PostConstruct public void init() {
		log.info("The dictionary file {} is in {} charset", charset, path);
		BufferedReader br = null;
		List<String> fileContent = new ArrayList<String>();

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(path), charset));
			String strLine;
	
			while ((strLine = br.readLine()) != null)   {
				fileContent.add(strLine);
			}
		} catch (Exception e) {
			log.error("Could not read from file", e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					log.error("Could not close the file", e);
				}
			}
		}
		words = Collections.unmodifiableList(fileContent);
		log.info("The dictionary file {} loaded with {} entries", path, words.size());
	}

	public List<String> listWords() {
		log.debug("The dictionary will be served");
		return words;
	}

	/**
	 * @param from 
	 * @param to 
	 * @return list of the words as appear in the file starting from the given index an ending at the given index
	 */
	@Override
	@RequestMapping(value="/list", 
	method = RequestMethod.GET, 
	produces={"application/json"})
	public @ResponseBody List<String> listWords(
			@RequestParam(value="from", required=false, defaultValue="0") int from, 
			@RequestParam(value="to", required=false, defaultValue="-1") int to) {
		if (to == -1 || to > words.size()) {
			to = words.size();
		} else if ( to < -1) {
			from = 0;
			to = 0;
		}
		if (from < 0) {
			from = 0;
		} else if (from > words.size()) {
			from = words.size() -1;
			to = from;
		}
		if (from > to) {
			int temp = from;
			from = to;
			to = temp;
		}
		return words.subList(from, to);
	}
}
