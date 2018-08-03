package com.varian.oiscn.anticorruption.assembler;

import com.varian.fhir.resources.TreatmentSummary;
import com.varian.fhir.resources.TreatmentSummary.FieldAccessory;
import com.varian.oiscn.core.treatmentsummary.DoseSummaryDto;
import com.varian.oiscn.core.treatmentsummary.FieldSummaryDto;
import com.varian.oiscn.core.treatmentsummary.PlanStatusEnum;
import com.varian.oiscn.core.treatmentsummary.PlanSummaryDto;
import com.varian.oiscn.core.treatmentsummary.TreatmentSummaryDto;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeComparator;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.varian.oiscn.anticorruption.converter.DataHelper.getReferenceValue;

/**
 * Created by fmk9441 on 2017-07-24.
 */
public class TreatmentSummaryAssembler {
    private TreatmentSummaryAssembler() {

    }

    /**
     * Return DTO from Fhir TreatmentSummary.<br>
     *
     * @param treatmentSummary Fhir TreatmentSummary
     * @return DTO
     */
    public static TreatmentSummaryDto getTreatmentSummaryDto(TreatmentSummary treatmentSummary) {
        TreatmentSummaryDto treatmentSummaryDto = new TreatmentSummaryDto();
        if (treatmentSummary == null) {
            return treatmentSummaryDto;
        }
        List<TreatmentSummary.PlanSetupDetails> plans = treatmentSummary.getCourses().stream().flatMap(i -> i.getPlans().stream()).collect(Collectors.toList());
        Optional<Date> lastTreatmentDate = plans.stream().flatMap(i -> i.getFractionSummary().stream().map(j -> j.getLastTreatmentTime())).filter(i -> i != null).map(i -> i.getValue()).max(DateTimeComparator.getInstance());
        if (lastTreatmentDate.isPresent()) {
            treatmentSummaryDto.setLastTreatmentDate(lastTreatmentDate.get());
        }
        Optional<String> codeUnit = plans.stream().flatMap(i -> i.getDoseSummary().stream()).map(i -> i.getDoseUnit().getCodingFirstRep().getCode()).findFirst();
        if (codeUnit.isPresent()) {
            treatmentSummaryDto.setDoseUnit(codeUnit.get());
        }

        List<PlanSummaryDto> pss = plans.stream().map(i -> getPlanDto(i)).collect(Collectors.toList());
        treatmentSummaryDto.setPlans(pss);
        return treatmentSummaryDto;
    }

    private static PlanSummaryDto getPlanDto(TreatmentSummary.PlanSetupDetails p) {
        PlanSummaryDto ps = new PlanSummaryDto();
        List<DoseSummaryDto> dss = p.getDoseSummary().stream().map(i -> getDoseSummary(i)).collect(Collectors.toList());
        List<FieldSummaryDto> fss = p.getFields().stream().map(i -> getFieldSummary(i)).collect(Collectors.toList());
        ps.setFields(fss);
        ps.setDoseSummary(dss);
        ps.setDeliveredDose(dss.stream().map(i -> i.getDeliveredDose()).collect(Collectors.summingDouble(Double::doubleValue)));
        ps.setPlannedDose(dss.stream().map(i -> i.getPlannedDose()).collect(Collectors.summingDouble(Double::doubleValue)));
        ps.setDeliveredFractions(p.getFractionSummary().stream().map(i -> i.getFractionsGiven().getValue()).collect(Collectors.summingInt(Integer::intValue)));
        ps.setPlannedFractions(p.getFractionSummary().stream().map(i -> i.getFractionsPlanned().getValue()).collect(Collectors.summingInt(Integer::intValue)));
        ps.setCreatedDt(p.getCreatedOn().getValue());
        ps.setPlanSetupId(p.getPlanSetupId().getValueNotNull());
        ps.setPlanSetupName(p.getPlanSetupName() != null ? p.getPlanSetupName().getValueNotNull() : null);
        ps.setStatus(PlanStatusEnum.code2Enum(p.getStatus().getValueNotNull()));
        Optional<Date> lastTreatmentDate = p.getFractionSummary().stream().map(j->j.getLastTreatmentTime()).filter(i->i!=null).map(i->i.getValue()).max(DateTimeComparator.getInstance());
        if(lastTreatmentDate.isPresent()){
            ps.setLastTreatmentTime(lastTreatmentDate.get());
        }
        return ps;
    }

    private static FieldSummaryDto getFieldSummary(TreatmentSummary.Field f) {
        FieldSummaryDto d = new FieldSummaryDto();
        d.setFieldId(f.getFieldId().getValue());
        d.setFieldName(f.getFieldName() != null ? f.getFieldName().getValue() : null);
        d.setEnergy(f.getEnergy() != null ? f.getEnergy().getValue() : null);
        d.setScale(f.getScale() != null ? f.getScale().getText() : null);
        StringBuilder gantry = new StringBuilder();
        if (f.getGantryRotation() != null) {
            String gantryRotation = String.format("%.1f", f.getGantryRotation().getValue());
            gantry.append(gantryRotation);
            if (f.getGantryDirection() != null) {
                gantry.append(" ").append(f.getGantryDirection().getValue());
            }
            if (f.getStopAngle() != null) {
                String stopAngle = String.format("%.1f", f.getStopAngle().getValue());
                gantry.append(" ").append(stopAngle);
            }
        }
        d.setGantryRotation(gantry.toString());
        d.setMu(f.getMu() != null ? f.getMu().getValue().toString() : null);
        d.setCollimatorRotation(f.getCollimatorRotation() != null ? f.getCollimatorRotation().getValue().doubleValue() : null);
        d.setCollimatorX(f.getCollimatorX() != null ? f.getCollimatorX().getValue().doubleValue() : null);
        d.setCollimatorY(f.getCollimatorY() != null ? f.getCollimatorY().getValue().doubleValue() : null);
        d.setCouchRotation(f.getCouchRotation() != null ? f.getCouchRotation().getValue().doubleValue() : null);
        d.setCouchLat(f.getCouchLat() != null ? f.getCouchLat().getValue().doubleValue() : null);
        d.setCouchLng(f.getCouchLng() != null ? f.getCouchLng().getValue().doubleValue() : null);
        d.setCouchVrt(f.getCouchVrt() != null ? f.getCouchVrt().getValue().doubleValue() : null);
        d.setTechnique(f.getTechnique() != null ? f.getTechnique().getValue() : null);
        d.setDoseRate(f.getDoseRate() != null ? f.getDoseRate().getValue() : null);
        d.setIsoCenterX(f.getIsoCenterPositionX() != null ? f.getIsoCenterPositionX().getValue().toString() : null);
        d.setIsoCenterY(f.getIsoCenterPositionY() != null ? f.getIsoCenterPositionY().getValue().toString() : null);
        d.setIsoCenterZ(f.getIsoCenterPositionZ() != null ? f.getIsoCenterPositionZ().getValue().toString() : null);
        if(StringUtils.isNotEmpty(d.getIsoCenterX())){
            d.setIsoCenterX(String.valueOf(Double.parseDouble(d.getIsoCenterX())/10));
        }
        if(StringUtils.isNotEmpty(d.getIsoCenterY())){
            d.setIsoCenterY(String.valueOf(Double.parseDouble(d.getIsoCenterY())/10));
        }
        if(StringUtils.isNotEmpty(d.getIsoCenterZ())){
            d.setIsoCenterZ(String.valueOf(Double.parseDouble(d.getIsoCenterZ())/10));
        }
        d.setCalculatedSSD(f.getSsd() != null ? f.getSsd().getValue().toString() : null);
        d.setToleranceId(f.getToleranceId() != null ? f.getToleranceId().getValue() : null);
        d.setToleranceName(f.getToleranceName() != null ? f.getToleranceName().getValue() : null);
        d.setMachine(f.getMachine() != null ? (f.getMachine().hasDisplay() ? f.getMachine().getDisplay() : getReferenceValue(f.getMachine())) : null);
        d.setWeight(f.getSetupField().booleanValue() ? new Double(0.000) : f.getWeight() != null ? f.getWeight().getValue().doubleValue() : null);
        // d.setWedge(null != f.getAccessories() && !f.getAccessories().isEmpty() ? f.getAccessories().get(0).getAccessoryName().getValueNotNull() : null);
        final List<FieldAccessory> accessories = f.getAccessories();
        if (accessories != null && accessories.size() > 0 && accessories.get(0).getAccessoryName() != null) {
            d.setWedge(accessories.get(0).getAccessoryName().getValueNotNull());
        } else {
            d.setWedge(null);
        }
        String size = StringUtils.EMPTY;
        if (f.getCollimatorX() != null && f.getCollimatorY() != null) {
            size = String.format("%1$s x %2$s", f.getCollimatorX().getValue().toString(), f.getCollimatorY().getValue().toString());
            if (f.getCollMode() != null) {
                size = String.format("%1$s(%2$s)", size, f.getCollMode().getValue());
            }
        }
        d.setSize(size);

        if (f.getCollUnit() != null && f.getCollUnit().getCodingFirstRep() != null) {
            d.setCollUnit(f.getCollUnit().getCodingFirstRep().getCode());
        }
        if (f.getCouchUnit() != null && f.getCouchUnit().getCodingFirstRep() != null) {
            d.setCouchUnit(f.getCouchUnit().getCodingFirstRep().getCode());
        }
        if (f.getIsocenterUnit() != null && f.getIsocenterUnit().getCodingFirstRep() != null) {
            d.setIsoCenterUnit(f.getIsocenterUnit().getCodingFirstRep().getCode());
        }
        if (f.getRotationUnit() != null && f.getRotationUnit().getCodingFirstRep() != null) {
            d.setRotationUnit(f.getRotationUnit().getCodingFirstRep().getCode());
        }
        if (f.getSsdUnit() != null && f.getSsdUnit().getCodingFirstRep() != null) {
            d.setSsdUnit(f.getSsdUnit().getCodingFirstRep().getCode());
        }
        if (f.getDoseRateUnit() != null && f.getDoseRateUnit().getCodingFirstRep() != null) {
            d.setDoseRateUnit(f.getDoseRateUnit().getCodingFirstRep().getCode());
        }
        return d;
    }

    private static DoseSummaryDto getDoseSummary(TreatmentSummary.DoseSummary d) {
        DoseSummaryDto ds = new DoseSummaryDto();
        ds.setDeliveredDose(d.getDoseGiven() != null ? d.getDoseGiven().getValueAsNumber().doubleValue() : 0.0);
        ds.setDosePerFraction(d.getDosePerFraction() != null ? d.getDosePerFraction().getValueAsNumber().doubleValue() : 0.0);
        ds.setPlannedDose(d.getDosePlanned() != null ? d.getDosePlanned().getValueAsNumber().doubleValue() : 0.0);
        ds.setRemainingDose(d.getDoseRemaining() != null ? d.getDoseRemaining().getValueAsNumber().doubleValue() : 0.0);
        ds.setSiteName(d.getSiteName() != null ? d.getSiteName().getValue() : StringUtils.EMPTY);
        ds.setSiteId(d.getSiteId() != null ? d.getSiteId().getValueNotNull() : StringUtils.EMPTY);
        return ds;
    }
}