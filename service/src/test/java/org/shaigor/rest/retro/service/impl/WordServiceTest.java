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

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.shaigor.rest.retro.service.IWordService;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

/**
 * For a good measure tests the REST web service that will need securing
 * @author Irena Shaigorodsky
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes={WordsSereviceTestConfig.class})
@Configurable
public class WordServiceTest {

	private static final int TEST_SAMPLE_SIZE = 235886;
	@Resource private IWordService service;
	
	
	@Test
	public void listWords() {
		List<String> result = service.listWords(0, TEST_SAMPLE_SIZE);
		assertResult(result, TEST_SAMPLE_SIZE);
	}

	@Test
	public void listWordsReverse() {
		List<String> result = service.listWords(TEST_SAMPLE_SIZE - 1, 0);
		assertResult(result, TEST_SAMPLE_SIZE - 1);
	}

	@Test
	public void listWordsDefault() {
		List<String> result = service.listWords(0, -1);
		assertResult(result, TEST_SAMPLE_SIZE);
	}

	@Test
	public void listWordsNegative() {
		List<String> result = service.listWords(-1, -7);
		assertResult(result, 0);
	}

	@Test
	public void listWordsToOver() {
		List<String> result = service.listWords(0, Integer.MAX_VALUE);
		assertResult(result, TEST_SAMPLE_SIZE);
	}

	@Test
	public void listWordsBeyond() {
		List<String> result = service.listWords(Integer.MAX_VALUE, Integer.MAX_VALUE);
		assertResult(result, 0);
	}

	@Test
	public void listWordsSubset() {
		List<String> result = service.listWords(10, 199);
		assertResult(result, 189);
	}

	private void assertResult(List<String> result, int size) {
		assertThat(result)
			.isNotNull();
		assertThat(result.size())
			.isEqualTo(size);
	}
}
