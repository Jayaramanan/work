package com.ni3.ag.navigator.server.graphXXL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ni3.ag.navigator.server.util.PrivateAccessor;
import junit.framework.TestCase;
import org.apache.log4j.Logger;

public class PreFilterObjectDataTest extends TestCase{
	public void testMultiValueToList(){
		Object[][] testDatas = new Object[][]{
				{"", new ArrayList<Integer>()},
				{null, new ArrayList<Integer>()},
				{"{{{", new ArrayList<Integer>()},
				{"}}}", new ArrayList<Integer>()},
				{"{}{}{}", new ArrayList<Integer>()},
				{"{a}{v}{d}", new ArrayList<Integer>()},
				{"{1}{2}{d}{}{}{{{}}{}{}}}}", Arrays.asList(1, 2)},
				{"{1}{2}{3}{4}{5}asdf", Arrays.asList(1, 2, 3, 4, 5)},
				{"{1}{2}{3}{4}{5}{{", Arrays.asList(1, 2, 3, 4, 5)},
				{"{1}{2}{3}{4}{5}}}", Arrays.asList(1, 2, 3, 4, 5)},
				{"a{1}{2}{3}{4}{5}", Arrays.asList(1, 2, 3, 4, 5)},
				{"{1}a{2}a{3}{4}{5}", Arrays.asList(1, 2, 3, 4, 5)},
				{"{1}{2}sdf{3}dfdf{4}{5}", Arrays.asList(1, 2, 3, 4, 5)},
				{"{1}{2}{3}{4}sdf{5}asdf", Arrays.asList(1, 2, 3, 4, 5)},
				{"{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf" +
						"{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf" +
						"{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf" +
						"{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf" +
						"{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf" +
						"{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf" +
						"{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf" +
						"{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf" +
						"{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf{1}{2}{3}{4}sdf{5}asdf",
						Arrays.asList(1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5,
								1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5,
								1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5,
								1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5,
								1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5,
								1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5,
								1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5,
								1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5,
								1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4, 5)}
		};
		for (Object[] testData : testDatas){
			List<Integer> result = (List<Integer>) PrivateAccessor.invokePrivateMethod(new PreFilterObjectData(), "multiValueToList", testData[0]);
			assertEquals(testData[1], result);
		}

		long startTime = System.currentTimeMillis();
		long iterations = 0;
		for (int i = 0; i < 10000; i++){
			for (Object[] testData : testDatas){
				List<Integer> result = (List<Integer>) PrivateAccessor.invokePrivateMethod(new PreFilterObjectData(), "multiValueToList", testData[0]);
				assertEquals(testData[1], result);
				iterations++;
			}
		}
		long time = System.currentTimeMillis() - startTime;
		Logger.getLogger(getClass()).info("parse multivalue iterations: " + iterations + " time: " + time + "ms (" + ((double) time / (double) iterations) + "ms/iteration)");
	}
}
