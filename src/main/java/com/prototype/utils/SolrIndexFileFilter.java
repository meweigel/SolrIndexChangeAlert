package com.prototype.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;

/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



/**
 * 
 * @author mweigel
 *
 *         The IndexFileFilter class is used to filter file access to Solr
 *         indexes
 */
public class SolrIndexFileFilter implements FileFilter {
	private static final HashMap<String, Boolean> hashMap;

	static {
		hashMap = new HashMap<String, Boolean>();
		for (String key : AppConstants.INDEX_FILE_EXTS) {
			hashMap.put(key, true);
		}
	}

	/**
	 * The accept method - Accepts or rejects a file based on its extension
	 */
	@Override
	public boolean accept(File file) {

		// If not a file we want to return true
		// so that no exception is thrown.
		boolean result = true;

		if (file.isFile()) {
			String name = file.getName();
			int i = name.indexOf('.') + 1;
			
			if (i > 0) {
				String key = name.substring(i);
				if (hashMap.containsKey(key)) {
					result = hashMap.get(key);
				}else{
					result = false;
				}
			}
		}

		return result;
	}
}