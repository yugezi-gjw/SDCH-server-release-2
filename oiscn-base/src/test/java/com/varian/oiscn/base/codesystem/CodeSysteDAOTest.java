package com.varian.oiscn.base.codesystem;

import com.varian.oiscn.base.diagnosis.BodyPart;
import com.varian.oiscn.base.util.MockDatabaseConnection;
import com.varian.oiscn.base.util.MockPreparedStatement;
import com.varian.oiscn.base.util.MockResultSet;
import com.varian.oiscn.core.codesystem.CodeValueDTO;
import com.varian.oiscn.util.I18nReader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by BHP9696 on 2017/7/28.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({CodeSystemDAO.class, MockPreparedStatement.class})
public class CodeSysteDAOTest {
    private Connection connection;
    private CodeSystemDAO codeSystemDAO;

    @Before
    public void setup() {
        Locale.setDefault(Locale.CHINA);
        connection = PowerMockito.mock(MockDatabaseConnection.class);
        codeSystemDAO = new CodeSystemDAO();
    }

    @Test
    public void givenCodeValueDTOsWhenCreateThenNoExceptionThrows() {
        try {
            List<CodeValueDTO> codeValueDTOs = Arrays.asList(new CodeValueDTO());
            PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
            PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
            codeSystemDAO.create(connection, codeValueDTOs);
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenSchemeKeywordsWhenQueryDiagnosisThenReturnCodeValueDTOs() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        PowerMockito.when(rs.getString(1)).thenReturn("{\"code\":123,\"system\":\"\",\"value\":3782,\"desc\":\"" + I18nReader.getLocaleValueByKey("CodeSysteDAOTests.description") + "\"}");
        List<CodeValueDTO> codeValueDTOS = codeSystemDAO.queryDiagnosis(connection, "scheme1", "key", "");
        Assert.assertTrue(codeValueDTOS.size() > 0);
    }

    @Test
    public void givenSchemeKeywordsWhenQueryDiagnosisThenReturnEmptyCollection() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(false);
        List<CodeValueDTO> codeValueDTOS = codeSystemDAO.queryDiagnosis(connection, "scheme1", "key", "");
        Assert.assertTrue(codeValueDTOS.size() == 0);
    }

    @Test
    public void givenSchemeWhenIsDiagnosisExistedThenReurnTrueOrFalse() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true);
        PowerMockito.when(rs.getInt(1)).thenReturn(2);
        boolean exists = codeSystemDAO.isDiagnosisExisted(connection, "scheme");
        Assert.assertTrue(exists);
        PowerMockito.when(rs.getInt(1)).thenReturn(0);
        exists = codeSystemDAO.isDiagnosisExisted(connection, "scheme");
        Assert.assertFalse(exists);

    }

    @Test
    public void givenWhenIsDiagnosisPairExistedThenReturnTrueOrFalse() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true);
        PowerMockito.when(rs.getInt(1)).thenReturn(2);
        boolean exists = codeSystemDAO.isBodyPartExisted(connection);
        Assert.assertTrue(exists);
        PowerMockito.when(rs.getInt(1)).thenReturn(0);
        exists = codeSystemDAO.isBodyPartExisted(connection);
        Assert.assertFalse(exists);

    }

    @Test
    public void givenKeywordsWhenBodyPartsThenReturnEmptyCollection() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(false);
        List<BodyPart> bodyParts = codeSystemDAO.queryBodyParts(connection,"key", "");
        Assert.assertTrue(bodyParts.size() == 0);
    }

    @Test
    public void givenKeywordsWhenBodyPartsThenReturnDiagnosisPairList() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet rs = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(rs);
        PowerMockito.when(rs.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(rs.getString("code")).thenReturn("1");
        PowerMockito.when(rs.getString("description")).thenReturn("feibu");
        PowerMockito.when(rs.getString("pinyin")).thenReturn("FB");
        List<BodyPart> bodyParts= codeSystemDAO.queryBodyParts(connection, "FB", "");
        Assert.assertTrue(bodyParts.size() > 0);
    }

    @Test
    public void givenBodyPartsThenBatchInsert() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        BodyPart bodyPart = new BodyPart("code", "description", "pinyin");
        codeSystemDAO.createBodyParts(connection, Arrays.asList(bodyPart));
        Mockito.verify(ps).executeBatch();
    }

}
