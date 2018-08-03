package com.varian.oiscn.anticorruption.assembler;

import com.varian.fhir.resources.Appointment;
import com.varian.oiscn.anticorruption.datahelper.MockAppointmentUtil;
import com.varian.oiscn.core.appointment.AppointmentDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.CoreMatchers.*;

/**
 * Created by fmk9441 on 2017-02-14.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AppointmentAssembler.class})
public class AppointmentAssemblerTest {
    @InjectMocks
    private AppointmentAssembler appointmentAssembler;

    @Test
    public void givenAnAppointmentDtoWhenConvertThenReturnAppointment() throws Exception {
        AppointmentDto appointmentDto = MockAppointmentUtil.givenAnAppointmentDto();
        Appointment appointment = AppointmentAssembler.getAppointment(appointmentDto);
        Assert.assertThat(appointment, is(not(nullValue())));
    }

    @Test
    public void givenAnAppointmentWhenConvertThenReturnAppointmentDto() throws Exception {
        Appointment appointment = MockAppointmentUtil.givenAnAppointment();
        AppointmentDto appointmentDto = AppointmentAssembler.getAppointmentDto(appointment);
        Assert.assertThat(appointmentDto, is(not(nullValue())));
    }

    @Test
    public void doUpdatePatient() throws Exception {
        Appointment appointment = MockAppointmentUtil.givenAnAppointment();
        AppointmentDto appointmentDto = MockAppointmentUtil.givenAnAppointmentDto();
        AppointmentAssembler.updateAppointment(appointment, appointmentDto);
        Assert.assertEquals(appointment.getStatus(), Appointment.AppointmentStatus.FULFILLED);
    }
}