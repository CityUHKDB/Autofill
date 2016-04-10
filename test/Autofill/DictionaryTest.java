package Autofill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import static org.easymock.EasyMock.expect;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DBUtil.class, Dictionary.class})
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
        String standard = "phone";
        String sql = "SELECT standard FROM Dictionary WHERE synonym = ? ORDER BY probability";
        
        Connection con = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);  
        when(rs.next()).thenReturn(true);
        when(rs.getString("standard")).thenReturn(standard);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(con.prepareStatement(sql)).thenReturn(pstmt);      

        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenReturn(con);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class);
        
        Dictionary dictionary = Dictionary.getInstance();
        String standardResult = dictionary.getStandardWord("phone");
        assertEquals(standard, standardResult);
    }
    
    // Synonym not exist in dictionary
    @Test
    public void testGetStandardWordNotExist() throws SQLException {        
        String sql = "SELECT standard FROM Dictionary WHERE synonym = ? ORDER BY probability";
        
        Connection con = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);  
        when(rs.next()).thenReturn(false);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(con.prepareStatement(sql)).thenReturn(pstmt);      

        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenReturn(con);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class);        
        
        Dictionary dictionary = Dictionary.getInstance();
        String standard = dictionary.getStandardWord("");
        assertEquals(null, standard);
    }
    
    // Database connection error
    @Test(expected = SQLException.class)
    public void testGetStandardWordThrow() throws SQLException {
        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenThrow(SQLException.class);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class);

        Dictionary dictionary = Dictionary.getInstance();
        dictionary.getStandardWord("");
    }
    
    // Synonym exists in dictionary
    @Test
    public void testGetSynonymsExist() throws SQLException {
        String synonym = "phone";
        String synonymSql = "SELECT synonym FROM Dictionary WHERE standard = ? OR standard = ? ORDER BY probability";
        String standardSql = "SELECT standard FROM Dictionary WHERE synonym = ? ORDER BY probability";

        Connection synonymCon = mock(Connection.class);
        PreparedStatement synonymPstmt = mock(PreparedStatement.class);
        ResultSet synonymRs = mock(ResultSet.class);  
        when(synonymRs.next()).thenReturn(true).thenReturn(false);
        when(synonymRs.getString("synonym")).thenReturn(synonym);
        when(synonymPstmt.executeQuery()).thenReturn(synonymRs);
        when(synonymCon.prepareStatement(synonymSql)).thenReturn(synonymPstmt);
        
        Connection standardCon = mock(Connection.class);
        PreparedStatement standardPstmt = mock(PreparedStatement.class);
        ResultSet standardRs = mock(ResultSet.class);  
        when(standardRs.next()).thenReturn(true);
        when(standardRs.getString("standard")).thenReturn("word");
        when(standardPstmt.executeQuery()).thenReturn(standardRs);
        when(standardCon.prepareStatement(standardSql)).thenReturn(standardPstmt);      

        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenReturn(standardCon).thenReturn(synonymCon);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil).times(2);
        replay(DBUtil.class);          
        
        Dictionary dictionary = Dictionary.getInstance();
        ArrayList<String> synonyms = dictionary.getSynonyms("word");
        assertEquals(true, synonyms.contains(synonym));
    }

    // Synonym not exists in dictionary
    @Test
    public void testGetSynonymsNotExist() throws SQLException {
        String synonymSql = "SELECT synonym FROM Dictionary WHERE standard = ? OR standard = ? ORDER BY probability";
        String standardSql = "SELECT standard FROM Dictionary WHERE synonym = ? ORDER BY probability";

        Connection synonymCon = mock(Connection.class);
        PreparedStatement synonymPstmt = mock(PreparedStatement.class);
        ResultSet synonymRs = mock(ResultSet.class);  
        when(synonymRs.next()).thenReturn(false);
        when(synonymPstmt.executeQuery()).thenReturn(synonymRs);
        when(synonymCon.prepareStatement(synonymSql)).thenReturn(synonymPstmt);
        
        Connection standardCon = mock(Connection.class);
        PreparedStatement standardPstmt = mock(PreparedStatement.class);
        ResultSet standardRs = mock(ResultSet.class);  
        when(standardRs.next()).thenReturn(false);
        when(standardPstmt.executeQuery()).thenReturn(standardRs);
        when(standardCon.prepareStatement(standardSql)).thenReturn(standardPstmt);      

        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenReturn(standardCon).thenReturn(synonymCon);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil).times(2);
        replay(DBUtil.class);  
        
        Dictionary dictionary = Dictionary.getInstance();
        ArrayList<String> synonyms = dictionary.getSynonyms("word");
        assertEquals(true, synonyms.isEmpty());
    }

    // Database connection error
    @Test(expected = SQLException.class)
    public void testGetSynonymThrow() throws SQLException {
        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenThrow(SQLException.class);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil).times(2);
        replay(DBUtil.class);

        Dictionary dictionary = Dictionary.getInstance();
        dictionary.getSynonyms("word");
    }
    
    // Get same prefix words from dictionary, contains itself and same prefix word
    @Test
    public void testGetSamePrefixWordsExist() throws SQLException {
        String word = "phone";
        String samePrefixWord = "phone number";
        String standardSql = "SELECT standard FROM Dictionary WHERE standard LIKE ?";
        String synonymSql = "SELECT synonym FROM Dictionary WHERE synonym LIKE ?";
        
        Connection con = mock(Connection.class);
        
        PreparedStatement standardPstmt = mock(PreparedStatement.class);
        ResultSet standardRs = mock(ResultSet.class);  
        when(standardRs.next()).thenReturn(true).thenReturn(false);
        when(standardRs.getString("standard")).thenReturn(samePrefixWord);
        when(standardPstmt.executeQuery()).thenReturn(standardRs);
        when(con.prepareStatement(standardSql)).thenReturn(standardPstmt);      

        PreparedStatement synonymPstmt = mock(PreparedStatement.class);
        ResultSet synonymRs = mock(ResultSet.class);  
        when(synonymRs.next()).thenReturn(false);
        when(synonymPstmt.executeQuery()).thenReturn(synonymRs);
        when(con.prepareStatement(synonymSql)).thenReturn(synonymPstmt);      

        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenReturn(con);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class); 
        
        Dictionary dictionary = Dictionary.getInstance();
        ArrayList<String> samePrefixWords = dictionary.getSamePrefixWords(word);
        assertEquals(true, samePrefixWords.contains(word));
        assertEquals(true, samePrefixWords.contains(samePrefixWord));
    }
 
    // No same prefix words from dictionary, only contains itself
    @Test
    public void testGetSamePrefixWordsNotExist() throws SQLException {
        String word = "phone";
        String standardSql = "SELECT standard FROM Dictionary WHERE standard LIKE ?";
        String synonymSql = "SELECT synonym FROM Dictionary WHERE synonym LIKE ?";
        
        Connection con = mock(Connection.class);
        
        PreparedStatement standardPstmt = mock(PreparedStatement.class);
        ResultSet standardRs = mock(ResultSet.class);  
        when(standardRs.next()).thenReturn(false);
        when(standardPstmt.executeQuery()).thenReturn(standardRs);
        when(con.prepareStatement(standardSql)).thenReturn(standardPstmt);      

        PreparedStatement synonymPstmt = mock(PreparedStatement.class);
        ResultSet synonymRs = mock(ResultSet.class);  
        when(synonymRs.next()).thenReturn(false);
        when(synonymPstmt.executeQuery()).thenReturn(synonymRs);
        when(con.prepareStatement(synonymSql)).thenReturn(synonymPstmt);      

        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenReturn(con);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class); 
        
        Dictionary dictionary = Dictionary.getInstance();
        ArrayList<String> samePrefixWords = dictionary.getSamePrefixWords(word);
        assertEquals(1, samePrefixWords.size());
        assertEquals(true, samePrefixWords.contains(word));
    }
    
    // Database connection error
    @Test(expected = SQLException.class)
    public void testGetSamePrefixWordsThrow() throws SQLException {
        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenThrow(SQLException.class);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class);

        Dictionary dictionary = Dictionary.getInstance();
        dictionary.getSamePrefixWords("word");
    }

    // Successfully add synonym to dictionary
    @Test
    public void testAddSynonym() throws Exception {
        String word1 = "test1";
        String word2 = "test2";
        String sql = "INSERT INTO Dictionary VALUES (?, ?, ?, ?)";
        
        Connection con = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        when(pstmt.executeUpdate()).thenReturn(1);
        when(con.prepareStatement(sql)).thenReturn(pstmt);      

        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenReturn(con);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class);   
        
        Dictionary dictionary = Dictionary.getInstance();
        boolean successful = dictionary.addSynonym(word1, word2);
        assertEquals(true, successful);
    }
    
    // Fail to add synonym to dictionary
    @Test (expected = SQLException.class)
    public void testAddSynonymThrow() throws Exception {
        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenThrow(SQLException.class);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class);
        
        Dictionary dictionary = Dictionary.getInstance();
        dictionary.addSynonym(null, null);
    }
    
    // Successfully add synonym to dictionary
    @Test
    public void testRemoveSynonym() throws SQLException {
        String word1 = "test1";
        String word2 = "test2";
        String sql = "DELETE FROM Dictionary WHERE standard = ? AND synonym = ?";
        
        Connection con = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        when(pstmt.executeUpdate()).thenReturn(1);
        when(con.prepareStatement(sql)).thenReturn(pstmt);      

        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenReturn(con);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class); 
        
        Dictionary dictionary = Dictionary.getInstance();
        boolean successful = dictionary.removeSynonym(word1, word2);
        assertEquals(true, successful);
    }

    // Fail to remove synonym from dictionary
    @Test(expected = SQLException.class)
    public void testRemoveSynonymThrow() throws SQLException {
        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenThrow(SQLException.class);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class);      
        
        Dictionary dictionary = Dictionary.getInstance();
        dictionary.removeSynonym(null, null);
    }
    
    // Successfully add probability for an existing standard-to-synonym matching 
    @Test
    public void testAddProbabilityStandardToSynonym() throws SQLException {
        String word1 = "test3";
        String word2 = "test4";
        String querySql = "SELECT probability, history FROM Dictionary WHERE standard = ? AND synonym = ?";
        String updateSql = "UPDATE Dictionary SET probability = ?, history = ? WHERE standard = ? AND synonym = ?";       
        
        Connection con = mock(Connection.class);
        PreparedStatement queryPstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true);
        when(rs.getString("history")).thenReturn("[]");
        when(queryPstmt.executeQuery()).thenReturn(rs);
        when(con.prepareStatement(querySql)).thenReturn(queryPstmt);
        
        PreparedStatement updatePstmt = mock(PreparedStatement.class);
        when(updatePstmt.executeUpdate()).thenReturn(1);
        when(con.prepareStatement(updateSql)).thenReturn(updatePstmt);

        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenReturn(con);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class); 
        
        Dictionary dictionary = Dictionary.getInstance();
        boolean successful = dictionary.addProbability(word1, word2);
        assertEquals(true, successful);
        
    }
    
    // Successfully add probability for an existing synonym-to-standard matching 
    @Test
    public void testAddProbabilitySynonymToStandard() throws SQLException {
        String word1 = "test5";
        String word2 = "test6";  
        String querySql = "SELECT probability, history FROM Dictionary WHERE standard = ? AND synonym = ?";
        String updateSql = "UPDATE Dictionary SET probability = ?, history = ? WHERE standard = ? AND synonym = ?";       
        
        Connection con = mock(Connection.class);
        PreparedStatement queryPstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(false).thenReturn(true);
        when(rs.getString("history")).thenReturn("[]");
        when(queryPstmt.executeQuery()).thenReturn(rs);
        when(con.prepareStatement(querySql)).thenReturn(queryPstmt);
        
        PreparedStatement updatePstmt = mock(PreparedStatement.class);
        when(updatePstmt.executeUpdate()).thenReturn(1);
        when(con.prepareStatement(updateSql)).thenReturn(updatePstmt);

        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenReturn(con);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class);         
        
        Dictionary dictionary = Dictionary.getInstance();
        boolean successful = dictionary.addProbability(word1, word2);
        assertEquals(true, successful);
    }
    
    // Successfully add probability for an existing synonym-to-synonym matching 
    @Test
    public void testAddProbabilitySynonymToSynonym() throws SQLException {
        String word1 = "test5";
        String word2 = "test7";
        String querySql = "SELECT probability, history FROM Dictionary WHERE standard = ? AND synonym = ?";
        String querySql2 = "SELECT D1.standard As standard, D1.probability As probability, D1.history As history FROM Dictionary D1, Dictionary D2 WHERE D1.standard = D2.standard AND D1.synonym = ? AND D2.synonym = ?";
        String updateSql = "UPDATE Dictionary SET probability = ROUND(LEAST(1, probability + 0.1), 1), history = ? WHERE standard = ? AND (synonym = ? OR synonym = ?)";

        Connection con = mock(Connection.class);
        PreparedStatement queryPstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(false);
        when(queryPstmt.executeQuery()).thenReturn(rs);
        when(con.prepareStatement(querySql)).thenReturn(queryPstmt);
        
        PreparedStatement queryPstmt2 = mock(PreparedStatement.class);
        ResultSet rs2 = mock(ResultSet.class);
        when(rs2.next()).thenReturn(true);
        when(rs2.getString("history")).thenReturn("[]");
        when(queryPstmt2.executeQuery()).thenReturn(rs2);
        when(con.prepareStatement(querySql2)).thenReturn(queryPstmt2);

        PreparedStatement updatePstmt = mock(PreparedStatement.class);
        when(updatePstmt.executeUpdate()).thenReturn(1);
        when(con.prepareStatement(updateSql)).thenReturn(updatePstmt);
        
        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenReturn(con);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class);   
        
        Dictionary dictionary = Dictionary.getInstance();
        boolean successful = dictionary.addProbability(word1, word2);
        assertEquals(true, successful);
    }
    
    // Fail to add probability 
    @Test(expected = SQLException.class)
    public void testAddProbabilityThrow() throws SQLException {
        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenThrow(SQLException.class);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class);  
        
        Dictionary dictionary = Dictionary.getInstance();
        dictionary.addProbability(null, null);
    }  

    // Successfully reduce probability for an existing standard-to-synonym matching 
    @Test
    public void testReduceProbabilityStandardToSynonym() throws SQLException {
        String word1 = "test3";
        String word2 = "test4";
        String querySql = "SELECT probability, history FROM Dictionary WHERE standard = ? AND synonym = ?";
        String updateSql = "UPDATE Dictionary SET probability = ?, history = ? WHERE standard = ? AND synonym = ?";
        
        Connection con = mock(Connection.class);
        PreparedStatement queryPstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true);
        when(rs.getFloat("probability")).thenReturn(0.5f);
        when(rs.getString("history")).thenReturn("[]");
        when(queryPstmt.executeQuery()).thenReturn(rs);
        when(con.prepareStatement(querySql)).thenReturn(queryPstmt);
        
        PreparedStatement updatePstmt = mock(PreparedStatement.class);
        when(updatePstmt.executeUpdate()).thenReturn(1);
        when(con.prepareStatement(updateSql)).thenReturn(updatePstmt);

        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenReturn(con);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class);         
        
        Dictionary dictionary = Dictionary.getInstance();
        boolean successful = dictionary.reduceProbability(word1, word2);
        assertEquals(true, successful); 
    }

    // Successfully reduce probability for an existing standard-to-synonym matching 
    // Remove the entry in database because probability = 0
    @Test
    public void testReduceProbabilityStandardToSynonymRemove() throws SQLException {
        String word1 = "test3";
        String word2 = "test4";
        String querySql = "SELECT probability, history FROM Dictionary WHERE standard = ? AND synonym = ?";
        String updateSql = "UPDATE Dictionary SET probability = ?, history = ? WHERE standard = ? AND synonym = ?";
        String removeSql = "DELETE FROM Dictionary WHERE standard = ? AND synonym = ?";
        
        Connection reduceCon = mock(Connection.class);
        PreparedStatement queryPstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true);
        when(rs.getFloat("probability")).thenReturn(0.1f);
        when(rs.getString("history")).thenReturn("[]");
        when(queryPstmt.executeQuery()).thenReturn(rs);
        when(reduceCon.prepareStatement(querySql)).thenReturn(queryPstmt);
        
        PreparedStatement updatePstmt = mock(PreparedStatement.class);
        when(updatePstmt.executeUpdate()).thenReturn(1);
        when(reduceCon.prepareStatement(updateSql)).thenReturn(updatePstmt);

        Connection removeCon = mock(Connection.class);
        PreparedStatement removePstmt = mock(PreparedStatement.class);
        when(removePstmt.executeUpdate()).thenReturn(1);
        when(removeCon.prepareStatement(removeSql)).thenReturn(removePstmt);
        
        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenReturn(reduceCon).thenReturn(removeCon);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil).times(2);
        replay(DBUtil.class);         
        
        Dictionary dictionary = Dictionary.getInstance();
        boolean successful = dictionary.reduceProbability(word1, word2);
        assertEquals(true, successful); 
    }
    
    // Successfully reduce probability for an existing synonym-to-standard matching 
    @Test
    public void testReduceProbabilitySynonymToStandard() throws SQLException {
        String word1 = "test5";
        String word2 = "test6";
        String querySql = "SELECT probability, history FROM Dictionary WHERE standard = ? AND synonym = ?";
        String updateSql = "UPDATE Dictionary SET probability = ?, history = ? WHERE standard = ? AND synonym = ?";
        
        Connection con = mock(Connection.class);
        PreparedStatement queryPstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(false).thenReturn(true);
        when(rs.getFloat("probability")).thenReturn(0.5f);
        when(rs.getString("history")).thenReturn("[]");
        when(queryPstmt.executeQuery()).thenReturn(rs);
        when(con.prepareStatement(querySql)).thenReturn(queryPstmt);
        
        PreparedStatement updatePstmt = mock(PreparedStatement.class);
        when(updatePstmt.executeUpdate()).thenReturn(1);
        when(con.prepareStatement(updateSql)).thenReturn(updatePstmt);

        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenReturn(con);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class); 
        
        Dictionary dictionary = Dictionary.getInstance();
        boolean successful = dictionary.reduceProbability(word1, word2);
        assertEquals(true, successful); 
    }

    // Successfully reduce probability for an existing synonym-to-standard matching 
    // Remove the entry in database because probability = 0
    @Test
    public void testReduceProbabilitySynonymToStandardRemove() throws SQLException {
        String word1 = "test5";
        String word2 = "test6";
        String querySql = "SELECT probability, history FROM Dictionary WHERE standard = ? AND synonym = ?";
        String updateSql = "UPDATE Dictionary SET probability = ?, history = ? WHERE standard = ? AND synonym = ?";
        String removeSql = "DELETE FROM Dictionary WHERE standard = ? AND synonym = ?";
       
        Connection reduceCon = mock(Connection.class);
        PreparedStatement queryPstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(false).thenReturn(true);
        when(rs.getFloat("probability")).thenReturn(0.1f);
        when(rs.getString("history")).thenReturn("[]");
        when(queryPstmt.executeQuery()).thenReturn(rs);
        when(reduceCon.prepareStatement(querySql)).thenReturn(queryPstmt);
        
        PreparedStatement updatePstmt = mock(PreparedStatement.class);
        when(updatePstmt.executeUpdate()).thenReturn(1);
        when(reduceCon.prepareStatement(updateSql)).thenReturn(updatePstmt);

        Connection removeCon = mock(Connection.class);
        PreparedStatement removePstmt = mock(PreparedStatement.class);
        when(removePstmt.executeUpdate()).thenReturn(1);
        when(removeCon.prepareStatement(removeSql)).thenReturn(removePstmt);
        
        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenReturn(reduceCon).thenReturn(removeCon);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil).times(2);
        replay(DBUtil.class);   
        
        Dictionary dictionary = Dictionary.getInstance();
        boolean successful = dictionary.reduceProbability(word1, word2);
        assertEquals(true, successful); 
    }
    
    // Successfully reduce probability for an existing synonym-to-synonym matching 
    @Test
    public void testReduceProbabilitySynonymToSynonym() throws SQLException {
        String word1 = "test5";
        String word2 = "test7";
        String querySql = "SELECT probability, history FROM Dictionary WHERE standard = ? AND synonym = ?";
        String querySql2 = "SELECT D1.standard As standard, D1.probability As probability, D1.history As history FROM Dictionary D1, Dictionary D2 WHERE D1.standard = D2.standard AND D1.synonym = ? AND D2.synonym = ?";
        String updateSql = "UPDATE Dictionary SET probability = ROUND(LEAST(1, probability - 0.1), 1), history = ? WHERE standard = ? AND (synonym = ? OR synonym = ?)";

        Connection con = mock(Connection.class);
        PreparedStatement queryPstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(false);

        when(queryPstmt.executeQuery()).thenReturn(rs);
        when(con.prepareStatement(querySql)).thenReturn(queryPstmt);

        PreparedStatement queryPstmt2 = mock(PreparedStatement.class);
        ResultSet rs2 = mock(ResultSet.class);
        when(rs2.next()).thenReturn(true);
        when(rs.getFloat("probability")).thenReturn(0.5f);
        when(rs2.getString("history")).thenReturn("[]");
        when(queryPstmt2.executeQuery()).thenReturn(rs2);
        when(con.prepareStatement(querySql2)).thenReturn(queryPstmt2);

        PreparedStatement updatePstmt = mock(PreparedStatement.class);
        when(updatePstmt.executeUpdate()).thenReturn(1);
        when(con.prepareStatement(updateSql)).thenReturn(updatePstmt);
        
        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenReturn(con);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class); 
        
        Dictionary dictionary = Dictionary.getInstance();
        boolean successful = dictionary.reduceProbability(word1, word2);
        assertEquals(true, successful);
    }
  
    // Successfully reduce probability for an existing synonym-to-synonym matching 
    // Remove the entry in database because probability = 0
    @Test
    public void testReduceProbabilitySynonymToSynonymRemove() throws SQLException {
        String word1 = "test5";
        String word2 = "test7";
        String querySql = "SELECT probability, history FROM Dictionary WHERE standard = ? AND synonym = ?";
        String querySql2 = "SELECT D1.standard As standard, D1.probability As probability, D1.history As history FROM Dictionary D1, Dictionary D2 WHERE D1.standard = D2.standard AND D1.synonym = ? AND D2.synonym = ?";
        String updateSql = "UPDATE Dictionary SET probability = ROUND(LEAST(1, probability - 0.1), 1), history = ? WHERE standard = ? AND (synonym = ? OR synonym = ?)";
        String removeSql = "DELETE FROM Dictionary WHERE standard = ? AND synonym = ?";

        Connection reduceCon = mock(Connection.class);
        PreparedStatement queryPstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(false);

        when(queryPstmt.executeQuery()).thenReturn(rs);
        when(reduceCon.prepareStatement(querySql)).thenReturn(queryPstmt);

        PreparedStatement queryPstmt2 = mock(PreparedStatement.class);
        ResultSet rs2 = mock(ResultSet.class);
        when(rs2.next()).thenReturn(true);
        when(rs.getFloat("probability")).thenReturn(0.1f);
        when(rs2.getString("history")).thenReturn("[]");
        when(queryPstmt2.executeQuery()).thenReturn(rs2);
        when(reduceCon.prepareStatement(querySql2)).thenReturn(queryPstmt2);

        PreparedStatement updatePstmt = mock(PreparedStatement.class);
        when(updatePstmt.executeUpdate()).thenReturn(1);
        when(reduceCon.prepareStatement(updateSql)).thenReturn(updatePstmt);
        
        Connection removeCon = mock(Connection.class);
        PreparedStatement removePstmt = mock(PreparedStatement.class);
        when(removePstmt.executeUpdate()).thenReturn(1);
        when(removeCon.prepareStatement(removeSql)).thenReturn(removePstmt);
        
        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenReturn(reduceCon).thenReturn(removeCon);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil).times(2);
        replay(DBUtil.class); 
        
        Dictionary dictionary = Dictionary.getInstance();
        boolean successful = dictionary.reduceProbability(word1, word2);
        assertEquals(true, successful);
    }
    
    // Fail to reduce probability 
    @Test(expected = SQLException.class)
    public void testReduceProbabilityFail() throws SQLException {
        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenThrow(SQLException.class);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class); 
        
        Dictionary dictionary = Dictionary.getInstance();
        dictionary.reduceProbability(null, null);
    }

    // Successfully get history from dictionary
    @Test
    public void testGetHistory() throws SQLException {
        String sql = "SELECT standard, synonym, history FROM Dictionary ORDER BY standard";
        
        Connection con = mock(Connection.class);
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(pstmt.executeQuery()).thenReturn(rs);
        when(con.prepareStatement(sql)).thenReturn(pstmt);
        
        DBUtil dbUtil = mock(DBUtil.class);
        when(dbUtil.getDBConnection()).thenReturn(con);
        mockStatic(DBUtil.class);
        expect(DBUtil.getInstance()).andReturn(dbUtil);
        replay(DBUtil.class); 
        
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
