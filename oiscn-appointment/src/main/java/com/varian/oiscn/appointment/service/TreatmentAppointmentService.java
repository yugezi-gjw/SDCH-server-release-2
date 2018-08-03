package com.varian.oiscn.appointment.service;

import com.varian.oiscn.appointment.dao.TreatmentAppointmentDAO;
import com.varian.oiscn.appointment.dto.TreatmentAppointmentDTO;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.appointment.AppointmentStatusEnum;
import com.varian.oiscn.core.pagination.Pagination;
import com.varian.oiscn.core.participant.ParticipantDto;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.util.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.StringUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Treatment Appoint Service.<br>
 */
@Slf4j
public class TreatmentAppointmentService {

	protected TreatmentAppointmentDAO dao;

	public TreatmentAppointmentService(UserContext userContext) {
		dao = new TreatmentAppointmentDAO(userContext);
	}

	/**
	 * Create a single Treatment Appointment.<br>
	 *
	 * @param dto
	 *            TreatmentAppointmentDTO
	 * @return the created Id
	 */
	public String create(TreatmentAppointmentDTO dto) {
		String newId = StringUtils.EMPTY;
		Connection con = null;
		try {
			con = ConnectionPool.getConnection();
			newId = dao.create(con, dto);
		} catch (SQLException e) {
			log.error("SQLException SQLState=[{}]", e.getSQLState());
		} finally {
			DatabaseUtil.safeCloseConnection(con);
		}
		return newId;
	}

	/**
	 * Create Treatment Appointment List.<br>
	 *
	 * @param dtoList
	 *            TreatmentAppointmentDTO List
	 * @return The created Id List
	 */
	public List<String> createList(List<TreatmentAppointmentDTO> dtoList) {
		List<String> idList = new ArrayList<>();
		String newId;
		Connection con = null;
		try {
			con = ConnectionPool.getConnection();
			DatabaseUtil.safeSetAutoCommit(con, false);
			for (TreatmentAppointmentDTO dto : dtoList) {
				newId = dao.create(con, dto);
				idList.add(newId);
			}
			con.commit();
		} catch (SQLException e) {
			log.error("SQLException SQLState=[{}]", e.getSQLState());
			DatabaseUtil.safeRollback(con);
		} finally {
			DatabaseUtil.safeSetAutoCommit(con, true);
			DatabaseUtil.safeCloseConnection(con);
		}
		return idList;
	}

	/**
	 * Update by StartTime, PatientSer, and ActivityCode.<br>
	 * If it not exist, create a new one.<br>
	 *
	 * @param dtoList
	 *            TreatmentAppointmentDTO List
	 * @return affected rows
	 */
	public int updateByStartTimeAndPatientSerAndActivity(List<TreatmentAppointmentDTO> dtoList) {
		int affectedRow = 0;
		Connection con = null;
		try {
			con = ConnectionPool.getConnection();
			DatabaseUtil.safeSetAutoCommit(con, false);
			for (TreatmentAppointmentDTO dto : dtoList) {
				int updatedRow = dao.updateByStartTimeAndPatientSerAndActivity(con, dto);
				if (updatedRow == 0) {
					String newId = dao.create(con, dto);
					if (StringUtil.isNotBlank(newId)) {
						affectedRow += 1;
					}
				}
				affectedRow += updatedRow;
			}
			con.commit();
		} catch (SQLException e) {
			log.error("SQLException SQLState=[{}]", e.getSQLState());
			DatabaseUtil.safeRollback(con);
		} finally {
			DatabaseUtil.safeSetAutoCommit(con, true);
			DatabaseUtil.safeCloseConnection(con);
		}
		return affectedRow;
	}

	public boolean update(TreatmentAppointmentDTO dto, String id) {
		Connection con = null;
		try {
			con = ConnectionPool.getConnection();
			return dao.update(con, dto, id);
		} catch (SQLException e) {
			log.error("SQLException SQLState=[{}]", e.getSQLState());
			return false;
		} finally {
			DatabaseUtil.safeCloseConnection(con);
		}
	}

	/**
	 * Query Appointment List by patientSer, Date, and Activity Code.<br>
	 * @param patientSer patientSer
	 * @param day Date
	 * @param activityCode Activity Code
	 * @return Appointment List
	 */
	public List<TreatmentAppointmentDTO> queryByPatientSerAndDateAndActivity(Long patientSer, Date day, String activityCode) {
		List<TreatmentAppointmentDTO> dtoList;
		Connection con = null;
		try {
			con = ConnectionPool.getConnection();
			dtoList = dao.queryByPatientSerAndDateAndActivity(con, patientSer, day, activityCode);
		} catch (SQLException e) {
			log.error("SQLException SQLState=[{}]", e.getSQLState());
			dtoList = new ArrayList<>();
		} finally {
			DatabaseUtil.safeCloseConnection(con);
		}
		return dtoList;
	}

	public List<TreatmentAppointmentDTO> queryTreatmentsAppointmentByPatientId(Long patientId, String activityCode) {
		Connection con = null;
		try {
			con = ConnectionPool.getConnection();
			List<TreatmentAppointmentDTO> dtoList = dao.queryByPatientSerAndActivityCode(con, patientId, activityCode);
			return dtoList;
		} catch (SQLException e) {
			log.error("SQLException SQLState=[{}]", e.getSQLState());
		} finally {
			DatabaseUtil.safeCloseConnection(con);
		}
		return null;
	}

	public TreatmentAppointmentDTO queryTheFirstTreatmentAppointmentByPatientSer(Long patientSer, String activityCode) {
		Connection con = null;
		try {
			con = ConnectionPool.getConnection();
			List<TreatmentAppointmentDTO> dtoList = dao.queryByPatientSerAndActivityCode(con, patientSer, activityCode);
			if (!dtoList.isEmpty()) {
				return dtoList.get(0);
			}
		} catch (SQLException e) {
			log.error("SQLException SQLState=[{}]", e.getSQLState());
		} finally {
			DatabaseUtil.safeCloseConnection(con);
		}
		return null;
	}

	public Pagination<TreatmentAppointmentDTO> queryByPatientSerListAndDatePagination(List<Long> patientSerList, Date startDate, Date endDate, String sort, String countPerPage, String pageNumber) {
		 Connection con = null;
		 Pagination<TreatmentAppointmentDTO> treatmentAppointmentDTOPage = new Pagination<>();
		 try {
		 	con = ConnectionPool.getConnection();
			 treatmentAppointmentDTOPage = dao.queryByPatientSerListAndDatePagination(con, patientSerList, startDate, endDate, sort, countPerPage, pageNumber);
		 } catch (SQLException e) {
			 log.error("SQLException SQLState=[{}]", e.getSQLState());
		 } finally {
		 	DatabaseUtil.safeCloseConnection(con);
		 }
		return treatmentAppointmentDTOPage;
	}

	public Pagination<TreatmentAppointmentDTO> queryByPatientSerListAndDeviceIdAndDatePagination(List<Long> patientSerList, String deviceId, Date startDate, Date endDate, String sort, String countPerPage, String pageNumber) {
		Connection con = null;
		Pagination<TreatmentAppointmentDTO> treatmentAppointmentDTOPage = new Pagination<>();
		try {
			con = ConnectionPool.getConnection();
			List<String> deviceIdList = new ArrayList<>();
			deviceIdList.add(deviceId);
			treatmentAppointmentDTOPage = dao.queryByPatientSerOrDeviceIdListAndDateStatusPagination(con, patientSerList, deviceIdList, startDate, endDate, Arrays.asList(AppointmentStatusEnum.BOOKED), sort, countPerPage, pageNumber);
		} catch (SQLException e) {
			log.error("SQLException SQLState=[{}]", e.getSQLState());
		} finally {
			DatabaseUtil.safeCloseConnection(con);
		}
		return treatmentAppointmentDTOPage;
	}

	public Pagination<TreatmentAppointmentDTO> queryByDatePagination(Date startDate, Date endDate, String sort, String countPerPage, String pageNumber) {
		Connection con = null;
		Pagination<TreatmentAppointmentDTO> treatmentAppointmentDTOPage = null;
		try {
			con = ConnectionPool.getConnection();
			treatmentAppointmentDTOPage = dao.queryByPatientSerOrDeviceIdListAndDateStatusPagination(con, null, null, startDate, endDate, Arrays.asList(AppointmentStatusEnum.BOOKED), sort, countPerPage, pageNumber);

		} catch (SQLException e) {
			log.error("SQLException SQLState=[{}]", e.getSQLState());
		} finally {
			DatabaseUtil.safeCloseConnection(con);
		}
		if (treatmentAppointmentDTOPage == null) {
			treatmentAppointmentDTOPage = new Pagination<>();
			treatmentAppointmentDTOPage.setLstObject(new ArrayList<>());
		}
		return treatmentAppointmentDTOPage;
	}

	public Pagination<TreatmentAppointmentDTO> queryByDeviceIdListAndDatePagination(List<String> deviceIdList, Date startDate, Date endDate, List<AppointmentStatusEnum> statusEnums, String sort, String countPerPage, String pageNumber) {
		Connection con = null;
		Pagination<TreatmentAppointmentDTO> treatmentAppointmentDTOPage = new Pagination<>();
		try {
			con = ConnectionPool.getConnection();
			treatmentAppointmentDTOPage = dao.queryByDeviceIdListAndDatePagination(con, deviceIdList, startDate, endDate, statusEnums,sort, countPerPage, pageNumber);
		} catch (SQLException e) {
			log.error("SQLException SQLState=[{}]", e.getSQLState());
		} finally {
			DatabaseUtil.safeCloseConnection(con);
		}
		return treatmentAppointmentDTOPage;
	}
	/**
	 * Update Status by patientSer, and ActivityCode.<br>
	 *
	 * @param patientSer patientSer
	 * @param activityCode Activity Code
	 * @param status Appointment Status
	 * @return affected rows
	 */
	public int updateStatusByPatientSerAndActivity(Long patientSer, String activityCode, String status) {
		int affectedRow = 0;
		Connection con = null;
		try {
			con = ConnectionPool.getConnection();
			affectedRow = dao.updateStatusByPatientSerAndActivity(con, patientSer, activityCode, status);
		} catch (SQLException e) {
			log.error("SQLException SQLState=[{}]", e.getSQLState());
		} finally {
			DatabaseUtil.safeCloseConnection(con);
		}
		return affectedRow;
	}

	/**
	 * Update Status by appointmentId.<br>
	 *
	 * @param appointmentId
	 * @param status
	 * @return
	 */
	public int updateStatusByAppointmentId(String appointmentId, AppointmentStatusEnum status) {
		int affectedRow = 0;
		Connection con = null;
		try {
			con = ConnectionPool.getConnection();
			affectedRow = dao.updateStatusByAppointmentId(con, appointmentId, AppointmentStatusEnum.getDisplay(status));
		} catch (SQLException e) {
			log.error("SQLException SQLState=[{}]", e.getSQLState());
		} finally {
			DatabaseUtil.safeCloseConnection(con);
		}
		return affectedRow;
	}

	public TreatmentAppointmentDTO queryByUidOrAppointmentId(String idOrAppointmnetId) {
		Connection con = null;
		try {
			con = ConnectionPool.getConnection();
			return dao.queryByUidOrAppointmentId(con, idOrAppointmnetId);
		} catch (SQLException e) {
			log.error("SQLException SQLState=[{}]", e.getSQLState());
		} finally {
			DatabaseUtil.safeCloseConnection(con);
		}
		return null;
	}
	public int updateStatusByPatientSerAndActivityAndStartTimeAndEndTime(Long patientSer, String activityCode, Date statTime, Date endTime, String status) {
		int affectedRow = 0;
		Connection con = null;
		try {
			con = ConnectionPool.getConnection();
			affectedRow = dao.updateStatusByPatientSerAndActivityAndStartTimeAndEndTime(con, patientSer, activityCode, statTime, endTime, status);
		} catch (SQLException e) {
			log.error("SQLException SQLState=[{}]", e.getSQLState());
		} finally {
			DatabaseUtil.safeCloseConnection(con);
		}
		return affectedRow;
	}

	/**
	 * @param patientSer
	 * @param statusEnumList
	 * @return
	 */
	public List<TreatmentAppointmentDTO> queryByPatientSerAndStatus(Long patientSer, List<AppointmentStatusEnum> statusEnumList) {
		Connection con = null;
		try {
			con = ConnectionPool.getConnection();
			return dao.queryByPatientSer(con, patientSer, statusEnumList);
		} catch (SQLException e) {
			log.error("SQLException SQLState=[{}]", e.getSQLState());
		} finally {
			DatabaseUtil.safeCloseConnection(con);
		}
		return new ArrayList<>();
	}

	/**
	 * Query Treatment Appointment DTO List (BOOKED).<br>
	 *
	 * @param patientSer  patientSer
	 * @param deviceId Device Id
	 * @return Treatment Appointment List
	 */
	public List<TreatmentAppointmentDTO> queryAppointmentListByPatientSerAndDeviceId(Long patientSer, String deviceId) {
		List<TreatmentAppointmentDTO> list = new ArrayList<>();
		Connection con = null;
		try {
			con = ConnectionPool.getConnection();
			list = dao.queryByPatientSerAndDeviceId(con, patientSer, deviceId, Arrays.asList(AppointmentStatusEnum.BOOKED, AppointmentStatusEnum.FULFILLED));
		} catch (SQLException e) {
			log.error("queryBookedListByHisIdDeviceId SQLException SQLState=[{}]", e.getSQLState());
		} finally {
			DatabaseUtil.safeCloseConnection(con);
		}
		return list;
	}

	public List<TreatmentAppointmentDTO> queryAppointmentListByPatientSerAndEncounterId(Long patientSer, int encounterId) {
		List<TreatmentAppointmentDTO> list = new ArrayList<>();
		Connection con = null;
		try {
			con = ConnectionPool.getConnection();
			list = dao.queryByPatientSerAndEncounterId(con, patientSer, encounterId, Arrays.asList(AppointmentStatusEnum.BOOKED, AppointmentStatusEnum.FULFILLED));
		} catch (SQLException e) {
			log.error("queryBookedListByHisIdDeviceId SQLException SQLState=[{}]", e.getSQLState());
		} finally {
			DatabaseUtil.safeCloseConnection(con);
		}
		return list;
	}

	/**
	 *
 	 * @param patientSer
	 * @param statTime
	 * @param endTime
	 * @return {totalNum=,completedNum=}
	 */

	public Map<String,Integer> queryTotalAndCompletedTreatment(Long patientSer, Date statTime, Date endTime){
		Connection con = null;
		try {
			con = ConnectionPool.getConnection();
			Map<String,Integer> map = dao.selectTotalAndCompletedTreatment(con,patientSer,statTime,endTime);
			return map;
		} catch (SQLException e) {
			log.error("SQLException SQLState=[{}]", e.getSQLState());
		} finally {
			DatabaseUtil.safeCloseConnection(con);
		}
		return null;
	}

	public List<AppointmentDto> searchAppointmentFromLocal(String deviceId, String startDate, String endDate, String orderId, Long patientSer, List<AppointmentDto> appointmentDtoList) {
		boolean appointmentStoredToLocal = SystemConfigPool.queryStoredTreatmentAppointment2Local();
		if (!appointmentStoredToLocal) {
			return new ArrayList<>();
		}
//		TreatmentAppointmentService treatmentAppointmentService = new TreatmentAppointmentService(new UserContext());
		Pagination<TreatmentAppointmentDTO> pagination = null;

		try {
			if (StringUtils.isNotEmpty(deviceId)) {
				String start;
				String end;
				if (StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(endDate)) {
					start = startDate;
					end = endDate;
				} else {
					start = DateUtil.getCurrentDate();
					end = DateUtil.getCurrentDate();
				}

				pagination = queryByDeviceIdListAndDatePagination(Arrays.asList(deviceId), DateUtil.parse(start), DateUtil.parse(end), Arrays.asList(AppointmentStatusEnum.BOOKED,AppointmentStatusEnum.FULFILLED),"asc", Integer.MAX_VALUE + "", "1");
				if (log.isDebugEnabled()) {
					log.debug("searchAppointmentFromLocal- Paging TotalCount: [{}]", pagination.getTotalCount());
					int debugIndex = 0;
					for (TreatmentAppointmentDTO dto : pagination.getLstObject()) {
						log.debug("searchAppointmentFromLocal - TreatmentAppointmentDTO[{}]: {}", debugIndex++, dto.toString());
					}
				}

			} else if (StringUtils.isNotEmpty(orderId)) {

			} else if (patientSer!=null) {
				pagination = queryByPatientSerListAndDatePagination(Arrays.asList(patientSer), DateUtil.parse(startDate), DateUtil.parse(endDate), "asc", Integer.MAX_VALUE + "", "1");
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		if (pagination != null) {
			if (pagination.getLstObject() != null) {
				pagination.getLstObject().forEach(treatmentAppointmentDTO -> {
					if (StringUtils.isEmpty(treatmentAppointmentDTO.getAppointmentId())) {
						appointmentDtoList.add(treatmentAppointmentDTO2AppointmentDto(treatmentAppointmentDTO));
					}
				});
			}
		}
		return appointmentDtoList;
	}

	public AppointmentDto treatmentAppointmentDTO2AppointmentDto(TreatmentAppointmentDTO appointmentDTO) {
		AppointmentDto dto = new AppointmentDto();
		dto.setStartTime(appointmentDTO.getStartTime());
		dto.setEndTime(appointmentDTO.getEndTime());
		if (StringUtils.isEmpty(appointmentDTO.getAppointmentId())) {
			dto.setAppointmentId(appointmentDTO.getUid());
		} else {
			dto.setAppointmentId(appointmentDTO.getAppointmentId());
		}
		dto.setStatus(appointmentDTO.getStatus());
		dto.setReason(appointmentDTO.getActivityCode());
		dto.setParticipants(Arrays.asList(new ParticipantDto(ParticipantTypeEnum.PATIENT, String.valueOf(appointmentDTO.getPatientSer())),
				new ParticipantDto(ParticipantTypeEnum.DEVICE, appointmentDTO.getDeviceId())));
		Date nowDate = new Date();
		dto.setCreatedDT(nowDate);
		dto.setLastModifiedDT(nowDate);
		return dto;
	}

}
