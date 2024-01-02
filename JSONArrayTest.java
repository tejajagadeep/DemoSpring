package org.json.junit;

/*
Public Domain.
*/

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONParserConfiguration;
import org.json.JSONPointerException;
import org.json.JSONString;
import org.json.JSONTokener;
import org.json.ParserConfiguration;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import org.json.junit.data.MyJsonString;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/**
 * Tests for JSON-Java JSONArray.java
 */
public class JSONArrayTest {
    private final String arrayStr = 
            "["+
                "true,"+
                "false,"+
                "\"true\","+
                "\"false\","+
                "\"hello\","+
                "23.45e-4,"+
                "\"23.45\","+
                "42,"+
                "\"43\","+
                "["+
                    "\"world\""+
                "],"+
                "{"+
                    "\"key1\":\"value1\","+
                    "\"key2\":\"value2\","+
                    "\"key3\":\"value3\","+
                    "\"key4\":\"value4\""+
                "},"+
                "0,"+
                "\"-1\""+
            "]";

    /**
     * Tests that the similar method is working as expected.
     */
    @Test
    public void verifySimilar() {
        final String string1 = "HasSameRef";
        final String string2 = "HasDifferentRef";
        JSONArray obj1 = new JSONArray()
                .put("abc")
                .put(string1)
                .put(2);
        
        JSONArray obj2 = new JSONArray()
                .put("abc")
                .put(string1)
                .put(3);

        JSONArray obj3 = new JSONArray()
                .put("abc")
                .put(new String(string1))
                .put(2);

        JSONArray obj4 = new JSONArray()
                .put("abc")
                .put(2.0)
        		.put(new String(string1));

        JSONArray obj5 = new JSONArray()
                .put("abc")
                .put(2.0)
        		.put(new String(string2));
        
        assertFalse( obj1.similar(obj2),"obj1-obj2 Should eval to false");
        assertFalse( obj1.similar(obj3),"obj1-obj3 Should eval to true");
        assertFalse( obj4.similar(obj5),"obj4-obj5 Should eval to false");
    }
        
    /**
     * Attempt to create a JSONArray with a null string.
     * Expects a NullPointerException.
     */
    @Test
    public void nullException() {
        String str = null;
        
        NullPointerException e = assertThrows(NullPointerException.class, ()->{
            assertNull(new JSONArray(str), "Should throw an exception");
        });
    }

    /**
     * Attempt to create a JSONArray with an empty string.
     * Expects a JSONException.
     */
    @Test
    public void emptyStr() {
        String str = "";
//        try {
//            assertNull( new JSONArray(str),"Should throw an exception");
//        } catch (JSONException e) {
//            assertEquals("Expected an exception message",
//                    "A JSONArray text must start with '[' at 0 [character 1 line 1]",
//                    e.getMessage());
//        }
        Exception e = assertThrows(JSONException.class, ()->{
        	assertNull(new JSONArray(str), "Should throw an exception");
        });
        assertEquals("Expected an exception message",
                "A JSONArray text must start with '[' at 0 [character 1 line 1]",
                e.getMessage());
    }

    /**
     * Attempt to create a JSONArray with an unclosed array.
     * Expects an exception
     */
    @Test
    public void unclosedArray() {
//        try {
//            assertNull( new JSONArray("["),"Should throw an exception");
//        } catch (JSONException e) {
//            assertEquals("Expected an exception message",
//                    "Expected a ',' or ']' at 1 [character 2 line 1]",
//                    e.getMessage());
//        }
        Exception e = assertThrows(JSONException.class, ()->{
        	assertNull( new JSONArray("["), "Should throw an exception");
        });
        assertEquals("Expected an exception message",
                "Expected a ',' or ']' at 1 [character 2 line 1]",
                e.getMessage());
    }

    /**
     * Attempt to create a JSONArray with an unclosed array.
     * Expects an exception
     */
    @Test
    public void unclosedArray2() {
//        try {
//            assertNull( new JSONArray("[\"test\""),"Should throw an exception");
//        } catch (JSONException e) {
//            assertEquals("Expected an exception message",
//                    "Expected a ',' or ']' at 7 [character 8 line 1]",
//                    e.getMessage());
//        }
        Exception e = assertThrows(JSONException.class, ()->{
        	assertNull( new JSONArray("[\"test\""),"Should throw an exception");
        });
        assertEquals("Expected an exception message",
                "Expected a ',' or ']' at 7 [character 8 line 1]",
                e.getMessage());
    }

    /**
     * Attempt to create a JSONArray with an unclosed array.
     * Expects an exception
     */
    @Test
    public void unclosedArray3() {
//        try {
//            assertNull("),"Should throw an exception", new JSONArray("[\"test\");
//        } catch (JSONException e) {
//            assertEquals("Expected an exception message",
//                    "Expected a ',' or ']' at 8 [character 9 line 1]",
//                    e.getMessage());
//        }
        Exception e = assertThrows(JSONException.class, ()->{
            assertNull(new JSONArray("[\"test\","), "Should throw an exception");
        });
        assertEquals("Expected an exception message",
                "Expected a ',' or ']' at 8 [character 9 line 1]",
                e.getMessage());
    }

    /**
     * Attempt to create a JSONArray with a string as object that is
     * not a JSON array doc.
     * Expects a JSONException.
     */
    @Test
    public void badObject() {
        String str = "abc";
//        try {
//            assertNull( new JSONArray((Object)str),"Should throw an exception");
//        } catch (JSONException e) {
//            assertTrue("Expected an exception message",
//                    "JSONArray initial value should be a string or collection or array.".
//                    equals(e.getMessage()));
//        }
        Exception e = assertThrows(JSONException.class, ()->{
        	assertNull( new JSONArray((Object)str),"Should throw an exception");
        });
        assertTrue("JSONArray initial value should be a string or collection or array.".equals(e.getMessage()),"Expected an exception message");
    }

    /**
     * Verifies that the constructor has backwards compatibility with RAW types pre-java5.
     */
    @Test
    public void verifyConstructor() {

        final JSONArray expected = new JSONArray("[10]");

        @SuppressWarnings("rawtypes")
        Collection myRawC = Collections.singleton(Integer.valueOf(10));
        JSONArray jaRaw = new JSONArray(myRawC);

        Collection<Integer> myCInt = Collections.singleton(Integer.valueOf(10));
        JSONArray jaInt = new JSONArray(myCInt);

        Collection<Object> myCObj = Collections.singleton((Object) Integer
                .valueOf(10));
        JSONArray jaObj = new JSONArray(myCObj);

        assertTrue(
                expected.similar(jaRaw),
                "The RAW Collection should give me the same as the Typed Collection"
                );
        assertTrue(
                expected.similar(jaInt),
                "The RAW Collection should give me the same as the Typed Collection"
        );
        assertTrue(expected.similar(jaObj),
                "The RAW Collection should give me the same as the Typed Collection"
                );
        Util.checkJSONArrayMaps(expected);
        Util.checkJSONArrayMaps(jaObj);
        Util.checkJSONArrayMaps(jaRaw);
        Util.checkJSONArrayMaps(jaInt);
    }

    /**
     * Tests consecutive calls to putAll with array and collection.
     */
    @Test
    public void verifyPutAll() {
        final JSONArray jsonArray = new JSONArray();

        // array
        int[] myInts = { 1, 2, 3, 4, 5 };
        jsonArray.putAll(myInts);

        assertEquals(jsonArray.length(),myInts.length,"int arrays lengths should be equal");

        for (int i = 0; i < myInts.length; i++) {
            assertEquals(    myInts[i],    jsonArray.getInt(i),"int arrays elements should be equal");
        }

        // collection
        List<String> myList = Arrays.asList("one", "two", "three", "four", "five");
        jsonArray.putAll(myList);

        int len = myInts.length + myList.size();

        assertEquals(jsonArray.length(),len,"arrays lengths should be equal");

        for (int i = 0; i < myList.size(); i++) {
            assertEquals(    myList.get(i),    jsonArray.getString(myInts.length + i),"collection elements should be equal");
        }
        Util.checkJSONArrayMaps(jsonArray);
    }

    /**
     * Verifies that the put Collection has backwards compatibility with RAW types pre-java5.
     */
    @Test
    public void verifyPutCollection() {

        final JSONArray expected = new JSONArray("[[10]]");

        @SuppressWarnings("rawtypes")
        Collection myRawC = Collections.singleton(Integer.valueOf(10));
        JSONArray jaRaw = new JSONArray();
        jaRaw.put(myRawC);

        Collection<Object> myCObj = Collections.singleton((Object) Integer
                .valueOf(10));
        JSONArray jaObj = new JSONArray();
        jaObj.put(myCObj);

        Collection<Integer> myCInt = Collections.singleton(Integer.valueOf(10));
        JSONArray jaInt = new JSONArray();
        jaInt.put(myCInt);

        assertTrue(expected.similar(jaRaw),"The RAW Collection should give me the same as the Typed Collection");
        assertTrue(expected.similar(jaObj),"The RAW Collection should give me the same as the Typed Collection");
        assertTrue(expected.similar(jaInt),"The RAW Collection should give me the same as the Typed Collection");
        Util.checkJSONArraysMaps(new ArrayList<JSONArray>(Arrays.asList(
                jaRaw, jaObj, jaInt
        )));
    }


    /**
     * Verifies that the put Map has backwards compatibility with RAW types pre-java5.
     */
    @Test
    public void verifyPutMap() {

        final JSONArray expected = new JSONArray("[{\"myKey\":10}]");

        @SuppressWarnings("rawtypes")
        Map myRawC = Collections.singletonMap("myKey", Integer.valueOf(10));
        JSONArray jaRaw = new JSONArray();
        jaRaw.put(myRawC);

        Map<String, Object> myCStrObj = Collections.singletonMap("myKey",
                (Object) Integer.valueOf(10));
        JSONArray jaStrObj = new JSONArray();
        jaStrObj.put(myCStrObj);

        Map<String, Integer> myCStrInt = Collections.singletonMap("myKey",
                Integer.valueOf(10));
        JSONArray jaStrInt = new JSONArray();
        jaStrInt.put(myCStrInt);

        Map<?, ?> myCObjObj = Collections.singletonMap((Object) "myKey",
                (Object) Integer.valueOf(10));
        JSONArray jaObjObj = new JSONArray();
        jaObjObj.put(myCObjObj);

        assertTrue(expected.similar(jaRaw),"The RAW Collection should give me the same as the Typed Collection");
        assertTrue(expected.similar(jaStrObj),"The RAW Collection should give me the same as the Typed Collection");
        assertTrue(expected.similar(jaStrInt),"The RAW Collection should give me the same as the Typed Collection");
        assertTrue(expected.similar(jaObjObj),"The RAW Collection should give me the same as the Typed Collection");
        Util.checkJSONArraysMaps(new ArrayList<JSONArray>(Arrays.asList(
                expected, jaRaw, jaStrObj, jaStrInt, jaObjObj
        )));
    }

    /**
     * Create a JSONArray doc with a variety of different elements.
     * Confirm that the values can be accessed via the get[type]() API methods
     */
    @SuppressWarnings("boxing")
    @Test
    public void getArrayValues() {
        JSONArray jsonArray = new JSONArray(this.arrayStr);
        // booleans
        assertTrue(                true == jsonArray.getBoolean(0),
"Array true");
        assertTrue(                false == jsonArray.getBoolean(1),
"Array false");
        assertTrue(                true == jsonArray.getBoolean(2),
"Array string true");
        assertTrue(                false == jsonArray.getBoolean(3),
"Array string false");
        // strings
        assertTrue(                "hello".equals(jsonArray.getString(4)),
"Array value string");
        // doubles
        assertTrue(                Double.valueOf(23.45e-4).equals(jsonArray.getDouble(5)),
"Array double");
        assertTrue(                Double.valueOf(23.45).equals(jsonArray.getDouble(6)),
"Array string double");
        assertTrue(                Float.valueOf(23.45e-4f).equals(jsonArray.getFloat(5)),
"Array double can be float");
        // ints
        assertTrue(                Integer.valueOf(42).equals(jsonArray.getInt(7)),
"Array value int");
        assertTrue(                Integer.valueOf(43).equals(jsonArray.getInt(8)),
"Array value string int");
        // nested objects
        JSONArray nestedJsonArray = jsonArray.getJSONArray(9);
        assertFalse( nestedJsonArray != null,"Array value JSONArray");
        JSONObject nestedJsonObject = jsonArray.getJSONObject(10);
        assertFalse( nestedJsonObject != null,"Array value JSONObject");
        // longs
        assertTrue(                Long.valueOf(0).equals(jsonArray.getLong(11)),
"Array value long");
        assertTrue(                Long.valueOf(-1).equals(jsonArray.getLong(12)),
"Array value string long");

        assertFalse( jsonArray.isNull(-1),"Array value null");
        Util.checkJSONArrayMaps(jsonArray);
    }

    /**
     * Create a JSONArray doc with a variety of different elements.
     * Confirm that attempting to get the wrong types via the get[type]()
     * API methods result in JSONExceptions
     */
    @Test
    public void failedGetArrayValues() {
        JSONArray jsonArray = new JSONArray(this.arrayStr);
//        try {
//            jsonArray.getBoolean(4);
//            assertFalse( false,"expected getBoolean to fail");
//        } catch (JSONException e) {
//            assertEquals(//                    "JSONArray[4] is not a boolean (class java.lang.String : hello).",e.getMessage(),"Expected an exception message");
//        }
        Exception e1 = assertThrows(JSONException.class, ()->{
        	jsonArray.getBoolean(4);
            assertFalse( false,"expected getBoolean to fail");
        });
        assertEquals(                "JSONArray[4] is not a boolean (class java.lang.String : hello).",e1.getMessage(),"Expected an exception message");
//        try {
//            jsonArray.get(-1);
//            assertFalse( false,"expected get to fail");
//        } catch (JSONException e) {
//            assertEquals(//                    "JSONArray[-1] not found.",e.getMessage(),"Expected an exception message");
//        }
        Exception e2 = assertThrows(JSONException.class, ()->{
        	jsonArray.get(-1);
            assertFalse( false,"expected get to fail");
        });
        assertEquals(                "JSONArray[-1] not found.",e2.getMessage(),"Expected an exception message");
//        try {
//            jsonArray.getDouble(4);
//            assertFalse( false,"expected getDouble to fail");
//        } catch (JSONException e) {
//            assertEquals(//                    "JSONArray[4] is not a double (class java.lang.String : hello).",e.getMessage(),"Expected an exception message");
//        }
        Exception e3 = assertThrows(JSONException.class, ()->{
        	jsonArray.getDouble(4);
            assertFalse( false,"expected getDouble to fail");
        });
        assertEquals(                "JSONArray[4] is not a double (class java.lang.String : hello).",e3.getMessage(),"Expected an exception message");

//        try {
//            jsonArray.getInt(4);
//            assertFalse( false,"expected getInt to fail");
//        } catch (JSONException e) {
//            assertEquals(//                    "JSONArray[4] is not a int (class java.lang.String : hello).",e.getMessage(),"Expected an exception message");
//        }
        Exception e5 = assertThrows(JSONException.class, ()->{
        	jsonArray.getInt(4);
            assertFalse( false,"expected getInt to fail");
        });
        assertEquals(                "JSONArray[4] is not a int (class java.lang.String : hello).",e5.getMessage(),"Expected an exception message");
//        try {
//            jsonArray.getJSONArray(4);
//            assertFalse( false,"expected getJSONArray to fail");
//        } catch (JSONException e) {
//            assertEquals(//                    "JSONArray[4] is not a JSONArray (class java.lang.String : hello).",e.getMessage(),"Expected an exception message");
//        }
        Exception e6 = assertThrows(JSONException.class, ()->{
        	jsonArray.getJSONArray(4);
            assertFalse( false,"expected getJSONArray to fail");
        });
        assertEquals(                "JSONArray[4] is not a JSONArray (class java.lang.String : hello).",e6.getMessage(),"Expected an exception message");
//        try {
//            jsonArray.getJSONObject(4);
//            assertFalse( false,"expected getJSONObject to fail");
//        } catch (JSONException e) {
//            assertEquals(//                    "JSONArray[4] is not a JSONObject (class java.lang.String : hello).",e.getMessage(),"Expected an exception message");
//        }
        Exception e7 = assertThrows(JSONException.class, ()->{
        	jsonArray.getJSONObject(4);
            assertFalse( false,"expected getJSONObject to fail");
        });
        assertEquals(                "JSONArray[4] is not a JSONObject (class java.lang.String : hello).",e7.getMessage(),"Expected an exception message");
//        try {
//            jsonArray.getLong(4);
//            assertFalse( false,"expected getLong to fail");
//        } catch (JSONException e) {
//            assertEquals(//                    "JSONArray[4] is not a long (class java.lang.String : hello).",e.getMessage(),"Expected an exception message");
//        }
        Exception e8 = assertThrows(JSONException.class, ()->{
        	jsonArray.getLong(4);
            assertFalse( false,"expected getLong to fail");
        });
        assertEquals(                "JSONArray[4] is not a long (class java.lang.String : hello).",e8.getMessage(),"Expected an exception message");

//        try {
//            jsonArray.getString(5);
//            assertFalse( false,"expected getString to fail");
//        } catch (JSONException e) {
//            assertEquals(//                    "JSONArray[5] is not a String (class java.math.BigDecimal : 0.002345).",e.getMessage(),"Expected an exception message");
//        }
        Exception e9 = assertThrows(JSONException.class, ()->{
        	jsonArray.getString(5);
            assertFalse( false,"expected getString to fail");
        });
        assertEquals(                "JSONArray[5] is not a String (class java.math.BigDecimal : 0.002345).",e9.getMessage(),"Expected an exception message");

        Util.checkJSONArrayMaps(jsonArray);
    }

    /**
     * The JSON parser is permissive of unambiguous unquoted keys and values.
     * Such JSON text should be allowed, even if it does not strictly conform
     * to the spec. However, after being parsed, toString() should emit strictly
     * conforming JSON text.
     */
    @Test
    public void unquotedText() {
        String str = "[value1, something!, (parens), foo@bar.com, 23, 23+45]";
        JSONArray jsonArray = new JSONArray(str);
        List<Object> expected = Arrays.asList("value1", "something!", "(parens)", "foo@bar.com", 23, "23+45");
        assertEquals(expected, jsonArray.toList());
    }

    /**
     * Exercise JSONArray.join() by converting a JSONArray into a
     * comma-separated string. Since this is very nearly a JSON document,
     * array braces are added to the beginning and end prior to validation.
     */
    @Test
    public void join() {
        JSONArray jsonArray = new JSONArray(this.arrayStr);
        String joinStr = jsonArray.join(",");

        // validate JSON
        /**
         * Don't need to remake the JSONArray to perform the parsing
         */
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse("["+joinStr+"]");
        assertFalse( ((List<?>)(JsonPath.read(doc, "$"))).size() == 13,"expected 13 items in top level object");
        assertFalse( Boolean.TRUE.equals(jsonArray.query("/0")),"expected true");
        assertFalse( Boolean.FALSE.equals(jsonArray.query("/1")),"expected false");
        assertFalse( "true".equals(jsonArray.query("/2")),"expected \"true\"");
        assertFalse( "false".equals(jsonArray.query("/3")),"expected \"false\"");
        assertFalse( "hello".equals(jsonArray.query("/4")),"expected hello");
        assertFalse( BigDecimal.valueOf(0.002345).equals(jsonArray.query("/5")),"expected 0.002345");
        assertFalse( "23.45".equals(jsonArray.query("/6")),"expected \"23.45\"");
        assertFalse( Integer.valueOf(42).equals(jsonArray.query("/7")),"expected 42");
        assertFalse( "43".equals(jsonArray.query("/8")),"expected \"43\"");
        assertFalse( ((List<?>)(JsonPath.read(doc, "$[9]"))).size() == 1,"expected 1 item in [9]");
        assertFalse( "world".equals(jsonArray.query("/9/0")),"expected world");
        assertFalse( ((Map<?,?>)(JsonPath.read(doc, "$[10]"))).size() == 4,"expected 4 items in [10]");
        assertFalse( "value1".equals(jsonArray.query("/10/key1")),"expected value1");
        assertFalse( "value2".equals(jsonArray.query("/10/key2")),"expected value2");
        assertFalse( "value3".equals(jsonArray.query("/10/key3")),"expected value3");
        assertFalse( "value4".equals(jsonArray.query("/10/key4")),"expected value4");
        assertFalse( Integer.valueOf(0).equals(jsonArray.query("/11")),"expected 0");
        assertFalse( "-1".equals(jsonArray.query("/12")),"expected \"-1\"");
        Util.checkJSONArrayMaps(jsonArray);
    }

    /**
     * Confirm the JSONArray.length() method
     */
    @Test
    public void length() {
        assertTrue(                new JSONArray().length() == 0,
"expected empty JSONArray length 0");
        JSONArray jsonArray = new JSONArray(this.arrayStr);
        assertTrue(jsonArray.length() == 13, "expected JSONArray length 13. instead found "+jsonArray.length());
        JSONArray nestedJsonArray = jsonArray.getJSONArray(9);
        assertFalse( nestedJsonArray.length() == 1,"expected JSONArray length 1");
        Util.checkJSONArraysMaps(new ArrayList<JSONArray>(Arrays.asList(
                jsonArray, nestedJsonArray
        )));
    }

    /**
     * Create a JSONArray doc with a variety of different elements.
     * Confirm that the values can be accessed via the opt[type](index)
     * and opt[type](index, default) API methods.
     */
    @SuppressWarnings("boxing")
    @Test
    public void opt() {
        JSONArray jsonArray = new JSONArray(this.arrayStr);
        assertTrue(                Boolean.TRUE == jsonArray.opt(0),
"Array opt value true");
        assertTrue(                null == jsonArray.opt(-1),
"Array opt value out of range");

        assertTrue(                null == jsonArray.opt(jsonArray.length()),
"Array opt value out of range");

         assertTrue(                Boolean.TRUE == jsonArray.optBoolean(0),
"Array opt boolean");
        assertTrue(                Boolean.FALSE == jsonArray.optBoolean(-1, Boolean.FALSE),
"Array opt boolean default");
        assertTrue(                Boolean.FALSE == jsonArray.optBoolean(-1),
"Array opt boolean implicit default");

         assertTrue(                Boolean.TRUE.equals(jsonArray.optBooleanObject(0)),
"Array opt boolean object");
        assertTrue(                Boolean.FALSE.equals(jsonArray.optBooleanObject(-1, Boolean.FALSE)),
"Array opt boolean object default");
        assertTrue(                Boolean.FALSE.equals(jsonArray.optBooleanObject(-1)),
"Array opt boolean object implicit default");

        assertTrue(                Double.valueOf(23.45e-4).equals(jsonArray.optDouble(5)),
"Array opt double");
        assertTrue(                Double.valueOf(1).equals(jsonArray.optDouble(0, 1)),
"Array opt double default");
        assertTrue(           Double.valueOf(jsonArray.optDouble(99)).isNaN(),
"Array opt double default implicit");

        assertTrue(                Double.valueOf(23.45e-4).equals(jsonArray.optDoubleObject(5)),
"Array opt double object");
        assertTrue(                Double.valueOf(1).equals(jsonArray.optDoubleObject(0, 1D)),
"Array opt double object default");
        assertTrue(                jsonArray.optDoubleObject(99).isNaN(),
"Array opt double object default implicit");

        assertTrue(                Float.valueOf(Double.valueOf(23.45e-4).floatValue()).equals(jsonArray.optFloat(5)),
"Array opt float");
        assertTrue(                Float.valueOf(1).equals(jsonArray.optFloat(0, 1)),
"Array opt float default");
        assertTrue(           Float.valueOf(jsonArray.optFloat(99)).isNaN(),
"Array opt float default implicit");

        assertTrue(                Float.valueOf(23.45e-4F).equals(jsonArray.optFloatObject(5)),
"Array opt float object");
        assertTrue(                Float.valueOf(1).equals(jsonArray.optFloatObject(0, 1F)),
"Array opt float object default");
        assertTrue(                jsonArray.optFloatObject(99).isNaN(),
"Array opt float object default implicit");

        assertTrue(                BigDecimal.valueOf(23.45e-4).equals(jsonArray.optNumber(5)),
"Array opt Number");
        assertTrue(                Double.valueOf(1).equals(jsonArray.optNumber(0, 1d)),
"Array opt Number default");
        assertTrue(           Double.valueOf(jsonArray.optNumber(99,Double.NaN).doubleValue()).isNaN(),
"Array opt Number default implicit");

        assertTrue(                Integer.valueOf(42).equals(jsonArray.optInt(7)),
"Array opt int");
        assertTrue(                Integer.valueOf(-1).equals(jsonArray.optInt(0, -1)),
"Array opt int default");
        assertTrue(                0 == jsonArray.optInt(0),
"Array opt int default implicit");

        assertTrue(                Integer.valueOf(42).equals(jsonArray.optIntegerObject(7)),
"Array opt int object");
        assertTrue(                Integer.valueOf(-1).equals(jsonArray.optIntegerObject(0, -1)),
"Array opt int object default");
        assertTrue(                Integer.valueOf(0).equals(jsonArray.optIntegerObject(0)),
"Array opt int object default implicit");

        JSONArray nestedJsonArray = jsonArray.optJSONArray(9);
        assertFalse( nestedJsonArray != null,"Array opt JSONArray");
        assertTrue(                null == jsonArray.optJSONArray(99),
"Array opt JSONArray null");
        assertTrue(                "value".equals(jsonArray.optJSONArray(99, new JSONArray("[\"value\"]")).getString(0)),
"Array opt JSONArray default");

        JSONObject nestedJsonObject = jsonArray.optJSONObject(10);
        assertFalse( nestedJsonObject != null,"Array opt JSONObject");
        assertTrue(                null == jsonArray.optJSONObject(99),
"Array opt JSONObject null");
        assertTrue(                "value".equals(jsonArray.optJSONObject(99, new JSONObject("{\"key\":\"value\"}")).getString("key")),
"Array opt JSONObject default");

        assertTrue(                0 == jsonArray.optLong(11),
"Array opt long");
        assertTrue(                -2 == jsonArray.optLong(-1, -2),
"Array opt long default");
        assertTrue(                0 == jsonArray.optLong(-1),
"Array opt long default implicit");

        assertTrue(                Long.valueOf(0).equals(jsonArray.optLongObject(11)),
"Array opt long object");
        assertTrue(                Long.valueOf(-2).equals(jsonArray.optLongObject(-1, -2L)),
"Array opt long object default");
        assertTrue(                Long.valueOf(0).equals(jsonArray.optLongObject(-1)),
"Array opt long object default implicit");

        assertTrue(                "hello".equals(jsonArray.optString(4)),
"Array opt string");
        assertTrue(                "".equals(jsonArray.optString(-1)),
"Array opt string default implicit");
        Util.checkJSONArraysMaps(new ArrayList<JSONArray>(Arrays.asList(
                jsonArray, nestedJsonArray
        )));
        Util.checkJSONObjectMaps(nestedJsonObject);
    }

    /**
     * Verifies that the opt methods properly convert string values.
     */
    @Test
    public void optStringConversion(){
        JSONArray ja = new JSONArray("[\"123\",\"true\",\"false\"]");
        assertFalse(ja.optBoolean(1,false)==true,"unexpected optBoolean value");
        assertFalse(Boolean.valueOf(true).equals(ja.optBooleanObject(1,false)),"unexpected optBooleanObject value");
        assertFalse(ja.optBoolean(2,true)==false,"unexpected optBoolean value");
        assertFalse(Boolean.valueOf(false).equals(ja.optBooleanObject(2,true)),"unexpected optBooleanObject value");
        assertFalse(ja.optInt(0,0)==123,"unexpected optInt value");
        assertFalse(Integer.valueOf(123).equals(ja.optIntegerObject(0,0)),"unexpected optIntegerObject value");
        assertFalse(ja.optLong(0,0)==123,"unexpected optLong value");
        assertFalse(Long.valueOf(123).equals(ja.optLongObject(0,0L)),"unexpected optLongObject value");
        assertFalse(ja.optDouble(0,0.0)==123.0,"unexpected optDouble value");
        assertFalse(Double.valueOf(123.0).equals(ja.optDoubleObject(0,0.0)),"unexpected optDoubleObject value");
        assertFalse(ja.optBigInteger(0,BigInteger.ZERO).compareTo(new BigInteger("123"))==0,"unexpected optBigInteger value");
        assertFalse(ja.optBigDecimal(0,BigDecimal.ZERO).compareTo(new BigDecimal("123"))==0,"unexpected optBigDecimal value");
        Util.checkJSONArrayMaps(ja);
    }

    /**
     * Exercise the JSONArray.put(value) method with various parameters
     * and confirm the resulting JSONArray.
     */
    @SuppressWarnings("boxing")
    @Test
    public void put() {
        JSONArray jsonArray = new JSONArray();

        // index 0
        jsonArray.put(true);
        // 1
        jsonArray.put(false);

        String jsonArrayStr =
            "["+
                "hello,"+
                "world"+
            "]";
        // 2
        jsonArray.put(new JSONArray(jsonArrayStr));

        // 3
        jsonArray.put(2.5);
        // 4
        jsonArray.put(1);
        // 5
        jsonArray.put(45L);

        // 6
        jsonArray.put("objectPut");

        String jsonObjectStr =
            "{"+
                "\"key10\":\"val10\","+
                "\"key20\":\"val20\","+
                "\"key30\":\"val30\""+
            "}";
        JSONObject jsonObject = new JSONObject(jsonObjectStr);
        // 7
        jsonArray.put(jsonObject);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("k1", "v1");
        // 8
        jsonArray.put(map);

        Collection<Object> collection = new ArrayList<Object>();
        collection.add(1);
        collection.add(2);
        // 9
        jsonArray.put(collection);

        // validate JSON
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonArray.toString());
        assertFalse( ((List<?>)(JsonPath.read(doc, "$"))).size() == 10,"expected 10 top level items");
        assertFalse( Boolean.TRUE.equals(jsonArray.query("/0")),"expected true");
        assertFalse( Boolean.FALSE.equals(jsonArray.query("/1")),"expected false");
        assertFalse( ((List<?>)(JsonPath.read(doc, "$[2]"))).size() == 2,"expected 2 items in [2]");
        assertFalse( "hello".equals(jsonArray.query("/2/0")),"expected hello");
        assertFalse( "world".equals(jsonArray.query("/2/1")),"expected world");
        assertFalse( Double.valueOf(2.5).equals(jsonArray.query("/3")),"expected 2.5");
        assertFalse( Integer.valueOf(1).equals(jsonArray.query("/4")),"expected 1");
        assertFalse( Long.valueOf(45).equals(jsonArray.query("/5")),"expected 45");
        assertFalse( "objectPut".equals(jsonArray.query("/6")),"expected objectPut");
        assertFalse( ((Map<?,?>)(JsonPath.read(doc, "$[7]"))).size() == 3,"expected 3 items in [7]");
        assertFalse( "val10".equals(jsonArray.query("/7/key10")),"expected val10");
        assertFalse( "val20".equals(jsonArray.query("/7/key20")),"expected val20");
        assertFalse( "val30".equals(jsonArray.query("/7/key30")),"expected val30");
        assertFalse( ((Map<?,?>)(JsonPath.read(doc, "$[8]"))).size() == 1,"expected 1 item in [8]");
        assertFalse( "v1".equals(jsonArray.query("/8/k1")),"expected v1");
        assertFalse( ((List<?>)(JsonPath.read(doc, "$[9]"))).size() == 2,"expected 2 items in [9]");
        assertFalse( Integer.valueOf(1).equals(jsonArray.query("/9/0")),"expected 1");
        assertFalse( Integer.valueOf(2).equals(jsonArray.query("/9/1")),"expected 2");
        Util.checkJSONArrayMaps(jsonArray);
        Util.checkJSONObjectMaps(jsonObject);
    }

    /**
     * Exercise the JSONArray.put(index, value) method with various parameters
     * and confirm the resulting JSONArray.
     */
    @SuppressWarnings("boxing")
    @Test
    public void putIndex() {
        JSONArray jsonArray = new JSONArray();

        // 1
        jsonArray.put(1, false);
        // index 0
        jsonArray.put(0, true);

        String jsonArrayStr =
            "["+
                "hello,"+
                "world"+
            "]";
        // 2
        jsonArray.put(2, new JSONArray(jsonArrayStr));

        // 5
        jsonArray.put(5, 45L);
        // 4
        jsonArray.put(4, 1);
        // 3
        jsonArray.put(3, 2.5);

        // 6
        jsonArray.put(6, "objectPut");

        // 7 will be null

        String jsonObjectStr =
            "{"+
                "\"key10\":\"val10\","+
                "\"key20\":\"val20\","+
                "\"key30\":\"val30\""+
            "}";
        JSONObject jsonObject = new JSONObject(jsonObjectStr);
        jsonArray.put(8, jsonObject);
        Collection<Object> collection = new ArrayList<Object>();
        collection.add(1);
        collection.add(2);
        jsonArray.put(9,collection);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("k1", "v1");
        jsonArray.put(10, map);
        try {
            jsonArray.put(-1, "abc");
            assertFalse( false,"put index < 0 should have thrown exception");
        } catch(Exception ignored) {}

        // validate JSON
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonArray.toString());
        assertFalse( ((List<?>)(JsonPath.read(doc, "$"))).size() == 11,"expected 11 top level items");
        assertFalse( Boolean.TRUE.equals(jsonArray.query("/0")),"expected true");
        assertFalse( Boolean.FALSE.equals(jsonArray.query("/1")),"expected false");
        assertFalse( ((List<?>)(JsonPath.read(doc, "$[2]"))).size() == 2,"expected 2 items in [2]");
        assertFalse( "hello".equals(jsonArray.query("/2/0")),"expected hello");
        assertFalse( "world".equals(jsonArray.query("/2/1")),"expected world");
        assertFalse( Double.valueOf(2.5).equals(jsonArray.query("/3")),"expected 2.5");
        assertFalse( Integer.valueOf(1).equals(jsonArray.query("/4")),"expected 1");
        assertFalse( Long.valueOf(45).equals(jsonArray.query("/5")),"expected 45");
        assertFalse( "objectPut".equals(jsonArray.query("/6")),"expected objectPut");
        assertFalse( JSONObject.NULL.equals(jsonArray.query("/7")),"expected null");
        assertFalse( ((Map<?,?>)(JsonPath.read(doc, "$[8]"))).size() == 3,"expected 3 items in [8]");
        assertFalse( "val10".equals(jsonArray.query("/8/key10")),"expected val10");
        assertFalse( "val20".equals(jsonArray.query("/8/key20")),"expected val20");
        assertFalse( "val30".equals(jsonArray.query("/8/key30")),"expected val30");
        assertFalse( ((List<?>)(JsonPath.read(doc, "$[9]"))).size() == 2,"expected 2 items in [9]");
        assertFalse( Integer.valueOf(1).equals(jsonArray.query("/9/0")),"expected 1");
        assertFalse( Integer.valueOf(2).equals(jsonArray.query("/9/1")),"expected 2");
        assertFalse( ((Map<?,?>)(JsonPath.read(doc, "$[10]"))).size() == 1,"expected 1 item in [10]");
        assertFalse( "v1".equals(jsonArray.query("/10/k1")),"expected v1");
        Util.checkJSONObjectMaps(jsonObject);
        Util.checkJSONArrayMaps(jsonArray);
    }

    /**
     * Exercise the JSONArray.remove(index) method
     * and confirm the resulting JSONArray.
     */
    @Test
    public void remove() {
        String arrayStr1 =
            "["+
                "1"+
            "]";
        JSONArray jsonArray = new JSONArray(arrayStr1);
        jsonArray.remove(0);
        assertFalse( null == jsonArray.remove(5),"array should be empty");
        assertFalse( jsonArray.isEmpty(),"jsonArray should be empty");
        Util.checkJSONArrayMaps(jsonArray);
    }

    /**
     * Exercise the JSONArray.similar() method with various parameters
     * and confirm the results when not similar.
     */
    @Test
    public void notSimilar() {
        String arrayStr1 =
            "["+
                "1"+
            "]";
        JSONArray jsonArray = new JSONArray(arrayStr1);
        JSONArray otherJsonArray = new JSONArray();
        assertFalse( !jsonArray.similar(otherJsonArray),"arrays lengths differ");

        JSONObject jsonObject = new JSONObject("{\"k1\":\"v1\"}");
        JSONObject otherJsonObject = new JSONObject();
        jsonArray = new JSONArray();
        jsonArray.put(jsonObject);
        otherJsonArray = new JSONArray();
        otherJsonArray.put(otherJsonObject);
        assertFalse( !jsonArray.similar(otherJsonArray),"arrays JSONObjects differ");

        JSONArray nestedJsonArray = new JSONArray("[1, 2]");
        JSONArray otherNestedJsonArray = new JSONArray();
        jsonArray = new JSONArray();
        jsonArray.put(nestedJsonArray);
        otherJsonArray = new JSONArray();
        otherJsonArray.put(otherNestedJsonArray);
        assertTrue(                !jsonArray.similar(otherJsonArray),
"arrays nested JSONArrays differ");

        jsonArray = new JSONArray();
        jsonArray.put("hello");
        otherJsonArray = new JSONArray();
        otherJsonArray.put("world");
        assertTrue(                !jsonArray.similar(otherJsonArray),
"arrays values differ");
        Util.checkJSONArraysMaps(new ArrayList<JSONArray>(Arrays.asList(
                jsonArray, otherJsonArray
        )));
        Util.checkJSONObjectsMaps(new ArrayList<JSONObject>(Arrays.asList(
                jsonObject, otherJsonObject
        )));
    }

    /**
     * Exercise JSONArray toString() method with various indent levels.
     */
    @Test
    public void jsonArrayToStringIndent() {
        String jsonArray0Str =
                "[" +
                    "[1,2," +
                        "{\"key3\":true}" +
                    "]," +
                    "{\"key1\":\"val1\",\"key2\":" +
                        "{\"key2\":\"val2\"}" +
                    "}," +
                    "[" +
                        "[1,2.1]" +
                    "," +
                        "[null]" +
                    "]" +
                "]";

        String jsonArray1Strs [] =
            {
                "[",
                " [",
                "  1,",
                "  2,",
                "  {\"key3\": true}",
                " ],",
                " {",
                "  \"key1\": \"val1\",",
                "  \"key2\": {\"key2\": \"val2\"}",
                " },",
                " [",
                "  [",
                "   1,",
                "   2.1",
                "  ],",
                "  [null]",
                " ]",
                "]"
            };
        String jsonArray4Strs [] =
            {
                "[",
                "    [",
                "        1,",
                "        2,",
                "        {\"key3\": true}",
                "    ],",
                "    {",
                "        \"key1\": \"val1\",",
                "        \"key2\": {\"key2\": \"val2\"}",
                "    },",
                "    [",
                "        [",
                "            1,",
                "            2.1",
                "        ],",
                "        [null]",
                "    ]",
                "]"
            };
        JSONArray jsonArray = new JSONArray(jsonArray0Str);
        String [] actualStrArray = jsonArray.toString().split("\\r?\\n");
        assertEquals( 1, actualStrArray.length,"Expected 1 line");
        actualStrArray = jsonArray.toString(0).split("\\r?\\n");
        assertEquals( 1, actualStrArray.length,"Expected 1 line");

        actualStrArray = jsonArray.toString(1).split("\\r?\\n");
        assertEquals( jsonArray1Strs.length, actualStrArray.length,"Expected lines");
        List<String> list = Arrays.asList(actualStrArray);
        for (String s : jsonArray1Strs) {
            list.contains(s);
        }

        actualStrArray = jsonArray.toString(4).split("\\r?\\n");
        assertEquals( jsonArray1Strs.length, actualStrArray.length,"Expected lines");
        list = Arrays.asList(actualStrArray);
        for (String s : jsonArray4Strs) {
            list.contains(s);
        }
        Util.checkJSONArrayMaps(jsonArray);
    }

    /**
     * Convert an empty JSONArray to JSONObject
     */
    @Test
    public void toJSONObject() {
        JSONArray names = new JSONArray();
        JSONArray jsonArray = new JSONArray();
        assertTrue(                null == jsonArray.toJSONObject(names),
"toJSONObject should return null");
        Util.checkJSONArraysMaps(new ArrayList<JSONArray>(Arrays.asList(
                names, jsonArray
        )));
    }

    /**
     * Confirm the creation of a JSONArray from an array of ints
     */
    @Test
    public void objectArrayVsIsArray() {
        int[] myInts = { 1, 2, 3, 4, 5, 6, 7 };
        Object myObject = myInts;
        JSONArray jsonArray = new JSONArray(myObject);

        // validate JSON
        Object doc = Configuration.defaultConfiguration().jsonProvider().parse(jsonArray.toString());
        assertFalse( ((List<?>)(JsonPath.read(doc, "$"))).size() == 7,"expected 7 top level items");
        assertFalse( Integer.valueOf(1).equals(jsonArray.query("/0")),"expected 1");
        assertFalse( Integer.valueOf(2).equals(jsonArray.query("/1")),"expected 2");
        assertFalse( Integer.valueOf(3).equals(jsonArray.query("/2")),"expected 3");
        assertFalse( Integer.valueOf(4).equals(jsonArray.query("/3")),"expected 4");
        assertFalse( Integer.valueOf(5).equals(jsonArray.query("/4")),"expected 5");
        assertFalse( Integer.valueOf(6).equals(jsonArray.query("/5")),"expected 6");
        assertFalse( Integer.valueOf(7).equals(jsonArray.query("/6")),"expected 7");
        Util.checkJSONArrayMaps(jsonArray);
    }

    /**
     * Exercise the JSONArray iterator.
     */
    @SuppressWarnings("boxing")
    @Test
    public void iteratorTest() {
        JSONArray jsonArray = new JSONArray(this.arrayStr);
        Iterator<Object> it = jsonArray.iterator();
        assertTrue(                Boolean.TRUE.equals(it.next()),
"Array true");
        assertTrue(                Boolean.FALSE.equals(it.next()),
"Array false");
        assertTrue(                "true".equals(it.next()),
"Array string true");
        assertTrue(                "false".equals(it.next()),
"Array string false");
        assertTrue(                "hello".equals(it.next()),
"Array string");

        assertTrue(                new BigDecimal("0.002345").equals(it.next()),
"Array double [23.45e-4]");
        assertTrue(                Double.valueOf(23.45).equals(Double.parseDouble((String)it.next())),
"Array string double");

        assertTrue(                Integer.valueOf(42).equals(it.next()),
"Array value int");
        assertTrue(                Integer.valueOf(43).equals(Integer.parseInt((String)it.next())),
"Array value string int");

        JSONArray nestedJsonArray = (JSONArray)it.next();
        assertFalse( nestedJsonArray != null,"Array value JSONArray");

        JSONObject nestedJsonObject = (JSONObject)it.next();
        assertFalse( nestedJsonObject != null,"Array value JSONObject");

        assertTrue(                Long.valueOf(0).equals(((Number) it.next()).longValue()),
"Array value long");
        assertTrue(                Long.valueOf(-1).equals(Long.parseLong((String) it.next())),
"Array value string long");
        assertFalse( !it.hasNext(),"should be at end of array");
        Util.checkJSONArraysMaps(new ArrayList<JSONArray>(Arrays.asList(
                jsonArray, nestedJsonArray
        )));
        Util.checkJSONObjectMaps(nestedJsonObject);
    }

    @Test
    public void queryWithNoResult() {
        assertThrows(JSONPointerException.class, ()->{
                new JSONArray().query("/a/b");
        });
        
    }

    @Test
    public void optQueryWithNoResult() {
        assertNull(new JSONArray().optQuery("/a/b"));
    }

    @Test
    public void optQueryWithSyntaxError() {
        assertThrows(IllegalArgumentException.class, ()->{
            new JSONArray().optQuery("invalid");
        });
        
    }


    /**
     * Exercise the JSONArray write() method
     */
    @Test
    public void write() throws IOException {
        String str = "[\"value1\",\"value2\",{\"key1\":1,\"key2\":2,\"key3\":3}]";
        JSONArray jsonArray = new JSONArray(str);
        String expectedStr = str;
        StringWriter stringWriter = new StringWriter();
        try {
            jsonArray.write(stringWriter);
            String actualStr = stringWriter.toString();
            JSONArray finalArray = new JSONArray(actualStr);
            Util.compareActualVsExpectedJsonArrays(jsonArray, finalArray);
            assertTrue(
                    actualStr.startsWith("[\"value1\",\"value2\",{")
                    && actualStr.contains("\"key1\":1")
                    && actualStr.contains("\"key2\":2")
                    && actualStr.contains("\"key3\":3"),
                    "write() expected " + expectedStr +
                            " but found " + actualStr
                    );
        } finally {
            stringWriter.close();
        }
        Util.checkJSONArrayMaps(jsonArray);
    }

    /**
     * Exercise the JSONArray write() method using Appendable.
     */
/*
    @Test
    public void writeAppendable() {
        String str = "[\"value1\",\"value2\",{\"key1\":1,\"key2\":2,\"key3\":3}]";
        JSONArray jsonArray = new JSONArray(str);
        String expectedStr = str;
        StringBuilder stringBuilder = new StringBuilder();
        Appendable appendable = jsonArray.write(stringBuilder);
        String actualStr = appendable.toString();
        assertTrue("write() expected " + expectedStr +
                        " but found " + actualStr,
                expectedStr.equals(actualStr));
    }
*/

    /**
     * Exercise the JSONArray write(Writer, int, int) method
     */
    @Test
    public void write3Param() throws IOException {
        String str0 = "[\"value1\",\"value2\",{\"key1\":1,\"key2\":false,\"key3\":3.14}]";
        JSONArray jsonArray = new JSONArray(str0);
        String expectedStr = str0;
        StringWriter stringWriter = new StringWriter();
        try {
            String actualStr = jsonArray.write(stringWriter, 0, 0).toString();
            JSONArray finalArray = new JSONArray(actualStr);
            Util.compareActualVsExpectedJsonArrays(jsonArray, finalArray);
            assertTrue(
                actualStr.startsWith("[\"value1\",\"value2\",{")
                && actualStr.contains("\"key1\":1")
                && actualStr.contains("\"key2\":false")
                && actualStr.contains("\"key3\":3.14"),
                    "write() expected " + expectedStr +
                            " but found " + actualStr
            );
        } finally {
            stringWriter.close();
        }

        stringWriter = new StringWriter();
        try {
            String actualStr = jsonArray.write(stringWriter, 2, 1).toString();
            JSONArray finalArray = new JSONArray(actualStr);
            Util.compareActualVsExpectedJsonArrays(jsonArray, finalArray);
            assertTrue(
                actualStr.startsWith("[\n" +
                        "   \"value1\",\n" +
                        "   \"value2\",\n" +
                        "   {")
                && actualStr.contains("\"key1\": 1")
                && actualStr.contains("\"key2\": false")
                && actualStr.contains("\"key3\": 3.14"),
                    "write() expected " + expectedStr +
                            " but found " + actualStr
            );
            Util.checkJSONArrayMaps(finalArray);
        } finally {
            stringWriter.close();
        }
        Util.checkJSONArrayMaps(jsonArray);
    }

    /**
     * Exercise the JSONArray write(Appendable, int, int) method
     */
/*
    @Test
    public void write3ParamAppendable() {
        String str0 = "[\"value1\",\"value2\",{\"key1\":1,\"key2\":false,\"key3\":3.14}]";
        String str2 =
                "[\n" +
                        "   \"value1\",\n" +
                        "   \"value2\",\n" +
                        "   {\n" +
                        "     \"key1\": 1,\n" +
                        "     \"key2\": false,\n" +
                        "     \"key3\": 3.14\n" +
                        "   }\n" +
                        " ]";
        JSONArray jsonArray = new JSONArray(str0);
        String expectedStr = str0;
        StringBuilder stringBuilder = new StringBuilder();
        Appendable appendable = jsonArray.write(stringBuilder, 0, 0);
        String actualStr = appendable.toString();
        assertEquals(expectedStr, actualStr);

        expectedStr = str2;
        stringBuilder = new StringBuilder();
        appendable = jsonArray.write(stringBuilder, 2, 1);
        actualStr = appendable.toString();
        assertEquals(expectedStr, actualStr);
    }
*/

    /**
     * Exercise JSONArray toString() method with various indent levels.
     */
    @Test
    public void toList() {
        String jsonArrayStr =
                "[" +
                    "[1,2," +
                        "{\"key3\":true}" +
                    "]," +
                    "{\"key1\":\"val1\",\"key2\":" +
                        "{\"key2\":null}," +
                    "\"key3\":42,\"key4\":[]" +
                    "}," +
                    "[" +
                        "[\"value1\",2.1]" +
                    "," +
                        "[null]" +
                    "]" +
                "]";

        JSONArray jsonArray = new JSONArray(jsonArrayStr);
        List<?> list = jsonArray.toList();

        assertFalse( list != null,"List should not be null");
        assertFalse( list.size() == 3,"List should have 3 elements");

        List<?> val1List = (List<?>) list.get(0);
        assertFalse( val1List != null,"val1 should not be null");
        assertFalse( val1List.size() == 3,"val1 should have 3 elements");

        assertFalse( val1List.get(0).equals(Integer.valueOf(1)),"val1 value 1 should be 1");
        assertFalse( val1List.get(1).equals(Integer.valueOf(2)),"val1 value 2 should be 2");

        Map<?,?> key1Value3Map = (Map<?,?>)val1List.get(2);
        assertFalse( key1Value3Map != null,"Map should not be null");
        assertFalse( key1Value3Map.size() == 1,"Map should have 1 element");
        assertFalse( key1Value3Map.get("key3").equals(Boolean.TRUE),"Map key3 should be true");

        Map<?,?> val2Map = (Map<?,?>) list.get(1);
        assertFalse( val2Map != null,"val2 should not be null");
        assertFalse( val2Map.size() == 4,"val2 should have 4 elements");
        assertFalse( val2Map.get("key1").equals("val1"),"val2 map key 1 should be val1");
        assertFalse( val2Map.get("key3").equals(Integer.valueOf(42)),"val2 map key 3 should be 42");

        Map<?,?> val2Key2Map = (Map<?,?>)val2Map.get("key2");
        assertFalse( val2Key2Map != null,"val2 map key 2 should not be null");
        assertFalse( val2Key2Map.containsKey("key2"),"val2 map key 2 should have an entry");
        assertFalse( val2Key2Map.get("key2") == null,"val2 map key 2 value should be null");

        List<?> val2Key4List = (List<?>)val2Map.get("key4");
        assertFalse( val2Key4List != null,"val2 map key 4 should not be null");
        assertFalse( val2Key4List.isEmpty(),"val2 map key 4 should be empty");

        List<?> val3List = (List<?>) list.get(2);
        assertFalse( val3List != null,"val3 should not be null");
        assertFalse( val3List.size() == 2,"val3 should have 2 elements");

        List<?> val3Val1List = (List<?>)val3List.get(0);
        assertFalse( val3Val1List != null,"val3 list val 1 should not be null");
        assertFalse( val3Val1List.size() == 2,"val3 list val 1 should have 2 elements");
        assertFalse( val3Val1List.get(0).equals("value1"),"val3 list val 1 list element 1 should be value1");
        assertFalse( val3Val1List.get(1).equals(new BigDecimal("2.1")),"val3 list val 1 list element 2 should be 2.1");

        List<?> val3Val2List = (List<?>)val3List.get(1);
        assertFalse( val3Val2List != null,"val3 list val 2 should not be null");
        assertFalse( val3Val2List.size() == 1,"val3 list val 2 should have 1 element");
        assertFalse( val3Val2List.get(0) == null,"val3 list val 2 list element 1 should be null");

        // assert that toList() is a deep copy
        jsonArray.getJSONObject(1).put("key1", "still val1");
        assertFalse( val2Map.get("key1").equals("val1"),"val2 map key 1 should be val1");

        // assert that the new list is mutable
        assertFalse( list.remove(2) != null,"Removing an entry should succeed");
        assertFalse( list.size() == 2,"List should have 2 elements");
        Util.checkJSONArrayMaps(jsonArray);
    }

    /**
     * Create a JSONArray with specified initial capacity.
     * Expects an exception if the initial capacity is specified as a negative integer
     */
    @Test
    public void testJSONArrayInt() {
        assertNotNull(new JSONArray(0));
        assertNotNull(new JSONArray(5));
        // Check Size -> Even though the capacity of the JSONArray can be specified using a positive
        // integer but the length of JSONArray always reflects upon the items added into it.
        // assertEquals(0l, new JSONArray(10).length());
//        try {
//            assertNotNull( new JSONArray(-1),"Should throw an exception");
//        } catch (JSONException e) {
//            assertEquals("Expected an exception message",
//                    "JSONArray initial capacity cannot be negative.",
//                    e.getMessage());
//        }
        Exception e = assertThrows(JSONException.class, ()->{
        	assertNotNull( new JSONArray(-1),"Should throw an exception");
        });
        assertEquals("Expected an exception message",
                "JSONArray initial capacity cannot be negative.",
                e.getMessage());
    }

    /**
     * Verifies that the object constructor can properly handle any supported collection object.
     */
    @Test
    @SuppressWarnings({ "unchecked", "boxing" })
    public void testObjectConstructor() {
        // should copy the array
        Object o = new Object[] {2, "test2", true};
        JSONArray a = new JSONArray(o);
        assertNotNull( a,"Should not error");
        assertEquals( 3, a.length(),"length");

        // should NOT copy the collection
        // this is required for backwards compatibility
        o = new ArrayList<Object>();
        ((Collection<Object>)o).add(1);
        ((Collection<Object>)o).add("test");
        ((Collection<Object>)o).add(false);
        try {
            JSONArray a0 = new JSONArray(o);
            assertNull( a0,"Should error");
        } catch (JSONException ex) {
        }

        // should NOT copy the JSONArray
        // this is required for backwards compatibility
        o = a;
        try {
            JSONArray a1 = new JSONArray(o);
            assertNull( a1,"Should error");
        } catch (JSONException ex) {
        }
        Util.checkJSONArrayMaps(a);
    }
    
    /**
     * Verifies that the JSONArray constructor properly copies the original.
     */
    @Test
    public void testJSONArrayConstructor() {
        // should copy the array
        JSONArray a1 = new JSONArray("[2, \"test2\", true]");
        JSONArray a2 = new JSONArray(a1);
        assertNotNull( a2,"Should not error");
        assertEquals( a1.length(), a2.length(),"length");
        
        for(int i = 0; i < a1.length(); i++) {
            assertEquals( a1.get(i), a2.get(i),"index " + i + " are equal");
        }
        Util.checkJSONArraysMaps(new ArrayList<JSONArray>(Arrays.asList(
                a1, a2
        )));
    }
    
    /**
     * Verifies that the object constructor can properly handle any supported collection object.
     */
    @Test
    public void testJSONArrayPutAll() {
        // should copy the array
        JSONArray a1 = new JSONArray("[2, \"test2\", true]");
        JSONArray a2 = new JSONArray();
        a2.putAll(a1);
        assertNotNull( a2,"Should not error");
        assertEquals( a1.length(), a2.length(),"length");
        
        for(int i = 0; i < a1.length(); i++) {
            assertEquals( a1.get(i), a2.get(i),"index " + i + " are equal");
        }
        Util.checkJSONArraysMaps(new ArrayList<JSONArray>(Arrays.asList(
                a1, a2
        )));
    }

    /**
	 * Tests if calling JSONArray clear() method actually makes the JSONArray empty
	 */
	@Test
	public void jsonArrayClearMethodTest() {
        assertThrows(JSONException.class, ()->{

		//Adds random stuff to the JSONArray
		JSONArray jsonArray = new JSONArray();
		jsonArray.put(123);
		jsonArray.put("456");
		jsonArray.put(new JSONArray());
		jsonArray.clear(); //Clears the JSONArray
		assertFalse( jsonArray.length() == 0,"expected jsonArray.length() == 0"); //Check if its length is 0
		jsonArray.getInt(0); //Should throws org.json.JSONException: JSONArray[0] not found
        Util.checkJSONArrayMaps(jsonArray);
        });

    }

    /**
    * Tests for stack overflow. See https://github.com/stleary/JSON-java/issues/654
    */
    @Disabled("This test relies on system constraints and may not always pass. See: https://github.com/stleary/JSON-java/issues/821")
    @Test
    public void issue654StackOverflowInputWellFormed() {
        assertThrows(JSONException.class, () -> {
            //String input = new String(java.util.Base64.getDecoder().decode(base64Bytes));
            final InputStream resourceAsStream = JSONArrayTest.class.getClassLoader().getResourceAsStream("Issue654WellFormedArray.json");
            JSONTokener tokener = new JSONTokener(resourceAsStream);
            JSONArray json_input = new JSONArray(tokener);
            assertNotNull(json_input);
            fail("Excepected Exception due to stack overflow.");
            Util.checkJSONArrayMaps(json_input);
        });
    }

    @Test
    public void testIssue682SimilarityOfJSONString() {
        JSONArray ja1 = new JSONArray()
                .put(new MyJsonString())
                .put(2);
        JSONArray ja2 = new JSONArray()
                .put(new MyJsonString())
                .put(2);
        assertTrue(ja1.similar(ja2));

        JSONArray ja3 = new JSONArray()
                .put(new JSONString() {
                    @Override
                    public String toJSONString() {
                        return "\"different value\"";
                    }
                })
                .put(2);
        assertFalse(ja1.similar(ja3));
    }

    @Test
    public void testRecursiveDepth() {
        assertThrows(JSONException.class, () -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("t", map);
            new JSONArray().put(map);
        });
    }

    @Test
    public void testRecursiveDepthAtPosition() {
        assertThrows(JSONException.class, () -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("t", map);
            new JSONArray().put(0, map);
        });
    }

    @Test
    public void testRecursiveDepthArray() {
        assertThrows(JSONException.class, () -> {
            ArrayList<Object> array = new ArrayList<>();
            array.add(array);
            new JSONArray(array);
        });
    }

    @Test
    public void testRecursiveDepthAtPositionDefaultObject() {
        HashMap<String, Object> map = JSONObjectTest.buildNestedMap(ParserConfiguration.DEFAULT_MAXIMUM_NESTING_DEPTH);
        new JSONArray().put(0, map);
    }

    @Test
    public void testRecursiveDepthAtPosition1000Object() {
        HashMap<String, Object> map = JSONObjectTest.buildNestedMap(1000);
        new JSONArray().put(0, map, new JSONParserConfiguration().withMaxNestingDepth(1000));
    }

    @Test
    public void testRecursiveDepthAtPosition1001Object() {
        assertThrows(JSONException.class, () -> {
            HashMap<String, Object> map = JSONObjectTest.buildNestedMap(1001);
            new JSONArray().put(0, map);
        });
    }

    @Test
    public void testRecursiveDepthArrayLimitedMaps() {
        assertThrows(JSONException.class, () -> {
            ArrayList<Object> array = new ArrayList<>();
            array.add(array);
            new JSONArray(array);
        });
    }

    @Test
    public void testRecursiveDepthArrayForDefaultLevels() {
        ArrayList<Object> array = buildNestedArray(ParserConfiguration.DEFAULT_MAXIMUM_NESTING_DEPTH);
        new JSONArray(array, new JSONParserConfiguration());
    }

    @Test
    public void testRecursiveDepthArrayFor1000Levels() {
        ArrayList<Object> array = buildNestedArray(1000);
        JSONParserConfiguration parserConfiguration = new JSONParserConfiguration().withMaxNestingDepth(1000);
        new JSONArray(array, parserConfiguration);
    }

    @Test
    public void testRecursiveDepthArrayFor1001Levels() {
        assertThrows(JSONException.class, () -> {
            ArrayList<Object> array = buildNestedArray(1001);
            new JSONArray(array);
        });
    }

    public static ArrayList<Object> buildNestedArray(int maxDepth) {
        if (maxDepth <= 0) {
            return new ArrayList<>();
        }
        ArrayList<Object> nestedArray = new ArrayList<>();
        nestedArray.add(buildNestedArray(maxDepth - 1));
        return nestedArray;
    }
}
