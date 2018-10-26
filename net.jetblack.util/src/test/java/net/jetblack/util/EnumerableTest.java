package net.jetblack.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import net.jetblack.util.invokables.BinaryFunction;
import net.jetblack.util.invokables.UnaryAction;
import net.jetblack.util.invokables.UnaryFunction;
import net.jetblack.util.selectors.IdentitySelector;
import net.jetblack.util.selectors.ToStringSelector;

class EnumerableTest {

	@Test
	public void testCreateArray() {
		Integer[] sourceArray = new Integer[]{1, 2, 3, 4, 5};
		Enumerable<Integer> enumerable = Enumerable.create(sourceArray);
		List<Integer> list = new ArrayList<Integer>();
		for (Integer value : enumerable) {
			list.add(value);
		}
		assertTrue(sourceArray.length == list.size());
		for (int i = 0; i < sourceArray.length; ++i) {
			assertEquals(sourceArray[i], list.get(i));
		}
	}

	@Test
	public void testCreateReverseArray() {
		Integer[] sourceArray = new Integer[]{1, 2, 3, 4, 5};
		Enumerable<Integer> enumerable = Enumerable.createReverse(sourceArray);
		List<Integer> list = new ArrayList<Integer>();
		for (Integer value : enumerable) {
			list.add(value);
		}
		assertTrue(sourceArray.length == list.size());
		for (int i = 0, j = sourceArray.length - 1; i < sourceArray.length; ++i, --j) {
			assertEquals(sourceArray[i], list.get(j));
		}
	}
	
	@Test
	public void testCreateIterator() {
		List<Integer> sourceList = Arrays.asList(1, 2, 3, 4, 5);
		Enumerable<Integer> enumerable = Enumerable.create(sourceList);
		List<Integer> list = new ArrayList<Integer>();
		for (Integer value : enumerable) {
			list.add(value);
		}
		assertTrue(sourceList.size() == list.size());
		for (int i = 0; i < sourceList.size(); ++i) {
			assertEquals(sourceList.get(i), list.get(i));
		}
	}

	@Test
	public void testCreateReverseListIterator() {
		List<Integer> sourceList = Arrays.asList(1, 2, 3, 4, 5);
		Enumerable<Integer> enumerable = Enumerable.createReverse(sourceList.listIterator());
		List<Integer> list = new ArrayList<Integer>();
		for (Integer value : enumerable) {
			list.add(value);
		}
		assertTrue(sourceList.size() == list.size());
		for (int i = 0, j = sourceList.size() - 1; i < sourceList.size(); ++i, --j) {
			assertEquals(sourceList.get(i), list.get(j));
		}
	}
	
	@Test
	public void testCreateMap() {
		Map<String,Integer> sourceMap = new HashMap<String,Integer>();
		sourceMap.put("One", 1);
		sourceMap.put("Two", 2);
		sourceMap.put("Three", 3);
		Enumerable<Map.Entry<String,Integer>> enumerable = Enumerable.create(sourceMap);
		Map<String,Integer> resultMap = new HashMap<String,Integer>();
		for (Map.Entry<String,Integer> entry : enumerable) {
			resultMap.put(entry.getKey(), entry.getValue());
		}
		for (String sourceKey : sourceMap.keySet()) {
			assertTrue(resultMap.containsKey(sourceKey));
		}
		for (String resultKey : resultMap.keySet()) {
			assertTrue(sourceMap.containsKey(resultKey));
		}
		for (Map.Entry<String, Integer> sourceEntry : sourceMap.entrySet()) {
			assertEquals(resultMap.get(sourceEntry.getKey()), sourceEntry.getValue());
		}
	}
	
	@Test
	public void testSelectIdentity() {
		Integer[] sourceArray = new Integer[]{1, 2, 3, 4, 5};
		int i = 0;
		for (Integer value : Enumerable.create(sourceArray).select(new IdentitySelector<Integer>())) {
			assertEquals(sourceArray[i++], value);
		}
	}
	
	@Test
	public void testSelectProject() {
		Integer[] sourceArray = new Integer[]{1, 2, 3, 4, 5};
		int i = 0;
		for (String value : Enumerable.create(sourceArray).select(new ToStringSelector<Integer>())) {
			assertEquals(sourceArray[i++].toString(), value);
		}
	}
	
	@Test
	public void testWhereAllTrue() {
		Integer[] sourceArray = new Integer[]{1, 2, 3, 4, 5};
		Enumerable<Integer> enumerable = Enumerable.create(sourceArray).where(new UnaryFunction<Integer, Boolean>() {

			@Override public Boolean invoke(Integer arg) {
				return true;
			}
			
		});
		assertArrayEquals(sourceArray, enumerable.toList().toArray(new Integer[] {}));
	}
	
	@Test
	public void testWhereAllFalse() {
		Integer[] sourceArray = new Integer[]{1, 2, 3, 4, 5};
		Enumerable<Integer> enumerable = Enumerable.create(sourceArray).where(new UnaryFunction<Integer, Boolean>() {

			@Override public Boolean invoke(Integer arg) {
				return false;
			}
			
		});
		assertFalse(enumerable.hasNext());
	}
	
	@Test
	public void testWhereFirstTrue() {
		Integer[] sourceArray = new Integer[]{1, 2, 3, 4, 5};
		Enumerable<Integer> enumerable = Enumerable.create(sourceArray).where(new UnaryFunction<Integer, Boolean>() {

			@Override public Boolean invoke(Integer arg) {
				return arg == 1;
			}
			
		});
		List<Integer> resultList = enumerable.toList();
		assertEquals(resultList.size(), 1);
		assertEquals(resultList.get(0).intValue(), 1);
	}
	
	@Test
	public void testWhereMiddleTrue() {
		Integer[] sourceArray = new Integer[]{1, 2, 3, 4, 5};
		Enumerable<Integer> enumerable = Enumerable.create(sourceArray).where(new UnaryFunction<Integer, Boolean>() {

			@Override public Boolean invoke(Integer arg) {
				return arg == 3;
			}
			
		});
		List<Integer> resultList = enumerable.toList();
		assertEquals(resultList.size(), 1);
		assertEquals(resultList.get(0).intValue(), 3);
	}
	
	@Test
	public void testWhereLastTrue() {
		Integer[] sourceArray = new Integer[]{1, 2, 3, 4, 5};
		Enumerable<Integer> enumerable = Enumerable.create(sourceArray).where(new UnaryFunction<Integer, Boolean>() {

			@Override public Boolean invoke(Integer arg) {
				return arg == 5;
			}
			
		});
		List<Integer> resultList = enumerable.toList();
		assertEquals(resultList.size(), 1);
		assertEquals(resultList.get(0).intValue(), 5);
	}
	
	@Test
	public void testWhereFirstAndLastTrue() {
		Integer[] sourceArray = new Integer[]{1, 2, 3, 4, 5};
		Enumerable<Integer> enumerable = Enumerable.create(sourceArray).where(new UnaryFunction<Integer, Boolean>() {

			@Override public Boolean invoke(Integer arg) {
				return arg == 1 || arg == 5;
			}
			
		});
		List<Integer> resultList = enumerable.toList();
		assertEquals(resultList.size(), 2);
		assertEquals(resultList.get(0).intValue(), 1);
		assertEquals(resultList.get(1).intValue(), 5);
	}
	
	@Test
	public void testWhereFirstAndLastFalse() {
		Integer[] sourceArray = new Integer[]{1, 2, 3, 4, 5};
		Enumerable<Integer> enumerable = Enumerable.create(sourceArray).where(new UnaryFunction<Integer, Boolean>() {

			@Override public Boolean invoke(Integer arg) {
				return !(arg == 1 || arg == 5);
			}
			
		});
		List<Integer> resultList = enumerable.toList();
		assertEquals(resultList.size(), 3);
		assertEquals(resultList.get(0).intValue(), 2);
		assertEquals(resultList.get(1).intValue(), 3);
		assertEquals(resultList.get(2).intValue(), 4);
	}
	
	@Test
	public void testAllOnEmpty() {
		Integer[] sourceArray = new Integer[] {};
		assertTrue(Enumerable.create(sourceArray).all(new UnaryFunction<Integer,Boolean>() {
			@Override public Boolean invoke(Integer arg) {
				return false;
			}
		}));
	}
	
	@Test
	public void testAllOnOneTrue() {
		Integer[] sourceArray = new Integer[] {1};
		assertTrue(Enumerable.create(sourceArray).all(new UnaryFunction<Integer,Boolean>() {
			@Override public Boolean invoke(Integer arg) {
				return arg == 1;
			}
		}));
	}
	
	@Test
	public void testAllOnOneFalse() {
		Integer[] sourceArray = new Integer[] {1};
		assertFalse(Enumerable.create(sourceArray).all(new UnaryFunction<Integer,Boolean>() {
			@Override public Boolean invoke(Integer arg) {
				return arg != 1;
			}
		}));
	}
	
	@Test
	public void testAllOnManyTrue() {
		Integer[] sourceArray = new Integer[] {1, 2, 3, 4};
		assertTrue(Enumerable.create(sourceArray).all(new UnaryFunction<Integer,Boolean>() {
			@Override public Boolean invoke(Integer arg) {
				return arg > 0;
			}
		}));
	}
	
	@Test
	public void testAllOnManyFalse() {
		Integer[] sourceArray = new Integer[] {1, 2, 3, 4};
		assertFalse(Enumerable.create(sourceArray).all(new UnaryFunction<Integer,Boolean>() {
			@Override public Boolean invoke(Integer arg) {
				return arg < 0;
			}
		}));
	}
	
	@Test
	public void testAllOnSomeFalse() {
		Integer[] sourceArray = new Integer[] {1, 2, 3, 4};
		assertFalse(Enumerable.create(sourceArray).all(new UnaryFunction<Integer,Boolean>() {
			@Override public Boolean invoke(Integer arg) {
				return arg < 3;
			}
		}));
	}
	
	@Test
	public void testAnyOnEmpty() {
		Integer[] sourceArray = new Integer[] {};
		assertFalse(Enumerable.create(sourceArray).any(new UnaryFunction<Integer,Boolean>() {
			@Override public Boolean invoke(Integer arg) {
				return false;
			}
		}));
	}
	
	@Test
	public void testAnyOnOneTrue() {
		Integer[] sourceArray = new Integer[] {1};
		assertTrue(Enumerable.create(sourceArray).any(new UnaryFunction<Integer,Boolean>() {
			@Override public Boolean invoke(Integer arg) {
				return arg == 1;
			}
		}));
	}
	
	@Test
	public void testAnyOnOneFalse() {
		Integer[] sourceArray = new Integer[] {1};
		assertFalse(Enumerable.create(sourceArray).any(new UnaryFunction<Integer,Boolean>() {
			@Override public Boolean invoke(Integer arg) {
				return arg != 1;
			}
		}));
	}
	
	@Test
	public void testAnyOnManyTrue() {
		Integer[] sourceArray = new Integer[] {1, 2, 3, 4};
		assertTrue(Enumerable.create(sourceArray).any(new UnaryFunction<Integer,Boolean>() {
			@Override public Boolean invoke(Integer arg) {
				return arg == 3;
			}
		}));
	}
	
	@Test
	public void testAnyOnManyFalse() {
		Integer[] sourceArray = new Integer[] {1, 2, 3, 4};
		assertFalse(Enumerable.create(sourceArray).any(new UnaryFunction<Integer,Boolean>() {
			@Override public Boolean invoke(Integer arg) {
				return arg < 0;
			}
		}));
	}
	
	@Test
	public void testCreateDepthFirst() {
		TestNode root = new TestNode("root");
		TestNode a = root.add("a");
		TestNode aa = a.add("aa");
		TestNode ab = a.add("ab");
		TestNode aaa = aa.add("aaa");
		TestNode aab = aa.add("aab");
		TestNode b = root.add("b");
		TestNode ba = b.add("ba");
		TestNode bb = b.add("bb");
		
		Enumerable<String> enumerator = Enumerable.createDepthFirst(root, new UnaryFunction<TestNode, Enumerable<TestNode>>() {

			@Override
			public Enumerable<TestNode> invoke(TestNode arg) {
				return Enumerable.create(arg.getChildren());
			}
			
		}, true).select(new UnaryFunction<TestNode,String>() {

			@Override
			public String invoke(TestNode arg) {
				return arg.getValue();
			}
			
		});
		
		String[] expected = new String[] {"root", "a", "aa", "aaa", "aab", "ab", "b", "ba", "bb"};
		assertTrue(enumerator.sequenceEquals(expected));
	}
	
	@Test
	public void testCreateBreadthFirst() {
		TestNode root = new TestNode("root");
		TestNode a = root.add("a");
		TestNode aa = a.add("aa");
		TestNode ab = a.add("ab");
		TestNode aaa = aa.add("aaa");
		TestNode aab = aa.add("aab");
		TestNode b = root.add("b");
		TestNode ba = b.add("ba");
		TestNode bb = b.add("bb");
		
		Enumerable<String> enumerator = Enumerable.createBreadthFirst(root, new UnaryFunction<TestNode, Enumerable<TestNode>>() {

			@Override
			public Enumerable<TestNode> invoke(TestNode arg) {
				return Enumerable.create(arg.getChildren());
			}
			
		}, true).select(new UnaryFunction<TestNode,String>() {

			@Override
			public String invoke(TestNode arg) {
				return arg.getValue();
			}
			
		});
		
		String[] expected = new String[] {"root", "a", "b", "aa", "ab", "ba", "bb", "aaa", "aab"};
		assertTrue(enumerator.sequenceEquals(expected));
	}
	
	@Test
	public void testSkipNone() {
		assertTrue(Enumerable.create(new Integer[] {1, 2, 3, 4}).skip(0).sequenceEquals(new Integer[] {1, 2, 3, 4}));
	}
	
	@Test
	public void testSkipSome() {
		assertTrue(Enumerable.create(new Integer[] {1, 2, 3, 4}).skip(2).sequenceEquals(new Integer[] {3, 4}));
	}
	
	@Test
	public void testSkipAll() {
		assertEquals(0, Enumerable.create(new Integer[] {1, 2, 3, 4}).skip(4).size());
	}
	
	@Test
	public void testForEach() {
		Integer[] array = new Integer[] {1, 2, 3};
		Enumerable<Integer> e = Enumerable.create(array);
		final int results[] = new int[] { 0, 0 };
		e.forEach(new UnaryAction<Integer>() {

			@Override
			public void invoke(Integer arg) {
				
				results[0] = results[0] + arg.intValue();
			}
			
		});
		for (int i = 0; i < array.length; ++i) {
			results[1] = results[1] + array[i];
		}
		
		assertEquals(results[0], results[1]);
	}

	@Test
	public void testBuffer() {
		Integer[] array = new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		final int blockSize = 4;
		List<Collection<Integer>> buffered = Enumerable.create(array).buffer(blockSize).select(new UnaryFunction<Enumerable<Integer>,Collection<Integer>>() {
			@Override public Collection<Integer> invoke(Enumerable<Integer> arg) {
				return arg.toList();
			}
		}).toList();
		
		Integer count = Enumerable.create(buffered).aggregate(Integer.valueOf(0), new BinaryFunction<Collection<Integer>, Integer, Integer>() {

			@Override
			public Integer invoke(Collection<Integer> arg1, Integer arg2) {
				return arg1.size() + arg2;
			}
			
		});
		assertEquals(array.length, count.intValue());
		assertEquals(array.length / blockSize + (array.length % blockSize == 0 ? 0 : 1), buffered.size());
		assertEquals(blockSize, buffered.get(0).size());
		assertEquals(array.length % blockSize, buffered.get(buffered.size()-1).size());
	}

	@Test
	public void testDistinct() {
		Integer[] sourceArray = new Integer[] {1, 2, 1, 2, 2, 3, 1, 2, 3, 3};
		List<Integer> resultList = Enumerable.create(sourceArray).distinct(new Comparator<Integer>() {

			@Override
			public int compare(Integer arg0, Integer arg1) {
				return arg0 - arg1;
			}
			
		}).toList();
		assertEquals(3, resultList.size());
		assertTrue(resultList.contains(1));
		assertTrue(resultList.contains(2));
		assertTrue(resultList.contains(3));
		
	}
	
	@Test
	public void testSort() {
		assertTrue(Enumerable.create(new Integer[] {7, 3, 2, 1, 9, 4, 5, 6, 8}).sort(new Comparator<Integer>() {

			@Override
			public int compare(Integer arg0, Integer arg1) {
				return arg0 - arg1;
			}
			
		}).sequenceEquals(new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9}));
	}
}
