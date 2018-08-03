package com.varian.oiscn.base.codesystem;

import com.varian.oiscn.base.diagnosis.BodyPart;
import com.varian.oiscn.base.diagnosis.BodyPartVO;
import com.varian.oiscn.base.util.MockDatabaseConnection;
import com.varian.oiscn.base.util.MockDtoUtil;
import com.varian.oiscn.base.util.MockPreparedStatement;
import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.codesystem.CodeSystem;
import com.varian.oiscn.core.codesystem.CodeValueDTO;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by gbt1220 on 6/19/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({CodeSystemServiceImp.class, ConnectionPool.class, CodeValueAssembler.class, BasicDataSourceFactory.class})
public class CodeSystemServiceImpTest {

    private Connection con;

    private CodeSystemDAO codeSystemDAO;

    private CodeSystemServiceImp codeSystemServiceImp;

    @Before
    public void setup() throws Exception {
        PowerMockito.mockStatic(BasicDataSourceFactory.class);
        PowerMockito.mockStatic(ConnectionPool.class);
        con = PowerMockito.mock(MockDatabaseConnection.class);
        PowerMockito.when(ConnectionPool.getConnection()).thenReturn(con);
        codeSystemDAO = PowerMockito.mock(CodeSystemDAO.class);
        PowerMockito.whenNew(CodeSystemDAO.class).withNoArguments().thenReturn(codeSystemDAO);
        PowerMockito.mockStatic(CodeValueAssembler.class);
        codeSystemServiceImp = new CodeSystemServiceImp();
    }

    @Test
    public void givenCodeSystemWhenCreateAndDaoThrowExceptionThenDoNothing() throws SQLException {
        CodeSystem codeSystem = MockDtoUtil.givenCodeSystem();
        PowerMockito.when(CodeValueAssembler.assemblerCodeValueDTO(codeSystem, codeSystem.getCodeValues().get(0))).thenReturn(new CodeValueDTO());
        PowerMockito.doThrow(new SQLException()).when(codeSystemDAO).create(con, Arrays.asList(new CodeValueDTO()));
        try {
			codeSystemServiceImp.create(codeSystem);
		} catch (Exception e) {
			Assert.assertNotNull(e);
		}
    }

    @Test
    public void givenCodeSystemWhenCreateThenCreateToDB() throws SQLException {
        CodeSystem codeSystem = MockDtoUtil.givenCodeSystem();
        PowerMockito.when(CodeValueAssembler.assemblerCodeValueDTO(codeSystem, codeSystem.getCodeValues().get(0))).thenReturn(new CodeValueDTO());

        PowerMockito.doNothing().when(codeSystemDAO).create(con, Arrays.asList(new CodeValueDTO()));
        codeSystemServiceImp.create(codeSystem);
    }

    @Test
    public void givenSchemeNameWhenIsDiagnosisExistedAndDaoThrowExceptionThenReturnTrue() throws SQLException {
        String scheme = "scheme";
        PowerMockito.doThrow(new SQLException()).when(codeSystemDAO).isDiagnosisExisted(con, scheme);
        Assert.assertFalse(codeSystemServiceImp.isDiagnosisExisted(scheme));
    }

    @Test
    public void givenSchemeNameWhenIsDiagnosisExistedThenReturnTrue() throws SQLException {
        String scheme = "scheme";
        PowerMockito.when(codeSystemDAO.isDiagnosisExisted(con, scheme)).thenReturn(true);
        Assert.assertTrue(codeSystemServiceImp.isDiagnosisExisted(scheme));
    }

    @Test
    public void givenSchemeNameAndKeywordWhenqueryDiagnosisAndDaoThrowExceptionThenReturnEmpty() throws SQLException {
        String scheme = "scheme";
        String keyword = "keyword";
        PowerMockito.doThrow(new SQLException()).when(codeSystemDAO).queryDiagnosis(con, scheme, keyword, "");
        Assert.assertEquals(new ArrayList<>(), codeSystemServiceImp.queryDiagnosis(scheme, keyword, ""));
    }

    @Test
    public void givenSchemeNameAndKeywordWhenqueryDiagnosisThenReturnDiagnosisDTOList() throws SQLException {
        String scheme = "scheme";
        String keyword = "keyword";
        List<CodeValueDTO> list = new ArrayList<>();
        PowerMockito.when(codeSystemDAO.queryDiagnosis(con, scheme, keyword, "")).thenReturn(list);
        Assert.assertEquals(list, codeSystemServiceImp.queryDiagnosis(scheme, keyword, ""));
    }

    @Test
    public void givenIsDiagnosisPairsExistedThenReturnTrue() throws SQLException {
        PowerMockito.when(codeSystemDAO.isBodyPartExisted(con)).thenReturn(true);
        Assert.assertTrue(codeSystemServiceImp.isBodyPartExisted());
    }

    @Test
    public void givenConditionWhenQueryBodyPartsThenReturnList() throws SQLException {
        List<BodyPart> bodyParts  = Arrays.asList(new BodyPart("1","fb","FB"));
        PowerMockito.when(codeSystemDAO.queryBodyParts(con,"FB", "")).thenReturn(bodyParts);
        List<BodyPartVO> rlist = codeSystemServiceImp.queryBodyParts("FB", "");
        Assert.assertTrue(!rlist.isEmpty());
    }

    @Test
    public void givenBodyPartsThenCreate() throws SQLException {
        List<BodyPartVO> bodyPartVOList = Arrays.asList(new BodyPartVO("1","fb",""));
        List<BodyPart> bodyPartList = Arrays.asList(new BodyPart("1","fb",""));
        codeSystemServiceImp.createBodyParts(bodyPartVOList);
        Mockito.verify(codeSystemDAO).createBodyParts(con, bodyPartList);
    }
}
