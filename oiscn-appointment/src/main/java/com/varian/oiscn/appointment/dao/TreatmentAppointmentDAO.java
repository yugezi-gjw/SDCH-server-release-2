package com.varian.oiscn.appointment.dao;

import com.varian.oiscn.appointment.dto.TreatmentAppointmentDTO;
import com.varian.oiscn.base.dao.AbstractDAO;
import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.core.appointment.AppointmentStatusEnum;
import com.varian.oiscn.core.pagination.Pagination;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.util.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Treatment Appoint DAO.<br>
 */
@Slf4j
public class TreatmentAppointmentDAO extends AbstractDAO<TreatmentAppointmentDTO> {

	protected static final String TABLE_NAME = "TreatmentAppointment";
	protected static final String COLUMN_NAME_JSON = "";
	protected static final String COLUMN_NAME_LIST_WITH_ID = "uid, appointmentId,patientSer,hisId,encounterId,deviceId, startTime, endTime, activityCode, status, createdUser, createdDate, lastUpdatedUser, lastUpdatedDate";
	protected static final String SQL_INSERT = "INSERT INTO " + TABLE_NAME + " (" + COLUMN_NAME_LIST_WITH_ID
			+ ") VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
	protected static final String SQL_SELECT_BASE = "SELECT id, " + COLUMN_NAME_LIST_WITH_ID + " FROM " + TABLE_NAME;
	protected static final String SQL_SELECT_BY_PATIENTID_ACTIVITYCODE = SQL_SELECT_BASE + " WHERE patientSer = ? "
			+ " AND activityCode = ? AND status != '" + AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.CANCELLED) + "'"
			+ " ORDER BY startTime ASC, endTime ASC";
	protected static final String SQL_SELECT_BY_APPOINTMENTID_OR_UID_ID = SQL_SELECT_BASE + " WHERE uid = ? OR appointmentId=?";

	public TreatmentAppointmentDAO(UserContext userContext) {
		super(userContext);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.varian.oiscn.base.dao.AbstractDAO#getTableName()
	 */
	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.varian.oiscn.base.dao.AbstractDAO#getJsonbColumnName()
	 */
	@Override
	protected String getJsonbColumnName() {
		return COLUMN_NAME_JSON;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.varian.oiscn.base.dao.AbstractDAO#create(java.sql.Connection,
	 * java.lang.Object)
	 */
	@Override
	public String create(Connection con, TreatmentAppointmentDTO dto) throws SQLException {
		String newId = StringUtils.EMPTY;
		if (dto == null) {
			return newId;
		}

		Timestamp nowTs = new Timestamp(new java.util.Date().getTime());
		String userName = userContext.getLogin().getName();

		PreparedStatement ps = null;
		ResultSet rs = null;
		int idx = 1;
		try {
			ps = con.prepareStatement(SQL_INSERT, PreparedStatement.RETURN_GENERATED_KEYS);
			ps.setString(idx++, UUID.randomUUID().toString());
			ps.setString(idx++, dto.getAppointmentId());
			ps.setLong(idx++,dto.getPatientSer());
			ps.setString(idx++, dto.getHisId());
			ps.setLong(idx++,Long.parseLong(dto.getEncounterId()));
			ps.setString(idx++, dto.getDeviceId());
			ps.setTimestamp(idx++, DateUtil.transferTimestampFromUtilToSql(dto.getStartTime()));
			ps.setTimestamp(idx++, DateUtil.transferTimestampFromUtilToSql(dto.getEndTime()));
			ps.setString(idx++, dto.getActivityCode());
			ps.setString(idx++, dto.getStatus());
			ps.setString(idx++, userName);
			ps.setTimestamp(idx++, nowTs);
			ps.setString(idx++, userName);
			ps.setTimestamp(idx++, nowTs);
			int rowNum = ps.executeUpdate();
			if (rowNum > 0) {
				rs = ps.getGeneratedKeys();
				if (rs.next()) {
					newId = rs.getString(1);
				}
			}
		} finally {
			DatabaseUtil.safeCloseAll(null, ps, rs);
		}
		return newId;
	}

	/**
	 * Query Treatment Appointment with HisID, Date, and Activity Code.<br>
	 *
	 * @param con
	 *            Connection
	 * @param patientSer
	 *            patientSer
	 * @param day
	 *            The appointment Day
	 * @param activityCode
	 *            Activity Code
	 * @return Appointment List for Treatment
     * @throws SQLException
     */
	public List<TreatmentAppointmentDTO> queryByPatientSerAndDateAndActivity(Connection con, Long patientSer, Date day,
																			 String activityCode) throws SQLException {
        PreparedStatement ps = null;
		ResultSet rs = null;
		List<TreatmentAppointmentDTO> dtoList = new ArrayList<>();
		try {
			String sql = SQL_SELECT_BASE + " WHERE patientSer = ?  AND startTime >= ?  AND startTime  < ? AND activityCode = ?  ORDER BY startTime ASC, endTime ASC";
			ps = con.prepareStatement(sql);
			java.sql.Date startDate = java.sql.Date.valueOf(DateUtil.formatDate(day, DateUtil.DATE_FORMAT));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new java.util.Date(startDate.getTime()));
			calendar.add(Calendar.DAY_OF_MONTH,1);
			Date endDate = calendar.getTime();

			ps.setLong(1, patientSer);
			ps.setDate(2, startDate);
			ps.setDate(3, new java.sql.Date(endDate.getTime()));
			ps.setString(4, activityCode);
			rs = ps.executeQuery();
			while (rs.next()) {
				dtoList.add(getTreatmentAppointmentDTO(rs));
			}
		} finally {
			DatabaseUtil.safeCloseAll(null, ps, rs);
		}
		return dtoList;
	}

	public Pagination<TreatmentAppointmentDTO> queryByPatientSerListAndDatePagination(Connection con, List<Long> patientSerList, Date startDate, Date endDate, String sort, String countPerPage, String pageNumber) throws SQLException {
		return queryByPatientSerOrDeviceIdListAndDateStatusPagination(con, patientSerList, null, startDate, endDate, Arrays.asList(AppointmentStatusEnum.BOOKED), sort, countPerPage, pageNumber);
	}

	public Pagination<TreatmentAppointmentDTO> queryByDeviceIdListAndDatePagination(Connection con, List<String> deviceIdList, Date startDate, Date endDate,List<AppointmentStatusEnum> statusEnums, String sort, String countPerPage, String pageNumber) throws SQLException {
		return queryByPatientSerOrDeviceIdListAndDateStatusPagination(con, null, deviceIdList, startDate, endDate, statusEnums, sort, countPerPage, pageNumber);
	}

	public Pagination<TreatmentAppointmentDTO> queryByPatientSerOrDeviceIdListAndDateStatusPagination(Connection con, List<Long> patientSerList, List<String> deviceIdList, Date startDate, Date endDate, List<AppointmentStatusEnum> statusEnums, String sort, String countPerPage, String pageNumber) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Pagination<TreatmentAppointmentDTO> result = new Pagination<>();
		List<TreatmentAppointmentDTO> list = new ArrayList<>();
		result.setLstObject(list);
		List<Object> params = new ArrayList<>();
		if (patientSerList != null) {
			params.addAll(patientSerList);
		}
		if (deviceIdList != null) {
			params.addAll(deviceIdList);
		}
		try {
			String sql;
			String selectColumns = "id,"+COLUMN_NAME_LIST_WITH_ID;
			String selectBase = "SELECT "+selectColumns+" FROM "+getTableName()+" WHERE 1=1 ";
			StringBuilder condition = new StringBuilder();
			if (patientSerList != null && !patientSerList.isEmpty()) {
				condition.append(" and (");
				int size = patientSerList.size();
				for (int i = 0; i < size; i++) {
					if (i != size - 1) {
						condition.append("patientSer = ? or ");
					} else {
						condition.append("patientSer = ?) ");
					}
				}
			}
			if (deviceIdList != null && !deviceIdList.isEmpty()) {
				condition.append(" and (");
				int size = deviceIdList.size();
				for (int i = 0; i < size; i++) {
					if (i != size - 1) {
						condition.append("deviceId = ? or ");
					} else {
						condition.append("deviceId = ?) ");
					}
				}
			}
			if (statusEnums != null && !statusEnums.isEmpty()) {
				condition.append(" and (");
				int size = statusEnums.size();
				for (int i = 0; i < size; i++) {
					params.add(AppointmentStatusEnum.getDisplay(statusEnums.get(i)));
					if (i != size - 1) {
						condition.append("status = ? or ");
					} else {
						condition.append("status = ?) ");
					}
				}
			}
			condition.append(" and startTime >= ? and startTime < ? ");
			String sqlSort = "";
			if (!StringUtils.isEmpty(sort)) {
				sqlSort = sort;
			}
			String orderByCondition = "startTime "+sqlSort+",endTime "+sqlSort;
			if (!StringUtils.isEmpty(countPerPage) && !StringUtils.isEmpty(pageNumber)) {
				sql = "SELECT "+selectColumns + " FROM ("
						+"SELECT ROW_NUMBER() OVER(ORDER BY "+orderByCondition+") n,"+selectColumns+" FROM ("
							+"SELECT TOP (?) "+selectColumns+" FROM "+this.getTableName()+" WHERE 1=1 " + condition.toString()+" ) t"
						+") tt WHERE tt.n >?";
			}else{
				sql = selectBase+condition+" ORDER BY "+orderByCondition;
			}

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			params.add(new java.sql.Date(DateUtil.parse(simpleDateFormat.format(startDate) + " 00:00:00").getTime()));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(endDate);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			params.add(new java.sql.Date(DateUtil.parse(simpleDateFormat.format(calendar.getTime()) + " 00:00:00").getTime()));

			if (!StringUtils.isEmpty(countPerPage) && !StringUtils.isEmpty(pageNumber)) {
				params.add(0,Integer.parseInt(countPerPage) * Integer.parseInt(pageNumber));
				params.add(Integer.parseInt(countPerPage) * (Integer.parseInt(pageNumber) -1));
			}
			ps = con.prepareStatement(sql);
			for(int i=0;i<params.size();i++){
				ps.setObject(i+1,params.get(i));
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				list.add(getTreatmentAppointmentDTO(rs));
			}
		} catch (ParseException e) {
			log.error("ParseException: {}", e.getMessage());
		} finally {
			DatabaseUtil.safeCloseAll(null, ps, rs);
		}
		result.setTotalCount(list.size());
		return result;
	}
	/*
     * (non-Javadoc)
     *
     * @see com.varian.oiscn.base.dao.AbstractDAO#update(java.sql.Connection,
     * java.lang.Object, java.lang.String)
     */
	@Override
	public boolean update(Connection con, TreatmentAppointmentDTO dto, String id) throws SQLException {
		PreparedStatement ps = null;
		int affectedRow = 0;
		Timestamp nowTs = new Timestamp(new java.util.Date().getTime());
		String userName = userContext.getLogin().getName();
		try {
			String sql = " UPDATE " + TABLE_NAME + " SET appointmentId = ?"+", patientSer = ?" + ", hisId = ?"
					+ ", deviceId = ?" + ", startTime = ?" + ", endTime = ?" + ", activityCode = ?" + ", status = ?"
					+ ", lastUpdatedUser = ?" + ", lastUpdatedDate = ? " + " WHERE id = ? ";
			ps = con.prepareStatement(sql.toString());
			int idx = 1;
			ps.setString(idx++, dto.getAppointmentId());
			ps.setLong(idx++,dto.getPatientSer());
			ps.setString(idx++, dto.getHisId());
			ps.setString(idx++, dto.getDeviceId());
			ps.setTimestamp(idx++, DateUtil.transferTimestampFromUtilToSql(dto.getStartTime()));
			ps.setTimestamp(idx++, DateUtil.transferTimestampFromUtilToSql(dto.getEndTime()));
			ps.setString(idx++, dto.getActivityCode());
			ps.setString(idx++, dto.getStatus());
			ps.setString(idx++, userName);
			ps.setTimestamp(idx++, nowTs);
			ps.setLong(idx++, Long.parseLong(id));
			affectedRow = ps.executeUpdate();
		} finally {
			DatabaseUtil.safeCloseStatement(ps);
		}
		return affectedRow > 0;
	}

	/**
	 * Update by StartTime & HisId & ActivityCode.<br>
	 *
	 * @param con
	 *            Connection
	 * @param dto
	 *            TreatmentAppointmentDTO
	 * @return the updated rows
	 * @throws SQLException
	 */
	public int updateByStartTimeAndPatientSerAndActivity(Connection con, TreatmentAppointmentDTO dto) throws SQLException {
		PreparedStatement ps = null;
		int affectedRow = 0;
		Timestamp nowTs = new Timestamp(new java.util.Date().getTime());
		String userName = userContext.getLogin().getName();
		try {
			String sql = " UPDATE " + TABLE_NAME + " SET appointmentId = ?" + ", deviceId = ?"
					+ ", endTime = ?" + ", status = ?" + ", lastUpdatedUser = ?" + ", lastUpdatedDate = ? "
					+ " WHERE patientSer = ? " + "   AND startTime = ?" + "   AND activityCode = ?";

			ps = con.prepareStatement(sql.toString());
			ps.setString(1, dto.getAppointmentId());
			ps.setString(2, dto.getDeviceId());
			ps.setTimestamp(3, DateUtil.transferTimestampFromUtilToSql(dto.getEndTime()));
			ps.setString(4, dto.getStatus());
			ps.setString(5, userName);
			ps.setTimestamp(6, nowTs);
			ps.setLong(7, dto.getPatientSer());
			ps.setTimestamp(8, DateUtil.transferTimestampFromUtilToSql(dto.getStartTime()));
			ps.setString(9, dto.getActivityCode());
			affectedRow = ps.executeUpdate();
		} finally {
			DatabaseUtil.safeCloseStatement(ps);
		}
		return affectedRow;
	}

	/**
	 * Return The Treatment Appointment List from LocalDB by Appointment Id.<br>
	 *
	 * @param con
	 *            Connection
	 * @param appointmentId
	 *            Appointment Id
	 * @return The TreatmentAppointment List
	 * @throws SQLException
	 */
	public TreatmentAppointmentDTO queryByAppointmentId(Connection con, String appointmentId) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		TreatmentAppointmentDTO dto = null;
		try {
			StringBuilder sql = new StringBuilder(SQL_SELECT_BASE);
			sql.append(" WHERE appointmentId = ? ORDER BY startTime ASC, endTime ASC ");
			ps = con.prepareStatement(sql.toString());
			ps.setString(1, appointmentId);
			rs = ps.executeQuery();
			if (rs.next()) {
				dto = getTreatmentAppointmentDTO(rs);
			}
		} finally {
			DatabaseUtil.safeCloseAll(null, ps, rs);
		}
		return dto;
	}

	/**
	 * Return Treatment Appointment List from LocalDB by HisId.<br>
	 *
	 * @param con
	 *            Connection
	 * @param patientSer
	 *            patientSer
	 * @return The TreatmentAppointment List
	 * @throws SQLException
	 */
	public List<TreatmentAppointmentDTO> queryByPatientSer(Connection con, Long patientSer) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<TreatmentAppointmentDTO> list = new ArrayList<>();
		try {
			StringBuilder sql = new StringBuilder(SQL_SELECT_BASE);
			sql.append(" WHERE patientSer = ? ORDER BY startTime ASC, endTime ASC ");
			ps = con.prepareStatement(sql.toString());
			ps.setLong(1, patientSer);
			rs = ps.executeQuery();
			while (rs.next()) {
				list.add(getTreatmentAppointmentDTO(rs));
			}
		} finally {
			DatabaseUtil.safeCloseAll(null, ps, rs);
		}
		return list;
	}


	public List<TreatmentAppointmentDTO> queryByPatientSer(Connection con, Long patientSer, List<AppointmentStatusEnum> statusList) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<TreatmentAppointmentDTO> list = new ArrayList<>();
		try {
			StringBuilder sql = new StringBuilder(SQL_SELECT_BASE);
			sql.append(" WHERE patientSer = ? ");
			if (statusList != null && !statusList.isEmpty()) {
				sql.append(" AND (");
				for (int i = 0; i < statusList.size(); i++) {
					if (i == statusList.size() - 1) {
						sql.append(" status = ? ");
					} else {
						sql.append(" status = ? OR ");
					}
				}
				sql.append(")");
			}
			sql.append(" ORDER BY startTime ASC, endTime ASC ");
			ps = con.prepareStatement(sql.toString());
			ps.setLong(1,patientSer);
			for (int i = 0; i < statusList.size(); i++) {
				ps.setString(i + 2, AppointmentStatusEnum.getDisplay(statusList.get(i)));
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				list.add(getTreatmentAppointmentDTO(rs));
			}
		} finally {
			DatabaseUtil.safeCloseAll(null, ps, rs);
		}
		return list;
	}

	public List<TreatmentAppointmentDTO> queryByPatientSerAndDeviceId(Connection con, Long patientSer, String deviceId, List<AppointmentStatusEnum> statusList) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<TreatmentAppointmentDTO> list = new ArrayList<>();
		try {
			StringBuilder sql = new StringBuilder(SQL_SELECT_BASE);
			sql.append(" WHERE patientSer = ? ");
			sql.append("   AND deviceId = ? ");
			if (statusList != null && !statusList.isEmpty()) {
				sql.append(" AND (status = ? ");
				for (int i = 1; i < statusList.size(); i++) {
					sql.append(" OR status = ? ");
				}
				sql.append(")");
			}
			sql.append(" ORDER BY startTime ASC, endTime ASC ");
			ps = con.prepareStatement(sql.toString());
			ps.setLong(1, patientSer);
			ps.setString(2, deviceId);
			for (int i = 0; i < statusList.size(); i++) {
				ps.setString(i + 3, AppointmentStatusEnum.getDisplay(statusList.get(i)));
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				list.add(getTreatmentAppointmentDTO(rs));
			}
		} finally {
			DatabaseUtil.safeCloseAll(null, ps, rs);
		}
		return list;
	}

	public List<TreatmentAppointmentDTO> queryByPatientSerAndEncounterId(Connection con, Long patientSer, int encounterId, List<AppointmentStatusEnum> statusList) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<TreatmentAppointmentDTO> list = new ArrayList<>();
		try {
			StringBuilder sql = new StringBuilder(SQL_SELECT_BASE);
			sql.append(" WHERE patientSer = ? ");
			sql.append("   AND encounterId = ? ");
			if (statusList != null && !statusList.isEmpty()) {
				sql.append(" AND (status = ? ");
				for (int i = 1; i < statusList.size(); i++) {
					sql.append(" OR status = ? ");
				}
				sql.append(")");
			}
			sql.append(" ORDER BY startTime ASC, endTime ASC ");
			ps = con.prepareStatement(sql.toString());
			ps.setLong(1, patientSer);
			ps.setInt(2, encounterId);
			for (int i = 0; i < statusList.size(); i++) {
				ps.setString(i + 3, AppointmentStatusEnum.getDisplay(statusList.get(i)));
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				list.add(getTreatmentAppointmentDTO(rs));
			}
		} finally {
			DatabaseUtil.safeCloseAll(null, ps, rs);
		}
		return list;
	}

	/**
	 * Update Status by HisId and ActivityCode.<br>
	 *
	 * @param con
	 *            Connection
	 * @param patientSer
	 *            patientSer
	 * @param activityCode
	 *            Activity Code
	 * @param status
	 *            Status
	 * @return the affected row number
	 */
	public int updateStatusByPatientSerAndActivity(Connection con, Long patientSer, String activityCode, String status)
			throws SQLException {
		PreparedStatement ps = null;
		int affectedRow = 0;
		Timestamp nowTs = new Timestamp(new java.util.Date().getTime());
		String userName = userContext.getLogin().getName();
		try {
			String sql = " UPDATE " + TABLE_NAME + " SET status = ?" + ", lastUpdatedUser = ?"
					+ ", lastUpdatedDate = ? " + " WHERE patientSer = ? " + "   AND activityCode = ?";

			ps = con.prepareStatement(sql.toString());
			ps.setString(1, status);
			ps.setString(2, userName);
			ps.setTimestamp(3, nowTs);
			ps.setLong(4, patientSer);
			ps.setString(5, activityCode);
			affectedRow = ps.executeUpdate();
		} finally {
			DatabaseUtil.safeCloseStatement(ps);
		}
		return affectedRow;
	}

	/**
	 * Update Status by appointmentId.<br>
	 *
	 * @param con
	 * @param appointmentId
	 * @param status
	 * @return
	 * @throws SQLException
	 */
	public int updateStatusByAppointmentId(Connection con, String appointmentId, String status)
			throws SQLException {
		PreparedStatement ps = null;
		int affectedRow = 0;
		Timestamp nowTs = new Timestamp(new java.util.Date().getTime());
		String userName = userContext.getLogin().getName();
		try {
			String sql = " UPDATE " + TABLE_NAME + " SET status = ?" + ", lastUpdatedUser = ?"
					+ ", lastUpdatedDate = ? " + " WHERE appointmentId = ?";

			ps = con.prepareStatement(sql.toString());
			ps.setString(1, status);
			ps.setString(2, userName);
			ps.setTimestamp(3, nowTs);
			ps.setString(4, appointmentId);
			affectedRow = ps.executeUpdate();
		} finally {
			DatabaseUtil.safeCloseStatement(ps);
		}
		return affectedRow;
	}

	/**
	 * @param con
	 * @param patientSer
	 * @param activityCode
	 * @param startTime
	 * @param endTime
	 * @param status
	 * @return
	 * @throws SQLException
	 */
	public int updateStatusByPatientSerAndActivityAndStartTimeAndEndTime(Connection con, Long patientSer, String activityCode, Date startTime, Date endTime, String status)
			throws SQLException {
		PreparedStatement ps = null;
		int affectedRow = 0;
		Timestamp nowTs = new Timestamp(new java.util.Date().getTime());
		String userName = userContext.getLogin().getName();
		try {
			String sql = " UPDATE " + TABLE_NAME + " SET status = ?" + ", lastUpdatedUser = ?"
					+ ", lastUpdatedDate = ? " + " WHERE patientSer = ? " + "   AND activityCode = ?"
					+ " AND startTime=? AND endTime = ?";

			ps = con.prepareStatement(sql.toString());
			ps.setString(1, status);
			ps.setString(2, userName);
			ps.setTimestamp(3, nowTs);
			ps.setLong(4, patientSer);
			ps.setString(5, activityCode);
			ps.setTimestamp(6, DateUtil.transferTimestampFromUtilToSql(startTime));
			ps.setTimestamp(7, DateUtil.transferTimestampFromUtilToSql(endTime));
			affectedRow = ps.executeUpdate();
		} finally {
			DatabaseUtil.safeCloseStatement(ps);
		}
		return affectedRow;
	}

	public List<TreatmentAppointmentDTO> queryByPatientSerAndActivityCode(Connection con, Long patientSer, String activityCode) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<TreatmentAppointmentDTO> dtoList = new ArrayList<>();
		try {
			ps = con.prepareStatement(SQL_SELECT_BY_PATIENTID_ACTIVITYCODE);
			ps.setLong(1, patientSer);
			ps.setString(2, activityCode);
			rs = ps.executeQuery();
			while (rs.next()) {
				dtoList.add(getTreatmentAppointmentDTO(rs));
			}
		} finally {
			DatabaseUtil.safeCloseAll(null, ps, rs);
		}
		return dtoList;
	}

	public TreatmentAppointmentDTO queryByUidOrAppointmentId(Connection con, String idOrAppointment) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement(SQL_SELECT_BY_APPOINTMENTID_OR_UID_ID);
			ps.setString(1, idOrAppointment);
			ps.setString(2, idOrAppointment);
			rs = ps.executeQuery();
			if (rs.next()) {
				return getTreatmentAppointmentDTO(rs);
			}
		} finally {
			DatabaseUtil.safeCloseAll(null, ps, rs);
		}
		return null;
	}

	/**
	 * @param connection
	 * @param patientSer
	 * @param startTime
	 * @param endTime
	 * @return {totalNum=,completedNum=}
	 * @throws SQLException
	 */
	public Map<String,Integer> selectTotalAndCompletedTreatment(Connection connection,Long patientSer,Date startTime,Date endTime) throws SQLException {
		Map<String,Integer> map = new HashMap<>();
		String sql = "SELECT status,SUM(CASE WHEN status=? THEN 1 ELSE 0 END) AS fulfilledNum, COUNT(*) AS totalNum " +
				"FROM TreatmentAppointment ta WHERE ta.patientSer=? AND ta.status IN (?,?) ";
		if(startTime != null){
			sql += " AND ta.startTime>=? ";
		}if(endTime != null){
			sql += " AND ta.endTime <=?  ";
		}
		sql+=" GROUP BY status ";
		int idx = 0;
		PreparedStatement ps = connection.prepareStatement(sql);
		ps.setString(++idx,AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.FULFILLED));
		ps.setLong(++idx,patientSer);
		ps.setString(++idx,AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED));
		ps.setString(++idx,AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.FULFILLED));
		if(startTime != null){
			ps.setDate(++idx,new java.sql.Date(startTime.getTime()));
		}
		if(endTime != null){
			ps.setDate(++idx,new java.sql.Date(endTime.getTime()));
		}
		ResultSet rs = ps.executeQuery();
		if(rs.next()){
			map.put("totalNum",rs.getInt("totalNum"));
			map.put("completedNum",rs.getInt("fulfilledNum"));
		}
		return map;
	}

	private TreatmentAppointmentDTO getTreatmentAppointmentDTO(ResultSet rs) throws SQLException {
		TreatmentAppointmentDTO dto = new TreatmentAppointmentDTO();
		dto.setId(rs.getString("id"));
		dto.setUid(rs.getString("uid"));
		dto.setAppointmentId(rs.getString("appointmentId"));
		dto.setPatientSer(rs.getLong("patientSer"));
		dto.setHisId(rs.getString("hisId"));
		dto.setDeviceId(rs.getString("deviceId"));
		dto.setStartTime(rs.getTimestamp("startTime"));
		dto.setEndTime(rs.getTimestamp("endTime"));
		dto.setActivityCode(rs.getString("activityCode"));
		dto.setStatus(rs.getString("status"));
		return dto;
	}
}