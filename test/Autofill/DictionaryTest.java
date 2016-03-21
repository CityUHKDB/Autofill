/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Autofill;

import java.sql.SQLException;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class DictionaryTest {
    
    public DictionaryTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // Synonym exist in dictionary
    @Test
    public void testGetStandardWordExist() throws SQLException {
        String synonym = "phone";
        Dictionary dictionary = Dictionary.getInstance();
        String standard = dictionary.getStandardWord(synonym);
        assertEquals("phone number", standard);
    }
    
    // Synonym not exist in dictionary
    @Test
    public void testGetStandardWordNotExist() throws SQLException {
        String synonym = "";
        Dictionary dictionary = Dictionary.getInstance();
        String standard = dictionary.getStandardWord(synonym);
        assertEquals("", standard);
    }
    
    // Synonym exists in dictionary
    @Test
    public void testGetSynonymsExist() throws SQLException {
        String word = "phone";
        Dictionary dictionary = Dictionary.getInstance();
        ArrayList<String> synonyms = dictionary.getSynonyms(word);
        assertEquals(true, !synonyms.isEmpty());
    }

    // Synonym not exists in dictionary
    @Test
    public void testGetSynonymsNotExist() throws SQLException {
        String word = "";
        Dictionary dictionary = Dictionary.getInstance();
        ArrayList<String> synonyms = dictionary.getSynonyms(word);
        assertEquals(true, synonyms.isEmpty());
    }


    // Get same prefix words from dictionary, loop > 1
    @Test
    public void testGetSamePrefixWords1() throws SQLException {
        String word = "phone";
        Dictionary dictionary = Dictionary.getInstance();
        ArrayList<String> samePrefixWords = dictionary.getSamePrefixWords(word);
        assertEquals(true, !samePrefixWords.isEmpty());
    }
    
    // Get same prefix words from dictionary, loop = 1
    @Test
    public void testGetSamePrefixWords2() throws SQLException {
        String word = "samePrefix";
        Dictionary dictionary = Dictionary.getInstance();
        ArrayList<String> samePrefixWords = dictionary.getSamePrefixWords(word);
        assertEquals(false, !samePrefixWords.isEmpty());
    }

    // Successfully add synonym to dictionary
    @Test
    public void testAddSynonymSuccessful() throws Exception {
        String word1 = "test1";
        String word2 = "test2";
        Dictionary dictionary = Dictionary.getInstance();
        boolean successful = dictionary.addSynonym(word1, word2);
        assertEquals(true, successful);
    }
    
    // Fail add synonym to dictionary
    @Test
    public void testAddSynonymFail() throws Exception {
        String word1 = null;
        String word2 = null;
        Dictionary dictionary = Dictionary.getInstance();
        boolean successful = dictionary.addSynonym(word1, word2);
        assertEquals(false, successful);
    }
    
    // Successfully add synonym to dictionary
    @Test
    public void testRemoveSynonymSuccessful() throws SQLException {
        String word1 = "test1";
        String word2 = "test2";
        Dictionary dictionary = Dictionary.getInstance();
        boolean successful = dictionary.removeSynonym(word1, word2);
        assertEquals(true, successful);
    }

    // Successfully add probability for an existing standard-to-synonym matching 
    @Test
    public void testAddProbabilityStandardToSynonym() throws SQLException {
        String word1 = "test3";
        String word2 = "test4";
        Dictionary dictionary = Dictionary.getInstance();
        boolean successful = dictionary.addProbability(word1, word2);
        assertEquals(true, successful);
    }
    
    // Successfully add probability for an existing synonym-to-standard matching 
    @Test
    public void testAddProbabilitySynonymToStandard() throws SQLException {
        String word1 = "test5";
        String word2 = "test6";
        Dictionary dictionary = Dictionary.getInstance();
        boolean successful = dictionary.addProbability(word1, word2);
        assertEquals(true, successful);
    }
    
    // Successfully add probability for an existing synonym-to-synonym matching 
    @Test
    public void testAddProbabilitySynonymToSynonym() throws SQLException {
        String word1 = "test5";
        String word2 = "test7";
        Dictionary dictionary = Dictionary.getInstance();
        boolean successful = dictionary.addProbability(word1, word2);
        assertEquals(true, successful);
    }
    
    // Fail to add probability 
    @Test
    public void testAddProbabilityFail() throws SQLException {
        String word1 = "abc";
        String word2 = "efg";
        Dictionary dictionary = Dictionary.getInstance();
        boolean successful = dictionary.addProbability(word1, word2);
        assertEquals(false, successful);
    }  

    // Successfully add probability for an existing standard-to-synonym matching 
    @Test
    public void testReduceProbabilityStandardToSynonym() throws SQLException {
        String word1 = "test3";
        String word2 = "test4";
        Dictionary dictionary = Dictionary.getInstance();
        boolean successful = dictionary.reduceProbability(word1, word2);
        assertEquals(true, successful); 
    }

    // Successfully add probability for an existing synonym-to-standard matching 
    @Test
    public void testReduceProbabilitySynonymToStandard() throws SQLException {
        String word1 = "test5";
        String word2 = "test6";
        Dictionary dictionary = Dictionary.getInstance();
        boolean successful = dictionary.reduceProbability(word1, word2);
        assertEquals(true, successful); 
    }
    
    // Successfully add probability for an existing synonym-to-synonym matching 
    @Test
    public void testReduceProbabilitySynonymToSynonym() throws SQLException {
        String word1 = "test5";
        String word2 = "test7";
        Dictionary dictionary = Dictionary.getInstance();
        boolean successful = dictionary.reduceProbability(word1, word2);
        assertEquals(true, successful); 
    }
    
    // Fail to reduce probability 
    @Test
    public void testReduceProbabilityFail() throws SQLException {
        String word1 = "abc";
        String word2 = "def";
        Dictionary dictionary = Dictionary.getInstance();
        boolean successful = dictionary.reduceProbability(word1, word2);
        assertEquals(false, successful); 
    }

    // Successfully get history from dictionary
    @Test
    public void testGetHistory() throws SQLException {
        Dictionary dictionary = Dictionary.getInstance();
        ArrayList<Statistics> history = dictionary.getHistory();
        assertEquals(true, !history.isEmpty());
    }
    
    // Add record to empty history
    @Test
    public void testAddHistoryEmpty() {
        String oldHistory = "[]";
        String newRecord = "{\"word1\": \"test1\", \"word2\": \"test2\", \"probability\": \"0.5\"}";
        Dictionary dictionary = Dictionary.getInstance();
        String newHistory = dictionary.addHistory(oldHistory, newRecord);
        String expectedResult = "[{\"word1\": \"test1\", \"word2\": \"test2\", \"probability\": \"0.5\"}]";
        assertEquals(expectedResult, newHistory);
    }
    
    // Add record to non-empty history
    @Test
    public void testAddHistoryNonEmpty() {
        String oldHistory = "[{\"word1\": \"test1\", \"word2\": \"test2\", \"probability\": \"0.5\"}]";
        String newRecord = "{\"word1\": \"test3\", \"word2\": \"test4\", \"probability\": \"1.0\"}";
        Dictionary dictionary = Dictionary.getInstance();
        String newHistory = dictionary.addHistory(oldHistory, newRecord);
        String expectedResult = "[{\"word1\": \"test1\", \"word2\": \"test2\", \"probability\": \"0.5\"}, {\"word1\": \"test3\", \"word2\": \"test4\", \"probability\": \"1.0\"}]";
        assertEquals(expectedResult, newHistory);
    }

    // Format history record
    @Test
    public void testFormatHistoryRecord() {
        String word1 = "test1";
        String word2 = "test2";
        float probability = 0.5f;
        Dictionary dictionary = Dictionary.getInstance();
        String historyRecord = dictionary.formatHistoryRecord(word1, word2, probability);
        String expectedResult = "{\"word1\": \"test1\", \"word2\": \"test2\", \"probability\": \"0.5\"}";
        assertEquals(expectedResult, historyRecord);
    }
}
