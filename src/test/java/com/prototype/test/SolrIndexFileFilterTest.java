package com.prototype.test;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import com.prototype.utils.SolrIndexFileFilter;

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
 *         A JUNIT test class for the SolrIndexFileFilterTest
 */
public class SolrIndexFileFilterTest {

	private static final String FALSE_FILES_PATH = "/src/test/resources/falseIndexFiles";
	private static final String TRUE_FILES_PATH = "/src/test/resources/trueIndexFiles";
	private static File[] falseFileArray;
	private static File[] trueFileArray;

	@Before
	public void setUp() {
		StringBuilder builder = new StringBuilder(System.getProperty("user.dir"));
		builder.append(FALSE_FILES_PATH);
		String falseFilesPath = builder.toString();
		File directory = new File(falseFilesPath);
		falseFileArray = directory.listFiles();

		builder = new StringBuilder(System.getProperty("user.dir"));
		builder.append(TRUE_FILES_PATH);
		String trueFilesPath = builder.toString();
		directory = new File(trueFilesPath);
		trueFileArray = directory.listFiles();
	}

	/**
	 * Test the acceptability of files as index files
	 */
	@Test
	public void testAcceptTrueIndexFiles() {
		SolrIndexFileFilter solrIndexFileFilter = new SolrIndexFileFilter();
		for (File file : trueFileArray) {
			assertTrue(solrIndexFileFilter.accept(file));
		}
	}

	/**
	 * Test the unacceptability of files as index files
	 */
	@Test
	public void testRejectFalseIndexFiles() {
		SolrIndexFileFilter solrIndexFileFilter = new SolrIndexFileFilter();
		for (File file : falseFileArray) {
			assertFalse(solrIndexFileFilter.accept(file));
		}
	}
}